package net.ooder.skill.scene.capability.service.impl;

import net.ooder.skill.scene.capability.model.*;
import net.ooder.skill.scene.capability.service.CapabilityBindingService;
import net.ooder.skill.scene.storage.JsonStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CapabilityBindingServiceImpl implements CapabilityBindingService {

    private static final Logger log = LoggerFactory.getLogger(CapabilityBindingServiceImpl.class);
    private static final String STORAGE_KEY = "capability_bindings";

    private final Map<String, CapabilityBinding> bindings = new ConcurrentHashMap<String, CapabilityBinding>();
    private final Map<String, List<String>> sceneGroupIndex = new ConcurrentHashMap<String, List<String>>();
    private final Map<String, List<String>> capabilityIndex = new ConcurrentHashMap<String, List<String>>();
    private final JsonStorageService storageService;
    private final AtomicInteger addressCounter = new AtomicInteger(0x40);

    public CapabilityBindingServiceImpl(JsonStorageService storageService) {
        this.storageService = storageService;
        loadFromStorage();
    }

    private void loadFromStorage() {
        try {
            List<CapabilityBinding> stored = storageService.loadList(STORAGE_KEY, CapabilityBinding.class);
            if (stored != null) {
                for (CapabilityBinding binding : stored) {
                    indexBinding(binding);
                }
                log.info("Loaded {} capability bindings from storage", stored.size());
            }
        } catch (Exception e) {
            log.warn("Failed to load capability bindings from storage: {}", e.getMessage());
        }
    }

    private void saveToStorage() {
        try {
            storageService.saveList(STORAGE_KEY, new ArrayList<CapabilityBinding>(bindings.values()));
        } catch (Exception e) {
            log.error("Failed to save capability bindings to storage: {}", e.getMessage());
        }
    }

    private void indexBinding(CapabilityBinding binding) {
        bindings.put(binding.getBindingId(), binding);

        String sceneGroupId = binding.getSceneGroupId();
        if (sceneGroupId != null) {
            if (!sceneGroupIndex.containsKey(sceneGroupId)) {
                sceneGroupIndex.put(sceneGroupId, new ArrayList<String>());
            }
            if (!sceneGroupIndex.get(sceneGroupId).contains(binding.getBindingId())) {
                sceneGroupIndex.get(sceneGroupId).add(binding.getBindingId());
            }
        }

        String capabilityId = binding.getCapabilityId();
        if (capabilityId != null) {
            if (!capabilityIndex.containsKey(capabilityId)) {
                capabilityIndex.put(capabilityId, new ArrayList<String>());
            }
            if (!capabilityIndex.get(capabilityId).contains(binding.getBindingId())) {
                capabilityIndex.get(capabilityId).add(binding.getBindingId());
            }
        }
    }

    private void removeIndex(CapabilityBinding binding) {
        bindings.remove(binding.getBindingId());

        String sceneGroupId = binding.getSceneGroupId();
        if (sceneGroupId != null && sceneGroupIndex.containsKey(sceneGroupId)) {
            sceneGroupIndex.get(sceneGroupId).remove(binding.getBindingId());
        }

        String capabilityId = binding.getCapabilityId();
        if (capabilityId != null && capabilityIndex.containsKey(capabilityId)) {
            capabilityIndex.get(capabilityId).remove(binding.getBindingId());
        }
    }

    private String generateBindingId() {
        return "cb-" + System.currentTimeMillis();
    }

    private String generateCapAddress() {
        int addr = addressCounter.getAndIncrement();
        if (addr > 0x9F) {
            addressCounter.set(0x40);
            addr = addressCounter.getAndIncrement();
        }
        return String.format("%02X:01", addr);
    }

    @Override
    public CapabilityBinding bind(String sceneGroupId, CapabilityBindingRequest request) {
        CapabilityBinding binding = new CapabilityBinding();
        binding.setBindingId(generateBindingId());
        binding.setSceneGroupId(sceneGroupId);
        binding.setCapabilityId(request.getCapabilityId());
        binding.setCapDefId(request.getCapDefId());
        binding.setCapId(request.getCapId() != null ? request.getCapId() : request.getCapabilityId());
        binding.setCapAddress(generateCapAddress());

        if (request.getProviderType() != null) {
            binding.setProviderType(CapabilityProviderType.valueOf(request.getProviderType()));
        } else {
            binding.setProviderType(CapabilityProviderType.SKILL);
        }

        binding.setProviderId(request.getProviderId());

        if (request.getConnectorType() != null) {
            binding.setConnectorType(ConnectorType.valueOf(request.getConnectorType()));
        } else {
            binding.setConnectorType(ConnectorType.HTTP);
        }

        binding.setPriority(request.getPriority());
        binding.setFallback(request.isFallback());
        binding.setStatus(CapabilityBindingStatus.ACTIVE);
        binding.setCreateTime(System.currentTimeMillis());

        indexBinding(binding);
        saveToStorage();

        log.info("Created capability binding: {} -> {}", sceneGroupId, request.getCapabilityId());
        return binding;
    }

    @Override
    public void unbind(String bindingId) {
        CapabilityBinding binding = bindings.get(bindingId);
        if (binding != null) {
            binding.setStatus(CapabilityBindingStatus.RELEASED);
            removeIndex(binding);
            saveToStorage();
            log.info("Unbound capability: {}", bindingId);
        }
    }

    @Override
    public CapabilityBinding findById(String bindingId) {
        return bindings.get(bindingId);
    }

    @Override
    public List<CapabilityBinding> listBySceneGroup(String sceneGroupId) {
        List<CapabilityBinding> result = new ArrayList<CapabilityBinding>();
        List<String> ids = sceneGroupIndex.get(sceneGroupId);
        if (ids != null) {
            for (String id : ids) {
                CapabilityBinding binding = bindings.get(id);
                if (binding != null && binding.getStatus() != CapabilityBindingStatus.RELEASED) {
                    result.add(binding);
                }
            }
        }
        return result;
    }

    @Override
    public List<CapabilityBinding> listByCapability(String capabilityId) {
        List<CapabilityBinding> result = new ArrayList<CapabilityBinding>();
        List<String> ids = capabilityIndex.get(capabilityId);
        if (ids != null) {
            for (String id : ids) {
                CapabilityBinding binding = bindings.get(id);
                if (binding != null && binding.getStatus() != CapabilityBindingStatus.RELEASED) {
                    result.add(binding);
                }
            }
        }
        return result;
    }

    @Override
    public void updateStatus(String bindingId, String status) {
        CapabilityBinding binding = bindings.get(bindingId);
        if (binding != null) {
            binding.setStatus(CapabilityBindingStatus.valueOf(status));
            saveToStorage();
            log.info("Updated binding status: {} -> {}", bindingId, status);
        }
    }

    @Override
    public CapabilityBinding findByCapId(String sceneGroupId, String capId) {
        List<CapabilityBinding> bindings = listBySceneGroup(sceneGroupId);
        for (CapabilityBinding binding : bindings) {
            if (capId.equals(binding.getCapId())) {
                return binding;
            }
        }
        return null;
    }
}
