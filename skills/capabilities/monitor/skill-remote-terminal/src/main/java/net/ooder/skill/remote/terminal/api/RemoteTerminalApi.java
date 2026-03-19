package net.ooder.skill.remote.terminal.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.Map;

/**
 * 杩滅▼缁堢API
 */
public interface RemoteTerminalApi {

    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    // 浼氳瘽绠＄悊
    Result<Map<String, Object>> createSession(Map<String, Object> config);
    Result<Boolean> closeSession(String sessionId);
    Result<Map<String, Object>> getSession(String sessionId);

    // 鍛戒护鎵ц
    Result<String> executeCommand(String sessionId, String command);
    Result<String> executeScript(String sessionId, String script);

    // 鏂囦欢浼犺緭
    Result<Boolean> uploadFile(String sessionId, String remotePath, byte[] content);
    Result<byte[]> downloadFile(String sessionId, String remotePath);
}
