package net.ooder.mvp.skill.scene.controller;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.scene.ExecutionLogDTO;
import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.mvp.skill.scene.service.ExecutionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scene-groups")
public class ExecutionLogController {

    @Autowired
    private ExecutionLogService logService;

    @GetMapping("/{sceneGroupId}/logs")
    public ResultModel<PageResult<ExecutionLogDTO>> listLogs(
            @PathVariable String sceneGroupId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        long start = startTime != null ? startTime : 0;
        long end = endTime != null ? endTime : 0;
        
        PageResult<ExecutionLogDTO> result = logService.listLogs(sceneGroupId, start, end, level, pageNum, pageSize);
        return ResultModel.success(result);
    }
    
    @GetMapping("/{sceneGroupId}/logs/recent")
    public ResultModel<List<ExecutionLogDTO>> listRecentLogs(
            @PathVariable String sceneGroupId,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<ExecutionLogDTO> result = logService.listLogs(sceneGroupId, limit);
        return ResultModel.success(result);
    }
}
