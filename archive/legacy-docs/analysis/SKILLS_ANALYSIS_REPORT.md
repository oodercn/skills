# Ooder Skills 技能分析报告

> **生成日期**: 2026-03-04  
> **分析范围**: ooder-skills/skills 目录下所有技能

---

## 一、技能类型统计

### 1.1 按类型分类

| 类型 | 数量 | 说明 |
|------|------|------|
| service-skill | 12 | 核心服务技能 |
| enterprise-skill | 10 | 企业级技能 |
| nexus-ui | 7 | Nexus UI 界面技能 |
| tool-skill | 4 | 工具类技能 |
| system-service | 4 | 系统服务 |

**总计**: 37 个技能

### 1.2 技能详细列表

#### service-skill (核心服务技能)

| 技能ID | 名称 | 描述 |
|--------|------|------|
| skill-management | Skill Management | 技能注册、生命周期、市场管理 |
| skill-document-processor | Document Processor | 文档解析、分块、元数据提取 |
| skill-llm-config-manager | LLM Config Manager | LLM配置管理、加密存储 |
| skill-llm-context-builder | LLM Context Builder | 上下文提取、合并、Token裁剪 |
| skill-knowledge-qa | Knowledge QA | 知识问答场景能力 |
| skill-llm-conversation | LLM Conversation | LLM对话服务 |
| skill-vfs-local | VFS Local | 本地文件存储 |
| skill-mqtt | MQTT Service | MQTT消息服务 |
| skill-user-auth | User Auth | 用户认证服务 |
| skill-scene | Scene | 场景管理服务 |
| skill-capability | Capability | 能力管理服务 |

#### enterprise-skill (企业级技能)

| 技能ID | 名称 | 描述 |
|--------|------|------|
| skill-org-dingding | DingTalk Org | 钉钉组织集成 |
| skill-org-feishu | Feishu Org | 飞书组织集成 |
| skill-org-ldap | LDAP Org | LDAP组织集成 |
| skill-org-wecom | WeCom Org | 企业微信组织集成 |
| skill-vfs-minio | MinIO Storage | MinIO对象存储 |
| skill-vfs-oss | OSS Storage | 阿里云OSS存储 |
| skill-vfs-s3 | S3 Storage | AWS S3存储 |
| skill-vfs-database | Database VFS | 数据库文件存储 |

#### nexus-ui (界面技能)

| 技能ID | 名称 | 描述 |
|--------|------|------|
| skill-llm-management-ui | LLM Management UI | LLM管理界面 |
| skill-llm-assistant-ui | LLM Assistant UI | LLM助手界面 |
| skill-knowledge-ui | Knowledge UI | 知识库界面 |
| skill-storage-management-nexus-ui | Storage Management UI | 存储管理界面 |
| skill-personal-dashboard-nexus-ui | Personal Dashboard UI | 个人仪表盘界面 |
| skill-nexus-system-status-nexus-ui | System Status UI | 系统状态界面 |
| skill-nexus-health-check-nexus-ui | Health Check UI | 健康检查界面 |
| skill-nexus-dashboard-nexus-ui | Dashboard UI | 仪表盘界面 |

#### tool-skill (工具技能)

| 技能ID | 名称 | 描述 |
|--------|------|------|
| skill-trae-solo | Trae Solo | 实用功能集成 |
| skill-openwrt | OpenWrt | OpenWrt路由器管理 |
| skill-network | Network | 网络管理 |
| skill-a2ui | A2UI | 图转代码工具 |

#### system-service (系统服务)

| 技能ID | 名称 | 描述 |
|--------|------|------|
| skill-rag | RAG | RAG检索增强 |
| skill-local-knowledge | Local Knowledge | 本地知识库 |
| skill-knowledge-base | Knowledge Base | 知识库核心 |

---

## 二、技能依赖关系

### 2.1 依赖关系图

```
skill-knowledge-qa
├── skill-knowledge-base (required) - 知识库核心服务
├── skill-rag (optional) - RAG检索增强
├── skill-llm-assistant (optional) - LLM智能助手
└── skill-indexing (required) - 文档索引服务

skill-llm-conversation
└── skill-llm-context-builder (required) - 上下文构建服务

skill-trae-solo
└── skill-a2ui (optional) - A2UI服务

skill-rag
└── skill-knowledge-base (required) - 知识库服务

skill-knowledge-base
└── skill-local-knowledge (optional) - 本地知识库
```

### 2.2 依赖关系详细表

| 技能 | 依赖 | 版本要求 | 是否必需 | 说明 |
|------|------|---------|---------|------|
| skill-knowledge-qa | skill-knowledge-base | >=1.0.0 | ✅ 必需 | 知识库核心服务 |
| skill-knowledge-qa | skill-rag | >=1.0.0 | 可选 | RAG检索增强 |
| skill-knowledge-qa | skill-llm-assistant | >=1.0.0 | 可选 | LLM智能助手 |
| skill-knowledge-qa | skill-indexing | >=1.0.0 | ✅ 必需 | 文档索引服务 |
| skill-llm-conversation | skill-llm-context-builder | >=1.0.0 | ✅ 必需 | 上下文构建服务 |
| skill-trae-solo | skill-a2ui | >=0.7.0 | 可选 | A2UI服务 |
| skill-rag | skill-knowledge-base | >=1.0.0 | ✅ 必需 | 知识库服务 |

### 2.3 无依赖技能

以下技能无外部依赖，可独立运行：

| 技能ID | 类型 |
|--------|------|
| skill-vfs-local | service-skill |
| skill-user-auth | service-skill |
| skill-mqtt | service-skill |
| skill-document-processor | service-skill |
| skill-llm-config-manager | service-skill |
| skill-management | service-skill |
| skill-org-dingding | enterprise-skill |
| skill-org-feishu | enterprise-skill |
| skill-org-ldap | enterprise-skill |
| skill-org-wecom | enterprise-skill |
| skill-vfs-minio | enterprise-skill |
| skill-vfs-oss | enterprise-skill |
| skill-vfs-s3 | enterprise-skill |
| skill-vfs-database | enterprise-skill |

---

## 三、能力(Capability)统计

### 3.1 按类别分类

| 类别 | 数量 | 主要技能 |
|------|------|---------|
| storage | 20+ | skill-vfs-*, skill-document-processor |
| messaging | 6 | skill-mqtt |
| network | 5 | skill-network, skill-openwrt |
| management | 5 | skill-management |
| authentication | 4 | skill-user-auth, skill-org-* |
| config | 3 | skill-llm-config-manager |
| context | 3 | skill-llm-context-builder |
| document | 4 | skill-document-processor |
| ai | 10+ | skill-rag, skill-knowledge-*, skill-llm-* |

### 3.2 核心能力列表

| 能力ID | 名称 | 所属技能 |
|--------|------|---------|
| vfs-storage | VFS Storage | skill-vfs-* |
| file-read | File Read | skill-vfs-* |
| file-write | File Write | skill-vfs-* |
| mqtt-broker | MQTT Broker | skill-mqtt |
| mqtt-publish | MQTT Publish | skill-mqtt |
| mqtt-subscribe | MQTT Subscribe | skill-mqtt |
| user-auth | User Authentication | skill-user-auth |
| token-validate | Token Validation | skill-user-auth |
| skill-registration | Skill Registration | skill-management |
| skill-lifecycle | Skill Lifecycle | skill-management |
| context-extraction | Context Extraction | skill-llm-context-builder |
| document-parsing | Document Parsing | skill-document-processor |
| text-chunking | Text Chunking | skill-document-processor |
| kb-management | Knowledge Base Management | skill-knowledge-base |
| kb-search | Knowledge Search | skill-knowledge-base |
| rag-retrieval | RAG Retrieval | skill-rag |

---

## 四、场景能力分析

### 4.1 场景技能列表

| 技能ID | 类型 | 场景数量 |
|--------|------|---------|
| skill-scene | service-skill | 场景管理核心 |
| skill-knowledge-qa | service-skill | 4个场景能力 |

### 4.2 场景能力详情

#### skill-knowledge-qa 场景能力

| 场景能力ID | 类型 | 说明 |
|-----------|------|------|
| kb-management | ATOMIC | 知识库管理 |
| document-management | ATOMIC | 文档管理 |
| kb-search | ATOMIC | 知识检索 |
| rag-retrieval | COMPOSITE | RAG检索 |
| intent-receiver | DRIVER | 意图接收 |
| event-listener | DRIVER | 事件监听 |
| capability-invoker | DRIVER | 能力调用 |

---

## 五、技能启动顺序建议

### 5.1 基础层（无依赖）

```
1. skill-user-auth (认证服务)
2. skill-vfs-local (本地存储)
3. skill-llm-config-manager (LLM配置)
4. skill-document-processor (文档处理)
```

### 5.2 核心层（依赖基础层）

```
5. skill-knowledge-base (知识库)
6. skill-llm-context-builder (上下文构建)
7. skill-mqtt (消息服务)
```

### 5.3 服务层（依赖核心层）

```
8. skill-rag (RAG服务)
9. skill-llm-conversation (对话服务)
10. skill-knowledge-qa (知识问答)
```

### 5.4 UI层（依赖服务层）

```
11. skill-knowledge-ui
12. skill-llm-assistant-ui
13. skill-llm-management-ui
```

---

## 六、总结

### 6.1 关键发现

1. **技能总数**: 37个
2. **场景技能**: 1个 (skill-scene) + 1个场景能力包 (skill-knowledge-qa)
3. **依赖关系**: 7个技能有外部依赖
4. **无依赖技能**: 14个可独立运行

### 6.2 建议

1. 优先启动无依赖的基础技能
2. 按依赖顺序启动核心服务
3. UI技能最后启动
4. 企业级技能按需配置

---

**报告生成时间**: 2026-03-04 09:00:00
