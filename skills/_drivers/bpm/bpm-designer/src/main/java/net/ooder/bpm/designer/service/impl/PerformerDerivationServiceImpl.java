package net.ooder.bpm.designer.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.designer.cache.CacheKeyGenerator;
import net.ooder.bpm.designer.cache.CacheService;
import net.ooder.bpm.designer.function.DesignerFunctionRegistry;
import net.ooder.bpm.designer.llm.FunctionCall;
import net.ooder.bpm.designer.llm.LLMResponse;
import net.ooder.bpm.designer.llm.LLMService;
import net.ooder.bpm.designer.model.dto.DesignerContextDTO;
import net.ooder.bpm.designer.model.dto.FunctionCallTraceDTO;
import net.ooder.bpm.designer.model.dto.PerformerDerivationResultDTO;
import net.ooder.bpm.designer.model.dto.PerformerDerivationResultDTO.PerformerCandidate;
import net.ooder.bpm.designer.prompt.PromptTemplateManager;
import net.ooder.bpm.designer.service.PerformerDerivationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformerDerivationServiceImpl implements PerformerDerivationService {
    
    private static final Logger log = LoggerFactory.getLogger(PerformerDerivationServiceImpl.class);
    
    private final DesignerFunctionRegistry functionRegistry;
    private final LLMService llmService;
    private final PromptTemplateManager promptTemplateManager;
    private final CacheService cacheService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public PerformerDerivationServiceImpl(
            DesignerFunctionRegistry functionRegistry,
            @Autowired(required = false) LLMService llmService,
            PromptTemplateManager promptTemplateManager,
            CacheService cacheService) {
        this.functionRegistry = functionRegistry;
        this.llmService = llmService;
        this.promptTemplateManager = promptTemplateManager;
        this.cacheService = cacheService;
    }
    
    private static final Map<String, String> ROLE_KEYWORDS = new HashMap<>() {{
        put("hr", "hr_specialist");
        put("人力资源", "hr_specialist");
        put("招聘", "hr_specialist");
        put("简历", "hr_specialist");
        put("面试", "hr_specialist");
        put("人事", "hr_specialist");
        put("技术", "tech_leader");
        put("开发", "developer");
        put("代码", "developer");
        put("财务", "finance_manager");
        put("预算", "finance_manager");
        put("报销", "finance_manager");
        put("领导", "dept_leader");
        put("经理", "manager");
        put("审批", "approver");
    }};
    
    @Override
    public PerformerDerivationResultDTO derive(DesignerContextDTO context, String activityDesc) {
        log.info("Deriving performer for activity: {}", activityDesc);
        
        String tenantId = context != null && context.getUserId() != null ? context.getUserId() : "default";
        String processId = context != null && context.getCurrentProcess() != null ? 
            context.getCurrentProcess().getProcessDefId() : "unknown";
        String cacheKey = CacheKeyGenerator.performerDerivation(tenantId, processId, activityDesc);
        
        Optional<PerformerDerivationResultDTO> cached = cacheService.get(cacheKey, PerformerDerivationResultDTO.class);
        if (cached.isPresent()) {
            log.info("Returning cached performer derivation result");
            return cached.get();
        }
        
        PerformerDerivationResultDTO result;
        if (isLLMAvailable()) {
            result = deriveWithLLM(context, activityDesc);
        } else {
            result = deriveWithRules(context, activityDesc);
        }
        
        if (result.getStatus() == PerformerDerivationResultDTO.DerivationStatus.SUCCESS) {
            cacheService.put(cacheKey, result, Duration.ofMinutes(30));
        }
        
        return result;
    }
    
    private boolean isLLMAvailable() {
        return llmService != null && llmService.isAvailable();
    }
    
    private PerformerDerivationResultDTO deriveWithLLM(DesignerContextDTO context, String activityDesc) {
        log.info("Using LLM for performer derivation");
        
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        try {
            Map<String, Object> promptContext = buildPromptContext(context, activityDesc);
            String systemPrompt = promptTemplateManager.getSystemPrompt("performer-derivation");
            String userPrompt = promptTemplateManager.getUserPrompt("performer-derivation", promptContext);
            
            List<Map<String, Object>> functions = functionRegistry.getOpenAISchemasByCategory(
                net.ooder.bpm.designer.function.DesignerFunctionDefinition.FunctionCategory.ORGANIZATION
            );
            
            LLMResponse response = llmService.chatWithFunctions(systemPrompt, userPrompt, functions);
            
            if (!response.isSuccess()) {
                log.warn("LLM call failed: {}, falling back to rules", response.getError());
                return deriveWithRules(context, activityDesc);
            }
            
            if (response.hasFunctionCalls()) {
                return handleFunctionCalls(context, activityDesc, response, traces);
            }
            
            return parseLLMContentResponse(response.getContent(), activityDesc, traces);
            
        } catch (Exception e) {
            log.error("LLM derivation failed: {}", e.getMessage(), e);
            return deriveWithRules(context, activityDesc);
        }
    }
    
    private PerformerDerivationResultDTO handleFunctionCalls(DesignerContextDTO context, String activityDesc,
            LLMResponse response, List<FunctionCallTraceDTO> traces) {
        
        try {
            for (FunctionCall call : response.getFunctionCalls()) {
                long startTime = System.currentTimeMillis();
                Object result = functionRegistry.executeFunction(call.getName(), call.getArguments());
                long execTime = System.currentTimeMillis() - startTime;
                
                traces.add(FunctionCallTraceDTO.success(
                    traces.size() + 1, call.getName(), call.getArguments(), result, execTime
                ));
                
                LLMResponse followUpResponse = llmService.chatWithFunctionResult(
                    activityDesc, call.getName(), result
                );
                
                if (followUpResponse.isSuccess() && !followUpResponse.hasFunctionCalls()) {
                    return parseLLMContentResponse(followUpResponse.getContent(), activityDesc, traces);
                }
            }
            
            return PerformerDerivationResultDTO.needClarification(
                "LLM无法确定办理人，请提供更多上下文信息"
            );
            
        } catch (Exception e) {
            log.error("Error handling function calls: {}", e.getMessage(), e);
            return PerformerDerivationResultDTO.failed("推导失败: " + e.getMessage());
        }
    }
    
    private PerformerDerivationResultDTO parseLLMContentResponse(String content, String activityDesc,
            List<FunctionCallTraceDTO> traces) {
        
        try {
            JsonNode jsonNode = objectMapper.readTree(extractJson(content));
            
            String assigneeType = jsonNode.path("assigneeType").asText("ROLE");
            String assigneeId = jsonNode.path("assigneeId").asText("");
            String assigneeName = jsonNode.path("assigneeName").asText("");
            String reasoning = jsonNode.path("reasoning").asText("LLM推导结果");
            
            Map<String, Object> derivedConfig = new HashMap<>();
            derivedConfig.put("assigneeType", assigneeType);
            derivedConfig.put("assigneeId", assigneeId);
            derivedConfig.put("assigneeName", assigneeName);
            
            PerformerDerivationResultDTO result = PerformerDerivationResultDTO.success(
                new ArrayList<>(), derivedConfig, reasoning
            );
            result.setFunctionTraces(traces);
            result.setConfidence(0.9);
            
            return result;
            
        } catch (Exception e) {
            log.warn("Failed to parse LLM response: {}", e.getMessage());
            return PerformerDerivationResultDTO.partial(new ArrayList<>(), 
                "LLM响应解析失败，请手动配置办理人");
        }
    }
    
    private String extractJson(String content) {
        if (content == null) return "{}";
        
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        
        return "{}";
    }
    
    private PerformerDerivationResultDTO deriveWithRules(DesignerContextDTO context, String activityDesc) {
        log.info("Using rules for performer derivation");
        
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        try {
            String identifiedRole = identifyRoleFromDescription(activityDesc);
            
            if (identifiedRole == null) {
                return PerformerDerivationResultDTO.needClarification(
                    "无法从活动描述中识别明确的角色需求，请提供更多上下文信息"
                );
            }
            
            long startTime = System.currentTimeMillis();
            Object roleResult = functionRegistry.executeFunction("get_users_by_role", 
                Map.of("roleId", identifiedRole));
            long execTime = System.currentTimeMillis() - startTime;
            
            traces.add(FunctionCallTraceDTO.success(1, "get_users_by_role", 
                Map.of("roleId", identifiedRole), roleResult, execTime));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) roleResult;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> users = (List<Map<String, Object>>) resultMap.get("data");
            
            if (users == null || users.isEmpty()) {
                return PerformerDerivationResultDTO.partial(new ArrayList<>(),
                    "识别到角色 " + identifiedRole + " 但未找到对应用户");
            }
            
            List<PerformerCandidate> candidates = users.stream()
                .map(this::mapToCandidate)
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());
            
            Map<String, Object> derivedConfig = buildDerivedConfig(identifiedRole, candidates);
            
            String reasoning = buildReasoning(activityDesc, identifiedRole, candidates);
            
            PerformerDerivationResultDTO result = PerformerDerivationResultDTO.success(
                candidates, derivedConfig, reasoning
            );
            result.setFunctionTraces(traces);
            
            return result;
            
        } catch (Exception e) {
            log.error("Error deriving performer: {}", e.getMessage(), e);
            return PerformerDerivationResultDTO.failed("推导失败: " + e.getMessage());
        }
    }
    
    @Override
    public PerformerDerivationResultDTO deriveWithCandidates(DesignerContextDTO context, 
            String activityDesc, List<String> candidateUserIds) {
        log.info("Deriving performer with candidates: {}", candidateUserIds);
        
        List<PerformerCandidate> candidates = new ArrayList<>();
        
        for (String userId : candidateUserIds) {
            try {
                Object result = functionRegistry.executeFunction("get_user_info", 
                    Map.of("userId", userId));
                
                @SuppressWarnings("unchecked")
                Map<String, Object> userMap = (Map<String, Object>) ((Map<?, ?>) result).get("data");
                
                PerformerCandidate candidate = new PerformerCandidate();
                candidate.setUserId(userId);
                candidate.setUserName((String) userMap.get("userName"));
                candidate.setDeptId((String) userMap.get("deptId"));
                candidate.setDeptName((String) userMap.get("deptName"));
                candidate.setMatchScore(0.85);
                candidate.setMatchReason("从候选列表中选择");
                
                candidates.add(candidate);
            } catch (Exception e) {
                log.warn("Failed to get user info for {}: {}", userId, e.getMessage());
            }
        }
        
        if (candidates.isEmpty()) {
            return PerformerDerivationResultDTO.failed("无法获取候选人信息");
        }
        
        Map<String, Object> derivedConfig = Map.of(
            "assigneeType", "USER",
            "assigneeId", candidates.get(0).getUserId(),
            "assigneeName", candidates.get(0).getUserName()
        );
        
        return PerformerDerivationResultDTO.success(candidates, derivedConfig, 
            "从指定候选人列表中选择办理人");
    }
    
    @Override
    public List<PerformerCandidate> searchCandidates(String query, Map<String, Object> filters) {
        log.info("Searching candidates with query: {}", query);
        
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("query", query);
            if (filters != null) {
                args.putAll(filters);
            }
            
            Object result = functionRegistry.executeFunction("search_users", args);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> users = (List<Map<String, Object>>) resultMap.get("data");
            
            return users.stream()
                .map(this::mapToCandidate)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error searching candidates: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    private Map<String, Object> buildPromptContext(DesignerContextDTO context, String activityDesc) {
        Map<String, Object> promptContext = new HashMap<>();
        
        promptContext.put("activityDesc", activityDesc);
        
        if (context != null) {
            promptContext.put("processName", context.getCurrentProcess() != null ? 
                context.getCurrentProcess().getName() : "");
            promptContext.put("activityName", context.getCurrentActivity() != null ? 
                context.getCurrentActivity().getName() : "");
        }
        
        return promptContext;
    }
    
    private String identifyRoleFromDescription(String description) {
        String lowerDesc = description.toLowerCase();
        
        for (Map.Entry<String, String> entry : ROLE_KEYWORDS.entrySet()) {
            if (lowerDesc.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    private PerformerCandidate mapToCandidate(Map<String, Object> userMap) {
        PerformerCandidate candidate = new PerformerCandidate();
        candidate.setUserId((String) userMap.get("userId"));
        candidate.setUserName((String) userMap.get("userName"));
        candidate.setDeptId((String) userMap.get("deptId"));
        candidate.setDeptName((String) userMap.get("deptName"));
        
        Object score = userMap.get("matchScore");
        candidate.setMatchScore(score != null ? ((Number) score).doubleValue() : 0.8);
        
        candidate.setMatchReason("根据活动描述匹配");
        
        return candidate;
    }
    
    private Map<String, Object> buildDerivedConfig(String roleId, List<PerformerCandidate> candidates) {
        Map<String, Object> config = new HashMap<>();
        
        config.put("assigneeType", "ROLE");
        config.put("assigneeId", roleId);
        config.put("assigneeName", getRoleDisplayName(roleId));
        
        if (!candidates.isEmpty()) {
            config.put("candidateUsers", candidates.stream()
                .map(PerformerCandidate::getUserId)
                .collect(Collectors.toList()));
        }
        
        return config;
    }
    
    private String getRoleDisplayName(String roleId) {
        Map<String, String> roleNames = new HashMap<>();
        roleNames.put("hr_specialist", "HR专员");
        roleNames.put("hr_manager", "HR经理");
        roleNames.put("tech_leader", "技术负责人");
        roleNames.put("developer", "开发工程师");
        roleNames.put("finance_manager", "财务经理");
        roleNames.put("dept_leader", "部门负责人");
        roleNames.put("manager", "经理");
        roleNames.put("approver", "审批人");
        
        return roleNames.getOrDefault(roleId, roleId);
    }
    
    private String buildReasoning(String activityDesc, String roleId, List<PerformerCandidate> candidates) {
        StringBuilder sb = new StringBuilder();
        sb.append("活动描述\"").append(activityDesc).append("\"中包含");
        sb.append("与\"").append(getRoleDisplayName(roleId)).append("\"相关的关键词，");
        sb.append("因此推荐由该角色执行此活动。");
        
        if (!candidates.isEmpty()) {
            sb.append("找到").append(candidates.size()).append("位符合条件的候选人。");
        }
        
        return sb.toString();
    }
}
