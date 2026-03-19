package net.ooder.mvp.skill.scene.llm.service;

import net.ooder.mvp.skill.scene.llm.model.LlmConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LlmConfigService {
    
    private static final Logger log = LoggerFactory.getLogger(LlmConfigService.class);
    
    private final Map<String, LlmConfig> configStore = new ConcurrentHashMap<>();
    
    public LlmConfigService() {
        initDefaultConfigs();
    }
    
    private void initDefaultConfigs() {
        long now = System.currentTimeMillis();
        
        LlmConfig aliyunBailianConfig = new LlmConfig();
        aliyunBailianConfig.setId("llm-config-aliyun-bailian");
        aliyunBailianConfig.setName("阿里百联默认配置");
        aliyunBailianConfig.setLevel(LlmConfig.ConfigLevel.DEPARTMENT);
        aliyunBailianConfig.setScopeId("default");
        aliyunBailianConfig.setProviderType("aliyun-bailian");
        aliyunBailianConfig.setModel("qwen-plus");
        aliyunBailianConfig.setEnabled(true);
        aliyunBailianConfig.setCreatedAt(now);
        aliyunBailianConfig.setUpdatedAt(now);
        aliyunBailianConfig.setCreatedBy("system");
        
        Map<String, Object> aliyunOptions = new HashMap<>();
        aliyunOptions.put("temperature", 0.7);
        aliyunOptions.put("max_tokens", 4096);
        aliyunBailianConfig.setOptions(aliyunOptions);
        
        configStore.put(aliyunBailianConfig.getId(), aliyunBailianConfig);
        
        LlmConfig deepseekConfig = new LlmConfig();
        deepseekConfig.setId("llm-config-deepseek");
        deepseekConfig.setName("DeepSeek默认配置");
        deepseekConfig.setLevel(LlmConfig.ConfigLevel.DEPARTMENT);
        deepseekConfig.setScopeId("default");
        deepseekConfig.setProviderType("deepseek");
        deepseekConfig.setModel("deepseek-chat");
        deepseekConfig.setEnabled(true);
        deepseekConfig.setCreatedAt(now);
        deepseekConfig.setUpdatedAt(now);
        deepseekConfig.setCreatedBy("system");
        
        Map<String, Object> deepseekOptions = new HashMap<>();
        deepseekOptions.put("temperature", 0.7);
        deepseekOptions.put("max_tokens", 4096);
        deepseekConfig.setOptions(deepseekOptions);
        
        configStore.put(deepseekConfig.getId(), deepseekConfig);
        
        log.info("Initialized default LLM configs: aliyun-bailian, deepseek");
    }
    
    public boolean validateConfig(LlmConfig config) {
        if (config.getName() == null || config.getName().isEmpty()) {
            return false;
        }
        if (config.getLevel() == null) {
            return false;
        }
        if (config.getProviderType() == null || config.getProviderType().isEmpty()) {
            return false;
        }
        if (config.getModel() == null || config.getModel().isEmpty()) {
            return false;
        }
        return true;
    }
    
    public LlmConfig createConfig(LlmConfig config) {
        if (config.getId() == null || config.getId().isEmpty()) {
            config.setId("llm-config-" + UUID.randomUUID().toString());
        }
        
        long now = System.currentTimeMillis();
        config.setCreatedAt(now);
        config.setUpdatedAt(now);
        
        configStore.put(config.getId(), config);
        log.info("Created LLM config: id={}, name={}", config.getId(), config.getName());
        
        return config;
    }
    
    public LlmConfig updateConfig(String id, LlmConfig config) {
        LlmConfig existing = configStore.get(id);
        if (existing == null) {
            throw new RuntimeException("Config not found: " + id);
        }
        
        config.setId(id);
        config.setCreatedAt(existing.getCreatedAt());
        config.setUpdatedAt(System.currentTimeMillis());
        
        configStore.put(id, config);
        log.info("Updated LLM config: id={}", id);
        
        return config;
    }
    
    public void deleteConfig(String id) {
        LlmConfig removed = configStore.remove(id);
        if (removed != null) {
            log.info("Deleted LLM config: id={}, name={}", id, removed.getName());
        }
    }
    
    public LlmConfig getConfig(String id) {
        return configStore.get(id);
    }
    
    public List<LlmConfig> getAllConfigs() {
        return new ArrayList<>(configStore.values());
    }
    
    public List<LlmConfig> getConfigsByLevel(LlmConfig.ConfigLevel level) {
        return configStore.values().stream()
            .filter(c -> c.getLevel() == level)
            .collect(Collectors.toList());
    }
    
    public LlmConfig getConfigByLevelAndScope(LlmConfig.ConfigLevel level, String scopeId) {
        return configStore.values().stream()
            .filter(c -> c.getLevel() == level && scopeId.equals(c.getScopeId()))
            .findFirst()
            .orElse(null);
    }
    
    public LlmConfig resolveConfig(String userId, String sceneId, String departmentId, Map<String, Object> context) {
        if (sceneId != null) {
            LlmConfig sceneConfig = getConfigByLevelAndScope(LlmConfig.ConfigLevel.SCENE, sceneId);
            if (sceneConfig != null && sceneConfig.isEnabled()) {
                return sceneConfig;
            }
        }
        
        if (userId != null) {
            LlmConfig personalConfig = getConfigByLevelAndScope(LlmConfig.ConfigLevel.PERSONAL, userId);
            if (personalConfig != null && personalConfig.isEnabled()) {
                return personalConfig;
            }
        }
        
        if (departmentId != null) {
            LlmConfig deptConfig = getConfigByLevelAndScope(LlmConfig.ConfigLevel.DEPARTMENT, departmentId);
            if (deptConfig != null && deptConfig.isEnabled()) {
                return deptConfig;
            }
        }
        
        LlmConfig defaultDeptConfig = getConfigByLevelAndScope(LlmConfig.ConfigLevel.DEPARTMENT, "default");
        if (defaultDeptConfig != null && defaultDeptConfig.isEnabled()) {
            return defaultDeptConfig;
        }
        
        LlmConfig enterpriseConfig = getConfigByLevelAndScope(LlmConfig.ConfigLevel.ENTERPRISE, "default");
        if (enterpriseConfig != null && enterpriseConfig.isEnabled()) {
            return enterpriseConfig;
        }
        
        return getConfigsByLevel(LlmConfig.ConfigLevel.DEPARTMENT).stream()
            .filter(LlmConfig::isEnabled)
            .findFirst()
            .orElse(null);
    }
}
