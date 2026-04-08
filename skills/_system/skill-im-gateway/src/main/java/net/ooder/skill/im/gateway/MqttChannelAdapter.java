package net.ooder.skill.im.gateway;

import net.ooder.skill.audit.annotation.Auditable;
import net.ooder.skill.common.spi.ImService;
import net.ooder.skill.common.spi.im.MessageContent;
import net.ooder.skill.common.spi.im.SendResult;
import net.ooder.skill.tenant.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

@ConditionalOnProperty(name = "mqtt.enabled", havingValue = "true")
@Service
public class MqttChannelAdapter implements ImService {

    private static final Logger log = LoggerFactory.getLogger(MqttChannelAdapter.class);
    private static final String PLATFORM_MQTT = "mqtt";

    @Value("${mqtt.broker-url:tcp://localhost:1883}")
    private String brokerUrl;

    @Value("${mqtt.client-id:agent-chat-gateway}")
    private String clientId;

    @Value("${mqtt.topic-prefix:agent/chat/}")
    private String topicPrefix;

    @Autowired(required = false)
    private RagEnhancer ragEnhancer;

    private volatile boolean connected = false;
    private final AtomicLong messageCounter = new AtomicLong(0);
    private final Map<String, BiConsumer<String, byte[]>> subscribers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            log.info("[MQTT] Initializing adapter, broker: {}, clientId: {}", brokerUrl, clientId);
            connected = true;
            log.info("[MQTT] Adapter ready (simulation mode - requires MQTT client library)");
        } catch (Exception e) {
            log.error("[MQTT] Init failed: {}", e.getMessage(), e);
            connected = false;
        }
    }

    @Override
    @Auditable(action = "mqtt_send_user", resourceType = "IMMessage", logParams = true)
    public SendResult sendToUser(String platform, String userId, MessageContent content) {
        if (!connected) return SendResult.failure("MQTT未连接");
        if (!PLATFORM_MQTT.equals(platform)) return SendResult.failure("不支持的平台: " + platform);

        try {
            String tenantScopedTopic = buildTenantScopedTopic("direct", userId);
            byte[] payload = buildPayload(content, userId);

            if (ragEnhancer != null && TenantContext.hasTenant()) {
                String enhanced = ragEnhancer.enanceForIm(content.getContent(), TenantContext.getTenantId());
                if (enhanced != null) {
                    content.setContent(enhanced);
                    payload = buildPayload(content, userId);
                }
            }

            publish(tenantScopedTopic, payload);
            log.debug("[MQTT] Published to user {} via {} (tenant={})", userId, tenantScopedTopic, TenantContext.getTenantId());
            return SendResult.success(generateMessageId());
        } catch (Exception e) {
            log.error("[MQTT] Send to user failed: {}", e.getMessage());
            return SendResult.failure("发送失败: " + e.getMessage());
        }
    }

    @Override
    @Auditable(action = "mqtt_send_group", resourceType = "IMMessage", logParams = true)
    public SendResult sendToGroup(String platform, String groupId, MessageContent content) {
        if (!connected) return SendResult.failure("MQTT未连接");
        if (!PLATFORM_MQTT.equals(platform)) return SendResult.failure("不支持的平台: " + platform);

        try {
            String tenantScopedTopic = buildTenantScopedTopic("group", groupId);
            byte[] payload = buildPayload(content, groupId);

            publish(tenantScopedTopic, payload);
            log.debug("[MQTT] Published to group {} via {} (tenant={})", groupId, tenantScopedTopic, TenantContext.getTenantId());
            return SendResult.success(generateMessageId());
        } catch (Exception e) {
            log.error("[MQTT] Send to group failed: {}", e.getMessage());
            return SendResult.failure("发送失败: " + e.getMessage());
        }
    }

    @Override
    public SendResult sendDing(String userId, String title, String content) {
        return SendResult.failure("MQTT通道不支持DING消息");
    }

    @Override
    public SendResult sendMarkdown(String platform, String userId, String title, String markdown) {
        return sendToUser(platform, userId, MessageContent.markdown(title, markdown));
    }

    @Override
    public List<String> getAvailablePlatforms() {
        return connected ? Collections.singletonList(PLATFORM_MQTT) : Collections.emptyList();
    }

    @Override
    public boolean isPlatformAvailable(String platform) {
        return PLATFORM_MQTT.equals(platform) && connected;
    }

    public void subscribe(String topicPattern, MessageCallback callback) {
        subscribers.put(topicPattern, (topic, payload) ->
            callback.onMessage(topic, new String(payload, StandardCharsets.UTF_8)));
        log.info("[MQTT] Subscribed to: {}", topicPattern);
    }

    public void unsubscribe(String topicPattern) {
        subscribers.remove(topicPattern);
        log.info("[MQTT] Unsubscribed from: {}", topicPattern);
    }

    public boolean isConnected() { return connected; }

    private String buildTenantScopedTopic(String channelType, String targetId) {
        String tenantId = TenantContext.hasTenant() ? TenantContext.getTenantId() : "default";
        return topicPrefix + tenantId + "/" + channelType + "/" + targetId;
    }

    private void publish(String topic, byte[] payload) {
        for (BiConsumer<String, byte[]> sub : subscribers.values()) {
            try { sub.accept(topic, payload); } catch (Exception ignored) {}
        }
    }

    private byte[] buildPayload(MessageContent content, String targetId) {
        String typeStr = content.getType() != null ? content.getType().name().toLowerCase() : "text";
        String json = String.format("{\"type\":\"%s\",\"title\":\"%s\",\"content\":\"%s\",\"target\":\"%s\",\"tenant\":\"%s\",\"ts\":%d}",
            typeStr,
            escapeJson(content.getTitle()),
            escapeJson(content.getContent()),
            targetId,
            TenantContext.hasTenant() ? TenantContext.getTenantId() : "",
            System.currentTimeMillis()
        );
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private String generateMessageId() {
        return "mqtt-" + System.currentTimeMillis() + "-" + messageCounter.incrementAndGet();
    }

    public interface MessageCallback {
        void onMessage(String topic, String message);
    }

    @PreDestroy
    public void destroy() {
        connected = false;
        subscribers.clear();
        log.info("[MQTT] Adapter shut down");
    }
}
