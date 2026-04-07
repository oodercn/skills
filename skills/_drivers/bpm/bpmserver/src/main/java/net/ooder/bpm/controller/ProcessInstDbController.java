package net.ooder.bpm.controller;

import net.ooder.bpm.service.ProcessInstManagerService;
import net.ooder.config.ResultModel;
import net.ooder.config.ErrorResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/processinst")
public class ProcessInstDbController {

    @Autowired
    private ProcessInstManagerService processInstManagerService;

    @PostMapping("/new")
    public ResultModel<Map<String, Object>> newProcess(
            @RequestParam String processDefId,
            @RequestParam String processInstName,
            @RequestParam(required = false, defaultValue = "normal") String urgency,
            @RequestParam(required = false, defaultValue = "user1") String userId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        try {
            Map<String, Object> data = processInstManagerService.newProcess(processDefId, processInstName, urgency, userId);
            result.setData(data);
        } catch (Exception e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/get")
    public ResultModel<Map<String, Object>> getProcessInst(@RequestParam String processInstId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = processInstManagerService.getProcessInst(processInstId);
        if (data != null) {
            result.setData(data);
        } else {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes("流程实例不存在: " + processInstId);
        }
        return result;
    }

    @GetMapping("/list")
    public ResultModel<List<Map<String, Object>>> listProcessInsts() {
        ResultModel<List<Map<String, Object>>> result = new ResultModel<>();
        List<Map<String, Object>> data = processInstManagerService.getProcessInstList();
        result.setData(data);
        return result;
    }

    @GetMapping("/activity/get")
    public ResultModel<Map<String, Object>> getActivityInst(@RequestParam String activityInstId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = processInstManagerService.getActivityInst(activityInstId);
        if (data != null) {
            result.setData(data);
        } else {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes("活动实例不存在: " + activityInstId);
        }
        return result;
    }

    @GetMapping("/activity/list")
    public ResultModel<List<Map<String, Object>>> listActivityInsts(@RequestParam String processInstId) {
        ResultModel<List<Map<String, Object>>> result = new ResultModel<>();
        List<Map<String, Object>> data = processInstManagerService.getActivityInstsByProcessInst(processInstId);
        result.setData(data);
        return result;
    }

    @GetMapping("/history/list")
    public ResultModel<List<Map<String, Object>>> listHistory(@RequestParam String processInstId) {
        ResultModel<List<Map<String, Object>>> result = new ResultModel<>();
        List<Map<String, Object>> data = processInstManagerService.getActivityInstHistoryByProcessInst(processInstId);
        result.setData(data);
        return result;
    }

    @PostMapping("/route")
    public ResultModel<Map<String, Object>> routeTo(
            @RequestParam String activityInstId,
            @RequestParam String targetActivityDefId,
            @RequestParam(required = false, defaultValue = "user1") String userId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        try {
            Map<String, Object> data = processInstManagerService.routeTo(activityInstId, targetActivityDefId, userId);
            result.setData(data);
        } catch (Exception e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @PostMapping("/endtask")
    public ResultModel<Map<String, Object>> endTask(
            @RequestParam String activityInstId,
            @RequestParam(required = false, defaultValue = "user1") String userId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        try {
            Map<String, Object> data = processInstManagerService.endTask(activityInstId, userId);
            result.setData(data);
        } catch (Exception e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @PostMapping("/complete")
    public ResultModel<Map<String, Object>> completeProcessInst(@RequestParam String processInstId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        try {
            Map<String, Object> data = processInstManagerService.completeProcessInst(processInstId);
            result.setData(data);
        } catch (Exception e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }
}
