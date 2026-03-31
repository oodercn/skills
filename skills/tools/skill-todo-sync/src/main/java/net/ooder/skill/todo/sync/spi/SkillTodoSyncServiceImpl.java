package net.ooder.skill.todo.sync.spi;

import net.ooder.skill.common.spi.TodoSyncService;
import net.ooder.skill.common.spi.todo.TodoInfo;
import net.ooder.skill.common.spi.todo.TodoStatus;
import net.ooder.skill.todo.sync.dto.TodoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnProperty(name = "skill.todo.sync.enabled", havingValue = "true", matchIfMissing = false)
public class SkillTodoSyncServiceImpl implements TodoSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(SkillTodoSyncServiceImpl.class);
    
    @Autowired
    private net.ooder.skill.todo.sync.service.TodoSyncService todoSyncService;
    
    @Override
    public TodoInfo createTodo(TodoInfo todo) {
        log.info("[createTodo] title={}", todo.getTitle());
        try {
            TodoDTO dto = convertToDTO(todo);
            TodoDTO created = todoSyncService.createTodo(dto);
            return convertFromDTO(created);
        } catch (Exception e) {
            log.error("[createTodo] Failed to create todo", e);
            return null;
        }
    }
    
    @Override
    public TodoInfo getTodo(String todoId) {
        log.info("[getTodo] todoId={}", todoId);
        try {
            TodoDTO dto = todoSyncService.getTodo(todoId);
            return dto != null ? convertFromDTO(dto) : null;
        } catch (Exception e) {
            log.error("[getTodo] Failed to get todo", e);
            return null;
        }
    }
    
    @Override
    public List<TodoInfo> listTodos(String userId) {
        log.info("[listTodos] userId={}", userId);
        try {
            List<TodoDTO> todos = todoSyncService.listTodos(userId);
            List<TodoInfo> result = new ArrayList<>();
            for (TodoDTO dto : todos) {
                result.add(convertFromDTO(dto));
            }
            return result;
        } catch (Exception e) {
            log.error("[listTodos] Failed to list todos", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<TodoInfo> listTodosByStatus(String userId, TodoStatus status) {
        log.info("[listTodosByStatus] userId={}, status={}", userId, status);
        try {
            List<TodoDTO> todos = todoSyncService.listTodosByStatus(userId, status.name());
            List<TodoInfo> result = new ArrayList<>();
            for (TodoDTO dto : todos) {
                result.add(convertFromDTO(dto));
            }
            return result;
        } catch (Exception e) {
            log.error("[listTodosByStatus] Failed to list todos", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public TodoInfo updateTodo(TodoInfo todo) {
        log.info("[updateTodo] todoId={}", todo.getTodoId());
        try {
            TodoDTO dto = convertToDTO(todo);
            TodoDTO updated = todoSyncService.updateTodo(dto);
            return convertFromDTO(updated);
        } catch (Exception e) {
            log.error("[updateTodo] Failed to update todo", e);
            return null;
        }
    }
    
    @Override
    public TodoInfo completeTodo(String todoId) {
        log.info("[completeTodo] todoId={}", todoId);
        try {
            TodoDTO dto = todoSyncService.completeTodo(todoId);
            return dto != null ? convertFromDTO(dto) : null;
        } catch (Exception e) {
            log.error("[completeTodo] Failed to complete todo", e);
            return null;
        }
    }
    
    @Override
    public void deleteTodo(String todoId) {
        log.info("[deleteTodo] todoId={}", todoId);
        try {
            todoSyncService.deleteTodo(todoId);
        } catch (Exception e) {
            log.error("[deleteTodo] Failed to delete todo", e);
        }
    }
    
    @Override
    public void syncFromPlatform(String platform) {
        log.info("[syncFromPlatform] platform={}", platform);
        try {
            todoSyncService.syncFromPlatform(platform);
        } catch (Exception e) {
            log.error("[syncFromPlatform] Failed to sync", e);
        }
    }
    
    @Override
    public List<String> getAvailablePlatforms() {
        return Arrays.asList("dingtalk", "feishu", "wecom");
    }
    
    private TodoDTO convertToDTO(TodoInfo info) {
        TodoDTO dto = new TodoDTO();
        dto.setTodoId(info.getTodoId());
        dto.setTitle(info.getTitle());
        dto.setDescription(info.getDescription());
        dto.setAssigneeId(info.getAssigneeId());
        dto.setCreatorId(info.getCreatorId());
        dto.setStatus(info.getStatus() != null ? info.getStatus().name() : "PENDING");
        dto.setPriority(info.getPriority());
        dto.setDueTime(info.getDueTime());
        dto.setPlatformSource(info.getPlatformSource());
        return dto;
    }
    
    private TodoInfo convertFromDTO(TodoDTO dto) {
        TodoInfo info = new TodoInfo();
        info.setTodoId(dto.getTodoId());
        info.setTitle(dto.getTitle());
        info.setDescription(dto.getDescription());
        info.setAssigneeId(dto.getAssigneeId());
        info.setCreatorId(dto.getCreatorId());
        info.setStatus(TodoStatus.valueOf(dto.getStatus()));
        info.setPriority(dto.getPriority());
        info.setDueTime(dto.getDueTime());
        info.setCreateTime(dto.getCreateTime());
        info.setCompleteTime(dto.getCompleteTime());
        info.setPlatformSource(dto.getPlatformSource());
        return info;
    }
}
