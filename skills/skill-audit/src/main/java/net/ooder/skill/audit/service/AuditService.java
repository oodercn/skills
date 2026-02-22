package net.ooder.skill.audit.service;

import net.ooder.skill.audit.dto.*;

import java.util.Map;

public interface AuditService {
    AuditLog record(AuditLog log);
    AuditLog getById(String logId);
    AuditQueryResult query(AuditQueryRequest request);
    AuditStatistics getStatistics(Long startTime, Long endTime);
    byte[] export(AuditQueryRequest request, String format);
    AuditQueryResult getByUserId(String userId, int page, int size);
}
