package net.ooder.skill.scene.service;

import net.ooder.skill.org.base.OrgSkill;
import net.ooder.skill.org.base.UserInfo;
import net.ooder.skill.org.base.OrgInfo;
import net.ooder.skill.scene.dto.LoginRequest;
import net.ooder.skill.scene.dto.UserSessionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String SESSION_USER_KEY = "ooder_user";

    @Autowired
    private OrgSkill orgSkill;

    private final Map<String, UserSessionDTO> sessions = new ConcurrentHashMap<>();

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
            "installer", "绯荤粺瀹夎鑰?, "瀹夎鍩虹鎶€鑳藉寘锛屽垵濮嬪寲绯荤粺鐜", "ri-install-line",
            "installer", Arrays.asList("skill:install", "skill:view", "system:init")
        ));

        roleConfigs.put("admin", new RoleConfig(
            "admin", "绯荤粺绠＄悊鍛?, "鍙戠幇鍦烘櫙鎶€鑳斤紝閰嶇疆鍒嗗彂锛屾帹閫佺粰鍙備笌鑰?, "ri-admin-line",
            "admin", Arrays.asList("capability:discover", "capability:install", "capability:distribute",
                "scene:create", "scene:manage", "user:assign", "capability:view", "scene:view")
        ));

        roleConfigs.put("leader", new RoleConfig(
            "leader", "涓诲鑰?, "婵€娲诲満鏅紝鑾峰彇KEY锛屾墽琛屽叆缃戝姩浣?, "ri-user-star-line",
            "leader", Arrays.asList("scene:activate", "scene:manage", "scene:view",
                "key:generate", "participant:manage", "task:assign")
        ));

        roleConfigs.put("collaborator", new RoleConfig(
            "collaborator", "鍗忎綔鑰?, "鍙備笌涓氬姟娴佽浆锛屾墽琛屽垎閰嶇殑浠诲姟", "ri-team-line",
            "collaborator", Arrays.asList("task:view", "task:execute", "task:submit", "scene:view", "todo:view")
        ));

        log.info("AuthService initialized with {} role configs", roleConfigs.size());
    }

    public UserSessionDTO login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.getUsername();
        String password = request.getPassword();
        String requestedRole = request.getRole();

        log.info("[login] Login attempt: username={}, role={}", username, requestedRole);

        String clientIp = getClientIp(httpRequest);
        UserInfo userInfo = orgSkill.login(username, password, clientIp);

        if (userInfo == null) {
            log.warn("[login] Login failed for user: {}", username);
            return null;
        }

        RoleConfig config = roleConfigs.get(requestedRole);
        if (config == null) {
            config = findRoleConfigByOrgRoles(userInfo.getRoles());
        }

        if (config == null) {
            log.warn("[login] No matching role config for user: {}, roles: {}", username, userInfo.getRoles());
            return null;
        }

        UserSessionDTO session = createUserSession(userInfo, config);

        String token = userInfo.getToken();
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

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private RoleConfig findRoleConfigByOrgRoles(List<String> orgRoles) {
        if (orgRoles == null || orgRoles.isEmpty()) {
            return null;
        }
        for (RoleConfig config : roleConfigs.values()) {
            if (orgRoles.contains(config.orgRole)) {
                return config;
            }
        }
        if (orgRoles.contains("admin")) {
            return roleConfigs.get("admin");
        }
        return roleConfigs.get("collaborator");
    }

    private UserSessionDTO createUserSession(UserInfo userInfo, RoleConfig config) {
        UserSessionDTO session = new UserSessionDTO();
        session.setUserId(userInfo.getUserId());
        session.setUsername(userInfo.getUsername());
        session.setName(userInfo.getNickname() != null ? userInfo.getNickname() : userInfo.getUsername());
        session.setRole(config.orgRole);
        session.setRoleType(config.id);
        session.setEmail(userInfo.getEmail());
        session.setDepartmentId(userInfo.getOrgId());
        session.setDepartmentName(userInfo.getOrgName());
        session.setTitle(userInfo.getNickname());
        session.setPermissions(config.permissions);
        session.setLoginTime(System.currentTimeMillis());
        return session;
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserSessionDTO user = (UserSessionDTO) session.getAttribute(SESSION_USER_KEY);
            if (user != null && user.getToken() != null) {
                orgSkill.logout(user.getToken());
                sessions.remove(user.getToken());
                log.info("[logout] Removed session for user: {}", user.getUserId());
            }
            session.invalidate();
            log.info("[logout] Session invalidated");
        }
    }

    public UserSessionDTO getCurrentUser(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            UserSessionDTO user = (UserSessionDTO) httpSession.getAttribute(SESSION_USER_KEY);
            if (user != null) {
                return user;
            }
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            UserSessionDTO userSession = sessions.get(token);
            if (userSession != null && orgSkill.validateToken(token)) {
                return userSession;
            }
        }

        return null;
    }

    public boolean hasPermission(HttpServletRequest request, String permission) {
        UserSessionDTO user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        List<String> permissions = user.getPermissions();
        return permissions != null && permissions.contains(permission);
    }

    public boolean hasAnyPermission(HttpServletRequest request, String... permissions) {
        UserSessionDTO user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        List<String> userPermissions = user.getPermissions();
        if (userPermissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRole(HttpServletRequest request, String roleType) {
        UserSessionDTO user = getCurrentUser(request);
        return user != null && roleType.equals(user.getRoleType());
    }

    public boolean hasAnyRole(HttpServletRequest request, String... roleTypes) {
        UserSessionDTO user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        for (String roleType : roleTypes) {
            if (roleType.equals(user.getRoleType())) {
                return true;
            }
        }
        return false;
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

    public List<UserInfo> getUsersByRole(String roleType) {
        RoleConfig config = roleConfigs.get(roleType);
        if (config != null) {
            List<OrgInfo> orgTree = orgSkill.getOrgTree();
            List<UserInfo> allUsers = new ArrayList<>();
            for (OrgInfo org : orgTree) {
                allUsers.addAll(orgSkill.getOrgUsers(org.getOrgId()));
            }
            List<UserInfo> filtered = new ArrayList<>();
            for (UserInfo user : allUsers) {
                if (user.getRoles() != null && user.getRoles().contains(config.orgRole)) {
                    filtered.add(user);
                }
            }
            return filtered;
        }
        return new ArrayList<>();
    }

    public UserInfo getUserById(String userId) {
        return orgSkill.getUser(userId);
    }

    public List<UserInfo> getAllUsers() {
        List<OrgInfo> orgTree = orgSkill.getOrgTree();
        List<UserInfo> allUsers = new ArrayList<>();
        for (OrgInfo org : orgTree) {
            allUsers.addAll(orgSkill.getOrgUsers(org.getOrgId()));
        }
        return allUsers;
    }

    public UserInfo createUser(String username, String nickname, String email, String orgId, List<String> roles) {
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setOrgId(orgId);
        user.setRoles(roles);
        user.setStatus("active");
        return orgSkill.registerUser(user);
    }

    public boolean bindUserToRole(String userId, String roleType) {
        UserInfo user = orgSkill.getUser(userId);
        if (user == null) {
            return false;
        }
        RoleConfig config = roleConfigs.get(roleType);
        if (config == null) {
            return false;
        }
        List<String> roles = new ArrayList<>(user.getRoles() != null ? user.getRoles() : new ArrayList<>());
        if (!roles.contains(config.orgRole)) {
            roles.add(config.orgRole);
        }
        user.setRoles(roles);
        orgSkill.updateUser(userId, user);
        log.info("Bound user {} to role {}", userId, roleType);
        return true;
    }
}
