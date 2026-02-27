package net.ooder.skill.audit.api;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuditApiImpl implements AuditApi {

    private boolean initialized = false;
    private boolean running = false;
    private SkillContext context;
    private final Map<String, Map<String, Object>> logs = new ConcurrentHashMap<>();

    @Override
    public String getApiName() { return "skill-audit"; }

    @Override
    public String getVersion() { return "2.3.0"; }

    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.initialized = true;
        log.info("AuditApi initialized");
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
    public Result<Boolean> logEvent(Map<String, Object> event) {
        String logId = UUID.randomUUID().toString();
        event.put("logId", logId);
        event.put("timestamp", System.currentTimeMillis());
        logs.put(logId, event);
        return Result.success(true);
    }

    @Override
    public Result<Map<String, Object>> getLog(String logId) {
        Map<String, Object> log = logs.get(logId);
        return log != null ? Result.success(log) : Result.error("Log not found");
    }

    @Override
    public Result<List<Map<String, Object>>> queryLogs(Map<String, Object> query) {
        List<Map<String, Object>> result = new ArrayList<>(logs.values());
        return Result.success(result);
    }

    @Override
    public Result<Long> countLogs(Map<String, Object> query) {
        return Result.success((long) logs.size());
    }

    @Override
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLogs", logs.size());
        stats.put("last24h", logs.values().stream()
                .filter(l -> System.currentTimeMillis() - (Long) l.get("timestamp") < 86400000)
                .count());
        return Result.success(stats);
    }

    @Override
    public Result<List<Map<String, Object>>> getTopEvents(int limit) {
        return Result.success(logs.values().stream().limit(limit).collect(Collectors.toList()));
    }
}
