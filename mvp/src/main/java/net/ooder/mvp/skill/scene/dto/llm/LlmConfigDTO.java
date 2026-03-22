package net.ooder.mvp.skill.scene.dto.llm;

import java.util.Map;

public class LlmConfigDTO {
    
    private Long id;
    private String name;
    private String level;
    private String scopeId;
    private String providerType;
    private String model;
    private Map<String, Object> providerConfig;
    private Map<String, Object> options;
    private Boolean enabled;
    private Long updatedAt;
    
    public LlmConfigDTO() {
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public String getScopeId() { return scopeId; }
    public void setScopeId(String scopeId) { this.scopeId = scopeId; }
    
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Map<String, Object> getProviderConfig() { return providerConfig; }
    public void setProviderConfig(Map<String, Object> providerConfig) { this.providerConfig = providerConfig; }
    
    public Map<String, Object> getOptions() { return options; }
    public void setOptions(Map<String, Object> options) { this.options = options; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
