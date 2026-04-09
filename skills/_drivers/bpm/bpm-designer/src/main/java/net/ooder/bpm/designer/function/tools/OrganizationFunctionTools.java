package net.ooder.bpm.designer.function.tools;

import net.ooder.bpm.designer.datasource.DataSourceAdapter;
import net.ooder.bpm.designer.datasource.config.DataSourceConfig;
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
    
    private final DesignerFunctionRegistry functionRegistry;
    private final DataSourceAdapter dataSourceAdapter;
    private final DataSourceConfig dataSourceConfig;
    
    @Autowired
    public OrganizationFunctionTools(
            DesignerFunctionRegistry functionRegistry,
            DataSourceAdapter dataSourceAdapter,
            DataSourceConfig dataSourceConfig) {
        this.functionRegistry = functionRegistry;
        this.dataSourceAdapter = dataSourceAdapter;
        this.dataSourceConfig = dataSourceConfig;
    }
    
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
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> orgTree = dataSourceAdapter.getOrganizationTree(tenantId);
            return wrapResult(orgTree);
        }
        
        return buildMockOrganizationTree(rootOrgId, includeUsers);
    }
    
    private Object handleGetUsersByRole(Map<String, Object> args) {
        String roleId = (String) args.get("roleId");
        String deptId = (String) args.get("deptId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> users = dataSourceAdapter.getUsersByRole(tenantId, roleId);
            return wrapResult(users);
        }
        
        return buildMockUsersByRole(roleId, deptId);
    }
    
    private Object handleGetUserInfo(Map<String, Object> args) {
        String userId = (String) args.get("userId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            Map<String, Object> user = dataSourceAdapter.getUserInfo(tenantId, userId);
            return wrapResult(user);
        }
        
        return buildMockUserInfoResponse(userId);
    }
    
    private Object handleSearchUsers(Map<String, Object> args) {
        String query = (String) args.get("query");
        String deptId = (String) args.get("deptId");
        String roleId = (String) args.get("roleId");
        Integer limit = args.get("limit") != null ? ((Number) args.get("limit")).intValue() : 10;
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> users = dataSourceAdapter.searchUsers(tenantId, query);
            return wrapResult(users);
        }
        
        return buildMockSearchUsers(query, deptId, roleId, limit);
    }
    
    private Object handleGetDepartmentMembers(Map<String, Object> args) {
        String deptId = (String) args.get("deptId");
        boolean recursive = Boolean.TRUE.equals(args.get("recursive"));
        boolean includeLeader = Boolean.TRUE.equals(args.get("includeLeader"));
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> members = dataSourceAdapter.getDepartmentMembers(tenantId, deptId);
            return wrapResult(members);
        }
        
        return buildMockDepartmentMembers(deptId, recursive, includeLeader);
    }
    
    private Object handleGetUserCapabilities(Map<String, Object> args) {
        String userId = (String) args.get("userId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> capabilities = dataSourceAdapter.getUserCapabilities(tenantId, userId);
            return wrapResult(capabilities);
        }
        
        return buildMockUserCapabilities(userId);
    }
    
    private Object handleGetDepartmentLeader(Map<String, Object> args) {
        String deptId = (String) args.get("deptId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            Map<String, Object> leader = dataSourceAdapter.getDepartmentLeader(tenantId, deptId);
            return wrapResult(leader);
        }
        
        return buildMockDepartmentLeader(deptId);
    }
    
    private Object handleListRoles(Map<String, Object> args) {
        String category = (String) args.get("category");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> roles = dataSourceAdapter.listRoles(tenantId);
            return wrapResult(roles);
        }
        
        return buildMockRoles(category);
    }
    
    private Map<String, Object> wrapResult(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("source", "real");
        return result;
    }
    
    private List<Map<String, Object>> buildMockOrganizationTree(String rootOrgId, boolean includeUsers) {
        List<Map<String, Object>> tree = new ArrayList<>();
        
        Map<String, Object> hq = new HashMap<>();
        hq.put("orgId", "org-001");
        hq.put("orgName", "总公司");
        hq.put("orgCode", "HQ");
        hq.put("parentId", null);
        hq.put("level", 1);
        
        List<Map<String, Object>> hqChildren = new ArrayList<>();
        
        Map<String, Object> hr = new HashMap<>();
        hr.put("orgId", "org-002");
        hr.put("orgName", "人力资源部");
        hr.put("orgCode", "HR");
        hr.put("parentId", "org-001");
        hr.put("level", 2);
        hr.put("children", new ArrayList<>());
        hqChildren.add(hr);
        
        Map<String, Object> tech = new HashMap<>();
        tech.put("orgId", "org-003");
        tech.put("orgName", "技术部");
        tech.put("orgCode", "TECH");
        tech.put("parentId", "org-001");
        tech.put("level", 2);
        
        List<Map<String, Object>> techChildren = new ArrayList<>();
        Map<String, Object> dev1 = new HashMap<>();
        dev1.put("orgId", "org-004");
        dev1.put("orgName", "研发一组");
        dev1.put("orgCode", "DEV1");
        dev1.put("parentId", "org-003");
        dev1.put("level", 3);
        dev1.put("children", new ArrayList<>());
        techChildren.add(dev1);
        tech.put("children", techChildren);
        hqChildren.add(tech);
        
        Map<String, Object> finance = new HashMap<>();
        finance.put("orgId", "org-005");
        finance.put("orgName", "财务部");
        finance.put("orgCode", "FIN");
        finance.put("parentId", "org-001");
        finance.put("level", 2);
        finance.put("children", new ArrayList<>());
        hqChildren.add(finance);
        
        hq.put("children", hqChildren);
        tree.add(hq);
        
        return tree;
    }
    
    private List<Map<String, Object>> buildMockUsersByRole(String roleId, String deptId) {
        List<Map<String, Object>> users = new ArrayList<>();
        
        Map<String, List<Map<String, Object>>> roleUsers = new HashMap<>();
        
        List<Map<String, Object>> hrSpecialists = new ArrayList<>();
        hrSpecialists.add(createMockUser("user-001", "张三", "org-002", "人力资源部", "zhangsan@company.com", false));
        hrSpecialists.add(createMockUser("user-002", "李四", "org-002", "人力资源部", "lisi@company.com", false));
        hrSpecialists.add(createMockUser("user-003", "王五", "org-002", "人力资源部", "wangwu@company.com", true));
        roleUsers.put("hr_specialist", hrSpecialists);
        
        List<Map<String, Object>> hrManagers = new ArrayList<>();
        hrManagers.add(createMockUser("user-003", "王五", "org-002", "人力资源部", "wangwu@company.com", true));
        roleUsers.put("hr_manager", hrManagers);
        
        List<Map<String, Object>> techLeaders = new ArrayList<>();
        techLeaders.add(createMockUser("user-004", "赵六", "org-003", "技术部", "zhaoliu@company.com", true));
        roleUsers.put("tech_leader", techLeaders);
        
        List<Map<String, Object>> developers = new ArrayList<>();
        developers.add(createMockUser("user-005", "钱七", "org-004", "研发一组", "qianqi@company.com", false));
        developers.add(createMockUser("user-006", "孙八", "org-004", "研发一组", "sunba@company.com", false));
        roleUsers.put("developer", developers);
        
        List<Map<String, Object>> financeManagers = new ArrayList<>();
        financeManagers.add(createMockUser("user-007", "周九", "org-005", "财务部", "zhoujiu@company.com", true));
        roleUsers.put("finance_manager", financeManagers);
        
        return roleUsers.getOrDefault(roleId, users);
    }
    
    private Map<String, Object> createMockUser(String userId, String userName, String deptId, String deptName, String email, boolean isLeader) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("userName", userName);
        user.put("deptId", deptId);
        user.put("deptName", deptName);
        user.put("email", email);
        user.put("isLeader", isLeader);
        return user;
    }
    
    private Map<String, Object> buildMockUserInfoResponse(String userId) {
        Map<String, Map<String, Object>> userInfos = new HashMap<>();
        
        Map<String, Object> user1 = new HashMap<>();
        user1.put("userId", "user-001");
        user1.put("userName", "张三");
        user1.put("email", "zhangsan@company.com");
        user1.put("phone", "13800138001");
        user1.put("deptId", "org-002");
        user1.put("deptName", "人力资源部");
        user1.put("roles", List.of(
            Map.of("roleId", "hr_specialist", "roleName", "HR专员")
        ));
        user1.put("status", "ACTIVE");
        user1.put("joinDate", "2020-01-15");
        userInfos.put("user-001", user1);
        
        Map<String, Object> user2 = new HashMap<>();
        user2.put("userId", "user-002");
        user2.put("userName", "李四");
        user2.put("email", "lisi@company.com");
        user2.put("phone", "13800138002");
        user2.put("deptId", "org-002");
        user2.put("deptName", "人力资源部");
        user2.put("roles", List.of(
            Map.of("roleId", "hr_specialist", "roleName", "HR专员")
        ));
        user2.put("status", "ACTIVE");
        userInfos.put("user-002", user2);
        
        Map<String, Object> user3 = new HashMap<>();
        user3.put("userId", "user-003");
        user3.put("userName", "王五");
        user3.put("email", "wangwu@company.com");
        user3.put("phone", "13800138003");
        user3.put("deptId", "org-002");
        user3.put("deptName", "人力资源部");
        user3.put("roles", List.of(
            Map.of("roleId", "hr_manager", "roleName", "HR经理")
        ));
        user3.put("status", "ACTIVE");
        user3.put("joinDate", "2018-06-20");
        user3.put("isLeader", true);
        userInfos.put("user-003", user3);
        
        Map<String, Object> user4 = new HashMap<>();
        user4.put("userId", "user-004");
        user4.put("userName", "赵六");
        user4.put("email", "zhaoliu@company.com");
        user4.put("phone", "13800138004");
        user4.put("deptId", "org-003");
        user4.put("deptName", "技术部");
        user4.put("roles", List.of(
            Map.of("roleId", "tech_leader", "roleName", "技术负责人")
        ));
        user4.put("status", "ACTIVE");
        user4.put("isLeader", true);
        userInfos.put("user-004", user4);
        
        Map<String, Object> user5 = new HashMap<>();
        user5.put("userId", "user-005");
        user5.put("userName", "钱七");
        user5.put("email", "qianqi@company.com");
        user5.put("phone", "13800138005");
        user5.put("deptId", "org-004");
        user5.put("deptName", "研发一组");
        user5.put("roles", List.of(
            Map.of("roleId", "developer", "roleName", "开发工程师")
        ));
        user5.put("status", "ACTIVE");
        userInfos.put("user-005", user5);
        
        Map<String, Object> user6 = new HashMap<>();
        user6.put("userId", "user-006");
        user6.put("userName", "孙八");
        user6.put("email", "sunba@company.com");
        user6.put("phone", "13800138006");
        user6.put("deptId", "org-004");
        user6.put("deptName", "研发一组");
        user6.put("roles", List.of(
            Map.of("roleId", "developer", "roleName", "开发工程师")
        ));
        user6.put("status", "ACTIVE");
        userInfos.put("user-006", user6);
        
        Map<String, Object> user7 = new HashMap<>();
        user7.put("userId", "user-007");
        user7.put("userName", "周九");
        user7.put("email", "zhoujiu@company.com");
        user7.put("phone", "13800138007");
        user7.put("deptId", "org-005");
        user7.put("deptName", "财务部");
        user7.put("roles", List.of(
            Map.of("roleId", "finance_manager", "roleName", "财务经理")
        ));
        user7.put("status", "ACTIVE");
        user7.put("isLeader", true);
        userInfos.put("user-007", user7);
        
        return userInfos.getOrDefault(userId, Map.of(
            "userId", userId,
            "userName", "未知用户",
            "status", "NOT_FOUND"
        ));
    }
    
    private List<Map<String, Object>> buildMockSearchUsers(String query, String deptId, String roleId, int limit) {
        List<Map<String, Object>> allUsers = new ArrayList<>();
        
        allUsers.add(createSearchResultUser("user-001", "张三", "org-002", "人力资源部", "zhangsan@company.com", 0.95, false));
        allUsers.add(createSearchResultUser("user-002", "李四", "org-002", "人力资源部", "lisi@company.com", 0.85, false));
        allUsers.add(createSearchResultUser("user-003", "王五", "org-002", "人力资源部", "wangwu@company.com", 0.80, true));
        allUsers.add(createSearchResultUser("user-004", "赵六", "org-003", "技术部", "zhaoliu@company.com", 0.75, true));
        allUsers.add(createSearchResultUser("user-005", "钱七", "org-004", "研发一组", "qianqi@company.com", 0.70, false));
        allUsers.add(createSearchResultUser("user-006", "孙八", "org-004", "研发一组", "sunba@company.com", 0.65, false));
        allUsers.add(createSearchResultUser("user-007", "周九", "org-005", "财务部", "zhoujiu@company.com", 0.60, true));
        
        return allUsers.stream()
            .filter(u -> deptId == null || deptId.equals(u.get("deptId")))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> createSearchResultUser(String userId, String userName, String deptId, String deptName, String email, double matchScore, boolean isLeader) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("userName", userName);
        user.put("deptId", deptId);
        user.put("deptName", deptName);
        user.put("email", email);
        user.put("matchScore", matchScore);
        user.put("isLeader", isLeader);
        return user;
    }
    
    private List<Map<String, Object>> buildMockDepartmentMembers(String deptId, boolean recursive, boolean includeLeader) {
        List<Map<String, Object>> members = new ArrayList<>();
        
        Map<String, List<Map<String, Object>>> deptMembers = new HashMap<>();
        
        List<Map<String, Object>> hrMembers = new ArrayList<>();
        hrMembers.add(createMockUser("user-001", "张三", "org-002", "人力资源部", "zhangsan@company.com", false));
        hrMembers.add(createMockUser("user-002", "李四", "org-002", "人力资源部", "lisi@company.com", false));
        hrMembers.add(createMockUser("user-003", "王五", "org-002", "人力资源部", "wangwu@company.com", true));
        deptMembers.put("org-002", hrMembers);
        
        List<Map<String, Object>> techMembers = new ArrayList<>();
        techMembers.add(createMockUser("user-004", "赵六", "org-003", "技术部", "zhaoliu@company.com", true));
        deptMembers.put("org-003", techMembers);
        
        List<Map<String, Object>> devMembers = new ArrayList<>();
        devMembers.add(createMockUser("user-005", "钱七", "org-004", "研发一组", "qianqi@company.com", false));
        devMembers.add(createMockUser("user-006", "孙八", "org-004", "研发一组", "sunba@company.com", false));
        deptMembers.put("org-004", devMembers);
        
        List<Map<String, Object>> financeMembers = new ArrayList<>();
        financeMembers.add(createMockUser("user-007", "周九", "org-005", "财务部", "zhoujiu@company.com", true));
        deptMembers.put("org-005", financeMembers);
        
        return deptMembers.getOrDefault(deptId, members);
    }
    
    private List<Map<String, Object>> buildMockUserCapabilities(String userId) {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        Map<String, List<Map<String, Object>>> userCapabilities = new HashMap<>();
        
        userCapabilities.put("user-001", List.of(
            Map.of("capabilityId", "cap-hr-001", "capabilityName", "员工入职办理", "level", "expert"),
            Map.of("capabilityId", "cap-hr-002", "capabilityName", "员工离职办理", "level", "intermediate")
        ));
        
        userCapabilities.put("user-003", List.of(
            Map.of("capabilityId", "cap-hr-001", "capabilityName", "员工入职办理", "level", "expert"),
            Map.of("capabilityId", "cap-hr-002", "capabilityName", "员工离职办理", "level", "expert"),
            Map.of("capabilityId", "cap-hr-003", "capabilityName", "薪酬核算", "level", "intermediate")
        ));
        
        userCapabilities.put("user-004", List.of(
            Map.of("capabilityId", "cap-tech-001", "capabilityName", "技术方案评审", "level", "expert"),
            Map.of("capabilityId", "cap-tech-002", "capabilityName", "代码审查", "level", "expert")
        ));
        
        userCapabilities.put("user-005", List.of(
            Map.of("capabilityId", "cap-dev-001", "capabilityName", "Java开发", "level", "intermediate"),
            Map.of("capabilityId", "cap-dev-002", "capabilityName", "数据库设计", "level", "beginner")
        ));
        
        return userCapabilities.getOrDefault(userId, capabilities);
    }
    
    private Map<String, Object> buildMockDepartmentLeader(String deptId) {
        Map<String, Map<String, Object>> deptLeaders = new HashMap<>();
        
        deptLeaders.put("org-002", Map.of(
            "userId", "user-003",
            "userName", "王五",
            "email", "wangwu@company.com",
            "title", "HR经理"
        ));
        
        deptLeaders.put("org-003", Map.of(
            "userId", "user-004",
            "userName", "赵六",
            "email", "zhaoliu@company.com",
            "title", "技术总监"
        ));
        
        deptLeaders.put("org-005", Map.of(
            "userId", "user-007",
            "userName", "周九",
            "email", "zhoujiu@company.com",
            "title", "财务经理"
        ));
        
        return deptLeaders.getOrDefault(deptId, Map.of(
            "found", false,
            "message", "未找到部门负责人"
        ));
    }
    
    private List<Map<String, Object>> buildMockRoles(String category) {
        List<Map<String, Object>> roles = new ArrayList<>();
        
        roles.add(Map.of(
            "roleId", "hr_specialist",
            "roleName", "HR专员",
            "category", "hr",
            "description", "负责日常人事事务"
        ));
        
        roles.add(Map.of(
            "roleId", "hr_manager",
            "roleName", "HR经理",
            "category", "hr",
            "description", "负责人力资源部门管理"
        ));
        
        roles.add(Map.of(
            "roleId", "tech_leader",
            "roleName", "技术负责人",
            "category", "tech",
            "description", "负责技术团队管理"
        ));
        
        roles.add(Map.of(
            "roleId", "developer",
            "roleName", "开发工程师",
            "category", "tech",
            "description", "负责软件开发"
        ));
        
        roles.add(Map.of(
            "roleId", "finance_manager",
            "roleName", "财务经理",
            "category", "finance",
            "description", "负责财务部门管理"
        ));
        
        if (category != null && !category.isEmpty()) {
            return roles.stream()
                .filter(r -> category.equals(r.get("category")))
                .collect(Collectors.toList());
        }
        
        return roles;
    }
}
