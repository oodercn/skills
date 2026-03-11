package net.ooder.skill.health.service;

import net.ooder.skill.health.dto.*;

import java.util.List;
import java.util.Map;

public interface HealthService {
    HealthCheckResult runHealthCheck(Map<String, Object> params);
    List<ServiceInfo> listServices();
    ServiceCheckResult checkService(String serviceName);
    HealthReport generateReport(String format);
    boolean scheduleHealthCheck(Map<String, Object> params);
    HealthStatus getStatus();
}
