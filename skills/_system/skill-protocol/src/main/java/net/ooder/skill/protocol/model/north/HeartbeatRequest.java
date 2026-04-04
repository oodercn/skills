package net.ooder.skill.protocol.model.north;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeartbeatRequest {
    
    @JsonProperty("agent_id")
    private String agentId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("metrics")
    private Object metrics;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getMetrics() {
        return metrics;
    }

    public void setMetrics(Object metrics) {
        this.metrics = metrics;
    }
}
