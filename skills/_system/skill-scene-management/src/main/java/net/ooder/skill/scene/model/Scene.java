package net.ooder.skill.scene.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Scene {

    private String sceneId;
    private String name;
    private String description;
    private SceneStatus status;
    private String type;
    private List<String> capabilities;
    private List<String> participants;
    private Map<String, Object> config;
    private Date createdAt;
    private Date updatedAt;

    public enum SceneStatus {
        DRAFT,
        ACTIVE,
        PAUSED,
        COMPLETED,
        ARCHIVED
    }

    public Scene() {
        this.status = SceneStatus.DRAFT;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SceneStatus getStatus() {
        return status;
    }

    public void setStatus(SceneStatus status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
