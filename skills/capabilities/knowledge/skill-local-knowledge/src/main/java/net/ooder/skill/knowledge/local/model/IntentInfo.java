package net.ooder.skill.knowledge.local.model;

import java.util.Map;

public class IntentInfo {
    
    private String type;
    private String target;
    private Map<String, Object> params;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
