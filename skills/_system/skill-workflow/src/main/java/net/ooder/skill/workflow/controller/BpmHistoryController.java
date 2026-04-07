package net.ooder.skill.workflow.controller;

import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.engine.BPMException;
import net.ooder.config.ListResultModel;
import net.ooder.skill.workflow.core.BpmCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bpm/history")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BpmHistoryController {

    private static final Logger log = LoggerFactory.getLogger(BpmHistoryController.class);

    @Autowired private BpmCoreService bpmCoreService;

    @GetMapping("/recent")
    public ResultModel<List<ActivityInstHistory>> getRecentHistory() {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        return ResultModel.success(Collections.emptyList());
    }

    @GetMapping("/activity/{activityInstId}")
    public ResultModel<List<ActivityInstHistory>> getActivityHistory(@PathVariable String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try { return ResultModel.success(bpmCoreService.getActivityInstHistoryListByActivityInst(activityInstId)); }
        catch (BPMException e) { return ResultModel.fail("获取活动历史失败: " + e.getMessage()); }
    }

    @GetMapping("/activity/{activityInstId}/last")
    public ResultModel<List<ActivityInstHistory>> getLastActivityHistory(@PathVariable String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try { return ResultModel.success(bpmCoreService.getLastActivityInstHistoryListByActivityInst(activityInstId)); }
        catch (BPMException e) { return ResultModel.fail("获取最近历史失败: " + e.getMessage()); }
    }

    @GetMapping("/process/{processInstId}")
    public ResultModel<List<ActivityInstHistory>> getProcessHistory(@PathVariable String processInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try {
            ListResultModel<List<ActivityInstHistory>> result = bpmCoreService.getActivityInstHistoryListByProcessInst(processInstId);
            return ResultModel.success(result != null && result.getData() != null ? result.getData() : Collections.emptyList());
        } catch (BPMException e) { return ResultModel.fail("获取流程历程失败: " + e.getMessage()); }
    }

    @GetMapping("/activity/{activityInstId}/route-back-options")
    public ResultModel<List<ActivityInstHistory>> getRouteBackOptions(@PathVariable String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try { return ResultModel.success(bpmCoreService.getRouteBackActivityHistoryInstList(activityInstId)); }
        catch (BPMException e) { return ResultModel.fail("获取退回选项失败: " + e.getMessage()); }
    }
}
