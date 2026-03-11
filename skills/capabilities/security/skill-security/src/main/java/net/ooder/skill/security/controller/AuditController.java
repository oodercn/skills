package net.ooder.skill.security.controller;

import net.ooder.skill.security.dto.audit.*;
import net.ooder.skill.security.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AuditController {
    
    @Autowired
    private AuditService auditService;
    
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLogDTO>> queryLogs(AuditQueryDTO query) {
        return ResponseEntity.ok(auditService.query(query));
    }
    
    @GetMapping("/logs/{recordId}")
    public ResponseEntity<AuditLogDTO> getRecord(@PathVariable String recordId) {
        AuditLogDTO record = auditService.getRecord(recordId);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<AuditStatsDTO> getStats(
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        return ResponseEntity.ok(auditService.getStats(startTime, endTime));
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> count(AuditQueryDTO query) {
        return ResponseEntity.ok(auditService.count(query));
    }
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportLogs(AuditQueryDTO query) {
        List<AuditLogDTO> logs = auditService.queryAll(query);
        
        StringBuilder csv = new StringBuilder();
        csv.append("记录ID,时间,事件类型,用户,Agent,资源类型,资源ID,操作,结果,详情,IP地址\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (AuditLogDTO log : logs) {
            csv.append("\"").append(log.getRecordId()).append("\",");
            csv.append("\"").append(sdf.format(new Date(log.getTimestamp()))).append("\",");
            csv.append("\"").append(log.getEventType() != null ? log.getEventType().getName() : "").append("\",");
            csv.append("\"").append(log.getUserId() != null ? log.getUserId() : "").append("\",");
            csv.append("\"").append(log.getAgentId() != null ? log.getAgentId() : "").append("\",");
            csv.append("\"").append(log.getResourceType() != null ? log.getResourceType() : "").append("\",");
            csv.append("\"").append(log.getResourceId() != null ? log.getResourceId() : "").append("\",");
            csv.append("\"").append(log.getAction() != null ? log.getAction() : "").append("\",");
            csv.append("\"").append(log.getResult() != null ? log.getResult().getName() : "").append("\",");
            csv.append("\"").append(log.getDetail() != null ? log.getDetail().replace("\"", "\"\"") : "").append("\",");
            csv.append("\"").append(log.getIpAddress() != null ? log.getIpAddress() : "").append("\"\n");
        }
        
        String filename = "audit_logs_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
