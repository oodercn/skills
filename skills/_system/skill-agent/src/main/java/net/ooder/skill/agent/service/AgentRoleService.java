package net.ooder.skill.agent.service;

import net.ooder.skill.agent.model.AgentRoleConfig;

import java.util.List;
import java.util.Map;

public interface AgentRoleService {
    
    AgentRoleConfig createRole(AgentRoleConfig roleConfig);
    
    AgentRoleConfig updateRole(String agentId, AgentRoleConfig roleConfig);
    
    AgentRoleConfig getRole(String agentId);
    
    void deleteRole(String agentId);
    
    List<AgentRoleConfig> listRoles();
    
    List<AgentRoleConfig> listRolesByType(String agentType);
    
    void setSystemPrompt(String agentId, String systemPrompt);
    
    String getSystemPrompt(String agentId);
    
    void addCapability(String agentId, String capability);
    
    void removeCapability(String agentId, String capability);
    
    List<String> getCapabilities(String agentId);
    
    void setLLMConfig(String agentId, Map<String, Object> llmConfig);
    
    Map<String, Object> getLLMConfig(String agentId);
    
    void setToolConfig(String agentId, Map<String, Object> toolConfig);
    
    Map<String, Object> getToolConfig(String agentId);
    
    void allowTool(String agentId, String toolName);
    
    void restrictTool(String agentId, String toolName);
    
    void restrictAction(String agentId, String action);
    
    void allowAction(String agentId, String action);
    
    void enableFunctionCalling(String agentId, boolean enabled);
    
    void enableStreaming(String agentId, boolean enabled);
    
    void setMaxTokens(String agentId, int maxTokens);
    
    void setTemperature(String agentId, double temperature);
    
    void setKnowledgeConfig(String agentId, Map<String, Object> knowledgeConfig);
    
    Map<String, Object> getKnowledgeConfig(String agentId);
}
