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
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/user/getbyemail?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("email", email);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                return parseUser(result.getJSONObject("result"));
            }
            logger.warn("getUserByEmail failed: errcode={}, errmsg={}", 
                    result.getIntValue("errcode"), result.getString("errmsg"));
            return null;
        } catch (Exception e) {
            logger.error("Failed to get user by email: {}", email, e);
            return null;
        }
    }

    public boolean verifyUser(String account, String password) {
        ensureToken();
        try {
            DingdingUser user = getUser(account);
            if (user == null || user.getStatus() != 1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to verify user: {}", account, e);
            return false;
        }
    }

    public DingdingUser getFreeLoginUser(String authCode) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/user/getuserinfo?access_token=" + accessToken + "&code=" + authCode;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject result = JSON.parseObject(response.getBody());
            
            if (result.getIntValue("errcode") == 0) {
                JSONObject userInfo = result.getJSONObject("userInfo");
                String userId = userInfo.getString("userid");
                if (userId != null) {
                    return getUser(userId);
                }
            }
            logger.warn("getFreeLoginUser failed: errcode={}", result.getIntValue("errcode"));
            return null;
        } catch (Exception e) {
            logger.error("Failed to get free login user", e);
            return null;
        }
    }

    // ==================== 部门 CRUD ====================

    public DingdingDepartment createDepartment(String parentId, String name, Long order, String deptManagerUserId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/department/create_subdept?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("parentid", Long.parseLong(parentId));
            body.put("name", name);
            if (order != null) body.put("order", order);
            if (deptManagerUserId != null) body.put("deptmanager_userid", deptManagerUserId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                Long deptId = result.getLongValue("result");
                logger.info("Created department: id={}, name={}", deptId, name);
                return getDepartment(String.valueOf(deptId));
            }
            logger.warn("createDepartment failed: errcode={}, errmsg={}", 
                    result.getIntValue("errcode"), result.getString("errmsg"));
            return null;
        } catch (Exception e) {
            logger.error("Failed to create department", e);
            return null;
        }
    }

    public boolean updateDepartment(String departmentId, String name, Long order, String deptManagerUserId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/department/update?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("dept_id", Long.parseLong(departmentId));
            if (name != null) body.put("name", name);
            if (order != null) body.put("order", order);
            if (deptManagerUserId != null) body.put("deptmanager_userid", deptManagerUserId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                logger.info("Updated department: {}", departmentId);
                return true;
            }
            logger.warn("updateDepartment failed: errcode={}, errmsg={}", 
                    result.getIntValue("errcode"), result.getString("errmsg"));
            return false;
        } catch (Exception e) {
            logger.error("Failed to update department: {}", departmentId, e);
            return false;
        }
    }

    public boolean deleteDepartment(String departmentId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/department/delete?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("dept_id", Long.parseLong(departmentId));
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                logger.info("Deleted department: {}", departmentId);
                return true;
            }
            logger.warn("deleteDepartment failed: errcode={}, errmsg={}", 
                    result.getIntValue("errcode"), result.getString("errmsg"));
            return false;
        } catch (Exception e) {
            logger.error("Failed to delete department: {}", departmentId, e);
            return false;
        }
    }

    // ==================== 用户 CRUD ====================

    public DingdingUser createUser(String name, String mobile, List<Long> departmentIds, 
                                     String position, String jobNumber, String email) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/user/create?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("name", name);
            if (mobile != null && !mobile.isEmpty()) body.put("mobile", mobile);
            if (departmentIds != null && !departmentIds.isEmpty()) body.put("dept_id_list", departmentIds);
            if (position != null) body.put("position", position);
            if (jobNumber != null) body.put("jobnumber", jobNumber);
            if (email != null && !email.isEmpty()) body.put("email", email);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                String userId = result.getString("result");
                logger.info("Created user: userId={}, name={}", userId, name);
                return getUser(userId);
            }
            logger.warn("createUser failed: errcode={}, errmsg={}", 
                    result.getIntValue("errcode"), result.getString("errmsg"));
            return null;
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            return null;
        }
    }

    public boolean updateUser(String userId, String name, String mobile, String email, 
                             Long departmentId, String position, int status) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/user/update?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("userid", userId);
            if (name != null) body.put("name", name);
            if (mobile != null) body.put("mobile", mobile);
            if (email != null) body.put("email", email);
            if (departmentId != null) body.put("dept_id_list", java.util.Arrays.asList(departmentId));
            if (position != null) body.put("position", position);
            if (status >= 0) body.put("active", status > 0);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                logger.info("Updated user: {}", userId);
                return true;
            }
            logger.warn("updateUser failed: errcode={}, errmsg={}", 
                    result.getIntValue("errcode"), result.getString("errmsg"));
            return false;
        } catch (Exception e) {
            logger.error("Failed to update user: {}", userId, e);
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/v2/user/delete?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("userid", userId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                logger.info("Deleted user: {}", userId);
                return true;
            }
            logger.warn("deleteUser failed: errcode={}, errmsg={}", 
                    result.getIntValue("errcode"), result.getString("errmsg"));
            return false;
        } catch (Exception e) {
            logger.error("Failed to delete user: {}", userId, e);
            return false;
        }
    }

    public boolean disableUser(String userId) {
        return updateUser(userId, null, null, null, null, null, 0);
    }

    public boolean enableUser(String userId) {
        return updateUser(userId, null, null, null, null, null, 1);
    }

    // ==================== 消息已读回执 ====================

    public boolean markConversationRead(String conversationId, long timestamp) {
        ensureToken();
        try {
            String url = config.getApiBaseUrl() + "/topapi/message/corpconversation/readsendsession?access_token=" + accessToken;
            
            JSONObject body = new JSONObject();
            body.put("session_id", conversationId);
            body.put("timestamp", timestamp);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(body.toJSONString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            JSONObject result = JSON.parseObject(response.getBody());
            if (result.getIntValue("errcode") == 0) {
                logger.info("Marked conversation as read: {}", conversationId);
                return true;
            }
            logger.warn("markConversationRead failed: errcode={}, errmsg={}", 
                    result.getIntValue("errcode"), result.getString("errmsg"));
            return false;
        } catch (Exception e) {
            logger.error("Failed to mark conversation as read: {}", conversationId, e);
            return false;
        }
    }

    private synchronized void ensureToken() {
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
