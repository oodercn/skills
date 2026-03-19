package net.ooder.skill.hosting.provider;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HostingProviderImpl implements HostingProvider {
    
    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    
    private final Map<String, HostingInstance> instances = new HashMap<>();
    
    @Override
    public String getProviderName() {
        return "skill-hosting";
    }
    
    @Override
    public String getVersion() {
        return "0.7.3";
    }
    
    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        this.initialized = true;
    }
    
    @Override
    public void start() {
        this.running = true;
    }
    
    @Override
    public void stop() {
        this.running = false;
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
    public List<HostingInstance> getAllInstances() {
        return new ArrayList<>(instances.values());
    }
    
    @Override
    public PageResult<HostingInstance> getInstances(int page, int size) {
        List<HostingInstance> allInstances = new ArrayList<>(instances.values());
        int start = page * size;
        int end = Math.min(start + size, allInstances.size());
        List<HostingInstance> pageInstances = start < allInstances.size() 
                ? allInstances.subList(start, end) 
                : new ArrayList<>();
        return new PageResult<>(pageInstances, allInstances.size(), page, size);
    }
    
    @Override
    public HostingInstance getInstance(String instanceId) {
        return instances.get(instanceId);
    }
    
    @Override
    public HostingInstance createInstance(HostingInstance instance) {
        if (instance.getInstanceId() == null) {
            instance.setInstanceId(UUID.randomUUID().toString());
        }
        instance.setCreatedAt(System.currentTimeMillis());
        instance.setUpdatedAt(System.currentTimeMillis());
        instance.setStatus("created");
        instances.put(instance.getInstanceId(), instance);
        return instance;
    }
    
    @Override
    public boolean updateInstance(HostingInstance instance) {
        if (instances.containsKey(instance.getInstanceId())) {
            instance.setUpdatedAt(System.currentTimeMillis());
            instances.put(instance.getInstanceId(), instance);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean deleteInstance(String instanceId) {
        return instances.remove(instanceId) != null;
    }
    
    @Override
    public boolean startInstance(String instanceId) {
        HostingInstance instance = instances.get(instanceId);
        if (instance != null) {
            instance.setStatus("running");
            instance.setUpdatedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean stopInstance(String instanceId) {
        HostingInstance instance = instances.get(instanceId);
        if (instance != null) {
            instance.setStatus("stopped");
            instance.setUpdatedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean restartInstance(String instanceId) {
        HostingInstance instance = instances.get(instanceId);
        if (instance != null) {
            instance.setStatus("running");
            instance.setUpdatedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean scaleInstance(String instanceId, int replicas) {
        HostingInstance instance = instances.get(instanceId);
        if (instance != null) {
            instance.setReplicas(replicas);
            instance.setUpdatedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }
    
    @Override
    public InstanceHealth getHealth(String instanceId) {
        InstanceHealth health = new InstanceHealth();
        health.setInstanceId(instanceId);
        health.setStatus("healthy");
        health.setHealthy(true);
        health.setMessage("Instance is healthy");
        health.setLastCheckTime(System.currentTimeMillis());
        return health;
    }
    
    @Override
    public List<InstanceMetric> getMetrics(String instanceId, long startTime, long endTime) {
        List<InstanceMetric> metrics = new ArrayList<>();
        
        InstanceMetric cpuMetric = new InstanceMetric();
        cpuMetric.setName("cpu_usage");
        cpuMetric.setUnit("percent");
        cpuMetric.setValue(50.0);
        cpuMetric.setTimestamp(System.currentTimeMillis());
        metrics.add(cpuMetric);
        
        InstanceMetric memoryMetric = new InstanceMetric();
        memoryMetric.setName("memory_usage");
        memoryMetric.setUnit("MB");
        memoryMetric.setValue(512.0);
        memoryMetric.setTimestamp(System.currentTimeMillis());
        metrics.add(memoryMetric);
        
        return metrics;
    }
    
    @Override
    public List<HostingLogEntry> getLogs(String instanceId, int lines) {
        List<HostingLogEntry> logs = new ArrayList<>();
        HostingLogEntry entry = new HostingLogEntry();
        entry.setInstanceId(instanceId);
        entry.setLevel("INFO");
        entry.setMessage("Instance running");
        entry.setSource("hosting");
        entry.setTimestamp(System.currentTimeMillis());
        logs.add(entry);
        return logs;
    }
    
    @Override
    public HostingCommandResult executeCommand(String instanceId, String command) {
        HostingCommandResult result = new HostingCommandResult();
        result.setInstanceId(instanceId);
        result.setCommand(command);
        result.setExitCode(0);
        result.setStdout("Command executed successfully");
        result.setStderr("");
        result.setDuration(100);
        return result;
    }
    
    @Override
    public List<String> getSupportedTypes() {
        return Arrays.asList("docker", "kubernetes", "ecs");
    }
    
    @Override
    public HostingQuota getQuota() {
        HostingQuota quota = new HostingQuota();
        quota.setMaxInstances(100);
        quota.setCurrentInstances(instances.size());
        quota.setMaxCpuCores(16);
        quota.setCurrentCpuCores(instances.size() * 2);
        quota.setMaxMemoryMB(32768);
        quota.setCurrentMemoryMB(instances.size() * 512);
        quota.setMaxStorageGB(1000);
        quota.setCurrentStorageGB(instances.size() * 10);
        return quota;
    }
}
