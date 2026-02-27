package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Firewall Rule DTO
 * 
 * <p>Represents a firewall rule configuration.</p>
 * 
 * <h3>Actions:</h3>
 * <ul>
 *   <li>{@code allow} - Allow the connection</li>
 *   <li>{@code deny} - Deny the connection</li>
 *   <li>{@code reject} - Reject with notification</li>
 *   <li>{@code log} - Log but allow</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class FirewallRule {

    /** Unique rule identifier */
    private String ruleId;

    /** Rule display name */
    private String name;

    /** Rule description */
    private String description;

    /** Action: allow, deny, reject, log */
    private String action;

    /** Protocol: tcp, udp, icmp, all */
    private String protocol;

    /** Source IP address or CIDR */
    private String sourceIp;

    /** Destination IP address or CIDR */
    private String destIp;

    /** Source port (0 for all) */
    private int sourcePort;

    /** Destination port (0 for all) */
    private int destPort;

    /** Whether the rule is enabled */
    private boolean enabled;

    /** Rule priority (higher = evaluated first) */
    private int priority;

    /** Creation timestamp */
    private long createdAt;

    /** Last update timestamp */
    private long updatedAt;

    /** Rule direction: inbound, outbound, both */
    private String direction;
}
