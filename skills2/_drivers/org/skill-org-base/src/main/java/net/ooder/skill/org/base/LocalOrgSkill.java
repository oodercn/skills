package net.ooder.skill.org.base;

import net.ooder.skill.common.storage.JsonStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocalOrgSkill implements OrgSkill {

    private static final Logger log = LoggerFactory.getLogger(LocalOrgSkill.class);

    private final JsonStorage userStorage;
    private final JsonStorage orgStorage;
    private final JsonStorage tokenStorage;
    
    private final Map<String, TokenInfo> tokenCache = new ConcurrentHashMap<>();
    private final Map<String, String> accountToUserId = new ConcurrentHashMap<>();
    
    private static final long DEFAULT_TOKEN_EXPIRE = 86400 * 1000L;
    private long tokenExpireMs = DEFAULT_TOKEN_EXPIRE;

    public LocalOrgSkill() {
        this("./data/org");
    }

    public LocalOrgSkill(String dataPath) {
        this.userStorage = new JsonStorage(dataPath + "/users");
        this.orgStorage = new JsonStorage(dataPath + "/orgs");
        this.tokenStorage = new JsonStorage(dataPath + "/tokens");
        initDefaultData();
        loadExistingUsers();
    }

    private void initDefaultData() {
        if (!orgStorage.exists("root")) {
            OrgInfo rootOrg = new OrgInfo();
            rootOrg.setOrgId("root");
            rootOrg.setName("默认组织");
            rootOrg.setTier(1);
            rootOrg.setIndex(1);
            orgStorage.save("root", rootOrg);
            log.info("Created default root organization");
        }
        
        if (!userStorage.exists("admin")) {
            UserInfo admin = new UserInfo();
            admin.setUserId("admin");
            admin.setUsername("admin");
            admin.setNickname("管理员");
            admin.setEmail("admin@ooder.net");
            admin.setStatus("active");
            admin.setOrgId("root");
            admin.setOrgName("默认组织");
            admin.setRoles(Arrays.asList("admin", "user"));
            userStorage.save("admin", admin);
            accountToUserId.put("admin", "admin");
            log.info("Created default admin user");
        }
    }

    private void loadExistingUsers() {
        List<String> keys = userStorage.listKeys();
        for (String userId : keys) {
            UserInfo user = userStorage.load(userId, UserInfo.class);
            if (user != null && user.getUsername() != null) {
                accountToUserId.put(user.getUsername(), userId);
            }
        }
        log.info("Loaded {} existing users", keys.size());
    }

    public void setTokenExpireMs(long tokenExpireMs) {
        this.tokenExpireMs = tokenExpireMs;
    }

    @Override
    public String getSkillId() {
        return "skill-org-local";
    }

    @Override
    public String getSkillName() {
        return "Local Organization Skill";
    }

    @Override
    public String getSkillVersion() {
        return "1.0.0";
    }

    @Override
    public List<String> getCapabilities() {
        return Arrays.asList("user.auth", "user.manage", "org.manage", "role.manage", "sync");
    }

    @Override
    public UserInfo login(String username, String password, String clientIp) {
        log.info("Login attempt for user: {} from {}", username, clientIp);
        
        String userId = accountToUserId.get(username);
        if (userId == null) {
            log.warn("User not found: {}", username);
            return null;
        }
        
        UserInfo user = userStorage.load(userId, UserInfo.class);
        if (user == null) {
            log.warn("User data not found: {}", userId);
            return null;
        }
        
        if (!"active".equals(user.getStatus())) {
            log.warn("User is not active: {}", username);
            return null;
        }
        
        String token = generateToken();
        String refreshToken = generateToken();
        long now = System.currentTimeMillis();
        
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setToken(token);
        tokenInfo.setRefreshToken(refreshToken);
        tokenInfo.setUserId(userId);
        tokenInfo.setCreateTime(now);
        tokenInfo.setExpireTime(now + tokenExpireMs);
        tokenInfo.setClientIp(clientIp);
        
        tokenCache.put(token, tokenInfo);
        tokenStorage.save(token, tokenInfo);
        
        user.setToken(token);
        user.setRefreshToken(refreshToken);
        
        log.info("User logged in successfully: {}", username);
        return user;
    }

    @Override
    public boolean logout(String token) {
        if (token == null) {
            return false;
        }
        
        TokenInfo tokenInfo = tokenCache.remove(token);
        if (tokenInfo != null) {
            tokenStorage.delete(token);
            log.info("User logged out: {}", tokenInfo.getUserId());
            return true;
        }
        return false;
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null) {
            return false;
        }
        
        TokenInfo tokenInfo = tokenCache.get(token);
        if (tokenInfo == null) {
            tokenInfo = tokenStorage.load(token, TokenInfo.class);
            if (tokenInfo != null) {
                tokenCache.put(token, tokenInfo);
            }
        }
        
        if (tokenInfo == null) {
            return false;
        }
        
        if (System.currentTimeMillis() > tokenInfo.getExpireTime()) {
            tokenCache.remove(token);
            tokenStorage.delete(token);
            return false;
        }
        
        return true;
    }

    @Override
    public UserInfo refreshToken(String refreshToken) {
        for (TokenInfo tokenInfo : tokenCache.values()) {
            if (refreshToken.equals(tokenInfo.getRefreshToken())) {
                UserInfo user = getUser(tokenInfo.getUserId());
                if (user != null) {
                    String newToken = generateToken();
                    String newRefreshToken = generateToken();
                    long now = System.currentTimeMillis();
                    
                    tokenCache.remove(tokenInfo.getToken());
                    tokenStorage.delete(tokenInfo.getToken());
                    
                    TokenInfo newTokenInfo = new TokenInfo();
                    newTokenInfo.setToken(newToken);
                    newTokenInfo.setRefreshToken(newRefreshToken);
                    newTokenInfo.setUserId(tokenInfo.getUserId());
                    newTokenInfo.setCreateTime(now);
                    newTokenInfo.setExpireTime(now + tokenExpireMs);
                    newTokenInfo.setClientIp(tokenInfo.getClientIp());
                    
                    tokenCache.put(newToken, newTokenInfo);
                    tokenStorage.save(newToken, newTokenInfo);
                    
                    user.setToken(newToken);
                    user.setRefreshToken(newRefreshToken);
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public UserInfo getUser(String userId) {
        if (userId == null) {
            return null;
        }
        return userStorage.load(userId, UserInfo.class);
    }

    @Override
    public UserInfo getUserByAccount(String account) {
        String userId = accountToUserId.get(account);
        if (userId == null) {
            return null;
        }
        return getUser(userId);
    }

    @Override
    public UserInfo registerUser(UserInfo userInfo) {
        if (userInfo == null || userInfo.getUsername() == null) {
            return null;
        }
        
        if (accountToUserId.containsKey(userInfo.getUsername())) {
            log.warn("Username already exists: {}", userInfo.getUsername());
            return null;
        }
        
        String userId = userInfo.getUserId();
        if (userId == null || userId.isEmpty()) {
            userId = "user-" + UUID.randomUUID().toString().substring(0, 8);
            userInfo.setUserId(userId);
        }
        
        if (userInfo.getStatus() == null) {
            userInfo.setStatus("active");
        }
        
        userStorage.save(userId, userInfo);
        accountToUserId.put(userInfo.getUsername(), userId);
        
        log.info("Registered new user: {} -> {}", userInfo.getUsername(), userId);
        return userInfo;
    }

    @Override
    public UserInfo updateUser(String userId, UserInfo userInfo) {
        if (userId == null || userInfo == null) {
            return null;
        }
        
        UserInfo existing = getUser(userId);
        if (existing == null) {
            log.warn("User not found for update: {}", userId);
            return null;
        }
        
        if (userInfo.getUsername() != null && !userInfo.getUsername().equals(existing.getUsername())) {
            accountToUserId.remove(existing.getUsername());
            accountToUserId.put(userInfo.getUsername(), userId);
        }
        
        if (userInfo.getUserId() == null) {
            userInfo.setUserId(userId);
        }
        
        userStorage.save(userId, userInfo);
        log.info("Updated user: {}", userId);
        return userInfo;
    }

    @Override
    public boolean deleteUser(String userId) {
        if (userId == null) {
            return false;
        }
        
        UserInfo user = getUser(userId);
        if (user == null) {
            return false;
        }
        
        if ("admin".equals(userId)) {
            log.warn("Cannot delete admin user");
            return false;
        }
        
        userStorage.delete(userId);
        if (user.getUsername() != null) {
            accountToUserId.remove(user.getUsername());
        }
        
        log.info("Deleted user: {}", userId);
        return true;
    }

    @Override
    public PageResult<UserInfo> listUsers(PageRequest request) {
        List<String> keys = userStorage.listKeys();
        List<UserInfo> allUsers = new ArrayList<>();
        
        for (String userId : keys) {
            UserInfo user = userStorage.load(userId, UserInfo.class);
            if (user != null) {
                allUsers.add(user);
            }
        }
        
        int total = allUsers.size();
        int offset = request.getOffset();
        int end = Math.min(offset + request.getPageSize(), total);
        
        List<UserInfo> pagedUsers = new ArrayList<>();
        if (offset < total) {
            pagedUsers = allUsers.subList(offset, end);
        }
        
        return new PageResult<>(pagedUsers, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public List<String> getUserRoles(String userId) {
        UserInfo user = getUser(userId);
        if (user != null && user.getRoles() != null) {
            return user.getRoles();
        }
        return new ArrayList<>();
    }

    @Override
    public List<OrgInfo> getOrgTree() {
        List<String> keys = orgStorage.listKeys();
        Map<String, OrgInfo> orgMap = new HashMap<>();
        
        for (String orgId : keys) {
            OrgInfo org = orgStorage.load(orgId, OrgInfo.class);
            if (org != null) {
                orgMap.put(orgId, org);
            }
        }
        
        List<OrgInfo> roots = new ArrayList<>();
        for (OrgInfo org : orgMap.values()) {
            if (org.getParentId() == null || org.getParentId().isEmpty()) {
                roots.add(org);
            } else {
                OrgInfo parent = orgMap.get(org.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(org);
                }
            }
        }
        
        return roots;
    }

    @Override
    public OrgInfo getOrg(String orgId) {
        if (orgId == null) {
            return null;
        }
        return orgStorage.load(orgId, OrgInfo.class);
    }

    @Override
    public List<UserInfo> getOrgUsers(String orgId) {
        if (orgId == null) {
            return new ArrayList<>();
        }
        
        List<String> keys = userStorage.listKeys();
        List<UserInfo> orgUsers = new ArrayList<>();
        
        for (String userId : keys) {
            UserInfo user = userStorage.load(userId, UserInfo.class);
            if (user != null && orgId.equals(user.getOrgId())) {
                orgUsers.add(user);
            }
        }
        
        return orgUsers;
    }

    @Override
    public Map<String, Object> syncOrganization(Map<String, Object> orgData) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "Local org skill does not support sync from external source");
        return result;
    }

    @Override
    public OrgInfo createDepartment(Map<String, Object> params) {
        String name = (String) params.get("name");
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        String orgId = "org-" + UUID.randomUUID().toString().substring(0, 8);
        String parentId = (String) params.get("parentId");
        String leaderId = (String) params.get("leaderId");
        
        OrgInfo org = new OrgInfo();
        org.setOrgId(orgId);
        org.setName(name);
        org.setParentId(parentId);
        org.setLeaderId(leaderId);
        org.setMemberCount(0);
        
        if (parentId != null && !parentId.isEmpty()) {
            OrgInfo parent = getOrg(parentId);
            if (parent != null) {
                org.setTier(parent.getTier() + 1);
            } else {
                org.setTier(1);
            }
        } else {
            org.setTier(1);
        }
        
        orgStorage.save(orgId, org);
        log.info("Created department: {} -> {}", name, orgId);
        return org;
    }

    @Override
    public OrgInfo updateDepartment(String orgId, Map<String, Object> params) {
        OrgInfo org = getOrg(orgId);
        if (org == null) {
            return null;
        }
        
        if (params.containsKey("name")) {
            org.setName((String) params.get("name"));
        }
        if (params.containsKey("leaderId")) {
            org.setLeaderId((String) params.get("leaderId"));
        }
        if (params.containsKey("brief")) {
            org.setBrief((String) params.get("brief"));
        }
        
        orgStorage.save(orgId, org);
        log.info("Updated department: {}", orgId);
        return org;
    }

    @Override
    public boolean deleteDepartment(String orgId) {
        if (orgId == null || "root".equals(orgId)) {
            return false;
        }
        
        List<OrgInfo> tree = getOrgTree();
        if (hasChildren(tree, orgId)) {
            log.warn("Cannot delete department with children: {}", orgId);
            return false;
        }
        
        List<UserInfo> users = getOrgUsers(orgId);
        if (!users.isEmpty()) {
            log.warn("Cannot delete department with users: {}", orgId);
            return false;
        }
        
        orgStorage.delete(orgId);
        log.info("Deleted department: {}", orgId);
        return true;
    }

    private boolean hasChildren(List<OrgInfo> orgs, String orgId) {
        for (OrgInfo org : orgs) {
            if (orgId.equals(org.getParentId())) {
                return true;
            }
            if (org.getChildren() != null && hasChildren(org.getChildren(), orgId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> createUser(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        UserInfo user = new UserInfo();
        user.setUsername((String) params.get("username"));
        user.setNickname((String) params.get("nickname"));
        user.setEmail((String) params.get("email"));
        user.setPhone((String) params.get("phone"));
        user.setOrgId((String) params.get("orgId"));
        
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            result.put("success", false);
            result.put("message", "Username is required");
            return result;
        }
        
        UserInfo created = registerUser(user);
        if (created != null) {
            result.put("success", true);
            result.put("userId", created.getUserId());
        } else {
            result.put("success", false);
            result.put("message", "Failed to create user");
        }
        
        return result;
    }

    @Override
    public Map<String, Object> batchCreateUsers(List<Map<String, Object>> users) {
        Map<String, Object> result = new HashMap<>();
        int success = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        
        for (Map<String, Object> userParams : users) {
            try {
                Map<String, Object> createResult = createUser(userParams);
                if (Boolean.TRUE.equals(createResult.get("success"))) {
                    success++;
                } else {
                    failed++;
                    errors.add((String) createResult.get("message"));
                }
            } catch (Exception e) {
                failed++;
                errors.add(e.getMessage());
            }
        }
        
        result.put("success", success);
        result.put("failed", failed);
        result.put("errors", errors);
        return result;
    }

    @Override
    public Map<String, Object> syncUsers(Map<String, Object> userData) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "Local org skill does not support sync from external source");
        return result;
    }

    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        switch (capability) {
            case "user.auth":
                return login(
                    (String) params.get("username"),
                    (String) params.get("password"),
                    (String) params.get("clientIp")
                );
            case "user.manage":
                return handleUserManage(params);
            case "org.manage":
                return handleOrgManage(params);
            case "role.manage":
                return handleRoleManage(params);
            default:
                throw new UnsupportedOperationException("Unknown capability: " + capability);
        }
    }

    private Object handleUserManage(Map<String, Object> params) {
        String action = (String) params.get("action");
        if (action == null) {
            return null;
        }
        
        switch (action) {
            case "get":
                return getUser((String) params.get("userId"));
            case "list":
                return listUsers(new PageRequest(
                    (Integer) params.getOrDefault("pageNum", 1),
                    (Integer) params.getOrDefault("pageSize", 20)
                ));
            case "create":
                return createUser(params);
            case "update":
                return updateUser((String) params.get("userId"), 
                    (UserInfo) params.get("userInfo"));
            case "delete":
                return deleteUser((String) params.get("userId"));
            default:
                return null;
        }
    }

    private Object handleOrgManage(Map<String, Object> params) {
        String action = (String) params.get("action");
        if (action == null) {
            return null;
        }
        
        switch (action) {
            case "tree":
                return getOrgTree();
            case "get":
                return getOrg((String) params.get("orgId"));
            case "users":
                return getOrgUsers((String) params.get("orgId"));
            case "create":
                return createDepartment(params);
            case "update":
                return updateDepartment((String) params.get("orgId"), params);
            case "delete":
                return deleteDepartment((String) params.get("orgId"));
            default:
                return null;
        }
    }

    private Object handleRoleManage(Map<String, Object> params) {
        return null;
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static class TokenInfo {
        private String token;
        private String refreshToken;
        private String userId;
        private long createTime;
        private long expireTime;
        private String clientIp;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public String getClientIp() {
            return clientIp;
        }

        public void setClientIp(String clientIp) {
            this.clientIp = clientIp;
        }
    }
}
