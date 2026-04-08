package net.ooder.skill.template.model;

public class DependencyConfig {
    private String skillId;
    private String version;
    private boolean required;
    private String description;

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
