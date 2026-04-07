package net.ooder.skill.workflow.controller;

import net.ooder.bpm.client.RouteDef;
import net.ooder.bpm.engine.BPMException;
import net.ooder.skill.workflow.core.BpmCoreService;
import net.ooder.skill.workflow.dto.RouteToRequest;
import net.ooder.skill.workflow.dto.CopyToRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bpm/perform")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BpmPerformController {

    private static final Logger log = LoggerFactory.getLogger(BpmPerformController.class);

    @Autowired private BpmCoreService bpmCoreService;

    @PostMapping("/sign-receive")
    public ResultModel<Boolean> signReceive(@RequestParam String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.signReceive(activityInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("签收失败: " + e.getMessage()); }
    }

    @PostMapping("/route-to")
    public ResultModel<Boolean> routeTo(@RequestBody RouteToRequest request) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try {
            bpmCoreService.routeTo(
                request.getActivityInstId(), 
                request.getNextActivityDefIds() != null ? request.getNextActivityDefIds() : Collections.emptyList(),
                request.getPerformerIds(),
                request.getReaderIds()
            );
            return ResultModel.success(true);
        } catch (BPMException e) { return ResultModel.fail("流转失败: " + e.getMessage()); }
    }

    @PostMapping("/route-back")
    public ResultModel<Boolean> routeBack(@RequestParam String activityInstId,
                                          @RequestParam(required = false) String historyId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.routeBack(activityInstId, historyId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("退回失败: " + e.getMessage()); }
    }

    @PostMapping("/take-back")
    public ResultModel<Boolean> takeBack(@RequestParam String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.takeBack(activityInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("收回失败: " + e.getMessage()); }
    }

    @PostMapping("/end-read")
    public ResultModel<Boolean> endRead(@RequestParam String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.endRead(activityInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("阅毕失败: " + e.getMessage()); }
    }

    @PostMapping("/end-task")
    public ResultModel<Boolean> endTask(@RequestParam String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.endTask(activityInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("完成任务失败: " + e.getMessage()); }
    }

    @PostMapping("/copy-to")
    public ResultModel<Boolean> copyTo(@RequestBody CopyToRequest request) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try {
            bpmCoreService.copyTo(request.getActivityInstId(), 
                request.getReaderIds() != null ? request.getReaderIds() : Collections.emptyList());
            return ResultModel.success(true);
        } catch (BPMException e) { return ResultModel.fail("抄送失败: " + e.getMessage()); }
    }

    @GetMapping("/{activityInstId}/routes")
    public ResultModel<List<RouteDef>> getNextRoutes(@PathVariable String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try { return ResultModel.success(bpmCoreService.getNextRoutes(activityInstId)); }
        catch (BPMException e) { return ResultModel.fail("获取路由失败: " + e.getMessage()); }
    }

    @GetMapping("/{activityInstId}/can-take-back")
    public ResultModel<Boolean> canTakeBack(@PathVariable String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(false);
        }
        try { return ResultModel.success(bpmCoreService.canTakeBack(activityInstId)); }
        catch (BPMException e) { return ResultModel.success(false); }
    }

    @GetMapping("/{activityInstId}/can-route-back")
    public ResultModel<Boolean> canRouteBack(@PathVariable String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(false);
        }
        try { return ResultModel.success(bpmCoreService.canRouteBack(activityInstId)); }
        catch (BPMException e) { return ResultModel.success(false); }
    }

    @GetMapping("/{activityInstId}/can-sign-receive")
    public ResultModel<Boolean> canSignReceive(@PathVariable String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(false);
        }
        try { return ResultModel.success(bpmCoreService.canSignReceive(activityInstId)); }
        catch (BPMException e) { return ResultModel.success(false); }
    }
}
