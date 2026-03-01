package net.ooder.nexus.common.dto;

import java.util.List;

public class Scene {
    private String sceneId;
    private String name;
    private String description;
    private String ownerId;
    private String ownerName;
    private List<SceneMember> members;
    private List<String> skillIds;
    private List<String> capabilities;
    private SceneStatus status;
    private String sceneKey;
    private SceneConfig config;
    private Long createTime;
    private Long updateTime;
    
    public enum SceneStatus {
        CREATED,
        ACTIVE,
        PAUSED,
        STOPPED,
        ARCHIVED
    }
    
    public static class SceneMember {
        private String memberId;
        private String memberName;
        private String role;
        private Long joinedAt;
        
        public String getMemberId() { return memberId; }
        public void setMemberId(String memberId) { this.memberId = memberId; }
        public String getMemberName() { return memberName; }
        public void setMemberName(String memberName) { this.memberName = memberName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Long getJoinedAt() { return joinedAt; }
        public void setJoinedAt(Long joinedAt) { this.joinedAt = joinedAt; }
    }
    
    public static class SceneConfig {
        private int maxMembers;
        private boolean autoStart;
        private String schedule;
        private Map<String, Object> customConfig;
        
        public int getMaxMembers() { return maxMembers; }
        public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
        public boolean isAutoStart() { return autoStart; }
        public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
        public Map<String, Object> getCustomConfig() { return customConfig; }
        public void setCustomConfig(Map<String, Object> customConfig) { this.customConfig = customConfig; }
    }
    
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public List<SceneMember> getMembers() { return members; }
    public void setMembers(List<SceneMember> members) { this.members = members; }
    public List<String> getSkillIds() { return skillIds; }
    public void setSkillIds(List<String> skillIds) { this.skillIds = skillIds; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    public SceneStatus getStatus() { return status; }
    public void setStatus(SceneStatus status) { this.status = status; }
    public String getSceneKey() { return sceneKey; }
    public void setSceneKey(String sceneKey) { this.sceneKey = sceneKey; }
    public SceneConfig getConfig() { return config; }
    public void setConfig(SceneConfig config) { this.config = config; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
    public Long getUpdateTime() { return updateTime; }
    public void setUpdateTime(Long updateTime) { this.updateTime = updateTime; }
}

import java.util.Map;
