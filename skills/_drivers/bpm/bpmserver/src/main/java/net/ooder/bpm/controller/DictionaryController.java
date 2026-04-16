package net.ooder.bpm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {

    private static final Logger log = LoggerFactory.getLogger(DictionaryController.class);

    @GetMapping("/org/tree")
    public ResponseEntity<Map<String, Object>> getOrgTree(
            @RequestParam(required = false) String parentId,
            @RequestParam(required = false, defaultValue = "false") boolean lazy) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> orgs = getMockOrganizations(parentId);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", orgs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get org tree", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/org/list")
    public ResponseEntity<Map<String, Object>> getOrgList(
            @RequestParam(required = false) String parentId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> orgs = getMockOrganizations(parentId);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", orgs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get org list", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/org/{orgId}")
    public ResponseEntity<Map<String, Object>> getOrgById(@PathVariable String orgId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> org = findOrgById(orgId);
            if (org == null) {
                response.put("code", 404);
                response.put("message", "组织机构不存在: " + orgId);
                return ResponseEntity.status(404).body(response);
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", org);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get org by id", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/person/list")
    public ResponseEntity<Map<String, Object>> getPersonList(
            @RequestParam(required = false) String orgId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> persons = getMockPersons(orgId, name);
            
            int total = persons.size();
            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, total);
            
            List<Map<String, Object>> pagedPersons = fromIndex < total 
                ? persons.subList(fromIndex, toIndex) 
                : new ArrayList<>();
            
            Map<String, Object> pageData = new LinkedHashMap<>();
            pageData.put("list", pagedPersons);
            pageData.put("total", total);
            pageData.put("page", page);
            pageData.put("size", size);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", pageData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get person list", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<Map<String, Object>> getPersonById(@PathVariable String personId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> person = findPersonById(personId);
            if (person == null) {
                response.put("code", 404);
                response.put("message", "人员不存在: " + personId);
                return ResponseEntity.status(404).body(response);
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", person);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get person by id", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/person/batch")
    public ResponseEntity<Map<String, Object>> getPersonsByIds(@RequestBody List<String> personIds) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> persons = new ArrayList<>();
            for (String personId : personIds) {
                Map<String, Object> person = findPersonById(personId);
                if (person != null) {
                    persons.add(person);
                }
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", persons);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get persons by ids", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/role/list")
    public ResponseEntity<Map<String, Object>> getRoleList(
            @RequestParam(required = false) String name) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> roles = getMockRoles(name);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get role list", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<Map<String, Object>> getRoleById(@PathVariable String roleId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> role = findRoleById(roleId);
            if (role == null) {
                response.put("code", 404);
                response.put("message", "角色不存在: " + roleId);
                return ResponseEntity.status(404).body(response);
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", role);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get role by id", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/expression/variables")
    public ResponseEntity<Map<String, Object>> getExpressionVariables(
            @RequestParam(required = false) String processDefId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> variables = getMockExpressionVariables(processDefId);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", variables);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get expression variables", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/expression/templates")
    public ResponseEntity<Map<String, Object>> getExpressionTemplates() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> templates = getMockExpressionTemplates();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", templates);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get expression templates", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/expression/validate")
    public ResponseEntity<Map<String, Object>> validateExpression(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            String expression = (String) request.get("expression");
            Map<String, Object> result = validateMockExpression(expression);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to validate expression", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/listener/list")
    public ResponseEntity<Map<String, Object>> getListenerList(
            @RequestParam(required = false) String type) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> listeners = getMockListeners(type);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", listeners);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get listener list", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/form/list")
    public ResponseEntity<Map<String, Object>> getFormList(
            @RequestParam(required = false) String name) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> forms = getMockForms(name);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", forms);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get form list", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/service/list")
    public ResponseEntity<Map<String, Object>> getServiceList(
            @RequestParam(required = false) String name) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> services = getMockServices(name);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", services);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get service list", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/agent/list")
    public ResponseEntity<Map<String, Object>> getAgentList(
            @RequestParam(required = false) String agentType,
            @RequestParam(required = false) String status) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> agents = getMockAgents(agentType, status);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", agents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get agent list", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<Map<String, Object>> getAgentById(@PathVariable String agentId) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Map<String, Object> agent = findAgentById(agentId);
            if (agent == null) {
                response.put("code", 404);
                response.put("message", "Agent不存在: " + agentId);
                return ResponseEntity.status(404).body(response);
            }
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", agent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get agent by id", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/agent/types")
    public ResponseEntity<Map<String, Object>> getAgentTypes() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> types = getMockAgentTypes();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", types);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get agent types", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/agent/roles")
    public ResponseEntity<Map<String, Object>> getAgentRoles(
            @RequestParam(required = false) String agentType) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> roles = getMockAgentRoles(agentType);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get agent roles", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/agent/llm-providers")
    public ResponseEntity<Map<String, Object>> getLlmProviders() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> providers = getMockLlmProviders();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", providers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get LLM providers", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/agent/capabilities")
    public ResponseEntity<Map<String, Object>> getAgentCapabilities() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            List<Map<String, Object>> capabilities = getMockAgentCapabilities();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", capabilities);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get agent capabilities", e);
            response.put("code", 500);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private List<Map<String, Object>> getMockOrganizations(String parentId) {
        List<Map<String, Object>> allOrgs = new ArrayList<>();
        
        allOrgs.add(createOrg("org1", "ORG001", "总公司", null, false));
        allOrgs.add(createOrg("org2", "ORG002", "技术部", "org1", false));
        allOrgs.add(createOrg("org3", "ORG003", "产品部", "org1", false));
        allOrgs.add(createOrg("org4", "ORG004", "市场部", "org1", false));
        allOrgs.add(createOrg("org5", "ORG005", "人力资源部", "org1", false));
        allOrgs.add(createOrg("org21", "ORG021", "前端组", "org2", true));
        allOrgs.add(createOrg("org22", "ORG022", "后端组", "org2", true));
        allOrgs.add(createOrg("org23", "ORG023", "测试组", "org2", true));
        allOrgs.add(createOrg("org31", "ORG031", "产品一组", "org3", true));
        allOrgs.add(createOrg("org32", "ORG032", "产品二组", "org3", true));
        
        if (parentId == null || parentId.isEmpty()) {
            return allOrgs.stream()
                .filter(org -> "org1".equals(org.get("id")))
                .toList();
        }
        
        return allOrgs.stream()
            .filter(org -> parentId.equals(org.get("parentId")))
            .toList();
    }

    private Map<String, Object> createOrg(String id, String code, String name, String parentId, boolean leaf) {
        Map<String, Object> org = new LinkedHashMap<>();
        org.put("id", id);
        org.put("code", code);
        org.put("name", name);
        org.put("parentId", parentId);
        org.put("leaf", leaf);
        org.put("icon", leaf ? "person" : "folder");
        return org;
    }

    private Map<String, Object> findOrgById(String orgId) {
        return getMockOrganizations(null).stream()
            .filter(org -> orgId.equals(org.get("id")))
            .findFirst()
            .orElse(null);
    }

    private List<Map<String, Object>> getMockPersons(String orgId, String name) {
        List<Map<String, Object>> allPersons = new ArrayList<>();
        
        allPersons.add(createPerson("user1", "U001", "张三", "org21", "前端工程师", "zhangsan@ooder.net"));
        allPersons.add(createPerson("user2", "U002", "李四", "org21", "高级前端工程师", "lisi@ooder.net"));
        allPersons.add(createPerson("user3", "U003", "王五", "org22", "后端工程师", "wangwu@ooder.net"));
        allPersons.add(createPerson("user4", "U004", "赵六", "org22", "高级后端工程师", "zhaoliu@ooder.net"));
        allPersons.add(createPerson("user5", "U005", "钱七", "org23", "测试工程师", "qianqi@ooder.net"));
        allPersons.add(createPerson("user6", "U006", "孙八", "org31", "产品经理", "sunba@ooder.net"));
        allPersons.add(createPerson("user7", "U007", "周九", "org32", "产品经理", "zhoujiu@ooder.net"));
        allPersons.add(createPerson("user8", "U008", "吴十", "org4", "市场专员", "wushi@ooder.net"));
        allPersons.add(createPerson("user9", "U009", "郑十一", "org5", "HR专员", "zheng11@ooder.net"));
        allPersons.add(createPerson("user10", "U010", "王经理", "org2", "技术总监", "manager@ooder.net"));
        
        return allPersons.stream()
            .filter(p -> orgId == null || orgId.isEmpty() || orgId.equals(p.get("orgId")))
            .filter(p -> name == null || name.isEmpty() || 
                ((String)p.get("name")).contains(name) || 
                ((String)p.get("code")).contains(name))
            .toList();
    }

    private Map<String, Object> createPerson(String id, String code, String name, String orgId, String title, String email) {
        Map<String, Object> person = new LinkedHashMap<>();
        person.put("id", id);
        person.put("code", code);
        person.put("name", name);
        person.put("orgId", orgId);
        person.put("title", title);
        person.put("email", email);
        person.put("icon", "person");
        return person;
    }

    private Map<String, Object> findPersonById(String personId) {
        return getMockPersons(null, null).stream()
            .filter(p -> personId.equals(p.get("id")))
            .findFirst()
            .orElse(null);
    }

    private List<Map<String, Object>> getMockRoles(String name) {
        List<Map<String, Object>> allRoles = new ArrayList<>();
        
        allRoles.add(createRole("role1", "R001", "系统管理员", "拥有系统所有权限"));
        allRoles.add(createRole("role2", "R002", "流程管理员", "管理流程定义和实例"));
        allRoles.add(createRole("role3", "R003", "部门经理", "部门审批权限"));
        allRoles.add(createRole("role4", "R004", "普通用户", "基本使用权限"));
        allRoles.add(createRole("role5", "R005", "访客", "只读权限"));
        
        return allRoles.stream()
            .filter(r -> name == null || name.isEmpty() || 
                ((String)r.get("name")).contains(name) || 
                ((String)r.get("code")).contains(name))
            .toList();
    }

    private Map<String, Object> createRole(String id, String code, String name, String description) {
        Map<String, Object> role = new LinkedHashMap<>();
        role.put("id", id);
        role.put("code", code);
        role.put("name", name);
        role.put("description", description);
        role.put("icon", "role");
        return role;
    }

    private Map<String, Object> findRoleById(String roleId) {
        return getMockRoles(null).stream()
            .filter(r -> roleId.equals(r.get("id")))
            .findFirst()
            .orElse(null);
    }

    private List<Map<String, Object>> getMockExpressionVariables(String processDefId) {
        List<Map<String, Object>> variables = new ArrayList<>();
        
        variables.add(createVariable("processDefId", "流程定义ID", "String", "当前流程定义ID"));
        variables.add(createVariable("processInstId", "流程实例ID", "String", "当前流程实例ID"));
        variables.add(createVariable("starter", "发起人", "String", "流程发起人ID"));
        variables.add(createVariable("starterName", "发起人姓名", "String", "流程发起人姓名"));
        variables.add(createVariable("starterOrg", "发起人部门", "String", "流程发起人部门ID"));
        variables.add(createVariable("starterOrgName", "发起人部门名称", "String", "流程发起人部门名称"));
        variables.add(createVariable("currentTime", "当前时间", "DateTime", "系统当前时间"));
        variables.add(createVariable("currentDate", "当前日期", "Date", "系统当前日期"));
        variables.add(createVariable("activityName", "活动名称", "String", "当前活动名称"));
        variables.add(createVariable("activityInstId", "活动实例ID", "String", "当前活动实例ID"));
        
        return variables;
    }

    private Map<String, Object> createVariable(String name, String label, String type, String description) {
        Map<String, Object> variable = new LinkedHashMap<>();
        variable.put("name", name);
        variable.put("label", label);
        variable.put("type", type);
        variable.put("description", description);
        return variable;
    }

    private List<Map<String, Object>> getMockExpressionTemplates() {
        List<Map<String, Object>> templates = new ArrayList<>();
        
        templates.add(createTemplate("tpl1", "部门经理审批", 
            "${orgManager(starterOrg)}", 
            "自动选择发起人所在部门的经理"));
        templates.add(createTemplate("tpl2", "直属领导审批", 
            "${directLeader(starter)}", 
            "自动选择发起人的直属领导"));
        templates.add(createTemplate("tpl3", "发起人自己", 
            "${starter}", 
            "选择流程发起人"));
        templates.add(createTemplate("tpl4", "部门所有人", 
            "${orgAllPersons(starterOrg)}", 
            "选择发起人部门的所有人员"));
        templates.add(createTemplate("tpl5", "指定角色", 
            "${rolePersons('role3')}", 
            "选择指定角色的所有人员"));
        templates.add(createTemplate("tpl6", "条件表达式", 
            "${amount > 10000 ? rolePersons('role2') : starter}", 
            "根据金额选择审批人"));
        
        return templates;
    }

    private Map<String, Object> createTemplate(String id, String name, String expression, String description) {
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("id", id);
        template.put("name", name);
        template.put("expression", expression);
        template.put("description", description);
        return template;
    }

    private Map<String, Object> validateMockExpression(String expression) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        if (expression == null || expression.isEmpty()) {
            result.put("valid", false);
            result.put("message", "表达式不能为空");
            return result;
        }
        
        if (!expression.startsWith("${") || !expression.endsWith("}")) {
            result.put("valid", false);
            result.put("message", "表达式格式错误，应以${开头，}结尾");
            return result;
        }
        
        result.put("valid", true);
        result.put("message", "表达式格式正确");
        return result;
    }

    private List<Map<String, Object>> getMockListeners(String type) {
        List<Map<String, Object>> allListeners = new ArrayList<>();
        
        allListeners.add(createListener("listener1", "流程启动监听器", 
            "PROCESS_START", "net.ooder.bpm.listener.ProcessStartListener"));
        allListeners.add(createListener("listener2", "流程结束监听器", 
            "PROCESS_END", "net.ooder.bpm.listener.ProcessEndListener"));
        allListeners.add(createListener("listener3", "活动创建监听器", 
            "ACTIVITY_CREATE", "net.ooder.bpm.listener.ActivityCreateListener"));
        allListeners.add(createListener("listener4", "活动完成监听器", 
            "ACTIVITY_COMPLETE", "net.ooder.bpm.listener.ActivityCompleteListener"));
        allListeners.add(createListener("listener5", "路由选择监听器", 
            "ROUTE_SELECT", "net.ooder.bpm.listener.RouteSelectListener"));
        
        return allListeners.stream()
            .filter(l -> type == null || type.isEmpty() || type.equals(l.get("type")))
            .toList();
    }

    private Map<String, Object> createListener(String id, String name, String type, String className) {
        Map<String, Object> listener = new LinkedHashMap<>();
        listener.put("id", id);
        listener.put("name", name);
        listener.put("type", type);
        listener.put("className", className);
        return listener;
    }

    private List<Map<String, Object>> getMockForms(String name) {
        List<Map<String, Object>> allForms = new ArrayList<>();
        
        allForms.add(createForm("form1", "请假申请表", "leave_apply", "请假流程使用"));
        allForms.add(createForm("form2", "报销申请表", "expense_apply", "报销流程使用"));
        allForms.add(createForm("form3", "出差申请表", "travel_apply", "出差流程使用"));
        allForms.add(createForm("form4", "采购申请表", "purchase_apply", "采购流程使用"));
        allForms.add(createForm("form5", "通用审批表", "general_approval", "通用审批流程"));
        
        return allForms.stream()
            .filter(f -> name == null || name.isEmpty() || 
                ((String)f.get("name")).contains(name))
            .toList();
    }

    private Map<String, Object> createForm(String id, String name, String code, String description) {
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("id", id);
        form.put("name", name);
        form.put("code", code);
        form.put("description", description);
        return form;
    }

    private List<Map<String, Object>> getMockServices(String name) {
        List<Map<String, Object>> allServices = new ArrayList<>();
        
        allServices.add(createService("svc1", "邮件发送服务", "emailService", 
            "net.ooder.bpm.service.EmailService", "发送邮件通知"));
        allServices.add(createService("svc2", "短信发送服务", "smsService", 
            "net.ooder.bpm.service.SmsService", "发送短信通知"));
        allServices.add(createService("svc3", "数据同步服务", "dataSyncService", 
            "net.ooder.bpm.service.DataSyncService", "同步外部系统数据"));
        allServices.add(createService("svc4", "审批通知服务", "approvalNotifyService", 
            "net.ooder.bpm.service.ApprovalNotifyService", "发送审批通知"));
        allServices.add(createService("svc5", "归档服务", "archiveService", 
            "net.ooder.bpm.service.ArchiveService", "流程归档处理"));
        
        return allServices.stream()
            .filter(s -> name == null || name.isEmpty() || 
                ((String)s.get("name")).contains(name))
            .toList();
    }

    private Map<String, Object> createService(String id, String name, String code, String className, String description) {
        Map<String, Object> service = new LinkedHashMap<>();
        service.put("id", id);
        service.put("name", name);
        service.put("code", code);
        service.put("className", className);
        service.put("description", description);
        return service;
    }

    private List<Map<String, Object>> getMockAgents(String agentType, String status) {
        List<Map<String, Object>> allAgents = new ArrayList<>();

        allAgents.add(createAgent("agent-001", "智能审批助手", "LLM_AGENT", "active",
            "assistant", "自动审批流程中的智能决策助手", List.of("chat", "approval", "document_analysis")));
        allAgents.add(createAgent("agent-002", "数据分析Agent", "LLM_AGENT", "active",
            "analyst", "数据查询与分析Agent", List.of("chat", "data_query", "chart_generation")));
        allAgents.add(createAgent("agent-003", "规则引擎Agent", "RULE_AGENT", "active",
            "rule_engine", "基于规则的业务处理Agent", List.of("rule_execution", "validation")));
        allAgents.add(createAgent("agent-004", "协调调度Agent", "SUPER_AGENT", "active",
            "coordinator", "多Agent协调与任务调度", List.of("task_delegation", "coordination", "monitoring")));
        allAgents.add(createAgent("agent-005", "文档处理Agent", "WORKER", "inactive",
            "worker", "文档格式转换与处理", List.of("document_conversion", "ocr", "formatting")));
        allAgents.add(createAgent("agent-006", "混合决策Agent", "HYBRID_AGENT", "active",
            "hybrid", "结合规则与LLM的混合决策Agent", List.of("chat", "rule_execution", "approval")));

        return allAgents.stream()
            .filter(a -> agentType == null || agentType.isEmpty() || agentType.equals(a.get("agentType")))
            .filter(a -> status == null || status.isEmpty() || status.equals(a.get("status")))
            .toList();
    }

    private Map<String, Object> createAgent(String agentId, String agentName, String agentType,
            String status, String role, String description, List<String> capabilities) {
        Map<String, Object> agent = new LinkedHashMap<>();
        agent.put("agentId", agentId);
        agent.put("agentName", agentName);
        agent.put("agentType", agentType);
        agent.put("status", status);
        agent.put("role", role);
        agent.put("description", description);
        agent.put("capabilities", capabilities);
        agent.put("enabled", "active".equals(status));
        agent.put("healthStatus", "active".equals(status) ? "healthy" : "offline");
        agent.put("clusterId", "cluster-default");
        agent.put("maxConcurrency", 10);
        agent.put("currentLoad", 0);
        return agent;
    }

    private Map<String, Object> findAgentById(String agentId) {
        return getMockAgents(null, null).stream()
            .filter(a -> agentId.equals(a.get("agentId")))
            .findFirst()
            .orElse(null);
    }

    private List<Map<String, Object>> getMockAgentTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        types.add(Map.of("value", "LLM_AGENT", "label", "LLM Agent"));
        types.add(Map.of("value", "RULE_AGENT", "label", "规则Agent"));
        types.add(Map.of("value", "HYBRID_AGENT", "label", "混合Agent"));
        types.add(Map.of("value", "WORKER", "label", "Worker Agent"));
        types.add(Map.of("value", "SUPER_AGENT", "label", "Super Agent"));
        types.add(Map.of("value", "DEVICE", "label", "设备Agent"));
        types.add(Map.of("value", "SENSOR", "label", "传感器Agent"));
        return types;
    }

    private List<Map<String, Object>> getMockAgentRoles(String agentType) {
        List<Map<String, Object>> roles = new ArrayList<>();
        roles.add(Map.of("value", "assistant", "label", "通用助手", "agentType", "LLM_AGENT"));
        roles.add(Map.of("value", "analyst", "label", "数据分析师", "agentType", "LLM_AGENT"));
        roles.add(Map.of("value", "rule_engine", "label", "规则引擎", "agentType", "RULE_AGENT"));
        roles.add(Map.of("value", "coordinator", "label", "协调调度", "agentType", "SUPER_AGENT"));
        roles.add(Map.of("value", "worker", "label", "任务执行", "agentType", "WORKER"));
        roles.add(Map.of("value", "hybrid", "label", "混合决策", "agentType", "HYBRID_AGENT"));

        if (agentType != null && !agentType.isEmpty()) {
            return roles.stream()
                .filter(r -> agentType.equals(r.get("agentType")))
                .toList();
        }
        return roles;
    }

    private List<Map<String, Object>> getMockLlmProviders() {
        List<Map<String, Object>> providers = new ArrayList<>();

        Map<String, Object> openai = new LinkedHashMap<>();
        openai.put("value", "openai");
        openai.put("label", "OpenAI");
        openai.put("models", List.of(
            Map.of("value", "gpt-4", "label", "GPT-4"),
            Map.of("value", "gpt-4-turbo", "label", "GPT-4 Turbo"),
            Map.of("value", "gpt-4o", "label", "GPT-4o"),
            Map.of("value", "gpt-3.5-turbo", "label", "GPT-3.5 Turbo"),
            Map.of("value", "o1-preview", "label", "o1 Preview")
        ));
        providers.add(openai);

        Map<String, Object> anthropic = new LinkedHashMap<>();
        anthropic.put("value", "anthropic");
        anthropic.put("label", "Anthropic");
        anthropic.put("models", List.of(
            Map.of("value", "claude-3-opus", "label", "Claude 3 Opus"),
            Map.of("value", "claude-3-sonnet", "label", "Claude 3 Sonnet"),
            Map.of("value", "claude-3-haiku", "label", "Claude 3 Haiku")
        ));
        providers.add(anthropic);

        Map<String, Object> zhipu = new LinkedHashMap<>();
        zhipu.put("value", "zhipu");
        zhipu.put("label", "智谱AI");
        zhipu.put("models", List.of(
            Map.of("value", "glm-4", "label", "GLM-4"),
            Map.of("value", "glm-4-flash", "label", "GLM-4 Flash"),
            Map.of("value", "glm-3-turbo", "label", "GLM-3 Turbo")
        ));
        providers.add(zhipu);

        Map<String, Object> local = new LinkedHashMap<>();
        local.put("value", "local");
        local.put("label", "本地模型");
        local.put("models", List.of(
            Map.of("value", "llama-3", "label", "LLaMA 3"),
            Map.of("value", "qwen-2", "label", "Qwen 2"),
            Map.of("value", "mistral-7b", "label", "Mistral 7B")
        ));
        providers.add(local);

        return providers;
    }

    private List<Map<String, Object>> getMockAgentCapabilities() {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        capabilities.add(Map.of("value", "chat", "label", "对话交互"));
        capabilities.add(Map.of("value", "approval", "label", "审批决策"));
        capabilities.add(Map.of("value", "document_analysis", "label", "文档分析"));
        capabilities.add(Map.of("value", "data_query", "label", "数据查询"));
        capabilities.add(Map.of("value", "chart_generation", "label", "图表生成"));
        capabilities.add(Map.of("value", "rule_execution", "label", "规则执行"));
        capabilities.add(Map.of("value", "validation", "label", "数据验证"));
        capabilities.add(Map.of("value", "task_delegation", "label", "任务委托"));
        capabilities.add(Map.of("value", "coordination", "label", "协调调度"));
        capabilities.add(Map.of("value", "monitoring", "label", "监控告警"));
        capabilities.add(Map.of("value", "document_conversion", "label", "文档转换"));
        capabilities.add(Map.of("value", "ocr", "label", "OCR识别"));
        capabilities.add(Map.of("value", "formatting", "label", "格式处理"));
        capabilities.add(Map.of("value", "function_calling", "label", "函数调用"));
        capabilities.add(Map.of("value", "rag", "label", "知识检索(RAG)"));
        return capabilities;
    }
}
