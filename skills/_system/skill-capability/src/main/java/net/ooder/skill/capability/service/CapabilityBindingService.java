package net.ooder.skill.capability.service;

import net.ooder.skill.capability.model.CapabilityBinding;

import java.util.List;

public interface CapabilityBindingService {

    List<CapabilityBinding> listByCapability(String capabilityId);

    List<CapabilityBinding> listByAgent(String agentId);

    List<CapabilityBinding> listByLink(String linkId);

    CapabilityBinding findById(String bindingId);

    CapabilityBinding bind(String capabilityId, String agentId, String linkId);

    void unbind(String bindingId);

    void updateStatus(String bindingId, String status);
}
