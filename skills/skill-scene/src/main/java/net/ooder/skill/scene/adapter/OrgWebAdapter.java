package net.ooder.skill.scene.adapter;

import net.ooder.skill.scene.dto.OrgUserDTO;
import net.ooder.skill.scene.dto.OrgDepartmentDTO;
import net.ooder.skill.scene.storage.JsonStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrgWebAdapter {

    private static final Logger log = LoggerFactory.getLogger(OrgWebAdapter.class);

    @Value("${org-web.enabled:false}")
    private boolean enabled;

    @Value("${org-web.base-url:}")
    private String baseUrl;

    @Value("${org-web.api-key:}")
    private String apiKey;

    @Autowired
    private JsonStorageService storage;

    private final Map<String, OrgUserDTO> users = new ConcurrentHashMap<>();
    private final Map<String, OrgDepartmentDTO> departments = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (enabled && baseUrl != null && !baseUrl.isEmpty()) {
            log.info("OrgWebAdapter initialized with URL: {}", baseUrl);
            syncFromOrgWeb();
        } else {
            log.info("OrgWebAdapter using fallback JSON storage");
            loadFromStorage();
            initDefaultData();
        }
    }

    private void syncFromOrgWeb() {
        log.info("Syncing data from org-web: {}", baseUrl);
    }

    @SuppressWarnings("unchecked")
    private void loadFromStorage() {
        Map<String, OrgUserDTO> storedUsers = storage.getAll("users");
        if (storedUsers != null && !storedUsers.isEmpty()) {
            users.putAll(storedUsers);
            log.info("Loaded {} users from storage", users.size());
        }

        Map<String, OrgDepartmentDTO> storedDepts = storage.getAll("departments");
        if (storedDepts != null && !storedDepts.isEmpty()) {
            departments.putAll(storedDepts);
            log.info("Loaded {} departments from storage", departments.size());
        }
    }

    private void initDefaultData() {
        if (users.isEmpty()) {
            OrgUserDTO manager = new OrgUserDTO();
            manager.setUserId("user-manager-001");
            manager.setName("张经理");
            manager.setEmail("zhang.manager@example.com");
            manager.setDepartmentId("dept-rd");
            manager.setRole("manager");
            manager.setTitle("研发经理");
            manager.setCreateTime(System.currentTimeMillis());
            manager.setActive(true);
            users.put(manager.getUserId(), manager);

            OrgUserDTO employee1 = new OrgUserDTO();
            employee1.setUserId("user-employee-001");
            employee1.setName("李员工");
            employee1.setEmail("li.employee@example.com");
            employee1.setDepartmentId("dept-rd");
            employee1.setRole("employee");
            employee1.setTitle("高级工程师");
            employee1.setCreateTime(System.currentTimeMillis());
            employee1.setActive(true);
            users.put(employee1.getUserId(), employee1);

            OrgUserDTO employee2 = new OrgUserDTO();
            employee2.setUserId("user-employee-002");
            employee2.setName("王员工");
            employee2.setEmail("wang.employee@example.com");
            employee2.setDepartmentId("dept-rd");
            employee2.setRole("employee");
            employee2.setTitle("工程师");
            employee2.setCreateTime(System.currentTimeMillis());
            employee2.setActive(true);
            users.put(employee2.getUserId(), employee2);

            OrgUserDTO employee3 = new OrgUserDTO();
            employee3.setUserId("user-employee-003");
            employee3.setName("赵员工");
            employee3.setEmail("zhao.employee@example.com");
            employee3.setDepartmentId("dept-rd");
            employee3.setRole("employee");
            employee3.setTitle("工程师");
            employee3.setCreateTime(System.currentTimeMillis());
            employee3.setActive(true);
            users.put(employee3.getUserId(), employee3);

            OrgUserDTO hr = new OrgUserDTO();
            hr.setUserId("user-hr-001");
            hr.setName("刘HR");
            hr.setEmail("liu.hr@example.com");
            hr.setDepartmentId("dept-hr");
            hr.setRole("hr");
            hr.setTitle("HR经理");
            hr.setCreateTime(System.currentTimeMillis());
            hr.setActive(true);
            users.put(hr.getUserId(), hr);

            OrgUserDTO admin = new OrgUserDTO();
            admin.setUserId("user-admin-001");
            admin.setName("系统管理员");
            admin.setEmail("admin@example.com");
            admin.setDepartmentId("dept-it");
            admin.setRole("admin");
            admin.setTitle("系统管理员");
            admin.setCreateTime(System.currentTimeMillis());
            admin.setActive(true);
            users.put(admin.getUserId(), admin);

            OrgUserDTO installer = new OrgUserDTO();
            installer.setUserId("user-installer-001");
            installer.setName("安装者");
            installer.setEmail("installer@example.com");
            installer.setDepartmentId("dept-it");
            installer.setRole("installer");
            installer.setTitle("系统安装者");
            installer.setCreateTime(System.currentTimeMillis());
            installer.setActive(true);
            users.put(installer.getUserId(), installer);

            OrgUserDTO leader = new OrgUserDTO();
            leader.setUserId("user-leader-001");
            leader.setName("主导者");
            leader.setEmail("leader@example.com");
            leader.setDepartmentId("dept-rd");
            leader.setRole("leader");
            leader.setTitle("项目主导");
            leader.setCreateTime(System.currentTimeMillis());
            leader.setActive(true);
            users.put(leader.getUserId(), leader);

            OrgUserDTO collaborator = new OrgUserDTO();
            collaborator.setUserId("user-collaborator-001");
            collaborator.setName("协作者");
            collaborator.setEmail("collaborator@example.com");
            collaborator.setDepartmentId("dept-rd");
            collaborator.setRole("collaborator");
            collaborator.setTitle("项目协作");
            collaborator.setCreateTime(System.currentTimeMillis());
            collaborator.setActive(true);
            users.put(collaborator.getUserId(), collaborator);

            users.forEach((id, user) -> storage.put("users", id, user));
            log.info("Initialized {} default users", users.size());
        }

        if (departments.isEmpty()) {
            OrgDepartmentDTO rd = new OrgDepartmentDTO();
            rd.setDepartmentId("dept-rd");
            rd.setName("研发部");
            rd.setDescription("负责产品研发和技术创新");
            rd.setParentId(null);
            rd.setManagerId("user-manager-001");
            rd.setMemberIds(Arrays.asList("user-manager-001", "user-employee-001", "user-employee-002", "user-employee-003", "user-leader-001", "user-collaborator-001"));
            rd.setCreateTime(System.currentTimeMillis());
            departments.put(rd.getDepartmentId(), rd);

            OrgDepartmentDTO hr = new OrgDepartmentDTO();
            hr.setDepartmentId("dept-hr");
            hr.setName("人力资源部");
            hr.setDescription("负责人才招聘和员工管理");
            hr.setParentId(null);
            hr.setManagerId("user-hr-001");
            hr.setMemberIds(Arrays.asList("user-hr-001"));
            hr.setCreateTime(System.currentTimeMillis());
            departments.put(hr.getDepartmentId(), hr);

            OrgDepartmentDTO it = new OrgDepartmentDTO();
            it.setDepartmentId("dept-it");
            it.setName("信息技术部");
            it.setDescription("负责系统运维和技术支持");
            it.setParentId(null);
            it.setManagerId("user-admin-001");
            it.setMemberIds(Arrays.asList("user-admin-001", "user-installer-001"));
            it.setCreateTime(System.currentTimeMillis());
            departments.put(it.getDepartmentId(), it);

            departments.forEach((id, dept) -> storage.put("departments", id, dept));
            log.info("Initialized {} default departments", departments.size());
        }
    }

    public boolean isEnabled() {
        return enabled;
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
        users.put(user.getUserId(), user);
        storage.put("users", user.getUserId(), user);
        log.info("Added user: {}", user.getUserId());
    }

    public void updateUser(OrgUserDTO user) {
        users.put(user.getUserId(), user);
        storage.put("users", user.getUserId(), user);
        log.info("Updated user: {}", user.getUserId());
    }

    public boolean deleteUser(String userId) {
        OrgUserDTO removed = users.remove(userId);
        if (removed != null) {
            storage.remove("users", userId);
            log.info("Deleted user: {}", userId);
            return true;
        }
        return false;
    }

    public void addDepartment(OrgDepartmentDTO department) {
        departments.put(department.getDepartmentId(), department);
        storage.put("departments", department.getDepartmentId(), department);
        log.info("Added department: {}", department.getDepartmentId());
    }

    public void updateDepartment(OrgDepartmentDTO department) {
        departments.put(department.getDepartmentId(), department);
        storage.put("departments", department.getDepartmentId(), department);
        log.info("Updated department: {}", department.getDepartmentId());
    }

    public boolean deleteDepartment(String departmentId) {
        OrgDepartmentDTO removed = departments.remove(departmentId);
        if (removed != null) {
            storage.remove("departments", departmentId);
            log.info("Deleted department: {}", departmentId);
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
            
            storage.put("departments", departmentId, dept);
            storage.put("users", userId, user);
            log.info("Added member {} to department {}", userId, departmentId);
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
            
            storage.put("departments", departmentId, dept);
            storage.put("users", userId, user);
            log.info("Removed member {} from department {}", userId, departmentId);
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
        return 0;
    }
    
    public int getUserCapabilityCount(String userId) {
        return 0;
    }
}
