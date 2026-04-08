# 能力地址段设计 - 完整版

## 一、核心概念

```
能力类型 = 地址段（5个地址）

设计要点：
1. 每个地址段的第一个地址（PRIMARY）作为降级固定地址
2. 多选模式时，由降级提供者提供UI配置器/管理器
3. 新增切换范围标记：
   - SYSTEM: 系统级，必须重启生效
   - RUNTIME: 运行时，每次调用可手工选择
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
│   │  基地址 + 0  │  PRIMARY    │  主提供者/降级固定地址  │  必选           │   │
│   │  基地址 + 1  │  STANDBY    │  备用提供者           │  可选           │   │
│   │  基地址 + 2  │  CACHE      │  缓存层              │  可选           │   │
│   │  基地址 + 3  │  READONLY   │  只读副本            │  可选           │   │
│   │  基地址 + 4  │  ARCHIVE    │  归档层              │  可选           │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   PRIMARY 地址特性：                                                         │
│   ├── 作为降级固定地址（内置兜底）                                             │
│   ├── 多选模式时，提供UI配置器/管理器                                          │
│   └── 保证系统始终可用                                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、切换范围

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        切换范围                                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   SYSTEM（系统级）                                                            │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  切换时机：系统重启时生效                                              │   │
│   │  适用场景：核心基础设施，切换影响全局                                   │   │
│   │  示例：database, vfs, org, auth                                       │   │
│   │                                                                     │   │
│   │  原因：                                                              │   │
│   │  - 数据库切换需要重新建立连接池                                        │   │
│   │  - 组织架构切换需要重新加载缓存                                        │   │
│   │  - 文件存储切换需要重新配置                                            │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   RUNTIME（运行时）                                                           │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  切换时机：每次调用可手工选择                                          │   │
│   │  适用场景：业务渠道，切换不影响系统稳定性                               │   │
│   │  示例：payment, media, llm, notification                              │   │
│   │                                                                     │   │
│   │  原因：                                                              │   │
│   │  - 支付渠道可以按用户选择                                             │   │
│   │  - LLM模型可以按任务选择                                             │   │
│   │  - 媒体发布可以按平台选择                                             │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、能力地址段枚举

```java
/**
 * 能力地址段枚举
 */
public enum CapabilitySegment {
    
    // ═══════════════════════════════════════════════════════════════════════
    // 系统保留区 (0x0000 - 0x00FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    SYSTEM_CORE(0x0000, "system.core", "系统核心", 
        SelectionMode.NONE, SwitchScope.NONE, null),
    
    INSTALLER(0x0005, "system.installer", "安装器", 
        SelectionMode.NONE, SwitchScope.NONE, null),
    
    SCENE_MANAGER(0x000A, "system.scene-manager", "场景管理器", 
        SelectionMode.NONE, SwitchScope.NONE, null),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 基础能力区 (0x0100 - 0x01FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 文件存储能力 - 系统级，重启生效 */
    VFS(0x0100, "vfs", "文件存储", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-vfs-local"),
    
    /** 数据库能力 - 系统级，重启生效 */
    DATABASE(0x0105, "database", "数据库", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-db-sqlite"),
    
    /** 缓存能力 - 系统级，重启生效 */
    CACHE(0x010A, "cache", "缓存", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-cache-memory"),
    
    /** 消息队列能力 - 系统级，重启生效 */
    MESSAGE_QUEUE(0x010F, "message-queue", "消息队列", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-mq-memory"),
    
    /** 通知能力 - 运行时，可手工选择 */
    NOTIFICATION(0x0114, "notification", "通知", 
        SelectionMode.MULTI, SwitchScope.RUNTIME, 
        "skill-notification-console"),
    
    /** 邮件能力 - 运行时，可手工选择 */
    EMAIL(0x0119, "email", "邮件", 
        SelectionMode.SINGLE, SwitchScope.RUNTIME, 
        "skill-email-mock"),
    
    /** 搜索能力 - 系统级，重启生效 */
    SEARCH(0x011E, "search", "搜索", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-search-memory"),
    
    /** 任务调度能力 - 系统级，重启生效 */
    SCHEDULER(0x0123, "scheduler", "任务调度", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-scheduler-memory"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // AI能力区 (0x0200 - 0x02FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 大语言模型能力 - 运行时，可手工选择 */
    LLM(0x0200, "llm", "大语言模型", 
        SelectionMode.SINGLE, SwitchScope.RUNTIME, 
        "skill-llm-ollama"),
    
    /** LLM对话能力 - 运行时，可手工选择 */
    LLM_CHAT(0x0205, "llm.chat", "LLM对话", 
        SelectionMode.SINGLE, SwitchScope.RUNTIME, 
        "skill-llm-ollama"),
    
    /** LLM嵌入能力 - 运行时，可手工选择 */
    LLM_EMBEDDING(0x020A, "llm.embedding", "LLM嵌入", 
        SelectionMode.SINGLE, SwitchScope.RUNTIME, 
        "skill-llm-ollama"),
    
    /** 知识库能力 - 系统级，重启生效 */
    KNOWLEDGE(0x020F, "knowledge", "知识库", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-knowledge-local"),
    
    /** RAG能力 - 系统级，重启生效 */
    RAG(0x0214, "rag", "RAG检索增强", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-rag-local"),
    
    /** 向量存储能力 - 系统级，重启生效 */
    VECTOR_STORE(0x0219, "vector-store", "向量存储", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-vector-sqlite"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 组织能力区 (0x0300 - 0x03FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 组织架构能力 - 系统级，重启生效 */
    ORG(0x0300, "org", "组织架构", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-org-local"),
    
    /** 用户认证能力 - 系统级，重启生效 */
    AUTH(0x0305, "auth", "用户认证", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-auth-local"),
    
    /** 权限管理能力 - 系统级，重启生效 */
    PERMISSION(0x030A, "permission", "权限管理", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-permission-local"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 业务能力区 (0x0400 - 0x04FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 支付能力 - 运行时，可手工选择 */
    PAYMENT(0x0400, "payment", "支付", 
        SelectionMode.MULTI, SwitchScope.RUNTIME, 
        "skill-payment-mock"),
    
    /** 媒体发布能力 - 运行时，可手工选择 */
    MEDIA(0x0405, "media", "媒体发布", 
        SelectionMode.MULTI, SwitchScope.RUNTIME, 
        "skill-media-mock"),
    
    /** 工作流能力 - 系统级，重启生效 */
    WORKFLOW(0x040A, "workflow", "工作流", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-workflow-memory"),
    
    /** 审批能力 - 系统级，重启生效 */
    APPROVAL(0x040F, "approval", "审批", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-approval-memory"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // IoT能力区 (0x0500 - 0x05FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 物联网能力 - 系统级，重启生效 */
    IOT(0x0500, "iot", "物联网", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-iot-mock"),
    
    /** 设备管理能力 - 系统级，重启生效 */
    DEVICE(0x0505, "iot.device", "设备管理", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-device-mock"),
    
    /** MQTT能力 - 系统级，重启生效 */
    MQTT(0x050F, "iot.mqtt", "MQTT", 
        SelectionMode.SINGLE, SwitchScope.SYSTEM, 
        "skill-mqtt-mock"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // UI能力区 (0x0600 - 0x06FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** UI生成能力 - 运行时，可手工选择 */
    UI(0x0600, "ui", "UI生成", 
        SelectionMode.SINGLE, SwitchScope.RUNTIME, 
        "skill-ui-console"),
    
    /** 仪表盘能力 - 运行时，可手工选择 */
    DASHBOARD(0x0605, "ui.dashboard", "仪表盘", 
        SelectionMode.SINGLE, SwitchScope.RUNTIME, 
        "skill-dashboard-default"),
    
    ;
    
    private final int baseAddress;             // 基地址
    private final String code;                 // 代码
    private final String name;                 // 名称
    private final SelectionMode selectionMode; // 选择模式
    private final SwitchScope switchScope;     // 切换范围
    private final String fallbackProvider;     // 降级固定提供者（PRIMARY地址）
    
    // ...
}

/**
 * 选择模式
 */
public enum SelectionMode {
    NONE,    // 不可选（系统内置）
    SINGLE,  // 单选（互斥）
    MULTI    // 多选（可组合）
}

/**
 * 切换范围
 */
public enum SwitchScope {
    NONE,     // 不可切换
    SYSTEM,   // 系统级，重启生效
    RUNTIME   // 运行时，可手工选择
}
```

---

## 五、切换范围详解

### 5.1 SYSTEM（系统级）

```yaml
# 系统级能力 - 切换需要重启

segments:
  - baseAddress: 0x0105
    code: database
    name: 数据库
    selectionMode: SINGLE
    switchScope: SYSTEM        # 系统级
    switchEffect: 需要重启系统生效
    
    slots:
      - offset: 0
        name: PRIMARY
        providers:
          - skill-db-postgresql
          - skill-db-mysql
          - skill-db-sqlite
        fallback: skill-db-sqlite

  - baseAddress: 0x0300
    code: org
    name: 组织架构
    selectionMode: SINGLE
    switchScope: SYSTEM        # 系统级
    switchEffect: 需要重启系统生效
    
    slots:
      - offset: 0
        name: PRIMARY
        providers:
          - skill-org-dingding
          - skill-org-feishu
          - skill-org-wecom
          - skill-org-ldap
        fallback: skill-org-local
```

### 5.2 RUNTIME（运行时）

```yaml
# 运行时能力 - 每次调用可手工选择

segments:
  - baseAddress: 0x0400
    code: payment
    name: 支付
    selectionMode: MULTI
    switchScope: RUNTIME       # 运行时
    switchEffect: 每次调用可手工选择
    
    slots:
      - offset: 0
        name: PRIMARY
        providers:
          - skill-payment-alipay
          - skill-payment-wechat
          - skill-payment-unionpay
        fallback: skill-payment-mock
        # 多选模式时，fallback 提供UI配置器/管理器
        role: CONFIG_MANAGER

  - baseAddress: 0x0200
    code: llm
    name: 大语言模型
    selectionMode: SINGLE
    switchScope: RUNTIME       # 运行时
    switchEffect: 每次调用可手工选择
    
    slots:
      - offset: 0
        name: PRIMARY
        providers:
          - skill-llm-openai
          - skill-llm-qianwen
          - skill-llm-deepseek
        fallback: skill-llm-ollama
```

---

## 六、多选模式的降级提供者角色

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                 多选模式的降级提供者角色                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   多选模式（MULTI）时，PRIMARY 地址的降级提供者承担特殊角色：                     │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  PAYMENT 支付能力                                                    │   │
│   │                                                                     │   │
│   │  PRIMARY (0x0400): skill-payment-mock                               │   │
│   │  ┌─────────────────────────────────────────────────────────────┐   │   │
│   │  │  角色：支付配置管理器                                         │   │   │
│   │  │  功能：                                                       │   │   │
│   │  │  - 提供支付渠道配置UI                                         │   │   │
│   │  │  - 管理多个支付渠道                                           │   │   │
│   │  │  - 路由支付请求到具体渠道                                     │   │   │
│   │  │  - 提供模拟支付功能（开发测试）                                │   │   │
│   │  └─────────────────────────────────────────────────────────────┘   │   │
│   │                                                                     │   │
│   │  STANDBY (0x0401): skill-payment-alipay   # 支付宝                  │   │
│   │  STANDBY (0x0402): skill-payment-wechat   # 微信支付                │   │
│   │  STANDBY (0x0403): skill-payment-unionpay # 银联                    │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  MEDIA 媒体发布能力                                                  │   │
│   │                                                                     │   │
│   │  PRIMARY (0x0405): skill-media-mock                                 │   │
│   │  ┌─────────────────────────────────────────────────────────────┐   │   │
│   │  │  角色：媒体发布配置管理器                                     │   │   │
│   │  │  功能：                                                       │   │   │
│   │  │  - 提供媒体平台配置UI                                         │   │   │
│   │  │  - 管理多个发布平台                                           │   │   │
│   │  │  - 分发内容到多个平台                                         │   │   │
│   │  │  - 提供模拟发布功能（开发测试）                                │   │   │
│   │  └─────────────────────────────────────────────────────────────┘   │   │
│   │                                                                     │   │
│   │  STANDBY (0x0406): skill-media-wechat     # 微信公众号              │   │
│   │  STANDBY (0x0407): skill-media-weibo      # 微博                    │   │
│   │  STANDBY (0x0408): skill-media-zhihu      # 知乎                    │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、配置示例

### 7.1 系统级能力配置

```yaml
# 数据库能力 - 系统级
capabilityBindings:
  0x0105:  # DATABASE
    mode: SINGLE
    scope: SYSTEM
    primary: skill-db-postgresql
    # 切换后需要重启系统
```

### 7.2 运行时能力配置（单选）

```yaml
# LLM能力 - 运行时单选
capabilityBindings:
  0x0200:  # LLM
    mode: SINGLE
    scope: RUNTIME
    primary: skill-llm-openai
    # 每次调用可手工选择模型
```

### 7.3 运行时能力配置（多选）

```yaml
# 支付能力 - 运行时多选
capabilityBindings:
  0x0400:  # PAYMENT
    mode: MULTI
    scope: RUNTIME
    primary: skill-payment-mock      # 配置管理器（降级）
    standbys:
      - skill-payment-alipay         # 支付宝
      - skill-payment-wechat         # 微信支付
      - skill-payment-unionpay       # 银联
    # 每次调用可手工选择支付渠道
```

---

## 八、能力段完整定义

```yaml
# capability-segments.yaml

segments:
  
  # ═══════════════════════════════════════════════════════════════════════
  # DATABASE 数据库能力段 (0x0105 - 0x0109)
  # ═══════════════════════════════════════════════════════════════════════
  
  - baseAddress: 0x0105
    code: database
    name: 数据库
    description: 数据库存储能力
    
    selectionMode: SINGLE
    switchScope: SYSTEM
    switchEffect: 需要重启系统生效
    
    slots:
      - offset: 0
        name: PRIMARY
        description: 主数据库
        role: DATA_PROVIDER
        providers:
          - skillId: skill-db-postgresql
            tier: large
            name: PostgreSQL
          - skillId: skill-db-mysql
            tier: medium
            name: MySQL
          - skillId: skill-db-sqlite
            tier: micro
            name: SQLite
        fallback: skill-db-sqlite
        
      - offset: 1
        name: STANDBY
        description: 备用数据库
        role: DATA_REPLICA
        providers: []
        
  # ═══════════════════════════════════════════════════════════════════════
  # ORG 组织架构能力段 (0x0300 - 0x0304)
  # ═══════════════════════════════════════════════════════════════════════
  
  - baseAddress: 0x0300
    code: org
    name: 组织架构
    description: 组织架构数据能力
    
    selectionMode: SINGLE
    switchScope: SYSTEM
    switchEffect: 需要重启系统生效
    
    slots:
      - offset: 0
        name: PRIMARY
        description: 主组织数据源
        role: DATA_PROVIDER
        providers:
          - skillId: skill-org-dingding
            tier: small
            name: 钉钉
          - skillId: skill-org-feishu
            tier: small
            name: 飞书
          - skillId: skill-org-wecom
            tier: small
            name: 企业微信
          - skillId: skill-org-ldap
            tier: medium
            name: LDAP
          - skillId: skill-org-local
            tier: micro
            name: 本地组织
        fallback: skill-org-local
        
  # ═══════════════════════════════════════════════════════════════════════
  # LLM 大语言模型能力段 (0x0200 - 0x0204)
  # ═══════════════════════════════════════════════════════════════════════
  
  - baseAddress: 0x0200
    code: llm
    name: 大语言模型
    description: 大语言模型对话能力
    
    selectionMode: SINGLE
    switchScope: RUNTIME
    switchEffect: 每次调用可手工选择
    
    slots:
      - offset: 0
        name: PRIMARY
        description: 主LLM提供者
        role: DATA_PROVIDER
        providers:
          - skillId: skill-llm-openai
            tier: large
            name: OpenAI
          - skillId: skill-llm-qianwen
            tier: large
            name: 通义千问
          - skillId: skill-llm-deepseek
            tier: medium
            name: DeepSeek
          - skillId: skill-llm-ollama
            tier: micro
            name: Ollama本地模型
        fallback: skill-llm-ollama
        
  # ═══════════════════════════════════════════════════════════════════════
  # PAYMENT 支付能力段 (0x0400 - 0x0404)
  # ═══════════════════════════════════════════════════════════════════════
  
  - baseAddress: 0x0400
    code: payment
    name: 支付
    description: 支付能力，支持多渠道
    
    selectionMode: MULTI
    switchScope: RUNTIME
    switchEffect: 每次调用可手工选择
    
    slots:
      - offset: 0
        name: PRIMARY
        description: 支付配置管理器
        role: CONFIG_MANAGER      # 特殊角色：配置管理器
        providers:
          - skillId: skill-payment-mock
            tier: micro
            name: 模拟支付
            features:
              - 支付渠道配置UI
              - 多渠道路由
              - 模拟支付
        fallback: skill-payment-mock
        
      - offset: 1
        name: STANDBY
        description: 支付渠道列表
        role: CHANNEL_LIST
        providers:
          - skillId: skill-payment-alipay
            tier: large
            name: 支付宝
          - skillId: skill-payment-wechat
            tier: large
            name: 微信支付
          - skillId: skill-payment-unionpay
            tier: large
            name: 银联
```

---

## 九、总结

### 核心设计

| 概念 | 说明 |
|------|------|
| **PRIMARY 地址** | 降级固定地址，保证系统可用 |
| **多选模式** | PRIMARY 提供配置管理器/UI |
| **切换范围** | SYSTEM(重启生效) / RUNTIME(运行时选择) |

### 切换范围分类

| 范围 | 能力 | 原因 |
|------|------|------|
| **SYSTEM** | database, vfs, org, auth, cache | 切换影响全局，需要重启 |
| **RUNTIME** | payment, media, llm, notification | 每次调用可手工选择 |

### 多选模式角色

| 角色 | 说明 | 示例 |
|------|------|------|
| `CONFIG_MANAGER` | 配置管理器，提供UI | skill-payment-mock |
| `DATA_PROVIDER` | 数据提供者 | skill-db-postgresql |
| `CHANNEL_LIST` | 渠道列表 | 支付渠道、媒体平台 |

---

**文档版本**: 2.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
