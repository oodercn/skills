package net.ooder.skill.common.sdk.driver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 驱动 Skill 管理器
 * 管理所有基础设施驱动的生命周期
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Slf4j
@Component
public class DriverSkillManager {

    /**
     * 驱动注册表: driverType -> DriverSkill
     */
    private final Map<String, DriverSkill> driverRegistry = new ConcurrentHashMap<>();

    /**
     * 驱动健康状态: driverType -> health status
     */
    private final Map<String, DriverHealthStatus> healthStatusMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("DriverSkillManager initialized");
        // 加载系统驱动
        loadSystemDrivers();
    }

    /**
     * 加载系统驱动
     */
    private void loadSystemDrivers() {
        log.info("Loading system drivers...");
        // TODO: 从配置加载系统驱动
        // 系统驱动在启动时自动加载
    }

    /**
     * 注册驱动
     *
     * @param driverType driver type
     * @param driver     DriverSkill instance
     */
    public void registerDriver(String driverType, DriverSkill driver) {
        if (driverType == null || driver == null) {
            throw new IllegalArgumentException("driverType and driver cannot be null");
        }

        if (driverRegistry.containsKey(driverType)) {
            log.warn("Driver already registered: {}, will be overwritten", driverType);
        }

        // 初始化驱动
        driver.initialize();
        
        driverRegistry.put(driverType, driver);
        healthStatusMap.put(driverType, DriverHealthStatus.HEALTHY);
        
        log.info("Driver registered: {} v{}", driverType, driver.getVersion());
    }

    /**
     * 获取驱动
     *
     * @param driverType driver type
     * @return DriverSkill
     */
    public DriverSkill getDriver(String driverType) {
        DriverSkill driver = driverRegistry.get(driverType);
        if (driver == null) {
            throw new DriverNotFoundException("Driver not found: " + driverType);
        }
        return driver;
    }

    /**
     * 检查驱动是否存在
     *
     * @param driverType driver type
     * @return true if exists
     */
    public boolean hasDriver(String driverType) {
        return driverRegistry.containsKey(driverType);
    }

    /**
     * 获取所有驱动类型
     *
     * @return list of driver types
     */
    public List<String> getAllDriverTypes() {
        return List.copyOf(driverRegistry.keySet());
    }

    /**
     * 获取所有健康驱动
     *
     * @return list of healthy drivers
     */
    public List<DriverSkill> getHealthyDrivers() {
        return driverRegistry.entrySet().stream()
            .filter(e -> healthStatusMap.get(e.getKey()) == DriverHealthStatus.HEALTHY)
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
    }

    /**
     * 驱动健康检查
     */
    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void healthCheck() {
        log.debug("Performing driver health check...");
        
        for (Map.Entry<String, DriverSkill> entry : driverRegistry.entrySet()) {
            String driverType = entry.getKey();
            DriverSkill driver = entry.getValue();
            
            DriverHealthStatus oldStatus = healthStatusMap.get(driverType);
            DriverHealthStatus newStatus;
            
            try {
                if (driver.isHealthy()) {
                    newStatus = DriverHealthStatus.HEALTHY;
                    if (oldStatus != DriverHealthStatus.HEALTHY) {
                        log.info("Driver recovered: {}", driverType);
                    }
                } else {
                    newStatus = DriverHealthStatus.UNHEALTHY;
                    log.warn("Driver unhealthy: {}", driverType);
                    
                    // 尝试恢复
                    tryRecover(driverType, driver);
                }
            } catch (Exception e) {
                newStatus = DriverHealthStatus.FAILED;
                log.error("Driver health check failed: {}", driverType, e);
            }
            
            healthStatusMap.put(driverType, newStatus);
        }
    }

    /**
     * 尝试恢复驱动
     */
    private void tryRecover(String driverType, DriverSkill driver) {
        log.info("Attempting to recover driver: {}", driverType);
        
        try {
            driver.recover();
            
            if (driver.isHealthy()) {
                healthStatusMap.put(driverType, DriverHealthStatus.HEALTHY);
                log.info("Driver recovered successfully: {}", driverType);
            } else {
                healthStatusMap.put(driverType, DriverHealthStatus.FAILED);
                log.error("Driver recovery failed: {}", driverType);
            }
        } catch (Exception e) {
            healthStatusMap.put(driverType, DriverHealthStatus.FAILED);
            log.error("Driver recovery failed: {}", driverType, e);
        }
    }

    /**
     * 升级驱动
     *
     * @param driverType driver type
     * @param newVersion new version
     */
    public void upgradeDriver(String driverType, String newVersion) {
        log.info("Upgrading driver: {} to version {}", driverType, newVersion);
        
        DriverSkill oldDriver = driverRegistry.get(driverType);
        if (oldDriver == null) {
            throw new DriverNotFoundException("Driver not found: " + driverType);
        }

        // TODO: 创建新驱动实例
        // DriverSkill newDriver = createDriver(driverType, newVersion);
        
        // 热切换
        synchronized (driverRegistry) {
            // driverRegistry.put(driverType, newDriver);
            healthStatusMap.put(driverType, DriverHealthStatus.HEALTHY);
        }
        
        // 停止旧驱动
        oldDriver.shutdown();
        
        log.info("Driver upgraded: {} -> {}", driverType, newVersion);
    }

    /**
     * 卸载驱动
     *
     * @param driverType driver type
     */
    public void unregisterDriver(String driverType) {
        DriverSkill driver = driverRegistry.remove(driverType);
        if (driver != null) {
            driver.shutdown();
            healthStatusMap.remove(driverType);
            log.info("Driver unregistered: {}", driverType);
        }
    }

    /**
     * 获取驱动统计信息
     *
     * @return DriverStatistics
     */
    public DriverStatistics getStatistics() {
        int total = driverRegistry.size();
        long healthy = healthStatusMap.values().stream()
            .filter(s -> s == DriverHealthStatus.HEALTHY)
            .count();
        long unhealthy = healthStatusMap.values().stream()
            .filter(s -> s == DriverHealthStatus.UNHEALTHY)
            .count();
        long failed = healthStatusMap.values().stream()
            .filter(s -> s == DriverHealthStatus.FAILED)
            .count();

        return DriverStatistics.builder()
            .totalDrivers(total)
            .healthyDrivers((int) healthy)
            .unhealthyDrivers((int) unhealthy)
            .failedDrivers((int) failed)
            .build();
    }
}
