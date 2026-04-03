package net.ooder.skill.scenes.service.impl;

import net.ooder.skill.scenes.dto.SceneDTO;
import net.ooder.skill.scenes.dto.SceneCapabilityDTO;
import net.ooder.skill.scenes.model.PageResult;
import net.ooder.skill.scenes.service.SceneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SceneServiceImpl implements SceneService {

    private static final Logger log = LoggerFactory.getLogger(SceneServiceImpl.class);

    private final Map<String, SceneDTO> sceneStore = new ConcurrentHashMap<>();
    private final Map<String, List<SceneCapabilityDTO>> sceneCapabilities = new ConcurrentHashMap<>();
    private final Map<String, List<String>> sceneCollaborators = new ConcurrentHashMap<>();
    private final Map<String, List<Map<String, Object>>> sceneSnapshots = new ConcurrentHashMap<>();
    private Long sceneIdCounter = 1L;

    @PostConstruct
    public void init() {
        log.info("[SceneService] Initializing with default scenes...");

        SceneDTO defaultScene = new SceneDTO();
        defaultScene.setSceneId("scene-default");
        defaultScene.setName("默认场景");
        defaultScene.setDescription("系统默认场景");
        defaultScene.setType("default");
        defaultScene.setStatus("active");
        defaultScene.setOwnerId("system");
        defaultScene.setOwnerName("System");
        defaultScene.setCapabilityIds(new ArrayList<>());
        defaultScene.setCollaborativeUserIds(new ArrayList<>());
        defaultScene.setCreatedAt(System.currentTimeMillis());
        defaultScene.setUpdatedAt(System.currentTimeMillis());
        defaultScene.setActivatedAt(System.currentTimeMillis());
        sceneStore.put(defaultScene.getSceneId(), defaultScene);

        log.info("[SceneService] Initialized {} scenes", sceneStore.size());
    }

    @Override
    public PageResult<SceneDTO> listScenes(String status, int pageNum, int pageSize) {
        List<SceneDTO> filtered = sceneStore.values().stream()
            .filter(s -> status == null || status.isEmpty() || status.equals(s.getStatus()))
            .sorted((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()))
            .collect(Collectors.toList());

        int total = filtered.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        PageResult<SceneDTO> result = new PageResult<>();
        result.setList(start < total ? filtered.subList(start, end) : new ArrayList<>());
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public SceneDTO get(String sceneId) {
        return sceneStore.get(sceneId);
    }

    @Override
    public SceneDTO create(SceneDTO scene) {
        String id = "scene-" + sceneIdCounter++;
        scene.setSceneId(id);
        scene.setStatus("inactive");
        scene.setCreatedAt(System.currentTimeMillis());
        scene.setUpdatedAt(System.currentTimeMillis());
        sceneStore.put(id, scene);
        sceneCapabilities.put(id, new ArrayList<>());
        sceneCollaborators.put(id, new ArrayList<>());
        log.info("[SceneService] Created scene: {} ({})", scene.getName(), id);
        return scene;
    }

    @Override
    public SceneDTO update(String sceneId, SceneDTO scene) {
        SceneDTO existing = sceneStore.get(sceneId);
        if (existing == null) return null;
        scene.setSceneId(sceneId);
        scene.setCreatedAt(existing.getCreatedAt());
        scene.setUpdatedAt(System.currentTimeMillis());
        sceneStore.put(sceneId, scene);
        log.info("[SceneService] Updated scene: {}", sceneId);
        return scene;
    }

    @Override
    public boolean delete(String sceneId) {
        if ("scene-default".equals(sceneId)) return false;
        sceneStore.remove(sceneId);
        sceneCapabilities.remove(sceneId);
        sceneCollaborators.remove(sceneId);
        sceneSnapshots.remove(sceneId);
        log.info("[SceneService] Deleted scene: {}", sceneId);
        return true;
    }

    @Override
    public SceneDTO activate(String sceneId) {
        SceneDTO scene = sceneStore.get(sceneId);
        if (scene == null) return null;
        scene.setStatus("active");
        scene.setActivatedAt(System.currentTimeMillis());
        scene.setUpdatedAt(System.currentTimeMillis());
        log.info("[SceneService] Activated scene: {}", sceneId);
        return scene;
    }

    @Override
    public SceneDTO deactivate(String sceneId) {
        SceneDTO scene = sceneStore.get(sceneId);
        if (scene == null) return null;
        scene.setStatus("inactive");
        scene.setUpdatedAt(System.currentTimeMillis());
        log.info("[SceneService] Deactivated scene: {}", sceneId);
        return scene;
    }

    @Override
    public List<SceneCapabilityDTO> listCapabilities(String sceneId) {
        return sceneCapabilities.getOrDefault(sceneId, new ArrayList<>());
    }

    @Override
    public SceneCapabilityDTO addCapability(String sceneId, String capId) {
        List<SceneCapabilityDTO> caps = sceneCapabilities.computeIfAbsent(sceneId, k -> new ArrayList<>());
        SceneCapabilityDTO cap = new SceneCapabilityDTO();
        cap.setCapId(capId);
        cap.setSceneId(sceneId);
        cap.setStatus("active");
        cap.setPriority(caps.size());
        cap.setAddedAt(System.currentTimeMillis());
        caps.add(cap);
        log.info("[SceneService] Added capability {} to scene {}", capId, sceneId);
        return cap;
    }

    @Override
    public boolean removeCapability(String sceneId, String capId) {
        List<SceneCapabilityDTO> caps = sceneCapabilities.get(sceneId);
        if (caps == null) return false;
        boolean removed = caps.removeIf(c -> capId.equals(c.getCapId()));
        if (removed) {
            log.info("[SceneService] Removed capability {} from scene {}", capId, sceneId);
        }
        return removed;
    }

    @Override
    public List<String> listCollaborativeUsers(String sceneId) {
        return sceneCollaborators.getOrDefault(sceneId, new ArrayList<>());
    }

    @Override
    public boolean addCollaborativeUser(String sceneId, String userId) {
        List<String> users = sceneCollaborators.computeIfAbsent(sceneId, k -> new ArrayList<>());
        if (!users.contains(userId)) {
            users.add(userId);
            log.info("[SceneService] Added collaborative user {} to scene {}", userId, sceneId);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeCollaborativeUser(String sceneId, String userId) {
        List<String> users = sceneCollaborators.get(sceneId);
        if (users != null) {
            boolean removed = users.remove(userId);
            if (removed) {
                log.info("[SceneService] Removed collaborative user {} from scene {}", userId, sceneId);
            }
            return removed;
        }
        return false;
    }

    @Override
    public String createSnapshot(String sceneId) {
        String snapshotId = "snapshot-" + System.currentTimeMillis();
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("snapshotId", snapshotId);
        snapshot.put("sceneId", sceneId);
        snapshot.put("createdAt", System.currentTimeMillis());
        snapshot.put("data", sceneStore.get(sceneId));
        sceneSnapshots.computeIfAbsent(sceneId, k -> new ArrayList<>()).add(snapshot);
        log.info("[SceneService] Created snapshot {} for scene {}", snapshotId, sceneId);
        return snapshotId;
    }

    @Override
    public List<Map<String, Object>> listSnapshots(String sceneId, int pageNum, int pageSize) {
        List<Map<String, Object>> snapshots = sceneSnapshots.getOrDefault(sceneId, new ArrayList<>());
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, snapshots.size());
        if (start >= snapshots.size()) return new ArrayList<>();
        return new ArrayList<>(snapshots.subList(start, end));
    }

    @Override
    public boolean restoreSnapshot(String sceneId, String snapshotId) {
        List<Map<String, Object>> snapshots = sceneSnapshots.get(sceneId);
        if (snapshots == null) return false;
        for (Map<String, Object> snap : snapshots) {
            if (snapshotId.equals(snap.get("snapshotId"))) {
                SceneDTO data = (SceneDTO) snap.get("data");
                if (data != null) {
                    data.setSceneId(sceneId);
                    data.setUpdatedAt(System.currentTimeMillis());
                    sceneStore.put(sceneId, data);
                    log.info("[SceneService] Restored snapshot {} for scene {}", snapshotId, sceneId);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> getLogs(String sceneId, int pageNum, int pageSize) {
        List<Map<String, Object>> logs = new ArrayList<>();
        Map<String, Object> logEntry = new LinkedHashMap<>();
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("sceneId", sceneId);
        logEntry.put("action", "log_entry");
        logEntry.put("message", "Scene log entry");
        logs.add(logEntry);
        return logs;
    }
}