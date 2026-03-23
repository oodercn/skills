package net.ooder.mvp.skill.scene.agent.service.impl;

import net.ooder.mvp.skill.scene.agent.dto.AgentSessionDTO;
import net.ooder.mvp.skill.scene.agent.dto.AgentStatus;
import net.ooder.mvp.skill.scene.agent.service.AgentHeartbeatMonitor;
import net.ooder.mvp.skill.scene.agent.service.AgentSessionService;
import net.ooder.mvp.skill.scene.agent.service.AgentMessageService;
import net.ooder.mvp.skill.scene.agent.dto.MessageType;
import net.ooder.mvp.skill.scene.event.SceneStateEvent;
import net.ooder.mvp.skill.scene.event.SceneStateEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentHeartbeatMonitorImpl implements AgentHeartbeatMonitor {
    
    private static final Logger log = LoggerFactory.getLogger(AgentHeartbeatMonitorImpl.class);
    
    private static final long DEFAULT_TIMEOUT_SECONDS = 60;
    
    @Value("${agent.heartbeat.timeout:60}")
    private long timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
    
    @Value("${agent.heartbeat.check-interval:30000}")
    private long checkIntervalMs = 30000;
    
    @Autowired
    private AgentSessionService sessionService;
    
    @Autowired(required = false)
    private AgentMessageService messageService;
    
    @Autowired(required = false)
    private SceneStateEventPublisher eventPublisher;
    
    private volatile boolean monitoring = false;
    private final Map<String, Long> lastHeartbeatTimes = new ConcurrentHashMap<>();
    private final Set<String> timedOutAgents = ConcurrentHashMap.newKeySet();
    
    @PostConstruct
    public void init() {
        log.info("[AgentHeartbeatMonitor] Initialized with timeout: {}s, check interval: {}ms", 
            timeoutSeconds, checkIntervalMs);
        startMonitoring();
    }
    
    @PreDestroy
    public void destroy() {
        stopMonitoring();
    }
    
    @Override
    public void startMonitoring() {
        monitoring = true;
        log.info("[AgentHeartbeatMonitor] Monitoring started");
    }
    
    @Override
    public void stopMonitoring() {
        monitoring = false;
        log.info("[AgentHeartbeatMonitor] Monitoring stopped");
    }
    
    @Scheduled(fixedRateString = "${agent.heartbeat.check-interval:30000}")
    @Override
    public void checkHeartbeats() {
        if (!monitoring) {
            return;
        }
        
        long now = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000;
        
        List<AgentSessionDTO> sessions = sessionService.getActiveSessions();
        int activeCount = 0;
        int timedOutCount = 0;
        
        for (AgentSessionDTO session : sessions) {
            String agentId = session.getAgentId();
            long lastHeartbeat = session.getLastHeartbeat();
            
            lastHeartbeatTimes.put(agentId, lastHeartbeat);
            
            if (now - lastHeartbeat > timeoutMs) {
                if (!timedOutAgents.contains(agentId)) {
                    timedOutAgents.add(agentId);
                    timedOutCount++;
                    
                    log.warn("[checkHeartbeats] Agent {} timed out. Last heartbeat: {}ms ago", 
                        agentId, now - lastHeartbeat);
                    
                    handleAgentTimeout(session);
                }
            } else {
                if (timedOutAgents.remove(agentId)) {
                    log.info("[checkHeartbeats] Agent {} recovered", agentId);
                    handleAgentRecovery(session);
                }
                activeCount++;
            }
        }
        
        if (timedOutCount > 0 || log.isDebugEnabled()) {
            log.debug("[checkHeartbeats] Active: {}, Timed out: {}, Total: {}", 
                activeCount, timedOutAgents.size(), sessions.size());
        }
    }
    
    private void handleAgentTimeout(AgentSessionDTO session) {
        sessionService.updateStatus(session.getAgentId(), AgentStatus.OFFLINE.name());
        
        if (eventPublisher != null) {
            SceneStateEvent event = SceneStateEvent.create(
                session.getSceneGroupId(), 
                SceneStateEvent.EVENT_PARTICIPANT_LEFT
            );
            event.setParticipantId(session.getAgentId());
            event.setParticipantName(session.getAgentName());
            event.setOldState(AgentStatus.ONLINE.name());
            event.setNewState(AgentStatus.OFFLINE.name());
            eventPublisher.publishParticipantLeft(event);
        }
        
        if (messageService != null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("agentId", session.getAgentId());
            payload.put("agentName", session.getAgentName());
            payload.put("sceneGroupId", session.getSceneGroupId());
            payload.put("timeoutTime", System.currentTimeMillis());
            
            messageService.sendMessage(
                session.getAgentId(),
                "system-admin",
                session.getSceneGroupId(),
                MessageType.STATUS_UPDATE,
                "Agent 心跳超时",
                "Agent " + session.getAgentName() + " 心跳超时，已标记为离线",
                payload,
                8
            );
        }
    }
    
    private void handleAgentRecovery(AgentSessionDTO session) {
        sessionService.updateStatus(session.getAgentId(), AgentStatus.ONLINE.name());
        
        if (eventPublisher != null) {
            SceneStateEvent event = SceneStateEvent.create(
                session.getSceneGroupId(), 
                SceneStateEvent.EVENT_PARTICIPANT_JOINED
            );
            event.setParticipantId(session.getAgentId());
            event.setParticipantName(session.getAgentName());
            event.setOldState(AgentStatus.OFFLINE.name());
            event.setNewState(AgentStatus.ONLINE.name());
            eventPublisher.publishParticipantJoined(event);
        }
    }
    
    @Override
    public List<String> getTimedOutAgents() {
        return new ArrayList<>(timedOutAgents);
    }
    
    @Override
    public Map<String, Long> getAgentLastHeartbeatTimes() {
        return new HashMap<>(lastHeartbeatTimes);
    }
    
    @Override
    public boolean isAgentTimedOut(String agentId) {
        return timedOutAgents.contains(agentId);
    }
    
    @Override
    public long getTimeoutThreshold() {
        return timeoutSeconds;
    }
    
    @Override
    public void setTimeoutThreshold(long thresholdSeconds) {
        this.timeoutSeconds = thresholdSeconds;
        log.info("[AgentHeartbeatMonitor] Timeout threshold updated to {}s", thresholdSeconds);
    }
}
