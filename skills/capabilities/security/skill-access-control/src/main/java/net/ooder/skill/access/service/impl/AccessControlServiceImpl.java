package net.ooder.skill.access.service.impl;

import net.ooder.skill.access.dto.*;
import net.ooder.skill.access.service.AccessControlService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AccessControlServiceImpl implements AccessControlService {

    private final Map<String, Permission> permissions = new ConcurrentHashMap<>();
    private final Map<String, Role> roles = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userRoles = new ConcurrentHashMap<>();

    public AccessControlServiceImpl() {
        initDefaultData();
    }

    private void initDefaultData() {
        Permission readPermission = new Permission();
        readPermission.setPermissionId("perm-read");
        readPermission.setName("Read");
        readPermission.setCode("read");
        readPermission.setResourceType("*");
        readPermission.setAction("read");
        readPermission.setDescription("Read permission");
        permissions.put(readPermission.getPermissionId(), readPermission);

        Permission writePermission = new Permission();
        writePermission.setPermissionId("perm-write");
        writePermission.setName("Write");
        writePermission.setCode("write");
        writePermission.setResourceType("*");
        writePermission.setAction("write");
        writePermission.setDescription("Write permission");
        permissions.put(writePermission.getPermissionId(), writePermission);

        Permission adminPermission = new Permission();
        adminPermission.setPermissionId("perm-admin");
        adminPermission.setName("Admin");
        adminPermission.setCode("admin");
        adminPermission.setResourceType("*");
        adminPermission.setAction("*");
        adminPermission.setDescription("Full admin permission");
        permissions.put(adminPermission.getPermissionId(), adminPermission);

        Role adminRole = new Role();
        adminRole.setRoleId("role-admin");
        adminRole.setName("Administrator");
        adminRole.setCode("admin");
        adminRole.setDescription("System administrator role");
        adminRole.setSystem(true);
        adminRole.setPermissionIds(Arrays.asList("perm-read", "perm-write", "perm-admin"));
        roles.put(adminRole.getRoleId(), adminRole);

        Role userRole = new Role();
        userRole.setRoleId("role-user");
        userRole.setName("User");
        userRole.setCode("user");
        userRole.setDescription("Standard user role");
        userRole.setSystem(true);
        userRole.setPermissionIds(Arrays.asList("perm-read"));
        roles.put(userRole.getRoleId(), userRole);
    }

    @Override
    public List<Permission> listPermissions() {
        return new ArrayList<>(permissions.values());
    }

    @Override
    public Permission createPermission(Permission permission) {
        if (permission.getPermissionId() == null || permission.getPermissionId().isEmpty()) {
            permission.setPermissionId("perm-" + UUID.randomUUID().toString().substring(0, 8));
        }
        permission.setCreateTime(System.currentTimeMillis());
        permissions.put(permission.getPermissionId(), permission);
        return permission;
    }

    @Override
    public Permission getPermission(String permissionId) {
        return permissions.get(permissionId);
    }

    @Override
    public boolean deletePermission(String permissionId) {
        return permissions.remove(permissionId) != null;
    }

    @Override
    public List<Role> listRoles() {
        return new ArrayList<>(roles.values());
    }

    @Override
    public Role createRole(Role role) {
        if (role.getRoleId() == null || role.getRoleId().isEmpty()) {
            role.setRoleId("role-" + UUID.randomUUID().toString().substring(0, 8));
        }
        role.setCreateTime(System.currentTimeMillis());
        roles.put(role.getRoleId(), role);
        return role;
    }

    @Override
    public Role getRole(String roleId) {
        return roles.get(roleId);
    }

    @Override
    public boolean deleteRole(String roleId) {
        Role role = roles.get(roleId);
        if (role != null && role.isSystem()) {
            return false;
        }
        return roles.remove(roleId) != null;
    }

    @Override
    public boolean assignPermissionsToRole(String roleId, List<String> permissionIds) {
        Role role = roles.get(roleId);
        if (role == null) {
            return false;
        }
        role.setPermissionIds(permissionIds);
        return true;
    }

    @Override
    public List<String> getUserRoles(String userId) {
        Set<String> roleIds = userRoles.get(userId);
        return roleIds != null ? new ArrayList<>(roleIds) : new ArrayList<>();
    }

    @Override
    public boolean assignRolesToUser(String userId, List<String> roleIds) {
        userRoles.computeIfAbsent(userId, k -> new HashSet<>()).addAll(roleIds);
        return true;
    }

    @Override
    public boolean removeRolesFromUser(String userId, List<String> roleIds) {
        Set<String> userRoleSet = userRoles.get(userId);
        if (userRoleSet != null) {
            userRoleSet.removeAll(roleIds);
        }
        return true;
    }

    @Override
    public PermissionCheckResult checkPermission(PermissionCheckRequest request) {
        PermissionCheckResult result = new PermissionCheckResult();
        result.setUserId(request.getUserId());
        result.setPermissionCode(request.getPermissionCode());

        Set<String> userRoleIds = userRoles.get(request.getUserId());
        if (userRoleIds == null || userRoleIds.isEmpty()) {
            result.setAllowed(false);
            result.setDenialReason("User has no roles assigned");
            return result;
        }

        List<String> matchedRoles = new ArrayList<>();
        for (String roleId : userRoleIds) {
            Role role = roles.get(roleId);
            if (role != null && role.getPermissionIds() != null) {
                for (String permId : role.getPermissionIds()) {
                    Permission perm = permissions.get(permId);
                    if (perm != null && matchesPermission(perm, request)) {
                        matchedRoles.add(role.getName());
                        break;
                    }
                }
            }
        }

        if (!matchedRoles.isEmpty()) {
            result.setAllowed(true);
            result.setMatchedRoles(matchedRoles);
        } else {
            result.setAllowed(false);
            result.setDenialReason("No matching permission found");
        }

        return result;
    }

    private boolean matchesPermission(Permission perm, PermissionCheckRequest request) {
        if (perm.getCode().equals(request.getPermissionCode())) {
            return true;
        }
        if (perm.getCode().equals("admin") || perm.getAction().equals("*")) {
            return true;
        }
        if (perm.getResourceType().equals("*") || perm.getResourceType().equals(request.getResourceType())) {
            return perm.getAction().equals("*") || perm.getAction().equals(request.getPermissionCode());
        }
        return false;
    }

    @Override
    public List<Permission> getUserPermissions(String userId) {
        Set<String> userRoleIds = userRoles.get(userId);
        if (userRoleIds == null) {
            return new ArrayList<>();
        }

        Set<String> permissionIds = new HashSet<>();
        for (String roleId : userRoleIds) {
            Role role = roles.get(roleId);
            if (role != null && role.getPermissionIds() != null) {
                permissionIds.addAll(role.getPermissionIds());
            }
        }

        return permissionIds.stream()
                .map(permissions::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getAccessStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPermissions", permissions.size());
        stats.put("totalRoles", roles.size());
        stats.put("totalUsersWithRoles", userRoles.size());
        stats.put("systemRoles", roles.values().stream().filter(Role::isSystem).count());
        return stats;
    }
}
