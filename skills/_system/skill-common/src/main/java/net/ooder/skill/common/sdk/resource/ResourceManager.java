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

@Slf4j
@Component
public class ResourceManager {

    private MemoryMXBean memoryMXBean;
    private OperatingSystemMXBean osMXBean;

    private final ConcurrentMap<String, ResourceQuota> quotas = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ResourceUsage> usageStats = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        osMXBean = ManagementFactory.getOperatingSystemMXBean();
        log.info("ResourceManager initialized");
    }

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

    public ComputeResource getComputeResource() {
        return ComputeResource.builder()
                .availableProcessors(osMXBean.getAvailableProcessors())
                .systemLoadAverage(osMXBean.getSystemLoadAverage())
                .heapMemoryUsed(memoryMXBean.getHeapMemoryUsage().getUsed())
                .heapMemoryMax(memoryMXBean.getHeapMemoryUsage().getMax())
                .nonHeapMemoryUsed(memoryMXBean.getNonHeapMemoryUsage().getUsed())
                .build();
    }

    public boolean allocateComputeQuota(String skillId, int maxCpuPercent, long maxMemoryBytes) {
        ComputeResource systemResource = getComputeResource();

        if (maxCpuPercent > 100 || maxMemoryBytes > systemResource.getHeapMemoryMax()) {
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

    public NetworkResource getNetworkResource() {
        return NetworkResource.builder()
                .hostname(getHostname())
                .portRangeStart(10000)
                .portRangeEnd(20000)
                .build();
    }

    public boolean allocateNetworkQuota(String skillId, int maxConnections, int maxBandwidthKbps) {
        ResourceQuota quota = quotas.computeIfAbsent(skillId, k -> new ResourceQuota());
        quota.setMaxConnections(maxConnections);
        quota.setMaxBandwidthKbps(maxBandwidthKbps);
        log.info("Allocated network quota for skill: {} = Connections: {}, Bandwidth: {} Kbps",
                skillId, maxConnections, maxBandwidthKbps);
        return true;
    }

    public ResourceQuota getQuota(String skillId) {
        return quotas.get(skillId);
    }

    public ConcurrentMap<String, ResourceQuota> getAllQuotas() {
        return new ConcurrentHashMap<>(quotas);
    }

    public void releaseQuota(String skillId) {
        quotas.remove(skillId);
        usageStats.remove(skillId);
        log.info("Released quota for skill: {}", skillId);
    }

    public void recordUsage(String skillId, ResourceUsage usage) {
        usageStats.put(skillId, usage);
    }

    public ResourceUsage getUsage(String skillId) {
        return usageStats.get(skillId);
    }

    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

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
        private long storageQuota;
        private int maxCpuPercent;
        private long maxMemoryBytes;
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
