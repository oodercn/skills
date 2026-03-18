package net.ooder.skill.scene.service;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.todo.TodoDTO;

public interface TodoService {
    
    PageResult<TodoDTO> listMyTodos(String userId, String status, int pageNum, int pageSize);
    
    TodoDTO getTodo(String todoId);
    
    boolean acceptTodo(String userId, String todoId);
    
    boolean rejectTodo(String userId, String todoId);
    
    boolean completeTodo(String userId, String todoId);
    
    boolean approveTodo(String userId, String todoId);
    
    boolean createInvitationTodo(String sceneGroupId, String fromUserId, String toUserId, String role);
    
    boolean createDelegationTodo(String sceneGroupId, String fromUserId, String toUserId, String title, Long deadline);
    
    boolean createReminderTodo(String sceneGroupId, String userId, String title, Long deadline);
}
