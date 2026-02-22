package net.ooder.skill.health.dto;

public class ServiceCheckResult {
    private String serviceName;
    private String serviceType;
    private String status;
    private boolean healthy;
    private String message;
    private long responseTime;
    private long checkTime;
    private String endpoint;

    public ServiceCheckResult() {
        this.checkTime = System.currentTimeMillis();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
