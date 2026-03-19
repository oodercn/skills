package net.ooder.mvp.api.scene.service;

import net.ooder.mvp.api.common.PageQuery;
import net.ooder.mvp.api.common.PageResult;
import net.ooder.mvp.api.scene.dto.*;

import java.util.List;
import java.util.Map;

public interface SceneGroupService {

    SceneGroupDTO create(SceneGroupCreateRequest request);
    
    SceneGroupDTO get(String sceneGroupId);
    
    PageResult<SceneGroupDTO> list(SceneGroupQuery query);
    
    SceneGroupDTO update(String sceneGroupId, SceneGroupUpdateRequest request);
    
    void destroy(String sceneGroupId);
    
    void activate(String sceneGroupId);
    
    void deactivate(String sceneGroupId);
    
    void join(String sceneGroupId, ParticipantJoinRequest request);
    
    void leave(String sceneGroupId, String participantId);
    
    PageResult<ParticipantDTO> listParticipants(String sceneGroupId, PageQuery query);
    
    void changeRole(String sceneGroupId, String participantId, String newRole);
    
    CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindRequest request);
    
    void unbindCapability(String sceneGroupId, String bindingId);
    
    PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, PageQuery query);
    
    SceneSnapshotDTO createSnapshot(String sceneGroupId);
    
    List<SceneSnapshotDTO> listSnapshots(String sceneGroupId);
    
    void restoreSnapshot(String sceneGroupId, String snapshotId);
    
    void bindKnowledgeBase(String sceneGroupId, KnowledgeBaseBindRequest request);
    
    void unbindKnowledgeBase(String sceneGroupId, String kbId);
    
    List<KnowledgeBaseBindingDTO> listKnowledgeBaseBindings(String sceneGroupId);
    
    Map<String, Object> getLlmConfig(String sceneGroupId);
    
    void updateLlmConfig(String sceneGroupId, Map<String, Object> config);
}
