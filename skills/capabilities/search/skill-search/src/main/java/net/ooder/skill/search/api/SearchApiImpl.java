package net.ooder.skill.search.api;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.infra.utils.Result;
import net.ooder.sdk.api.scene.SkillContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SearchApiImpl implements SearchApi {

    private boolean initialized = false;
    private boolean running = false;
    private SkillContext context;
    private final Map<String, Map<String, Map<String, Object>>> indices = new ConcurrentHashMap<>();

    @Override
    public String getApiName() { return "skill-search"; }

    @Override
    public String getVersion() { return "2.3.0"; }

    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.initialized = true;
        log.info("SearchApi initialized");
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
    public Result<List<Map<String, Object>>> search(String index, String query) {
        Map<String, Map<String, Object>> idx = indices.getOrDefault(index, new HashMap<>());
        List<Map<String, Object>> results = idx.values().stream()
                .filter(doc -> doc.toString().contains(query))
                .collect(Collectors.toList());
        return Result.success(results);
    }

    @Override
    public Result<List<Map<String, Object>>> searchWithFilters(String index, Map<String, Object> filters) {
        Map<String, Map<String, Object>> idx = indices.getOrDefault(index, new HashMap<>());
        return Result.success(new ArrayList<>(idx.values()));
    }

    @Override
    public Result<Boolean> createIndex(String index, Map<String, Object> settings) {
        indices.putIfAbsent(index, new ConcurrentHashMap<>());
        return Result.success(true);
    }

    @Override
    public Result<Boolean> deleteIndex(String index) {
        indices.remove(index);
        return Result.success(true);
    }

    @Override
    public Result<Boolean> indexDocument(String index, String id, Map<String, Object> document) {
        indices.computeIfAbsent(index, k -> new ConcurrentHashMap<>()).put(id, document);
        return Result.success(true);
    }

    @Override
    public Result<Map<String, Object>> aggregate(String index, Map<String, Object> aggregation) {
        Map<String, Object> result = new HashMap<>();
        result.put("count", indices.getOrDefault(index, new HashMap<>()).size());
        return Result.success(result);
    }
}
