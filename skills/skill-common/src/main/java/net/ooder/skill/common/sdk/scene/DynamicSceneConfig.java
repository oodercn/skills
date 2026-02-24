package net.ooder.skill.common.sdk.scene;

import lombok.extern.slf4j.Slf4j;
import net.ooder.skill.common.sdk.lifecycle.SkillLifecycleManager;
import net.ooder.skill.common.sdk.registry.SkillRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态场景配置管理器
 * 支持运行时场景配置更新
 *
 * @author SDK Team
 * @version 2.3.0
 * @since 2026-02-24
 */
@Slf4j
@Component
public class DynamicSceneConfig {

    @Autowired
    private SkillRegistry skillRegistry;

    @Autowired
    private SkillLifecycleManager lifecycleManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 场景配置缓存: sceneId -> SceneConfiguration
     */
    private final Map<String, SceneConfiguration> sceneConfigs = new ConcurrentHashMap<>();

    /**
     * 文件监听器
     */
    private WatchService watchService;

    @PostConstruct
    public void init() {
        log.info("DynamicSceneConfig initialized");
        startFileWatcher();
    }

    /**
     * 启动文件监听器
     */
    private void startFileWatcher() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            
            // 监听场景配置目录
            Path configDir = Paths.get(System.getProperty("user.home"), ".ooder", "scenes");
            if (Files.exists(configDir)) {
                configDir.register(watchService, 
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
                
                // 启动监听线程
                Thread watcherThread = new Thread(this::watchConfigFiles);
                watcherThread.setDaemon(true);
                watcherThread.setName("scene-config-watcher");
                watcherThread.start();
                
                log.info("File watcher started for: {}", configDir);
            }
        } catch (IOException e) {
            log.error("Failed to start file watcher", e);
        }
    }

    /**
     * 监听配置文件变化
     */
    private void watchConfigFiles() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WatchKey key = watchService.take();
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path fileName = (Path) event.context();
                    
                    if (fileName.toString().endsWith(".yaml") || fileName.toString().endsWith(".yml")) {
                        String sceneId = fileName.toString().replace(".yaml", "").replace(".yml", "");
                        
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            log.info("Scene config created: {}", sceneId);
                            loadSceneConfig(sceneId);
                        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                            log.info("Scene config modified: {}", sceneId);
                            reloadSceneConfig(sceneId);
                        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                            log.info("Scene config deleted: {}", sceneId);
                            unloadSceneConfig(sceneId);
                        }
                    }
                }
                
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 加载场景配置
     */
    public void loadSceneConfig(String sceneId) {
        try {
            Path configPath = getSceneConfigPath(sceneId);
            if (!Files.exists(configPath)) {
                log.warn("Scene config not found: {}", configPath);
                return;
            }

            SceneConfiguration config = parseConfig(configPath);
            sceneConfigs.put(sceneId, config);
            
            // 自动挂载配置的 Skills
            mountConfiguredSkills(sceneId, config);
            
            log.info("Scene config loaded: {}", sceneId);
            
            // 发布事件
            eventPublisher.publishEvent(new SceneConfigLoadedEvent(this, sceneId, config));
            
        } catch (Exception e) {
            log.error("Failed to load scene config: {}", sceneId, e);
        }
    }

    /**
     * 重新加载场景配置
     */
    public void reloadSceneConfig(String sceneId) {
        // 先卸载旧配置
        unloadSceneConfig(sceneId);
        
        // 加载新配置
        loadSceneConfig(sceneId);
        
        log.info("Scene config reloaded: {}", sceneId);
    }

    /**
     * 卸载场景配置
     */
    public void unloadSceneConfig(String sceneId) {
        SceneConfiguration config = sceneConfigs.remove(sceneId);
        if (config != null) {
            // 卸载该场景的所有 Skills
            for (String skillId : config.getRequiredSkills()) {
                try {
                    lifecycleManager.stop(skillId);
                    lifecycleManager.uninstall(skillId, true);
                } catch (Exception e) {
                    log.error("Failed to unload skill: {} from scene: {}", skillId, sceneId, e);
                }
            }
            
            log.info("Scene config unloaded: {}", sceneId);
            
            // 发布事件
            eventPublisher.publishEvent(new SceneConfigUnloadedEvent(this, sceneId));
        }
    }

    /**
     * 动态添加 Skill 到场景
     */
    public void addSkillToScene(String sceneId, String skillId) {
        SceneConfiguration config = sceneConfigs.get(sceneId);
        if (config == null) {
            throw new SceneNotFoundException("Scene not found: " + sceneId);
        }

        // 安装并启动 Skill
        if (!skillRegistry.hasSkill(skillId)) {
            lifecycleManager.install(skillId, true);
            lifecycleManager.register(skillId, null); // TODO: 提供正确的路径
        }
        
        lifecycleManager.start(skillId);
        
        // 更新配置
        config.getRequiredSkills().add(skillId);
        
        log.info("Skill {} added to scene {}", skillId, sceneId);
        
        // 发布事件
        eventPublisher.publishEvent(new SceneSkillAddedEvent(this, sceneId, skillId));
    }

    /**
     * 从场景移除 Skill
     */
    public void removeSkillFromScene(String sceneId, String skillId) {
        SceneConfiguration config = sceneConfigs.get(sceneId);
        if (config == null) {
            throw new SceneNotFoundException("Scene not found: " + sceneId);
        }

        // 停止并卸载 Skill
        lifecycleManager.stop(skillId);
        
        // 更新配置
        config.getRequiredSkills().remove(skillId);
        
        log.info("Skill {} removed from scene {}", skillId, sceneId);
        
        // 发布事件
        eventPublisher.publishEvent(new SceneSkillRemovedEvent(this, sceneId, skillId));
    }

    /**
     * 更新场景配置参数
     */
    public void updateConfigParam(String sceneId, String key, String value) {
        SceneConfiguration config = sceneConfigs.get(sceneId);
        if (config == null) {
            throw new SceneNotFoundException("Scene not found: " + sceneId);
        }

        String oldValue = config.getParameters().put(key, value);
        
        log.info("Scene {} config updated: {} = {} (was: {})", sceneId, key, value, oldValue);
        
        // 发布配置变更事件
        eventPublisher.publishEvent(new SceneConfigChangedEvent(this, sceneId, key, oldValue, value));
    }

    /**
     * 获取场景配置
     */
    public SceneConfiguration getSceneConfig(String sceneId) {
        return sceneConfigs.get(sceneId);
    }

    /**
     * 获取所有场景ID
     */
    public java.util.List<String> getAllSceneIds() {
        return java.util.List.copyOf(sceneConfigs.keySet());
    }

    /**
     * 获取场景配置路径
     */
    private Path getSceneConfigPath(String sceneId) {
        return Paths.get(System.getProperty("user.home"), ".ooder", "scenes", sceneId + ".yaml");
    }

    /**
     * 解析配置文件
     */
    private SceneConfiguration parseConfig(Path configPath) throws IOException {
        // TODO: 实现 YAML 解析
        // 临时返回空配置
        return SceneConfiguration.builder()
            .sceneId(configPath.getFileName().toString().replace(".yaml", ""))
            .requiredSkills(new java.util.ArrayList<>())
            .parameters(new ConcurrentHashMap<>())
            .build();
    }

    /**
     * 挂载配置的 Skills
     */
    private void mountConfiguredSkills(String sceneId, SceneConfiguration config) {
        for (String skillId : config.getRequiredSkills()) {
            try {
                if (!skillRegistry.hasSkill(skillId)) {
                    lifecycleManager.install(skillId, true);
                }
                lifecycleManager.start(skillId);
            } catch (Exception e) {
                log.error("Failed to mount skill: {} to scene: {}", skillId, sceneId, e);
            }
        }
    }
}
