package net.ooder.skill.agent.service;

import net.ooder.skill.agent.dto.AgentChatMessageDTO;
import net.ooder.skill.agent.dto.SceneChatContextDTO;
import net.ooder.skill.agent.dto.PageResult;
import net.ooder.skill.scene.dto.todo.TodoDTO;

import java.util.List;
import java.util.Map;

public interface AgentChatService {
    
    SceneChatContextDTO getChatContext(String sceneGroupId, String userId);
    
    PageResult<AgentChatMessageDTO> getMessages(String sceneGroupId, String userId, String type, 
            Long before, Long after, int pageNum, int pageSize);
    
    AgentChatMessageDTO getMessage(String messageId);
    
    String sendMessage(String sceneGroupId, AgentChatMessageDTO message);
    
    void markAsRead(String sceneGroupId, String userId, String messageId);
    
    void markAllAsRead(String sceneGroupId, String userId, String type);
    
    Map<String, Integer> getUnreadCounts(String sceneGroupId, String userId);
    
    Object executeMessageAction(String sceneGroupId, String messageId, String userId, 
            String actionId, Map<String, Object> actionData);
    
    List<TodoDTO> getTodos(String sceneGroupId, String userId, String status);
    
    boolean acceptTodo(String userId, String todoId);
    
    boolean rejectTodo(String userId, String todoId, String reason);
    
    boolean delegateTodo(String userId, String todoId, String toUserId);
    
    boolean completeTodo(String userId, String todoId);
    
    void addReaction(String messageId, String userId, String emoji);
    
    void removeReaction(String messageId, String userId, String emoji);
}
