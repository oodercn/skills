package net.ooder.skill.scene.dto.scene;

import java.util.List;
import java.util.Map;

public class SuperAgentParticipantDTO {
    private String superAgentId;
    private String name;
    private String description;
    private String status;
    private List<AgentParticipantDTO> subAgents;
    private List<String> capabilities;
    private Map<String, Object> config;

    public String getSuperAgentId() { return superAgentId; }
    public void setSuperAgentId(String superAgentId) { this.superAgentId = superAgentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<AgentParticipantDTO> getSubAgents() { return subAgents; }
    public void setSubAgents(List<AgentParticipantDTO> subAgents) { this.subAgents = subAgents; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
}
