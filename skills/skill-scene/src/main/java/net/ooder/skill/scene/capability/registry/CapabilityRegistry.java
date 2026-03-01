package net.ooder.skill.scene.capability.registry;

import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.CapabilityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityRegistry {

    private final Map<String, Capability> capabilities = new ConcurrentHashMap<String, Capability>();
    private final Map<String, List<String>> typeIndex = new ConcurrentHashMap<String, List<String>>();
    private final Map<String, List<String>> sceneTypeIndex = new ConcurrentHashMap<String, List<String>>();

    public void register(Capability capability) {
        if (capability == null || capability.getCapabilityId() == null) {
            throw new IllegalArgumentException("Capability and capabilityId must not be null");
        }

        String capId = capability.getCapabilityId();
        capabilities.put(capId, capability);

        if (capability.getType() != null) {
            String typeName = capability.getType().name();
            if (!typeIndex.containsKey(typeName)) {
                typeIndex.put(typeName, new ArrayList<String>());
            }
            if (!typeIndex.get(typeName).contains(capId)) {
                typeIndex.get(typeName).add(capId);
            }
        }

        if (capability.getSupportedSceneTypes() != null) {
            for (String sceneType : capability.getSupportedSceneTypes()) {
                if (!sceneTypeIndex.containsKey(sceneType)) {
                    sceneTypeIndex.put(sceneType, new ArrayList<String>());
                }
                if (!sceneTypeIndex.get(sceneType).contains(capId)) {
                    sceneTypeIndex.get(sceneType).add(capId);
                }
            }
        }
    }

    public void unregister(String capabilityId) {
        Capability capability = capabilities.remove(capabilityId);
        if (capability != null) {
            if (capability.getType() != null) {
                String typeName = capability.getType().name();
                List<String> typeList = typeIndex.get(typeName);
                if (typeList != null) {
                    typeList.remove(capabilityId);
                }
            }

            if (capability.getSupportedSceneTypes() != null) {
                for (String sceneType : capability.getSupportedSceneTypes()) {
                    List<String> sceneList = sceneTypeIndex.get(sceneType);
                    if (sceneList != null) {
                        sceneList.remove(capabilityId);
                    }
                }
            }
        }
    }

    public Capability findById(String capabilityId) {
        return capabilities.get(capabilityId);
    }

    public List<Capability> findAll() {
        return new ArrayList<Capability>(capabilities.values());
    }

    public List<Capability> findByType(CapabilityType type) {
        List<Capability> result = new ArrayList<Capability>();
        List<String> ids = typeIndex.get(type.name());
        if (ids != null) {
            for (String id : ids) {
                Capability cap = capabilities.get(id);
                if (cap != null) {
                    result.add(cap);
                }
            }
        }
        return result;
    }

    public List<Capability> findBySceneType(String sceneType) {
        List<Capability> result = new ArrayList<Capability>();
        List<String> ids = sceneTypeIndex.get(sceneType);
        if (ids != null) {
            for (String id : ids) {
                Capability cap = capabilities.get(id);
                if (cap != null) {
                    result.add(cap);
                }
            }
        }
        return result;
    }

    public List<Capability> search(String query) {
        List<Capability> result = new ArrayList<Capability>();
        String lowerQuery = query.toLowerCase();

        for (Capability cap : capabilities.values()) {
            if (cap.getName() != null && cap.getName().toLowerCase().contains(lowerQuery)) {
                result.add(cap);
            } else if (cap.getDescription() != null && cap.getDescription().toLowerCase().contains(lowerQuery)) {
                result.add(cap);
            } else if (cap.getCapabilityId() != null && cap.getCapabilityId().toLowerCase().contains(lowerQuery)) {
                result.add(cap);
            }
        }
        return result;
    }

    public int size() {
        return capabilities.size();
    }

    public void clear() {
        capabilities.clear();
        typeIndex.clear();
        sceneTypeIndex.clear();
    }
}
