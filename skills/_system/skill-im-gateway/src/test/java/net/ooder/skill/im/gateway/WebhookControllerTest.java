package net.ooder.skill.im.gateway;

import net.ooder.skill.tenant.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WebhookControllerTest {

    private WebhookController controller;
    private MessageGateway mockGateway;

    @BeforeEach
    void setUp() {
        controller = new WebhookController();
        mockGateway = mock(net.ooder.skill.im.gateway.MessageGateway.class);
        ReflectionTestUtils.setField(controller, "messageGateway", mockGateway);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("feishuCallback - 飞书回调")
    class FeishuTests {

        @Test
        void shouldReturnOkForFeishuEvent() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("event_type", "im.message.receive_v1");
            payload.put("app_id", "cli_xxxxxxxxxx");

            ResponseEntity<String> response = controller.feishuCallback(payload);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("\"code\":0"));
            verify(mockGateway).handleInbound(eq("feishu"), anyMap());
        }

        @Test
        void shouldExtractTenantFromFeishuAppId() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("event_type", "test");
            payload.put("app_id", "cli_abcd1234");

            controller.feishuCallback(payload);

            verify(mockGateway).handleInbound(eq("feishu"), anyMap());
        }

        @Test
        void shouldExtractTenantFromFeishuTenantField() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("event_type", "test");
            payload.put("tenant_id", "feishu-custom-tenant");

            controller.feishuCallback(payload);

            verify(mockGateway).handleInbound(eq("feishu"), anyMap());
        }
    }

    @Nested
    @DisplayName("wecomCallback - 企业微信回调")
    class WeComTests {

        @Test
        void shouldReturnSuccessForWeComEvent() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("MsgType", "text");

            ResponseEntity<String> response = controller.wecomCallback(payload);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("success", response.getBody());
            verify(mockGateway).handleInbound(eq("wecom"), anyMap());
        }
    }

    @Nested
    @DisplayName("dingtalkCallback - 钉钉回调")
    class DingTalkTests {

        @Test
        void shouldReturnSuccessForDingTalkEvent() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("msgtype", "text");

            ResponseEntity<String> response = controller.dingtalkCallback(payload);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("success"));
            verify(mockGateway).handleInbound(eq("dingtalk"), anyMap());
        }

        @Test
        void shouldExtractTenantFromDingTalkCorpId() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("msgtype", "text");
            payload.put("corp_id", "dingcorp_abcdef12");

            controller.dingtalkCallback(payload);

            verify(mockGateway).handleInbound(eq("dingtalk"), anyMap());
        }
    }

    @Nested
    @DisplayName("mqttPublish - MQTT桥接发布")
    class MqttPublishTests {

        @Test
        void shouldAcceptMqttBridgePublish() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("topic", "agent/chat/default/direct/user1");
            payload.put("payload", "{\"content\":\"hello\"}");

            ResponseEntity<String> response = controller.mqttPublish(payload);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("published"));
            verify(mockGateway).handleInbound(eq("mqtt"), anyMap());
        }
    }

    @Nested
    @DisplayName("health - 健康检查")
    class HealthTests {

        @Test
        void shouldReturnUpStatus() {
            Set<String> availableChannels = new HashSet<>(Arrays.asList("websocket", "mqtt"));
            when(mockGateway.getAvailableChannels()).thenReturn(availableChannels);

            ResponseEntity<Map<String, Object>> response = controller.health();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            Map<String, Object> body = response.getBody();
            assertNotNull(body);
            assertEquals("UP", body.get("status"));
            assertNotNull(body.get("channels"));
            assertNotNull(body.get("timestamp"));
        }

        @Test
        void healthShouldIncludeCurrentTenant() {
            TenantContext.setTenantId("health-tenant");
            when(mockGateway.getAvailableChannels()).thenReturn(new HashSet<>());

            ResponseEntity<Map<String, Object>> response = controller.health();

            assertEquals("health-tenant", response.getBody().get("currentTenant"));
        }

        @Test
        void healthShouldReturnNullTenantWhenNotSet() {
            when(mockGateway.getAvailableChannels()).thenReturn(new HashSet<>());

            ResponseEntity<Map<String, Object>> response = controller.health();

            assertNull(response.getBody().get("currentTenant"));
        }
    }

    @Nested
    @DisplayName("租户上下文清理")
    class TenantCleanupTests {

        @Test
        void shouldClearTenantAfterFeishuCallback() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("event_type", "test");
            payload.put("app_id", "cli_test1234");

            controller.feishuCallback(payload);

            assertNull(TenantContext.getTenantId());
        }

        @Test
        void shouldClearTenantAfterWecomCallback() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("MsgType", "text");

            controller.wecomCallback(payload);

            assertNull(TenantContext.getTenantId());
        }

        @Test
        void shouldClearTenantAfterDingtalkCallback() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("msgtype", "text");

            controller.dingtalkCallback(payload);

            assertNull(TenantContext.getTenantId());
        }

        @Test
        void shouldClearTenantAfterMqttPublish() {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("topic", "test");

            controller.mqttPublish(payload);

            assertNull(TenantContext.getTenantId());
        }
    }
}
