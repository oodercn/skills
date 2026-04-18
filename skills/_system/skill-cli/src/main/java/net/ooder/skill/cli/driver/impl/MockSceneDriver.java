package net.ooder.skill.cli.driver.impl;

import net.ooder.skill.cli.driver.SceneDriver;
import net.ooder.skill.cli.model.*;

import java.time.LocalDateTime;
import java.util.*;

public class MockSceneDriver implements SceneDriver {
    
    private final Map<String, SceneEntity> scenes = new HashMap<>();
    
    public MockSceneDriver() {
        initMockData();
    }
    
    private void initMockData() {
        SceneEntity scene1 = new SceneEntity();
        scene1.setSceneId("scene-001");
        scene1.setSceneGroupId("sg-default");
        scene1.setName("Default Scene");
        scene1.setStatus(SceneStatus.RUNNING);
        scene1.setCreatedAt(LocalDateTime.now().minusHours(2));
        scene1.setStartedAt(LocalDateTime.now().minusHours(2));
        scenes.put(scene1.getSceneId(), scene1);
    }
    
    @Override
    public String getDriverId() {
        return "mock-scene-driver";
    }
    
    @Override
    public String getDriverName() {
        return "Mock Scene Driver";
    }
    
    @Override
    public boolean isAvailable() {
        return true;
    }
    
    @Override
    public CreateSceneResult create(String sceneGroupId, Map<String, Object> config) {
        String sceneId = "scene-" + UUID.randomUUID().toString().substring(0, 8);
        SceneEntity entity = new SceneEntity();
        entity.setSceneId(sceneId);
        entity.setSceneGroupId(sceneGroupId);
        entity.setName("Scene " + sceneId);
        entity.setStatus(SceneStatus.CREATED);
        entity.setCreatedAt(LocalDateTime.now());
        scenes.put(sceneId, entity);
        return CreateSceneResult.success(sceneId);
    }
    
    @Override
    public boolean destroy(String sceneId) {
        return scenes.remove(sceneId) != null;
    }
    
    @Override
    public boolean start(String sceneId) {
        SceneEntity entity = scenes.get(sceneId);
        if (entity != null) {
            entity.setStatus(SceneStatus.RUNNING);
            entity.setStartedAt(LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean stop(String sceneId) {
        SceneEntity entity = scenes.get(sceneId);
        if (entity != null) {
            entity.setStatus(SceneStatus.STOPPED);
            return true;
        }
        return false;
    }
    
    @Override
    public List<SceneEntity> getAllScenes() {
        return new ArrayList<>(scenes.values());
    }
    
    @Override
    public SceneEntity getScene(String sceneId) {
        return scenes.get(sceneId);
    }
    
    @Override
    public SceneStatus getStatus(String sceneId) {
        SceneEntity entity = scenes.get(sceneId);
        return entity != null ? entity.getStatus() : null;
    }
    
    @Override
    public Map<String, Object> getVariables(String sceneId) {
        return Map.of("var1", "value1", "var2", 123);
    }
    
    @Override
    public boolean setVariable(String sceneId, String key, Object value) {
        return true;
    }
    
    @Override
    public void refresh() {
    }
}
