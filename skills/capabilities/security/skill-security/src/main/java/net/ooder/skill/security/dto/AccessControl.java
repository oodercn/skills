package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Access Control Entry DTO
 * 
 * <p>Represents an access control entry that defines permissions
 * for a principal (user or role) on a specific resource.</p>
 * 
 * <h3>Permission Types:</h3>
 * <ul>
 *   <li>{@code read} - Read access</li>
 *   <li>{@code write} - Write access</li>
 *   <li>{@code delete} - Delete access</li>
 *   <li>{@code admin} - Full administrative access</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class AccessControl {

    /** Unique ACL entry identifier */
    private String aclId;

    /** Resource type: file, folder, project, system */
    private String resourceType;

    /** Resource identifier */
    private String resourceId;

    /** Resource name for display */
    private String resource;

    /** Principal type: user, role, group */
    private String principalType;

    /** Principal identifier */
    private String principalId;

    /** User ID (alias for principalId when principalType=user) */
    private String userId;

    /** Action permission: read, write, delete, admin */
    private String action;

    /** Permission level (alias for action) */
    private String permission;

    /** ACL status: active, inactive */
    private String status;

    /** Timestamp when permission was granted */
    private long grantedAt;

    /** User who granted the permission */
    private String grantedBy;

    /**
     * Default constructor with initial values
     */
    public AccessControl() {
        this.status = "active";
        this.grantedAt = System.currentTimeMillis();
    }
}
