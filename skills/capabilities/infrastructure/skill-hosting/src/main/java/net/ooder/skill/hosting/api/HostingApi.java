package net.ooder.skill.hosting.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 鎵樼鏈嶅姟API
 */
public interface HostingApi {
    
    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();
    
    // 瀹炰緥绠＄悊
    Result<Map<String, Object>> createInstance(Map<String, Object> config);
    Result<Boolean> deleteInstance(String instanceId);
    Result<Map<String, Object>> getInstance(String instanceId);
    Result<List<Map<String, Object>>> listInstances();
}
