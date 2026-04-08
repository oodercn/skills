package net.ooder.skill.messaging.service.impl;

import net.ooder.skill.messaging.service.UnifiedMessagingService;
import net.ooder.spi.messaging.MessageStreamHandler;
import net.ooder.spi.messaging.model.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UnifiedMessagingServiceImpl implements UnifiedMessagingService {
    
    private final Map<String, List<UnifiedMessage>> messageStore = new ConcurrentHashMap<>();
    
    @Override
    public UnifiedMessage sendMessage(SendMessageRequest request) {
        log.info("[UnifiedMessaging] Sending message to conversation: {}", request.getConversationId());
        
        UnifiedMessage message = new UnifiedMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setConversationId(request.getConversationId());
        message.setSceneGroupId(request.getSceneGroupId());
        message.setMessageType(request.getMessageType());
        message.setConversationType(request.getConversationType());
        message.setFrom(request.getFrom());
        message.setTo(request.getTo());
        message.setCc(request.getCc());
        message.setContent(request.getContent());
        message.setMetadata(request.getMetadata());
        message.setPriority(request.getPriority());
        message.setStatus(MessageStatus.SENT);
        message.setCreateTime(System.currentTimeMillis());
        message.setRequiresAction(false);
        
        messageStore.computeIfAbsent(request.getConversationId(), k -> new ArrayList<>())
            .add(message);
        
        log.info("[UnifiedMessaging] Message sent successfully: {}", message.getMessageId());
        return message;
    }
    
    @Override
    public void streamMessage(SendMessageRequest request, MessageStreamHandler handler) {
        log.info("[UnifiedMessaging] Starting stream message to conversation: {}", request.getConversationId());
        
        try {
            handler.onStart();
            
            UnifiedMessage message = sendMessage(request);
            
            handler.onComplete(message);
            
        } catch (Exception e) {
            log.error("[UnifiedMessaging] Stream message error", e);
            handler.onError(e);
        }
    }
    
    @Override
    public List<UnifiedMessage> getMessages(String conversationId, int limit, Long before, Long after) {
        log.info("[UnifiedMessaging] Getting messages for conversation: {}", conversationId);
        
        List<UnifiedMessage> messages = messageStore.getOrDefault(conversationId, new ArrayList<>());
        
        return messages.stream()
            .filter(msg -> after == null || msg.getCreateTime() > after)
            .filter(msg -> before == null || msg.getCreateTime() < before)
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .limit(limit)
            .toList();
    }
    
    @Override
    public void markAsRead(String conversationId, String userId, String messageId) {
        log.info("[UnifiedMessaging] Marking message as read: {} by user: {}", messageId, userId);
        
    }
    
    @Override
    public void addReaction(String messageId, String userId, String emoji) {
        log.info("[UnifiedMessaging] Adding reaction: {} to message: {} by user: {}", emoji, messageId, userId);
        
    }
    
    @Override
    public void removeReaction(String messageId, String userId, String emoji) {
        log.info("[UnifiedMessaging] Removing reaction: {} from message: {} by user: {}", emoji, messageId, userId);
        
    }
    
    @Override
    public void executeAction(String messageId, String userId, String actionId, Map<String, Object> params) {
        log.info("[UnifiedMessaging] Executing action: {} on message: {} by user: {}", actionId, messageId, userId);
        
    }
}
