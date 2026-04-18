package net.ooder.skill.cli.model;

public class CreateSceneResult {
    
    private boolean success;
    private String sceneId;
    private String message;
    private String error;
    
    public static CreateSceneResult success(String sceneId) {
        CreateSceneResult result = new CreateSceneResult();
        result.setSuccess(true);
        result.setSceneId(sceneId);
        return result;
    }
    
    public static CreateSceneResult failure(String error) {
        CreateSceneResult result = new CreateSceneResult();
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
    
    public String getSceneId() {
        return sceneId;
    }
    
    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
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
