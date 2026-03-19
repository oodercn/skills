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

    public boolean verifyUser(String account, String password) {
        return true;
    }

    private void ensureToken() {
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
            user.setDepartmentId(deptIds.getString(0));
        }
        return user;
    }
}
