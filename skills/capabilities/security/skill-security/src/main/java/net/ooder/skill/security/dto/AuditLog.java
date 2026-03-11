package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Audit Log Entry DTO
 * 
 * <p>Represents an audit log entry for security-related actions.</p>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class AuditLog {

    /** Unique log entry identifier */
    private String logId;

    /** User who performed the action */
    private String userId;

    /** Action type: login, logout, create, update, delete, access */
    private String action;

    /** Resource that was accessed or modified */
    private String resource;

    /** Result of the action: success, failure, denied */
    private String result;

    /** Client IP address */
    private String ip;

    /** Timestamp of the action */
    private long timestamp;

    /** Additional details about the action */
    private String details;

    /** User agent string */
    private String userAgent;

    /** Session ID */
    private String sessionId;
}
