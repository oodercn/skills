package net.ooder.mvp.skill.scene.adapter;

import net.ooder.mvp.skill.scene.dto.OrgUserDTO;
import net.ooder.mvp.skill.scene.dto.OrgDepartmentDTO;
import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.scene.SceneGroupDTO;
import net.ooder.mvp.skill.scene.dto.scene.SceneParticipantDTO;
import net.ooder.mvp.skill.scene.service.SceneGroupService;
import net.ooder.mvp.skill.scene.capability.service.CapabilityService;
import net.ooder.mvp.skill.scene.capability.model.Capability;
import net.ooder.skill.common.storage.JsonStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrgWebAdapter {

    private static final Logger log = LoggerFactory.getLogger(OrgWebAdapter.class);

    private static final String STORAGE_KEY_USERS = "org-users";
    private static final String STORAGE_KEY_DEPARTMENTS = "org-departments";
    private static final String STORAGE_KEY_SYNC_TIME = "org-sync-time";

    @Value("${org-web.storage-path:org}")
    private String storagePath;

    @Value("${org-web.auto-init:true}")
    private boolean autoInit;

    @Autowired
    private JsonStorageService storage;

    @Autowired(required = false)
    private SceneGroupService sceneGroupService;

    @Autowired(required = false)
    private CapabilityService capabilityService;

    private final Map<String, OrgUserDTO> users = new ConcurrentHashMap<>();
    private final Map<String, OrgDepartmentDTO> departments = new ConcurrentHashMap<>();
    private volatile long lastSyncTime = 0;

    @PostConstruct
    public void init() {
        log.info("[OrgWeb] Initializing minimal org-web implementation...");
        
        loadFromStorage();
        
        if (users.isEmpty() && autoInit) {
            log.info("[OrgWeb] No existing data found, initializing default organization...");
            initDefaultOrganization();
        }
        
        log.info("[OrgWeb] Loaded {} users, {} departments", users.size(), departments.size());
    }

    private void loadFromStorage() {
        try {
            Map<String, OrgUserDTO> storedUsers = storage.getAll(STORAGE_KEY_USERS);
            if (storedUsers != null && !storedUsers.isEmpty()) {
                users.putAll(storedUsers);
                log.info("[OrgWeb] Loaded {} users from storage", users.size());
            }

            Map<String, OrgDepartmentDTO> storedDepts = storage.getAll(STORAGE_KEY_DEPARTMENTS);
            if (storedDepts != null && !storedDepts.isEmpty()) {
                departments.putAll(storedDepts);
                log.info("[OrgWeb] Loaded {} departments from storage", departments.size());
            }
            
            Map<String, Object> syncData = storage.getAll(STORAGE_KEY_SYNC_TIME);
            if (syncData != null && syncData.containsKey("lastSync")) {
                Object syncValue = syncData.get("lastSync");
                if (syncValue instanceof Number) {
                    lastSyncTime = ((Number) syncValue).longValue();
                }
            }
        } catch (Exception e) {
            log.warn("[OrgWeb] Failed to load from storage: {}", e.getMessage());
        }
    }

    private void persistUsers() {
        try {
            users.forEach((id, user) -> storage.put(STORAGE_KEY_USERS, id, user));
            log.debug("[OrgWeb] Persisted {} users", users.size());
        } catch (Exception e) {
            log.error("[OrgWeb] Failed to persist users: {}", e.getMessage());
        }
    }

    private void persistDepartments() {
        try {
            departments.forEach((id, dept) -> storage.put(STORAGE_KEY_DEPARTMENTS, id, dept));
            log.debug("[OrgWeb] Persisted {} departments", departments.size());
        } catch (Exception e) {
            log.error("[OrgWeb] Failed to persist departments: {}", e.getMessage());
        }
    }

    private void updateSyncTime() {
        lastSyncTime = System.currentTimeMillis();
        storage.put(STORAGE_KEY_SYNC_TIME, "lastSync", lastSyncTime);
    }

    private void initDefaultOrganization() {
        initDefaultDepartments();
        initDefaultUsers();
        updateSyncTime();
        log.info("[OrgWeb] Default organization initialized: {} users, {} departments", 
            users.size(), departments.size());
    }

    private void initDefaultDepartments() {
        long now = System.currentTimeMillis();
        
        OrgDepartmentDTO rd = new OrgDepartmentDTO();
        rd.setDepartmentId("dept-rd");
        rd.setName("研发部");
        rd.setDescription("负责产品研发和技术创新");
        rd.setParentId(null);
        rd.setManagerId("user-manager-001");
        rd.setMemberIds(Arrays.asList(
            "user-manager-001", "user-employee-001", "user-employee-002", 
            "user-employee-003", "user-leader-001", "user-collaborator-001"
        ));
        rd.setCreateTime(now);
        rd.setUpdateTime(now);
        departments.put(rd.getDepartmentId(), rd);

        OrgDepartmentDTO hr = new OrgDepartmentDTO();
        hr.setDepartmentId("dept-hr");
        hr.setName("人力资源部");
        hr.setDescription("负责人才招聘和员工管理");
        hr.setParentId(null);
        hr.setManagerId("user-hr-001");
        hr.setMemberIds(Arrays.asList("user-hr-001"));
        hr.setCreateTime(now);
        hr.setUpdateTime(now);
        departments.put(hr.getDepartmentId(), hr);

        OrgDepartmentDTO it = new OrgDepartmentDTO();
        it.setDepartmentId("dept-it");
        it.setName("信息技术部");
        it.setDescription("负责系统运维和技术支持");
        it.setParentId(null);
        it.setManagerId("user-admin-001");
        it.setMemberIds(Arrays.asList("user-admin-001", "user-installer-001"));
        it.setCreateTime(now);
        it.setUpdateTime(now);
        departments.put(it.getDepartmentId(), it);

        persistDepartments();
    }

    private void initDefaultUsers() {
        long now = System.currentTimeMillis();
        
        createUser("user-manager-001", "张经理", "zhang.manager@example.com", 
            "dept-rd", "manager", "研发经理", now);
        createUser("user-employee-001", "李员工", "li.employee@example.com", 
            "dept-rd", "employee", "高级工程师", now);
        createUser("user-employee-002", "王员工", "wang.employee@example.com", 
            "dept-rd", "employee", "工程师", now);
        createUser("user-employee-003", "赵员工", "zhao.employee@example.com", 
            "dept-rd", "employee", "工程师", now);
        createUser("user-hr-001", "刘HR", "liu.hr@example.com", 
            "dept-hr", "hr", "HR经理", now);
        createUser("user-admin-001", "系统管理员", "admin@example.com", 
            "dept-it", "admin", "系统管理员", now);
        createUser("user-installer-001", "安装者", "installer@example.com", 
            "dept-it", "installer", "系统安装者", now);
        createUser("user-leader-001", "主导者", "leader@example.com", 
            "dept-rd", "leader", "项目主导", now);
        createUser("user-collaborator-001", "协作者", "collaborator@example.com", 
            "dept-rd", "collaborator", "项目协作", now);

        persistUsers();
    }

    private void createUser(String userId, String name, String email, 
                           String deptId, String role, String title, long now) {
        OrgUserDTO user = new OrgUserDTO();
        user.setUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setDepartmentId(deptId);
        user.setRole(role);
        user.setTitle(title);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setActive(true);
        users.put(userId, user);
    }

    public boolean isInitialized() {
        return !users.isEmpty();
    }

    public long getLastSyncTime() {
        return lastSyncTime;
    }

    public OrgUserDTO getUser(String userId) {
        return users.get(userId);
    }

    public OrgUserDTO getCurrentUser() {
        if (!users.isEmpty()) {
            OrgUserDTO admin = users.get("user-admin-001");
            if (admin != null) {
                return admin;
            }
            return users.values().iterator().next();
        }
        OrgUserDTO defaultUser = new OrgUserDTO();
        defaultUser.setUserId("default");
        defaultUser.setName("默认用户");
        defaultUser.setRole("user");
        defaultUser.setActive(true);
        return defaultUser;
    }

    public List<OrgUserDTO> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<OrgUserDTO> getUsersByDepartment(String departmentId) {
        List<OrgUserDTO> result = new ArrayList<>();
        for (OrgUserDTO user : users.values()) {
            if (departmentId.equals(user.getDepartmentId())) {
                result.add(user);
            }
        }
        return result;
    }

    public List<OrgUserDTO> getUsersByRole(String role) {
        List<OrgUserDTO> result = new ArrayList<>();
        for (OrgUserDTO user : users.values()) {
            if (role.equals(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }

    public OrgDepartmentDTO getDepartment(String departmentId) {
        return departments.get(departmentId);
    }

    public List<OrgDepartmentDTO> getAllDepartments() {
        return new ArrayList<>(departments.values());
    }

    public List<OrgUserDTO> getDepartmentMembers(String departmentId) {
        OrgDepartmentDTO dept = departments.get(departmentId);
        if (dept != null && dept.getMemberIds() != null) {
            List<OrgUserDTO> members = new ArrayList<>();
            for (String memberId : dept.getMemberIds()) {
                OrgUserDTO user = users.get(memberId);
                if (user != null) {
                    members.add(user);
                }
            }
            return members;
        }
        return new ArrayList<>();
    }

    public OrgUserDTO getDepartmentManager(String departmentId) {
        OrgDepartmentDTO dept = departments.get(departmentId);
        if (dept != null && dept.getManagerId() != null) {
            return users.get(dept.getManagerId());
        }
        return null;
    }

    public void addUser(OrgUserDTO user) {
        user.setCreateTime(System.currentTimeMillis());
        user.setUpdateTime(System.currentTimeMillis());
        users.put(user.getUserId(), user);
        storage.put(STORAGE_KEY_USERS, user.getUserId(), user);
        updateSyncTime();
        log.info("[OrgWeb] Added user: {}", user.getUserId());
    }

    public void updateUser(OrgUserDTO user) {
        user.setUpdateTime(System.currentTimeMillis());
        users.put(user.getUserId(), user);
        storage.put(STORAGE_KEY_USERS, user.getUserId(), user);
        updateSyncTime();
        log.info("[OrgWeb] Updated user: {}", user.getUserId());
    }

    public boolean deleteUser(String userId) {
        OrgUserDTO removed = users.remove(userId);
        if (removed != null) {
            storage.remove(STORAGE_KEY_USERS, userId);
            updateSyncTime();
            log.info("[OrgWeb] Deleted user: {}", userId);
            return true;
        }
        return false;
    }

    public void addDepartment(OrgDepartmentDTO department) {
        long now = System.currentTimeMillis();
        department.setCreateTime(now);
        department.setUpdateTime(now);
        departments.put(department.getDepartmentId(), department);
        storage.put(STORAGE_KEY_DEPARTMENTS, department.getDepartmentId(), department);
        updateSyncTime();
        log.info("[OrgWeb] Added department: {}", department.getDepartmentId());
    }

    public void updateDepartment(OrgDepartmentDTO department) {
        department.setUpdateTime(System.currentTimeMillis());
        departments.put(department.getDepartmentId(), department);
        storage.put(STORAGE_KEY_DEPARTMENTS, department.getDepartmentId(), department);
        updateSyncTime();
        log.info("[OrgWeb] Updated department: {}", department.getDepartmentId());
    }

    public boolean deleteDepartment(String departmentId) {
        OrgDepartmentDTO removed = departments.remove(departmentId);
        if (removed != null) {
            storage.remove(STORAGE_KEY_DEPARTMENTS, departmentId);
            updateSyncTime();
            log.info("[OrgWeb] Deleted department: {}", departmentId);
            return true;
        }
        return false;
    }

    public boolean addMemberToDepartment(String departmentId, String userId) {
        OrgDepartmentDTO dept = departments.get(departmentId);
        OrgUserDTO user = users.get(userId);
        
        if (dept == null || user == null) {
            return false;
        }
        
        List<String> memberIds = dept.getMemberIds();
        if (memberIds == null) {
            memberIds = new ArrayList<>();
        }
        
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
            dept.setMemberIds(memberIds);
            user.setDepartmentId(departmentId);
            
            dept.setUpdateTime(System.currentTimeMillis());
            user.setUpdateTime(System.currentTimeMillis());
            
            storage.put(STORAGE_KEY_DEPARTMENTS, departmentId, dept);
            storage.put(STORAGE_KEY_USERS, userId, user);
            updateSyncTime();
            log.info("[OrgWeb] Added member {} to department {}", userId, departmentId);
        }
        
        return true;
    }

    public boolean removeMemberFromDepartment(String departmentId, String userId) {
        OrgDepartmentDTO dept = departments.get(departmentId);
        OrgUserDTO user = users.get(userId);
        
        if (dept == null || user == null) {
            return false;
        }
        
        List<String> memberIds = dept.getMemberIds();
        if (memberIds != null && memberIds.contains(userId)) {
            memberIds.remove(userId);
            dept.setMemberIds(memberIds);
            user.setDepartmentId(null);
            
            dept.setUpdateTime(System.currentTimeMillis());
            user.setUpdateTime(System.currentTimeMillis());
            
            storage.put(STORAGE_KEY_DEPARTMENTS, departmentId, dept);
            storage.put(STORAGE_KEY_USERS, userId, user);
            updateSyncTime();
            log.info("[OrgWeb] Removed member {} from department {}", userId, departmentId);
        }
        
        return true;
    }

    public boolean isUserInRole(String userId, String role) {
        OrgUserDTO user = users.get(userId);
        return user != null && role.equals(user.getRole());
    }

    public boolean isUserManager(String userId) {
        return isUserInRole(userId, "manager");
    }
    
    public int getUserSceneCount(String userId) {
        if (sceneGroupService == null) {
            log.debug("[OrgWeb] SceneGroupService not available, returning 0 for user scene count");
            return 0;
        }
        
        try {
            int count = 0;
            
            PageResult<SceneGroupDTO> createdGroups = sceneGroupService.listByCreator(userId, 1, 1000);
            if (createdGroups != null) {
                count += createdGroups.getTotal();
            }
            
            PageResult<SceneGroupDTO> participatedGroups = sceneGroupService.listByParticipant(userId, 1, 1000);
            if (participatedGroups != null) {
                Set<String> uniqueGroupIds = new HashSet<String>();
                
                if (createdGroups != null && createdGroups.getList() != null) {
                    for (SceneGroupDTO group : createdGroups.getList()) {
                        uniqueGroupIds.add(group.getSceneGroupId());
                    }
                }
                
                if (participatedGroups.getList() != null) {
                    for (SceneGroupDTO group : participatedGroups.getList()) {
                        uniqueGroupIds.add(group.getSceneGroupId());
                    }
                }
                
                count = uniqueGroupIds.size();
            }
            
            log.debug("[OrgWeb] User {} has {} scenes", userId, count);
            return count;
        } catch (Exception e) {
            log.warn("[OrgWeb] Failed to get scene count for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }
    
    public int getUserCapabilityCount(String userId) {
        if (capabilityService == null) {
            log.debug("[OrgWeb] CapabilityService not available, returning 0 for user capability count");
            return 0;
        }
        
        try {
            List<Capability> allCapabilities = capabilityService.findAll();
            
            int count = 0;
            for (Capability cap : allCapabilities) {
                String ownerId = cap.getOwnerId();
                if (ownerId != null && ownerId.equals(userId)) {
                    count++;
                }
            }
            
            log.debug("[OrgWeb] User {} has {} capabilities", userId, count);
            return count;
        } catch (Exception e) {
            log.warn("[OrgWeb] Failed to get capability count for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    public void syncUsers(List<OrgUserDTO> externalUsers) {
        log.info("[OrgWeb] Syncing {} users from external source", externalUsers.size());
        for (OrgUserDTO user : externalUsers) {
            if (user.getUserId() != null) {
                OrgUserDTO existing = users.get(user.getUserId());
                if (existing == null || user.getUpdateTime() > existing.getUpdateTime()) {
                    users.put(user.getUserId(), user);
                    storage.put(STORAGE_KEY_USERS, user.getUserId(), user);
                }
            }
        }
        updateSyncTime();
        log.info("[OrgWeb] Sync completed, total users: {}", users.size());
    }

    public void syncDepartments(List<OrgDepartmentDTO> externalDepartments) {
        log.info("[OrgWeb] Syncing {} departments from external source", externalDepartments.size());
        for (OrgDepartmentDTO dept : externalDepartments) {
            if (dept.getDepartmentId() != null) {
                OrgDepartmentDTO existing = departments.get(dept.getDepartmentId());
                if (existing == null || dept.getUpdateTime() > existing.getUpdateTime()) {
                    departments.put(dept.getDepartmentId(), dept);
                    storage.put(STORAGE_KEY_DEPARTMENTS, dept.getDepartmentId(), dept);
                }
            }
        }
        updateSyncTime();
        log.info("[OrgWeb] Sync completed, total departments: {}", departments.size());
    }

    public Map<String, Object> getOrganizationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", users.size());
        stats.put("totalDepartments", departments.size());
        stats.put("activeUsers", users.values().stream().filter(OrgUserDTO::isActive).count());
        stats.put("lastSyncTime", lastSyncTime);
        
        Map<String, Integer> roleCount = new HashMap<>();
        for (OrgUserDTO user : users.values()) {
            String role = user.getRole();
            roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
        }
        stats.put("roleDistribution", roleCount);
        
        return stats;
    }

    public void clearAll() {
        users.clear();
        departments.clear();
        storage.clear(STORAGE_KEY_USERS);
        storage.clear(STORAGE_KEY_DEPARTMENTS);
        updateSyncTime();
        log.info("[OrgWeb] Cleared all organization data");
    }

    public void resetToDefault() {
        clearAll();
        initDefaultOrganization();
        log.info("[OrgWeb] Reset to default organization");
    }
}
