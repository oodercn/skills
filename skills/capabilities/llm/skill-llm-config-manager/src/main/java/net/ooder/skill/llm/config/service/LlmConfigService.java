package net.ooder.skill.llm.config.service;

import net.ooder.skill.llm.config.model.LlmConfig;
import net.ooder.skill.llm.config.model.ResolvedConfig;

import java.util.List;
import java.util.Map;

public interface LlmConfigService {
    
    LlmConfig createConfig(LlmConfig config);
    
    LlmConfig updateConfig(String id, LlmConfig config);
    
    void deleteConfig(String id);
    
    LlmConfig getConfig(String id);
    
    LlmConfig getConfigByLevelAndScope(LlmConfig.ConfigLevel level, String scopeId);
    
    List<LlmConfig> getConfigsByLevel(LlmConfig.ConfigLevel level);
    
    List<LlmConfig> getAllConfigs();
    
    ResolvedConfig resolveConfig(String userId, String sceneId, String departmentId);
    
    ResolvedConfig resolveConfigWithPriority(String userId, String sceneId, String departmentId, Map<String, Object> context);
    
    String encryptApiKey(String apiKey);
    
    String decryptApiKey(String encryptedKey);
    
    boolean validateConfig(LlmConfig config);
}
