package net.ooder.skill.org.feishu.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.org.feishu.config.FeishuConfig;
import net.ooder.skill.org.feishu.model.FeishuDepartment;
import net.ooder.skill.org.feishu.model.FeishuUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class FeishuApiClient {

    private static final Logger logger = LoggerFactory.getLogger(FeishuApiClient.class);

    @Autowired
    private FeishuConfig config;

    private RestTemplate restTemplate = new RestTemplate();
    private String accessToken;
    private long tokenExpireTime;

    public List<FeishuDepartment> getDepartments(String parentDepartmentId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/departments/" 
                + (parentDepartmentId != null ? parentDepartmentId : "0") + "/children";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            List<FeishuDepartment> departments = new ArrayList<>();
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                JSONObject data = result.getJSONObject("data");
                if (data.containsKey("items")) {
                    JSONArray items = data.getJSONArray("items");
                    for (int i = 0; i < items.size(); i++) {
                        departments.add(parseDepartment(items.getJSONObject(i)));
                    }
                }
            }
            return departments;
        } catch (Exception e) {
            logger.error("Failed to get departments", e);
            return new ArrayList<>();
        }
    }

    public FeishuDepartment getDepartment(String departmentId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/departments/" + departmentId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                JSONObject data = result.getJSONObject("data");
                if (data.containsKey("department")) {
                    return parseDepartment(data.getJSONObject("department"));
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get department: {}", departmentId, e);
            return null;
        }
    }

    public List<FeishuUser> getAllUsers() {
        ensureToken();
        List<FeishuUser> allUsers = new ArrayList<>();
        try {
            List<FeishuDepartment> topDepts = getDepartments(null);
            for (FeishuDepartment dept : topDepts) {
                allUsers.addAll(getUsersByDepartment(dept.getOpenDepartmentId()));
            }
        } catch (Exception e) {
            logger.error("Failed to get all users", e);
        }
        return allUsers;
    }

    public List<FeishuUser> getUsersByDepartment(String departmentId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/users/find_by_department?department_id=" + departmentId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            List<FeishuUser> users = new ArrayList<>();
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                JSONObject data = result.getJSONObject("data");
                if (data.containsKey("items")) {
                    JSONArray items = data.getJSONArray("items");
                    for (int i = 0; i < items.size(); i++) {
                        users.add(parseUser(items.getJSONObject(i)));
                    }
                }
            }
            return users;
        } catch (Exception e) {
            logger.error("Failed to get users by department: {}", departmentId, e);
            return new ArrayList<>();
        }
    }

    public FeishuUser getUser(String userId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/users/" + userId;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                JSONObject data = result.getJSONObject("data");
                if (data.containsKey("user")) {
                    return parseUser(data.getJSONObject("user"));
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get user: {}", userId, e);
            return null;
        }
    }

    public FeishuUser getUserByAccount(String account) {
        return getUser(account);
    }

    public FeishuUser getUserByMobile(String mobile) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/users/find_by_phone?mobile=" + mobile;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                JSONObject data = result.getJSONObject("data");
                if (data.containsKey("user")) {
                    return parseUser(data.getJSONObject("user"));
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get user by mobile: {}", mobile, e);
            return null;
        }
    }

    public FeishuUser getUserByEmail(String email) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/users/find_by_email?email=" + email;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                JSONObject data = result.getJSONObject("data");
                if (data.containsKey("user")) {
                    return parseUser(data.getJSONObject("user"));
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get user by email: {}", email, e);
            return null;
        }
    }

    public Map<String, Object> getUserAccessToken(String code) {
        try {
            String url = config.getApiBaseUrl() + "/authen/v1/oidc/access_token";

            JSONObject body = new JSONObject();
            body.put("grant_type", "authorization_code");
            body.put("code", code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("code") == 0) {
                Map<String, Object> tokenInfo = new java.util.HashMap<>();
                tokenInfo.put("access_token", result.getJSONObject("data").getString("access_token"));
                tokenInfo.put("expires_in", result.getJSONObject("data").getInteger("expires_in"));
                tokenInfo.put("errcode", 0);
                tokenInfo.put("errmsg", "ok");
                return tokenInfo;
            }
            logger.warn("getUserAccessToken failed: code={}, msg={}", result.getIntValue("code"), result.getString("msg"));
        } catch (Exception e) {
            logger.error("Failed to get user access token", e);
        }
        return null;
    }

    public FeishuUser getUserInfo(String userAccessToken) {
        try {
            String url = config.getApiBaseUrl() + "/authen/v1/oidc/user_info";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userAccessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("code") == 0 && result.containsKey("data")) {
                JSONObject data = result.getJSONObject("data");
                return getUser(data.getString("open_id"));
            }
            logger.warn("getUserInfo failed: {}", result);
        } catch (Exception e) {
            logger.error("Failed to get user info", e);
        }
        return null;
    }

    public boolean verifyUser(String account, String password) {
        try {
            FeishuUser user = getUser(account);
            return user != null && user.getStatus() == 1;
        } catch (Exception e) {
            logger.error("Failed to verify user: {}", account, e);
            return false;
        }
    }

    public FeishuUser getFreeLoginUser(String accessTokenStr) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/authen/v1/oidc/user_info";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessTokenStr);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                JSONObject data = result.getJSONObject("data");
                String openId = data.getString("open_id");
                if (openId != null) {
                    return getUser(openId);
                }
            }
            logger.warn("getFreeLoginUser failed: {}", result);
        } catch (Exception e) {
            logger.error("Failed to get free login user", e);
        }
        return null;
    }

    // ==================== 部门 CRUD ====================

    public boolean createDepartment(String parentDeptId, String name, String leaderUserId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/departments";

            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("parent_department_id", parentDeptId != null ? parentDeptId : "0");
            if (leaderUserId != null && !leaderUserId.isEmpty()) {
                body.put("leader_user_id", leaderUserId);
            }

            HttpHeaders headers = buildAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                logger.info("Created department: {}", result.getJSONObject("data").getString("department"));
                return true;
            }
            logger.warn("createDepartment failed: {}", result);
        } catch (Exception e) {
            logger.error("Failed to create department", e);
        }
        return false;
    }

    public boolean updateDepartment(String departmentId, String name, String leaderUserId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/departments/" + departmentId;

            JSONObject body = new JSONObject();
            if (name != null) body.put("name", name);
            if (leaderUserId != null) body.put("leader_user_id", leaderUserId);

            HttpHeaders headers = buildAuthHeaders();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("code") == 0 || result.containsKey("data")) {
                logger.info("Updated department: {}", departmentId);
                return true;
            }
            logger.warn("updateDepartment failed: {}", result);
        } catch (Exception e) {
            logger.error("Failed to update department: {}", departmentId, e);
        }
        return false;
    }

    public boolean deleteDepartment(String departmentId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/departments/" + departmentId;

            HttpHeaders headers = buildAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("code") == 0) {
                logger.info("Deleted department: {}", departmentId);
                return true;
            }
            logger.warn("deleteDepartment failed: {}", result);
        } catch (Exception e) {
            logger.error("Failed to delete department: {}", departmentId, e);
        }
        return false;
    }

    // ==================== 用户 CRUD ====================

    public boolean createUser(String name, String mobile, List<String> departmentIds,
                               String position, String email) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/users";

            JSONObject body = new JSONObject();
            body.put("name", name);
            if (mobile != null && !mobile.isEmpty()) body.put("mobiles", java.util.Arrays.asList(mobile));
            if (departmentIds != null && !departmentIds.isEmpty()) body.put("department_ids", departmentIds);
            if (position != null) body.put("position", position);
            if (email != null && !email.isEmpty()) body.put("emails", java.util.Arrays.asList(email));

            HttpHeaders headers = buildAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("data")) {
                logger.info("Created user: {}", result.getJSONObject("data").getString("user"));
                return true;
            }
            logger.warn("createUser failed: {}", result);
        } catch (Exception e) {
            logger.error("Failed to create user", e);
        }
        return false;
    }

    public boolean updateUser(String userId, String name, String mobile, String email,
                               String departmentId, int status) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/users/" + userId;

            JSONObject body = new JSONObject();
            if (name != null) body.put("name", name);
            if (mobile != null) body.put("mobiles", java.util.Arrays.asList(mobile));
            if (email != null) body.put("emails", java.util.Arrays.asList(email));
            if (departmentId != null) body.put("department_ids", java.util.Arrays.asList(departmentId));
            if (status >= 0) body.put("status", status);

            HttpHeaders headers = buildAuthHeaders();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("code") == 0 || result.containsKey("data")) {
                logger.info("Updated user: {}", userId);
                return true;
            }
            logger.warn("updateUser failed: {}", result);
        } catch (Exception e) {
            logger.error("Failed to update user: {}", userId, e);
        }
        return false;
    }

    public boolean deleteUser(String userId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/contact/v3/users/" + userId;

            HttpHeaders headers = buildAuthHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);

            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("code") == 0) {
                logger.info("Deleted user: {}", userId);
                return true;
            }
            logger.warn("deleteUser failed: {}", result);
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", userId, e);
        }
        return false;
    }

    public boolean disableUser(String userId) {
        return updateUser(userId, null, null, null, null, 0);
    }

    public boolean enableUser(String userId) {
        return updateUser(userId, null, null, null, null, 1);
    }

    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private synchronized void ensureToken() {
        if (accessToken == null || System.currentTimeMillis() > tokenExpireTime) {
            refreshToken();
        }
    }

    private void refreshToken() {
        try {
            String url = config.getApiBaseUrl() + "/auth/v3/tenant_access_token/internal";
            
            JSONObject body = new JSONObject();
            body.put("app_id", config.getAppId());
            body.put("app_secret", config.getAppSecret());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.containsKey("tenant_access_token")) {
                accessToken = result.getString("tenant_access_token");
                int expire = result.getIntValue("expire");
                tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;
                logger.info("Feishu access token refreshed");
            }
        } catch (Exception e) {
            logger.error("Failed to refresh token", e);
        }
    }

    private FeishuDepartment parseDepartment(JSONObject json) {
        FeishuDepartment dept = new FeishuDepartment();
        dept.setOpenDepartmentId(json.getString("open_department_id"));
        dept.setName(json.getString("name"));
        dept.setParentDepartmentId(json.getString("parent_department_id"));
        dept.setDepartmentId(json.getString("department_id"));
        dept.setMemberCount(json.getIntValue("member_count"));
        return dept;
    }

    private FeishuUser parseUser(JSONObject json) {
        FeishuUser user = new FeishuUser();
        user.setOpenId(json.getString("open_id"));
        user.setUserId(json.getString("user_id"));
        user.setName(json.getString("name"));
        user.setNickname(json.getString("nickname"));
        user.setMobile(json.getString("mobile"));
        user.setEmail(json.getString("email"));
        user.setStatus(json.getIntValue("status"));
        
        JSONArray deptIds = json.getJSONArray("department_ids");
        if (deptIds != null && !deptIds.isEmpty()) {
            List<String> idList = new ArrayList<>();
            for (int i = 0; i < deptIds.size(); i++) {
                idList.add(deptIds.getString(i));
            }
            user.setDepartmentIds(idList);
        }
        return user;
    }
}
