package net.ooder.skill.key.dto;

import java.util.List;
import java.util.Map;

public class KeyRuleDTO {
    
    private String ruleId;
    private String ruleName;
    private String scope;
    private String keyType;
    private String status;
    private String description;
    private int maxUseCount;
    private long expireTimeMs;
    private List<String> allowedProviders;
    private List<String> allowedScenes;
    private Map<String, Object> constraints;
    private long createdAt;
    private long updatedAt;

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getMaxUseCount() { return maxUseCount; }
    public void setMaxUseCount(int maxUseCount) { this.maxUseCount = maxUseCount; }
    public long getExpireTimeMs() { return expireTimeMs; }
    public void setExpireTimeMs(long expireTimeMs) { this.expireTimeMs = expireTimeMs; }
    public List<String> getAllowedProviders() { return allowedProviders; }
    public void setAllowedProviders(List<String> allowedProviders) { this.allowedProviders = allowedProviders; }
    public List<String> getAllowedScenes() { return allowedScenes; }
    public void setAllowedScenes(List<String> allowedScenes) { this.allowedScenes = allowedScenes; }
    public Map<String, Object> getConstraints() { return constraints; }
    public void setConstraints(Map<String, Object> constraints) { this.constraints = constraints; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
