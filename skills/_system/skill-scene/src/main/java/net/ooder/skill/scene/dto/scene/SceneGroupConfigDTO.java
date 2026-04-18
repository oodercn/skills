package net.ooder.skill.scene.dto.scene;

import java.util.List;
import java.util.Map;

public class SceneGroupConfigDTO {
    
    private String name;
    private String description;
    private String selectedRole;
    private String leader;
    private List<String> collaborators;
    private String pushType;
    private List<String> driverConditions;
    private Map<String, Object> llmConfig;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSelectedRole() { return selectedRole; }
    public void setSelectedRole(String selectedRole) { this.selectedRole = selectedRole; }
    public String getLeader() { return leader; }
    public void setLeader(String leader) { this.leader = leader; }
    public List<String> getCollaborators() { return collaborators; }
    public void setCollaborators(List<String> collaborators) { this.collaborators = collaborators; }
    public String getPushType() { return pushType; }
    public void setPushType(String pushType) { this.pushType = pushType; }
    public List<String> getDriverConditions() { return driverConditions; }
    public void setDriverConditions(List<String> driverConditions) { this.driverConditions = driverConditions; }
    public Map<String, Object> getLlmConfig() { return llmConfig; }
    public void setLlmConfig(Map<String, Object> llmConfig) { this.llmConfig = llmConfig; }
}
