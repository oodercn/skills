package net.ooder.skill.workflow.controller;

import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.skill.workflow.core.BpmCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bpm/todo")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BpmTodoController {

    private static final Logger log = LoggerFactory.getLogger(BpmTodoController.class);

    @Autowired private BpmCoreService bpmCoreService;

    @GetMapping("/waited")
    public ResultModel<List<ActivityInst>> getWaitedTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String processDefId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) String keyword) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try {
            return ResultModel.success(bpmCoreService.getWaitedWorkList(page, size, processDefId, startTime, endTime, keyword));
        } catch (BPMException e) {
            return ResultModel.fail("获取待办列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/mywork")
    public ResultModel<List<ActivityInst>> getMyWorkTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String processDefId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) String keyword) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try {
            return ResultModel.success(bpmCoreService.getMyWorkList(page, size, processDefId, startTime, endTime, keyword));
        } catch (BPMException e) {
            return ResultModel.fail("获取在办列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/completed")
    public ResultModel<List<ActivityInst>> getCompletedTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String processDefId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try {
            return ResultModel.success(bpmCoreService.getCompletedWorkList(page, size, processDefId, startTime, endTime));
        } catch (BPMException e) {
            return ResultModel.fail("获取已办列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/read")
    public ResultModel<List<ActivityInst>> getReadTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String processDefId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try {
            return ResultModel.success(bpmCoreService.getReadWorkList(page, size, processDefId, startTime, endTime));
        } catch (BPMException e) {
            return ResultModel.fail("获取阅文列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/draft")
    public ResultModel<List<ActivityInst>> getDraftList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String processDefId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try {
            return ResultModel.success(bpmCoreService.getDraftList(page, size, processDefId));
        } catch (BPMException e) {
            return ResultModel.fail("获取草稿列表失败: " + e.getMessage());
        }
    }
}
