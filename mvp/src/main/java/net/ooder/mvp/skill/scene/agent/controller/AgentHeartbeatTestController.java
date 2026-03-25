package net.ooder.mvp.skill.scene.agent.controller;

import net.ooder.mvp.skill.scene.agent.config.AgentHeartbeatConfig;
import net.ooder.mvp.skill.scene.agent.dto.AgentSessionDTO;
import net.ooder.mvp.skill.scene.agent.service.AgentSessionService;
import net.ooder.mvp.skill.scene.model.ResultModel;
import net.ooder.sdk.service.heartbeat.EnhancedHeartbeatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent/heartbeat")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AgentHeartbeatTestController {

    @Autowired
    private AgentHeartbeatConfig heartbeatConfig;

    @Autowired
    private AgentSessionService sessionService;

    @GetMapping("/stats/{agentId}")
    public ResultModel<Map<String, Object>> getHeartbeatStats(@PathVariable String agentId) {
        Map<String, Object> result = new HashMap<>();
        
        EnhancedHeartbeatService.HeartbeatStats stats = heartbeatConfig.getAgentHeartbeatStats(agentId);
        if (stats != null) {
            result.put("agentId", agentId);
            result.put("totalHeartbeats", stats.getTotalHeartbeats());
            result.put("successfulHeartbeats", stats.getSuccessfulHeartbeats());
            result.put("failedHeartbeats", stats.getFailedHeartbeats());
            result.put("consecutiveMisses", stats.getConsecutiveMisses());
            result.put("lastHeartbeatTime", stats.getLastHeartbeatTime());
            result.put("status", stats.getStatus().name());
            result.put("successRate", stats.getSuccessRate());
        } else {
            result.put("agentId", agentId);
            result.put("error", "Agent not found in heartbeat service");
        }
        
        return ResultModel.success(result);
    }

    @GetMapping("/status/{agentId}")
    public ResultModel<Map<String, Object>> getAgentDeviceStatus(@PathVariable String agentId) {
        Map<String, Object> result = new HashMap<>();
        
        EnhancedHeartbeatService.DeviceStatus status = heartbeatConfig.getAgentDeviceStatus(agentId);
        AgentSessionDTO session = sessionService.getSession(agentId);
        
        result.put("agentId", agentId);
        result.put("deviceStatus", status.name());
        result.put("deviceStatusCode", status.getCode());
        result.put("deviceStatusDescription", status.getDescription());
        
        if (session != null) {
            result.put("sessionStatus", session.getStatus());
            result.put("lastHeartbeat", session.getLastHeartbeat());
            result.put("heartbeatAge", System.currentTimeMillis() - session.getLastHeartbeat());
        }
        
        return ResultModel.success(result);
    }

    @GetMapping("/all/stats")
    public ResultModel<Map<String, Object>> getAllAgentsStats() {
        Map<String, Object> result = new HashMap<>();
        
        List<AgentSessionDTO> sessions = sessionService.getActiveSessions();
        int online = 0, offline = 0, degraded = 0, unknown = 0;
        
        for (AgentSessionDTO session : sessions) {
            EnhancedHeartbeatService.DeviceStatus status = 
                heartbeatConfig.getAgentDeviceStatus(session.getAgentId());
            
            switch (status) {
                case ONLINE: online++; break;
                case OFFLINE: offline++; break;
                case DEGRADED: degraded++; break;
                default: unknown++; break;
            }
        }
        
        result.put("total", sessions.size());
        result.put("online", online);
        result.put("offline", offline);
        result.put("degraded", degraded);
        result.put("unknown", unknown);
        
        return ResultModel.success(result);
    }

    @PostMapping("/trigger/{agentId}")
    public ResultModel<Map<String, Object>> triggerHeartbeat(@PathVariable String agentId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            AgentSessionDTO session = sessionService.getSession(agentId);
            if (session == null) {
                return ResultModel.error("Agent not found: " + agentId);
            }
            
            sessionService.heartbeat(agentId);
            
            result.put("agentId", agentId);
            result.put("triggered", true);
            result.put("timestamp", System.currentTimeMillis());
            result.put("message", "Heartbeat triggered successfully");
            
            return ResultModel.success(result);
        } catch (Exception e) {
            result.put("agentId", agentId);
            result.put("triggered", false);
            result.put("error", e.getMessage());
            return ResultModel.error("Failed to trigger heartbeat: " + e.getMessage());
        }
    }
}
