package net.ooder.skill.share.model;

import java.util.List;
import java.util.Map;

public class DelegateRecord {
    
    private String delegateId;
    private String skillId;
    private String skillName;
    private String skillVersion;
    private String fromUserId;
    private String fromUserName;
    private List<String> toUserIds;
    private List<String> toUserNames;
    private String message;
    private Long deadline;
    private String priority;
    private Long createdAt;
    private String status;
    private String sourcePath;
    private Map<String, Object> extra;

    public DelegateRecord() {
        this.status = "pending";
        this.priority = "medium";
    }

    public String getDelegateId() {
        return delegateId;
    }

    public void setDelegateId(String delegateId) {
        this.delegateId = delegateId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillVersion() {
        return skillVersion;
    }

    public void setSkillVersion(String skillVersion) {
        this.skillVersion = skillVersion;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public List<String> getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(List<String> toUserIds) {
        this.toUserIds = toUserIds;
    }

    public List<String> getToUserNames() {
        return toUserNames;
    }

    public void setToUserNames(List<String> toUserNames) {
        this.toUserNames = toUserNames;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public boolean isExpired() {
        if (deadline == null) {
            return false;
        }
        return System.currentTimeMillis() > deadline;
    }

    public boolean isPending() {
        return "pending".equals(status) && !isExpired();
    }
}
