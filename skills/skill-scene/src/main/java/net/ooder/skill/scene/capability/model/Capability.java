package net.ooder.skill.scene.capability.model;

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

    public Capability() {
        this.version = "1.0.0";
        this.accessLevel = AccessLevel.SCENE;
        this.supportedSceneTypes = new ArrayList<String>();
        this.parameters = new ArrayList<ParameterDef>();
        this.status = CapabilityStatus.REGISTERED;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
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
