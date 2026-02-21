package net.ooder.skill.monitor.service.impl;

import net.ooder.skill.monitor.dto.*;
import net.ooder.skill.monitor.service.MonitorService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MonitorServiceImpl implements MonitorService {

    private final Map<String, MetricData> metrics = new ConcurrentHashMap<>();
    private final Map<String, AlertRule> alerts = new ConcurrentHashMap<>();
    private final Map<String, List<ObservationData>> observationHistory = new ConcurrentHashMap<>();

    public MonitorServiceImpl() {
        initDefaultMetrics();
    }

    private void initDefaultMetrics() {
        addMetric("cpu.usage", "gauge", Math.random() * 100, "percent");
        addMetric("memory.usage", "gauge", Math.random() * 100, "percent");
        addMetric("disk.usage", "gauge", Math.random() * 100, "percent");
        addMetric("network.in.bytes", "counter", System.currentTimeMillis() % 1000000, "bytes");
        addMetric("network.out.bytes", "counter", System.currentTimeMillis() % 1000000, "bytes");
    }

    private void addMetric(String name, String type, double value, String unit) {
        MetricData metric = new MetricData();
        metric.setName(name);
        metric.setType(type);
        metric.setValue(value);
        metric.setUnit(unit);
        metric.setTags(new HashMap<>());
        metrics.put(name, metric);
    }

    @Override
    public List<MetricData> getAllMetrics() {
        return new ArrayList<>(metrics.values());
    }

    @Override
    public MetricData getMetric(String name) {
        return metrics.get(name);
    }

    @Override
    public List<AlertRule> listAlerts() {
        return new ArrayList<>(alerts.values());
    }

    @Override
    public AlertRule createAlert(AlertRule alert) {
        if (alert.getAlertId() == null || alert.getAlertId().isEmpty()) {
            alert.setAlertId("alert-" + UUID.randomUUID().toString().substring(0, 8));
        }
        alert.setCreatedAt(System.currentTimeMillis());
        alerts.put(alert.getAlertId(), alert);
        return alert;
    }

    @Override
    public boolean acknowledgeAlert(String alertId) {
        AlertRule alert = alerts.get(alertId);
        if (alert != null) {
            alert.setStatus("acknowledged");
            return true;
        }
        return false;
    }

    @Override
    public PageResult<LogEntry> queryLogs(String level, String source, long start, long end, int page, int size) {
        List<LogEntry> allLogs = generateMockLogs(level, source, start, end);
        int startIdx = page * size;
        int endIdx = Math.min(startIdx + size, allLogs.size());
        List<LogEntry> pageItems = startIdx < allLogs.size() ? allLogs.subList(startIdx, endIdx) : new ArrayList<>();
        return new PageResult<>(pageItems, page, size, allLogs.size());
    }

    private List<LogEntry> generateMockLogs(String level, String source, long start, long end) {
        List<LogEntry> logs = new ArrayList<>();
        String[] levels = {"INFO", "WARN", "ERROR", "DEBUG"};
        String[] sources = {"skill-security", "skill-network", "skill-hosting", "skill-monitor"};
        Random random = new Random();
        
        for (int i = 0; i < 20; i++) {
            LogEntry entry = new LogEntry();
            entry.setLogId("log-" + UUID.randomUUID().toString().substring(0, 8));
            String entryLevel = levels[random.nextInt(levels.length)];
            if (level != null && !level.isEmpty() && !level.equals(entryLevel)) {
                continue;
            }
            entry.setLevel(entryLevel);
            String entrySource = sources[random.nextInt(sources.length)];
            if (source != null && !source.isEmpty() && !source.equals(entrySource)) {
                continue;
            }
            entry.setSource(entrySource);
            entry.setMessage("Sample log message from " + entrySource);
            entry.setLogger("net.ooder.skill." + entrySource.replace("-", "."));
            entry.setThread("main");
            entry.setTimestamp(start + (long) (random.nextDouble() * (end - start)));
            logs.add(entry);
        }
        return logs;
    }

    @Override
    public ObservationData observe(String targetId) {
        ObservationData data = new ObservationData();
        data.setTargetId(targetId);
        data.setTargetType("node");
        data.setStatus("healthy");
        
        Map<String, Object> metricsMap = new HashMap<>();
        metricsMap.put("cpu", Math.random() * 100);
        metricsMap.put("memory", Math.random() * 100);
        metricsMap.put("disk", Math.random() * 100);
        data.setMetrics(metricsMap);
        
        Map<String, String> labels = new HashMap<>();
        labels.put("env", "production");
        labels.put("region", "default");
        data.setLabels(labels);
        
        observationHistory.computeIfAbsent(targetId, k -> new ArrayList<>()).add(data);
        
        return data;
    }

    @Override
    public List<ObservationData> getHistory(String targetId, long start, long end) {
        List<ObservationData> history = observationHistory.get(targetId);
        if (history == null) {
            return new ArrayList<>();
        }
        List<ObservationData> filtered = new ArrayList<>();
        for (ObservationData data : history) {
            if (data.getObservedAt() >= start && data.getObservedAt() <= end) {
                filtered.add(data);
            }
        }
        return filtered;
    }
}
