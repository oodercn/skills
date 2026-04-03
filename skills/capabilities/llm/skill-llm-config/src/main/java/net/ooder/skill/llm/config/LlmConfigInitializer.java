package net.ooder.skill.llm.config;

import net.ooder.skill.llm.config.dto.LlmConfigDTO;
import net.ooder.skill.llm.config.dto.LlmOptionsDTO;
import net.ooder.skill.llm.config.dto.ProviderConfigDTO;
import net.ooder.skill.llm.config.service.ApiKeyProvider;
import net.ooder.skill.llm.config.service.LlmConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class LlmConfigInitializer implements ApplicationRunner {
    
    private static final Logger log = LoggerFactory.getLogger(LlmConfigInitializer.class);
    
    @Autowired
    private LlmConfigService llmConfigService;
    
    @Autowired
    private ApiKeyProvider apiKeyProvider;
    
    @Value("${ooder.llm.provider:qianwen}")
    private String defaultProvider;
    
    @Value("${ooder.llm.model:qwen-plus}")
    private String defaultModel;
    
    @Value("${ooder.llm.qianwen.base-url:https://dashscope.aliyuncs.com/api/v1}")
    private String qianwenBaseUrl;
    
    @Value("${ooder.llm.deepseek.base-url:https://api.deepseek.com}")
    private String deepseekBaseUrl;
    
    @Value("${ooder.llm.baidu.base-url:https://qianfan.baidubce.com/v2}")
    private String baiduBaseUrl;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=== LLM Configuration Initialization ===");
        logAvailableSources();
        
        LlmConfigDTO systemConfig = llmConfigService.getConfigByScope(
            LlmConfigDTO.LEVEL_SYSTEM, "default");
        
        if (systemConfig == null) {
            log.info("Creating system LLM config from application configuration");
            createSystemConfig();
        } else if (!isValidConfig(systemConfig)) {
            log.info("Updating system LLM config with valid API key");
            updateSystemConfig(systemConfig);
        } else {
            log.info("System LLM config is valid and ready to use");
            logConfigDetails(systemConfig);
        }
        
        log.info("=== LLM Configuration Initialization Complete ===");
    }
    
    private void logAvailableSources() {
        String[] providers = {"qianwen", "deepseek", "baidu"};
        for (String provider : providers) {
            if (apiKeyProvider.hasApiKey(provider)) {
                ApiKeyProvider.ApiKeySource source = apiKeyProvider.getApiKeySource(provider);
                log.info("API Key available for {} from: {}", provider, source.getDescription());
            } else {
                log.warn("No API Key configured for {}", provider);
            }
        }
    }
    
    private boolean isValidConfig(LlmConfigDTO config) {
        if (config.getProviderConfig() == null) {
            return false;
        }
        
        String apiKey = config.getProviderConfig().getApiKey();
        return apiKey != null 
            && !apiKey.isEmpty() 
            && !"${API_KEY}".equals(apiKey);
    }
    
    private void createSystemConfig() {
        LlmConfigDTO config = new LlmConfigDTO();
        config.setName("系统默认配置");
        config.setLevel(LlmConfigDTO.LEVEL_SYSTEM);
        config.setScopeId("default");
        config.setProviderType(defaultProvider);
        config.setModel(defaultModel);
        
        ProviderConfigDTO providerConfig = createProviderConfig(defaultProvider);
        config.setProviderConfig(providerConfig);
        
        LlmOptionsDTO options = new LlmOptionsDTO();
        options.setTemperature(0.7);
        options.setMaxTokens(128000);
        config.setOptions(options);
        
        config.setEnabled(true);
        
        llmConfigService.createConfig(config, "system");
        log.info("System LLM config created successfully");
    }
    
    private void updateSystemConfig(LlmConfigDTO config) {
        String apiKey = apiKeyProvider.getApiKey(config.getProviderType());
        if (apiKey != null && !apiKey.isEmpty()) {
            ProviderConfigDTO providerConfig = config.getProviderConfig();
            if (providerConfig == null) {
                providerConfig = createProviderConfig(config.getProviderType());
                config.setProviderConfig(providerConfig);
            } else {
                providerConfig.setApiKey(apiKey);
            }
            
            llmConfigService.updateConfig(config.getId(), config, "system");
            log.info("System LLM config updated with API key from: {}", 
                apiKeyProvider.getApiKeySource(config.getProviderType()));
        }
    }
    
    private ProviderConfigDTO createProviderConfig(String providerType) {
        ProviderConfigDTO config = new ProviderConfigDTO();
        String apiKey = apiKeyProvider.getApiKey(providerType);
        config.setApiKey(apiKey != null ? apiKey : "");
        
        switch (providerType.toLowerCase()) {
            case "qianwen":
                config.setBaseUrl(qianwenBaseUrl);
                break;
            case "deepseek":
                config.setBaseUrl(deepseekBaseUrl);
                break;
            case "baidu":
                config.setBaseUrl(baiduBaseUrl);
                break;
            default:
                config.setBaseUrl("https://api.example.com/v1");
        }
        
        config.setTimeout(30000);
        config.setMaxRetries(3);
        
        return config;
    }
    
    private void logConfigDetails(LlmConfigDTO config) {
        log.info("Provider: {}", config.getProviderType());
        log.info("Model: {}", config.getModel());
        log.info("Base URL: {}", config.getProviderConfig().getBaseUrl());
        
        String apiKey = config.getProviderConfig().getApiKey();
        if (apiKey != null && !apiKey.isEmpty()) {
            String maskedKey = maskApiKey(apiKey);
            log.info("API Key: {} (source: {})", 
                maskedKey, 
                apiKeyProvider.getApiKeySource(config.getProviderType()).getDescription());
        }
    }
    
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "****";
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
}
