package net.ooder.skill.knowledge.local.model;

import java.util.Map;

public class BuildQueryRequest {
    private String text;
    private String entityType;
    private Map<String, Object> context;
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
