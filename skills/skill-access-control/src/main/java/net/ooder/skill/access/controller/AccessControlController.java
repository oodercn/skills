package net.ooder.skill.access.controller;

import net.ooder.skill.access.dto.*;
import net.ooder.skill.access.service.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 访问控制REST API控制器
 * 
 * <p>提供权限管理、角色管理和访问控制的HTTP接口。</p>
 * 
 * <h3>API端点列表：</h3>
 * <table border="1">
 *   <tr><th>方法</th><th>路径</th><th>描述</th></tr>
 *   <tr><td>GET</td><td>/api/access/permissions</td><td>列出所有权限</td></tr>
 *   <tr><td>POST</td><td>/api/access/permissions</td><td>创建权限</td></tr>
 *   <tr><td>GET</td><td>/api/access/permissions/{id}</td><td>获取权限详情</td></tr>
 *   <tr><td>DELETE</td><td>/api/access/permissions/{id}</td><td>删除权限</td></tr>
 *   <tr><td>GET</td><td>/api/access/roles</td><td>列出所有角色</td></tr>
 *   <tr><td>POST</td><td>/api/access/roles</td><td>创建角色</td></tr>
 *   <tr><td>GET</td><td>/api/access/roles/{id}</td><td>获取角色详情</td></tr>
 *   <tr><td>DELETE</td><td>/api/access/roles/{id}</td><td>删除角色</td></tr>
 *   <tr><td>POST</td><td>/api/access/roles/{id}/permissions</td><td>为角色分配权限</td></tr>
 *   <tr><td>GET</td><td>/api/access/users/{userId}/roles</td><td>获取用户角色</td></tr>
 *   <tr><td>POST</td><td>/api/access/users/{userId}/roles</td><td>为用户分配角色</td></tr>
 *   <tr><td>DELETE</td><td>/api/access/users/{userId}/roles</td><td>移除用户角色</td></tr>
 *   <tr><td>POST</td><td>/api/access/check</td><td>检查权限</td></tr>
 *   <tr><td>GET</td><td>/api/access/users/{userId}/permissions</td><td>获取用户权限</td></tr>
 *   <tr><td>GET</td><td>/api/access/statistics</td><td>获取统计数据</td></tr>
 * </table>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 检查权限
 * POST /api/access/check
 * Content-Type: application/json
 * {
 *   "userId": "user-001",
 *   "permissionCode": "user:delete",
 *   "resourceType": "user",
 *   "resourceId": "user-002"
 * }
 * 
 * // 响应
 * {
 *   "allowed": true,
 *   "userId": "user-001",
 *   "permissionCode": "user:delete",
 *   "matchedRoles": ["管理员"]
 * }
 * }</pre>
 * 
 * @author Ooder Team
 * @version 0.7.3
 * @since 2026-02-22
 */
@RestController
@RequestMapping("/api/access")
public class AccessControlController {

    @Autowired
    private AccessControlService accessControlService;

    /**
     * 列出所有权限
     * 
     * @return 权限列表
     */
    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> listPermissions() {
        return ResponseEntity.ok(accessControlService.listPermissions());
    }

    /**
     * 创建权限
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * {
     *   "name": "删除用户",
     *   "code": "user:delete",
     *   "resourceType": "user",
     *   "action": "delete",
     *   "description": "允许删除系统用户"
     * }
     * }</pre>
     * 
     * @param permission 权限对象
     * @return 创建后的权限对象
     */
    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(accessControlService.createPermission(permission));
    }

    /**
     * 获取权限详情
     * 
     * @param permissionId 权限ID
     * @return 权限对象，不存在返回404
     */
    @GetMapping("/permissions/{permissionId}")
    public ResponseEntity<Permission> getPermission(@PathVariable String permissionId) {
        Permission permission = accessControlService.getPermission(permissionId);
        if (permission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(permission);
    }

    /**
     * 删除权限
     * 
     * @param permissionId 权限ID
     * @return 是否删除成功
     */
    @DeleteMapping("/permissions/{permissionId}")
    public ResponseEntity<Boolean> deletePermission(@PathVariable String permissionId) {
        return ResponseEntity.ok(accessControlService.deletePermission(permissionId));
    }

    /**
     * 列出所有角色
     * 
     * @return 角色列表
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(accessControlService.listRoles());
    }

    /**
     * 创建角色
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * {
     *   "name": "审计员",
     *   "code": "auditor",
     *   "description": "负责系统审计工作"
     * }
     * }</pre>
     * 
     * @param role 角色对象
     * @return 创建后的角色对象
     */
    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(accessControlService.createRole(role));
    }

    /**
     * 获取角色详情
     * 
     * @param roleId 角色ID
     * @return 角色对象，不存在返回404
     */
    @GetMapping("/roles/{roleId}")
    public ResponseEntity<Role> getRole(@PathVariable String roleId) {
        Role role = accessControlService.getRole(roleId);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(role);
    }

    /**
     * 删除角色
     * 
     * <p>系统角色不可删除。</p>
     * 
     * @param roleId 角色ID
     * @return 是否删除成功
     */
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<Boolean> deleteRole(@PathVariable String roleId) {
        return ResponseEntity.ok(accessControlService.deleteRole(roleId));
    }

    /**
     * 为角色分配权限
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * ["perm-read", "perm-write", "perm-delete"]
     * }</pre>
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否分配成功
     */
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Boolean> assignPermissionsToRole(
            @PathVariable String roleId,
            @RequestBody List<String> permissionIds) {
        return ResponseEntity.ok(accessControlService.assignPermissionsToRole(roleId, permissionIds));
    }

    /**
     * 获取用户的角色列表
     * 
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable String userId) {
        return ResponseEntity.ok(accessControlService.getUserRoles(userId));
    }

    /**
     * 为用户分配角色
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * ["role-admin", "role-auditor"]
     * }</pre>
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否分配成功
     */
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<Boolean> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        return ResponseEntity.ok(accessControlService.assignRolesToUser(userId, roleIds));
    }

    /**
     * 移除用户的角色
     * 
     * @param userId 用户ID
     * @param roleIds 要移除的角色ID列表
     * @return 是否移除成功
     */
    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<Boolean> removeRolesFromUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        return ResponseEntity.ok(accessControlService.removeRolesFromUser(userId, roleIds));
    }

    /**
     * 检查用户权限
     * 
     * <p>检查指定用户是否拥有某个权限。</p>
     * 
     * <h4>请求示例：</h4>
     * <pre>{@code
     * {
     *   "userId": "user-001",
     *   "permissionCode": "user:delete",
     *   "resourceType": "user",
     *   "resourceId": "user-002"
     * }
     * }</pre>
     * 
     * <h4>响应示例：</h4>
     * <pre>{@code
     * {
     *   "allowed": true,
     *   "userId": "user-001",
     *   "permissionCode": "user:delete",
     *   "matchedRoles": ["管理员"],
     *   "denialReason": null
     * }
     * }</pre>
     * 
     * @param request 权限检查请求
     * @return 检查结果
     */
    @PostMapping("/check")
    public ResponseEntity<PermissionCheckResult> checkPermission(@RequestBody PermissionCheckRequest request) {
        return ResponseEntity.ok(accessControlService.checkPermission(request));
    }

    /**
     * 获取用户的所有权限
     * 
     * <p>返回用户通过所有角色获得的权限聚合列表。</p>
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<List<Permission>> getUserPermissions(@PathVariable String userId) {
        return ResponseEntity.ok(accessControlService.getUserPermissions(userId));
    }

    /**
     * 获取访问控制统计数据
     * 
     * <h4>返回数据：</h4>
     * <ul>
     *   <li>totalPermissions - 权限总数</li>
     *   <li>totalRoles - 角色总数</li>
     *   <li>totalUsersWithRoles - 有角色分配的用户数</li>
     *   <li>systemRoles - 系统角色数</li>
     * </ul>
     * 
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(accessControlService.getAccessStatistics());
    }
}
