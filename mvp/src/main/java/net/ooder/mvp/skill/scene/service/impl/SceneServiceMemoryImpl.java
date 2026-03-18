package net.ooder.mvp.skill.scene.service.impl;

import net.ooder.mvp.skill.scene.dto.PageResult;
import net.ooder.mvp.skill.scene.dto.SceneDefinitionDTO;
import net.ooder.mvp.skill.scene.dto.SceneStateDTO;
import net.ooder.mvp.skill.scene.dto.discovery.CapabilityDTO;
import net.ooder.mvp.skill.scene.dto.scene.SceneSnapshotDTO;
import net.ooder.mvp.skill.scene.controller.SceneController.LogDTO;
import net.ooder.mvp.skill.scene.service.SceneService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SceneServiceMemoryImpl implements SceneService {

    private final Map<String, SceneDefinitionDTO> scenes = new ConcurrentHashMap<>();
    private final Map<String, List<CapabilityDTO>> capabilities = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> collaborativeScenes = new ConcurrentHashMap<>();
    private final Map<String, List<SceneSnapshotDTO>> snapshots = new ConcurrentHashMap<>();

    public SceneServiceMemoryImpl() {
        initDefaultScenes();
    }

    private void initDefaultScenes() {
        SceneDefinitionDTO scene1 = new SceneDefinitionDTO();
        scene1.setSceneId("scene-data-processing");
        scene1.setName("数据处理场景");
        scene1.setDescription("用于数据采集、处理和分析的场景");
        scene1.setType("primary");
        scene1.setVersion("1.0.0");
        scene1.setCreateTime(System.currentTimeMillis());
        scene1.setActive(true);
        scenes.put(scene1.getSceneId(), scene1);

        SceneDefinitionDTO scene2 = new SceneDefinitionDTO();
        scene2.setSceneId("scene-api-integration");
        scene2.setName("API集成场景");
        scene2.setDescription("用于外部API集成的场景");
        scene2.setType("primary");
        scene2.setVersion("1.0.0");
        scene2.setCreateTime(System.currentTimeMillis());
        scene2.setActive(false);
        scenes.put(scene2.getSceneId(), scene2);

        SceneDefinitionDTO scene3 = new SceneDefinitionDTO();
        scene3.setSceneId("scene-collaboration");
        scene3.setName("协作场景");
        scene3.setDescription("多技能协作场景");
        scene3.setType("collaborative");
        scene3.setVersion("1.0.0");
        scene3.setCreateTime(System.currentTimeMillis());
        scene3.setActive(true);
        scenes.put(scene3.getSceneId(), scene3);
    }

    @Override
    public SceneDefinitionDTO create(SceneDefinitionDTO definition) {
        if (definition.getSceneId() == null || definition.getSceneId().isEmpty()) {
            definition.setSceneId("scene-" + System.currentTimeMillis());
        }
        definition.setCreateTime(System.currentTimeMillis());
        definition.setActive(false);
        scenes.put(definition.getSceneId(), definition);
        return definition;
    }

    @Override
    public boolean delete(String sceneId) {
        return scenes.remove(sceneId) != null;
    }

    @Override
    public SceneDefinitionDTO get(String sceneId) {
        return scenes.get(sceneId);
    }

    @Override
    public PageResult<SceneDefinitionDTO> listAll(int pageNum, int pageSize) {
        List<SceneDefinitionDTO> allScenes = new ArrayList<>(scenes.values());
        
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, allScenes.size());
        
        List<SceneDefinitionDTO> pagedScenes = start < allScenes.size() 
            ? allScenes.subList(start, end) 
            : new ArrayList<>();
        
        PageResult<SceneDefinitionDTO> result = new PageResult<>();
        result.setList(pagedScenes);
        result.setTotal(allScenes.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public boolean activate(String sceneId) {
        SceneDefinitionDTO scene = scenes.get(sceneId);
        if (scene != null) {
            scene.setActive(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean deactivate(String sceneId) {
        SceneDefinitionDTO scene = scenes.get(sceneId);
        if (scene != null) {
            scene.setActive(false);
            return true;
        }
        return false;
    }

    @Override
    public SceneStateDTO getState(String sceneId) {
        SceneDefinitionDTO scene = scenes.get(sceneId);
        if (scene != null) {
            SceneStateDTO state = new SceneStateDTO();
            state.setSceneId(sceneId);
            state.setActive(scene.isActive());
            state.setStatus(scene.isActive() ? "active" : "inactive");
            return state;
        }
        return null;
    }

    @Override
    public boolean addCapability(String sceneId, CapabilityDTO capability) {
        List<CapabilityDTO> caps = capabilities.computeIfAbsent(sceneId, k -> new ArrayList<>());
        caps.add(capability);
        return true;
    }

    @Override
    public boolean removeCapability(String sceneId, String capId) {
        List<CapabilityDTO> caps = capabilities.get(sceneId);
        if (caps != null) {
            return caps.removeIf(cap -> capId.equals(cap.getCapId()));
        }
        return false;
    }

    @Override
    public PageResult<CapabilityDTO> listCapabilities(String sceneId, int pageNum, int pageSize) {
        List<CapabilityDTO> caps = capabilities.getOrDefault(sceneId, new ArrayList<>());
        
        PageResult<CapabilityDTO> result = new PageResult<>();
        result.setList(caps);
        result.setTotal(caps.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public CapabilityDTO getCapability(String sceneId, String capId) {
        List<CapabilityDTO> caps = capabilities.get(sceneId);
        if (caps != null) {
            for (CapabilityDTO cap : caps) {
                if (capId.equals(cap.getCapId())) {
                    return cap;
                }
            }
        }
        return null;
    }

    @Override
    public boolean addCollaborativeScene(String sceneId, String collaborativeSceneId) {
        Set<String> collab = collaborativeScenes.computeIfAbsent(sceneId, k -> new HashSet<>());
        return collab.add(collaborativeSceneId);
    }

    @Override
    public boolean removeCollaborativeScene(String sceneId, String collaborativeSceneId) {
        Set<String> collab = collaborativeScenes.get(sceneId);
        if (collab != null) {
            return collab.remove(collaborativeSceneId);
        }
        return false;
    }

    @Override
    public PageResult<String> listCollaborativeScenes(String sceneId, int pageNum, int pageSize) {
        Set<String> collab = collaborativeScenes.getOrDefault(sceneId, new HashSet<>());
        List<String> list = new ArrayList<>(collab);
        
        PageResult<String> result = new PageResult<>();
        result.setList(list);
        result.setTotal(list.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public SceneSnapshotDTO createSnapshot(String sceneId) {
        SceneDefinitionDTO scene = scenes.get(sceneId);
        if (scene == null) {
            return null;
        }
        
        SceneSnapshotDTO snapshot = new SceneSnapshotDTO();
        snapshot.setSnapshotId("snap-" + System.currentTimeMillis());
        snapshot.setSceneGroupId(sceneId);
        snapshot.setCreateTime(System.currentTimeMillis());
        snapshot.setStatus("valid");
        
        List<SceneSnapshotDTO> snapList = snapshots.computeIfAbsent(sceneId, k -> new ArrayList<>());
        snapList.add(snapshot);
        
        return snapshot;
    }

    @Override
    public boolean restoreSnapshot(String sceneId, SceneSnapshotDTO snapshot) {
        return snapshot != null && snapshot.getSceneGroupId().equals(sceneId);
    }

    @Override
    public PageResult<SceneSnapshotDTO> listSnapshots(String sceneId, int pageNum, int pageSize) {
        List<SceneSnapshotDTO> snapList = snapshots.getOrDefault(sceneId, new ArrayList<>());
        
        PageResult<SceneSnapshotDTO> result = new PageResult<>();
        result.setList(snapList);
        result.setTotal(snapList.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public PageResult<LogDTO> getLogs(String sceneId, String level, Long startTime, Long endTime, int pageNum, int pageSize) {
        List<LogDTO> mockLogs = new ArrayList<>();
        long now = System.currentTimeMillis();
        
        String[] levels = {"INFO", "WARN", "ERROR", "DEBUG"};
        String[] sources = {"scene-manager", "capability-exec", "session", "auth", "config"};
        String[] messages = {
            "场景状态更新: 活跃",
            "能力执行完成: user-login",
            "会话即将过期",
            "认证失败: 无效令牌",
            "配置加载完成",
            "场景启动成功",
            "能力调用: send-message",
            "VFS同步完成"
        };
        
        for (int i = 0; i < 20; i++) {
            LogDTO log = new LogDTO();
            log.setTime(now - i * 60000);
            String logLevel = levels[i % levels.length];
            if (level == null || level.isEmpty() || level.equals(logLevel)) {
                log.setLevel(logLevel);
                log.setSource(sources[i % sources.length]);
                log.setMessage(messages[i % messages.length]);
                mockLogs.add(log);
            }
        }
        
        PageResult<LogDTO> result = new PageResult<>();
        result.setList(mockLogs);
        result.setTotal(mockLogs.size());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}
