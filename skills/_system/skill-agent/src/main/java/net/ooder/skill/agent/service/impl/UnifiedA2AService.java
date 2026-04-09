package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.config.UnifiedInterfaceConfig.UnifiedInterfaceAdapter;
import net.ooder.scene.a2a.A2AMessageRouter;
import net.ooder.scene.a2a.A2AProtocolService;
import net.ooder.scene.a2a.A2AMessage;
import net.ooder.scene.message.queue.MessageQueueService;
import net.ooder.scene.message.queue.MessageEnvelope;
import net.ooder.scene.session.unified.UnifiedSessionManager;
import net.ooder.scene.session.unified.UnifiedSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UnifiedA2AService {

    private static final Logger log = LoggerFactory.getLogger(UnifiedA2AService.class);

    @Autowired
    private UnifiedInterfaceAdapter unifiedAdapter;

    private final Map<String, List<java.util.function.Consumer<Map<String, Object>>>> subscriptions = new ConcurrentHashMap<>();

    public boolean isA2AAvailable() {
        return unifiedAdapter.hasA2AProtocolService() && unifiedAdapter.hasA2AMessageRouter();
    }

    public boolean isSessionManagerAvailable() {
        return unifiedAdapter.hasSessionManager();
    }

    public boolean isMessageQueueAvailable() {
        return unifiedAdapter.hasMessageQueueService();
    }

    public void sendMessage(String fromAgent, String toAgent, String messageType,
            Map<String, Object> content) {
        log.info("[sendMessage] {} -> {}: {}", fromAgent, toAgent, messageType);

        if (!isA2AAvailable()) {
            log.warn("[sendMessage] A2A service not available");
            return;
        }

        A2AProtocolService a2aService = unifiedAdapter.getA2aProtocolService();

        Map<String, Object> msgContent = content != null ? new HashMap<>(content) : new HashMap<>();
        msgContent.put("fromAgent", fromAgent);
        msgContent.put("toAgent", toAgent);
        msgContent.put("messageType", messageType);
        msgContent.put("timestamp", System.currentTimeMillis());

        A2AMessage message = new A2AMessage();
        message.setFromAgentId(fromAgent);
        message.setToAgentId(toAgent);
        message.setMessageType(net.ooder.scene.a2a.A2AMessageType.valueOf(messageType));
        message.setPayload(msgContent);

        String messageId = a2aService.sendMessage(message);
        log.info("[sendMessage] Message sent, id: {}", messageId);

        notifySubscribers(toAgent, messageType, msgContent);
    }

    public List<Map<String, Object>> receiveMessages(String agentId) {
        log.debug("[receiveMessages] Agent: {}", agentId);

        if (!isA2AAvailable()) {
            return Collections.emptyList();
        }

        if (isMessageQueueAvailable()) {
            MessageQueueService queueService = unifiedAdapter.getMessageQueueService();
            List<MessageEnvelope> offlineMessages = queueService.getOfflineMessages(agentId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (MessageEnvelope envelope : offlineMessages) {
                Map<String, Object> msgMap = new HashMap<>();
                msgMap.put("messageId", envelope.getMessageId());
                msgMap.put("senderId", envelope.getFrom().getId());
                msgMap.put("recipientId", envelope.getTo().getId());
                msgMap.put("content", envelope.getContent());
                msgMap.put("timestamp", envelope.getCreatedAt());
                result.add(msgMap);
            }
            return result;
        }

        return Collections.emptyList();
    }

    public void broadcastMessage(String fromAgent, String sceneGroupId,
            String messageType, Map<String, Object> content) {
        log.info("[broadcastMessage] {} -> scene {}: {}", fromAgent, sceneGroupId, messageType);

        if (!isA2AAvailable()) {
            log.warn("[broadcastMessage] A2A service not available");
            return;
        }

        A2AProtocolService a2aService = unifiedAdapter.getA2aProtocolService();

        Map<String, Object> msgContent = content != null ? new HashMap<>(content) : new HashMap<>();
        msgContent.put("fromAgent", fromAgent);
        msgContent.put("sceneGroupId", sceneGroupId);
        msgContent.put("messageType", messageType);
        msgContent.put("timestamp", System.currentTimeMillis());

        A2AMessage message = new A2AMessage();
        message.setFromAgentId(fromAgent);
        message.setMessageType(net.ooder.scene.a2a.A2AMessageType.valueOf(messageType));
        message.setPayload(msgContent);

        a2aService.broadcast(sceneGroupId, message);
        log.info("[broadcastMessage] Message broadcast to scene: {}", sceneGroupId);
    }

    public void subscribeToMessages(String agentId, String messageType,
            java.util.function.Consumer<Map<String, Object>> handler) {
        log.info("[subscribeToMessages] Agent: {}, type: {}", agentId, messageType);

        if (!isA2AAvailable()) {
            log.warn("[subscribeToMessages] A2A service not available");
            return;
        }

        String key = agentId + ":" + messageType;
        subscriptions.computeIfAbsent(key, k -> new ArrayList<>()).add(handler);

        A2AProtocolService a2aService = unifiedAdapter.getA2aProtocolService();
        a2aService.registerHandler(agentId, msg -> {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("messageId", msg.getMessageId());
            msgMap.put("fromAgentId", msg.getFromAgentId());
            msgMap.put("type", msg.getMessageType().name());
            msgMap.put("content", msg.getPayload());
            handler.accept(msgMap);
        });
        log.info("[subscribeToMessages] Subscription registered for agent: {}, type: {}", agentId, messageType);
    }

    public void unsubscribeFromMessages(String agentId, String messageType) {
        log.info("[unsubscribeFromMessages] Agent: {}, type: {}", agentId, messageType);

        String key = agentId + ":" + messageType;
        subscriptions.remove(key);

        if (isA2AAvailable()) {
            A2AProtocolService a2aService = unifiedAdapter.getA2aProtocolService();
            a2aService.unregisterHandler(agentId);
        }
        log.info("[unsubscribeFromMessages] Subscription removed for agent: {}, type: {}", agentId, messageType);
    }

    public Map<String, Object> getSessionInfo(String sessionId) {
        log.debug("[getSessionInfo] Session: {}", sessionId);

        if (!isSessionManagerAvailable()) {
            return Collections.emptyMap();
        }

        UnifiedSessionManager sessionManager = unifiedAdapter.getSessionManager();
        UnifiedSession session = sessionManager.getSession(sessionId);
        if (session == null) {
            log.debug("[getSessionInfo] Session not found: {}", sessionId);
            return Collections.emptyMap();
        }

        Map<String, Object> info = new HashMap<>();
        info.put("sessionId", session.getSessionId());
        info.put("type", session.getType());
        info.put("ownerId", session.getOwnerId());
        info.put("sceneGroupId", session.getSceneGroupId());
        info.put("createdAt", session.getCreatedAt());
        info.put("lastAccessedAt", session.getLastActiveAt());
        info.put("metadata", session.getMetadata());
        return info;
    }

    public void updateSessionState(String sessionId, Map<String, Object> state) {
        log.debug("[updateSessionState] Session: {}", sessionId);

        if (!isSessionManagerAvailable()) {
            return;
        }

        UnifiedSessionManager sessionManager = unifiedAdapter.getSessionManager();
        sessionManager.updateSession(sessionId, state);
        log.debug("[updateSessionState] Session state updated: {}", sessionId);
    }

    public void enqueueMessage(String queueName, Map<String, Object> message) {
        log.debug("[enqueueMessage] Queue: {}", queueName);

        if (!isMessageQueueAvailable()) {
            log.warn("[enqueueMessage] Message queue not available");
            return;
        }

        MessageQueueService queueService = unifiedAdapter.getMessageQueueService();

        MessageEnvelope envelope = new MessageEnvelope();
        envelope.setTo(net.ooder.scene.message.queue.MessageParticipant.virtualAgent(queueName));
        envelope.setContent(message);
        envelope.setCreatedAt(System.currentTimeMillis());

        queueService.sendMessage(envelope);
        log.debug("[enqueueMessage] Message enqueued to: {}", queueName);
    }

    public Map<String, Object> dequeueMessage(String queueName, long timeoutMs) {
        log.debug("[dequeueMessage] Queue: {}", queueName);

        if (!isMessageQueueAvailable()) {
            return null;
        }

        MessageQueueService queueService = unifiedAdapter.getMessageQueueService();
        List<MessageEnvelope> messages = queueService.getOfflineMessages(queueName);

        if (messages != null && !messages.isEmpty()) {
            MessageEnvelope envelope = messages.get(0);
            queueService.acknowledgeMessage(envelope.getMessageId(), queueName);

            Map<String, Object> result = new HashMap<>();
            result.put("messageId", envelope.getMessageId());
            result.put("senderId", envelope.getFrom().getId());
            result.put("content", envelope.getContent());
            result.put("timestamp", envelope.getCreatedAt());
            return result;
        }

        return null;
    }

    public Map<String, Object> getUnifiedInterfaceStatus() {
        Map<String, Object> status = new HashMap<>();

        status.put("a2aAvailable", isA2AAvailable());
        status.put("sessionManagerAvailable", isSessionManagerAvailable());
        status.put("messageQueueAvailable", isMessageQueueAvailable());
        status.put("agentContextAvailable", unifiedAdapter.hasAgentContextManager());
        status.put("northboundQueueAvailable", unifiedAdapter.hasNorthboundMessageQueue());
        status.put("offlineMessageAvailable", unifiedAdapter.hasOfflineMessageService());
        status.put("reliabilityAvailable", unifiedAdapter.hasMessageReliabilityService());
        status.put("webSocketAuthAvailable", unifiedAdapter.hasWebSocketAuthService());

        return status;
    }

    private void notifySubscribers(String agentId, String messageType, Map<String, Object> content) {
        String key = agentId + ":" + messageType;
        List<java.util.function.Consumer<Map<String, Object>>> handlers = subscriptions.get(key);
        if (handlers != null) {
            for (java.util.function.Consumer<Map<String, Object>> handler : handlers) {
                try {
                    handler.accept(content);
                } catch (Exception e) {
                    log.warn("[notifySubscribers] Handler error for {}: {}", key, e.getMessage());
                }
            }
        }
    }
}