package net.ooder.skill.cli.model;

public class InstallResult {
    
    private boolean success;
    private String skillId;
    private String message;
    private String error;
    
    public static InstallResult success(String skillId) {
        InstallResult result = new InstallResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        result.setMessage("Skill installed successfully");
        return result;
    }
    
    public static InstallResult failure(String error) {
        InstallResult result = new InstallResult();
        result.setSuccess(false);
        result.setError(error);
        return result;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
