package net.ooder.skills.loadbalancer;

import lombok.extern.slf4j.Slf4j;
import net.ooder.sdk.api.skill.Skill;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 璐熻浇鍧囪　�?Skill
 *
 * 涓哄涓?Skill 瀹炰緥鎻愪緵璐熻浇鍧囪　鑳藉�?
 *
 * @author Skills Team
 * @version 1.0.0
 * @since 2026-02-24
 */
@Slf4j
@Component
@Skill(
        id = "skill-load-balancer",
        name = "Load Balancer Skill",
        version = "1.0.0",
        description = "Provides load balancing across multiple skill instances"
)
public class LoadBalancerSkill {

    /**
     * 鍚庣瀹炰緥鏄犲皠
     */
    private final Map<String, List<BackendInstance>> backends = new ConcurrentHashMap<>();

    /**
     * 璐熻浇鍧囪　绛栫�?
     */
    private final Map<String, LoadBalanceStrategy> strategies = new ConcurrentHashMap<>();

    /**
     * 杞璁℃暟�?     */
    private final Map<String, AtomicInteger> roundRobinCounters = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("LoadBalancerSkill initialized");
    }

    // ============================================================
    // 鍚庣瀹炰緥绠＄悊
    // ============================================================

    /**
     * 娉ㄥ唽鍚庣瀹炰�?
     */
    public void registerBackend(String serviceId, String instanceId, String host, int port, int weight) {
        BackendInstance instance = BackendInstance.builder()
                .instanceId(instanceId)
                .host(host)
                .port(port)
                .weight(weight)
                .healthy(true)
                .registerTime(System.currentTimeMillis())
                .build();

        backends.computeIfAbsent(serviceId, k -> new ArrayList<>()).add(instance);
        roundRobinCounters.computeIfAbsent(serviceId, k -> new AtomicInteger(0));

        log.info("Registered backend: {} for service: {} at {}:{}", instanceId, serviceId, host, port);
    }

    /**
     * 娉ㄩ攢鍚庣瀹炰�?
     */
    public void unregisterBackend(String serviceId, String instanceId) {
        List<BackendInstance> instances = backends.get(serviceId);
        if (instances != null) {
            instances.removeIf(i -> i.getInstanceId().equals(instanceId));
            log.info("Unregistered backend: {} for service: {}", instanceId, serviceId);
        }
    }

    /**
     * 鏇存柊鍚庣鍋ュ悍鐘舵€?     */
    public void updateHealthStatus(String serviceId, String instanceId, boolean healthy) {
        BackendInstance instance = findInstance(serviceId, instanceId);
        if (instance != null) {
            instance.setHealthy(healthy);
            instance.setLastHealthCheck(System.currentTimeMillis());
            log.debug("Updated health status for {}: {}", instanceId, healthy);
        }
    }

    /**
     * 鏇存柊鍚庣鏉冮�?
     */
    public void updateWeight(String serviceId, String instanceId, int weight) {
        BackendInstance instance = findInstance(serviceId, instanceId);
        if (instance != null) {
            instance.setWeight(weight);
            log.info("Updated weight for {}: {}", instanceId, weight);
        }
    }

    // ============================================================
    // 璐熻浇鍧囪　绛栫�?
    // ============================================================

    /**
     * 璁剧疆璐熻浇鍧囪　绛栫�?
     */
    public void setStrategy(String serviceId, LoadBalanceStrategy strategy) {
        strategies.put(serviceId, strategy);
        log.info("Set load balance strategy for {}: {}", serviceId, strategy);
    }

    /**
     * 閫夋嫨鍚庣瀹炰�?
     */
    public BackendInstance selectBackend(String serviceId) {
        List<BackendInstance> instances = getHealthyInstances(serviceId);

        if (instances.isEmpty()) {
            log.warn("No healthy backend available for service: {}", serviceId);
            return null;
        }

        LoadBalanceStrategy strategy = strategies.getOrDefault(serviceId, LoadBalanceStrategy.ROUND_ROBIN);

        return switch (strategy) {
            case ROUND_ROBIN -> selectByRoundRobin(serviceId, instances);
            case RANDOM -> selectByRandom(instances);
            case WEIGHTED -> selectByWeight(instances);
            case LEAST_CONNECTIONS -> selectByLeastConnections(instances);
            case IP_HASH -> selectByIpHash(serviceId, instances);
        };
    }

    /**
     * 杞閫夋嫨
     */
    private BackendInstance selectByRoundRobin(String serviceId, List<BackendInstance> instances) {
        AtomicInteger counter = roundRobinCounters.get(serviceId);
        if (counter == null) {
            return instances.get(0);
        }

        int index = counter.getAndIncrement() % instances.size();
        return instances.get(index);
    }

    /**
     * 闅忔満閫夋�?
     */
    private BackendInstance selectByRandom(List<BackendInstance> instances) {
        int index = new Random().nextInt(instances.size());
        return instances.get(index);
    }

    /**
     * 鍔犳潈閫夋嫨
     */
    private BackendInstance selectByWeight(List<BackendInstance> instances) {
        int totalWeight = instances.stream().mapToInt(BackendInstance::getWeight).sum();
        int randomWeight = new Random().nextInt(totalWeight);

        int currentWeight = 0;
        for (BackendInstance instance : instances) {
            currentWeight += instance.getWeight();
            if (randomWeight < currentWeight) {
                return instance;
            }
        }

        return instances.get(instances.size() - 1);
    }

    /**
     * 鏈€灏戣繛鎺ラ€夋嫨
     */
    private BackendInstance selectByLeastConnections(List<BackendInstance> instances) {
        return instances.stream()
                .min(Comparator.comparingInt(BackendInstance::getActiveConnections))
                .orElse(instances.get(0));
    }

    /**
     * IP 鍝堝笇閫夋嫨
     */
    private BackendInstance selectByIpHash(String clientIp, List<BackendInstance> instances) {
        int hash = clientIp.hashCode();
        int index = Math.abs(hash) % instances.size();
        return instances.get(index);
    }

    // ============================================================
    // 杩炴帴绠＄悊
    // ============================================================

    /**
     * 璁板綍杩炴帴寮€�?     */
    public void recordConnectionStart(String serviceId, String instanceId) {
        BackendInstance instance = findInstance(serviceId, instanceId);
        if (instance != null) {
            instance.incrementActiveConnections();
            instance.incrementTotalRequests();
        }
    }

    /**
     * 璁板綍杩炴帴缁撴�?
     */
    public void recordConnectionEnd(String serviceId, String instanceId, boolean success, long responseTime) {
        BackendInstance instance = findInstance(serviceId, instanceId);
        if (instance != null) {
            instance.decrementActiveConnections();

            if (success) {
                instance.incrementSuccessRequests();
            } else {
                instance.incrementFailedRequests();
            }

            instance.recordResponseTime(responseTime);
        }
    }

    // ============================================================
    // 缁熻淇℃伅
    // ============================================================

    /**
     * 鑾峰彇鏈嶅姟缁熻�?
     */
    public LoadBalanceStats getStats(String serviceId) {
        List<BackendInstance> instances = backends.get(serviceId);
        if (instances == null) {
            return null;
        }

        int totalInstances = instances.size();
        int healthyInstances = (int) instances.stream().filter(BackendInstance::isHealthy).count();
        long totalRequests = instances.stream().mapToLong(BackendInstance::getTotalRequests).sum();
        long activeConnections = instances.stream().mapToLong(BackendInstance::getActiveConnections).sum();

        return LoadBalanceStats.builder()
                .serviceId(serviceId)
                .totalInstances(totalInstances)
                .healthyInstances(healthyInstances)
                .totalRequests(totalRequests)
                .activeConnections(activeConnections)
                .build();
    }

    /**
     * 鑾峰彇鎵€鏈夊悗绔疄渚?     */
    public List<BackendInstance> getBackends(String serviceId) {
        return backends.getOrDefault(serviceId, Collections.emptyList());
    }

    /**
     * 鑾峰彇鍋ュ悍瀹炰�?
     */
    public List<BackendInstance> getHealthyInstances(String serviceId) {
        return backends.getOrDefault(serviceId, Collections.emptyList())
                .stream()
                .filter(BackendInstance::isHealthy)
                .toList();
    }

    // ============================================================
    // 杈呭姪鏂规硶
    // ============================================================

    private BackendInstance findInstance(String serviceId, String instanceId) {
        List<BackendInstance> instances = backends.get(serviceId);
        if (instances == null) {
            return null;
        }

        return instances.stream()
                .filter(i -> i.getInstanceId().equals(instanceId))
                .findFirst()
                .orElse(null);
    }

    // ============================================================
    // 鏁版嵁绫诲畾�?    // ============================================================

    @lombok.Data
    @lombok.Builder
    public static class BackendInstance {
        private String instanceId;
        private String host;
        private int port;
        private int weight;
        private boolean healthy;
        private long registerTime;
        private long lastHealthCheck;

        @lombok.Builder.Default
        private AtomicInteger activeConnections = new AtomicInteger(0);

        @lombok.Builder.Default
        private AtomicLong totalRequests = new AtomicLong(0);

        @lombok.Builder.Default
        private AtomicLong successRequests = new AtomicLong(0);

        @lombok.Builder.Default
        private AtomicLong failedRequests = new AtomicLong(0);

        @lombok.Builder.Default
        private AtomicLong totalResponseTime = new AtomicLong(0);

        public void incrementActiveConnections() {
            activeConnections.incrementAndGet();
        }

        public void decrementActiveConnections() {
            activeConnections.decrementAndGet();
        }

        public void incrementTotalRequests() {
            totalRequests.incrementAndGet();
        }

        public void incrementSuccessRequests() {
            successRequests.incrementAndGet();
        }

        public void incrementFailedRequests() {
            failedRequests.incrementAndGet();
        }

        public void recordResponseTime(long responseTime) {
            totalResponseTime.addAndGet(responseTime);
        }

        public double getAverageResponseTime() {
            long total = totalRequests.get();
            return total > 0 ? (double) totalResponseTime.get() / total : 0;
        }

        public double getSuccessRate() {
            long total = totalRequests.get();
            return total > 0 ? (double) successRequests.get() / total : 0;
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class LoadBalanceStats {
        private String serviceId;
        private int totalInstances;
        private int healthyInstances;
        private long totalRequests;
        private long activeConnections;

        public double getHealthyRate() {
            return totalInstances > 0 ? (double) healthyInstances / totalInstances : 0;
        }
    }

    public enum LoadBalanceStrategy {
        ROUND_ROBIN,        // 杞�?
        RANDOM,             // 闅忔満
        WEIGHTED,           // 鍔犳�?
        LEAST_CONNECTIONS,  // 鏈€灏戣繛鎺?        IP_HASH             // IP 鍝堝�?
    }
}
