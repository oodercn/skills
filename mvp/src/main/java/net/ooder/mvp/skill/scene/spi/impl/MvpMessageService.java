package net.ooder.mvp.skill.scene.spi.impl;

import net.ooder.mvp.skill.scene.spi.MessageService;
import net.ooder.mvp.skill.scene.spi.message.Message;
import net.ooder.mvp.skill.scene.spi.message.SceneNotification;
import net.ooder.mvp.skill.scene.spi.message.SendMessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MvpMessageService implements MessageService {
    
    private static final Logger log = LoggerFactory.getLogger(MvpMessageService.class);
    
    private final Map<String, List<Message>> messageStore = new ConcurrentHashMap<>();
    private final Map<String, List<SceneNotification>> notificationStore = new ConcurrentHashMap<>();
    private long messageIdCounter = 0;
    
    @Override
    public SendMessageResult sendMessage(Message message) {
        if (message == null) {
            return SendMessageResult.failure("Message is null");
        }
        
        try {
            String messageId = generateMessageId();
            message.setMessageId(messageId);
            message.setTimestamp(System.currentTimeMillis());
            
            if (message.getToUserIds() != null && !message.getToUserIds().isEmpty()) {
                for (String userId : message.getToUserIds()) {
                    List<Message> userMessages = messageStore.computeIfAbsent(userId, k -> new ArrayList<>());
                    userMessages.add(message);
                }
            }
            
            log.info("Sent message {} to {} users", messageId, 
                message.getToUserIds() != null ? message.getToUserIds().size() : 0);
            
            return SendMessageResult.success(messageId);
            
        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage());
            return SendMessageResult.failure(e.getMessage());
        }
    }
    
    @Override
    public List<SendMessageResult> batchSendMessages(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }
        
        return messages.stream()
            .map(this::sendMessage)
            .collect(Collectors.toList());
    }
    
    @Override
    public SendMessageResult sendSceneNotification(
            String sceneId, 
            List<String> userIds, 
            SceneNotification notification) {
        
        if (sceneId == null || userIds == null || userIds.isEmpty()) {
            return SendMessageResult.failure("Invalid parameters");
        }
        
        try {
            List<SceneNotification> notifications = notificationStore.computeIfAbsent(sceneId, k -> new ArrayList<>());
            
            for (String userId : userIds) {
                notifications.add(notification);
                log.debug("Sent notification to user {} in scene {}", userId, sceneId);
            }
            
            log.info("Sent scene notification to {} users in scene {}", userIds.size(), sceneId);
            return SendMessageResult.success("notification-" + System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("Failed to send scene notification: {}", e.getMessage());
            return SendMessageResult.failure(e.getMessage());
        }
    }
    
    private synchronized String generateMessageId() {
        return "msg-" + System.currentTimeMillis() + "-" + (++messageIdCounter);
    }
    
    public List<Message> getUserMessages(String userId) {
        return new ArrayList<>(messageStore.getOrDefault(userId, Collections.emptyList()));
    }
    
    public List<SceneNotification> getSceneNotifications(String sceneId) {
        return new ArrayList<>(notificationStore.getOrDefault(sceneId, Collections.emptyList()));
    }
}
