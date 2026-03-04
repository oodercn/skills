package net.ooder.skill.scene.capability.service.impl;

import net.ooder.skill.scene.capability.driver.DriverCondition;
import net.ooder.skill.scene.capability.model.CapabilityType;
import net.ooder.skill.scene.capability.service.CapabilityDiscoveryService;
import net.ooder.skill.scene.capability.service.CapabilityService;
import net.ooder.skill.scene.capability.model.Capability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("skillSceneCapabilityDiscoveryService")
public class CapabilityDiscoveryServiceImpl implements CapabilityDiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(CapabilityDiscoveryServiceImpl.class);

    @Autowired
    private CapabilityService capabilityService;

    @Override
    public DiscoveryResult discoverCapabilities(DiscoveryRequest request) {
        log.info("[discoverCapabilities] Discovering capabilities with request: {}", request);
        
        DiscoveryResult result = new DiscoveryResult();
        
        List<Capability> allCapabilities = capabilityService.findAll();
        
        if (request.getType() != null) {
            allCapabilities = allCapabilities.stream()
                .filter(c -> c.getType() == request.getType())
                .collect(Collectors.toList());
        }
        
        if (request.getSceneType() != null && !request.getSceneType().isEmpty()) {
            final String sceneType = request.getSceneType();
            allCapabilities = allCapabilities.stream()
                .filter(c -> c.getSupportedSceneTypes() != null && c.getSupportedSceneTypes().contains(sceneType))
                .collect(Collectors.toList());
        }
        
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            String query = request.getQuery().toLowerCase();
            allCapabilities = allCapabilities.stream()
                .filter(c -> c.getName().toLowerCase().contains(query) ||
                            (c.getDescription() != null && c.getDescription().toLowerCase().contains(query)))
                .collect(Collectors.toList());
        }
        
        List<CapabilityItem> sceneCapabilities = new ArrayList<>();
        List<CapabilityItem> collaborationCapabilities = new ArrayList<>();
        
        for (Capability cap : allCapabilities) {
            CapabilityItem item = convertToItem(cap);
            
            if (cap.getType() == CapabilityType.SCENE) {
                sceneCapabilities.add(item);
            } else if (cap.getType() != CapabilityType.SCENE_GROUP && 
                       cap.getType() != CapabilityType.CAPABILITY_CHAIN) {
                collaborationCapabilities.add(item);
            }
        }
        
        result.setSceneCapabilities(sceneCapabilities);
        result.setCollaborationCapabilities(collaborationCapabilities);
        result.setTotalScene(sceneCapabilities.size());
        result.setTotalCollaboration(collaborationCapabilities.size());
        
        return result;
    }

    @Override
    public CapabilityDetail getCapabilityDetail(String capabilityId) {
        log.info("[getCapabilityDetail] Getting detail for: {}", capabilityId);
        
        Capability capability = capabilityService.findById(capabilityId);
        if (capability == null) {
            return null;
        }
        
        CapabilityDetail detail = new CapabilityDetail();
        detail.setCapabilityId(capability.getCapabilityId());
        detail.setName(capability.getName());
        detail.setDescription(capability.getDescription());
        detail.setType(capability.getType());
        detail.setIcon(capability.getIcon());
        detail.setVersion(capability.getVersion());
        detail.setInstalled(capability.isInstalled());
        detail.setDriverConditions(getDriverConditions(capabilityId));
        detail.setDependencies(capability.getDependencies());
        detail.setOptionalCapabilities(capability.getOptionalCapabilities());
        detail.setConfig(capability.getConfig());
        
        return detail;
    }

    @Override
    public List<DriverCondition> getDriverConditions(String capabilityId) {
        log.info("[getDriverConditions] Getting driver conditions for: {}", capabilityId);
        
        List<DriverCondition> conditions = new ArrayList<>();
        
        if ("daily-log-scene".equals(capabilityId)) {
            DriverCondition daily = new DriverCondition();
            daily.setConditionId("daily-log");
            daily.setName("每日日志");
            daily.setDescription("每日工作日志汇报");
            daily.setSceneType("DAILY_REPORT");
            
            DriverCondition.Trigger trigger = new DriverCondition.Trigger();
            trigger.setType(DriverCondition.TriggerType.SCHEDULE);
            trigger.setCron("0 0 18 * * ?");
            daily.setTriggers(Arrays.asList(trigger));
            
            conditions.add(daily);
            
            DriverCondition weekly = new DriverCondition();
            weekly.setConditionId("weekly-report");
            weekly.setName("周报");
            weekly.setDescription("每周工作汇报");
            weekly.setSceneType("WEEKLY_REPORT");
            conditions.add(weekly);
            
            DriverCondition project = new DriverCondition();
            project.setConditionId("project-log");
            project.setName("项目日志");
            project.setDescription("项目工作日志");
            project.setSceneType("PROJECT_REPORT");
            conditions.add(project);
        }
        
        return conditions;
    }

    private CapabilityItem convertToItem(Capability cap) {
        CapabilityItem item = new CapabilityItem();
        item.setCapabilityId(cap.getCapabilityId());
        item.setName(cap.getName());
        item.setDescription(cap.getDescription());
        item.setType(cap.getType());
        item.setIcon(cap.getIcon());
        item.setVersion(cap.getVersion());
        item.setInstalled(cap.isInstalled());
        item.setSupportedSceneTypes(cap.getSupportedSceneTypes());
        item.setMetadata(cap.getMetadata());
        
        if (cap.getType() == CapabilityType.SCENE) {
            List<DriverConditionInfo> conditions = getDriverConditions(cap.getCapabilityId())
                .stream()
                .map(dc -> {
                    DriverConditionInfo info = new DriverConditionInfo();
                    info.setConditionId(dc.getConditionId());
                    info.setName(dc.getName());
                    info.setDescription(dc.getDescription());
                    info.setSceneType(dc.getSceneType());
                    return info;
                })
                .collect(Collectors.toList());
            item.setDriverConditions(conditions);
        }
        
        return item;
    }
}
