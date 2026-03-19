# Skill-Scene 文档中心 v2.3.1

> **当前版本**: 2.3.1  
> **更新日期**: 2026-03-16  
> **维护团队**: Skills Team

---

## 一、文档结构

```
docs/
├── README.md                    # 本文件 - 文档中心入口
├── archived/                    # 归档文档
│   └── v2.3.1/                  # v2.3.1 归档批次
│       ├── INDEX.md             # 归档索引
│       ├── capability/          # 外部能力相关文档
│       ├── scene/               # 场景技能相关文档
│       ├── collaboration/       # 协作规范文档
│       ├── execution/           # 执行计划文档
│       └── analysis/            # 分析报告文档
```

---

## 二、核心概念

### 2.1 技能分类体系

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        技能分类框架 v2.3.1                                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│   维度一：SkillForm (技能形态)                                                    │
│   ├── SCENE      - 场景技能：面向最终用户，需要激活                                │
│   ├── PROVIDER   - 能力提供者：独立运行，可被场景调用                              │
│   │   └── subType: DRIVER - 驱动：互斥、无界面、数据源集成                         │
│   └── INTERNAL   - 内部能力：被其他能力调用，不独立运行                            │
│                                                                                 │
│   维度二：SceneType (场景类型) - 仅 SkillForm=SCENE 时有效                         │
│   ├── AUTO       - 自驱场景 (hasSelfDrive=true)                                  │
│   └── TRIGGER    - 触发场景 (hasSelfDrive=false)                                 │
│                                                                                 │
│   维度三：Visibility (可见性)                                                     │
│   ├── public     - 普通用户可见                                                  │
│   ├── developer  - 开发者可见                                                    │
│   └── internal   - 系统内部                                                      │
│                                                                                 │
│   维度四：CapabilityCategory (能力地址分类) - 17个标准分类                         │
│   ├── sys/org/auth/vfs/db/llm/know/payment/media/comm                           │
│   └── mon/iot/search/sched/sec/net/util                                         │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 旧分类映射

| 旧分类 | 新分类 | 说明 |
|--------|--------|------|
| **ABS** (自驱业务场景) | `SCENE` + `AUTO` + `public` | 自驱场景，用户可见 |
| **ASS** (自驱系统场景) | `SCENE` + `AUTO` + `internal` | 自驱场景，后台运行 |
| **TBS** (触发业务场景) | `SCENE` + `TRIGGER` + `public` | 触发场景，用户参与 |
| **STANDALONE** | `PROVIDER` 或 `DRIVER` | 独立能力 |

### 2.3 能力地址空间

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                     能力地址空间 (总计 256 地址)                                   │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│   区域           │  地址范围        │  数量    │  说明                           │
│   ───────────────┼──────────────────┼──────────┼────────────────────────────────│
│   系统区 (SYS)   │  0x00 - 0x07     │  8       │  系统核心服务                   │
│   组织区 (ORG)   │  0x08 - 0x0F     │  8       │  组织驱动                       │
│   认证区 (AUTH)  │  0x10 - 0x17     │  8       │  认证驱动                       │
│   存储区 (VFS)   │  0x18 - 0x1F     │  8       │  存储驱动                       │
│   数据库区 (DB)  │  0x20 - 0x27     │  8       │  数据库驱动                     │
│   AI区 (LLM)     │  0x28 - 0x2F     │  8       │  LLM驱动                        │
│   知识区 (KNOW)  │  0x30 - 0x37     │  8       │  知识库驱动                     │
│   支付区 (PAY)   │  0x38 - 0x3F     │  8       │  支付驱动                       │
│   媒体区 (MEDIA) │  0x40 - 0x47     │  8       │  媒体发布驱动                   │
│   通讯区 (COMM)  │  0x48 - 0x4F     │  8       │  通讯驱动                       │
│   监控区 (MON)   │  0x50 - 0x57     │  8       │  监控驱动                       │
│   IoT区 (IOT)    │  0x58 - 0x5F     │  8       │  IoT驱动                        │
│   搜索区 (SEARCH)│  0x60 - 0x67     │  8       │  搜索驱动                       │
│   调度区 (SCHED) │  0x68 - 0x6F     │  8       │  任务调度驱动                   │
│   安全区 (SEC)   │  0x70 - 0x77     │  8       │  安全驱动                       │
│   网络区 (NET)   │  0x78 - 0x7F     │  8       │  网络驱动                       │
│   ───────────────┼──────────────────┼──────────┼────────────────────────────────│
│   已分配         │  0x00 - 0x7F     │  128     │  16个分类 × 8地址               │
│   扩展区         │  0x80 - 0xFF     │  128     │  用户自定义扩展                 │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.4 Driver (驱动) 特性

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        DRIVER (驱动) 定义                                         │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│   特性：                                                                         │
│   ├── exclusive  - 互斥：同类驱动只能激活一个                                     │
│   ├── headless   - 无界面：不提供用户界面，纯后台服务                              │
│   └── dataSource - 数据源：提供特定类型的数据访问能力                              │
│                                                                                 │
│   驱动组 (driverGroup)：                                                         │
│   ├── org        - 组织驱动：dingding, feishu, wecom, ldap                       │
│   ├── storage    - 存储驱动：local, database, minio, oss, s3                     │
│   ├── payment    - 支付驱动：alipay, wechat, unionpay                            │
│   ├── media      - 媒体驱动：wechat-mp, weibo, zhihu, toutiao                    │
│   └── llm        - LLM驱动：openai, qianwen, deepseek, volcengine                │
│                                                                                 │
│   规模等级 (tier)：                                                              │
│   ├── micro      - 微型：开发测试、单机部署                                       │
│   ├── small      - 小型：小团队、低并发                                           │
│   ├── medium     - 中型：中等规模、中等并发                                       │
│   └── large      - 大型：大规模、高并发、生产环境                                 │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、归档文档

所有历史文档已归档至 `archived/v2.3.1/` 目录，详见 [归档索引](./archived/v2.3.1/INDEX.md)。

### 归档分类

| 目录 | 文档数 | 说明 |
|------|:------:|------|
| capability/ | 23 | 外部能力相关文档 |
| scene/ | 19 | 场景技能相关文档 |
| collaboration/ | 18 | 协作规范文档 |
| execution/ | 22 | 执行计划文档 |
| analysis/ | 22 | 分析报告文档 |

---

## 四、快速参考

### 4.1 关键字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `skillForm` | enum | SCENE / PROVIDER / INTERNAL |
| `sceneType` | enum | AUTO / TRIGGER (仅SCENE时有效) |
| `visibility` | enum | public / developer / internal |
| `capabilityCategory` | string | 17个标准分类之一 |
| `driverGroup` | string | 驱动组标识 (仅DRIVER时有效) |
| `exclusive` | boolean | 是否互斥 (仅DRIVER时有效) |
| `headless` | boolean | 是否无界面 (仅DRIVER时有效) |
| `tier` | enum | micro / small / medium / large |

### 4.2 状态流转

```
场景技能状态流转：

安装 → DRAFT → SCHEDULED/RUNNING → ACTIVE → DEACTIVATED

AUTO + public:   DRAFT → SCHEDULED → RUNNING → ACTIVE
AUTO + internal: DRAFT → RUNNING → ACTIVE
TRIGGER + public: DRAFT → PENDING → ACTIVE
```

---

## 五、相关链接

- [归档索引 v2.3.1](./archived/v2.3.1/INDEX.md)
- [能力地址空间设计](./archived/v2.3.1/capability/capability-address-space-design-v5.md)
- [能力分类设计](./archived/v2.3.1/capability/capability-classification-design-v3.md)
- [场景技能分类规范](./archived/v2.3.1/scene/scene-skill-classification-spec-v2.md)
- [技能分类框架](./archived/v2.3.1/scene/skill-classification-framework-v2.md)
- [协作规范](./archived/v2.3.1/collaboration/collaboration-specification-v2.md)

---

**文档版本**: 2.3.1  
**最后更新**: 2026-03-16
