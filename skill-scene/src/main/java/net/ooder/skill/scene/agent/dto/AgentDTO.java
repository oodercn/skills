package net.ooder.skill.scene.agent.dto;

public class AgentDTO {
    private String agentId;
    private String agentName;
    private String agentType;
    private String status;
    private String ipAddress;
    private int port;
    private String version;
    private String sceneGroupId;
    private String role;
    private long registerTime;
    private long lastHeartbeat;
    private int bindingCount;
    private String description;

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public long getRegisterTime() { return registerTime; }
    public void setRegisterTime(long registerTime) { this.registerTime = registerTime; }
    public long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(long lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    public int getBindingCount() { return bindingCount; }
    public void setBindingCount(int bindingCount) { this.bindingCount = bindingCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
