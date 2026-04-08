package net.ooder.skill.todo.service.impl;

import net.ooder.skill.todo.dto.TodoDTO;
import net.ooder.skill.todo.model.PageResult;
import net.ooder.skill.todo.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final Map<String, TodoDTO> todoStore = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userTodos = new ConcurrentHashMap<>();
    private Long todoIdCounter = 1L;

    @PostConstruct
    public void init() {
        log.info("[TodoService] Initializing...");

        TodoDTO sampleTodo = new TodoDTO();
        sampleTodo.setTodoId("todo-1");
        sampleTodo.setUserId("current-user");
        sampleTodo.setTitle("欢迎使用任务系统");
        sampleTodo.setDescription("这是一个示例任务");
        sampleTodo.setType("system");
        sampleTodo.setStatus("pending");
        sampleTodo.setPriority("medium");
        sampleTodo.setCreatedAt(System.currentTimeMillis());
        sampleTodo.setUpdatedAt(System.currentTimeMillis());
        todoStore.put(sampleTodo.getTodoId(), sampleTodo);
        userTodos.computeIfAbsent("current-user", k -> new ArrayList<>()).add(sampleTodo.getTodoId());

        log.info("[TodoService] Initialized with {} todos", todoStore.size());
    }

    @Override
    public PageResult<TodoDTO> listMyTodos(String userId, String status, String type, int pageNum, int pageSize) {
        List<String> todoIds = userTodos.getOrDefault(userId, new ArrayList<>());

        List<TodoDTO> filtered = todoIds.stream()
            .map(todoStore::get)
            .filter(Objects::nonNull)
            .filter(t -> status == null || status.isEmpty() || status.equals(t.getStatus()))
            .filter(t -> type == null || type.isEmpty() || type.equals(t.getType()))
            .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
            .collect(Collectors.toList());

        int total = filtered.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        PageResult<TodoDTO> result = new PageResult<>();
        result.setList(start < total ? filtered.subList(start, end) : new ArrayList<>());
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public List<TodoDTO> listPendingTodos(String userId) {
        List<String> todoIds = userTodos.getOrDefault(userId, new ArrayList<>());

        return todoIds.stream()
            .map(todoStore::get)
            .filter(Objects::nonNull)
            .filter(t -> "pending".equals(t.getStatus()))
            .sorted((a, b) -> {
                if (a.getDueDate() > 0 && b.getDueDate() > 0) {
                    return Long.compare(a.getDueDate(), b.getDueDate());
                }
                return Long.compare(b.getCreatedAt(), a.getCreatedAt());
            })
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> countByType(String userId) {
        List<String> todoIds = userTodos.getOrDefault(userId, new ArrayList<>());

        Map<String, Long> statusCounts = todoIds.stream()
            .map(todoStore::get)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(TodoDTO::getStatus, Collectors.counting()));

        Map<String, Integer> result = new HashMap<>();
        result.put("pending", statusCounts.getOrDefault("pending", 0L).intValue());
        result.put("completed", statusCounts.getOrDefault("completed", 0L).intValue());
        result.put("processing", statusCounts.getOrDefault("processing", 0L).intValue());
        result.put("total", todoIds.size());

        return result;
    }

    @Override
    public TodoDTO get(String todoId) {
        return todoStore.get(todoId);
    }

    @Override
    public TodoDTO create(String userId, TodoDTO todo) {
        String todoId = "todo-" + todoIdCounter++;
        todo.setTodoId(todoId);
        todo.setUserId(userId);
        todo.setStatus("pending");
        todo.setCreatedAt(System.currentTimeMillis());
        todo.setUpdatedAt(System.currentTimeMillis());

        todoStore.put(todoId, todo);
        userTodos.computeIfAbsent(userId, k -> new ArrayList<>()).add(todoId);

        log.info("[TodoService] Created todo: {} for user: {}", todoId, userId);
        return todo;
    }

    @Override
    public TodoDTO update(String todoId, TodoDTO todo) {
        TodoDTO existing = todoStore.get(todoId);
        if (existing == null) {
            return null;
        }

        todo.setTodoId(todoId);
        todo.setUserId(existing.getUserId());
        todo.setCreatedAt(existing.getCreatedAt());
        todo.setUpdatedAt(System.currentTimeMillis());

        todoStore.put(todoId, todo);
        log.info("[TodoService] Updated todo: {}", todoId);
        return todo;
    }

    @Override
    public boolean delete(String todoId) {
        TodoDTO todo = todoStore.remove(todoId);
        if (todo != null) {
            List<String> userTodoList = userTodos.get(todo.getUserId());
            if (userTodoList != null) {
                userTodoList.remove(todoId);
            }
            log.info("[TodoService] Deleted todo: {}", todoId);
            return true;
        }
        return false;
    }

    @Override
    public TodoDTO complete(String todoId) {
        TodoDTO todo = todoStore.get(todoId);
        if (todo == null) {
            return null;
        }

        todo.setStatus("completed");
        todo.setCompletedAt(System.currentTimeMillis());
        todo.setUpdatedAt(System.currentTimeMillis());

        log.info("[TodoService] Completed todo: {}", todoId);
        return todo;
    }

    @Override
    public TodoDTO process(String todoId, Map<String, Object> actionData) {
        TodoDTO todo = todoStore.get(todoId);
        if (todo == null) {
            return null;
        }

        todo.setStatus("processing");
        todo.setUpdatedAt(System.currentTimeMillis());

        log.info("[TodoService] Processing todo: {}", todoId);
        return todo;
    }

    @Override
    public TodoDTO accept(String todoId) {
        TodoDTO todo = todoStore.get(todoId);
        if (todo == null) {
            return null;
        }

        todo.setStatus("accepted");
        todo.setUpdatedAt(System.currentTimeMillis());

        log.info("[TodoService] Accepted todo: {}", todoId);
        return todo;
    }

    @Override
    public TodoDTO reject(String todoId, Map<String, Object> reason) {
        TodoDTO todo = todoStore.get(todoId);
        if (todo == null) {
            return null;
        }

        todo.setStatus("rejected");
        todo.setUpdatedAt(System.currentTimeMillis());
        if (reason != null) {
            todo.setMetadata(reason);
        }

        log.info("[TodoService] Rejected todo: {}", todoId);
        return todo;
    }
}