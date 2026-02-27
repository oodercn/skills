package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Security Status DTO
 * 
 * <p>Represents the current security status of the system,
 * including active policies, threats, and security features.</p>
 * 
 * <h3>Status Values:</h3>
 * <ul>
 *   <li>{@code SECURE} - System is secure</li>
 *   <li>{@code WARNING} - Potential issues detected</li>
 *   <li>{@code CRITICAL} - Critical security issues</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class SecurityStatus {

    /** Current security status: SECURE, WARNING, CRITICAL */
    private String status;

    /** Security level: low, medium, high */
    private String securityLevel;

    /** Number of active security policies */
    private int activePolicies;

    /** Total number of policies */
    private int totalPolicies;

    /** Number of active threats */
    private int activeThreats;

    /** Number of recent security alerts */
    private int recentAlerts;

    /** Number of blocked access attempts */
    private int blockedAttempts;

    /** Threat score (0.0 - 100.0) */
    private double threatScore;

    /** Whether firewall is enabled */
    private boolean firewallEnabled;

    /** Whether encryption is enabled */
    private boolean encryptionEnabled;

    /** Whether audit logging is enabled */
    private boolean auditEnabled;

    /** Timestamp of last security scan */
    private long lastScanTime;

    /**
     * Default constructor with initial values
     */
    public SecurityStatus() {
        this.status = "SECURE";
        this.securityLevel = "medium";
        this.activePolicies = 0;
        this.totalPolicies = 0;
        this.activeThreats = 0;
        this.recentAlerts = 0;
        this.blockedAttempts = 0;
        this.threatScore = 0.0;
        this.firewallEnabled = true;
        this.encryptionEnabled = true;
        this.auditEnabled = true;
        this.lastScanTime = System.currentTimeMillis();
    }
}
