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
public class CapabilityFunctionTools {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityFunctionTools.class);
    
    private final DesignerFunctionRegistry functionRegistry;
    private final DataSourceAdapter dataSourceAdapter;
    private final DataSourceConfig dataSourceConfig;
    
    @Autowired
    public CapabilityFunctionTools(
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
            .name("list_capabilities")
            .description("列出所有可用能力")
            .category(DesignerFunctionDefinition.FunctionCategory.CAPABILITY)
            .addParameter("category", "string", "能力分类过滤", false)
            .addParameter("status", "string", "能力状态：ACTIVE, INACTIVE", false)
            .handler(this::handleListCapabilities)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("search_capabilities")
            .description("语义搜索能力，根据关键词匹配能力")
            .category(DesignerFunctionDefinition.FunctionCategory.CAPABILITY)
            .addParameter("query", "string", "搜索关键词", true)
            .addParameter("category", "string", "限定能力分类", false)
            .addParameter("limit", "integer", "返回数量限制，默认10", false)
            .handler(this::handleSearchCapabilities)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_capability_detail")
            .description("获取能力详细信息")
            .category(DesignerFunctionDefinition.FunctionCategory.CAPABILITY)
            .addParameter("capId", "string", "能力ID", true)
            .handler(this::handleGetCapabilityDetail)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_capability_skills")
            .description("获取能力关联的技能列表")
            .category(DesignerFunctionDefinition.FunctionCategory.CAPABILITY)
            .addParameter("capId", "string", "能力ID", true)
            .handler(this::handleGetCapabilitySkills)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("match_capability_by_activity")
            .description("根据活动描述匹配最合适的能力")
            .category(DesignerFunctionDefinition.FunctionCategory.CAPABILITY)
            .addParameter("activityDesc", "string", "活动描述", true)
            .addParameter("activityType", "string", "活动类型：HUMAN, AGENT, SCENE", false)
            .addParameter("context", "object", "上下文信息", false)
            .handler(this::handleMatchCapabilityByActivity)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("get_capability_providers")
            .description("获取能力的提供者列表（技能或Agent）")
            .category(DesignerFunctionDefinition.FunctionCategory.CAPABILITY)
            .addParameter("capId", "string", "能力ID", true)
            .handler(this::handleGetCapabilityProviders)
            .build());
        
        functionRegistry.registerFunction(DesignerFunctionDefinition.builder()
            .name("list_capability_categories")
            .description("列出所有能力分类")
            .category(DesignerFunctionDefinition.FunctionCategory.CAPABILITY)
            .handler(this::handleListCapabilityCategories)
            .build());
        
        log.info("Registered {} capability functions", 7);
    }
    
    private Object handleListCapabilities(Map<String, Object> args) {
        String category = (String) args.get("category");
        String status = (String) args.get("status");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> capabilities = dataSourceAdapter.listCapabilities(tenantId);
            return wrapResult(capabilities);
        }
        
        return buildMockCapabilities(category, status);
    }
    
    private Object handleSearchCapabilities(Map<String, Object> args) {
        String query = (String) args.get("query");
        String category = (String) args.get("category");
        Integer limit = args.get("limit") != null ? ((Number) args.get("limit")).intValue() : 10;
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> capabilities = dataSourceAdapter.searchCapabilities(tenantId, query, category);
            return wrapResult(capabilities);
        }
        
        return buildMockSearchCapabilities(query, category, limit);
    }
    
    private Object handleGetCapabilityDetail(Map<String, Object> args) {
        String capId = (String) args.get("capId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            Map<String, Object> capability = dataSourceAdapter.getCapabilityDetail(tenantId, capId);
            return wrapResult(capability);
        }
        
        return buildMockCapabilityDetail(capId);
    }
    
    private Object handleGetCapabilitySkills(Map<String, Object> args) {
        String capId = (String) args.get("capId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> skills = dataSourceAdapter.getCapabilitySkills(tenantId, capId);
            return wrapResult(skills);
        }
        
        return buildMockCapabilitySkills(capId);
    }
    
    private Object handleMatchCapabilityByActivity(Map<String, Object> args) {
        String activityDesc = (String) args.get("activityDesc");
        String activityType = (String) args.get("activityType");
        @SuppressWarnings("unchecked")
        Map<String, Object> context = (Map<String, Object>) args.get("context");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> matches = dataSourceAdapter.matchCapabilityByActivity(tenantId, activityDesc);
            return wrapResult(matches);
        }
        
        return buildMockCapabilityMatches(activityDesc, activityType);
    }
    
    private Object handleGetCapabilityProviders(Map<String, Object> args) {
        String capId = (String) args.get("capId");
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> providers = dataSourceAdapter.getCapabilityProviders(tenantId, capId);
            return wrapResult(providers);
        }
        
        return buildMockCapabilityProviders(capId);
    }
    
    private Object handleListCapabilityCategories(Map<String, Object> args) {
        String tenantId = "default";
        
        if (dataSourceConfig.isUseRealData()) {
            List<Map<String, Object>> categories = dataSourceAdapter.listCapabilityCategories(tenantId);
            return wrapResult(categories);
        }
        
        return buildMockCategories();
    }
    
    private Map<String, Object> wrapResult(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        result.put("source", "real");
        return result;
    }
    
    private Object buildMockCapabilities(String category, String status) {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        capabilities.add(Map.of(
            "capId", "resume_screening",
            "capName", "简历筛选",
            "description", "自动筛选候选人简历，评估匹配度",
            "category", "HR",
            "status", "ACTIVE",
            "providerType", "SKILL"
        ));
        capabilities.add(Map.of(
            "capId", "interview_schedule",
            "capName", "面试安排",
            "description", "安排面试时间和地点，发送通知",
            "category", "HR",
            "status", "ACTIVE",
            "providerType", "SKILL"
        ));
        capabilities.add(Map.of(
            "capId", "calendar_schedule",
            "capName", "日程安排",
            "description", "管理日程，安排会议和提醒",
            "category", "ADMIN",
            "status", "ACTIVE",
            "providerType", "SKILL"
        ));
        capabilities.add(Map.of(
            "capId", "meeting_arrange",
            "capName", "会议管理",
            "description", "创建和管理会议，发送邀请",
            "category", "ADMIN",
            "status", "ACTIVE",
            "providerType", "SKILL"
        ));
        capabilities.add(Map.of(
            "capId", "notification_send",
            "capName", "通知发送",
            "description", "发送各类通知消息",
            "category", "ADMIN",
            "status", "ACTIVE",
            "providerType", "SKILL"
        ));
        capabilities.add(Map.of(
            "capId", "document_review",
            "capName", "文档审核",
            "description", "审核文档内容和格式",
            "category", "ADMIN",
            "status", "ACTIVE",
            "providerType", "AGENT"
        ));
        capabilities.add(Map.of(
            "capId", "budget_approval",
            "capName", "预算审批",
            "description", "审批预算申请",
            "category", "FIN",
            "status", "ACTIVE",
            "providerType", "WORKFLOW"
        ));
        capabilities.add(Map.of(
            "capId", "code_review",
            "capName", "代码审查",
            "description", "自动审查代码质量和规范",
            "category", "TECH",
            "status", "ACTIVE",
            "providerType", "AGENT"
        ));
        
        if (category != null) {
            capabilities = capabilities.stream()
                .filter(c -> category.equalsIgnoreCase((String) c.get("category")))
                .collect(Collectors.toList());
        }
        
        return Map.of(
            "success", true,
            "data", capabilities,
            "count", capabilities.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockSearchCapabilities(String query, String category, int limit) {
        List<Map<String, Object>> allCapabilities = new ArrayList<>();
        
        allCapabilities.add(Map.of(
            "capId", "resume_screening",
            "capName", "简历筛选",
            "description", "自动筛选候选人简历，评估匹配度",
            "category", "HR",
            "status", "ACTIVE"
        ));
        allCapabilities.add(Map.of(
            "capId", "interview_schedule",
            "capName", "面试安排",
            "description", "安排面试时间和地点，发送通知",
            "category", "HR",
            "status", "ACTIVE"
        ));
        allCapabilities.add(Map.of(
            "capId", "calendar_schedule",
            "capName", "日程安排",
            "description", "管理日程，安排会议和提醒",
            "category", "ADMIN",
            "status", "ACTIVE"
        ));
        
        String lowerQuery = query.toLowerCase();
        
        List<Map<String, Object>> results = allCapabilities.stream()
            .filter(c -> {
                String name = ((String) c.get("capName")).toLowerCase();
                String desc = ((String) c.get("description")).toLowerCase();
                return name.contains(lowerQuery) || desc.contains(lowerQuery);
            })
            .filter(c -> category == null || category.equalsIgnoreCase((String) c.get("category")))
            .limit(limit)
            .map(c -> {
                Map<String, Object> result = new HashMap<>(c);
                result.put("matchScore", 0.85 + Math.random() * 0.15);
                return result;
            })
            .collect(Collectors.toList());
        
        return Map.of(
            "success", true,
            "data", results,
            "query", query,
            "count", results.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockCapabilityDetail(String capId) {
        Map<String, Map<String, Object>> details = new HashMap<>();
        
        details.put("resume_screening", Map.of(
            "capId", "resume_screening",
            "capName", "简历筛选",
            "description", "自动筛选候选人简历，评估匹配度",
            "category", "HR",
            "status", "ACTIVE",
            "providerType", "SKILL",
            "parameters", List.of(
                Map.of("name", "resumeUrl", "type", "string", "description", "简历文件URL", "required", true),
                Map.of("name", "jobRequirements", "type", "object", "description", "岗位要求", "required", true),
                Map.of("name", "threshold", "type", "number", "description", "匹配阈值", "required", false)
            ),
            "returns", Map.of(
                "type", "object",
                "properties", Map.of(
                    "score", "匹配分数",
                    "matchedSkills", "匹配的技能列表",
                    "recommendation", "推荐意见"
                )
            ),
            "permissions", List.of("hr:resume:read", "hr:resume:evaluate")
        ));
        
        details.put("interview_schedule", Map.of(
            "capId", "interview_schedule",
            "capName", "面试安排",
            "description", "安排面试时间和地点，发送通知",
            "category", "HR",
            "status", "ACTIVE",
            "providerType", "SKILL",
            "parameters", List.of(
                Map.of("name", "candidateId", "type", "string", "description", "候选人ID", "required", true),
                Map.of("name", "interviewers", "type", "array", "description", "面试官列表", "required", true),
                Map.of("name", "duration", "type", "number", "description", "面试时长(分钟)", "required", false)
            ),
            "returns", Map.of(
                "type", "object",
                "properties", Map.of(
                    "interviewId", "面试ID",
                    "scheduledTime", "安排时间",
                    "location", "地点"
                )
            ),
            "permissions", List.of("hr:interview:create", "hr:interview:notify")
        ));
        
        return Map.of(
            "success", true,
            "data", details.getOrDefault(capId, Map.of(
                "capId", capId,
                "capName", "未知能力",
                "status", "NOT_FOUND"
            )),
            "source", "mock"
        );
    }
    
    private Object buildMockCapabilitySkills(String capId) {
        List<Map<String, Object>> skills = new ArrayList<>();
        
        skills.add(Map.of(
            "skillId", "skill-001",
            "skillName", "简历解析器",
            "version", "1.0.0",
            "status", "ACTIVE"
        ));
        skills.add(Map.of(
            "skillId", "skill-002",
            "skillName", "NLP分析器",
            "version", "2.1.0",
            "status", "ACTIVE"
        ));
        
        return Map.of(
            "success", true,
            "data", skills,
            "capId", capId,
            "count", skills.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockCapabilityMatches(String activityDesc, String activityType) {
        List<Map<String, Object>> matches = new ArrayList<>();
        
        String lowerDesc = activityDesc.toLowerCase();
        
        if (lowerDesc.contains("简历") || lowerDesc.contains("筛选")) {
            matches.add(Map.of(
                "capId", "resume_screening",
                "capName", "简历筛选",
                "matchScore", 0.95,
                "matchReason", "活动涉及简历筛选，该能力可直接支持",
                "bindingConfig", Map.of(
                    "priority", 1,
                    "connectorType", "SDK",
                    "autoTrigger", true
                )
            ));
        }
        
        if (lowerDesc.contains("面试") || lowerDesc.contains("约谈")) {
            matches.add(Map.of(
                "capId", "interview_schedule",
                "capName", "面试安排",
                "matchScore", 0.92,
                "matchReason", "活动涉及面试安排，该能力可自动化处理",
                "bindingConfig", Map.of(
                    "priority", 1,
                    "connectorType", "SDK",
                    "autoTrigger", true
                )
            ));
            matches.add(Map.of(
                "capId", "calendar_schedule",
                "capName", "日程安排",
                "matchScore", 0.85,
                "matchReason", "面试需要日程管理支持",
                "bindingConfig", Map.of(
                    "priority", 2,
                    "connectorType", "SDK",
                    "autoTrigger", false
                )
            ));
            matches.add(Map.of(
                "capId", "notification_send",
                "capName", "通知发送",
                "matchScore", 0.80,
                "matchReason", "面试安排需要发送通知",
                "bindingConfig", Map.of(
                    "priority", 3,
                    "connectorType", "SDK",
                    "autoTrigger", false
                )
            ));
        }
        
        if (lowerDesc.contains("审批") || lowerDesc.contains("审核")) {
            matches.add(Map.of(
                "capId", "document_review",
                "capName", "文档审核",
                "matchScore", 0.88,
                "matchReason", "活动涉及审批审核，该能力可辅助处理",
                "bindingConfig", Map.of(
                    "priority", 1,
                    "connectorType", "AGENT",
                    "autoTrigger", false
                )
            ));
        }
        
        if (matches.isEmpty()) {
            matches.add(Map.of(
                "capId", "notification_send",
                "capName", "通知发送",
                "matchScore", 0.60,
                "matchReason", "通用通知能力，可用于活动通知",
                "bindingConfig", Map.of(
                    "priority", 5,
                    "connectorType", "SDK",
                    "autoTrigger", false
                )
            ));
        }
        
        return Map.of(
            "success", true,
            "data", matches,
            "activityDesc", activityDesc,
            "activityType", activityType,
            "count", matches.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockCapabilityProviders(String capId) {
        List<Map<String, Object>> providers = new ArrayList<>();
        
        providers.add(Map.of(
            "providerId", "skill-001",
            "providerName", "简历解析器",
            "providerType", "SKILL",
            "version", "1.0.0",
            "status", "ACTIVE",
            "priority", 1
        ));
        
        providers.add(Map.of(
            "providerId", "agent-001",
            "providerName", "HR助手Agent",
            "providerType", "AGENT",
            "version", "2.0.0",
            "status", "ACTIVE",
            "priority", 2
        ));
        
        return Map.of(
            "success", true,
            "data", providers,
            "capId", capId,
            "count", providers.size(),
            "source", "mock"
        );
    }
    
    private Object buildMockCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        categories.add(Map.of(
            "categoryId", "HR",
            "categoryName", "人力资源",
            "description", "招聘、培训、绩效等HR相关能力",
            "capabilityCount", 15
        ));
        categories.add(Map.of(
            "categoryId", "TECH",
            "categoryName", "技术开发",
            "description", "开发、测试、运维等技术能力",
            "capabilityCount", 25
        ));
        categories.add(Map.of(
            "categoryId", "FIN",
            "categoryName", "财务管理",
            "description", "预算、报销、审批等财务能力",
            "capabilityCount", 10
        ));
        categories.add(Map.of(
            "categoryId", "ADMIN",
            "categoryName", "行政管理",
            "description", "会议、日程、文档等行政能力",
            "capabilityCount", 12
        ));
        categories.add(Map.of(
            "categoryId", "AI",
            "categoryName", "AI智能",
            "description", "NLP、图像识别、推荐等AI能力",
            "capabilityCount", 8
        ));
        
        return Map.of(
            "success", true,
            "data", categories,
            "count", categories.size(),
            "source", "mock"
        );
    }
}
