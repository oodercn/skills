# 能力地址空间设计 - 修正版

## 一、地址分配原则

```
能力地址 = 能力基地址 + 驱动偏移地址

划分原则：
- 需要挂接驱动的能力，其地址段包含驱动地址
- 能力基地址：能力的抽象定义
- 驱动偏移地址：具体实现的驱动
```

---

## 二、地址空间规划

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力地址空间规划                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   系统保留区 (0x0000 - 0x00FF)                                               │
│   ├── 0x0000: system.core         # 系统核心                                 │
│   ├── 0x0001: system.installer    # 安装器                                   │
│   ├── 0x0002: system.scene-manager # 场景管理器                              │
│   └── 0x0003: system.capability-registry # 能力注册表                        │
│                                                                             │
│   组织能力区 (0x0100 - 0x01FF)                                               │
│   ├── 0x0100: org                 # 组织架构能力（基地址）                    │
│   │   ├── 0x0100: org-base        # 组织基础服务                             │
│   │   ├── 0x0101: org-dingding    # 钉钉驱动                                 │
│   │   ├── 0x0102: org-feishu      # 飞书驱动                                 │
│   │   ├── 0x0103: org-wecom       # 企业微信驱动                             │
│   │   ├── 0x0104: org-ldap        # LDAP驱动                                 │
│   │   └── 0x010F: org-local       # 本地组织（兜底）                          │
│   │                                                                       │
│   ├── 0x0110: auth                # 认证能力（基地址）                        │
│   │   ├── 0x0110: auth-base       # 认证基础服务                             │
│   │   ├── 0x0111: auth-dingding   # 钉钉认证                                 │
│   │   ├── 0x0112: auth-feishu     # 飞书认证                                 │
│   │   └── 0x011F: auth-local      # 本地认证                                 │
│   │                                                                       │
│   └── 0x0120: permission          # 权限能力                                 │
│                                                                             │
│   存储能力区 (0x0200 - 0x02FF)                                               │
│   ├── 0x0200: vfs                 # 文件存储能力（基地址）                    │
│   │   ├── 0x0200: vfs-base        # VFS基础服务                              │
│   │   ├── 0x0201: vfs-local       # 本地存储                                 │
│   │   ├── 0x0202: vfs-database    # 数据库存储                               │
│   │   ├── 0x0203: vfs-minio       # MinIO存储                                │
│   │   ├── 0x0204: vfs-oss         # 阿里云OSS                                │
│   │   └── 0x0205: vfs-s3          # AWS S3                                   │
│   │                                                                       │
│   ├── 0x0210: database            # 数据库能力（基地址）                      │
│   │   ├── 0x0210: db-base         # 数据库基础服务                           │
│   │   ├── 0x0211: db-sqlite       # SQLite                                   │
│   │   ├── 0x0212: db-mysql        # MySQL                                    │
│   │   ├── 0x0213: db-postgresql   # PostgreSQL                               │
│   │   └── 0x0214: db-mongodb      # MongoDB                                  │
│   │                                                                       │
│   └── 0x0220: cache               # 缓存能力                                 │
│                                                                             │
│   AI能力区 (0x0300 - 0x03FF)                                                 │
│   ├── 0x0300: llm                 # 大语言模型能力（基地址）                  │
│   │   ├── 0x0300: llm-base        # LLM基础服务                              │
│   │   ├── 0x0301: llm-openai      # OpenAI                                   │
│   │   ├── 0x0302: llm-qianwen     # 通义千问                                 │
│   │   ├── 0x0303: llm-deepseek    # DeepSeek                                 │
│   │   ├── 0x0304: llm-volcengine  # 火山引擎                                 │
│   │   └── 0x030F: llm-ollama      # Ollama本地（兜底）                        │
│   │                                                                       │
│   ├── 0x0310: knowledge           # 知识库能力                               │
│   ├── 0x0320: rag                 # RAG能力                                  │
│   └── 0x0330: vector-store        # 向量存储能力                             │
│                                                                             │
│   支付能力区 (0x0400 - 0x04FF)                                               │
│   ├── 0x0400: payment             # 支付能力（基地址）                        │
│   │   ├── 0x0400: payment-base    # 支付基础服务                             │
│   │   ├── 0x0401: payment-alipay  # 支付宝                                   │
│   │   ├── 0x0402: payment-wechat  # 微信支付                                 │
│   │   ├── 0x0403: payment-unionpay # 银联                                    │
│   │   └── 0x040F: payment-mock    # 模拟支付（兜底）                          │
│   │                                                                       │
│   └── 0x0410: media               # 媒体发布能力                             │
│       ├── 0x0410: media-base      # 媒体基础服务                             │
│       ├── 0x0411: media-wechat    # 微信公众号                               │
│       ├── 0x0412: media-weibo     # 微博                                     │
│       ├── 0x0413: media-zhihu     # 知乎                                     │
│       └── 0x041F: media-mock      # 模拟发布                                 │
│                                                                             │
│   通讯能力区 (0x0500 - 0x05FF)                                               │
│   ├── 0x0500: communication       # 通讯能力                                 │
│   ├── 0x0510: notification        # 通知能力                                 │
│   └── 0x0520: email               # 邮件能力                                 │
│                                                                             │
│   监控运维区 (0x0600 - 0x06FF)                                               │
│   ├── 0x0600: monitor             # 监控能力                                 │
│   ├── 0x0610: health              # 健康检查                                 │
│   └── 0x0620: security            # 安全能力                                 │
│                                                                             │
│   IoT能力区 (0x0700 - 0x07FF)                                                │
│   ├── 0x0700: iot                 # 物联网能力                               │
│   └── 0x0710: mqtt                # MQTT能力                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、地址结构详解

### 3.1 能力基地址 + 驱动偏移

```
能力地址结构：

┌─────────────────────────────────────────────────────────────────────────────┐
│                                                                             │
│   0x0301 = llm-openai                                                       │
│   ├── 0x03 = llm 能力区                                                     │
│   └── 01 = openai 驱动                                                      │
│                                                                             │
│   0x0402 = payment-wechat                                                   │
│   ├── 0x04 = payment 能力区                                                 │
│   └── 02 = wechat 驱动                                                      │
│                                                                             │
│   0x0203 = vfs-minio                                                        │
│   ├── 0x02 = vfs 能力区                                                     │
│   └── 03 = minio 驱动                                                       │
│                                                                             │
│   0x0101 = org-dingding                                                     │
│   ├── 0x01 = org 能力区                                                     │
│   └── 01 = dingding 驱动                                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 地址分配规则

| 地址范围 | 说明 |
|----------|------|
| `xx00` | 能力基地址（基础服务） |
| `xx01 - xx0E` | 驱动地址（具体实现） |
| `xx0F` | 兜底驱动（本地/模拟） |

---

## 四、能力地址枚举（修正版）

```java
public enum CapabilityAddress {
    
    // ═══════════════════════════════════════════════════════════════════════
    // 系统保留区 (0x00xx)
    // ═══════════════════════════════════════════════════════════════════════
    
    SYSTEM_CORE(0x0000, "system.core", "系统核心"),
    INSTALLER(0x0001, "system.installer", "安装器"),
    SCENE_MANAGER(0x0002, "system.scene-manager", "场景管理器"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 组织能力区 (0x01xx)
    // ═══════════════════════════════════════════════════════════════════════
    
    // 组织架构能力
    ORG_BASE(0x0100, "org.base", "组织基础服务"),
    ORG_DINGDING(0x0101, "org.dingding", "钉钉驱动"),
    ORG_FEISHU(0x0102, "org.feishu", "飞书驱动"),
    ORG_WECOM(0x0103, "org.wecom", "企业微信驱动"),
    ORG_LDAP(0x0104, "org.ldap", "LDAP驱动"),
    ORG_LOCAL(0x010F, "org.local", "本地组织"),
    
    // 认证能力
    AUTH_BASE(0x0110, "auth.base", "认证基础服务"),
    AUTH_DINGDING(0x0111, "auth.dingding", "钉钉认证"),
    AUTH_FEISHU(0x0112, "auth.feishu", "飞书认证"),
    AUTH_LOCAL(0x011F, "auth.local", "本地认证"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 存储能力区 (0x02xx)
    // ═══════════════════════════════════════════════════════════════════════
    
    // 文件存储能力
    VFS_BASE(0x0200, "vfs.base", "VFS基础服务"),
    VFS_LOCAL(0x0201, "vfs.local", "本地存储"),
    VFS_DATABASE(0x0202, "vfs.database", "数据库存储"),
    VFS_MINIO(0x0203, "vfs.minio", "MinIO存储"),
    VFS_OSS(0x0204, "vfs.oss", "阿里云OSS"),
    VFS_S3(0x0205, "vfs.s3", "AWS S3"),
    
    // 数据库能力
    DB_BASE(0x0210, "db.base", "数据库基础服务"),
    DB_SQLITE(0x0211, "db.sqlite", "SQLite"),
    DB_MYSQL(0x0212, "db.mysql", "MySQL"),
    DB_POSTGRESQL(0x0213, "db.postgresql", "PostgreSQL"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // AI能力区 (0x03xx)
    // ═══════════════════════════════════════════════════════════════════════
    
    // LLM能力
    LLM_BASE(0x0300, "llm.base", "LLM基础服务"),
    LLM_OPENAI(0x0301, "llm.openai", "OpenAI"),
    LLM_QIANWEN(0x0302, "llm.qianwen", "通义千问"),
    LLM_DEEPSEEK(0x0303, "llm.deepseek", "DeepSeek"),
    LLM_VOLCENGINE(0x0304, "llm.volcengine", "火山引擎"),
    LLM_OLLAMA(0x030F, "llm.ollama", "Ollama本地"),
    
    // 知识库能力
    KNOWLEDGE_BASE(0x0310, "knowledge.base", "知识库基础服务"),
    RAG(0x0320, "rag", "RAG检索增强"),
    VECTOR_STORE(0x0330, "vector-store", "向量存储"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 支付能力区 (0x04xx)
    // ═══════════════════════════════════════════════════════════════════════
    
    // 支付能力
    PAYMENT_BASE(0x0400, "payment.base", "支付基础服务"),
    PAYMENT_ALIPAY(0x0401, "payment.alipay", "支付宝"),
    PAYMENT_WECHAT(0x0402, "payment.wechat", "微信支付"),
    PAYMENT_UNIONPAY(0x0403, "payment.unionpay", "银联"),
    PAYMENT_MOCK(0x040F, "payment.mock", "模拟支付"),
    
    // 媒体发布能力
    MEDIA_BASE(0x0410, "media.base", "媒体基础服务"),
    MEDIA_WECHAT(0x0411, "media.wechat", "微信公众号"),
    MEDIA_WEIBO(0x0412, "media.weibo", "微博"),
    MEDIA_ZHIHU(0x0413, "media.zhihu", "知乎"),
    MEDIA_MOCK(0x041F, "media.mock", "模拟发布"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 通讯能力区 (0x05xx)
    // ═══════════════════════════════════════════════════════════════════════
    
    COMMUNICATION(0x0500, "communication", "通讯能力"),
    NOTIFICATION(0x0510, "notification", "通知能力"),
    EMAIL(0x0520, "email", "邮件能力"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // 监控运维区 (0x06xx)
    // ═══════════════════════════════════════════════════════════════════════
    
    MONITOR(0x0600, "monitor", "监控能力"),
    HEALTH(0x0610, "health", "健康检查"),
    SECURITY(0x0620, "security", "安全能力"),
    
    // ═══════════════════════════════════════════════════════════════════════
    // IoT能力区 (0x07xx)
    // ═══════════════════════════════════════════════════════════════════════
    
    IOT(0x0700, "iot", "物联网能力"),
    MQTT(0x0710, "mqtt", "MQTT能力"),
    ;
}
```

---

## 五、总结

### 地址分配规则

| 规则 | 说明 |
|------|------|
| `xx00` | 能力基地址（基础服务） |
| `xx01 - xx0E` | 驱动地址（具体实现） |
| `xx0F` | 兜底驱动（本地/模拟） |

### 能力区划分

| 区域 | 地址范围 | 包含能力 |
|------|----------|----------|
| 系统保留 | 0x00xx | system, installer, scene-manager |
| 组织能力 | 0x01xx | org, auth, permission |
| 存储能力 | 0x02xx | vfs, database, cache |
| AI能力 | 0x03xx | llm, knowledge, rag, vector |
| 支付能力 | 0x04xx | payment, media |
| 通讯能力 | 0x05xx | communication, notification, email |
| 监控运维 | 0x06xx | monitor, health, security |
| IoT能力 | 0x07xx | iot, mqtt |

---

**文档版本**: 2.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
