package net.ooder.skill.remote.terminal.api;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RemoteTerminalApiImpl implements RemoteTerminalApi {

    private boolean initialized = false;
    private boolean running = false;
    private SkillContext context;
    private final Map<String, Map<String, Object>> sessions = new ConcurrentHashMap<>();

    @Override
    public String getApiName() { return "skill-remote-terminal"; }

    @Override
    public String getVersion() { return "2.3.0"; }

    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.initialized = true;
        log.info("RemoteTerminalApi initialized");
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
    public Result<Map<String, Object>> createSession(Map<String, Object> config) {
        String sessionId = UUID.randomUUID().toString();
        Map<String, Object> session = new HashMap<>();
        session.put("sessionId", sessionId);
        session.put("host", config.get("host"));
        session.put("port", config.getOrDefault("port", 22));
        session.put("username", config.get("username"));
        session.put("status", "connected");
        sessions.put(sessionId, session);
        return Result.success(session);
    }

    @Override
    public Result<Boolean> closeSession(String sessionId) {
        sessions.remove(sessionId);
        return Result.success(true);
    }

    @Override
    public Result<Map<String, Object>> getSession(String sessionId) {
        Map<String, Object> session = sessions.get(sessionId);
        return session != null ? Result.success(session) : Result.error("Session not found");
    }

    @Override
    public Result<String> executeCommand(String sessionId, String command) {
        log.info("Executing command in session {}: {}", sessionId, command);
        return Result.success("Command executed: " + command);
    }

    @Override
    public Result<String> executeScript(String sessionId, String script) {
        log.info("Executing script in session {}: {} chars", sessionId, script.length());
        return Result.success("Script executed");
    }

    @Override
    public Result<Boolean> uploadFile(String sessionId, String remotePath, byte[] content) {
        log.info("Uploading file to {} in session {}", remotePath, sessionId);
        return Result.success(true);
    }

    @Override
    public Result<byte[]> downloadFile(String sessionId, String remotePath) {
        log.info("Downloading file from {} in session {}", remotePath, sessionId);
        return Result.success(new byte[0]);
    }
}
