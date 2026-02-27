package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Firewall Status DTO
 * 
 * <p>Represents the current status of the firewall.</p>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class FirewallStatus {

    /** Whether firewall is enabled */
    private boolean enabled;

    /** Number of active firewall rules */
    private int activeRules;

    /** Total number of blocked connections */
    private long blockedConnections;

    /** Total number of allowed connections */
    private long allowedConnections;

    /** Timestamp of last status update */
    private long lastUpdated;

    /** Firewall mode: allow_all, deny_all, custom */
    private String mode;

    /** Whether firewall is in learning mode */
    private boolean learningMode;
}
