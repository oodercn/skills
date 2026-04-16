package net.ooder.skill.scenes.dto;

import java.util.Map;

public class StartWorkflowRequest {
    
    private String workflowId;
    private Map<String, Object> params;

    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
