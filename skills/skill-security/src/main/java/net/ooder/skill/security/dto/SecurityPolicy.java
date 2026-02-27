package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Security Policy DTO
 * 
 * <p>Represents a security policy configuration that defines
 * security rules and actions for the system.</p>
 * 
 * <h3>Policy Types:</h3>
 * <ul>
 *   <li>{@code access} - Access control policy</li>
 *   <li>{@code firewall} - Firewall rule policy</li>
 *   <li>{@code encryption} - Encryption requirement policy</li>
 *   <li>{@code audit} - Audit logging policy</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class SecurityPolicy {

    /** Unique policy identifier */
    private String policyId;

    /** Policy display name */
    private String policyName;

    /** Policy type: access, firewall, encryption, audit */
    private String policyType;

    /** Policy description */
    private String description;

    /** Policy status: enabled, disabled */
    private String status;

    /** Policy priority (higher = more important) */
    private int priority;

    /** Action to take: allow, deny, log, alert */
    private String action;

    /** Whether the policy is enabled */
    private boolean enabled;

    /** Creation timestamp */
    private long createdAt;

    /** Last update timestamp */
    private long updatedAt;

    /**
     * Default constructor with initial values
     */
    public SecurityPolicy() {
        this.status = "enabled";
        this.priority = 100;
        this.enabled = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
