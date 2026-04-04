package net.ooder.skill.org.controller;

import net.ooder.skill.org.dto.*;
import net.ooder.skill.org.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/org")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class OrgController {

    private static final Logger log = LoggerFactory.getLogger(OrgController.class);

    @GetMapping("/users")
    public ResultModel<List<UserDTO>> getUsers() {
        log.info("[OrgController] Get users");
        List<UserDTO> users = new ArrayList<>();
        
        UserDTO user1 = new UserDTO();
        user1.setId("user-001");
        user1.setName("Admin");
        user1.setEmail("admin@ooder.net");
        user1.setRole("admin");
        user1.setDepartment("IT Department");
        users.add(user1);
        
        UserDTO user2 = new UserDTO();
        user2.setId("user-002");
        user2.setName("User");
        user2.setEmail("user@ooder.net");
        user2.setRole("user");
        user2.setDepartment("IT Department");
        users.add(user2);
        
        return ResultModel.success(users);
    }

    @GetMapping("/users/current")
    public ResultModel<UserDTO> getCurrentUser() {
        log.info("[OrgController] Get current user");
        UserDTO user = new UserDTO();
        user.setId("user-001");
        user.setName("Admin");
        user.setEmail("admin@ooder.net");
        user.setRole("admin");
        return ResultModel.success(user);
    }

    @GetMapping("/users/current/stats")
    public ResultModel<UserStatsDTO> getCurrentUserStats() {
        log.info("[OrgController] Get current user stats");
        UserStatsDTO stats = new UserStatsDTO();
        stats.setTotalTasks(10);
        stats.setCompletedTasks(7);
        stats.setPendingTasks(3);
        return ResultModel.success(stats);
    }

    @GetMapping("/roles")
    public ResultModel<List<RoleDTO>> getRoles() {
        log.info("[OrgController] Get roles");
        List<RoleDTO> roles = new ArrayList<>();
        
        RoleDTO role1 = new RoleDTO();
        role1.setId("admin");
        role1.setName("Administrator");
        roles.add(role1);
        
        RoleDTO role2 = new RoleDTO();
        role2.setId("user");
        role2.setName("User");
        roles.add(role2);
        
        return ResultModel.success(roles);
    }

    @GetMapping("/departments")
    public ResultModel<List<DepartmentDTO>> getDepartments() {
        log.info("[OrgController] Get departments");
        List<DepartmentDTO> depts = new ArrayList<>();
        
        DepartmentDTO dept1 = new DepartmentDTO();
        dept1.setId("dept-001");
        dept1.setName("IT Department");
        depts.add(dept1);
        
        return ResultModel.success(depts);
    }

    @GetMapping("/tree")
    public ResultModel<OrgTreeDTO> getOrgTree() {
        log.info("[OrgController] Get org tree");
        OrgTreeDTO tree = new OrgTreeDTO();
        tree.setId("root");
        tree.setName("Ooder Org");
        tree.setChildren(new ArrayList<>());
        return ResultModel.success(tree);
    }
}
