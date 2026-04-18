package net.ooder.skill.cli.driver;

import net.ooder.skill.cli.model.SceneEntity;
import net.ooder.skill.cli.model.SceneStatus;
import net.ooder.skill.cli.model.CreateSceneResult;

import java.util.List;
import java.util.Map;

public interface SceneDriver {
    
    String getDriverId();
    
    String getDriverName();
    
    boolean isAvailable();
    
    CreateSceneResult create(String sceneGroupId, Map<String, Object> config);
    
    boolean destroy(String sceneId);
    
    boolean start(String sceneId);
    
    boolean stop(String sceneId);
    
    List<SceneEntity> getAllScenes();
    
    SceneEntity getScene(String sceneId);
    
    SceneStatus getStatus(String sceneId);
    
    Map<String, Object> getVariables(String sceneId);
    
    boolean setVariable(String sceneId, String key, Object value);
    
    void refresh();
}
