# 分层配置装载架构设计

## 一、架构概述

### 1.1 分层原则

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         分层配置装载架构                                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Layer 3: 应用层 (Application)                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │ 系统启动    │ │ 安装向导    │ │ 配置管理    │ │ 生命周期    │   │   │
│  │  │ SystemInit  │ │ InstallWiz  │ │ ConfigMgr   │ │ Lifecycle   │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ↓ 调用                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Layer 2: 服务层 (Service)                         │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │ConfigLoader │ │ConfigInherit│ │ConfigMerge  │ │ConfigValidate│   │   │
│  │  │ Service     │ │ Resolver    │ │ Service     │ │ Service     │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ↓ 调用                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Layer 1: SDK层 (SDK)                              │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │JsonConfig   │ │ProfileLoader│ │ConfigCache  │ │ConfigWatch  │   │   │
│  │  │ Storage     │ │             │ │             │ │             │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ↓ 读写                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    Layer 0: 存储层 (Storage)                         │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │                    JSON配置文件存储                           │    │   │
│  │  │  config/system-config.json                                   │    │   │
│  │  │  config/profiles/*.json                                      │    │   │
│  │  │  config/runtime/*.json                                       │    │   │
│  │  └─────────────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 层级职责

| 层级 | 职责 | 通用性 | 迁移方向 |
|------|------|--------|----------|
| **SDK层** | 配置存储、缓存、监听 | 最高 | 可独立打包到SDK |
| **服务层** | 配置装载、继承解析、合并 | 高 | 可迁移到SDK |
| **应用层** | 业务集成、生命周期绑定 | 中 | 应用特定 |
| **存储层** | JSON文件读写 | 最高 | 可迁移到SDK |

---

## 二、SDK层设计 (可迁移到SDK)

### 2.1 SDK层接口定义

```java
package net.ooder.sdk.config;

public interface SdkConfigStorage {
    
    ConfigNode loadSystemConfig();
    
    ConfigNode loadProfile(String profileName);
    
    ConfigNode loadSkillConfig(String skillId);
    
    ConfigNode loadSceneConfig(String sceneId);
    
    ConfigNode loadInternalSkillConfig(String sceneId, String skillId);
    
    void saveSystemConfig(ConfigNode config);
    
    void saveSkillConfig(String skillId, ConfigNode config);
    
    void saveSceneConfig(String sceneId, ConfigNode config);
    
    void saveInternalSkillConfig(String sceneId, String skillId, ConfigNode config);
    
    void deleteConfig(String targetType, String targetId);
    
    boolean exists(String targetType, String targetId);
}
```

### 2.2 SDK层实现

```java
package net.ooder.sdk.config.impl;

public class JsonConfigStorageImpl implements SdkConfigStorage {

    private final Path configRoot;
    private final ObjectMapper objectMapper;
    private final ConfigCache cache;
    private final ConfigWatcher watcher;

    public JsonConfigStorageImpl(String configRootPath) {
        this.configRoot = Paths.get(configRootPath);
        this.objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule());
        this.cache = new ConfigCache();
        this.watcher = new ConfigWatcher(this);
        this.watcher.start();
    }

    @Override
    public ConfigNode loadSystemConfig() {
        String cacheKey = "system";
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        
        Path configFile = configRoot.resolve("system-config.json");
        if (!Files.exists(configFile)) {
            return loadDefaultProfile();
        }
        
        ConfigNode config = readJson(configFile);
        cache.put(cacheKey, config);
        return config;
    }

    @Override
    public ConfigNode loadProfile(String profileName) {
        String cacheKey = "profile:" + profileName;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        
        Path profileFile = configRoot.resolve("profiles/" + profileName + ".json");
        if (!Files.exists(profileFile)) {
            throw new ConfigNotFoundException("Profile not found: " + profileName);
        }
        
        ConfigNode config = readJson(profileFile);
        cache.put(cacheKey, config);
        return config;
    }

    @Override
    public ConfigNode loadSkillConfig(String skillId) {
        String cacheKey = "skill:" + skillId;
        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }
        
        Path configFile = configRoot.resolve("runtime/skill-" + skillId + ".json");
        if (!Files.exists(configFile)) {
            return null;
        }
        
        ConfigNode config = readJson(configFile);
        cache.put(cacheKey, config);
        return config;
    }

    @Override
    public void saveSystemConfig(ConfigNode config) {
        Path configFile = configRoot.resolve("system-config.json");
        writeJson(configFile, config);
        cache.invalidate("system");
    }

    private ConfigNode readJson(Path path) {
        try {
            Map<String, Object> data = objectMapper.readValue(
                path.toFile(), 
                new TypeReference<Map<String, Object>>() {}
            );
            return new ConfigNode(data);
        } catch (IOException e) {
            throw new ConfigLoadException("Failed to load config: " + path, e);
        }
    }

    private void writeJson(Path path, ConfigNode config) {
        try {
            objectMapper.writeValue(path.toFile(), config.getData());
        } catch (IOException e) {
            throw new ConfigSaveException("Failed to save config: " + path, e);
        }
    }
}
```

### 2.3 配置缓存

```java
package net.ooder.sdk.config.cache;

public class ConfigCache {

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public ConfigCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public ConfigCache() {
        this(300000);
    }

    public void put(String key, ConfigNode config) {
        cache.put(key, new CacheEntry(config, System.currentTimeMillis()));
    }

    public ConfigNode get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (System.currentTimeMillis() - entry.timestamp > ttlMillis) {
            cache.remove(key);
            return null;
        }
        return entry.config;
    }

    public boolean containsKey(String key) {
        return get(key) != null;
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    public void invalidateAll() {
        cache.clear();
    }

    private static class CacheEntry {
        final ConfigNode config;
        final long timestamp;

        CacheEntry(ConfigNode config, long timestamp) {
            this.config = config;
            this.timestamp = timestamp;
        }
    }
}
```

### 2.4 配置监听器

```java
package net.ooder.sdk.config.watch;

public class ConfigWatcher {

    private final SdkConfigStorage storage;
    private final WatchService watchService;
    private final Map<Path, List<ConfigChangeListener>> listeners = new ConcurrentHashMap<>();
    private volatile boolean running = false;

    public ConfigWatcher(SdkConfigStorage storage) {
        this.storage = storage;
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new ConfigException("Failed to create watch service", e);
        }
    }

    public void registerListener(String configPath, ConfigChangeListener listener) {
        Path path = Paths.get(configPath);
        listeners.computeIfAbsent(path, k -> new ArrayList<>()).add(listener);
    }

    public void start() {
        if (running) return;
        running = true;
        
        Thread watchThread = new Thread(this::watchLoop, "config-watcher");
        watchThread.setDaemon(true);
        watchThread.start();
    }

    public void stop() {
        running = false;
        try {
            watchService.close();
        } catch (IOException e) {
            // ignore
        }
    }

    private void watchLoop() {
        while (running) {
            try {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    handleEvent(key, event);
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void handleEvent(WatchKey key, WatchEvent<?> event) {
        Path dir = (Path) key.watchable();
        Path file = dir.resolve((Path) event.context());
        
        List<ConfigChangeListener> pathListeners = listeners.get(dir);
        if (pathListeners != null) {
            ConfigChangeEvent changeEvent = new ConfigChangeEvent(
                file.toString(),
                event.kind().name()
            );
            for (ConfigChangeListener listener : pathListeners) {
                listener.onConfigChange(changeEvent);
            }
        }
    }
}
```

---

## 三、服务层设计

### 3.1 配置装载服务

```java
package net.ooder.skill.scene.config.service;

public interface ConfigLoaderService {

    ConfigNode loadSystemConfig();
    
    ConfigNode loadSkillConfig(String skillId, boolean resolveInheritance);
    
    ConfigNode loadSceneConfig(String sceneId, boolean resolveInheritance);
    
    ConfigNode loadInternalSkillConfig(String sceneId, String skillId);
    
    ConfigInheritanceChain getInheritanceChain(String targetType, String targetId);
    
    void saveConfig(String targetType, String targetId, ConfigNode config);
    
    void resetConfig(String targetType, String targetId, String key);
}
```

### 3.2 配置装载服务实现

```java
package net.ooder.skill.scene.config.service.impl;

@Service
public class ConfigLoaderServiceImpl implements ConfigLoaderService {

    private final SdkConfigStorage sdkStorage;
    private final ConfigInheritanceResolver inheritanceResolver;

    public ConfigLoaderServiceImpl(SdkConfigStorage sdkStorage) {
        this.sdkStorage = sdkStorage;
        this.inheritanceResolver = new ConfigInheritanceResolver();
    }

    @Override
    public ConfigNode loadSystemConfig() {
        return sdkStorage.loadSystemConfig();
    }

    @Override
    public ConfigNode loadSkillConfig(String skillId, boolean resolveInheritance) {
        ConfigNode systemConfig = loadSystemConfig();
        ConfigNode skillConfig = sdkStorage.loadSkillConfig(skillId);
        
        if (!resolveInheritance || skillConfig == null) {
            return skillConfig != null ? skillConfig : systemConfig;
        }
        
        return inheritanceResolver.merge(systemConfig, skillConfig);
    }

    @Override
    public ConfigNode loadSceneConfig(String sceneId, boolean resolveInheritance) {
        ConfigNode baseConfig = loadSkillConfig(sceneId, true);
        ConfigNode sceneConfig = sdkStorage.loadSceneConfig(sceneId);
        
        if (!resolveInheritance || sceneConfig == null) {
            return sceneConfig != null ? sceneConfig : baseConfig;
        }
        
        return inheritanceResolver.merge(baseConfig, sceneConfig);
    }

    @Override
    public ConfigNode loadInternalSkillConfig(String sceneId, String skillId) {
        ConfigNode sceneConfig = loadSceneConfig(sceneId, true);
        ConfigNode internalConfig = sdkStorage.loadInternalSkillConfig(sceneId, skillId);
        
        if (internalConfig == null) {
            return sceneConfig;
        }
        
        return inheritanceResolver.merge(sceneConfig, internalConfig);
    }

    @Override
    public ConfigInheritanceChain getInheritanceChain(String targetType, String targetId) {
        ConfigInheritanceChain chain = new ConfigInheritanceChain();
        chain.setTargetType(targetType);
        chain.setTargetId(targetId);
        
        chain.addLevel("system", "system-config.json", loadSystemConfig());
        
        if ("skill".equals(targetType) || "scene".equals(targetType) || "internal_skill".equals(targetType)) {
            ConfigNode skillConfig = sdkStorage.loadSkillConfig(targetId);
            if (skillConfig != null) {
                chain.addLevel("skill", "skill-config.yaml", skillConfig);
            }
        }
        
        if ("scene".equals(targetType) || "internal_skill".equals(targetType)) {
            ConfigNode sceneConfig = sdkStorage.loadSceneConfig(targetId);
            if (sceneConfig != null) {
                chain.addLevel("scene", "scene-config.yaml", sceneConfig);
            }
        }
        
        if ("internal_skill".equals(targetType)) {
            String[] parts = targetId.split(":");
            if (parts.length == 2) {
                ConfigNode internalConfig = sdkStorage.loadInternalSkillConfig(parts[0], parts[1]);
                if (internalConfig != null) {
                    chain.addLevel("internal_skill", "internal-skill-config.yaml", internalConfig);
                }
            }
        }
        
        return chain;
    }

    @Override
    public void saveConfig(String targetType, String targetId, ConfigNode config) {
        switch (targetType) {
            case "system":
                sdkStorage.saveSystemConfig(config);
                break;
            case "skill":
                sdkStorage.saveSkillConfig(targetId, config);
                break;
            case "scene":
                sdkStorage.saveSceneConfig(targetId, config);
                break;
            case "internal_skill":
                String[] parts = targetId.split(":");
                sdkStorage.saveInternalSkillConfig(parts[0], parts[1], config);
                break;
            default:
                throw new IllegalArgumentException("Unknown target type: " + targetType);
        }
    }

    @Override
    public void resetConfig(String targetType, String targetId, String key) {
        ConfigInheritanceChain chain = getInheritanceChain(targetType, targetId);
        ConfigNode inheritedValue = chain.getInheritedValue(key);
        
        if (inheritedValue != null) {
            ConfigNode currentConfig = loadConfigByType(targetType, targetId);
            currentConfig.put(key, inheritedValue);
            saveConfig(targetType, targetId, currentConfig);
        }
    }
}
```

---

## 四、配置融入生命周期

### 4.1 系统启动配置装载

```java
package net.ooder.skill.scene.config.init;

@Component
@Order(1)
public class SystemConfigInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigInitializer.class);

    private final ConfigLoaderService configLoader;
    private final SdkConfigStorage sdkStorage;

    @Value("${ooder.profile:micro}")
    private String profile;

    @Override
    public void run(ApplicationArguments args) {
        log.info("[SystemConfig] Initializing system configuration with profile: {}", profile);
        
        if (!sdkStorage.exists("system", "system")) {
            log.info("[SystemConfig] No system config found, initializing from profile: {}", profile);
            initializeFromProfile(profile);
        }
        
        ConfigNode systemConfig = configLoader.loadSystemConfig();
        log.info("[SystemConfig] System configuration loaded successfully");
        
        initializeCapabilities(systemConfig);
    }

    private void initializeFromProfile(String profile) {
        try {
            ConfigNode profileConfig = sdkStorage.loadProfile(profile);
            sdkStorage.saveSystemConfig(profileConfig);
            log.info("[SystemConfig] System config initialized from profile: {}", profile);
        } catch (ConfigNotFoundException e) {
            log.warn("[SystemConfig] Profile not found: {}, using defaults", profile);
            ConfigNode defaultConfig = createDefaultConfig();
            sdkStorage.saveSystemConfig(defaultConfig);
        }
    }

    private ConfigNode createDefaultConfig() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("apiVersion", "skills.ooder.io/v1");
        config.put("kind", "SystemConfig");
        
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("name", "ooder-skills-system");
        metadata.put("version", "1.0.0");
        metadata.put("profile", profile);
        metadata.put("createdAt", Instant.now().toString());
        config.put("metadata", metadata);
        
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("capabilities", new LinkedHashMap<>());
        config.put("spec", spec);
        
        return new ConfigNode(config);
    }

    private void initializeCapabilities(ConfigNode systemConfig) {
        Map<String, Object> capabilities = systemConfig.getNested("spec.capabilities");
        if (capabilities == null) {
            capabilities = new LinkedHashMap<>();
        }
        
        for (String address : CapabilityAddressSpace.getAddresses()) {
            if (!capabilities.containsKey(address)) {
                capabilities.put(address, createDefaultCapabilityConfig(address));
            }
        }
        
        systemConfig.put("spec.capabilities", capabilities);
        sdkStorage.saveSystemConfig(systemConfig);
    }

    private Map<String, Object> createDefaultCapabilityConfig(String address) {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("enabled", false);
        config.put("default", null);
        config.put("config", new LinkedHashMap<>());
        return config;
    }
}
```

### 4.2 技能安装配置装载

```java
package net.ooder.skill.scene.capability.install;

@Service
public class SkillInstallConfigHandler {

    private final ConfigLoaderService configLoader;
    private final SdkConfigStorage sdkStorage;

    public void onSkillInstall(String skillId, Map<String, Object> userConfig) {
        ConfigNode systemConfig = configLoader.loadSystemConfig();
        
        ConfigNode skillConfig = createSkillConfig(skillId, systemConfig, userConfig);
        
        sdkStorage.saveSkillConfig(skillId, skillConfig);
    }

    public void onSkillUninstall(String skillId) {
        sdkStorage.deleteConfig("skill", skillId);
    }

    public void onSkillUpgrade(String skillId, Map<String, Object> newConfig) {
        ConfigNode existingConfig = sdkStorage.loadSkillConfig(skillId);
        if (existingConfig != null) {
            ConfigNode mergedConfig = mergeConfigs(existingConfig, newConfig);
            sdkStorage.saveSkillConfig(skillId, mergedConfig);
        }
    }

    private ConfigNode createSkillConfig(String skillId, ConfigNode systemConfig, 
                                          Map<String, Object> userConfig) {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("apiVersion", "skills.ooder.io/v1");
        config.put("kind", "SkillRuntimeConfig");
        
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("skillId", skillId);
        metadata.put("installedAt", Instant.now().toString());
        metadata.put("updatedAt", Instant.now().toString());
        config.put("metadata", metadata);
        
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("inheritFrom", "system");
        spec.put("overrides", extractOverrides(userConfig));
        spec.put("userConfig", userConfig);
        config.put("spec", spec);
        
        return new ConfigNode(config);
    }

    private Map<String, Object> extractOverrides(Map<String, Object> userConfig) {
        Map<String, Object> overrides = new LinkedHashMap<>();
        
        for (Map.Entry<String, Object> entry : userConfig.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("capabilities.")) {
                String[] parts = key.split("\\.", 3);
                if (parts.length >= 3) {
                    String capability = parts[1];
                    String configKey = parts[2];
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> capOverrides = (Map<String, Object>) 
                        overrides.computeIfAbsent(capability, k -> new LinkedHashMap<>());
                    capOverrides.put(configKey, entry.getValue());
                }
            }
        }
        
        return overrides;
    }
}
```

### 4.3 场景安装配置装载

```java
package net.ooder.skill.scene.capability.install;

@Service
public class SceneInstallConfigHandler {

    private final ConfigLoaderService configLoader;
    private final SdkConfigStorage sdkStorage;
    private final SkillInstallConfigHandler skillConfigHandler;

    public void onSceneInstall(String sceneId, SceneInstallRequest request) {
        ConfigNode sceneConfig = createSceneConfig(sceneId, request);
        sdkStorage.saveSceneConfig(sceneId, sceneConfig);
        
        if (request.getInternalSkills() != null) {
            for (String internalSkillId : request.getInternalSkills()) {
                ConfigNode internalConfig = createInternalSkillConfig(
                    sceneId, internalSkillId, request.getInternalSkillConfig(internalSkillId)
                );
                sdkStorage.saveInternalSkillConfig(sceneId, internalSkillId, internalConfig);
            }
        }
    }

    public void onSceneUninstall(String sceneId) {
        sdkStorage.deleteConfig("scene", sceneId);
        
        List<String> internalSkills = getInternalSkills(sceneId);
        for (String skillId : internalSkills) {
            sdkStorage.deleteConfig("internal_skill", sceneId + ":" + skillId);
        }
    }

    private ConfigNode createSceneConfig(String sceneId, SceneInstallRequest request) {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("apiVersion", "skills.ooder.io/v1");
        config.put("kind", "SceneRuntimeConfig");
        
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("sceneId", sceneId);
        metadata.put("installedAt", Instant.now().toString());
        metadata.put("updatedAt", Instant.now().toString());
        metadata.put("sceneType", request.getSceneType());
        config.put("metadata", metadata);
        
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("inheritFrom", "skill");
        spec.put("overrides", request.getConfigOverrides());
        spec.put("participants", request.getParticipants());
        spec.put("driverConditions", request.getDriverConditions());
        spec.put("llmConfig", request.getLlmConfig());
        spec.put("knowledgeConfig", request.getKnowledgeConfig());
        config.put("spec", spec);
        
        return new ConfigNode(config);
    }

    private ConfigNode createInternalSkillConfig(String sceneId, String skillId, 
                                                   Map<String, Object> userConfig) {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("apiVersion", "skills.ooder.io/v1");
        config.put("kind", "InternalSkillRuntimeConfig");
        
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("sceneId", sceneId);
        metadata.put("skillId", skillId);
        metadata.put("installedAt", Instant.now().toString());
        config.put("metadata", metadata);
        
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("inheritFrom", "scene");
        spec.put("overrides", userConfig);
        config.put("spec", spec);
        
        return new ConfigNode(config);
    }
}
```

### 4.4 生命周期配置集成

```java
package net.ooder.skill.scene.capability.service.impl;

@Service
public class SceneSkillLifecycleServiceImpl implements SceneSkillLifecycleService {

    private final ConfigLoaderService configLoader;
    private final SceneInstallConfigHandler sceneConfigHandler;

    @Override
    public LifecycleResult activate(String capabilityId, String userId) {
        ConfigNode config = configLoader.loadSceneConfig(capabilityId, true);
        
        Map<String, Object> participants = config.getNested("spec.participants");
        if (participants != null) {
            updateParticipantStatus(participants, userId, "active");
            config.put("spec.participants", participants);
            sceneConfigHandler.updateSceneConfig(capabilityId, config);
        }
        
        return LifecycleResult.success(capabilityId, 
            CapabilityStatus.INSTALLED, CapabilityStatus.ACTIVE);
    }

    @Override
    public LifecycleResult pause(String capabilityId, String userId) {
        ConfigNode config = configLoader.loadSceneConfig(capabilityId, true);
        config.put("spec.status", "paused");
        sceneConfigHandler.updateSceneConfig(capabilityId, config);
        
        return LifecycleResult.success(capabilityId,
            CapabilityStatus.ACTIVE, CapabilityStatus.PAUSED);
    }

    @Override
    public LifecycleResult resume(String capabilityId, String userId) {
        ConfigNode config = configLoader.loadSceneConfig(capabilityId, true);
        config.put("spec.status", "active");
        sceneConfigHandler.updateSceneConfig(capabilityId, config);
        
        return LifecycleResult.success(capabilityId,
            CapabilityStatus.PAUSED, CapabilityStatus.ACTIVE);
    }

    @Override
    public LifecycleResult archive(String capabilityId, String userId) {
        sceneConfigHandler.onSceneUninstall(capabilityId);
        
        return LifecycleResult.success(capabilityId,
            CapabilityStatus.ACTIVE, CapabilityStatus.ARCHIVED);
    }
}
```

---

## 五、配置与生命周期映射

### 5.1 生命周期配置映射表

| 生命周期阶段 | 配置操作 | 配置层级 | 说明 |
|--------------|----------|----------|------|
| **系统启动** | 初始化系统配置 | System | 从Profile加载或创建默认配置 |
| **技能发现** | 读取系统配置 | System | 获取能力默认配置 |
| **技能安装** | 创建技能配置 | Skill | 继承系统配置+用户配置 |
| **场景安装** | 创建场景配置 | Scene | 继承技能配置+场景配置 |
| **内部技能安装** | 创建内部技能配置 | InternalSkill | 继承场景配置 |
| **激活** | 更新参与者状态 | Scene | 更新配置中的参与者状态 |
| **暂停/恢复** | 更新场景状态 | Scene | 更新配置中的状态 |
| **配置变更** | 更新配置覆盖 | Skill/Scene | 合并新配置 |
| **版本升级** | 合并配置 | Skill/Scene | 保留用户配置+合并新默认值 |
| **卸载** | 删除配置 | Skill/Scene | 清理配置文件 |

### 5.2 配置装载时机

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        配置装载时机                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐                                                           │
│  │ 系统启动     │ ──────────────────────────────────────────────────────┐   │
│  │              │                                                     │   │
│  │ 1. 检查配置  │                                                     │   │
│  │ 2. 加载Profile│                                                     │   │
│  │ 3. 初始化17种│                                                     │   │
│  │    能力配置  │                                                     │   │
│  └──────────────┘                                                     │   │
│         ↓                                                             │   │
│  ┌──────────────┐                                                     │   │
│  │ 技能发现     │ ← 读取系统配置获取能力默认值                          │   │
│  │              │                                                     │   │
│  │ 1. 获取能力  │                                                     │   │
│  │    元数据    │                                                     │   │
│  │ 2. 合并系统  │                                                     │   │
│  │    默认配置  │                                                     │   │
│  └──────────────┘                                                     │   │
│         ↓                                                             │   │
│  ┌──────────────┐                                                     │   │
│  │ 技能安装     │ ← 创建技能配置文件                                   │   │
│  │              │                                                     │   │
│  │ 1. 收集用户  │                                                     │   │
│  │    配置      │                                                     │   │
│  │ 2. 创建配置  │                                                     │   │
│  │    文件      │                                                     │   │
│  │ 3. 保存运行  │                                                     │   │
│  │    时配置    │                                                     │   │
│  └──────────────┘                                                     │   │
│         ↓                                                             │   │
│  ┌──────────────┐                                                     │   │
│  │ 场景安装     │ ← 创建场景配置+内部技能配置                           │   │
│  │              │                                                     │   │
│  │ 1. 创建场景  │                                                     │   │
│  │    配置      │                                                     │   │
│  │ 2. 创建内部  │                                                     │   │
│  │    技能配置  │                                                     │   │
│  │ 3. 保存参与  │                                                     │   │
│  │    者信息    │                                                     │   │
│  └──────────────┘                                                     │   │
│         ↓                                                             │   │
│  ┌──────────────┐                                                     │   │
│  │ 激活/使用    │ ← 读取完整配置继承链                                  │   │
│  │              │                                                     │   │
│  │ 1. 解析继承  │                                                     │   │
│  │    链        │                                                     │   │
│  │ 2. 合并配置  │                                                     │   │
│  │ 3. 应用配置  │                                                     │   │
│  └──────────────┘                                                     │   │
│         ↓                                                             │   │
│  ┌──────────────┐                                                     │   │
│  │ 配置变更     │ ← 更新配置覆盖                                        │   │
│  │              │                                                     │   │
│  │ 1. 更新配置  │                                                     │   │
│  │ 2. 失效缓存  │                                                     │   │
│  │ 3. 通知监听  │                                                     │   │
│  └──────────────┘                                                     │   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、SDK迁移计划

### 6.1 可迁移组件清单

| 组件 | 当前位置 | 迁移目标 | 优先级 |
|------|----------|----------|--------|
| SdkConfigStorage | skill-scene | ooder-sdk | 高 |
| ConfigCache | skill-scene | ooder-sdk | 高 |
| ConfigWatcher | skill-scene | ooder-sdk | 中 |
| ConfigNode | skill-scene | ooder-sdk | 高 |
| ConfigInheritanceResolver | skill-scene | ooder-sdk | 高 |

### 6.2 迁移步骤

```
Phase 1: 接口抽象
├── 在 ooder-sdk 中定义配置相关接口
├── 保持 skill-scene 中的实现不变
└── 通过接口调用

Phase 2: 实现迁移
├── 将实现类移动到 ooder-sdk
├── 更新 skill-scene 中的依赖
└── 验证功能正常

Phase 3: 优化重构
├── 优化SDK层实现
├── 添加更多通用功能
└── 完善文档
```

---

**文档版本**: 1.0  
**创建日期**: 2026-03-12  
**作者**: AI Assistant
