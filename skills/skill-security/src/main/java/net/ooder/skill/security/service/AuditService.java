package net.ooder.skill.security.service;

import net.ooder.skill.security.dto.audit.*;

import java.util.List;

public interface AuditService {
    
    void log(AuditLogDTO record);
    
    AuditLogDTO getRecord(String recordId);
    
    List<AuditLogDTO> query(AuditQueryDTO query);
    
    AuditStatsDTO getStats(Long startTime, Long endTime);
    
    long count(AuditQueryDTO query);
    
    List<AuditLogDTO> queryAll(AuditQueryDTO query);
}
