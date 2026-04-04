package net.ooder.skill.agent.config;

import net.ooder.sdk.service.heartbeat.EnhancedHeartbeatService;
import net.ooder.sdk.service.heartbeat.HeartbeatConfig;
import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.agent.AgentFactory;
import net.ooder.sdk.core.agent.factory.AgentFactoryImpl;
import net.ooder.skill.agent.dto.AgentSessionDTO;
import net.ooder.skill.agent.service.AgentSessionService;
import net.ooder.skill.agent.service.AgentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;

@Configuration
@EnableScheduling
public class AgentHeartbeatConfig {
    
    private static final Logger log = LoggerFactory.getLogger(AgentHeartbeatConfig.class);
    
    @Value("${agent.heartbeat.interval:30000}")
    private int heartbeatInterval;
    
    @Value("${agent.heartbeat.timeout:60000}")
    private int heartbeatTimeout;
    
    @Value("${agent.heartbeat.offline-threshold:3}")
    private int offlineThreshold;
    
    @Autowired
    private AgentSessionService sessionService;
    
    @Autowired
    private AgentService agentService;
    
    private EnhancedHeartbeatService heartbeatService;
    private AgentFactory agentFactory;
    
    @PostConstruct
    public void init() {
        log.info("[AgentHeartbeatConfig] Initializing heartbeat service with interval: {}ms, timeout: {}ms", 
            heartbeatInterval, heartbeatTimeout);
        
        agentFactory = new AgentFactoryImpl();
        heartbeatService = new EnhancedHeartbeatService(agentFactory);
        
        registerExistingAgents();
        
        log.info("[AgentHeartbeatConfig] Heartbeat service initialized successfully");
    }
    
    private void registerExistingAgents() {
        List<AgentSessionDTO> sessions = sessionService.getActiveSessions();
        
        for (AgentSessionDTO session : sessions) {
            HeartbeatConfig config = createHeartbeatConfig(session.getAgentType());
            heartbeatService.registerAgent(session.getAgentId(), config);
            heartbeatService.startHeartbeat(session.getAgentId());
            
            log.info("[AgentHeartbeatConfig] Registered and started heartbeat for agent: {}", 
                session.getAgentId());
        }
        
        log.info("[AgentHeartbeatConfig] Registered {} agents for heartbeat monitoring", sessions.size());
    }
    
    private HeartbeatConfig createHeartbeatConfig(String agentType) {
        HeartbeatConfig.DeviceType deviceType = determineDeviceType(agentType);
        HeartbeatConfig config = new HeartbeatConfig(deviceType);
        
        config.setNormalInterval(heartbeatInterval);
        config.setOfflineThreshold(offlineThreshold);
        config.setTimeout(heartbeatTimeout);
        
        return config;
    }
    
    private HeartbeatConfig.DeviceType determineDeviceType(String agentType) {
        if (agentType == null) {
            return HeartbeatConfig.DeviceType.FIXED;
        }
        
        switch (agentType.toUpperCase()) {
            case "LLM":
            case "WORKER":
            case "SUPER_AGENT":
                return HeartbeatConfig.DeviceType.FIXED;
            case "DEVICE":
            case "MOBILE":
                return HeartbeatConfig.DeviceType.MOBILE;
            case "SENSOR":
            case "IOT":
                return HeartbeatConfig.DeviceType.BATTERY;
            default:
                return HeartbeatConfig.DeviceType.FIXED;
        }
    }
    
    @Scheduled(fixedRateString = "${agent.heartbeat.check-interval:30000}")
    public void maintainHeartbeats() {
        if (heartbeatService == null) {
            return;
        }
        
        List<AgentSessionDTO> currentSessions = sessionService.getActiveSessions();
        
        for (AgentSessionDTO session : currentSessions) {
            String agentId = session.getAgentId();
            
            if (!isAgentRegistered(agentId)) {
                HeartbeatConfig config = createHeartbeatConfig(session.getAgentType());
                heartbeatService.registerAgent(agentId, config);
                heartbeatService.startHeartbeat(agentId);
                log.debug("[maintainHeartbeats] Registered new agent: {}", agentId);
            }
        }
    }
    
    private boolean isAgentRegistered(String agentId) {
        try {
            EnhancedHeartbeatService.HeartbeatStats stats = heartbeatService.getHeartbeatStats(agentId);
            return stats != null;
        } catch (Exception e) {
            log.debug("[isAgentRegistered] Agent {} not registered: {}", agentId, e.getMessage());
            return false;
        }
    }
    
    public void registerNewAgent(String agentId, String agentType) {
        if (heartbeatService != null && !isAgentRegistered(agentId)) {
            HeartbeatConfig config = createHeartbeatConfig(agentType);
            heartbeatService.registerAgent(agentId, config);
            heartbeatService.startHeartbeat(agentId);
            log.info("[registerNewAgent] Registered and started heartbeat for new agent: {}", agentId);
        }
    }
    
    public void unregisterAgent(String agentId) {
        if (heartbeatService != null) {
            heartbeatService.unregisterAgent(agentId);
            log.info("[unregisterAgent] Unregistered agent: {}", agentId);
        }
    }
    
    public EnhancedHeartbeatService.HeartbeatStats getAgentHeartbeatStats(String agentId) {
        if (heartbeatService != null) {
            return heartbeatService.getHeartbeatStats(agentId);
        }
        return null;
    }
    
    public EnhancedHeartbeatService.DeviceStatus getAgentDeviceStatus(String agentId) {
        if (heartbeatService != null) {
            return heartbeatService.getDeviceStatus(agentId);
        }
        return EnhancedHeartbeatService.DeviceStatus.UNKNOWN;
    }
    
    @PreDestroy
    public void destroy() {
        if (heartbeatService != null) {
            log.info("[AgentHeartbeatConfig] Shutting down heartbeat service");
            heartbeatService.shutdown();
        }
    }
}
