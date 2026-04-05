package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.dto.LoginRequest;
import net.ooder.skill.scene.dto.menu.MenuItemDTO;
import net.ooder.skill.scene.dto.UserSessionDTO;
import net.ooder.skill.scene.model.ResultModel;
import net.ooder.skill.scene.service.AuthService;
import net.ooder.skill.scene.service.MenuRoleConfigService;
import net.ooder.skill.scene.service.RoleManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleManagementService roleManagementService;

    @Autowired
    private MenuRoleConfigService menuRoleConfigService;

    @PostMapping("/login")
    public ResultModel<UserSessionDTO> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        log.info("[login] Login request: username={}, role={}", request.getUsername(), request.getRole());
        
        UserSessionDTO session = authService.login(request, httpRequest);
        if (session != null) {
            return ResultModel.success(session);
        }
        return ResultModel.error(401, "鐢ㄦ埛鍚嶆垨瀵嗙爜閿欒");
    }

    @PostMapping("/logout")
    public ResultModel<Void> logout(HttpServletRequest request) {
        log.info("[logout] Logout request");
        authService.logout(request);
        return ResultModel.success(null);
    }

    @GetMapping("/session")
    public ResultModel<UserSessionDTO> getSession(HttpServletRequest request) {
        UserSessionDTO user = authService.getCurrentUser(request);
        if (user != null) {
            return ResultModel.success(user);
        }
        return ResultModel.error(401, "鏈櫥褰曟垨浼氳瘽宸茶繃鏈?);
    }

    @GetMapping("/current-user")
    public ResultModel<UserSessionDTO> getCurrentUser(HttpServletRequest request) {
        return getSession(request);
    }

    @GetMapping("/roles")
    public ResultModel<List<Map<String, Object>>> getAvailableRoles() {
        List<Map<String, Object>> roles = authService.getAvailableRoles();
        return ResultModel.success(roles);
    }

    @GetMapping("/check-permission")
    public ResultModel<Map<String, Object>> checkPermission(
            @RequestParam String permission,
            HttpServletRequest request) {
        
        UserSessionDTO user = authService.getCurrentUser(request);
        Map<String, Object> result = new HashMap<>();
        result.put("hasPermission", authService.hasPermission(request, permission));
        result.put("user", user);
        
        return ResultModel.success(result);
    }

    @GetMapping("/check-role")
    public ResultModel<Map<String, Object>> checkRole(
            @RequestParam String role,
            HttpServletRequest request) {
        
        UserSessionDTO user = authService.getCurrentUser(request);
        Map<String, Object> result = new HashMap<>();
        result.put("hasRole", authService.hasRole(request, role));
        result.put("user", user);
        
        return ResultModel.success(result);
    }

    @GetMapping("/menu-config")
    public ResultModel<List<Map<String, Object>>> getMenuConfig(HttpServletRequest request) {
        UserSessionDTO user = authService.getCurrentUser(request);
        
        if (user == null) {
            return ResultModel.error(401, "鏈櫥褰?);
        }

        String roleType = user.getRoleType();
        List<MenuItemDTO> menus = menuRoleConfigService.getMenusByRole(roleType);
        
        List<Map<String, Object>> menuItems = new ArrayList<>();
        for (MenuItemDTO menu : menus) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", menu.getId());
            item.put("name", menu.getName());
            item.put("url", menu.getUrl());
            item.put("icon", menu.getIcon());
            item.put("active", menu.isActive());
            item.put("sort", menu.getSort());
            menuItems.add(item);
        }
        
        return ResultModel.success(menuItems);
    }
}
