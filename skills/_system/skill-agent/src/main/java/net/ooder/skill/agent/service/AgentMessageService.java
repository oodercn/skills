package net.ooder.skill.agent.service;

import net.ooder.skill.agent.dto.AgentMessageDTO;
import net.ooder.skill.agent.dto.MessageType;

import java.util.List;

public interface AgentMessageService {
    
    String sendMessage(AgentMessageDTO message);
    
    String sendMessage(String fromAgent, String toAgent, String sceneGroupId,
            MessageType type, String title, String content, 
            java.util.Map<String, Object> payload, int priority);
    
    List<AgentMessageDTO> receiveMessages(String agentId);
    
    List<AgentMessageDTO> receiveMessages(String agentId, String type);
    
    List<AgentMessageDTO> receiveUnreadMessages(String agentId);
    
    AgentMessageDTO getMessage(String messageId);
    
    void acknowledge(String agentId, String messageId);
    
    void markAsRead(String agentId, String messageId);
    
    void markAsProcessed(String agentId, String messageId);
    
    int getPendingCount(String agentId);
    
    int getUnreadCount(String agentId);
    
    List<AgentMessageDTO> getMessagesByScene(String sceneGroupId);
    
    boolean deleteMessage(String messageId);
    
    void expireMessage(String messageId);
    
    void cleanExpiredMessages();
    
    void setMaxRetryCount(int count);
    
    void setMessageTTL(long ttlSeconds);
}
