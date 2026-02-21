package net.ooder.skill.monitor.controller;

import net.ooder.skill.monitor.dto.*;
import net.ooder.skill.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/metrics")
    public ResponseEntity<List<MetricData>> getAllMetrics() {
        return ResponseEntity.ok(monitorService.getAllMetrics());
    }

    @GetMapping("/metrics/{name}")
    public ResponseEntity<MetricData> getMetric(@PathVariable String name) {
        MetricData metric = monitorService.getMetric(name);
        if (metric == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(metric);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertRule>> listAlerts() {
        return ResponseEntity.ok(monitorService.listAlerts());
    }

    @PostMapping("/alerts")
    public ResponseEntity<AlertRule> createAlert(@RequestBody AlertRule alert) {
        return ResponseEntity.ok(monitorService.createAlert(alert));
    }

    @PostMapping("/alerts/{alertId}/acknowledge")
    public ResponseEntity<Boolean> acknowledgeAlert(@PathVariable String alertId) {
        return ResponseEntity.ok(monitorService.acknowledgeAlert(alertId));
    }

    @GetMapping("/logs")
    public ResponseEntity<PageResult<LogEntry>> queryLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String source,
            @RequestParam(defaultValue = "0") long start,
            @RequestParam(defaultValue = "9223372036854775807") long end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (start == 0) {
            start = System.currentTimeMillis() - 3600000;
        }
        if (end == Long.MAX_VALUE) {
            end = System.currentTimeMillis();
        }
        return ResponseEntity.ok(monitorService.queryLogs(level, source, start, end, page, size));
    }

    @GetMapping("/observe/{targetId}")
    public ResponseEntity<ObservationData> observe(@PathVariable String targetId) {
        return ResponseEntity.ok(monitorService.observe(targetId));
    }

    @GetMapping("/observe/{targetId}/history")
    public ResponseEntity<List<ObservationData>> getHistory(
            @PathVariable String targetId,
            @RequestParam(defaultValue = "0") long start,
            @RequestParam(defaultValue = "9223372036854775807") long end) {
        if (start == 0) {
            start = System.currentTimeMillis() - 3600000;
        }
        if (end == Long.MAX_VALUE) {
            end = System.currentTimeMillis();
        }
        return ResponseEntity.ok(monitorService.getHistory(targetId, start, end));
    }
}
