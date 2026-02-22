package net.ooder.skill.access.service;

import net.ooder.skill.access.dto.*;

import java.util.List;
import java.util.Map;

public interface AccessControlService {
    List<Permission> listPermissions();
    Permission createPermission(Permission permission);
    Permission getPermission(String permissionId);
    boolean deletePermission(String permissionId);
    List<Role> listRoles();
    Role createRole(Role role);
    Role getRole(String roleId);
    boolean deleteRole(String roleId);
    boolean assignPermissionsToRole(String roleId, List<String> permissionIds);
    List<String> getUserRoles(String userId);
    boolean assignRolesToUser(String userId, List<String> roleIds);
    boolean removeRolesFromUser(String userId, List<String> roleIds);
    PermissionCheckResult checkPermission(PermissionCheckRequest request);
    List<Permission> getUserPermissions(String userId);
    Map<String, Object> getAccessStatistics();
}
