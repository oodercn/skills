package net.ooder.skill.cli.model;

public class SubmitResult {
    
    private boolean success;
    private String taskId;
    private String message;
    private String error;
    
    public static SubmitResult success(String taskId) {
        SubmitResult result = new SubmitResult();
        result.setSuccess(true);
        result.setTaskId(taskId);
        return result;
    }
    
    public static SubmitResult failure(String error) {
        SubmitResult result = new SubmitResult();
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
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
