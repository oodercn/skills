package net.ooder.mvp.skill.scene.service.impl;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.todo.TodoDTO;
import net.ooder.mvp.skill.scene.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Primary
public class TodoServiceHybridImpl implements TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoServiceHybridImpl.class);

    private final TodoServiceSdkImpl sdkService;
    private final TodoServiceMemoryImpl memoryService;

    public TodoServiceHybridImpl(
            @Autowired TodoServiceSdkImpl sdkService,
            @Autowired(required = false) TodoServiceMemoryImpl memoryService) {
        this.sdkService = sdkService;
        this.memoryService = memoryService;
        
        log.info("TodoServiceHybridImpl initialized with SDK service as primary");
    }

    @Override
    public PageResult<TodoDTO> listMyTodos(String userId, String status, int pageNum, int pageSize) {
        return sdkService.listMyTodos(userId, status, pageNum, pageSize);
    }

    @Override
    public PageResult<TodoDTO> listMyTodos(String userId, String status, String type, int pageNum, int pageSize) {
        return sdkService.listMyTodos(userId, status, type, pageNum, pageSize);
    }

    @Override
    public List<TodoDTO> listPendingTodos(String userId) {
        return sdkService.listPendingTodos(userId);
    }

    @Override
    public Map<String, Integer> countByType(String userId) {
        return sdkService.countByType(userId);
    }

    @Override
    public TodoDTO getTodo(String todoId) {
        return sdkService.getTodo(todoId);
    }

    @Override
    public boolean acceptTodo(String userId, String todoId) {
        boolean result = sdkService.acceptTodo(userId, todoId);
        if (memoryService != null) {
            memoryService.acceptTodo(userId, todoId);
        }
        return result;
    }

    @Override
    public boolean rejectTodo(String userId, String todoId) {
        boolean result = sdkService.rejectTodo(userId, todoId);
        if (memoryService != null) {
            memoryService.rejectTodo(userId, todoId);
        }
        return result;
    }

    @Override
    public boolean completeTodo(String userId, String todoId) {
        boolean result = sdkService.completeTodo(userId, todoId);
        if (memoryService != null) {
            memoryService.completeTodo(userId, todoId);
        }
        return result;
    }

    @Override
    public boolean approveTodo(String userId, String todoId) {
        boolean result = sdkService.approveTodo(userId, todoId);
        if (memoryService != null) {
            memoryService.approveTodo(userId, todoId);
        }
        return result;
    }

    @Override
    public boolean createInvitationTodo(String sceneGroupId, String fromUserId, String toUserId, String role) {
        boolean result = sdkService.createInvitationTodo(sceneGroupId, fromUserId, toUserId, role);
        if (memoryService != null) {
            memoryService.createInvitationTodo(sceneGroupId, fromUserId, toUserId, role);
        }
        return result;
    }

    @Override
    public boolean createDelegationTodo(String sceneGroupId, String fromUserId, String toUserId, String title, Long deadline) {
        boolean result = sdkService.createDelegationTodo(sceneGroupId, fromUserId, toUserId, title, deadline);
        if (memoryService != null) {
            memoryService.createDelegationTodo(sceneGroupId, fromUserId, toUserId, title, deadline);
        }
        return result;
    }

    @Override
    public boolean createReminderTodo(String sceneGroupId, String userId, String title, Long deadline) {
        boolean result = sdkService.createReminderTodo(sceneGroupId, userId, title, deadline);
        if (memoryService != null) {
            memoryService.createReminderTodo(sceneGroupId, userId, title, deadline);
        }
        return result;
    }

    @Override
    public boolean createActivationTodo(String userId, String installId, String capabilityId, String capabilityName) {
        boolean result = sdkService.createActivationTodo(userId, installId, capabilityId, capabilityName);
        if (memoryService != null) {
            memoryService.createActivationTodo(userId, installId, capabilityId, capabilityName);
        }
        return result;
    }

    @Override
    public boolean createApprovalTodo(String sceneGroupId, String fromUserId, String toUserId, String title, String description) {
        boolean result = sdkService.createApprovalTodo(sceneGroupId, fromUserId, toUserId, title, description);
        if (memoryService != null) {
            memoryService.createApprovalTodo(sceneGroupId, fromUserId, toUserId, title, description);
        }
        return result;
    }

    @Override
    public boolean createSceneNotificationTodo(String sceneGroupId, String userId, String title, String description) {
        boolean result = sdkService.createSceneNotificationTodo(sceneGroupId, userId, title, description);
        if (memoryService != null) {
            memoryService.createSceneNotificationTodo(sceneGroupId, userId, title, description);
        }
        return result;
    }

    @Override
    public boolean deleteTodo(String todoId) {
        boolean result = sdkService.deleteTodo(todoId);
        if (memoryService != null) {
            memoryService.deleteTodo(todoId);
        }
        return result;
    }
}
