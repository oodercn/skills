# 能力分类 - 简化设计

## 一、核心观点

```
能力 = 固定分类常量

场景技能声明需要的能力，独立技能提供这些能力。
能力本身就是分类的基础。
```

---

## 二、能力分类（固定常量）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力分类（固定常量）                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   基础能力                                                                    │
│   ├── vfs          - 文件存储能力                                            │
│   ├── database     - 数据库能力                                              │
│   ├── llm          - 大语言模型能力                                          │
│   ├── knowledge    - 知识库能力                                              │
│   ├── org          - 组织架构能力                                            │
│   ├── auth         - 认证能力                                                │
│   ├── message      - 消息能力                                                │
│   ├── notification - 通知能力                                                │
│   ├── payment      - 支付能力                                                │
│   ├── media        - 媒体发布能力                                            │
│   ├── monitor      - 监控能力                                                │
│   └── iot          - 物联网能力                                              │
│                                                                             │
│   扩展能力                                                                    │
│   ├── workflow     - 工作流能力                                              │
│   ├── collaboration- 协作能力                                                │
│   └── ...                                                                   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、简化设计

### 3.1 场景技能声明能力

```yaml
# 场景技能
- skillId: skill-recruitment-assistant
  name: 招聘助手
  type: SCENE
  domain: hr
  
  # 声明需要的能力（固定分类常量）
  capabilities:
    - vfs          # 需要文件存储能力
    - org          # 需要组织架构能力
    - notification # 需要通知能力（可选）
    
  # 能力绑定（用户可配置）
  capabilityBindings:
    vfs: skill-vfs-minio      # 使用 MinIO
    org: skill-org-dingding   # 使用钉钉
```

### 3.2 独立技能提供能力

```yaml
# 独立技能（提供者）
- skillId: skill-vfs-minio
  name: MinIO存储服务
  type: PROVIDER
  
  # 提供的能力（固定分类常量）
  provides: vfs
  
  # 驱动属性
  driverGroup: storage
  tier: medium
  exclusive: true

# 独立技能（提供者）
- skillId: skill-org-dingding
  name: 钉钉组织服务
  type: PROVIDER
  
  # 提供的能力
  provides: org
  
  driverGroup: org
  tier: small
  exclusive: true
```

---

## 四、能力注册表

```yaml
# 能力注册表（固定常量定义）

capabilities:
  # 基础能力
  vfs:
    name: 文件存储能力
    description: 提供文件存储和访问能力
    providers:
      - skill-vfs-local (micro)
      - skill-vfs-database (small)
      - skill-vfs-minio (medium)
      - skill-vfs-oss (large)
      - skill-vfs-s3 (large)
    default: skill-vfs-local
    
  database:
    name: 数据库能力
    description: 提供数据库存储和查询能力
    providers:
      - skill-db-sqlite (micro)
      - skill-db-mysql (small)
      - skill-db-postgresql (medium)
      - skill-db-mongodb (medium)
    default: skill-db-sqlite
    
  llm:
    name: 大语言模型能力
    description: 提供LLM对话和嵌入能力
    providers:
      - skill-llm-openai (large)
      - skill-llm-qianwen (large)
      - skill-llm-deepseek (medium)
      - skill-llm-volcengine (large)
    default: skill-llm-openai
    
  knowledge:
    name: 知识库能力
    description: 提供知识存储和检索能力
    providers:
      - skill-knowledge-base
      - skill-rag
      - skill-vector-sqlite
    default: skill-knowledge-base
    
  org:
    name: 组织架构能力
    description: 提供组织架构和用户数据能力
    providers:
      - skill-org-dingding
      - skill-org-feishu
      - skill-org-wecom
      - skill-org-ldap
    default: skill-org-dingding
    
  auth:
    name: 认证能力
    description: 提供用户认证能力
    providers:
      - skill-user-auth
      - skill-org-dingding
      - skill-org-feishu
      - skill-org-wecom
    default: skill-user-auth
    
  message:
    name: 消息能力
    description: 提供消息发送能力
    providers:
      - skill-msg
      - skill-msg-service
      - skill-im
    default: skill-msg
    
  notification:
    name: 通知能力
    description: 提供消息通知能力
    providers:
      - skill-email
      - skill-notify
    default: skill-email
    
  payment:
    name: 支付能力
    description: 提供支付和退款能力
    providers:
      - skill-payment-alipay
      - skill-payment-wechat
      - skill-payment-unionpay
    default: skill-payment-alipay
    
  media:
    name: 媒体发布能力
    description: 提供内容发布能力
    providers:
      - skill-media-wechat
      - skill-media-weibo
      - skill-media-zhihu
      - skill-media-toutiao
    default: skill-media-wechat
```

---

## 五、技能分类简化

### 5.1 按能力分类

```
技能分类 = 能力分类

每个独立技能提供一种能力，能力本身就是分类。
```

### 5.2 技能定义

```yaml
# 技能定义简化
- skillId: skill-vfs-minio
  name: MinIO存储服务
  type: PROVIDER
  provides: vfs           # 提供的能力（即分类）
  tier: medium
  driverGroup: storage
```

---

## 六、场景激活流程

```
1. 场景声明能力需求
   ┌─────────────────────────────────────────────────────────────────────────┐
   │  capabilities: [vfs, org, notification]                                 │
   └─────────────────────────────────────────────────────────────────────────┘
                                    ↓
2. 查找能力提供者
   ┌─────────────────────────────────────────────────────────────────────────┐
   │  vfs → [vfs-local, vfs-database, vfs-minio, vfs-oss, vfs-s3]           │
   │  org → [org-dingding, org-feishu, org-wecom, org-ldap]                 │
   │  notification → [email, notify]                                         │
   └─────────────────────────────────────────────────────────────────────────┘
                                    ↓
3. 检查用户绑定配置
   ┌─────────────────────────────────────────────────────────────────────────┐
   │  capabilityBindings:                                                    │
   │    vfs: skill-vfs-minio        # 用户指定                               │
   │    org: skill-org-dingding     # 用户指定                               │
   │    notification: null          # 未指定，使用默认                        │
   └─────────────────────────────────────────────────────────────────────────┘
                                    ↓
4. 激活能力提供者
   ┌─────────────────────────────────────────────────────────────────────────┐
   │  激活: skill-vfs-minio, skill-org-dingding, skill-email                │
   └─────────────────────────────────────────────────────────────────────────┘
```

---

## 七、总结

### 核心简化

| 原设计 | 简化设计 |
|--------|----------|
| 能力是抽象概念 | 能力是固定分类常量 |
| 需要能力定义层 | 能力即分类 |
| category + capability | capability 即 category |

### 设计原则

1. **能力是常量** - `vfs`, `database`, `llm` 等是固定常量
2. **能力即分类** - 技能的分类由其提供的能力决定
3. **场景声明能力** - 场景声明需要哪些能力
4. **独立技能提供能力** - PROVIDER 技能提供能力

### 配置示例

```yaml
# 场景
- skillId: skill-hr
  type: SCENE
  capabilities: [vfs, org, notification]
  capabilityBindings:
    vfs: skill-vfs-minio
    org: skill-org-dingding

# 独立技能
- skillId: skill-vfs-minio
  type: PROVIDER
  provides: vfs
  tier: medium
```

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
