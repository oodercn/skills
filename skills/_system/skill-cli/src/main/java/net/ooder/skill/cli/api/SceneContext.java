package net.ooder.skill.cli.api;

import java.util.Map;
import java.util.HashMap;

public class SceneContext {
    
    private String sceneId;
    private String sceneGroupId;
    private String userId;
    private Map<String, Object> variables;
    private Map<String, Object> config;
    
    public SceneContext() {
        this.variables = new HashMap<>();
        this.config = new HashMap<>();
    }
    
    public static SceneContext create(String sceneId) {
        SceneContext ctx = new SceneContext();
        ctx.setSceneId(sceneId);
        return ctx;
    }
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public void setSceneGroupId(String sceneGroupId) {
        this.sceneGroupId = sceneGroupId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
    
    public Object getVariable(String key) {
        return variables.get(key);
    }
    
    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    public Object getConfig(String key) {
        return config.get(key);
    }
}
