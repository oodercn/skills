package net.ooder.skill.agent.dto;

import java.util.List;
import java.util.Map;

public class SceneChatContextDTO {

    private String sceneGroupId;
    private String sceneGroupName;
    private String sceneGroupStatus;
    private String sceneType;
    private String sceneDescription;

    private List<ParticipantInfo> participants;
    private List<AgentInfo> agents;
    private List<CapabilityInfo> capabilities;
    private List<KnowledgeInfo> knowledgeBases;

    private Map<String, Object> sceneVariables;
    private Map<String, Object> sceneState;
    private Map<String, Object> llmConfig;

    private String currentUserId;
    private String currentUserName;
    private String currentUserRole;
    private String tenantId;
    private int activeSessionCount;
    private boolean userOnline;

    private String sceneId;
    private String sceneName;
    private String sceneStatus;

    public SceneChatContextDTO() {}

    public String getSceneGroupId() { return sceneGroupId; }
    public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }

    public String getSceneGroupName() { return sceneGroupName; }
    public void setSceneGroupName(String sceneGroupName) { this.sceneGroupName = sceneGroupName; }

    public String getSceneGroupStatus() { return sceneGroupStatus; }
    public void setSceneGroupStatus(String sceneGroupStatus) { this.sceneGroupStatus = sceneGroupStatus; }

    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }

    public String getSceneDescription() { return sceneDescription; }
    public void setSceneDescription(String sceneDescription) { this.sceneDescription = sceneDescription; }

    public List<ParticipantInfo> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantInfo> participants) { this.participants = participants; }

    public List<AgentInfo> getAgents() { return agents; }
    public void setAgents(List<AgentInfo> agents) { this.agents = agents; }

    public List<CapabilityInfo> getCapabilities() { return capabilities; }
    public void setCapabilities(List<CapabilityInfo> capabilities) { this.capabilities = capabilities; }

    public List<KnowledgeInfo> getKnowledgeBases() { return knowledgeBases; }
    public void setKnowledgeBases(List<KnowledgeInfo> knowledgeBases) { this.knowledgeBases = knowledgeBases; }

    public String getSceneId() { return sceneId; }
    public void setSceneId(String sceneId) { this.sceneId = sceneId; }

    public String getSceneName() { return sceneName; }
    public void setSceneName(String sceneName) { this.sceneName = sceneName; }

    public String getSceneStatus() { return sceneStatus; }
    public void setSceneStatus(String sceneStatus) { this.sceneStatus = sceneStatus; }

    public Map<String, Object> getSceneVariables() { return sceneVariables; }
    public void setSceneVariables(Map<String, Object> sceneVariables) { this.sceneVariables = sceneVariables; }

    public Map<String, Object> getSceneState() { return sceneState; }
    public void setSceneState(Map<String, Object> sceneState) { this.sceneState = sceneState; }

    public Map<String, Object> getLlmConfig() { return llmConfig; }
    public void setLlmConfig(Map<String, Object> llmConfig) { this.llmConfig = llmConfig; }

    public String getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(String currentUserId) { this.currentUserId = currentUserId; }

    public String getCurrentUserName() { return currentUserName; }
    public void setCurrentUserName(String currentUserName) { this.currentUserName = currentUserName; }

    public String getCurrentUserRole() { return currentUserRole; }
    public void setCurrentUserRole(String currentUserRole) { this.currentUserRole = currentUserRole; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public int getActiveSessionCount() { return activeSessionCount; }
    public void setActiveSessionCount(int activeSessionCount) { this.activeSessionCount = activeSessionCount; }

    public boolean isUserOnline() { return userOnline; }
    public void setUserOnline(boolean userOnline) { this.userOnline = userOnline; }

    public static class ParticipantInfo {
        private String id;
        private String name;
        private String type;
        private String role;
        private boolean online;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }
    }

    public static class AgentInfo {
        private String agentId;
        private String name;
        private String type;
        private String role;
        private String status;
        private String systemPrompt;
        private Map<String, Object> capabilities;

        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getSystemPrompt() { return systemPrompt; }
        public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
        public Map<String, Object> getCapabilities() { return capabilities; }
        public void setCapabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; }
    }

    public static class CapabilityInfo {
        private String capId;
        private String capName;
        private String capType;
        private String description;

        public String getCapId() { return capId; }
        public void setCapId(String capId) { this.capId = capId; }
        public String getCapName() { return capName; }
        public void setCapName(String capName) { this.capName = capName; }
        public String getCapType() { return capType; }
        public void setCapType(String capType) { this.capType = capType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class KnowledgeInfo {
        private String kbId;
        private String name;
        private String type;
        private String description;

        public String getKbId() { return kbId; }
        public void setKbId(String kbId) { this.kbId = kbId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
