package net.ooder.skill.todo.dto;

import java.util.Map;

public class TodoDTO {

    private String todoId;
    private String userId;
    private String title;
    private String description;
    private String type;
    private String status;
    private String priority;
    private String sceneGroupId;
    private String sceneGroupName;
    private String relatedCapabilityId;
    private String relatedCapabilityName;
    private Map<String, Object> metadata;
    private String callbackUrl;
    private long dueDate;
    private long createdAt;
    private long updatedAt;
    private long completedAt;

    public String getTodoId() { return todoId; }
    public void setTodoId(String todoId) { this.todoId = todoId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

    public String getSceneGroupName() { return sceneGroupName; }
    public void setSceneGroupName(String sceneGroupName) { this.sceneGroupName = sceneGroupName; }

    public String getRelatedCapabilityId() { return relatedCapabilityId; }
    public void setRelatedCapabilityId(String relatedCapabilityId) { this.relatedCapabilityId = relatedCapabilityId; }

    public String getRelatedCapabilityName() { return relatedCapabilityName; }
    public void setRelatedCapabilityName(String relatedCapabilityName) { this.relatedCapabilityName = relatedCapabilityName; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
}