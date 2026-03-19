package net.ooder.mvp.skill.scene.service.impl;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.todo.TodoDTO;
import net.ooder.mvp.skill.scene.service.TodoService;

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
        return listMyTodos(userId, status, null, pageNum, pageSize);
    }

    @Override
    public PageResult<TodoDTO> listMyTodos(String userId, String status, String type, int pageNum, int pageSize) {
        List<TodoDTO> todos = userTodos.getOrDefault(userId, new ArrayList<>());
        
        if (status != null && !status.isEmpty()) {
            todos = todos.stream()
                .filter(t -> status.equals(t.getStatus()))
                .collect(Collectors.toList());
        }
        
        if (type != null && !type.isEmpty()) {
            todos = todos.stream()
                .filter(t -> type.equals(t.getType()))
                .collect(Collectors.toList());
        }
        
        todos.sort((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()));
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, todos.size());
        
        List<TodoDTO> pagedTodos = start < todos.size() 
            ? new ArrayList<>(todos.subList(start, end))
            : new ArrayList<>();
        
        PageResult<TodoDTO> result = new PageResult<>();
        result.setList(pagedTodos);
        result.setTotal(todos.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public List<TodoDTO> listPendingTodos(String userId) {
        List<TodoDTO> todos = userTodos.getOrDefault(userId, new ArrayList<>());
        return todos.stream()
            .filter(t -> "pending".equals(t.getStatus()))
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> countByType(String userId) {
        List<TodoDTO> todos = userTodos.getOrDefault(userId, new ArrayList<>());
        Map<String, Integer> counts = new HashMap<>();
        
        for (TodoDTO todo : todos) {
            if ("pending".equals(todo.getStatus())) {
                String type = todo.getType();
                counts.put(type, counts.getOrDefault(type, 0) + 1);
            }
        }
        
        return counts;
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
        todo.setToUser(toUserId);
        todo.setRole(role);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        todo.setPriority("normal");
        
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
        todo.setToUser(toUserId);
        todo.setDeadline(deadline);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        todo.setPriority("high");
        
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
        todo.setToUser(userId);
        todo.setDeadline(deadline);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        todo.setPriority("normal");
        
        allTodos.put(todo.getId(), todo);
        userTodos.computeIfAbsent(userId, k -> new ArrayList<>()).add(todo);
        
        return true;
    }

    @Override
    public boolean createActivationTodo(String userId, String installId, String capabilityId, String capabilityName) {
        TodoDTO todo = new TodoDTO();
        todo.setId("todo-" + UUID.randomUUID().toString().substring(0, 8));
        todo.setType("activation");
        todo.setTitle("能力待激活: " + capabilityName);
        todo.setDescription("您安装的能力 " + capabilityName + " 需要激活后才能使用");
        todo.setToUser(userId);
        todo.setInstallId(installId);
        todo.setCapabilityId(capabilityId);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        todo.setPriority("high");
        todo.setActionType("activate");
        
        allTodos.put(todo.getId(), todo);
        userTodos.computeIfAbsent(userId, k -> new ArrayList<>()).add(todo);
        
        return true;
    }

    @Override
    public boolean createApprovalTodo(String sceneGroupId, String fromUserId, String toUserId, String title, String description) {
        TodoDTO todo = new TodoDTO();
        todo.setId("todo-" + UUID.randomUUID().toString().substring(0, 8));
        todo.setType("approval");
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setSceneGroupId(sceneGroupId);
        todo.setFromUser(fromUserId);
        todo.setToUser(toUserId);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        todo.setPriority("high");
        todo.setActionType("approve");
        
        allTodos.put(todo.getId(), todo);
        userTodos.computeIfAbsent(toUserId, k -> new ArrayList<>()).add(todo);
        
        return true;
    }

    @Override
    public boolean createSceneNotificationTodo(String sceneGroupId, String userId, String title, String description) {
        TodoDTO todo = new TodoDTO();
        todo.setId("todo-" + UUID.randomUUID().toString().substring(0, 8));
        todo.setType("scene-notification");
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setSceneGroupId(sceneGroupId);
        todo.setToUser(userId);
        todo.setCreateTime(System.currentTimeMillis());
        todo.setStatus("pending");
        todo.setPriority("normal");
        todo.setActionType("view");
        
        allTodos.put(todo.getId(), todo);
        userTodos.computeIfAbsent(userId, k -> new ArrayList<>()).add(todo);
        
        return true;
    }

    @Override
    public boolean deleteTodo(String todoId) {
        TodoDTO todo = allTodos.remove(todoId);
        if (todo == null) {
            return false;
        }
        
        String toUser = todo.getToUser();
        if (toUser != null) {
            List<TodoDTO> userTodoList = userTodos.get(toUser);
            if (userTodoList != null) {
                userTodoList.removeIf(t -> t.getId().equals(todoId));
            }
        }
        
        return true;
    }
}
