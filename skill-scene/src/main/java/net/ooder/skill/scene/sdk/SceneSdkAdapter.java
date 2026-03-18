package net.ooder.skill.scene.sdk;

import net.ooder.skill.scene.dto.scene.*;

import java.util.List;
import java.util.Map;

public interface SceneSdkAdapter {

    boolean isAvailable();

    SceneGroupDTO createSceneGroup(String templateId, SceneGroupConfigDTO config);

    SceneGroupDTO getSceneGroup(String sceneGroupId);

    List<SceneGroupDTO> listSceneGroups();

    boolean activateSceneGroup(String sceneGroupId);

    boolean deactivateSceneGroup(String sceneGroupId);

    boolean joinSceneGroup(String sceneGroupId, SceneParticipantDTO participant);

    boolean leaveSceneGroup(String sceneGroupId, String participantId);

    List<SceneParticipantDTO> listParticipants(String sceneGroupId);

    CapabilityBindingDTO bindCapability(String sceneGroupId, CapabilityBindingDTO binding);

    boolean unbindCapability(String sceneGroupId, String bindingId);

    List<CapabilityBindingDTO> listCapabilityBindings(String sceneGroupId);

    Object invokeCapability(String skillId, Map<String, Object> params);
}
