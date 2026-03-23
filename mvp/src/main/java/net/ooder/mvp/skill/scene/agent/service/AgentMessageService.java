package net.ooder.mvp.skill.scene.agent.service;

import net.ooder.mvp.skill.scene.agent.dto.AgentMessageDTO;
import net.ooder.mvp.skill.scene.agent.dto.MessageType;

import java.util.List;
import java.util.function.Consumer;

public interface AgentMessageService {
    
    String sendMessage(AgentMessageDTO message);
    
    String sendMessage(String fromAgent, String toAgent, MessageType type, 
                       String title, String content);
    
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
    
    void subscribe(String agentId, Consumer<AgentMessageDTO> handler);
    
    void unsubscribe(String agentId);
    
    int getPendingCount(String agentId);
    
    int getUnreadCount(String agentId);
    
    List<AgentMessageDTO> getMessagesByScene(String sceneGroupId);
    
    List<AgentMessageDTO> getMessagesFromAgent(String fromAgent);
    
    List<AgentMessageDTO> getMessagesToAgent(String toAgent);
    
    boolean deleteMessage(String messageId);
    
    int cleanupExpiredMessages();
}
