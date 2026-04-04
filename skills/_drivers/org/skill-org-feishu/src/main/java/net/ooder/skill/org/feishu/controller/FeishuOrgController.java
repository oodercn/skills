package net.ooder.skill.org.feishu.controller;

import net.ooder.skill.org.feishu.dto.AuthTokenDTO;
import net.ooder.skill.org.feishu.dto.QrCodeDTO;
import net.ooder.skill.org.feishu.dto.SyncResultDTO;
import net.ooder.skill.org.feishu.model.FeishuDepartment;
import net.ooder.skill.org.feishu.model.FeishuUser;
import net.ooder.skill.org.feishu.client.FeishuApiClient;
import net.ooder.skill.org.feishu.service.FeishuAuthService;
import net.ooder.skill.org.feishu.service.FeishuOrgSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/org/feishu")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FeishuOrgController {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuOrgController.class);
    
    @Autowired
    private FeishuAuthService authService;
    
    @Autowired
    private FeishuOrgSyncService syncService;
    
    @Autowired
    private FeishuApiClient apiClient;
    
    @PostMapping("/auth/qrcode")
    public Map<String, Object> generateQrCode() {
        log.info("[generateQrCode] Generating Feishu QR code for auth");
        Map<String, Object> result = new HashMap<>();
        try {
            QrCodeDTO qrCode = authService.generateQrCode();
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", qrCode);
            result.put("message", "二维码生成成功");
        } catch (Exception e) {
            log.error("Failed to generate QR code", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "二维码生成失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/auth/login")
    public Map<String, Object> loginWithRecommend() {
        log.info("[loginWithRecommend] Generating Feishu QR code with recommend scopes");
        Map<String, Object> result = new HashMap<>();
        try {
            QrCodeDTO qrCode = authService.generateQrCodeWithRecommend();
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", qrCode);
            result.put("message", "授权二维码生成成功");
        } catch (Exception e) {
            log.error("Failed to generate QR code", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "二维码生成失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/auth/status/{sessionId}")
    public Map<String, Object> checkAuthStatus(@PathVariable String sessionId) {
        log.info("[checkAuthStatus] Checking auth status for session: {}", sessionId);
        Map<String, Object> result = new HashMap<>();
        try {
            String status = authService.checkScanStatus(sessionId);
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", Map.of(
                "sessionId", sessionId,
                "scanStatus", status
            ));
        } catch (Exception e) {
            log.error("Failed to check auth status", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "状态查询失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/auth/callback")
    public Map<String, Object> handleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state) {
        log.info("[handleCallback] Handling Feishu callback, code: {}, state: {}", code, state);
        Map<String, Object> result = new HashMap<>();
        try {
            AuthTokenDTO token = authService.handleCallback(code, state);
            if (token != null) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("data", token);
                result.put("message", "授权成功");
            } else {
                result.put("code", 401);
                result.put("status", "error");
                result.put("message", "授权失败");
            }
        } catch (Exception e) {
            log.error("Failed to handle callback", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "回调处理失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/auth/refresh")
    public Map<String, Object> refreshToken(@RequestBody Map<String, String> request) {
        log.info("[refreshToken] Refreshing token");
        Map<String, Object> result = new HashMap<>();
        try {
            String refreshToken = request.get("refreshToken");
            AuthTokenDTO token = authService.refreshToken(refreshToken);
            if (token != null) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("data", token);
                result.put("message", "令牌刷新成功");
            } else {
                result.put("code", 401);
                result.put("status", "error");
                result.put("message", "刷新令牌无效");
            }
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "令牌刷新失败: " + e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/auth/unbind")
    public Map<String, Object> unbind(@RequestHeader("Authorization") String authorization) {
        log.info("[unbind] Unbinding Feishu account");
        Map<String, Object> result = new HashMap<>();
        try {
            String accessToken = authorization.replace("Bearer ", "");
            boolean success = authService.unbind(accessToken);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "解绑成功");
            } else {
                result.put("code", 404);
                result.put("status", "error");
                result.put("message", "未找到绑定信息");
            }
        } catch (Exception e) {
            log.error("Failed to unbind", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "解绑失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/sync/all")
    public Map<String, Object> syncAll() {
        log.info("[syncAll] Starting full organization sync");
        Map<String, Object> result = new HashMap<>();
        try {
            SyncResultDTO syncResult = syncService.syncAll();
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", syncResult);
            result.put("message", syncResult.getMessage());
        } catch (Exception e) {
            log.error("Failed to sync organization", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "组织同步失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/sync/users")
    public Map<String, Object> syncUsers() {
        log.info("[syncUsers] Starting users sync");
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeishuUser> users = syncService.syncUsers();
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", Map.of(
                "total", users.size(),
                "users", users
            ));
            result.put("message", "用户同步成功");
        } catch (Exception e) {
            log.error("Failed to sync users", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "用户同步失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/sync/departments")
    public Map<String, Object> syncDepartments() {
        log.info("[syncDepartments] Starting departments sync");
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeishuDepartment> depts = syncService.syncDepartments();
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", Map.of(
                "total", depts.size(),
                "departments", depts
            ));
            result.put("message", "部门同步成功");
        } catch (Exception e) {
            log.error("Failed to sync departments", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "部门同步失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/users")
    public Map<String, Object> getUsers(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String keyword) {
        log.info("[getUsers] Getting users, departmentId: {}, keyword: {}", departmentId, keyword);
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeishuUser> users;
            if (keyword != null && !keyword.isEmpty()) {
                users = syncService.searchUsers(keyword);
            } else {
                users = syncService.getCachedUsers();
            }
            
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", Map.of(
                "total", users.size(),
                "list", users
            ));
        } catch (Exception e) {
            log.error("Failed to get users", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "获取用户失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/users/{userId}")
    public Map<String, Object> getUser(@PathVariable String userId) {
        log.info("[getUser] Getting user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        try {
            FeishuUser user = syncService.getCachedUser(userId);
            if (user != null) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("data", user);
            } else {
                result.put("code", 404);
                result.put("status", "error");
                result.put("message", "用户不存在: " + userId);
            }
        } catch (Exception e) {
            log.error("Failed to get user", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "获取用户失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/departments")
    public Map<String, Object> getDepartments() {
        log.info("[getDepartments] Getting all departments");
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeishuDepartment> depts = syncService.getCachedDepartments();
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", Map.of(
                "total", depts.size(),
                "list", depts
            ));
        } catch (Exception e) {
            log.error("Failed to get departments", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "获取部门失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/departments/{deptId}")
    public Map<String, Object> getDepartment(@PathVariable String deptId) {
        log.info("[getDepartment] Getting department: {}", deptId);
        Map<String, Object> result = new HashMap<>();
        try {
            FeishuDepartment dept = syncService.getCachedDepartment(deptId);
            if (dept != null) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("data", dept);
            } else {
                result.put("code", 404);
                result.put("status", "error");
                result.put("message", "部门不存在: " + deptId);
            }
        } catch (Exception e) {
            log.error("Failed to get department", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "获取部门失败: " + e.getMessage());
        }
        return result;
    }
    
    @GetMapping("/tree")
    public Map<String, Object> getOrgTree() {
        log.info("[getOrgTree] Getting organization tree");
        Map<String, Object> result = new HashMap<>();
        try {
            List<FeishuDepartment> depts = syncService.getCachedDepartments();
            List<Map<String, Object>> tree = buildOrgTree(depts);
            result.put("code", 200);
            result.put("status", "success");
            result.put("data", tree);
        } catch (Exception e) {
            log.error("Failed to get org tree", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "获取组织树失败: " + e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/cache")
    public Map<String, Object> clearCache() {
        log.info("[clearCache] Clearing organization cache");
        Map<String, Object> result = new HashMap<>();
        try {
            syncService.clearCache();
            result.put("code", 200);
            result.put("status", "success");
            result.put("message", "缓存清理成功");
        } catch (Exception e) {
            log.error("Failed to clear cache", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "缓存清理失败: " + e.getMessage());
        }
        return result;
    }
    
    // ==================== 部门 CRUD 端点 ====================
    
    @PostMapping("/departments")
    public Map<String, Object> createDepartment(@RequestBody Map<String, Object> body) {
        log.info("[createDepartment] Creating department: {}", body);
        Map<String, Object> result = new HashMap<>();
        try {
            String parentDeptId = (String) body.getOrDefault("parentDeptId", "0");
            String name = (String) body.get("name");
            String leaderUserId = (String) body.get("leaderUserId");
            
            if (name == null || name.isEmpty()) {
                result.put("code", 400);
                result.put("status", "error");
                result.put("message", "部门名称不能为空");
                return result;
            }
            
            boolean success = apiClient.createDepartment(parentDeptId, name, leaderUserId);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "部门创建成功");
            } else {
                result.put("code", 500);
                result.put("status", "error");
                result.put("message", "部门创建失败，请检查权限或参数");
            }
        } catch (Exception e) {
            log.error("Failed to create department", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "部门创建失败: " + e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/departments/{deptId}")
    public Map<String, Object> updateDepartment(@PathVariable String deptId, @RequestBody Map<String, Object> body) {
        log.info("[updateDepartment] Updating department: {}, body: {}", deptId, body);
        Map<String, Object> result = new HashMap<>();
        try {
            String name = (String) body.get("name");
            String leaderUserId = (String) body.get("leaderUserId");
            
            boolean success = apiClient.updateDepartment(deptId, name, leaderUserId);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "部门更新成功");
            } else {
                result.put("code", 500);
                result.put("status", "error");
                result.put("message", "部门更新失败，请检查参数或权限");
            }
        } catch (Exception e) {
            log.error("Failed to update department: {}", deptId, e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "部门更新失败: " + e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/departments/{deptId}")
    public Map<String, Object> deleteDepartment(@PathVariable String deptId) {
        log.info("[deleteDepartment] Deleting department: {}", deptId);
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = apiClient.deleteDepartment(deptId);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "部门删除成功");
            } else {
                result.put("code", 500);
                result.put("status", "error");
                result.put("message", "部门删除失败，可能存在子部门或成员");
            }
        } catch (Exception e) {
            log.error("Failed to delete department: {}", deptId, e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "部门删除失败: " + e.getMessage());
        }
        return result;
    }
    
    // ==================== 用户 CRUD 端点 ====================
    
    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Map<String, Object> body) {
        log.info("[createUser] Creating user: {}", body);
        Map<String, Object> result = new HashMap<>();
        try {
            String name = (String) body.get("name");
            String mobile = (String) body.get("mobile");
            String email = (String) body.get("email");
            String position = (String) body.get("position");
            
            List<String> deptIds = null;
            if (body.get("departmentIds") instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> rawList = (List<Object>) body.get("departmentIds");
                deptIds = new ArrayList<>();
                for (Object o : rawList) {
                    deptIds.add(o.toString());
                }
            }
            
            if (name == null || name.isEmpty()) {
                result.put("code", 400);
                result.put("status", "error");
                result.put("message", "用户姓名不能为空");
                return result;
            }
            
            boolean success = apiClient.createUser(name, mobile, deptIds, position, email);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "用户创建成功");
            } else {
                result.put("code", 500);
                result.put("status", "error");
                result.put("message", "用户创建失败，请检查手机号/邮箱是否已存在");
            }
        } catch (Exception e) {
            log.error("Failed to create user", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "用户创建失败: " + e.getMessage());
        }
        return result;
    }
    
    @PutMapping("/users/{userId}")
    public Map<String, Object> updateUser(@PathVariable String userId, @RequestBody Map<String, Object> body) {
        log.info("[updateUser] Updating user: {}, body: {}", userId, body);
        Map<String, Object> result = new HashMap<>();
        try {
            String name = (String) body.get("name");
            String mobile = (String) body.get("mobile");
            String email = (String) body.get("email");
            String departmentId = (String) body.get("departmentId");
            int status = body.containsKey("status") ? Integer.parseInt(body.get("status").toString()) : -1;
            
            boolean success = apiClient.updateUser(userId, name, mobile, email, departmentId, status);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "用户更新成功");
            } else {
                result.put("code", 500);
                result.put("status", "error");
                result.put("message", "用户更新失败，请检查参数或权限");
            }
        } catch (Exception e) {
            log.error("Failed to update user: {}", userId, e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "用户更新失败: " + e.getMessage());
        }
        return result;
    }
    
    @DeleteMapping("/users/{userId}")
    public Map<String, Object> deleteUser(@PathVariable String userId) {
        log.info("[deleteUser] Deleting user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = apiClient.deleteUser(userId);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "用户删除成功");
            } else {
                result.put("code", 500);
                result.put("status", "error");
                result.put("message", "用户删除失败，请检查权限");
            }
        } catch (Exception e) {
            log.error("Failed to delete user: {}", userId, e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "用户删除失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/users/{userId}/disable")
    public Map<String, Object> disableUser(@PathVariable String userId) {
        log.info("[disableUser] Disabling user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = apiClient.disableUser(userId);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "用户已禁用");
            } else {
                result.put("code", 500);
                result.put("status", "error");
                result.put("message", "禁用用户失败");
            }
        } catch (Exception e) {
            log.error("Failed to disable user: {}", userId, e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "禁用用户失败: " + e.getMessage());
        }
        return result;
    }
    
    @PostMapping("/users/{userId}/enable")
    public Map<String, Object> enableUser(@PathVariable String userId) {
        log.info("[enableUser] Enabling user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = apiClient.enableUser(userId);
            if (success) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("message", "用户已启用");
            } else {
                result.put("code", 500);
                result.put("status", "error");
                result.put("message", "启用用户失败");
            }
        } catch (Exception e) {
            log.error("Failed to enable user: {}", userId, e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "启用用户失败: " + e.getMessage());
        }
        return result;
    }

    // ==================== 免登接口（OAuth2 OIDC） ====================
    
    @GetMapping("/auth/free-login")
    public Map<String, Object> freeLogin(@RequestParam String access_token) {
        log.info("[freeLogin] Feishu OIDC free login");
        Map<String, Object> result = new HashMap<>();
        try {
            FeishuUser user = apiClient.getFreeLoginUser(access_token);
            if (user != null) {
                result.put("code", 200);
                result.put("status", "success");
                result.put("data", user);
                result.put("message", "免登成功");
            } else {
                result.put("code", 401);
                result.put("status", "error");
                result.put("message", "免登失败，无效的access_token");
            }
        } catch (Exception e) {
            log.error("Failed to free login", e);
            result.put("code", 500);
            result.put("status", "error");
            result.put("message", "免登接口异常: " + e.getMessage());
        }
        return result;
    }
    
    private List<Map<String, Object>> buildOrgTree(List<FeishuDepartment> departments) {
        Map<String, Map<String, Object>> nodeMap = new HashMap<>();
        List<Map<String, Object>> roots = new ArrayList<>();
        
        for (FeishuDepartment dept : departments) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", dept.getDepartmentId());
            node.put("name", dept.getName());
            node.put("parentId", dept.getParentDepartmentId());
            node.put("leaderId", dept.getLeaderId());
            node.put("children", new ArrayList<>());
            nodeMap.put(dept.getDepartmentId(), node);
        }
        
        for (FeishuDepartment dept : departments) {
            Map<String, Object> node = nodeMap.get(dept.getDepartmentId());
            String parentId = dept.getParentDepartmentId();
            if (parentId == null || "0".equals(parentId)) {
                roots.add(node);
            } else {
                Map<String, Object> parent = nodeMap.get(parentId);
                if (parent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> children = (List<Map<String, Object>>) parent.get("children");
                    children.add(node);
                }
            }
        }
        
        return roots;
    }
}
