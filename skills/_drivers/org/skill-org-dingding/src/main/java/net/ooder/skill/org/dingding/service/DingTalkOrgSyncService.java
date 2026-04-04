package net.ooder.skill.org.dingding.service;

import net.ooder.skill.org.dingding.client.DingdingApiClient;
import net.ooder.skill.org.dingding.dto.SyncResultDTO;
import net.ooder.skill.org.dingding.model.DingdingDepartment;
import net.ooder.skill.org.dingding.model.DingdingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DingTalkOrgSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(DingTalkOrgSyncService.class);
    
    @Autowired
    private DingdingApiClient apiClient;
    
    private Map<String, DingdingUser> userCache = new ConcurrentHashMap<>();
    private Map<String, DingdingDepartment> deptCache = new ConcurrentHashMap<>();
    private long lastSyncTime = 0;
    
    public SyncResultDTO syncAll() {
        log.info("Starting full organization sync from DingTalk");
        SyncResultDTO result = new SyncResultDTO();
        result.setSyncTime(System.currentTimeMillis());
        
        try {
            List<DingdingDepartment> departments = syncDepartments();
            List<DingdingUser> users = syncUsers();
            
            result.setSuccess(true);
            result.setTotalDepartments(departments.size());
            result.setTotalUsers(users.size());
            result.setMessage("同步成功");
            
            lastSyncTime = System.currentTimeMillis();
            log.info("Organization sync completed: {} departments, {} users", 
                departments.size(), users.size());
            
        } catch (Exception e) {
            log.error("Organization sync failed", e);
            result.setSuccess(false);
            result.setMessage("同步失败: " + e.getMessage());
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            result.setErrors(errors);
        }
        
        return result;
    }
    
    public List<DingdingDepartment> syncDepartments() {
        log.info("Syncing departments from DingTalk");
        
        List<DingdingDepartment> allDepts = new ArrayList<>();
        List<DingdingDepartment> rootDepts = apiClient.getDepartments(null);
        
        for (DingdingDepartment dept : rootDepts) {
            allDepts.add(dept);
            deptCache.put(dept.getDeptId(), dept);
            syncSubDepartments(dept.getDeptId(), allDepts);
        }
        
        log.info("Synced {} departments", allDepts.size());
        return allDepts;
    }
    
    private void syncSubDepartments(String parentId, List<DingdingDepartment> allDepts) {
        List<DingdingDepartment> subDepts = apiClient.getDepartments(parentId);
        for (DingdingDepartment dept : subDepts) {
            allDepts.add(dept);
            deptCache.put(dept.getDeptId(), dept);
            syncSubDepartments(dept.getDeptId(), allDepts);
        }
    }
    
    public List<DingdingUser> syncUsers() {
        log.info("Syncing users from DingTalk");
        
        List<DingdingUser> allUsers = new ArrayList<>();
        
        for (DingdingDepartment dept : deptCache.values()) {
            List<DingdingUser> deptUsers = apiClient.getUsersByDepartment(dept.getDeptId());
            for (DingdingUser user : deptUsers) {
                userCache.put(user.getUserid(), user);
                if (!containsUser(allUsers, user.getUserid())) {
                    allUsers.add(user);
                }
            }
        }
        
        log.info("Synced {} users", allUsers.size());
        return allUsers;
    }
    
    private boolean containsUser(List<DingdingUser> users, String userId) {
        for (DingdingUser u : users) {
            if (u.getUserid().equals(userId)) {
                return true;
            }
        }
        return false;
    }
    
    public SyncResultDTO syncUsersByDepartment(String departmentId) {
        log.info("Syncing users for department: {}", departmentId);
        
        SyncResultDTO result = new SyncResultDTO();
        result.setSyncTime(System.currentTimeMillis());
        
        try {
            List<DingdingUser> users = apiClient.getUsersByDepartment(departmentId);
            for (DingdingUser user : users) {
                userCache.put(user.getUserid(), user);
            }
            
            result.setSuccess(true);
            result.setTotalUsers(users.size());
            result.setMessage("部门用户同步成功");
            
        } catch (Exception e) {
            log.error("Failed to sync users for department: {}", departmentId, e);
            result.setSuccess(false);
            result.setMessage("同步失败: " + e.getMessage());
        }
        
        return result;
    }
    
    public List<DingdingUser> getCachedUsers() {
        return new ArrayList<>(userCache.values());
    }
    
    public DingdingUser getCachedUser(String userId) {
        return userCache.get(userId);
    }
    
    public List<DingdingDepartment> getCachedDepartments() {
        return new ArrayList<>(deptCache.values());
    }
    
    public DingdingDepartment getCachedDepartment(String deptId) {
        return deptCache.get(deptId);
    }
    
    public long getLastSyncTime() {
        return lastSyncTime;
    }
    
    public void clearCache() {
        userCache.clear();
        deptCache.clear();
        lastSyncTime = 0;
        log.info("Organization cache cleared");
    }
    
    // ==================== 带缓存同步的 CRUD 操作 ====================
    
    public DingdingUser createUserWithCache(String name, String mobile, List<Long> departmentIds,
                                              String position, String jobNumber, String email) {
        log.info("Creating user and updating cache: name={}", name);
        DingdingUser user = apiClient.createUser(name, mobile, departmentIds, position, jobNumber, email);
        if (user != null) {
            userCache.put(user.getUserid(), user);
        }
        return user;
    }
    
    public boolean updateUserWithCache(String userId, String name, String mobile, String email,
                                        Long departmentId, String position, int status) {
        log.info("Updating user and refreshing cache: userId={}", userId);
        boolean success = apiClient.updateUser(userId, name, mobile, email, departmentId, position, status);
        if (success) {
            DingdingUser refreshed = apiClient.getUser(userId);
            if (refreshed != null) {
                userCache.put(userId, refreshed);
            } else {
                userCache.remove(userId);
            }
        }
        return success;
    }
    
    public boolean deleteUserWithCache(String userId) {
        log.info("Deleting user from cache: userId={}", userId);
        boolean success = apiClient.deleteUser(userId);
        if (success) {
            userCache.remove(userId);
        }
        return success;
    }
    
    public DingdingDepartment createDepartmentWithCache(String parentId, String name, Long order, String deptManagerUserId) {
        log.info("Creating department and updating cache: name={}", name);
        DingdingDepartment dept = apiClient.createDepartment(parentId, name, order, deptManagerUserId);
        if (dept != null) {
            deptCache.put(dept.getDeptId(), dept);
        }
        return dept;
    }
    
    public boolean updateDepartmentWithCache(String departmentId, String name, Long order, String deptManagerUserId) {
        log.info("Updating department and refreshing cache: deptId={}", departmentId);
        boolean success = apiClient.updateDepartment(departmentId, name, order, deptManagerUserId);
        if (success) {
            DingdingDepartment refreshed = apiClient.getDepartment(departmentId);
            if (refreshed != null) {
                deptCache.put(departmentId, refreshed);
            } else {
                deptCache.remove(departmentId);
            }
        }
        return success;
    }
    
    public boolean deleteDepartmentWithCache(String departmentId) {
        log.info("Deleting department from cache: deptId={}", departmentId);
        boolean success = apiClient.deleteDepartment(departmentId);
        if (success) {
            deptCache.remove(departmentId);
        }
        return success;
    }
    
    public DingdingUser getUserByEmailFromApi(String email) {
        return apiClient.getUserByEmail(email);
    }
    
    public DingdingUser getFreeLoginUserFromApi(String authCode) {
        return apiClient.getFreeLoginUser(authCode);
    }
}
