package net.ooder.skill.llm.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LlmConfigDTO implements Serializable {

    public static final String LEVEL_SYSTEM = "SYSTEM";
    public static final String LEVEL_ENTERPRISE = "ENTERPRISE";
    public static final String LEVEL_AGENT = "AGENT";
    public static final String LEVEL_SCENE = "SCENE";
    public static final String LEVEL_USER = "USER";

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "配置名称不能为空")
    @Size(max = 200, message = "配置名称长度不能超过200")
    private String name;

    @NotBlank(message = "配置级别不能为空")
    private String level;

    @Size(max = 100, message = "范围ID长度不能超过100")
    private String scopeId;

    @NotBlank(message = "提供商类型不能为空")
    private String providerType;

    @NotBlank(message = "模型不能为空")
    private String model;

    private ProviderConfigDTO providerConfig;
    private LlmOptionsDTO options;
    private Boolean enabled;
    private Long updatedAt;
    private Long createdAt;
    private String createdBy;

    private String agentId;
    private List<String> models;
    private RateLimitsDTO rateLimits;
    private CostConfigDTO costConfig;
    private boolean fallbackEnabled;
    private String fallbackConfigId;
    private long lastUsedTime;
    private long totalTokens;
    private long totalRequests;
    private double totalCost;
    private String description;
    private List<String> tags;
    private int priority;
    private ExtendedConfigDTO extendedConfig;

    public LlmConfigDTO() {
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public List<String> getModels() {
        return models;
    }

    public void setModels(List<String> models) {
        this.models = models;
    }

    public RateLimitsDTO getRateLimits() {
        return rateLimits;
    }

    public void setRateLimits(RateLimitsDTO rateLimits) {
        this.rateLimits = rateLimits;
    }

    public CostConfigDTO getCostConfig() {
        return costConfig;
    }

    public void setCostConfig(CostConfigDTO costConfig) {
        this.costConfig = costConfig;
    }

    public boolean isFallbackEnabled() {
        return fallbackEnabled;
    }

    public void setFallbackEnabled(boolean fallbackEnabled) {
        this.fallbackEnabled = fallbackEnabled;
    }

    public String getFallbackConfigId() {
        return fallbackConfigId;
    }

    public void setFallbackConfigId(String fallbackConfigId) {
        this.fallbackConfigId = fallbackConfigId;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public long getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(long totalTokens) {
        this.totalTokens = totalTokens;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ExtendedConfigDTO getExtendedConfig() {
        return extendedConfig;
    }

    public void setExtendedConfig(ExtendedConfigDTO extendedConfig) {
        this.extendedConfig = extendedConfig;
    }

    public int getLevelPriority() {
        switch (level) {
            case LEVEL_USER:
                return 100;
            case LEVEL_SCENE:
                return 80;
            case LEVEL_AGENT:
                return 60;
            case LEVEL_ENTERPRISE:
                return 40;
            case LEVEL_SYSTEM:
                return 20;
            default:
                return 0;
        }
    }

    public boolean isHigherPriorityThan(LlmConfigDTO other) {
        if (other == null)
            return true;
        return this.getLevelPriority() > other.getLevelPriority();
    }

    @JsonIgnore
    public boolean isUserLevel() {
        return LEVEL_USER.equals(this.level);
    }

    @JsonIgnore
    public boolean hasCustomProviderConfig() {
        return providerConfig != null && providerConfig.getApiKey() != null;
    }
}
