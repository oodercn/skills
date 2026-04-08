package net.ooder.bpm.designer.controller;

import net.ooder.bpm.designer.model.ApiResponse;
import net.ooder.bpm.designer.model.dto.DesignerContextDTO;
import net.ooder.bpm.designer.model.dto.ProcessDefDTO;
import net.ooder.bpm.designer.model.dto.ActivityDefDTO;
import net.ooder.bpm.designer.service.DesignerNlpService;
import net.ooder.bpm.designer.service.DesignerNlpService.NlpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bpm/nlp")
@CrossOrigin(origins = "*")
public class DesignerNlpController {

    @Autowired
    private DesignerNlpService nlpService;

    @PostMapping("/chat")
    public ApiResponse<NlpResponse> chat(
            @RequestBody Map<String, Object> request) {
        try {
            String userInput = (String) request.get("input");
            @SuppressWarnings("unchecked")
            Map<String, Object> contextMap = (Map<String, Object>) request.get("context");
            
            DesignerContextDTO context = convertToContext(contextMap);
            
            NlpResponse response = nlpService.processNaturalLanguage(userInput, context);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "NLP processing failed: " + e.getMessage());
        }
    }

    @PostMapping("/process/create")
    public ApiResponse<ProcessDefDTO> createProcessFromNlp(
            @RequestBody Map<String, Object> request) {
        try {
            String description = (String) request.get("description");
            @SuppressWarnings("unchecked")
            Map<String, Object> contextMap = (Map<String, Object>) request.get("context");
            
            DesignerContextDTO context = convertToContext(contextMap);
            
            ProcessDefDTO process = nlpService.createProcessFromNlp(description, context);
            return ApiResponse.success(process);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to create process: " + e.getMessage());
        }
    }

    @PostMapping("/activity/create")
    public ApiResponse<ActivityDefDTO> createActivityFromNlp(
            @RequestBody Map<String, Object> request) {
        try {
            String description = (String) request.get("description");
            @SuppressWarnings("unchecked")
            Map<String, Object> contextMap = (Map<String, Object>) request.get("context");
            
            DesignerContextDTO context = convertToContext(contextMap);
            
            ActivityDefDTO activity = nlpService.createActivityFromNlp(description, context);
            return ApiResponse.success(activity);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to create activity: " + e.getMessage());
        }
    }

    @PostMapping("/attribute/update")
    public ApiResponse<Map<String, Object>> updateAttributeFromNlp(
            @RequestBody Map<String, Object> request) {
        try {
            String attributeName = (String) request.get("attribute");
            String value = (String) request.get("value");
            @SuppressWarnings("unchecked")
            Map<String, Object> contextMap = (Map<String, Object>) request.get("context");
            
            DesignerContextDTO context = convertToContext(contextMap);
            
            Map<String, Object> result = nlpService.updateAttributeFromNlp(attributeName, value, context);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to update attribute: " + e.getMessage());
        }
    }

    @PostMapping("/suggestions")
    public ApiResponse<List<DesignerNlpService.NlpSuggestion>> getSuggestions(
            @RequestBody Map<String, Object> contextMap) {
        try {
            DesignerContextDTO context = convertToContext(contextMap);
            
            List<DesignerNlpService.NlpSuggestion> suggestions = nlpService.getSuggestions(context);
            return ApiResponse.success(suggestions);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to get suggestions: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ApiResponse<String> validateProcess(
            @RequestBody ProcessDefDTO processDef,
            @RequestParam(required = false) Map<String, Object> contextMap) {
        try {
            DesignerContextDTO context = convertToContext(contextMap);
            
            String result = nlpService.validateAndFix(processDef, context);
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "Validation failed: " + e.getMessage());
        }
    }

    @PostMapping("/describe/process")
    public ApiResponse<String> describeProcess(@RequestBody ProcessDefDTO processDef) {
        try {
            String description = nlpService.generateDescription(processDef);
            return ApiResponse.success(description);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to generate description: " + e.getMessage());
        }
    }

    @PostMapping("/describe/activity")
    public ApiResponse<String> describeActivity(@RequestBody ActivityDefDTO activityDef) {
        try {
            String description = nlpService.generateActivityDescription(activityDef);
            return ApiResponse.success(description);
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to generate description: " + e.getMessage());
        }
    }

    @PostMapping("/intent/analyze")
    public ApiResponse<List<DesignerNlpService.NlpIntent>> analyzeIntent(
            @RequestBody Map<String, String> request) {
        try {
            String userInput = request.get("input");
            
            List<DesignerNlpService.NlpIntent> intents = nlpService.analyzeIntent(userInput);
            return ApiResponse.success(intents);
        } catch (Exception e) {
            return ApiResponse.error(500, "Intent analysis failed: " + e.getMessage());
        }
    }

    @PostMapping("/entities/extract")
    public ApiResponse<Map<String, Object>> extractEntities(
            @RequestBody Map<String, Object> request) {
        try {
            String userInput = (String) request.get("input");
            String intentType = (String) request.get("intentType");
            
            Map<String, Object> entities = nlpService.extractEntities(userInput, intentType);
            return ApiResponse.success(entities);
        } catch (Exception e) {
            return ApiResponse.error(500, "Entity extraction failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private DesignerContextDTO convertToContext(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        
        DesignerContextDTO context = new DesignerContextDTO();
        
        if (map.containsKey("sessionId")) {
            context.setSessionId((String) map.get("sessionId"));
        }
        if (map.containsKey("userId")) {
            context.setUserId((String) map.get("userId"));
        }
        if (map.containsKey("userName")) {
            context.setUserName((String) map.get("userName"));
        }
        if (map.containsKey("userRole")) {
            context.setUserRole((String) map.get("userRole"));
        }
        if (map.containsKey("mode")) {
            context.setMode(DesignerContextDTO.DesignerMode.valueOf((String) map.get("mode")));
        }
        if (map.containsKey("sceneGroupId")) {
            context.setSceneGroupId((String) map.get("sceneGroupId"));
        }
        if (map.containsKey("sceneType")) {
            context.setSceneType((String) map.get("sceneType"));
        }
        if (map.containsKey("selectedElementId")) {
            context.setSelectedElementId((String) map.get("selectedElementId"));
        }
        if (map.containsKey("selectedElementType")) {
            context.setSelectedElementType((String) map.get("selectedElementType"));
        }
        
        if (map.containsKey("currentProcess")) {
            Map<String, Object> processMap = (Map<String, Object>) map.get("currentProcess");
            context.setCurrentProcess(convertToProcessDef(processMap));
        }
        
        if (map.containsKey("currentActivity")) {
            Map<String, Object> activityMap = (Map<String, Object>) map.get("currentActivity");
            context.setCurrentActivity(convertToActivityDef(activityMap));
        }
        
        if (map.containsKey("sceneContext")) {
            context.setSceneContext((Map<String, Object>) map.get("sceneContext"));
        }
        
        if (map.containsKey("ragContext")) {
            context.setRagContext((Map<String, Object>) map.get("ragContext"));
        }
        
        if (map.containsKey("conversationHistory")) {
            context.setConversationHistory((List<Map<String, String>>) map.get("conversationHistory"));
        }
        
        return context;
    }

    @SuppressWarnings("unchecked")
    private ProcessDefDTO convertToProcessDef(Map<String, Object> map) {
        if (map == null) return null;
        
        ProcessDefDTO process = new ProcessDefDTO();
        process.setProcessDefId((String) map.get("processDefId"));
        process.setName((String) map.get("name"));
        process.setDescription((String) map.get("description"));
        process.setClassification((String) map.get("classification"));
        process.setSystemCode((String) map.get("systemCode"));
        process.setAccessLevel((String) map.get("accessLevel"));
        
        if (map.containsKey("version")) {
            process.setVersion((Integer) map.get("version"));
        }
        
        process.setPublicationStatus((String) map.get("publicationStatus"));
        
        return process;
    }

    @SuppressWarnings("unchecked")
    private ActivityDefDTO convertToActivityDef(Map<String, Object> map) {
        if (map == null) return null;
        
        ActivityDefDTO activity = new ActivityDefDTO();
        activity.setActivityDefId((String) map.get("activityDefId"));
        activity.setName((String) map.get("name"));
        activity.setDescription((String) map.get("description"));
        activity.setPosition((String) map.get("position"));
        activity.setActivityType((String) map.get("activityType"));
        activity.setActivityCategory((String) map.get("activityCategory"));
        activity.setImplementation((String) map.get("implementation"));
        
        return activity;
    }
}
