package net.ooder.skill.llm.monitor.dto;

public class OverallStatsDTO {
    
    private long totalCalls;
    private long totalTokens;
    private double totalCost;
    private double avgLatency;
    private double successRate;
    private long errorCount;
    private long uniqueUsers;
    private long uniqueModules;

    public long getTotalCalls() { return totalCalls; }
    public void setTotalCalls(long totalCalls) { this.totalCalls = totalCalls; }
    public long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(long totalTokens) { this.totalTokens = totalTokens; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(double avgLatency) { this.avgLatency = avgLatency; }
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public long getErrorCount() { return errorCount; }
    public void setErrorCount(long errorCount) { this.errorCount = errorCount; }
    public long getUniqueUsers() { return uniqueUsers; }
    public void setUniqueUsers(long uniqueUsers) { this.uniqueUsers = uniqueUsers; }
    public long getUniqueModules() { return uniqueModules; }
    public void setUniqueModules(long uniqueModules) { this.uniqueModules = uniqueModules; }
}
