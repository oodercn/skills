package net.ooder.skill.llm.config.dto;

import java.util.List;

public class LlmProviderConfigDTO {
    private String providerId;
    private String name;
    private String type;
    private String apiKey;
    private String baseUrl;
    private List<ModelConfigDTO> models;

    public static class ModelConfigDTO {
        private String modelId;
        private String modelName;
        private Integer contextLength;

        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public Integer getContextLength() { return contextLength; }
        public void setContextLength(Integer contextLength) { this.contextLength = contextLength; }
    }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public List<ModelConfigDTO> getModels() { return models; }
    public void setModels(List<ModelConfigDTO> models) { this.models = models; }
}
