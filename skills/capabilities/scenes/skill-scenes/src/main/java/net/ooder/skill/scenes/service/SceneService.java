package net.ooder.skill.scenes.service;

import net.ooder.skill.scenes.dto.SceneDTO;
import net.ooder.skill.scenes.dto.SceneCapabilityDTO;
import net.ooder.skill.scenes.model.PageResult;

import java.util.List;
import java.util.Map;

public interface SceneService {

    PageResult<SceneDTO> listScenes(String status, int pageNum, int pageSize);

    SceneDTO get(String sceneId);

    SceneDTO create(SceneDTO scene);

    SceneDTO update(String sceneId, SceneDTO scene);

    boolean delete(String sceneId);

    SceneDTO activate(String sceneId);

    SceneDTO deactivate(String sceneId);

    List<SceneCapabilityDTO> listCapabilities(String sceneId);

    SceneCapabilityDTO addCapability(String sceneId, String capId);

    boolean removeCapability(String sceneId, String capId);

    List<String> listCollaborativeUsers(String sceneId);

    boolean addCollaborativeUser(String sceneId, String userId);

    boolean removeCollaborativeUser(String sceneId, String userId);

    String createSnapshot(String sceneId);

    List<Map<String, Object>> listSnapshots(String sceneId, int pageNum, int pageSize);

    boolean restoreSnapshot(String sceneId, String snapshotId);

    List<Map<String, Object>> getLogs(String sceneId, int pageNum, int pageSize);
}