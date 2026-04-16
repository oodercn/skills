package net.ooder.skill.config.dto;

import java.util.Map;

public class CliConfigDTO {
    
    private String cliId;
    private String name;
    private String type;
    private boolean enabled;
    private String icon;
    private String description;
    private Map<String, Object> settings;
    private String status;
    private long lastSyncTime;
    private String appId;
    private String appSecret;
    private String baseUrl;
    private String callbackUrl;
    private String token;
    private String encodingAesKey;

    public String getCliId() { return cliId; }
    public void setCliId(String cliId) { this.cliId = cliId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, Object> getSettings() { return settings; }
    public void setSettings(Map<String, Object> settings) { this.settings = settings; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(long lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getAppSecret() { return appSecret; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEncodingAesKey() { return encodingAesKey; }
    public void setEncodingAesKey(String encodingAesKey) { this.encodingAesKey = encodingAesKey; }
}
