# 能力默认降级配置

## 一、默认降级机制

```
能力绑定优先级：
1. 用户显式配置 → 使用用户指定的提供者
2. 场景默认配置 → 使用场景推荐的提供者
3. 能力默认配置 → 使用能力注册表的默认提供者
4. 系统内置兜底 → 使用系统内置的最小实现
```

---

## 二、能力注册表配置

```yaml
# capability-registry.yaml

capabilityRegistry:
  
  # ═══════════════════════════════════════════════════════════════════════
  # 系统能力（内置，不可覆盖）
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0000
    code: system.core
    name: 系统核心
    builtIn: true
    providers: []
    
  - address: 0x0001
    code: system.installer
    name: 安装器
    builtIn: true
    providers: []
    
  - address: 0x0002
    code: system.scene-manager
    name: 场景管理器
    builtIn: true
    providers: []
  
  # ═══════════════════════════════════════════════════════════════════════
  # 文件存储能力 (0x0100)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0100
    code: vfs
    name: 文件存储
    description: 提供文件存储和访问能力
    
    # 提供者列表（按优先级排序）
    providers:
      # 生产级（推荐）
      - skillId: skill-vfs-oss
        tier: large
        priority: 100
        name: 阿里云OSS
        description: 阿里云对象存储，适合大规模生产
        
      - skillId: skill-vfs-s3
        tier: large
        priority: 100
        name: AWS S3
        description: AWS对象存储，适合大规模生产
        
      - skillId: skill-vfs-minio
        tier: medium
        priority: 90
        name: MinIO存储
        description: MinIO对象存储，适合中等规模
        
      # 开发级
      - skillId: skill-vfs-database
        tier: small
        priority: 50
        name: 数据库存储
        description: 数据库存储，适合小规模
        
      # 兜底级（内置）
      - skillId: skill-vfs-local
        tier: micro
        priority: 10
        name: 本地存储
        description: 本地文件系统存储，适合开发测试
        builtIn: true
        
    # 默认配置
    defaults:
      # 按环境自动选择
      byEnvironment:
        production: skill-vfs-oss      # 生产环境 → OSS
        staging: skill-vfs-minio       # 预发环境 → MinIO
        development: skill-vfs-local   # 开发环境 → 本地
        
      # 按规模自动选择
      byTier:
        large: skill-vfs-oss           # 大规模 → OSS
        medium: skill-vfs-minio        # 中规模 → MinIO
        small: skill-vfs-database      # 小规模 → 数据库
        micro: skill-vfs-local         # 微型 → 本地
        
      # 全局默认（兜底）
      fallback: skill-vfs-local
  
  # ═══════════════════════════════════════════════════════════════════════
  # 数据库能力 (0x0101)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0101
    code: database
    name: 数据库
    description: 提供数据库存储和查询能力
    
    providers:
      - skillId: skill-db-postgresql
        tier: large
        priority: 100
        name: PostgreSQL
        
      - skillId: skill-db-mysql
        tier: medium
        priority: 90
        name: MySQL
        
      - skillId: skill-db-mariadb
        tier: medium
        priority: 85
        name: MariaDB
        
      - skillId: skill-db-sqlite
        tier: micro
        priority: 10
        name: SQLite
        builtIn: true
        
    defaults:
      byEnvironment:
        production: skill-db-postgresql
        staging: skill-db-mysql
        development: skill-db-sqlite
      byTier:
        large: skill-db-postgresql
        medium: skill-db-mysql
        small: skill-db-mysql
        micro: skill-db-sqlite
      fallback: skill-db-sqlite
  
  # ═══════════════════════════════════════════════════════════════════════
  # 大语言模型能力 (0x0110)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0110
    code: llm
    name: 大语言模型
    description: 提供大语言模型对话能力
    
    providers:
      - skillId: skill-llm-openai
        tier: large
        priority: 100
        name: OpenAI
        
      - skillId: skill-llm-qianwen
        tier: large
        priority: 95
        name: 通义千问
        
      - skillId: skill-llm-volcengine
        tier: large
        priority: 90
        name: 火山引擎豆包
        
      - skillId: skill-llm-deepseek
        tier: medium
        priority: 80
        name: DeepSeek
        
      - skillId: skill-llm-ollama
        tier: micro
        priority: 10
        name: Ollama本地模型
        builtIn: true
        
    defaults:
      byEnvironment:
        production: skill-llm-openai
        staging: skill-llm-qianwen
        development: skill-llm-ollama
      byTier:
        large: skill-llm-openai
        medium: skill-llm-deepseek
        small: skill-llm-deepseek
        micro: skill-llm-ollama
      fallback: skill-llm-ollama
  
  # ═══════════════════════════════════════════════════════════════════════
  # 组织架构能力 (0x0120)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0120
    code: org
    name: 组织架构
    description: 提供组织架构和用户数据能力
    
    providers:
      - skillId: skill-org-dingding
        tier: small
        priority: 100
        name: 钉钉
        
      - skillId: skill-org-feishu
        tier: small
        priority: 100
        name: 飞书
        
      - skillId: skill-org-wecom
        tier: small
        priority: 100
        name: 企业微信
        
      - skillId: skill-org-ldap
        tier: medium
        priority: 80
        name: LDAP
        
      - skillId: skill-org-local
        tier: micro
        priority: 10
        name: 本地组织
        builtIn: true
        
    defaults:
      # 组织驱动需要用户显式选择，默认使用本地
      byEnvironment:
        production: skill-org-dingding
        staging: skill-org-dingding
        development: skill-org-local
      byTier:
        large: skill-org-ldap
        medium: skill-org-ldap
        small: skill-org-dingding
        micro: skill-org-local
      fallback: skill-org-local
  
  # ═══════════════════════════════════════════════════════════════════════
  # 通知能力 (0x0104)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0104
    code: notification
    name: 通知
    description: 提供消息通知能力
    
    providers:
      - skillId: skill-notify
        tier: medium
        priority: 100
        name: 多渠道通知
        
      - skillId: skill-email
        tier: small
        priority: 90
        name: 邮件通知
        
      - skillId: skill-sms
        tier: medium
        priority: 80
        name: 短信通知
        
      - skillId: skill-notification-console
        tier: micro
        priority: 10
        name: 控制台通知
        builtIn: true
        
    defaults:
      byEnvironment:
        production: skill-notify
        staging: skill-email
        development: skill-notification-console
      byTier:
        large: skill-notify
        medium: skill-notify
        small: skill-email
        micro: skill-notification-console
      fallback: skill-notification-console
  
  # ═══════════════════════════════════════════════════════════════════════
  # 支付能力 (0x0200)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0200
    code: payment
    name: 支付
    description: 提供支付和退款能力
    
    providers:
      - skillId: skill-payment-alipay
        tier: large
        priority: 100
        name: 支付宝
        
      - skillId: skill-payment-wechat
        tier: large
        priority: 100
        name: 微信支付
        
      - skillId: skill-payment-unionpay
        tier: large
        priority: 90
        name: 银联
        
      - skillId: skill-payment-mock
        tier: micro
        priority: 10
        name: 模拟支付
        builtIn: true
        
    defaults:
      byEnvironment:
        production: skill-payment-alipay
        staging: skill-payment-mock
        development: skill-payment-mock
      byTier:
        large: skill-payment-alipay
        medium: skill-payment-alipay
        small: skill-payment-mock
        micro: skill-payment-mock
      fallback: skill-payment-mock
  
  # ═══════════════════════════════════════════════════════════════════════
  # 媒体发布能力 (0x0201)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0201
    code: media
    name: 媒体发布
    description: 提供内容发布能力
    
    providers:
      - skillId: skill-media-wechat
        tier: medium
        priority: 100
        name: 微信公众号
        
      - skillId: skill-media-weibo
        tier: medium
        priority: 90
        name: 微博
        
      - skillId: skill-media-zhihu
        tier: medium
        priority: 80
        name: 知乎
        
      - skillId: skill-media-toutiao
        tier: medium
        priority: 80
        name: 头条
        
      - skillId: skill-media-xiaohongshu
        tier: medium
        priority: 70
        name: 小红书
        
      - skillId: skill-media-mock
        tier: micro
        priority: 10
        name: 模拟发布
        builtIn: true
        
    defaults:
      byEnvironment:
        production: skill-media-wechat
        staging: skill-media-mock
        development: skill-media-mock
      byTier:
        large: skill-media-wechat
        medium: skill-media-wechat
        small: skill-media-mock
        micro: skill-media-mock
      fallback: skill-media-mock
  
  # ═══════════════════════════════════════════════════════════════════════
  # 知识库能力 (0x0113)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0113
    code: knowledge
    name: 知识库
    description: 提供知识存储和检索能力
    
    providers:
      - skillId: skill-knowledge-base
        tier: medium
        priority: 100
        name: 知识库服务
        
      - skillId: skill-rag
        tier: medium
        priority: 90
        name: RAG服务
        
      - skillId: skill-vector-sqlite
        tier: small
        priority: 50
        name: SQLite向量存储
        
      - skillId: skill-knowledge-local
        tier: micro
        priority: 10
        name: 本地知识库
        builtIn: true
        
    defaults:
      byEnvironment:
        production: skill-knowledge-base
        staging: skill-rag
        development: skill-knowledge-local
      byTier:
        large: skill-knowledge-base
        medium: skill-rag
        small: skill-vector-sqlite
        micro: skill-knowledge-local
      fallback: skill-knowledge-local
  
  # ═══════════════════════════════════════════════════════════════════════
  # UI能力 (0x0130)
  # ═══════════════════════════════════════════════════════════════════════
  
  - address: 0x0130
    code: ui
    name: UI生成
    description: 提供UI生成能力
    
    providers:
      - skillId: skill-a2ui
        tier: medium
        priority: 100
        name: A2UI图转代码
        
      - skillId: skill-ui-dashboard
        tier: small
        priority: 50
        name: 仪表盘UI
        
      - skillId: skill-ui-console
        tier: micro
        priority: 10
        name: 控制台UI
        builtIn: true
        
    defaults:
      byEnvironment:
        production: skill-a2ui
        staging: skill-a2ui
        development: skill-ui-console
      byTier:
        large: skill-a2ui
        medium: skill-a2ui
        small: skill-ui-dashboard
        micro: skill-ui-console
      fallback: skill-ui-console
```

---

## 三、降级逻辑

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力绑定降级逻辑                                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   1. 检查用户显式配置                                                         │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  capabilityBindings:                                             │    │
│      │    0x0100: skill-vfs-minio    # 用户指定 → 直接使用              │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓ 未配置                                        │
│   2. 检查场景默认配置                                                         │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  sceneDefaults:                                                  │    │
│      │    0x0100: skill-vfs-database   # 场景推荐 → 使用场景默认        │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓ 未配置                                        │
│   3. 检查环境配置                                                             │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  environment: production                                         │    │
│      │  defaults.byEnvironment.production: skill-vfs-oss               │    │
│      │  # 生产环境 → OSS                                                │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓ 未配置                                        │
│   4. 检查规模配置                                                             │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  tier: medium                                                    │    │
│      │  defaults.byTier.medium: skill-vfs-minio                        │    │
│      │  # 中规模 → MinIO                                                │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓ 未配置                                        │
│   5. 使用兜底配置                                                             │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  defaults.fallback: skill-vfs-local                             │    │
│      │  # 兜底 → 本地存储                                               │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、配置示例

### 4.1 场景配置

```yaml
- skillId: skill-recruitment-assistant
  name: 招聘助手
  type: SCENE
  domain: hr
  
  # 声明需要的能力
  requiredCapabilities:
    - address: 0x0100        # VFS
      tier: medium           # 规模要求
    - address: 0x0120        # ORG
    - address: 0x0104        # NOTIFICATION
      
  # 场景默认配置（可选）
  sceneDefaults:
    0x0100: skill-vfs-database   # 场景推荐使用数据库存储
    0x0120: skill-org-dingding   # 场景推荐使用钉钉
```

### 4.2 用户配置

```yaml
# 用户激活场景时的配置
sceneActivation:
  sceneId: recruitment-assistant
  
  # 用户显式配置（最高优先级）
  capabilityBindings:
    0x0100: skill-vfs-minio      # 用户指定使用 MinIO
    # 0x0120 未配置，使用降级逻辑
    # 0x0104 未配置，使用降级逻辑
```

### 4.3 系统配置

```yaml
# 系统级配置
systemConfig:
  # 当前环境
  environment: production
  
  # 系统规模
  tier: large
  
  # 全局默认覆盖
  globalDefaults:
    0x0110: skill-llm-qianwen    # 全局使用通义千问
```

---

## 五、降级代码实现

```java
/**
 * 能力绑定解析器
 */
public class CapabilityBindingResolver {
    
    /**
     * 解析能力绑定
     */
    public Skill resolveCapabilityBinding(
        int capabilityAddress,
        SceneConfig sceneConfig,
        UserConfig userConfig,
        SystemConfig systemConfig
    ) {
        // 1. 用户显式配置（最高优先级）
        if (userConfig.hasBinding(capabilityAddress)) {
            return userConfig.getBinding(capabilityAddress);
        }
        
        // 2. 场景默认配置
        if (sceneConfig.hasDefault(capabilityAddress)) {
            return sceneConfig.getDefault(capabilityAddress);
        }
        
        // 3. 获取能力注册表
        CapabilityRegistry registry = CapabilityRegistry.getInstance();
        CapabilityDefinition cap = registry.getCapability(capabilityAddress);
        
        // 4. 按环境配置
        String environment = systemConfig.getEnvironment();
        if (cap.getDefaults().hasByEnvironment(environment)) {
            return cap.getDefaults().getByEnvironment(environment);
        }
        
        // 5. 按规模配置
        String tier = systemConfig.getTier();
        if (cap.getDefaults().hasByTier(tier)) {
            return cap.getDefaults().getByTier(tier);
        }
        
        // 6. 兜底配置
        return cap.getDefaults().getFallback();
    }
}
```

---

## 六、总结

### 降级优先级

```
1. 用户显式配置 > 2. 场景默认配置 > 3. 环境配置 > 4. 规模配置 > 5. 兜底配置
```

### 每个能力的默认配置

| 能力 | 生产环境 | 开发环境 | 兜底 |
|------|----------|----------|------|
| vfs | OSS | 本地 | 本地 |
| database | PostgreSQL | SQLite | SQLite |
| llm | OpenAI | Ollama | Ollama |
| org | 钉钉 | 本地 | 本地 |
| notification | 多渠道 | 控制台 | 控制台 |
| payment | 支付宝 | 模拟 | 模拟 |
| media | 微信公众号 | 模拟 | 模拟 |
| knowledge | 知识库服务 | 本地 | 本地 |
| ui | A2UI | 控制台 | 控制台 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
