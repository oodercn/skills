package net.ooder.skill.scene.capability.activation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ActivationService {
    
    ActivationProcess getProcess(String installId);
    
    ActivationProcess startProcess(String installId, String activator);
    
    ActivationProcess executeStep(String installId, String stepId, Map<String, Object> data);
    
    KeyResult getKey(String installId);
    
    ActivationProcess confirmActivation(String installId);
    
    ActivationProcess cancelActivation(String installId);
    
    List<ActivationProcess.NetworkAction> getNetworkActions(String installId);
    
    CompletableFuture<ActivationProcess> executeNetworkActions(String installId);
    
    ActivationProcess skipStep(String installId, String stepId);
    
    public static class KeyResult {
        private String keyId;
        private String keyStatus;
        private long expireTime;
        private Map<String, Object> permissions;
        private String message;
        
        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        public String getKeyStatus() { return keyStatus; }
        public void setKeyStatus(String keyStatus) { this.keyStatus = keyStatus; }
        public long getExpireTime() { return expireTime; }
        public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
        public Map<String, Object> getPermissions() { return permissions; }
        public void setPermissions(Map<String, Object> permissions) { this.permissions = permissions; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
