# Skills 体系规划报告

**版本**: 2.3.1  
**日期**: 2026-03-18

---

## 一、MVP 内置功能 vs Skills 重复分析

### 1.1 高重复功能（需要移除）

| Skills 目录 | MVP 内置功能 | 重复程度 | 建议 |
|------------|-------------|---------|------|
| skill-knowledge-base | KnowledgeBaseController | **完全重复** | 移除 skill |
| skill-rag | MVP 内置 RAG 功能 | **完全重复** | 移除 skill |
| skill-llm-conversation | LlmController | **完全重复** | 移除 skill |
| skill-llm-config-manager | LlmProviderController | **完全重复** | 移除 skill |
| skill-llm-context-builder | MVP 内置 LLM 上下文 | **完全重复** | 移除 skill |
| skill-audit | AuditController/AuditService | **完全重复** | 移除 skill |
| skill-security | MVP 内置安全功能 | **完全重复** | 移除 skill |
| skill-access-control | RoleManagementController | **完全重复** | 移除 skill |
| skill-agent | AgentController | **完全重复** | 移除 skill |

### 1.2 中等重复功能（需要评估）

| Skills 目录 | MVP 内置功能 | 重复程度 | 建议 |
|------------|-------------|---------|------|
| skill-notification | MVP 可能有通知 | 部分重复 | 保留扩展 |
| skill-task | TodoController | 部分重复 | 保留扩展 |
| skill-search | MVP 内置搜索 | 部分重复 | 保留扩展 |

### 1.3 _system 目录分析

| Skill ID | MVP 内置 | 建议 |
|----------|---------|------|
| skill-capability | CapabilityController | **移除** - MVP 已内置完整能力管理 |
| skill-scene-management | SceneController | **移除** - MVP 已内置完整场景管理 |
| skill-llm | LlmController | **移除** - MVP 已内置 LLM 服务 |
| skill-llm-chat | - | **保留** - LLM 聊天助手是独立功能 |
| skill-common | - | **保留** - 通用工具库 |
| skill-protocol | - | **保留** - 协议处理服务 |
| skill-management | - | **保留** - 技能市场管理 |

---

## 二、建议移除的 Skills 清单

### 2.1 capabilities 目录（移除 9 个）

```
skills/capabilities/knowledge/
├── skill-knowledge-base/     # 移除 - MVP 内置
├── skill-local-knowledge/    # 移除 - MVP 内置
├── skill-rag/                # 移除 - MVP 内置
└── skill-vector-sqlite/      # 移除 - MVP 内置

skills/capabilities/llm/
├── skill-llm-config-manager/ # 移除 - MVP 内置
├── skill-llm-context-builder/# 移除 - MVP 内置
└── skill-llm-conversation/   # 移除 - MVP 内置

skills/capabilities/security/
├── skill-access-control/     # 移除 - MVP 内置
├── skill-audit/              # 移除 - MVP 内置
└── skill-security/           # 移除 - MVP 内置

skills/capabilities/monitor/
└── skill-agent/              # 移除 - MVP 内置
```

### 2.2 _system 目录（移除 3 个）

```
skills/_system/
├── skill-capability/         # 移除 - MVP 内置
├── skill-scene-management/   # 移除 - MVP 内置
└── skill-llm/                # 移除 - MVP 内置
```

---

## 三、Skills 体系架构规划

### 3.1 分层架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        MVP 核心运行时                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ 能力管理  │ │ 场景管理  │ │ LLM核心  │ │ 知识库   │          │
│  │Capability│ │ Scene    │ │ LLM Core │ │Knowledge │          │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ 审计服务  │ │ 安全服务  │ │ Agent管理│ │ 用户认证 │          │
│  │ Audit    │ │ Security │ │ Agent    │ │ Auth     │          │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │ 接口调用
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Skills 扩展层                             │
├─────────────────────────────────────────────────────────────────┤
│  _system (系统服务)                                              │
│  ├── skill-common        通用工具库                              │
│  ├── skill-protocol      协议处理服务                            │
│  ├── skill-llm-chat      LLM聊天助手                            │
│  └── skill-management    技能市场管理                            │
├─────────────────────────────────────────────────────────────────┤
│  _drivers (驱动层)                                               │
│  ├── llm/                LLM Provider 驱动                       │
│  │   ├── skill-llm-deepseek                                    │
│  │   ├── skill-llm-openai                                      │
│  │   ├── skill-llm-qianwen                                     │
│  │   ├── skill-llm-ollama                                      │
│  │   └── skill-llm-volcengine                                  │
│  ├── vfs/                虚拟文件系统驱动                        │
│  │   ├── skill-vfs-base                                        │
│  │   ├── skill-vfs-local                                       │
│  │   ├── skill-vfs-s3                                          │
│  │   ├── skill-vfs-oss                                         │
│  │   ├── skill-vfs-minio                                       │
│  │   └── skill-vfs-database                                    │
│  ├── org/                组织管理驱动                            │
│  │   ├── skill-org-base                                        │
│  │   ├── skill-org-dingding                                    │
│  │   ├── skill-org-feishu                                      │
│  │   ├── skill-org-wecom                                       │
│  │   └── skill-org-ldap                                        │
│  ├── payment/            支付驱动                                │
│  │   ├── skill-payment-alipay                                  │
│  │   ├── skill-payment-wechat                                  │
│  │   └── skill-payment-unionpay                                │
│  └── media/              媒体发布驱动                            │
│      ├── skill-media-toutiao                                   │
│      ├── skill-media-wechat                                    │
│      ├── skill-media-weibo                                     │
│      ├── skill-media-xiaohongshu                               │
│      └── skill-media-zhihu                                     │
├─────────────────────────────────────────────────────────────────┤
│  capabilities (能力服务)                                         │
│  ├── communication/      通信服务                                │
│  │   ├── skill-email                                           │
│  │   ├── skill-im                                              │
│  │   ├── skill-mqtt                                            │
│  │   ├── skill-msg                                             │
│  │   ├── skill-notify                                          │
│  │   ├── skill-notification                                    │
│  │   └── skill-group                                           │
│  ├── monitor/            监控服务                                │
│  │   ├── skill-health                                          │
│  │   ├── skill-monitor                                         │
│  │   ├── skill-network                                         │
│  │   ├── skill-remote-terminal                                 │
│  │   ├── skill-res-service                                     │
│  │   └── skill-cmd-service                                     │
│  ├── scheduler/          调度服务                                │
│  │   ├── skill-scheduler-quartz                                │
│  │   └── skill-task                                            │
│  ├── search/             搜索服务                                │
│  │   └── skill-search                                          │
│  ├── auth/               认证服务                                │
│  │   └── skill-user-auth                                       │
│  └── infrastructure/     基础设施                                │
│      └── skill-openwrt                                         │
├─────────────────────────────────────────────────────────────────┤
│  scenes (场景服务)                                               │
│  ├── skill-document-assistant   智能文档助手                    │
│  ├── skill-onboarding-assistant 新人培训助手                    │
│  ├── skill-meeting-minutes      会议纪要整理                    │
│  ├── skill-project-knowledge    项目知识沉淀                    │
│  ├── skill-knowledge-share      知识共享管理                    │
│  ├── skill-collaboration        协作场景                        │
│  └── skill-business             业务场景                        │
├─────────────────────────────────────────────────────────────────┤
│  tools (工具服务)                                                │
│  ├── skill-document-processor   文档处理器                      │
│  ├── skill-market               技能市场                        │
│  ├── skill-report               报表服务                        │
│  └── skill-share                分享服务                        │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 分类说明

| 层级 | 目录 | 职责 | 数量 |
|------|------|------|------|
| **核心层** | MVP 内置 | 运行时核心服务 | 8 个模块 |
| **系统层** | _system | 系统级扩展服务 | 4 个 |
| **驱动层** | _drivers | 外部系统集成驱动 | 24 个 |
| **能力层** | capabilities | 可插拔能力服务 | 17 个 |
| **场景层** | scenes | 业务场景封装 | 7 个 |
| **工具层** | tools | 通用工具服务 | 4 个 |

---

## 四、移除后的 Skills 统计

### 4.1 移除前

| 目录 | 数量 |
|------|------|
| _system | 8 |
| capabilities | 30 |
| _drivers | 24 |
| scenes | 7 |
| tools | 4 |
| **总计** | **73** |

### 4.2 移除后

| 目录 | 数量 | 变化 |
|------|------|------|
| _system | 4 | -4 |
| capabilities | 17 | -13 |
| _drivers | 24 | 0 |
| scenes | 7 | 0 |
| tools | 4 | 0 |
| **总计** | **56** | **-17** |

---

## 五、执行计划

### Phase 1: 移除重复 Skills

1. 移除 `capabilities/knowledge/` 下 4 个 skills
2. 移除 `capabilities/llm/` 下 3 个 skills
3. 移除 `capabilities/security/` 下 3 个 skills
4. 移除 `capabilities/monitor/skill-agent/`
5. 移除 `_system/skill-capability/`
6. 移除 `_system/skill-scene-management/`
7. 移除 `_system/skill-llm/`

### Phase 2: 更新 skill-index

1. 从 skill-index 中移除已删除 skills 的引用
2. 更新分类和依赖关系

### Phase 3: 验证

1. 确保 MVP 核心功能正常
2. 验证剩余 skills 加载正确
3. 检查依赖关系无断裂

---

## 六、保留 Skills 的职责定义

### 6.1 _system 层

| Skill ID | 职责 | 与 MVP 关系 |
|----------|------|------------|
| skill-common | 提供通用 API 和工具类 | 被 MVP 调用 |
| skill-protocol | 协议解析、适配、转换 | 独立服务 |
| skill-llm-chat | LLM 聊天助手，Function Calling | 扩展 MVP LLM |
| skill-management | 技能市场管理 | 独立服务 |

### 6.2 capabilities 层

保留的 capabilities 都是 MVP **未内置**或**可扩展**的功能：

- **communication**: 通信服务（邮件、IM、MQTT等）
- **monitor**: 监控服务（健康检查、网络管理等）
- **scheduler**: 调度服务（定时任务）
- **search**: 搜索服务（全文、语义搜索）
- **auth**: 认证服务（扩展认证方式）
- **infrastructure**: 基础设施（OpenWrt等）

### 6.3 _drivers 层

所有驱动层 skills 都是必需的，用于对接外部系统：

- **LLM Provider**: 各大模型厂商 API
- **VFS**: 各种存储后端
- **Org**: 各种组织架构源
- **Payment**: 各种支付渠道
- **Media**: 各种媒体平台

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-18
