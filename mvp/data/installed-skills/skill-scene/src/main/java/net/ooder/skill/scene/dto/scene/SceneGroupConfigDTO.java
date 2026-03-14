package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class SceneGroupConfigDTO {
    private String name;
    private String description;
    private String creatorId;
    private ParticipantType creatorType;
    private Integer minMembers;
    private Integer maxMembers;
    private String securityPolicy;
    private Long heartbeatInterval;
    private Long heartbeatTimeout;
    private Map<String, Object> extendedConfig;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public ParticipantType getCreatorType() { return creatorType; }
    public void setCreatorType(ParticipantType creatorType) { this.creatorType = creatorType; }
    public Integer getMinMembers() { return minMembers; }
    public void setMinMembers(Integer minMembers) { this.minMembers = minMembers; }
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    public String getSecurityPolicy() { return securityPolicy; }
    public void setSecurityPolicy(String securityPolicy) { this.securityPolicy = securityPolicy; }
    public Long getHeartbeatInterval() { return heartbeatInterval; }
    public void setHeartbeatInterval(Long heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
    public Long getHeartbeatTimeout() { return heartbeatTimeout; }
    public void setHeartbeatTimeout(Long heartbeatTimeout) { this.heartbeatTimeout = heartbeatTimeout; }
    public Map<String, Object> getExtendedConfig() { return extendedConfig; }
    public void setExtendedConfig(Map<String, Object> extendedConfig) { this.extendedConfig = extendedConfig; }
}
