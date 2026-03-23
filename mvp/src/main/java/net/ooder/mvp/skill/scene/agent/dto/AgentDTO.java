package net.ooder.mvp.skill.scene.agent.dto;

import java.util.List;
import java.util.Map;

public class AgentDTO {
    private String agentId;
    private String agentName;
    private String agentType;
    private String status;
    private String ipAddress;
    private Integer port;
    private String version;
    private String sceneGroupId;
    private String role;
    private Long registerTime;
    private Long lastHeartbeat;
    private Integer bindingCount;
    private String description;
    private Boolean enabled;
    
    private String clusterId;
    private Map<String, Object> metrics;
    private List<String> capabilities;
    private Map<String, String> tags;
    private Integer maxConcurrency;
    private Integer currentLoad;
    private Double cpuUsage;
    private Double memoryUsage;
    private Long totalRequests;
    private Long successRequests;
    private Long failedRequests;
    private Double avgResponseTime;
    private String healthStatus;
    private String llmConfigId;
    private List<String> supportedModels;
    private Map<String, Object> extendedConfig;

    public AgentDTO() {}

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getRegisterTime() { return registerTime; }
    public void setRegisterTime(Long registerTime) { this.registerTime = registerTime; }
    public Long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(Long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    public Integer getBindingCount() { return bindingCount; }
    public void setBindingCount(Integer bindingCount) { this.bindingCount = bindingCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public String getClusterId() { return clusterId; }
    public void setClusterId(String clusterId) { this.clusterId = clusterId; }
    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    public Map<String, String> getTags() { return tags; }
    public void setTags(Map<String, String> tags) { this.tags = tags; }
    public Integer getMaxConcurrency() { return maxConcurrency; }
    public void setMaxConcurrency(Integer maxConcurrency) { this.maxConcurrency = maxConcurrency; }
    public Integer getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Integer currentLoad) { this.currentLoad = currentLoad; }
    public Double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }
    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }
    public Long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }
    public Long getSuccessRequests() { return successRequests; }
    public void setSuccessRequests(Long successRequests) { this.successRequests = successRequests; }
    public Long getFailedRequests() { return failedRequests; }
    public void setFailedRequests(Long failedRequests) { this.failedRequests = failedRequests; }
    public Double getAvgResponseTime() { return avgResponseTime; }
    public void setAvgResponseTime(Double avgResponseTime) { this.avgResponseTime = avgResponseTime; }
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public String getLlmConfigId() { return llmConfigId; }
    public void setLlmConfigId(String llmConfigId) { this.llmConfigId = llmConfigId; }
    public List<String> getSupportedModels() { return supportedModels; }
    public void setSupportedModels(List<String> supportedModels) { this.supportedModels = supportedModels; }
    public Map<String, Object> getExtendedConfig() { return extendedConfig; }
    public void setExtendedConfig(Map<String, Object> extendedConfig) { this.extendedConfig = extendedConfig; }
    
    public Double getSuccessRate() {
        if (totalRequests == null || totalRequests == 0) return 100.0;
        if (successRequests == null) return 0.0;
        return (successRequests * 100.0) / totalRequests;
    }
    
    public Double getLoadPercentage() {
        if (maxConcurrency == null || maxConcurrency == 0) return 0.0;
        if (currentLoad == null) return 0.0;
        return (currentLoad * 100.0) / maxConcurrency;
    }
    
    public Boolean isHealthy() {
        return "healthy".equals(healthStatus) || "online".equals(status);
    }
    
    public Long getUptime() {
        if (registerTime == null || registerTime == 0) return 0L;
        return System.currentTimeMillis() - registerTime;
    }
}
