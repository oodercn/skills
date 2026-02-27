package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Security Statistics DTO
 * 
 * <p>Represents security statistics and metrics for monitoring
 * and reporting purposes.</p>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class SecurityStats {

    /** Total number of security policies */
    private long totalPolicies;

    /** Number of active policies */
    private long activePolicies;

    /** Total number of ACL entries */
    private long totalAcls;

    /** Total number of security scans performed */
    private long totalScans;

    /** Total number of threats detected */
    private long totalThreats;

    /** Number of threats detected */
    private long threatsDetected;

    /** Number of threats resolved */
    private long resolvedThreats;

    /** Number of blocked connections */
    private long blockedConnections;

    /** Number of audit log entries */
    private long auditLogs;

    /** Average scan time in milliseconds */
    private double averageScanTime;

    /**
     * Default constructor with initial values
     */
    public SecurityStats() {
        this.totalPolicies = 0;
        this.activePolicies = 0;
        this.totalAcls = 0;
        this.totalScans = 0;
        this.totalThreats = 0;
        this.threatsDetected = 0;
        this.resolvedThreats = 0;
        this.blockedConnections = 0;
        this.auditLogs = 0;
        this.averageScanTime = 0.0;
    }
}
