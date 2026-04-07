package net.ooder.skill.workflow.controller;

import net.ooder.bpm.client.ProcessDef;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.common.Filter;
import net.ooder.config.ListResultModel;
import net.ooder.skill.workflow.core.BpmCoreService;
import net.ooder.skill.workflow.core.BpmConditionHelper;
import net.ooder.skill.workflow.dto.BpmStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/bpm/process-def")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BpmProcessDefController {

    private static final Logger log = LoggerFactory.getLogger(BpmProcessDefController.class);

    @Autowired private BpmCoreService bpmCoreService;

    @GetMapping("/list")
    public ResultModel<List<ProcessDefVersion>> getProcessDefList(
            @RequestParam(required = false) String status) {
        if (!bpmCoreService.isAvailable()) {
            return ResultModel.success(Collections.emptyList(), "BPM引擎未配置");
        }
        try {
            BPMCondition condition = null;
            if ("RELEASED".equals(status)) {
                condition = BpmConditionHelper.buildPublishedProcessDefCondition();
            }
            ListResultModel<List<ProcessDefVersion>> result = bpmCoreService.getProcessDefVersionList(condition, null);
            return ResultModel.success(result != null && result.getData() != null ? result.getData() : Collections.emptyList());
        } catch (BPMException e) {
            log.error("[getProcessDefList] {}", e.getMessage());
            return ResultModel.fail("获取流程定义列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{processDefVersionId}")
    public ResultModel<ProcessDefVersion> getProcessDef(@PathVariable String processDefVersionId) {
        try {
            ProcessDefVersion version = bpmCoreService.getProcessDefVersion(processDefVersionId);
            return ResultModel.success(version);
        } catch (BPMException e) {
            log.error("[getProcessDef] {}", e.getMessage());
            return ResultModel.fail("获取流程定义失败: " + e.getMessage());
        }
    }

    @GetMapping("/{processDefVersionId}/first-activity")
    public ResultModel<Object> getFirstActivity(@PathVariable String processDefVersionId) {
        try {
            return ResultModel.success(bpmCoreService.getFirstActivityDefInProcess(processDefVersionId));
        } catch (BPMException e) {
            return ResultModel.fail("获取首节点失败: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResultModel<BpmStatusDTO> getStatus() {
        BpmStatusDTO status = new BpmStatusDTO();
        status.setAvailable(bpmCoreService.isAvailable());
        status.setSystemCode(bpmCoreService.getSystemCode());
        return ResultModel.success(status);
    }
}
