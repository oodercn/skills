# Skills 应用开发参考统计数据

> **统计日期**: 2026-03-12  
> **版本**: 2.3.1  
> **数据来源**: skills/*/skill-index-entry.yaml  
> **总技能数**: 71

---

## 一、概览统计

### 1.1 技能总数

| 指标 | 数量 |
|------|:----:|
| **总技能数** | 71 |
| **skill.yaml 文件** | 51 |
| **覆盖率** | 72% |

### 1.2 技能形态分布

| 形态 | 数量 | 占比 | 说明 |
|------|:----:|:----:|------|
| **PROVIDER** | 41 | 58% | 能力服务，提供核心功能接口 |
| **DRIVER** | 18 | 25% | 驱动适配，连接第三方服务 |
| **SCENE** | 10 | 14% | 场景应用，用户直接使用的场景 |
| **INTERNAL** | 2 | 3% | 内部服务，系统内部使用 |

---

## 二、应用开发分类统计

### 2.1 按业务领域分类 (businessCategory)

| 业务领域 | 数量 | 占比 | 应用场景 |
|----------|:----:|:----:|----------|
| **AI_ASSISTANT** | 16 | 23% | AI助手、智能对话、知识问答 |
| **INFRASTRUCTURE** | 12 | 17% | 基础设施、系统管理 |
| **SYSTEM_TOOLS** | 8 | 11% | 系统工具、文件处理 |
| **SYSTEM_MONITOR** | 8 | 11% | 系统监控、运维管理 |
| **OFFICE_COLLABORATION** | 7 | 10% | 办公协作、团队沟通 |
| **MARKETING_OPERATIONS** | 5 | 7% | 营销运营、内容发布 |
| **SECURITY_AUDIT** | 5 | 7% | 安全审计、权限管理 |
| **DATA_PROCESSING** | 4 | 6% | 数据处理、报表分析 |
| **HUMAN_RESOURCE** | 2 | 3% | 人力资源、入职管理 |
| **FINANCE_ACCOUNTING** | 1 | 1% | 财务会计 |

### 2.2 按技术分类 (category)

| 技术分类 | 数量 | 占比 | 技术栈 |
|----------|:----:|:----:|--------|
| **SERVICE** | 32 | 45% | 服务类技能 |
| **LLM** | 10 | 14% | 大语言模型 |
| **KNOWLEDGE** | 8 | 11% | 知识库 |
| **TOOL** | 5 | 7% | 工具类 |
| **WORKFLOW** | 5 | 7% | 工作流 |
| **DATA** | 5 | 7% | 数据处理 |
| **UI** | 2 | 3% | 界面类 |
| **OTHER** | 1 | 1% | 其他 |

### 2.3 按可见性分类 (visibility)

| 可见性 | 数量 | 占比 | 目标用户 |
|--------|:----:|:----:|----------|
| **public** | 28 | 39% | 所有用户 |
| **developer** | 30 | 42% | 开发者 |
| **internal** | 13 | 19% | 系统内部 |

---

## 三、应用开发场景推荐

### 3.1 AI 应用开发

**可用技能**: 16个

| 技能ID | 名称 | 用途 |
|--------|------|------|
| skill-llm-ollama | Ollama LLM服务 | 本地模型部署 |
| skill-llm-openai | OpenAI LLM服务 | GPT系列模型 |
| skill-llm-qianwen | 通义千问LLM服务 | 阿里云模型 |
| skill-llm-deepseek | DeepSeek LLM服务 | DeepSeek模型 |
| skill-llm-volcengine | 火山引擎豆包LLM服务 | 字节跳动模型 |
| skill-llm-chat | LLM智能对话场景能力 | 对话应用 |
| skill-knowledge-qa | 知识问答场景能力 | 知识库问答 |
| skill-knowledge-base | 知识库核心服务 | 知识管理 |
| skill-rag | RAG检索增强 | 检索增强生成 |
| skill-document-assistant | 文档助手场景能力 | 文档处理 |

**推荐组合**:
```
AI对话应用: skill-llm-chat + skill-llm-ollama
知识问答应用: skill-knowledge-qa + skill-rag + skill-knowledge-base
文档助手: skill-document-assistant + skill-llm-conversation
```

### 3.2 企业协作应用

**可用技能**: 7个

| 技能ID | 名称 | 用途 |
|--------|------|------|
| skill-org-base | 组织基础服务 | 组织架构管理 |
| skill-org-dingding | 钉钉组织服务 | 钉钉集成 |
| skill-org-feishu | 飞书组织服务 | 飞书集成 |
| skill-org-wecom | 企业微信组织服务 | 企业微信集成 |
| skill-collaboration | 协作场景服务 | 团队协作 |
| skill-meeting-minutes | 会议纪要场景能力 | 会议管理 |
| skill-group | 群组服务 | 群组管理 |

**推荐组合**:
```
企业协作: skill-collaboration + skill-org-base + skill-msg
会议管理: skill-meeting-minutes + skill-notify
组织集成: skill-org-dingding + skill-org-feishu + skill-org-wecom
```

### 3.3 内容营销应用

**可用技能**: 5个

| 技能ID | 名称 | 用途 |
|--------|------|------|
| skill-media-toutiao | 头条发布服务 | 头条内容发布 |
| skill-media-wechat | 微信公众号发布服务 | 公众号管理 |
| skill-media-weibo | 微博发布服务 | 微博运营 |
| skill-media-xiaohongshu | 小红书发布服务 | 小红书运营 |
| skill-media-zhihu | 知乎发布服务 | 知乎运营 |

**推荐组合**:
```
多平台发布: skill-media-toutiao + skill-media-wechat + skill-media-weibo
内容管理: skill-document-processor + skill-media-*
```

### 3.4 支付电商应用

**可用技能**: 3个

| 技能ID | 名称 | 用途 |
|--------|------|------|
| skill-payment-alipay | 支付宝支付服务 | 支付宝支付 |
| skill-payment-wechat | 微信支付服务 | 微信支付 |
| skill-payment-unionpay | 银联支付服务 | 银联支付 |

**推荐组合**:
```
多渠道支付: skill-payment-alipay + skill-payment-wechat
电商应用: skill-business + skill-payment-* + skill-notify
```

### 3.5 系统运维应用

**可用技能**: 8个

| 技能ID | 名称 | 用途 |
|--------|------|------|
| skill-monitor | 监控服务 | 系统监控 |
| skill-health | 健康检查服务 | 健康检查 |
| skill-agent | 代理管理服务 | 代理管理 |
| skill-network | 网络管理服务 | 网络管理 |
| skill-remote-terminal | 远程终端服务 | 远程管理 |
| skill-res-service | 资源管理服务 | 资源监控 |
| skill-k8s | Kubernetes集群管理 | 容器管理 |
| skill-hosting | 托管服务 | 应用托管 |

**推荐组合**:
```
运维监控: skill-monitor + skill-health + skill-agent
容器管理: skill-k8s + skill-hosting
远程管理: skill-remote-terminal + skill-network
```

---

## 四、能力地址分配表

### 4.1 地址空间分布

| 地址范围 | 分类 | 技能数 |
|----------|------|:------:|
| 0x00-0x07 | SYS (系统) | 5 |
| 0x08-0x0F | ORG (组织) | 5 |
| 0x10-0x17 | AUTH (认证) | 2 |
| 0x18-0x1F | NET (网络) | 1 |
| 0x20-0x27 | VFS (文件存储) | 6 |
| 0x30-0x3F | LLM (大语言模型) | 10 |
| 0x38-0x3F | KNOW (知识库) | 8 |
| 0x40-0x47 | PAYMENT (支付) | 3 |
| 0x48-0x4F | MEDIA (媒体) | 5 |
| 0x50-0x57 | COMM (通讯) | 6 |
| 0x58-0x5F | MON (监控) | 8 |
| 0x60-0x67 | IOT (物联网) | 3 |
| 0x68-0x6F | SEARCH (搜索) | 1 |
| 0x70-0x77 | SCHED (调度) | 2 |
| 0x78-0x7F | SEC (安全) | 3 |
| 0xF0-0xFF | UTIL (工具) | 4 |

### 4.2 能力依赖关系

```
skill-llm-chat
├── skill-llm-conversation
│   └── skill-llm-context-builder
└── skill-knowledge-base
    └── skill-rag

skill-knowledge-qa
├── skill-knowledge-base
├── skill-rag
└── skill-llm-*

skill-collaboration
├── skill-msg
└── skill-notify
    └── skill-msg

skill-business
└── (独立)
```

---

## 五、API 端点统计

### 5.1 端点数量分布

| 端点数量 | 技能数 | 典型技能 |
|----------|:------:|----------|
| 1-2个 | 15 | 驱动类技能 |
| 3-4个 | 35 | 能力类技能 |
| 5-6个 | 15 | 场景类技能 |
| 7+个 | 6 | 复杂技能 |

### 5.2 常见端点模式

| 模式 | 示例 | 用途 |
|------|------|------|
| `/api/{module}` | `/api/llm/chat` | 模块入口 |
| `/api/{module}/{resource}` | `/api/knowledge/documents` | 资源操作 |
| `/api/{module}/{resource}/{id}` | `/api/vfs/files/{id}` | 单资源操作 |
| `/ws/{module}` | `/ws/im` | WebSocket连接 |

---

## 六、配置项统计

### 6.1 必需配置项

| 配置类型 | 技能数 | 示例 |
|----------|:------:|------|
| API密钥 | 18 | OPENAI_API_KEY, ALIPAY_APP_ID |
| 连接信息 | 12 | DB_HOST, SMTP_HOST |
| 认证信息 | 15 | USERNAME, PASSWORD |

### 6.2 可选配置项

| 配置类型 | 技能数 | 示例 |
|----------|:------:|------|
| 超时设置 | 25 | TIMEOUT, RETRY_COUNT |
| 缓存配置 | 20 | CACHE_TTL, MAX_CACHE_SIZE |
| 功能开关 | 15 | ENABLED, DEBUG_MODE |

---

## 七、资源需求统计

### 7.1 CPU 需求分布

| CPU | 技能数 | 占比 |
|-----|:------:|:----:|
| 50m | 35 | 49% |
| 100m | 25 | 35% |
| 200m+ | 11 | 16% |

### 7.2 内存需求分布

| 内存 | 技能数 | 占比 |
|------|:------:|:----:|
| 64Mi | 30 | 42% |
| 128Mi | 25 | 35% |
| 256Mi+ | 16 | 23% |

### 7.3 存储需求分布

| 存储 | 技能数 | 占比 |
|------|:------:|:----:|
| 10Mi | 40 | 56% |
| 100Mi | 20 | 28% |
| 1Gi+ | 11 | 16% |

---

## 八、开发建议

### 8.1 快速启动技能

以下技能无需外部依赖，可快速启动：

| 技能ID | 名称 | 用途 |
|--------|------|------|
| skill-common | 通用工具库 | 基础工具 |
| skill-msg | 消息服务 | 消息队列 |
| skill-scheduler-quartz | Quartz调度服务 | 定时任务 |
| skill-monitor | 监控服务 | 系统监控 |

### 8.2 推荐开发顺序

1. **基础层**: skill-common → skill-msg → skill-notify
2. **能力层**: skill-knowledge-base → skill-rag → skill-llm-*
3. **场景层**: skill-llm-chat → skill-knowledge-qa

### 8.3 依赖最小化组合

| 应用类型 | 最小技能组合 |
|----------|--------------|
| AI对话 | skill-llm-ollama + skill-llm-chat |
| 知识问答 | skill-knowledge-base + skill-rag |
| 消息通知 | skill-msg + skill-notify |
| 文件处理 | skill-vfs-base + skill-vfs-local |

---

## 九、版本兼容性

### 9.1 版本分布

| 版本 | 技能数 | 占比 |
|------|:------:|:----:|
| 2.x.x | 15 | 21% |
| 1.x.x | 25 | 35% |
| 0.7.x | 31 | 44% |

### 9.2 兼容性建议

- 所有技能兼容 Java 8+
- Spring Boot 2.x 框架
- 支持 Kubernetes 部署

---

**统计完成时间**: 2026-03-12  
**数据版本**: 2.3.1  
**维护团队**: Skills Team
