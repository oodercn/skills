package net.ooder.skill.key.dto;

import java.util.List;

public class CreateKeyRequest {
    
    private String sceneGroupId;
    private String installId;
    private String keyType;
    private String description;
    private String keyName;
    private String provider;
    private List<String> allowedUsers;
    private List<String> allowedRoles;
    private List<String> allowedScenes;
    private Long expiresAt;
    private Integer maxUseCount;

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getInstallId() { return installId; }
    public void setInstallId(String installId) { this.installId = installId; }
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public List<String> getAllowedUsers() { return allowedUsers; }
    public void setAllowedUsers(List<String> allowedUsers) { this.allowedUsers = allowedUsers; }
    public List<String> getAllowedRoles() { return allowedRoles; }
    public void setAllowedRoles(List<String> allowedRoles) { this.allowedRoles = allowedRoles; }
    public List<String> getAllowedScenes() { return allowedScenes; }
    public void setAllowedScenes(List<String> allowedScenes) { this.allowedScenes = allowedScenes; }
    public Long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Long expiresAt) { this.expiresAt = expiresAt; }
    public Integer getMaxUseCount() { return maxUseCount; }
    public void setMaxUseCount(Integer maxUseCount) { this.maxUseCount = maxUseCount; }
}
