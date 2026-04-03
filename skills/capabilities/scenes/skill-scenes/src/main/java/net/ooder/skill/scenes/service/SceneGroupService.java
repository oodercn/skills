package net.ooder.skill.scenes.service;

import net.ooder.skill.scenes.dto.SceneGroupDTO;
import net.ooder.skill.scenes.model.PageResult;

import java.util.List;
import java.util.Map;

public interface SceneGroupService {

    PageResult<SceneGroupDTO> getMyCreatedGroups(int pageNum, int pageSize);

    PageResult<SceneGroupDTO> getMyLedGroups(int pageNum, int pageSize);

    PageResult<SceneGroupDTO> getMyParticipatedGroups(int pageNum, int pageSize);

    SceneGroupDTO create(SceneGroupDTO group);

    SceneGroupDTO get(String id);

    SceneGroupDTO update(SceneGroupDTO group);

    boolean delete(String id);

    SceneGroupDTO activate(String id);

    SceneGroupDTO deactivate(String id);

    List<Map<String, Object>> getCapabilities(String groupId);

    Map<String, Object> addCapability(String groupId, String capId);

    boolean removeCapability(String groupId, String capId);

    List<Map<String, Object>> getParticipants(String groupId);

    Map<String, Object> addParticipant(String groupId, String userId, String role);

    boolean updateParticipantRole(String groupId, String participantId, String role);

    boolean removeParticipant(String groupId, String participantId);

    List<Map<String, Object>> getSnapshots(String groupId);

    Map<String, Object> createSnapshot(String groupId, String name);

    boolean restoreSnapshot(String groupId, String snapshotId);

    boolean deleteSnapshot(String groupId, String snapshotId);

    List<Map<String, Object>> getKnowledgeBases(String groupId);

    Map<String, Object> addKnowledgeBase(String groupId, String kbId);

    boolean removeKnowledgeBase(String groupId, String kbId);

    Map<String, Object> getLlmConfig(String groupId);

    Map<String, Object> updateLlmConfig(String groupId, Map<String, Object> config);

    PageResult<Map<String, Object>> getEventLog(String groupId, int pageNum, int pageSize);

    Map<String, Object> startWorkflow(String groupId, String workflowId, Map<String, Object> params);

    SceneGroupDTO createFromFusion(String fusionId, Map<String, Object> params);

    Map<String, Object> getCapability(String groupId, String capId);

    Map<String, Object> getKnowledgeConfig(String groupId);

    Map<String, Object> updateKnowledgeConfig(String groupId, Map<String, Object> config);

    List<Map<String, Object>> getLlmProviderModels(String groupId, String providerId);

    boolean resetLlmConfig(String groupId);

    Map<String, Object> executeAction(String groupId, String action, Map<String, Object> params);

    List<Map<String, Object>> getMyLedMembers();
}
