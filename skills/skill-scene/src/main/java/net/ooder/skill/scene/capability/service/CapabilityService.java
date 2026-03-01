package net.ooder.skill.scene.capability.service;

import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.CapabilityType;

import java.util.List;

public interface CapabilityService {

    Capability register(Capability capability);

    void unregister(String capabilityId);

    Capability findById(String capabilityId);

    List<Capability> findAll();

    List<Capability> findByType(CapabilityType type);

    List<Capability> findBySceneType(String sceneType);

    List<Capability> search(String query);

    Capability update(Capability capability);

    void updateStatus(String capabilityId, String status);
}
