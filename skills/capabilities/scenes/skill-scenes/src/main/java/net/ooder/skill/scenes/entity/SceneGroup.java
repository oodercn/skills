package net.ooder.skill.scenes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scene_groups")
public class SceneGroup {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "status", length = 50)
    private String status = "active";

    @Column(name = "owner_id", length = 64)
    private String ownerId;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "template_id", length = 64)
    private String templateId;

    @Column(name = "llm_config", length = 4000)
    private String llmConfig;

    @Column(name = "knowledge_config", length = 4000)
    private String knowledgeConfig;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getLlmConfig() {
        return llmConfig;
    }

    public void setLlmConfig(String llmConfig) {
        this.llmConfig = llmConfig;
    }

    public String getKnowledgeConfig() {
        return knowledgeConfig;
    }

    public void setKnowledgeConfig(String knowledgeConfig) {
        this.knowledgeConfig = knowledgeConfig;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
