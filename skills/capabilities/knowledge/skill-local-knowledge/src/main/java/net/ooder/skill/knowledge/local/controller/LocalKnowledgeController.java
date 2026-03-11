package net.ooder.skill.knowledge.local.controller;

import net.ooder.skill.knowledge.local.model.*;
import net.ooder.skill.knowledge.local.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS})
public class LocalKnowledgeController {
    
    private static final Logger log = LoggerFactory.getLogger(LocalKnowledgeController.class);
    
    @Autowired
    private LocalIndexService localIndexService;
    
    @Autowired
    private IntentClassifier intentClassifier;
    
    @Autowired
    private TermMappingService termMappingService;

    @PostMapping("/local-search")
    public Result<Map<String, Object>> search(@RequestBody SearchRequest request) {
        log.info("Local search request: query={}", request.getQuery());
        
        Result<Map<String, Object>> result = new Result<>();
        
        try {
            String query = request.getQuery();
            Integer topK = request.getTopK() != null ? request.getTopK() : 10;
            Map<String, Object> filters = request.getFilters();
            
            List<SearchResult> searchResults;
            if (filters != null && !filters.isEmpty()) {
                searchResults = localIndexService.searchWithFilters(query, filters, topK);
            } else {
                searchResults = localIndexService.search(query, topK);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("results", searchResults);
            data.put("total", searchResults.size());
            data.put("query", query);
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("Success");
            
        } catch (Exception e) {
            log.error("Local search failed", e);
            result.setRequestStatus(500);
            result.setMessage("Search failed: " + e.getMessage());
        }
        
        return result;
    }
    
    @PostMapping("/local-search/index")
    public Result<Map<String, Object>> indexDirectory(@RequestBody IndexRequest request) {
        log.info("Index directory request: path={}", request.getPath());
        
        Result<Map<String, Object>> result = new Result<>();
        
        try {
            String path = request.getPath();
            
            localIndexService.scanAndIndex(path);
            
            Map<String, Object> data = new HashMap<>();
            data.put("documentCount", localIndexService.getDocumentCount());
            data.put("path", path);
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("Index completed");
            
        } catch (Exception e) {
            log.error("Index failed", e);
            result.setRequestStatus(500);
            result.setMessage("Index failed: " + e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/local-search/documents")
    public Result<Map<String, Object>> listDocuments(@RequestParam(required = false) String path) {
        log.info("List documents request, path: {}", path);
        
        Result<Map<String, Object>> result = new Result<>();
        
        try {
            List<LocalDocument> docs = localIndexService.listDocuments(path);
            
            Map<String, Object> data = new HashMap<>();
            data.put("documents", docs);
            data.put("total", docs.size());
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("Success");
            
        } catch (Exception e) {
            log.error("List documents failed", e);
            result.setRequestStatus(500);
            result.setMessage("List failed: " + e.getMessage());
        }
        
        return result;
    }
    
    @PostMapping("/nlp/classify")
    public Result<IntentClassification> classifyIntent(@RequestBody ClassifyIntentRequest request) {
        log.info("Classify intent request: text={}", request.getText());
        
        Result<IntentClassification> result = new Result<>();
        
        try {
            String text = request.getText();
            Map<String, Object> context = request.getContext();
            
            IntentClassification classification;
            if (context != null) {
                classification = intentClassifier.classifyWithContext(text, context);
            } else {
                classification = intentClassifier.classify(text);
            }
            
            result.setData(classification);
            result.setRequestStatus(200);
            result.setMessage("Success");
            
        } catch (Exception e) {
            log.error("Intent classification failed", e);
            result.setRequestStatus(500);
            result.setMessage("Classification failed: " + e.getMessage());
        }
        
        return result;
    }
    
    @PostMapping("/term/resolve")
    public Result<TermResolution> resolveTerm(@RequestBody ResolveTermRequest request) {
        log.info("Resolve term request: text={}", request.getText());
        
        Result<TermResolution> result = new Result<>();
        
        try {
            String text = request.getText();
            Map<String, Object> context = request.getContext();
            
            TermResolution resolution = termMappingService.resolveTerm(text, context);
            
            result.setData(resolution);
            result.setRequestStatus(200);
            result.setMessage("Success");
            
        } catch (Exception e) {
            log.error("Term resolution failed", e);
            result.setRequestStatus(500);
            result.setMessage("Resolution failed: " + e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/terms")
    public Result<Map<String, Object>> getTermMappings(@RequestParam(required = false) String domain) {
        log.info("Get term mappings request, domain: {}", domain);
        
        Result<Map<String, Object>> result = new Result<>();
        
        try {
            List<TermMapping> mappings = termMappingService.getTermMappings(domain);
            
            Map<String, Object> data = new HashMap<>();
            data.put("mappings", mappings);
            data.put("total", mappings.size());
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("Success");
            
        } catch (Exception e) {
            log.error("Get term mappings failed", e);
            result.setRequestStatus(500);
            result.setMessage("Failed: " + e.getMessage());
        }
        
        return result;
    }
    
    @PostMapping("/terms")
    public Result<Boolean> registerTermMapping(@RequestBody RegisterTermRequest request) {
        log.info("Register term mapping request: term={}", request.getTerm());
        
        Result<Boolean> result = new Result<>();
        
        try {
            String term = request.getTerm();
            String systemConcept = request.getSystemConcept();
            String type = request.getType();
            
            TermMappingDTO options = new TermMappingDTO(term, systemConcept, type);
            termMappingService.registerTermMapping(term, systemConcept, options);
            
            result.setData(true);
            result.setRequestStatus(200);
            result.setMessage("Term mapping registered");
            
        } catch (Exception e) {
            log.error("Register term mapping failed", e);
            result.setRequestStatus(500);
            result.setMessage("Failed: " + e.getMessage());
            result.setData(false);
        }
        
        return result;
    }
    
    @PostMapping("/form/assist")
    public Result<Map<String, Object>> assistForm(@RequestBody FormAssistRequest request) {
        log.info("Form assist request: formId={}", request.getFormId());
        
        Result<Map<String, Object>> result = new Result<>();
        
        try {
            String formId = request.getFormId();
            String userInput = request.getUserInput();
            Map<String, Object> currentData = request.getCurrentData();
            Map<String, Object> formSchema = request.getFormSchema();
            
            TermResolution resolution = termMappingService.resolveTerm(userInput, currentData);
            IntentClassification intent = intentClassifier.classify(userInput);
            
            Map<String, Object> fieldUpdates = new HashMap<>();
            List<String> suggestions = new ArrayList<>();
            
            if (resolution.getResolvedTerms() != null) {
                for (ResolvedTerm rt : resolution.getResolvedTerms()) {
                    String fieldName = inferFieldName(rt.getMappedTo(), formSchema);
                    if (fieldName != null) {
                        Map<String, Object> update = new HashMap<>();
                        update.put("action", "SET");
                        update.put("value", rt.getTerm());
                        update.put("confidence", rt.getConfidence());
                        fieldUpdates.put(fieldName, update);
                        suggestions.add("已识别'" + rt.getTerm() + "'，是否设置到" + fieldName + "字段？");
                    }
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("fieldUpdates", fieldUpdates);
            data.put("suggestions", suggestions);
            data.put("intent", intent);
            data.put("resolution", resolution);
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("Success");
            
        } catch (Exception e) {
            log.error("Form assist failed", e);
            result.setRequestStatus(500);
            result.setMessage("Failed: " + e.getMessage());
        }
        
        return result;
    }
    
    private String inferFieldName(String systemConcept, Map<String, Object> formSchema) {
        if (formSchema == null) return null;
        
        String lower = systemConcept.toLowerCase();
        
        for (Map.Entry<String, Object> entry : formSchema.entrySet()) {
            String fieldName = entry.getKey().toLowerCase();
            if (fieldName.contains(lower) || lower.contains(fieldName)) {
                return entry.getKey();
            }
        }
        
        return null;
    }
    
    @PostMapping("/query/build")
    public Result<Map<String, Object>> buildQuery(@RequestBody BuildQueryRequest request) {
        log.info("Build query request: text={}", request.getText());
        
        Result<Map<String, Object>> result = new Result<>();
        
        try {
            String text = request.getText();
            String entityType = request.getEntityType();
            Map<String, Object> context = request.getContext();
            
            TermResolution resolution = termMappingService.resolveTerm(text, context);
            IntentClassification intent = intentClassifier.classify(text);
            
            List<Map<String, Object>> filters = new ArrayList<>();
            StringBuilder queryUrl = new StringBuilder("/api/v1/");
            
            if ("Scene".equals(entityType)) {
                queryUrl.append("scenes");
            } else if ("Capability".equals(entityType)) {
                queryUrl.append("capabilities");
            } else if ("Task".equals(entityType)) {
                queryUrl.append("tasks");
            } else if ("User".equals(entityType)) {
                queryUrl.append("users");
            } else {
                queryUrl.append("search");
            }
            
            queryUrl.append("?");
            
            if (resolution.getResolvedTerms() != null) {
                for (ResolvedTerm rt : resolution.getResolvedTerms()) {
                    Map<String, Object> filter = new HashMap<>();
                    filter.put("field", rt.getMappedTo());
                    filter.put("operator", "CONTAINS");
                    filter.put("value", rt.getTerm());
                    filters.add(filter);
                    
                    queryUrl.append(rt.getMappedTo().toLowerCase())
                            .append("=")
                            .append(rt.getTerm())
                            .append("&");
                }
            }
            
            if (context != null && context.containsKey("userId")) {
                Map<String, Object> userFilter = new HashMap<>();
                userFilter.put("field", "userId");
                userFilter.put("operator", "EQ");
                userFilter.put("value", context.get("userId"));
                filters.add(userFilter);
            }
            
            String explanation = "查询" + (entityType != null ? entityType : "数据");
            if (!filters.isEmpty()) {
                explanation += "，条件：";
                for (int i = 0; i < filters.size(); i++) {
                    if (i > 0) explanation += " 且 ";
                    Map<String, Object> f = filters.get(i);
                    explanation += f.get("field") + "=" + f.get("value");
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("queryUrl", queryUrl.toString());
            data.put("filters", filters);
            data.put("explanation", explanation);
            data.put("intent", intent);
            
            result.setData(data);
            result.setRequestStatus(200);
            result.setMessage("Success");
            
        } catch (Exception e) {
            log.error("Build query failed", e);
            result.setRequestStatus(500);
            result.setMessage("Failed: " + e.getMessage());
        }
        
        return result;
    }
    
    public static class Result<T> {
        private int requestStatus;
        private String message;
        private T data;
        
        public int getRequestStatus() { return requestStatus; }
        public void setRequestStatus(int requestStatus) { this.requestStatus = requestStatus; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }
}
