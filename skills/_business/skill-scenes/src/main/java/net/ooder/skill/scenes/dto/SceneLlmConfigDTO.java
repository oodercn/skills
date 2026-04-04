package net.ooder.skill.scenes.dto;

public class SceneLlmConfigDTO {
    private String providerId;
    private String modelId;
    private Double temperature;
    private Integer maxTokens;
    private String systemPrompt;

    public SceneLlmConfigDTO() {
        this.temperature = 0.7;
        this.maxTokens = 4096;
    }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
}
