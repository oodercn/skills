package net.ooder.skill.common.service;

import net.ooder.skill.common.model.LoginRequest;
import net.ooder.skill.common.model.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String SESSION_USER_KEY = "ooder_user";

    private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, RoleConfig> roleConfigs = new LinkedHashMap<>();

    private static class RoleConfig {
        String id;
        String name;
        String description;
        String icon;
        String orgRole;
        List<String> permissions;

        RoleConfig(String id, String name, String description, String icon, String orgRole, List<String> permissions) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.icon = icon;
            this.orgRole = orgRole;
            this.permissions = permissions;
        }
    }

    public AuthService() {
        initRoleConfigs();
    }

    private void initRoleConfigs() {
        roleConfigs.put("installer", new RoleConfig(
            "installer", "系统安装者", "安装基础技能包，初始化系统环境", "ri-install-line",
            "installer", Arrays.asList("skill:install", "skill:view", "system:init")
        ));

        roleConfigs.put("admin", new RoleConfig(
            "admin", "系统管理员", "发现场景技能，配置分发，推送给参与者", "ri-admin-line",
            "admin", Arrays.asList("capability:discover", "capability:install", "capability:distribute",
                "scene:create", "scene:manage", "user:assign", "capability:view", "scene:view")
        ));

        roleConfigs.put("leader", new RoleConfig(
            "leader", "主导者", "激活场景，获取KEY，执行入网动作", "ri-user-star-line",
            "leader", Arrays.asList("scene:activate", "scene:manage", "scene:view",
                "key:generate", "participant:manage", "task:assign")
        ));

        roleConfigs.put("collaborator", new RoleConfig(
            "collaborator", "协作者", "参与业务流转，执行分配的任务", "ri-team-line",
            "collaborator", Arrays.asList("task:view", "task:execute", "task:submit", "scene:view", "todo:view")
        ));

        log.info("AuthService initialized with {} role configs", roleConfigs.size());
    }

    public UserSession login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.getUsername();
        String password = request.getPassword();
        String requestedRole = request.getRole();

        log.info("[login] Login attempt: username={}, role={}", username, requestedRole);

        if (username == null || password == null) {
            log.warn("[login] Login failed: missing credentials");
            return null;
        }

        if (!"admin".equals(username) && !"user".equals(username)) {
            log.warn("[login] Login failed for user: {}", username);
            return null;
        }

        RoleConfig config = roleConfigs.get(requestedRole);
        if (config == null) {
            config = roleConfigs.get("collaborator");
        }

        UserSession session = createUserSession(username, config);
        String token = UUID.randomUUID().toString().replace("-", "");
        session.setToken(token);
        sessions.put(token, session);

        if (httpRequest != null) {
            HttpSession httpSession = httpRequest.getSession(true);
            httpSession.setAttribute(SESSION_USER_KEY, session);
            log.info("[login] Session created for user: {}", session.getUserId());
        }

        log.info("[login] Login successful: userId={}, role={}", session.getUserId(), config.id);
        return session;
    }

    private UserSession createUserSession(String username, RoleConfig config) {
        UserSession session = new UserSession();
        session.setUserId("user-" + username);
        session.setUsername(username);
        session.setName(username.equals("admin") ? "管理员" : "用户");
        session.setRole(config.orgRole);
        session.setRoleType(config.id);
        session.setEmail(username + "@ooder.local");
        session.setDepartmentId("root");
        session.setDepartmentName("根组织");
        session.setPermissions(config.permissions);
        session.setLoginTime(System.currentTimeMillis());
        return session;
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserSession user = (UserSession) session.getAttribute(SESSION_USER_KEY);
            if (user != null && user.getToken() != null) {
                sessions.remove(user.getToken());
                log.info("[logout] Removed session for user: {}", user.getUserId());
            }
            session.invalidate();
            log.info("[logout] Session invalidated");
        }
    }

    public UserSession getCurrentUser(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            UserSession user = (UserSession) httpSession.getAttribute(SESSION_USER_KEY);
            if (user != null) {
                return user;
            }
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return sessions.get(token);
        }

        return null;
    }

    public boolean hasPermission(HttpServletRequest request, String permission) {
        UserSession user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        List<String> permissions = user.getPermissions();
        return permissions != null && permissions.contains(permission);
    }

    public boolean hasRole(HttpServletRequest request, String roleType) {
        UserSession user = getCurrentUser(request);
        return user != null && roleType.equals(user.getRoleType());
    }

    public List<Map<String, Object>> getAvailableRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();

        for (RoleConfig config : roleConfigs.values()) {
            Map<String, Object> role = new HashMap<>();
            role.put("id", config.id);
            role.put("name", config.name);
            role.put("description", config.description);
            role.put("icon", config.icon);
            roles.add(role);
        }

        return roles;
    }
}
