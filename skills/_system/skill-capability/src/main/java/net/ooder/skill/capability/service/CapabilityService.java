package net.ooder.skill.capability.service;

import net.ooder.skill.capability.model.*;

import java.util.List;

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
}
