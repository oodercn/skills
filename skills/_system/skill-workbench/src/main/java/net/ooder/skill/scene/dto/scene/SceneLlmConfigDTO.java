package net.ooder.skill.scene.dto.scene;

import java.util.Map;

public class SceneLlmConfigDTO {
    private String configId;
    private String level;
    private String model;
    private String provider;
    private Map<String, Object> options;
    private boolean fallbackEnabled;
    private String fallbackConfigId;

    public String getConfigId() { return configId; }
    public void setConfigId(String configId) { this.configId = configId; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public Map<String, Object> getOptions() { return options; }
    public void setOptions(Map<String, Object> options) { this.options = options; }
    public boolean isFallbackEnabled() { return fallbackEnabled; }
    public void setFallbackEnabled(boolean fallbackEnabled) { this.fallbackEnabled = fallbackEnabled; }
    public String getFallbackConfigId() { return fallbackConfigId; }
    public void setFallbackConfigId(String fallbackConfigId) { this.fallbackConfigId = fallbackConfigId; }
}
