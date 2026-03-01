package net.ooder.skill.security.dto.audit;

import lombok.Data;

import java.util.Map;

@Data
public class AuditStatsDTO {
    
    private long totalEvents;
    private long successCount;
    private long failureCount;
    private long deniedCount;
    private Map<String, Long> eventsByType;
    private Map<String, Long> eventsByUser;
    private Map<String, Long> eventsByResult;
}
