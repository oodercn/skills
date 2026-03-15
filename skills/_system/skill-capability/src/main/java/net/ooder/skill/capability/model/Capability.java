package net.ooder.skill.capability.model;

import java.io.Serializable;
import java.util.*;

public class Capability implements Serializable {
    private static final long serialVersionUID = 1L;

    private String capabilityId;
    private String name;
    private String description;
    private CapabilityType type;
    private String version;
    private AccessLevel accessLevel;
    private String ownerId;
    private List<String> supportedSceneTypes;
    private String endpoint;
    private List<ParameterDef> parameters;
    private ReturnDef returns;
    private CapabilityStatus status;
    private long createTime;
    private long updateTime;
    private String skillId;
    
    private List<String> capabilities;
    private boolean mainFirst;
    private String icon;
    private Map<String, Object> metadata;
    private List<String> dependencies;
    
    private SkillForm skillForm;
    private String visibility;
    private CapabilityCategory capabilityCategory;
    
    private String businessCategory;
    private String subCategory;
    private List<String> tags;
    
    private boolean dynamicSceneTypes;
    private String parentSkill;
    private String parentScene;
    
    private boolean installed;
    private Integer businessSemanticsScore;

    public Capability() {
        this.version = "1.0.0";
        this.accessLevel = AccessLevel.SCENE;
        this.supportedSceneTypes = new ArrayList<String>();
        this.parameters = new ArrayList<ParameterDef>();
        this.status = CapabilityStatus.REGISTERED;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
        this.capabilities = new ArrayList<String>();
        this.skillForm = SkillForm.PROVIDER;
        this.visibility = Visibility.PUBLIC.getCode();
        this.tags = new ArrayList<String>();
        this.metadata = new HashMap<String, Object>();
    }

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CapabilityType getType() { return type; }
    public void setType(CapabilityType type) { this.type = type; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public AccessLevel getAccessLevel() { return accessLevel; }
    public void setAccessLevel(AccessLevel accessLevel) { this.accessLevel = accessLevel; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public List<String> getSupportedSceneTypes() { return supportedSceneTypes; }
    public void setSupportedSceneTypes(List<String> supportedSceneTypes) { this.supportedSceneTypes = supportedSceneTypes; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public List<ParameterDef> getParameters() { return parameters; }
    public void setParameters(List<ParameterDef> parameters) { this.parameters = parameters; }

    public ReturnDef getReturns() { return returns; }
    public void setReturns(ReturnDef returns) { this.returns = returns; }

    public CapabilityStatus getStatus() { return status; }
    public void setStatus(CapabilityStatus status) { this.status = status; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }

    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }

    public boolean isMainFirst() { return mainFirst; }
    public void setMainFirst(boolean mainFirst) { this.mainFirst = mainFirst; }

    public String getIcon() {
        if (icon != null) return icon;
        return type != null ? type.getIcon() : "ri-flashlight-line";
    }
    public void setIcon(String icon) { this.icon = icon; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public List<String> getDependencies() { return dependencies != null ? dependencies : capabilities; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }

    public SkillForm getSkillForm() { return skillForm; }
    public void setSkillForm(SkillForm skillForm) { this.skillForm = skillForm; }
    public void setSkillForm(String skillForm) { this.skillForm = SkillForm.fromCode(skillForm); }

    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public Visibility getVisibilityEnum() { return Visibility.fromCode(visibility); }

    public CapabilityCategory getCapabilityCategory() { return capabilityCategory; }
    public void setCapabilityCategory(CapabilityCategory capabilityCategory) { this.capabilityCategory = capabilityCategory; }
    public void setCapabilityCategory(String capabilityCategory) { this.capabilityCategory = CapabilityCategory.fromCode(capabilityCategory); }

    public String getBusinessCategory() { return businessCategory; }
    public void setBusinessCategory(String businessCategory) { this.businessCategory = businessCategory; }

    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public boolean isDynamicSceneTypes() { return dynamicSceneTypes; }
    public void setDynamicSceneTypes(boolean dynamicSceneTypes) { this.dynamicSceneTypes = dynamicSceneTypes; }

    public String getParentSkill() { return parentSkill; }
    public void setParentSkill(String parentSkill) { this.parentSkill = parentSkill; }

    public String getParentScene() { return parentScene; }
    public void setParentScene(String parentScene) { this.parentScene = parentScene; }

    public boolean isInstalled() { return installed; }
    public void setInstalled(boolean installed) { this.installed = installed; }

    public Integer getBusinessSemanticsScore() { return businessSemanticsScore; }
    public void setBusinessSemanticsScore(Integer businessSemanticsScore) { this.businessSemanticsScore = businessSemanticsScore; }

    public String getCategory() { return capabilityCategory != null ? capabilityCategory.getCode() : null; }
    public void setCategory(String category) { this.capabilityCategory = CapabilityCategory.fromCode(category); }

    public boolean isEnabled() { return status == CapabilityStatus.ENABLED || status == CapabilityStatus.REGISTERED; }

    public void addSceneType(String sceneType) {
        if (supportedSceneTypes == null) supportedSceneTypes = new ArrayList<String>();
        if (!supportedSceneTypes.contains(sceneType)) {
            supportedSceneTypes.add(sceneType);
            this.updateTime = System.currentTimeMillis();
        }
    }

    public void removeSceneType(String sceneType) {
        if (supportedSceneTypes != null) {
            supportedSceneTypes.remove(sceneType);
            this.updateTime = System.currentTimeMillis();
        }
    }

    public static class ParameterDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String type;
        private boolean required;
        private Object defaultValue;
        private String description;

        public ParameterDef() {}
        public ParameterDef(String name, String type, boolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ReturnDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private String type;
        private Map<String, String> properties;

        public ReturnDef() { this.properties = new HashMap<String, String>(); }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, String> getProperties() { return properties; }
        public void setProperties(Map<String, String> properties) { this.properties = properties; }
    }
}
