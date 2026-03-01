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
            rd.setMemberIds(Arrays.asList("user-manager-001", "user-employee-001", "user-employee-002", "user-employee-003"));
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

    public void addDepartment(OrgDepartmentDTO department) {
        departments.put(department.getDepartmentId(), department);
        storage.put("departments", department.getDepartmentId(), department);
        log.info("Added department: {}", department.getDepartmentId());
    }

    public boolean isUserInRole(String userId, String role) {
        OrgUserDTO user = users.get(userId);
        return user != null && role.equals(user.getRole());
    }

    public boolean isUserManager(String userId) {
        return isUserInRole(userId, "manager");
    }
}
