package net.ooder.skill.template.dto;

import java.util.List;
import java.util.Map;

public class SceneTemplateDTO {
    
    private String id;
    private String name;
    private String description;
    private String category;
    private String icon;
    private String version;
    private String author;
    private String participantMode;
    private List<SkillRefDTO> skills;
    private List<String> installOrder;
    private List<CapabilityDefDTO> capabilities;
    private SceneConfigDTO scene;
    private Map<String, String> estimatedResources;
    private String estimatedDuration;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getParticipantMode() { return participantMode; }
    public void setParticipantMode(String participantMode) { this.participantMode = participantMode; }
    public List<SkillRefDTO> getSkills() { return skills; }
    public void setSkills(List<SkillRefDTO> skills) { this.skills = skills; }
    public List<String> getInstallOrder() { return installOrder; }
    public void setInstallOrder(List<String> installOrder) { this.installOrder = installOrder; }
    public List<CapabilityDefDTO> getCapabilities() { return capabilities; }
    public void setCapabilities(List<CapabilityDefDTO> capabilities) { this.capabilities = capabilities; }
    public SceneConfigDTO getScene() { return scene; }
    public void setScene(SceneConfigDTO scene) { this.scene = scene; }
    public Map<String, String> getEstimatedResources() { return estimatedResources; }
    public void setEstimatedResources(Map<String, String> estimatedResources) { this.estimatedResources = estimatedResources; }
    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }

    public static class SkillRefDTO {
        private String id;
        private String version;
        private boolean required;
        private String description;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class CapabilityDefDTO {
        private String id;
        private String name;
        private String description;
        private String category;
        private boolean autoBind;
        private List<String> dependencies;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public boolean isAutoBind() { return autoBind; }
        public void setAutoBind(boolean autoBind) { this.autoBind = autoBind; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    }

    public static class SceneConfigDTO {
        private String type;
        private String name;
        private String description;
        private Map<String, Object> config;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }
}
