package net.ooder.skill.hosting.service.impl;

import net.ooder.skill.hosting.dto.*;
import net.ooder.skill.hosting.service.HostingService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HostingServiceImpl implements HostingService {

    private final Map<String, HostingInstance> instances = new ConcurrentHashMap<>();
    private final Map<String, CloudProvider> providers = new ConcurrentHashMap<>();

    public HostingServiceImpl() {
        initProviders();
    }

    private void initProviders() {
        CloudProvider kubernetes = new CloudProvider();
        kubernetes.setProviderId("kubernetes");
        kubernetes.setProviderName("Kubernetes");
        kubernetes.setProviderType("container");
        kubernetes.setStatus("available");
        kubernetes.setRegion("default");
        kubernetes.setDescription("Kubernetes container orchestration platform");
        providers.put(kubernetes.getProviderId(), kubernetes);

        CloudProvider aliyun = new CloudProvider();
        aliyun.setProviderId("aliyun");
        aliyun.setProviderName("Aliyun ECS");
        aliyun.setProviderType("cloud");
        aliyun.setStatus("available");
        aliyun.setRegion("cn-hangzhou");
        aliyun.setDescription("Aliyun Elastic Compute Service");
        providers.put(aliyun.getProviderId(), aliyun);

        CloudProvider tencent = new CloudProvider();
        tencent.setProviderId("tencent");
        tencent.setProviderName("Tencent Cloud");
        tencent.setProviderType("cloud");
        tencent.setStatus("available");
        tencent.setRegion("ap-guangzhou");
        tencent.setDescription("Tencent Cloud Virtual Machine");
        providers.put(tencent.getProviderId(), tencent);
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
        List<HostingInstance> pageItems = start < allInstances.size() ? allInstances.subList(start, end) : new ArrayList<>();
        return new PageResult<>(pageItems, page, size, allInstances.size());
    }

    @Override
    public HostingInstance getInstance(String instanceId) {
        return instances.get(instanceId);
    }

    @Override
    public HostingInstance createInstance(HostingInstance instance) {
        if (instance.getInstanceId() == null || instance.getInstanceId().isEmpty()) {
            instance.setInstanceId("inst-" + UUID.randomUUID().toString().substring(0, 8));
        }
        if (instance.getProvider() == null || instance.getProvider().isEmpty()) {
            instance.setProvider("kubernetes");
        }
        instance.setStatus("running");
        instance.setCreatedAt(System.currentTimeMillis());
        instance.setUpdatedAt(System.currentTimeMillis());
        instances.put(instance.getInstanceId(), instance);
        return instance;
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
    public boolean scaleInstance(String instanceId, int replicas) {
        HostingInstance instance = instances.get(instanceId);
        if (instance != null && replicas > 0) {
            instance.setDesiredReplicas(replicas);
            instance.setReplicas(replicas);
            instance.setUpdatedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public InstanceHealth getHealth(String instanceId) {
        HostingInstance instance = instances.get(instanceId);
        if (instance == null) {
            return null;
        }
        InstanceHealth health = new InstanceHealth();
        health.setInstanceId(instanceId);
        health.setStatus(instance.getStatus());
        health.setHealthy("running".equals(instance.getStatus()));
        health.setMessage("running".equals(instance.getStatus()) ? "Instance is healthy" : "Instance is not running");
        health.setCpuUsage(Math.random() * 80);
        health.setMemoryUsage(Math.random() * 70);
        health.setLastCheckTime(System.currentTimeMillis());
        return health;
    }

    @Override
    public List<CloudProvider> listProviders() {
        return new ArrayList<>(providers.values());
    }
}
