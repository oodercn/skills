package net.ooder.nexus.common.dto;

import java.util.List;
import java.util.Map;

public class Capability {
    private String capabilityId;
    private String name;
    private String description;
    private String category;
    private String providerSkillId;
    private List<String> inputParams;
    private List<String> outputParams;
    private HealthStatus healthStatus;
    private CapabilityStats stats;
    private Long createTime;
    private Long updateTime;
    
    public enum HealthStatus {
        HEALTHY,
        UNHEALTHY,
        UNKNOWN,
        UNAVAILABLE
    }
    
    public static class CapabilityStats {
        private int successCount;
        private int failureCount;
        private double successRate;
        private long avgResponseTime;
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        public long getAvgResponseTime() { return avgResponseTime; }
        public void setAvgResponseTime(long avgResponseTime) { this.avgResponseTime = avgResponseTime; }
    }
    
    public static class CapabilityRoute {
        private String capabilityId;
        private String skillId;
        private RoutePriority priority;
        private boolean enabled;
        
        public enum RoutePriority {
            HIGH, MEDIUM, LOW
        }
        
        public String getCapabilityId() { return capabilityId; }
        public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public RoutePriority getPriority() { return priority; }
        public void setPriority(RoutePriority priority) { this.priority = priority; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getProviderSkillId() { return providerSkillId; }
    public void setProviderSkillId(String providerSkillId) { this.providerSkillId = providerSkillId; }
    public List<String> getInputParams() { return inputParams; }
    public void setInputParams(List<String> inputParams) { this.inputParams = inputParams; }
    public List<String> getOutputParams() { return outputParams; }
    public void setOutputParams(List<String> outputParams) { this.outputParams = outputParams; }
    public HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }
    public CapabilityStats getStats() { return stats; }
    public void setStats(CapabilityStats stats) { this.stats = stats; }
    public Long getCreateTime() { return createTime; }
    public void setCreateTime(Long createTime) { this.createTime = createTime; }
    public Long getUpdateTime() { return updateTime; }
    public void setUpdateTime(Long updateTime) { this.updateTime = updateTime; }
}
