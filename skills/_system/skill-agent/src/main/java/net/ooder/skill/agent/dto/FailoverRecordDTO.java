package net.ooder.skill.agent.dto;

import java.util.Map;

public class FailoverRecordDTO {
    private String recordId;
    private String failedAgentId;
    private String failedAgentName;
    private String targetAgentId;
    private String targetAgentName;
    private String sceneGroupId;
    private String sceneGroupName;
    private String status;
    private long createTime;
    private long recoveryTime;
    private String failureReason;
    private Map<String, Object> details;

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getFailedAgentId() { return failedAgentId; }
    public void setFailedAgentId(String failedAgentId) { this.failedAgentId = failedAgentId; }
    public String getFailedAgentName() { return failedAgentName; }
    public void setFailedAgentName(String failedAgentName) { this.failedAgentName = failedAgentName; }
    public String getTargetAgentId() { return targetAgentId; }
    public void setTargetAgentId(String targetAgentId) { this.targetAgentId = targetAgentId; }
    public String getTargetAgentName() { return targetAgentName; }
    public void setTargetAgentName(String targetAgentName) { this.targetAgentName = targetAgentName; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getSceneGroupName() { return sceneGroupName; }
    public void setSceneGroupName(String sceneGroupName) { this.sceneGroupName = sceneGroupName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getRecoveryTime() { return recoveryTime; }
    public void setRecoveryTime(long recoveryTime) { this.recoveryTime = recoveryTime; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
