package net.ooder.skill.llm.monitor.dto;

import java.util.Map;

public class LlmMonitorStatsDTO {
    
    private long totalCalls;
    private long successCalls;
    private long failedCalls;
    private double avgLatency;
    private Map<String, Object> details;

    public long getTotalCalls() { return totalCalls; }
    public void setTotalCalls(long totalCalls) { this.totalCalls = totalCalls; }
    public long getSuccessCalls() { return successCalls; }
    public void setSuccessCalls(long successCalls) { this.successCalls = successCalls; }
    public long getFailedCalls() { return failedCalls; }
    public void setFailedCalls(long failedCalls) { this.failedCalls = failedCalls; }
    public double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(double avgLatency) { this.avgLatency = avgLatency; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
