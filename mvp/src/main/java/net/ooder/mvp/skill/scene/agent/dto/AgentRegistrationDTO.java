package net.ooder.mvp.skill.scene.agent.dto;

public class AgentRegistrationDTO {
    private String agentId;
    private String agentName;
    private String agentType;
    private String secretKey;
    private String ipAddress;
    private int port;
    private String version;
    private String description;
    private String sceneGroupId;
    private String role;

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
