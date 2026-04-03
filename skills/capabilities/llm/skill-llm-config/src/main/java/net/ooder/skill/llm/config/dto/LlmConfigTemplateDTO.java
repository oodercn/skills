package net.ooder.skill.llm.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LlmConfigTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "模板名称不能为空")
    @Size(max = 200, message = "模板名称长度不能超过200")
    private String name;

    @Size(max = 1000, message = "描述长度不能超过1000")
    private String description;

    @NotBlank(message = "提供商类型不能为空")
    private String providerType;

    @NotBlank(message = "模型不能为空")
    private String model;

    private ProviderConfigDTO providerConfig;
    private LlmOptionsDTO options;
    private List<String> tags;
    private Boolean isDefault;
    private Boolean isBuiltin;
    private Long createdAt;
    private Long updatedAt;
    private String createdBy;

    public LlmConfigTemplateDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public ProviderConfigDTO getProviderConfig() {
        return providerConfig;
    }

    public void setProviderConfig(ProviderConfigDTO providerConfig) {
        this.providerConfig = providerConfig;
    }

    public LlmOptionsDTO getOptions() {
        return options;
    }

    public void setOptions(LlmOptionsDTO options) {
        this.options = options;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getIsBuiltin() {
        return isBuiltin;
    }

    public void setBuiltin(Boolean isBuiltin) {
        this.isBuiltin = isBuiltin;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonIgnore
    public boolean isBuiltinTemplate() {
        return isBuiltin != null && isBuiltin;
    }

    @JsonIgnore
    public boolean isDefaultTemplate() {
        return isDefault != null && isDefault;
    }

    @JsonIgnore
    public boolean isBuiltin() {
        return isBuiltin != null && isBuiltin;
    }

    public LlmConfigDTO toConfigDTO(String level, String scopeId) {
        LlmConfigDTO config = new LlmConfigDTO();
        config.setName(this.name + " - 实例");
        config.setLevel(level);
        config.setScopeId(scopeId);
        config.setProviderType(this.providerType);
        config.setModel(this.model);
        config.setProviderConfig(this.providerConfig);
        config.setOptions(this.options);
        config.setDescription("从模板创建: " + this.name);
        config.setEnabled(true);
        return config;
    }
}
