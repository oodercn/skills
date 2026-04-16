package net.ooder.skill.org.base;

import java.util.List;
import java.util.Map;

/**
 * Organization Skill Interface
 * 
 * <p>Provides comprehensive organization management capabilities including:</p>
 * <ul>
 *   <li>User authentication and authorization</li>
 *   <li>Organization structure management</li>
 *   <li>Role and permission management</li>
 *   <li>User lifecycle management</li>
 *   <li>Department management</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * @Autowired
 * private OrgSkill orgSkill;
 * 
 * // User login
 * UserInfo user = orgSkill.login("username", "password", "192.168.1.1");
 * 
 * // Get organization tree
 * List<OrgInfo> orgTree = orgSkill.getOrgTree();
 * }</pre>
 * 
 * <h3>Capabilities:</h3>
 * <ul>
 *   <li>{@code user.auth} - User authentication</li>
 *   <li>{@code user.manage} - User management</li>
 *   <li>{@code org.manage} - Organization management</li>
 *   <li>{@code role.manage} - Role management</li>
 *   <li>{@code sync} - Data synchronization</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 * @see UserInfo
 * @see OrgInfo
 * @see PageRequest
 * @see PageResult
 */
public interface OrgSkill {

    // ==================== Skill Metadata ====================

    /**
     * Get the unique skill identifier
     * 
     * @return the skill ID, e.g., "skill-org"
     */
    String getSkillId();

    /**
     * Get the skill display name
     * 
     * @return the skill name, e.g., "Organization Skill"
     */
    String getSkillName();

    /**
     * Get the skill version
     * 
     * @return the version string, e.g., "2.3"
     */
    String getSkillVersion();

    /**
     * Get the list of supported capabilities
     * 
     * @return list of capability identifiers
     */
    List<String> getCapabilities();

    // ==================== Authentication ====================

    /**
     * Authenticate a user with credentials
     * 
     * @param username the username
     * @param password the password
     * @param clientIp the client IP address for audit logging
     * @return the authenticated user info with token, or null if authentication failed
     */
    UserInfo login(String username, String password, String clientIp);

    /**
     * Logout a user session
     * 
     * @param token the session token to invalidate
     * @return true if logout successful
     */
    boolean logout(String token);

    /**
     * Validate a session token
     * 
     * @param token the session token to validate
     * @return true if token is valid and not expired
     */
    boolean validateToken(String token);

    /**
     * Refresh an access token using a refresh token
     * 
     * @param refreshToken the refresh token
     * @return new user info with refreshed tokens, or null if refresh failed
     */
    UserInfo refreshToken(String refreshToken);

    // ==================== User Management ====================

    /**
     * Get a user by ID
     * 
     * @param userId the user ID
     * @return the user info, or null if not found
     */
    UserInfo getUser(String userId);

    /**
     * Get a user by account name
     * 
     * @param account the account name
     * @return the user info, or null if not found
     */
    UserInfo getUserByAccount(String account);

    /**
     * Register a new user
     * 
     * @param userInfo the user information to register
     * @return the registered user with generated ID
     */
    UserInfo registerUser(UserInfo userInfo);

    /**
     * Update an existing user
     * 
     * @param userId the user ID to update
     * @param userInfo the updated user information
     * @return the updated user info
     */
    UserInfo updateUser(String userId, UserInfo userInfo);

    /**
     * Delete a user
     * 
     * @param userId the user ID to delete
     * @return true if deleted successfully
     */
    boolean deleteUser(String userId);

    /**
     * List users with pagination
     * 
     * @param request the pagination request
     * @return paginated result of users
     */
    PageResult<UserInfo> listUsers(PageRequest request);

    /**
     * Get the roles assigned to a user
     * 
     * @param userId the user ID
     * @return list of role names
     */
    List<String> getUserRoles(String userId);

    // ==================== Organization Management ====================

    /**
     * Get the complete organization tree
     * 
     * @return list of root organizations with nested children
     */
    List<OrgInfo> getOrgTree();

    /**
     * Get an organization by ID
     * 
     * @param orgId the organization ID
     * @return the organization info, or null if not found
     */
    OrgInfo getOrg(String orgId);

    /**
     * Get all users in an organization
     * 
     * @param orgId the organization ID
     * @return list of users in the organization
     */
    List<UserInfo> getOrgUsers(String orgId);

    /**
     * Synchronize organization data from external source
     * 
     * @param orgData the organization data to sync
     * @return synchronization result with status
     */
    Map<String, Object> syncOrganization(Map<String, Object> orgData);

    // ==================== Department Management ====================

    /**
     * Create a new department
     * 
     * @param params department parameters including name, parentId, leaderId
     * @return the created department
     */
    OrgInfo createDepartment(Map<String, Object> params);

    /**
     * Update an existing department
     * 
     * @param orgId the department ID
     * @param params updated department parameters
     * @return the updated department
     */
    OrgInfo updateDepartment(String orgId, Map<String, Object> params);

    /**
     * Delete a department
     * 
     * @param orgId the department ID to delete
     * @return true if deleted successfully
     */
    boolean deleteDepartment(String orgId);

    // ==================== Batch Operations ====================

    /**
     * Create a new user with parameters
     * 
     * @param params user parameters including username, password, email, etc.
     * @return creation result with user ID
     */
    Map<String, Object> createUser(Map<String, Object> params);

    /**
     * Batch create multiple users
     * 
     * @param users list of user parameters
     * @return batch creation result with success/failure counts
     */
    Map<String, Object> batchCreateUsers(List<Map<String, Object>> users);

    /**
     * Synchronize users from external source
     * 
     * @param userData the user data to sync
     * @return synchronization result
     */
    Map<String, Object> syncUsers(Map<String, Object> userData);

    // ==================== Generic Invocation ====================

    /**
     * Invoke a capability with parameters
     * 
     * @param capability the capability identifier
     * @param params the invocation parameters
     * @return the invocation result
     */
    Object invoke(String capability, Map<String, Object> params);
}
