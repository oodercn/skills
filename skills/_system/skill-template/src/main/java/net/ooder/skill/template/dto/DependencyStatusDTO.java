package net.ooder.skill.template.dto;

public class DependencyStatusDTO {
    
    private String skillId;
    private String name;
    private boolean required;
    private String status;
    private String version;
    private String installedVersion;
    private String message;

    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getInstalledVersion() { return installedVersion; }
    public void setInstalledVersion(String installedVersion) { this.installedVersion = installedVersion; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
