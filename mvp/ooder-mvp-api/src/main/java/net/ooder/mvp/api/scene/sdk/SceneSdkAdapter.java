package net.ooder.mvp.api.scene.sdk;

import net.ooder.mvp.api.common.PageQuery;
import net.ooder.mvp.api.common.PageResult;
import net.ooder.mvp.api.common.SdkHealthStatus;
import net.ooder.mvp.api.scene.dto.*;

import java.util.List;
import java.util.Map;

public interface SceneSdkAdapter {

    SdkHealthStatus healthCheck();
    
    SceneGroupDTO createSceneGroup(SceneGroupCreateRequest request);
    
    SceneGroupDTO getSceneGroup(String sceneGroupId);
    
    PageResult<SceneGroupDTO> listSceneGroups(SceneGroupQuery query);
    
    SceneGroupDTO updateSceneGroup(String sceneGroupId, SceneGroupUpdateRequest request);
    
    void deleteSceneGroup(String sceneGroupId);
    
    void activateSceneGroup(String sceneGroupId);
    
    void deactivateSceneGroup(String sceneGroupId);
    
    void joinSceneGroup(String sceneGroupId, ParticipantJoinRequest request);
    
    void leaveSceneGroup(String sceneGroupId, String participantId);
    
    PageResult<ParticipantDTO> listParticipants(String sceneGroupId, PageQuery query);
    
    CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindRequest request);
    
    void unbindCapability(String sceneGroupId, String bindingId);
    
    PageResult<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId, PageQuery query);
    
    void bindKnowledgeBase(String sceneGroupId, KnowledgeBaseBindRequest request);
    
    void unbindKnowledgeBase(String sceneGroupId, String kbId);
    
    List<KnowledgeBaseBindingDTO> listKnowledgeBaseBindings(String sceneGroupId);
    
    Object invokeCapability(String skillId, Map<String, Object> params);
}
