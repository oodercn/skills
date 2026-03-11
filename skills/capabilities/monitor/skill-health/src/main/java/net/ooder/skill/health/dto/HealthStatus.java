package net.ooder.skill.health.dto;

public class HealthStatus {
    private String overallStatus;
    private boolean healthy;
    private int totalServices;
    private int healthyServices;
    private int unhealthyServices;
    private long lastCheckTime;
    private long nextCheckTime;

    public HealthStatus() {
        this.overallStatus = "unknown";
        this.healthy = false;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
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

    public long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public long getNextCheckTime() {
        return nextCheckTime;
    }

    public void setNextCheckTime(long nextCheckTime) {
        this.nextCheckTime = nextCheckTime;
    }
}
