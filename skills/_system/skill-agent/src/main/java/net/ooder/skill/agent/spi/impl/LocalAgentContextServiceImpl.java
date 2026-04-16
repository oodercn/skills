package net.ooder.skill.agent.spi.impl;

import net.ooder.skill.agent.spi.LocalAgentContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Deprecated
public class LocalAgentContextServiceImpl implements LocalAgentContextService {

    private static final Logger log = LoggerFactory.getLogger(LocalAgentContextServiceImpl.class);
    
    private final Map<String, AgentContext> agentContexts = new ConcurrentHashMap<>();
    private final Map<String, AgentInfo> agents = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sceneAgentIndex = new ConcurrentHashMap<>();

    @Override
    public AgentContext getAgentContext(String agentId) {
        return agentContexts.get(agentId);
    }

    @Override
    public List<AgentInfo> getAgentsByScene(String sceneGroupId) {
        Set<String> agentIds = sceneAgentIndex.getOrDefault(sceneGroupId, Collections.emptySet());
        return agentIds.stream()
            .map(agents::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<AgentInfo> getOnlineAgents(String sceneGroupId) {
        Set<String> agentIds = sceneAgentIndex.getOrDefault(sceneGroupId, Collections.emptySet());
        return agentIds.stream()
            .map(agents::get)
            .filter(Objects::nonNull)
            .filter(AgentInfo::isOnline)
            .collect(Collectors.toList());
    }

    @Override
    public void registerAgent(AgentRegistration registration) {
        String agentId = registration.getAgentId();
        
        AgentInfo info = new AgentInfo();
        info.setAgentId(agentId);
        info.setAgentName(registration.getAgentName());
        info.setSceneGroupId(registration.getSceneGroupId());
        info.setRole(registration.getRole());
        info.setOnline(true);
        info.setStatus("ACTIVE");
        
        agents.put(agentId, info);
        sceneAgentIndex.computeIfAbsent(registration.getSceneGroupId(), k -> ConcurrentHashMap.newKeySet()).add(agentId);
        
        AgentContext context = new AgentContext();
        context.setAgentId(agentId);
        context.setAgentName(registration.getAgentName());
        context.setSceneGroupId(registration.getSceneGroupId());
        context.setStatus("ACTIVE");
        context.setCapabilities(registration.getCapabilities() != null ? registration.getCapabilities() : new HashMap<>());
        context.setConfig(new HashMap<>());
        context.setRegisterTime(System.currentTimeMillis());
        context.setLastActiveTime(System.currentTimeMillis());
        
        agentContexts.put(agentId, context);
        
        log.info("[registerAgent] Registered agent: {} in scene: {}", agentId, registration.getSceneGroupId());
    }

    @Override
    public void unregisterAgent(String agentId) {
        AgentInfo info = agents.remove(agentId);
        if (info != null) {
            Set<String> sceneAgents = sceneAgentIndex.get(info.getSceneGroupId());
            if (sceneAgents != null) {
                sceneAgents.remove(agentId);
            }
            agentContexts.remove(agentId);
            log.info("[unregisterAgent] Unregistered agent: {}", agentId);
        }
    }

    @Override
    public void updateAgentStatus(String agentId, String status) {
        updateAgentStatus(agentId, status, true);
    }

    public void updateAgentStatus(String agentId, String status, boolean online) {
        AgentInfo info = agents.get(agentId);
        if (info != null) {
            info.setStatus(status);
            info.setOnline(online);
            
            AgentContext context = agentContexts.get(agentId);
            if (context != null) {
                context.setStatus(status);
                context.setLastActiveTime(System.currentTimeMillis());
            }
            
            log.debug("[updateAgentStatus] Updated agent: {} to status: {}, online: {}", agentId, status, online);
        }
    }
}
