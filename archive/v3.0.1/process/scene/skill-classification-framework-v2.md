# 技能分类框架 - 修订版

## 一、核心问题澄清

### 1.1 CONNECTOR 移除

**结论**: CONNECTOR 不是必须的

**替代方案**:
- 场景协作：通过场景间安全协作关系实现
- 数据共享：通过独立的数据共享技能（PROVIDER）实现

### 1.2 新增问题

| 问题 | 说明 |
|------|------|
| **驱动类技能** | dingding、feishu、wecom 互斥、无界面，以前叫"驱动" |
| **方案切换** | 数据库 微/小/大方案切换 (local/minio/oss/s3) |

---

## 二、修订后的分类框架

### 2.1 能力类型 (type)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力类型 (type)                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   SCENE (场景技能)                                                            │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  面向最终用户，需要激活                                                │   │
│   │  特点：有业务语义、有参与者、有可见性                                   │   │
│   │  例：招聘助手、会议纪要、文档问答                                       │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   PROVIDER (提供者)                                                          │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  独立运行，可被场景调用                                                │   │
│   │  特点：提供基础能力，可复用                                            │   │
│   │  例：邮件服务、存储服务、LLM服务                                        │   │
│   │                                                                     │   │
│   │  子类型 (subType):                                                   │   │
│   │  ├── DRIVER     - 驱动：互斥、无界面、数据源集成                        │   │
│   │  │                 例：dingding、feishu、wecom、ldap                   │   │
│   │  │                                                                   │   │
│   │  └── SERVICE    - 服务：标准提供者                                     │   │
│   │                    例：email、notify、storage                          │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   INTERNAL (内部能力)                                                         │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  被其他能力调用，不独立运行                                            │   │
│   │  特点：工具类，无独立业务语义                                          │   │
│   │  例：上下文构建器、文档处理器                                          │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 驱动类技能定义

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        DRIVER (驱动)                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   定义：提供外部数据源集成的提供者，具有互斥性和无界面特性                        │
│                                                                             │
│   特性：                                                                      │
│   ├── 互斥性 (exclusive): 同类驱动只能激活一个                                 │
│   │   例：组织驱动只能选 dingding / feishu / wecom / ldap 其中之一             │
│   │                                                                       │
│   ├── 无界面 (headless): 不提供用户界面，纯后台服务                             │
│   │                                                                       │
│   └── 数据源 (dataSource): 提供特定类型的数据访问能力                          │
│       例：组织架构数据、用户认证数据                                           │
│                                                                             │
│   分类：                                                                      │
│   ├── 组织驱动 (org-driver): dingding, feishu, wecom, ldap                   │
│   ├── 存储驱动 (storage-driver): local, minio, oss, s3, database             │
│   ├── 支付驱动 (payment-driver): alipay, wechat, unionpay                    │
│   └── 媒体驱动 (media-driver): wechat-mp, weibo, zhihu, toutiao              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 方案切换问题

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        方案切换 (Alternative)                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   问题：同一功能有多个实现方案，用户需要切换                                     │
│                                                                             │
│   示例：存储服务                                                              │
│   ├── vfs-local     - 本地存储 (微/开发)                                      │
│   ├── vfs-database  - 数据库存储 (小)                                        │
│   ├── vfs-minio     - MinIO存储 (中)                                         │
│   ├── vfs-oss       - 阿里云OSS (大)                                         │
│   └── vfs-s3        - AWS S3 (大)                                           │
│                                                                             │
│   解决方案：                                                                  │
│                                                                             │
│   方案A：驱动组 (driverGroup)                                                 │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  driverGroup: storage                                                │   │
│   │  alternatives:                                                       │   │
│   │    - skillId: vfs-local                                              │   │
│   │      tier: micro        # 规模等级                                   │   │
│   │      description: 本地存储，适合开发测试                               │   │
│   │    - skillId: vfs-database                                           │   │
│   │      tier: small                                                     │   │
│   │      description: 数据库存储，适合小规模                               │   │
│   │    - skillId: vfs-minio                                              │   │
│   │      tier: medium                                                    │   │
│   │      description: MinIO存储，适合中等规模                             │   │
│   │    - skillId: vfs-oss                                                │   │
│   │      tier: large                                                     │   │
│   │      description: 阿里云OSS，适合大规模                               │   │
│   │    - skillId: vfs-s3                                                 │   │
│   │      tier: large                                                     │   │
│   │      description: AWS S3，适合大规模                                  │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   方案B：能力接口 + 多实现                                                     │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  capability: vfs-storage        # 能力接口                            │   │
│   │  implementations:               # 多实现                              │   │
│   │    - vfs-local (micro)                                               │   │
│   │    - vfs-database (small)                                            │   │
│   │    - vfs-minio (medium)                                              │   │
│   │    - vfs-oss (large)                                                 │   │
│   │    - vfs-s3 (large)                                                  │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、技能定义示例

### 3.1 驱动类技能

```yaml
# 组织驱动 - 钉钉
- skillId: skill-org-dingding
  name: 钉钉组织驱动
  type: PROVIDER
  subType: DRIVER
  category: ORG
  domain: hr
  driverGroup: org        # 驱动组：组织驱动
  exclusive: true         # 互斥：同类驱动只能选一个
  headless: true          # 无界面：不提供用户界面
  description: 钉钉组织数据集成驱动，提供组织架构和用户认证能力

# 组织驱动 - 飞书
- skillId: skill-org-feishu
  name: 飞书组织驱动
  type: PROVIDER
  subType: DRIVER
  category: ORG
  domain: hr
  driverGroup: org
  exclusive: true
  headless: true
  description: 飞书组织数据集成驱动，提供组织架构和用户认证能力

# 组织驱动 - 企业微信
- skillId: skill-org-wecom
  name: 企业微信组织驱动
  type: PROVIDER
  subType: DRIVER
  category: ORG
  domain: hr
  driverGroup: org
  exclusive: true
  headless: true
  description: 企业微信组织数据集成驱动，提供组织架构和用户认证能力
```

### 3.2 存储驱动（方案切换）

```yaml
# 存储驱动组定义
driverGroups:
  - id: storage
    name: 存储服务
    description: 文件存储服务，根据规模选择合适的方案
    exclusive: true
    alternatives:
      - skillId: skill-vfs-local
        tier: micro
        name: 本地存储
        description: 本地文件系统存储，适合开发测试
        
      - skillId: skill-vfs-database
        tier: small
        name: 数据库存储
        description: 数据库存储，适合小规模生产
        
      - skillId: skill-vfs-minio
        tier: medium
        name: MinIO存储
        description: MinIO对象存储，适合中等规模
        
      - skillId: skill-vfs-oss
        tier: large
        name: 阿里云OSS
        description: 阿里云对象存储，适合大规模生产
        
      - skillId: skill-vfs-s3
        tier: large
        name: AWS S3
        description: AWS对象存储，适合大规模生产

# 存储驱动 - 本地
- skillId: skill-vfs-local
  name: 本地存储驱动
  type: PROVIDER
  subType: DRIVER
  category: DATA
  domain: storage
  driverGroup: storage
  tier: micro
  exclusive: true
  headless: true
  description: 本地文件系统存储驱动

# 存储驱动 - MinIO
- skillId: skill-vfs-minio
  name: MinIO存储驱动
  type: PROVIDER
  subType: DRIVER
  category: DATA
  domain: storage
  driverGroup: storage
  tier: medium
  exclusive: true
  headless: true
  description: MinIO对象存储驱动
```

---

## 四、驱动组 (driverGroup) 设计

### 4.1 驱动组定义

| 驱动组 | 说明 | 可选方案 |
|--------|------|----------|
| `org` | 组织驱动 | dingding, feishu, wecom, ldap |
| `storage` | 存储驱动 | local, database, minio, oss, s3 |
| `payment` | 支付驱动 | alipay, wechat, unionpay |
| `media` | 媒体驱动 | wechat-mp, weibo, zhihu, toutiao, xiaohongshu |
| `llm` | LLM驱动 | openai, qianwen, deepseek, volcengine |

### 4.2 规模等级 (tier)

| tier | 说明 | 适用场景 |
|------|------|----------|
| `micro` | 微型 | 开发测试、单机部署 |
| `small` | 小型 | 小团队、低并发 |
| `medium` | 中型 | 中等规模、中等并发 |
| `large` | 大型 | 大规模、高并发、生产环境 |

---

## 五、修订后的分类框架

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        技能分类框架 (修订版)                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   维度一：能力类型 (type)                                                      │
│   ├── SCENE      - 场景技能：面向最终用户，需要激活                             │
│   ├── PROVIDER   - 提供者：独立运行，可被场景调用                               │
│   │   ├── subType: DRIVER   - 驱动：互斥、无界面、数据源集成                    │
│   │   └── subType: SERVICE  - 服务：标准提供者                                 │
│   └── INTERNAL   - 内部能力：被其他能力调用，不独立运行                          │
│                                                                             │
│   维度二：功能分类 (category)                                                  │
│   ├── KNOWLEDGE     - 知识管理    │  SECURITY      - 安全服务                 │
│   ├── LLM           - AI模型      │  MONITOR       - 监控运维                 │
│   ├── DATA          - 数据存储    │  TOOL          - 工具服务                 │
│   ├── COMMUNICATION - 通讯服务    │  UI            - 界面服务                 │
│   ├── WORKFLOW      - 业务流程    │  ORG           - 组织管理                 │
│   ├── IOT           - 物联网      │  COLLABORATION - 协作服务                 │
│                                                                             │
│   维度三：业务领域 (domain)                                                    │
│   ├── hr / crm / finance / project / oa / core                              │
│   ├── ai / storage / messaging / payment / media / iot / nexus              │
│                                                                             │
│   驱动特有属性：                                                              │
│   ├── driverGroup  - 驱动组：同类驱动的分组                                    │
│   ├── exclusive    - 互斥：同类驱动只能选一个                                  │
│   ├── headless     - 无界面：不提供用户界面                                    │
│   └── tier         - 规模等级：micro/small/medium/large                       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 六、与 Engine v3.0 兼容性

| Engine v3.0 | 本方案 | 兼容性 |
|-------------|--------|:------:|
| SkillForm: SCENE | type: SCENE | ✅ |
| SkillForm: STANDALONE | type: PROVIDER | ✅ |
| - | subType: DRIVER | ✅ 新增 |
| - | driverGroup | ✅ 新增 |
| - | exclusive | ✅ 新增 |
| - | headless | ✅ 新增 |
| - | tier | ✅ 新增 |

---

## 七、待确认

1. **驱动组 (driverGroup) 是否需要预定义？**
   - 固定列表 vs 动态发现

2. **规模等级 (tier) 是否足够？**
   - micro/small/medium/large 是否覆盖所有场景？

3. **驱动切换机制**
   - 如何实现平滑切换？
   - 数据迁移如何处理？

---

**文档版本**: 2.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
