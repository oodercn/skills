package net.ooder.skill.security.dto.audit;

import lombok.Data;
import net.ooder.skill.security.dto.audit.AuditEventType;
import net.ooder.skill.security.dto.audit.AuditResult;

import java.util.HashMap;
import java.util.Map;

@Data
public class AuditLogDTO {
    
    private String recordId;
    private AuditEventType eventType;
    private String userId;
    private String agentId;
    private String resourceType;
    private String resourceId;
    private String action;
    private AuditResult result;
    private String detail;
    private String ipAddress;
    private long timestamp;
    private Map<String, Object> metadata = new HashMap<>();
    
    public AuditLogDTO() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public static AuditLogDTO of(AuditEventType eventType, String userId, String resourceId, AuditResult result) {
        AuditLogDTO log = new AuditLogDTO();
        log.setEventType(eventType);
        log.setUserId(userId);
        log.setResourceId(resourceId);
        log.setResult(result);
        return log;
    }
    
    public static AuditLogDTO success(AuditEventType eventType, String userId, String resourceId) {
        return of(eventType, userId, resourceId, AuditResult.SUCCESS);
    }
    
    public static AuditLogDTO failure(AuditEventType eventType, String userId, String resourceId) {
        return of(eventType, userId, resourceId, AuditResult.FAILURE);
    }
    
    public static AuditLogDTO denied(AuditEventType eventType, String userId, String resourceId) {
        return of(eventType, userId, resourceId, AuditResult.DENIED);
    }
}
