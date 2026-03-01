package net.ooder.skillcenter.dto;

public class K8sResult {
    private boolean success;
    private String message;
    private Object data;
    
    public static K8sResult success(String message) {
        K8sResult result = new K8sResult();
        result.setSuccess(true);
        result.setMessage(message);
        return result;
    }
    
    public static K8sResult fail(String message) {
        K8sResult result = new K8sResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
