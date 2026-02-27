package net.ooder.skill.audit.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 瀹¤鏃ュ織API
 */
public interface AuditApi {

    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    // 鏃ュ織璁板綍
    Result<Boolean> logEvent(Map<String, Object> event);
    Result<Map<String, Object>> getLog(String logId);

    // 鏃ュ織鏌ヨ
    Result<List<Map<String, Object>>> queryLogs(Map<String, Object> query);
    Result<Long> countLogs(Map<String, Object> query);

    // 缁熻鍒嗘瀽
    Result<Map<String, Object>> getStatistics();
    Result<List<Map<String, Object>>> getTopEvents(int limit);
}
