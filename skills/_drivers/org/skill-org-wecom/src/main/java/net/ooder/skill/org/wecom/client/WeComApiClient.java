package net.ooder.skill.org.wecom.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ooder.skill.org.wecom.config.WeComConfig;
import net.ooder.skill.org.wecom.model.WeComDepartment;
import net.ooder.skill.org.wecom.model.WeComUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeComApiClient {

    private static final Logger logger = LoggerFactory.getLogger(WeComApiClient.class);

    @Autowired
    private WeComConfig config;

    private RestTemplate restTemplate = new RestTemplate();

    private String accessToken;
    private long tokenExpireTime;

    public synchronized String getAccessToken() {
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/gettoken?corpid=" + config.getCorpId() 
                    + "&corpsecret=" + config.getSecret();
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = JSON.parseObject(response.getBody());
            
            if (json.getInteger("errcode") == 0) {
                accessToken = json.getString("access_token");
                tokenExpireTime = System.currentTimeMillis() + json.getLong("expires_in") * 1000 - 60000;
                return accessToken;
            } else {
                logger.error("Failed to get access token: {}", json.getString("errmsg"));
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting access token", e);
            return null;
        }
    }

    public List<WeComDepartment> getDepartments(String deptId) {
        List<WeComDepartment> departments = new ArrayList<>();
        String token = getAccessToken();
        if (token == null) {
            return departments;
        }

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/department/list?access_token=" + token;
            if (deptId != null) {
                url += "&id=" + deptId;
            }
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = JSON.parseObject(response.getBody());
            
            if (json.getInteger("errcode") == 0) {
                JSONArray deptArray = json.getJSONArray("department");
                for (int i = 0; i < deptArray.size(); i++) {
                    JSONObject deptJson = deptArray.getJSONObject(i);
                    WeComDepartment dept = new WeComDepartment();
                    dept.setDeptId(String.valueOf(deptJson.getLong("id")));
                    dept.setName(deptJson.getString("name"));
                    dept.setParentId(deptJson.getLong("parentid") != null ? String.valueOf(deptJson.getLong("parentid")) : null);
                    dept.setOrder(deptJson.getInteger("order"));
                    departments.add(dept);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting departments", e);
        }

        return departments;
    }

    public WeComDepartment getDepartment(String deptId) {
        List<WeComDepartment> departments = getDepartments(deptId);
        if (!departments.isEmpty()) {
            return departments.get(0);
        }
        return null;
    }

    public List<WeComUser> getUsersByDepartment(String deptId) {
        List<WeComUser> users = new ArrayList<>();
        String token = getAccessToken();
        if (token == null) {
            return users;
        }

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/user/list?access_token=" + token 
                    + "&department_id=" + deptId;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = JSON.parseObject(response.getBody());
            
            if (json.getInteger("errcode") == 0) {
                JSONArray userArray = json.getJSONArray("userlist");
                for (int i = 0; i < userArray.size(); i++) {
                    JSONObject userJson = userArray.getJSONObject(i);
                    WeComUser user = new WeComUser();
                    user.setUserid(userJson.getString("userid"));
                    user.setName(userJson.getString("name"));
                    user.setMobile(userJson.getString("mobile"));
                    user.setEmail(userJson.getString("email"));
                    user.setStatus(userJson.getInteger("status"));
                    
                    JSONArray deptArray = userJson.getJSONArray("department");
                    if (deptArray != null) {
                        List<String> depts = new ArrayList<>();
                        for (int j = 0; j < deptArray.size(); j++) {
                            depts.add(String.valueOf(deptArray.getLong(j)));
                        }
                        user.setDepartment(depts);
                    }
                    users.add(user);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting users by department", e);
        }

        return users;
    }

    public WeComUser getUser(String userId) {
        String token = getAccessToken();
        if (token == null) {
            return null;
        }

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/user/get?access_token=" + token 
                    + "&userid=" + userId;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = JSON.parseObject(response.getBody());
            
            if (json.getInteger("errcode") == 0) {
                WeComUser user = new WeComUser();
                user.setUserid(json.getString("userid"));
                user.setName(json.getString("name"));
                user.setMobile(json.getString("mobile"));
                user.setEmail(json.getString("email"));
                user.setStatus(json.getInteger("status"));
                
                JSONArray deptArray = json.getJSONArray("department");
                if (deptArray != null) {
                    List<String> depts = new ArrayList<>();
                    for (int j = 0; j < deptArray.size(); j++) {
                        depts.add(String.valueOf(deptArray.getLong(j)));
                    }
                    user.setDepartment(depts);
                }
                return user;
            }
        } catch (Exception e) {
            logger.error("Error getting user", e);
        }

        return null;
    }

    public WeComUser getUserByMobile(String mobile) {
        String token = getAccessToken();
        if (token == null) {
            return null;
        }

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/user/getuserid?access_token=" + token;
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            JSONObject body = new JSONObject();
            body.put("mobile", mobile);
            
            HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            JSONObject json = JSON.parseObject(response.getBody());
            if (json.getInteger("errcode") == 0) {
                String userId = json.getString("userid");
                if (userId != null) {
                    return getUser(userId);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting user by mobile", e);
        }

        return null;
    }

    public List<WeComUser> getAllUsers() {
        List<WeComUser> allUsers = new ArrayList<>();
        List<WeComDepartment> departments = getDepartments(null);
        
        for (WeComDepartment dept : departments) {
            List<WeComUser> users = getUsersByDepartment(dept.getDeptId());
            allUsers.addAll(users);
        }
        
        return allUsers;
    }

    public boolean verifyUser(String userId, String password) {
        WeComUser user = getUser(userId);
        return user != null && user.getStatus() != null && user.getStatus() == 1;
    }

    public WeComUser getUserByEmail(String email) {
        String token = getAccessToken();
        if (token == null) return null;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/user/getuserid?access_token=" + token;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject body = new JSONObject();
            body.put("email", email);

            HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JSONObject json = JSON.parseObject(response.getBody());
            if (json.getInteger("errcode") == 0) {
                String userId = json.getString("userid");
                if (userId != null) {
                    return getUser(userId);
                }
            }
            logger.warn("getUserByEmail failed: errcode={}, errmsg={}",
                    json.getInteger("errcode"), json.getString("errmsg"));
        } catch (Exception e) {
            logger.error("Error getting user by email: {}", email, e);
        }
        return null;
    }

    public WeComUser getFreeLoginUser(String code) {
        String token = getAccessToken();
        if (token == null) return null;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/auth/getuserinfo?access_token=" + token + "&code=" + code;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = JSON.parseObject(response.getBody());

            if (json.getInteger("errcode") == 0) {
                String userId = json.getString("userid");
                if (userId != null) {
                    return getUser(userId);
                }
            }
            logger.warn("getFreeLoginUser failed: errcode={}", json.getInteger("errcode"));
        } catch (Exception e) {
            logger.error("Error getting free login user", e);
        }
        return null;
    }

    public Map<String, Object> getUserInfoByCode(String code) {
        String token = getAccessToken();
        if (token == null) return null;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/auth/getuserinfo?access_token=" + token + "&code=" + code;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = JSON.parseObject(response.getBody());

            if (json.getInteger("errcode") == 0) {
                Map<String, Object> result = new java.util.HashMap<>();
                result.put("UserId", json.getString("userid"));
                result.put("DeviceId", json.getString("deviceid"));
                result.put("errcode", json.getInteger("errcode"));
                result.put("errmsg", "ok");
                return result;
            }
            logger.warn("getUserInfoByCode failed: errcode={}, errmsg={}",
                    json.getInteger("errcode"), json.getString("errmsg"));
        } catch (Exception e) {
            logger.error("Error getting user info by code: {}", code, e);
        }
        return null;
    }

    // ==================== 部门 CRUD ====================

    public boolean createDepartment(Long parentId, String name, Integer order, Long overrideParentId) {
        String token = getAccessToken();
        if (token == null) return false;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/department/create?access_token=" + token;

            JSONObject body = new JSONObject();
            body.put("name", name);
            Long effectiveParentId = (overrideParentId != null) ? overrideParentId : parentId;
            body.put("parentid", effectiveParentId != null ? effectiveParentId : 1L);
            if (order != null) body.put("order", order);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JSONObject json = JSON.parseObject(response.getBody());
            if (json.getInteger("errcode") == 0) {
                logger.info("Created department: id={}, name={}", json.getLong("id"), name);
                return true;
            }
            logger.warn("createDepartment failed: errcode={}, errmsg={}",
                    json.getInteger("errcode"), json.getString("errmsg"));
        } catch (Exception e) {
            logger.error("Failed to create department", e);
        }
        return false;
    }

    public boolean updateDepartment(Long deptId, String name, Integer order, Long parentId) {
        String token = getAccessToken();
        if (token == null) return false;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/department/update?access_token=" + token;

            JSONObject body = new JSONObject();
            body.put("id", deptId);
            if (name != null) body.put("name", name);
            if (order != null) body.put("order", order);
            if (parentId != null) body.put("parentid", parentId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JSONObject json = JSON.parseObject(response.getBody());
            if (json.getInteger("errcode") == 0) {
                logger.info("Updated department: {}", deptId);
                return true;
            }
            logger.warn("updateDepartment failed: errcode={}, errmsg={}",
                    json.getInteger("errcode"), json.getString("errmsg"));
        } catch (Exception e) {
            logger.error("Failed to update department: {}", deptId, e);
        }
        return false;
    }

    public boolean deleteDepartment(Long deptId) {
        String token = getAccessToken();
        if (token == null) return false;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/department/delete?access_token=" + token + "&id=" + deptId;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = JSON.parseObject(response.getBody());

            if (json.getInteger("errcode") == 0) {
                logger.info("Deleted department: {}", deptId);
                return true;
            }
            logger.warn("deleteDepartment failed: errcode={}, errmsg={}",
                    json.getInteger("errcode"), json.getString("errmsg"));
        } catch (Exception e) {
            logger.error("Failed to delete department: {}", deptId, e);
        }
        return false;
    }

    // ==================== 用户 CRUD ====================

    public boolean createUser(String userid, String name, String mobile, List<Long> departmentIds,
                               Integer position, String email) {
        String token = getAccessToken();
        if (token == null) return false;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/user/create?access_token=" + token;

            JSONObject body = new JSONObject();
            body.put("userid", userid);
            body.put("name", name);
            if (mobile != null && !mobile.isEmpty()) body.put("mobile", mobile);
            if (departmentIds != null && !departmentIds.isEmpty()) body.put("department", departmentIds);
            if (position != null) body.put("position", position);
            if (email != null && !email.isEmpty()) body.put("email", email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JSONObject json = JSON.parseObject(response.getBody());
            if (json.getInteger("errcode") == 0) {
                logger.info("Created user: userid={}, name={}", userid, name);
                return true;
            }
            logger.warn("createUser failed: errcode={}, errmsg={}",
                    json.getInteger("errcode"), json.getString("errmsg"));
        } catch (Exception e) {
            logger.error("Failed to create user", e);
        }
        return false;
    }

    public boolean updateUser(String userId, String name, String mobile, String email,
                               Long departmentId, Integer position, int enable) {
        String token = getAccessToken();
        if (token == null) return false;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/user/update?access_token=" + token;

            JSONObject body = new JSONObject();
            body.put("userid", userId);
            if (name != null) body.put("name", name);
            if (mobile != null) body.put("mobile", mobile);
            if (email != null) body.put("email", email);
            if (departmentId != null) body.put("department", java.util.Arrays.asList(departmentId));
            if (position != null) body.put("position", position);
            if (enable >= 0) body.put("enable", enable > 0 ? 1 : 0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JSONObject json = JSON.parseObject(response.getBody());
            if (json.getInteger("errcode") == 0) {
                logger.info("Updated user: {}", userId);
                return true;
            }
            logger.warn("updateUser failed: errcode={}, errmsg={}",
                    json.getInteger("errcode"), json.getString("errmsg"));
        } catch (Exception e) {
            logger.error("Failed to update user: {}", userId, e);
        }
        return false;
    }

    public boolean deleteUser(String userId) {
        String token = getAccessToken();
        if (token == null) return false;

        try {
            String url = config.getApiBaseUrl() + "/cgi-bin/user/delete?access_token=" + token + "&userid=" + userId;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = JSON.parseObject(response.getBody());

            if (json.getInteger("errcode") == 0) {
                logger.info("Deleted user: {}", userId);
                return true;
            }
            logger.warn("deleteUser failed: errcode={}, errmsg={}",
                    json.getInteger("errcode"), json.getString("errmsg"));
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", userId, e);
        }
        return false;
    }

    public boolean disableUser(String userId) {
        return updateUser(userId, null, null, null, null, null, 0);
    }

    public boolean enableUser(String userId) {
        return updateUser(userId, null, null, null, null, null, 1);
    }
}
