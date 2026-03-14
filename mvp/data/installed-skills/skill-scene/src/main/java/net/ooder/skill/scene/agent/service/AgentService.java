package net.ooder.skill.scene.agent.service;

import net.ooder.skill.scene.agent.dto.AgentDTO;
import net.ooder.skill.scene.dto.PageResult;

import java.util.List;

public interface AgentService {
    
    PageResult<AgentDTO> listAgents(int pageNum, int pageSize);
    
    AgentDTO getAgent(String agentId);
    
    List<AgentDTO> searchAgents(String keyword);
    
    List<AgentDTO> listByType(String agentType);
    
    List<AgentDTO> listByStatus(String status);
    
    boolean sendHeartbeat(String agentId);
    
    boolean updateAgentStatus(String agentId, String status);
    
    int getBindingCount(String agentId);
}
