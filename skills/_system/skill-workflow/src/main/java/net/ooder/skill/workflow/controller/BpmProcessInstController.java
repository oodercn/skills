package net.ooder.skill.workflow.controller;

import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.right.RightConditionEnums;
import net.ooder.config.ListResultModel;
import net.ooder.skill.workflow.core.BpmCoreService;
import net.ooder.skill.workflow.dto.StartProcessRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bpm/process-inst")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BpmProcessInstController {

    private static final Logger log = LoggerFactory.getLogger(BpmProcessInstController.class);

    @Autowired private BpmCoreService bpmCoreService;

    @PostMapping("/start")
    public ResultModel<ProcessInst> startProcess(@RequestBody StartProcessRequest request) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置，无法启动流程");
        }
        try {
            String urgency = request.getUrgency() != null ? request.getUrgency() : "NORMAL";
            ProcessInst inst = bpmCoreService.startProcess(
                request.getProcessDefVersionId(), 
                request.getName(), 
                urgency, 
                request.getFormValues()
            );
            return ResultModel.success(inst);
        } catch (BPMException e) {
            log.error("[startProcess] {}", e.getMessage());
            return ResultModel.fail("启动流程失败: " + e.getMessage());
        }
    }

    @GetMapping("/{processInstId}")
    public ResultModel<ProcessInst> getProcessInst(@PathVariable String processInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try {
            return ResultModel.success(bpmCoreService.getProcessInst(processInstId));
        } catch (BPMException e) {
            return ResultModel.fail("获取流程实例失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResultModel<List<ProcessInst>> getProcessInstList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try {
            ListResultModel<List<ProcessInst>> result = bpmCoreService.getProcessInstList(
                null, RightConditionEnums.CONDITION_ALLWORK, null);
            if (result == null || result.getData() == null) return ResultModel.success(Collections.emptyList());
            List<ProcessInst> all = result.getData();
            int from = (page - 1) * size;
            int to = Math.min(from + size, all.size());
            return ResultModel.success(from < all.size() ? all.subList(from, to) : Collections.emptyList());
        } catch (BPMException e) {
            return ResultModel.fail("获取流程实例列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/{processInstId}/complete")
    public ResultModel<Boolean> completeProcess(@PathVariable String processInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.completeProcessInst(processInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("完成流程失败: " + e.getMessage()); }
    }

    @PostMapping("/{processInstId}/suspend")
    public ResultModel<Boolean> suspendProcess(@PathVariable String processInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.suspendProcessInst(processInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("挂起流程失败: " + e.getMessage()); }
    }

    @PostMapping("/{processInstId}/resume")
    public ResultModel<Boolean> resumeProcess(@PathVariable String processInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.resumeProcessInst(processInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("恢复流程失败: " + e.getMessage()); }
    }

    @PostMapping("/{processInstId}/abort")
    public ResultModel<Boolean> abortProcess(@PathVariable String processInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.abortProcessInst(processInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("终止流程失败: " + e.getMessage()); }
    }

    @PostMapping("/{processInstId}/delete")
    public ResultModel<Boolean> deleteProcess(@PathVariable String processInstId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.deleteProcessInst(processInstId); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("删除流程失败: " + e.getMessage()); }
    }
}
