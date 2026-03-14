package net.ooder.skill.scene.template;

public class DependencyConfig {
    private String skillId;
    private String version;
    private boolean autoInstall;
    private String healthCheck;
    private String description;

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public boolean isAutoInstall() { return autoInstall; }
    public void setAutoInstall(boolean autoInstall) { this.autoInstall = autoInstall; }
    public String getHealthCheck() { return healthCheck; }
    public void setHealthCheck(String healthCheck) { this.healthCheck = healthCheck; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
