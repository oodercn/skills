package net.ooder.skill.protocol.api;

import net.ooder.skill.common.Result;
import net.ooder.skill.common.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 协议管理API
 */
public interface ProtocolApi {
    
    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();
    
    Result<Map<String, Object>> executeCommand(String protocol, String command, Map<String, Object> params);
    Result<List<String>> listSupportedProtocols();
    Result<Boolean> registerHandler(String protocol, Map<String, Object> config);
}
