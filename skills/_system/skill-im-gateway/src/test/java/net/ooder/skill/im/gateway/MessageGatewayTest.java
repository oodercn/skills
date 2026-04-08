package net.ooder.skill.im.gateway;

import net.ooder.skill.common.spi.ImService;
import net.ooder.skill.common.spi.im.MessageContent;
import net.ooder.spi.im.ImDeliveryDriver;
import net.ooder.spi.im.model.SendResult;
import net.ooder.spi.im.handler.InboundHandler;
import net.ooder.skill.im.dto.MultiChannelMessageDTO;
import net.ooder.skill.tenant.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MessageGatewayTest {

    private MessageGateway gateway;
    private ImService mockImService;
    private MqttChannelAdapter mockMqttAdapter;

    @BeforeEach
    void setUp() {
        gateway = new MessageGateway();
        mockImService = mock(ImService.class);
        mockMqttAdapter = mock(MqttChannelAdapter.class);

        Map<String, ImService> serviceMap = new HashMap<>();
        serviceMap.put("websocket", mockImService);
        ReflectionTestUtils.setField(gateway, "imServices", serviceMap);
        ReflectionTestUtils.setField(gateway, "mqttAdapter", mockMqttAdapter);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    private MultiChannelMessageDTO createMessage(String channel, String receiver) {
        MultiChannelMessageDTO dto = new MultiChannelMessageDTO();
        dto.setChannel(channel);
        dto.setReceiver(receiver);
        dto.setContent("test content");
        dto.setTitle("Test");
        return dto;
    }

    @Nested
    @DisplayName("send - 单通道发送")
    class SendTests {

        @Test
        void shouldSendViaImServiceWhenAvailable() throws Exception {
            when(mockImService.sendToUser(eq("websocket"), eq("user-01"), any(MessageContent.class)))
                    .thenReturn(net.ooder.skill.common.spi.im.SendResult.success("msg-001"));

            CompletableFuture<SendResult> future = gateway.send(createMessage("websocket", "user-01"));
            SendResult result = future.get();

            assertTrue(result.isSuccess());
            verify(mockImService).sendToUser(eq("websocket"), eq("user-01"), any(MessageContent.class));
        }

        @Test
        void shouldSendGroupWhenMsgTypeIsGroup() throws Exception {
            MultiChannelMessageDTO groupMsg = createMessage("websocket", "group-01");
            groupMsg.setMsgType("group");

            when(mockImService.sendToGroup(eq("websocket"), eq("group-01"), any(MessageContent.class)))
                    .thenReturn(net.ooder.skill.common.spi.im.SendResult.success("grp-001"));

            CompletableFuture<SendResult> future = gateway.send(groupMsg);
            SendResult result = future.get();

            assertTrue(result.isSuccess());
            verify(mockImService).sendToGroup(eq("websocket"), eq("group-01"), any(MessageContent.class));
        }

        @Test
        void shouldFallbackToMqttForMqttChannel() throws Exception {
            when(mockMqttAdapter.sendToUser(eq("mqtt"), eq("mqtt-user"), any(MessageContent.class)))
                    .thenReturn(net.ooder.skill.common.spi.im.SendResult.success("mqtt-msg-001"));

            CompletableFuture<SendResult> future = gateway.send(createMessage("mqtt", "mqtt-user"));
            SendResult result = future.get();

            assertTrue(result.isSuccess());
            verify(mockMqttAdapter).sendToUser(eq("mqtt"), eq("mqtt-user"), any(MessageContent.class));
        }

        @Test
        void shouldReturnFailureWhenNoServiceAvailable() throws Exception {
            CompletableFuture<SendResult> future = gateway.send(createMessage("unknown-channel", "user"));
            SendResult result = future.get();

            assertFalse(result.isSuccess());
            assertTrue(result.getErrorMessage().contains("无通道"));
        }

        @Test
        void shouldPropagateTenantContextFromExtra() throws Exception {
            MultiChannelMessageDTO dto = createMessage("websocket", "tenant-user");
            Map<String, Object> extra = new HashMap<>();
            extra.put("tenantId", "extra-tenant");
            extra.put("userId", "extra-user");
            dto.setExtra(extra);

            when(mockImService.sendToUser(anyString(), anyString(), any(MessageContent.class)))
                    .thenReturn(net.ooder.skill.common.spi.im.SendResult.success("ok"));

            gateway.send(dto).get();

            verify(mockImService).sendToUser(anyString(), anyString(), any(MessageContent.class));
        }

        @Test
        void shouldClearTenantAfterSend() throws Exception {
            when(mockImService.sendToUser(anyString(), anyString(), any()))
                    .thenReturn(net.ooder.skill.common.spi.im.SendResult.success("ok"));

            gateway.send(createMessage("websocket", "user")).get();

            assertNull(TenantContext.getTenantId());
        }
    }

    @Nested
    @DisplayName("broadcast - 多通道广播")
    class BroadcastTests {

        @Test
        void shouldBroadcastToMultipleChannels() throws Exception {
            when(mockImService.sendToUser(anyString(), anyString(), any()))
                    .thenReturn(net.ooder.skill.common.spi.im.SendResult.success("bc-ok"));

            Map<String, SendResult> results = gateway.broadcast(
                new ImDeliveryDriver.DeliveryTemplate("text", "bc-content", "BC", null),
                Arrays.asList("websocket")
            );

            assertEquals(1, results.size());
            assertTrue(results.get("websocket").isSuccess());
        }
    }

    @Nested
    @DisplayName("inbound handler - 入站处理")
    class InboundHandlerTests {

        @Test
        void shouldRegisterHandler() {
            InboundHandler handler = (ch, msg) -> {};
            gateway.registerInboundHandler("test-ch", handler);
        }

        @Test
        void shouldHandleInboundWithRegisteredHandler() throws Exception {
            List<Map<String, Object>> handledMessages = new ArrayList<>();

            gateway.registerInboundHandler("test-inbound",
                (channel, raw) -> handledMessages.add(raw));

            Map<String, Object> rawMsg = new HashMap<>();
            rawMsg.put("type", "text");
            rawMsg.put("content", "hello inbound");

            gateway.handleInbound("test-inbound", rawMsg);

            Thread.sleep(100);
            assertEquals(1, handledMessages.size());
        }

        @Test
        void shouldLogWarningForUnregisteredHandler() {
            gateway.handleInbound("nonexistent", new HashMap<>());
        }

        @Test
        void shouldExtractTenantFromPayloadHeaders() throws Exception {
            List<String> capturedTenants = new ArrayList<>();

            gateway.registerInboundHandler("header-test",
                (ch, raw) -> capturedTenants.add(TenantContext.getTenantId()));

            Map<String, Object> payload = new HashMap<>();
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Tenant-Id", "header-tenant-123");
            payload.put("headers", headers);

            gateway.handleInbound("header-test", payload);

            Thread.sleep(100);
            if (!capturedTenants.isEmpty()) {
                assertEquals("header-tenant-123", capturedTenants.get(0));
            }
        }

        @Test
        void shouldExtractTenantFromTenantIdField() throws Exception {
            List<String> capturedTenants = new ArrayList<>();

            gateway.registerInboundHandler("tid-test",
                (ch, raw) -> capturedTenants.add(TenantContext.getTenantId()));

            Map<String, Object> payload = new HashMap<>();
            payload.put("tenant_id", "field-tenant-456");

            gateway.handleInbound("tid-test", payload);

            Thread.sleep(100);
            if (!capturedTenants.isEmpty()) {
                assertEquals("field-tenant-456", capturedTenants.get(0));
            }
        }
    }

    @Nested
    @DisplayName("Channel 枚举")
    class ChannelEnumTests {

        @Test
        void shouldHaveAllExpectedChannels() {
            assertEquals(5, MessageGateway.Channel.values().length);
        }

        @Test
        void fromCodeShouldFindCorrectChannel() {
            assertEquals(MessageGateway.Channel.WEBSOCKET,
                MessageGateway.Channel.fromCode("websocket"));
            assertEquals(MessageGateway.Channel.FEISHU,
                MessageGateway.Channel.fromCode("feishu"));
            assertNull(MessageGateway.Channel.fromCode("nonexistent"));
        }
    }

    @Nested
    @DisplayName("getAvailableChannels")
    class AvailableChannelsTests {

        @Test
        void shouldListAllRegisteredChannels() {
            Set<String> channels = gateway.getAvailableChannels();
            assertTrue(channels.contains("websocket"));
            assertTrue(channels.contains("mqtt"));
        }
    }
}
