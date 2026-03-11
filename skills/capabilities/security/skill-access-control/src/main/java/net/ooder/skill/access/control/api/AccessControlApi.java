package net.ooder.skill.access.control.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 璁块棶鎺у埗API
 */
public interface AccessControlApi {

    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    // 鏉冮檺绠＄悊
    Result<Map<String, Object>> grantPermission(String userId, String resource, String action);
    Result<Boolean> revokePermission(String userId, String resource, String action);
    Result<List<String>> listUserPermissions(String userId);
    Result<Boolean> checkAccess(String userId, String resource, String action);

    // 瑙掕壊绠＄悊
    Result<Map<String, Object>> createRole(Map<String, Object> role);
    Result<Boolean> deleteRole(String roleId);
    Result<Boolean> assignRole(String userId, String roleId);
    Result<Boolean> removeRole(String userId, String roleId);
}
