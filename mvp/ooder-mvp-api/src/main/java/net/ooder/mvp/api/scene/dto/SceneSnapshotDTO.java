package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;

public class SceneSnapshotDTO implements Serializable {
    private String snapshotId;
    private String sceneGroupId;
    private long createTime;
    private String status;
    private Map<String, Object> state;
    
    public String getSnapshotId() { return snapshotId; }
    public void setSnapshotId(String snapshotId) { this.snapshotId = snapshotId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<String, Object> getState() { return state; }
    public void setState(Map<String, Object> state) { this.state = state; }
}
