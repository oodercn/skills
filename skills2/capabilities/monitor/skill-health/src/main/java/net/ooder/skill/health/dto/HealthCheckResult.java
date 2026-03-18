package net.ooder.skill.health.dto;

import java.util.List;

public class HealthCheckResult {
    private String checkId;
    private String status;
    private boolean healthy;
    private List<ServiceCheckResult> serviceResults;
    private int totalServices;
    private int healthyServices;
    private int unhealthyServices;
    private long checkTime;
    private long duration;

    public HealthCheckResult() {
        this.checkId = "check-" + System.currentTimeMillis();
        this.checkTime = System.currentTimeMillis();
    }

    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
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

    public List<ServiceCheckResult> getServiceResults() {
        return serviceResults;
    }

    public void setServiceResults(List<ServiceCheckResult> serviceResults) {
        this.serviceResults = serviceResults;
    }

    public int getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(int totalServices) {
        this.totalServices = totalServices;
    }

    public int getHealthyServices() {
        return healthyServices;
    }

    public void setHealthyServices(int healthyServices) {
        this.healthyServices = healthyServices;
    }

    public int getUnhealthyServices() {
        return unhealthyServices;
    }

    public void setUnhealthyServices(int unhealthyServices) {
        this.unhealthyServices = unhealthyServices;
    }

    public long getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(long checkTime) {
        this.checkTime = checkTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
