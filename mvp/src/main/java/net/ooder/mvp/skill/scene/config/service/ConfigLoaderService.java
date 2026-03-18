package net.ooder.mvp.skill.scene.config.service;

import net.ooder.mvp.skill.scene.config.sdk.ConfigNode;

import java.util.List;
import java.util.Map;

public interface ConfigLoaderService {

    ConfigNode loadSystemConfig();
    
    ConfigNode loadSkillConfig(String skillId, boolean resolveInheritance);
    
    ConfigNode loadSceneConfig(String sceneId, boolean resolveInheritance);
    
    ConfigNode loadInternalSkillConfig(String sceneId, String skillId);
    
    ConfigInheritanceChain getInheritanceChain(String targetType, String targetId);
    
    void saveConfig(String targetType, String targetId, ConfigNode config);
    
    void resetConfig(String targetType, String targetId, String key);
    
    void updateConfig(String targetType, String targetId, String path, Object value);
    
    Map<String, Object> getCapabilityConfig(String targetType, String targetId, String capabilityAddress);
    
    void updateCapabilityConfig(String targetType, String targetId, String capabilityAddress, Map<String, Object> config);
}
