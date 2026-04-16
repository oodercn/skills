package net.ooder.skill.scenes.dto;

public class ActionResultDTO {
    
    private String action;
    private boolean success;
    private String message;
    private Object data;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
