package net.ooder.skill.access.controller;

import net.ooder.skill.access.dto.*;
import net.ooder.skill.access.service.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/access")
public class AccessControlController {

    @Autowired
    private AccessControlService accessControlService;

    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> listPermissions() {
        return ResponseEntity.ok(accessControlService.listPermissions());
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(accessControlService.createPermission(permission));
    }

    @GetMapping("/permissions/{permissionId}")
    public ResponseEntity<Permission> getPermission(@PathVariable String permissionId) {
        Permission permission = accessControlService.getPermission(permissionId);
        if (permission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(permission);
    }

    @DeleteMapping("/permissions/{permissionId}")
    public ResponseEntity<Boolean> deletePermission(@PathVariable String permissionId) {
        return ResponseEntity.ok(accessControlService.deletePermission(permissionId));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(accessControlService.listRoles());
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(accessControlService.createRole(role));
    }

    @GetMapping("/roles/{roleId}")
    public ResponseEntity<Role> getRole(@PathVariable String roleId) {
        Role role = accessControlService.getRole(roleId);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<Boolean> deleteRole(@PathVariable String roleId) {
        return ResponseEntity.ok(accessControlService.deleteRole(roleId));
    }

    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<Boolean> assignPermissionsToRole(
            @PathVariable String roleId,
            @RequestBody List<String> permissionIds) {
        return ResponseEntity.ok(accessControlService.assignPermissionsToRole(roleId, permissionIds));
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable String userId) {
        return ResponseEntity.ok(accessControlService.getUserRoles(userId));
    }

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<Boolean> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        return ResponseEntity.ok(accessControlService.assignRolesToUser(userId, roleIds));
    }

    @DeleteMapping("/users/{userId}/roles")
    public ResponseEntity<Boolean> removeRolesFromUser(
            @PathVariable String userId,
            @RequestBody List<String> roleIds) {
        return ResponseEntity.ok(accessControlService.removeRolesFromUser(userId, roleIds));
    }

    @PostMapping("/check")
    public ResponseEntity<PermissionCheckResult> checkPermission(@RequestBody PermissionCheckRequest request) {
        return ResponseEntity.ok(accessControlService.checkPermission(request));
    }

    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<List<Permission>> getUserPermissions(@PathVariable String userId) {
        return ResponseEntity.ok(accessControlService.getUserPermissions(userId));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(accessControlService.getAccessStatistics());
    }
}
