package net.ooder.skill.scene.dto.discovery;

import java.util.List;
import java.util.Map;

public class CapabilityDTO {
    
    private String id;
    
    private String name;
    
    private String type;
    
    private String description;
    
    private String version;
    
    private String source;
    
    private String status;
    
    private String skillId;
    
    private List<String> capabilities;
    
    private List<String> supportedSceneTypes;
    
    private boolean isSceneCapability;
    
    private String category;
    
    private boolean mainFirst;
    
    private String visibility;
    
    private List<Map<String, Object>> driverConditions;
    
    private List<Map<String, Object>> participants;
    
    private Map<String, Object> metadata;

    public CapabilityDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public List<String> getSupportedSceneTypes() {
        return supportedSceneTypes;
    }

    public void setSupportedSceneTypes(List<String> supportedSceneTypes) {
        this.supportedSceneTypes = supportedSceneTypes;
    }

    public boolean isSceneCapability() {
        return isSceneCapability;
    }

    public void setSceneCapability(boolean sceneCapability) {
        isSceneCapability = sceneCapability;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isMainFirst() {
        return mainFirst;
    }

    public void setMainFirst(boolean mainFirst) {
        this.mainFirst = mainFirst;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public List<Map<String, Object>> getDriverConditions() {
        return driverConditions;
    }

    public void setDriverConditions(List<Map<String, Object>> driverConditions) {
        this.driverConditions = driverConditions;
    }

    public List<Map<String, Object>> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Map<String, Object>> participants) {
        this.participants = participants;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
