package net.ooder.skill.access.controller;

import net.ooder.skill.access.dto.*;
import net.ooder.skill.access.service.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 璁块棶鎺у埗REST API鎺у埗鍣?
 * 
 * <p>鎻愪緵鏉冮檺绠＄悊銆佽鑹茬鐞嗗拰璁块棶鎺у埗鐨凥TTP鎺ュ彛銆?/p>
 * 
 * <h3>API绔偣鍒楄〃锛?/h3>
 * <table border="1">
 *   <tr><th>鏂规硶</th><th>璺緞</th><th>鎻忚堪</th></tr>
 *   <tr><td>GET</td><td>/api/access/permissions</td><td>鍒楀嚭鎵€鏈夋潈闄?/td></tr>
 *   <tr><td>POST</td><td>/api/access/permissions</td><td>鍒涘缓鏉冮檺</td></tr>
 *   <tr><td>GET</td><td>/api/access/permissions/{id}</td><td>鑾峰彇鏉冮檺璇︽儏</td></tr>
 *   <tr><td>DELETE</td><td>/api/access/permissions/{id}</td><td>鍒犻櫎鏉冮檺</td></tr>
 *   <tr><td>GET</td><td>/api/access/roles</td><td>鍒楀嚭鎵€鏈夎鑹?/td></tr>
 *   <tr><td>POST</td><td>/api/access/roles</td><td>鍒涘缓瑙掕壊</td></tr>
 *   <tr><td>GET</td><td>/api/access/roles/{id}</td><td>鑾峰彇瑙掕壊璇︽儏</td></tr>
 *   <tr><td>DELETE</td><td>/api/access/roles/{id}</td><td>鍒犻櫎瑙掕壊</td></tr>
 *   <tr><td>POST</td><td>/api/access/roles/{id}/permissions</td><td>涓鸿鑹插垎閰嶆潈闄?/td></tr>
 *   <tr><td>GET</td><td>/api/access/users/{userId}/roles</td><td>鑾峰彇鐢ㄦ埛瑙掕壊</td></tr>
 *   <tr><td>POST</td><td>/api/access/users/{userId}/roles</td><td>涓虹敤鎴峰垎閰嶈鑹?/td></tr>
 *   <tr><td>DELETE</td><td>/api/access/users/{userId}/roles</td><td>绉婚櫎鐢ㄦ埛瑙掕壊</td></tr>
 *   <tr><td>POST</td><td>/api/access/check</td><td>妫€鏌ユ潈闄?/td></tr>
 *   <tr><td>GET</td><td>/api/access/users/{userId}/permissions</td><td>鑾峰彇鐢ㄦ埛鏉冮檺</td></tr>
 *   <tr><td>GET</td><td>/api/access/statistics</td><td>鑾峰彇缁熻鏁版嵁</td></tr>
 * </table>
 * 
 * <h3>浣跨敤绀轰緥锛?/h3>
 * <pre>{@code
 * // 妫€鏌ユ潈闄?
 * POST /api/access/check
 * Content-Type: application/json
 * {
 *   "userId": "user-001",
 *   "permissionCode": "user:delete",
 *   "resourceType": "user",
 *   "resourceId": "user-002"
 * }
 * 
 * // 鍝嶅簲
 * {
 *   "allowed": true,
 *   "userId": "user-001",
 *   "permissionCode": "user:delete",
 *   "matchedRoles": ["绠＄悊鍛?]
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
     * 鍒楀嚭鎵€鏈夋潈闄?
     * 
     * @return 鏉冮檺鍒楄〃
     */
    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> listPermissions() {
        return ResponseEntity.ok(accessControlService.listPermissions());
    }

    /**
     * 鍒涘缓鏉冮檺
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "name": "鍒犻櫎鐢ㄦ埛",
     *   "code": "user:delete",
     *   "resourceType": "user",
     *   "action": "delete",
     *   "description": "鍏佽鍒犻櫎绯荤粺鐢ㄦ埛"
     * }
     * }</pre>
     * 
     * @param permission 鏉冮檺瀵硅薄
     * @return 鍒涘缓鍚庣殑鏉冮檺瀵硅薄
     */
    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(accessControlService.createPermission(permission));
    }

    /**
     * 鑾峰彇鏉冮檺璇︽儏
     * 
     * @param permissionId 鏉冮檺ID
     * @return 鏉冮檺瀵硅薄锛屼笉瀛樺湪杩斿洖404
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
     * 鍒犻櫎鏉冮檺
     * 
     * @param permissionId 鏉冮檺ID
     * @return 鏄惁鍒犻櫎鎴愬姛
     */
    @DeleteMapping("/permissions/{permissionId}")
    public ResponseEntity<Boolean> deletePermission(@PathVariable String permissionId) {
        return ResponseEntity.ok(accessControlService.deletePermission(permissionId));
    }

    /**
     * 鍒楀嚭鎵€鏈夎鑹?
     * 
     * @return 瑙掕壊鍒楄〃
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(accessControlService.listRoles());
    }

    /**
     * 鍒涘缓瑙掕壊
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "name": "瀹¤鍛?,
     *   "code": "auditor",
     *   "description": "璐熻矗绯荤粺瀹¤宸ヤ綔"
     * }
     * }</pre>
     * 
     * @param role 瑙掕壊瀵硅薄
     * @return 鍒涘缓鍚庣殑瑙掕壊瀵硅薄
     */
    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(accessControlService.createRole(role));
    }

    /**
     * 鑾峰彇瑙掕壊璇︽儏
     * 
     * @param roleId 瑙掕壊ID
     * @return 瑙掕壊瀵硅薄锛屼笉瀛樺湪杩斿洖404
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
     * 鍒犻櫎瑙掕壊
     * 
     * <p>绯荤粺瑙掕壊涓嶅彲鍒犻櫎銆?/p>
     * 
     * @param roleId 瑙掕壊ID
     * @return 鏄惁鍒犻櫎鎴愬姛
     */
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<Boolean> deleteRole(@PathVariable String roleId) {
        return ResponseEntity.ok(accessControlService.deleteRole(roleId));
    }

    /**
     * 涓鸿鑹插垎閰嶆潈闄?
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * ["perm-read", "perm-write", "perm-delete"]
     * }</pre>
     * 
     * @param roleId 瑙掕壊ID
     * @param permissionIds 鏉冮檺ID鍒楄〃
     * @return 鏄惁鍒嗛厤鎴愬姛
     */
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Boolean> assignPermissionsToRole(
            @PathVariable String roleId,
            @RequestBody List<String> permissionIds) {
        return ResponseEntity.ok(accessControlService.assignPermissionsToRole(roleId, permissionIds));
    }

    /**
     * 鑾峰彇鐢ㄦ埛鐨勮鑹插垪琛?
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 瑙掕壊ID鍒楄〃
     */
    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable String userId) {
        return ResponseEntity.ok(accessControlService.getUserRoles(userId));
    }

    /**
     * 涓虹敤鎴峰垎閰嶈鑹?
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * ["role-admin", "role-auditor"]
     * }</pre>
     * 
     * @param userId 鐢ㄦ埛ID
     * @param roleIds 瑙掕壊ID鍒楄〃
     * @return 鏄惁鍒嗛厤鎴愬姛
     */
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<Boolean> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        return ResponseEntity.ok(accessControlService.assignRolesToUser(userId, roleIds));
    }

    /**
     * 绉婚櫎鐢ㄦ埛鐨勮鑹?
     * 
     * @param userId 鐢ㄦ埛ID
     * @param roleIds 瑕佺Щ闄ょ殑瑙掕壊ID鍒楄〃
     * @return 鏄惁绉婚櫎鎴愬姛
     */
    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<Boolean> removeRolesFromUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        return ResponseEntity.ok(accessControlService.removeRolesFromUser(userId, roleIds));
    }

    /**
     * 妫€鏌ョ敤鎴锋潈闄?
     * 
     * <p>妫€鏌ユ寚瀹氱敤鎴锋槸鍚︽嫢鏈夋煇涓潈闄愩€?/p>
     * 
     * <h4>璇锋眰绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "userId": "user-001",
     *   "permissionCode": "user:delete",
     *   "resourceType": "user",
     *   "resourceId": "user-002"
     * }
     * }</pre>
     * 
     * <h4>鍝嶅簲绀轰緥锛?/h4>
     * <pre>{@code
     * {
     *   "allowed": true,
     *   "userId": "user-001",
     *   "permissionCode": "user:delete",
     *   "matchedRoles": ["绠＄悊鍛?],
     *   "denialReason": null
     * }
     * }</pre>
     * 
     * @param request 鏉冮檺妫€鏌ヨ姹?
     * @return 妫€鏌ョ粨鏋?
     */
    @PostMapping("/check")
    public ResponseEntity<PermissionCheckResult> checkPermission(@RequestBody PermissionCheckRequest request) {
        return ResponseEntity.ok(accessControlService.checkPermission(request));
    }

    /**
     * 鑾峰彇鐢ㄦ埛鐨勬墍鏈夋潈闄?
     * 
     * <p>杩斿洖鐢ㄦ埛閫氳繃鎵€鏈夎鑹茶幏寰楃殑鏉冮檺鑱氬悎鍒楄〃銆?/p>
     * 
     * @param userId 鐢ㄦ埛ID
     * @return 鏉冮檺鍒楄〃
     */
    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<List<Permission>> getUserPermissions(@PathVariable String userId) {
        return ResponseEntity.ok(accessControlService.getUserPermissions(userId));
    }

    /**
     * 鑾峰彇璁块棶鎺у埗缁熻鏁版嵁
     * 
     * <h4>杩斿洖鏁版嵁锛?/h4>
     * <ul>
     *   <li>totalPermissions - 鏉冮檺鎬绘暟</li>
     *   <li>totalRoles - 瑙掕壊鎬绘暟</li>
     *   <li>totalUsersWithRoles - 鏈夎鑹插垎閰嶇殑鐢ㄦ埛鏁?/li>
     *   <li>systemRoles - 绯荤粺瑙掕壊鏁?/li>
     * </ul>
     * 
     * @return 缁熻鏁版嵁
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(accessControlService.getAccessStatistics());
    }
}
