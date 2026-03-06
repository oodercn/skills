package net.ooder.skill.scene.capability.model;

import net.ooder.skill.scene.capability.driver.DriverCondition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ConnectorType connectorType;
    private String endpoint;
    private List<ParameterDef> parameters;
    private ReturnDef returns;
    private CapabilityStatus status;
    private long createTime;
    private long updateTime;
    private String skillId;
    
    private List<String> capabilities;
    private boolean mainFirst;
    private MainFirstConfig mainFirstConfig;
    private List<CollaborativeCapabilityRef> collaborativeCapabilities;
    private DriverType driverType;
    private String icon;
    private Map<String, Object> metadata;
    private List<String> dependencies;
    private List<String> optionalCapabilities;
    
    private SceneSkillCategory category;
    private String visibility;
    private List<DriverCondition> driverConditions;
    private List<Participant> participants;

    public Capability() {
        this.version = "1.0.0";
        this.accessLevel = AccessLevel.SCENE;
        this.supportedSceneTypes = new ArrayList<String>();
        this.parameters = new ArrayList<ParameterDef>();
        this.status = CapabilityStatus.REGISTERED;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
        this.capabilities = new ArrayList<String>();
        this.collaborativeCapabilities = new ArrayList<CollaborativeCapabilityRef>();
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CapabilityType getType() {
        return type;
    }

    public void setType(CapabilityType type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getSupportedSceneTypes() {
        return supportedSceneTypes;
    }

    public void setSupportedSceneTypes(List<String> supportedSceneTypes) {
        this.supportedSceneTypes = supportedSceneTypes;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(ConnectorType connectorType) {
        this.connectorType = connectorType;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<ParameterDef> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDef> parameters) {
        this.parameters = parameters;
    }

    public ReturnDef getReturns() {
        return returns;
    }

    public void setReturns(ReturnDef returns) {
        this.returns = returns;
    }

    public CapabilityStatus getStatus() {
        return status;
    }

    public void setStatus(CapabilityStatus status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean supportsSceneType(String sceneType) {
        return supportedSceneTypes != null && supportedSceneTypes.contains(sceneType);
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

    public boolean isMainFirst() {
        return mainFirst;
    }

    public void setMainFirst(boolean mainFirst) {
        this.mainFirst = mainFirst;
    }

    public MainFirstConfig getMainFirstConfig() {
        return mainFirstConfig;
    }

    public void setMainFirstConfig(MainFirstConfig mainFirstConfig) {
        this.mainFirstConfig = mainFirstConfig;
    }

    public List<CollaborativeCapabilityRef> getCollaborativeCapabilities() {
        return collaborativeCapabilities;
    }

    public void setCollaborativeCapabilities(List<CollaborativeCapabilityRef> collaborativeCapabilities) {
        this.collaborativeCapabilities = collaborativeCapabilities;
    }

    public DriverType getDriverType() {
        return driverType;
    }

    public void setDriverType(DriverType driverType) {
        this.driverType = driverType;
    }

    public boolean isSceneCapability() {
        return type == CapabilityType.SCENE;
    }

    public boolean isDriverCapability() {
        return type == CapabilityType.DRIVER;
    }

    public boolean isInstalled() {
        return status == CapabilityStatus.ENABLED || status == CapabilityStatus.REGISTERED;
    }

    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<String, Object>();
        if (metadata != null) {
            config.putAll(metadata);
        }
        return config;
    }

    public boolean hasMainFirst() {
        return mainFirst && mainFirstConfig != null;
    }

    public Map<String, Object> getMetadata() {
        Map<String, Object> result = new HashMap<String, Object>();
        if (metadata != null) {
            result.putAll(metadata);
        }
        result.put("version", version);
        result.put("createTime", createTime);
        result.put("updateTime", updateTime);
        result.put("ownerId", ownerId);
        result.put("skillId", skillId);
        return result;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getIcon() {
        if (icon != null) {
            return icon;
        }
        return type != null ? type.getIcon() : "ri-flashlight-line";
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getDependencies() {
        if (dependencies != null) {
            return dependencies;
        }
        return capabilities;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<String> getOptionalCapabilities() {
        if (optionalCapabilities != null) {
            return optionalCapabilities;
        }
        List<String> optional = new ArrayList<String>();
        if (collaborativeCapabilities != null) {
            for (CollaborativeCapabilityRef ref : collaborativeCapabilities) {
                optional.add(ref.getCapabilityId());
            }
        }
        return optional;
    }

    public void setOptionalCapabilities(List<String> optionalCapabilities) {
        this.optionalCapabilities = optionalCapabilities;
    }

    public SceneSkillCategory getCategory() {
        return category;
    }

    public void setCategory(SceneSkillCategory category) {
        this.category = category;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public List<DriverCondition> getDriverConditions() {
        return driverConditions != null ? driverConditions : new ArrayList<DriverCondition>();
    }

    public void setDriverConditions(List<DriverCondition> driverConditions) {
        this.driverConditions = driverConditions;
    }

    public List<Participant> getParticipants() {
        return participants != null ? participants : new ArrayList<Participant>();
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public boolean hasBusinessSemantics() {
        return (driverConditions != null && !driverConditions.isEmpty()) 
            && (participants != null && !participants.isEmpty());
    }

    public boolean isPublicVisible() {
        return "public".equals(visibility) || (category != null && category.isPublicVisible());
    }

    public boolean isInternalVisible() {
        return "internal".equals(visibility) || (category != null && category.isInternalVisible());
    }

    public static class Participant implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String role;
        private String name;
        private String userId;
        private List<String> permissions;

        public Participant() {
            this.permissions = new ArrayList<String>();
        }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
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

        public ReturnDef() {
            this.properties = new HashMap<String, String>();
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Map<String, String> getProperties() { return properties; }
        public void setProperties(Map<String, String> properties) { this.properties = properties; }
    }
}
