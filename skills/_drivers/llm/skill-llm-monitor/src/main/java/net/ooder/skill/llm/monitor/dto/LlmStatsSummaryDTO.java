package net.ooder.skill.llm.monitor.dto;

import java.util.List;
import java.util.Map;

public class LlmStatsSummaryDTO {
    
    private Long totalCalls;
    private Long totalTokens;
    private Double totalCost;
    private Long avgLatency;
    private Double successRate;
    private Long errorCount;
    private Long successCount;
    
    public LlmStatsSummaryDTO() {
    }
    
    public Long getTotalCalls() { return totalCalls; }
    public void setTotalCalls(Long totalCalls) { this.totalCalls = totalCalls; }
    
    public Long getTotalTokens() { return totalTokens; }
    public void setTotalTokens(Long totalTokens) { this.totalTokens = totalTokens; }
    
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
    
    public Long getAvgLatency() { return avgLatency; }
    public void setAvgLatency(Long avgLatency) { this.avgLatency = avgLatency; }
    
    public Double getSuccessRate() { return successRate; }
    public void setSuccessRate(Double successRate) { this.successRate = successRate; }
    
    public Long getErrorCount() { return errorCount; }
    public void setErrorCount(Long errorCount) { this.errorCount = errorCount; }
    
    public Long getSuccessCount() { return successCount; }
    public void setSuccessCount(Long successCount) { this.successCount = successCount; }
}
