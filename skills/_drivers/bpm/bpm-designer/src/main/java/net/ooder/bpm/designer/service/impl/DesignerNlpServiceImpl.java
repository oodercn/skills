package net.ooder.bpm.designer.service.impl;

import net.ooder.bpm.designer.model.dto.*;
import net.ooder.bpm.designer.service.DesignerNlpService;
import net.ooder.bpm.designer.prompt.DesignerPromptBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DesignerNlpServiceImpl implements DesignerNlpService {

    private static final Logger log = LoggerFactory.getLogger(DesignerNlpServiceImpl.class);

    @Autowired
    private DesignerPromptBuilder promptBuilder;



    private static final Map<String, String> INTENT_PATTERNS = new HashMap<>() {{
        put("create_process", "(创建|新建|添加).*(流程|过程)");
        put("create_activity", "(创建|新建|添加).*(活动|节点|任务)");
        put("create_route", "(创建|新建|添加).*(路由|连线|连接)");
        put("update_attribute", "(设置|修改|更新|更改).*(属性|配置)");
        put("delete_element", "(删除|移除).*(活动|节点|路由)");
        put("get_suggestion", "(建议|推荐|提示|帮助)");
        put("validate_process", "(检查|验证|校验).*(流程|错误)");
        put("describe_element", "(描述|说明|解释).*(活动|节点|流程)");
    }};

    private static final Map<String, String> ACTIVITY_TYPE_KEYWORDS = new HashMap<>() {{
        put("TASK", "(任务|审批|处理|办理)");
        put("SERVICE", "(服务|调用|接口)");
        put("SCRIPT", "(脚本|代码|程序)");
        put("START", "(开始|起始)");
        put("END", "(结束|终止)");
        put("XOR_GATEWAY", "(排他|条件|分支|选择)");
        put("AND_GATEWAY", "(并行|同时)");
        put("OR_GATEWAY", "(包容|或)");
        put("SUBPROCESS", "(子流程|嵌套)");
        put("LLM_TASK", "(LLM|AI|智能|大模型)");
    }};

    private static final Map<String, String> CATEGORY_KEYWORDS = new HashMap<>() {{
        put("HUMAN", "(人工|手动|人员|用户)");
        put("AGENT", "(Agent|代理|智能体)");
        put("SCENE", "(场景|情景)");
    }};

    @Override
    public NlpResponse processNaturalLanguage(String userInput, DesignerContextDTO context) {
        log.info("Processing NLP input: {}", userInput);

        NlpResponse response = new NlpResponse();
        
        List<NlpIntent> intents = analyzeIntent(userInput);
        if (intents.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("无法识别您的意图，请尝试更具体的描述。");
            return response;
        }

        NlpIntent primaryIntent = intents.get(0);
        response.setIntent(primaryIntent.getName());
        response.setConfidence(primaryIntent.getConfidence());

        Map<String, Object> entities = extractEntities(userInput, primaryIntent.getName());
        response.setEntities(entities);

        try {
            switch (primaryIntent.getName()) {
                case "create_process":
                    response.setAction("create_process");
                    response.setActionParams(entities);
                    response.setMessage("将创建新流程");
                    break;
                    
                case "create_activity":
                    response.setAction("add_activity");
                    response.setActionParams(entities);
                    response.setMessage("将添加新活动");
                    break;
                    
                case "create_route":
                    response.setAction("add_route");
                    response.setActionParams(entities);
                    response.setMessage("将添加新路由");
                    break;
                    
                case "update_attribute":
                    response.setAction("update_attribute");
                    response.setActionParams(entities);
                    response.setMessage("将更新属性");
                    break;
                    
                case "delete_element":
                    response.setAction("delete_element");
                    response.setActionParams(entities);
                    response.setMessage("将删除元素");
                    break;
                    
                case "get_suggestion":
                    List<NlpSuggestion> suggestions = getSuggestions(context);
                    response.setSuggestions(suggestions);
                    response.setAction("show_suggestions");
                    response.setMessage("以下是建议");
                    break;
                    
                case "validate_process":
                    if (context != null && context.getCurrentProcess() != null) {
                        String validation = validateAndFix(context.getCurrentProcess(), context);
                        response.setAction("show_validation");
                        response.setMessage(validation);
                    } else {
                        response.setMessage("没有可验证的流程");
                    }
                    break;
                    
                case "describe_element":
                    if (context != null) {
                        if (context.getCurrentActivity() != null) {
                            String desc = generateActivityDescription(context.getCurrentActivity());
                            response.setAction("show_description");
                            response.setMessage(desc);
                        } else if (context.getCurrentProcess() != null) {
                            String desc = generateDescription(context.getCurrentProcess());
                            response.setAction("show_description");
                            response.setMessage(desc);
                        } else {
                            response.setMessage("没有可描述的元素");
                        }
                    } else {
                        response.setMessage("没有可描述的元素");
                    }
                    break;
                    
                default:
                    response.setMessage("正在处理您的请求");
            }
            
            response.setSuccess(true);
            
        } catch (Exception e) {
            log.error("Error processing NLP input", e);
            response.setSuccess(false);
            response.setMessage("处理失败: " + e.getMessage());
        }

        return response;
    }

    @Override
    public ProcessDefDTO createProcessFromNlp(String description, DesignerContextDTO context) {
        log.info("Creating process from description: {}", description);

        ProcessDefDTO process = new ProcessDefDTO();
        
        String processId = extractProcessId(description);
        process.setProcessDefId(processId);
        
        String processName = extractProcessName(description);
        process.setName(processName);
        
        process.setDescription(description);
        process.setClassification("NORMAL");
        process.setAccessLevel("PUBLIC");
        process.setVersion(1);
        process.setPublicationStatus("DRAFT");
        
        List<ActivityDefDTO> activities = inferActivities(description);
        process.setActivities(activities);
        
        List<RouteDefDTO> routes = inferRoutes(activities);
        process.setRoutes(routes);

        return process;
    }

    @Override
    public ActivityDefDTO createActivityFromNlp(String description, DesignerContextDTO context) {
        log.info("Creating activity from description: {}", description);

        ActivityDefDTO activity = new ActivityDefDTO();
        
        String activityId = extractActivityId(description);
        activity.setActivityDefId(activityId);
        
        String activityName = extractActivityName(description);
        activity.setName(activityName);
        
        String activityType = inferActivityType(description);
        activity.setActivityType(activityType);
        
        String category = inferCategory(description);
        activity.setActivityCategory(category);
        
        String implementation = inferImplementation(activityType, category);
        activity.setImplementation(implementation);
        
        activity.setPosition("NORMAL");
        activity.setDescription(description);

        if ("AGENT".equals(category)) {
            activity.setAgentConfig(inferAgentConfig(description));
        } else if ("SCENE".equals(category)) {
            activity.setSceneConfig(inferSceneConfig(description));
        }

        return activity;
    }

    @Override
    public Map<String, Object> updateAttributeFromNlp(String attributeName, String value, DesignerContextDTO context) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("attribute", attributeName);
        result.put("value", parseAttributeValue(attributeName, value));
        result.put("success", true);
        
        return result;
    }

    @Override
    public List<NlpSuggestion> getSuggestions(DesignerContextDTO context) {
        List<NlpSuggestion> suggestions = new ArrayList<>();

        if (context == null || context.getCurrentProcess() == null) {
            suggestions.add(createSuggestion("create", "创建新流程", 
                "开始创建一个新的流程定义", "create_process"));
            return suggestions;
        }

        ProcessDefDTO process = context.getCurrentProcess();
        
        if (process.getActivities() == null || process.getActivities().isEmpty()) {
            suggestions.add(createSuggestion("add", "添加开始节点", 
                "流程需要一个开始节点", "add_activity"));
            return suggestions;
        }

        boolean hasStart = process.getActivities().stream()
            .anyMatch(a -> "START".equals(a.getActivityType()));
        boolean hasEnd = process.getActivities().stream()
            .anyMatch(a -> "END".equals(a.getActivityType()));
        
        if (!hasStart) {
            suggestions.add(createSuggestion("add", "添加开始节点", 
                "流程缺少开始节点", "add_activity"));
        }
        
        if (!hasEnd) {
            suggestions.add(createSuggestion("add", "添加结束节点", 
                "流程缺少结束节点", "add_activity"));
        }

        if (context.getCurrentActivity() != null) {
            ActivityDefDTO activity = context.getCurrentActivity();
            
            if (activity.getTiming() == null) {
                suggestions.add(createSuggestion("config", "配置时限", 
                    "建议为活动设置处理时限", "update_attribute"));
            }
            
            if ("TASK".equals(activity.getActivityType()) && activity.getRight() == null) {
                suggestions.add(createSuggestion("config", "配置权限", 
                    "建议为任务活动配置办理权限", "update_attribute"));
            }
        }

        return suggestions;
    }

    @Override
    public String validateAndFix(ProcessDefDTO processDef, DesignerContextDTO context) {
        StringBuilder result = new StringBuilder();
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (processDef.getProcessDefId() == null || processDef.getProcessDefId().isEmpty()) {
            errors.add("流程ID不能为空");
        }
        
        if (processDef.getName() == null || processDef.getName().isEmpty()) {
            errors.add("流程名称不能为空");
        }

        if (processDef.getActivities() == null || processDef.getActivities().isEmpty()) {
            errors.add("流程必须包含至少一个活动");
        } else {
            boolean hasStart = false;
            boolean hasEnd = false;
            Set<String> activityIds = new HashSet<>();
            
            for (ActivityDefDTO activity : processDef.getActivities()) {
                if (activity.getActivityDefId() == null || activity.getActivityDefId().isEmpty()) {
                    errors.add("活动ID不能为空");
                } else if (activityIds.contains(activity.getActivityDefId())) {
                    errors.add("活动ID重复: " + activity.getActivityDefId());
                } else {
                    activityIds.add(activity.getActivityDefId());
                }
                
                if ("START".equals(activity.getActivityType())) {
                    hasStart = true;
                }
                if ("END".equals(activity.getActivityType())) {
                    hasEnd = true;
                }
            }
            
            if (!hasStart) {
                warnings.add("流程缺少开始节点");
            }
            if (!hasEnd) {
                warnings.add("流程缺少结束节点");
            }
        }

        if (processDef.getRoutes() != null && !processDef.getRoutes().isEmpty()) {
            Set<String> routeIds = new HashSet<>();
            Set<String> activityIds = processDef.getActivities() != null 
                ? processDef.getActivities().stream()
                    .map(ActivityDefDTO::getActivityDefId)
                    .collect(java.util.stream.Collectors.toSet())
                : Collections.emptySet();
            
            for (RouteDefDTO route : processDef.getRoutes()) {
                if (routeIds.contains(route.getRouteDefId())) {
                    errors.add("路由ID重复: " + route.getRouteDefId());
                } else {
                    routeIds.add(route.getRouteDefId());
                }
                
                if (!activityIds.contains(route.getFrom())) {
                    warnings.add("路由源活动不存在: " + route.getFrom());
                }
                if (!activityIds.contains(route.getTo())) {
                    warnings.add("路由目标活动不存在: " + route.getTo());
                }
            }
        }

        result.append("## 验证结果\n\n");
        
        if (errors.isEmpty() && warnings.isEmpty()) {
            result.append("✅ 流程定义验证通过，没有发现问题。\n");
        } else {
            if (!errors.isEmpty()) {
                result.append("### ❌ 错误 (").append(errors.size()).append(")\n");
                for (String error : errors) {
                    result.append("- ").append(error).append("\n");
                }
                result.append("\n");
            }
            
            if (!warnings.isEmpty()) {
                result.append("### ⚠️ 警告 (").append(warnings.size()).append(")\n");
                for (String warning : warnings) {
                    result.append("- ").append(warning).append("\n");
                }
            }
        }

        return result.toString();
    }

    @Override
    public String generateDescription(ProcessDefDTO processDef) {
        StringBuilder desc = new StringBuilder();
        desc.append("流程《").append(processDef.getName()).append("》");
        
        if (processDef.getDescription() != null && !processDef.getDescription().isEmpty()) {
            desc.append("：").append(processDef.getDescription());
        }
        
        if (processDef.getActivities() != null && !processDef.getActivities().isEmpty()) {
            desc.append("，包含 ").append(processDef.getActivities().size()).append(" 个活动");
        }
        
        if (processDef.getRoutes() != null && !processDef.getRoutes().isEmpty()) {
            desc.append("，").append(processDef.getRoutes().size()).append(" 条路由");
        }
        
        return desc.toString();
    }

    @Override
    public String generateActivityDescription(ActivityDefDTO activityDef) {
        StringBuilder desc = new StringBuilder();
        desc.append("活动《").append(activityDef.getName()).append("》");
        desc.append("，类型：").append(activityDef.getActivityType());
        desc.append("，分类：").append(activityDef.getActivityCategory());
        
        if (activityDef.getDescription() != null && !activityDef.getDescription().isEmpty()) {
            desc.append("。").append(activityDef.getDescription());
        }
        
        return desc.toString();
    }

    @Override
    public List<NlpIntent> analyzeIntent(String userInput) {
        List<NlpIntent> intents = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : INTENT_PATTERNS.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getValue());
            Matcher matcher = pattern.matcher(userInput);
            
            if (matcher.find()) {
                NlpIntent intent = new NlpIntent();
                intent.setName(entry.getKey());
                intent.setConfidence(0.8);
                intent.setCategory(entry.getKey().split("_")[0]);
                intents.add(intent);
            }
        }

        intents.sort((a, b) -> Double.compare(b.getConfidence(), a.getConfidence()));
        
        return intents;
    }

    @Override
    public Map<String, Object> extractEntities(String userInput, String intentType) {
        Map<String, Object> entities = new HashMap<>();
        
        switch (intentType) {
            case "create_process":
                entities.put("processId", extractProcessId(userInput));
                entities.put("processName", extractProcessName(userInput));
                entities.put("description", userInput);
                break;
                
            case "create_activity":
                entities.put("activityId", extractActivityId(userInput));
                entities.put("activityName", extractActivityName(userInput));
                entities.put("activityType", inferActivityType(userInput));
                entities.put("category", inferCategory(userInput));
                break;
                
            case "update_attribute":
                entities.put("attribute", extractAttributeName(userInput));
                entities.put("value", extractAttributeValue(userInput));
                break;
                
            case "delete_element":
                entities.put("elementId", extractElementId(userInput));
                entities.put("elementType", extractElementType(userInput));
                break;
        }
        
        return entities;
    }

    private String extractProcessId(String text) {
        Pattern pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9_-]*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        return "process_" + System.currentTimeMillis();
    }

    private String extractProcessName(String text) {
        Pattern pattern = Pattern.compile("(创建|新建).*(流程|过程)[\"']?([^\"']+)[\"']?");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(3).trim();
        }
        
        pattern = Pattern.compile("([\\u4e00-\\u9fa5]+)流程");
        matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) + "流程";
        }
        
        return "新流程";
    }

    private String extractActivityId(String text) {
        Pattern pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9_-]*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).toLowerCase();
        }
        return "activity_" + System.currentTimeMillis();
    }

    private String extractActivityName(String text) {
        Pattern pattern = Pattern.compile("(创建|新建|添加).*(节点|活动|任务)[\"']?([^\"']+)[\"']?");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(3).trim();
        }
        
        pattern = Pattern.compile("([\\u4e00-\\u9fa5]+)(审批|处理|任务)");
        matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) + matcher.group(2);
        }
        
        return "新活动";
    }

    private String inferActivityType(String text) {
        for (Map.Entry<String, String> entry : ACTIVITY_TYPE_KEYWORDS.entrySet()) {
            if (Pattern.compile(entry.getValue()).matcher(text).find()) {
                return entry.getKey();
            }
        }
        return "TASK";
    }

    private String inferCategory(String text) {
        for (Map.Entry<String, String> entry : CATEGORY_KEYWORDS.entrySet()) {
            if (Pattern.compile(entry.getValue()).matcher(text).find()) {
                return entry.getKey();
            }
        }
        return "HUMAN";
    }

    private String inferImplementation(String activityType, String category) {
        if ("START".equals(activityType) || "END".equals(activityType)) {
            return "IMPL_NO";
        }
        if ("SERVICE".equals(activityType)) {
            return "IMPL_SERVICE";
        }
        if ("AGENT".equals(category)) {
            return "IMPL_AGENT";
        }
        return "IMPL_NO";
    }

    private List<ActivityDefDTO> inferActivities(String description) {
        List<ActivityDefDTO> activities = new ArrayList<>();
        
        activities.add(createDefaultActivity("start", "开始", "START", "HUMAN", "START"));
        
        if (description.contains("审批") || description.contains("流程")) {
            activities.add(createDefaultActivity("submit", "提交申请", "TASK", "HUMAN", "NORMAL"));
            activities.add(createDefaultActivity("approve", "审批", "TASK", "HUMAN", "NORMAL"));
        }
        
        activities.add(createDefaultActivity("end", "结束", "END", "HUMAN", "END"));
        
        return activities;
    }

    private List<RouteDefDTO> inferRoutes(List<ActivityDefDTO> activities) {
        List<RouteDefDTO> routes = new ArrayList<>();
        
        for (int i = 0; i < activities.size() - 1; i++) {
            RouteDefDTO route = new RouteDefDTO();
            route.setRouteDefId("route_" + i);
            route.setFrom(activities.get(i).getActivityDefId());
            route.setTo(activities.get(i + 1).getActivityDefId());
            routes.add(route);
        }
        
        return routes;
    }

    private ActivityDefDTO createDefaultActivity(String id, String name, String type, String category, String position) {
        ActivityDefDTO activity = new ActivityDefDTO();
        activity.setActivityDefId(id);
        activity.setName(name);
        activity.setActivityType(type);
        activity.setActivityCategory(category);
        activity.setImplementation(inferImplementation(type, category));
        activity.setPosition(position);
        return activity;
    }

    private Map<String, Object> inferAgentConfig(String description) {
        Map<String, Object> config = new HashMap<>();
        config.put("agentType", "LLM_AGENT");
        config.put("status", "online");
        config.put("role", "worker");
        return config;
    }

    private Map<String, Object> inferSceneConfig(String description) {
        Map<String, Object> config = new HashMap<>();
        config.put("sceneType", "AUTO");
        config.put("status", "ENABLED");
        return config;
    }

    private String extractAttributeName(String text) {
        String[] attributes = {"时限", "名称", "描述", "类型", "分类", "实现", "权限"};
        for (String attr : attributes) {
            if (text.contains(attr)) {
                return attr;
            }
        }
        return null;
    }

    private String extractAttributeValue(String text) {
        Pattern pattern = Pattern.compile("(设置|修改|更新|更改).*为[\"']?([^\"']+)[\"']?");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(2).trim();
        }
        return null;
    }

    private String extractElementId(String text) {
        Pattern pattern = Pattern.compile("([a-zA-Z][a-zA-Z0-9_-]*)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractElementType(String text) {
        if (text.contains("活动") || text.contains("节点") || text.contains("任务")) {
            return "activity";
        }
        if (text.contains("路由") || text.contains("连线")) {
            return "route";
        }
        return null;
    }

    private Object parseAttributeValue(String attributeName, String value) {
        if (attributeName == null) return value;
        
        switch (attributeName) {
            case "时限":
                try {
                    return Integer.parseInt(value.replaceAll("[^0-9]", ""));
                } catch (NumberFormatException e) {
                    return value;
                }
            case "版本":
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return value;
                }
            default:
                return value;
        }
    }

    private NlpSuggestion createSuggestion(String type, String title, String description, String action) {
        NlpSuggestion suggestion = new NlpSuggestion();
        suggestion.setType(type);
        suggestion.setTitle(title);
        suggestion.setDescription(description);
        suggestion.setAction(action);
        return suggestion;
    }

}
