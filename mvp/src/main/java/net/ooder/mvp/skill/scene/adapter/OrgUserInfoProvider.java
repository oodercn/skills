package net.ooder.mvp.skill.scene.adapter;

import net.ooder.mvp.skill.scene.dto.OrgUserDTO;
import net.ooder.skill.common.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrgUserInfoProvider implements AuthService.UserInfoProvider {

    private static final Logger log = LoggerFactory.getLogger(OrgUserInfoProvider.class);

    @Autowired
    private OrgWebAdapter orgWebAdapter;

    private final Map<String, Map<String, Object>> userCache = new ConcurrentHashMap<>();
    private final Map<String, String> tokenToUserMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("[OrgUserInfoProvider] Initialized with OrgWebAdapter");
    }

    @Override
    public Object login(String username, String password, String role) {
        log.info("[OrgUserInfoProvider] Login attempt: username={}, role={}", username, role);
        
        List<OrgUserDTO> users = orgWebAdapter.getUsersByRole(role);
        OrgUserDTO matchedUser = null;
        
        if (users != null && !users.isEmpty()) {
            matchedUser = users.get(0);
        }
        
        if (matchedUser == null) {
            matchedUser = orgWebAdapter.getAllUsers().stream()
                .filter(u -> role.equals(u.getRole()))
                .findFirst()
                .orElse(null);
        }
        
        if (matchedUser == null) {
            matchedUser = new OrgUserDTO();
            matchedUser.setUserId("user-" + role + "-001");
            matchedUser.setName(getRoleDisplayName(role));
            matchedUser.setEmail(role + "@ooder.local");
            matchedUser.setRole(role);
            matchedUser.setActive(true);
        }
        
        String token = UUID.randomUUID().toString();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", matchedUser.getUserId());
        userInfo.put("username", username);
        userInfo.put("name", matchedUser.getName());
        userInfo.put("email", matchedUser.getEmail());
        userInfo.put("role", role);
        userInfo.put("roleType", role);
        userInfo.put("departmentId", matchedUser.getDepartmentId());
        userInfo.put("departmentName", matchedUser.getDepartmentName());
        userInfo.put("title", matchedUser.getTitle());
        userInfo.put("token", token);
        userInfo.put("loginTime", System.currentTimeMillis());
        
        userCache.put(matchedUser.getUserId(), userInfo);
        tokenToUserMap.put(token, matchedUser.getUserId());
        
        log.info("[OrgUserInfoProvider] Login successful: userId={}, name={}", 
            matchedUser.getUserId(), matchedUser.getName());
        
        return userInfo;
    }

    @Override
    public void logout(String userId) {
        log.info("[OrgUserInfoProvider] Logout: userId={}", userId);
        Map<String, Object> userInfo = userCache.remove(userId);
        if (userInfo != null) {
            String token = (String) userInfo.get("token");
            if (token != null) {
                tokenToUserMap.remove(token);
            }
        }
    }

    @Override
    public boolean validateToken(String token) {
        String userId = tokenToUserMap.get(token);
        return userId != null && userCache.containsKey(userId);
    }

    @Override
    public Object getUser(String userId) {
        return userCache.get(userId);
    }

    @Override
    public List<?> getOrgTree() {
        List<Map<String, Object>> orgTree = new ArrayList<>();
        
        Map<String, Object> rootOrg = new HashMap<>();
        rootOrg.put("id", "org-root");
        rootOrg.put("name", "组织架构");
        rootOrg.put("type", "root");
        rootOrg.put("children", new ArrayList<>());
        orgTree.add(rootOrg);
        
        return orgTree;
    }

    @Override
    public List<?> getOrgUsers(String orgId) {
        List<Map<String, Object>> users = new ArrayList<>();
        
        for (Map<String, Object> userInfo : userCache.values()) {
            String deptId = (String) userInfo.get("departmentId");
            if (orgId.equals(deptId) || "org-root".equals(orgId)) {
                users.add(userInfo);
            }
        }
        
        if (users.isEmpty() && "org-root".equals(orgId)) {
            for (OrgUserDTO dto : orgWebAdapter.getAllUsers()) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("userId", dto.getUserId());
                userInfo.put("name", dto.getName());
                userInfo.put("email", dto.getEmail());
                userInfo.put("role", dto.getRole());
                userInfo.put("departmentId", dto.getDepartmentId());
                userInfo.put("departmentName", dto.getDepartmentName());
                users.add(userInfo);
            }
        }
        
        return users;
    }

    @Override
    public Object registerUser(Object userInfo) {
        log.info("[OrgUserInfoProvider] Register user: {}", userInfo);
        return userInfo;
    }

    @Override
    public void updateUser(String userId, Object userInfo) {
        log.info("[OrgUserInfoProvider] Update user: userId={}", userId);
    }
    
    private String getRoleDisplayName(String role) {
        Map<String, String> roleNames = new HashMap<>();
        roleNames.put("admin", "系统管理员");
        roleNames.put("installer", "系统安装者");
        roleNames.put("leader", "主导者");
        roleNames.put("collaborator", "协作者");
        roleNames.put("manager", "管理者");
        roleNames.put("employee", "员工");
        roleNames.put("hr", "HR");
        roleNames.put("developer", "开发者");
        roleNames.put("user", "普通用户");
        
        return roleNames.getOrDefault(role, role);
    }
}
