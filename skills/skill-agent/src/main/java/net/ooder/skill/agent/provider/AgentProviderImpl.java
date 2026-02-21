package net.ooder.skill.agent.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.AgentProvider;
import net.ooder.scene.provider.model.agent.EndAgent;
import net.ooder.scene.provider.model.agent.NetworkStatusData;
import net.ooder.scene.provider.model.agent.TestCommandResult;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AgentProviderImpl implements AgentProvider {
    
    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    
    private final Map<String, EndAgent> agents = new ConcurrentHashMap<>();
    private final Map<String, Long> lastHeartbeat = new ConcurrentHashMap<>();
    
    @Override
    public String getProviderName() {
        return "skill-agent";
    }
    
    @Override
    public String getVersion() {
        return "0.7.3";
    }
    
    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
        log.info("AgentProvider initialized");
    }
    
    @Override
    public void start() {
        this.running = true;
        log.info("AgentProvider started");
    }
    
    @Override
    public void stop() {
        this.running = false;
        log.info("AgentProvider stopped");
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public Result<List<EndAgent>> getEndAgents() {
        List<EndAgent> agentList = new ArrayList<>(agents.values());
        log.debug("Retrieved {} agents", agentList.size());
        return Result.success(agentList);
    }
    
    @Override
    public Result<EndAgent> addEndAgent(Map<String, Object> agentData) {
        try {
            String agentId = (String) agentData.getOrDefault("agentId", UUID.randomUUID().toString());
            
            EndAgent agent = new EndAgent();
            agent.setAgentId(agentId);
            agent.setName((String) agentData.get("name"));
            agent.setType((String) agentData.getOrDefault("type", "generic"));
            agent.setStatus("online");
            agent.setIpAddress((String) agentData.get("ipAddress"));
            agent.setMacAddress((String) agentData.get("macAddress"));
            agent.setOsType((String) agentData.get("osType"));
            agent.setOsVersion((String) agentData.get("osVersion"));
            agent.setAgentVersion((String) agentData.getOrDefault("agentVersion", "0.7.3"));
            agent.setRegisteredAt(System.currentTimeMillis());
            agent.setLastSeen(System.currentTimeMillis());
            agent.setCapabilities((List<String>) agentData.getOrDefault("capabilities", new ArrayList<>()));
            agent.setMetadata((Map<String, Object>) agentData.getOrDefault("metadata", new HashMap<>()));
            
            agents.put(agentId, agent);
            lastHeartbeat.put(agentId, System.currentTimeMillis());
            
            log.info("Added agent: {} ({})", agent.getName(), agentId);
            return Result.success(agent);
        } catch (Exception e) {
            log.error("Failed to add agent", e);
            return Result.error("Failed to add agent: " + e.getMessage());
        }
    }
    
    @Override
    public Result<EndAgent> editEndAgent(String agentId, Map<String, Object> agentData) {
        EndAgent agent = agents.get(agentId);
        if (agent == null) {
            return Result.error("Agent not found: " + agentId);
        }
        
        try {
            if (agentData.containsKey("name")) {
                agent.setName((String) agentData.get("name"));
            }
            if (agentData.containsKey("type")) {
                agent.setType((String) agentData.get("type"));
            }
            if (agentData.containsKey("ipAddress")) {
                agent.setIpAddress((String) agentData.get("ipAddress"));
            }
            if (agentData.containsKey("macAddress")) {
                agent.setMacAddress((String) agentData.get("macAddress"));
            }
            if (agentData.containsKey("osType")) {
                agent.setOsType((String) agentData.get("osType"));
            }
            if (agentData.containsKey("osVersion")) {
                agent.setOsVersion((String) agentData.get("osVersion"));
            }
            if (agentData.containsKey("agentVersion")) {
                agent.setAgentVersion((String) agentData.get("agentVersion"));
            }
            if (agentData.containsKey("capabilities")) {
                agent.setCapabilities((List<String>) agentData.get("capabilities"));
            }
            if (agentData.containsKey("metadata")) {
                agent.setMetadata((Map<String, Object>) agentData.get("metadata"));
            }
            
            agent.setLastSeen(System.currentTimeMillis());
            agents.put(agentId, agent);
            
            log.info("Updated agent: {}", agentId);
            return Result.success(agent);
        } catch (Exception e) {
            log.error("Failed to edit agent: {}", agentId, e);
            return Result.error("Failed to edit agent: " + e.getMessage());
        }
    }
    
    @Override
    public Result<EndAgent> deleteEndAgent(String agentId) {
        EndAgent agent = agents.remove(agentId);
        lastHeartbeat.remove(agentId);
        
        if (agent == null) {
            return Result.error("Agent not found: " + agentId);
        }
        
        log.info("Deleted agent: {}", agentId);
        return Result.success(agent);
    }
    
    @Override
    public Result<EndAgent> getEndAgentDetails(String agentId) {
        EndAgent agent = agents.get(agentId);
        if (agent == null) {
            return Result.error("Agent not found: " + agentId);
        }
        return Result.success(agent);
    }
    
    @Override
    public Result<NetworkStatusData> getNetworkStatus() {
        NetworkStatusData status = new NetworkStatusData();
        
        int onlineAgents = 0;
        int offlineAgents = 0;
        long totalHeartbeats = 0;
        
        long now = System.currentTimeMillis();
        long threshold = 60000;
        
        for (Map.Entry<String, Long> entry : lastHeartbeat.entrySet()) {
            if (now - entry.getValue() < threshold) {
                onlineAgents++;
            } else {
                offlineAgents++;
            }
            totalHeartbeats += entry.getValue();
        }
        
        status.setOnlineAgents(onlineAgents);
        status.setOfflineAgents(offlineAgents);
        status.setTotalAgents(agents.size());
        status.setAverageHeartbeat(agents.isEmpty() ? 0 : totalHeartbeats / agents.size());
        status.setTimestamp(now);
        
        return Result.success(status);
    }
    
    @Override
    public Result<TestCommandResult> testCommand(Map<String, Object> commandData) {
        String agentId = (String) commandData.get("agentId");
        String command = (String) commandData.get("command");
        
        EndAgent agent = agents.get(agentId);
        if (agent == null) {
            return Result.error("Agent not found: " + agentId);
        }
        
        TestCommandResult result = new TestCommandResult();
        result.setAgentId(agentId);
        result.setCommand(command);
        result.setSuccess(true);
        result.setOutput("Mock execution: " + command);
        result.setError("");
        result.setExitCode(0);
        result.setDuration(100L);
        result.setExecutedAt(System.currentTimeMillis());
        
        log.info("Test command executed on agent {}: {}", agentId, command);
        return Result.success(result);
    }
    
    public void updateHeartbeat(String agentId) {
        if (agents.containsKey(agentId)) {
            lastHeartbeat.put(agentId, System.currentTimeMillis());
            EndAgent agent = agents.get(agentId);
            agent.setLastSeen(System.currentTimeMillis());
            agent.setStatus("online");
        }
    }
    
    public void markAgentOffline(String agentId) {
        EndAgent agent = agents.get(agentId);
        if (agent != null) {
            agent.setStatus("offline");
        }
    }
}
