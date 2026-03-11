package net.ooder.skill.conversation.service;

import net.ooder.skill.conversation.model.Conversation;
import net.ooder.skill.conversation.model.CreateConversationRequest;
import net.ooder.skill.conversation.model.Message;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ConversationService {
    
    Conversation createConversation(CreateConversationRequest request);
    
    Optional<Conversation> getConversation(String id);
    
    Optional<Conversation> getConversation(String id, String userId);
    
    List<Conversation> getUserConversations(String userId);
    
    List<Conversation> getSceneConversations(String sceneId);
    
    Conversation updateConversation(String id, Conversation updates);
    
    void deleteConversation(String id);
    
    void archiveConversation(String id);
    
    Message addMessage(String conversationId, String role, String content, Integer tokenCount, String metadata);
    
    List<Message> getMessages(String conversationId);
    
    List<Message> getRecentMessages(String conversationId, int limit);
    
    List<Map<String, Object>> getConversationContext(String conversationId, int maxTokens);
    
    void clearMessages(String conversationId);
    
    long getConversationCount(String userId);
    
    long getTotalTokens(String userId);
}
