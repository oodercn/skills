package net.ooder.mvp.skill.scene.dto.stats;

import java.util.List;

public class CapabilityRankDTO {
    
    private String capabilityId;
    private String name;
    private String type;
    private int invokeCount;
    private int successCount;
    private double avgResponseTime;
    private double successRate;

    public CapabilityRankDTO() {
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getInvokeCount() {
        return invokeCount;
    }

    public void setInvokeCount(int invokeCount) {
        this.invokeCount = invokeCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public double getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(double avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }
}
