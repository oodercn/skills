package net.ooder.skill.role.controller;

import net.ooder.skill.role.dto.PageResult;
import net.ooder.skill.role.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/role-management")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class RoleManagementController {

    private static final Logger log = LoggerFactory.getLogger(RoleManagementController.class);

    private final Map<String, Map<String, Object>> roleStore = new HashMap<>();
    private final Map<String, Map<String, Object>> userStore = new HashMap<>();

    @GetMapping("/roles")
    public ResultModel<List<Map<String, Object>>> getAllRoles() {
        log.info("[RoleManagementController] Get all roles");
        
        if (roleStore.isEmpty()) {
            Map<String, Object> adminRole = new HashMap<>();
            adminRole.put("id", "admin");
            adminRole.put("name", "Administrator");
            adminRole.put("description", "Full access to all features");
            adminRole.put("permissions", Arrays.asList("read", "write", "delete", "admin"));
            roleStore.put("admin", adminRole);
            
            Map<String, Object> userRole = new HashMap<>();
            userRole.put("id", "user");
            userRole.put("name", "User");
            userRole.put("description", "Standard user access");
            userRole.put("permissions", Arrays.asList("read", "write"));
            roleStore.put("user", userRole);
        }
        
        return ResultModel.success(new ArrayList<>(roleStore.values()));
    }

    @PostMapping("/roles")
    public ResultModel<Map<String, Object>> createRole(@RequestBody Map<String, Object> role) {
        log.info("[RoleManagementController] Create role: {}", role.get("name"));
        String id = UUID.randomUUID().toString();
        role.put("id", id);
        role.put("createTime", new Date().toString());
        roleStore.put(id, role);
        return ResultModel.success(role);
    }

    @GetMapping("/roles/{id}")
    public ResultModel<Map<String, Object>> getRole(@PathVariable String id) {
        log.info("[RoleManagementController] Get role: {}", id);
        Map<String, Object> role = roleStore.get(id);
        if (role == null) {
            return ResultModel.notFound("Role not found: " + id);
        }
        return ResultModel.success(role);
    }

    @PutMapping("/roles/{id}")
    public ResultModel<Map<String, Object>> updateRole(@PathVariable String id, @RequestBody Map<String, Object> role) {
        log.info("[RoleManagementController] Update role: {}", id);
        if (!roleStore.containsKey(id)) {
            return ResultModel.notFound("Role not found: " + id);
        }
        role.put("id", id);
        role.put("updateTime", new Date().toString());
        roleStore.put(id, role);
        return ResultModel.success(role);
    }

    @DeleteMapping("/roles/{id}")
    public ResultModel<Boolean> deleteRole(@PathVariable String id) {
        log.info("[RoleManagementController] Delete role: {}", id);
        if (!roleStore.containsKey(id)) {
            return ResultModel.notFound("Role not found: " + id);
        }
        roleStore.remove(id);
        return ResultModel.success(true);
    }

    @GetMapping("/users")
    public ResultModel<List<Map<String, Object>>> getAllUsers() {
        log.info("[RoleManagementController] Get all users");
        
        if (userStore.isEmpty()) {
            Map<String, Object> user1 = new HashMap<>();
            user1.put("id", "user-001");
            user1.put("name", "Admin");
            user1.put("email", "admin@ooder.net");
            user1.put("role", "admin");
            userStore.put("user-001", user1);
            
            Map<String, Object> user2 = new HashMap<>();
            user2.put("id", "user-002");
            user2.put("name", "User");
            user2.put("email", "user@ooder.net");
            user2.put("role", "user");
            userStore.put("user-002", user2);
        }
        
        return ResultModel.success(new ArrayList<>(userStore.values()));
    }

    @GetMapping("/users/{id}")
    public ResultModel<Map<String, Object>> getUser(@PathVariable String id) {
        log.info("[RoleManagementController] Get user: {}", id);
        Map<String, Object> user = userStore.get(id);
        if (user == null) {
            return ResultModel.notFound("User not found: " + id);
        }
        return ResultModel.success(user);
    }

    @PutMapping("/users/{id}")
    public ResultModel<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody Map<String, Object> user) {
        log.info("[RoleManagementController] Update user: {}", id);
        if (!userStore.containsKey(id)) {
            return ResultModel.notFound("User not found: " + id);
        }
        user.put("id", id);
        user.put("updateTime", new Date().toString());
        userStore.put(id, user);
        return ResultModel.success(user);
    }

    @DeleteMapping("/users/{id}")
    public ResultModel<Boolean> deleteUser(@PathVariable String id) {
        log.info("[RoleManagementController] Delete user: {}", id);
        if (!userStore.containsKey(id)) {
            return ResultModel.notFound("User not found: " + id);
        }
        userStore.remove(id);
        return ResultModel.success(true);
    }

    @PostMapping("/users/{userId}/bind-role/{roleId}")
    public ResultModel<Map<String, Object>> bindUserToRole(@PathVariable String userId, @PathVariable String roleId) {
        log.info("[RoleManagementController] Bind role {} to user {}", roleId, userId);
        
        Map<String, Object> user = userStore.get(userId);
        if (user == null) {
            user = new HashMap<>();
            user.put("id", userId);
            userStore.put(userId, user);
        }
        
        user.put("role", roleId);
        user.put("roleBindTime", new Date().toString());
        
        return ResultModel.success(user);
    }

    @GetMapping("/roles/{roleId}/menus")
    public ResultModel<List<String>> getRoleMenus(@PathVariable String roleId) {
        log.info("[RoleManagementController] Get menus for role: {}", roleId);
        
        List<String> menus = new ArrayList<>();
        Map<String, Object> role = roleStore.get(roleId);
        if (role != null && role.containsKey("menus")) {
            @SuppressWarnings("unchecked")
            List<String> roleMenus = (List<String>) role.get("menus");
            menus = roleMenus;
        }
        
        return ResultModel.success(menus);
    }

    @PutMapping("/roles/{roleId}/menus")
    public ResultModel<Boolean> setRoleMenus(@PathVariable String roleId, @RequestBody List<String> menuIds) {
        log.info("[RoleManagementController] Set menus for role: {}, count: {}", roleId, menuIds != null ? menuIds.size() : 0);
        
        Map<String, Object> role = roleStore.get(roleId);
        if (role == null) {
            return ResultModel.notFound("Role not found: " + roleId);
        }
        
        role.put("menus", menuIds);
        role.put("menuUpdateTime", new Date().toString());
        
        return ResultModel.success(true);
    }

    @GetMapping("/roles/{roleId}/users")
    public ResultModel<List<Map<String, Object>>> getUsersByRole(@PathVariable String roleId) {
        log.info("[RoleManagementController] Get users by role: {}", roleId);
        
        List<Map<String, Object>> users = new ArrayList<>();
        for (Map<String, Object> user : userStore.values()) {
            if (roleId.equals(user.get("role"))) {
                users.add(user);
            }
        }
        
        return ResultModel.success(users);
    }

    @PostMapping("/users")
    public ResultModel<Map<String, Object>> createUser(@RequestBody Map<String, Object> user) {
        log.info("[RoleManagementController] Create user: {}", user.get("name"));
        String id = UUID.randomUUID().toString();
        user.put("id", id);
        user.put("createTime", new Date().toString());
        userStore.put(id, user);
        return ResultModel.success(user);
    }

    @GetMapping("/types")
    public ResultModel<List<Map<String, Object>>> listRoleTypes() {
        log.info("[RoleManagementController] List role types");
        
        List<Map<String, Object>> types = new ArrayList<>();
        
        Map<String, Object> type1 = new HashMap<>();
        type1.put("code", "system");
        type1.put("name", "系统角色");
        type1.put("description", "系统内置角色");
        types.add(type1);
        
        Map<String, Object> type2 = new HashMap<>();
        type2.put("code", "custom");
        type2.put("name", "自定义角色");
        type2.put("description", "用户自定义角色");
        types.add(type2);
        
        Map<String, Object> type3 = new HashMap<>();
        type3.put("code", "business");
        type3.put("name", "业务角色");
        type3.put("description", "业务相关角色");
        types.add(type3);
        
        return ResultModel.success(types);
    }

    @GetMapping("/statuses")
    public ResultModel<List<Map<String, Object>>> listRoleStatuses() {
        log.info("[RoleManagementController] List role statuses");
        
        List<Map<String, Object>> statuses = new ArrayList<>();
        
        Map<String, Object> status1 = new HashMap<>();
        status1.put("code", "active");
        status1.put("name", "启用");
        status1.put("description", "角色已启用");
        statuses.add(status1);
        
        Map<String, Object> status2 = new HashMap<>();
        status2.put("code", "inactive");
        status2.put("name", "禁用");
        status2.put("description", "角色已禁用");
        statuses.add(status2);
        
        return ResultModel.success(statuses);
    }
}
