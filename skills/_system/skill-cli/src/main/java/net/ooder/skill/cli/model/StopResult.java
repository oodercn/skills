package net.ooder.skill.cli.model;

public class StopResult {
    
    private boolean success;
    private String skillId;
    private String message;
    private String error;
    
    public static StopResult success(String skillId) {
        StopResult result = new StopResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        return result;
    }
    
    public static StopResult failure(String error) {
        StopResult result = new StopResult();
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
