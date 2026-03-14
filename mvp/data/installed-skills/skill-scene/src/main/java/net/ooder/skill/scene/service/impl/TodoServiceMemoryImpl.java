package net.ooder.skill.scene.service.impl;

import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.todo.TodoDTO;
import net.ooder.skill.scene.service.TodoService;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TodoServiceMemoryImpl implements TodoService {

    private final Map<String, List<TodoDTO>> userTodos = new ConcurrentHashMap<>();
    private final Map<String, TodoDTO> allTodos = new ConcurrentHashMap<>();

    @Override
    public PageResult<TodoDTO> listMyTodos(String userId, String status, int pageNum, int pageSize) {
        List<TodoDTO> todos = userTodos.getOrDefault(userId, new ArrayList<>());
        
        if (status != null && !status.isEmpty()) {
            todos = todos.stream()
                .filter(t -> status.equals(t.getStatus()))
                .collect(Collectors.toList());
        }
        
        todos.sort((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()));
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, todos.size());
        
        List<TodoDTO> pagedTodos = start < todos.size() 
            ? todos.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<TodoDTO> result = new PageResult<>();
        result.setList(pagedTodos);
        result.setTotal(todos.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public TodoDTO getTodo(String todoId) {
        return allTodos.get(todoId);
    }

    @Override
    public boolean acceptTodo(String userId, String todoId) {
        TodoDTO todo = allTodos.get(todoId);
        if (todo == null) {
            return false;
        }
        todo.setStatus("completed");
        todo.setCompletedTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean rejectTodo(String userId, String todoId) {
        TodoDTO todo = allTodos.get(todoId);
        if (todo == null) {
            return false;
        }
        todo.setStatus("completed");
        todo.setCompletedTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean completeTodo(String userId, String todoId) {
        TodoDTO todo = allTodos.get(todoId);
        if (todo == null) {
            return false;
        }
        todo.setStatus("completed");
        todo.setCompletedTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean approveTodo(String userId, String todoId) {
        TodoDTO todo = allTodos.get(todoId);
        if (todo == null) {
            return false;
        }
        todo.setStatus("completed");
        todo.setCompletedTime(System.currentTimeMillis());
        return true;
    }

    @Override
    public boolean createInvitationTodo(String sceneGroupId, String fromUserId, String toUserId, String role) {
        TodoDTO todo = new TodoDTO();
        todo.setId("todo-" + UUID.randomUUID().toString().substring(0, 8));
        todo.setType("invitation");
        todo.setTitle(fromUserId + " 邀请您加入场景");
        todo.setSceneGroupId(sceneGroupId);
        todo.setFromUser(fromUserId);
        todo.setRole(role);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        
        allTodos.put(todo.getId(), todo);
        userTodos.computeIfAbsent(toUserId, k -> new ArrayList<>()).add(todo);
        
        return true;
    }

    @Override
    public boolean createDelegationTodo(String sceneGroupId, String fromUserId, String toUserId, String title, Long deadline) {
        TodoDTO todo = new TodoDTO();
        todo.setId("todo-" + UUID.randomUUID().toString().substring(0, 8));
        todo.setType("delegation");
        todo.setTitle(title);
        todo.setSceneGroupId(sceneGroupId);
        todo.setFromUser(fromUserId);
        todo.setDeadline(deadline);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        
        allTodos.put(todo.getId(), todo);
        userTodos.computeIfAbsent(toUserId, k -> new ArrayList<>()).add(todo);
        
        return true;
    }

    @Override
    public boolean createReminderTodo(String sceneGroupId, String userId, String title, Long deadline) {
        TodoDTO todo = new TodoDTO();
        todo.setId("todo-" + UUID.randomUUID().toString().substring(0, 8));
        todo.setType("reminder");
        todo.setTitle(title);
        todo.setSceneGroupId(sceneGroupId);
        todo.setDeadline(deadline);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        
        allTodos.put(todo.getId(), todo);
        userTodos.computeIfAbsent(userId, k -> new ArrayList<>()).add(todo);
        
        return true;
    }
}
