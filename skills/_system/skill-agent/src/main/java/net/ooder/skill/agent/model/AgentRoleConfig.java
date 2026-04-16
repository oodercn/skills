package net.ooder.skill.agent.model;

import java.util.List;
import java.util.Map;

public class AgentRoleConfig {
    private String agentId;
    private String agentName;
    private String agentType;
    private String role;
    private String systemPrompt;
    private List<String> capabilities;
    private Map<String, Object> llmConfig;
    private Map<String, Object> toolConfig;
    private Map<String, Object> knowledgeConfig;
    private int maxTokens;
    private double temperature;
    private List<String> allowedTools;
    private List<String> restrictedActions;
    private boolean functionCallingEnabled;
    private boolean streamingEnabled;
    private long createdAt;
    private long updatedAt;

    private TriggerMode triggerMode = TriggerMode.MENTION;
    private List<String> triggerKeywords;
    private boolean ragEnabled;
    private List<String> knowledgeBaseIds;
    private boolean active = true;
    private List<String> mentionedAgents;
    private List<Map<String, String>> recentMessages;

    public enum TriggerMode { MENTION, KEYWORD, ALL, NONE }

    public AgentRoleConfig() {
        this.capabilities = new java.util.ArrayList<>();
        this.allowedTools = new java.util.ArrayList<>();
        this.restrictedActions = new java.util.ArrayList<>();
        this.functionCallingEnabled = true;
        this.streamingEnabled = true;
        this.maxTokens = 4096;
        this.temperature = 0.7;
        this.triggerKeywords = new java.util.ArrayList<>();
        this.knowledgeBaseIds = new java.util.ArrayList<>();
        this.recentMessages = new java.util.ArrayList<>();
    }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAgentType() { return agentType; }
    public void setAgentType(String agentType) { this.agentType = agentType; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    public Map<String, Object> getLlmConfig() { return llmConfig; }
    public void setLlmConfig(Map<String, Object> llmConfig) { this.llmConfig = llmConfig; }
    public Map<String, Object> getToolConfig() { return toolConfig; }
    public void setToolConfig(Map<String, Object> toolConfig) { this.toolConfig = toolConfig; }
    public Map<String, Object> getKnowledgeConfig() { return knowledgeConfig; }
    public void setKnowledgeConfig(Map<String, Object> knowledgeConfig) { this.knowledgeConfig = knowledgeConfig; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public List<String> getAllowedTools() { return allowedTools; }
    public void setAllowedTools(List<String> allowedTools) { this.allowedTools = allowedTools; }
    public List<String> getRestrictedActions() { return restrictedActions; }
    public void setRestrictedActions(List<String> restrictedActions) { this.restrictedActions = restrictedActions; }
    public boolean isFunctionCallingEnabled() { return functionCallingEnabled; }
    public void setFunctionCallingEnabled(boolean functionCallingEnabled) { this.functionCallingEnabled = functionCallingEnabled; }
    public boolean isStreamingEnabled() { return streamingEnabled; }
    public void setStreamingEnabled(boolean streamingEnabled) { this.streamingEnabled = streamingEnabled; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public boolean hasCapability(String capability) {
        return capabilities != null && capabilities.contains(capability);
    }

    public boolean isToolAllowed(String toolName) {
        if (allowedTools == null || allowedTools.isEmpty()) {
            return true;
        }
        return allowedTools.contains(toolName);
    }

    public boolean isActionRestricted(String action) {
        return restrictedActions != null && restrictedActions.contains(action);
    }

    public TriggerMode getTriggerMode() { return triggerMode; }
    public void setTriggerMode(TriggerMode triggerMode) { this.triggerMode = triggerMode; }
    public List<String> getTriggerKeywords() { return triggerKeywords; }
    public void setTriggerKeywords(List<String> triggerKeywords) { this.triggerKeywords = triggerKeywords; }
    public boolean isRagEnabled() { return ragEnabled; }
    public void setRagEnabled(boolean ragEnabled) { this.ragEnabled = ragEnabled; }
    public List<String> getKnowledgeBaseIds() { return knowledgeBaseIds; }
    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) { this.knowledgeBaseIds = knowledgeBaseIds; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<String> getMentionedAgents() { return mentionedAgents; }
    public void setMentionedAgents(List<String> mentionedAgents) { this.mentionedAgents = mentionedAgents; }
    public List<Map<String, String>> getRecentMessages() { return recentMessages; }
    public void setRecentMessages(List<Map<String, String>> recentMessages) { this.recentMessages = recentMessages; }
}
