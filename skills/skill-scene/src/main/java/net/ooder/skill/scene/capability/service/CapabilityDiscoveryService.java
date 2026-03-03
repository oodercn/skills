package net.ooder.skill.scene.capability.service;

import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.CapabilityType;
import net.ooder.skill.scene.capability.driver.DriverCondition;

import java.util.List;
import java.util.Map;

public interface CapabilityDiscoveryService {
    
    DiscoveryResult discoverCapabilities(DiscoveryRequest request);
    
    CapabilityDetail getCapabilityDetail(String capabilityId);
    
    List<DriverCondition> getDriverConditions(String capabilityId);
    
    public static class DiscoveryRequest {
        private DiscoveryMethod method;
        private String query;
        private CapabilityType type;
        private String sceneType;
        private int page;
        private int size;
        
        public DiscoveryMethod getMethod() { return method; }
        public void setMethod(DiscoveryMethod method) { this.method = method; }
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }
        public CapabilityType getType() { return type; }
        public void setType(CapabilityType type) { this.type = type; }
        public String getSceneType() { return sceneType; }
        public void setSceneType(String sceneType) { this.sceneType = sceneType; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
    }
    
    public static class DiscoveryResult {
        private List<CapabilityItem> sceneCapabilities;
        private List<CapabilityItem> collaborationCapabilities;
        private int totalScene;
        private int totalCollaboration;
        
        public List<CapabilityItem> getSceneCapabilities() { 
            return sceneCapabilities != null ? sceneCapabilities : java.util.Collections.emptyList(); 
        }
        public void setSceneCapabilities(List<CapabilityItem> sceneCapabilities) { 
            this.sceneCapabilities = sceneCapabilities; 
        }
        public List<CapabilityItem> getCollaborationCapabilities() { 
            return collaborationCapabilities != null ? collaborationCapabilities : java.util.Collections.emptyList(); 
        }
        public void setCollaborationCapabilities(List<CapabilityItem> collaborationCapabilities) { 
            this.collaborationCapabilities = collaborationCapabilities; 
        }
        public int getTotalScene() { return totalScene; }
        public void setTotalScene(int totalScene) { this.totalScene = totalScene; }
        public int getTotalCollaboration() { return totalCollaboration; }
        public void setTotalCollaboration(int totalCollaboration) { this.totalCollaboration = totalCollaboration; }
    }
    
    public static class CapabilityItem {
        private String capabilityId;
        private String name;
        private String description;
        private CapabilityType type;
        private String icon;
        private String version;
        private boolean installed;
        private List<DriverConditionInfo> driverConditions;
        private List<String> supportedSceneTypes;
        private Map<String, Object> metadata;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public CapabilityType getType() { return type; }
        public void setType(CapabilityType type) { this.type = type; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public boolean isInstalled() { return installed; }
        public void setInstalled(boolean installed) { this.installed = installed; }
        public List<DriverConditionInfo> getDriverConditions() { return driverConditions; }
        public void setDriverConditions(List<DriverConditionInfo> driverConditions) { this.driverConditions = driverConditions; }
        public List<String> getSupportedSceneTypes() { return supportedSceneTypes; }
        public void setSupportedSceneTypes(List<String> supportedSceneTypes) { this.supportedSceneTypes = supportedSceneTypes; }
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class DriverConditionInfo {
        private String conditionId;
        private String name;
        private String description;
        private String sceneType;
        
        public String getConditionId() { return conditionId; }
        public void setConditionId(String conditionId) { this.conditionId = conditionId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSceneType() { return sceneType; }
        public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    }
    
    public static class CapabilityDetail {
        private String capabilityId;
        private String name;
        private String description;
        private CapabilityType type;
        private String icon;
        private String version;
        private boolean installed;
        private List<DriverCondition> driverConditions;
        private List<String> dependencies;
        private List<String> optionalCapabilities;
        private Map<String, Object> config;
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public CapabilityType getType() { return type; }
        public void setType(CapabilityType type) { this.type = type; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public boolean isInstalled() { return installed; }
        public void setInstalled(boolean installed) { this.installed = installed; }
        public List<DriverCondition> getDriverConditions() { return driverConditions; }
        public void setDriverConditions(List<DriverCondition> driverConditions) { this.driverConditions = driverConditions; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
        public List<String> getOptionalCapabilities() { return optionalCapabilities; }
        public void setOptionalCapabilities(List<String> optionalCapabilities) { this.optionalCapabilities = optionalCapabilities; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }
    
    public enum DiscoveryMethod {
        LOCAL_FS,
        GITHUB,
        GITEE,
        SKILL_CENTER,
        UDP_BROADCAST,
        AUTO
    }
}
