package net.ooder.skill.llm.config.service.impl;

import net.ooder.skill.llm.config.service.ApiKeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiKeyProviderImpl implements ApiKeyProvider {
    
    private static final Logger log = LoggerFactory.getLogger(ApiKeyProviderImpl.class);
    
    private final Map<String, String> commandLineApiKeys = new ConcurrentHashMap<>();
    private final Map<String, String> runtimeApiKeys = new ConcurrentHashMap<>();
    
    @Value("${ooder.llm.qianwen.api-key:}")
    private String qianwenApiKeyFromConfig;
    
    @Value("${ooder.llm.deepseek.api-key:}")
    private String deepseekApiKeyFromConfig;
    
    @Value("${ooder.llm.baidu.api-key:}")
    private String baiduApiKeyFromConfig;
    
    @Value("${qianwen.api-key:}")
    private String qianwenApiKeyFromRootConfig;
    
    @Value("${deepseek.api-key:}")
    private String deepseekApiKeyFromRootConfig;
    
    @Value("${baidu.api-key:}")
    private String baiduApiKeyFromRootConfig;
    
    public ApiKeyProviderImpl() {
        loadCommandLineArguments();
    }
    
    private void loadCommandLineArguments() {
        Map<String, String> env = System.getenv();
        
        String qianwenKey = System.getProperty("qianwen.api.key");
        if (qianwenKey != null && !qianwenKey.isEmpty()) {
            commandLineApiKeys.put("qianwen", qianwenKey);
            log.info("Loaded Qianwen API Key from command line argument");
        }
        
        String deepseekKey = System.getProperty("deepseek.api.key");
        if (deepseekKey != null && !deepseekKey.isEmpty()) {
            commandLineApiKeys.put("deepseek", deepseekKey);
            log.info("Loaded DeepSeek API Key from command line argument");
        }
        
        String baiduKey = System.getProperty("baidu.api.key");
        if (baiduKey != null && !baiduKey.isEmpty()) {
            commandLineApiKeys.put("baidu", baiduKey);
            log.info("Loaded Baidu API Key from command line argument");
        }
        
        String envQianwenKey = env.get("QIANWEN_API_KEY");
        if (envQianwenKey != null && !envQianwenKey.isEmpty()) {
            commandLineApiKeys.putIfAbsent("qianwen", envQianwenKey);
            log.info("Loaded Qianwen API Key from environment variable");
        }
        
        String envDeepseekKey = env.get("DEEPSEEK_API_KEY");
        if (envDeepseekKey != null && !envDeepseekKey.isEmpty()) {
            commandLineApiKeys.putIfAbsent("deepseek", envDeepseekKey);
            log.info("Loaded DeepSeek API Key from environment variable");
        }
        
        String envBaiduKey = env.get("BAIDU_API_KEY");
        if (envBaiduKey != null && !envBaiduKey.isEmpty()) {
            commandLineApiKeys.putIfAbsent("baidu", envBaiduKey);
            log.info("Loaded Baidu API Key from environment variable");
        }
    }
    
    @Override
    public String getApiKey(String providerType) {
        return getApiKey(providerType, null);
    }
    
    @Override
    public String getApiKey(String providerType, String scopeId) {
        String key = providerType.toLowerCase();
        
        String apiKey = runtimeApiKeys.get(key);
        if (apiKey != null && !apiKey.isEmpty()) {
            log.debug("Using runtime API Key for provider: {}", providerType);
            return apiKey;
        }
        
        apiKey = commandLineApiKeys.get(key);
        if (apiKey != null && !apiKey.isEmpty()) {
            log.debug("Using command line/environment API Key for provider: {}", providerType);
            return apiKey;
        }
        
        apiKey = getApiKeyFromConfig(providerType);
        if (apiKey != null && !apiKey.isEmpty()) {
            log.debug("Using config file API Key for provider: {}", providerType);
            return apiKey;
        }
        
        log.warn("No API Key found for provider: {}", providerType);
        return null;
    }
    
    private String getApiKeyFromConfig(String providerType) {
        switch (providerType.toLowerCase()) {
            case "qianwen":
                return getConfigValue(qianwenApiKeyFromConfig, qianwenApiKeyFromRootConfig);
            case "deepseek":
                return getConfigValue(deepseekApiKeyFromConfig, deepseekApiKeyFromRootConfig);
            case "baidu":
                return getConfigValue(baiduApiKeyFromConfig, baiduApiKeyFromRootConfig);
            default:
                return null;
        }
    }
    
    private String getConfigValue(String primary, String fallback) {
        if (primary != null && !primary.isEmpty()) {
            return primary;
        }
        return fallback;
    }
    
    @Override
    public boolean hasApiKey(String providerType) {
        String apiKey = getApiKey(providerType);
        return apiKey != null && !apiKey.isEmpty();
    }
    
    @Override
    public void setApiKey(String providerType, String apiKey) {
        if (apiKey != null && !apiKey.isEmpty()) {
            runtimeApiKeys.put(providerType.toLowerCase(), apiKey);
            log.info("Runtime API Key set for provider: {}", providerType);
        } else {
            runtimeApiKeys.remove(providerType.toLowerCase());
            log.info("Runtime API Key removed for provider: {}", providerType);
        }
    }
    
    @Override
    public ApiKeySource getApiKeySource(String providerType) {
        String key = providerType.toLowerCase();
        
        if (runtimeApiKeys.containsKey(key)) {
            return ApiKeySource.DATABASE;
        }
        
        if (commandLineApiKeys.containsKey(key)) {
            String value = commandLineApiKeys.get(key);
            if (System.getProperty(providerType.toLowerCase() + ".api.key") != null) {
                return ApiKeySource.COMMAND_LINE;
            }
            return ApiKeySource.ENVIRONMENT;
        }
        
        if (getApiKeyFromConfig(providerType) != null) {
            return ApiKeySource.CONFIG_FILE;
        }
        
        return ApiKeySource.DEFAULT;
    }
}
