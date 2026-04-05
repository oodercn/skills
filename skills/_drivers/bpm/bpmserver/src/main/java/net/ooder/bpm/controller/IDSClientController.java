package net.ooder.bpm.controller;

import net.ooder.bpm.client.*;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.WorkflowClientService;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.ReturnType;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IDSClientController {

    @Autowired
    private WorkflowClientService workflowClientService;

    @GetMapping("/activityinst/get")
    public ResultModel<ActivityInst> getActivityInst(@RequestParam String activityInstId) {
        ResultModel<ActivityInst> result = new ResultModel<>();
        try {
            ActivityInst inst = workflowClientService.getActivityInst(activityInstId);
            result.setData(inst);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/processdef/get")
    public ResultModel<ProcessDef> getProcessDef(@RequestParam String processDefId) {
        ResultModel<ProcessDef> result = new ResultModel<>();
        try {
            ProcessDef def = workflowClientService.getProcessDef(processDefId);
            result.setData(def);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/activitydef/get")
    public ResultModel<ActivityDef> getActivityDef(@RequestParam String activityDefId) {
        ResultModel<ActivityDef> result = new ResultModel<>();
        try {
            ActivityDef def = workflowClientService.getActivityDef(activityDefId);
            result.setData(def);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/routedef/get")
    public ResultModel<RouteDef> getRouteDef(@RequestParam String routeDefId) {
        ResultModel<RouteDef> result = new ResultModel<>();
        try {
            RouteDef def = workflowClientService.getRouteDef(routeDefId);
            result.setData(def);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @PostMapping("/process/new")
    public ResultModel<ActivityInst> newProcess(
            @RequestParam String processDefId,
            @RequestParam String processInstName) {
        ResultModel<ActivityInst> result = new ResultModel<>();
        try {
            Map<RightCtx, Object> ctx = new HashMap<>();
            ProcessInst processInst = workflowClientService.newProcess(processDefId, processInstName, null, ctx);
            if (processInst != null && processInst.getActivityInstList() != null && !processInst.getActivityInstList().isEmpty()) {
                result.setData(processInst.getActivityInstList().get(0));
            }
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/processinst/get")
    public ResultModel<ProcessInst> getProcessInst(@RequestParam String processInstId) {
        ResultModel<ProcessInst> result = new ResultModel<>();
        try {
            ProcessInst inst = workflowClientService.getProcessInst(processInstId);
            result.setData(inst);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/processdefversion/get")
    public ResultModel<ProcessDefVersion> getProcessDefVersion(@RequestParam String processDefVersionId) {
        ResultModel<ProcessDefVersion> result = new ResultModel<>();
        try {
            ProcessDefVersion version = workflowClientService.getProcessDefVersion(processDefVersionId);
            result.setData(version);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/routeinst/get")
    public ResultModel<RouteInst> getRouteInst(@RequestParam String routeInstId) {
        ResultModel<RouteInst> result = new ResultModel<>();
        try {
            RouteInst inst = workflowClientService.getRouteInst(routeInstId);
            result.setData(inst);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/activityinsthistory/get")
    public ResultModel<ActivityInstHistory> getActivityInstHistory(@RequestParam String activityInstHistoryId) {
        ResultModel<ActivityInstHistory> result = new ResultModel<>();
        try {
            ActivityInstHistory history = workflowClientService.getActivityInstHistory(activityInstHistoryId);
            result.setData(history);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @PostMapping("/activityinst/endtask")
    public ResultModel<ReturnType> endTask(@RequestParam String activityInstId) {
        ResultModel<ReturnType> result = new ResultModel<>();
        try {
            Map<RightCtx, Object> ctx = new HashMap<>();
            ReturnType returnType = workflowClientService.endTask(activityInstId, ctx);
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @PostMapping("/activityinst/routeback")
    public ResultModel<ReturnType> routeBack(
            @RequestParam String activityInstId,
            @RequestParam String activityInstHistoryId) {
        ResultModel<ReturnType> result = new ResultModel<>();
        try {
            Map<RightCtx, Object> ctx = new HashMap<>();
            ReturnType returnType = workflowClientService.routeBack(activityInstId, activityInstHistoryId, ctx);
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @PostMapping("/activityinst/signreceive")
    public ResultModel<ReturnType> signReceive(@RequestParam String activityInstId) {
        ResultModel<ReturnType> result = new ResultModel<>();
        try {
            Map<RightCtx, Object> ctx = new HashMap<>();
            ReturnType returnType = workflowClientService.signReceive(activityInstId, ctx);
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @PostMapping("/processinst/complete")
    public ResultModel<ReturnType> completeProcessInst(@RequestParam String processInstId) {
        ResultModel<ReturnType> result = new ResultModel<>();
        try {
            Map<RightCtx, Object> ctx = new HashMap<>();
            ReturnType returnType = workflowClientService.completeProcessInst(processInstId, ctx);
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @PostMapping("/processinst/abort")
    public ResultModel<ReturnType> abortProcessInst(@RequestParam String processInstId) {
        ResultModel<ReturnType> result = new ResultModel<>();
        try {
            Map<RightCtx, Object> ctx = new HashMap<>();
            ReturnType returnType = workflowClientService.abortProcessInst(processInstId, ctx);
            result.setData(returnType);
        } catch (BPMException e) {
            result = new ErrorResultModel();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "bpmserver");
        health.put("systemCode", workflowClientService.getSystemCode());
        return health;
    }
}
