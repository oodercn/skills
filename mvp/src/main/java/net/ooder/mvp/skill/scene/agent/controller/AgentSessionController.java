package net.ooder.mvp.skill.scene.agent.controller;

import net.ooder.mvp.skill.scene.agent.dto.AgentSessionDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentRegistrationDTO;
import net.ooder.mvp.skill.scene.agent.service.AgentSessionService;
import net.ooder.mvp.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent-sessions")
public class AgentSessionController {

    private static final Logger log = LoggerFactory.getLogger(AgentSessionController.class);

    @Autowired
    private AgentSessionService sessionService;

    @PostMapping("/register")
    public ResultModel<AgentSessionDTO> register(@RequestBody AgentRegistrationDTO registration) {
        log.info("[register] Registering agent: {}", registration.getAgentId());
        
        try {
            AgentSessionDTO session = sessionService.register(registration);
            session.setSecretKey(null);
            return ResultModel.success(session);
        } catch (Exception e) {
            log.error("[register] Failed to register agent: {}", e.getMessage());
            return ResultModel.error(500, "注册失败: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResultModel<AgentSessionDTO> login(
            @RequestParam String agentId,
            @RequestParam String secretKey) {
        log.info("[login] Agent login: {}", agentId);
        
        try {
            AgentSessionDTO session = sessionService.login(agentId, secretKey);
            if (session == null) {
                return ResultModel.error(401, "认证失败: 无效的 agentId 或 secretKey");
            }
            session.setSecretKey(null);
            return ResultModel.success(session);
        } catch (Exception e) {
            log.error("[login] Failed to login agent: {}", e.getMessage());
            return ResultModel.error(500, "登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResultModel<Boolean> logout(@RequestParam String agentId) {
        log.info("[logout] Agent logout: {}", agentId);
        
        try {
            sessionService.logout(agentId);
            return ResultModel.success(true);
        } catch (Exception e) {
            log.error("[logout] Failed to logout agent: {}", e.getMessage());
            return ResultModel.error(500, "登出失败: " + e.getMessage());
        }
    }

    @PostMapping("/{agentId}/heartbeat")
    public ResultModel<Boolean> heartbeat(@PathVariable String agentId) {
        log.debug("[heartbeat] Heartbeat from: {}", agentId);
        
        try {
            sessionService.heartbeat(agentId);
            return ResultModel.success(true);
        } catch (Exception e) {
            log.error("[heartbeat] Failed: {}", e.getMessage());
            return ResultModel.error(500, "心跳失败: " + e.getMessage());
        }
    }

    @PostMapping("/{agentId}/status")
    public ResultModel<Boolean> updateStatus(
            @PathVariable String agentId,
            @RequestParam String status) {
        log.info("[updateStatus] Agent {} status: {}", agentId, status);
        
        try {
            sessionService.updateStatus(agentId, status);
            return ResultModel.success(true);
        } catch (Exception e) {
            log.error("[updateStatus] Failed: {}", e.getMessage());
            return ResultModel.error(500, "状态更新失败: " + e.getMessage());
        }
    }

    @GetMapping("/{agentId}/session")
    public ResultModel<AgentSessionDTO> getSession(@PathVariable String agentId) {
        try {
            AgentSessionDTO session = sessionService.getSession(agentId);
            if (session == null) {
                return ResultModel.notFound("会话不存在或已过期");
            }
            session.setSecretKey(null);
            return ResultModel.success(session);
        } catch (Exception e) {
            log.error("[getSession] Failed: {}", e.getMessage());
            return ResultModel.error(500, "获取会话失败: " + e.getMessage());
        }
    }

    @GetMapping("/session/validate")
    public ResultModel<Map<String, Object>> validateToken(@RequestParam String sessionToken) {
        try {
            boolean valid = sessionService.isValidToken(sessionToken);
            Map<String, Object> result = new HashMap<>();
            result.put("valid", valid);
            
            if (valid) {
                AgentSessionDTO session = sessionService.getSessionByToken(sessionToken);
                if (session != null) {
                    result.put("agentId", session.getAgentId());
                    result.put("agentName", session.getAgentName());
                    result.put("agentType", session.getAgentType());
                    result.put("status", session.getStatus());
                }
            }
            
            return ResultModel.success(result);
        } catch (Exception e) {
            log.error("[validateToken] Failed: {}", e.getMessage());
            return ResultModel.error(500, "验证失败: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResultModel<List<AgentSessionDTO>> getActiveSessions() {
        try {
            List<AgentSessionDTO> sessions = sessionService.getActiveSessions();
            for (AgentSessionDTO session : sessions) {
                session.setSecretKey(null);
            }
            return ResultModel.success(sessions);
        } catch (Exception e) {
            log.error("[getActiveSessions] Failed: {}", e.getMessage());
            return ResultModel.error(500, "获取活动会话失败: " + e.getMessage());
        }
    }

    @GetMapping("/scene/{sceneGroupId}/sessions")
    public ResultModel<List<AgentSessionDTO>> getSessionsByScene(@PathVariable String sceneGroupId) {
        try {
            List<AgentSessionDTO> sessions = sessionService.getSessionsByScene(sceneGroupId);
            for (AgentSessionDTO session : sessions) {
                session.setSecretKey(null);
            }
            return ResultModel.success(sessions);
        } catch (Exception e) {
            log.error("[getSessionsByScene] Failed: {}", e.getMessage());
            return ResultModel.error(500, "获取场景会话失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResultModel<Map<String, Object>> getStats() {
        try {
            List<AgentSessionDTO> sessions = sessionService.getActiveSessions();
            
            int online = 0;
            int busy = 0;
            int idle = 0;
            int offline = 0;
            
            for (AgentSessionDTO session : sessions) {
                switch (session.getStatus()) {
                    case "ONLINE": online++; break;
                    case "BUSY": busy++; break;
                    case "IDLE": idle++; break;
                    case "OFFLINE": offline++; break;
                }
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", sessions.size());
            stats.put("online", online);
            stats.put("busy", busy);
            stats.put("idle", idle);
            stats.put("offline", offline);
            stats.put("sessionTimeout", sessionService.getSessionTimeout());
            
            return ResultModel.success(stats);
        } catch (Exception e) {
            log.error("[getStats] Failed: {}", e.getMessage());
            return ResultModel.error(500, "获取统计失败: " + e.getMessage());
        }
    }
}
