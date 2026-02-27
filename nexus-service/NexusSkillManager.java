package net.ooder.nexus.service.skill;

import net.ooder.skill.mqtt.discovery.SkillDiscoveryService;
import net.ooder.skill.mqtt.discovery.SkillDiscoveryService.SkillInfo;
import net.ooder.skill.hotplug.model.SkillMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Nexus Skill 管理器
 * 
 * 集成 SkillDiscoveryService 实现 Nexus-UI Skill 的发现、安装、运行流程
 */
@Service
public class NexusSkillManager {
    
    private static final Logger log = LoggerFactory.getLogger(NexusSkillManager.class);
    
    private final SkillDiscoveryService discoveryService;
    private final MenuRegistry menuRegistry;
    
    @Value("${nexus.skills.path:./skills}")
    private String skillsPath;
    
    @Value("${nexus.skills.auto-scan:true}")
    private boolean autoScan;
    
    private final Map<String, SkillMetadata> localSkills = new ConcurrentHashMap<>();
    
    public NexusSkillManager(
            SkillDiscoveryService discoveryService,
            MenuRegistry menuRegistry) {
        this.discoveryService = discoveryService;
        this.menuRegistry = menuRegistry;
    }
    
    @PostConstruct
    public void init() {
        log.info("Initializing Nexus Skill Manager...");
        
        File skillsDir = new File(skillsPath);
        if (!skillsDir.exists()) {
            skillsDir.mkdirs();
            log.info("Created skills directory: {}", skillsPath);
        }
        
        if (autoScan) {
            discoverAndRegister();
        }
        
        log.info("Nexus Skill Manager initialized");
    }
    
    @PreDestroy
    public void destroy() {
        log.info("Shutting down Nexus Skill Manager...");
        localSkills.clear();
        log.info("Nexus Skill Manager shut down");
    }
    
    /**
     * 发现并注册所有 Skill
     */
    public void discoverAndRegister() {
        log.info("Discovering skills from: {}", skillsPath);
        
        scanLocalSkills();
        
        List<SkillMetadata> skills = new ArrayList<>(localSkills.values());
        log.info("Discovered {} skills", skills.size());
        
        for (SkillMetadata skill : skills) {
            if (isNexusUiSkill(skill)) {
                menuRegistry.registerSkillMenu(skill);
                log.info("Registered Nexus-UI skill: {}", skill.getId());
            }
        }
    }
    
    /**
     * 扫描本地 Skill 目录
     */
    private void scanLocalSkills() {
        try {
            Path skillsDir = Paths.get(skillsPath);
            if (!Files.exists(skillsDir)) {
                return;
            }
            
            Files.list(skillsDir)
                .filter(Files::isDirectory)
                .forEach(this::scanSkillDirectory);
                
        } catch (Exception e) {
            log.error("Failed to scan local skills", e);
        }
    }
    
    /**
     * 扫描单个 Skill 目录
     */
    private void scanSkillDirectory(Path skillDir) {
        Path skillYaml = skillDir.resolve("skill.yaml");
        if (!Files.exists(skillYaml)) {
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(skillYaml.toFile())) {
            SkillMetadata metadata = SkillMetadata.loadFromYaml(fis);
            if (metadata != null && metadata.getId() != null) {
                localSkills.put(metadata.getId(), metadata);
                log.debug("Loaded skill: {}", metadata.getId());
            }
        } catch (Exception e) {
            log.warn("Failed to load skill from: {}", skillDir, e);
        }
    }
    
    /**
     * 安装 Skill
     *
     * @param skillId Skill ID
     * @return 安装结果
     */
    public boolean installSkill(String skillId) {
        try {
            SkillInfo skillInfo = discoveryService.getSkill(skillId);
            if (skillInfo == null) {
                log.error("Skill not found in registry: {}", skillId);
                return false;
            }
            
            boolean installed = discoveryService.installSkill(skillId, skillsPath);
            if (installed) {
                discoverAndRegister();
                log.info("Installed skill: {}", skillId);
            }
            return installed;
        } catch (Exception e) {
            log.error("Failed to install skill: {}", skillId, e);
            return false;
        }
    }
    
    /**
     * 卸载 Skill
     *
     * @param skillId Skill ID
     * @return 卸载结果
     */
    public boolean uninstallSkill(String skillId) {
        try {
            localSkills.remove(skillId);
            menuRegistry.unregisterSkillMenu(skillId);
            
            Path skillDir = Paths.get(skillsPath, skillId);
            if (Files.exists(skillDir)) {
                deleteDirectory(skillDir);
            }
            
            log.info("Uninstalled skill: {}", skillId);
            return true;
        } catch (Exception e) {
            log.error("Failed to uninstall skill: {}", skillId, e);
            return false;
        }
    }
    
    /**
     * 获取所有已发现的 Skill
     *
     * @return Skill 列表
     */
    public List<SkillMetadata> getAllSkills() {
        return new ArrayList<>(localSkills.values());
    }
    
    /**
     * 获取远程 Skill 列表
     *
     * @return Skill 列表
     */
    public List<SkillInfo> getRemoteSkills() {
        return discoveryService.discoverSkills();
    }
    
    /**
     * 获取指定 Skill
     *
     * @param skillId Skill ID
     * @return Skill 信息
     */
    public SkillMetadata getSkill(String skillId) {
        return localSkills.get(skillId);
    }
    
    /**
     * 刷新 Skill 发现
     */
    public void refresh() {
        localSkills.clear();
        discoverAndRegister();
    }
    
    /**
     * 检查是否为 Nexus-UI Skill
     */
    private boolean isNexusUiSkill(SkillMetadata skill) {
        if (skill == null) {
            return false;
        }
        
        if ("nexus-ui".equals(skill.getType())) {
            return true;
        }
        
        Map<String, Object> ui = skill.getUi();
        return ui != null && ui.containsKey("nexusUi");
    }
    
    /**
     * 递归删除目录
     */
    private void deleteDirectory(Path path) throws Exception {
        if (Files.isDirectory(path)) {
            Files.list(path).forEach(p -> {
                try {
                    deleteDirectory(p);
                } catch (Exception e) {
                    log.warn("Failed to delete: {}", p, e);
                }
            });
        }
        Files.deleteIfExists(path);
    }
}
