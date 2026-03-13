package net.ooder.skill.capability.service;

import net.ooder.skill.capability.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CapabilityServiceImpl implements CapabilityService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityServiceImpl.class);
    
    private final Map<String, Capability> capabilities = new ConcurrentHashMap<>();
    
    @Override
    public List<Capability> findAll() {
        return new ArrayList<>(capabilities.values());
    }
    
    @Override
    public Capability findById(String capabilityId) {
        return capabilities.get(capabilityId);
    }
    
    @Override
    public List<Capability> findByType(String type) {
        return capabilities.values().stream()
            .filter(cap -> cap.getType() != null && cap.getType().name().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Capability> findByStatus(CapabilityStatus status) {
        return capabilities.values().stream()
            .filter(cap -> cap.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Capability> search(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return capabilities.values().stream()
            .filter(cap -> 
                (cap.getName() != null && cap.getName().toLowerCase().contains(lowerKeyword)) ||
                (cap.getDescription() != null && cap.getDescription().toLowerCase().contains(lowerKeyword)) ||
                (cap.getCapabilityId() != null && cap.getCapabilityId().toLowerCase().contains(lowerKeyword))
            )
            .collect(Collectors.toList());
    }
    
    @Override
    public Capability register(Capability capability) {
        if (capability.getCapabilityId() == null) {
            capability.setCapabilityId(UUID.randomUUID().toString());
        }
        capability.setCreatedAt(new Date());
        capability.setUpdatedAt(new Date());
        capabilities.put(capability.getCapabilityId(), capability);
        log.info("Capability registered: {}", capability.getCapabilityId());
        return capability;
    }
    
    @Override
    public Capability update(Capability capability) {
        Capability existing = capabilities.get(capability.getCapabilityId());
        if (existing != null) {
            capability.setUpdatedAt(new Date());
            capability.setCreatedAt(existing.getCreatedAt());
            capabilities.put(capability.getCapabilityId(), capability);
            log.info("Capability updated: {}", capability.getCapabilityId());
        }
        return capability;
    }
    
    @Override
    public void unregister(String capabilityId) {
        capabilities.remove(capabilityId);
        log.info("Capability unregistered: {}", capabilityId);
    }
    
    @Override
    public void updateStatus(String capabilityId, String status) {
        Capability capability = capabilities.get(capabilityId);
        if (capability != null) {
            capability.setStatus(CapabilityStatus.valueOf(status.toUpperCase()));
            capability.setUpdatedAt(new Date());
            log.info("Capability status updated: {} -> {}", capabilityId, status);
        }
    }
}
