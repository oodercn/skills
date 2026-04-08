package net.ooder.skill.template.dto;

import java.util.List;

public class InstallResultDTO {
    
    private String templateId;
    private boolean success;
    private String message;
    private List<String> installedSkills;
    private List<String> skippedSkills;
    private List<String> failedSkills;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<String> getInstalledSkills() { return installedSkills; }
    public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
    public List<String> getSkippedSkills() { return skippedSkills; }
    public void setSkippedSkills(List<String> skippedSkills) { this.skippedSkills = skippedSkills; }
    public List<String> getFailedSkills() { return failedSkills; }
    public void setFailedSkills(List<String> failedSkills) { this.failedSkills = failedSkills; }
}
