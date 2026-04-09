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
public class SceneFunctionTools {
    
    private static final Logger log = LoggerFactory.getLogger(SceneFunctionTools.class);
    
    private final DesignerFunctionRegistry functionRegistry;
    private final DataSourceAdapter dataSourceAdapter;
    private final DataSourceConfig dataSourceConfig;
    
    @Autowired
    public SceneFunctionTools(
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
            .name("list_scene_templates")
            .description("列出所有场景模板")
            .category(DesignerFunctionDefinition.FunctionCategory.SCENE)
            .addParameter("category", "string", "场景分类过滤", false)
            .addParameter("status", "string", "模板状态：PUBLISHED, DRAFT", false)
            .handler(this::handleListSceneTemplates)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_scene_template")
            .description("获取场景模板详情")
            .category(DesignerFunctionDefinition.FunctionCategory.SCENE)
            .addParameter("templateId", "string", "模板ID", true)
            .handler(this::handleGetSceneTemplate)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_scene_capabilities")
            .description("获取场景绑定的能力列表")
            .category(DesignerFunctionDefinition.FunctionCategory.SCENE)
            .addParameter("sceneGroupId", "string", "场景组ID", true)
            .handler(this::handleGetSceneCapabilities)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("list_scene_groups")
            .description("列出所有场景组")
            .category(DesignerFunctionDefinition.FunctionCategory.SCENE)
            .addParameter("status", "string", "场景状态：ACTIVE, INACTIVE", false)
            .handler(this::handleListSceneGroups)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_scene_participants")
            .description("获取场景参与者列表")
            .category(DesignerFunctionDefinition.FunctionCategory.SCENE)
            .addParameter("sceneGroupId", "string", "场景组ID", true)
            .handler(this::handleGetSceneParticipants)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("match_scene_by_activity")
            .description("根据活动描述匹配场景模板")
            .category(DesignerFunctionDefinition.FunctionCategory.SCENE)
            .addParameter("activityDesc", "string", "活动描述", true)
            .addParameter("activityType", "string", "活动类型", false)
            .handler(this::handleMatchSceneByActivity)
            .build());
        
        log.info("Registered {} scene functions", 6);
    }
    
    private Object handleListSceneTemplates(Map<String, Object> args) {
        String category = (String) args.get("category");
        String status = (String) args.get("status");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> templates = dataSourceAdapter.listSceneTemplates(tenantId, category);
            return wrapResult(templates);
        }
        
        return buildMockSceneTemplates(category, status);
    }
    
    private Object handleGetSceneTemplate(Map<String, Object> args) {
        String templateId = (String) args.get("templateId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            Map<String, Object> template = dataSourceAdapter.getSceneTemplate(tenantId, templateId);
            return wrapResult(template);
        }
        
        return buildMockSceneTemplateDetail(templateId);
    }
    
    private Object handleGetSceneCapabilities(Map<String, Object> args) {
        String sceneGroupId = (String) args.get("sceneGroupId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> capabilities = dataSourceAdapter.getSceneCapabilities(tenantId, sceneGroupId);
            return wrapResult(capabilities);
        }
        
        return buildMockSceneCapabilities(sceneGroupId);
    }
    
    private Object handleListSceneGroups(Map<String, Object> args) {
        String status = (String) args.get("status");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> groups = dataSourceAdapter.listSceneGroups(tenantId);
            return wrapResult(groups);
        }
        
        return buildMockSceneGroups(status);
    }
    
    private Object handleGetSceneParticipants(Map<String, Object> args) {
        String sceneGroupId = (String) args.get("sceneGroupId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> participants = dataSourceAdapter.getSceneParticipants(tenantId, sceneGroupId);
            return wrapResult(participants);
        }
        
        return buildMockSceneParticipants(sceneGroupId);
    }
    
    private Object handleListSceneTemplates(Map<String, Object> args) {
        String category = (String) args.get("category");
        String status = (String) args.get("status");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> templates = dataSourceAdapter.listSceneTemplates(tenantId);
            return wrapResult(templates);
        }
        
        return buildMockSceneTemplates(category, status);
    }
    
    private Map<String, Object> wrapResult(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("source", "real");
        return result;
    }
    
    private Object buildMockSceneTemplates(String category, String status) {
        List<Map<String, Object>> templates = new ArrayList<>();
        
        templates.add(Map.of(
            "templateId", "scene-tpl-001",
            "templateName", "招聘流程场景",
            "description", "完整的招聘流程场景模板，包含简历筛选、面试安排、Offer审批等",
            "category", "HR",
            "status", "PUBLISHED",
            "version", "1.0.0",
            "capabilityCount", 5
        ));
        templates.add(Map.of(
            "templateId", "scene-tpl-002",
            "templateName", "报销审批场景",
            "description", "费用报销审批场景模板，包含申请、审批、财务处理等",
            "category", "FIN",
            "status", "PUBLISHED",
            "version", "1.2.0",
            "capabilityCount", 3
        ));
        templates.add(Map.of(
            "templateId", "scene-tpl-003",
            "templateName", "项目立项场景",
            "description", "项目立项审批场景模板，包含需求评审、资源分配、启动等",
            "category", "PM",
            "status", "PUBLISHED",
            "version", "1.0.0",
            "capabilityCount", 4
        ));
        templates.add(Map.of(
            "templateId", "scene-tpl-004",
            "templateName", "合同审批场景",
            "description", "合同审批流程场景模板，包含起草、法务审核、签署等",
            "category", "LEGAL",
            "status", "PUBLISHED",
            "version", "1.1.0",
            "capabilityCount", 4
        ));
        
        if (category != null) {
            templates = templates.stream()
                .filter(t -> category.equalsIgnoreCase((String) t.get("category")))
                .collect(Collectors.toList());
        }
        
        return Map.of(
            "success", true,
            "data", templates,
            "count", templates.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockSceneTemplateDetail(String templateId) {
        Map<String, Map<String, Object>> details = new HashMap<>();
        
        details.put("scene-tpl-001", Map.of(
            "templateId", "scene-tpl-001",
            "templateName", "招聘流程场景",
            "description", "完整的招聘流程场景模板",
            "category", "HR",
            "status", "PUBLISHED",
            "version", "1.0.0",
            "capabilities", List.of(
                Map.of("capId", "resume_screening", "capName", "简历筛选", "required", true),
                Map.of("capId", "interview_schedule", "capName", "面试安排", "required", true),
                Map.of("capId", "notification_send", "capName", "通知发送", "required", false)
            ),
            "participants", List.of(
                Map.of("type", "ROLE", "id", "hr_specialist", "name", "HR专员"),
                Map.of("type", "ROLE", "id", "hr_manager", "name", "HR经理"),
                Map.of("type", "ROLE", "id", "dept_leader", "name", "部门负责人")
            ),
            "workflow", Map.of(
                "type", "SEQUENTIAL",
                "steps", List.of(
                    Map.of("stepId", "step-1", "stepName", "简历筛选", "assignee", "hr_specialist"),
                    Map.of("stepId", "step-2", "stepName", "面试安排", "assignee", "hr_specialist"),
                    Map.of("stepId", "step-3", "stepName", "Offer审批", "assignee", "hr_manager")
                )
            )
        ));
        
        return Map.of(
            "success", true,
            "data", details.getOrDefault(templateId, Map.of(
                "templateId", templateId,
                "templateName", "未知模板",
                "status", "NOT_FOUND"
            )),
            "source", "mock"
        );
    }
    
    private Object buildMockSceneCapabilities(String sceneGroupId) {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        capabilities.add(Map.of(
            "capId", "resume_screening",
            "capName", "简历筛选",
            "connectorType", "SDK",
            "priority", 1,
            "required", true
        ));
        capabilities.add(Map.of(
            "capId", "interview_schedule",
            "capName", "面试安排",
            "connectorType", "SDK",
            "priority", 2,
            "required", true
        ));
        capabilities.add(Map.of(
            "capId", "notification_send",
            "capName", "通知发送",
            "connectorType", "SDK",
            "priority", 3,
            "required", false
        ));
        
        return Map.of(
            "success", true,
            "data", capabilities,
            "sceneGroupId", sceneGroupId,
            "count", capabilities.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockSceneGroups(String status) {
        List<Map<String, Object>> groups = new ArrayList<>();
        
        groups.add(Map.of(
            "sceneGroupId", "scene-grp-001",
            "sceneGroupName", "2024春季招聘",
            "templateId", "scene-tpl-001",
            "templateName", "招聘流程场景",
            "status", "ACTIVE",
            "createdAt", "2024-03-01",
            "participantCount", 5
        ));
        groups.add(Map.of(
            "sceneGroupId", "scene-grp-002",
            "sceneGroupName", "财务报销处理",
            "templateId", "scene-tpl-002",
            "templateName", "报销审批场景",
            "status", "ACTIVE",
            "createdAt", "2024-03-15",
            "participantCount", 3
        ));
        
        if (status != null) {
            groups = groups.stream()
                .filter(g -> status.equalsIgnoreCase((String) g.get("status")))
                .collect(Collectors.toList());
        }
        
        return Map.of(
            "success", true,
            "data", groups,
            "count", groups.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockSceneParticipants(String sceneGroupId) {
        List<Map<String, Object>> participants = new ArrayList<>();
        
        participants.add(Map.of(
            "participantId", "part-001",
            "participantType", "USER",
            "userId", "user-001",
            "participantName", "张三",
            "role", "HR专员",
            "status", "ACTIVE"
        ));
        participants.add(Map.of(
            "participantId", "part-002",
            "participantType", "USER",
            "userId", "user-003",
            "participantName", "王五",
            "role", "HR经理",
            "status", "ACTIVE"
        ));
        
        return Map.of(
            "success", true,
            "data", participants,
            "sceneGroupId", sceneGroupId,
            "count", participants.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockSceneMatches(String activityDesc) {
        List<Map<String, Object>> matches = new ArrayList<>();
        
        String lowerDesc = activityDesc.toLowerCase();
        
        if (lowerDesc.contains("招聘") || lowerDesc.contains("简历") || lowerDesc.contains("面试")) {
            matches.add(Map.of(
                "templateId", "scene-tpl-001",
                "templateName", "招聘流程场景",
                "matchScore", 0.95,
                "matchReason", "活动涉及招聘相关内容，该场景模板可直接支持",
                "recommendedCapabilities", List.of("resume_screening", "interview_schedule")
            ));
        }
        
        if (lowerDesc.contains("报销") || lowerDesc.contains("费用") || lowerDesc.contains("审批")) {
            matches.add(Map.of(
                "templateId", "scene-tpl-002",
                "templateName", "报销审批场景",
                "matchScore", 0.92,
                "matchReason", "活动涉及报销审批，该场景模板可支持",
                "recommendedCapabilities", List.of("budget_approval", "notification_send")
            ));
        }
        
        if (lowerDesc.contains("项目") || lowerDesc.contains("立项")) {
            matches.add(Map.of(
                "templateId", "scene-tpl-003",
                "templateName", "项目立项场景",
                "matchScore", 0.90,
                "matchReason", "活动涉及项目管理，该场景模板可支持",
                "recommendedCapabilities", List.of("document_review", "notification_send")
            ));
        }
        
        if (lowerDesc.contains("合同") || lowerDesc.contains("签署")) {
            matches.add(Map.of(
                "templateId", "scene-tpl-004",
                "templateName", "合同审批场景",
                "matchScore", 0.88,
                "matchReason", "活动涉及合同处理，该场景模板可支持",
                "recommendedCapabilities", List.of("document_review", "notification_send")
            ));
        }
        
        return Map.of(
            "success", true,
            "data", matches,
            "activityDesc", activityDesc,
            "count", matches.size(),
            "source", "mock"
        );
    }
}
