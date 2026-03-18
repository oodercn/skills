package net.ooder.skill.cmd.api;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CmdApiImpl implements CmdApi {

    private boolean initialized = false;
    private boolean running = false;
    private SkillContext context;
    private final Map<String, String> commands = new HashMap<>();

    @Override
    public String getApiName() { return "skill-cmd-service"; }

    @Override
    public String getVersion() { return "2.3.0"; }

    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.initialized = true;
        commands.put("status", "Get system status");
        commands.put("restart", "Restart service");
        commands.put("config", "Show configuration");
        log.info("CmdApi initialized");
    }

    @Override
    public void start() { this.running = true; }

    @Override
    public void stop() { this.running = false; }

    @Override
    public boolean isInitialized() { return initialized; }

    @Override
    public boolean isRunning() { return running; }

    @Override
    public Result<Map<String, Object>> execute(String command) {
        Map<String, Object> result = new HashMap<>();
        result.put("command", command);
        result.put("status", "success");
        result.put("output", "Executed: " + command);
        result.put("timestamp", System.currentTimeMillis());
        return Result.success(result);
    }

    @Override
    public Result<Map<String, Object>> executeWithParams(String command, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("command", command);
        result.put("params", params);
        result.put("status", "success");
        result.put("output", "Executed: " + command + " with params");
        return Result.success(result);
    }

    @Override
    public Result<List<String>> listCommands() {
        return Result.success(new ArrayList<>(commands.keySet()));
    }

    @Override
    public Result<Map<String, Object>> getCommandInfo(String command) {
        Map<String, Object> info = new HashMap<>();
        info.put("name", command);
        info.put("description", commands.getOrDefault(command, "Unknown command"));
        return Result.success(info);
    }

    @Override
    public Result<List<Map<String, Object>>> executeBatch(List<String> commands) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (String cmd : commands) {
            results.add(execute(cmd).getData());
        }
        return Result.success(results);
    }
}
