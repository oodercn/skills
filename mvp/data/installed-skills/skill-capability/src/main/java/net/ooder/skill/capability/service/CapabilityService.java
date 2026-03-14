package net.ooder.skill.capability.service;

import net.ooder.skill.capability.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CapabilityService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityService.class);
    
    private final Map<String, Capability> capabilities = new ConcurrentHashMap<>();
    
    public List<Capability> findAll() {
        return new ArrayList<>(capabilities.values());
    }
    
    public Capability findById(String capabilityId) {
        return capabilities.get(capabilityId);
    }
    
    public List<Capability> findByType(String type) {
        List<Capability> result = new ArrayList<>();
        for (Capability cap : capabilities.values()) {
            if (cap.getType() != null && cap.getType().name().equalsIgnoreCase(type)) {
                result.add(cap);
            }
        }
        return result;
    }
    
    public List<Capability> findByStatus(CapabilityStatus status) {
        List<Capability> result = new ArrayList<>();
        for (Capability cap : capabilities.values()) {
            if (cap.getStatus() == status) {
                result.add(cap);
            }
        }
        return result;
    }
    
    public List<Capability> search(String keyword) {
        List<Capability> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Capability cap : capabilities.values()) {
            if (cap.getName() != null && cap.getName().toLowerCase().contains(lowerKeyword)) {
                result.add(cap);
            } else if (cap.getDescription() != null && cap.getDescription().toLowerCase().contains(lowerKeyword)) {
                result.add(cap);
            }
        }
        return result;
    }
    
    public Capability register(Capability capability) {
        if (capability.getCapabilityId() == null || capability.getCapabilityId().isEmpty()) {
            capability.setCapabilityId("cap-" + UUID.randomUUID().toString().substring(0, 8));
        }
        capability.setCreatedAt(new Date());
        capability.setUpdatedAt(new Date());
        if (capability.getStatus() == null) {
            capability.setStatus(CapabilityStatus.REGISTERED);
        }
        capabilities.put(capability.getCapabilityId(), capability);
        log.info("Registered capability: {}", capability.getCapabilityId());
        return capability;
    }
    
    public Capability update(Capability capability) {
        String id = capability.getCapabilityId();
        if (!capabilities.containsKey(id)) {
            return null;
        }
        capability.setUpdatedAt(new Date());
        capabilities.put(id, capability);
        log.info("Updated capability: {}", id);
        return capability;
    }
    
    public void unregister(String capabilityId) {
        capabilities.remove(capabilityId);
        log.info("Unregistered capability: {}", capabilityId);
    }
    
    public void updateStatus(String capabilityId, String status) {
        Capability cap = capabilities.get(capabilityId);
        if (cap != null) {
            cap.setStatus(CapabilityStatus.valueOf(status.toUpperCase()));
            cap.setUpdatedAt(new Date());
            log.info("Updated capability status: {} -> {}", capabilityId, status);
        }
    }
}
