package net.ooder.skill.protocol.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 鍗忚绠＄悊API
 */
public interface ProtocolApi {
    
    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();
    
    // 鍗忚澶勭悊
    Result<Map<String, Object>> executeCommand(String protocol, String command, Map<String, Object> params);
    Result<List<String>> listSupportedProtocols();
    Result<Boolean> registerHandler(String protocol, Map<String, Object> config);
}
