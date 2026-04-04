package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class FailoverStatusDTO {
    private String failoverId;
    private String sceneGroupId;
    private String status;
    private String primaryRegion;
    private String standbyRegion;
    private boolean autoFailover;
    private long lastCheckTime;
    private Map<String, Object> healthStatus;

    public String getFailoverId() { return failoverId; }
    public void setFailoverId(String failoverId) { this.failoverId = failoverId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrimaryRegion() { return primaryRegion; }
    public void setPrimaryRegion(String primaryRegion) { this.primaryRegion = primaryRegion; }
    public String getStandbyRegion() { return standbyRegion; }
    public void setStandbyRegion(String standbyRegion) { this.standbyRegion = standbyRegion; }
    public boolean isAutoFailover() { return autoFailover; }
    public void setAutoFailover(boolean autoFailover) { this.autoFailover = autoFailover; }
    public long getLastCheckTime() { return lastCheckTime; }
    public void setLastCheckTime(long lastCheckTime) { this.lastCheckTime = lastCheckTime; }
    public Map<String, Object> getHealthStatus() { return healthStatus; }
    public void setHealthStatus(Map<String, Object> healthStatus) { this.healthStatus = healthStatus; }
}
