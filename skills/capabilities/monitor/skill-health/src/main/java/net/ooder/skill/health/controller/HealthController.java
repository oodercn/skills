package net.ooder.skill.health.controller;

import net.ooder.skill.health.dto.*;
import net.ooder.skill.health.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private HealthService healthService;

    @PostMapping("/check")
    public ResponseEntity<HealthCheckResult> runHealthCheck(@RequestBody(required = false) Map<String, Object> params) {
        return ResponseEntity.ok(healthService.runHealthCheck(params != null ? params : new HashMap<>()));
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceInfo>> listServices() {
        return ResponseEntity.ok(healthService.listServices());
    }

    @PostMapping("/services/{serviceName}/check")
    public ResponseEntity<ServiceCheckResult> checkService(@PathVariable String serviceName) {
        return ResponseEntity.ok(healthService.checkService(serviceName));
    }

    @PostMapping("/report")
    public ResponseEntity<HealthReport> generateReport(@RequestBody(required = false) Map<String, Object> params) {
        String format = params != null ? (String) params.get("format") : "json";
        return ResponseEntity.ok(healthService.generateReport(format));
    }

    @PostMapping("/schedule")
    public ResponseEntity<Boolean> scheduleHealthCheck(@RequestBody Map<String, Object> params) {
        return ResponseEntity.ok(healthService.scheduleHealthCheck(params));
    }

    @GetMapping("/status")
    public ResponseEntity<HealthStatus> getStatus() {
        return ResponseEntity.ok(healthService.getStatus());
    }
}
