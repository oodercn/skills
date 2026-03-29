package net.ooder.skill.org.feishu.service;

import net.ooder.skill.org.feishu.client.FeishuApiClient;
import net.ooder.skill.org.feishu.dto.SyncResultDTO;
import net.ooder.skill.org.feishu.model.FeishuDepartment;
import net.ooder.skill.org.feishu.model.FeishuUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FeishuOrgSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(FeishuOrgSyncService.class);
    
    @Autowired
    private FeishuApiClient apiClient;
    
    private Map<String, FeishuUser> userCache = new ConcurrentHashMap<>();
    private Map<String, FeishuDepartment> deptCache = new ConcurrentHashMap<>();
    private long lastSyncTime = 0;
    
    public SyncResultDTO syncAll() {
        log.info("Starting full organization sync from Feishu");
        SyncResultDTO result = new SyncResultDTO();
        result.setSyncTime(System.currentTimeMillis());
        
        try {
            List<FeishuDepartment> departments = syncDepartments();
            List<FeishuUser> users = syncUsers();
            
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
    
    public List<FeishuDepartment> syncDepartments() {
        log.info("Syncing departments from Feishu");
        
        List<FeishuDepartment> allDepts = new ArrayList<>();
        List<FeishuDepartment> rootDepts = apiClient.getDepartments(null);
        
        for (FeishuDepartment dept : rootDepts) {
            allDepts.add(dept);
            deptCache.put(dept.getDepartmentId(), dept);
            syncSubDepartments(dept.getDepartmentId(), allDepts);
        }
        
        log.info("Synced {} departments", allDepts.size());
        return allDepts;
    }
    
    private void syncSubDepartments(String parentId, List<FeishuDepartment> allDepts) {
        List<FeishuDepartment> subDepts = apiClient.getDepartments(parentId);
        for (FeishuDepartment dept : subDepts) {
            allDepts.add(dept);
            deptCache.put(dept.getDepartmentId(), dept);
            syncSubDepartments(dept.getDepartmentId(), allDepts);
        }
    }
    
    public List<FeishuUser> syncUsers() {
        log.info("Syncing users from Feishu");
        
        List<FeishuUser> allUsers = new ArrayList<>();
        
        for (FeishuDepartment dept : deptCache.values()) {
            List<FeishuUser> deptUsers = apiClient.getUsersByDepartment(dept.getDepartmentId());
            for (FeishuUser user : deptUsers) {
                userCache.put(user.getUserId(), user);
                if (!containsUser(allUsers, user.getUserId())) {
                    allUsers.add(user);
                }
            }
        }
        
        log.info("Synced {} users", allUsers.size());
        return allUsers;
    }
    
    private boolean containsUser(List<FeishuUser> users, String userId) {
        for (FeishuUser u : users) {
            if (u.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
    
    public List<FeishuUser> searchUsers(String keyword) {
        log.info("Searching users with keyword: {}", keyword);
        List<FeishuUser> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        
        for (FeishuUser user : userCache.values()) {
            if ((user.getName() != null && user.getName().toLowerCase().contains(lowerKeyword)) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword)) ||
                (user.getMobile() != null && user.getMobile().contains(keyword))) {
                results.add(user);
            }
        }
        
        return results;
    }
    
    public List<FeishuUser> getCachedUsers() {
        return new ArrayList<>(userCache.values());
    }
    
    public FeishuUser getCachedUser(String userId) {
        return userCache.get(userId);
    }
    
    public List<FeishuDepartment> getCachedDepartments() {
        return new ArrayList<>(deptCache.values());
    }
    
    public FeishuDepartment getCachedDepartment(String deptId) {
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
