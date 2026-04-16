package net.ooder.skill.scenes.service;

import net.ooder.skill.scenes.dto.SceneInfo;
import net.ooder.skill.scenes.dto.SceneDTO;
import java.util.List;
import java.util.Map;

public interface SceneService {
    
    SceneInfo getScene(String sceneId);
    
    SceneDTO get(String sceneId);
    
    List<SceneInfo> getSceneList(String userId);
    
    List<SceneDTO> listScenes(String userId, int page, int size);
    
    SceneInfo createScene(String name, String type, String ownerId, Map<String, Object> params);
    
    boolean updateScene(String sceneId, Map<String, Object> params);
    
    boolean deleteScene(String sceneId);
    
    boolean joinScene(String sceneId, String userId, String role);
    
    boolean addCollaborativeUser(String sceneId, String userId);
    
    boolean leaveScene(String sceneId, String userId);
    
    boolean removeCollaborativeUser(String sceneId, String userId);
    
    List<String> getSceneMembers(String sceneId);
    
    String getSceneStatus(String sceneId);
    
    boolean setSceneStatus(String sceneId, String status);
}
