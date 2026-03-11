package net.ooder.skill.common.sdk.resource;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 璧勬簮绠＄悊鍣? *
 * 缁熶竴绠＄悊瀛樺偍銆佽绠椼€佺綉缁滆祫婧? * 涓?Skills 鎻愪緵璧勬簮鐢宠銆佺洃鎺с€侀檺鍒惰兘鍔? *
 * @author Skills Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Slf4j
@Component
public class ResourceManager {

    private MemoryMXBean memoryMXBean;
    private OperatingSystemMXBean osMXBean;

    /**
     * 璧勬簮閰嶉鏄犲皠
     */
    private final ConcurrentMap<String, ResourceQuota> quotas = new ConcurrentHashMap<>();

    /**
     * 璧勬簮浣跨敤缁熻
     */
    private final ConcurrentMap<String, ResourceUsage> usageStats = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        osMXBean = ManagementFactory.getOperatingSystemMXBean();
        log.info("ResourceManager initialized");
    }

    // ============================================================
    // 瀛樺偍璧勬簮绠＄悊
    // ============================================================

    /**
     * 鑾峰彇瀛樺偍璧勬簮淇℃伅
     */
    public StorageResource getStorageResource(Path path) {
        try {
            FileStore store = Files.getFileStore(path);
            return StorageResource.builder()
                    .totalSpace(store.getTotalSpace())
                    .usableSpace(store.getUsableSpace())
                    .unallocatedSpace(store.getUnallocatedSpace())
                    .path(path.toString())
                    .build();
        } catch (Exception e) {
            log.error("Failed to get storage resource for: {}", path, e);
            return null;
        }
    }

    /**
     * 鑾峰彇鎵€鏈夊瓨鍌ㄨ祫婧?     */
    public List<StorageResource> getAllStorageResources() {
        List<StorageResource> resources = new ArrayList<>();
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            StorageResource resource = getStorageResource(root);
            if (resource != null) {
                resources.add(resource);
            }
        }
        return resources;
    }

    /**
     * 鐢宠瀛樺偍閰嶉
     */
    public boolean allocateStorageQuota(String skillId, long quotaBytes) {
        StorageResource systemResource = getStorageResource(FileSystems.getDefault().getPath("."));
        if (systemResource == null || systemResource.getUsableSpace() < quotaBytes) {
            log.warn("Insufficient storage for skill: {}, requested: {}", skillId, quotaBytes);
            return false;
        }

        ResourceQuota quota = quotas.computeIfAbsent(skillId, k -> new ResourceQuota());
        quota.setStorageQuota(quotaBytes);
        log.info("Allocated storage quota for skill: {} = {} bytes", skillId, quotaBytes);
        return true;
    }

    // ============================================================
    // 璁＄畻璧勬簮绠＄悊
    // ============================================================

    /**
     * 鑾峰彇璁＄畻璧勬簮淇℃伅
     */
    public ComputeResource getComputeResource() {
        return ComputeResource.builder()
                .availableProcessors(osMXBean.getAvailableProcessors())
                .systemLoadAverage(osMXBean.getSystemLoadAverage())
                .heapMemoryUsed(memoryMXBean.getHeapMemoryUsage().getUsed())
                .heapMemoryMax(memoryMXBean.getHeapMemoryUsage().getMax())
                .nonHeapMemoryUsed(memoryMXBean.getNonHeapMemoryUsage().getUsed())
                .build();
    }

    /**
     * 鐢宠璁＄畻閰嶉
     */
    public boolean allocateComputeQuota(String skillId, int maxCpuPercent, long maxMemoryBytes) {
        ComputeResource systemResource = getComputeResource();

        // 妫€鏌ユ槸鍚︽湁瓒冲鐨勮绠楄祫婧?        if (maxCpuPercent > 100 || maxMemoryBytes > systemResource.getHeapMemoryMax()) {
            log.warn("Invalid compute quota for skill: {}", skillId);
            return false;
        }

        ResourceQuota quota = quotas.computeIfAbsent(skillId, k -> new ResourceQuota());
        quota.setMaxCpuPercent(maxCpuPercent);
        quota.setMaxMemoryBytes(maxMemoryBytes);
        log.info("Allocated compute quota for skill: {} = CPU: {}%, Memory: {} bytes",
                skillId, maxCpuPercent, maxMemoryBytes);
        return true;
    }

    // ============================================================
    // 缃戠粶璧勬簮绠＄悊
    // ============================================================

    /**
     * 鑾峰彇缃戠粶璧勬簮淇℃伅
     */
    public NetworkResource getNetworkResource() {
        // 绠€鍖栧疄鐜帮紝瀹為檯搴旈€氳繃缃戠粶鎺ュ彛鑾峰彇
        return NetworkResource.builder()
                .hostname(getHostname())
                .portRangeStart(10000)
                .portRangeEnd(20000)
                .build();
    }

    /**
     * 鐢宠缃戠粶閰嶉
     */
    public boolean allocateNetworkQuota(String skillId, int maxConnections, int maxBandwidthKbps) {
        ResourceQuota quota = quotas.computeIfAbsent(skillId, k -> new ResourceQuota());
        quota.setMaxConnections(maxConnections);
        quota.setMaxBandwidthKbps(maxBandwidthKbps);
        log.info("Allocated network quota for skill: {} = Connections: {}, Bandwidth: {} Kbps",
                skillId, maxConnections, maxBandwidthKbps);
        return true;
    }

    // ============================================================
    // 璧勬簮閰嶉鏌ヨ
    // ============================================================

    /**
     * 鑾峰彇 Skill 鐨勮祫婧愰厤棰?     */
    public ResourceQuota getQuota(String skillId) {
        return quotas.get(skillId);
    }

    /**
     * 鑾峰彇鎵€鏈夐厤棰?     */
    public ConcurrentMap<String, ResourceQuota> getAllQuotas() {
        return new ConcurrentHashMap<>(quotas);
    }

    /**
     * 閲婃斁璧勬簮閰嶉
     */
    public void releaseQuota(String skillId) {
        quotas.remove(skillId);
        usageStats.remove(skillId);
        log.info("Released quota for skill: {}", skillId);
    }

    // ============================================================
    // 璧勬簮浣跨敤缁熻
    // ============================================================

    /**
     * 璁板綍璧勬簮浣跨敤
     */
    public void recordUsage(String skillId, ResourceUsage usage) {
        usageStats.put(skillId, usage);
    }

    /**
     * 鑾峰彇璧勬簮浣跨敤缁熻
     */
    public ResourceUsage getUsage(String skillId) {
        return usageStats.get(skillId);
    }

    // ============================================================
    // 杈呭姪鏂规硶
    // ============================================================

    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    // ============================================================
    // 鏁版嵁绫诲畾涔?    // ============================================================

    @Data
    @Builder
    public static class StorageResource {
        private long totalSpace;
        private long usableSpace;
        private long unallocatedSpace;
        private String path;

        public double getUsagePercent() {
            if (totalSpace == 0) return 0;
            return (double) (totalSpace - usableSpace) / totalSpace * 100;
        }
    }

    @Data
    @Builder
    public static class ComputeResource {
        private int availableProcessors;
        private double systemLoadAverage;
        private long heapMemoryUsed;
        private long heapMemoryMax;
        private long nonHeapMemoryUsed;

        public double getMemoryUsagePercent() {
            if (heapMemoryMax == 0) return 0;
            return (double) heapMemoryUsed / heapMemoryMax * 100;
        }
    }

    @Data
    @Builder
    public static class NetworkResource {
        private String hostname;
        private int portRangeStart;
        private int portRangeEnd;
    }

    @Data
    public static class ResourceQuota {
        // 瀛樺偍閰嶉
        private long storageQuota;

        // 璁＄畻閰嶉
        private int maxCpuPercent;
        private long maxMemoryBytes;

        // 缃戠粶閰嶉
        private int maxConnections;
        private int maxBandwidthKbps;
    }

    @Data
    @Builder
    public static class ResourceUsage {
        private String skillId;
        private long storageUsed;
        private long memoryUsed;
        private int cpuPercent;
        private int activeConnections;
        private long timestamp;
    }
}
