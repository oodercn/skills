package net.ooder.skill.llm.monitor.dto;

public class ProviderStatsDTO {
    
    private String providerId;
    private String providerName;
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double successRate;
    private long totalTokens;
    private double totalCost;
    private double avgLatency;

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public long getTotalCalls() { return totalCalls; }
    public void setTotalCalls(long totalCalls) { this.totalCalls = totalCalls; }
    public long getSuccessCalls() { return successCalls; }
    public void setSuccessCalls(long successCalls) { this.successCalls = successCalls; }
    public long getFailedCalls() { return failedCalls; }
    public void setFailedCalls(long failedCalls) { this.failedCalls = failedCalls; }
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(double avgLatency) { this.avgLatency = avgLatency; }
}
