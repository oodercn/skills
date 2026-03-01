package net.ooder.skill.scene.capability.service.impl;

import net.ooder.skill.scene.capability.model.Capability;
import net.ooder.skill.scene.capability.model.CapabilityStatus;
import net.ooder.skill.scene.capability.model.CapabilityType;
import net.ooder.skill.scene.capability.registry.CapabilityRegistry;
import net.ooder.skill.scene.capability.service.CapabilityService;
import net.ooder.skill.scene.storage.JsonStorageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CapabilityServiceImpl implements CapabilityService {

    private static final Logger log = LoggerFactory.getLogger(CapabilityServiceImpl.class);
    private static final String STORAGE_KEY = "capabilities";

    private final CapabilityRegistry registry;
    private final JsonStorageService storageService;

    public CapabilityServiceImpl(JsonStorageService storageService) {
        this.storageService = storageService;
        this.registry = new CapabilityRegistry();
        loadFromStorage();
        initDefaultCapabilities();
    }

    private void loadFromStorage() {
        try {
            List<Capability> stored = storageService.loadList(STORAGE_KEY, Capability.class);
            if (stored != null) {
                for (Capability cap : stored) {
                    registry.register(cap);
                }
                log.info("Loaded {} capabilities from storage", stored.size());
            }
        } catch (Exception e) {
            log.warn("Failed to load capabilities from storage: {}", e.getMessage());
        }
    }

    private void saveToStorage() {
        try {
            storageService.saveList(STORAGE_KEY, registry.findAll());
        } catch (Exception e) {
            log.error("Failed to save capabilities to storage: {}", e.getMessage());
        }
    }

    private void initDefaultCapabilities() {
        if (registry.size() == 0) {
            registerDefaultCapabilities();
        }
    }

    private void registerDefaultCapabilities() {
        Capability reportRemind = new Capability();
        reportRemind.setCapabilityId("report-remind");
        reportRemind.setName("日志提醒");
        reportRemind.setDescription("定时提醒员工提交日志");
        reportRemind.setType(CapabilityType.COMMUNICATION);
        reportRemind.getSupportedSceneTypes().add("daily-report");
        register(reportRemind);

        Capability reportSubmit = new Capability();
        reportSubmit.setCapabilityId("report-submit");
        reportSubmit.setName("日志提交");
        reportSubmit.setDescription("员工提交工作日志");
        reportSubmit.setType(CapabilityType.SERVICE);
        reportSubmit.getSupportedSceneTypes().add("daily-report");
        register(reportSubmit);

        Capability reportAggregate = new Capability();
        reportAggregate.setCapabilityId("report-aggregate");
        reportAggregate.setName("日志汇总");
        reportAggregate.setDescription("汇总所有员工日志");
        reportAggregate.setType(CapabilityType.SERVICE);
        reportAggregate.getSupportedSceneTypes().add("daily-report");
        register(reportAggregate);

        Capability reportAnalyze = new Capability();
        reportAnalyze.setCapabilityId("report-analyze");
        reportAnalyze.setName("日志分析");
        reportAnalyze.setDescription("AI分析日志内容");
        reportAnalyze.setType(CapabilityType.AI);
        reportAnalyze.getSupportedSceneTypes().add("daily-report");
        register(reportAnalyze);

        Capability emailSend = new Capability();
        emailSend.setCapabilityId("email-send");
        emailSend.setName("邮件发送");
        emailSend.setDescription("发送邮件通知");
        emailSend.setType(CapabilityType.COMMUNICATION);
        emailSend.getSupportedSceneTypes().add("daily-report");
        emailSend.getSupportedSceneTypes().add("notification");
        register(emailSend);

        log.info("Registered {} default capabilities", registry.size());
    }

    @Override
    public Capability register(Capability capability) {
        if (capability == null || capability.getCapabilityId() == null) {
            throw new IllegalArgumentException("Capability and capabilityId must not be null");
        }

        capability.setUpdateTime(System.currentTimeMillis());
        if (capability.getCreateTime() == 0) {
            capability.setCreateTime(capability.getUpdateTime());
        }
        if (capability.getStatus() == null) {
            capability.setStatus(CapabilityStatus.REGISTERED);
        }

        registry.register(capability);
        saveToStorage();
        log.info("Registered capability: {}", capability.getCapabilityId());
        return capability;
    }

    @Override
    public void unregister(String capabilityId) {
        registry.unregister(capabilityId);
        saveToStorage();
        log.info("Unregistered capability: {}", capabilityId);
    }

    @Override
    public Capability findById(String capabilityId) {
        return registry.findById(capabilityId);
    }

    @Override
    public List<Capability> findAll() {
        return registry.findAll();
    }

    @Override
    public List<Capability> findByType(CapabilityType type) {
        return registry.findByType(type);
    }

    @Override
    public List<Capability> findBySceneType(String sceneType) {
        return registry.findBySceneType(sceneType);
    }

    @Override
    public List<Capability> search(String query) {
        return registry.search(query);
    }

    @Override
    public Capability update(Capability capability) {
        Capability existing = registry.findById(capability.getCapabilityId());
        if (existing == null) {
            throw new IllegalArgumentException("Capability not found: " + capability.getCapabilityId());
        }

        capability.setUpdateTime(System.currentTimeMillis());
        capability.setCreateTime(existing.getCreateTime());

        registry.register(capability);
        saveToStorage();
        log.info("Updated capability: {}", capability.getCapabilityId());
        return capability;
    }

    @Override
    public void updateStatus(String capabilityId, String status) {
        Capability capability = registry.findById(capabilityId);
        if (capability != null) {
            capability.setStatus(CapabilityStatus.valueOf(status));
            capability.setUpdateTime(System.currentTimeMillis());
            saveToStorage();
            log.info("Updated capability status: {} -> {}", capabilityId, status);
        }
    }
}
