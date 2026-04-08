package net.ooder.skill.im.gateway;

import net.ooder.skill.common.spi.im.MessageContent;
import net.ooder.skill.common.spi.im.SendResult;
import net.ooder.skill.rag.RagPipeline;
import net.ooder.skill.tenant.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MqttChannelAdapterTest {

    private MqttChannelAdapter adapter;
    private RagEnhancer mockRagEnhancer;

    @BeforeEach
    void setUp() {
        adapter = new MqttChannelAdapter();
        mockRagEnhancer = mock(RagEnhancer.class);
        ReflectionTestUtils.setField(adapter, "ragEnhancer", mockRagEnhancer);
        ReflectionTestUtils.setField(adapter, "topicPrefix", "agent/chat/");
        ReflectionTestUtils.setField(adapter, "connected", true);
    }

    @AfterEach
    void cleanUp() {
        TenantContext.clear();
    }

    @Nested
    @DisplayName("sendToUser - 发送用户消息")
    class SendToUserTests {

        @Test
        void shouldReturnFailureWhenDisconnected() {
            ReflectionTestUtils.setField(adapter, "connected", false);

            SendResult result = adapter.sendToUser("mqtt", "user-001",
                MessageContent.text("hello"));

            assertFalse(result.isSuccess());
            assertTrue(result.getError().contains("未连接"));
        }

        @Test
        void shouldReturnFailureForUnsupportedPlatform() {
            SendResult result = adapter.sendToUser("wechat", "user-001",
                MessageContent.text("hello"));

            assertFalse(result.isSuccess());
            assertTrue(result.getError().contains("不支持"));
        }

        @Test
        void shouldSucceedForMqttPlatform() {
            SendResult result = adapter.sendToUser("mqtt", "user-001",
                MessageContent.text("test message"));

            assertTrue(result.isSuccess());
            assertNotNull(result.getMessageId());
            assertTrue(result.getMessageId().startsWith("mqtt-"));
        }

        @Test
        void shouldIncludeTenantIdInTopicWhenSet() {
            TenantContext.setTenantId("tenant-abc");

            List<String> publishedTopics = new CopyOnWriteArrayList<>();
            ReflectionTestUtils.setField(adapter, "subscribers",
                java.util.Map.of("test", (java.util.function.BiConsumer<String, byte[]>) (t, p) -> publishedTopics.add(t)));

            adapter.sendToUser("mqtt", "user-001", MessageContent.text("msg"));

            assertTrue(publishedTopics.stream().anyMatch(t -> t.contains("tenant-abc")));
            assertTrue(publishedTopics.stream().anyMatch(t -> t.contains("direct/user-001")));
        }

        @Test
        void shouldUseDefaultTopicWhenNoTenant() {
            List<String> publishedTopics = new CopyOnWriteArrayList<>();
            ReflectionTestUtils.setField(adapter, "subscribers",
                java.util.Map.of("test", (java.util.function.BiConsumer<String, byte[]>) (t, p) -> publishedTopics.add(t)));

            adapter.sendToUser("mqtt", "user-002", MessageContent.text("no tenant msg"));

            assertTrue(publishedTopics.stream().anyMatch(t -> t.contains("default/direct/")));
        }
    }

    @Nested
    @DisplayName("sendToGroup - 发送群组消息")
    class SendToGroupTests {

        @Test
        void shouldSucceedForMqttGroupSend() {
            SendResult result = adapter.sendToGroup("mqtt", "group-001",
                MessageContent.text("group message"));

            assertTrue(result.isSuccess());
            assertNotNull(result.getMessageId());
        }

        @Test
        void shouldIncludeGroupInTopicPath() {
            TenantContext.setTenantId("corp-xyz");

            List<String> topics = new CopyOnWriteArrayList<>();
            ReflectionTestUtils.setField(adapter, "subscribers",
                java.util.Map.of("sub", (java.util.function.BiConsumer<String, byte[]>) (t, p) -> topics.add(t)));

            adapter.sendToGroup("mqtt", "team-alpha", MessageContent.text("team msg"));

            assertTrue(topics.stream().anyMatch(t -> t.contains("group/team-alpha")));
        }
    }

    @Nested
    @DisplayName("RAG 增强集成")
    class RAGIntegrationTests {

        @Test
        void shouldCallRagEnhancerWhenAvailableAndTenantSet() {
            TenantContext.setTenantId("rag-tenant");
            when(mockRagEnhancer.enanceForIm(anyString(), eq("rag-tenant")))
                    .thenReturn("[RAG增强] 原始问题 + 参考资料");

            SendResult result = adapter.sendToUser("mqtt", "user-rag",
                MessageContent.text("什么是微服务架构？"));

            assertTrue(result.isSuccess());
            verify(mockRagEnhancer).enanceForIm(eq("什么是微服务架构？"), eq("rag-tenant"));
        }

        @Test
        void shouldSkipRagWhenNoTenantContext() {
            SendResult result = adapter.sendToUser("mqtt", "user-no-tenant",
                MessageContent.text("普通问题"));

            assertTrue(result.isSuccess());
            verify(mockRagEnhancer, never()).enanceForIm(anyString(), anyString());
        }

        @Test
        void shouldSkipRagWhenEnhancerReturnsNull() {
            TenantContext.setTenantId("tenant-null");
            when(mockRagEnhancer.enanceForIm(anyString(), anyString())).thenReturn(null);

            SendResult result = adapter.sendToUser("mqtt", "user-null-enhance",
                MessageContent.text("原始消息"));

            assertTrue(result.isSuccess());
            verify(mockRagEnhancer).enanceForIm(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("辅助方法")
    class UtilityMethodTests {

        @Test
        void sendDingShouldAlwaysFail() {
            SendResult result = adapter.sendDing("u1", "title", "content");
            assertFalse(result.isSuccess());
            assertTrue(result.getError().contains("不支持"));
        }

        @Test
        void sendMarkdownShouldDelegateToSendToUser() {
            SendResult result = adapter.sendMarkdown("mqtt", "user-md", "MD Title", "# Markdown");
            assertTrue(result.isSuccess());
        }

        @Test
        void getAvailablePlatformsShouldReturnMqttWhenConnected() {
            List<String> platforms = adapter.getAvailablePlatforms();
            assertEquals(1, platforms.size());
            assertEquals("mqtt", platforms.get(0));
        }

        @Test
        void getAvailablePlatformsShouldReturnEmptyWhenDisconnected() {
            ReflectionTestUtils.setField(adapter, "connected", false);
            List<String> platforms = adapter.getAvailablePlatforms();
            assertTrue(platforms.isEmpty());
        }

        @Test
        void isPlatformAvailableShouldCheckBothPlatformAndConnection() {
            assertTrue(adapter.isPlatformAvailable("mqtt"));
            assertFalse(adapter.isPlatformAvailable("wechat"));

            ReflectionTestUtils.setField(adapter, "connected", false);
            assertFalse(adapter.isPlatformAvailable("mqtt"));
        }

        @Test
        void subscribeAndUnsubscribeLifecycle() {
            List<String> logMessages = new ArrayList<>();
            MqttChannelAdapter.MessageCallback callback = (t, m) -> logMessages.add(m);

            adapter.subscribe("agent/chat/+/direct/#", callback);

            assertTrue(adapter.isConnected());

            adapter.unsubscribe("agent/chat/+/direct/#");

            assertTrue(adapter.isConnected());
        }

        @Test
        void initShouldSetConnectedTrue() {
            adapter.init();
            assertTrue(adapter.isConnected());
        }

        @Test
        void destroyShouldClearSubscribersAndDisconnect() {
            adapter.destroy();
            assertFalse(adapter.isConnected());
        }
    }

    @Nested
    @DisplayName("Payload 构建验证")
    class PayloadTests {

        @Test
        void payloadShouldContainTargetAndTimestamp() {
            TenantContext.setTenantId("payload-test");

            List<byte[]> payloads = new CopyOnWriteArrayList<>();
            ReflectionTestUtils.setField(adapter, "subscribers",
                java.util.Map.of("p", (java.util.function.BiConsumer<String, byte[]>) (t, p) -> payloads.add(p)));

            adapter.sendToUser("mqtt", "target-user", MessageContent.text("payload content"));

            assertFalse(payloads.isEmpty());
            String json = new String(payloads.get(0), java.nio.charset.StandardCharsets.UTF_8);
            assertTrue(json.contains("target-user"));
            assertTrue(json.contains("payload-test"));
            assertTrue(json.contains("\"ts\":"));
        }
    }
}
