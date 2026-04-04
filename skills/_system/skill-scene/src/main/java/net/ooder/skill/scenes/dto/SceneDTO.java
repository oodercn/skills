package net.ooder.skill.scenes.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SceneDTO implements Serializable {
    
    private String sceneId;
    private String sceneName;
    private String sceneType;
    private String status;
    private String description;
    private String ownerId;
    private List<String> members;
    private Map<String, Object> properties;
    private Long createdAt;
    private Long updatedAt;
    
    public SceneDTO() {
    }
    
    public SceneDTO(String sceneId, String sceneName) {
        this.sceneId = sceneId;
        this.sceneName = sceneName;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getSceneName() {
        return sceneName;
    }
    
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
    
    public String getSceneType() {
        return sceneType;
    }
    
    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    
    public List<String> getMembers() {
        return members;
    }
    
    public void setMembers(List<String> members) {
        this.members = members;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
    
    public Long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getName() {
        return sceneName;
    }
    
    public String getType() {
        return sceneType;
    }
}
