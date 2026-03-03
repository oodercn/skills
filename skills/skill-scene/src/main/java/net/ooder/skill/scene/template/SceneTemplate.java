package net.ooder.skill.scene.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SceneTemplate {

    private String apiVersion;
    private String kind;
    private Metadata metadata;
    private Spec spec;

    public static class Metadata {
        private String id;
        private String name;
        private String description;
        private String category;
        private String icon;
        private String version;
        private String author;

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
    }

    public static class Spec {
        private List<SkillRef> skills;
        private List<CapabilityDef> capabilities;
        private SceneConfig scene;
        private List<String> installOrder;
        private Map<String, String> estimatedResources;
        private String estimatedDuration;

        public List<SkillRef> getSkills() { return skills; }
        public void setSkills(List<SkillRef> skills) { this.skills = skills; }
        public List<CapabilityDef> getCapabilities() { return capabilities; }
        public void setCapabilities(List<CapabilityDef> capabilities) { this.capabilities = capabilities; }
        public SceneConfig getScene() { return scene; }
        public void setScene(SceneConfig scene) { this.scene = scene; }
        public List<String> getInstallOrder() { return installOrder; }
        public void setInstallOrder(List<String> installOrder) { this.installOrder = installOrder; }
        public Map<String, String> getEstimatedResources() { return estimatedResources; }
        public void setEstimatedResources(Map<String, String> estimatedResources) { this.estimatedResources = estimatedResources; }
        public String getEstimatedDuration() { return estimatedDuration; }
        public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    }

    public static class SkillRef {
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

    public static class CapabilityDef {
        private String id;
        private String name;
        private String description;
        private String category;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class SceneConfig {
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

    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public Metadata getMetadata() { return metadata; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }
    public Spec getSpec() { return spec; }
    public void setSpec(Spec spec) { this.spec = spec; }

    public String getId() {
        return metadata != null ? metadata.getId() : null;
    }

    public String getName() {
        return metadata != null ? metadata.getName() : null;
    }

    public String getDescription() {
        return metadata != null ? metadata.getDescription() : null;
    }

    public String getCategory() {
        return metadata != null ? metadata.getCategory() : null;
    }

    public String getIcon() {
        return metadata != null ? metadata.getIcon() : null;
    }

    public List<String> getSkillIds() {
        if (spec == null || spec.getSkills() == null) {
            return Collections.emptyList();
        }
        List<String> ids = new ArrayList<>();
        for (SkillRef ref : spec.getSkills()) {
            ids.add(ref.getId());
        }
        return ids;
    }

    public List<SkillRef> getRequiredSkills() {
        if (spec == null || spec.getSkills() == null) {
            return Collections.emptyList();
        }
        List<SkillRef> required = new ArrayList<>();
        for (SkillRef ref : spec.getSkills()) {
            if (ref.isRequired()) {
                required.add(ref);
            }
        }
        return required;
    }

    public List<SkillRef> getOptionalSkills() {
        if (spec == null || spec.getSkills() == null) {
            return Collections.emptyList();
        }
        List<SkillRef> optional = new ArrayList<>();
        for (SkillRef ref : spec.getSkills()) {
            if (!ref.isRequired()) {
                optional.add(ref);
            }
        }
        return optional;
    }
}
