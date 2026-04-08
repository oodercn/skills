package net.ooder.bpm.designer.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.ooder.bpm.designer.cache.CacheKeyGenerator;
import net.ooder.bpm.designer.cache.CacheService;
import net.ooder.bpm.designer.function.DesignerFunctionRegistry;
import net.ooder.bpm.designer.llm.FunctionCall;
import net.ooder.bpm.designer.llm.LLMResponse;
import net.ooder.bpm.designer.llm.LLMService;
import net.ooder.bpm.designer.model.dto.CapabilityMatchingResultDTO;
import net.ooder.bpm.designer.model.dto.CapabilityMatchingResultDTO.CapabilityMatch;
import net.ooder.bpm.designer.model.dto.CapabilityMatchingResultDTO.MatchDimensionScores;
import net.ooder.bpm.designer.model.dto.DesignerContextDTO;
import net.ooder.bpm.designer.model.dto.FunctionCallTraceDTO;
import net.ooder.bpm.designer.prompt.PromptTemplateManager;
import net.ooder.bpm.designer.service.CapabilityMatchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CapabilityMatchingServiceImpl implements CapabilityMatchingService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityMatchingServiceImpl.class);
    
    private final DesignerFunctionRegistry functionRegistry;
    private final LLMService llmService;
    private final PromptTemplateManager promptTemplateManager;
    private final CacheService cacheService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public CapabilityMatchingServiceImpl(
            DesignerFunctionRegistry functionRegistry,
            @Autowired(required = false) LLMService llmService,
            PromptTemplateManager promptTemplateManager,
            CacheService cacheService) {
        this.functionRegistry = functionRegistry;
        this.llmService = llmService;
        this.promptTemplateManager = promptTemplateManager;
        this.cacheService = cacheService;
    }
    
    @Override
    public CapabilityMatchingResultDTO match(DesignerContextDTO context, String activityDesc) {
        log.info("Matching capability for activity: {}", activityDesc);
        
        String tenantId = context != null && context.getUserId() != null ? context.getUserId() : "default";
        String cacheKey = CacheKeyGenerator.capabilityMatching(tenantId, activityDesc, null);
        
        Optional<CapabilityMatchingResultDTO> cached = cacheService.get(cacheKey, CapabilityMatchingResultDTO.class);
        if (cached.isPresent()) {
            log.info("Returning cached capability matching result");
            return cached.get();
        }
        
        CapabilityMatchingResultDTO result;
        if (isLLMAvailable()) {
            result = matchWithLLM(context, activityDesc);
        } else {
            result = matchWithRules(context, activityDesc);
        }
        
        if (result.getStatus() == CapabilityMatchingResultDTO.MatchingStatus.EXACT_MATCH ||
            result.getStatus() == CapabilityMatchingResultDTO.MatchingStatus.PARTIAL_MATCH) {
            cacheService.put(cacheKey, result, Duration.ofMinutes(30));
        }
        
        return result;
    }
    
    private boolean isLLMAvailable() {
        return llmService != null && llmService.isAvailable();
    }
    
    private CapabilityMatchingResultDTO matchWithLLM(DesignerContextDTO context, String activityDesc) {
        log.info("Using LLM for capability matching");
        
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        try {
            Map<String, Object> promptContext = buildPromptContext(context, activityDesc);
            String systemPrompt = promptTemplateManager.getSystemPrompt("capability-matching");
            String userPrompt = promptTemplateManager.getUserPrompt("capability-matching", promptContext);
            
            List<Map<String, Object>> functions = functionRegistry.getOpenAISchemasByCategory(
                net.ooder.bpm.designer.function.DesignerFunctionDefinition.FunctionCategory.CAPABILITY
            );
            
            LLMResponse response = llmService.chatWithFunctions(systemPrompt, userPrompt, functions);
            
            if (!response.isSuccess()) {
                log.warn("LLM call failed: {}, falling back to rules", response.getError());
                return matchWithRules(context, activityDesc);
            }
            
            if (response.hasFunctionCalls()) {
                return handleFunctionCalls(context, activityDesc, response, traces);
            }
            
            return parseLLMContentResponse(response.getContent(), activityDesc, traces);
            
        } catch (Exception e) {
            log.error("LLM matching failed: {}", e.getMessage(), e);
            return matchWithRules(context, activityDesc);
        }
    }
    
    private CapabilityMatchingResultDTO handleFunctionCalls(DesignerContextDTO context, String activityDesc,
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
            
            return CapabilityMatchingResultDTO.noMatch("LLM无法确定匹配的能力");
            
        } catch (Exception e) {
            log.error("Error handling function calls: {}", e.getMessage(), e);
            return CapabilityMatchingResultDTO.noMatch("能力匹配失败: " + e.getMessage());
        }
    }
    
    private CapabilityMatchingResultDTO parseLLMContentResponse(String content, String activityDesc,
            List<FunctionCallTraceDTO> traces) {
        
        try {
            JsonNode jsonNode = objectMapper.readTree(extractJson(content));
            
            List<CapabilityMatch> matches = new ArrayList<>();
            JsonNode matchesNode = jsonNode.path("matches");
            
            if (matchesNode.isArray()) {
                for (JsonNode matchNode : matchesNode) {
                    CapabilityMatch match = new CapabilityMatch();
                    match.setCapId(matchNode.path("capId").asText(""));
                    match.setCapName(matchNode.path("capName").asText(""));
                    match.setMatchScore(matchNode.path("matchScore").asDouble(0.8));
                    match.setMatchReason(matchNode.path("matchReason").asText(""));
                    
                    MatchDimensionScores dimensions = new MatchDimensionScores();
                    JsonNode dimNode = matchNode.path("dimensionScores");
                    if (!dimNode.isMissingNode()) {
                        dimensions.setSemanticSimilarity(dimNode.path("semanticSimilarity").asDouble(0.8));
                        dimensions.setFunctionalMatch(dimNode.path("functionalMatch").asDouble(0.8));
                        dimensions.setParameterCompatibility(dimNode.path("parameterCompatibility").asDouble(0.8));
                    } else {
                        dimensions.setSemanticSimilarity(match.getMatchScore() * 0.9);
                        dimensions.setFunctionalMatch(match.getMatchScore() * 0.95);
                        dimensions.setParameterCompatibility(match.getMatchScore() * 0.85);
                    }
                    match.setDimensionScores(dimensions);
                    
                    matches.add(match);
                }
            }
            
            String reasoning = jsonNode.path("reasoning").asText("LLM匹配结果");
            
            CapabilityMatchingResultDTO result = !matches.isEmpty() && matches.get(0).getMatchScore() > 0.85
                ? CapabilityMatchingResultDTO.exactMatch(matches, reasoning)
                : CapabilityMatchingResultDTO.partialMatch(matches, reasoning);
            result.setFunctionTraces(traces);
            
            return result;
            
        } catch (Exception e) {
            log.warn("Failed to parse LLM response: {}", e.getMessage());
            return CapabilityMatchingResultDTO.noMatch("LLM响应解析失败");
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
    
    private CapabilityMatchingResultDTO matchWithRules(DesignerContextDTO context, String activityDesc) {
        log.info("Using rules for capability matching");
        
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        try {
            long startTime = System.currentTimeMillis();
            Object result = functionRegistry.executeFunction("match_capability_by_activity",
                Map.of("activityDesc", activityDesc));
            long execTime = System.currentTimeMillis() - startTime;
            
            traces.add(FunctionCallTraceDTO.success(1, "match_capability_by_activity",
                Map.of("activityDesc", activityDesc), result, execTime));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> matches = (List<Map<String, Object>>) resultMap.get("data");
            
            if (matches == null || matches.isEmpty()) {
                return CapabilityMatchingResultDTO.noMatch("未找到匹配的能力");
            }
            
            List<CapabilityMatch> capabilityMatches = matches.stream()
                .map(this::mapToCapabilityMatch)
                .collect(Collectors.toList());
            
            String reasoning = buildReasoning(activityDesc, capabilityMatches);
            
            CapabilityMatchingResultDTO resultDTO = CapabilityMatchingResultDTO.exactMatch(
                capabilityMatches, reasoning
            );
            resultDTO.setFunctionTraces(traces);
            
            return resultDTO;
            
        } catch (Exception e) {
            log.error("Error matching capability: {}", e.getMessage(), e);
            return CapabilityMatchingResultDTO.noMatch("能力匹配失败: " + e.getMessage());
        }
    }
    
    @Override
    public CapabilityMatchingResultDTO smartMatch(DesignerContextDTO context, String activityDesc) {
        log.info("Smart matching capability for activity: {}", activityDesc);
        
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        try {
            List<String> keywords = extractKeywords(activityDesc);
            
            long startTime = System.currentTimeMillis();
            Object searchResult = functionRegistry.executeFunction("search_capabilities",
                Map.of("query", String.join(" ", keywords), "limit", 10));
            long execTime = System.currentTimeMillis() - startTime;
            
            traces.add(FunctionCallTraceDTO.success(1, "search_capabilities",
                Map.of("query", String.join(" ", keywords)), searchResult, execTime));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> searchMap = (Map<String, Object>) searchResult;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> capabilities = (List<Map<String, Object>>) searchMap.get("data");
            
            if (capabilities == null || capabilities.isEmpty()) {
                return CapabilityMatchingResultDTO.noMatch("未找到匹配的能力");
            }
            
            List<CapabilityMatch> matches = new ArrayList<>();
            int sequence = 2;
            
            for (Map<String, Object> cap : capabilities) {
                String capId = (String) cap.get("capId");
                
                try {
                    startTime = System.currentTimeMillis();
                    Object detailResult = functionRegistry.executeFunction("get_capability_detail",
                        Map.of("capId", capId));
                    execTime = System.currentTimeMillis() - startTime;
                    
                    traces.add(FunctionCallTraceDTO.success(sequence++, "get_capability_detail",
                        Map.of("capId", capId), detailResult, execTime));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> detailMap = (Map<String, Object>) ((Map<?, ?>) detailResult).get("data");
                    
                    CapabilityMatch match = mapToCapabilityMatchWithDetail(cap, detailMap);
                    matches.add(match);
                    
                } catch (Exception e) {
                    log.warn("Failed to get capability detail for {}: {}", capId, e.getMessage());
                }
            }
            
            matches.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
            
            String reasoning = buildReasoning(activityDesc, matches);
            
            CapabilityMatchingResultDTO resultDTO = matches.get(0).getMatchScore() > 0.85
                ? CapabilityMatchingResultDTO.exactMatch(matches, reasoning)
                : CapabilityMatchingResultDTO.partialMatch(matches, reasoning);
            
            resultDTO.setFunctionTraces(traces);
            
            return resultDTO;
            
        } catch (Exception e) {
            log.error("Error in smart matching: {}", e.getMessage(), e);
            return CapabilityMatchingResultDTO.noMatch("智能匹配失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<CapabilityMatch> matchByKeywords(List<String> keywords) {
        log.info("Matching capability by keywords: {}", keywords);
        
        try {
            Object result = functionRegistry.executeFunction("search_capabilities",
                Map.of("query", String.join(" ", keywords), "limit", 10));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> capabilities = (List<Map<String, Object>>) resultMap.get("data");
            
            return capabilities.stream()
                .map(this::mapToCapabilityMatch)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error matching by keywords: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> buildBindingConfig(String capId, Map<String, Object> context) {
        Map<String, Object> config = new HashMap<>();
        
        config.put("capId", capId);
        config.put("priority", 1);
        config.put("connectorType", "SDK");
        config.put("status", "ACTIVE");
        
        Map<String, Object> llmConfig = new HashMap<>();
        llmConfig.put("enableFunctionCall", true);
        llmConfig.put("functionTools", Arrays.asList(capId));
        config.put("llmConfig", llmConfig);
        
        if (context != null) {
            config.putAll(context);
        }
        
        return config;
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
    
    private List<String> extractKeywords(String description) {
        String[] words = description.toLowerCase().split("[\\s,，。、！？]+");
        return Arrays.stream(words)
            .filter(w -> w.length() >= 2)
            .distinct()
            .collect(Collectors.toList());
    }
    
    private CapabilityMatch mapToCapabilityMatch(Map<String, Object> capMap) {
        CapabilityMatch match = new CapabilityMatch();
        match.setCapId((String) capMap.get("capId"));
        match.setCapName((String) capMap.get("capName"));
        match.setDescription((String) capMap.get("description"));
        match.setCategory((String) capMap.get("category"));
        
        Object score = capMap.get("matchScore");
        match.setMatchScore(score != null ? ((Number) score).doubleValue() : 0.8);
        
        match.setMatchReason((String) capMap.get("matchReason"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> bindingConfig = (Map<String, Object>) capMap.get("bindingConfig");
        match.setBindingConfig(bindingConfig);
        
        MatchDimensionScores dimensions = new MatchDimensionScores();
        dimensions.setSemanticSimilarity(match.getMatchScore() * 0.9);
        dimensions.setFunctionalMatch(match.getMatchScore() * 0.95);
        dimensions.setParameterCompatibility(match.getMatchScore() * 0.85);
        match.setDimensionScores(dimensions);
        
        return match;
    }
    
    private CapabilityMatch mapToCapabilityMatchWithDetail(Map<String, Object> capMap, 
            Map<String, Object> detailMap) {
        CapabilityMatch match = mapToCapabilityMatch(capMap);
        
        if (detailMap != null) {
            match.setDescription((String) detailMap.get("description"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> parameters = 
                (List<Map<String, Object>>) detailMap.get("parameters");
            if (parameters != null) {
                match.getDimensionScores().setParameterCompatibility(
                    calculateParameterCompatibility(parameters)
                );
            }
        }
        
        return match;
    }
    
    private double calculateParameterCompatibility(List<Map<String, Object>> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return 0.5;
        }
        
        long requiredCount = parameters.stream()
            .filter(p -> Boolean.TRUE.equals(p.get("required")))
            .count();
        
        return Math.min(1.0, 0.5 + (parameters.size() - requiredCount) * 0.1);
    }
    
    private String buildReasoning(String activityDesc, List<CapabilityMatch> matches) {
        StringBuilder sb = new StringBuilder();
        sb.append("活动描述\"").append(activityDesc).append("\"");
        
        if (!matches.isEmpty()) {
            CapabilityMatch top = matches.get(0);
            sb.append("与能力\"").append(top.getCapName()).append("\"匹配度最高(")
              .append(String.format("%.0f%%", top.getMatchScore() * 100)).append(")。");
            
            if (matches.size() > 1) {
                sb.append("其他备选能力：");
                for (int i = 1; i < Math.min(3, matches.size()); i++) {
                    sb.append(matches.get(i).getCapName())
                      .append("(").append(String.format("%.0f%%", matches.get(i).getMatchScore() * 100)).append(")");
                    if (i < Math.min(3, matches.size()) - 1) {
                        sb.append("、");
                    }
                }
            }
        }
        
        return sb.toString();
    }
}
