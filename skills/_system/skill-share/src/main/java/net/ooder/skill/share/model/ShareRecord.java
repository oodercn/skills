package net.ooder.skill.share.model;

import java.util.List;
import java.util.Map;

public class ShareRecord {
    
    private String shareId;
    private String shareCode;
    private String skillId;
    private String skillName;
    private String skillVersion;
    private String fromUserId;
    private String fromUserName;
    private List<String> toUserIds;
    private List<String> toUserNames;
    private String message;
    private String password;
    private boolean passwordProtected;
    private Long expiresAt;
    private Integer maxAccessCount;
    private Integer accessCount;
    private Long createdAt;
    private String status;
    private String sourcePath;
    private Map<String, Object> extra;

    public ShareRecord() {
        this.accessCount = 0;
        this.status = "active";
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        this.passwordProtected = password != null && !password.isEmpty();
    }

    public boolean isPasswordProtected() {
        return passwordProtected;
    }

    public void setPasswordProtected(boolean passwordProtected) {
        this.passwordProtected = passwordProtected;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getMaxAccessCount() {
        return maxAccessCount;
    }

    public void setMaxAccessCount(Integer maxAccessCount) {
        this.maxAccessCount = maxAccessCount;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
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
        if (expiresAt == null) {
            return false;
        }
        return System.currentTimeMillis() > expiresAt;
    }

    public boolean isActive() {
        return "active".equals(status) && !isExpired() && 
               (maxAccessCount == null || accessCount < maxAccessCount);
    }
}
