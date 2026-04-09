package net.ooder.skill.agent.repository;

import com.alibaba.fastjson2.JSON;
import net.ooder.skill.agent.dto.SceneChatContextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FileChatContextRepository implements ChatContextRepository {

    private static final Logger log = LoggerFactory.getLogger(FileChatContextRepository.class);

    private final Map<String, SceneChatContextDTO> memoryCache = new ConcurrentHashMap<>();
    private final Path storageDir;

    public FileChatContextRepository() {
        String dataDir = System.getProperty("agent.data.dir",
            System.getProperty("java.io.tmpdir") + File.separator + "agent-chat-context");
        this.storageDir = Paths.get(dataDir);
        try {
            Files.createDirectories(storageDir);
            log.info("[FileChatContextRepository] Storage dir: {}", storageDir);
        } catch (IOException e) {
            log.warn("[FileChatContextRepository] Failed to create storage dir: {}", e.getMessage());
        }
    }

    @Override
    public SceneChatContextDTO save(SceneChatContextDTO context) {
        if (context == null || context.getSceneGroupId() == null) {
            return context;
        }
        memoryCache.put(context.getSceneGroupId(), context);
        persistToFile(context);
        return context;
    }

    @Override
    public Optional<SceneChatContextDTO> findBySceneGroupId(String sceneGroupId) {
        SceneChatContextDTO cached = memoryCache.get(sceneGroupId);
        if (cached != null) {
            return Optional.of(cached);
        }
        SceneChatContextDTO loaded = loadFromFile(sceneGroupId);
        if (loaded != null) {
            memoryCache.put(sceneGroupId, loaded);
            return Optional.of(loaded);
        }
        return Optional.empty();
    }

    @Override
    public Optional<SceneChatContextDTO> findBySessionId(String sessionId) {
        return memoryCache.values().stream()
            .filter(c -> sessionId.equals(c.getSceneId()))
            .findFirst();
    }

    @Override
    public void deleteBySceneGroupId(String sceneGroupId) {
        memoryCache.remove(sceneGroupId);
        Path filePath = storageDir.resolve(sceneGroupId + ".json");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("[deleteBySceneGroupId] Failed to delete file: {}", e.getMessage());
        }
    }

    @Override
    public long count() {
        return memoryCache.size();
    }

    private void persistToFile(SceneChatContextDTO context) {
        try {
            Path filePath = storageDir.resolve(context.getSceneGroupId() + ".json");
            String json = JSON.toJSONString(context);
            Files.writeString(filePath, json);
            log.debug("[persistToFile] Saved context for: {}", context.getSceneGroupId());
        } catch (IOException e) {
            log.warn("[persistToFile] Failed to persist context: {}", e.getMessage());
        }
    }

    private SceneChatContextDTO loadFromFile(String sceneGroupId) {
        try {
            Path filePath = storageDir.resolve(sceneGroupId + ".json");
            if (Files.exists(filePath)) {
                String json = Files.readString(filePath);
                SceneChatContextDTO context = JSON.parseObject(json, SceneChatContextDTO.class);
                log.debug("[loadFromFile] Loaded context for: {}", sceneGroupId);
                return context;
            }
        } catch (IOException e) {
            log.warn("[loadFromFile] Failed to load context: {}", e.getMessage());
        }
        return null;
    }
}
