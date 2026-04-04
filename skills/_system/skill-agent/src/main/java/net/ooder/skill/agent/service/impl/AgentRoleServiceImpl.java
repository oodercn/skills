package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.model.AgentRoleConfig;
import net.ooder.skill.agent.service.AgentRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AgentRoleServiceImpl implements AgentRoleService {

    private static final Logger log = LoggerFactory.getLogger(AgentRoleServiceImpl.class);

    private Map<String, AgentRoleConfig> roleStore = new ConcurrentHashMap<>();

    @Override
    public AgentRoleConfig createRole(AgentRoleConfig roleConfig) {
        log.info("[createRole] Creating role for agent: {}", roleConfig.getAgentId());
        
        roleConfig.setCreatedAt(System.currentTimeMillis());
        roleConfig.setUpdatedAt(System.currentTimeMillis());
        
        roleStore.put(roleConfig.getAgentId(), roleConfig);
        
        return roleConfig;
    }

    @Override
    public AgentRoleConfig updateRole(String agentId, AgentRoleConfig roleConfig) {
        log.info("[updateRole] Updating role for agent: {}", agentId);
        
        AgentRoleConfig existing = roleStore.get(agentId);
        if (existing == null) {
            return createRole(roleConfig);
        }
        
        roleConfig.setAgentId(agentId);
        roleConfig.setCreatedAt(existing.getCreatedAt());
        roleConfig.setUpdatedAt(System.currentTimeMillis());
        
        roleStore.put(agentId, roleConfig);
        
        return roleConfig;
    }

    @Override
    public AgentRoleConfig getRole(String agentId) {
        return roleStore.get(agentId);
    }

    @Override
    public void deleteRole(String agentId) {
        log.info("[deleteRole] Deleting role for agent: {}", agentId);
        roleStore.remove(agentId);
    }

    @Override
    public List<AgentRoleConfig> listRoles() {
        return new ArrayList<>(roleStore.values());
    }

    @Override
    public List<AgentRoleConfig> listRolesByType(String agentType) {
        return roleStore.values().stream()
            .filter(r -> agentType.equals(r.getAgentType()))
            .collect(Collectors.toList());
    }

    @Override
    public void setSystemPrompt(String agentId, String systemPrompt) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        role.setSystemPrompt(systemPrompt);
        role.setUpdatedAt(System.currentTimeMillis());
        log.info("[setSystemPrompt] Set system prompt for agent: {}", agentId);
    }

    @Override
    public String getSystemPrompt(String agentId) {
        AgentRoleConfig role = roleStore.get(agentId);
        return role != null ? role.getSystemPrompt() : null;
    }

    @Override
    public void addCapability(String agentId, String capability) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        if (!role.getCapabilities().contains(capability)) {
            role.getCapabilities().add(capability);
            role.setUpdatedAt(System.currentTimeMillis());
        }
        log.info("[addCapability] Added capability {} for agent: {}", capability, agentId);
    }

    @Override
    public void removeCapability(String agentId, String capability) {
        AgentRoleConfig role = roleStore.get(agentId);
        if (role != null && role.getCapabilities().remove(capability)) {
            role.setUpdatedAt(System.currentTimeMillis());
            log.info("[removeCapability] Removed capability {} for agent: {}", capability, agentId);
        }
    }

    @Override
    public List<String> getCapabilities(String agentId) {
        AgentRoleConfig role = roleStore.get(agentId);
        return role != null ? role.getCapabilities() : Collections.emptyList();
    }

    @Override
    public void setLLMConfig(String agentId, Map<String, Object> llmConfig) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        role.setLlmConfig(llmConfig);
        role.setUpdatedAt(System.currentTimeMillis());
        log.info("[setLLMConfig] Set LLM config for agent: {}", agentId);
    }

    @Override
    public Map<String, Object> getLLMConfig(String agentId) {
        AgentRoleConfig role = roleStore.get(agentId);
        return role != null ? role.getLlmConfig() : null;
    }

    @Override
    public void setToolConfig(String agentId, Map<String, Object> toolConfig) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        role.setToolConfig(toolConfig);
        role.setUpdatedAt(System.currentTimeMillis());
        log.info("[setToolConfig] Set tool config for agent: {}", agentId);
    }

    @Override
    public Map<String, Object> getToolConfig(String agentId) {
        AgentRoleConfig role = roleStore.get(agentId);
        return role != null ? role.getToolConfig() : null;
    }

    @Override
    public void allowTool(String agentId, String toolName) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        if (!role.getAllowedTools().contains(toolName)) {
            role.getAllowedTools().add(toolName);
            role.setUpdatedAt(System.currentTimeMillis());
        }
        log.info("[allowTool] Allowed tool {} for agent: {}", toolName, agentId);
    }

    @Override
    public void restrictTool(String agentId, String toolName) {
        AgentRoleConfig role = roleStore.get(agentId);
        if (role != null && role.getAllowedTools().remove(toolName)) {
            role.setUpdatedAt(System.currentTimeMillis());
            log.info("[restrictTool] Restricted tool {} for agent: {}", toolName, agentId);
        }
    }

    @Override
    public void restrictAction(String agentId, String action) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        if (!role.getRestrictedActions().contains(action)) {
            role.getRestrictedActions().add(action);
            role.setUpdatedAt(System.currentTimeMillis());
        }
        log.info("[restrictAction] Restricted action {} for agent: {}", action, agentId);
    }

    @Override
    public void allowAction(String agentId, String action) {
        AgentRoleConfig role = roleStore.get(agentId);
        if (role != null && role.getRestrictedActions().remove(action)) {
            role.setUpdatedAt(System.currentTimeMillis());
            log.info("[allowAction] Allowed action {} for agent: {}", action, agentId);
        }
    }

    @Override
    public void enableFunctionCalling(String agentId, boolean enabled) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        role.setFunctionCallingEnabled(enabled);
        role.setUpdatedAt(System.currentTimeMillis());
        log.info("[enableFunctionCalling] Set function calling {} for agent: {}", enabled, agentId);
    }

    @Override
    public void enableStreaming(String agentId, boolean enabled) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        role.setStreamingEnabled(enabled);
        role.setUpdatedAt(System.currentTimeMillis());
        log.info("[enableStreaming] Set streaming {} for agent: {}", enabled, agentId);
    }

    @Override
    public void setMaxTokens(String agentId, int maxTokens) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        role.setMaxTokens(maxTokens);
        role.setUpdatedAt(System.currentTimeMillis());
        log.info("[setMaxTokens] Set max tokens {} for agent: {}", maxTokens, agentId);
    }

    @Override
    public void setTemperature(String agentId, double temperature) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        role.setTemperature(temperature);
        role.setUpdatedAt(System.currentTimeMillis());
        log.info("[setTemperature] Set temperature {} for agent: {}", temperature, agentId);
    }

    @Override
    public void setKnowledgeConfig(String agentId, Map<String, Object> knowledgeConfig) {
        AgentRoleConfig role = roleStore.computeIfAbsent(agentId, id -> {
            AgentRoleConfig r = new AgentRoleConfig();
            r.setAgentId(id);
            return r;
        });
        role.setKnowledgeConfig(knowledgeConfig);
        role.setUpdatedAt(System.currentTimeMillis());
        log.info("[setKnowledgeConfig] Set knowledge config for agent: {}", agentId);
    }

    @Override
    public Map<String, Object> getKnowledgeConfig(String agentId) {
        AgentRoleConfig role = roleStore.get(agentId);
        return role != null ? role.getKnowledgeConfig() : null;
    }
}
