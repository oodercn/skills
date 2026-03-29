# 能力默认降级配置位置讨论

## 一、三种方案对比

### 方案A：枚举中直接定义

```java
public enum CapabilityAddress {
    
    VFS(0x0100, "vfs", "文件存储",
        // 默认降级配置
        DefaultConfig.builder()
            .production("skill-vfs-oss")
            .staging("skill-vfs-minio")
            .development("skill-vfs-local")
            .fallback("skill-vfs-local")
            .build()
    ),
    
    DATABASE(0x0101, "database", "数据库",
        DefaultConfig.builder()
            .production("skill-db-postgresql")
            .development("skill-db-sqlite")
            .fallback("skill-db-sqlite")
            .build()
    ),
    
    LLM(0x0110, "llm", "大语言模型",
        DefaultConfig.builder()
            .production("skill-llm-openai")
            .development("skill-llm-ollama")
            .fallback("skill-llm-ollama")
            .build()
    ),
    ;
    
    private final int address;
    private final String code;
    private final String name;
    private final DefaultConfig defaults;
    
    // ...
}

// 默认配置类
@Data
@Builder
public class DefaultConfig {
    private String production;
    private String staging;
    private String development;
    private String fallback;
}
```

**优点**：
- ✅ 配置和能力定义在一起，易于维护
- ✅ 编译时检查，类型安全
- ✅ 代码即文档

**缺点**：
- ❌ 修改配置需要重新编译
- ❌ 枚举变得臃肿
- ❌ 不支持动态配置

---

### 方案B：独立配置文件

```yaml
# capability-defaults.yaml

defaults:
  0x0100:  # VFS
    production: skill-vfs-oss
    staging: skill-vfs-minio
    development: skill-vfs-local
    fallback: skill-vfs-local
    
  0x0101:  # DATABASE
    production: skill-db-postgresql
    development: skill-db-sqlite
    fallback: skill-db-sqlite
    
  0x0110:  # LLM
    production: skill-llm-openai
    development: skill-llm-ollama
    fallback: skill-llm-ollama
```

```java
// 枚举保持简洁
public enum CapabilityAddress {
    VFS(0x0100, "vfs", "文件存储"),
    DATABASE(0x0101, "database", "数据库"),
    LLM(0x0110, "llm", "大语言模型"),
    ;
    
    private final int address;
    private final String code;
    private final String name;
    // 不包含默认配置
}

// 独立的默认配置管理器
public class CapabilityDefaultsManager {
    private Map<Integer, DefaultConfig> defaults;
    
    public DefaultConfig getDefaults(int address) {
        return defaults.get(address);
    }
}
```

**优点**：
- ✅ 配置可动态修改
- ✅ 枚举保持简洁
- ✅ 支持热更新

**缺点**：
- ❌ 配置和能力定义分离
- ❌ 需要额外的配置加载逻辑
- ❌ 配置文件可能丢失

---

### 方案C：系统集成内置

```java
public enum CapabilityAddress {
    
    VFS(0x0100, "vfs", "文件存储", 
        BuiltInProvider.VFS_LOCAL),  // 内置兜底提供者
        
    DATABASE(0x0101, "database", "数据库",
        BuiltInProvider.DB_SQLITE),
        
    LLM(0x0110, "llm", "大语言模型",
        BuiltInProvider.LLM_OLLAMA),
    ;
    
    private final int address;
    private final String code;
    private final String name;
    private final String builtInFallback;  // 系统内置兜底
    
    // 获取默认提供者（从系统配置获取）
    public String getDefaultProvider() {
        // 1. 检查系统配置
        String configured = SystemConfig.getCapabilityDefault(this.address);
        if (configured != null) {
            return configured;
        }
        // 2. 返回内置兜底
        return builtInFallback;
    }
}

// 内置提供者常量
public class BuiltInProvider {
    public static final String VFS_LOCAL = "skill-vfs-local";
    public static final String DB_SQLITE = "skill-db-sqlite";
    public static final String LLM_OLLAMA = "skill-llm-ollama";
    public static final String ORG_LOCAL = "skill-org-local";
    public static final String NOTIFY_CONSOLE = "skill-notification-console";
    public static final String PAYMENT_MOCK = "skill-payment-mock";
}
```

**优点**：
- ✅ 枚举简洁，只包含内置兜底
- ✅ 保证系统可用性（至少有兜底）
- ✅ 支持系统配置覆盖

**缺点**：
- ❌ 需要额外的系统配置层
- ❌ 内置兜底和默认配置概念混淆

---

## 二、推荐方案

### 推荐：方案C + 系统配置层

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        推荐方案：分层设计                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   第一层：枚举定义（内置兜底）                                                  │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  VFS(0x0100, "vfs", "文件存储", "skill-vfs-local")                   │   │
│   │  // 只定义内置兜底，保证系统可用性                                      │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   第二层：系统配置（环境/规模默认）                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  capability-defaults.yaml                                            │   │
│   │  // 定义环境、规模相关的默认配置                                       │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   第三层：用户配置（显式指定）                                                  │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  capabilityBindings:                                                 │   │
│   │    0x0100: skill-vfs-minio                                           │   │
│   │  // 用户显式配置，最高优先级                                           │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、最终设计

### 3.1 枚举定义（简洁）

```java
/**
 * 能力地址枚举
 * 只定义地址、代码、名称和内置兜底
 */
public enum CapabilityAddress {
    
    // ═══════════════════════════════════════════════════════════════════════
    // 系统保留区 (0x0000 - 0x00FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    SYSTEM_CORE(0x0000, "system.core", "系统核心", null),
    INSTALLER(0x0001, "system.installer", "安装器", null),
    SCENE_MANAGER(0x0002, "system.scene-manager", "场景管理器", null),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 基础能力区 (0x0100 - 0x01FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    VFS(0x0100, "vfs", "文件存储", "skill-vfs-local"),
    DATABASE(0x0101, "database", "数据库", "skill-db-sqlite"),
    CACHE(0x0102, "cache", "缓存", "skill-cache-memory"),
    MESSAGE_QUEUE(0x0103, "message-queue", "消息队列", "skill-mq-memory"),
    NOTIFICATION(0x0104, "notification", "通知", "skill-notification-console"),
    EMAIL(0x0105, "email", "邮件", "skill-email-mock"),
    SEARCH(0x0106, "search", "搜索", "skill-search-memory"),
    SCHEDULER(0x0107, "scheduler", "任务调度", "skill-scheduler-memory"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // AI能力区 (0x0110 - 0x011F)
    // ═══════════════════════════════════════════════════════════════════════
    
    LLM(0x0110, "llm", "大语言模型", "skill-llm-ollama"),
    LLM_CHAT(0x0111, "llm.chat", "LLM对话", "skill-llm-ollama"),
    LLM_EMBEDDING(0x0112, "llm.embedding", "LLM嵌入", "skill-llm-ollama"),
    KNOWLEDGE(0x0113, "knowledge", "知识库", "skill-knowledge-local"),
    RAG(0x0114, "rag", "RAG检索增强", "skill-rag-local"),
    VECTOR_STORE(0x0115, "vector-store", "向量存储", "skill-vector-sqlite"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 组织能力区 (0x0120 - 0x012F)
    // ═══════════════════════════════════════════════════════════════════════
    
    ORG(0x0120, "org", "组织架构", "skill-org-local"),
    AUTH(0x0121, "auth", "用户认证", "skill-auth-local"),
    PERMISSION(0x0122, "permission", "权限管理", "skill-permission-local"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 业务能力区 (0x0200 - 0x02FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    PAYMENT(0x0200, "payment", "支付", "skill-payment-mock"),
    MEDIA(0x0201, "media", "媒体发布", "skill-media-mock"),
    WORKFLOW(0x0202, "workflow", "工作流", "skill-workflow-memory"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // IoT能力区 (0x0220 - 0x022F)
    // ═══════════════════════════════════════════════════════════════════════
    
    IOT(0x0220, "iot", "物联网", "skill-iot-mock"),
    DEVICE(0x0221, "iot.device", "设备管理", "skill-device-mock"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // UI能力区 (0x0130 - 0x013F)
    // ═══════════════════════════════════════════════════════════════════════
    
    UI(0x0130, "ui", "UI生成", "skill-ui-console"),
    DASHBOARD(0x0131, "ui.dashboard", "仪表盘", "skill-dashboard-default"),
    ;
    
    private final int address;
    private final String code;
    private final String name;
    private final String builtInFallback;  // 内置兜底提供者
    
    CapabilityAddress(int address, String code, String name, String builtInFallback) {
        this.address = address;
        this.code = code;
        this.name = name;
        this.builtInFallback = builtInFallback;
    }
    
    // Getters...
    public int getAddress() { return address; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getBuiltInFallback() { return builtInFallback; }
    
    /**
     * 获取默认提供者（考虑系统配置）
     */
    public String getDefaultProvider() {
        return CapabilityDefaultsManager.getInstance().getDefaultProvider(this);
    }
}
```

### 3.2 系统配置文件

```yaml
# config/capability-defaults.yaml

# 当前环境
environment: production

# 当前规模
tier: large

# 能力默认配置（覆盖内置兜底）
capabilityDefaults:
  
  # VFS 文件存储
  0x0100:
    production: skill-vfs-oss
    staging: skill-vfs-minio
    development: skill-vfs-local
    large: skill-vfs-oss
    medium: skill-vfs-minio
    small: skill-vfs-database
    micro: skill-vfs-local
    
  # LLM 大语言模型
  0x0110:
    production: skill-llm-openai
    staging: skill-llm-qianwen
    development: skill-llm-ollama
    
  # ORG 组织架构
  0x0120:
    production: skill-org-dingding
    staging: skill-org-dingding
    development: skill-org-local
```

### 3.3 默认配置管理器

```java
/**
 * 能力默认配置管理器
 */
public class CapabilityDefaultsManager {
    
    private static CapabilityDefaultsManager instance;
    
    private String environment;
    private String tier;
    private Map<Integer, Map<String, String>> capabilityDefaults;
    
    /**
     * 获取能力的默认提供者
     */
    public String getDefaultProvider(CapabilityAddress capability) {
        Map<String, String> config = capabilityDefaults.get(capability.getAddress());
        
        if (config != null) {
            // 1. 按环境查找
            String byEnv = config.get(environment);
            if (byEnv != null) return byEnv;
            
            // 2. 按规模查找
            String byTier = config.get(tier);
            if (byTier != null) return byTier;
        }
        
        // 3. 返回内置兜底
        return capability.getBuiltInFallback();
    }
}
```

---

## 四、总结

### 推荐方案

| 层级 | 位置 | 内容 |
|------|------|------|
| **枚举** | CapabilityAddress | 只定义内置兜底（保证可用性） |
| **系统配置** | capability-defaults.yaml | 环境/规模相关的默认配置 |
| **用户配置** | capabilityBindings | 用户显式配置（最高优先级） |

### 设计原则

1. **枚举保持简洁** - 只定义地址、代码、名称和内置兜底
2. **系统配置可覆盖** - 环境/规模相关的默认配置独立管理
3. **保证系统可用** - 内置兜底确保系统始终可用

### 降级优先级

```
用户配置 > 系统配置(环境) > 系统配置(规模) > 枚举内置兜底
```

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
