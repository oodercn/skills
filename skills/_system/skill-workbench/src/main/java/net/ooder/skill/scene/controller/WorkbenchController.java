package net.ooder.skill.scene.controller;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.skill.scene.dto.todo.TodoDTO;
import net.ooder.skill.scene.dto.workbench.SceneTodoGroup;
import net.ooder.skill.scene.dto.workbench.WorkbenchDTO;
import net.ooder.skill.scene.dto.workbench.BpmStatusDTO;
import net.ooder.skill.scene.dto.workbench.SceneTodoStatisticsDTO;
import net.ooder.skill.scene.dto.workbench.ProcessTodoRequest;
import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.service.SceneTodoService;
import net.ooder.skill.workflow.core.BpmCoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/workbench")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class WorkbenchController {

    private static final Logger log = LoggerFactory.getLogger(WorkbenchController.class);

    @Autowired(required = false)
    private SceneTodoService sceneTodoService;

    @Autowired(required = false)
    private BpmCoreService bpmCoreService;

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
            enrichWithBpmData(data, effectiveUserId);
            return ResultModel.success(data);
        } catch (Exception e) {
            log.error("[getWorkbenchData] Error: {}", e.getMessage());
            return ResultModel.success(createMockWorkbench(effectiveUserId));
        }
    }

    @GetMapping("/bpm-status")
    public ResultModel<BpmStatusDTO> getBpmStatus() {
        BpmStatusDTO status = new BpmStatusDTO();
        if (bpmCoreService != null && bpmCoreService.isAvailable()) {
            status.setAvailable(true);
            status.setSystemCode(bpmCoreService.getSystemCode());
        } else {
            status.setAvailable(false);
            status.setSystemCode("N/A");
        }
        return ResultModel.success(status);
    }

    @GetMapping("/bpm-waited-todos")
    public ResultModel<List<ActivityInst>> getBpmWaitedTodos(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (bpmCoreService == null || !bpmCoreService.isAvailable()) {
            return ResultModel.success(new ArrayList<>());
        }
        try { return ResultModel.success(bpmCoreService.getWaitedWorkList(page, size, null, null, null, null)); }
        catch (BPMException e) { return ResultModel.error("获取待办失败: " + e.getMessage()); }
    }

    @GetMapping("/bpm-mywork-todos")
    public ResultModel<List<ActivityInst>> getBpmMyWorkTodos(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (bpmCoreService == null || !bpmCoreService.isAvailable()) {
            return ResultModel.success(new ArrayList<>());
        }
        try { return ResultModel.success(bpmCoreService.getMyWorkList(page, size, null, null, null, null)); }
        catch (BPMException e) { return ResultModel.error("获取在办失败: " + e.getMessage()); }
    }

    @PostMapping("/bpm-process-todo")
    public ResultModel<Boolean> processBpmTodo(@RequestBody ProcessTodoRequest request) {
        if (bpmCoreService == null || !bpmCoreService.isAvailable()) {
            return ResultModel.success(false);
        }
        String activityInstId = request.getActivityInstId();
        String action = request.getAction() != null ? request.getAction() : "end-task";
        log.info("[processBpmTodo] activityInstId={}, action={}", activityInstId, action);

        try {
            switch (action.toLowerCase()) {
                case "sign": bpmCoreService.signReceive(activityInstId); break;
                case "route-to":
                    List<String> nextIds = request.getNextActivityDefIds() != null ?
                            request.getNextActivityDefIds() : Collections.emptyList();
                    bpmCoreService.routeTo(activityInstId, nextIds, request.getPerformerIds(), null); break;
                case "route-back": bpmCoreService.routeBack(activityInstId, request.getHistoryId()); break;
                case "take-back": bpmCoreService.takeBack(activityInstId); break;
                case "end-read": bpmCoreService.endRead(activityInstId); break;
                case "end-task":
                default: bpmCoreService.endTask(activityInstId); break;
            }
            return ResultModel.success(true);
        } catch (BPMException e) {
            return ResultModel.error("操作失败: " + e.getMessage());
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

        if (effectiveTodoId != null && effectiveTodoId.startsWith("bpm-") && bpmCoreService != null && bpmCoreService.isAvailable()) {
            try {
                processViaBpm(effectiveTodoId.substring(4), effectiveAction, body);
                return ResultModel.success(true);
            } catch (BPMException e) { return ResultModel.error(e.getMessage()); }
        }

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

        if (effectiveTodoId != null && effectiveTodoId.startsWith("bpm-") && bpmCoreService != null && bpmCoreService.isAvailable()) {
            try { bpmCoreService.endTask(effectiveTodoId.substring(4)); return ResultModel.success(true); }
            catch (BPMException e) { return ResultModel.error(e.getMessage()); }
        }

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

        Map<String, Integer> stats = new HashMap<>();
        if (sceneTodoService != null) {
            try {
                stats.putAll(sceneTodoService.getTodoStatistics(effectiveUserId));
            } catch (Exception ignored) {}
        }
        if (bpmCoreService != null && bpmCoreService.isAvailable()) {
            try {
                stats.put("bpmWaitedCount", bpmCoreService.getWaitedWorkList(1, 1, null, null, null, null).size());
                stats.put("bpmMyWorkCount", bpmCoreService.getMyWorkList(1, 1, null, null, null, null).size());
            } catch (Exception ignored) {}
        }
        return ResultModel.success(stats.isEmpty() ? Map.of("total", 0) : stats);
    }

    @GetMapping("/scene/{sceneGroupId}/statistics")
    public ResultModel<SceneTodoStatisticsDTO> getSceneTodoStatistics(@PathVariable String sceneGroupId) {
        log.info("[getSceneTodoStatistics] sceneGroupId={}", sceneGroupId);

        SceneTodoStatisticsDTO stats = new SceneTodoStatisticsDTO();
        stats.setTotal(0);
        
        if (sceneTodoService == null) {
            return ResultModel.success(stats);
        }

        try {
            Map<String, Object> rawStats = sceneTodoService.getSceneTodoStatistics(sceneGroupId);
            if (rawStats != null) {
                stats.setTotal(rawStats.get("total") != null ? ((Number)rawStats.get("total")).intValue() : 0);
                stats.setPending(rawStats.get("pending") != null ? ((Number)rawStats.get("pending")).intValue() : 0);
                stats.setCompleted(rawStats.get("completed") != null ? ((Number)rawStats.get("completed")).intValue() : 0);
            }
            return ResultModel.success(stats);
        } catch (Exception e) {
            log.error("[getSceneTodoStatistics] Error: {}", e.getMessage());
            return ResultModel.success(stats);
        }
    }

    @GetMapping("/scene/{sceneGroupId}/has-pending")
    public ResultModel<Boolean> hasPendingTodos(
            @PathVariable String sceneGroupId,
            @RequestParam(required = false) String userId) {
        log.info("[hasPendingTodos] sceneGroupId={}, userId={}", sceneGroupId, userId);

        if (sceneTodoService == null) {
            if (bpmCoreService != null && bpmCoreService.isAvailable()) {
                try {
                    List<ActivityInst> waited = bpmCoreService.getWaitedWorkList(1, 1, null, null, null, null);
                    return ResultModel.success(!waited.isEmpty());
                } catch (BPMException ignored) {}
            }
            return ResultModel.success(false);
        }

        try {
            boolean hasPending = sceneTodoService.hasPendingTodos(sceneGroupId, userId);
            if (!hasPending && bpmCoreService != null && bpmCoreService.isAvailable()) {
                try {
                    hasPending = !bpmCoreService.getWaitedWorkList(1, 1, null, null, null, null).isEmpty();
                } catch (Exception ignored) {}
            }
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
            if (bpmCoreService != null && bpmCoreService.isAvailable()) {
                try {
                    List<ActivityInst> waited = bpmCoreService.getWaitedWorkList(1, 5, null, null, null, null);
                    if (!waited.isEmpty()) {
                        return ResultModel.success("您有 " + waited.size() + " 个工作流待办需要处理");
                    }
                } catch (Exception ignored) {}
            }
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

    private void enrichWithBpmData(WorkbenchDTO data, String userId) {
        if (data == null || bpmCoreService == null || !bpmCoreService.isAvailable()) return;
        try {
            List<ActivityInst> waited = bpmCoreService.getWaitedWorkList(1, 5, null, null, null, null);
            if (!waited.isEmpty()) {
                data.setPendingTodos(data.getPendingTodos() != null ? data.getPendingTodos() : new ArrayList<>());
                for (ActivityInst act : waited) {
                    TodoDTO bpmTodo = new TodoDTO();
                    bpmTodo.setId("bpm-" + act.getActivityInstId());
                    bpmTodo.setTitle("BPM-" + act.getActivityInstId());
                    bpmTodo.setStatus("PENDING");
                    bpmTodo.setType("BPM_TASK");
                    data.getPendingTodos().add(bpmTodo);
                }
            }
        } catch (Exception ignored) {}
    }

    private void processViaBpm(String activityInstId, String action, Map<String, String> body) throws BPMException {
        switch (action.toLowerCase()) {
            case "sign": bpmCoreService.signReceive(activityInstId); break;
            case "approve":
            case "send":
                List<String> nextIds = body != null && body.get("nextActivityDefId") != null ?
                        List.of(body.get("nextActivityDefId")) : Collections.emptyList();
                bpmCoreService.routeTo(activityInstId, nextIds, null, null); break;
            case "reject":
            case "return": bpmCoreService.routeBack(activityInstId, body != null ? body.get("historyId") : null); break;
            case "take-back": bpmCoreService.takeBack(activityInstId); break;
            case "read": bpmCoreService.endRead(activityInstId); break;
            case "complete":
            case "done": bpmCoreService.endTask(activityInstId); break;
            default: bpmCoreService.endTask(activityInstId); break;
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
