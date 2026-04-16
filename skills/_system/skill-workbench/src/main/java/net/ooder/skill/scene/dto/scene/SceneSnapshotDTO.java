package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class SceneSnapshotDTO {
    
    private String snapshotId;
    private String sceneGroupId;
    private String name;
    private String description;
    private Map<String, Object> state;
    private long createTime;
    private String creator;

    public String getSnapshotId() { return snapshotId; }
    public void setSnapshotId(String snapshotId) { this.snapshotId = snapshotId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getState() { return state; }
    public void setState(Map<String, Object> state) { this.state = state; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
}
