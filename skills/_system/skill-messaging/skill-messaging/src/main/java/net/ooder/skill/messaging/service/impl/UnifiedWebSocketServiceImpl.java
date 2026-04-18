package net.ooder.skill.messaging.service.impl;

import net.ooder.skill.messaging.service.UnifiedWebSocketService;
import net.ooder.spi.messaging.model.UnifiedMessage;
import net.ooder.spi.messaging.model.WsToken;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UnifiedWebSocketServiceImpl implements UnifiedWebSocketService {
    
    private final Map<String, Set<String>> conversationSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, WsToken> tokenStore = new ConcurrentHashMap<>();
    
    @Override
    public WsToken generateToken(String userId, String sceneGroupId, long expireSeconds) {
        log.info("[UnifiedWebSocket] Generating token for user: {}", userId);
        
        WsToken token = new WsToken();
        token.setToken(UUID.randomUUID().toString());
        token.setTokenId(UUID.randomUUID().toString());
        token.setUserId(userId);
        token.setSceneGroupId(sceneGroupId);
        token.setCreatedAt(System.currentTimeMillis());
        token.setExpireAt(System.currentTimeMillis() + expireSeconds * 1000);
        
        tokenStore.put(token.getToken(), token);
        
        log.info("[UnifiedWebSocket] Token generated successfully: {}", token.getTokenId());
        return token;
    }
    
    @Override
    public WsToken refreshToken(String currentToken) {
        log.info("[UnifiedWebSocket] Refreshing token: {}", currentToken);
        
        WsToken oldToken = tokenStore.get(currentToken);
        if (oldToken == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        
        if (oldToken.getExpireAt() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("Token expired");
        }
        
        tokenStore.remove(currentToken);
        
        return generateToken(oldToken.getUserId(), oldToken.getSceneGroupId(), 3600);
    }
    
    @Override
    public void broadcast(String sceneGroupId, UnifiedMessage message) {
        log.info("[UnifiedWebSocket] Broadcasting message to scene group: {}", sceneGroupId);
        
    }
    
    @Override
    public void sendToUser(String userId, UnifiedMessage message) {
        log.info("[UnifiedWebSocket] Sending message to user: {}", userId);
        
    }
    
    @Override
    public void sendToConversation(String conversationId, UnifiedMessage message) {
        log.info("[UnifiedWebSocket] Sending message to conversation: {}", conversationId);
        
    }
    
    @Override
    public void subscribe(String conversationId, String userId) {
        log.info("[UnifiedWebSocket] User: {} subscribing to conversation: {}", userId, conversationId);
        
        conversationSubscriptions.computeIfAbsent(conversationId, k -> ConcurrentHashMap.newKeySet())
            .add(userId);
    }
    
    @Override
    public void unsubscribe(String conversationId, String userId) {
        log.info("[UnifiedWebSocket] User: {} unsubscribing from conversation: {}", userId, conversationId);
        
        Set<String> subscribers = conversationSubscriptions.get(conversationId);
        if (subscribers != null) {
            subscribers.remove(userId);
        }
    }
}
