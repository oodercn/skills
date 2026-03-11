package net.ooder.skill.capability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class CapabilityRegistry {

    private static final Logger log = LoggerFactory.getLogger(CapabilityRegistry.class);

    private final Map<String, CapabilityDefinition> capabilities = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> skillCapabilities = new ConcurrentHashMap<>();
    private final Map<String, CapabilityMetadata> metadataMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        registerStandardCapabilities();
        log.info("CapabilityRegistry initialized with {} standard capabilities", capabilities.size());
    }

    private void registerStandardCapabilities() {
        register(CapabilityDefinition.dataAccess("org-data-read", "组织数据读取", "读取组织架构和成员数据"));
        register(CapabilityDefinition.dataAccess("file-read", "文件读取", "读取文件内容"));
        register(CapabilityDefinition.dataAccess("file-write", "文件写入", "写入文件内容"));
        register(CapabilityDefinition.authentication("user-auth", "用户认证", "用户身份认证"));
        register(CapabilityDefinition.authentication("sso-auth", "SSO认证", "单点登录认证"));
        register(CapabilityDefinition.authentication("token-validate", "令牌验证", "验证访问令牌"));
        register(CapabilityDefinition.communication("send-message", "发送消息", "发送即时消息"));
        register(CapabilityDefinition.communication("send-email", "发送邮件", "发送电子邮件通知"));
        register(CapabilityDefinition.communication("send-notification", "发送通知", "发送系统通知"));
        register(CapabilityDefinition.integration("sync-data", "数据同步", "同步外部系统数据"));
        register(CapabilityDefinition.integration("api-call", "API调用", "调用外部API"));
        register(CapabilityDefinition.processing("transform-data", "数据转换", "转换数据格式"));
        register(CapabilityDefinition.processing("analyze-data", "数据分析", "分析处理数据"));
        register(CapabilityDefinition.processing("validate-data", "数据验证", "验证数据有效性"));
        register(CapabilityDefinition.storage("store-data", "数据存储", "存储数据到持久化存储"));
        register(CapabilityDefinition.storage("retrieve-data", "数据检索", "从持久化存储检索数据"));
    }

    public void register(CapabilityDefinition capability) {
        if (capability == null || capability.getId() == null) {
            log.warn("Cannot register null capability or capability with null id");
            return;
        }
        capabilities.put(capability.getId(), capability);
        metadataMap.put(capability.getId(), new CapabilityMetadata(capability.getId()));
        log.debug("Registered capability: {} ({})", capability.getName(), capability.getId());
    }

    public void registerForSkill(String skillId, String capabilityId) {
        CapabilityDefinition capability = capabilities.get(capabilityId);
        if (capability == null) {
            log.warn("Capability not found: {}", capabilityId);
            return;
        }
        skillCapabilities.computeIfAbsent(skillId, k -> ConcurrentHashMap.newKeySet()).add(capabilityId);
        log.debug("Registered capability {} for skill {}", capabilityId, skillId);
    }

    public void registerForSkill(String skillId, List<String> capabilityIds) {
        for (String capId : capabilityIds) {
            registerForSkill(skillId, capId);
        }
    }

    public CapabilityDefinition getCapability(String capabilityId) {
        return capabilities.get(capabilityId);
    }

    public CapabilityMetadata getMetadata(String capabilityId) {
        return metadataMap.get(capabilityId);
    }

    public List<CapabilityDefinition> getCapabilitiesForSkill(String skillId) {
        Set<String> capIds = skillCapabilities.get(skillId);
        if (capIds == null || capIds.isEmpty()) {
            return Collections.emptyList();
        }
        return capIds.stream()
                .map(capabilities::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<CapabilityDefinition> getCapabilitiesByCategory(CapabilityCategory category) {
        return capabilities.values().stream()
                .filter(cap -> cap.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<CapabilityDefinition> getAllCapabilities() {
        return new ArrayList<>(capabilities.values());
    }

    public List<CapabilityDefinition> searchCapabilities(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllCapabilities();
        }
        String lowerKeyword = keyword.toLowerCase();
        return capabilities.values().stream()
                .filter(cap -> 
                    cap.getName().toLowerCase().contains(lowerKeyword) ||
                    cap.getDescription().toLowerCase().contains(lowerKeyword) ||
                    cap.getId().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    public boolean hasCapability(String skillId, String capabilityId) {
        Set<String> caps = skillCapabilities.get(skillId);
        return caps != null && caps.contains(capabilityId);
    }

    public void unregisterForSkill(String skillId) {
        skillCapabilities.remove(skillId);
        log.debug("Unregistered all capabilities for skill: {}", skillId);
    }

    public void unregisterCapability(String capabilityId) {
        capabilities.remove(capabilityId);
        metadataMap.remove(capabilityId);
        skillCapabilities.values().forEach(set -> set.remove(capabilityId));
        log.debug("Unregistered capability: {}", capabilityId);
    }

    public void recordUsage(String capabilityId, long executionTime) {
        CapabilityMetadata metadata = metadataMap.get(capabilityId);
        if (metadata != null) {
            metadata.incrementUsageCount();
            metadata.updateLastUsed();
            metadata.addExecutionTime(executionTime);
        }
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCapabilities", capabilities.size());
        stats.put("skillsWithCapabilities", skillCapabilities.size());
        
        Map<String, Long> byCategory = new HashMap<>();
        for (CapabilityCategory cat : CapabilityCategory.values()) {
            byCategory.put(cat.getValue(), 
                    capabilities.values().stream()
                            .filter(c -> c.getCategory() == cat)
                            .count());
        }
        stats.put("byCategory", byCategory);
        
        return stats;
    }
}
