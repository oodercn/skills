package net.ooder.mvp.api.common;

import java.io.Serializable;
import java.util.Map;

public class SdkHealthStatus implements Serializable {
    private boolean available;
    private String status;
    private String message;
    private long responseTime;
    private Map<String, Object> details;
    
    public static final String STATUS_UP = "UP";
    public static final String STATUS_DOWN = "DOWN";
    public static final String STATUS_DEGRADED = "DEGRADED";
    
    public SdkHealthStatus() {}
    
    public static SdkHealthStatus up() {
        SdkHealthStatus status = new SdkHealthStatus();
        status.setAvailable(true);
        status.setStatus(STATUS_UP);
        status.setMessage("SDK is available");
        return status;
    }
    
    public static SdkHealthStatus down(String message) {
        SdkHealthStatus status = new SdkHealthStatus();
        status.setAvailable(false);
        status.setStatus(STATUS_DOWN);
        status.setMessage(message);
        return status;
    }
    
    public static SdkHealthStatus degraded(String message) {
        SdkHealthStatus status = new SdkHealthStatus();
        status.setAvailable(true);
        status.setStatus(STATUS_DEGRADED);
        status.setMessage(message);
        return status;
    }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getResponseTime() { return responseTime; }
    public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
