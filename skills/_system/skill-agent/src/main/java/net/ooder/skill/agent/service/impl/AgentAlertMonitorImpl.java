package net.ooder.skill.agent.service.impl;

import net.ooder.skill.agent.dto.AgentAlertConfigDTO;
import net.ooder.skill.agent.dto.AgentDTO;
import net.ooder.skill.agent.dto.AgentMetricsDTO;
import net.ooder.skill.agent.service.AgentAlertMonitor;
import net.ooder.skill.agent.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AgentAlertMonitorImpl implements AgentAlertMonitor {

    private static final Logger log = LoggerFactory.getLogger(AgentAlertMonitorImpl.class);

    @Autowired
    private AgentService agentService;

    private boolean monitoring = false;
    private Map<String, List<AlertEvent>> alertHistory = new ConcurrentHashMap<>();
    private Map<String, AlertEvent> activeAlerts = new ConcurrentHashMap<>();
    private Map<String, Map<String, Double>> alertThresholds = new ConcurrentHashMap<>();

    @Override
    public void startMonitoring() {
        monitoring = true;
        log.info("[AgentAlertMonitor] Alert monitoring started");
    }

    @Override
    public void stopMonitoring() {
        monitoring = false;
        log.info("[AgentAlertMonitor] Alert monitoring stopped");
    }

    @Override
    public boolean isMonitoring() {
        return monitoring;
    }

    @Override
    public void checkAgentAlerts(String agentId) {
        if (!monitoring) {
            return;
        }

        AgentDTO agent = agentService.getAgent(agentId);
        if (agent == null) {
            return;
        }

        AgentMetricsDTO metrics = agentService.getAgentMetrics(agentId);
        if (metrics == null) {
            return;
        }

        checkMetricThreshold(agentId, "cpu_usage", metrics.getCpuUsage());
        checkMetricThreshold(agentId, "memory_usage", metrics.getMemoryUsage());
        checkMetricThreshold(agentId, "load_percentage", metrics.getLoadPercentage());
        checkMetricThreshold(agentId, "response_time", metrics.getAvgResponseTime());
        checkMetricThreshold(agentId, "error_rate", 
            metrics.getTotalRequests() > 0 ? 
            (double) metrics.getFailedRequests() / metrics.getTotalRequests() * 100 : 0);
    }

    private void checkMetricThreshold(String agentId, String metricType, double value) {
        Map<String, Double> thresholds = alertThresholds.get(agentId);
        if (thresholds == null) {
            return;
        }

        Double threshold = thresholds.get(metricType);
        if (threshold == null) {
            return;
        }

        if (value > threshold) {
            AlertEvent alert = new AlertEvent();
            alert.setAlertId(UUID.randomUUID().toString());
            alert.setAgentId(agentId);
            alert.setAlertType(metricType);
            alert.setSeverity(determineSeverity(value, threshold));
            alert.setMessage(String.format("%s exceeded threshold: %.2f > %.2f", 
                metricType, value, threshold));
            alert.setValue(value);
            alert.setThreshold(threshold);
            alert.setTimestamp(System.currentTimeMillis());
            alert.setAcknowledged(false);

            activeAlerts.put(alert.getAlertId(), alert);
            alertHistory.computeIfAbsent(agentId, k -> new ArrayList<>()).add(alert);

            log.warn("[AgentAlertMonitor] Alert triggered: {} for agent {}", 
                alert.getMessage(), agentId);
        }
    }

    private String determineSeverity(double value, double threshold) {
        double ratio = value / threshold;
        if (ratio >= 2.0) {
            return "CRITICAL";
        } else if (ratio >= 1.5) {
            return "HIGH";
        } else if (ratio >= 1.2) {
            return "MEDIUM";
        }
        return "LOW";
    }

    @Override
    public void checkAllAgents() {
        if (!monitoring) {
            return;
        }

        List<AgentDTO> agents = agentService.listAgents(1, 1000).getList();
        for (AgentDTO agent : agents) {
            checkAgentAlerts(agent.getAgentId());
        }
    }

    @Override
    public List<AlertEvent> getActiveAlerts() {
        return new ArrayList<>(activeAlerts.values());
    }

    @Override
    public List<AlertEvent> getAlertHistory(String agentId, long startTime, long endTime) {
        List<AlertEvent> history = alertHistory.get(agentId);
        if (history == null) {
            return Collections.emptyList();
        }

        List<AlertEvent> filtered = new ArrayList<>();
        for (AlertEvent event : history) {
            if (event.getTimestamp() >= startTime && event.getTimestamp() <= endTime) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    @Override
    public void acknowledgeAlert(String alertId) {
        AlertEvent alert = activeAlerts.get(alertId);
        if (alert != null) {
            alert.setAcknowledged(true);
            activeAlerts.remove(alertId);
            log.info("[AgentAlertMonitor] Alert acknowledged: {}", alertId);
        }
    }

    @Override
    public void configureAlertThreshold(String agentId, String metricType, double threshold) {
        alertThresholds.computeIfAbsent(agentId, k -> new HashMap<>())
            .put(metricType, threshold);
        log.info("[AgentAlertMonitor] Threshold configured: agent={}, metric={}, threshold={}", 
            agentId, metricType, threshold);
    }

    @Scheduled(fixedRate = 60000)
    public void scheduledCheck() {
        if (monitoring) {
            checkAllAgents();
        }
    }
}
