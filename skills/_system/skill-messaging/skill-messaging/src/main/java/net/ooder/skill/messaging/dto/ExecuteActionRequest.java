package net.ooder.skill.messaging.dto;

import java.util.Map;

public class ExecuteActionRequest {
    
    private String userId;
    private String actionId;
    private Map<String, Object> params;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getActionId() { return actionId; }
    public void setActionId(String actionId) { this.actionId = actionId; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
