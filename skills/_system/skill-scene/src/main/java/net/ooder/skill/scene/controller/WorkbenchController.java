package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.dto.todo.TodoDTO;
import net.ooder.skill.scene.dto.workbench.SceneTodoGroup;
import net.ooder.skill.scene.dto.workbench.WorkbenchDTO;
import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.service.SceneTodoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workbench")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class WorkbenchController {

    private static final Logger log = LoggerFactory.getLogger(WorkbenchController.class);

    @Autowired(required = false)
    private SceneTodoService sceneTodoService;

    @GetMapping("/data")
    public ResultModel<WorkbenchDTO> getWorkbenchData(
            @RequestParam(required = false) String userId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String effectiveUserId = userId != null ? userId : userIdHeader;
        log.info("[getWorkbenchData] userId={}", effectiveUserId);
        
        if (sceneTodoService == null) {
            return ResultModel.success(createMockWorkbench(effectiveUserId));
        }
        
        try {
            WorkbenchDTO data = sceneTodoService.getWorkbenchData(effectiveUserId);
            return ResultModel.success(data);
        } catch (Exception e) {
            log.error("[getWorkbenchData] Error: {}", e.getMessage());
            return ResultModel.success(createMockWorkbench(effectiveUserId));
        }
    }

    @GetMapping("/scene-todos")
    public ResultModel<List<SceneTodoGroup>> getSceneTodoGroups(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String status,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String effectiveUserId = userId != null ? userId : userIdHeader;
        log.info("[getSceneTodoGroups] userId={}, status={}", effectiveUserId, status);
        
        if (sceneTodoService == null) {
            return ResultModel.success(List.of());
        }
        
        try {
            List<SceneTodoGroup> groups = sceneTodoService.getSceneTodoGroups(effectiveUserId, status);
            return ResultModel.success(groups);
        } catch (Exception e) {
            log.error("[getSceneTodoGroups] Error: {}", e.getMessage());
            return ResultModel.success(List.of());
        }
    }

    @GetMapping("/scene/{sceneGroupId}/todos")
    public ResultModel<List<TodoDTO>> getSceneTodos(@PathVariable String sceneGroupId) {
        log.info("[getSceneTodos] sceneGroupId={}", sceneGroupId);
        
        if (sceneTodoService == null) {
            return ResultModel.success(List.of());
        }
        
        try {
            List<TodoDTO> todos = sceneTodoService.getSceneTodos(sceneGroupId);
            return ResultModel.success(todos);
        } catch (Exception e) {
            log.error("[getSceneTodos] Error: {}", e.getMessage());
            return ResultModel.success(List.of());
        }
    }

    @GetMapping("/user/{userId}/scene/{sceneGroupId}/todos")
    public ResultModel<List<TodoDTO>> getMyTodosInScene(
            @PathVariable String userId,
            @PathVariable String sceneGroupId) {
        log.info("[getMyTodosInScene] userId={}, sceneGroupId={}", userId, sceneGroupId);
        
        if (sceneTodoService == null) {
            return ResultModel.success(List.of());
        }
        
        try {
            List<TodoDTO> todos = sceneTodoService.getMyTodosInScene(userId, sceneGroupId);
            return ResultModel.success(todos);
        } catch (Exception e) {
            log.error("[getMyTodosInScene] Error: {}", e.getMessage());
            return ResultModel.success(List.of());
        }
    }

    @PostMapping("/process-todo")
    public ResultModel<Boolean> processTodo(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String todoId,
            @RequestParam(required = false) String action,
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String effectiveUserId = userId != null ? userId : (body != null ? body.get("userId") : userIdHeader);
        String effectiveTodoId = todoId != null ? todoId : (body != null ? body.get("todoId") : null);
        String effectiveAction = action != null ? action : (body != null ? body.get("action") : null);
        
        log.info("[processTodo] userId={}, todoId={}, action={}", effectiveUserId, effectiveTodoId, effectiveAction);
        
        if (sceneTodoService == null) {
            return ResultModel.success(true);
        }
        
        try {
            boolean success = sceneTodoService.processTodo(effectiveUserId, effectiveTodoId, effectiveAction);
            return ResultModel.success(success);
        } catch (Exception e) {
            log.error("[processTodo] Error: {}", e.getMessage());
            return ResultModel.success(true);
        }
    }

    @PostMapping("/complete-todo")
    public ResultModel<Boolean> completeTodoWithCallback(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String todoId,
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String effectiveUserId = userId != null ? userId : (body != null ? body.get("userId") : userIdHeader);
        String effectiveTodoId = todoId != null ? todoId : (body != null ? body.get("todoId") : null);
        
        log.info("[completeTodoWithCallback] userId={}, todoId={}", effectiveUserId, effectiveTodoId);
        
        if (sceneTodoService == null) {
            return ResultModel.success(true);
        }
        
        try {
            boolean success = sceneTodoService.completeTodoWithCallback(effectiveUserId, effectiveTodoId);
            return ResultModel.success(success);
        } catch (Exception e) {
            log.error("[completeTodoWithCallback] Error: {}", e.getMessage());
            return ResultModel.success(true);
        }
    }

    @PostMapping("/batch-process")
    public ResultModel<Integer> batchProcessSceneTodos(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String sceneGroupId,
            @RequestParam(required = false) String action,
            @RequestBody(required = false) Map<String, String> body,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String effectiveUserId = userId != null ? userId : (body != null ? body.get("userId") : userIdHeader);
        String effectiveSceneGroupId = sceneGroupId != null ? sceneGroupId : (body != null ? body.get("sceneGroupId") : null);
        String effectiveAction = action != null ? action : (body != null ? body.get("action") : null);
        
        log.info("[batchProcessSceneTodos] userId={}, sceneGroupId={}, action={}", effectiveUserId, effectiveSceneGroupId, effectiveAction);
        
        if (sceneTodoService == null) {
            return ResultModel.success(0);
        }
        
        try {
            int count = sceneTodoService.batchProcessSceneTodos(effectiveUserId, effectiveSceneGroupId, effectiveAction);
            return ResultModel.success(count);
        } catch (Exception e) {
            log.error("[batchProcessSceneTodos] Error: {}", e.getMessage());
            return ResultModel.success(0);
        }
    }

    @GetMapping("/statistics")
    public ResultModel<Map<String, Integer>> getTodoStatistics(
            @RequestParam(required = false) String userId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String effectiveUserId = userId != null ? userId : userIdHeader;
        log.info("[getTodoStatistics] userId={}", effectiveUserId);
        
        if (sceneTodoService == null) {
            return ResultModel.success(Map.of("total", 0));
        }
        
        try {
            Map<String, Integer> stats = sceneTodoService.getTodoStatistics(effectiveUserId);
            return ResultModel.success(stats);
        } catch (Exception e) {
            log.error("[getTodoStatistics] Error: {}", e.getMessage());
            return ResultModel.success(Map.of("total", 0));
        }
    }

    @GetMapping("/scene/{sceneGroupId}/statistics")
    public ResultModel<Map<String, Object>> getSceneTodoStatistics(@PathVariable String sceneGroupId) {
        log.info("[getSceneTodoStatistics] sceneGroupId={}", sceneGroupId);
        
        if (sceneTodoService == null) {
            return ResultModel.success(Map.of("total", 0));
        }
        
        try {
            Map<String, Object> stats = sceneTodoService.getSceneTodoStatistics(sceneGroupId);
            return ResultModel.success(stats);
        } catch (Exception e) {
            log.error("[getSceneTodoStatistics] Error: {}", e.getMessage());
            return ResultModel.success(Map.of("total", 0));
        }
    }

    @GetMapping("/scene/{sceneGroupId}/has-pending")
    public ResultModel<Boolean> hasPendingTodos(
            @PathVariable String sceneGroupId,
            @RequestParam(required = false) String userId) {
        log.info("[hasPendingTodos] sceneGroupId={}, userId={}", sceneGroupId, userId);
        
        if (sceneTodoService == null) {
            return ResultModel.success(false);
        }
        
        try {
            boolean hasPending = sceneTodoService.hasPendingTodos(sceneGroupId, userId);
            return ResultModel.success(hasPending);
        } catch (Exception e) {
            log.error("[hasPendingTodos] Error: {}", e.getMessage());
            return ResultModel.success(false);
        }
    }

    @GetMapping("/scene/{sceneGroupId}/next-action")
    public ResultModel<String> getNextActionHint(
            @PathVariable String sceneGroupId,
            @RequestParam(required = false) String userId,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        String effectiveUserId = userId != null ? userId : userIdHeader;
        log.info("[getNextActionHint] sceneGroupId={}, userId={}", sceneGroupId, effectiveUserId);
        
        if (sceneTodoService == null) {
            return ResultModel.success("暂无待处理事项");
        }
        
        try {
            String hint = sceneTodoService.getNextActionHint(sceneGroupId, effectiveUserId);
            return ResultModel.success(hint);
        } catch (Exception e) {
            log.error("[getNextActionHint] Error: {}", e.getMessage());
            return ResultModel.success("暂无待处理事项");
        }
    }

    private WorkbenchDTO createMockWorkbench(String userId) {
        WorkbenchDTO workbench = new WorkbenchDTO();
        workbench.setUserId(userId);
        workbench.setActiveScenes(List.of());
        workbench.setPendingTodos(List.of());
        workbench.setSceneTodoGroups(List.of());
        
        WorkbenchDTO.WorkbenchStatistics stats = new WorkbenchDTO.WorkbenchStatistics();
        stats.setActiveSceneCount(0);
        stats.setPendingTodoCount(0);
        workbench.setStatistics(stats);
        
        return workbench;
    }
}
