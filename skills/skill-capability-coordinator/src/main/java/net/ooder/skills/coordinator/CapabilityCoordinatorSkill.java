package net.ooder.skills.coordinator;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.api.skill.Capability;
import net.ooder.sdk.api.skill.Skill;
import net.ooder.skills.container.api.CapabilityRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 鑳藉姏鍗忚皟鍣?Skill
 *
 * 鍗忚皟绯荤粺涓墍鏈夎兘鍔涳紝鎻愪緵鑳藉姏鍙戠幇銆佽矾鐢便€侀檷绾х瓑鍔熻兘
 *
 * @author Skills Team
 * @version 1.0.0
 * @since 2026-02-24
 */
@Slf4j
@Component
@Skill(
        id = "skill-capability-coordinator",
        name = "Capability Coordinator Skill",
        version = "1.0.0",
        description = "Coordinates and manages capabilities across the system"
)
public class CapabilityCoordinatorSkill {

    @Autowired
    private CapabilityRegistry capabilityRegistry;

    /**
     * 鑳藉姏璺敱琛?     */
    private final Map<String, CapabilityRoute> routeTable = new ConcurrentHashMap<>();

    /**
     * 鑳藉姏闄嶇骇鏄犲皠
     */
    private final Map<String, String> fallbackMap = new ConcurrentHashMap<>();

    /**
     * 鑳藉姏鍋ュ悍鐘舵€?     */
    private final Map<String, CapabilityHealth> healthStatus = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("CapabilityCoordinatorSkill initialized");
        initializeRouteTable();
        initializeFallbackMap();
    }

    // ============================================================
    // 鑳藉姏鍙戠幇
    // ============================================================

    /**
     * 鍙戠幇鎵€鏈夊彲鐢ㄨ兘鍔?     */
    public List<CapabilityInfo> discoverCapabilities() {
        List<Capability> capabilities = capabilityRegistry.discoverCapabilities("all");

        return capabilities.stream()
                .map(this::toCapabilityInfo)
                .collect(Collectors.toList());
    }

    /**
     * 鍙戠幇鐗瑰畾绫诲埆鐨勮兘鍔?     */
    public List<CapabilityInfo> discoverCapabilitiesByCategory(String category) {
        List<Capability> capabilities = capabilityRegistry.discoverCapabilities(category);

        return capabilities.stream()
                .map(this::toCapabilityInfo)
                .collect(Collectors.toList());
    }

    /**
     * 鎼滅储鑳藉姏
     */
    public List<CapabilityInfo> searchCapabilities(String keyword) {
        List<Capability> allCapabilities = capabilityRegistry.discoverCapabilities("all");

        return allCapabilities.stream()
                .filter(c -> matchesKeyword(c, keyword))
                .map(this::toCapabilityInfo)
                .collect(Collectors.toList());
    }

    // ============================================================
    // 鑳藉姏璺敱
    // ============================================================

    /**
     * 娉ㄥ唽鑳藉姏璺敱
     */
    public void registerRoute(String capabilityId, String skillId, RoutePriority priority) {
        CapabilityRoute route = CapabilityRoute.builder()
                .capabilityId(capabilityId)
                .skillId(skillId)
                .priority(priority)
                .enabled(true)
                .build();

        routeTable.put(capabilityId, route);
        log.info("Registered route: {} -> {} (priority: {})", capabilityId, skillId, priority);
    }

    /**
     * 鑾峰彇鑳藉姏鎻愪緵鑰?     */
    public String getProviderSkill(String capabilityId) {
        // 1. 妫€鏌ヨ矾鐢辫〃
        CapabilityRoute route = routeTable.get(capabilityId);
        if (route != null && route.isEnabled()) {
            String skillId = route.getSkillId();
            if (isSkillHealthy(skillId)) {
                return skillId;
            }
        }

        // 2. 浣跨敤娉ㄥ唽涓績
        String skillId = capabilityRegistry.getProviderSkill(capabilityId);
        if (skillId != null && isSkillHealthy(skillId)) {
            return skillId;
        }

        // 3. 浣跨敤闄嶇骇
        return getFallbackProvider(capabilityId);
    }

    /**
     * 璋冪敤鑳藉姏
     */
    public Object invokeCapability(String capabilityId, Map<String, Object> params) {
        String skillId = getProviderSkill(capabilityId);

        if (skillId == null) {
            throw new CapabilityNotAvailableException("No provider available for capability: " + capabilityId);
        }

        try {
            Object result = capabilityRegistry.invokeCapability(capabilityId, params);
            recordSuccess(capabilityId, skillId);
            return result;

        } catch (Exception e) {
            recordFailure(capabilityId, skillId);
            log.error("Capability invocation failed: {} from skill: {}", capabilityId, skillId, e);

            // 灏濊瘯闄嶇骇
            return invokeFallback(capabilityId, params);
        }
    }

    // ============================================================
    // 鑳藉姏闄嶇骇
    // ============================================================

    /**
     * 娉ㄥ唽闄嶇骇鏄犲皠
     */
    public void registerFallback(String capabilityId, String fallbackCapabilityId) {
        fallbackMap.put(capabilityId, fallbackCapabilityId);
        log.info("Registered fallback: {} -> {}", capabilityId, fallbackCapabilityId);
    }

    /**
     * 璋冪敤闄嶇骇鑳藉姏
     */
    private Object invokeFallback(String capabilityId, Map<String, Object> params) {
        String fallbackId = fallbackMap.get(capabilityId);

        if (fallbackId == null) {
            log.warn("No fallback available for capability: {}", capabilityId);
            return null;
        }

        log.info("Invoking fallback for {}: using {}", capabilityId, fallbackId);
        return invokeCapability(fallbackId, params);
    }

    /**
     * 鑾峰彇闄嶇骇鎻愪緵鑰?     */
    private String getFallbackProvider(String capabilityId) {
        String fallbackId = fallbackMap.get(capabilityId);
        if (fallbackId != null) {
            return capabilityRegistry.getProviderSkill(fallbackId);
        }
        return null;
    }

    // ============================================================
    // 鍋ュ悍妫€鏌?    // ============================================================

    /**
     * 瀹氭椂鍋ュ悍妫€鏌?     */
    @Scheduled(fixedRate = 60000) // 姣忓垎閽?    public void performHealthChecks() {
        log.debug("Performing capability health checks...");

        for (String capabilityId : routeTable.keySet()) {
            checkCapabilityHealth(capabilityId);
        }
    }

    /**
     * 妫€鏌ヨ兘鍔涘仴搴风姸鎬?     */
    public CapabilityHealth checkCapabilityHealth(String capabilityId) {
        String skillId = getProviderSkill(capabilityId);

        if (skillId == null) {
            return CapabilityHealth.builder()
                    .capabilityId(capabilityId)
                    .status(HealthStatus.UNAVAILABLE)
                    .build();
        }

        // 绠€鍖栧疄鐜帮細瀹為檯搴旇皟鐢ㄥ仴搴锋鏌ユ帴鍙?        boolean healthy = isSkillHealthy(skillId);

        CapabilityHealth health = CapabilityHealth.builder()
                .capabilityId(capabilityId)
                .skillId(skillId)
                .status(healthy ? HealthStatus.HEALTHY : HealthStatus.UNHEALTHY)
                .lastChecked(System.currentTimeMillis())
                .build();

        healthStatus.put(capabilityId, health);
        return health;
    }

    /**
     * 鑾峰彇鑳藉姏鍋ュ悍鐘舵€?     */
    public CapabilityHealth getHealthStatus(String capabilityId) {
        return healthStatus.get(capabilityId);
    }

    // ============================================================
    // 缁熻涓庣洃鎺?    // ============================================================

    /**
     * 璁板綍鎴愬姛璋冪敤
     */
    private void recordSuccess(String capabilityId, String skillId) {
        CapabilityHealth health = healthStatus.computeIfAbsent(capabilityId,
                k -> CapabilityHealth.builder().capabilityId(capabilityId).build());
        health.incrementSuccessCount();
    }

    /**
     * 璁板綍澶辫触璋冪敤
     */
    private void recordFailure(String capabilityId, String skillId) {
        CapabilityHealth health = healthStatus.computeIfAbsent(capabilityId,
                k -> CapabilityHealth.builder().capabilityId(capabilityId).build());
        health.incrementFailureCount();
    }

    /**
     * 鑾峰彇鑳藉姏缁熻
     */
    public Map<String, CapabilityStats> getCapabilityStats() {
        Map<String, CapabilityStats> stats = new HashMap<>();

        for (Map.Entry<String, CapabilityHealth> entry : healthStatus.entrySet()) {
            CapabilityHealth health = entry.getValue();
            stats.put(entry.getKey(), CapabilityStats.builder()
                    .capabilityId(health.getCapabilityId())
                    .successCount(health.getSuccessCount())
                    .failureCount(health.getFailureCount())
                    .successRate(health.getSuccessRate())
                    .build());
        }

        return stats;
    }

    // ============================================================
    // 鍒濆鍖?    // ============================================================

    private void initializeRouteTable() {
        // 浠庨厤缃姞杞借矾鐢辫〃
        log.info("Initializing capability route table...");
    }

    private void initializeFallbackMap() {
        // 娉ㄥ唽榛樿闄嶇骇鏄犲皠
        // 渚嬪锛氶珮绾у姛鑳?-> 鍩虹鍔熻兘
        log.info("Initializing capability fallback map...");
    }

    // ============================================================
    // 杈呭姪鏂规硶
    // ============================================================

    private boolean isSkillHealthy(String skillId) {
        // 绠€鍖栧疄鐜帮細瀹為檯搴旀煡璇㈠仴搴风姸鎬?        return true;
    }

    private boolean matchesKeyword(Capability capability, String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return capability.getId().toLowerCase().contains(lowerKeyword) ||
                (capability.getName() != null && capability.getName().toLowerCase().contains(lowerKeyword));
    }

    private CapabilityInfo toCapabilityInfo(Capability capability) {
        String skillId = capabilityRegistry.getProviderSkill(capability.getId());
        CapabilityHealth health = healthStatus.get(capability.getId());

        return CapabilityInfo.builder()
                .capabilityId(capability.getId())
                .name(capability.getName())
                .providerSkillId(skillId)
                .healthStatus(health != null ? health.getStatus() : HealthStatus.UNKNOWN)
                .build();
    }

    // ============================================================
    // 鏁版嵁绫诲畾涔?    // ============================================================

    @lombok.Data
    @lombok.Builder
    public static class CapabilityRoute {
        private String capabilityId;
        private String skillId;
        private RoutePriority priority;
        private boolean enabled;
    }

    @lombok.Data
    @lombok.Builder
    public static class CapabilityInfo {
        private String capabilityId;
        private String name;
        private String providerSkillId;
        private HealthStatus healthStatus;
    }

    @lombok.Data
    @lombok.Builder
    public static class CapabilityHealth {
        private String capabilityId;
        private String skillId;
        private HealthStatus status;
        private long lastChecked;
        @lombok.Builder.Default
        private int successCount = 0;
        @lombok.Builder.Default
        private int failureCount = 0;

        public void incrementSuccessCount() {
            successCount++;
        }

        public void incrementFailureCount() {
            failureCount++;
        }

        public double getSuccessRate() {
            int total = successCount + failureCount;
            return total > 0 ? (double) successCount / total : 0;
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class CapabilityStats {
        private String capabilityId;
        private int successCount;
        private int failureCount;
        private double successRate;
    }

    public enum RoutePriority {
        HIGH, MEDIUM, LOW
    }

    public enum HealthStatus {
        HEALTHY, UNHEALTHY, UNKNOWN, UNAVAILABLE
    }

    public static class CapabilityNotAvailableException extends RuntimeException {
        public CapabilityNotAvailableException(String message) {
            super(message);
        }
    }
}
