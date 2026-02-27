package net.ooder.skill.security.api;

import net.ooder.skill.security.dto.*;

import java.util.List;
import java.util.Map;

/**
 * Security API Interface
 * 
 * <p>Provides comprehensive security management capabilities including:</p>
 * <ul>
 *   <li>Security policy management</li>
 *   <li>Access control list (ACL) management</li>
 *   <li>Threat detection and management</li>
 *   <li>Firewall configuration</li>
 *   <li>Audit logging</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * @Autowired
 * private SecurityApi securityApi;
 * 
 * SecurityPolicy policy = new SecurityPolicy();
 * policy.setName("Default Policy");
 * securityApi.createPolicy(policy);
 * }</pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 * @see SecurityPolicy
 * @see AccessControl
 * @see ThreatInfo
 */
public interface SecurityApi {

    /**
     * Get the API name identifier
     * 
     * @return the API name, typically "skill-security"
     */
    String getApiName();

    /**
     * Get the API version
     * 
     * @return the version string, e.g., "2.3"
     */
    String getVersion();

    /**
     * Initialize the security API with the given context
     * 
     * @param context initialization context containing configuration parameters
     */
    void initialize(Map<String, Object> context);

    /**
     * Start the security service
     */
    void start();

    /**
     * Stop the security service
     */
    void stop();

    /**
     * Check if the API is initialized
     * 
     * @return true if initialized, false otherwise
     */
    boolean isInitialized();

    /**
     * Check if the API is running
     * 
     * @return true if running, false otherwise
     */
    boolean isRunning();

    // ==================== Status & Statistics ====================

    /**
     * Get the current security status
     * 
     * @return the security status containing firewall state, active policies, and threats
     */
    SecurityStatus getStatus();

    /**
     * Get security statistics
     * 
     * @return the security statistics including policy counts, ACL counts, and threat counts
     */
    SecurityStats getStats();

    // ==================== Policy Management ====================

    /**
     * Create a new security policy
     * 
     * @param policy the policy to create
     * @return the created policy with generated ID
     */
    SecurityPolicy createPolicy(SecurityPolicy policy);

    /**
     * Update an existing security policy
     * 
     * @param policyId the ID of the policy to update
     * @param policy the updated policy data
     * @return the updated policy, or null if not found
     */
    SecurityPolicy updatePolicy(String policyId, SecurityPolicy policy);

    /**
     * Delete a security policy
     * 
     * @param policyId the ID of the policy to delete
     * @return true if deleted successfully
     */
    boolean deletePolicy(String policyId);

    /**
     * Get a security policy by ID
     * 
     * @param policyId the policy ID
     * @return the policy, or null if not found
     */
    SecurityPolicy getPolicy(String policyId);

    /**
     * List all security policies
     * 
     * @return list of all policies
     */
    List<SecurityPolicy> listPolicies();

    /**
     * Enable a security policy
     * 
     * @param policyId the ID of the policy to enable
     * @return true if enabled successfully
     */
    boolean enablePolicy(String policyId);

    /**
     * Disable a security policy
     * 
     * @param policyId the ID of the policy to disable
     * @return true if disabled successfully
     */
    boolean disablePolicy(String policyId);

    // ==================== Access Control ====================

    /**
     * Create a new access control entry
     * 
     * @param acl the ACL entry to create
     * @return the created ACL with generated ID
     */
    AccessControl createAcl(AccessControl acl);

    /**
     * Update an existing ACL entry
     * 
     * @param aclId the ID of the ACL to update
     * @param acl the updated ACL data
     * @return the updated ACL
     */
    AccessControl updateAcl(String aclId, AccessControl acl);

    /**
     * Delete an ACL entry
     * 
     * @param aclId the ID of the ACL to delete
     * @return true if deleted successfully
     */
    boolean deleteAcl(String aclId);

    /**
     * Get an ACL entry by ID
     * 
     * @param aclId the ACL ID
     * @return the ACL entry, or null if not found
     */
    AccessControl getAcl(String aclId);

    /**
     * List all ACL entries
     * 
     * @return list of all ACL entries
     */
    List<AccessControl> listAcls();

    /**
     * Check if a user has permission to perform an action on a resource
     * 
     * @param userId the user ID
     * @param resource the resource identifier
     * @param action the action to perform (e.g., "read", "write", "delete")
     * @return true if permission is granted
     */
    boolean checkPermission(String userId, String resource, String action);

    // ==================== Threat Management ====================

    /**
     * Report a new security threat
     * 
     * @param threat the threat information
     * @return the reported threat with generated ID and timestamp
     */
    ThreatInfo reportThreat(ThreatInfo threat);

    /**
     * Get a threat by ID
     * 
     * @param threatId the threat ID
     * @return the threat information, or null if not found
     */
    ThreatInfo getThreat(String threatId);

    /**
     * List threats by status
     * 
     * @param status the status filter (e.g., "active", "resolved"), or null for all
     * @return list of matching threats
     */
    List<ThreatInfo> listThreats(String status);

    /**
     * Resolve a threat
     * 
     * @param threatId the ID of the threat to resolve
     * @param resolution the resolution description
     * @return the resolved threat, or null if not found
     */
    ThreatInfo resolveThreat(String threatId, String resolution);

    // ==================== Firewall Management ====================

    /**
     * Enable the firewall
     * 
     * @return true if enabled successfully
     */
    boolean enableFirewall();

    /**
     * Disable the firewall
     * 
     * @return true if disabled successfully
     */
    boolean disableFirewall();

    /**
     * Get the current firewall status
     * 
     * @return the firewall status
     */
    FirewallStatus getFirewallStatus();

    /**
     * Add a firewall rule
     * 
     * @param rule the firewall rule to add
     * @return the created rule with generated ID
     */
    FirewallRule addFirewallRule(FirewallRule rule);

    /**
     * Remove a firewall rule
     * 
     * @param ruleId the ID of the rule to remove
     * @return true if removed successfully
     */
    boolean removeFirewallRule(String ruleId);

    /**
     * List all firewall rules
     * 
     * @return list of all firewall rules
     */
    List<FirewallRule> listFirewallRules();

    // ==================== Audit Logging ====================

    /**
     * Query audit logs
     * 
     * @param query the query parameters (e.g., userId, action, startTime, endTime)
     * @return list of matching audit log entries
     */
    List<AuditLog> queryAuditLogs(Map<String, Object> query);

    /**
     * Get audit log statistics
     * 
     * @return the audit statistics
     */
    AuditStats getAuditStats();
}
