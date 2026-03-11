package net.ooder.skill.knowledge.local.model;

import java.util.Map;

public class ClassifyIntentRequest {
    private String text;
    private Map<String, Object> context;
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
