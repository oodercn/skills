package net.ooder.skill.todo.sync.service;

import net.ooder.skill.todo.sync.dto.*;
import net.ooder.skill.todo.sync.dict.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class TodoSyncService {

    @Autowired
    private DingTalkTodoService dingTalkTodoService;

    @Autowired
    private FeishuTodoService feishuTodoService;

    @Autowired
    private WeComTodoService weComTodoService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TodoDTO createTodo(TodoDTO todo) {
        log.info("Creating todo: {}", todo.getTitle());
        
        todo.setTodoId(UUID.randomUUID().toString());
        todo.setStatus(TodoStatus.PENDING.getCode());
        todo.setCreateTime(LocalDateTime.now().format(FORMATTER));
        todo.setUpdateTime(LocalDateTime.now().format(FORMATTER));
        todo.setProgress(0);
        
        if (todo.getPlatform() != null) {
            TodoDTO platformTodo = syncToPlatform(todo);
            if (platformTodo != null) {
                todo.setPlatformTodoId(platformTodo.getPlatformTodoId());
            }
        }
        
        return todo;
    }

    public TodoDTO getTodo(String todoId) {
        log.info("Getting todo: {}", todoId);
        return TodoDTO.builder()
                .todoId(todoId)
                .title("示例待办")
                .description("这是一个示例待办任务")
                .status(TodoStatus.PENDING.getCode())
                .priority(TodoPriority.MEDIUM.getCode())
                .dueDate(LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .progress(30)
                .build();
    }

    public TodoDTO updateTodo(String todoId, TodoDTO todo) {
        log.info("Updating todo: {}", todoId);
        todo.setTodoId(todoId);
        todo.setUpdateTime(LocalDateTime.now().format(FORMATTER));
        
        if (todo.getPlatform() != null && todo.getPlatformTodoId() != null) {
            updateOnPlatform(todo);
        }
        
        return todo;
    }

    public boolean deleteTodo(String todoId, String platform) {
        log.info("Deleting todo: {}", todoId);
        
        if (platform != null) {
            deleteFromPlatform(todoId, platform);
        }
        
        return true;
    }

    public TodoDTO completeTodo(String todoId, String platform) {
        log.info("Completing todo: {}", todoId);
        
        TodoDTO todo = TodoDTO.builder()
                .todoId(todoId)
                .status(TodoStatus.COMPLETED.getCode())
                .completeTime(LocalDateTime.now().format(FORMATTER))
                .progress(100)
                .build();
        
        if (platform != null) {
            completeOnPlatform(todoId, platform);
        }
        
        return todo;
    }

    public TodoSyncResultDTO syncTodos(String userId, String platform) {
        log.info("Syncing todos for user: {}, platform: {}", userId, platform);
        
        TodoSyncResultDTO result = TodoSyncResultDTO.builder()
                .platform(platform)
                .success(true)
                .syncTime(LocalDateTime.now().format(FORMATTER))
                .totalTodos(0)
                .createdTodos(0)
                .updatedTodos(0)
                .completedTodos(0)
                .failedTodos(0)
                .errors(new ArrayList<>())
                .build();
        
        if (platform != null) {
            switch (platform.toUpperCase()) {
                case "DINGTALK":
                    result = dingTalkTodoService.syncTodos(userId);
                    break;
                case "FEISHU":
                    result = feishuTodoService.syncTodos(userId);
                    break;
                case "WECOM":
                    result = weComTodoService.syncTodos(userId);
                    break;
                default:
                    break;
            }
        }
        
        return result;
    }

    public List<TodoDTO> listTodos(String userId, String status) {
        log.info("Listing todos for user: {}, status: {}", userId, status);
        
        List<TodoDTO> todos = new ArrayList<>();
        todos.add(TodoDTO.builder()
                .todoId(UUID.randomUUID().toString())
                .title("完成项目报告")
                .description("完成月度项目进度报告")
                .status(TodoStatus.PENDING.getCode())
                .priority(TodoPriority.HIGH.getCode())
                .dueDate(LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .progress(50)
                .build());
        todos.add(TodoDTO.builder()
                .todoId(UUID.randomUUID().toString())
                .title("代码评审")
                .description("评审团队提交的代码")
                .status(TodoStatus.IN_PROGRESS.getCode())
                .priority(TodoPriority.MEDIUM.getCode())
                .dueDate(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .progress(70)
                .build());
        
        return todos;
    }

    private TodoDTO syncToPlatform(TodoDTO todo) {
        switch (todo.getPlatform().toUpperCase()) {
            case "DINGTALK":
                return dingTalkTodoService.createTodo(todo);
            case "FEISHU":
                return feishuTodoService.createTodo(todo);
            case "WECOM":
                return weComTodoService.createTodo(todo);
            default:
                return null;
        }
    }

    private void updateOnPlatform(TodoDTO todo) {
        switch (todo.getPlatform().toUpperCase()) {
            case "DINGTALK":
                dingTalkTodoService.updateTodo(todo);
                break;
            case "FEISHU":
                feishuTodoService.updateTodo(todo);
                break;
            case "WECOM":
                weComTodoService.updateTodo(todo);
                break;
            default:
                break;
        }
    }

    private void deleteFromPlatform(String todoId, String platform) {
        switch (platform.toUpperCase()) {
            case "DINGTALK":
                dingTalkTodoService.deleteTodo(todoId);
                break;
            case "FEISHU":
                feishuTodoService.deleteTodo(todoId);
                break;
            case "WECOM":
                weComTodoService.deleteTodo(todoId);
                break;
            default:
                break;
        }
    }

    private void completeOnPlatform(String todoId, String platform) {
        switch (platform.toUpperCase()) {
            case "DINGTALK":
                dingTalkTodoService.completeTodo(todoId);
                break;
            case "FEISHU":
                feishuTodoService.completeTodo(todoId);
                break;
            case "WECOM":
                weComTodoService.completeTodo(todoId);
                break;
            default:
                break;
        }
    }
}
