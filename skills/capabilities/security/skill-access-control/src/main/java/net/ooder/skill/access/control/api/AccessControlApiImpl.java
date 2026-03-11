package net.ooder.skill.access.control.api;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AccessControlApiImpl implements AccessControlApi {

    private boolean initialized = false;
    private boolean running = false;
    private SkillContext context;
    private final Map<String, Set<String>> userPermissions = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> roles = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userRoles = new ConcurrentHashMap<>();

    @Override
    public String getApiName() { return "skill-access-control"; }

    @Override
    public String getVersion() { return "2.3.0"; }

    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.initialized = true;
        log.info("AccessControlApi initialized");
    }

    @Override
    public void start() { this.running = true; }

    @Override
    public void stop() { this.running = false; }

    @Override
    public boolean isInitialized() { return initialized; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public Result<Map<String, Object>> grantPermission(String userId, String resource, String action) {
        String permission = resource + ":" + action;
        userPermissions.computeIfAbsent(userId, k -> new HashSet<>()).add(permission);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("permission", permission);
        result.put("granted", true);
        return Result.success(result);
    }

    @Override
    public Result<Boolean> revokePermission(String userId, String resource, String action) {
        String permission = resource + ":" + action;
        Set<String> perms = userPermissions.get(userId);
        if (perms != null) {
            perms.remove(permission);
        }
        return Result.success(true);
    }

    @Override
    public Result<List<String>> listUserPermissions(String userId) {
        Set<String> perms = userPermissions.getOrDefault(userId, new HashSet<>());
        return Result.success(new ArrayList<>(perms));
    }

    @Override
    public Result<Boolean> checkAccess(String userId, String resource, String action) {
        String permission = resource + ":" + action;
        Set<String> perms = userPermissions.getOrDefault(userId, new HashSet<>());
        return Result.success(perms.contains(permission));
    }

    @Override
    public Result<Map<String, Object>> createRole(Map<String, Object> role) {
        String roleId = UUID.randomUUID().toString();
        role.put("roleId", roleId);
        roles.put(roleId, role);
        return Result.success(role);
    }

    @Override
    public Result<Boolean> deleteRole(String roleId) {
        roles.remove(roleId);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> assignRole(String userId, String roleId) {
        userRoles.computeIfAbsent(userId, k -> new HashSet<>()).add(roleId);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> removeRole(String userId, String roleId) {
        Set<String> userRoleSet = userRoles.get(userId);
        if (userRoleSet != null) {
            userRoleSet.remove(roleId);
        }
        return Result.success(true);
    }
}
