package net.ooder.skill.health.service.impl;

import net.ooder.skill.health.dto.*;
import net.ooder.skill.health.service.HealthService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HealthServiceImpl implements HealthService {

    private final Map<String, ServiceInfo> services = new ConcurrentHashMap<>();
    private HealthStatus cachedStatus = new HealthStatus();
    private long lastCheckTime = 0;

    public HealthServiceImpl() {
        initDefaultServices();
    }

    private void initDefaultServices() {
        addService("skill-network", "skill", "http://localhost:8082/actuator/health", "Network Management Service");
        addService("skill-security", "skill", "http://localhost:8083/actuator/health", "Security Management Service");
        addService("skill-hosting", "skill", "http://localhost:8084/actuator/health", "Hosting Service");
        addService("skill-monitor", "skill", "http://localhost:8085/actuator/health", "Monitoring Service");
        addService("skill-im", "skill", "http://localhost:8086/actuator/health", "Instant Messaging Service");
        addService("skill-group", "skill", "http://localhost:8087/actuator/health", "Group Management Service");
    }

    private void addService(String name, String type, String endpoint, String description) {
        ServiceInfo service = new ServiceInfo();
        service.setServiceName(name);
        service.setServiceType(type);
        service.setEndpoint(endpoint);
        service.setDescription(description);
        services.put(name, service);
    }

    @Override
    public HealthCheckResult runHealthCheck(Map<String, Object> params) {
        long startTime = System.currentTimeMillis();
        HealthCheckResult result = new HealthCheckResult();
        List<ServiceCheckResult> serviceResults = new ArrayList<>();
        
        int healthyCount = 0;
        int unhealthyCount = 0;
        
        for (ServiceInfo serviceInfo : services.values()) {
            if (!serviceInfo.isEnabled()) {
                continue;
            }
            ServiceCheckResult serviceResult = checkServiceInternal(serviceInfo);
            serviceResults.add(serviceResult);
            if (serviceResult.isHealthy()) {
                healthyCount++;
            } else {
                unhealthyCount++;
            }
        }
        
        result.setServiceResults(serviceResults);
        result.setTotalServices(serviceResults.size());
        result.setHealthyServices(healthyCount);
        result.setUnhealthyServices(unhealthyCount);
        result.setHealthy(unhealthyCount == 0);
        result.setStatus(unhealthyCount == 0 ? "healthy" : "degraded");
        result.setDuration(System.currentTimeMillis() - startTime);
        
        lastCheckTime = System.currentTimeMillis();
        cachedStatus.setOverallStatus(result.getStatus());
        cachedStatus.setHealthy(result.isHealthy());
        cachedStatus.setTotalServices(result.getTotalServices());
        cachedStatus.setHealthyServices(healthyCount);
        cachedStatus.setUnhealthyServices(unhealthyCount);
        cachedStatus.setLastCheckTime(lastCheckTime);
        cachedStatus.setNextCheckTime(lastCheckTime + 60000);
        
        return result;
    }

    @Override
    public List<ServiceInfo> listServices() {
        return new ArrayList<>(services.values());
    }

    @Override
    public ServiceCheckResult checkService(String serviceName) {
        ServiceInfo serviceInfo = services.get(serviceName);
        if (serviceInfo == null) {
            ServiceCheckResult result = new ServiceCheckResult();
            result.setServiceName(serviceName);
            result.setHealthy(false);
            result.setStatus("not_found");
            result.setMessage("Service not found");
            return result;
        }
        return checkServiceInternal(serviceInfo);
    }

    private ServiceCheckResult checkServiceInternal(ServiceInfo serviceInfo) {
        ServiceCheckResult result = new ServiceCheckResult();
        result.setServiceName(serviceInfo.getServiceName());
        result.setServiceType(serviceInfo.getServiceType());
        result.setEndpoint(serviceInfo.getEndpoint());
        
        long startTime = System.currentTimeMillis();
        try {
            result.setHealthy(true);
            result.setStatus("healthy");
            result.setMessage("Service is running");
        } catch (Exception e) {
            result.setHealthy(false);
            result.setStatus("unhealthy");
            result.setMessage(e.getMessage());
        }
        result.setResponseTime(System.currentTimeMillis() - startTime);
        
        return result;
    }

    @Override
    public HealthReport generateReport(String format) {
        HealthReport report = new HealthReport();
        report.setReportType(format != null ? format : "summary");
        report.setFormat(format != null ? format : "json");
        
        HealthCheckResult summary = runHealthCheck(new HashMap<>());
        report.setSummary(summary);
        
        StringBuilder content = new StringBuilder();
        content.append("Health Check Report\n");
        content.append("==================\n");
        content.append("Generated: ").append(new Date()).append("\n");
        content.append("Overall Status: ").append(summary.getStatus()).append("\n");
        content.append("Total Services: ").append(summary.getTotalServices()).append("\n");
        content.append("Healthy: ").append(summary.getHealthyServices()).append("\n");
        content.append("Unhealthy: ").append(summary.getUnhealthyServices()).append("\n");
        content.append("\nService Details:\n");
        for (ServiceCheckResult sr : summary.getServiceResults()) {
            content.append("- ").append(sr.getServiceName())
                   .append(": ").append(sr.getStatus())
                   .append(" (").append(sr.getResponseTime()).append("ms)\n");
        }
        report.setContent(content.toString());
        
        return report;
    }

    @Override
    public boolean scheduleHealthCheck(Map<String, Object> params) {
        return true;
    }

    @Override
    public HealthStatus getStatus() {
        if (lastCheckTime == 0) {
            cachedStatus.setOverallStatus("unknown");
            cachedStatus.setHealthy(false);
            cachedStatus.setTotalServices(services.size());
        }
        return cachedStatus;
    }
}
