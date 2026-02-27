package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Audit Statistics DTO
 * 
 * <p>Represents audit log statistics for reporting and monitoring.</p>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class AuditStats {

    /** Total number of audit log entries */
    private long totalLogs;

    /** Number of successful actions */
    private long successCount;

    /** Number of failed actions */
    private long failureCount;

    /** Number of denied actions */
    private long deniedCount;

    /** Number of logs today */
    private long todayCount;

    /** Number of logs this week */
    private long weekCount;

    /** Number of logs this month */
    private long monthCount;
}
