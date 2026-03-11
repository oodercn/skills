# 能力地址段设计

## 一、核心概念

```
能力类型 = 地址段（5个地址）

每个能力类型不是单个地址，而是一个包含5个地址的段落。
段内地址可用于：主提供者、备用提供者、缓存层、只读副本、归档层等。
支持单选（互斥）或多选（组合）配置。
```

---

## 二、地址段结构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力地址段结构                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   地址段结构（每个能力类型占用 5 个地址）                                        │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  基地址 + 0  │  PRIMARY    │  主提供者      │  必选                   │   │
│   │  基地址 + 1  │  STANDBY    │  备用提供者    │  可选（主备切换）         │   │
│   │  基地址 + 2  │  CACHE      │  缓存层        │  可选（加速访问）         │   │
│   │  基地址 + 3  │  READONLY   │  只读副本      │  可选（读写分离）         │   │
│   │  基地址 + 4  │  ARCHIVE    │  归档层        │  可选（冷数据归档）       │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   示例：VFS 能力段 (0x0100 - 0x0104)                                         │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  0x0100  │  PRIMARY  │  MinIO        │  主存储                      │   │
│   │  0x0101  │  STANDBY  │  OSS          │  备用存储（容灾）             │   │
│   │  0x0102  │  CACHE    │  Redis        │  缓存层                      │   │
│   │  0x0103  │  READONLY │  S3           │  只读副本                    │   │
│   │  0x0104  │  ARCHIVE  │  OSS-Archive  │  归档存储                    │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、地址空间规划

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力地址空间规划                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   地址范围           │  段数量  │  区域名称        │  说明                    │
│   ───────────────────┼─────────┼──────────────────┼─────────────────────────│
│   0x0000 - 0x00FF    │  32     │  系统保留区      │  系统核心能力             │
│   0x0100 - 0x01FF    │  32     │  基础能力区      │  基础服务能力             │
│   0x0200 - 0x02FF    │  32     │  AI能力区        │  AI相关能力              │
│   0x0300 - 0x03FF    │  32     │  组织能力区      │  组织管理能力             │
│   0x0400 - 0x04FF    │  32     │  业务能力区      │  业务领域能力             │
│   0x0500 - 0x05FF    │  32     │  IoT能力区       │  物联网能力              │
│   0x0600 - 0x06FF    │  32     │  UI能力区        │  界面能力                │
│   0x0700 - 0x07FF    │  32     │  扩展能力区      │  第三方扩展能力           │
│   0x0800 - 0xFFFF    │  ~12K   │  用户自定义区    │  用户自定义能力           │
│                                                                             │
│   注：每个段占用 5 个地址，实际可用段数 = 地址范围 / 5                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、能力地址段枚举

```java
/**
 * 能力地址段枚举
 * 每个能力类型占用 5 个地址
 */
public enum CapabilitySegment {
    
    // ═══════════════════════════════════════════════════════════════════════
    // 系统保留区 (0x0000 - 0x00FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 系统核心 */
    SYSTEM_CORE(0x0000, "system.core", "系统核心", SelectionMode.NONE),
    
    /** 安装器 */
    INSTALLER(0x0005, "system.installer", "安装器", SelectionMode.NONE),
    
    /** 场景管理器 */
    SCENE_MANAGER(0x000A, "system.scene-manager", "场景管理器", SelectionMode.NONE),
    
    /** 能力注册表 */
    CAPABILITY_REGISTRY(0x000F, "system.capability-registry", "能力注册表", SelectionMode.NONE),
    
    /** 配置管理 */
    CONFIG_MANAGER(0x0014, "system.config-manager", "配置管理", SelectionMode.NONE),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 基础能力区 (0x0100 - 0x01FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 文件存储能力 */
    VFS(0x0100, "vfs", "文件存储", SelectionMode.SINGLE,
        "skill-vfs-local", null, null, null, null),
    
    /** 数据库能力 */
    DATABASE(0x0105, "database", "数据库", SelectionMode.SINGLE,
        "skill-db-sqlite", null, null, null, null),
    
    /** 缓存能力 */
    CACHE(0x010A, "cache", "缓存", SelectionMode.SINGLE,
        "skill-cache-memory", null, null, null, null),
    
    /** 消息队列能力 */
    MESSAGE_QUEUE(0x010F, "message-queue", "消息队列", SelectionMode.SINGLE,
        "skill-mq-memory", null, null, null, null),
    
    /** 通知能力 */
    NOTIFICATION(0x0114, "notification", "通知", SelectionMode.MULTI,
        "skill-notification-console", null, null, null, null),
    
    /** 邮件能力 */
    EMAIL(0x0119, "email", "邮件", SelectionMode.SINGLE,
        "skill-email-mock", null, null, null, null),
    
    /** 搜索能力 */
    SEARCH(0x011E, "search", "搜索", SelectionMode.SINGLE,
        "skill-search-memory", null, null, null, null),
    
    /** 任务调度能力 */
    SCHEDULER(0x0123, "scheduler", "任务调度", SelectionMode.SINGLE,
        "skill-scheduler-memory", null, null, null, null),
    
    // ═══════════════════════════════════════════════════════════════════════
    // AI能力区 (0x0200 - 0x02FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 大语言模型能力 */
    LLM(0x0200, "llm", "大语言模型", SelectionMode.SINGLE,
        "skill-llm-ollama", null, null, null, null),
    
    /** LLM对话能力 */
    LLM_CHAT(0x0205, "llm.chat", "LLM对话", SelectionMode.SINGLE,
        "skill-llm-ollama", null, null, null, null),
    
    /** LLM嵌入能力 */
    LLM_EMBEDDING(0x020A, "llm.embedding", "LLM嵌入", SelectionMode.SINGLE,
        "skill-llm-ollama", null, null, null, null),
    
    /** 知识库能力 */
    KNOWLEDGE(0x020F, "knowledge", "知识库", SelectionMode.SINGLE,
        "skill-knowledge-local", null, null, null, null),
    
    /** RAG能力 */
    RAG(0x0214, "rag", "RAG检索增强", SelectionMode.SINGLE,
        "skill-rag-local", null, null, null, null),
    
    /** 向量存储能力 */
    VECTOR_STORE(0x0219, "vector-store", "向量存储", SelectionMode.SINGLE,
        "skill-vector-sqlite", null, null, null, null),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 组织能力区 (0x0300 - 0x03FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 组织架构能力 */
    ORG(0x0300, "org", "组织架构", SelectionMode.SINGLE,
        "skill-org-local", null, null, null, null),
    
    /** 用户认证能力 */
    AUTH(0x0305, "auth", "用户认证", SelectionMode.SINGLE,
        "skill-auth-local", null, null, null, null),
    
    /** 权限管理能力 */
    PERMISSION(0x030A, "permission", "权限管理", SelectionMode.SINGLE,
        "skill-permission-local", null, null, null, null),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 业务能力区 (0x0400 - 0x04FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 支付能力 */
    PAYMENT(0x0400, "payment", "支付", SelectionMode.MULTI,
        "skill-payment-mock", null, null, null, null),
    
    /** 媒体发布能力 */
    MEDIA(0x0405, "media", "媒体发布", SelectionMode.MULTI,
        "skill-media-mock", null, null, null, null),
    
    /** 工作流能力 */
    WORKFLOW(0x040A, "workflow", "工作流", SelectionMode.SINGLE,
        "skill-workflow-memory", null, null, null, null),
    
    /** 审批能力 */
    APPROVAL(0x040F, "approval", "审批", SelectionMode.SINGLE,
        "skill-approval-memory", null, null, null, null),
    
    /** 协作能力 */
    COLLABORATION(0x0414, "collaboration", "协作", SelectionMode.SINGLE,
        "skill-collaboration-memory", null, null, null, null),
    
    // ═══════════════════════════════════════════════════════════════════════
    // IoT能力区 (0x0500 - 0x05FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 物联网能力 */
    IOT(0x0500, "iot", "物联网", SelectionMode.SINGLE,
        "skill-iot-mock", null, null, null, null),
    
    /** 设备管理能力 */
    DEVICE(0x0505, "iot.device", "设备管理", SelectionMode.SINGLE,
        "skill-device-mock", null, null, null, null),
    
    /** 边缘计算能力 */
    EDGE(0x050A, "iot.edge", "边缘计算", SelectionMode.SINGLE,
        "skill-edge-mock", null, null, null, null),
    
    /** MQTT能力 */
    MQTT(0x050F, "iot.mqtt", "MQTT", SelectionMode.SINGLE,
        "skill-mqtt-mock", null, null, null, null),
    
    // ═══════════════════════════════════════════════════════════════════════
    // UI能力区 (0x0600 - 0x06FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** UI生成能力 */
    UI(0x0600, "ui", "UI生成", SelectionMode.SINGLE,
        "skill-ui-console", null, null, null, null),
    
    /** 仪表盘能力 */
    DASHBOARD(0x0605, "ui.dashboard", "仪表盘", SelectionMode.SINGLE,
        "skill-dashboard-default", null, null, null, null),
    
    ;
    
    private final int baseAddress;           // 基地址
    private final String code;               // 代码
    private final String name;               // 名称
    private final SelectionMode selectionMode; // 选择模式
    
    // 内置默认配置（5个地址对应的提供者）
    private final String primaryDefault;     // 主提供者默认
    private final String standbyDefault;     // 备用提供者默认
    private final String cacheDefault;       // 缓存层默认
    private final String readonlyDefault;    // 只读副本默认
    private final String archiveDefault;     // 归档层默认
    
    /**
     * 获取段内地址
     */
    public int getAddress(SegmentSlot slot) {
        return baseAddress + slot.getOffset();
    }
    
    /**
     * 获取所有地址
     */
    public int[] getAllAddresses() {
        return new int[] {
            baseAddress,      // PRIMARY
            baseAddress + 1,  // STANDBY
            baseAddress + 2,  // CACHE
            baseAddress + 3,  // READONLY
            baseAddress + 4   // ARCHIVE
        };
    }
}

/**
 * 段内槽位
 */
public enum SegmentSlot {
    PRIMARY(0, "主提供者"),
    STANDBY(1, "备用提供者"),
    CACHE(2, "缓存层"),
    READONLY(3, "只读副本"),
    ARCHIVE(4, "归档层");
    
    private final int offset;
    private final String name;
    
    SegmentSlot(int offset, String name) {
        this.offset = offset;
        this.name = name;
    }
    
    public int getOffset() { return offset; }
    public String getName() { return name; }
}

/**
 * 选择模式
 */
public enum SelectionMode {
    NONE,    // 不可选（系统内置）
    SINGLE,  // 单选（互斥，如组织驱动）
    MULTI    // 多选（可组合，如支付渠道）
}
```

---

## 五、配置示例

### 5.1 单选配置（互斥）

```yaml
# 组织架构能力 - 单选（只能选一个）
capabilityBindings:
  0x0300:  # ORG 组织架构
    mode: SINGLE
    primary: skill-org-dingding    # 主提供者：钉钉
    # standby: null                 # 无备用
    # cache: null                   # 无缓存
```

### 5.2 多选配置（组合）

```yaml
# 支付能力 - 多选（可以组合多个）
capabilityBindings:
  0x0400:  # PAYMENT 支付
    mode: MULTI
    primary: skill-payment-alipay   # 主：支付宝
    standby: skill-payment-wechat    # 备：微信支付
    # 可同时启用多个支付渠道

# 媒体发布能力 - 多选
capabilityBindings:
  0x0405:  # MEDIA 媒体发布
    mode: MULTI
    primary: skill-media-wechat      # 主：微信公众号
    standby: skill-media-weibo       # 备：微博
    readonly: skill-media-zhihu      # 只读：知乎
```

### 5.3 完整配置（主备+缓存+只读+归档）

```yaml
# 文件存储能力 - 完整配置
capabilityBindings:
  0x0100:  # VFS 文件存储
    mode: SINGLE
    primary: skill-vfs-minio         # 主存储：MinIO
    standby: skill-vfs-oss           # 备用存储：OSS（容灾）
    cache: skill-cache-redis         # 缓存层：Redis
    readonly: skill-vfs-s3           # 只读副本：S3
    archive: skill-vfs-oss-archive   # 归档层：OSS归档
```

---

## 六、能力段注册表

```yaml
# capability-segments.yaml

segments:
  
  # ═══════════════════════════════════════════════════════════════════════
  # VFS 文件存储能力段 (0x0100 - 0x0104)
  # ═══════════════════════════════════════════════════════════════════════
  
  - baseAddress: 0x0100
    code: vfs
    name: 文件存储
    selectionMode: SINGLE
    description: 文件存储能力，支持主备、缓存、只读、归档
    
    slots:
      - offset: 0
        name: PRIMARY
        description: 主存储
        providers:
          - skill-vfs-local (micro)
          - skill-vfs-database (small)
          - skill-vfs-minio (medium)
          - skill-vfs-oss (large)
          - skill-vfs-s3 (large)
        default: skill-vfs-local
        
      - offset: 1
        name: STANDBY
        description: 备用存储（容灾）
        providers:
          - skill-vfs-oss
          - skill-vfs-s3
        default: null
        
      - offset: 2
        name: CACHE
        description: 缓存层
        providers:
          - skill-cache-redis
          - skill-cache-memory
        default: null
        
      - offset: 3
        name: READONLY
        description: 只读副本
        providers:
          - skill-vfs-s3
          - skill-vfs-oss
        default: null
        
      - offset: 4
        name: ARCHIVE
        description: 归档层
        providers:
          - skill-vfs-oss-archive
        default: null
  
  # ═══════════════════════════════════════════════════════════════════════
  # ORG 组织架构能力段 (0x0300 - 0x0304)
  # ═══════════════════════════════════════════════════════════════════════
  
  - baseAddress: 0x0300
    code: org
    name: 组织架构
    selectionMode: SINGLE
    description: 组织架构能力，互斥选择
    
    slots:
      - offset: 0
        name: PRIMARY
        description: 主组织源
        providers:
          - skill-org-dingding
          - skill-org-feishu
          - skill-org-wecom
          - skill-org-ldap
          - skill-org-local
        default: skill-org-local
        
      - offset: 1
        name: STANDBY
        description: 备用组织源
        providers: []
        default: null
  
  # ═══════════════════════════════════════════════════════════════════════
  # PAYMENT 支付能力段 (0x0400 - 0x0404)
  # ═══════════════════════════════════════════════════════════════════════
  
  - baseAddress: 0x0400
    code: payment
    name: 支付
    selectionMode: MULTI
    description: 支付能力，支持多渠道组合
    
    slots:
      - offset: 0
        name: PRIMARY
        description: 主支付渠道
        providers:
          - skill-payment-alipay
          - skill-payment-wechat
          - skill-payment-unionpay
          - skill-payment-mock
        default: skill-payment-mock
        
      - offset: 1
        name: STANDBY
        description: 备用支付渠道
        providers:
          - skill-payment-alipay
          - skill-payment-wechat
        default: null
        
      - offset: 2
        name: CACHE
        description: 支付缓存
        providers: []
        default: null
```

---

## 七、场景配置示例

```yaml
# 招聘助手场景
- skillId: skill-recruitment-assistant
  name: 招聘助手
  type: SCENE
  domain: hr
  
  # 声明需要的能力段
  requiredCapabilities:
    - segment: 0x0100        # VFS 文件存储
      slots: [PRIMARY]       # 只需要主存储
      
    - segment: 0x0300        # ORG 组织架构
      slots: [PRIMARY]       # 只需要主组织源
      
    - segment: 0x0400        # PAYMENT 支付
      slots: [PRIMARY, STANDBY]  # 需要主备支付渠道
      
  # 能力绑定
  capabilityBindings:
    0x0100:  # VFS
      primary: skill-vfs-minio
      
    0x0300:  # ORG
      primary: skill-org-dingding
      
    0x0400:  # PAYMENT
      primary: skill-payment-alipay
      standby: skill-payment-wechat
```

---

## 八、总结

### 地址段设计

| 概念 | 说明 |
|------|------|
| **地址段** | 每个能力类型占用 5 个地址 |
| **槽位** | PRIMARY, STANDBY, CACHE, READONLY, ARCHIVE |
| **选择模式** | NONE(不可选), SINGLE(单选), MULTI(多选) |

### 选择模式

| 模式 | 说明 | 示例 |
|------|------|------|
| `NONE` | 系统内置，不可选 | system.core |
| `SINGLE` | 单选（互斥） | org, vfs, llm |
| `MULTI` | 多选（可组合） | payment, media, notification |

### 优势

1. **支持主备架构** - PRIMARY + STANDBY
2. **支持读写分离** - PRIMARY + READONLY
3. **支持缓存加速** - PRIMARY + CACHE
4. **支持冷热分层** - PRIMARY + ARCHIVE
5. **支持多渠道组合** - MULTI 模式

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
