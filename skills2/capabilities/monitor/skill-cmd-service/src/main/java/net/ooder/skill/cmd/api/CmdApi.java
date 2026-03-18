package net.ooder.skill.cmd.api;

import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;

import java.util.List;
import java.util.Map;

/**
 * 鍛戒护鏈嶅姟API
 */
public interface CmdApi {

    String getApiName();
    String getVersion();
    void initialize(SkillContext context);
    void start();
    void stop();
    boolean isInitialized();
    boolean isRunning();

    // 鍛戒护鎵ц
    Result<Map<String, Object>> execute(String command);
    Result<Map<String, Object>> executeWithParams(String command, Map<String, Object> params);

    // 鍛戒护绠＄悊
    Result<List<String>> listCommands();
    Result<Map<String, Object>> getCommandInfo(String command);

    // 鎵瑰鐞?    Result<List<Map<String, Object>>> executeBatch(List<String> commands);
}
