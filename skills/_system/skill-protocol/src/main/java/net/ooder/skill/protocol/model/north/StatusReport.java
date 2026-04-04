package net.ooder.skill.protocol.model.north;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusReport {
    
    @JsonProperty("agent_id")
    private String agentId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("metrics")
    private Map<String, Object> metrics;
    
    @JsonProperty("active_scenes")
    private Integer activeScenes;
    
    @JsonProperty("active_tasks")
    private Integer activeTasks;

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

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    public Integer getActiveScenes() {
        return activeScenes;
    }

    public void setActiveScenes(Integer activeScenes) {
        this.activeScenes = activeScenes;
    }

    public Integer getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(Integer activeTasks) {
        this.activeTasks = activeTasks;
    }
}
