package net.ooder.skill.llm.monitor.dto;

import java.util.Map;

public class LlmMonitorStatsDTO {
    
    private Long totalCalls;
    private Long successCalls;
    private Long failedCalls;
    private Double avgLatency;
    private Map<String, Object> details;

    public Long getTotalCalls() { return totalCalls; }
    public void setTotalCalls(Long totalCalls) { this.totalCalls = totalCalls; }
    public Long getSuccessCalls() { return successCalls; }
    public void setSuccessCalls(Long successCalls) { this.successCalls = successCalls; }
    public Long getFailedCalls() { return failedCalls; }
    public void setFailedCalls(Long failedCalls) { this.failedCalls = failedCalls; }
    public Double getAvgLatency() { return avgLatency; }
    public void setAvgLatency(Double avgLatency) { this.avgLatency = avgLatency; }
    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
