package net.ooder.skill.llm.config.dto;

import java.util.List;

public class LlmProviderMeta {
    private String providerId;
    private String name;
    private String description;
    private List<ConfigField> configFields;
    private List<ModelInfo> models;

    public static class ConfigField {
        private String name;
        private String type;
        private boolean required;
        private String defaultValue;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    }

    public static class ModelInfo {
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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ConfigField> getConfigFields() { return configFields; }
    public void setConfigFields(List<ConfigField> configFields) { this.configFields = configFields; }
    public List<ModelInfo> getModels() { return models; }
    public void setModels(List<ModelInfo> models) { this.models = models; }
}
