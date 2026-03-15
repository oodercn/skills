package net.ooder.skill.capability.service;

import net.ooder.skill.capability.model.*;

import java.util.List;
import java.util.Map;

public interface CapabilityService {
    
    List<Capability> findAll();
    
    Capability findById(String capabilityId);
    
    List<Capability> findByType(String type);
    
    List<Capability> findByStatus(CapabilityStatus status);
    
    List<Capability> search(String keyword);
    
    Capability register(Capability capability);
    
    Capability update(Capability capability);
    
    void unregister(String capabilityId);
    
    void updateStatus(String capabilityId, String status);
    
    List<Capability> findByOwnerId(String ownerId);
    
    List<Capability> findByCategory(String category);
    
    List<Capability> findByVisibility(String visibility);
    
    List<Capability> findInstalled();
    
    List<Capability> findActive();
    
    long count();
    
    long countByType(String type);
    
    long countByStatus(CapabilityStatus status);
    
    long countInstalled();
    
    long countActive();
    
    Map<String, Long> getStatistics();
    
    void install(String capabilityId);
    
    void uninstall(String capabilityId);
    
    void enable(String capabilityId);
    
    void disable(String capabilityId);
}
