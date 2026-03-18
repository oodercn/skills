package net.ooder.skill.openwrt.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * OpenWrt璺敱鍣ㄧ鐞咥PI
 */
public interface OpenWrtApi {
    
    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();
    
    // 璺敱鍣ㄧ鐞?    Result<Map<String, Object>> getRouterInfo();
    Result<Map<String, Object>> getSystemStatus();
    Result<List<Map<String, Object>>> listConnectedDevices();
    
    // 閰嶇疆绠＄悊
    Result<Boolean> applyConfig(Map<String, Object> config);
    Result<Map<String, Object>> getConfig();
    
    // 缃戠粶绠＄悊
    Result<Boolean> setWifiConfig(Map<String, Object> wifiConfig);
    Result<Map<String, Object>> getWifiStatus();
    Result<Boolean> reboot();
}
