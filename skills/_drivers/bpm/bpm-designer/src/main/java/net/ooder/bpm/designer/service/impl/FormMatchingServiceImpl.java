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
import net.ooder.bpm.designer.model.dto.FormMatchingResultDTO;
import net.ooder.bpm.designer.model.dto.FormMatchingResultDTO.FieldMapping;
import net.ooder.bpm.designer.model.dto.FormMatchingResultDTO.FormMatch;
import net.ooder.bpm.designer.model.dto.FormMatchingResultDTO.FormSchema;
import net.ooder.bpm.designer.model.dto.FormMatchingResultDTO.FormField;
import net.ooder.bpm.designer.model.dto.FunctionCallTraceDTO;
import net.ooder.bpm.designer.prompt.PromptTemplateManager;
import net.ooder.bpm.designer.service.FormMatchingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FormMatchingServiceImpl implements FormMatchingService {
    
    private static final Logger log = LoggerFactory.getLogger(FormMatchingServiceImpl.class);
    
    private final DesignerFunctionRegistry functionRegistry;
    private final LLMService llmService;
    private final PromptTemplateManager promptTemplateManager;
    private final CacheService cacheService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public FormMatchingServiceImpl(
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
    public FormMatchingResultDTO match(DesignerContextDTO context, String activityDesc) {
        log.info("Matching form for activity: {}", activityDesc);
        
        String tenantId = context != null && context.getUserId() != null ? context.getUserId() : "default";
        String cacheKey = CacheKeyGenerator.formMatching(tenantId, activityDesc, null);
        
        Optional<FormMatchingResultDTO> cached = cacheService.get(cacheKey, FormMatchingResultDTO.class);
        if (cached.isPresent()) {
            log.info("Returning cached form matching result");
            return cached.get();
        }
        
        FormMatchingResultDTO result;
        if (isLLMAvailable()) {
            result = matchWithLLM(context, activityDesc);
        } else {
            result = matchWithRules(context, activityDesc);
        }
        
        if (result.getStatus() == FormMatchingResultDTO.MatchingStatus.EXACT_MATCH ||
            result.getStatus() == FormMatchingResultDTO.MatchingStatus.PARTIAL_MATCH ||
            result.getStatus() == FormMatchingResultDTO.MatchingStatus.SUGGESTED) {
            cacheService.put(cacheKey, result, Duration.ofMinutes(30));
        }
        
        return result;
    }
    
    private boolean isLLMAvailable() {
        return llmService != null && llmService.isAvailable();
    }
    
    private FormMatchingResultDTO matchWithLLM(DesignerContextDTO context, String activityDesc) {
        log.info("Using LLM for form matching");
        
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        try {
            Map<String, Object> promptContext = buildPromptContext(context, activityDesc);
            String systemPrompt = promptTemplateManager.getSystemPrompt("form-matching");
            String userPrompt = promptTemplateManager.getUserPrompt("form-matching", promptContext);
            
            List<Map<String, Object>> functions = functionRegistry.getOpenAISchemasByCategory(
                net.ooder.bpm.designer.function.DesignerFunctionDefinition.FunctionCategory.FORM
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
    
    private FormMatchingResultDTO handleFunctionCalls(DesignerContextDTO context, String activityDesc,
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
            
            return FormMatchingResultDTO.noMatch("LLM无法确定匹配的表单");
            
        } catch (Exception e) {
            log.error("Error handling function calls: {}", e.getMessage(), e);
            return FormMatchingResultDTO.noMatch("表单匹配失败: " + e.getMessage());
        }
    }
    
    private FormMatchingResultDTO parseLLMContentResponse(String content, String activityDesc,
            List<FunctionCallTraceDTO> traces) {
        
        try {
            JsonNode jsonNode = objectMapper.readTree(extractJson(content));
            
            List<FormMatch> matches = new ArrayList<>();
            JsonNode matchesNode = jsonNode.path("matches");
            
            if (matchesNode.isArray()) {
                for (JsonNode matchNode : matchesNode) {
                    FormMatch match = new FormMatch();
                    match.setFormId(matchNode.path("formId").asText(""));
                    match.setFormName(matchNode.path("formName").asText(""));
                    match.setMatchScore(matchNode.path("matchScore").asDouble(0.8));
                    match.setCoverage(matchNode.path("coverage").asDouble(0.8));
                    match.setMatchReason(matchNode.path("matchReason").asText(""));
                    
                    List<FieldMapping> fieldMappings = new ArrayList<>();
                    JsonNode mappingsNode = matchNode.path("fieldMappings");
                    if (mappingsNode.isArray()) {
                        for (JsonNode mappingNode : mappingsNode) {
                            FieldMapping mapping = new FieldMapping();
                            mapping.setActivityField(mappingNode.path("activityField").asText(""));
                            mapping.setFormField(mappingNode.path("formField").asText(""));
                            mapping.setMappingScore(mappingNode.path("mappingScore").asDouble(0.8));
                            fieldMappings.add(mapping);
                        }
                    }
                    match.setFieldMappings(fieldMappings);
                    
                    matches.add(match);
                }
            }
            
            JsonNode schemaNode = jsonNode.path("suggestedSchema");
            FormSchema suggestedSchema = null;
            if (!schemaNode.isMissingNode()) {
                suggestedSchema = parseFormSchema(schemaNode);
            }
            
            String reasoning = jsonNode.path("reasoning").asText("LLM匹配结果");
            
            FormMatchingResultDTO result;
            if (!matches.isEmpty() && matches.get(0).getMatchScore() > 0.85) {
                result = FormMatchingResultDTO.exactMatch(matches, reasoning);
            } else if (suggestedSchema != null) {
                result = FormMatchingResultDTO.suggested(suggestedSchema, reasoning);
            } else if (!matches.isEmpty()) {
                result = FormMatchingResultDTO.partialMatch(matches, reasoning);
            } else {
                result = FormMatchingResultDTO.noMatch("未找到匹配的表单");
            }
            
            result.setFunctionTraces(traces);
            
            return result;
            
        } catch (Exception e) {
            log.warn("Failed to parse LLM response: {}", e.getMessage());
            return FormMatchingResultDTO.noMatch("LLM响应解析失败");
        }
    }
    
    private FormSchema parseFormSchema(JsonNode schemaNode) {
        FormSchema schema = new FormSchema();
        schema.setFormId(schemaNode.path("formId").asText("form-generated-" + System.currentTimeMillis()));
        schema.setFormName(schemaNode.path("formName").asText(""));
        schema.setDescription(schemaNode.path("description").asText(""));
        schema.setCategory(schemaNode.path("category").asText("AUTO"));
        schema.setGenerated(true);
        
        List<FormField> fields = new ArrayList<>();
        JsonNode fieldsNode = schemaNode.path("fields");
        if (fieldsNode.isArray()) {
            for (JsonNode fieldNode : fieldsNode) {
                FormField field = new FormField();
                field.setFieldId(fieldNode.path("fieldId").asText(""));
                field.setFieldName(fieldNode.path("fieldName").asText(""));
                field.setType(fieldNode.path("type").asText("text"));
                field.setRequired(fieldNode.path("required").asBoolean(false));
                fields.add(field);
            }
        }
        schema.setFields(fields);
        
        return schema;
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
    
    private FormMatchingResultDTO matchWithRules(DesignerContextDTO context, String activityDesc) {
        log.info("Using rules for form matching");
        
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        try {
            long startTime = System.currentTimeMillis();
            Object result = functionRegistry.executeFunction("match_form_by_activity",
                Map.of("activityDesc", activityDesc));
            long execTime = System.currentTimeMillis() - startTime;
            
            traces.add(FunctionCallTraceDTO.success(1, "match_form_by_activity",
                Map.of("activityDesc", activityDesc), result, execTime));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> matches = (List<Map<String, Object>>) resultMap.get("data");
            
            if (matches == null || matches.isEmpty()) {
                return generateAndSuggest(activityDesc, traces);
            }
            
            List<FormMatch> formMatches = matches.stream()
                .map(this::mapToFormMatch)
                .collect(Collectors.toList());
            
            String reasoning = buildReasoning(activityDesc, formMatches);
            
            FormMatchingResultDTO resultDTO = formMatches.get(0).getMatchScore() > 0.85
                ? FormMatchingResultDTO.exactMatch(formMatches, reasoning)
                : FormMatchingResultDTO.partialMatch(formMatches, reasoning);
            
            resultDTO.setFunctionTraces(traces);
            
            return resultDTO;
            
        } catch (Exception e) {
            log.error("Error matching form: {}", e.getMessage(), e);
            return FormMatchingResultDTO.noMatch("表单匹配失败: " + e.getMessage());
        }
    }
    
    @Override
    public FormMatchingResultDTO smartMatch(DesignerContextDTO context, String activityDesc) {
        log.info("Smart matching form for activity: {}", activityDesc);
        
        List<FunctionCallTraceDTO> traces = new ArrayList<>();
        
        try {
            List<String> keywords = extractKeywords(activityDesc);
            
            long startTime = System.currentTimeMillis();
            Object searchResult = functionRegistry.executeFunction("search_forms",
                Map.of("query", String.join(" ", keywords), "limit", 10));
            long execTime = System.currentTimeMillis() - startTime;
            
            traces.add(FunctionCallTraceDTO.success(1, "search_forms",
                Map.of("query", String.join(" ", keywords)), searchResult, execTime));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> searchMap = (Map<String, Object>) searchResult;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> forms = (List<Map<String, Object>>) searchMap.get("data");
            
            if (forms == null || forms.isEmpty()) {
                return generateAndSuggest(activityDesc, traces);
            }
            
            List<FormMatch> matches = new ArrayList<>();
            int sequence = 2;
            
            for (Map<String, Object> form : forms) {
                String formId = (String) form.get("formId");
                
                try {
                    startTime = System.currentTimeMillis();
                    Object schemaResult = functionRegistry.executeFunction("get_form_schema",
                        Map.of("formId", formId));
                    execTime = System.currentTimeMillis() - startTime;
                    
                    traces.add(FunctionCallTraceDTO.success(sequence++, "get_form_schema",
                        Map.of("formId", formId), schemaResult, execTime));
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> schemaMap = (Map<String, Object>) ((Map<?, ?>) schemaResult).get("data");
                    
                    FormMatch match = mapToFormMatchWithSchema(form, schemaMap);
                    matches.add(match);
                    
                } catch (Exception e) {
                    log.warn("Failed to get form schema for {}: {}", formId, e.getMessage());
                }
            }
            
            matches.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
            
            String reasoning = buildReasoning(activityDesc, matches);
            
            FormMatchingResultDTO resultDTO = matches.get(0).getMatchScore() > 0.85
                ? FormMatchingResultDTO.exactMatch(matches, reasoning)
                : FormMatchingResultDTO.partialMatch(matches, reasoning);
            
            resultDTO.setFunctionTraces(traces);
            
            return resultDTO;
            
        } catch (Exception e) {
            log.error("Error in smart matching: {}", e.getMessage(), e);
            return FormMatchingResultDTO.noMatch("智能匹配失败: " + e.getMessage());
        }
    }
    
    @Override
    public FormSchema generateSchema(DesignerContextDTO context, String activityDesc) {
        log.info("Generating form schema for activity: {}", activityDesc);
        
        try {
            Object result = functionRegistry.executeFunction("generate_form_schema",
                Map.of("activityDesc", activityDesc));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            Map<String, Object> schemaMap = (Map<String, Object>) resultMap.get("data");
            
            return mapToFormSchema(schemaMap);
            
        } catch (Exception e) {
            log.error("Error generating schema: {}", e.getMessage(), e);
            return createDefaultSchema(activityDesc);
        }
    }
    
    @Override
    public List<FormMatch> matchByFields(List<String> requiredFields) {
        log.info("Matching form by fields: {}", requiredFields);
        
        try {
            Object result = functionRegistry.executeFunction("get_form_field_mappings",
                Map.of("requiredFields", requiredFields));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mappings = (List<Map<String, Object>>) resultMap.get("data");
            
            return mappings.stream()
                .map(this::mapToFormMatchFromMapping)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error matching by fields: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    private FormMatchingResultDTO generateAndSuggest(String activityDesc, List<FunctionCallTraceDTO> traces) {
        log.info("No matching forms found, generating suggested schema");
        
        try {
            FormSchema schema = generateSchema(null, activityDesc);
            String reasoning = "未找到匹配的现有表单，建议生成新表单: " + schema.getFormName();
            
            FormMatchingResultDTO result = FormMatchingResultDTO.suggested(schema, reasoning);
            result.setFunctionTraces(traces);
            
            return result;
            
        } catch (Exception e) {
            return FormMatchingResultDTO.noMatch("未找到匹配的表单，且生成建议失败");
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
    
    private List<String> extractKeywords(String description) {
        String[] words = description.toLowerCase().split("[\\s,，。、！？]+");
        return Arrays.stream(words)
            .filter(w -> w.length() >= 2)
            .distinct()
            .collect(Collectors.toList());
    }
    
    private FormMatch mapToFormMatch(Map<String, Object> formMap) {
        FormMatch match = new FormMatch();
        match.setFormId((String) formMap.get("formId"));
        match.setFormName((String) formMap.get("formName"));
        match.setDescription((String) formMap.get("description"));
        match.setCategory((String) formMap.get("category"));
        
        Object score = formMap.get("matchScore");
        match.setMatchScore(score != null ? ((Number) score).doubleValue() : 0.8);
        
        Object coverage = formMap.get("coverage");
        match.setCoverage(coverage != null ? ((Number) coverage).doubleValue() : 0.8);
        
        match.setMatchReason((String) formMap.get("matchReason"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> fieldMappings = 
            (List<Map<String, Object>>) formMap.get("fieldMappings");
        if (fieldMappings != null) {
            match.setFieldMappings(fieldMappings.stream()
                .map(this::mapToFieldMapping)
                .collect(Collectors.toList()));
        }
        
        return match;
    }
    
    private FormMatch mapToFormMatchWithSchema(Map<String, Object> formMap, Map<String, Object> schemaMap) {
        FormMatch match = mapToFormMatch(formMap);
        
        if (schemaMap != null) {
            match.setDescription((String) schemaMap.get("description"));
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> fields = 
                (List<Map<String, Object>>) schemaMap.get("fields");
            if (fields != null) {
                match.setCoverage(calculateCoverage(fields));
            }
        }
        
        return match;
    }
    
    private FieldMapping mapToFieldMapping(Map<String, Object> mappingMap) {
        FieldMapping mapping = new FieldMapping();
        mapping.setActivityField((String) mappingMap.get("activityField"));
        mapping.setFormField((String) mappingMap.get("formField"));
        
        Object score = mappingMap.get("mappingScore");
        mapping.setMappingScore(score != null ? ((Number) score).doubleValue() : 0.8);
        
        return mapping;
    }
    
    private FormMatch mapToFormMatchFromMapping(Map<String, Object> mappingMap) {
        FormMatch match = new FormMatch();
        match.setFormId((String) mappingMap.get("formId"));
        match.setFormName((String) mappingMap.get("formName"));
        match.setMatchScore(0.8);
        match.setCoverage(0.8);
        return match;
    }
    
    private FormSchema mapToFormSchema(Map<String, Object> schemaMap) {
        FormSchema schema = new FormSchema();
        schema.setFormId((String) schemaMap.get("formId"));
        schema.setFormName((String) schemaMap.get("formName"));
        schema.setDescription((String) schemaMap.get("description"));
        schema.setCategory((String) schemaMap.get("category"));
        schema.setGenerated(Boolean.TRUE.equals(schemaMap.get("generated")));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> fields = 
            (List<Map<String, Object>>) schemaMap.get("fields");
        if (fields != null) {
            schema.setFields(fields.stream()
                .map(this::mapToFormField)
                .collect(Collectors.toList()));
        }
        
        return schema;
    }
    
    private FormField mapToFormField(Map<String, Object> fieldMap) {
        FormField field = new FormField();
        field.setFieldId((String) fieldMap.get("fieldId"));
        field.setFieldName((String) fieldMap.get("fieldName"));
        field.setType((String) fieldMap.get("type"));
        field.setRequired(Boolean.TRUE.equals(fieldMap.get("required")));
        return field;
    }
    
    private double calculateCoverage(List<Map<String, Object>> fields) {
        if (fields == null || fields.isEmpty()) {
            return 0.5;
        }
        
        long requiredCount = fields.stream()
            .filter(f -> Boolean.TRUE.equals(f.get("required")))
            .count();
        
        return Math.min(1.0, 0.5 + (fields.size() - requiredCount) * 0.1);
    }
    
    private FormSchema createDefaultSchema(String activityDesc) {
        FormSchema schema = new FormSchema();
        schema.setFormId("form-default-" + System.currentTimeMillis());
        schema.setFormName("默认表单");
        schema.setDescription("为活动生成的默认表单: " + activityDesc);
        schema.setCategory("AUTO");
        schema.setGenerated(true);
        
        List<FormField> fields = new ArrayList<>();
        fields.add(createFormField("title", "标题", "text", true));
        fields.add(createFormField("description", "描述", "textarea", false));
        fields.add(createFormField("attachments", "附件", "file", false));
        
        schema.setFields(fields);
        return schema;
    }
    
    private FormField createFormField(String id, String name, String type, boolean required) {
        FormField field = new FormField();
        field.setFieldId(id);
        field.setFieldName(name);
        field.setType(type);
        field.setRequired(required);
        return field;
    }
    
    private String buildReasoning(String activityDesc, List<FormMatch> matches) {
        StringBuilder sb = new StringBuilder();
        sb.append("活动描述\"").append(activityDesc).append("\"");
        
        if (!matches.isEmpty()) {
            FormMatch top = matches.get(0);
            sb.append("与表单\"").append(top.getFormName()).append("\"匹配度最高(")
              .append(String.format("%.0f%%", top.getMatchScore() * 100)).append(")。");
            
            if (matches.size() > 1) {
                sb.append("其他备选表单：");
                for (int i = 1; i < Math.min(3, matches.size()); i++) {
                    sb.append(matches.get(i).getFormName())
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
