package net.ooder.skill.agent.spi.impl;

import net.ooder.skill.agent.spi.LocalA2AProtocolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@Deprecated
public class LocalA2AProtocolServiceImpl implements LocalA2AProtocolService {

    private static final Logger log = LoggerFactory.getLogger(LocalA2AProtocolServiceImpl.class);
    
    @Autowired(required = false)
    private LocalAgentContextServiceImpl agentContextService;
    
    private final Map<String, List<MessageHandler>> handlers = new ConcurrentHashMap<>();
    private final List<A2AMessage> messageHistory = new CopyOnWriteArrayList<>();
    private final int maxHistorySize = 1000;

    @Override
    public void sendA2AMessage(A2AMessage message) {
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID().toString());
        }
        message.setCreateTime(System.currentTimeMillis());
        message.setStatus("SENT");
        
        messageHistory.add(message);
        trimHistory();
        
        List<MessageHandler> messageHandlers = handlers.get(message.getMessageType());
        if (messageHandlers == null || messageHandlers.isEmpty()) {
            log.debug("[broadcastToAgents] No handler for type {}, trying all registered handlers", message.getMessageType());
            messageHandlers = handlers.values().stream()
                .flatMap(List::stream)
                .collect(java.util.stream.Collectors.toList());
        }
        if (messageHandlers != null) {
            for (MessageHandler handler : messageHandlers) {
                try {
                    handler.handle(message);
                } catch (Exception e) {
                    log.error("[sendA2AMessage] Handler error for message {}: {}", 
                        message.getMessageId(), e.getMessage());
                }
            }
        }
        
        log.info("[sendA2AMessage] Sent A2A message: {} from {} to {}", 
            message.getMessageId(), message.getFromAgentId(), message.getToAgentId());
    }

    @Override
    public void broadcastToAgents(String sceneGroupId, A2AMessage message) {
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID().toString());
        }
        message.setSceneGroupId(sceneGroupId);
        message.setCreateTime(System.currentTimeMillis());
        message.setStatus("BROADCAST");
        
        messageHistory.add(message);
        trimHistory();
        
        List<MessageHandler> messageHandlers = handlers.get(message.getMessageType());
        if (messageHandlers != null) {
            for (MessageHandler handler : messageHandlers) {
                try {
                    handler.handle(message);
                } catch (Exception e) {
                    log.error("[broadcastToAgents] Handler error: {}", e.getMessage());
                }
            }
        }
        
        log.info("[broadcastToAgents] Broadcast message: {} to scene: {}", 
            message.getMessageId(), sceneGroupId);
    }

    @Override
    public void registerHandler(String messageType, MessageHandler handler) {
        handlers.computeIfAbsent(messageType, k -> new CopyOnWriteArrayList<>()).add(handler);
        log.info("[registerHandler] Registered handler for message type: {}", messageType);
    }

    @Override
    public void unregisterHandler(String messageType, MessageHandler handler) {
        List<MessageHandler> messageHandlers = handlers.get(messageType);
        if (messageHandlers != null) {
            messageHandlers.remove(handler);
            log.info("[unregisterHandler] Unregistered handler for message type: {}", messageType);
        }
    }

    @Override
    public void routeMessage(A2AMessage message) {
        String toAgentId = message.getToAgentId();
        
        if (toAgentId == null || toAgentId.isEmpty()) {
            if (message.getSceneGroupId() != null) {
                broadcastToAgents(message.getSceneGroupId(), message);
            }
            return;
        }
        
        sendA2AMessage(message);
    }

    @Override
    public List<A2AMessage> getMessageHistory(String sceneGroupId, int limit) {
        return messageHistory.stream()
            .filter(m -> sceneGroupId == null || sceneGroupId.equals(m.getSceneGroupId()))
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .limit(limit > 0 ? limit : 100)
            .collect(Collectors.toList());
    }
    
    private void trimHistory() {
        while (messageHistory.size() > maxHistorySize) {
            messageHistory.remove(0);
        }
    }
}
