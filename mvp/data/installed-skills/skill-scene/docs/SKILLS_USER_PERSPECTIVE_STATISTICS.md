# Skills 用户视角分类统计

> **统计日期**: 2026-03-12  
> **版本**: 2.3.1  
> **总数**: 71 个技能

---

## 一、用户视角分类概览

### 1.1 按业务领域分类 (businessCategory)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        业务领域分布 (用户视角)                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  AI_ASSISTANT (AI助手)                    ████████████████░░░░  16个 (23%)  │
│  INFRASTRUCTURE (基础设施)                ████████████░░░░░░░░  12个 (17%)  │
│  SYSTEM_TOOLS (系统工具)                  ████████░░░░░░░░░░░░   8个 (11%)  │
│  SYSTEM_MONITOR (系统监控)                ████████░░░░░░░░░░░░   8个 (11%)  │
│  OFFICE_COLLABORATION (办公协作)          ███████░░░░░░░░░░░░░   7个 (10%)  │
│  MARKETING_OPERATIONS (营销运营)          █████░░░░░░░░░░░░░░░   5个  (7%)  │
│  SECURITY_AUDIT (安全审计)                █████░░░░░░░░░░░░░░░   5个  (7%)  │
│  DATA_PROCESSING (数据处理)               ████░░░░░░░░░░░░░░░░   4个  (6%)  │
│  HUMAN_RESOURCE (人力资源)                ██░░░░░░░░░░░░░░░░░░   2个  (3%)  │
│  FINANCE_ACCOUNTING (财务会计)            █░░░░░░░░░░░░░░░░░░░   1个  (1%)  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 按技能形态分类 (skillForm)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        技能形态分布 (用户视角)                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  PROVIDER (能力服务)                      ████████████████████░░  35个 (49%) │
│  ├── 用户可直接调用的能力服务                                              │
│  └── 提供特定功能接口                                                       │
│                                                                             │
│  DRIVER (驱动适配)                        █████████████░░░░░░░░  20个 (28%) │
│  ├── 第三方服务适配器                                                      │
│  └── 外部系统集成                                                          │
│                                                                             │
│  SCENE (场景应用)                         ██████████░░░░░░░░░░░   9个 (13%) │
│  ├── 用户可直接使用的场景                                                  │
│  └── 包含角色权限管理                                                      │
│                                                                             │
│  INTERNAL (内部服务)                      █████░░░░░░░░░░░░░░░░   7个 (10%) │
│  ├── 系统内部使用                                                          │
│  └── 不对用户开放                                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 按可见性分类 (visibility)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        可见性分布 (用户视角)                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  public (公开)                           ██████████████████████░░  28个 (39%)│
│  └── 所有用户可见可用                                                      │
│                                                                             │
│  developer (开发者)                       ████████████████████████  30个 (42%)│
│  └── 开发者可见可用                                                        │
│                                                                             │
│  internal (内部)                         ██████████████░░░░░░░░░░  13个 (19%)│
│  └── 仅系统内部使用                                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、用户可见技能详情

### 2.1 公开技能 (public) - 28个

用户可直接使用的公开技能：

| 技能ID | 名称 | 业务领域 | 技术分类 |
|--------|------|----------|----------|
| skill-llm-ollama | Ollama LLM服务 | AI_ASSISTANT | LLM |
| skill-llm-openai | OpenAI LLM服务 | AI_ASSISTANT | LLM |
| skill-llm-qianwen | 通义千问LLM服务 | AI_ASSISTANT | LLM |
| skill-llm-deepseek | DeepSeek LLM服务 | AI_ASSISTANT | LLM |
| skill-llm-volcengine | 火山引擎豆包LLM服务 | AI_ASSISTANT | LLM |
| skill-llm-chat | LLM智能对话场景能力 | AI_ASSISTANT | LLM |
| skill-knowledge-qa | 知识问答场景能力 | AI_ASSISTANT | KNOWLEDGE |
| skill-document-assistant | 文档助手场景能力 | AI_ASSISTANT | TOOL |
| skill-knowledge-base | 知识库核心服务 | AI_ASSISTANT | KNOWLEDGE |
| skill-rag | RAG检索增强 | AI_ASSISTANT | KNOWLEDGE |
| skill-local-knowledge | 本地知识服务 | AI_ASSISTANT | KNOWLEDGE |
| skill-vector-sqlite | SQLite向量存储 | AI_ASSISTANT | KNOWLEDGE |
| skill-llm-conversation | LLM对话服务 | AI_ASSISTANT | LLM |
| skill-llm-context-builder | 上下文构建服务 | AI_ASSISTANT | LLM |
| skill-llm-config-manager | LLM配置管理 | AI_ASSISTANT | LLM |
| skill-business | 业务场景服务 | SYSTEM_TOOLS | WORKFLOW |
| skill-collaboration | 协作场景服务 | OFFICE_COLLABORATION | WORKFLOW |
| skill-knowledge-share | 知识分享场景能力 | AI_ASSISTANT | KNOWLEDGE |
| skill-meeting-minutes | 会议纪要场景能力 | OFFICE_COLLABORATION | WORKFLOW |
| skill-project-knowledge | 项目知识场景能力 | AI_ASSISTANT | KNOWLEDGE |
| skill-onboarding-assistant | 入职助手场景能力 | HUMAN_RESOURCE | WORKFLOW |
| skill-market | 技能市场服务 | SYSTEM_TOOLS | SERVICE |
| skill-document-processor | 文档处理服务 | SYSTEM_TOOLS | TOOL |
| skill-report | 报表服务 | DATA_PROCESSING | TOOL |
| skill-share | 分享服务 | SYSTEM_TOOLS | TOOL |
| skill-search | 搜索服务 | DATA_PROCESSING | DATA |
| skill-email | 邮件服务 | OFFICE_COLLABORATION | SERVICE |
| skill-mqtt | MQTT服务 | OFFICE_COLLABORATION | SERVICE |

### 2.2 开发者技能 (developer) - 30个

开发者可使用的技能：

| 技能ID | 名称 | 业务领域 | 技术分类 |
|--------|------|----------|----------|
| skill-org-base | 组织基础服务 | INFRASTRUCTURE | SERVICE |
| skill-org-dingding | 钉钉组织服务 | INFRASTRUCTURE | SERVICE |
| skill-org-feishu | 飞书组织服务 | INFRASTRUCTURE | SERVICE |
| skill-org-wecom | 企业微信组织服务 | INFRASTRUCTURE | SERVICE |
| skill-org-ldap | LDAP组织服务 | INFRASTRUCTURE | SERVICE |
| skill-vfs-base | VFS基础服务 | SYSTEM_TOOLS | DATA |
| skill-vfs-local | 本地文件存储 | SYSTEM_TOOLS | DATA |
| skill-vfs-minio | MinIO对象存储 | SYSTEM_TOOLS | DATA |
| skill-vfs-oss | 阿里云OSS存储 | SYSTEM_TOOLS | DATA |
| skill-vfs-s3 | AWS S3存储 | SYSTEM_TOOLS | DATA |
| skill-vfs-database | 数据库存储 | SYSTEM_TOOLS | DATA |
| skill-media-toutiao | 头条发布服务 | MARKETING_OPERATIONS | SERVICE |
| skill-media-wechat | 微信公众号发布服务 | MARKETING_OPERATIONS | SERVICE |
| skill-media-weibo | 微博发布服务 | MARKETING_OPERATIONS | SERVICE |
| skill-media-xiaohongshu | 小红书发布服务 | MARKETING_OPERATIONS | SERVICE |
| skill-media-zhihu | 知乎发布服务 | MARKETING_OPERATIONS | SERVICE |
| skill-payment-alipay | 支付宝支付服务 | SYSTEM_TOOLS | SERVICE |
| skill-payment-wechat | 微信支付服务 | SYSTEM_TOOLS | SERVICE |
| skill-payment-unionpay | 银联支付服务 | SYSTEM_TOOLS | SERVICE |
| skill-msg | 消息服务 | OFFICE_COLLABORATION | SERVICE |
| skill-notify | 通知服务 | OFFICE_COLLABORATION | SERVICE |
| skill-im | 即时通讯服务 | OFFICE_COLLABORATION | SERVICE |
| skill-group | 群组服务 | OFFICE_COLLABORATION | SERVICE |
| skill-scheduler-quartz | Quartz调度服务 | INFRASTRUCTURE | SERVICE |
| skill-task | 任务管理服务 | INFRASTRUCTURE | SERVICE |
| skill-openwrt | OpenWrt管理服务 | INFRASTRUCTURE | SERVICE |
| skill-hosting | 托管服务 | INFRASTRUCTURE | SERVICE |
| skill-k8s | Kubernetes集群管理 | INFRASTRUCTURE | SERVICE |
| skill-user-auth | 用户认证服务 | SECURITY_AUDIT | SERVICE |
| skill-access-control | 访问控制服务 | SECURITY_AUDIT | SERVICE |

### 2.3 内部技能 (internal) - 13个

系统内部使用的技能：

| 技能ID | 名称 | 业务领域 | 技术分类 |
|--------|------|----------|----------|
| skill-capability | 能力管理服务 | INFRASTRUCTURE | SERVICE |
| skill-management | 技能管理服务 | INFRASTRUCTURE | SERVICE |
| skill-common | 通用工具库 | INFRASTRUCTURE | SERVICE |
| skill-protocol | 协议处理服务 | INFRASTRUCTURE | SERVICE |
| skill-agent | 代理管理服务 | SYSTEM_MONITOR | SERVICE |
| skill-health | 健康检查服务 | SYSTEM_MONITOR | SERVICE |
| skill-monitor | 监控服务 | SYSTEM_MONITOR | SERVICE |
| skill-network | 网络管理服务 | SYSTEM_MONITOR | SERVICE |
| skill-remote-terminal | 远程终端服务 | SYSTEM_MONITOR | SERVICE |
| skill-res-service | 资源管理服务 | SYSTEM_MONITOR | SERVICE |
| skill-cmd-service | 命令监控服务 | SYSTEM_MONITOR | SERVICE |
| skill-security | 安全管理服务 | SECURITY_AUDIT | SERVICE |
| skill-audit | 审计服务 | SECURITY_AUDIT | SERVICE |

---

## 三、技术分类统计 (category)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        技术分类分布 (SE标准)                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  SERVICE (服务)                           ████████████████████████  32个 (45%)│
│  LLM (大语言模型)                         ████████████░░░░░░░░░░░  10个 (14%)│
│  KNOWLEDGE (知识库)                       █████████░░░░░░░░░░░░░░   8个 (11%)│
│  TOOL (工具)                              ██████░░░░░░░░░░░░░░░░░   5个  (7%) │
│  WORKFLOW (工作流)                        █████░░░░░░░░░░░░░░░░░░   5个  (7%) │
│  DATA (数据)                              █████░░░░░░░░░░░░░░░░░░   5个  (7%) │
│  UI (界面)                                ██░░░░░░░░░░░░░░░░░░░░░   2个  (3%) │
│  OTHER (其他)                             █░░░░░░░░░░░░░░░░░░░░░░   1个  (1%) │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、能力地址分类统计 (capabilityCategory)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        能力地址分布                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  llm (LLM服务)           0x30-0x3F        ████████████░░░░░░░░░  10个 (14%) │
│  know (知识库)           0x38-0x3F        ████████░░░░░░░░░░░░░   8个 (11%) │
│  mon (监控)              0x58-0x5F        ████████░░░░░░░░░░░░░   8个 (11%) │
│  vfs (文件存储)          0x20-0x27        ██████░░░░░░░░░░░░░░░   6个  (8%) │
│  comm (通讯)             0x50-0x57        ██████░░░░░░░░░░░░░░░   6个  (8%) │
│  org (组织)              0x08-0x0F        █████░░░░░░░░░░░░░░░░   5个  (7%) │
│  media (媒体)            0x48-0x4F        █████░░░░░░░░░░░░░░░░   5个  (7%) │
│  sys (系统)              0x00-0x07        █████░░░░░░░░░░░░░░░░   5个  (7%) │
│  sec (安全)              0x78-0x7F        ████░░░░░░░░░░░░░░░░░   4个  (6%) │
│  util (工具)             0xF0-0xFF        ████░░░░░░░░░░░░░░░░░   4个  (6%) │
│  payment (支付)          0x40-0x47        ███░░░░░░░░░░░░░░░░░░   3个  (4%) │
│  iot (物联网)            0x60-0x67        ███░░░░░░░░░░░░░░░░░░   3个  (4%) │
│  sched (调度)            0x70-0x77        ██░░░░░░░░░░░░░░░░░░░   2个  (3%) │
│  auth (认证)             0x10-0x17        ██░░░░░░░░░░░░░░░░░░░   2个  (3%) │
│  search (搜索)           0x68-0x6F        █░░░░░░░░░░░░░░░░░░░░   1个  (1%) │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 五、场景技能详情 (SCENE)

用户可直接使用的场景应用：

| 场景ID | 名称 | 类型 | 业务领域 | 描述 |
|--------|------|------|----------|------|
| skill-llm-chat | LLM智能对话 | AUTO | AI_ASSISTANT | 多轮对话、上下文感知、流式输出 |
| skill-knowledge-qa | 知识问答 | AUTO | AI_ASSISTANT | 知识库管理、语义检索、RAG问答 |
| skill-document-assistant | 文档助手 | AUTO | AI_ASSISTANT | 文档生成、摘要、翻译、校对 |
| skill-business | 业务场景 | TRIGGER | SYSTEM_TOOLS | 通用业务流程管理 |
| skill-collaboration | 协作场景 | TRIGGER | OFFICE_COLLABORATION | 团队协作、任务分配、进度跟踪 |
| skill-knowledge-share | 知识分享 | TRIGGER | AI_ASSISTANT | 知识发布、订阅、推送、统计 |
| skill-meeting-minutes | 会议纪要 | TRIGGER | OFFICE_COLLABORATION | 会议记录、摘要生成、任务提取 |
| skill-project-knowledge | 项目知识 | TRIGGER | AI_ASSISTANT | 项目文档管理、知识沉淀、经验复用 |
| skill-onboarding-assistant | 入职助手 | AUTO | HUMAN_RESOURCE | 入职流程引导、知识学习、问答支持 |

---

## 六、用户使用指南

### 6.1 普通用户

**可直接使用的场景应用 (9个)**:
- LLM智能对话
- 知识问答
- 文档助手
- 知识分享
- 会议纪要
- 项目知识
- 入职助手
- 业务场景
- 协作场景

### 6.2 开发者用户

**可使用的驱动适配器 (20个)**:
- LLM驱动: Ollama, OpenAI, 通义千问, DeepSeek, 火山引擎
- 组织驱动: 钉钉, 飞书, 企业微信, LDAP
- 存储驱动: 本地, MinIO, OSS, S3, 数据库
- 媒体驱动: 头条, 微信公众号, 微博, 小红书, 知乎
- 支付驱动: 支付宝, 微信支付, 银联

### 6.3 系统管理员

**可管理的内部服务 (13个)**:
- 能力管理、技能管理
- 监控服务、健康检查
- 安全管理、审计服务
- 网络管理、资源管理

---

## 七、统计图表

### 7.1 业务领域饼图

```
         AI_ASSISTANT (23%)
              ╭──────╮
         ╭────│      │
         │    │ 16个 │
         │    │      │────╮
         │    ╰──────╯    │
         │                │ INFRASTRUCTURE (17%)
         │    SYSTEM_     │      12个
         │    TOOLS ──────╯
         │     8个
         │
         │    SYSTEM_MONITOR (11%)
         │         8个
         │
         ╰──────────────────── OFFICE_COLLABORATION (10%)
                                    7个
```

### 7.2 技能形态柱状图

```
  40 ┤
     │
  35 ┤ ████████████████████  PROVIDER: 35个
     │
  30 ┤
     │
  25 ┤
     │
  20 ┤ ████████████  DRIVER: 20个
     │
  15 ┤
     │
  10 ┤ ██████  SCENE: 9个
     │
   5 ┤ ███  INTERNAL: 7个
     │
   0 ┼────────────────────────────────────
        PROVIDER  DRIVER  SCENE  INTERNAL
```

---

**统计完成时间**: 2026-03-12  
**数据来源**: skills/*/skill-index-entry.yaml  
**版本**: 2.3.1
