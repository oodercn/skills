package net.ooder.skill.llm.config.dto;

import java.io.Serializable;

public class RateLimitsDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer requestsPerMinute;
    private Integer tokensPerMinute;
    private Integer requestsPerDay;
    private Integer tokensPerDay;
    private Integer concurrentRequests;
    
    public RateLimitsDTO() {}
    
    public Integer getRequestsPerMinute() {
        return requestsPerMinute;
    }
    
    public void setRequestsPerMinute(Integer requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }
    
    public Integer getTokensPerMinute() {
        return tokensPerMinute;
    }
    
    public void setTokensPerMinute(Integer tokensPerMinute) {
        this.tokensPerMinute = tokensPerMinute;
    }
    
    public Integer getRequestsPerDay() {
        return requestsPerDay;
    }
    
    public void setRequestsPerDay(Integer requestsPerDay) {
        this.requestsPerDay = requestsPerDay;
    }
    
    public Integer getTokensPerDay() {
        return tokensPerDay;
    }
    
    public void setTokensPerDay(Integer tokensPerDay) {
        this.tokensPerDay = tokensPerDay;
    }
    
    public Integer getConcurrentRequests() {
        return concurrentRequests;
    }
    
    public void setConcurrentRequests(Integer concurrentRequests) {
        this.concurrentRequests = concurrentRequests;
    }
}
