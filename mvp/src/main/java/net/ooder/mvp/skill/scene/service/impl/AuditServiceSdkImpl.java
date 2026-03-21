package net.ooder.mvp.skill.scene.service.impl;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.audit.AuditEventType;
import net.ooder.mvp.skill.scene.dto.audit.AuditLogDTO;
import net.ooder.mvp.skill.scene.dto.audit.AuditResultType;
import net.ooder.mvp.skill.scene.dto.audit.AuditStatsDTO;
import net.ooder.mvp.skill.scene.service.AuditService;
import net.ooder.scene.core.security.AuditLog;
import net.ooder.scene.core.security.AuditLogQuery;
import net.ooder.scene.core.security.OperationResult;
import net.ooder.scene.core.security.OperationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
public class AuditServiceSdkImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceSdkImpl.class);

    private net.ooder.scene.core.security.AuditService sdkAuditService;

    @Autowired(required = false)
    public void setSdkAuditService(net.ooder.scene.core.security.AuditService sdkAuditService) {
        this.sdkAuditService = sdkAuditService;
        if (sdkAuditService != null) {
            log.info("[AuditServiceSdkImpl] Initialized with SE SDK AuditService");
        } else {
            log.error("[AuditServiceSdkImpl] SE SDK AuditService not available!");
        }
    }
    
    private net.ooder.scene.core.security.AuditService getAuditService() {
        if (sdkAuditService == null) {
            throw new IllegalStateException("SE SDK AuditService is not available. Please check the SDK configuration.");
        }
        return sdkAuditService;
    }

    @Override
    public PageResult<AuditLogDTO> listLogs(String eventType, String result, String userId,
            String resourceId, Long startTime, Long endTime, int pageNum, int pageSize) {
        log.info("[listLogs] Querying audit logs - eventType: {}, result: {}, userId: {}", eventType, result, userId);
        
        AuditLogQuery query = new AuditLogQuery();
        query.setUserId(userId);
        query.setResource(resourceId);
        query.setStartTime(startTime != null ? startTime : 0L);
        query.setEndTime(endTime != null ? endTime : System.currentTimeMillis());
        if (result != null && !result.isEmpty()) {
            query.setResult(convertToOperationResult(result));
        }
        
        List<AuditLog> logs = getAuditService().queryLogs(query).join();
        
        if (logs == null) {
            return PageResult.empty();
        }
        
        List<AuditLogDTO> pageData = logs.stream()
            .skip((long) (pageNum - 1) * pageSize)
            .limit(pageSize)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return PageResult.of(pageData, logs.size(), pageNum, pageSize);
    }

    @Override
    public AuditLogDTO getLogById(String recordId) {
        log.info("[getLogById] Getting audit log: {}", recordId);
        
        AuditLogQuery query = new AuditLogQuery();
        query.setOperation(recordId);
        
        List<AuditLog> logs = getAuditService().queryLogs(query).join();
        if (logs != null && !logs.isEmpty()) {
            return convertToDTO(logs.get(0));
        }
        
        return null;
    }

    @Override
    public AuditStatsDTO getStats() {
        log.info("[getStats] Getting audit statistics");
        
        AuditStatsDTO statsDTO = new AuditStatsDTO();
        
        long now = System.currentTimeMillis();
        long dayMs = 24 * 3600000L;
        
        AuditLogQuery query = new AuditLogQuery();
        query.setStartTime(now - 30 * dayMs);
        query.setEndTime(now);
        
        List<AuditLog> logs = getAuditService().queryLogs(query).join();
        
        if (logs != null) {
            statsDTO.setTotalEvents(logs.size());
            
            long successCount = logs.stream()
                .filter(l -> l.getResult() == OperationResult.SUCCESS)
                .count();
            long failureCount = logs.stream()
                .filter(l -> l.getResult() == OperationResult.FAILURE)
                .count();
            long deniedCount = logs.stream()
                .filter(l -> l.getResult() == OperationResult.DENIED)
                .count();
            
            statsDTO.setSuccessCount(successCount);
            statsDTO.setFailureCount(failureCount);
            statsDTO.setDeniedCount(deniedCount);
            
            long todayStart = now - dayMs;
            long todayCount = logs.stream()
                .filter(l -> l.getTimestamp() >= todayStart)
                .count();
            statsDTO.setTodayCount(todayCount);
            
            long weekStart = now - 7 * dayMs;
            long weekCount = logs.stream()
                .filter(l -> l.getTimestamp() >= weekStart)
                .count();
            statsDTO.setWeekCount(weekCount);
            
            statsDTO.setMonthCount(logs.size());
        }
        
        return statsDTO;
    }

    @Override
    public void logEvent(AuditLogDTO logEntry) {
        log.info("[logEvent] Logging audit event: {}", logEntry.getEventType());
        
        Map<String, Object> details = logEntry.getMetadata();
        if (details == null) {
            details = new HashMap<>();
        }
        if (logEntry.getDetail() != null) {
            details.put("description", logEntry.getDetail());
        }
        if (logEntry.getEventType() != null) {
            details.put("eventType", logEntry.getEventType().getCode());
        }
        
        OperationContext context = new OperationContext();
        context.setUserId(logEntry.getUserId());
        context.setTimestamp(logEntry.getTimestamp() > 0 ? logEntry.getTimestamp() : System.currentTimeMillis());
        
        getAuditService().logOperation(
            context,
            logEntry.getAction(),
            logEntry.getResourceType(),
            logEntry.getResourceId(),
            convertResult(logEntry.getResult()),
            details
        );
    }

    @Override
    public void exportLogs(String eventType, String result, String userId,
            String resourceId, Long startTime, Long endTime) {
        log.info("[exportLogs] Exporting audit logs - eventType: {}", eventType);
        
        AuditLogQuery query = new AuditLogQuery();
        query.setUserId(userId);
        query.setResource(resourceId);
        query.setStartTime(startTime != null ? startTime : 0L);
        query.setEndTime(endTime != null ? endTime : System.currentTimeMillis());
        if (result != null && !result.isEmpty()) {
            query.setResult(convertToOperationResult(result));
        }
        
        getAuditService().exportLogs(query).join();
    }
    
    private AuditLogDTO convertToDTO(AuditLog log) {
        if (log == null) return null;
        
        AuditLogDTO dto = new AuditLogDTO();
        dto.setRecordId(log.getLogId());
        dto.setTimestamp(log.getTimestamp());
        dto.setUserId(log.getUserId());
        dto.setResourceType(log.getResource());
        dto.setResourceId(log.getResourceId());
        dto.setAction(log.getOperation());
        dto.setIpAddress(log.getIpAddress());
        
        if (log.getResult() != null) {
            dto.setResult(convertOperationResult(log.getResult()));
        }
        
        Map<String, Object> details = log.getDetails();
        if (details != null) {
            dto.setMetadata(details);
            Object desc = details.get("description");
            if (desc != null) {
                dto.setDetail(desc.toString());
            }
            Object eventTypeCode = details.get("eventType");
            if (eventTypeCode != null) {
                dto.setEventType(convertEventType(eventTypeCode.toString()));
            }
        }
        
        return dto;
    }
    
    private OperationResult convertResult(AuditResultType result) {
        if (result == null) return OperationResult.SUCCESS;
        switch (result.getCode()) {
            case "SUCCESS":
                return OperationResult.SUCCESS;
            case "FAILURE":
                return OperationResult.FAILURE;
            case "DENIED":
                return OperationResult.DENIED;
            default:
                return OperationResult.SUCCESS;
        }
    }
    
    private OperationResult convertToOperationResult(String code) {
        if (code == null) return null;
        switch (code) {
            case "SUCCESS":
                return OperationResult.SUCCESS;
            case "FAILURE":
                return OperationResult.FAILURE;
            case "DENIED":
                return OperationResult.DENIED;
            default:
                return null;
        }
    }
    
    private AuditResultType convertOperationResult(OperationResult result) {
        if (result == null) return null;
        switch (result) {
            case SUCCESS:
                return AuditResultType.SUCCESS;
            case FAILURE:
                return AuditResultType.FAILURE;
            case DENIED:
                return AuditResultType.DENIED;
            default:
                return AuditResultType.SUCCESS;
        }
    }
    
    private AuditEventType convertEventType(String code) {
        if (code == null) return null;
        for (AuditEventType type : AuditEventType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
