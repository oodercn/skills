package net.ooder.skill.llm.monitor.dto;

public class LlmCallLogDTO {
    
    private String logId;
    private String providerId;
    private String model;
    private String userId;
    private String moduleId;
    private String companyId;
    private String departmentId;
    private String sceneGroupId;
    private String prompt;
    private String response;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private double cost;
    private long latency;
    private String status;
    private String errorMessage;
    private long timestamp;
    private String requestId;

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getModuleId() { return moduleId; }
    public void setModuleId(String moduleId) { this.moduleId = moduleId; }
    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    public int getPromptTokens() { return promptTokens; }
    public void setPromptTokens(int promptTokens) { this.promptTokens = promptTokens; }
    public int getCompletionTokens() { return completionTokens; }
    public void setCompletionTokens(int completionTokens) { this.completionTokens = completionTokens; }
    public int getTotalTokens() { return totalTokens; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public long getLatency() { return latency; }
    public void setLatency(long latency) { this.latency = latency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}
