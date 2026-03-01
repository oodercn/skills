package net.ooder.skill.scene.snapshot;

import net.ooder.skill.scene.dto.scene.SceneSnapshotDTO;
import net.ooder.skill.scene.dto.scene.SceneGroupDTO;
import net.ooder.skill.scene.dto.scene.SceneParticipantDTO;
import net.ooder.skill.scene.dto.scene.CapabilityBindingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SnapshotService {

    private static final Logger log = LoggerFactory.getLogger(SnapshotService.class);

    @Value("${app.snapshot.path:./data/snapshots}")
    private String snapshotPath;

    @Value("${app.snapshot.max-count:100}")
    private int maxSnapshotCount;

    private final ObjectMapper objectMapper;
    private final Map<String, List<SceneSnapshotDTO>> snapshotCache = new ConcurrentHashMap<>();

    public SnapshotService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void init() {
        Path path = Paths.get(snapshotPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                log.info("Created snapshot directory: {}", snapshotPath);
            } catch (IOException e) {
                log.error("Failed to create snapshot directory", e);
            }
        }
        loadAllSnapshots();
    }

    private void loadAllSnapshots() {
        File snapshotDir = new File(snapshotPath);
        File[] files = snapshotDir.listFiles((dir, name) -> name.endsWith(".snapshot"));
        
        if (files != null) {
            for (File file : files) {
                try {
                    SceneSnapshotDTO snapshot = loadSnapshotFromFile(file);
                    if (snapshot != null) {
                        String sceneGroupId = snapshot.getSceneGroupId();
                        snapshotCache.computeIfAbsent(sceneGroupId, k -> new ArrayList<>()).add(snapshot);
                    }
                } catch (Exception e) {
                    log.warn("Failed to load snapshot file: {}", file.getName());
                }
            }
        }
        
        log.info("Loaded {} snapshots from storage", 
            snapshotCache.values().stream().mapToInt(List::size).sum());
    }

    public SceneSnapshotDTO createSnapshot(String sceneGroupId, SceneGroupDTO group,
            List<SceneParticipantDTO> participants, List<CapabilityBindingDTO> bindings) {
        
        SceneSnapshotDTO snapshot = new SceneSnapshotDTO();
        snapshot.setSnapshotId("snap-" + sceneGroupId + "-" + System.currentTimeMillis());
        snapshot.setSceneGroupId(sceneGroupId);
        snapshot.setCreateTime(System.currentTimeMillis());
        snapshot.setStatus("valid");
        
        Map<String, Object> state = new HashMap<>();
        state.put("group", group);
        state.put("participants", participants);
        state.put("capabilityBindings", bindings);
        state.put("timestamp", System.currentTimeMillis());
        snapshot.setState(state);
        
        snapshotCache.computeIfAbsent(sceneGroupId, k -> new ArrayList<>()).add(snapshot);
        
        saveSnapshotToFile(snapshot);
        
        cleanupOldSnapshots(sceneGroupId);
        
        log.info("Created snapshot {} for scene group {}", snapshot.getSnapshotId(), sceneGroupId);
        return snapshot;
    }

    public SceneSnapshotDTO getSnapshot(String snapshotId) {
        for (List<SceneSnapshotDTO> snapshots : snapshotCache.values()) {
            for (SceneSnapshotDTO snapshot : snapshots) {
                if (snapshotId.equals(snapshot.getSnapshotId())) {
                    return snapshot;
                }
            }
        }
        return null;
    }

    public List<SceneSnapshotDTO> getSnapshotsForGroup(String sceneGroupId) {
        return new ArrayList<>(snapshotCache.getOrDefault(sceneGroupId, new ArrayList<>()));
    }

    public boolean restoreSnapshot(String snapshotId, String sceneGroupId) {
        SceneSnapshotDTO snapshot = getSnapshot(snapshotId);
        
        if (snapshot == null || !sceneGroupId.equals(snapshot.getSceneGroupId())) {
            log.warn("Snapshot {} not found or does not belong to group {}", snapshotId, sceneGroupId);
            return false;
        }
        
        log.info("Restoring snapshot {} for scene group {}", snapshotId, sceneGroupId);
        return true;
    }

    public boolean deleteSnapshot(String snapshotId) {
        for (Map.Entry<String, List<SceneSnapshotDTO>> entry : snapshotCache.entrySet()) {
                Iterator<SceneSnapshotDTO> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    SceneSnapshotDTO snapshot = iterator.next();
                    if (snapshotId.equals(snapshot.getSnapshotId())) {
                        iterator.remove();
                        deleteSnapshotFile(snapshot);
                        log.info("Deleted snapshot {}", snapshotId);
                        return true;
                    }
                }
            }
        return false;
    }

    private void saveSnapshotToFile(SceneSnapshotDTO snapshot) {
        try {
            File file = new File(snapshotPath, snapshot.getSnapshotId() + ".snapshot");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, snapshot);
            log.debug("Saved snapshot to file: {}", file.getName());
        } catch (IOException e) {
            log.error("Failed to save snapshot to file", e);
        }
    }

    private SceneSnapshotDTO loadSnapshotFromFile(File file) {
        try {
            return objectMapper.readValue(file, SceneSnapshotDTO.class);
        } catch (IOException e) {
            log.error("Failed to load snapshot from file: {}", file.getName(), e);
            return null;
        }
    }

    private void deleteSnapshotFile(SceneSnapshotDTO snapshot) {
        File file = new File(snapshotPath, snapshot.getSnapshotId() + ".snapshot");
        if (file.exists()) {
                file.delete();
            }
    }

    private void cleanupOldSnapshots(String sceneGroupId) {
        List<SceneSnapshotDTO> snapshots = snapshotCache.get(sceneGroupId);
        if (snapshots != null && snapshots.size() > maxSnapshotCount) {
                snapshots.sort(Comparator.comparingLong(SceneSnapshotDTO::getCreateTime));
            
                while (snapshots.size() > maxSnapshotCount) {
                    SceneSnapshotDTO oldest = snapshots.remove(0);
                    deleteSnapshotFile(oldest);
                    log.info("Removed old snapshot: {}", oldest.getSnapshotId());
                }
            }
    }

    public byte[] exportSnapshot(String snapshotId) {
        SceneSnapshotDTO snapshot = getSnapshot(snapshotId);
        if (snapshot == null) {
                return null;
            }
        
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(snapshot);
        } catch (IOException e) {
            log.error("Failed to export snapshot", e);
            return null;
        }
    }

    public boolean importSnapshot(byte[] data) {
        try {
            SceneSnapshotDTO snapshot = objectMapper.readValue(data, SceneSnapshotDTO.class);
            String sceneGroupId = snapshot.getSceneGroupId();
            
            snapshotCache.computeIfAbsent(sceneGroupId, k -> new ArrayList<>()).add(snapshot);
            saveSnapshotToFile(snapshot);
            
            log.info("Imported snapshot {} for group {}", snapshot.getSnapshotId(), sceneGroupId);
            return true;
        } catch (IOException e) {
            log.error("Failed to import snapshot", e);
            return false;
        }
    }

    public Map<String, Object> getSnapshotStats(String sceneGroupId) {
        Map<String, Object> stats = new HashMap<>();
        List<SceneSnapshotDTO> snapshots = snapshotCache.getOrDefault(sceneGroupId, new ArrayList<>());
        
        stats.put("totalSnapshots", snapshots.size());
        stats.put("maxSnapshots", maxSnapshotCount);
        
        if (!snapshots.isEmpty()) {
            stats.put("oldestSnapshot", snapshots.stream()
                .mapToLong(SceneSnapshotDTO::getCreateTime)
                .min()
                .orElse(0L));
            stats.put("newestSnapshot", snapshots.stream()
                .mapToLong(SceneSnapshotDTO::getCreateTime)
                .max()
                .orElse(0L));
        }
        
        return stats;
    }
}
