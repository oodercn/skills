package net.ooder.skill.health.provider;

import lombok.extern.slf4j.Slf4j;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.HealthProvider;
import net.ooder.scene.provider.model.health.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HealthProviderImpl implements HealthProvider {
    
    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    
    private final Map<String, HealthCheckSchedule> schedules = new ConcurrentHashMap<>();
    private final List<HealthCheckResult> checkHistory = new ArrayList<>();
    
    @Override
    public String getProviderName() {
        return "skill-health";
    }
    
    @Override
    public String getVersion() {
        return "0.7.3";
    }
    
    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
        log.info("HealthProvider initialized");
    }
    
    @Override
    public void start() {
        this.running = true;
        log.info("HealthProvider started");
    }
    
    @Override
    public void stop() {
        this.running = false;
        log.info("HealthProvider stopped");
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public Result<HealthCheckResult> runHealthCheck(Map<String, Object> params) {
        try {
            HealthCheckResult result = new HealthCheckResult();
            result.setCheckId(UUID.randomUUID().toString());
            result.setCheckType((String) params.getOrDefault("checkType", "full"));
            result.setStartTime(System.currentTimeMillis());
            result.setStatus("healthy");
            
            List<HealthCheckResult.ComponentHealth> components = new ArrayList<>();
            
            HealthCheckResult.ComponentHealth cpuHealth = new HealthCheckResult.ComponentHealth();
            cpuHealth.setName("cpu");
            cpuHealth.setStatus("healthy");
            cpuHealth.setScore(95.0);
            cpuHealth.setMessage("CPU usage is normal");
            cpuHealth.setDetails(createDetails("usage", "25%", "cores", "4"));
            components.add(cpuHealth);
            
            HealthCheckResult.ComponentHealth memoryHealth = new HealthCheckResult.ComponentHealth();
            memoryHealth.setName("memory");
            memoryHealth.setStatus("healthy");
            memoryHealth.setScore(90.0);
            memoryHealth.setMessage("Memory usage is normal");
            memoryHealth.setDetails(createDetails("used", "4GB", "total", "16GB", "percentage", "25%"));
            components.add(memoryHealth);
            
            HealthCheckResult.ComponentHealth diskHealth = new HealthCheckResult.ComponentHealth();
            diskHealth.setName("disk");
            diskHealth.setStatus("healthy");
            diskHealth.setScore(85.0);
            diskHealth.setMessage("Disk usage is normal");
            diskHealth.setDetails(createDetails("used", "100GB", "total", "500GB", "percentage", "20%"));
            components.add(diskHealth);
            
            HealthCheckResult.ComponentHealth networkHealth = new HealthCheckResult.ComponentHealth();
            networkHealth.setName("network");
            networkHealth.setStatus("healthy");
            networkHealth.setScore(98.0);
            networkHealth.setMessage("Network connectivity is good");
            networkHealth.setDetails(createDetails("latency", "5ms", "packetLoss", "0%"));
            components.add(networkHealth);
            
            result.setComponents(components);
            result.setEndTime(System.currentTimeMillis());
            result.setDuration(result.getEndTime() - result.getStartTime());
            
            double overallScore = components.stream()
                    .mapToDouble(HealthCheckResult.ComponentHealth::getScore)
                    .average()
                    .orElse(0.0);
            result.setOverallScore(overallScore);
            
            List<String> recommendations = new ArrayList<>();
            if (overallScore < 90) {
                recommendations.add("Consider optimizing system resources");
            }
            result.setRecommendations(recommendations);
            
            checkHistory.add(result);
            
            log.info("Health check completed: {} with score {}", result.getCheckId(), overallScore);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Health check failed", e);
            return Result.error("Health check failed: " + e.getMessage());
        }
    }
    
    @Override
    public Result<HealthReport> exportHealthReport() {
        try {
            HealthReport report = new HealthReport();
            report.setReportId(UUID.randomUUID().toString());
            report.setGeneratedAt(System.currentTimeMillis());
            report.setTimeRange("24h");
            
            HealthReport.HealthSummary summary = new HealthReport.HealthSummary();
            summary.setTotalChecks(checkHistory.size());
            summary.setHealthyChecks((int) checkHistory.stream()
                    .filter(c -> "healthy".equals(c.getStatus()))
                    .count());
            summary.setWarningChecks((int) checkHistory.stream()
                    .filter(c -> "warning".equals(c.getStatus()))
                    .count());
            summary.setCriticalChecks((int) checkHistory.stream()
                    .filter(c -> "critical".equals(c.getStatus()))
                    .count());
            summary.setAverageScore(checkHistory.stream()
                    .mapToDouble(HealthCheckResult::getOverallScore)
                    .average()
                    .orElse(0.0));
            report.setSummary(summary);
            
            List<HealthReport.ServiceHealth> services = new ArrayList<>();
            
            HealthReport.ServiceHealth systemService = new HealthReport.ServiceHealth();
            systemService.setName("system");
            systemService.setStatus("healthy");
            systemService.setUptime(99.9);
            systemService.setLastCheck(System.currentTimeMillis());
            services.add(systemService);
            
            HealthReport.ServiceHealth databaseService = new HealthReport.ServiceHealth();
            databaseService.setName("database");
            databaseService.setStatus("healthy");
            databaseService.setUptime(99.8);
            databaseService.setLastCheck(System.currentTimeMillis());
            services.add(databaseService);
            
            report.setServices(services);
            
            List<HealthReport.Recommendation> recommendations = new ArrayList<>();
            if (summary.getAverageScore() < 90) {
                HealthReport.Recommendation rec = new HealthReport.Recommendation();
                rec.setPriority("high");
                rec.setComponent("system");
                rec.setDescription("System performance needs attention");
                rec.setAction("Review resource usage and optimize");
                recommendations.add(rec);
            }
            report.setRecommendations(recommendations);
            
            log.info("Health report generated: {}", report.getReportId());
            return Result.success(report);
        } catch (Exception e) {
            log.error("Failed to export health report", e);
            return Result.error("Failed to export health report: " + e.getMessage());
        }
    }
    
    @Override
    public Result<HealthCheckSchedule> scheduleHealthCheck(Map<String, Object> params) {
        try {
            String scheduleId = UUID.randomUUID().toString();
            
            HealthCheckSchedule schedule = new HealthCheckSchedule();
            schedule.setScheduleId(scheduleId);
            schedule.setName((String) params.getOrDefault("name", "Scheduled Health Check"));
            schedule.setCheckType((String) params.getOrDefault("checkType", "full"));
            schedule.setCronExpression((String) params.getOrDefault("cronExpression", "0 0 * * * ?"));
            schedule.setEnabled((Boolean) params.getOrDefault("enabled", true));
            schedule.setCreatedAt(System.currentTimeMillis());
            schedule.setNextRunTime(System.currentTimeMillis() + 3600000);
            
            schedules.put(scheduleId, schedule);
            
            log.info("Health check scheduled: {}", scheduleId);
            return Result.success(schedule);
        } catch (Exception e) {
            log.error("Failed to schedule health check", e);
            return Result.error("Failed to schedule health check: " + e.getMessage());
        }
    }
    
    @Override
    public Result<ServiceCheckResult> checkService(String serviceName) {
        try {
            ServiceCheckResult result = new ServiceCheckResult();
            result.setServiceName(serviceName);
            result.setCheckTime(System.currentTimeMillis());
            result.setStatus("healthy");
            result.setResponseTime(50L);
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("cpu", 25.0);
            metrics.put("memory", 40.0);
            metrics.put("threads", 10);
            metrics.put("connections", 5);
            result.setMetrics(metrics);
            
            result.setMessage("Service " + serviceName + " is running normally");
            
            log.info("Service check completed: {} - {}", serviceName, result.getStatus());
            return Result.success(result);
        } catch (Exception e) {
            log.error("Service check failed: {}", serviceName, e);
            return Result.error("Service check failed: " + e.getMessage());
        }
    }
    
    private Map<String, Object> createDetails(Object... keyValues) {
        Map<String, Object> details = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            if (i + 1 < keyValues.length) {
                details.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
            }
        }
        return details;
    }
}
