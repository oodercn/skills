package net.ooder.skill.key.dto;

import java.util.List;
import java.util.Map;

public class KeyGenerateRequestDTO {
    
    private String userId;
    private String sceneGroupId;
    private String installId;
    private String scope;
    private String description;
    private String keyName;
    private String keyType;
    private String provider;
    private List<String> allowedUsers;
    private List<String> allowedRoles;
    private List<String> allowedScenes;
    private long expireTimeMs;
    private Map<String, Object> permissions;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getInstallId() { return installId; }
    public void setInstallId(String installId) { this.installId = installId; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public List<String> getAllowedUsers() { return allowedUsers; }
    public void setAllowedUsers(List<String> allowedUsers) { this.allowedUsers = allowedUsers; }
    public List<String> getAllowedRoles() { return allowedRoles; }
    public void setAllowedRoles(List<String> allowedRoles) { this.allowedRoles = allowedRoles; }
    public List<String> getAllowedScenes() { return allowedScenes; }
    public void setAllowedScenes(List<String> allowedScenes) { this.allowedScenes = allowedScenes; }
    public long getExpireTimeMs() { return expireTimeMs; }
    public void setExpireTimeMs(long expireTimeMs) { this.expireTimeMs = expireTimeMs; }
    public Map<String, Object> getPermissions() { return permissions; }
    public void setPermissions(Map<String, Object> permissions) { this.permissions = permissions; }
}
