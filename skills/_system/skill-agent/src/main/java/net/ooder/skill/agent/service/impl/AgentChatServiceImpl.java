package net.ooder.skill.agent.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.skill.agent.config.UnifiedInterfaceConfig.UnifiedInterfaceAdapter;
import net.ooder.skill.agent.dto.AgentChatMessageDTO;
import net.ooder.skill.agent.dto.SceneChatContextDTO;
import net.ooder.skill.agent.entity.ChatMessage;
import net.ooder.skill.agent.entity.Todo;
import net.ooder.skill.agent.repository.ChatMessageRepository;
import net.ooder.skill.agent.repository.TodoRepository;
import net.ooder.skill.agent.service.AgentChatService;
import net.ooder.skill.audit.annotation.Auditable;
import net.ooder.spi.im.ImDeliveryDriver;
import net.ooder.spi.im.model.MessageContent;
import net.ooder.spi.im.model.SendResult;
import net.ooder.spi.rag.RagEnhanceDriver;
import net.ooder.bpm.engine.BPMException;
import net.ooder.skill.workflow.core.BpmCoreService;
import net.ooder.skill.agent.dto.PageResult;
import net.ooder.skill.scene.dto.todo.TodoDTO;
import net.ooder.skill.tenant.context.TenantContext;

import net.ooder.scene.message.northbound.NorthboundMessageQueue;
import net.ooder.scene.message.queue.MessageEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AgentChatServiceImpl implements AgentChatService {

    private static final Logger log = LoggerFactory.getLogger(AgentChatServiceImpl.class);

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private ImDeliveryDriver messageGateway;

    @Autowired(required = false)
    private RagEnhanceDriver ragEnhanceDriver;

    @Autowired(required = false)
    private NorthboundMessageQueue northboundQueue;

    @Autowired(required = false)
    private BpmCoreService bpmCoreService;

    private Map<String, List<AgentChatMessageDTO>> messageStore = new ConcurrentHashMap<>();
    private Map<String, SceneChatContextDTO> contextStore = new ConcurrentHashMap<>();
    private Map<String, Set<String>> readStatus = new ConcurrentHashMap<>();
    private Map<String, TodoDTO> todoStore = new ConcurrentHashMap<>();
    private Map<String, List<String>> sceneTodoIndex = new ConcurrentHashMap<>();

    private boolean useDatabase() {
        return chatMessageRepository != null && todoRepository != null;
    }

    @Override
    public SceneChatContextDTO getChatContext(String sceneGroupId, String userId) {
        SceneChatContextDTO ctx = contextStore.computeIfAbsent(sceneGroupId + ":" + userId, k -> {
            SceneChatContextDTO c = new SceneChatContextDTO();
            c.setSceneGroupId(sceneGroupId);
            c.setCurrentUserId(userId);
            c.setActiveSessionCount(0);
            c.setUserOnline(true);
            return c;
        });
        return ctx;
    }

    @Override
    public PageResult<AgentChatMessageDTO> getMessages(String sceneGroupId, String userId, String type,
            Long before, Long after, int pageNum, int pageSize) {
        if (!useDatabase()) {
            List<AgentChatMessageDTO> all = messageStore.getOrDefault(sceneGroupId, Collections.emptyList());
            if (type != null) all = all.stream().filter(m -> type.equals(m.getMessageType())).collect(Collectors.toList());
            int start = pageNum * pageSize;
            int end = Math.min(start + pageSize, all.size());
            return new PageResult<>(start < all.size() ? all.subList(start, end) : Collections.emptyList(), all.size(), pageNum, pageSize);
        }
        PageRequest pr = PageRequest.of(pageNum, pageSize);
        Page<ChatMessage> ep = (type != null) ?
            chatMessageRepository.findBySceneGroupIdAndMessageTypeOrderByCreateTimeDesc(sceneGroupId, type, pr) :
            chatMessageRepository.findBySceneGroupIdOrderByCreateTimeDesc(sceneGroupId, pr);
        return new PageResult<>(ep.getContent().stream().map(this::toDTO).collect(Collectors.toList()),
            (int) ep.getTotalElements(), pageNum, pageSize);
    }

    @Override
    public AgentChatMessageDTO getMessage(String messageId) {
        if (!useDatabase()) return messageStore.values().stream()
            .flatMap(List::stream).filter(m -> messageId.equals(m.getMessageId())).findFirst().orElse(null);
        return chatMessageRepository.findById(messageId).map(this::toDTO).orElse(null);
    }

    @Override
    @Auditable(action = "send_message", resourceType = "AgentChat", logParams = true, logResult = true)
    public String sendMessage(String sceneGroupId, AgentChatMessageDTO message) {
        if (sceneGroupId == null || message == null) throw new IllegalArgumentException("Invalid args");
        message.setMessageId(UUID.randomUUID().toString());
        message.setCreateTime(System.currentTimeMillis());
        if (message.getSenderId() == null) message.setSenderId("system-agent");
        if (message.getSender() == null) message.setSender("System Agent");

        if (useDatabase()) {
            ChatMessage e = new ChatMessage();
            e.setId(message.getMessageId()); e.setSceneGroupId(sceneGroupId);
            e.setSenderId(message.getSenderId()); e.setSenderName(message.getSender());
            e.setSenderType(message.getSenderType() != null ? message.getSenderType() : "agent");
            e.setReceiverId(message.getReceiverId()); e.setReceiverName(message.getReceiverName());
            e.setMessageType(message.getMessageType() != null ? message.getMessageType() : "P2A");
            e.setContent(message.getContent());
            e.setPriority(message.getPriority() > 0 ? message.getPriority() : 5);
            e.setStatus("SENT");
            try { e.setMetadataJson(message.getMetadata() != null ? objectMapper.writeValueAsString(message.getMetadata()) : null); } catch (Exception ignored) {}
            chatMessageRepository.save(e);
        } else {
            messageStore.computeIfAbsent(sceneGroupId, k -> Collections.synchronizedList(new ArrayList<>())).add(message);
        }

        deliverViaNorthbound(sceneGroupId, message);
        deliverViaImGateway(sceneGroupId, message);

        log.info("[sendMessage] id={}, scene={}", message.getMessageId(), sceneGroupId);
        return message.getMessageId();
    }

    private void deliverViaNorthbound(String sceneGroupId, AgentChatMessageDTO m) {
        if (northboundQueue == null) return;
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("messageId", m.getMessageId());
            payload.put("content", m.getContent());
            payload.put("type", m.getMessageType());
            payload.put("sender", m.getSender());
            
            if ("P2P".equals(m.getMessageType()) && m.getReceiverId() != null) {
                northboundQueue.sendToUser(m.getSenderId(), m.getReceiverId(), payload);
            } else if (m.getReceiverId() != null) {
                northboundQueue.sendToAgent(m.getSenderId(), m.getReceiverId(), payload);
            }
        } catch (Exception ex) { log.warn("[Northbound] fail: {}", ex.getMessage()); }
    }

    private void deliverViaImGateway(String sceneGroupId, AgentChatMessageDTO m) {
        if (messageGateway == null || !"P2P".equals(m.getMessageType())) return;
        try {
            MessageContent mc = MessageContent.text(m.getContent());
            mc.setTitle(m.getSender() != null ? m.getSender() : "Agent");
            ImDeliveryDriver.DeliveryContext ctx = new ImDeliveryDriver.DeliveryContext(
                "websocket", m.getReceiverId() != null ? m.getReceiverId() : "broadcast",
                TenantContext.hasTenant() ? TenantContext.getTenantId() : null,
                TenantContext.getUserId(), Map.of("sceneGroupId", sceneGroupId, "messageId", m.getMessageId()));
            SendResult r = messageGateway.sendAsync(mc, ctx).get();
            if (r.isSuccess() && m.getMetadata() == null) { m.setMetadata(new HashMap<>()); }
            if (m.getMetadata() != null) { m.getMetadata().put("imDelivered", true); }
        } catch (Exception ex) { log.warn("[IM Gateway] fail: {}", ex.getMessage()); }
    }

    @Override
    public void markAsRead(String sceneGroupId, String userId, String messageId) {
        if (userId == null || messageId == null) return;
        if (!useDatabase()) { readStatus.computeIfAbsent(sceneGroupId + ":" + userId, k -> ConcurrentHashMap.newKeySet()).add(messageId); return; }
        chatMessageRepository.findById(messageId).ifPresent(msg -> { msg.setStatus("READ"); chatMessageRepository.save(msg); });
    }

    @Override
    public void markAllAsRead(String sceneGroupId, String userId, String type) {
        if (userId == null) return;
        if (!useDatabase()) {
            Set<String> rs = readStatus.computeIfAbsent(sceneGroupId + ":" + userId, k -> ConcurrentHashMap.newKeySet());
            messageStore.getOrDefault(sceneGroupId, Collections.emptyList()).forEach(m -> rs.add(m.getMessageId()));
            return;
        }
        List<ChatMessage> unread = (type != null) ?
            chatMessageRepository.findBySceneGroupIdAndMessageTypeAndStatusNot(sceneGroupId, type, "READ") :
            chatMessageRepository.findBySceneGroupIdAndStatusNot(sceneGroupId, "READ");
        unread.forEach(m -> { m.setStatus("READ"); chatMessageRepository.save(m); });
    }

    @Override
    public Map<String, Integer> getUnreadCounts(String sceneGroupId, String userId) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("total", 0);
        if (!useDatabase()) {
            Set<String> rs = readStatus.getOrDefault(sceneGroupId + ":" + userId, Collections.emptySet());
            long total = messageStore.getOrDefault(sceneGroupId, Collections.emptyList()).stream().filter(m -> !rs.contains(m.getMessageId())).count();
            counts.put("total", (int) total);
            return counts;
        }
        long total = chatMessageRepository.countBySceneGroupIdAndStatusNotInAndSenderIdNot(sceneGroupId, List.of("READ"), userId);
        counts.put("total", (int) total);
        return counts;
    }

    @Override
    public Object executeMessageAction(String sceneGroupId, String messageId, String userId, String actionId, Map<String, Object> actionData) {
        log.info("[executeMessageAction] action={}, messageId={}", actionId, messageId);
        return Map.of("success", true, "action", actionId, "messageId", messageId);
    }

    @Override
    public List<TodoDTO> getTodos(String sceneGroupId, String userId, String status) {
        if (!useDatabase()) {
            List<String> ids = sceneTodoIndex.getOrDefault(sceneGroupId, Collections.emptyList());
            List<TodoDTO> result = ids.stream().map(todoStore::get).filter(Objects::nonNull).collect(Collectors.toList());
            if (status != null) result = result.stream().filter(t -> status.equals(t.getStatus())).collect(Collectors.toList());
            return result;
        }
        List<Todo> entities = (status != null) ?
            todoRepository.findBySceneGroupIdAndStatusOrderByCreateTimeDesc(sceneGroupId, status) :
            todoRepository.findBySceneGroupIdOrderByCreateTimeDesc(sceneGroupId);
        return entities.stream().map(this::todoToDTO).collect(Collectors.toList());
    }

    @Override
    @Auditable(action = "accept_todo", resourceType = "Todo")
    public boolean acceptTodo(String userId, String todoId) {
        log.info("[acceptTodo] userId={}, todoId={}", userId, todoId);
        if (updateTodoStatus(todoId, "ACCEPTED")) return true;
        String bpmId = extractBpmActivityId(todoId);
        if (bpmId != null && bpmCoreService != null && bpmCoreService.isAvailable()) {
            try { bpmCoreService.signReceive(bpmId); return true; }
            catch (BPMException e) { log.warn("[acceptTodo] BPM error: {}", e.getMessage()); }
        }
        return false;
    }

    @Override
    @Auditable(action = "reject_todo", resourceType = "Todo")
    public boolean rejectTodo(String userId, String todoId, String reason) {
        log.info("[rejectTodo] userId={}, todoId={}, reason={}", userId, todoId, reason);
        if (updateTodoStatus(todoId, "REJECTED")) return true;
        String bpmId = extractBpmActivityId(todoId);
        if (bpmId != null && bpmCoreService != null && bpmCoreService.isAvailable()) {
            try { bpmCoreService.routeBack(bpmId, null); return true; }
            catch (BPMException e) { log.warn("[rejectTodo] BPM error: {}", e.getMessage()); }
        }
        return false;
    }

    @Override
    @Auditable(action = "delegate_todo", resourceType = "Todo")
    public boolean delegateTodo(String userId, String todoId, String toUserId) {
        log.info("[delegateTodo] {} -> {}, todo={}", userId, toUserId, todoId);
        if (updateTodoStatus(todoId, "DELEGATED")) return true;
        String bpmId = extractBpmActivityId(todoId);
        if (bpmId != null && bpmCoreService != null && bpmCoreService.isAvailable()) {
            try { bpmCoreService.routeTo(bpmId, Collections.emptyList(), List.of(toUserId), null); return true; }
            catch (BPMException e) { log.warn("[delegateTodo] BPM error: {}", e.getMessage()); }
        }
        return false;
    }

    @Override
    @Auditable(action = "complete_todo", resourceType = "Todo")
    public boolean completeTodo(String userId, String todoId) {
        log.info("[completeTodo] userId={}, todoId={}", userId, todoId);
        if (updateTodoStatus(todoId, "COMPLETED")) return true;
        String bpmId = extractBpmActivityId(todoId);
        if (bpmId != null && bpmCoreService != null && bpmCoreService.isAvailable()) {
            try { bpmCoreService.endTask(bpmId); return true; }
            catch (BPMException e) { log.warn("[completeTodo] BPM error: {}", e.getMessage()); }
        }
        return false;
    }

    @Override
    public void addReaction(String messageId, String userId, String emoji) {
        log.info("[addReaction] messageId={}, user={}, emoji={}", messageId, userId, emoji);
    }

    @Override
    public void removeReaction(String messageId, String userId, String emoji) {
        log.info("[removeReaction] messageId={}, user={}, emoji={}", messageId, userId, emoji);
    }

    private boolean updateTodoStatus(String todoId, String status) {
        if (!useDatabase()) { TodoDTO t = todoStore.get(todoId); if (t != null) { t.setStatus(status); t.setUpdateTime(System.currentTimeMillis()); return true; } return false; }
        Optional<Todo> opt = todoRepository.findById(todoId);
        if (opt.isPresent()) { Todo e = opt.get(); e.setStatus(status); e.setUpdateTime(LocalDateTime.now()); todoRepository.save(e); return true; }
        return false;
    }

    public void registerNorthboundHandler(net.ooder.scene.message.northbound.NorthboundMessageHandler handler) {
        log.info("[registerNorthboundHandler] handler={}", handler.getClass().getSimpleName());
    }

    public String sendWithRagEnhancement(String sceneGroupId, AgentChatMessageDTO message, List<String> knowledgeBaseIds) {
        if (ragEnhanceDriver == null || !ragEnhanceDriver.isAvailable() || knowledgeBaseIds == null || knowledgeBaseIds.isEmpty()) {
            return this.sendMessage(sceneGroupId, message);
        }
        try {
            String enhanced = ragEnhanceDriver.enhancePrompt(message.getContent(), sceneGroupId, knowledgeBaseIds);
            AgentChatMessageDTO em = new AgentChatMessageDTO(); em.setSenderId(message.getSenderId());
            em.setSender(message.getSender()); em.setSenderType(message.getSenderType());
            em.setReceiverId(message.getReceiverId()); em.setReceiverName(message.getReceiverName());
            em.setMessageType("P2A"); em.setContent(enhanced); em.setPriority(message.getPriority());
            em.setMetadata(new HashMap<>()); em.getMetadata().put("ragEnhanced", true);
            em.getMetadata().put("knowledgeBaseIds", knowledgeBaseIds);
            log.info("[sendWithRAG] original={} enhanced={}", message.getContent().length(), enhanced.length());
            return this.sendMessage(sceneGroupId, em);
        } catch (Exception e) { log.warn("[sendWithRAG] failed: {}", e.getMessage()); return this.sendMessage(sceneGroupId, message); }
    }

    private AgentChatMessageDTO toDTO(ChatMessage e) {
        AgentChatMessageDTO d = new AgentChatMessageDTO();
        d.setMessageId(e.getId()); d.setSceneGroupId(e.getSceneGroupId());
        d.setSenderId(e.getSenderId()); d.setSender(e.getSenderName());
        d.setSenderType(e.getSenderType()); d.setReceiverId(e.getReceiverId());
        d.setReceiverName(e.getReceiverName()); d.setMessageType(e.getMessageType());
        d.setContent(e.getContent()); d.setPriority(e.getPriority() != null ? e.getPriority() : 0);
        d.setStatus(e.getStatus().toLowerCase());
        d.setCreateTime(e.getCreateTime() != null ? e.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : System.currentTimeMillis());
        if (e.getMetadataJson() != null && !e.getMetadataJson().isEmpty()) {
            try { d.setMetadata(objectMapper.readValue(e.getMetadataJson(), Map.class)); } catch (Exception ex) { d.setMetadata(Collections.emptyMap()); }
        }
        return d;
    }

    private TodoDTO todoToDTO(Todo e) {
        TodoDTO d = new TodoDTO(); d.setId(e.getId()); d.setTitle(e.getTitle());
        d.setDescription(e.getDescription()); d.setSceneGroupId(e.getSceneGroupId());
        d.setAssignee(e.getAssignee()); d.setCreator(e.getCreator());
        d.setToUser(e.getToUser()); d.setFromUser(e.getFromUser());
        d.setDeadline(e.getDeadline() != null ? e.getDeadline().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
        d.setPriority(e.getPriority()); d.setStatus(e.getStatus());
        d.setCreateTime(e.getCreateTime() != null ? e.getCreateTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
        if (e.getMetadataJson() != null && !e.getMetadataJson().isEmpty()) {
            try { d.setMetadata(objectMapper.readValue(e.getMetadataJson(), Map.class)); } catch (Exception ex) { d.setMetadata(Collections.emptyMap()); }
        }
        return d;
    }

    private String extractBpmActivityId(String todoId) {
        return (todoId != null && todoId.startsWith("bpm-")) ? todoId.substring(4) : null;
    }
}
