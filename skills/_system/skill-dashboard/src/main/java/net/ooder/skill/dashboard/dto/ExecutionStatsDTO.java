package net.ooder.skill.dashboard.dto;

public class ExecutionStatsDTO {
    
    private long totalExecutions;
    private long successCount;
    private long failureCount;
    private double successRate;
    private long avgExecutionTime;

    public long getTotalExecutions() { return totalExecutions; }
    public void setTotalExecutions(long totalExecutions) { this.totalExecutions = totalExecutions; }
    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }
    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    public long getAvgExecutionTime() { return avgExecutionTime; }
    public void setAvgExecutionTime(long avgExecutionTime) { this.avgExecutionTime = avgExecutionTime; }
}
