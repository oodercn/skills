package net.ooder.skill.scene.agent.service.impl;

import net.ooder.skill.scene.agent.dto.AgentDTO;
import net.ooder.skill.scene.agent.service.AgentService;
import net.ooder.skill.scene.capability.service.CapabilityBindingService;
import net.ooder.skill.scene.dto.PageResult;
import net.ooder.skill.scene.dto.scene.ParticipantType;
import net.ooder.skill.scene.dto.scene.SceneGroupDTO;
import net.ooder.skill.scene.dto.scene.SceneParticipantDTO;
import net.ooder.skill.scene.service.SceneGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentServiceImpl implements AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentServiceImpl.class);

    @Autowired
    private SceneGroupService sceneGroupService;

    @Autowired
    private CapabilityBindingService bindingService;

    private final Map<String, AgentDTO> agentCache = new ConcurrentHashMap<>();
    private final Map<String, Long> heartbeatCache = new ConcurrentHashMap<>();

    @Override
    public PageResult<AgentDTO> listAgents(int pageNum, int pageSize) {
        List<AgentDTO> allAgents = collectAllAgents();
        
        int total = allAgents.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<AgentDTO> pagedList = start < total ? allAgents.subList(start, end) : new ArrayList<>();
        
        PageResult<AgentDTO> result = new PageResult<>();
        result.setList(pagedList);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    @Override
    public AgentDTO getAgent(String agentId) {
        AgentDTO cached = agentCache.get(agentId);
        if (cached != null) {
            return cached;
        }
        
        List<AgentDTO> allAgents = collectAllAgents();
        for (AgentDTO agent : allAgents) {
            if (agentId.equals(agent.getAgentId())) {
                return agent;
            }
        }
        return null;
    }

    @Override
    public List<AgentDTO> searchAgents(String keyword) {
        List<AgentDTO> allAgents = collectAllAgents();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allAgents;
        }
        
        String lowerKeyword = keyword.toLowerCase();
        List<AgentDTO> result = new ArrayList<>();
        for (AgentDTO agent : allAgents) {
            if ((agent.getAgentId() != null && agent.getAgentId().toLowerCase().contains(lowerKeyword)) ||
                (agent.getAgentName() != null && agent.getAgentName().toLowerCase().contains(lowerKeyword)) ||
                (agent.getIpAddress() != null && agent.getIpAddress().toLowerCase().contains(lowerKeyword))) {
                result.add(agent);
            }
        }
        return result;
    }

    @Override
    public List<AgentDTO> listByType(String agentType) {
        List<AgentDTO> allAgents = collectAllAgents();
        List<AgentDTO> result = new ArrayList<>();
        for (AgentDTO agent : allAgents) {
            if (agentType.equals(agent.getAgentType())) {
                result.add(agent);
            }
        }
        return result;
    }

    @Override
    public List<AgentDTO> listByStatus(String status) {
        List<AgentDTO> allAgents = collectAllAgents();
        List<AgentDTO> result = new ArrayList<>();
        for (AgentDTO agent : allAgents) {
            if (status.equalsIgnoreCase(agent.getStatus())) {
                result.add(agent);
            }
        }
        return result;
    }

    @Override
    public boolean sendHeartbeat(String agentId) {
        heartbeatCache.put(agentId, System.currentTimeMillis());
        
        AgentDTO agent = getAgent(agentId);
        if (agent != null) {
            agent.setLastHeartbeat(System.currentTimeMillis());
            agent.setStatus("online");
            agentCache.put(agentId, agent);
            log.info("Agent heartbeat received: {}", agentId);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAgentStatus(String agentId, String status) {
        AgentDTO agent = getAgent(agentId);
        if (agent != null) {
            agent.setStatus(status);
            agentCache.put(agentId, agent);
            return true;
        }
        return false;
    }

    @Override
    public int getBindingCount(String agentId) {
        return bindingService.listByAgent(agentId).size();
    }

    private List<AgentDTO> collectAllAgents() {
        List<AgentDTO> agents = new ArrayList<>();
        
        PageResult<SceneGroupDTO> groups = sceneGroupService.listAll(1, 1000);
        if (groups != null && groups.getList() != null) {
            for (SceneGroupDTO group : groups.getList()) {
                PageResult<SceneParticipantDTO> participants = 
                    sceneGroupService.listParticipants(group.getSceneGroupId(), 1, 100);
                
                if (participants != null && participants.getList() != null) {
                    for (SceneParticipantDTO p : participants.getList()) {
                        if (p.getParticipantType() == ParticipantType.AGENT || 
                            p.getParticipantType() == ParticipantType.SUPER_AGENT) {
                            AgentDTO agent = convertToAgent(p, group.getSceneGroupId());
                            agents.add(agent);
                            agentCache.put(agent.getAgentId(), agent);
                        }
                    }
                }
            }
        }
        
        if (agents.isEmpty()) {
            agents = getDefaultAgents();
        }
        
        return agents;
    }

    private AgentDTO convertToAgent(SceneParticipantDTO participant, String sceneGroupId) {
        AgentDTO agent = new AgentDTO();
        agent.setAgentId(participant.getParticipantId());
        agent.setAgentName(participant.getName() != null ? participant.getName() : participant.getParticipantId());
        agent.setAgentType(participant.getParticipantType() == ParticipantType.SUPER_AGENT ? "SUPER_AGENT" : "AGENT");
        agent.setSceneGroupId(sceneGroupId);
        agent.setRole(participant.getRole());
        agent.setRegisterTime(participant.getJoinTime());
        
        Long lastHeartbeat = heartbeatCache.get(participant.getParticipantId());
        if (lastHeartbeat != null) {
            agent.setLastHeartbeat(lastHeartbeat);
        } else {
            agent.setLastHeartbeat(participant.getLastHeartbeat() > 0 ? 
                participant.getLastHeartbeat() : System.currentTimeMillis());
        }
        
        long heartbeatAge = System.currentTimeMillis() - agent.getLastHeartbeat();
        if (heartbeatAge < 60000) {
            agent.setStatus("online");
        } else if (heartbeatAge < 300000) {
            agent.setStatus("busy");
        } else {
            agent.setStatus("offline");
        }
        
        agent.setBindingCount(getBindingCount(agent.getAgentId()));
        
        return agent;
    }

    private List<AgentDTO> getDefaultAgents() {
        List<AgentDTO> agents = new ArrayList<>();
        
        AgentDTO llmAgent = new AgentDTO();
        llmAgent.setAgentId("agent-llm-001");
        llmAgent.setAgentName("LLM Assistant");
        llmAgent.setAgentType("LLM");
        llmAgent.setStatus("online");
        llmAgent.setIpAddress("127.0.0.1");
        llmAgent.setPort(8080);
        llmAgent.setVersion("1.0.0");
        llmAgent.setRegisterTime(System.currentTimeMillis() - 86400000);
        llmAgent.setLastHeartbeat(System.currentTimeMillis());
        llmAgent.setBindingCount(getBindingCount("agent-llm-001"));
        agents.add(llmAgent);
        
        AgentDTO coordinatorAgent = new AgentDTO();
        coordinatorAgent.setAgentId("agent-coordinator-001");
        coordinatorAgent.setAgentName("Coordinator Agent");
        coordinatorAgent.setAgentType("WORKER");
        coordinatorAgent.setStatus("online");
        coordinatorAgent.setIpAddress("127.0.0.1");
        coordinatorAgent.setPort(8081);
        coordinatorAgent.setVersion("1.0.0");
        coordinatorAgent.setRegisterTime(System.currentTimeMillis() - 172800000);
        coordinatorAgent.setLastHeartbeat(System.currentTimeMillis() - 30000);
        coordinatorAgent.setBindingCount(getBindingCount("agent-coordinator-001"));
        agents.add(coordinatorAgent);
        
        AgentDTO superAgent = new AgentDTO();
        superAgent.setAgentId("super-agent-001");
        superAgent.setAgentName("Super Agent");
        superAgent.setAgentType("SUPER_AGENT");
        superAgent.setStatus("online");
        superAgent.setIpAddress("127.0.0.1");
        superAgent.setPort(8082);
        superAgent.setVersion("2.0.0");
        superAgent.setRegisterTime(System.currentTimeMillis() - 259200000);
        superAgent.setLastHeartbeat(System.currentTimeMillis() - 60000);
        superAgent.setBindingCount(getBindingCount("super-agent-001"));
        agents.add(superAgent);
        
        return agents;
    }
}
