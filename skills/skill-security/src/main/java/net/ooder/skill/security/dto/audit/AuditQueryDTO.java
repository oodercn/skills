package net.ooder.skill.security.dto.audit;

import lombok.Data;

import java.util.List;

@Data
public class AuditQueryDTO {
    
    private AuditEventType eventType;
    private String userId;
    private String agentId;
    private String resourceType;
    private String resourceId;
    private AuditResult result;
    private Long startTime;
    private Long endTime;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
