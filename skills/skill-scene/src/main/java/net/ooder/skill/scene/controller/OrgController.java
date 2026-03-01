package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.dto.OrgUserDTO;
import net.ooder.skill.scene.dto.OrgDepartmentDTO;
import net.ooder.skill.scene.adapter.OrgWebAdapter;
import net.ooder.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/org")
public class OrgController {

    private static final Logger log = LoggerFactory.getLogger(OrgController.class);

    @Autowired
    private OrgWebAdapter orgWebAdapter;

    @GetMapping("/users")
    public ResultModel<List<OrgUserDTO>> listUsers(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String role) {
        
        log.info("[listUsers] request start, departmentId: {}, role: {}", departmentId, role);
        
        List<OrgUserDTO> users;
        if (departmentId != null && !departmentId.isEmpty()) {
            users = orgWebAdapter.getUsersByDepartment(departmentId);
        } else if (role != null && !role.isEmpty()) {
            users = orgWebAdapter.getUsersByRole(role);
        } else {
            users = orgWebAdapter.getAllUsers();
        }
        
        return ResultModel.success(users);
    }

    @GetMapping("/users/{userId}")
    public ResultModel<OrgUserDTO> getUser(@PathVariable String userId) {
        OrgUserDTO user = orgWebAdapter.getUser(userId);
        if (user != null) {
            return ResultModel.success(user);
        }
        return ResultModel.error(404, "User not found: " + userId);
    }

    @GetMapping("/departments")
    public ResultModel<List<OrgDepartmentDTO>> listDepartments() {
        log.info("[listDepartments] request start");
        List<OrgDepartmentDTO> departments = orgWebAdapter.getAllDepartments();
        return ResultModel.success(departments);
    }

    @GetMapping("/departments/{departmentId}")
    public ResultModel<OrgDepartmentDTO> getDepartment(@PathVariable String departmentId) {
        OrgDepartmentDTO department = orgWebAdapter.getDepartment(departmentId);
        if (department != null) {
            return ResultModel.success(department);
        }
        return ResultModel.error(404, "Department not found: " + departmentId);
    }

    @GetMapping("/departments/{departmentId}/members")
    public ResultModel<List<OrgUserDTO>> getDepartmentMembers(@PathVariable String departmentId) {
        List<OrgUserDTO> members = orgWebAdapter.getDepartmentMembers(departmentId);
        return ResultModel.success(members);
    }

    @GetMapping("/departments/{departmentId}/manager")
    public ResultModel<OrgUserDTO> getDepartmentManager(@PathVariable String departmentId) {
        OrgUserDTO manager = orgWebAdapter.getDepartmentManager(departmentId);
        if (manager != null) {
            return ResultModel.success(manager);
        }
        return ResultModel.error(404, "Manager not found for department: " + departmentId);
    }

    @GetMapping("/tree")
    public ResultModel<List<Map<String, Object>>> getOrgTree() {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        List<OrgDepartmentDTO> departments = orgWebAdapter.getAllDepartments();
        
        for (OrgDepartmentDTO dept : departments) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", dept.getDepartmentId());
            node.put("name", dept.getName());
            node.put("type", "department");
            node.put("parentId", dept.getParentId());
            node.put("managerId", dept.getManagerId());
            
            List<Map<String, Object>> children = new ArrayList<>();
            List<OrgUserDTO> members = orgWebAdapter.getDepartmentMembers(dept.getDepartmentId());
            for (OrgUserDTO user : members) {
                Map<String, Object> userNode = new HashMap<>();
                userNode.put("id", user.getUserId());
                userNode.put("name", user.getName());
                userNode.put("type", "user");
                userNode.put("role", user.getRole());
                userNode.put("title", user.getTitle());
                userNode.put("email", user.getEmail());
                userNode.put("parentId", dept.getDepartmentId());
                children.add(userNode);
            }
            node.put("children", children);
            
            tree.add(node);
        }
        
        return ResultModel.success(tree);
    }

    @GetMapping("/roles")
    public ResultModel<List<Map<String, Object>>> listRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();
        
        String[][] roleData = {
            {"manager", "管理者", "场景管理者，拥有完整管理权限"},
            {"employee", "员工", "普通员工，参与场景执行"},
            {"hr", "HR", "人力资源，管理人事相关"},
            {"admin", "管理员", "系统管理员，拥有所有权限"},
            {"llm-assistant", "LLM助手", "AI分析助手"},
            {"coordinator", "协调Agent", "任务协调Agent"}
        };
        
        for (String[] data : roleData) {
            Map<String, Object> role = new HashMap<>();
            role.put("id", data[0]);
            role.put("name", data[1]);
            role.put("description", data[2]);
            roles.add(role);
        }
        
        return ResultModel.success(roles);
    }
}
