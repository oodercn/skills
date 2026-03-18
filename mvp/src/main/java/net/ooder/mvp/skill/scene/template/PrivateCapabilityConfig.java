package net.ooder.mvp.skill.scene.template;

public class PrivateCapabilityConfig {
    private String capId;
    private String name;
    private String description;
    private boolean optional;
    private boolean enabled;
    private String skillId;

    public String getCapId() { return capId; }
    public void setCapId(String capId) { this.capId = capId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isOptional() { return optional; }
    public void setOptional(boolean optional) { this.optional = optional; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
}
