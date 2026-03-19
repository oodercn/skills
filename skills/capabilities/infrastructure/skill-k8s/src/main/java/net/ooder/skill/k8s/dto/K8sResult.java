package net.ooder.skill.k8s.dto;

import lombok.Data;

@Data
public class K8sResult {
    private Boolean success;
    private String message;
    private Object data;
    
    public static K8sResult success(Object data) {
        K8sResult result = new K8sResult();
        result.setSuccess(true);
        result.setMessage("Success");
        result.setData(data);
        return result;
    }
    
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
}
