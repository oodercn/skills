package net.ooder.skill.template.dto;

import java.util.List;

public class DeployResultDTO {
    
    private String templateId;
    private String templateName;
    private String sceneId;
    private boolean success;
    private String message;
    private List<String> installedSkills;
    private List<String> skippedSkills;
    private List<String> failedSkills;
    private List<String> boundCapabilities;
    private long startTime;
    private long endTime;
    private long duration;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }
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
    public List<String> getBoundCapabilities() { return boundCapabilities; }
    public void setBoundCapabilities(List<String> boundCapabilities) { this.boundCapabilities = boundCapabilities; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}
