package net.ooder.skill.workflow.controller;

import net.ooder.bpm.client.data.DataMap;
import net.ooder.bpm.engine.BPMException;
import net.ooder.skill.workflow.core.BpmCoreService;
import net.ooder.skill.workflow.dto.FormValuesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/api/v1/bpm/form", "/api/v1/workform"})
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BpmFormController {

    private static final Logger log = LoggerFactory.getLogger(BpmFormController.class);

    @Autowired private BpmCoreService bpmCoreService;

    @GetMapping("/activity/{activityDefId}/main-def")
    public ResultModel<Object> getActivityMainFormDef(@PathVariable String activityDefId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(null, "BPM引擎未配置");
        }
        try { return ResultModel.success(bpmCoreService.getActivityMainFormDef(activityDefId)); }
        catch (BPMException e) { return ResultModel.fail("获取活动主表单失败: " + e.getMessage()); }
    }

    @GetMapping("/activity/{activityDefId}/all-defs")
    public ResultModel<Object> getAllActivityFormDefs(@PathVariable String activityDefId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(null, "BPM引擎未配置");
        }
        try { return ResultModel.success(bpmCoreService.getAllActivityDataFormDef(activityDefId)); }
        catch (BPMException e) { return ResultModel.fail("获取全部表单定义失败: " + e.getMessage()); }
    }

    @GetMapping("/process/{processDefVersionId}/def")
    public ResultModel<Object> getProcessFormDef(@PathVariable String processDefVersionId) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(null, "BPM引擎未配置");
        }
        try { return ResultModel.success(bpmCoreService.getProcessDefForm(processDefVersionId)); }
        catch (BPMException e) { return ResultModel.fail("获取流程表单定义失败: " + e.getMessage()); }
    }

    @GetMapping("/activity/{activityInstId}/values")
    public ResultModel<FormValuesDTO> getFormValues(@PathVariable String activityInstId) {
        if (!bpmCoreService.isAvailable()) {
            FormValuesDTO empty = new FormValuesDTO();
            empty.setValues(Collections.emptyMap());
            return ResultModel.success(empty, "BPM引擎未配置");
        }
        try {
            DataMap dataMap = bpmCoreService.getActivityInstFormValues(activityInstId);
            FormValuesDTO result = new FormValuesDTO();
            if (dataMap == null) {
                result.setValues(Collections.emptyMap());
                return ResultModel.success(result);
            }
            Object mapObj = dataMap.getClass().getMethod("toMap").invoke(dataMap);
            result.setValues(mapObj instanceof Map ? (Map<String, Object>) mapObj : Collections.emptyMap());
            return ResultModel.success(result);
        } catch (BPMException e) {
            return ResultModel.fail("获取表单数据失败: " + e.getMessage());
        } catch (Exception e) {
            FormValuesDTO empty = new FormValuesDTO();
            empty.setValues(Collections.emptyMap());
            return ResultModel.success(empty);
        }
    }

    @PostMapping("/activity/{activityInstId}/values")
    public ResultModel<Boolean> saveFormValues(@PathVariable String activityInstId,
                                              @RequestBody FormValuesDTO formValues) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.saveActivityInstFormValues(activityInstId, formValues.getValues()); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("保存表单数据失败: " + e.getMessage()); }
    }

    @GetMapping("/process/{processInstId}/values")
    public ResultModel<FormValuesDTO> getProcessFormValues(@PathVariable String processInstId) {
        if (!bpmCoreService.isAvailable()) {
            FormValuesDTO empty = new FormValuesDTO();
            empty.setValues(Collections.emptyMap());
            return ResultModel.success(empty, "BPM引擎未配置");
        }
        try {
            DataMap dataMap = bpmCoreService.getProcessInstFormValues(processInstId);
            FormValuesDTO result = new FormValuesDTO();
            if (dataMap == null) {
                result.setValues(Collections.emptyMap());
                return ResultModel.success(result);
            }
            Object mapObj = dataMap.getClass().getMethod("toMap").invoke(dataMap);
            result.setValues(mapObj instanceof Map ? (Map<String, Object>) mapObj : Collections.emptyMap());
            return ResultModel.success(result);
        } catch (BPMException e) {
            return ResultModel.fail("获取流程表单数据失败: " + e.getMessage());
        } catch (Exception e) {
            FormValuesDTO empty = new FormValuesDTO();
            empty.setValues(Collections.emptyMap());
            return ResultModel.success(empty);
        }
    }

    @PostMapping("/process/{processInstId}/values")
    public ResultModel<Boolean> saveProcessFormValues(@PathVariable String processInstId,
                                                      @RequestBody FormValuesDTO formValues) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.fail("BPM引擎未配置");
        }
        try { bpmCoreService.saveProcessInstFormValues(processInstId, formValues.getValues()); return ResultModel.success(true); }
        catch (BPMException e) { return ResultModel.fail("保存流程表单数据失败: " + e.getMessage()); }
    }
}
