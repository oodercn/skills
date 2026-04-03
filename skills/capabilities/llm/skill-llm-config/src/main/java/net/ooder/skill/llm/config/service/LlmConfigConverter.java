package net.ooder.skill.llm.config.service;

import net.ooder.skill.llm.config.dto.*;

public class LlmConfigConverter {
    
    public static ProviderConfigDTO createProviderConfig(String apiKey, String baseUrl) {
        ProviderConfigDTO config = new ProviderConfigDTO();
        config.setApiKey(apiKey);
        config.setBaseUrl(baseUrl);
        config.setTimeout(30000);
        config.setMaxRetries(3);
        return config;
    }
    
    public static LlmOptionsDTO createDefaultOptions() {
        LlmOptionsDTO options = new LlmOptionsDTO();
        options.setTemperature(0.7);
        options.setMaxTokens(128000);
        options.setTopP(1.0);
        options.setStream(false);
        return options;
    }
    
    public static LlmOptionsDTO createOptions(Double temperature, Integer maxTokens) {
        LlmOptionsDTO options = new LlmOptionsDTO();
        options.setTemperature(temperature);
        options.setMaxTokens(maxTokens);
        options.setTopP(1.0);
        options.setStream(false);
        return options;
    }
    
    public static LlmConfigDTO createConfig(String name, String level, String providerType, String model) {
        LlmConfigDTO config = new LlmConfigDTO();
        config.setName(name);
        config.setLevel(level);
        config.setProviderType(providerType);
        config.setModel(model);
        config.setEnabled(true);
        config.setCreatedAt(System.currentTimeMillis());
        config.setUpdatedAt(System.currentTimeMillis());
        return config;
    }
    
    public static LlmConfigDTO createSystemConfig(String providerType, String model, 
                                                    String apiKey, String baseUrl) {
        LlmConfigDTO config = createConfig("系统默认配置", LlmConfigDTO.LEVEL_SYSTEM, providerType, model);
        config.setScopeId("default");
        config.setDescription("系统级默认LLM配置");
        config.setProviderConfig(createProviderConfig(apiKey, baseUrl));
        config.setOptions(createDefaultOptions());
        config.setCreatedBy("system");
        return config;
    }
    
    public static RateLimitsDTO createDefaultRateLimits() {
        RateLimitsDTO limits = new RateLimitsDTO();
        limits.setRequestsPerMinute(60);
        limits.setTokensPerMinute(100000);
        limits.setRequestsPerDay(10000);
        limits.setTokensPerDay(10000000);
        limits.setConcurrentRequests(10);
        return limits;
    }
    
    public static CostConfigDTO createDefaultCostConfig() {
        CostConfigDTO costConfig = new CostConfigDTO();
        costConfig.setTrackCosts(true);
        costConfig.setAlertThreshold(80.0);
        return costConfig;
    }
}
