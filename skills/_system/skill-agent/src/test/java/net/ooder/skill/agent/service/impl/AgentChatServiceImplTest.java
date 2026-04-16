package net.ooder.skill.agent.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skill.agent.dto.AgentChatMessageDTO;
import net.ooder.skill.agent.entity.ChatMessage;
import net.ooder.skill.agent.entity.Todo;
import net.ooder.skill.agent.repository.ChatMessageRepository;
import net.ooder.skill.agent.repository.TodoRepository;
import net.ooder.skill.agent.service.AgentChatService;
import net.ooder.spi.im.ImDeliveryDriver;
import net.ooder.spi.im.model.MessageContent;
import net.ooder.spi.im.model.SendResult;
import net.ooder.spi.rag.RagEnhanceDriver;
import net.ooder.bpm.engine.BPMException;
import net.ooder.skill.workflow.core.BpmCoreService;
import net.ooder.skill.scene.dto.todo.TodoDTO;
import net.ooder.skill.tenant.context.TenantContext;

import net.ooder.scene.message.northbound.NorthboundMessageQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class AgentChatServiceImplTest {

    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private TodoRepository todoRepository;
    @Mock private ImDeliveryDriver messageGateway;
    @Mock private RagEnhanceDriver ragEnhanceDriver;
    @Mock private BpmCoreService bpmCoreService;
    @Mock private NorthboundMessageQueue northboundQueue;

    private AgentChatServiceImpl service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        service = new AgentChatServiceImpl();
        objectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(service, "chatMessageRepository", chatMessageRepository);
        ReflectionTestUtils.setField(service, "todoRepository", todoRepository);
        ReflectionTestUtils.setField(service, "messageGateway", messageGateway);
        ReflectionTestUtils.setField(service, "ragEnhanceDriver", ragEnhanceDriver);
        ReflectionTestUtils.setField(service, "bpmCoreService", bpmCoreService);
        ReflectionTestUtils.setField(service, "northboundQueue", northboundQueue);
        ReflectionTestUtils.setField(service, "objectMapper", objectMapper);

        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(inv -> inv.getArgument(0));
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    AgentChatMessageDTO createP2PMessage(String content) {
        AgentChatMessageDTO m = new AgentChatMessageDTO();
        m.setMessageId(UUID.randomUUID().toString());
        m.setSceneGroupId("sg-test");
        m.setSenderId("agent-1"); m.setSender("Agent");
        m.setSenderType("AI_AGENT");
        m.setReceiverId("user-1"); m.setReceiverName("User");
        m.setMessageType("P2P");
        m.setContent(content); m.setPriority(5);
        return m;
    }

    TodoDTO todoStoreGet(String id) {
        try {
            var field = AgentChatServiceImpl.class.getDeclaredField("todoStore");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, TodoDTO> store = (Map<String, TodoDTO>) field.get(service);
            return store != null ? store.get(id) : null;
        } catch (Exception e) { return null; }
    }

    void initTodoInStore(String id, String status) {
        try {
            var field = AgentChatServiceImpl.class.getDeclaredField("todoStore");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, TodoDTO> store = (Map<String, TodoDTO>) field.get(service);
            if (store == null) { store = new ConcurrentHashMap<>(); field.set(service, store); }
            TodoDTO t = new TodoDTO();
            t.setId(id); t.setTitle("Test Todo"); t.setStatus(status);
            t.setAssignee("user-1"); t.setSceneGroupId("sg-test");
            t.setCreateTime(System.currentTimeMillis());
            store.put(id, t);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Nested
    @DisplayName("sendMessage - 基础流程")
    class SendMessageTests {

        @Test
        void shouldSaveMessageAndReturnId() {
            String id = service.sendMessage("sg-test", createP2PMessage("hello"));

            assertNotNull(id);
            assertDoesNotThrow(() -> UUID.fromString(id));
            verify(chatMessageRepository).save(any(ChatMessage.class));
        }

        @Test
        void shouldSetCorrectMetadata() throws Exception {
            AgentChatMessageDTO msg = createP2PMessage("test meta");
            msg.setMetadata(Map.of("key1", "val1", "key2", 123));

            service.sendMessage("sg-meta", msg);

            verify(chatMessageRepository).save(argThat(entity ->
                entity.getMetadataJson() != null && entity.getMetadataJson().contains("key1")));
        }

        @Test
        void shouldThrowOnNullSceneGroup() {
            AgentChatMessageDTO msg = createP2PMessage("no scene");
            msg.setSceneGroupId(null);

            assertThrows(IllegalArgumentException.class, () -> service.sendMessage(null, msg));
        }

        @Test
        void shouldHandleLargeContent() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) sb.append("word ");
            AgentChatMessageDTO msg = createP2PMessage(sb.toString());

            assertDoesNotThrow(() -> service.sendMessage("sg-large", msg));
        }

        @Test
        void shouldSetDefaultPriorityWhenMissing() {
            AgentChatMessageDTO msg = createP2PMessage("no priority");
            msg.setPriority(0);

            assertDoesNotThrow(() -> service.sendMessage("sg-priority", msg));
            verify(chatMessageRepository).save(argThat(e -> e.getPriority() >= 1));
        }
    }

    @Nested
    @DisplayName("sendMessage - IM Gateway 推送")
    class ImDeliveryTests {

        @Test
        void shouldDeliverP2PViaImGateway() throws Exception {
            when(messageGateway.sendAsync(any(MessageContent.class), any(ImDeliveryDriver.DeliveryContext.class)))
                    .thenReturn(CompletableFuture.completedFuture(SendResult.success("im-msg-001")));

            service.sendMessage("sg-im", createP2PMessage("im delivery"));

            verify(messageGateway).sendAsync(any(MessageContent.class), any(ImDeliveryDriver.DeliveryContext.class));
        }

        @Test
        void shouldNotDeliverNonP2P() throws Exception {
            when(messageGateway.sendAsync(any(MessageContent.class), any(ImDeliveryDriver.DeliveryContext.class)))
                    .thenReturn(CompletableFuture.completedFuture(SendResult.success("ok")));

            AgentChatMessageDTO groupMsg = createP2PMessage("group msg");
            groupMsg.setMessageType("GROUP");
            service.sendMessage("sg-group", groupMsg);

            verify(messageGateway, never()).sendAsync(any(MessageContent.class), any(ImDeliveryDriver.DeliveryContext.class));
        }

        @Test
        void shouldHandleImFailureGracefully() throws Exception {
            when(messageGateway.sendAsync(any(MessageContent.class), any(ImDeliveryDriver.DeliveryContext.class)))
                    .thenReturn(CompletableFuture.failedFuture(new RuntimeException("IM down")));

            String id = service.sendMessage("sg-fail", createP2PMessage("fail im"));
            assertNotNull(id);
        }

        @Test
        void shouldNotThrowWhenGatewayNull() {
            ReflectionTestUtils.setField(service, "messageGateway", null);
            assertDoesNotThrow(() -> service.sendMessage("sg-no-gw", createP2PMessage("no gw")));
        }
    }

    @Nested
    @DisplayName("getMessages / getTodos 查询")
    class QueryTests {

        @Test
        void shouldReturnEmptyPageWhenNoMessages() {
            when(chatMessageRepository.findBySceneGroupIdOrderByCreateTimeDesc(anyString(), any(PageRequest.class)))
                    .thenReturn(org.springframework.data.domain.Page.empty());

            var result = service.getMessages("sg-empty", "user-1", null, null, null, 0, 10);
            assertTrue(result.getList().isEmpty());
        }

        @Test
        void shouldMapEntitiesToDTOs() {
            List<ChatMessage> entities = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                ChatMessage e = new ChatMessage(); e.setId(UUID.randomUUID().toString());
                e.setSceneGroupId("sg-query"); e.setContent("msg-" + i);
                e.setSenderId("sender-" + i); e.setSenderName("Sender" + i);
                e.setReceiverId("recv-" + i); e.setReceiverName("Recv" + i);
                e.setMessageType("P2P"); e.setStatus("SENT");
                e.setPriority(i + 1); e.setCreateTime(LocalDateTime.now());
                entities.add(e);
            }
            when(chatMessageRepository.findBySceneGroupIdOrderByCreateTimeDesc(eq("sg-query"), any(PageRequest.class)))
                    .thenReturn(new org.springframework.data.domain.PageImpl<>(entities));

            var result = service.getMessages("sg-query", "user-1", null, null, null, 0, 10);
            assertEquals(3, result.getList().size());
            assertEquals("msg-0", result.getList().get(0).getContent());
        }

        @Test
        void shouldReturnEmptyTodoListWhenNoTodos() {
            List<Todo> emptyList = Collections.emptyList();
            when(todoRepository.findBySceneGroupIdAndStatusOrderByCreateTimeDesc(anyString(), anyString()))
                    .thenReturn(emptyList);

            List<TodoDTO> result = service.getTodos("sg-todo", "user-1", "PENDING");
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Todo 操作 (内存模式)")
    class TodoOperationTests {

        private String newTodoId() { return UUID.randomUUID().toString(); }

        @BeforeEach
        void switchToMemoryMode() {
            ReflectionTestUtils.setField(service, "chatMessageRepository", null);
            ReflectionTestUtils.setField(service, "todoRepository", null);
        }

        @Test
        void shouldAcceptTodo() {
            String id = newTodoId();
            initTodoInStore(id, "PENDING");

            boolean accepted = service.acceptTodo("user-1", id);

            assertTrue(accepted);
            assertEquals("ACCEPTED", todoStoreGet(id).getStatus());
        }

        @Test
        void shouldRejectAcceptedTodo() {
            String id = newTodoId();
            initTodoInStore(id, "PENDING");
            service.acceptTodo("user-1", id);

            boolean rejected = service.rejectTodo("user-1", id, "不合适");

            assertTrue(rejected);
            assertEquals("REJECTED", todoStoreGet(id).getStatus());
        }

        @Test
        void shouldCompleteAcceptedTodo() {
            String id = newTodoId();
            initTodoInStore(id, "PENDING");
            service.acceptTodo("user-1", id);

            boolean completed = service.completeTodo("user-1", id);

            assertTrue(completed);
            assertEquals("COMPLETED", todoStoreGet(id).getStatus());
        }

        @Test
        void shouldDelegateTodo() {
            String id = newTodoId();
            initTodoInStore(id, "PENDING");
            service.acceptTodo("user-1", id);

            boolean delegated = service.delegateTodo("user-1", id, "user-2");

            assertTrue(delegated);
            assertEquals("DELEGATED", todoStoreGet(id).getStatus());
        }

        @Test
        void shouldReturnFalseForNonExistentTodo() {
            boolean result = service.acceptTodo("user-1", "non-existent-id");
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("BPM 桥接操作")
    class BpmBridgeTests {

        @Test
        void shouldAcceptBpmTaskViaBpmCoreService() throws BPMException {
            when(bpmCoreService.isAvailable()).thenReturn(true);
            doNothing().when(bpmCoreService).signReceive(anyString());

            boolean result = service.acceptTodo("user-1", "bpm-act-001");

            assertTrue(result);
            verify(bpmCoreService).signReceive("act-001");
        }

        @Test
        void shouldSkipBpmWhenServiceUnavailable() {
            when(bpmCoreService.isAvailable()).thenReturn(false);

            boolean result = service.acceptTodo("user-1", "bpm-act-002");

            assertFalse(result);
        }

        @Test
        void shouldCompleteBpmTask() throws BPMException {
            when(bpmCoreService.isAvailable()).thenReturn(true);
            doNothing().when(bpmCoreService).endTask(anyString());

            boolean result = service.completeTodo("user-1", "bpm-act-003");

            assertTrue(result);
            verify(bpmCoreService).endTask("act-003");
        }

        @Test
        void shouldRejectBpmTaskViaRouteBack() throws BPMException {
            when(bpmCoreService.isAvailable()).thenReturn(true);
            doNothing().when(bpmCoreService).routeBack(anyString(), isNull());

            boolean result = service.rejectTodo("user-1", "bpm-act-004", "不合适");

            assertTrue(result);
            verify(bpmCoreService).routeBack("act-004", null);
        }

        @Test
        void shouldDelegateBpmTaskViaRouteTo() throws BPMException {
            when(bpmCoreService.isAvailable()).thenReturn(true);
            doNothing().when(bpmCoreService).routeTo(anyString(), anyList(), anyList(), isNull());

            boolean result = service.delegateTodo("user-1", "bpm-act-005", "user-2");

            assertTrue(result);
            verify(bpmCoreService).routeTo(eq("act-005"), eq(Collections.emptyList()), eq(List.of("user-2")), isNull());
        }

        @Test
        void shouldHandleBpmExceptionGracefully() throws BPMException {
            when(bpmCoreService.isAvailable()).thenReturn(true);
            doThrow(new BPMException("BPM engine error")).when(bpmCoreService).signReceive(anyString());

            boolean result = service.acceptTodo("user-1", "bpm-act-error");

            assertFalse(result);
        }
    }
}
