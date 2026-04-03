package net.ooder.skill.llm.config.dto;

import java.io.Serializable;

public class CostConfigDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Double inputCostPer1k;
    private Double outputCostPer1k;
    private Double monthlyBudget;
    private Double alertThreshold;
    private Boolean trackCosts;
    
    public CostConfigDTO() {}
    
    public Double getInputCostPer1k() {
        return inputCostPer1k;
    }
    
    public void setInputCostPer1k(Double inputCostPer1k) {
        this.inputCostPer1k = inputCostPer1k;
    }
    
    public Double getOutputCostPer1k() {
        return outputCostPer1k;
    }
    
    public void setOutputCostPer1k(Double outputCostPer1k) {
        this.outputCostPer1k = outputCostPer1k;
    }
    
    public Double getMonthlyBudget() {
        return monthlyBudget;
    }
    
    public void setMonthlyBudget(Double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }
    
    public Double getAlertThreshold() {
        return alertThreshold;
    }
    
    public void setAlertThreshold(Double alertThreshold) {
        this.alertThreshold = alertThreshold;
    }
    
    public Boolean getTrackCosts() {
        return trackCosts;
    }
    
    public void setTrackCosts(Boolean trackCosts) {
        this.trackCosts = trackCosts;
    }
}
