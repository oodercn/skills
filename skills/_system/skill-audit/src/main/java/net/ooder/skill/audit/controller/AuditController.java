package net.ooder.skill.audit.controller;

import net.ooder.skill.audit.model.ResultModel;
import net.ooder.skill.audit.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/audit")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class AuditController {

    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    @GetMapping("/logs")
    public ResultModel<PageResult<AuditLogDTO>> listLogs(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        
        log.info("[listLogs] Listing audit logs, page: {}, size: {}", pageNum, pageSize);
        
        try {
            PageResult<AuditLogDTO> pageResult = new PageResult<>(new ArrayList<>(), 0, pageNum, pageSize);
            return ResultModel.success(pageResult);
        } catch (Exception e) {
            log.error("[listLogs] Failed: {}", e.getMessage());
            return ResultModel.error("获取审计日志失败: " + e.getMessage());
        }
    }

    @GetMapping("/logs/{recordId}")
    public ResultModel<AuditLogDTO> getLogById(@PathVariable String recordId) {
        log.info("[getLogById] Getting audit log: {}", recordId);
        
        try {
            AuditLogDTO logDTO = new AuditLogDTO();
            logDTO.setRecordId(recordId);
            logDTO.setTimestamp(System.currentTimeMillis());
            return ResultModel.success(logDTO);
        } catch (Exception e) {
            log.error("[getLogById] Failed: {}", e.getMessage());
            return ResultModel.error("获取审计日志失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResultModel<AuditStatsDTO> getStats() {
        log.info("[getStats] Getting audit statistics");
        
        try {
            AuditStatsDTO stats = new AuditStatsDTO();
            stats.setTotalEvents(0);
            stats.setSuccessEvents(0);
            stats.setFailedEvents(0);
            stats.setTodayEvents(0);
            stats.setWeekEvents(0);
            stats.setMonthEvents(0);
            stats.setEventsByType(new HashMap<>());
            stats.setEventsByUser(new HashMap<>());
            stats.setEventsByResource(new HashMap<>());
            return ResultModel.success(stats);
        } catch (Exception e) {
            log.error("[getStats] Failed: {}", e.getMessage());
            return ResultModel.error("获取审计统计失败: " + e.getMessage());
        }
    }

    @PostMapping("/logs")
    public ResultModel<Boolean> createLog(@RequestBody AuditLogDTO logDTO) {
        log.info("[createLog] Creating audit log: {}", logDTO.getEventType());
        
        try {
            logDTO.setRecordId(UUID.randomUUID().toString());
            logDTO.setTimestamp(System.currentTimeMillis());
            return ResultModel.success(true);
        } catch (Exception e) {
            log.error("[createLog] Failed: {}", e.getMessage());
            return ResultModel.error("创建审计日志失败: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public void exportLogs(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String result,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime,
            HttpServletResponse response) throws IOException {
        
        log.info("[exportLogs] Exporting audit logs");
        
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=audit_logs.csv");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter writer = response.getWriter();
        writer.write("\uFEFF");
        writer.println("记录ID,事件类型,结果,时间,用户ID,Agent ID,资源类型,资源ID,操作,详情,IP地址");
        
        writer.flush();
    }
    
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
