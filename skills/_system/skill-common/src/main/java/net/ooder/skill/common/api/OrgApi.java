package net.ooder.skill.common.api;

import net.ooder.skill.common.model.OrgUser;
import net.ooder.skill.common.model.OrgDepartment;
import net.ooder.skill.common.model.ResultModel;
import net.ooder.skill.common.service.OrgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/org")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrgApi {

    private static final Logger log = LoggerFactory.getLogger(OrgApi.class);

    @Autowired
    private OrgService orgService;

    @GetMapping("/users/current")
    public ResultModel<OrgUser> getCurrentUser() {
        OrgUser user = orgService.getCurrentUser();
        if (user != null) {
            return ResultModel.success(user);
        }
        return ResultModel.success(createDefaultUser());
    }

    private OrgUser createDefaultUser() {
        OrgUser user = new OrgUser();
        user.setUserId("default");
        user.setName("默认用户");
        user.setRole("user");
        user.setActive(true);
        return user;
    }

    @GetMapping("/users")
    public ResultModel<List<OrgUser>> listUsers(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String role) {
        
        log.info("[listUsers] request start, departmentId: {}, role: {}", departmentId, role);
        
        List<OrgUser> users;
        if (departmentId != null && !departmentId.isEmpty()) {
            users = orgService.getUsersByDepartment(departmentId);
        } else if (role != null && !role.isEmpty()) {
            users = orgService.getUsersByRole(role);
        } else {
            users = orgService.getAllUsers();
        }
        
        return ResultModel.success(users);
    }

    @GetMapping("/users/{userId}")
    public ResultModel<OrgUser> getUser(@PathVariable String userId) {
        OrgUser user = orgService.getUser(userId);
        if (user != null) {
            return ResultModel.success(user);
        }
        return ResultModel.error(404, "User not found: " + userId);
    }

    @PostMapping("/users")
    public ResultModel<OrgUser> createUser(@RequestBody OrgUser user) {
        log.info("[createUser] request start, userId: {}", user.getUserId());
        
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId("user-" + System.currentTimeMillis());
        }
        if (user.getCreateTime() == 0) {
            user.setCreateTime(System.currentTimeMillis());
        }
        user.setActive(true);
        
        orgService.addUser(user);
        return ResultModel.success(user);
    }

    @PutMapping("/users/{userId}")
    public ResultModel<OrgUser> updateUser(@PathVariable String userId, @RequestBody OrgUser user) {
        log.info("[updateUser] request start, userId: {}", userId);
        
        OrgUser existing = orgService.getUser(userId);
        if (existing == null) {
            return ResultModel.error(404, "User not found: " + userId);
        }
        
        user.setUserId(userId);
        user.setCreateTime(existing.getCreateTime());
        user.setActive(existing.isActive());
        
        orgService.updateUser(user);
        return ResultModel.success(user);
    }

    @DeleteMapping("/users/{userId}")
    public ResultModel<Boolean> deleteUser(@PathVariable String userId) {
        log.info("[deleteUser] request start, userId: {}", userId);
        
        boolean result = orgService.deleteUser(userId);
        if (result) {
            return ResultModel.success(true);
        }
        return ResultModel.error(404, "User not found: " + userId);
    }

    @GetMapping("/departments")
    public ResultModel<List<OrgDepartment>> listDepartments() {
        log.info("[listDepartments] request start");
        List<OrgDepartment> departments = orgService.getAllDepartments();
        return ResultModel.success(departments);
    }

    @GetMapping("/departments/{departmentId}")
    public ResultModel<OrgDepartment> getDepartment(@PathVariable String departmentId) {
        OrgDepartment department = orgService.getDepartment(departmentId);
        if (department != null) {
            return ResultModel.success(department);
        }
        return ResultModel.error(404, "Department not found: " + departmentId);
    }

    @PostMapping("/departments")
    public ResultModel<OrgDepartment> createDepartment(@RequestBody OrgDepartment department) {
        log.info("[createDepartment] request start, departmentId: {}", department.getDepartmentId());
        
        if (department.getDepartmentId() == null || department.getDepartmentId().isEmpty()) {
            department.setDepartmentId("dept-" + System.currentTimeMillis());
        }
        if (department.getCreateTime() == 0) {
            department.setCreateTime(System.currentTimeMillis());
        }
        
        orgService.addDepartment(department);
        return ResultModel.success(department);
    }

    @PutMapping("/departments/{departmentId}")
    public ResultModel<OrgDepartment> updateDepartment(@PathVariable String departmentId, @RequestBody OrgDepartment department) {
        log.info("[updateDepartment] request start, departmentId: {}", departmentId);
        
        OrgDepartment existing = orgService.getDepartment(departmentId);
        if (existing == null) {
            return ResultModel.error(404, "Department not found: " + departmentId);
        }
        
        department.setDepartmentId(departmentId);
        department.setCreateTime(existing.getCreateTime());
        
        orgService.updateDepartment(department);
        return ResultModel.success(department);
    }

    @DeleteMapping("/departments/{departmentId}")
    public ResultModel<Boolean> deleteDepartment(@PathVariable String departmentId) {
        log.info("[deleteDepartment] request start, departmentId: {}", departmentId);
        
        boolean result = orgService.deleteDepartment(departmentId);
        if (result) {
            return ResultModel.success(true);
        }
        return ResultModel.error(404, "Department not found: " + departmentId);
    }

    @GetMapping("/departments/{departmentId}/members")
    public ResultModel<List<OrgUser>> getDepartmentMembers(@PathVariable String departmentId) {
        List<OrgUser> members = orgService.getDepartmentMembers(departmentId);
        return ResultModel.success(members);
    }

    @GetMapping("/departments/{departmentId}/manager")
    public ResultModel<OrgUser> getDepartmentManager(@PathVariable String departmentId) {
        OrgUser manager = orgService.getDepartmentManager(departmentId);
        if (manager != null) {
            return ResultModel.success(manager);
        }
        return ResultModel.error(404, "Manager not found for department: " + departmentId);
    }

    @PostMapping("/departments/{departmentId}/members/{userId}")
    public ResultModel<Boolean> addMemberToDepartment(@PathVariable String departmentId, @PathVariable String userId) {
        log.info("[addMemberToDepartment] departmentId: {}, userId: {}", departmentId, userId);
        boolean result = orgService.addMemberToDepartment(departmentId, userId);
        return ResultModel.success(result);
    }

    @DeleteMapping("/departments/{departmentId}/members/{userId}")
    public ResultModel<Boolean> removeMemberFromDepartment(@PathVariable String departmentId, @PathVariable String userId) {
        log.info("[removeMemberFromDepartment] departmentId: {}, userId: {}", departmentId, userId);
        boolean result = orgService.removeMemberFromDepartment(departmentId, userId);
        return ResultModel.success(result);
    }

    @GetMapping("/tree")
    public ResultModel<List<Map<String, Object>>> getOrgTree() {
        List<Map<String, Object>> tree = orgService.getOrgTree();
        return ResultModel.success(tree);
    }

    @GetMapping("/roles")
    public ResultModel<List<Map<String, Object>>> listRoles() {
        List<Map<String, Object>> roles = orgService.getAvailableRoles();
        return ResultModel.success(roles);
    }

    @PutMapping("/users/{userId}/role")
    public ResultModel<OrgUser> updateUserRole(@PathVariable String userId, @RequestParam String role) {
        log.info("[updateUserRole] userId: {}, role: {}", userId, role);
        
        OrgUser user = orgService.getUser(userId);
        if (user == null) {
            return ResultModel.error(404, "User not found: " + userId);
        }
        
        if (role == null || role.isEmpty()) {
            return ResultModel.error(400, "Role is required");
        }
        
        user.setRole(role);
        orgService.updateUser(user);
        
        return ResultModel.success(user);
    }
}
