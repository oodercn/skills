package net.ooder.bpm.designer.service;

import net.ooder.bpm.designer.model.dto.DesignerContextDTO;
import net.ooder.bpm.designer.model.dto.ProcessDefDTO;
import net.ooder.bpm.designer.model.dto.ActivityDefDTO;

import java.util.List;
import java.util.Map;

public interface DesignerNlpService {

    NlpResponse processNaturalLanguage(String userInput, DesignerContextDTO context);
    
    ProcessDefDTO createProcessFromNlp(String description, DesignerContextDTO context);
    
    ActivityDefDTO createActivityFromNlp(String description, DesignerContextDTO context);
    
    Map<String, Object> updateAttributeFromNlp(String attributeName, String value, DesignerContextDTO context);
    
    List<NlpSuggestion> getSuggestions(DesignerContextDTO context);
    
    String validateAndFix(ProcessDefDTO processDef, DesignerContextDTO context);
    
    String generateDescription(ProcessDefDTO processDef);
    
    String generateActivityDescription(ActivityDefDTO activityDef);
    
    List<NlpIntent> analyzeIntent(String userInput);
    
    Map<String, Object> extractEntities(String userInput, String intentType);
    
    public static class NlpResponse {
        private String intent;
        private double confidence;
        private Map<String, Object> entities;
        private String action;
        private Map<String, Object> actionParams;
        private String message;
        private List<NlpSuggestion> suggestions;
        private boolean success;
        
        public String getIntent() { return intent; }
        public void setIntent(String intent) { this.intent = intent; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public Map<String, Object> getEntities() { return entities; }
        public void setEntities(Map<String, Object> entities) { this.entities = entities; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public Map<String, Object> getActionParams() { return actionParams; }
        public void setActionParams(Map<String, Object> actionParams) { this.actionParams = actionParams; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<NlpSuggestion> getSuggestions() { return suggestions; }
        public void setSuggestions(List<NlpSuggestion> suggestions) { this.suggestions = suggestions; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
    }
    
    public static class NlpSuggestion {
        private String type;
        private String title;
        private String description;
        private String action;
        private Map<String, Object> params;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public Map<String, Object> getParams() { return params; }
        public void setParams(Map<String, Object> params) { this.params = params; }
    }
    
    public static class NlpIntent {
        private String name;
        private double confidence;
        private String category;
        private Map<String, Object> slots;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Map<String, Object> getSlots() { return slots; }
        public void setSlots(Map<String, Object> slots) { this.slots = slots; }
    }
}
