package net.ooder.skill.collaboration.service;

import net.ooder.skill.collaboration.dto.*;

import java.util.List;

public interface CollaborationService {
    CollaborationScene createScene(SceneCreateRequest request);
    List<CollaborationScene> listScenes(String ownerId);
    CollaborationScene getScene(String sceneId);
    CollaborationScene updateScene(String sceneId, SceneUpdateRequest request);
    boolean deleteScene(String sceneId);
    boolean addMember(String sceneId, MemberAddRequest request);
    boolean removeMember(String sceneId, String memberId);
    List<SceneMember> getMembers(String sceneId);
    SceneKeyResult generateKey(String sceneId);
    boolean changeStatus(String sceneId, SceneStatus status);
}
