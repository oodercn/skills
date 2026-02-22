package net.ooder.skill.audit.controller;

import net.ooder.skill.audit.dto.*;
import net.ooder.skill.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/record")
    public ResponseEntity<AuditLog> record(@RequestBody AuditLog log) {
        return ResponseEntity.ok(auditService.record(log));
    }

    @GetMapping("/logs/{logId}")
    public ResponseEntity<AuditLog> getById(@PathVariable String logId) {
        AuditLog log = auditService.getById(logId);
        if (log == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(log);
    }

    @PostMapping("/logs")
    public ResponseEntity<AuditQueryResult> query(@RequestBody AuditQueryRequest request) {
        return ResponseEntity.ok(auditService.query(request));
    }

    @GetMapping("/statistics")
    public ResponseEntity<AuditStatistics> getStatistics(
            @RequestParam(required = false) Long startTime,
            @RequestParam(required = false) Long endTime) {
        return ResponseEntity.ok(auditService.getStatistics(startTime, endTime));
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody AuditQueryRequest request,
                                         @RequestParam(defaultValue = "json") String format) {
        byte[] content = auditService.export(request, format);
        String filename = "audit-export." + format;
        String contentType = "csv".equalsIgnoreCase(format) ? "text/csv" : "application/json";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(content);
    }

    @GetMapping("/users/{userId}/logs")
    public ResponseEntity<AuditQueryResult> getByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getByUserId(userId, page, size));
    }
}
