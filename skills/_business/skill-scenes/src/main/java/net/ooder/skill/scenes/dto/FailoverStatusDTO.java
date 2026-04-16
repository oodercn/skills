package net.ooder.skill.scenes.dto;

public class FailoverStatusDTO {
    private String sceneGroupId;
    private String status;
    private String failedParticipantId;
    private String failoverStrategy;
    private long lastFailoverTime;

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFailedParticipantId() { return failedParticipantId; }
    public void setFailedParticipantId(String failedParticipantId) { this.failedParticipantId = failedParticipantId; }
    public String getFailoverStrategy() { return failoverStrategy; }
    public void setFailoverStrategy(String failoverStrategy) { this.failoverStrategy = failoverStrategy; }
    public long getLastFailoverTime() { return lastFailoverTime; }
    public void setLastFailoverTime(long lastFailoverTime) { this.lastFailoverTime = lastFailoverTime; }
}
