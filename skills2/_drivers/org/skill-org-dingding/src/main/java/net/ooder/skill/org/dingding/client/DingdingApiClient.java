package net.ooder.skill.org.dingding.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.org.dingding.config.DingdingConfig;
import net.ooder.skill.org.dingding.model.DingdingDepartment;
import net.ooder.skill.org.dingding.model.DingdingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class DingdingApiClient {

    private static final Logger logger = LoggerFactory.getLogger(DingdingApiClient.class);

    @Autowired
    private DingdingConfig config;

    private RestTemplate restTemplate = new RestTemplate();
    private String accessToken;
    private long tokenExpireTime;

    public List<DingdingDepartment> getDepartments(String parentDepartmentId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/department/listsub?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("dept_id", parentDepartmentId != null ? Long.parseLong(parentDepartmentId) : 1L);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            List<DingdingDepartment> departments = new ArrayList<>();
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                JSONArray items = result.getJSONArray("result");
                if (items != null) {
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

    public DingdingDepartment getDepartment(String departmentId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/department/get?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("dept_id", Long.parseLong(departmentId));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                return parseDepartment(result.getJSONObject("result"));
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get department: {}", departmentId, e);
            return null;
        }
    }

    public List<DingdingUser> getAllUsers() {
        ensureToken();
        List<DingdingUser> allUsers = new ArrayList<>();
        try {
            List<DingdingDepartment> topDepts = getDepartments(null);
            for (DingdingDepartment dept : topDepts) {
                allUsers.addAll(getUsersByDepartment(dept.getDeptId()));
            }
        } catch (Exception e) {
            logger.error("Failed to get all users", e);
        }
        return allUsers;
    }

    public List<DingdingUser> getUsersByDepartment(String departmentId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/user/list?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("dept_id", Long.parseLong(departmentId));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            List<DingdingUser> users = new ArrayList<>();
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                JSONArray items = result.getJSONArray("result");
                if (items != null) {
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

    public DingdingUser getUser(String userId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/user/get?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("userid", userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                return parseUser(result.getJSONObject("result"));
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get user: {}", userId, e);
            return null;
        }
    }

    public DingdingUser getUserByAccount(String account) {
        return getUser(account);
    }

    public DingdingUser getUserByMobile(String mobile) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/user/getbymobile?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("mobile", mobile);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                return parseUser(result.getJSONObject("result"));
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to get user by mobile: {}", mobile, e);
            return null;
        }
    }

    public DingdingUser getUserByEmail(String email) {
        return null;
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
            String url = config.getApiBaseUrl() + "/gettoken?appkey=" + config.getAppKey() + "&appsecret=" + config.getAppSecret();
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                accessToken = result.getString("access_token");
                int expire = result.getIntValue("expires_in");
                tokenExpireTime = System.currentTimeMillis() + (expire - 300) * 1000L;
                logger.info("Dingding access token refreshed");
            }
        } catch (Exception e) {
            logger.error("Failed to refresh token", e);
        }
    }

    private DingdingDepartment parseDepartment(JSONObject json) {
        DingdingDepartment dept = new DingdingDepartment();
        dept.setDeptId(String.valueOf(json.getLong("dept_id")));
        dept.setName(json.getString("name"));
        dept.setParentId(String.valueOf(json.getLong("parent_id")));
        dept.setMemberCount(json.getIntValue("member_count"));
        return dept;
    }

    private DingdingUser parseUser(JSONObject json) {
        DingdingUser user = new DingdingUser();
        user.setUserid(json.getString("userid"));
        user.setName(json.getString("name"));
        user.setMobile(json.getString("mobile"));
        user.setEmail(json.getString("email"));
        user.setStatus(json.getIntValue("status"));
        JSONArray deptIds = json.getJSONArray("dept_id_list");
        if (deptIds != null && !deptIds.isEmpty()) {
            user.setDeptId(String.valueOf(deptIds.getLong(0)));
        }
        return user;
    }
}
