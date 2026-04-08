# Engine 团队协同任务说明

## 一、背景

Skills 团队已完成能力地址空间设计（v6.0），采用 **1+6+1** 模式，需要 Engine 团队协同实现以下功能。

---

## 二、设计变更说明

### 2.1 核心变更

| 项目 | 旧设计 | 新设计 |
|------|--------|--------|
| 地址模式 | 每类型5地址 | **1+6+1模式** (每分类8地址) |
| 地址含义 | 能力段+槽位偏移 | **地址即路由** |
| 驱动关系 | 互斥（单选） | **多驱动并存** |
| 总地址数 | 256 | 256 |
| 已分配 | 不明确 | 128 (16分类×8) |
| 扩展区 | 不明确 | 128 (0x80-0xFF) |

### 2.2 1+6+1 模式说明

```
每个分类 8 个地址:
├── 0x?0: 默认地址 (fallback)
├── 0x?1 - 0x?6: 6个常用驱动地址
└── 0x?7: 预留扩展地址

示例 - 组织区 (ORG) 0x08 - 0x0F:
├── 0x08: org.local      [默认] 本地组织管理
├── 0x09: org.dingding   钉钉组织数据
├── 0x0A: org.feishu     飞书组织数据
├── 0x0B: org.wecom      企业微信组织数据
├── 0x0C: org.ldap       LDAP组织数据
├── 0x0D: org.ad         AD域
├── 0x0E: org.custom     自定义组织源
└── 0x0F: org.reserved   [预留] 扩展
```

### 2.3 地址即路由

```java
// 调用钉钉组织服务 - 地址作为路由标识
CapabilityAddress address = CapabilityAddress.ORG_DINGDING; // 0x09
orgService.getUsers(address);

// 多驱动同时运行
Set<CapabilityAddress> activeOrgs = Set.of(
    CapabilityAddress.ORG_DINGDING,   // 0x09 钉钉
    CapabilityAddress.ORG_FEISHU,     // 0x0A 飞书
    CapabilityAddress.ORG_WECOM       // 0x0B 企业微信
);
// 通过地址路由到对应驱动实例
```

---

## 三、协同任务清单

### 3.1 能力地址枚举实现

**优先级**: P0（高）

**任务描述**:
实现能力地址枚举，每个地址对应一个具体的驱动/服务。

**技术要求**:
```java
public enum CapabilityAddress {
    
    // ========== 系统区 (SYS) 0x00 - 0x07 ==========
    SYS_CORE(0x00, "sys.core", "系统核心", true),
    SYS_INSTALLER(0x01, "sys.installer", "安装器", false),
    SYS_REGISTRY(0x02, "sys.registry", "能力注册表", false),
    SYS_CONFIG(0x03, "sys.config", "配置管理", false),
    SYS_LOGGING(0x04, "sys.logging", "日志服务", false),
    SYS_AUDIT(0x05, "sys.audit", "审计服务", false),
    SYS_MONITOR(0x06, "sys.monitor", "系统监控", false),
    SYS_RESERVED(0x07, "sys.reserved", "系统预留", false),
    
    // ========== 组织区 (ORG) 0x08 - 0x0F ==========
    ORG_LOCAL(0x08, "org.local", "本地组织", true),
    ORG_DINGDING(0x09, "org.dingding", "钉钉", false),
    ORG_FEISHU(0x0A, "org.feishu", "飞书", false),
    ORG_WECOM(0x0B, "org.wecom", "企业微信", false),
    ORG_LDAP(0x0C, "org.ldap", "LDAP", false),
    ORG_AD(0x0D, "org.ad", "AD域", false),
    ORG_CUSTOM(0x0E, "org.custom", "自定义组织", false),
    ORG_RESERVED(0x0F, "org.reserved", "组织预留", false),
    
    // ========== 认证区 (AUTH) 0x10 - 0x17 ==========
    AUTH_LOCAL(0x10, "auth.local", "本地认证", true),
    AUTH_DINGDING(0x11, "auth.dingding", "钉钉认证", false),
    AUTH_FEISHU(0x12, "auth.feishu", "飞书认证", false),
    AUTH_WECOM(0x13, "auth.wecom", "企业微信认证", false),
    AUTH_LDAP(0x14, "auth.ldap", "LDAP认证", false),
    AUTH_SAML(0x15, "auth.saml", "SAML认证", false),
    AUTH_OAUTH2(0x16, "auth.oauth2", "OAuth2认证", false),
    AUTH_RESERVED(0x17, "auth.reserved", "认证预留", false),
    
    // ========== 存储区 (VFS) 0x18 - 0x1F ==========
    VFS_LOCAL(0x18, "vfs.local", "本地存储", true),
    VFS_DATABASE(0x19, "vfs.database", "数据库存储", false),
    VFS_MINIO(0x1A, "vfs.minio", "MinIO", false),
    VFS_OSS(0x1B, "vfs.oss", "阿里云OSS", false),
    VFS_S3(0x1C, "vfs.s3", "AWS S3", false),
    VFS_COS(0x1D, "vfs.cos", "腾讯云COS", false),
    VFS_OBS(0x1E, "vfs.obs", "华为云OBS", false),
    VFS_RESERVED(0x1F, "vfs.reserved", "存储预留", false),
    
    // ========== 数据库区 (DB) 0x20 - 0x27 ==========
    DB_SQLITE(0x20, "db.sqlite", "SQLite", true),
    DB_MYSQL(0x21, "db.mysql", "MySQL", false),
    DB_POSTGRESQL(0x22, "db.postgresql", "PostgreSQL", false),
    DB_MONGODB(0x23, "db.mongodb", "MongoDB", false),
    DB_REDIS(0x24, "db.redis", "Redis", false),
    DB_ELASTICSEARCH(0x25, "db.elasticsearch", "Elasticsearch", false),
    DB_CLICKHOUSE(0x26, "db.clickhouse", "ClickHouse", false),
    DB_RESERVED(0x27, "db.reserved", "数据库预留", false),
    
    // ========== AI区 (LLM) 0x28 - 0x2F ==========
    LLM_OLLAMA(0x28, "llm.ollama", "Ollama本地", true),
    LLM_OPENAI(0x29, "llm.openai", "OpenAI", false),
    LLM_QIANWEN(0x2A, "llm.qianwen", "通义千问", false),
    LLM_DEEPSEEK(0x2B, "llm.deepseek", "DeepSeek", false),
    LLM_VOLCENGINE(0x2C, "llm.volcengine", "火山引擎", false),
    LLM_CLAUDE(0x2D, "llm.claude", "Claude", false),
    LLM_GEMINI(0x2E, "llm.gemini", "Gemini", false),
    LLM_RESERVED(0x2F, "llm.reserved", "LLM预留", false),
    
    // ========== 知识区 (KNOW) 0x30 - 0x37 ==========
    KNOW_LOCAL(0x30, "know.local", "本地知识库", true),
    KNOW_RAG(0x31, "know.rag", "RAG服务", false),
    KNOW_MILVUS(0x32, "know.milvus", "Milvus", false),
    KNOW_PINECONE(0x33, "know.pinecone", "Pinecone", false),
    KNOW_WEAVIATE(0x34, "know.weaviate", "Weaviate", false),
    KNOW_QDRANT(0x35, "know.qdrant", "Qdrant", false),
    KNOW_CHROMA(0x36, "know.chroma", "Chroma", false),
    KNOW_RESERVED(0x37, "know.reserved", "知识库预留", false),
    
    // ========== 支付区 (PAY) 0x38 - 0x3F ==========
    PAY_MOCK(0x38, "pay.mock", "模拟支付", true),
    PAY_ALIPAY(0x39, "pay.alipay", "支付宝", false),
    PAY_WECHAT(0x3A, "pay.wechat", "微信支付", false),
    PAY_UNIONPAY(0x3B, "pay.unionpay", "银联", false),
    PAY_STRIPE(0x3C, "pay.stripe", "Stripe", false),
    PAY_PAYPAL(0x3D, "pay.paypal", "PayPal", false),
    PAY_CUSTOM(0x3E, "pay.custom", "自定义支付", false),
    PAY_RESERVED(0x3F, "pay.reserved", "支付预留", false),
    
    // ========== 媒体区 (MEDIA) 0x40 - 0x47 ==========
    MEDIA_MOCK(0x40, "media.mock", "模拟发布", true),
    MEDIA_WECHAT(0x41, "media.wechat", "微信公众号", false),
    MEDIA_WEIBO(0x42, "media.weibo", "微博", false),
    MEDIA_ZHIHU(0x43, "media.zhihu", "知乎", false),
    MEDIA_TOUTIAO(0x44, "media.toutiao", "头条", false),
    MEDIA_XIAOHONGSHU(0x45, "media.xiaohongshu", "小红书", false),
    MEDIA_DOUYIN(0x46, "media.douyin", "抖音", false),
    MEDIA_RESERVED(0x47, "media.reserved", "媒体预留", false),
    
    // ========== 通讯区 (COMM) 0x48 - 0x4F ==========
    COMM_CONSOLE(0x48, "comm.console", "控制台通知", true),
    COMM_EMAIL(0x49, "comm.email", "邮件通知", false),
    COMM_SMS(0x4A, "comm.sms", "短信通知", false),
    COMM_MQTT(0x4B, "comm.mqtt", "MQTT消息", false),
    COMM_WEBSOCKET(0x4C, "comm.websocket", "WebSocket", false),
    COMM_WEBHOOK(0x4D, "comm.webhook", "Webhook", false),
    COMM_DINGDING(0x4E, "comm.dingding", "钉钉机器人", false),
    COMM_RESERVED(0x4F, "comm.reserved", "通讯预留", false),
    
    // ========== 监控区 (MON) 0x50 - 0x57 ==========
    MON_LOCAL(0x50, "mon.local", "本地监控", true),
    MON_PROMETHEUS(0x51, "mon.prometheus", "Prometheus", false),
    MON_GRAFANA(0x52, "mon.grafana", "Grafana", false),
    MON_JAEGER(0x53, "mon.jaeger", "Jaeger", false),
    MON_ZIPKIN(0x54, "mon.zipkin", "Zipkin", false),
    MON_SKYWALKING(0x55, "mon.skywalking", "SkyWalking", false),
    MON_DATADOG(0x56, "mon.datadog", "Datadog", false),
    MON_RESERVED(0x57, "mon.reserved", "监控预留", false),
    
    // ========== IoT区 (IOT) 0x58 - 0x5F ==========
    IOT_MOCK(0x58, "iot.mock", "模拟IoT", true),
    IOT_DEVICE(0x59, "iot.device", "设备管理", false),
    IOT_GATEWAY(0x5A, "iot.gateway", "网关管理", false),
    IOT_MODBUS(0x5B, "iot.modbus", "Modbus", false),
    IOT_OPCUA(0x5C, "iot.opcua", "OPC-UA", false),
    IOT_COAP(0x5D, "iot.coap", "CoAP", false),
    IOT_LORA(0x5E, "iot.lora", "LoRa", false),
    IOT_RESERVED(0x5F, "iot.reserved", "IoT预留", false),
    
    // ========== 搜索区 (SEARCH) 0x60 - 0x67 ==========
    SEARCH_LOCAL(0x60, "search.local", "本地搜索", true),
    SEARCH_ES(0x61, "search.es", "Elasticsearch", false),
    SEARCH_SOLR(0x62, "search.solr", "Solr", false),
    SEARCH_MEILISEARCH(0x63, "search.meilisearch", "Meilisearch", false),
    SEARCH_TYPESENSE(0x64, "search.typesense", "Typesense", false),
    SEARCH_ALGOLIA(0x65, "search.algolia", "Algolia", false),
    SEARCH_WHOOSH(0x66, "search.whoosh", "Whoosh", false),
    SEARCH_RESERVED(0x67, "search.reserved", "搜索预留", false),
    
    // ========== 调度区 (SCHED) 0x68 - 0x6F ==========
    SCHED_LOCAL(0x68, "sched.local", "本地调度", true),
    SCHED_QUARTZ(0x69, "sched.quartz", "Quartz", false),
    SCHED_XXJOB(0x6A, "sched.xxjob", "XXL-Job", false),
    SCHED_ELASTICJOB(0x6B, "sched.elasticjob", "ElasticJob", false),
    SCHED_CELERY(0x6C, "sched.celery", "Celery", false),
    SCHED_TEMPORAL(0x6D, "sched.temporal", "Temporal", false),
    SCHED_AIRFLOW(0x6E, "sched.airflow", "Airflow", false),
    SCHED_RESERVED(0x6F, "sched.reserved", "调度预留", false),
    
    // ========== 安全区 (SEC) 0x70 - 0x77 ==========
    SEC_LOCAL(0x70, "sec.local", "本地安全", true),
    SEC_RBAC(0x71, "sec.rbac", "RBAC", false),
    SEC_ABAC(0x72, "sec.abac", "ABAC", false),
    SEC_ACL(0x73, "sec.acl", "ACL", false),
    SEC_CASBIN(0x74, "sec.casbin", "Casbin", false),
    SEC_VAULT(0x75, "sec.vault", "Vault", false),
    SEC_WAF(0x76, "sec.waf", "WAF", false),
    SEC_RESERVED(0x77, "sec.reserved", "安全预留", false),
    
    // ========== 网络区 (NET) 0x78 - 0x7F ==========
    NET_LOCAL(0x78, "net.local", "本地网络", true),
    NET_DNS(0x79, "net.dns", "DNS服务", false),
    NET_DHCP(0x7A, "net.dhcp", "DHCP服务", false),
    NET_PROXY(0x7B, "net.proxy", "代理服务", false),
    NET_VPN(0x7C, "net.vpn", "VPN服务", false),
    NET_FIREWALL(0x7D, "net.firewall", "防火墙", false),
    NET_LOADBALANCER(0x7E, "net.loadbalancer", "负载均衡", false),
    NET_RESERVED(0x7F, "net.reserved", "网络预留", false);
    
    private final int address;
    private final String code;
    private final String name;
    private final boolean isDefault;
    
    // 获取分类
    public CapabilityCategory getCategory() {
        return CapabilityCategory.fromAddress(this.address);
    }
    
    // 获取分类基地址
    public int getCategoryBase() {
        return this.address & 0xF8; // 清除低3位
    }
    
    // 是否为预留地址
    public boolean isReserved() {
        return (this.address & 0x07) == 0x07;
    }
}

public enum CapabilityCategory {
    SYS(0x00, "系统"),
    ORG(0x08, "组织"),
    AUTH(0x10, "认证"),
    VFS(0x18, "存储"),
    DB(0x20, "数据库"),
    LLM(0x28, "AI"),
    KNOW(0x30, "知识"),
    PAY(0x38, "支付"),
    MEDIA(0x40, "媒体"),
    COMM(0x48, "通讯"),
    MON(0x50, "监控"),
    IOT(0x58, "IoT"),
    SEARCH(0x60, "搜索"),
    SCHED(0x68, "调度"),
    SEC(0x70, "安全"),
    NET(0x78, "网络");
    
    public static CapabilityCategory fromAddress(int address) {
        int base = address & 0xF8;
        for (CapabilityCategory cat : values()) {
            if (cat.baseAddress == base) return cat;
        }
        return null; // 扩展区
    }
}
```

**交付物**:
- [ ] CapabilityAddress 枚举实现 (128个地址)
- [ ] CapabilityCategory 枚举实现 (16个分类)
- [ ] 地址分类查询工具类

---

### 3.2 能力路由器实现

**优先级**: P0（高）

**任务描述**:
实现能力路由器，根据地址路由到对应的驱动实例。

**技术要求**:
```java
public interface CapabilityRouter {
    
    /**
     * 根据地址获取驱动实例
     * @param address 能力地址
     * @return 驱动实例
     */
    <T> T getDriver(CapabilityAddress address, Class<T> driverType);
    
    /**
     * 获取分类下所有活跃的驱动
     * @param category 能力分类
     * @return 活跃驱动地址集合
     */
    Set<CapabilityAddress> getActiveDrivers(CapabilityCategory category);
    
    /**
     * 注册驱动实例
     * @param address 能力地址
     * @param driver 驱动实例
     */
    void registerDriver(CapabilityAddress address, Object driver);
    
    /**
     * 注销驱动实例
     * @param address 能力地址
     */
    void unregisterDriver(CapabilityAddress address);
}

@Service
public class CapabilityRouterImpl implements CapabilityRouter {
    
    private final Map<CapabilityAddress, Object> driverRegistry = new ConcurrentHashMap<>();
    
    @Override
    public <T> T getDriver(CapabilityAddress address, Class<T> driverType) {
        Object driver = driverRegistry.get(address);
        if (driver == null) {
            // 降级到默认驱动
            CapabilityAddress defaultAddr = getDefaultAddress(address.getCategory());
            driver = driverRegistry.get(defaultAddr);
        }
        return driverType.cast(driver);
    }
    
    @Override
    public Set<CapabilityAddress> getActiveDrivers(CapabilityCategory category) {
        return driverRegistry.keySet().stream()
            .filter(addr -> addr.getCategory() == category)
            .collect(Collectors.toSet());
    }
    
    private CapabilityAddress getDefaultAddress(CapabilityCategory category) {
        // 返回分类的默认地址 (基地址)
        return CapabilityAddress.fromAddress(category.getBaseAddress());
    }
}
```

**交付物**:
- [ ] CapabilityRouter 接口定义
- [ ] CapabilityRouterImpl 实现
- [ ] 驱动注册/注销机制
- [ ] 默认降级逻辑

---

### 3.3 多驱动管理器实现

**优先级**: P0（高）

**任务描述**:
实现多驱动管理器，支持同一分类下多个驱动同时运行。

**技术要求**:
```java
public interface MultiDriverManager {
    
    /**
     * 启动驱动
     * @param address 能力地址
     * @param config 驱动配置
     * @return 是否启动成功
     */
    boolean startDriver(CapabilityAddress address, DriverConfig config);
    
    /**
     * 停止驱动
     * @param address 能力地址
     */
    void stopDriver(CapabilityAddress address);
    
    /**
     * 获取驱动状态
     * @param address 能力地址
     * @return 驱动状态
     */
    DriverStatus getDriverStatus(CapabilityAddress address);
    
    /**
     * 获取分类下所有驱动状态
     * @param category 能力分类
     * @return 驱动状态映射
     */
    Map<CapabilityAddress, DriverStatus> getCategoryStatus(CapabilityCategory category);
}

@Data
public class DriverConfig {
    private CapabilityAddress address;
    private String skillId;
    private Map<String, Object> properties;
    private boolean autoStart;
    private int priority;
}

public enum DriverStatus {
    STOPPED,      // 已停止
    STARTING,     // 启动中
    RUNNING,      // 运行中
    STOPPING,     // 停止中
    ERROR         // 错误
}
```

**交付物**:
- [ ] MultiDriverManager 接口定义
- [ ] MultiDriverManagerImpl 实现
- [ ] 驱动生命周期管理
- [ ] 驱动状态监控

---

### 3.4 能力绑定配置

**优先级**: P1（中）

**任务描述**:
实现能力绑定的多级配置和降级机制。

**降级优先级**:
```
1. 用户显式配置 → 使用用户指定的驱动
2. 场景默认配置 → 使用场景推荐的驱动
3. 系统配置(环境) → 根据环境自动选择
4. 枚举默认兜底 → 使用分类默认地址
```

**技术要求**:
```java
@Configuration
@ConfigurationProperties(prefix = "capability")
public class CapabilityBindingConfig {
    
    /**
     * 分类配置
     * key: 分类代码 (org, vfs, llm...)
     * value: 驱动地址列表
     */
    private Map<String, List<AddressBinding>> categories = new HashMap<>();
    
    /**
     * 默认绑定
     * key: 分类代码
     * value: 默认驱动地址
     */
    private Map<String, Integer> defaults = new HashMap<>();
}

@Data
public class AddressBinding {
    private int address;
    private String skillId;
    private boolean enabled;
    private Map<String, Object> config;
}

// YAML 配置示例
/*
capability:
  categories:
    org:
      - address: 0x08
        skillId: skill-org-local
        enabled: true
      - address: 0x09
        skillId: skill-org-dingding
        enabled: true
        config:
          appKey: xxx
          appSecret: xxx
      - address: 0x0A
        skillId: skill-org-feishu
        enabled: false
    llm:
      - address: 0x28
        skillId: skill-llm-ollama
        enabled: true
      - address: 0x29
        skillId: skill-llm-openai
        enabled: true
        config:
          apiKey: sk-xxx
  defaults:
    org: 0x08
    llm: 0x28
*/
```

**交付物**:
- [ ] CapabilityBindingConfig 配置类
- [ ] YAML 配置加载
- [ ] 多级降级解析器

---

### 3.5 场景能力需求声明

**优先级**: P1（中）

**任务描述**:
实现场景能力需求声明，支持场景指定需要的驱动。

**技术要求**:
```yaml
# skill.yaml 场景能力需求声明示例
sceneId: hr-recruitment
name: 招聘场景
version: "1.0.0"

capabilities:
  # 组织能力 - 需要钉钉组织数据
  - category: ORG
    required: true
    preferred: ORG_DINGDING  # 优先使用钉钉
    fallback: ORG_LOCAL      # 降级到本地
    
  # AI能力 - 需要LLM
  - category: LLM
    required: true
    preferred: LLM_QIANWEN   # 优先使用通义千问
    fallback: LLM_OLLAMA     # 降级到本地Ollama
    
  # 存储能力 - 可选
  - category: VFS
    required: false
    preferred: VFS_OSS       # 优先使用OSS
```

```java
@Data
public class SceneCapabilityRequirement {
    private CapabilityCategory category;
    private boolean required;
    private CapabilityAddress preferred;
    private CapabilityAddress fallback;
}

public interface SceneCapabilityResolver {
    
    /**
     * 解析场景能力需求
     * @param sceneId 场景ID
     * @return 能力地址映射
     */
    Map<CapabilityCategory, CapabilityAddress> resolve(String sceneId);
    
    /**
     * 检查场景能力是否满足
     * @param sceneId 场景ID
     * @return 不满足的能力列表
     */
    List<CapabilityCategory> checkRequirements(String sceneId);
}
```

**交付物**:
- [ ] SceneCapabilityRequirement 数据结构
- [ ] SceneCapabilityResolver 实现
- [ ] 场景能力需求检查

---

## 四、数据结构

### 4.1 能力地址信息

```java
@Data
public class CapabilityAddressInfo {
    private int address;
    private String code;
    private String name;
    private CapabilityCategory category;
    private boolean isDefault;
    private boolean isReserved;
    private String skillId;
    private DriverStatus status;
}
```

### 4.2 分类统计信息

```java
@Data
public class CategoryStats {
    private CapabilityCategory category;
    private int totalAddresses;      // 总地址数 (8)
    private int activeDrivers;       // 活跃驱动数
    private int defaultAddress;      // 默认地址
    private int reservedAddress;     // 预留地址
}
```

---

## 五、时间计划

| 阶段 | 任务 | 预计时间 | 优先级 |
|------|------|----------|:------:|
| **Phase 1** | CapabilityAddress 枚举 | 1天 | P0 |
| **Phase 1** | CapabilityRouter 实现 | 2天 | P0 |
| **Phase 1** | MultiDriverManager 实现 | 2天 | P0 |
| **Phase 2** | 能力绑定配置 | 2天 | P1 |
| **Phase 2** | 场景能力需求声明 | 2天 | P1 |
| **Phase 3** | 集成测试 | 2天 | P2 |
| **Phase 3** | 文档完善 | 1天 | P2 |

---

## 六、依赖关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        任务依赖关系                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   CapabilityAddress枚举 ──────────────────→ CapabilityRouter实现             │
│            │                                                               │
│            ↓                                                               │
│   MultiDriverManager实现 ─────────────────→ 能力绑定配置                     │
│            │                                                               │
│            ↓                                                               │
│   场景能力需求声明 ────────────────────────→ 集成测试                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、验收标准

### 7.1 功能验收

- [ ] 128个能力地址正确定义
- [ ] 16个能力分类正确划分
- [ ] 地址路由正确工作
- [ ] 多驱动可同时运行
- [ ] 默认降级机制正常工作
- [ ] 场景能力需求正确解析

### 7.2 性能验收

- [ ] 地址路由响应时间 < 1ms
- [ ] 驱动启动时间 < 5s
- [ ] 系统启动时驱动加载时间 < 10s

### 7.3 兼容性验收

- [ ] 向后兼容现有技能配置
- [ ] 支持平滑升级
- [ ] 扩展区 (0x80-0xFF) 可正常使用

---

## 八、参考文档

- [能力地址空间设计 v6.0](./capability-address-space-design-v5.md)
- [LLM多级配置示例](./llm-multi-level-config-example.md)

---

## 九、联系方式

| 角色 | 联系人 | 职责 |
|------|--------|------|
| Skills 团队 | - | 能力分类设计、技能迁移 |
| Engine 团队 | - | 能力框架实现、运行时支持 |

---

**文档版本**: 2.0.0  
**更新日期**: 2026-03-11  
**作者**: Skills Team
