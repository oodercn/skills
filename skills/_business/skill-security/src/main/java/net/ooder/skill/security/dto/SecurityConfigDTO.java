package net.ooder.skill.security.dto;

import java.util.Map;

public class SecurityConfigDTO {

    private boolean enabled;
    private String encryption;
    private Map<String, Object> policies;
    private long lastUpdated;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getEncryption() { return encryption; }
    public void setEncryption(String encryption) { this.encryption = encryption; }
    public Map<String, Object> getPolicies() { return policies; }
    public void setPolicies(Map<String, Object> policies) { this.policies = policies; }
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}