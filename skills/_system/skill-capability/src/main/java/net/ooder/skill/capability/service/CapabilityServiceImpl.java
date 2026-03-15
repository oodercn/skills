package net.ooder.skill.capability.service;

import net.ooder.skill.capability.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CapabilityServiceImpl implements CapabilityService {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityServiceImpl.class);
    
    private final Map<String, Capability> capabilities = new ConcurrentHashMap<>();
    
    public CapabilityServiceImpl() {
        initDefaultCapabilities();
        log.info("Initialized {} capabilities", capabilities.size());
    }
    
    private void initDefaultCapabilities() {
        registerCapability("cap-llm-chat", "LLM对话", "大语言模型对话能力", CapabilityType.LLM, 
            "ri-chat-3-line", CapabilityCategory.COMMUNICATION);
        registerCapability("cap-llm-vision", "LLM视觉", "大语言模型视觉理解能力", CapabilityType.LLM, 
            "ri-eye-line", CapabilityCategory.COMMUNICATION);
        registerCapability("cap-llm-embedding", "LLM嵌入", "文本嵌入向量生成能力", CapabilityType.LLM, 
            "ri-code-line", CapabilityCategory.COMMUNICATION);
        
        registerCapability("cap-knowledge-search", "知识检索", "知识库检索能力", CapabilityType.KNOWLEDGE, 
            "ri-search-line", CapabilityCategory.KNOWLEDGE);
        registerCapability("cap-knowledge-index", "知识索引", "知识库索引管理能力", CapabilityType.KNOWLEDGE, 
            "ri-database-2-line", CapabilityCategory.KNOWLEDGE);
        
        registerCapability("cap-vfs-read", "文件读取", "虚拟文件系统读取能力", CapabilityType.VFS, 
            "ri-file-read-line", CapabilityCategory.STORAGE);
        registerCapability("cap-vfs-write", "文件写入", "虚拟文件系统写入能力", CapabilityType.VFS, 
            "ri-file-edit-line", CapabilityCategory.STORAGE);
        
        registerCapability("cap-db-query", "数据库查询", "数据库查询能力", CapabilityType.DRIVER, 
            "ri-database-2-line", CapabilityCategory.STORAGE);
        registerCapability("cap-http-request", "HTTP请求", "HTTP请求能力", CapabilityType.TOOL, 
            "ri-global-line", CapabilityCategory.INTEGRATION);
        
        registerCapability("cap-scene-create", "场景创建", "创建新场景能力", CapabilityType.SCENE, 
            "ri-add-circle-line", CapabilityCategory.BUSINESS);
        registerCapability("cap-scene-manage", "场景管理", "场景管理能力", CapabilityType.SCENE, 
            "ri-settings-3-line", CapabilityCategory.BUSINESS);
        
        log.info("Registered {} default capabilities", capabilities.size());
    }
    
    private void registerCapability(String id, String name, String description, 
            CapabilityType type, String icon, CapabilityCategory category) {
        Capability cap = new Capability();
        cap.setCapabilityId(id);
        cap.setName(name);
        cap.setDescription(description);
        cap.setType(type);
        cap.setIcon(icon);
        cap.setCapabilityCategory(category);
        cap.setStatus(CapabilityStatus.REGISTERED);
        cap.setVisibility(Visibility.PUBLIC.getCode());
        cap.setInstalled(true);
        cap.setOwnerId("system");
        capabilities.put(id, cap);
    }
    
    @Override
    public List<Capability> findAll() {
        return new ArrayList<>(capabilities.values());
    }
    
    @Override
    public Capability findById(String capabilityId) {
        return capabilities.get(capabilityId);
    }
    
    @Override
    public List<Capability> findByType(String type) {
        CapabilityType capType = CapabilityType.fromCode(type);
        if (capType == null) return new ArrayList<>();
        
        return capabilities.values().stream()
            .filter(c -> c.getType() == capType)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Capability> findByStatus(CapabilityStatus status) {
        return capabilities.values().stream()
            .filter(c -> c.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Capability> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        
        String lower = keyword.toLowerCase();
        return capabilities.values().stream()
            .filter(c -> c.getName().toLowerCase().contains(lower) ||
                        c.getDescription().toLowerCase().contains(lower) ||
                        c.getCapabilityId().toLowerCase().contains(lower))
            .collect(Collectors.toList());
    }
    
    @Override
    public Capability register(Capability capability) {
        if (capability.getCapabilityId() == null || capability.getCapabilityId().isEmpty()) {
            capability.setCapabilityId("cap-" + UUID.randomUUID().toString().substring(0, 8));
        }
        capability.setCreateTime(System.currentTimeMillis());
        capability.setUpdateTime(capability.getCreateTime());
        capabilities.put(capability.getCapabilityId(), capability);
        log.info("Registered capability: {}", capability.getCapabilityId());
        return capability;
    }
    
    @Override
    public Capability update(Capability capability) {
        Capability existing = capabilities.get(capability.getCapabilityId());
        if (existing == null) {
            throw new IllegalArgumentException("Capability not found: " + capability.getCapabilityId());
        }
        capability.setUpdateTime(System.currentTimeMillis());
        capability.setCreateTime(existing.getCreateTime());
        capabilities.put(capability.getCapabilityId(), capability);
        log.info("Updated capability: {}", capability.getCapabilityId());
        return capability;
    }
    
    @Override
    public void unregister(String capabilityId) {
        capabilities.remove(capabilityId);
        log.info("Unregistered capability: {}", capabilityId);
    }
    
    @Override
    public void updateStatus(String capabilityId, String status) {
        Capability cap = capabilities.get(capabilityId);
        if (cap != null) {
            cap.setStatus(CapabilityStatus.valueOf(status.toUpperCase()));
            cap.setUpdateTime(System.currentTimeMillis());
        }
    }
    
    @Override
    public List<Capability> findByOwnerId(String ownerId) {
        return capabilities.values().stream()
            .filter(c -> ownerId.equals(c.getOwnerId()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Capability> findByCategory(String category) {
        CapabilityCategory cat = CapabilityCategory.fromCode(category);
        if (cat == null) return new ArrayList<>();
        
        return capabilities.values().stream()
            .filter(c -> c.getCapabilityCategory() == cat)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Capability> findByVisibility(String visibility) {
        Visibility vis = Visibility.fromCode(visibility);
        if (vis == null) return new ArrayList<>();
        
        return capabilities.values().stream()
            .filter(c -> c.getVisibilityEnum() == vis)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Capability> findInstalled() {
        return capabilities.values().stream()
            .filter(Capability::isInstalled)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Capability> findActive() {
        return capabilities.values().stream()
            .filter(c -> c.getStatus() == CapabilityStatus.ENABLED || 
                        c.getStatus() == CapabilityStatus.REGISTERED)
            .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return capabilities.size();
    }
    
    @Override
    public long countByType(String type) {
        CapabilityType capType = CapabilityType.fromCode(type);
        if (capType == null) return 0;
        
        return capabilities.values().stream()
            .filter(c -> c.getType() == capType)
            .count();
    }
    
    @Override
    public long countByStatus(CapabilityStatus status) {
        return capabilities.values().stream()
            .filter(c -> c.getStatus() == status)
            .count();
    }
    
    @Override
    public long countInstalled() {
        return capabilities.values().stream()
            .filter(Capability::isInstalled)
            .count();
    }
    
    @Override
    public long countActive() {
        return capabilities.values().stream()
            .filter(c -> c.getStatus() == CapabilityStatus.ENABLED || 
                        c.getStatus() == CapabilityStatus.REGISTERED)
            .count();
    }
    
    @Override
    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", count());
        stats.put("installed", countInstalled());
        stats.put("active", countActive());
        stats.put("llm", countByType("llm"));
        stats.put("knowledge", countByType("knowledge"));
        stats.put("vfs", countByType("vfs"));
        stats.put("driver", countByType("driver"));
        stats.put("tool", countByType("tool"));
        stats.put("scene", countByType("scene"));
        return stats;
    }
    
    @Override
    public void install(String capabilityId) {
        Capability cap = capabilities.get(capabilityId);
        if (cap != null) {
            cap.setInstalled(true);
            cap.setUpdateTime(System.currentTimeMillis());
            log.info("Installed capability: {}", capabilityId);
        }
    }
    
    @Override
    public void uninstall(String capabilityId) {
        Capability cap = capabilities.get(capabilityId);
        if (cap != null) {
            cap.setInstalled(false);
            cap.setUpdateTime(System.currentTimeMillis());
            log.info("Uninstalled capability: {}", capabilityId);
        }
    }
    
    @Override
    public void enable(String capabilityId) {
        Capability cap = capabilities.get(capabilityId);
        if (cap != null) {
            cap.setStatus(CapabilityStatus.ENABLED);
            cap.setUpdateTime(System.currentTimeMillis());
            log.info("Enabled capability: {}", capabilityId);
        }
    }
    
    @Override
    public void disable(String capabilityId) {
        Capability cap = capabilities.get(capabilityId);
        if (cap != null) {
            cap.setStatus(CapabilityStatus.DISABLED);
            cap.setUpdateTime(System.currentTimeMillis());
            log.info("Disabled capability: {}", capabilityId);
        }
    }
}
