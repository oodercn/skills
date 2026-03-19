package net.ooder.skill.monitor.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 鐩戞帶鏈嶅姟API
 */
public interface MonitorApi {
    
    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();
    
    // 鐩戞帶鏁版嵁
    Result<Map<String, Object>> getMetrics(String serviceId);
    Result<List<Map<String, Object>>> getAllMetrics();
    Result<Map<String, Object>> getAlerts();
}
