package net.ooder.skill.agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public class AgentStatusDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "Agent ID不能为空")
    private String agentId;
    
    @Size(max = 20, message = "状态长度不能超过20")
    private String status;
    private String lastCheck;
    private Long lastHeartbeat;
    private Boolean enabled;
    private String healthStatus;
    private Integer currentLoad;
    private Double cpuUsage;
    private Double memoryUsage;
    
    public AgentStatusDTO() {}
    
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
    
    public String getLastCheck() {
        return lastCheck;
    }
    
    public void setLastCheck(String lastCheck) {
        this.lastCheck = lastCheck;
    }
    
    public Long getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void setLastHeartbeat(Long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getHealthStatus() {
        return healthStatus;
    }
    
    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }
    
    public Integer getCurrentLoad() {
        return currentLoad;
    }
    
    public void setCurrentLoad(Integer currentLoad) {
        this.currentLoad = currentLoad;
    }
    
    public Double getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public Double getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(Double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
}
