package net.ooder.skill.health.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 鍋ュ悍妫€鏌PI
 */
public interface HealthApi {
    
    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();
    
    // 鍋ュ悍妫€鏌?    Result<Map<String, Object>> checkHealth(String serviceId);
    Result<List<Map<String, Object>>> checkAllServices();
    Result<Map<String, Object>> getHealthStatus();
}
