package net.ooder.skill.access.service;

import net.ooder.skill.access.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 访问控制服务接口
 * 
 * <p>提供基于RBAC（Role-Based Access Control）模型的权限管理服务，
 * 包括权限管理、角色管理和访问控制功能。</p>
 * 
 * <h3>核心概念：</h3>
 * <ul>
 *   <li><b>权限（Permission）</b>：对特定资源的操作许可，如"读取文件"、"删除用户"</li>
 *   <li><b>角色（Role）</b>：权限的集合，如"管理员"、"普通用户"、"审计员"</li>
 *   <li><b>用户（User）</b>：被分配角色的主体，通过角色间接获得权限</li>
 * </ul>
 * 
 * <h3>应用场景：</h3>
 * <ul>
 *   <li>企业应用：根据部门、职位分配不同权限</li>
 *   <li>SaaS平台：多租户权限隔离，不同套餐不同权限</li>
 *   <li>管理系统：后台管理权限控制，敏感操作审批</li>
 *   <li>API网关：接口访问权限校验</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 创建权限
 * Permission perm = new Permission();
 * perm.setName("删除用户");
 * perm.setCode("user:delete");
 * perm.setResourceType("user");
 * perm.setAction("delete");
 * accessControlService.createPermission(perm);
 * 
 * // 创建角色并分配权限
 * Role adminRole = new Role();
 * adminRole.setName("管理员");
 * adminRole.setCode("admin");
 * accessControlService.createRole(adminRole);
 * accessControlService.assignPermissionsToRole("role-admin", 
 *     Arrays.asList("perm-read", "perm-write", "perm-delete"));
 * 
 * // 给用户分配角色
 * accessControlService.assignRolesToUser("user-001", 
 *     Arrays.asList("role-admin"));
 * 
 * // 检查权限
 * PermissionCheckRequest request = new PermissionCheckRequest();
 * request.setUserId("user-001");
 * request.setPermissionCode("user:delete");
 * PermissionCheckResult result = accessControlService.checkPermission(request);
 * if (result.isAllowed()) {
 *     // 执行删除操作
 * }
 * }</pre>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
public interface AccessControlService {
    
    /**
     * 列出所有权限
     * 
     * @return 权限列表
     */
    List<Permission> listPermissions();
    
    /**
     * 创建权限
     * 
     * <p>创建一个新的权限定义。权限通常按"资源类型:操作"格式命名，
     * 如"user:read"、"file:delete"。</p>
     * 
     * @param permission 权限对象
     * @return 创建后的权限对象，包含生成的ID
     */
    Permission createPermission(Permission permission);
    
    /**
     * 获取权限详情
     * 
     * @param permissionId 权限ID
     * @return 权限对象，不存在则返回null
     */
    Permission getPermission(String permissionId);
    
    /**
     * 删除权限
     * 
     * <p>删除权限会同时从所有角色中移除该权限。</p>
     * 
     * @param permissionId 权限ID
     * @return 是否删除成功
     */
    boolean deletePermission(String permissionId);
    
    /**
     * 列出所有角色
     * 
     * @return 角色列表
     */
    List<Role> listRoles();
    
    /**
     * 创建角色
     * 
     * <p>创建一个新的角色定义。系统角色（system=true）不可删除。</p>
     * 
     * @param role 角色对象
     * @return 创建后的角色对象，包含生成的ID
     */
    Role createRole(Role role);
    
    /**
     * 获取角色详情
     * 
     * @param roleId 角色ID
     * @return 角色对象，不存在则返回null
     */
    Role getRole(String roleId);
    
    /**
     * 删除角色
     * 
     * <p>系统角色不可删除。删除角色会同时移除所有用户的该角色分配。</p>
     * 
     * @param roleId 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(String roleId);
    
    /**
     * 为角色分配权限
     * 
     * <p>设置角色拥有的权限列表，会覆盖原有权限。</p>
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否分配成功
     */
    boolean assignPermissionsToRole(String roleId, List<String> permissionIds);
    
    /**
     * 获取用户的角色列表
     * 
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<String> getUserRoles(String userId);
    
    /**
     * 为用户分配角色
     * 
     * <p>为用户添加角色，不会覆盖已有角色。</p>
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否分配成功
     */
    boolean assignRolesToUser(String userId, List<String> roleIds);
    
    /**
     * 移除用户的角色
     * 
     * @param userId 用户ID
     * @param roleIds 要移除的角色ID列表
     * @return 是否移除成功
     */
    boolean removeRolesFromUser(String userId, List<String> roleIds);
    
    /**
     * 检查用户权限
     * 
     * <p>检查用户是否拥有指定的权限。会检查用户所有角色下的权限，
     * 只要有一个角色拥有该权限即返回允许。</p>
     * 
     * <h4>检查逻辑：</h4>
     * <ol>
     *   <li>获取用户的所有角色</li>
     *   <li>遍历角色，检查是否包含请求的权限</li>
     *   <li>支持通配符匹配：admin权限或*操作可匹配所有权限</li>
     * </ol>
     * 
     * @param request 权限检查请求
     * @return 检查结果，包含是否允许、匹配的角色等信息
     */
    PermissionCheckResult checkPermission(PermissionCheckRequest request);
    
    /**
     * 获取用户的所有权限
     * 
     * <p>聚合用户所有角色下的权限，返回去重后的权限列表。</p>
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(String userId);
    
    /**
     * 获取访问控制统计数据
     * 
     * <p>返回系统中权限、角色、用户的统计信息。</p>
     * 
     * @return 统计数据Map
     */
    Map<String, Object> getAccessStatistics();
}
