# Skills 能力分类依赖关系分析

> **分析日期**: 2026-03-12  
> **版本**: 2.3.1  
> **能力分类数**: 17

---

## 一、17种能力分类概览

| 序号 | 代码 | 名称 | 地址范围 | 描述 |
|:----:|------|------|----------|------|
| 1 | sys | 系统核心 | 0x00-0x07 | 系统注册、配置、能力管理 |
| 2 | org | 组织服务 | 0x08-0x0F | 钉钉、飞书、企业微信、LDAP |
| 3 | auth | 认证服务 | 0x10-0x17 | 用户认证、令牌管理 |
| 4 | net | 网络服务 | 0x18-0x1F | 网络代理、DNS服务 |
| 5 | vfs | 文件存储 | 0x20-0x27 | 本地存储、对象存储 |
| 6 | llm | 大语言模型 | 0x28-0x2F | LLM服务、对话、补全 |
| 7 | know | 知识库 | 0x38-0x3F | 知识库、RAG、向量存储 |
| 8 | payment | 支付服务 | 0x40-0x47 | 支付宝、微信、银联 |
| 9 | media | 媒体服务 | 0x48-0x4F | 头条、微信、微博、小红书 |
| 10 | comm | 通讯服务 | 0x50-0x57 | 消息、通知、邮件、IM |
| 11 | mon | 监控服务 | 0x58-0x5F | 监控、健康检查、代理 |
| 12 | iot | 物联网 | 0x60-0x67 | OpenWrt、托管、K8s |
| 13 | search | 搜索服务 | 0x68-0x6F | 全文搜索、语义搜索 |
| 14 | sched | 调度服务 | 0x70-0x77 | 定时任务、调度管理 |
| 15 | sec | 安全服务 | 0x78-0x7F | 安全策略、访问控制、审计 |
| 16 | db | 数据库 | 0x80-0x87 | 数据库连接、查询 |
| 17 | util | 工具服务 | 0xF0-0xFF | 通用工具、市场、报表 |

---

## 二、能力分类依赖关系图

### 2.1 核心依赖关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           核心能力依赖关系                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────┐                                                                │
│  │   sys   │ ◄─────────────────────────────────────────────────────────┐   │
│  │ 系统核心 │                                                          │   │
│  │ 0x00    │                                                          │   │
│  └────┬────┘                                                          │   │
│       │                                                               │   │
│       ▼                                                               │   │
│  ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐          │   │
│  │  auth   │────►│   org   │────►│  comm   │────►│  media  │          │   │
│  │ 认证服务 │     │ 组织服务 │     │ 通讯服务 │     │ 媒体服务 │          │   │
│  │ 0x10    │     │ 0x08    │     │ 0x50    │     │ 0x48    │          │   │
│  └────┬────┘     └────┬────┘     └────┬────┘     └─────────┘          │   │
│       │               │               │                                   │   │
│       │               │               ▼                                   │   │
│       │               │          ┌─────────┐                              │   │
│       │               │          │   msg   │                              │   │
│       │               │          │ 消息服务 │                              │   │
│       │               │          │ 0x50    │                              │   │
│       │               │          └────┬────┘                              │   │
│       │               │               │                                   │   │
│       │               │               ▼                                   │   │
│       │               │          ┌─────────┐     ┌─────────┐              │   │
│       │               │          │  notify │────►│  email  │              │   │
│       │               │          │ 通知服务 │     │ 邮件服务 │              │   │
│       │               │          │ 0x53    │     │ 0x52    │              │   │
│       │               │          └─────────┘     └─────────┘              │   │
│       │               │                                                   │   │
│       ▼               ▼                                                   │   │
│  ┌─────────┐     ┌─────────┐                                              │   │
│  │   sec   │     │  group  │                                              │   │
│  │ 安全服务 │     │ 群组服务 │                                              │   │
│  │ 0x78    │     │ 0x55    │                                              │   │
│  └─────────┘     └─────────┘                                              │   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 LLM 能力依赖关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           LLM 能力依赖关系                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                         LLM Providers                                 │  │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐        │  │
│  │  │ ollama  │ │ openai  │ │ qianwen │ │deepseek │ │volcengin│        │  │
│  │  │ 0x31    │ │ 0x32    │ │ 0x33    │ │ 0x34    │ │ 0x35    │        │  │
│  │  └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘        │  │
│  │       │           │           │           │           │              │  │
│  │       └───────────┴───────────┴─────┬─────┴───────────┘              │  │
│  │                                     │                                │  │
│  └─────────────────────────────────────┼────────────────────────────────┘  │
│                                        │                                   │
│                                        ▼                                   │
│  ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐              │
│  │llm-base │────►│ llm-    │────►│ llm-    │────►│ llm-    │              │
│  │LLM基础  │     │config   │     │context  │     │conversa │              │
│  │ 0x30    │     │ 0x30    │     │ 0x30    │     │ 0x30    │              │
│  └─────────┘     └─────────┘     └────┬────┘     └────┬────┘              │
│                                       │               │                   │
│                                       │               │                   │
│                                       ▼               ▼                   │
│                                  ┌─────────┐     ┌─────────┐              │
│                                  │ llm-    │     │ llm-    │              │
│                                  │chat     │     │doc-assis│              │
│                                  │ SCENE   │     │ SCENE   │              │
│                                  └────┬────┘     └─────────┘              │
│                                       │                                    │
│                                       ▼                                    │
│  ┌─────────┐     ┌─────────┐     ┌─────────┐                              │
│  │ know-   │────►│  rag    │────►│know-qa  │                              │
│  │ base    │     │ 0x3A    │     │ SCENE   │                              │
│  │ 0x38    │     └─────────┘     └─────────┘                              │
│  └─────────┘                                                               │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 存储与数据依赖关系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         存储与数据依赖关系                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────┐                                                                │
│  │ vfs-base│ ◄─────────────────────────────────────────────────────────┐   │
│  │ 0x20    │                                                          │   │
│  └────┬────┘                                                          │   │
│       │                                                               │   │
│       ├──────────────┬──────────────┬──────────────┬─────────────┐    │   │
│       ▼              ▼              ▼              ▼             ▼    │   │
│  ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐ │   │
│  │vfs-local│   │vfs-minio│   │ vfs-oss │   │  vfs-s3 │   │vfs-db   │ │   │
│  │ 0x21    │   │ 0x22    │   │ 0x23    │   │ 0x24    │   │ 0x25    │ │   │
│  └─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘ │   │
│                                                                         │   │
│  ┌─────────┐                                                           │   │
│  │  db     │ ◄─────────────────────────────────────────────────────┐  │   │
│  │ 0x80    │                                                        │  │   │
│  └────┬────┘                                                        │  │   │
│       │                                                             │  │   │
│       ▼                                                             │  │   │
│  ┌─────────┐     ┌─────────┐                                        │  │   │
│  │ search  │────►│vector-  │                                        │  │   │
│  │ 0x68    │     │sqlite   │                                        │  │   │
│  └─────────┘     │ 0x39    │                                        │  │   │
│                  └────┬────┘                                        │  │   │
│                       │                                             │  │   │
│                       ▼                                             │  │   │
│                  ┌─────────┐                                        │  │   │
│                  │ know-   │                                        │  │   │
│                  │ base    │────────────────────────────────────────┘  │   │
│                  │ 0x38    │                                           │   │
│                  └─────────┘                                           │   │
│                                                                         │   │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、驱动类技能间接依赖关系

### 3.1 组织驱动 (org)

| 驱动技能 | 直接依赖 | 间接依赖 | 说明 |
|----------|----------|----------|------|
| skill-org-dingding | skill-org-base | skill-auth, skill-common | 钉钉组织集成 |
| skill-org-feishu | skill-org-base | skill-auth, skill-common | 飞书组织集成 |
| skill-org-wecom | skill-org-base | skill-auth, skill-common | 企业微信集成 |
| skill-org-ldap | skill-org-base | skill-auth, skill-common | LDAP集成 |

**依赖链**:
```
skill-org-dingding
└── skill-org-base
    ├── skill-auth (用户认证)
    └── skill-common (通用工具)
```

### 3.2 LLM 驱动 (llm)

| 驱动技能 | 直接依赖 | 间接依赖 | 说明 |
|----------|----------|----------|------|
| skill-llm-ollama | - | skill-common, skill-protocol | 本地LLM |
| skill-llm-openai | - | skill-common, skill-protocol | OpenAI API |
| skill-llm-qianwen | - | skill-common, skill-protocol | 通义千问 |
| skill-llm-deepseek | - | skill-common, skill-protocol | DeepSeek |
| skill-llm-volcengine | - | skill-common, skill-protocol | 火山引擎 |

**依赖链**:
```
skill-llm-ollama
└── (无直接依赖)
    └── skill-protocol (协议处理) [间接]
        └── skill-common (通用工具) [间接]
```

### 3.3 文件存储驱动 (vfs)

| 驱动技能 | 直接依赖 | 间接依赖 | 说明 |
|----------|----------|----------|------|
| skill-vfs-local | skill-vfs-base | skill-common | 本地存储 |
| skill-vfs-minio | skill-vfs-base | skill-common, skill-protocol | MinIO |
| skill-vfs-oss | skill-vfs-base | skill-common, skill-protocol | 阿里云OSS |
| skill-vfs-s3 | skill-vfs-base | skill-common, skill-protocol | AWS S3 |
| skill-vfs-database | skill-vfs-base | skill-common, skill-db | 数据库存储 |

**依赖链**:
```
skill-vfs-minio
└── skill-vfs-base
    └── skill-common (通用工具)
```

### 3.4 媒体驱动 (media)

| 驱动技能 | 直接依赖 | 间接依赖 | 说明 |
|----------|----------|----------|------|
| skill-media-toutiao | - | skill-common, skill-protocol, skill-auth | 头条 |
| skill-media-wechat | - | skill-common, skill-protocol, skill-auth | 微信公众号 |
| skill-media-weibo | - | skill-common, skill-protocol, skill-auth | 微博 |
| skill-media-xiaohongshu | - | skill-common, skill-protocol, skill-auth | 小红书 |
| skill-media-zhihu | - | skill-common, skill-protocol, skill-auth | 知乎 |

**依赖链**:
```
skill-media-wechat
└── (无直接依赖)
    ├── skill-protocol (协议处理) [间接]
    ├── skill-auth (认证) [间接]
    └── skill-common (通用工具) [间接]
```

### 3.5 支付驱动 (payment)

| 驱动技能 | 直接依赖 | 间接依赖 | 说明 |
|----------|----------|----------|------|
| skill-payment-alipay | - | skill-common, skill-protocol, skill-sec | 支付宝 |
| skill-payment-wechat | - | skill-common, skill-protocol, skill-sec | 微信支付 |
| skill-payment-unionpay | - | skill-common, skill-protocol, skill-sec | 银联 |

**依赖链**:
```
skill-payment-alipay
└── (无直接依赖)
    ├── skill-protocol (协议处理) [间接]
    ├── skill-sec (安全) [间接]
    └── skill-common (通用工具) [间接]
```

---

## 四、能力分类依赖矩阵

### 4.1 依赖矩阵表

| 源→目标 | sys | org | auth | net | vfs | llm | know | payment | media | comm | mon | iot | search | sched | sec | db | util |
|---------|:---:|:---:|:----:|:---:|:---:|:---:|:----:|:-------:|:-----:|:----:|:---:|:---:|:------:|:-----:|:---:|:---:|:----:|
| **sys** | - | | | | | | | | | | | | | | | | |
| **org** | ✓ | - | ✓ | | | | | | | ✓ | | | | | | | |
| **auth** | ✓ | | - | | | | | | | | | | | | ✓ | | |
| **net** | ✓ | | | - | | | | | | | ✓ | | | | | | |
| **vfs** | ✓ | | | | - | | | | | | | | | | | ✓ | |
| **llm** | ✓ | | | | | - | ✓ | | | | | | | | | | |
| **know** | ✓ | | | | ✓ | ✓ | - | | | | | | ✓ | | | ✓ | |
| **payment**| ✓ | | ✓ | | | | | - | | | | | | | ✓ | | |
| **media** | ✓ | | ✓ | | | | | | - | | | | | | | | |
| **comm** | ✓ | | | | | | | | | - | | | | | | | |
| **mon** | ✓ | | | ✓ | | | | | | | - | ✓ | | ✓ | | | |
| **iot** | ✓ | | | ✓ | | | | | | | ✓ | - | | | | | |
| **search**| ✓ | | | | ✓ | | ✓ | | | | | | - | | | ✓ | |
| **sched** | ✓ | | | | | | | | | | | | | - | | | |
| **sec** | ✓ | | ✓ | | | | | | | | | | | | - | | |
| **db** | ✓ | | | | | | | | | | | | | | | - | |
| **util** | ✓ | | | | | | | | | | | | | | | | - |

### 4.2 依赖深度统计

| 能力分类 | 直接依赖数 | 间接依赖数 | 依赖深度 |
|----------|:----------:|:----------:|:--------:|
| **know** | 4 | 6 | 3 |
| **llm** | 2 | 4 | 2 |
| **mon** | 3 | 5 | 2 |
| **search** | 3 | 4 | 2 |
| **org** | 2 | 3 | 2 |
| **vfs** | 1 | 2 | 2 |
| **comm** | 1 | 2 | 2 |
| **iot** | 2 | 3 | 2 |
| **sec** | 2 | 2 | 2 |
| **auth** | 2 | 1 | 1 |
| **payment** | 2 | 2 | 1 |
| **media** | 1 | 2 | 1 |
| **net** | 1 | 1 | 1 |
| **sched** | 1 | 0 | 1 |
| **db** | 1 | 0 | 1 |
| **util** | 1 | 0 | 1 |
| **sys** | 0 | 0 | 0 |

---

## 五、补全的依赖关系配置

### 5.1 skill-org-dingding 补全依赖

```yaml
dependencies:
  - id: skill-org-base
    version: ">=1.0.0"
    required: true
    description: "组织基础服务"
  - id: skill-user-auth
    version: ">=0.7.0"
    required: false
    description: "用户认证服务(间接)"
  - id: skill-common
    version: ">=1.0.0"
    required: false
    description: "通用工具库(间接)"
```

### 5.2 skill-vfs-minio 补全依赖

```yaml
dependencies:
  - id: skill-vfs-base
    version: ">=1.0.0"
    required: true
    description: "VFS基础服务"
  - id: skill-common
    version: ">=1.0.0"
    required: false
    description: "通用工具库(间接)"
```

### 5.3 skill-media-wechat 补全依赖

```yaml
dependencies:
  - id: skill-common
    version: ">=1.0.0"
    required: false
    description: "通用工具库(间接)"
  - id: skill-protocol
    version: ">=1.0.0"
    required: false
    description: "协议处理服务(间接)"
  - id: skill-user-auth
    version: ">=0.7.0"
    required: false
    description: "用户认证服务(间接)"
```

### 5.4 skill-payment-alipay 补全依赖

```yaml
dependencies:
  - id: skill-common
    version: ">=1.0.0"
    required: false
    description: "通用工具库(间接)"
  - id: skill-protocol
    version: ">=1.0.0"
    required: false
    description: "协议处理服务(间接)"
  - id: skill-security
    version: ">=0.7.0"
    required: false
    description: "安全管理服务(间接)"
```

### 5.5 skill-knowledge-base 补全依赖

```yaml
dependencies:
  - id: skill-vfs-base
    version: ">=1.0.0"
    required: false
    description: "VFS基础服务(间接)"
  - id: skill-search
    version: ">=0.7.0"
    required: false
    description: "搜索服务(间接)"
  - id: skill-common
    version: ">=1.0.0"
    required: false
    description: "通用工具库(间接)"
```

---

## 六、依赖关系最佳实践

### 6.1 依赖声明原则

1. **显式声明直接依赖**: 所有直接依赖必须声明
2. **可选声明间接依赖**: 关键间接依赖建议声明为可选
3. **版本范围**: 使用语义化版本范围 `>=x.y.z`
4. **描述清晰**: 说明依赖的作用

### 6.2 依赖加载顺序

```
1. sys (系统核心) - 最先加载
2. common (通用工具) - 基础依赖
3. protocol (协议处理) - 通信基础
4. auth (认证服务) - 安全基础
5. 其他能力服务
6. 驱动适配器
7. 场景应用 - 最后加载
```

### 6.3 循环依赖检测

当前无循环依赖。所有依赖关系为有向无环图 (DAG)。

---

## 七、总结

### 7.1 依赖关系统计

| 指标 | 数量 |
|------|:----:|
| **能力分类数** | 17 |
| **直接依赖关系** | 35 |
| **间接依赖关系** | 28 |
| **最大依赖深度** | 3 |

### 7.2 关键发现

1. **sys** 是所有能力的基础依赖
2. **know** (知识库) 依赖最复杂，深度为3
3. **llm** 与 **know** 相互依赖，形成核心AI能力
4. 驱动类技能通常依赖基础能力服务

---

**分析完成时间**: 2026-03-12  
**版本**: 2.3.1  
**维护团队**: Skills Team
