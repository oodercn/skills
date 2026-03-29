package net.ooder.skill.org.wecom.service;

import net.ooder.skill.org.wecom.client.WeComApiClient;
import net.ooder.skill.org.wecom.dto.SyncResultDTO;
import net.ooder.skill.org.wecom.model.WeComDepartment;
import net.ooder.skill.org.wecom.model.WeComUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeComOrgSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(WeComOrgSyncService.class);
    
    @Autowired
    private WeComApiClient apiClient;
    
    private Map<String, WeComUser> userCache = new ConcurrentHashMap<>();
    private Map<String, WeComDepartment> deptCache = new ConcurrentHashMap<>();
    private long lastSyncTime = 0;
    
    public SyncResultDTO syncAll() {
        log.info("Starting full organization sync from WeCom");
        SyncResultDTO result = new SyncResultDTO();
        result.setSyncTime(System.currentTimeMillis());
        
        try {
            List<WeComDepartment> departments = syncDepartments();
            List<WeComUser> users = syncUsers();
            
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
        }
        
        return result;
    }
    
    public List<WeComDepartment> syncDepartments() {
        log.info("Syncing departments from WeCom");
        
        List<WeComDepartment> allDepts = new ArrayList<>();
        List<WeComDepartment> rootDepts = apiClient.getDepartments(null);
        
        for (WeComDepartment dept : rootDepts) {
            allDepts.add(dept);
            deptCache.put(String.valueOf(dept.getId()), dept);
            syncSubDepartments(dept.getId(), allDepts);
        }
        
        log.info("Synced {} departments", allDepts.size());
        return allDepts;
    }
    
    private void syncSubDepartments(Long parentId, List<WeComDepartment> allDepts) {
        List<WeComDepartment> subDepts = apiClient.getDepartments(parentId);
        for (WeComDepartment dept : subDepts) {
            allDepts.add(dept);
            deptCache.put(String.valueOf(dept.getId()), dept);
        }
    }
    
    public List<WeComUser> syncUsers() {
        log.info("Syncing users from WeCom");
        
        List<WeComUser> allUsers = new ArrayList<>();
        
        for (WeComDepartment dept : deptCache.values()) {
            List<WeComUser> deptUsers = apiClient.getUsersByDepartment(dept.getId());
            for (WeComUser user : deptUsers) {
                userCache.put(user.getUserid(), user);
                if (!containsUser(allUsers, user.getUserid())) {
                    allUsers.add(user);
                }
            }
        }
        
        log.info("Synced {} users", allUsers.size());
        return allUsers;
    }
    
    private boolean containsUser(List<WeComUser> users, String userId) {
        for (WeComUser u : users) {
            if (u.getUserid().equals(userId)) {
                return true;
            }
        }
        return false;
    }
    
    public List<WeComUser> searchUsers(String keyword) {
        log.info("Searching users with keyword: {}", keyword);
        List<WeComUser> results = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        
        for (WeComUser user : userCache.values()) {
            if ((user.getName() != null && user.getName().toLowerCase().contains(lowerKeyword)) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword)) ||
                (user.getMobile() != null && user.getMobile().contains(keyword))) {
                results.add(user);
            }
        }
        
        return results;
    }
    
    public List<WeComUser> getCachedUsers() { return new ArrayList<>(userCache.values()); }
    public WeComUser getCachedUser(String userId) { return userCache.get(userId); }
    public List<WeComDepartment> getCachedDepartments() { return new ArrayList<>(deptCache.values()); }
    public WeComDepartment getCachedDepartment(String deptId) { return deptCache.get(deptId); }
    public long getLastSyncTime() { return lastSyncTime; }
    
    public void clearCache() {
        userCache.clear();
        deptCache.clear();
        lastSyncTime = 0;
        log.info("Organization cache cleared");
    }
}
