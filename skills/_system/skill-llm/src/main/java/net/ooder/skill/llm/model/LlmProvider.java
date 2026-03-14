package net.ooder.skill.llm.model;

import java.util.*;

public class LlmProvider {
    private String providerId;
    private String name;
    private String type;
    private String endpoint;
    private String apiKey;
    private List<String> models;
    private Map<String, Object> config;
    private boolean enabled;
    private Date createdAt;
    private Date updatedAt;

    public LlmProvider() {
        this.models = new ArrayList<>();
        this.config = new HashMap<>();
        this.enabled = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public List<String> getModels() { return models; }
    public void setModels(List<String> models) { this.models = models; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
