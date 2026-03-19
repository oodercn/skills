package net.ooder.skill.hosting.service;

import net.ooder.skill.hosting.dto.*;

import java.util.List;

public interface HostingService {
    List<HostingInstance> getAllInstances();
    PageResult<HostingInstance> getInstances(int page, int size);
    HostingInstance getInstance(String instanceId);
    HostingInstance createInstance(HostingInstance instance);
    boolean deleteInstance(String instanceId);
    boolean startInstance(String instanceId);
    boolean stopInstance(String instanceId);
    boolean scaleInstance(String instanceId, int replicas);
    InstanceHealth getHealth(String instanceId);
    List<CloudProvider> listProviders();
}
