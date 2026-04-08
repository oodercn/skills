package net.ooder.skill.template.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SceneTemplate {

    private String apiVersion;
    private String kind;
    private Metadata metadata;
    private Spec spec;
    private UiConfig ui;
    private List<MenuConfig> menu;

    public static class Metadata {
        private String id;
        private String name;
        private String description;
        private String category;
        private String icon;
        private String version;
        private String author;
        private String participantMode;

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
    }

    public static class Spec {
        private List<SkillRef> skills;
        private DependenciesConfig dependencies;
        private List<CapabilityDef> capabilities;
        private List<RoleConfig> roles;
        private Map<String, List<ActivationStepConfig>> activationSteps;
        private Map<String, List<MenuConfig>> menus;
        private List<UiSkillConfig> uiSkills;
        private List<PrivateCapabilityConfig> privateCapabilities;
        private SceneConfig scene;
        private List<String> installOrder;
        private Map<String, String> estimatedResources;
        private String estimatedDuration;

        public List<SkillRef> getSkills() { return skills; }
        public void setSkills(List<SkillRef> skills) { this.skills = skills; }
        public DependenciesConfig getDependencies() { return dependencies; }
        public void setDependencies(DependenciesConfig dependencies) { this.dependencies = dependencies; }
        public List<CapabilityDef> getCapabilities() { return capabilities; }
        public void setCapabilities(List<CapabilityDef> capabilities) { this.capabilities = capabilities; }
        public List<RoleConfig> getRoles() { return roles; }
        public void setRoles(List<RoleConfig> roles) { this.roles = roles; }
        public Map<String, List<ActivationStepConfig>> getActivationSteps() { return activationSteps; }
        public void setActivationSteps(Map<String, List<ActivationStepConfig>> activationSteps) { this.activationSteps = activationSteps; }
        public Map<String, List<MenuConfig>> getMenus() { return menus; }
        public void setMenus(Map<String, List<MenuConfig>> menus) { this.menus = menus; }
        public List<UiSkillConfig> getUiSkills() { return uiSkills; }
        public void setUiSkills(List<UiSkillConfig> uiSkills) { this.uiSkills = uiSkills; }
        public List<PrivateCapabilityConfig> getPrivateCapabilities() { return privateCapabilities; }
        public void setPrivateCapabilities(List<PrivateCapabilityConfig> privateCapabilities) { this.privateCapabilities = privateCapabilities; }
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

    public String getId() { return metadata != null ? metadata.getId() : null; }
    public String getName() { return metadata != null ? metadata.getName() : null; }
    public String getDescription() { return metadata != null ? metadata.getDescription() : null; }
    public String getCategory() { return metadata != null ? metadata.getCategory() : null; }
    public String getIcon() { return metadata != null ? metadata.getIcon() : null; }

    public List<String> getSkillIds() {
        if (spec == null || spec.getSkills() == null) { return Collections.emptyList(); }
        List<String> ids = new ArrayList<String>();
        for (SkillRef ref : spec.getSkills()) { ids.add(ref.getId()); }
        return ids;
    }

    public List<SkillRef> getRequiredSkills() {
        if (spec == null || spec.getSkills() == null) { return Collections.emptyList(); }
        List<SkillRef> required = new ArrayList<SkillRef>();
        for (SkillRef ref : spec.getSkills()) { if (ref.isRequired()) { required.add(ref); } }
        return required;
    }

    public List<SkillRef> getOptionalSkills() {
        if (spec == null || spec.getSkills() == null) { return Collections.emptyList(); }
        List<SkillRef> optional = new ArrayList<SkillRef>();
        for (SkillRef ref : spec.getSkills()) { if (!ref.isRequired()) { optional.add(ref); } }
        return optional;
    }

    public String getParticipantMode() { return metadata != null ? metadata.getParticipantMode() : "single-user"; }
    public List<RoleConfig> getRoles() { return spec != null && spec.getRoles() != null ? spec.getRoles() : Collections.emptyList(); }

    public List<ActivationStepConfig> getActivationSteps(String role) {
        if (spec == null || spec.getActivationSteps() == null) { return Collections.emptyList(); }
        List<ActivationStepConfig> steps = spec.getActivationSteps().get(role);
        return steps != null ? steps : Collections.emptyList();
    }

    public List<MenuConfig> getMenus(String role) {
        if (spec != null && spec.getMenus() != null) {
            List<MenuConfig> menus = spec.getMenus().get(role);
            if (menus != null && !menus.isEmpty()) { return menus; }
        }
        if (menu != null && !menu.isEmpty()) { return menu; }
        return Collections.emptyList();
    }

    public List<MenuConfig> getMenu() { return menu; }
    public void setMenu(List<MenuConfig> menu) { this.menu = menu; }
    public UiConfig getUi() { return ui; }
    public void setUi(UiConfig ui) { this.ui = ui; }
    public List<UiSkillConfig> getUiSkills() { return spec != null && spec.getUiSkills() != null ? spec.getUiSkills() : Collections.emptyList(); }
    public List<PrivateCapabilityConfig> getPrivateCapabilities() { return spec != null && spec.getPrivateCapabilities() != null ? spec.getPrivateCapabilities() : Collections.emptyList(); }
    public DependenciesConfig getDependencies() { return spec != null ? spec.getDependencies() : null; }
}
