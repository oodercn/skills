package net.ooder.skill.org.feishu.controller;

import net.ooder.skill.org.feishu.dto.AuthTokenDTO;
import net.ooder.skill.org.feishu.dto.QrCodeDTO;
import net.ooder.skill.org.feishu.dto.SyncResultDTO;
import net.ooder.skill.org.feishu.model.FeishuDepartment;
import net.ooder.skill.org.feishu.model.FeishuUser;
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
