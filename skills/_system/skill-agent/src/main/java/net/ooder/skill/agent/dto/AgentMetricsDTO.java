package net.ooder.skill.agent.dto;

import java.util.List;
import java.util.Map;

public class AgentMetricsDTO {
    private String agentId;
    private String agentName;
    private Long timestamp;
    private Double cpuUsage;
    private Double memoryUsage;
    private Integer currentLoad;
    private Integer maxConcurrency;
    private Double loadPercentage;
    private Long totalRequests;
    private Long successRequests;
    private Long failedRequests;
    private Double successRate;
    private Double avgResponseTime;
    private Long queueSize;
    private String healthStatus;
    private List<MetricPoint> responseTimeHistory;
    private List<MetricPoint> requestRateHistory;
    private Map<String, Object> customMetrics;

    public AgentMetricsDTO() {}

    public static class MetricPoint {
        private Long timestamp;
        private Double value;

        public MetricPoint() {}
        
        public MetricPoint(Long timestamp, Double value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public Double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }
    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }
    public Integer getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(Integer currentLoad) { this.currentLoad = currentLoad; }
    public Integer getMaxConcurrency() { return maxConcurrency; }
    public void setMaxConcurrency(Integer maxConcurrency) { this.maxConcurrency = maxConcurrency; }
    public Double getLoadPercentage() { return loadPercentage; }
    public void setLoadPercentage(Double loadPercentage) { this.loadPercentage = loadPercentage; }
    public Long getTotalRequests() { return totalRequests; }
    public void setTotalRequests(Long totalRequests) { this.totalRequests = totalRequests; }
    public Long getSuccessRequests() { return successRequests; }
    public void setSuccessRequests(Long successRequests) { this.successRequests = successRequests; }
    public Long getFailedRequests() { return failedRequests; }
    public void setFailedRequests(Long failedRequests) { this.failedRequests = failedRequests; }
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    public Double getAvgResponseTime() { return avgResponseTime; }
    public void setAvgResponseTime(Double avgResponseTime) { this.avgResponseTime = avgResponseTime; }
    public Long getQueueSize() { return queueSize; }
    public void setQueueSize(Long queueSize) { this.queueSize = queueSize; }
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public List<MetricPoint> getResponseTimeHistory() { return responseTimeHistory; }
    public void setResponseTimeHistory(List<MetricPoint> responseTimeHistory) { this.responseTimeHistory = responseTimeHistory; }
    public List<MetricPoint> getRequestRateHistory() { return requestRateHistory; }
    public void setRequestRateHistory(List<MetricPoint> requestRateHistory) { this.requestRateHistory = requestRateHistory; }
    public Map<String, Object> getCustomMetrics() { return customMetrics; }
    public void setCustomMetrics(Map<String, Object> customMetrics) { this.customMetrics = customMetrics; }
}
