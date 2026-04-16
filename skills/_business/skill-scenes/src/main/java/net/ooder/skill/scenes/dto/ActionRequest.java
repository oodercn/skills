package net.ooder.skill.scenes.dto;

import java.util.Map;

public class ActionRequest {
    
    private String action;
    private Map<String, Object> params;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
