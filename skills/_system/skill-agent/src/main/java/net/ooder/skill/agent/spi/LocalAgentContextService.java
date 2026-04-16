package net.ooder.skill.agent.spi;

import java.util.List;
import java.util.Map;

@Deprecated
public interface LocalAgentContextService {

    AgentContext getAgentContext(String agentId);
    
    List<AgentInfo> getAgentsByScene(String sceneGroupId);
    
    List<AgentInfo> getOnlineAgents(String sceneGroupId);
    
    void registerAgent(AgentRegistration registration);
    
    void updateAgentStatus(String agentId, String status);

    default void updateAgentStatus(String agentId, String status, boolean online) {
        updateAgentStatus(agentId, status);
    }

    void unregisterAgent(String agentId);
    
    class AgentContext {
        private String agentId;
        private String agentName;
        private String sceneGroupId;
        private String status;
        private Map<String, Object> capabilities;
        private Map<String, Object> config;
        private long registerTime;
        private long lastActiveTime;
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Map<String, Object> getCapabilities() { return capabilities; }
        public void setCapabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
        public long getRegisterTime() { return registerTime; }
        public void setRegisterTime(long registerTime) { this.registerTime = registerTime; }
        public long getLastActiveTime() { return lastActiveTime; }
        public void setLastActiveTime(long lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    }
    
    class AgentInfo {
        private String agentId;
        private String agentName;
        private String sceneGroupId;
        private String status;
        private String role;
        private boolean online;
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }
    }
    
    class AgentRegistration {
        private String agentId;
        private String agentName;
        private String sceneGroupId;
        private String role;
        private Map<String, Object> capabilities;
        
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        public String getAgentName() { return agentName; }
        public void setAgentName(String agentName) { this.agentName = agentName; }
        public String getSceneGroupId() { return sceneGroupId; }
        public void setSceneGroupId(String sceneGroupId) { this.sceneGroupId = sceneGroupId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Map<String, Object> getCapabilities() { return capabilities; }
        public void setCapabilities(Map<String, Object> capabilities) { this.capabilities = capabilities; }
    }
}
