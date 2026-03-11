package net.ooder.skill.security.service.impl;

import net.ooder.skill.security.dto.audit.*;
import net.ooder.skill.security.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AuditServiceImpl implements AuditService {
    
    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);
    
    private final Map<String, AuditLogDTO> auditStore = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        log.info("AuditService initialized");
    }
    
    @Override
    public void log(AuditLogDTO record) {
        if (record.getRecordId() == null) {
            record.setRecordId("audit-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (record.getTimestamp() == 0) {
            record.setTimestamp(System.currentTimeMillis());
        }
        
        auditStore.put(record.getRecordId(), record);
        log.debug("Audit log: {} - {} - {}", record.getEventType(), record.getResult(), record.getResourceId());
    }
    
    @Override
    public AuditLogDTO getRecord(String recordId) {
        return auditStore.get(recordId);
    }
    
    @Override
    public List<AuditLogDTO> query(AuditQueryDTO query) {
        return auditStore.values().stream()
            .filter(r -> query.getEventType() == null || query.getEventType() == r.getEventType())
            .filter(r -> query.getUserId() == null || query.getUserId().equals(r.getUserId()))
            .filter(r -> query.getAgentId() == null || query.getAgentId().equals(r.getAgentId()))
            .filter(r -> query.getResourceType() == null || query.getResourceType().equals(r.getResourceType()))
            .filter(r -> query.getResourceId() == null || query.getResourceId().equals(r.getResourceId()))
            .filter(r -> query.getResult() == null || query.getResult() == r.getResult())
            .filter(r -> query.getStartTime() == null || r.getTimestamp() >= query.getStartTime())
            .filter(r -> query.getEndTime() == null || r.getTimestamp() <= query.getEndTime())
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .skip((query.getPageNum() - 1) * query.getPageSize())
            .limit(query.getPageSize())
            .collect(Collectors.toList());
    }
    
    @Override
    public AuditStatsDTO getStats(Long startTime, Long endTime) {
        AuditStatsDTO stats = new AuditStatsDTO();
        
        List<AuditLogDTO> records = auditStore.values().stream()
            .filter(r -> startTime == null || r.getTimestamp() >= startTime)
            .filter(r -> endTime == null || r.getTimestamp() <= endTime)
            .collect(Collectors.toList());
        
        stats.setTotalEvents(records.size());
        stats.setSuccessCount(records.stream().filter(r -> r.getResult() == AuditResult.SUCCESS).count());
        stats.setFailureCount(records.stream().filter(r -> r.getResult() == AuditResult.FAILURE).count());
        stats.setDeniedCount(records.stream().filter(r -> r.getResult() == AuditResult.DENIED).count());
        
        Map<String, Long> byType = new HashMap<>();
        Map<String, Long> byUser = new HashMap<>();
        Map<String, Long> byResult = new HashMap<>();
        
        for (AuditLogDTO r : records) {
            String typeKey = r.getEventType() != null ? r.getEventType().getCode() : "unknown";
            byType.merge(typeKey, 1L, Long::sum);
            
            String userKey = r.getUserId() != null ? r.getUserId() : "anonymous";
            byUser.merge(userKey, 1L, Long::sum);
            
            String resultKey = r.getResult() != null ? r.getResult().getCode() : "unknown";
            byResult.merge(resultKey, 1L, Long::sum);
        }
        
        stats.setEventsByType(byType);
        stats.setEventsByUser(byUser);
        stats.setEventsByResult(byResult);
        
        return stats;
    }
    
    @Override
    public long count(AuditQueryDTO query) {
        return auditStore.values().stream()
            .filter(r -> query.getEventType() == null || query.getEventType() == r.getEventType())
            .filter(r -> query.getUserId() == null || query.getUserId().equals(r.getUserId()))
            .filter(r -> query.getResourceId() == null || query.getResourceId().equals(r.getResourceId()))
            .filter(r -> query.getResult() == null || query.getResult() == r.getResult())
            .count();
    }
    
    @Override
    public List<AuditLogDTO> queryAll(AuditQueryDTO query) {
        return auditStore.values().stream()
            .filter(r -> query.getEventType() == null || query.getEventType() == r.getEventType())
            .filter(r -> query.getUserId() == null || query.getUserId().equals(r.getUserId()))
            .filter(r -> query.getAgentId() == null || query.getAgentId().equals(r.getAgentId()))
            .filter(r -> query.getResourceType() == null || query.getResourceType().equals(r.getResourceType()))
            .filter(r -> query.getResourceId() == null || query.getResourceId().equals(r.getResourceId()))
            .filter(r -> query.getResult() == null || query.getResult() == r.getResult())
            .filter(r -> query.getStartTime() == null || r.getTimestamp() >= query.getStartTime())
            .filter(r -> query.getEndTime() == null || r.getTimestamp() <= query.getEndTime())
            .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
            .collect(Collectors.toList());
    }
}
