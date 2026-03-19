package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SceneGroupUpdateRequest implements Serializable {
    private String name;
    private String description;
    private SceneGroupConfigDTO config;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public SceneGroupConfigDTO getConfig() { return config; }
    public void setConfig(SceneGroupConfigDTO config) { this.config = config; }
}
