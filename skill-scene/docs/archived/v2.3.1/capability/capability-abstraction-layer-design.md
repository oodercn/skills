# 能力抽象层设计

## 一、问题场景

```
故事：
我是HR系统，我需要一个数据库能力。

问题：
1. "数据库能力"怎么描述？
2. 是常量？还是场景？
3. HR技能如何声明需要这个能力？
4. 如何匹配到具体的实现？
```

---

## 二、核心概念

### 2.1 能力抽象层

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力抽象层                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                     能力需求                         │   │
│   │                                                                     │   │
│   │   HR场景声明：我需要 vfs-storage 能力                                │   │
│   │                                                                     │   │
│   │   - capability: vfs-storage                                        │   │
│   │   - tier: medium          # 规模要求                                │   │
│   │   - required: true        # 是否必须                                │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                              ↓ 匹配                                         │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                     能力定义                         │   │
│   │                                                                     │   │
│   │   vfs-storage:                                                      │   │
│   │     name: 文件存储能力                                               │   │
│   │     description: 提供文件存储和访问能力                               │   │
│   │     providers:                      # 能力提供者列表                  │   │
│   │       - skill-vfs-local (micro)                                     │   │
│   │       - skill-vfs-database (small)                                  │   │
│   │       - skill-vfs-minio (medium)                                    │   │
│   │       - skill-vfs-oss (large)                                       │   │
│   │       - skill-vfs-s3 (large)                                        │   │
│   │     default: skill-vfs-local        # 默认提供者                     │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                              ↓ 实现                                         │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │                     能力提供者                      │   │
│   │                                                                     │   │
│   │   skill-vfs-minio:                                                  │   │
│   │     type: PROVIDER                                                  │   │
│   │     subType: DRIVER                                                 │   │
│   │     provides: vfs-storage          # 提供的能力                      │   │
│   │     driverGroup: storage                                            │   │
│   │     tier: medium                                                    │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 三层概念

| 层级 | 名称 | 说明 | 示例 |
|------|------|------|------|
| **需求层** | 能力需求 | 场景声明需要什么能力 | `capability: vfs-storage` |
| **定义层** | 能力定义 | 能力的抽象描述和可用提供者 | `vfs-storage` 定义 |
| **实现层** | 能力提供者 | 具体实现能力的技能 | `skill-vfs-minio` |

---

## 三、能力定义

### 3.1 能力注册表

```yaml
# capabilities.yaml - 能力定义注册表

capabilities:
  # 文件存储能力
  vfs-storage:
    name: 文件存储能力
    description: 提供文件存储和访问能力
    category: DATA
    providers:
      - skillId: skill-vfs-local
        tier: micro
        name: 本地存储
      - skillId: skill-vfs-database
        tier: small
        name: 数据库存储
      - skillId: skill-vfs-minio
        tier: medium
        name: MinIO存储
      - skillId: skill-vfs-oss
        tier: large
        name: 阿里云OSS
      - skillId: skill-vfs-s3
        tier: large
        name: AWS S3
    default: skill-vfs-local
    
  # 组织数据能力
  org-data:
    name: 组织数据能力
    description: 提供组织架构和用户认证数据能力
    category: ORG
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
    default: skill-org-dingding
    
  # LLM能力
  llm-chat:
    name: LLM对话能力
    description: 提供大语言模型对话能力
    category: LLM
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
      - skillId: skill-llm-volcengine
        tier: large
        name: 火山引擎
    default: skill-llm-openai
    
  # 消息通知能力
  notification:
    name: 消息通知能力
    description: 提供消息推送和通知能力
    category: COMMUNICATION
    providers:
      - skillId: skill-email
        tier: small
        name: 邮件
      - skillId: skill-notify
        tier: medium
        name: 多渠道通知
    default: skill-email
    
  # 支付能力
  payment:
    name: 支付能力
    description: 提供支付和退款能力
    category: WORKFLOW
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
    default: skill-payment-alipay
```

### 3.2 能力命名规范

```
能力ID命名规范：{domain}-{function}

示例：
├── vfs-storage      - 文件存储能力
├── vfs-database     - 数据库能力
├── org-data         - 组织数据能力
├── org-auth         - 组织认证能力
├── llm-chat         - LLM对话能力
├── llm-embedding    - LLM嵌入能力
├── notification     - 通知能力
├── payment          - 支付能力
└── media-publish    - 媒体发布能力
```

---

## 四、场景声明能力需求

### 4.1 场景配置示例

```yaml
# HR招聘场景
- skillId: skill-recruitment-assistant
  name: 招聘助手
  type: SCENE
  category: WORKFLOW
  domain: hr
  
  # 声明需要的能力
  requiredCapabilities:
    - capability: vfs-storage       # 需要文件存储能力
      tier: medium                  # 规模要求：中等
      required: true                # 必须满足
      
    - capability: org-data          # 需要组织数据能力
      required: true
      
    - capability: notification      # 需要通知能力
      required: false               # 可选
      
  # 能力绑定（用户可配置）
  capabilityBindings:
    vfs-storage: skill-vfs-minio    # 使用 MinIO 作为存储
    org-data: skill-org-dingding    # 使用钉钉作为组织数据源
    notification: skill-email       # 使用邮件通知
```

### 4.2 能力匹配逻辑

```
场景激活时的能力匹配：

1. 场景声明需要 vfs-storage 能力，tier: medium
2. 查找能力定义，找到可用提供者列表
3. 筛选满足 tier >= medium 的提供者
4. 检查用户是否已配置 capabilityBindings
5. 如果已配置，使用用户指定的提供者
6. 如果未配置，使用能力定义中的 default 提供者
```

---

## 五、能力提供者声明

### 5.1 技能配置示例

```yaml
# MinIO存储驱动
- skillId: skill-vfs-minio
  name: MinIO存储驱动
  type: PROVIDER
  subType: DRIVER
  category: DATA
  domain: storage
  
  # 声明提供的能力
  provides:
    - capability: vfs-storage       # 提供文件存储能力
      tier: medium                  # 规模等级
      
  driverGroup: storage              # 驱动组
  exclusive: true                   # 互斥
  headless: true                    # 无界面
  
  # 能力接口定义
  capabilities:
    - file-read
    - file-write
    - file-delete
    - folder-create
    - folder-list
```

### 5.2 能力接口

```yaml
# 能力接口定义
capability-interfaces:
  vfs-storage:
    operations:
      - id: file-read
        name: 读取文件
        input:
          path: string
        output:
          content: bytes
          
      - id: file-write
        name: 写入文件
        input:
          path: string
          content: bytes
        output:
          success: boolean
          
      - id: file-delete
        name: 删除文件
        input:
          path: string
        output:
          success: boolean
```

---

## 六、能力绑定配置

### 6.1 场景级绑定

```yaml
# 场景激活时配置
sceneActivation:
  sceneId: recruitment-assistant
  
  # 能力绑定配置
  capabilityBindings:
    vfs-storage:
      provider: skill-vfs-minio
      config:
        endpoint: http://minio.example.com
        bucket: recruitment
      
    org-data:
      provider: skill-org-dingding
      config:
        corpId: xxx
        appKey: xxx
        appSecret: xxx
```

### 6.2 系统级默认绑定

```yaml
# 系统默认能力绑定
systemDefaults:
  capabilityBindings:
    vfs-storage: skill-vfs-local     # 默认使用本地存储
    org-data: skill-org-dingding     # 默认使用钉钉
    llm-chat: skill-llm-openai       # 默认使用OpenAI
    notification: skill-email        # 默认使用邮件
```

---

## 七、能力发现流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力发现流程                                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   1. 场景声明能力需求                                                         │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  requiredCapabilities:                                           │    │
│      │    - capability: vfs-storage                                    │    │
│      │      tier: medium                                               │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓                                              │
│   2. 查找能力定义                                                            │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  capabilities.yaml:                                              │    │
│      │    vfs-storage:                                                  │    │
│      │      providers: [vfs-local, vfs-database, vfs-minio, ...]       │    │
│      │      default: vfs-local                                         │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓                                              │
│   3. 筛选满足条件的提供者                                                     │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  满足 tier >= medium:                                            │    │
│      │    - vfs-minio (medium)                                         │    │
│      │    - vfs-oss (large)                                            │    │
│      │    - vfs-s3 (large)                                             │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓                                              │
│   4. 检查用户配置                                                            │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  用户已配置: vfs-minio                                           │    │
│      │  → 使用 skill-vfs-minio                                         │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓                                              │
│   5. 激活能力提供者                                                          │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  激活 skill-vfs-minio                                            │    │
│      │  注入能力接口到场景                                               │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 八、与现有设计的关系

### 8.1 概念映射

| 原有概念 | 新概念 | 说明 |
|----------|--------|------|
| capability | capability (能力需求) | 场景声明需要的能力 |
| capability provider | capability provider (能力提供者) | 提供能力的技能 |
| driver | PROVIDER + subType: DRIVER | 驱动是特殊的提供者 |
| default scene | capabilityBindings.default | 能力的默认提供者 |

### 8.2 兼容性

```yaml
# 原有配置方式（保持兼容）
- skillId: skill-hr
  capabilities:
    - vfs-storage    # 声明需要的能力
  defaultScenes:
    vfs-storage: skill-vfs-database  # 默认场景

# 新配置方式（推荐）
- skillId: skill-hr
  requiredCapabilities:
    - capability: vfs-storage
      tier: small
  capabilityBindings:
    vfs-storage: skill-vfs-database
```

---

## 九、总结

### 核心概念

| 概念 | 说明 | 示例 |
|------|------|------|
| **能力** | 抽象的能力描述，是常量定义 | `vfs-storage` |
| **能力需求** | 场景声明需要的能力 | `requiredCapabilities` |
| **能力提供者** | 实现能力的具体技能 | `skill-vfs-minio` |
| **能力绑定** | 需求与提供者的映射 | `capabilityBindings` |

### 设计原则

1. **能力是常量** - 能力ID是预定义的常量，如 `vfs-storage`
2. **提供者是技能** - 具体实现是 PROVIDER 类型的技能
3. **绑定可配置** - 用户可以配置使用哪个提供者
4. **默认有兜底** - 每个能力有默认的提供者

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
