package net.ooder.skill.scenes.service;

import net.ooder.skill.scenes.dto.*;
import net.ooder.skill.scenes.model.PageResult;

import java.util.List;

public interface SceneGroupService {

    PageResult<SceneGroupDTO> getAllGroups(int pageNum, int pageSize);

    PageResult<SceneGroupDTO> getMyCreatedGroups(int pageNum, int pageSize);

    PageResult<SceneGroupDTO> getMyLedGroups(int pageNum, int pageSize);

    PageResult<SceneGroupDTO> getMyParticipatedGroups(int pageNum, int pageSize);

    SceneGroupDTO create(SceneGroupDTO group);

    SceneGroupDTO get(String id);

    SceneGroupDTO update(SceneGroupDTO group);

    boolean delete(String id);

    SceneGroupDTO activate(String id);

    SceneGroupDTO deactivate(String id);

    List<SceneGroupCapabilityDTO> getCapabilities(String groupId);

    SceneGroupCapabilityDTO addCapability(String groupId, String capId);

    boolean removeCapability(String groupId, String capId);

    List<SceneParticipantDTO> getParticipants(String groupId);

    SceneParticipantDTO addParticipant(String groupId, String userId, String role);

    boolean updateParticipantRole(String groupId, String participantId, String role);

    boolean removeParticipant(String groupId, String participantId);

    List<SceneSnapshotDTO> getSnapshots(String groupId);

    SceneSnapshotDTO createSnapshot(String groupId, String name);

    boolean restoreSnapshot(String groupId, String snapshotId);

    boolean deleteSnapshot(String groupId, String snapshotId);

    boolean bindKnowledgeBase(String groupId, KnowledgeBindingDTO binding);

    boolean unbindKnowledgeBase(String groupId, String kbId);

    List<KnowledgeBindingDTO> getKnowledgeBases(String groupId);

    SceneLlmConfigDTO getLlmConfig(String groupId);

    boolean setLlmConfig(String groupId, SceneLlmConfigDTO config);

    PageResult<SceneGroupEventLogDTO> getEventLog(String groupId, int pageNum, int pageSize);

    WorkflowResultDTO startWorkflow(String groupId, String workflowId);

    SceneGroupDTO createFromFusion(String fusionId);

    SceneGroupCapabilityDTO getCapability(String groupId, String capId);

    SceneKnowledgeConfigDTO getKnowledgeConfig(String groupId);

    SceneKnowledgeConfigDTO updateKnowledgeConfig(String groupId, SceneKnowledgeConfigDTO config);

    List<LlmModelDTO> getLlmProviderModels(String groupId, String providerId);

    boolean resetLlmConfig(String groupId);

    ActionResultDTO executeAction(String groupId, String action);

    List<MemberDTO> getMyLedMembers();
}
