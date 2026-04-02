package net.ooder.skill.hotplug;

import net.ooder.skill.hotplug.classloader.ClassLoaderManager;
import net.ooder.skill.hotplug.classloader.PluginClassLoader;
import net.ooder.skill.hotplug.config.ServiceDefinition;
import net.ooder.skill.hotplug.config.SkillConfiguration;
import net.ooder.skill.hotplug.exception.PluginException;
import net.ooder.skill.hotplug.model.*;
import net.ooder.skill.hotplug.registry.RouteRegistry;
import net.ooder.skill.hotplug.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Skill热插拔管理器
 * 负责Skill的安装、卸载、更新和生命周期管理
 */
@Component
public class PluginManager {

    private static final Logger logger = LoggerFactory.getLogger(PluginManager.class);

    @Autowired
    private ClassLoaderManager classLoaderManager;

    @Autowired
    private RouteRegistry routeRegistry;

    @Autowired
    private ServiceRegistry serviceRegistry;

    @Autowired
    private HotPlugProperties properties;

    // 已激活的插件上下文
    private final Map<String, PluginContext> activePlugins = new ConcurrentHashMap<>();

    // 插件状态监听器
    private final List<PluginStateListener> stateListeners = new java.util.concurrent.CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        logger.info("Initializing PluginManager...");
        // 初始化插件目录
        initPluginDirectory();
        // 从多来源加载插件
        loadSkillsFromSources();
    }

    /**
     * 从多来源加载 Skill
     * 支持 system、plugins、external 三种来源
     */
    public void loadSkillsFromSources() {
        if (!properties.isEnabled() || !properties.isAutoLoad()) {
            logger.info("PluginManager auto-load is disabled");
            return;
        }

        List<HotPlugProperties.SkillSource> sources = properties.getSources();
        
        // 如果没有配置 sources，使用向后兼容的方式加载
        if (sources == null || sources.isEmpty()) {
            logger.info("No sources configured, using backward compatible loading");
            loadInstalledPlugins();
            return;
        }

        // 按优先级排序
        sources.stream()
                .filter(HotPlugProperties.SkillSource::isEnabled)
                .sorted(Comparator.comparingInt(HotPlugProperties.SkillSource::getPriority))
                .forEach(this::loadFromSource);
    }

    /**
     * 从指定来源加载 Skill
     */
    private void loadFromSource(HotPlugProperties.SkillSource source) {
        String type = source.getType();
        String path = source.getPath();
        
        logger.info("Loading skills from source: type={}, path={}", type, path);
        
        try {
            switch (type) {
                case "system":
                    loadFromSystem(path);
                    break;
                case "plugins":
                    loadFromPlugins(path);
                    break;
                case "external":
                    loadFromExternal(path);
                    break;
                default:
                    logger.warn("Unknown source type: {}", type);
            }
        } catch (Exception e) {
            logger.error("Failed to load skills from source: type={}, path={}", type, path, e);
        }
    }

    /**
     * 从 system 目录加载 Skill
     */
    public void loadFromSystem(String path) {
        logger.info("Loading system skills from: {}", path);
        loadFromDirectory(path, "system");
    }

    /**
     * 从 plugins 目录加载 Skill
     */
    public void loadFromPlugins(String path) {
        logger.info("Loading plugin skills from: {}", path);
        loadFromDirectory(path, "plugins");
    }

    /**
     * 从 external 目录加载 Skill
     */
    public void loadFromExternal(String path) {
        logger.info("Loading external skills from: {}", path);
        loadFromDirectory(path, "external");
    }

    /**
     * 从目录加载 JAR 文件
     */
    private void loadFromDirectory(String path, String sourceType) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warn("Directory does not exist: {}", path);
            return;
        }

        File[] jarFiles = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            logger.info("No JAR files found in: {}", path);
            return;
        }

        logger.info("Found {} JAR files in {}: {}", jarFiles.length, sourceType, path);
        
        for (File jarFile : jarFiles) {
            try {
                loadSkillJar(jarFile, sourceType);
            } catch (Exception e) {
                logger.error("Failed to load skill JAR: {} from {}", jarFile.getName(), sourceType, e);
            }
        }
    }

    /**
     * 加载单个 Skill JAR 文件
     */
    public PluginInstallResult loadSkillJar(File jarFile, String sourceType) {
        logger.info("Loading skill JAR from {}: {}", sourceType, jarFile.getName());
        
        try {
            SkillPackage skillPackage = SkillPackage.fromFile(jarFile);
            String skillId = skillPackage.getMetadata().getId();
            
            // 防重复加载检查
            if (isInstalled(skillId)) {
                logger.info("Skill {} is already installed (from {}), skipping", skillId, sourceType);
                return PluginInstallResult.success(skillId);
            }
            
            return installSkill(skillPackage);
        } catch (Exception e) {
            logger.error("Failed to load skill JAR: {} from {}", jarFile.getName(), sourceType, e);
            return PluginInstallResult.failure(jarFile.getName(), e.getMessage());
        }
    }

    /**
     * 安装Skill
     */
    public synchronized PluginInstallResult installSkill(SkillPackage skillPackage) {
        String skillId = skillPackage.getMetadata().getId();
        logger.info("Installing skill: {}", skillId);

        try {
            // 检查是否已存在
            if (activePlugins.containsKey(skillId)) {
                return PluginInstallResult.failure(skillId, "Skill already installed: " + skillId);
            }

            // 1. 创建类加载器
            PluginClassLoader classLoader = classLoaderManager.createClassLoader(skillPackage);

            // 2. 加载Skill配置
            SkillConfiguration config = loadSkillConfiguration(skillPackage, classLoader);

            // 3. 创建插件上下文
            PluginContext context = new PluginContext(skillId, classLoader, config, skillPackage);

            // 4. 注册服务
            registerServices(context);

            // 5. 注册路由
            registerRoutes(context);

            // 6. 启动Skill
            startSkill(context);

            // 7. 保存到激活列表
            activePlugins.put(skillId, context);

            // 8. 通知监听器
            notifyListeners(PluginState.INSTALLED, context);

            logger.info("Skill installed successfully: {}", skillId);
            return PluginInstallResult.success(skillId);

        } catch (Exception e) {
            logger.error("Failed to install skill: {}", skillId, e);
            return PluginInstallResult.failure(skillId, e.getMessage());
        }
    }

    /**
     * 卸载Skill
     */
    public synchronized PluginUninstallResult uninstallSkill(String skillId) {
        logger.info("Uninstalling skill: {}", skillId);

        PluginContext context = activePlugins.get(skillId);
        if (context == null) {
            return PluginUninstallResult.failure(skillId, "Skill not found: " + skillId);
        }

        try {
            // 1. 停止Skill
            stopSkill(context);

            // 2. 注销路由
            unregisterRoutes(context);

            // 3. 注销服务
            unregisterServices(context);

            // 4. 关闭类加载器
            classLoaderManager.destroyClassLoader(skillId);

            // 5. 从激活列表移除
            activePlugins.remove(skillId);

            // 6. 通知监听器
            notifyListeners(PluginState.UNINSTALLED, context);

            logger.info("Skill uninstalled successfully: {}", skillId);
            return PluginUninstallResult.success(skillId);

        } catch (Exception e) {
            logger.error("Failed to uninstall skill: {}", skillId, e);
            return PluginUninstallResult.failure(skillId, e.getMessage());
        }
    }

    /**
     * 更新Skill
     */
    public synchronized PluginUpdateResult updateSkill(String skillId, SkillPackage newPackage) {
        logger.info("Updating skill: {}", skillId);

        PluginContext oldContext = activePlugins.get(skillId);
        if (oldContext == null) {
            return PluginUpdateResult.failure(skillId, "Skill not found: " + skillId);
        }

        try {
            // 1. 备份旧版本
            PluginContext backup = oldContext;

            // 2. 卸载旧版本（保留配置）
            stopSkill(oldContext);
            unregisterRoutes(oldContext);
            unregisterServices(oldContext);

            // 3. 安装新版本
            PluginClassLoader newClassLoader = classLoaderManager.createClassLoader(newPackage);
            SkillConfiguration newConfig = loadSkillConfiguration(newPackage, newClassLoader);

            // 合并配置（保留用户配置）
            newConfig.merge(backup.getConfiguration());

            PluginContext newContext = new PluginContext(skillId, newClassLoader, newConfig);

            // 4. 注册新服务
            registerServices(newContext);

            // 5. 注册新路由
            registerRoutes(newContext);

            // 6. 启动新版本
            startSkill(newContext);

            // 7. 替换上下文
            activePlugins.put(skillId, newContext);

            // 8. 销毁旧类加载器
            classLoaderManager.destroyClassLoader(skillId + "_old");

            // 9. 通知监听器
            notifyListeners(PluginState.UPDATED, newContext);

            logger.info("Skill updated successfully: {}", skillId);
            return PluginUpdateResult.success(skillId);

        } catch (Exception e) {
            logger.error("Failed to update skill: {}", skillId, e);
            // 回滚到旧版本
            return PluginUpdateResult.failure(skillId, e.getMessage());
        }
    }

    /**
     * 获取已安装的Skill列表
     */
    public List<PluginInfo> getInstalledSkills() {
        return activePlugins.values().stream()
                .map(this::toPluginInfo)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定Skill信息
     */
    public PluginInfo getSkillInfo(String skillId) {
        PluginContext context = activePlugins.get(skillId);
        return context != null ? toPluginInfo(context) : null;
    }

    /**
     * 获取指定Skill的类加载器
     */
    public PluginClassLoader getClassLoader(String skillId) {
        PluginContext context = activePlugins.get(skillId);
        return context != null ? context.getClassLoader() : null;
    }

    /**
     * 检查Skill是否已安装
     */
    public boolean isInstalled(String skillId) {
        return activePlugins.containsKey(skillId);
    }

    /**
     * 添加状态监听器
     */
    public void addStateListener(PluginStateListener listener) {
        stateListeners.add(listener);
    }

    /**
     * 移除状态监听器
     */
    public void removeStateListener(PluginStateListener listener) {
        stateListeners.remove(listener);
    }

    // ==================== 私有方法 ====================

    private void initPluginDirectory() {
        try {
            Path pluginDir = Paths.get(properties.getPluginDirectory());
            if (!Files.exists(pluginDir)) {
                Files.createDirectories(pluginDir);
                logger.info("Created plugin directory: {}", pluginDir);
            }
        } catch (IOException e) {
            throw new PluginException("Failed to create plugin directory", e);
        }
    }

    private void loadInstalledPlugins() {
        File pluginDir = new File(properties.getPluginDirectory());
        File[] pluginFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if (pluginFiles != null) {
            for (File file : pluginFiles) {
                try {
                    SkillPackage skillPackage = SkillPackage.fromFile(file);
                    installSkill(skillPackage);
                } catch (Exception e) {
                    logger.error("Failed to load plugin: {}", file.getName(), e);
                }
            }
        }
    }

    private SkillConfiguration loadSkillConfiguration(SkillPackage skillPackage, 
                                                       PluginClassLoader classLoader) {
        try {
            // 从skill.yaml加载配置
            return SkillConfiguration.load(skillPackage.getResource("skill.yaml"), classLoader);
        } catch (Exception e) {
            throw new PluginException("Failed to load skill configuration", e);
        }
    }

    private void registerServices(PluginContext context) {
        SkillConfiguration config = context.getConfiguration();
        if (config.getServices() != null) {
            for (ServiceDefinition service : config.getServices()) {
                serviceRegistry.registerService(context.getSkillId(), service, context.getClassLoader());
            }
        }
    }

    private void unregisterServices(PluginContext context) {
        serviceRegistry.unregisterServices(context.getSkillId());
    }

    private void registerRoutes(PluginContext context) {
        SkillConfiguration config = context.getConfiguration();
        if (config.getRoutes() == null || config.getRoutes().isEmpty()) {
            logger.warn("No routes defined in skill configuration for: {}", context.getSkillId());
            return;
        }
        logger.info("Registering {} routes for skill: {}", config.getRoutes().size(), context.getSkillId());
        routeRegistry.registerRoutes(context.getSkillId(), config.getRoutes(), context.getClassLoader());
    }

    private void unregisterRoutes(PluginContext context) {
        routeRegistry.unregisterRoutes(context.getSkillId());
    }

    private void startSkill(PluginContext context) {
        try {
            SkillConfiguration config = context.getConfiguration();
            if (config.getLifecycle() != null && config.getLifecycle().getStartup() != null) {
                String startupClass = config.getLifecycle().getStartup();
                Class<?> clazz = context.getClassLoader().loadClass(startupClass);
                SkillLifecycle lifecycle = (SkillLifecycle) clazz.newInstance();
                lifecycle.onStart(context);
                context.setLifecycle(lifecycle);
            }
            context.setState(PluginState.ACTIVE);
        } catch (Exception e) {
            throw new PluginException("Failed to start skill: " + context.getSkillId(), e);
        }
    }

    private void stopSkill(PluginContext context) {
        try {
            SkillLifecycle lifecycle = context.getLifecycle();
            if (lifecycle != null) {
                lifecycle.onStop(context);
            }
            context.setState(PluginState.STOPPED);
        } catch (Exception e) {
            logger.error("Error stopping skill: {}", context.getSkillId(), e);
        }
    }

    private void notifyListeners(PluginState state, PluginContext context) {
        for (PluginStateListener listener : stateListeners) {
            try {
                listener.onStateChange(state, context);
            } catch (Exception e) {
                logger.error("Error notifying listener", e);
            }
        }
    }

    /**
     * 获取插件上下文
     * 用于访问SkillPackage和静态资源
     */
    public PluginContext getPluginContext(String skillId) {
        return activePlugins.get(skillId);
    }

    private PluginInfo toPluginInfo(PluginContext context) {
        PluginInfo info = new PluginInfo();
        info.setSkillId(context.getSkillId());
        info.setState(context.getState());
        info.setConfiguration(context.getConfiguration());
        info.setInstallTime(context.getInstallTime());
        return info;
    }
}
