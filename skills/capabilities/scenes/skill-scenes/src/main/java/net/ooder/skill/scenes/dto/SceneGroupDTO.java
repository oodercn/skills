package net.ooder.skill.scenes.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SceneGroupDTO {
    private String id;
    private String name;
    private String description;
    private String status;
    private String ownerId;
    private String ownerName;
    private List<String> memberIds;
    private List<Map<String, Object>> capabilities;
    private Map<String, Object> llmConfig;
    private Map<String, Object> knowledgeConfig;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

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

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public List<Map<String, Object>> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<Map<String, Object>> capabilities) {
        this.capabilities = capabilities;
    }

    public Map<String, Object> getLlmConfig() {
        return llmConfig;
    }

    public void setLlmConfig(Map<String, Object> llmConfig) {
        this.llmConfig = llmConfig;
    }

    public Map<String, Object> getKnowledgeConfig() {
        return knowledgeConfig;
    }

    public void setKnowledgeConfig(Map<String, Object> knowledgeConfig) {
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
