package net.ooder.skill.scene.service;

import net.ooder.skill.scene.dto.audit.AuditLogDTO;
import net.ooder.skill.scene.dto.audit.AuditStatsDTO;
import net.ooder.skill.scene.dto.PageResult;

import java.util.List;

public interface AuditService {
    
    PageResult<AuditLogDTO> listLogs(String eventType, String result, String userId, 
            String resourceId, Long startTime, Long endTime, int pageNum, int pageSize);
    
    AuditLogDTO getLogById(String recordId);
    
    AuditStatsDTO getStats();
    
    void logEvent(AuditLogDTO log);
    
    void exportLogs(String eventType, String result, String userId, 
            String resourceId, Long startTime, Long endTime);
}
