package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.dto.AgentSessionDTO;
import net.ooder.skill.agent.service.AgentHeartbeatMonitor;
import net.ooder.skill.agent.service.AgentSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentHeartbeatMonitorImpl implements AgentHeartbeatMonitor {

    private static final Logger log = LoggerFactory.getLogger(AgentHeartbeatMonitorImpl.class);

    @Autowired
    private AgentSessionService sessionService;

    private boolean running = false;
    private int offlineThreshold = 60;
    private int checkInterval = 30;
    private Map<String, HeartbeatStatus> statusMap = new ConcurrentHashMap<>();
    private Map<String, HeartbeatConfig> configMap = new ConcurrentHashMap<>();
    private List<HeartbeatListener> listeners = new ArrayList<>();

    @Override
    public void start() {
        running = true;
        log.info("[AgentHeartbeatMonitor] Heartbeat monitor started");
    }

    @Override
    public void stop() {
        running = false;
        log.info("[AgentHeartbeatMonitor] Heartbeat monitor stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void registerAgent(String agentId, HeartbeatConfig config) {
        configMap.put(agentId, config);
        
        HeartbeatStatus status = new HeartbeatStatus();
        status.setAgentId(agentId);
        status.setOnline(true);
        status.setMissedCount(0);
        status.setStatus("ONLINE");
        statusMap.put(agentId, status);
        
        log.info("[registerAgent] Agent registered: {}", agentId);
    }

    @Override
    public void unregisterAgent(String agentId) {
        configMap.remove(agentId);
        statusMap.remove(agentId);
        log.info("[unregisterAgent] Agent unregistered: {}", agentId);
    }

    @Override
    public void updateHeartbeat(String agentId) {
        HeartbeatStatus status = statusMap.get(agentId);
        if (status != null) {
            status.setLastHeartbeat(System.currentTimeMillis());
            status.setCurrentTime(System.currentTimeMillis());
            status.setOnline(true);
            status.setMissedCount(0);
            status.setStatus("ONLINE");
        }
    }

    @Override
    public HeartbeatStatus getStatus(String agentId) {
        return statusMap.get(agentId);
    }

    @Override
    public List<HeartbeatStatus> getAllStatuses() {
        return new ArrayList<>(statusMap.values());
    }

    @Override
    public List<String> getOfflineAgents() {
        List<String> offline = new ArrayList<>();
        for (HeartbeatStatus status : statusMap.values()) {
            if (!status.isOnline()) {
                offline.add(status.getAgentId());
            }
        }
        return offline;
    }

    @Override
    public void setOfflineThreshold(int seconds) {
        this.offlineThreshold = seconds;
        log.info("[setOfflineThreshold] Offline threshold set to {} seconds", seconds);
    }

    @Override
    public void setCheckInterval(int seconds) {
        this.checkInterval = seconds;
        log.info("[setCheckInterval] Check interval set to {} seconds", seconds);
    }

    @Override
    public void addHeartbeatListener(HeartbeatListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeHeartbeatListener(HeartbeatListener listener) {
        listeners.remove(listener);
    }

    @Scheduled(fixedRateString = "${agent.heartbeat.check-interval:30000}")
    public void checkHeartbeats() {
        if (!running) {
            return;
        }

        long now = System.currentTimeMillis();
        long thresholdMs = offlineThreshold * 1000L;

        for (HeartbeatStatus status : statusMap.values()) {
            String agentId = status.getAgentId();
            long lastHeartbeat = status.getLastHeartbeat();
            
            if (lastHeartbeat == 0) {
                lastHeartbeat = now;
                status.setLastHeartbeat(lastHeartbeat);
            }
            
            long elapsed = now - lastHeartbeat;
            status.setCurrentTime(now);

            if (elapsed > thresholdMs) {
                if (status.isOnline()) {
                    status.setOnline(false);
                    status.setStatus("OFFLINE");
                    notifyAgentOffline(agentId);
                }
            } else {
                if (!status.isOnline()) {
                    status.setOnline(true);
                    status.setStatus("ONLINE");
                    notifyAgentOnline(agentId);
                }
            }

            int missedCount = (int) (elapsed / (checkInterval * 1000L));
            if (missedCount > status.getMissedCount()) {
                status.setMissedCount(missedCount);
                notifyHeartbeatMissed(agentId, missedCount);
            }
        }
    }

    private void notifyHeartbeatMissed(String agentId, int missedCount) {
        for (HeartbeatListener listener : listeners) {
            try {
                listener.onHeartbeatMissed(agentId, missedCount);
            } catch (Exception e) {
                log.error("[notifyHeartbeatMissed] Listener error: {}", e.getMessage());
            }
        }
    }

    private void notifyAgentOffline(String agentId) {
        log.warn("[notifyAgentOffline] Agent went offline: {}", agentId);
        for (HeartbeatListener listener : listeners) {
            try {
                listener.onAgentOffline(agentId);
            } catch (Exception e) {
                log.error("[notifyAgentOffline] Listener error: {}", e.getMessage());
            }
        }
    }

    private void notifyAgentOnline(String agentId) {
        log.info("[notifyAgentOnline] Agent came online: {}", agentId);
        for (HeartbeatListener listener : listeners) {
            try {
                listener.onAgentOnline(agentId);
            } catch (Exception e) {
                log.error("[notifyAgentOnline] Listener error: {}", e.getMessage());
            }
        }
    }
}
