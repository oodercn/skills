package net.ooder.skill.network.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 缃戠粶绠＄悊API
 */
public interface NetworkApi {
    
    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();
    
    // 缃戠粶璁惧绠＄悊
    Result<Map<String, Object>> getNetworkInfo();
    Result<List<Map<String, Object>>> listInterfaces();
    Result<Map<String, Object>> getInterfaceStatus(String interfaceName);
    Result<Boolean> configureInterface(String interfaceName, Map<String, Object> config);
    
    // 缃戠粶璇婃柇
    Result<Map<String, Object>> ping(String host);
    Result<Map<String, Object>> traceroute(String host);
    Result<Map<String, Object>> nslookup(String domain);
}
