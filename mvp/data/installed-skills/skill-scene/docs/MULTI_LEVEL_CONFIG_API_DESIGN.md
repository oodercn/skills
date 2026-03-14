# 多级配置可视化与API开发方案

## 一、整体架构

### 1.1 系统架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           配置管理系统架构                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        前端可视化层                                   │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │ 系统配置页  │ │ 技能配置页  │ │ 场景配置页  │ │ 继承链视图  │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ↓ REST API                               │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                         API服务层                                     │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │ConfigAPI    │ │InheritAPI   │ │ProfileAPI   │ │ValidateAPI  │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ↓                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        服务层                                         │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │              ConfigLoaderService (配置装载服务)               │    │   │
│  │  └─────────────────────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │           ConfigInheritanceResolver (继承解析服务)            │    │   │
│  │  └─────────────────────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────────────────────┐    │   │
│  │  │              ConfigMergeService (配置合并服务)                │    │   │
│  │  └─────────────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                    ↓                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                      SDK JSON存储层                                   │   │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │   │
│  │  │SystemConfig │ │SkillConfig  │ │SceneConfig  │ │ProfileConfig│   │   │
│  │  │   .json     │ │   .json     │ │   .json     │ │   .json     │   │   │
│  │  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 二、API开发方案

### 2.1 API端点设计

#### 系统配置API

```java
@RestController
@RequestMapping("/api/v1/config")
public class SystemConfigController {

    @GetMapping("/system")
    public ResponseEntity<SystemConfigDTO> getSystemConfig();

    @GetMapping("/system/capabilities/{address}")
    public ResponseEntity<CapabilityConfigDTO> getCapabilityConfig(
        @PathVariable String address);

    @PutMapping("/system/capabilities/{address}")
    public ResponseEntity<Void> updateCapabilityConfig(
        @PathVariable String address,
        @RequestBody Map<String, Object> config);

    @GetMapping("/system/profile")
    public ResponseEntity<ProfileDTO> getCurrentProfile();

    @PutMapping("/system/profile")
    public ResponseEntity<Void> switchProfile(
        @RequestBody ProfileSwitchRequest request);
}
```

#### 技能配置API

```java
@RestController
@RequestMapping("/api/v1/config/skills")
public class SkillConfigController {

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillConfigDTO> getSkillConfig(
        @PathVariable String skillId,
        @RequestParam(defaultValue = "true") boolean resolveInheritance);

    @GetMapping("/{skillId}/capabilities/{address}")
    public ResponseEntity<CapabilityConfigDTO> getSkillCapabilityConfig(
        @PathVariable String skillId,
        @PathVariable String address);

    @PutMapping("/{skillId}")
    public ResponseEntity<Void> updateSkillConfig(
        @PathVariable String skillId,
        @RequestBody SkillConfigUpdateRequest request);

    @GetMapping("/{skillId}/inheritance")
    public ResponseEntity<ConfigInheritanceDTO> getConfigInheritance(
        @PathVariable String skillId);

    @DeleteMapping("/{skillId}/keys/{key}")
    public ResponseEntity<Void> resetConfigToInherited(
        @PathVariable String skillId,
        @PathVariable String key);
}
```

#### 场景配置API

```java
@RestController
@RequestMapping("/api/v1/config/scenes")
public class SceneConfigController {

    @GetMapping("/{sceneId}")
    public ResponseEntity<SceneConfigDTO> getSceneConfig(
        @PathVariable String sceneId,
        @RequestParam(defaultValue = "true") boolean resolveInheritance);

    @PutMapping("/{sceneId}")
    public ResponseEntity<Void> updateSceneConfig(
        @PathVariable String sceneId,
        @RequestBody SceneConfigUpdateRequest request);

    @GetMapping("/{sceneId}/skills/{skillId}")
    public ResponseEntity<SkillConfigDTO> getInternalSkillConfig(
        @PathVariable String sceneId,
        @PathVariable String skillId);

    @PutMapping("/{sceneId}/skills/{skillId}")
    public ResponseEntity<Void> updateInternalSkillConfig(
        @PathVariable String sceneId,
        @PathVariable String skillId,
        @RequestBody SkillConfigUpdateRequest request);

    @GetMapping("/{sceneId}/inheritance-tree")
    public ResponseEntity<ConfigInheritanceTreeDTO> getInheritanceTree(
        @PathVariable String sceneId);
}
```

#### 配置预览与验证API

```java
@RestController
@RequestMapping("/api/v1/config")
public class ConfigPreviewController {

    @PostMapping("/preview")
    public ResponseEntity<ConfigPreviewDTO> previewMergedConfig(
        @RequestBody ConfigPreviewRequest request);

    @PostMapping("/validate")
    public ResponseEntity<ValidationResultDTO> validateConfig(
        @RequestBody ConfigValidateRequest request);

    @PostMapping("/diff")
    public ResponseEntity<ConfigDiffDTO> compareConfigs(
        @RequestBody ConfigCompareRequest request);
}
```

### 2.2 DTO定义

```java
public class SystemConfigDTO {
    private String apiVersion;
    private ConfigMetadata metadata;
    private SystemConfigSpec spec;
    
    public static class ConfigMetadata {
        private String name;
        private String version;
        private String profile;
        private Instant createdAt;
        private Instant updatedAt;
    }
    
    public static class SystemConfigSpec {
        private Map<String, CapabilityConfig> capabilities;
    }
    
    public static class CapabilityConfig {
        private boolean enabled;
        private String defaultDriver;
        private String fallbackDriver;
        private Map<String, Object> config;
    }
}

public class ConfigInheritanceDTO {
    private String targetType;
    private String targetId;
    private List<ConfigLevel> inheritanceChain;
    
    public static class ConfigLevel {
        private String level;
        private String source;
        private Map<String, Object> config;
        private Map<String, String> valueSources;
    }
}

public class ConfigInheritanceTreeDTO {
    private String sceneId;
    private ConfigNode systemLevel;
    private ConfigNode skillLevel;
    private ConfigNode sceneLevel;
    private List<InternalSkillNode> internalSkills;
    
    public static class ConfigNode {
        private String id;
        private String type;
        private Map<String, Object> resolvedConfig;
        private Map<String, Object> overriddenConfig;
    }
}
```

## 三、服务层实现

### 3.1 配置装载服务

```java
@Service
public class ConfigLoaderService {

    private final JsonConfigStorage jsonStorage;
    private final ConfigInheritanceResolver inheritanceResolver;
    private final ConfigCacheService cacheService;

    public ConfigNode loadSystemConfig() {
        return jsonStorage.loadSystemConfig();
    }

    public ConfigNode loadSkillConfig(String skillId, boolean resolveInheritance) {
        ConfigNode systemConfig = loadSystemConfig();
        ConfigNode skillConfig = jsonStorage.loadSkillConfig(skillId);
        
        if (!resolveInheritance || skillConfig == null) {
            return skillConfig != null ? skillConfig : systemConfig;
        }
        
        return inheritanceResolver.merge(systemConfig, skillConfig);
    }

    public ConfigNode loadSceneConfig(String sceneId, boolean resolveInheritance) {
        ConfigNode baseConfig = loadSkillConfig(sceneId, true);
        ConfigNode sceneConfig = jsonStorage.loadSceneConfig(sceneId);
        
        if (!resolveInheritance || sceneConfig == null) {
            return sceneConfig != null ? sceneConfig : baseConfig;
        }
        
        return inheritanceResolver.merge(baseConfig, sceneConfig);
    }

    public ConfigNode loadInternalSkillConfig(String sceneId, String skillId) {
        ConfigNode sceneConfig = loadSceneConfig(sceneId, true);
        ConfigNode internalConfig = jsonStorage.loadInternalSkillConfig(sceneId, skillId);
        
        if (internalConfig == null) {
            return sceneConfig;
        }
        
        return inheritanceResolver.merge(sceneConfig, internalConfig);
    }
}
```

### 3.2 配置继承解析器

```java
@Service
public class ConfigInheritanceResolver {

    private static final Pattern INHERIT_PATTERN = Pattern.compile("\\$\\{inherit(?::([^}]*))?\\}");
    private static final Pattern MERGE_PATTERN = Pattern.compile("\\$\\{merge\\}");
    private static final Pattern APPEND_PATTERN = Pattern.compile("\\$\\{append\\}");

    public Object resolveValue(Object value, Object parentValue, String key) {
        if (!(value instanceof String)) {
            return value;
        }
        
        String strValue = (String) value;
        
        Matcher inheritMatcher = INHERIT_PATTERN.matcher(strValue);
        if (inheritMatcher.matches()) {
            if (parentValue != null) {
                return parentValue;
            }
            String defaultValue = inheritMatcher.group(1);
            return defaultValue != null ? defaultValue : null;
        }
        
        if (MERGE_PATTERN.matcher(strValue).matches()) {
            return deepMerge(parentValue, value);
        }
        
        if (APPEND_PATTERN.matcher(strValue).matches()) {
            return appendToArray(parentValue, value);
        }
        
        return value;
    }

    public ConfigNode merge(ConfigNode parent, ConfigNode child) {
        ConfigNode result = new ConfigNode();
        result.putAll(parent);
        
        for (Map.Entry<String, Object> entry : child.entrySet()) {
            String key = entry.getKey();
            Object childValue = entry.getValue();
            Object parentValue = parent.get(key);
            
            result.put(key, resolveValue(childValue, parentValue, key));
        }
        
        return result;
    }

    private Object deepMerge(Object base, Object overlay) {
        if (!(base instanceof Map) || !(overlay instanceof Map)) {
            return overlay;
        }
        
        Map<String, Object> baseMap = (Map<String, Object>) base;
        Map<String, Object> overlayMap = (Map<String, Object>) overlay;
        Map<String, Object> result = new LinkedHashMap<>(baseMap);
        
        for (Map.Entry<String, Object> entry : overlayMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (result.containsKey(key) && 
                result.get(key) instanceof Map && 
                value instanceof Map) {
                result.put(key, deepMerge(result.get(key), value));
            } else {
                result.put(key, value);
            }
        }
        
        return result;
    }

    private Object appendToArray(Object base, Object addition) {
        List<Object> result = new ArrayList<>();
        
        if (base instanceof List) {
            result.addAll((List<?>) base);
        }
        
        if (addition instanceof List) {
            result.addAll((List<?>) addition);
        } else if (addition instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) addition;
            if (map.containsKey("items")) {
                result.addAll((List<?>) map.get("items"));
            }
        }
        
        return result;
    }
}
```

### 3.3 JSON存储服务

```java
@Service
public class JsonConfigStorage {

    private final Path configRoot;
    private final ObjectMapper objectMapper;

    public JsonConfigStorage(@Value("${ooder.config.root:./config}") String configRoot) {
        this.configRoot = Paths.get(configRoot);
        this.objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule());
    }

    public ConfigNode loadSystemConfig() {
        Path configFile = configRoot.resolve("system-config.json");
        if (!Files.exists(configFile)) {
            return loadDefaultProfile();
        }
        return readJson(configFile);
    }

    public ConfigNode loadProfile(String profileName) {
        Path profileFile = configRoot.resolve("profiles/" + profileName + ".json");
        if (!Files.exists(profileFile)) {
            throw new ConfigNotFoundException("Profile not found: " + profileName);
        }
        return readJson(profileFile);
    }

    public ConfigNode loadSkillConfig(String skillId) {
        Path configFile = configRoot.resolve("runtime/skill-" + skillId + ".json");
        if (!Files.exists(configFile)) {
            return null;
        }
        return readJson(configFile);
    }

    public ConfigNode loadSceneConfig(String sceneId) {
        Path configFile = configRoot.resolve("runtime/scene-" + sceneId + ".json");
        if (!Files.exists(configFile)) {
            return null;
        }
        return readJson(configFile);
    }

    public ConfigNode loadInternalSkillConfig(String sceneId, String skillId) {
        Path configFile = configRoot.resolve(
            "runtime/scene-" + sceneId + "-skill-" + skillId + ".json");
        if (!Files.exists(configFile)) {
            return null;
        }
        return readJson(configFile);
    }

    public void saveSystemConfig(ConfigNode config) {
        Path configFile = configRoot.resolve("system-config.json");
        writeJson(configFile, config);
    }

    public void saveSkillConfig(String skillId, ConfigNode config) {
        Path runtimeDir = configRoot.resolve("runtime");
        ensureDirectory(runtimeDir);
        Path configFile = runtimeDir.resolve("skill-" + skillId + ".json");
        writeJson(configFile, config);
    }

    public void saveSceneConfig(String sceneId, ConfigNode config) {
        Path runtimeDir = configRoot.resolve("runtime");
        ensureDirectory(runtimeDir);
        Path configFile = runtimeDir.resolve("scene-" + sceneId + ".json");
        writeJson(configFile, config);
    }

    private ConfigNode readJson(Path path) {
        try {
            Map<String, Object> data = objectMapper.readValue(path.toFile(), 
                new TypeReference<Map<String, Object>>() {});
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

    private void ensureDirectory(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new ConfigException("Failed to create directory: " + dir, e);
        }
    }
}
```

## 四、前端可视化方案

### 4.1 页面结构

```
/console/pages/
├── config-system.html          # 系统配置管理
├── config-skill.html           # 技能配置管理
├── config-scene.html           # 场景配置管理
└── config-inheritance.html     # 配置继承可视化

/console/js/pages/
├── config-system.js
├── config-skill.js
├── config-scene.js
└── config-inheritance.js

/console/css/pages/
├── config-system.css
├── config-skill.css
├── config-scene.css
└── config-inheritance.css
```

### 4.2 系统配置页面设计

```html
<!-- config-system.html 核心结构 -->
<div class="config-system-page">
    <div class="page-header">
        <h1>系统配置管理</h1>
        <div class="profile-selector">
            <label>部署规模:</label>
            <select id="profile-select">
                <option value="micro">微型 (开发测试)</option>
                <option value="small">小型 (小团队)</option>
                <option value="large">大型 (企业生产)</option>
                <option value="enterprise">企业 (大规模)</option>
            </select>
        </div>
    </div>

    <div class="config-content">
        <div class="capability-tabs">
            <div class="tab active" data-address="llm">LLM</div>
            <div class="tab" data-address="db">数据库</div>
            <div class="tab" data-address="vfs">存储</div>
            <div class="tab" data-address="org">组织</div>
            <div class="tab" data-address="know">知识库</div>
        </div>

        <div class="config-editor" id="config-editor">
        </div>

        <div class="config-actions">
            <button class="btn-preview">预览变更</button>
            <button class="btn-save">保存配置</button>
            <button class="btn-reset">重置默认</button>
        </div>
    </div>
</div>
```

### 4.3 配置继承可视化组件

```javascript
var ConfigInheritanceView = {
    render: function(container, inheritanceTree) {
        var html = '<div class="inheritance-tree">';
        html += this.renderLevel('system', inheritanceTree.systemLevel, 0);
        
        if (inheritanceTree.skillLevel) {
            html += this.renderLevel('skill', inheritanceTree.skillLevel, 1);
        }
        
        if (inheritanceTree.sceneLevel) {
            html += this.renderLevel('scene', inheritanceTree.sceneLevel, 2);
        }
        
        inheritanceTree.internalSkills.forEach(function(skill) {
            html += this.renderLevel('internal-skill', skill, 3);
        }.bind(this));
        
        html += '</div>';
        container.innerHTML = html;
    },

    renderLevel: function(type, node, depth) {
        var indent = depth * 24;
        var typeLabels = {
            'system': '系统配置',
            'skill': '技能配置',
            'scene': '场景配置',
            'internal-skill': '内部技能配置'
        };

        var html = '<div class="config-level" style="margin-left: ' + indent + 'px">';
        html += '<div class="level-header">';
        html += '<span class="level-type">' + typeLabels[type] + '</span>';
        html += '<span class="level-id">' + node.id + '</span>';
        html += '</div>';
        html += '<div class="level-content">';
        html += this.renderConfigDiff(node.resolvedConfig, node.overriddenConfig);
        html += '</div>';
        html += '</div>';
        
        return html;
    },

    renderConfigDiff: function(resolved, overridden) {
        var html = '<table class="config-diff-table">';
        html += '<thead><tr><th>配置项</th><th>继承值</th><th>覆盖值</th><th>来源</th></tr></thead>';
        html += '<tbody>';
        
        for (var key in resolved) {
            var inherited = resolved[key];
            var override = overridden ? overridden[key] : null;
            var isOverridden = override !== undefined && override !== null;
            
            html += '<tr class="' + (isOverridden ? 'overridden' : 'inherited') + '">';
            html += '<td>' + key + '</td>';
            html += '<td class="inherited-value">' + this.formatValue(inherited) + '</td>';
            html += '<td class="override-value">' + (isOverridden ? this.formatValue(override) : '-') + '</td>';
            html += '<td>' + (isOverridden ? '自定义' : '继承') + '</td>';
            html += '</tr>';
        }
        
        html += '</tbody></table>';
        return html;
    },

    formatValue: function(value) {
        if (typeof value === 'object') {
            return '<pre>' + JSON.stringify(value, null, 2) + '</pre>';
        }
        return String(value);
    }
};
```

### 4.4 配置编辑器组件

```javascript
var ConfigEditor = {
    init: function(container, config, schema) {
        this.container = container;
        this.config = config;
        this.schema = schema;
        this.render();
    },

    render: function() {
        var html = '<div class="config-editor-container">';
        html += '<div class="editor-toolbar">';
        html += '<button class="btn-expand-all">展开全部</button>';
        html += '<button class="btn-collapse-all">折叠全部</button>';
        html += '<button class="btn-add-key">添加配置项</button>';
        html += '</div>';
        html += '<div class="editor-body">';
        html += this.renderConfigGroup(this.config, this.schema, '');
        html += '</div>';
        html += '</div>';
        
        this.container.innerHTML = html;
        this.bindEvents();
    },

    renderConfigGroup: function(config, schema, prefix) {
        var html = '<div class="config-group">';
        
        for (var key in config) {
            var fullKey = prefix ? prefix + '.' + key : key;
            var value = config[key];
            var valueSchema = schema ? schema.properties[key] : null;
            
            html += '<div class="config-item">';
            html += '<label>' + key + '</label>';
            
            if (typeof value === 'object' && !Array.isArray(value)) {
                html += '<div class="config-nested">';
                html += this.renderConfigGroup(value, valueSchema, fullKey);
                html += '</div>';
            } else {
                html += this.renderInput(fullKey, value, valueSchema);
            }
            
            html += '</div>';
        }
        
        html += '</div>';
        return html;
    },

    renderInput: function(key, value, schema) {
        var type = schema ? schema.type : typeof value;
        var html = '<div class="config-input-wrapper">';
        
        if (type === 'boolean') {
            html += '<input type="checkbox" name="' + key + '" ' + 
                    (value ? 'checked' : '') + '>';
        } else if (type === 'integer' || type === 'number') {
            html += '<input type="number" name="' + key + '" value="' + value + '">';
        } else if (type === 'enum' && schema.values) {
            html += '<select name="' + key + '">';
            schema.values.forEach(function(v) {
                html += '<option value="' + v + '" ' + (v === value ? 'selected' : '') + '>' + v + '</option>';
            });
            html += '</select>';
        } else {
            html += '<input type="text" name="' + key + '" value="' + value + '">';
        }
        
        html += '<button class="btn-reset-key" data-key="' + key + '" title="重置为继承值">↺</button>';
        html += '</div>';
        
        return html;
    },

    collectConfig: function() {
        var config = {};
        var inputs = this.container.querySelectorAll('input, select');
        
        inputs.forEach(function(input) {
            var key = input.name;
            var value = input.type === 'checkbox' ? input.checked : input.value;
            
            if (input.type === 'number') {
                value = parseFloat(value);
            }
            
            this.setNestedValue(config, key, value);
        }.bind(this));
        
        return config;
    },

    setNestedValue: function(obj, key, value) {
        var parts = key.split('.');
        var current = obj;
        
        for (var i = 0; i < parts.length - 1; i++) {
            if (!current[parts[i]]) {
                current[parts[i]] = {};
            }
            current = current[parts[i]];
        }
        
        current[parts[parts.length - 1]] = value;
    }
};
```

## 五、开发任务分解

### 5.1 后端开发任务

| 任务ID | 任务描述 | 预计工时 | 依赖 |
|--------|----------|----------|------|
| BE-01 | JsonConfigStorage 存储服务 | 4h | 无 |
| BE-02 | ConfigInheritanceResolver 继承解析 | 6h | 无 |
| BE-03 | ConfigLoaderService 装载服务 | 4h | BE-01, BE-02 |
| BE-04 | SystemConfigController API | 3h | BE-03 |
| BE-05 | SkillConfigController API | 3h | BE-03 |
| BE-06 | SceneConfigController API | 3h | BE-03 |
| BE-07 | ConfigPreviewController API | 4h | BE-03 |
| BE-08 | Profile模板加载服务 | 3h | BE-01 |
| BE-09 | 配置验证服务 | 3h | BE-03 |
| BE-10 | 单元测试 | 6h | BE-01~09 |

### 5.2 前端开发任务

| 任务ID | 任务描述 | 预计工时 | 依赖 |
|--------|----------|----------|------|
| FE-01 | config-system.html 页面 | 4h | 无 |
| FE-02 | config-skill.html 页面 | 4h | FE-01 |
| FE-03 | config-scene.html 页面 | 4h | FE-02 |
| FE-04 | config-inheritance.html 页面 | 6h | FE-03 |
| FE-05 | ConfigEditor 组件 | 6h | 无 |
| FE-06 | ConfigInheritanceView 组件 | 4h | 无 |
| FE-07 | API调用服务封装 | 3h | 无 |
| FE-08 | 配置差异对比组件 | 4h | FE-06 |
| FE-09 | 集成测试 | 4h | FE-01~08 |

## 六、接口响应示例

### 6.1 获取技能配置 (含继承链)

```json
GET /api/v1/config/skills/skill-llm-chat?resolveInheritance=true

{
  "skillId": "skill-llm-chat",
  "resolvedConfig": {
    "llm": {
      "enabled": true,
      "default": "skill-llm-deepseek",
      "config": {
        "temperature": 0.8,
        "maxTokens": 4096,
        "timeout": 60000
      }
    }
  },
  "inheritanceChain": [
    {
      "level": "system",
      "source": "system-config.json",
      "config": {
        "temperature": 0.7,
        "maxTokens": 4096,
        "timeout": 60000
      }
    },
    {
      "level": "skill",
      "source": "skill-config.yaml",
      "config": {
        "temperature": 0.8
      },
      "overriddenKeys": ["temperature"]
    }
  ],
  "valueSources": {
    "temperature": "skill",
    "maxTokens": "system",
    "timeout": "system"
  }
}
```

### 6.2 配置继承树

```json
GET /api/v1/config/scenes/skill-daily-report/inheritance-tree

{
  "sceneId": "skill-daily-report",
  "systemLevel": {
    "id": "system",
    "type": "system",
    "resolvedConfig": {
      "llm": { "temperature": 0.7 }
    }
  },
  "skillLevel": {
    "id": "skill-daily-report",
    "type": "skill",
    "resolvedConfig": {
      "llm": { "temperature": 0.75 }
    },
    "overriddenConfig": {
      "llm": { "temperature": 0.75 }
    }
  },
  "sceneLevel": {
    "id": "skill-daily-report-scene",
    "type": "scene",
    "resolvedConfig": {
      "llm": { "temperature": 0.8 }
    },
    "overriddenConfig": {
      "llm": { "temperature": 0.8 }
    }
  },
  "internalSkills": [
    {
      "id": "skill-report-generator",
      "type": "internal-skill",
      "resolvedConfig": {
        "llm": { "temperature": 0.8, "maxTokens": 8192 }
      },
      "overriddenConfig": {
        "llm": { "maxTokens": 8192 }
      }
    }
  ]
}
```

---

**文档版本**: 1.0  
**创建日期**: 2026-03-12  
**作者**: AI Assistant
