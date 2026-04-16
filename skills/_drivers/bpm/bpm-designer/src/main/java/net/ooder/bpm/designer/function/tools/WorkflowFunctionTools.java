package net.ooder.bpm.designer.function.tools;

import net.ooder.bpm.designer.chat.ChatSession;
import net.ooder.bpm.designer.chat.ChatSessionManager;
import net.ooder.bpm.designer.function.DesignerFunctionDefinition;
import net.ooder.bpm.designer.function.DesignerFunctionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Component
public class WorkflowFunctionTools {
    
    private static final Logger log = LoggerFactory.getLogger(WorkflowFunctionTools.class);
    
    private static final ThreadLocal<String> CURRENT_SESSION_ID = new ThreadLocal<>();
    
    private final DesignerFunctionRegistry functionRegistry;
    private final ChatSessionManager sessionManager;
    
    @Autowired
    public WorkflowFunctionTools(DesignerFunctionRegistry functionRegistry, ChatSessionManager sessionManager) {
        this.functionRegistry = functionRegistry;
        this.sessionManager = sessionManager;
    }
    
    public static void setCurrentSessionId(String sessionId) {
        CURRENT_SESSION_ID.set(sessionId);
    }
    
    public static void clearCurrentSessionId() {
        CURRENT_SESSION_ID.remove();
    }
    
    @PostConstruct
    public void init() {
        registerFunctions();
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getProcessContext() {
        String sessionId = CURRENT_SESSION_ID.get();
        if (sessionId == null) return Collections.emptyMap();
        
        ChatSession session = sessionManager.getSession(sessionId);
        if (session == null || session.getContext() == null) return Collections.emptyMap();
        
        return session.getContext();
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getActivitiesFromContext() {
        Map<String, Object> ctx = getProcessContext();
        Object activities = ctx.get("activities");
        if (activities instanceof List) {
            return (List<Map<String, Object>>) activities;
        }
        return Collections.emptyList();
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getRoutesFromContext() {
        Map<String, Object> ctx = getProcessContext();
        Object routes = ctx.get("routes");
        if (routes instanceof List) {
            return (List<Map<String, Object>>) routes;
        }
        return Collections.emptyList();
    }
    
    private void registerFunctions() {
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("create_activity")
            .description("在当前流程中创建一个新的活动节点。支持创建用户任务、服务任务、开始/结束节点、LLM任务、网关等。")
            .category(DesignerFunctionDefinition.FunctionCategory.WORKFLOW)
            .addParameter("name", "string", "活动名称，如'提交申请'、'审批'、'开始'、'结束'", true)
            .addParameter("activityType", "string", "活动类型：TASK, SERVICE, SCRIPT, START, END, XOR_GATEWAY, AND_GATEWAY, OR_GATEWAY, SUBPROCESS, LLM_TASK", true)
            .addParameter("activityCategory", "string", "活动分类：HUMAN(人工), AGENT(AI代理), SCENE(场景)", false)
            .addParameter("description", "string", "活动描述", false)
            .addParameter("implementation", "string", "实现类型：IMPL_NO, IMPL_TOOL, IMPL_SERVICE, IMPL_SUBFLOW", false)
            .handler(this::handleCreateActivity)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("update_activity")
            .description("更新指定活动的属性。需要提供活动ID和要更新的属性。")
            .category(DesignerFunctionDefinition.FunctionCategory.WORKFLOW)
            .addParameter("activityId", "string", "要更新的活动ID", true)
            .addParameter("attributeName", "string", "要更新的属性名：name, description, activityType, activityCategory, implementation, timing, right", true)
            .addParameter("attributeValue", "string", "属性的新值", true)
            .handler(this::handleUpdateActivity)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("delete_activity")
            .description("删除指定的活动节点。需要提供活动ID。")
            .category(DesignerFunctionDefinition.FunctionCategory.WORKFLOW)
            .addParameter("activityId", "string", "要删除的活动ID", true)
            .handler(this::handleDeleteActivity)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("create_route")
            .description("在两个活动之间创建路由连线。需要提供源活动和目标活动的ID。")
            .category(DesignerFunctionDefinition.FunctionCategory.WORKFLOW)
            .addParameter("fromActivityId", "string", "源活动ID（路由起点）", true)
            .addParameter("toActivityId", "string", "目标活动ID（路由终点）", true)
            .addParameter("name", "string", "路由名称", false)
            .addParameter("condition", "string", "路由条件表达式", false)
            .handler(this::handleCreateRoute)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("list_activities")
            .description("列出当前流程中的所有活动节点及其基本信息。")
            .category(DesignerFunctionDefinition.FunctionCategory.WORKFLOW)
            .handler(this::handleListActivities)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_activity_detail")
            .description("获取指定活动的详细信息，包括所有配置属性。")
            .category(DesignerFunctionDefinition.FunctionCategory.WORKFLOW)
            .addParameter("activityId", "string", "活动ID", true)
            .handler(this::handleGetActivityDetail)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("validate_process")
            .description("验证当前流程定义的完整性和正确性，检查缺失的节点、断开的路由等问题。")
            .category(DesignerFunctionDefinition.FunctionCategory.WORKFLOW)
            .handler(this::handleValidateProcess)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("suggest_next_step")
            .description("根据当前流程状态，建议下一步应该添加或配置的元素。")
            .category(DesignerFunctionDefinition.FunctionCategory.WORKFLOW)
            .addParameter("currentActivityId", "string", "当前选中的活动ID（可选）", false)
            .handler(this::handleSuggestNextStep)
            .build());
        
        log.info("Registered {} workflow functions", 8);
    }
    
    private Object handleCreateActivity(Map<String, Object> args) {
        String name = (String) args.get("name");
        String activityType = (String) args.get("activityType");
        String category = (String) args.getOrDefault("activityCategory", inferCategory(activityType));
        String description = (String) args.getOrDefault("description", "");
        String implementation = (String) args.getOrDefault("implementation", inferImplementation(activityType, category));
        
        String activityId = "act_" + System.currentTimeMillis();
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("activityDefId", activityId);
        data.put("name", name);
        data.put("activityType", activityType);
        data.put("activityCategory", category);
        data.put("description", description);
        data.put("implementation", implementation);
        data.put("position", Map.of("x", 200, "y", 200));
        
        if ("AGENT".equals(category)) {
            Map<String, Object> agentConfig = new LinkedHashMap<>();
            agentConfig.put("agentType", "LLM_AGENT");
            agentConfig.put("status", "online");
            agentConfig.put("role", "worker");
            data.put("agentConfig", agentConfig);
        } else if ("SCENE".equals(category)) {
            Map<String, Object> sceneConfig = new LinkedHashMap<>();
            sceneConfig.put("sceneType", "AUTO");
            sceneConfig.put("status", "ENABLED");
            data.put("sceneConfig", sceneConfig);
        }
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("action", "create_activity");
        result.put("data", data);
        result.put("message", "已创建活动「" + name + "」");
        
        return result;
    }
    
    private Object handleUpdateActivity(Map<String, Object> args) {
        String activityId = (String) args.get("activityId");
        String attributeName = (String) args.get("attributeName");
        String attributeValue = (String) args.get("attributeValue");
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("activityDefId", activityId);
        data.put("attribute", attributeName);
        data.put("value", attributeValue);
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("action", "update_activity");
        result.put("data", data);
        result.put("message", "已更新活动「" + activityId + "」的" + attributeName);
        
        return result;
    }
    
    private Object handleDeleteActivity(Map<String, Object> args) {
        String activityId = (String) args.get("activityId");
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("action", "delete_activity");
        result.put("data", Map.of("activityId", activityId));
        result.put("message", "已删除活动「" + activityId + "」");
        
        return result;
    }
    
    private Object handleCreateRoute(Map<String, Object> args) {
        String fromId = (String) args.get("fromActivityId");
        String toId = (String) args.get("toActivityId");
        String name = (String) args.getOrDefault("name", "");
        String condition = (String) args.getOrDefault("condition", "");
        
        String routeId = "route_" + System.currentTimeMillis();
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("action", "create_route");
        result.put("data", Map.of(
            "routeDefId", routeId,
            "name", name,
            "from", fromId,
            "to", toId,
            "condition", condition
        ));
        result.put("message", "已创建路由「" + fromId + " → " + toId + "」");
        
        return result;
    }
    
    private Object handleListActivities(Map<String, Object> args) {
        List<Map<String, Object>> contextActivities = getActivitiesFromContext();
        
        if (!contextActivities.isEmpty()) {
            List<Map<String, Object>> summaries = new ArrayList<>();
            for (Map<String, Object> act : contextActivities) {
                Map<String, Object> summary = new LinkedHashMap<>();
                summary.put("activityDefId", act.get("activityDefId"));
                summary.put("name", act.get("name"));
                summary.put("activityType", act.get("activityType"));
                summary.put("activityCategory", act.getOrDefault("activityCategory", "HUMAN"));
                if (act.containsKey("implementation")) {
                    summary.put("implementation", act.get("implementation"));
                }
                summaries.add(summary);
            }
            
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("data", summaries);
            result.put("count", summaries.size());
            result.put("source", "session_context");
            return result;
        }
        
        List<Map<String, Object>> defaultActivities = new ArrayList<>();
        defaultActivities.add(Map.of(
            "activityDefId", "start",
            "name", "开始",
            "activityType", "START",
            "activityCategory", "HUMAN"
        ));
        defaultActivities.add(Map.of(
            "activityDefId", "end",
            "name", "结束",
            "activityType", "END",
            "activityCategory", "HUMAN"
        ));
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("data", defaultActivities);
        result.put("count", defaultActivities.size());
        result.put("source", "default");
        return result;
    }
    
    private Object handleGetActivityDetail(Map<String, Object> args) {
        String activityId = (String) args.get("activityId");
        
        List<Map<String, Object>> contextActivities = getActivitiesFromContext();
        for (Map<String, Object> act : contextActivities) {
            if (activityId.equals(act.get("activityDefId"))) {
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("success", true);
                result.put("data", act);
                result.put("source", "session_context");
                return result;
            }
        }
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("data", Map.of(
            "activityDefId", activityId,
            "name", "未找到活动",
            "activityType", "UNKNOWN",
            "activityCategory", "HUMAN"
        ));
        result.put("source", "not_found");
        return result;
    }
    
    private Object handleValidateProcess(Map<String, Object> args) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        List<Map<String, Object>> activities = getActivitiesFromContext();
        List<Map<String, Object>> routes = getRoutesFromContext();
        
        boolean hasStart = false;
        boolean hasEnd = false;
        Set<String> activityIds = new HashSet<>();
        Set<String> connectedActivities = new HashSet<>();
        
        for (Map<String, Object> act : activities) {
            String id = (String) act.get("activityDefId");
            if (id != null) activityIds.add(id);
            
            String type = (String) act.get("activityType");
            if ("START".equals(type)) hasStart = true;
            if ("END".equals(type)) hasEnd = true;
        }
        
        for (Map<String, Object> route : routes) {
            String from = (String) route.get("from");
            String to = (String) route.get("to");
            if (from != null) connectedActivities.add(from);
            if (to != null) connectedActivities.add(to);
            
            if (from != null && !activityIds.contains(from)) {
                warnings.add("路由源活动不存在: " + from);
            }
            if (to != null && !activityIds.contains(to)) {
                warnings.add("路由目标活动不存在: " + to);
            }
        }
        
        if (!hasStart) errors.add("流程缺少开始节点");
        if (!hasEnd) errors.add("流程缺少结束节点");
        
        for (String id : activityIds) {
            if (!connectedActivities.contains(id)) {
                String type = activities.stream()
                    .filter(a -> id.equals(a.get("activityDefId")))
                    .map(a -> (String) a.get("activityType"))
                    .findFirst().orElse("");
                if (!"START".equals(type) && !"END".equals(type)) {
                    warnings.add("活动「" + id + "」没有路由连接");
                }
            }
        }
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("errors", errors);
        result.put("warnings", warnings);
        result.put("valid", errors.isEmpty());
        result.put("activityCount", activities.size());
        result.put("routeCount", routes.size());
        result.put("message", errors.isEmpty() ? 
            "流程验证通过，有" + warnings.size() + "条建议" : 
            "发现" + errors.size() + "个错误");
        
        return result;
    }
    
    private Object handleSuggestNextStep(Map<String, Object> args) {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        List<Map<String, Object>> activities = getActivitiesFromContext();
        List<Map<String, Object>> routes = getRoutesFromContext();
        
        boolean hasStart = activities.stream().anyMatch(a -> "START".equals(a.get("activityType")));
        boolean hasEnd = activities.stream().anyMatch(a -> "END".equals(a.get("activityType")));
        long taskCount = activities.stream().filter(a -> "TASK".equals(a.get("activityType"))).count();
        
        if (!hasStart) {
            suggestions.add(Map.of(
                "type", "add",
                "title", "添加开始节点",
                "description", "流程需要一个开始节点作为入口",
                "action", "create_activity",
                "params", Map.of("activityType", "START", "activityCategory", "HUMAN", "name", "开始"),
                "priority", 1
            ));
        }
        
        if (!hasEnd) {
            suggestions.add(Map.of(
                "type", "add",
                "title", "添加结束节点",
                "description", "流程需要一个结束节点",
                "action", "create_activity",
                "params", Map.of("activityType", "END", "activityCategory", "HUMAN", "name", "结束"),
                "priority", 1
            ));
        }
        
        if (taskCount == 0) {
            suggestions.add(Map.of(
                "type", "add",
                "title", "添加用户任务",
                "description", "在流程中添加一个需要人工处理的任务节点",
                "action", "create_activity",
                "params", Map.of("activityType", "TASK", "activityCategory", "HUMAN"),
                "priority", 2
            ));
        }
        
        if (activities.size() > 1 && routes.isEmpty()) {
            suggestions.add(Map.of(
                "type", "add",
                "title", "连接活动节点",
                "description", "已有" + activities.size() + "个活动但还没有路由连接",
                "action", "create_route",
                "priority", 2
            ));
        }
        
        suggestions.add(Map.of(
            "type", "add",
            "title", "添加LLM任务",
            "description", "添加一个AI智能处理任务节点",
            "action", "create_activity",
            "params", Map.of("activityType", "LLM_TASK", "activityCategory", "AGENT"),
            "priority", 3
        ));
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("suggestions", suggestions);
        result.put("count", suggestions.size());
        
        return result;
    }
    
    private String inferCategory(String activityType) {
        if ("LLM_TASK".equals(activityType)) return "AGENT";
        if ("START".equals(activityType) || "END".equals(activityType)) return "HUMAN";
        if ("SERVICE".equals(activityType)) return "HUMAN";
        return "HUMAN";
    }
    
    private String inferImplementation(String activityType, String category) {
        if ("START".equals(activityType) || "END".equals(activityType)) return "IMPL_NO";
        if ("SERVICE".equals(activityType)) return "IMPL_SERVICE";
        if ("AGENT".equals(category)) return "IMPL_TOOL";
        return "IMPL_NO";
    }
}
