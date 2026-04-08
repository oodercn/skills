package net.ooder.bpm.designer.model.dto;

import java.util.List;
import java.util.Map;

public class PanelRenderDataDTO {
    
    private String panelType;
    private String title;
    private String description;
    private List<RenderSection> sections;
    private Map<String, Object> derivedConfig;
    private List<DerivationSuggestion> suggestions;
    
    public static class RenderSection {
        private String sectionId;
        private String title;
        private RenderType renderType;
        private List<RenderItem> items;
        private boolean collapsible;
        private boolean editable;
        
        public String getSectionId() { return sectionId; }
        public void setSectionId(String sectionId) { this.sectionId = sectionId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public RenderType getRenderType() { return renderType; }
        public void setRenderType(RenderType renderType) { this.renderType = renderType; }
        public List<RenderItem> getItems() { return items; }
        public void setItems(List<RenderItem> items) { this.items = items; }
        public boolean isCollapsible() { return collapsible; }
        public void setCollapsible(boolean collapsible) { this.collapsible = collapsible; }
        public boolean isEditable() { return editable; }
        public void setEditable(boolean editable) { this.editable = editable; }
    }
    
    public static class RenderItem {
        private String itemId;
        private String label;
        private Object value;
        private ValueType valueType;
        private boolean derived;
        private double confidence;
        private String reasoning;
        private List<Alternative> alternatives;
        
        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        public ValueType getValueType() { return valueType; }
        public void setValueType(ValueType valueType) { this.valueType = valueType; }
        public boolean isDerived() { return derived; }
        public void setDerived(boolean derived) { this.derived = derived; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        public String getReasoning() { return reasoning; }
        public void setReasoning(String reasoning) { this.reasoning = reasoning; }
        public List<Alternative> getAlternatives() { return alternatives; }
        public void setAlternatives(List<Alternative> alternatives) { this.alternatives = alternatives; }
    }
    
    public static class Alternative {
        private Object value;
        private String label;
        private double score;
        private String reason;
        
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    public static class DerivationSuggestion {
        private String type;
        private String message;
        private String action;
        private Map<String, Object> data;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public Map<String, Object> getData() { return data; }
        public void setData(Map<String, Object> data) { this.data = data; }
    }
    
    public enum RenderType {
        LIST,
        TREE,
        FORM,
        TABLE,
        CARD
    }
    
    public enum ValueType {
        STRING, NUMBER, BOOLEAN, OBJECT, ARRAY, USER, ROLE, DEPT, CAPABILITY, FORM
    }
    
    public String getPanelType() { return panelType; }
    public void setPanelType(String panelType) { this.panelType = panelType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<RenderSection> getSections() { return sections; }
    public void setSections(List<RenderSection> sections) { this.sections = sections; }
    public Map<String, Object> getDerivedConfig() { return derivedConfig; }
    public void setDerivedConfig(Map<String, Object> derivedConfig) { this.derivedConfig = derivedConfig; }
    public List<DerivationSuggestion> getSuggestions() { return suggestions; }
    public void setSuggestions(List<DerivationSuggestion> suggestions) { this.suggestions = suggestions; }
}
