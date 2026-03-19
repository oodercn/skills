package net.ooder.mvp.api.scene.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SceneGroupCreateRequest implements Serializable {
    private String templateId;
    private String name;
    private String description;
    private String creatorId;
    private String creatorType;
    private SceneGroupConfigDTO config;
    
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public String getCreatorType() { return creatorType; }
    public void setCreatorType(String creatorType) { this.creatorType = creatorType; }
    public SceneGroupConfigDTO getConfig() { return config; }
    public void setConfig(SceneGroupConfigDTO config) { this.config = config; }
}
