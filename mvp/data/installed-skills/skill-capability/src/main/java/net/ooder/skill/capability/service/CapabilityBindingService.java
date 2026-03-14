package net.ooder.skill.capability.service;

import net.ooder.skill.capability.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CapabilityBindingService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityBindingService.class);
    
    private final Map<String, CapabilityBinding> bindings = new ConcurrentHashMap<>();
    
    public List<CapabilityBinding> listByCapability(String capabilityId) {
        return bindings.values().stream()
            .filter(b -> capabilityId.equals(b.getCapabilityId()))
            .collect(Collectors.toList());
    }
    
    public List<CapabilityBinding> listByAgent(String agentId) {
        return bindings.values().stream()
            .filter(b -> agentId.equals(b.getAgentId()))
            .collect(Collectors.toList());
    }
    
    public List<CapabilityBinding> listByLink(String linkId) {
        return bindings.values().stream()
            .filter(b -> linkId.equals(b.getLinkId()))
            .collect(Collectors.toList());
    }
    
    public CapabilityBinding findById(String bindingId) {
        return bindings.get(bindingId);
    }
    
    public CapabilityBinding bind(String capabilityId, String agentId, String linkId) {
        CapabilityBinding binding = new CapabilityBinding();
        binding.setBindingId("bind-" + UUID.randomUUID().toString().substring(0, 8));
        binding.setCapabilityId(capabilityId);
        binding.setAgentId(agentId);
        binding.setLinkId(linkId);
        binding.setStatus(CapabilityBindingStatus.ACTIVE);
        binding.setCreatedAt(new Date());
        binding.setUpdatedAt(new Date());
        
        bindings.put(binding.getBindingId(), binding);
        log.info("Created binding: {} for capability: {}", binding.getBindingId(), capabilityId);
        return binding;
    }
    
    public void unbind(String bindingId) {
        bindings.remove(bindingId);
        log.info("Removed binding: {}", bindingId);
    }
    
    public void updateStatus(String bindingId, String status) {
        CapabilityBinding binding = bindings.get(bindingId);
        if (binding != null) {
            binding.setStatus(CapabilityBindingStatus.valueOf(status.toUpperCase()));
            binding.setUpdatedAt(new Date());
            log.info("Updated binding status: {} -> {}", bindingId, status);
        }
    }
}
