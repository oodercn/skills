package net.ooder.bpm.designer.function.tools;

import net.ooder.bpm.designer.function.DesignerFunctionDefinition;
import net.ooder.bpm.designer.function.DesignerFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OrganizationFunctionTools {
    
    private static final Logger log = LoggerFactory.getLogger(OrganizationFunctionTools.class);
    
    @Autowired
    private DesignerFunctionRegistry functionRegistry;
    
    @PostConstruct
    public void init() {
        registerFunctions();
    }
    
    private void registerFunctions() {
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_organization_tree")
            .description("获取组织架构树结构，返回部门层级关系")
            .category(DesignerFunctionDefinition.FunctionCategory.ORGANIZATION)
            .addParameter("rootOrgId", "string", "根组织ID，不传则返回完整组织树", false)
            .addParameter("includeUsers", "boolean", "是否包含用户信息", false)
            .handler(this::handleGetOrganizationTree)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_users_by_role")
            .description("根据角色ID获取用户列表")
            .category(DesignerFunctionDefinition.FunctionCategory.ORGANIZATION)
            .addParameter("roleId", "string", "角色ID", true)
            .addParameter("includeSubRoles", "boolean", "是否包含子角色用户", false)
            .addParameter("deptId", "string", "限定部门ID", false)
            .handler(this::handleGetUsersByRole)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_user_info")
            .description("获取用户详细信息")
            .category(DesignerFunctionDefinition.FunctionCategory.ORGANIZATION)
            .addParameter("userId", "string", "用户ID", true)
            .handler(this::handleGetUserInfo)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("search_users")
            .description("语义搜索用户，支持按姓名、部门、角色等条件搜索")
            .category(DesignerFunctionDefinition.FunctionCategory.ORGANIZATION)
            .addParameter("query", "string", "搜索关键词", true)
            .addParameter("deptId", "string", "限定部门ID", false)
            .addParameter("roleId", "string", "限定角色ID", false)
            .addParameter("status", "string", "用户状态：ACTIVE, INACTIVE", false)
            .addParameter("limit", "integer", "返回数量限制，默认10", false)
            .handler(this::handleSearchUsers)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_department_members")
            .description("获取部门成员列表")
            .category(DesignerFunctionDefinition.FunctionCategory.ORGANIZATION)
            .addParameter("deptId", "string", "部门ID", true)
            .addParameter("recursive", "boolean", "是否递归获取子部门成员", false)
            .addParameter("includeLeader", "boolean", "是否包含部门负责人", false)
            .handler(this::handleGetDepartmentMembers)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_user_capabilities")
            .description("获取用户具备的能力列表")
            .category(DesignerFunctionDefinition.FunctionCategory.ORGANIZATION)
            .addParameter("userId", "string", "用户ID", true)
            .handler(this::handleGetUserCapabilities)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_department_leader")
            .description("获取部门负责人")
            .category(DesignerFunctionDefinition.FunctionCategory.ORGANIZATION)
            .addParameter("deptId", "string", "部门ID", true)
            .handler(this::handleGetDepartmentLeader)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("list_roles")
            .description("列出所有角色")
            .category(DesignerFunctionDefinition.FunctionCategory.ORGANIZATION)
            .addParameter("category", "string", "角色分类", false)
            .handler(this::handleListRoles)
            .build());
        
        log.info("Registered {} organization functions", 8);
    }
    
    private Object handleGetOrganizationTree(Map<String, Object> args) {
        String rootOrgId = (String) args.get("rootOrgId");
        boolean includeUsers = Boolean.TRUE.equals(args.get("includeUsers"));
        
        List<Map<String, Object>> orgTree = buildMockOrganizationTree(rootOrgId, includeUsers);
        
        return Map.of(
            "success", true,
            "data", orgTree,
            "count", orgTree.size()
        );
    }
    
    private Object handleGetUsersByRole(Map<String, Object> args) {
        String roleId = (String) args.get("roleId");
        boolean includeSubRoles = Boolean.TRUE.equals(args.get("includeSubRoles"));
        String deptId = (String) args.get("deptId");
        
        List<Map<String, Object>> users = buildMockUsersByRole(roleId, deptId);
        
        return Map.of(
            "success", true,
            "data", users,
            "roleId", roleId,
            "count", users.size()
        );
    }
    
    private Object handleGetUserInfo(Map<String, Object> args) {
        String userId = (String) args.get("userId");
        
        Map<String, Object> user = buildMockUserInfo(userId);
        
        return Map.of(
            "success", true,
            "data", user
        );
    }
    
    private Object handleSearchUsers(Map<String, Object> args) {
        String query = (String) args.get("query");
        String deptId = (String) args.get("deptId");
        String roleId = (String) args.get("roleId");
        Integer limit = args.get("limit") != null ? ((Number) args.get("limit")).intValue() : 10;
        
        List<Map<String, Object>> users = buildMockSearchUsers(query, deptId, roleId, limit);
        
        return Map.of(
            "success", true,
            "data", users,
            "query", query,
            "count", users.size()
        );
    }
    
    private Object handleGetDepartmentMembers(Map<String, Object> args) {
        String deptId = (String) args.get("deptId");
        boolean recursive = Boolean.TRUE.equals(args.get("recursive"));
        boolean includeLeader = Boolean.TRUE.equals(args.get("includeLeader"));
        
        List<Map<String, Object>> members = buildMockDepartmentMembers(deptId, recursive, includeLeader);
        
        return Map.of(
            "success", true,
            "data", members,
            "deptId", deptId,
            "count", members.size()
        );
    }
    
    private Object handleGetUserCapabilities(Map<String, Object> args) {
        String userId = (String) args.get("userId");
        
        List<Map<String, Object>> capabilities = buildMockUserCapabilities(userId);
        
        return Map.of(
            "success", true,
            "data", capabilities,
            "userId", userId,
            "count", capabilities.size()
        );
    }
    
    private Object handleGetDepartmentLeader(Map<String, Object> args) {
        String deptId = (String) args.get("deptId");
        
        Map<String, Object> leader = buildMockDepartmentLeader(deptId);
        
        return Map.of(
            "success", true,
            "data", leader,
            "deptId", deptId
        );
    }
    
    private Object handleListRoles(Map<String, Object> args) {
        String category = (String) args.get("category");
        
        List<Map<String, Object>> roles = buildMockRoles(category);
        
        return Map.of(
            "success", true,
            "data", roles,
            "count", roles.size()
        );
    }
    
    private List<Map<String, Object>> buildMockOrganizationTree(String rootOrgId, boolean includeUsers) {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        tree.add(Map.of(
            "orgId", "org-001",
            "orgName", "总公司",
            "orgCode", "HQ",
            "parentId", null,
            "level", 1,
            "children", List.of(
                Map.of(
                    "orgId", "org-002",
                    "orgName", "人力资源部",
                    "orgCode", "HR",
                    "parentId", "org-001",
                    "level", 2,
                    "children", new ArrayList<>()
                ),
                Map.of(
                    "orgId", "org-003",
                    "orgName", "技术部",
                    "orgCode", "TECH",
                    "parentId", "org-001",
                    "level", 2,
                    "children", List.of(
                        Map.of(
                            "orgId", "org-004",
                            "orgName", "研发一组",
                            "orgCode", "DEV1",
                            "parentId", "org-003",
                            "level", 3,
                            "children", new ArrayList<>()
                        )
                    )
                ),
                Map.of(
                    "orgId", "org-005",
                    "orgName", "财务部",
                    "orgCode", "FIN",
                    "parentId", "org-001",
                    "level", 2,
                    "children", new ArrayList<>()
                )
            )
        ));
        
        return tree;
    }
    
    private List<Map<String, Object>> buildMockUsersByRole(String roleId, String deptId) {
        List<Map<String, Object>> users = new ArrayList<>();
        
        Map<String, List<Map<String, Object>>> roleUsers = new HashMap<>();
        roleUsers.put("hr_specialist", List.of(
            Map.of("userId", "user-001", "userName", "张三", "deptId", "org-002", "deptName", "人力资源部", "email", "zhangsan@company.com"),
            Map.of("userId", "user-002", "userName", "李四", "deptId", "org-002", "deptName", "人力资源部", "email", "lisi@company.com")
        ));
        roleUsers.put("hr_manager", List.of(
            Map.of("userId", "user-003", "userName", "王五", "deptId", "org-002", "deptName", "人力资源部", "email", "wangwu@company.com")
        ));
        roleUsers.put("tech_leader", List.of(
            Map.of("userId", "user-004", "userName", "赵六", "deptId", "org-003", "deptName", "技术部", "email", "zhaoliu@company.com")
        ));
        roleUsers.put("developer", List.of(
            Map.of("userId", "user-005", "userName", "钱七", "deptId", "org-004", "deptName", "研发一组", "email", "qianqi@company.com"),
            Map.of("userId", "user-006", "userName", "孙八", "deptId", "org-004", "deptName", "研发一组", "email", "sunba@company.com")
        ));
        roleUsers.put("finance_manager", List.of(
            Map.of("userId", "user-007", "userName", "周九", "deptId", "org-005", "deptName", "财务部", "email", "zhoujiu@company.com")
        ));
        
        List<Map<String, Object>> matchedUsers = roleUsers.getOrDefault(roleId, new ArrayList<>());
        
        if (deptId != null) {
            return matchedUsers.stream()
                .filter(u -> deptId.equals(u.get("deptId")))
                .collect(Collectors.toList());
        }
        
        return matchedUsers;
    }
    
    private Map<String, Object> buildMockUserInfo(String userId) {
        Map<String, Map<String, Object>> userInfos = new HashMap<>();
        userInfos.put("user-001", Map.of(
            "userId", "user-001",
            "userName", "张三",
            "email", "zhangsan@company.com",
            "phone", "13800138001",
            "deptId", "org-002",
            "deptName", "人力资源部",
            "roles", List.of(
                Map.of("roleId", "hr_specialist", "roleName", "HR专员")
            ),
            "status", "ACTIVE",
            "joinDate", "2020-01-15"
        ));
        userInfos.put("user-003", Map.of(
            "userId", "user-003",
            "userName", "王五",
            "email", "wangwu@company.com",
            "phone", "13800138003",
            "deptId", "org-002",
            "deptName", "人力资源部",
            "roles", List.of(
                Map.of("roleId", "hr_manager", "roleName", "HR经理")
            ),
            "status", "ACTIVE",
            "joinDate", "2018-06-20"
        ));
        
        return userInfos.getOrDefault(userId, Map.of(
            "userId", userId,
            "userName", "未知用户",
            "status", "NOT_FOUND"
        ));
    }
    
    private List<Map<String, Object>> buildMockSearchUsers(String query, String deptId, String roleId, int limit) {
        List<Map<String, Object>> allUsers = new ArrayList<>();
        allUsers.add(Map.of("userId", "user-001", "userName", "张三", "deptId", "org-002", "deptName", "人力资源部", "matchScore", 0.95));
        allUsers.add(Map.of("userId", "user-002", "userName", "李四", "deptId", "org-002", "deptName", "人力资源部", "matchScore", 0.85));
        allUsers.add(Map.of("userId", "user-003", "userName", "王五", "deptId", "org-002", "deptName", "人力资源部", "matchScore", 0.80));
        allUsers.add(Map.of("userId", "user-004", "userName", "赵六", "deptId", "org-003", "deptName", "技术部", "matchScore", 0.75));
        allUsers.add(Map.of("userId", "user-005", "userName", "钱七", "deptId", "org-004", "deptName", "研发一组", "matchScore", 0.70));
        
        return allUsers.stream()
            .filter(u -> deptId == null || deptId.equals(u.get("deptId")))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> buildMockDepartmentMembers(String deptId, boolean recursive, boolean includeLeader) {
        List<Map<String, Object>> members = new ArrayList<>();
        
        members.add(Map.of(
            "userId", "user-001",
            "userName", "张三",
            "position", "HR专员",
            "isLeader", false,
            "email", "zhangsan@company.com"
        ));
        members.add(Map.of(
            "userId", "user-002",
            "userName", "李四",
            "position", "HR专员",
            "isLeader", false,
            "email", "lisi@company.com"
        ));
        
        if (includeLeader) {
            members.add(Map.of(
                "userId", "user-003",
                "userName", "王五",
                "position", "HR经理",
                "isLeader", true,
                "email", "wangwu@company.com"
            ));
        }
        
        return members;
    }
    
    private List<Map<String, Object>> buildMockUserCapabilities(String userId) {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        capabilities.add(Map.of(
            "capId", "resume_screening",
            "capName", "简历筛选",
            "category", "HR",
            "proficiency", "EXPERT"
        ));
        capabilities.add(Map.of(
            "capId", "interview",
            "capName", "面试评估",
            "category", "HR",
            "proficiency", "ADVANCED"
        ));
        
        return capabilities;
    }
    
    private Map<String, Object> buildMockDepartmentLeader(String deptId) {
        return Map.of(
            "userId", "user-003",
            "userName", "王五",
            "position", "部门负责人",
            "email", "wangwu@company.com",
            "phone", "13800138003"
        );
    }
    
    private List<Map<String, Object>> buildMockRoles(String category) {
        List<Map<String, Object>> roles = new ArrayList<>();
        
        roles.add(Map.of("roleId", "hr_specialist", "roleName", "HR专员", "category", "HR", "description", "负责招聘、员工关系等HR日常工作"));
        roles.add(Map.of("roleId", "hr_manager", "roleName", "HR经理", "category", "HR", "description", "负责HR部门管理和决策"));
        roles.add(Map.of("roleId", "tech_leader", "roleName", "技术负责人", "category", "TECH", "description", "负责技术团队管理和架构决策"));
        roles.add(Map.of("roleId", "developer", "roleName", "开发工程师", "category", "TECH", "description", "负责软件开发和编码"));
        roles.add(Map.of("roleId", "finance_manager", "roleName", "财务经理", "category", "FIN", "description", "负责财务管理和预算审批"));
        
        if (category != null) {
            return roles.stream()
                .filter(r -> category.equalsIgnoreCase((String) r.get("category")))
                .collect(Collectors.toList());
        }
        
        return roles;
    }
}
