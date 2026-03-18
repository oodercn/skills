# 能力地址空间设计

## 一、核心概念

```
能力单元 = 固定地址空间

系统区域使用固定地址（如 0x0001, 0x0002...）
这些固定地址定义为枚举，作为能力的唯一标识。
```

---

## 二、能力地址空间

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力地址空间                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   地址范围           │  区域名称        │  说明                              │
│   ───────────────────┼──────────────────┼───────────────────────────────────│
│   0x0000 - 0x00FF    │  系统保留区      │  系统核心能力                      │
│   0x0100 - 0x01FF    │  基础能力区      │  基础服务能力                      │
│   0x0200 - 0x02FF    │  业务能力区      │  业务领域能力                      │
│   0x0300 - 0x03FF    │  扩展能力区      │  第三方扩展能力                    │
│   0x0400 - 0xFFFF    │  用户自定义区    │  用户自定义能力                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、能力地址枚举定义

```java
/**
 * 能力地址枚举
 * 固定地址定义，作为能力的唯一标识
 */
public enum CapabilityAddress {
    
    // ═══════════════════════════════════════════════════════════════════════
    // 系统保留区 (0x0000 - 0x00FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 系统核心能力 */
    SYSTEM_CORE(0x0000, "system.core", "系统核心"),
    
    /** 安装器能力 */
    INSTALLER(0x0001, "system.installer", "安装器"),
    
    /** 场景管理器能力 */
    SCENE_MANAGER(0x0002, "system.scene-manager", "场景管理器"),
    
    /** 能力注册表 */
    CAPABILITY_REGISTRY(0x0003, "system.capability-registry", "能力注册表"),
    
    /** 配置管理 */
    CONFIG_MANAGER(0x0004, "system.config-manager", "配置管理"),
    
    /** 日志服务 */
    LOGGING(0x0005, "system.logging", "日志服务"),
    
    /** 监控服务 */
    MONITOR(0x0006, "system.monitor", "监控服务"),
    
    /** 安全服务 */
    SECURITY(0x0007, "system.security", "安全服务"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 基础能力区 (0x0100 - 0x01FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 文件存储能力 */
    VFS(0x0100, "vfs", "文件存储"),
    
    /** 数据库能力 */
    DATABASE(0x0101, "database", "数据库"),
    
    /** 缓存能力 */
    CACHE(0x0102, "cache", "缓存"),
    
    /** 消息队列能力 */
    MESSAGE_QUEUE(0x0103, "message-queue", "消息队列"),
    
    /** 通知能力 */
    NOTIFICATION(0x0104, "notification", "通知"),
    
    /** 邮件能力 */
    EMAIL(0x0105, "email", "邮件"),
    
    /** 搜索能力 */
    SEARCH(0x0106, "search", "搜索"),
    
    /** 任务调度能力 */
    SCHEDULER(0x0107, "scheduler", "任务调度"),
    
    /** 文档处理能力 */
    DOCUMENT(0x0108, "document", "文档处理"),
    
    /** 报表能力 */
    REPORT(0x0109, "report", "报表"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // AI能力区 (0x0110 - 0x011F)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 大语言模型能力 */
    LLM(0x0110, "llm", "大语言模型"),
    
    /** LLM对话能力 */
    LLM_CHAT(0x0111, "llm.chat", "LLM对话"),
    
    /** LLM嵌入能力 */
    LLM_EMBEDDING(0x0112, "llm.embedding", "LLM嵌入"),
    
    /** 知识库能力 */
    KNOWLEDGE(0x0113, "knowledge", "知识库"),
    
    /** RAG能力 */
    RAG(0x0114, "rag", "RAG检索增强"),
    
    /** 向量存储能力 */
    VECTOR_STORE(0x0115, "vector-store", "向量存储"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 组织能力区 (0x0120 - 0x012F)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 组织架构能力 */
    ORG(0x0120, "org", "组织架构"),
    
    /** 用户认证能力 */
    AUTH(0x0121, "auth", "用户认证"),
    
    /** 权限管理能力 */
    PERMISSION(0x0122, "permission", "权限管理"),
    
    /** 角色管理能力 */
    ROLE(0x0123, "role", "角色管理"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 业务能力区 (0x0200 - 0x02FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 支付能力 */
    PAYMENT(0x0200, "payment", "支付"),
    
    /** 媒体发布能力 */
    MEDIA(0x0201, "media", "媒体发布"),
    
    /** 工作流能力 */
    WORKFLOW(0x0202, "workflow", "工作流"),
    
    /** 审批能力 */
    APPROVAL(0x0203, "approval", "审批"),
    
    /** 协作能力 */
    COLLABORATION(0x0204, "collaboration", "协作"),
    
    /** HR能力 */
    HR(0x0210, "hr", "人力资源"),
    
    /** CRM能力 */
    CRM(0x0211, "crm", "客户管理"),
    
    /** 财务能力 */
    FINANCE(0x0212, "finance", "财务管理"),
    
    /** 项目管理能力 */
    PROJECT(0x0213, "project", "项目管理"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // IoT能力区 (0x0220 - 0x022F)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 物联网能力 */
    IOT(0x0220, "iot", "物联网"),
    
    /** 设备管理能力 */
    DEVICE(0x0221, "iot.device", "设备管理"),
    
    /** 边缘计算能力 */
    EDGE(0x0222, "iot.edge", "边缘计算"),
    
    /** MQTT能力 */
    MQTT(0x0223, "iot.mqtt", "MQTT"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // UI能力区 (0x0130 - 0x013F)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** UI生成能力 */
    UI(0x0130, "ui", "UI生成"),
    
    /** 仪表盘能力 */
    DASHBOARD(0x0131, "ui.dashboard", "仪表盘"),
    
    /** 表单能力 */
    FORM(0x0132, "ui.form", "表单"),
    
    /** 图表能力 */
    CHART(0x0133, "ui.chart", "图表"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 扩展能力区 (0x0300 - 0x03FF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 扩展能力起始地址 */
    EXTENSION_START(0x0300, "extension", "扩展能力起始"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 用户自定义区 (0x0400 - 0xFFFF)
    // ═══════════════════════════════════════════════════════════════════════
    
    /** 用户自定义能力起始地址 */
    USER_DEFINED_START(0x0400, "user", "用户自定义起始"),
    
    ;
    
    private final int address;
    private final String code;
    private final String name;
    
    CapabilityAddress(int address, String code, String name) {
        this.address = address;
        this.code = code;
        this.name = name;
    }
    
    public int getAddress() { return address; }
    public String getCode() { return code; }
    public String getName() { return name; }
    
    /**
     * 根据地址获取能力
     */
    public static CapabilityAddress fromAddress(int address) {
        for (CapabilityAddress cap : values()) {
            if (cap.address == address) {
                return cap;
            }
        }
        return null;
    }
    
    /**
     * 根据代码获取能力
     */
    public static CapabilityAddress fromCode(String code) {
        for (CapabilityAddress cap : values()) {
            if (cap.code.equals(code)) {
                return cap;
            }
        }
        return null;
    }
}
```

---

## 四、能力地址映射表

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力地址映射表                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   地址      │  代码                │  名称          │  提供者示例            │
│   ──────────┼──────────────────────┼────────────────┼───────────────────────│
│   0x0000    │  system.core         │  系统核心       │  (内置)               │
│   0x0001    │  system.installer    │  安装器         │  (内置)               │
│   0x0002    │  system.scene-manager│  场景管理器     │  (内置)               │
│   ──────────┼──────────────────────┼────────────────┼───────────────────────│
│   0x0100    │  vfs                 │  文件存储       │  minio, oss, s3       │
│   0x0101    │  database            │  数据库         │  mysql, postgres      │
│   0x0102    │  cache               │  缓存           │  redis, memcached     │
│   0x0103    │  message-queue       │  消息队列       │  kafka, rabbitmq      │
│   0x0104    │  notification        │  通知           │  email, sms           │
│   ──────────┼──────────────────────┼────────────────┼───────────────────────│
│   0x0110    │  llm                 │  大语言模型     │  openai, qianwen      │
│   0x0111    │  llm.chat            │  LLM对话        │  openai, deepseek     │
│   0x0112    │  llm.embedding       │  LLM嵌入        │  openai, qianwen      │
│   0x0113    │  knowledge           │  知识库         │  knowledge-base       │
│   0x0114    │  rag                 │  RAG            │  rag-service          │
│   ──────────┼──────────────────────┼────────────────┼───────────────────────│
│   0x0120    │  org                 │  组织架构       │  dingding, feishu     │
│   0x0121    │  auth                │  用户认证       │  user-auth            │
│   0x0122    │  permission          │  权限管理       │  access-control       │
│   ──────────┼──────────────────────┼────────────────┼───────────────────────│
│   0x0200    │  payment             │  支付           │  alipay, wechat       │
│   0x0201    │  media               │  媒体发布       │  wechat-mp, weibo     │
│   0x0202    │  workflow            │  工作流         │  approval-workflow    │
│   0x0210    │  hr                  │  人力资源       │  recruitment          │
│   0x0211    │  crm                 │  客户管理       │  customer-service     │
│   ──────────┼──────────────────────┼────────────────┼───────────────────────│
│   0x0220    │  iot                 │  物联网         │  mqtt, device-mgr     │
│   ──────────┼──────────────────────┼────────────────┼───────────────────────│
│   0x0130    │  ui                  │  UI生成         │  a2ui, dashboard      │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 五、技能配置示例

### 5.1 场景技能

```yaml
- skillId: skill-recruitment-assistant
  name: 招聘助手
  type: SCENE
  domain: hr
  
  # 声明需要的能力（使用地址或代码）
  requiredCapabilities:
    - address: 0x0100        # VFS 文件存储
    - address: 0x0120        # ORG 组织架构
    - address: 0x0104        # NOTIFICATION 通知
      
  # 或使用代码
  # requiredCapabilities:
  #   - code: vfs
  #   - code: org
  #   - code: notification
  
  # 能力绑定
  capabilityBindings:
    0x0100: skill-vfs-minio      # VFS → MinIO
    0x0120: skill-org-dingding   # ORG → 钉钉
    0x0104: skill-email          # NOTIFICATION → 邮件
```

### 5.2 独立技能（提供者）

```yaml
- skillId: skill-vfs-minio
  name: MinIO存储服务
  type: PROVIDER
  subType: DRIVER
  
  # 提供的能力（使用地址）
  provides:
    address: 0x0100           # VFS
    code: vfs
    
  # 驱动属性
  driverGroup: storage
  tier: medium
  exclusive: true
```

---

## 六、能力注册表

```yaml
# 能力注册表配置

capabilityRegistry:
  
  # 系统能力（内置，不可覆盖）
  system:
    - address: 0x0000
      code: system.core
      name: 系统核心
      builtIn: true
      
    - address: 0x0001
      code: system.installer
      name: 安装器
      builtIn: true
      
    - address: 0x0002
      code: system.scene-manager
      name: 场景管理器
      builtIn: true
  
  # 基础能力
  basic:
    - address: 0x0100
      code: vfs
      name: 文件存储
      providers:
        - skill-vfs-local (micro)
        - skill-vfs-database (small)
        - skill-vfs-minio (medium)
        - skill-vfs-oss (large)
        - skill-vfs-s3 (large)
      default: skill-vfs-local
      
    - address: 0x0101
      code: database
      name: 数据库
      providers:
        - skill-db-sqlite (micro)
        - skill-db-mysql (small)
        - skill-db-postgresql (medium)
      default: skill-db-sqlite
      
    - address: 0x0110
      code: llm
      name: 大语言模型
      providers:
        - skill-llm-openai
        - skill-llm-qianwen
        - skill-llm-deepseek
      default: skill-llm-openai
      
    - address: 0x0120
      code: org
      name: 组织架构
      providers:
        - skill-org-dingding
        - skill-org-feishu
        - skill-org-wecom
        - skill-org-ldap
      default: skill-org-dingding
```

---

## 七、能力地址查询

### 7.1 按地址查询

```java
// 根据地址获取能力信息
CapabilityAddress cap = CapabilityAddress.fromAddress(0x0100);
// cap.getCode() = "vfs"
// cap.getName() = "文件存储"
```

### 7.2 按代码查询

```java
// 根据代码获取能力信息
CapabilityAddress cap = CapabilityAddress.fromCode("vfs");
// cap.getAddress() = 0x0100
// cap.getName() = "文件存储"
```

### 7.3 能力匹配

```java
// 场景声明需要的能力
int[] requiredCapabilities = {0x0100, 0x0120, 0x0104};

// 查找提供者
for (int address : requiredCapabilities) {
    CapabilityAddress cap = CapabilityAddress.fromAddress(address);
    List<Skill> providers = capabilityRegistry.getProviders(cap);
    // ...
}
```

---

## 八、优势

| 特性 | 说明 |
|------|------|
| **唯一性** | 地址是唯一标识，不会冲突 |
| **高效查询** | 整数地址查询，O(1) 复杂度 |
| **可扩展** | 预留扩展区域，支持自定义能力 |
| **类型安全** | 枚举定义，编译时检查 |
| **向后兼容** | 地址固定，代码可变 |

---

## 九、总结

```
能力地址空间设计：

1. 能力 = 固定地址（枚举）
2. 地址空间分区：系统区、基础区、业务区、扩展区、用户区
3. 场景声明能力地址，独立技能提供能力地址
4. 地址查询高效，类型安全
```

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
