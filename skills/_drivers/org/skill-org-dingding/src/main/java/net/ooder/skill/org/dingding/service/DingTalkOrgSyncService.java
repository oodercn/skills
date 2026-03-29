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
}
