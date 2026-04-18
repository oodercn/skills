package net.ooder.skill.cli.model;

public class UninstallResult {
    
    private boolean success;
    private String skillId;
    private String message;
    private String error;
    
    public static UninstallResult success(String skillId) {
        UninstallResult result = new UninstallResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        return result;
    }
    
    public static UninstallResult failure(String error) {
        UninstallResult result = new UninstallResult();
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
