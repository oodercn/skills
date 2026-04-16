package net.ooder.skill.agent.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_todo", indexes = {
    @Index(name = "idx_todo_scene_group", columnList = "sceneGroupId"),
    @Index(name = "idx_todo_status", columnList = "status"),
    @Index(name = "idx_todo_assignee", columnList = "assignee"),
    @Index(name = "idx_todo_deadline", columnList = "deadline")
})
public class Todo {

    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 36)
    private String sceneGroupId;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String status;

    @Column(length = 10)
    private String priority;

    @Column(length = 20)
    private String type;

    @Column(length = 36)
    private String assignee;

    @Column(length = 36)
    private String creator;

    @Column(length = 36)
    private String toUser;

    @Column(length = 36)
    private String fromUser;

    @Column
    private LocalDateTime deadline;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private LocalDateTime completedTime;

    @Column(length = 36)
    private String completedBy;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    public Todo() {
        this.status = "PENDING";
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PrePersist
    protected void onPersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
    }

    public static Builder builder() { return new Builder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }

    public String getFromUser() { return fromUser; }
    public void setFromUser(String fromUser) { this.fromUser = fromUser; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }

    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }

    @PreUpdate
    protected void onUpdate() { this.updateTime = LocalDateTime.now(); }

    public static class Builder {
        private final Todo todo = new Todo();

        public Builder id(String id) { todo.id = id; return this; }
        public Builder sceneGroupId(String sceneGroupId) { todo.sceneGroupId = sceneGroupId; return this; }
        public Builder title(String title) { todo.title = title; return this; }
        public Builder description(String description) { todo.description = description; return this; }
        public Builder status(String status) { todo.status = status; return this; }
        public Builder priority(String priority) { todo.priority = priority; return this; }
        public Builder type(String type) { todo.type = type; return this; }
        public Builder assignee(String assignee) { todo.assignee = assignee; return this; }
        public Builder creator(String creator) { todo.creator = creator; return this; }
        public Builder toUser(String toUser) { todo.toUser = toUser; return this; }
        public Builder fromUser(String fromUser) { todo.fromUser = fromUser; return this; }
        public Builder deadline(LocalDateTime deadline) { todo.deadline = deadline; return this; }
        public Builder metadataJson(String json) { todo.metadataJson = json; return this; }

        public Todo build() { return todo; }
    }
}
