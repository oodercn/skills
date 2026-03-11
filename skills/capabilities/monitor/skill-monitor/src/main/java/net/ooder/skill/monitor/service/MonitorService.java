package net.ooder.skill.monitor.service;

import net.ooder.skill.monitor.dto.*;

import java.util.List;

public interface MonitorService {
    List<MetricData> getAllMetrics();
    MetricData getMetric(String name);
    List<AlertRule> listAlerts();
    AlertRule createAlert(AlertRule alert);
    boolean acknowledgeAlert(String alertId);
    PageResult<LogEntry> queryLogs(String level, String source, long start, long end, int page, int size);
    ObservationData observe(String targetId);
    List<ObservationData> getHistory(String targetId, long start, long end);
}
