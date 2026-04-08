# Skills配置装载体系设计

## 一、配置层级架构

### 1.1 三级配置继承体系

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     Skills配置装载体系                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Level 1: 系统级配置 (System Level)                                      │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  系统安装时初始化17种能力配置                                      │   │
│  │  文件: /config/system-config.yaml                                │   │
│  │  作用域: 全局默认配置                                             │   │
│  │  优先级: 最低 (可被覆盖)                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓ 继承                                      │
│  Level 2: 技能级配置 (Skill Level)                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  技能独立配置，覆盖系统默认                                        │   │
│  │  文件: skill-config.yaml (每个skill目录下)                        │   │
│  │  作用域: 单个技能                                                 │   │
│  │  优先级: 中等 (覆盖系统级)                                        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓ 继承                                      │
│  Level 3: 场景级配置 (Scene Level)                                       │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  场景技能配置，内部技能继承                                        │   │
│  │  文件: scene-config.yaml (场景目录下)                             │   │
│  │  作用域: 场景及其内部技能                                          │   │
│  │  优先级: 最高 (覆盖技能级)                                        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 1.2 配置优先级规则

```
配置查找顺序 (优先级从高到低):

1. 场景内部技能独立配置 → scene-config.yaml (技能级覆盖)
2. 场景配置 → scene-config.yaml (场景级)
3. 技能独立配置 → skill-config.yaml (技能级)
4. 系统默认配置 → system-config.yaml (系统级)
5. 硬编码默认值 → 代码内置默认值

配置合并策略:
- 深度合并 (Deep Merge)
- 数组覆盖 (Array Override)
- 特殊字段标记 (如: ${inherit} 表示继承上级)
```

---

## 二、系统级配置 (Level 1)

### 2.1 系统配置文件结构

```yaml
# /config/system-config.yaml
apiVersion: skills.ooder.io/v1
kind: SystemConfig
metadata:
  name: ooder-skills-system
  version: "1.0.0"
  createdAt: "2026-03-12T00:00:00Z"

spec:
  profile: "micro"  # micro | small | large | enterprise
  
  capabilities:
    llm:
      enabled: true
      default: "skill-llm-deepseek"
      fallback: "skill-llm-ollama"
      config:
        temperature: 0.7
        maxTokens: 4096
        timeout: 60000
        
    db:
      enabled: true
      default: "skill-db-mysql"
      config:
        pool:
          maxActive: 20
          maxIdle: 10
          
    vfs:
      enabled: true
      default: "skill-vfs-local"
      config:
        basePath: "/data/storage"
        
    org:
      enabled: true
      default: "skill-org-local"
      
    know:
      enabled: true
      default: "skill-know-rag"
      config:
        topK: 5
        scoreThreshold: 0.7
        
    comm:
      enabled: true
      default: "skill-comm-notify"
      
    auth:
      enabled: true
      default: "skill-auth-user"
      
    mon:
      enabled: true
      default: "skill-mon-health"
      
    payment:
      enabled: false
      
    media:
      enabled: false
      
    search:
      enabled: true
      default: "skill-search-es"
      
    sched:
      enabled: true
      default: "skill-sched-quartz"
      
    sec:
      enabled: true
      default: "skill-sec-access"
      
    iot:
      enabled: false
      
    net:
      enabled: true
      default: "skill-net-proxy"
      
    sys:
      enabled: true
      default: "skill-sys-registry"
      
    util:
      enabled: true
      default: "skill-util-report"
```

### 2.2 Profile配置模板

系统安装时根据选择的Profile初始化配置：

```yaml
# /config/profiles/micro.yaml (微型配置)
apiVersion: skills.ooder.io/v1
kind: ProfileConfig
metadata:
  name: micro
  description: "微型部署，适合开发测试"

spec:
  resources:
    cpu: "2"
    memory: "4Gi"
    
  capabilities:
    llm:
      default: "skill-llm-ollama"  # 使用本地模型
      config:
        model: "llama3"
        
    db:
      default: "skill-db-mysql"
      config:
        pool:
          maxActive: 5
          
    vfs:
      default: "skill-vfs-local"
      
    know:
      enabled: false  # 微型版禁用知识库
      
---
# /config/profiles/small.yaml (小型配置)
apiVersion: skills.ooder.io/v1
kind: ProfileConfig
metadata:
  name: small
  description: "小型部署，适合小团队"

spec:
  resources:
    cpu: "4"
    memory: "8Gi"
    
  capabilities:
    llm:
      default: "skill-llm-deepseek"
      config:
        model: "deepseek-chat"
        
    db:
      default: "skill-db-mysql"
      config:
        pool:
          maxActive: 10
          
    know:
      enabled: true
      default: "skill-know-rag"

---
# /config/profiles/large.yaml (大型配置)
apiVersion: skills.ooder.io/v1
kind: ProfileConfig
metadata:
  name: large
  description: "大型部署，适合企业生产"

spec:
  resources:
    cpu: "8"
    memory: "16Gi"
    
  capabilities:
    llm:
      default: "skill-llm-deepseek"
      fallback: "skill-llm-openai"
      config:
        model: "deepseek-chat"
        pool:
          maxConnections: 10
          
    db:
      default: "skill-db-postgres"
      config:
        pool:
          maxActive: 30
          
    know:
      enabled: true
      default: "skill-know-rag"
      config:
        vector:
          enabled: true
          backend: "milvus"
```

---

## 三、技能级配置 (Level 2)

### 3.1 技能配置文件结构

每个技能目录下可以包含 `skill-config.yaml`：

```yaml
# /skills/capabilities/llm/skill-llm-deepseek/skill-config.yaml
apiVersion: skills.ooder.io/v1
kind: SkillConfig
metadata:
  name: skill-llm-deepseek
  version: "1.0.0"
  inherits: "system"  # 继承系统配置

spec:
  capabilities:
    llm:
      config:
        apiKey: "${DEEPSEEK_API_KEY}"
        baseUrl: "https://api.deepseek.com/v1"
        model: "deepseek-chat"
        temperature: "${inherit}"  # 继承系统配置的0.7
        maxTokens: 8192            # 覆盖系统配置的4096
        timeout: "${inherit}"
        
      models:
        - id: "deepseek-chat"
          name: "DeepSeek Chat"
          contextWindow: 64000
          supportsFunctionCalling: true
          pricing:
            input: 0.001  # ¥/1K tokens
            output: 0.002
            
        - id: "deepseek-coder"
          name: "DeepSeek Coder"
          contextWindow: 16000
          supportsFunctionCalling: true
```

### 3.2 配置继承语法

```yaml
spec:
  capabilities:
    llm:
      config:
        # 方式1: 完全继承上级配置
        temperature: "${inherit}"
        
        # 方式2: 覆盖上级配置
        maxTokens: 8192
        
        # 方式3: 合并上级配置
        extraParams:
          ${merge}:
            - "${inherit}"
            - customParam: "value"
            
        # 方式4: 追加到数组
        models:
          ${append}:
            - id: "custom-model"
              name: "Custom Model"
              
        # 方式5: 条件继承
        timeout: "${inherit:60000}"  # 继承或使用默认值60000
```

---

## 四、场景级配置 (Level 3)

### 4.1 场景配置文件结构

场景技能目录下包含 `scene-config.yaml`：

```yaml
# /skills/scenes/skill-llm-chat/scene-config.yaml
apiVersion: skills.ooder.io/v1
kind: SceneConfig
metadata:
  name: skill-llm-chat
  version: "1.0.0"
  inherits: "skill"  # 继承技能配置

spec:
  scene:
    type: "SCENE"
    category: "ai-assistant"
    
  capabilities:
    llm:
      config:
        model: "deepseek-chat"
        temperature: 0.8          # 场景特定配置
        maxTokens: 4096
        systemPrompt: |
          你是一个智能助手，请根据用户需求提供帮助。
          
      functionCalling:
        enabled: true
        tools:
          - name: "query_knowledge"
            enabled: true
          - name: "send_notification"
            enabled: true
            
    know:
      enabled: true
      config:
        topK: 3
        scoreThreshold: 0.8
        
  internalSkills:
    - id: "skill-llm-conversation"
      config:
        temperature: "${inherit}"
        contextWindow: 8192
        
    - id: "skill-llm-context-builder"
      config:
        maxHistoryTurns: 10
```

### 4.2 场景内部技能配置

场景内部技能可以独立覆盖配置：

```yaml
# /skills/scenes/skill-llm-chat/skills/skill-llm-conversation/skill-config.yaml
apiVersion: skills.ooder.io/v1
kind: SkillConfig
metadata:
  name: skill-llm-conversation
  parent: "skill-llm-chat"  # 父场景
  inherits: "scene"          # 继承场景配置

spec:
  capabilities:
    llm:
      config:
        # 继承场景配置
        model: "${inherit}"
        temperature: "${inherit}"
        
        # 内部技能特定配置
        contextWindow: 8192
        maxHistoryTurns: 20
        historyStrategy: "sliding"
```

---

## 五、配置装载流程

### 5.1 系统安装时配置初始化

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        系统安装配置初始化流程                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Step 1: 选择部署规模                                                    │
│  ├── micro (微型) - 开发测试                                            │
│  ├── small (小型) - 小团队                                              │
│  ├── large (大型) - 企业生产                                            │
│  └── enterprise (企业) - 大型企业                                       │
│                                                                         │
│  Step 2: 加载Profile模板                                                 │
│  ├── 读取 /config/profiles/{profile}.yaml                               │
│  ├── 合并到 system-config.yaml                                          │
│  └── 生成初始配置                                                       │
│                                                                         │
│  Step 3: 配置17种能力                                                    │
│  ├── 遍历17种能力分类                                                   │
│  ├── 应用Profile默认值                                                  │
│  └── 用户可自定义修改                                                   │
│                                                                         │
│  Step 4: 保存系统配置                                                    │
│  ├── 写入 /config/system-config.yaml                                    │
│  ├── 写入配置数据库                                                     │
│  └── 生成配置校验和                                                     │
│                                                                         │
│  Step 5: 初始化能力服务                                                  │
│  ├── 加载必需能力驱动                                                   │
│  ├── 建立连接池                                                         │
│  └── 启动健康检查                                                       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.2 技能安装时配置装载

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        技能安装配置装载流程                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Step 1: 读取技能元数据                                                  │
│  ├── 解析 skill-index-entry.yaml                                        │
│  ├── 获取 capabilityAddresses                                           │
│  └── 获取 dependencies                                                  │
│                                                                         │
│  Step 2: 检查配置文件                                                    │
│  ├── 检查 skill-config.yaml 是否存在                                    │
│  ├── 不存在则使用系统默认配置                                           │
│  └── 存在则解析继承关系                                                 │
│                                                                         │
│  Step 3: 配置合并                                                        │
│  ├── 加载系统级配置                                                     │
│  ├── 加载技能级配置                                                     │
│  ├── 执行深度合并                                                       │
│  └── 解析 ${inherit} 语法                                               │
│                                                                         │
│  Step 4: 配置验证                                                        │
│  ├── 验证必需配置项                                                     │
│  ├── 验证配置值范围                                                     │
│  └── 验证依赖关系                                                       │
│                                                                         │
│  Step 5: 用户自定义                                                      │
│  ├── 安装向导展示配置项                                                 │
│  ├── 用户可修改配置值                                                   │
│  └── 保存用户配置                                                       │
│                                                                         │
│  Step 6: 配置生效                                                        │
│  ├── 写入运行时配置                                                     │
│  ├── 初始化能力连接                                                     │
│  └── 注册到能力管理器                                                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.3 场景安装时配置装载

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        场景安装配置装载流程                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Step 1: 读取场景元数据                                                  │
│  ├── 解析 scene-index-entry.yaml                                        │
│  ├── 获取场景类型和分类                                                 │
│  └── 获取内部技能列表                                                   │
│                                                                         │
│  Step 2: 加载场景配置                                                    │
│  ├── 检查 scene-config.yaml                                             │
│  ├── 解析场景级配置                                                     │
│  └── 解析内部技能配置                                                   │
│                                                                         │
│  Step 3: 配置继承链构建                                                  │
│  ├── 系统配置 → 技能配置 → 场景配置                                     │
│  ├── 每个内部技能: 系统配置 → 技能配置 → 场景配置 → 内部技能配置         │
│  └── 生成完整配置树                                                     │
│                                                                         │
│  Step 4: 安装向导                                                        │
│  ├── 展示场景配置项                                                     │
│  ├── 展示LLM配置 (Provider/模型/参数)                                   │
│  ├── 展示知识库配置                                                     │
│  ├── 展示参与者配置                                                     │
│  └── 展示驱动条件配置                                                   │
│                                                                         │
│  Step 5: 配置保存                                                        │
│  ├── 保存场景配置到数据库                                               │
│  ├── 保存用户自定义配置                                                 │
│  └── 关联内部技能配置                                                   │
│                                                                         │
│  Step 6: 内部技能安装                                                    │
│  ├── 遍历内部技能列表                                                   │
│  ├── 应用继承配置                                                       │
│  └── 安装并注册                                                         │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 六、配置存储结构

> **重要说明**: 微方案不使用数据库，统一采用SDK JSON文件存储方案。

### 6.1 SDK JSON存储方案

#### 设计原则
- **轻量化**: 微方案无需数据库依赖，使用JSON文件存储
- **可移植性**: 配置文件可直接复制迁移
- **版本控制友好**: JSON格式便于Git管理
- **热更新支持**: 配置修改无需重启服务

#### 存储目录结构

```
config/
├── system-config.json            # 系统级配置 (JSON格式)
├── profiles/
│   ├── micro.json                # 微型配置模板
│   ├── small.json                # 小型配置模板
│   ├── large.json                # 大型配置模板
│   └── enterprise.json           # 企业配置模板
├── capabilities/                 # 能力独立配置
│   ├── llm.json                  # LLM能力配置
│   ├── db.json                   # 数据库配置
│   ├── vfs.json                  # 虚拟文件系统配置
│   ├── org.json                  # 组织管理配置
│   ├── know.json                 # 知识库配置
│   ├── comm.json                 # 通信配置
│   ├── auth.json                 # 认证配置
│   ├── mon.json                  # 监控配置
│   ├── payment.json              # 支付配置
│   ├── media.json                # 媒体配置
│   ├── search.json               # 搜索配置
│   ├── sched.json                # 调度配置
│   ├── sec.json                  # 安全配置
│   ├── iot.json                  # IoT配置
│   ├── net.json                  # 网络配置
│   ├── sys.json                  # 系统配置
│   └── util.json                 # 工具配置
├── runtime/                      # 运行时配置缓存
│   ├── skill-{id}.json           # 技能运行时配置
│   ├── scene-{id}.json           # 场景运行时配置
│   └── scene-{sceneId}-skill-{skillId}.json  # 内部技能配置
└── secrets/                      # 敏感配置 (加密存储)
    ├── llm-secrets.json          # LLM密钥
    ├── db-secrets.json           # 数据库密码
    └── ...
```

#### JSON配置文件格式

**系统配置 (system-config.json)**:
```json
{
  "apiVersion": "skills.ooder.io/v1",
  "kind": "SystemConfig",
  "metadata": {
    "name": "ooder-skills-system",
    "version": "1.0.0",
    "profile": "micro",
    "createdAt": "2026-03-12T00:00:00Z",
    "updatedAt": "2026-03-12T00:00:00Z"
  },
  "spec": {
    "capabilities": {
      "llm": {
        "enabled": true,
        "default": "skill-llm-deepseek",
        "fallback": "skill-llm-ollama",
        "config": {
          "temperature": 0.7,
          "maxTokens": 4096,
          "timeout": 60000
        }
      },
      "db": {
        "enabled": true,
        "default": "skill-db-mysql",
        "config": {
          "pool": {
            "maxActive": 20,
            "maxIdle": 10
          }
        }
      }
    }
  }
}
```

**Profile模板 (profiles/micro.json)**:
```json
{
  "apiVersion": "skills.ooder.io/v1",
  "kind": "ProfileConfig",
  "metadata": {
    "name": "micro",
    "description": "微型部署，适合开发测试"
  },
  "spec": {
    "resources": {
      "cpu": "2",
      "memory": "4Gi"
    },
    "capabilities": {
      "llm": {
        "default": "skill-llm-ollama",
        "config": {
          "model": "llama3"
        }
      },
      "db": {
        "default": "skill-db-mysql",
        "config": {
          "pool": {
            "maxActive": 5
          }
        }
      },
      "know": {
        "enabled": false
      }
    }
  }
}
```

**技能运行时配置 (runtime/skill-{id}.json)**:
```json
{
  "apiVersion": "skills.ooder.io/v1",
  "kind": "SkillRuntimeConfig",
  "metadata": {
    "skillId": "skill-llm-chat",
    "installedAt": "2026-03-12T10:00:00Z",
    "updatedAt": "2026-03-12T10:00:00Z"
  },
  "spec": {
    "inheritFrom": "system",
    "overrides": {
      "llm": {
        "config": {
          "temperature": 0.8,
          "maxTokens": 8192
        }
      }
    },
    "userConfig": {
      "selectedRole": "assistant",
      "participants": {
        "leader": "user-001"
      }
    }
  }
}
```

### 6.2 配置文件目录结构 (技能包内)

```
/config/
├── system-config.yaml           # 系统级配置
├── profiles/
│   ├── micro.yaml               # 微型配置模板
│   ├── small.yaml               # 小型配置模板
│   ├── large.yaml               # 大型配置模板
│   └── enterprise.yaml          # 企业配置模板
├── capabilities/                # 能力配置
│   ├── llm.yaml
│   ├── db.yaml
│   ├── vfs.yaml
│   └── ...
└── secrets/                     # 敏感配置
    ├── llm-secrets.yaml
    ├── db-secrets.yaml
    └── ...

/skills/
├── capabilities/
│   └── llm/
│       └── skill-llm-deepseek/
│           ├── skill-index-entry.yaml
│           └── skill-config.yaml    # 技能级配置
└── scenes/
    └── skill-llm-chat/
        ├── scene-index-entry.yaml
        ├── scene-config.yaml        # 场景级配置
        └── skills/
            └── skill-llm-conversation/
                ├── skill-index-entry.yaml
                └── skill-config.yaml    # 内部技能配置
```

---

## 七、配置API设计

### 7.1 配置查询API

```http
# 获取系统配置
GET /api/v1/config/system

# 获取指定分类的系统配置
GET /api/v1/config/system/{category}

# 获取技能配置 (包含继承链)
GET /api/v1/config/skills/{skillId}

# 获取技能指定分类配置
GET /api/v1/config/skills/{skillId}/{category}

# 获取场景配置 (包含继承链)
GET /api/v1/config/scenes/{sceneId}

# 获取场景内部技能配置
GET /api/v1/config/scenes/{sceneId}/skills/{skillId}

# 获取完整配置继承链
GET /api/v1/config/inheritance-chain/{targetType}/{targetId}
```

### 7.2 配置更新API

```http
# 更新系统配置
PUT /api/v1/config/system/{category}
Body: { "key": "value", ... }

# 更新技能配置
PUT /api/v1/config/skills/{skillId}/{category}
Body: { "key": "value", ... }

# 更新场景配置
PUT /api/v1/config/scenes/{sceneId}/{category}
Body: { "key": "value", ... }

# 重置配置到继承值
DELETE /api/v1/config/{targetType}/{targetId}/{category}/{key}
```

### 7.3 配置继承API

```http
# 获取配置继承预览
POST /api/v1/config/preview
Body: {
  "targetType": "skill",
  "targetId": "skill-llm-chat",
  "config": { ... }
}

# 验证配置继承
POST /api/v1/config/validate
Body: {
  "targetType": "scene",
  "targetId": "skill-llm-chat",
  "config": { ... }
}
```

---

## 八、配置装载服务实现

### 8.1 配置装载服务接口

```java
public interface ConfigLoaderService {
    
    ConfigNode loadSystemConfig();
    
    ConfigNode loadSkillConfig(String skillId);
    
    ConfigNode loadSceneConfig(String sceneId);
    
    ConfigNode loadInternalSkillConfig(String sceneId, String skillId);
    
    ConfigNode mergeConfig(ConfigNode parent, ConfigNode child);
    
    ConfigNode resolveInheritance(ConfigNode config, String targetType, String targetId);
    
    void saveConfig(String targetType, String targetId, String category, Map<String, Object> config);
    
    void resetConfig(String targetType, String targetId, String category, String key);
}
```

### 8.2 JSON存储服务

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

### 8.3 配置继承解析器

```java
@Service
public class ConfigInheritanceResolver {
    
    private static final Pattern INHERIT_PATTERN = 
        Pattern.compile("\\$\\{inherit(?::([^}]*))?\\}");
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

---

## 九、安装向导集成

### 9.1 系统安装向导步骤

```
Step 1: 选择部署规模
├── 微型 - 本地开发测试
├── 小型 - 小团队协作
├── 大型 - 企业生产
└── 企业 - 大规模部署

Step 2: 配置基础服务
├── 数据库配置
├── 缓存配置
└── 存储配置

Step 3: 配置AI服务
├── LLM Provider选择
├── 模型配置
└── 知识库配置

Step 4: 配置组织服务
├── 组织源选择
└── 认证配置

Step 5: 确认并安装
├── 配置预览
├── 配置验证
└── 开始安装
```

### 9.2 技能安装向导步骤

```
Step 1: 技能预览
├── 技能信息
├── 依赖列表
└── 配置需求

Step 2: 配置继承预览
├── 显示继承的系统配置
├── 显示技能默认配置
└── 用户可修改

Step 3: 自定义配置
├── LLM配置 (如需要)
├── 知识库配置 (如需要)
└── 其他配置

Step 4: 确认安装
├── 配置摘要
└── 开始安装
```

---

**文档版本**: 1.0  
**创建日期**: 2026-03-12  
**作者**: AI Assistant
