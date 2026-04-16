package net.ooder.skill.llm.monitor.dto;

public class LlmSessionDTO {
    
    private String sessionId;
    private String providerId;
    private String model;
    private long startTime;
    private long endTime;
    private int messageCount;
    private String status;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public int getMessageCount() { return messageCount; }
    public void setMessageCount(int messageCount) { this.messageCount = messageCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
