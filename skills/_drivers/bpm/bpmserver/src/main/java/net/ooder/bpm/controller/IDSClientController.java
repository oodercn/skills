package net.ooder.bpm.controller;

import net.ooder.config.ResultModel;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IDSClientController {

    @GetMapping("/activityinst/get")
    public ResultModel<Map<String, Object>> getActivityInst(@RequestParam String activityInstId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("activityInstId", activityInstId);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @GetMapping("/processdef/get")
    public ResultModel<Map<String, Object>> getProcessDef(@RequestParam String processDefId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("processDefId", processDefId);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @GetMapping("/activitydef/get")
    public ResultModel<Map<String, Object>> getActivityDef(@RequestParam String activityDefId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("activityDefId", activityDefId);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @GetMapping("/routedef/get")
    public ResultModel<Map<String, Object>> getRouteDef(@RequestParam String routeDefId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("routeDefId", routeDefId);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @PostMapping("/process/new")
    public ResultModel<Map<String, Object>> newProcess(
            @RequestParam String processDefId,
            @RequestParam String processInstName) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("processDefId", processDefId);
        data.put("processInstName", processInstName);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @GetMapping("/processinst/get")
    public ResultModel<Map<String, Object>> getProcessInst(@RequestParam String processInstId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("processInstId", processInstId);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @GetMapping("/processdefversion/get")
    public ResultModel<Map<String, Object>> getProcessDefVersion(@RequestParam String processDefVersionId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("processDefVersionId", processDefVersionId);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @GetMapping("/routeinst/get")
    public ResultModel<Map<String, Object>> getRouteInst(@RequestParam String routeInstId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("routeInstId", routeInstId);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @GetMapping("/activityinsthistory/get")
    public ResultModel<Map<String, Object>> getActivityInstHistory(@RequestParam String activityInstHistoryId) {
        ResultModel<Map<String, Object>> result = new ResultModel<>();
        Map<String, Object> data = new HashMap<>();
        data.put("activityInstHistoryId", activityInstHistoryId);
        data.put("status", "not_implemented");
        result.setData(data);
        return result;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "bpmserver");
        return health;
    }
}
