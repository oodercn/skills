# 现有能力归属盘点

> **文档版本**: v2.0  
> **盘点日期**: 2026-03-08  
> **关联文档**: CAPABILITY_GOVERNANCE_TASKS.md, CAPABILITY_OWNERSHIP_GUIDE.md

---

## 一、盘点标准

### 1.1 归属类型判定规则

| 类型 | 判定标准 | skill.yaml 特征 |
|------|---------|----------------|
| **场景内部能力 (SIC)** | 定义在场景技能包内，仅场景内可见 | `type: scene-skill` + `capabilities` 列表 |
| **独立能力 (IC)** | 独立部署，支持多场景，可跨场景组运行 | `type: service-skill` + `supportedSceneTypes` + `ownership: independent` |
| **平台能力 (PC)** | 平台核心基础设施，全局可见 | `type: system-service` 或 `type: enterprise-skill` |

---

## 二、技能归属分类

### 2.1 场景技能 (Scene Skill) - 场景内部能力 (SIC)

| 技能ID | 技能名称 | 归属类型 | 内部能力数 | 完整性 |
|--------|---------|---------|-----------|--------|
| skill-llm-chat | LLM智能对话 | SIC | 6 | ✅ 完整 |
| skill-knowledge-qa | 知识问答 | SIC | 7 | ✅ 完整 |
| skill-document-assistant | 智能文档助手 | SIC | 3 | ✅ 完整 |
| skill-meeting-minutes | 会议纪要整理 | SIC | 3 | ✅ 完整 |
| skill-knowledge-share | 知识共享管理 | SIC | 3 | ✅ 完整 |
| skill-onboarding-assistant | 新人培训助手 | SIC | 4 | ✅ 完整 |
| skill-project-knowledge | 项目知识沉淀 | SIC | 4 | ✅ 完整 |
| skill-document-assistant-ui | 智能文档助手UI | SIC | 4 | ✅ 完整 |
| skill-llm-assistant-ui | LLM智能助手UI | SIC | 5 | ✅ 完整 |
| skill-knowledge-ui | 知识库UI | SIC | 4 | ✅ 完整 |
| skill-llm-management-ui | LLM管理界面 | SIC | 4 | ✅ 完整 |
| skill-nexus-health-check-nexus-ui | 健康检查UI | SIC | 2 | ✅ 完整 |
| skill-scene | 场景管理 | SIC | 9 | ✅ 完整 |

### 2.2 独立能力 (Independent Capability) - 独立能力 (IC)

| 技能ID | 技能名称 | 归属类型 | supportedSceneTypes | 完整性 |
|--------|---------|---------|---------------------|--------|
| skill-mqtt | MQTT Service | IC | iot-device, smart-home, industrial-monitor, recruitment | ✅ 完整 |
| skill-knowledge-base | 知识库核心服务 | IC | knowledge-qa, document-assistant, meeting-minutes | ✅ 完整 |
| skill-rag | RAG检索增强 | IC | knowledge-qa, document-assistant | ✅ 完整 |
| skill-llm-conversation | LLM对话服务 | IC | llm-chat, knowledge-qa, onboarding-assistant, document-assistant | ✅ 完整 |
| skill-llm-context-builder | LLM上下文构建 | IC | llm-chat, knowledge-qa, document-assistant | ✅ 完整 |
| skill-llm-config-manager | LLM配置管理 | IC | llm-chat, knowledge-qa | ✅ 完整 |
| skill-document-processor | 文档处理器 | IC | document-assistant, knowledge-qa, meeting-minutes | ✅ 完整 |
| skill-local-knowledge | 本地知识服务 | IC | llm-assistant, llm-chat, knowledge-qa | ✅ 完整 |

### 2.3 平台能力 (Platform Capability) - 平台能力 (PC)

| 技能ID | 技能名称 | 归属类型 | 类型 | 完整性 |
|--------|---------|---------|------|--------|
| skill-user-auth | 用户认证服务 | PC | service-skill | ✅ 完整 |
| skill-vfs-local | 本地文件存储 | PC | service-skill | ✅ 完整 |
| skill-vfs-minio | MinIO存储 | PC | enterprise-skill | ✅ 完整 |
| skill-vfs-oss | 阿里云OSS存储 | PC | enterprise-skill | ✅ 完整 |
| skill-vfs-s3 | AWS S3存储 | PC | enterprise-skill | ✅ 完整 |
| skill-vfs-database | 数据库存储 | PC | enterprise-skill | ✅ 完整 |
| skill-org-ldap | LDAP组织 | PC | enterprise-skill | ✅ 完整 |
| skill-org-feishu | 飞书组织 | PC | enterprise-skill | ✅ 完整 |
| skill-org-dingding | 钉钉组织 | PC | enterprise-skill | ✅ 完整 |
| skill-org-wecom | 企业微信组织 | PC | enterprise-skill | ✅ 完整 |
| skill-org-base | 本地组织服务 | PC | service-skill | ✅ 完整 |
| skill-health | 健康检查服务 | PC | system-service | ✅ 完整 |
| skill-monitor | 监控服务 | PC | system-service | ✅ 完整 |
| skill-management | 技能管理 | PC | service-skill | ✅ 完整 |
| skill-capability | 能力管理 | PC | service-skill | ✅ 完整 |
| skill-network | 网络管理 | PC | service-skill | ✅ 完整 |

### 2.4 工具技能 (Tool Skill)

| 技能ID | 技能名称 | 归属类型 | 类型 | 完整性 | 说明 |
|--------|---------|---------|------|--------|------|
| skill-openwrt | OpenWrt驱动 | TOOL | tool-skill | ✅ 完整 | 硬件驱动工具 |
| skill-trae-solo | Trae Solo | TOOL | tool-skill | ✅ 完整 | A2UI集成工具 |
| skill-a2ui | A2UI技能 | TOOL | tool-skill | ✅ 完整 | 图转代码开发工具 |

### 2.5 Nexus UI 技能 - 平台UI组件 (PC)

| 技能ID | 技能名称 | 归属类型 | 类型 | 完整性 | 说明 |
|--------|---------|---------|------|--------|------|
| skill-nexus-dashboard-nexus-ui | Nexus仪表盘 | PC | nexus-ui | ✅ 完整 | 依赖skill-monitor |
| skill-nexus-health-check-nexus-ui | 健康检查 | SIC | scene-skill | ✅ 完整 | - |
| skill-nexus-system-status-nexus-ui | 系统状态 | SIC | scene-skill | ✅ 完整 | - |
| skill-storage-management-nexus-ui | 存储管理 | PC | nexus-ui | ✅ 完整 | 依赖skill-vfs-* |
| skill-personal-dashboard-nexus-ui | 个人仪表盘 | PC | nexus-ui | ✅ 完整 | 依赖skill-user-auth |

---

## 三、完整性检查结果

### 3.1 完整的技能 (符合规范)

以下技能完全符合能力归属规范：

| 技能ID | 归属类型 | 规范特征 |
|--------|---------|---------|
| skill-mqtt | IC | ownership: independent, supportedSceneTypes, dynamicSceneTypes, autoStart, autoJoin |
| skill-knowledge-base | IC | ownership: independent, supportedSceneTypes, dynamicSceneTypes, autoStart, autoJoin |
| skill-rag | IC | ownership: independent, supportedSceneTypes, dynamicSceneTypes, autoStart, autoJoin |
| skill-llm-conversation | IC | ownership: independent, supportedSceneTypes, dynamicSceneTypes, autoStart, autoJoin |
| skill-llm-context-builder | IC | ownership: independent, supportedSceneTypes, dynamicSceneTypes, autoStart, autoJoin |
| skill-llm-config-manager | IC | ownership: independent, supportedSceneTypes, dynamicSceneTypes, autoStart, autoJoin |
| skill-document-processor | IC | ownership: independent, supportedSceneTypes, dynamicSceneTypes, autoStart, autoJoin |
| skill-local-knowledge | IC | ownership: independent, supportedSceneTypes, dynamicSceneTypes, autoStart, autoJoin |
| skill-llm-chat | SIC | type: scene-skill, capabilities列表, sceneCapabilities |
| skill-knowledge-qa | SIC | type: scene-skill, capabilities列表, sceneCapabilities |
| skill-document-assistant | SIC | type: scene-skill, capabilities列表, sceneCapabilities |
| skill-user-auth | PC | ownership: platform |
| skill-vfs-local | PC | ownership: platform |
| skill-vfs-minio | PC | ownership: platform |
| skill-vfs-oss | PC | ownership: platform |
| skill-vfs-s3 | PC | ownership: platform |
| skill-vfs-database | PC | ownership: platform |
| skill-org-ldap | PC | ownership: platform |
| skill-org-feishu | PC | ownership: platform |
| skill-org-dingding | PC | ownership: platform |
| skill-org-wecom | PC | ownership: platform |
| skill-org-base | PC | ownership: platform |
| skill-health | PC | ownership: platform |
| skill-monitor | PC | ownership: platform |
| skill-management | PC | ownership: platform |
| skill-capability | PC | ownership: platform |
| skill-network | PC | ownership: platform |
| skill-nexus-dashboard-nexus-ui | PC | ownership: platform, dependencies |
| skill-storage-management-nexus-ui | PC | ownership: platform, dependencies |
| skill-personal-dashboard-nexus-ui | PC | ownership: platform, dependencies |
| skill-openwrt | TOOL | ownership: tool |
| skill-trae-solo | TOOL | ownership: tool |
| skill-a2ui | TOOL | ownership: tool |

### 3.2 需要补充的技能 (不符合规范)

所有技能已完成归属配置，无待补充项。

---

## 四、能力依赖关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                     场景技能层 (SIC)                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  skill-llm-chat ─────┬──▶ skill-llm-conversation (IC)          │
│                      ├──▶ skill-llm-context-builder (IC)        │
│                      └──▶ skill-llm-config-manager (IC)         │
│                                                                 │
│  skill-knowledge-qa ─┬──▶ skill-knowledge-base (IC)            │
│                      └──▶ skill-rag (IC)                        │
│                                                                 │
│  skill-document-assistant ──▶ skill-knowledge-base (IC)         │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                     独立能力层 (IC)                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  skill-llm-conversation ──▶ skill-llm-context-builder (IC)     │
│  skill-knowledge-base ────▶ (无依赖)                            │
│  skill-mqtt ─────────────▶ (无依赖)                             │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                     平台能力层 (PC)                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  skill-user-auth ─────────▶ 平台核心                            │
│  skill-vfs-* ─────────────▶ 平台核心                            │
│  skill-org-* ─────────────▶ 平台核心                            │
│  skill-health ────────────▶ 平台核心                            │
│  skill-monitor ───────────▶ 平台核心                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 五、协作关系验证

### 5.1 已验证的协作关系

| 协作类型 | 示例 | 是否符合规则 | 实现机制 |
|---------|------|-------------|---------|
| 场景内能力调用 | llm-chat → context-extraction | ✅ 符合 | CapabilityInvoker + CapabilityBinding |
| 场景调用独立能力 | skill-llm-chat → skill-llm-conversation | ✅ 符合 | CapabilityBinding.sceneGroupId 绑定 |
| 独立能力无依赖 | skill-mqtt 独立运行 | ✅ 符合 | autoStart + autoJoin 机制 |
| 场景组数据隔离 | 不同 sceneGroupId 的 Binding | ✅ 符合 | CapabilityBinding.sceneGroupId 字段 |

### 5.2 跨场景协作机制

| 协作类型 | 实现机制 | 验证状态 |
|---------|---------|---------|
| 跨场景能力调用 | CommandController → targetAgent 命令转发 | ✅ 已实现 |
| 独立能力多场景组 | autoJoin.matchSceneTypes + maxSceneGroups | ✅ 已配置 |
| 数据隔离 | CapabilityBinding.sceneGroupId 绑定 | ✅ 已实现 |

### 5.3 关键代码验证

**CapabilityInvoker** ([CapabilityInvokerImpl.java](file:///e:/github/ooder-skills/skills/skill-scene/src/main/java/net/ooder/skill/scene/capability/invoke/CapabilityInvokerImpl.java)):
- 通过 `CapabilityBindingService.getByCapId()` 获取绑定
- 检查 `isBindingActive(binding)` 确保绑定有效
- 支持 HTTP/HTTPS/INTERNAL/WEBSOCKET 连接器

**CommandController** ([CommandController.java](file:///e:/github/ooder-skills/temp/ooder-Nexus/src/main/java/net/ooder/nexus/adapter/inbound/controller/command/CommandController.java)):
- `targetAgent` 参数支持跨 Agent 命令转发
- 命令队列管理 (PENDING → PROCESSING → COMPLETED)
- NetworkLink 管理 Agent 间连接

**CapabilityBinding** ([CapabilityBinding.java](file:///e:/github/ooder-skills/skills/skill-scene/src/main/java/net/ooder/skill/scene/capability/model/CapabilityBinding.java#L10-L11)):
- `sceneGroupId` 字段确保场景组隔离
- `providerType` 标识能力提供者类型
- `connectorType` 支持多种连接方式

---

## 六、下一步行动

### 6.1 已完成 - 独立能力配置

以下独立能力已添加 `ownership: independent` 和 `supportedSceneTypes`:

| 技能ID | supportedSceneTypes | 状态 |
|--------|---------------------|------|
| skill-llm-context-builder | llm-chat, knowledge-qa, document-assistant | ✅ 完成 |
| skill-llm-config-manager | llm-chat, knowledge-qa | ✅ 完成 |
| skill-document-processor | document-assistant, knowledge-qa, meeting-minutes | ✅ 完成 |
| skill-local-knowledge | llm-assistant, llm-chat, knowledge-qa | ✅ 完成 |

### 6.2 已完成 - 平台能力归属

以下平台能力已添加 `ownership: platform`:

| 技能ID | 状态 |
|--------|------|
| skill-user-auth | ✅ 完成 |
| skill-vfs-local, skill-vfs-minio, skill-vfs-oss, skill-vfs-s3, skill-vfs-database | ✅ 完成 |
| skill-org-ldap, skill-org-feishu, skill-org-dingding, skill-org-wecom, skill-org-base | ✅ 完成 |
| skill-health, skill-monitor | ✅ 完成 |
| skill-management, skill-capability | ✅ 完成 |

### 6.3 已完成 - 工具技能归属

以下工具技能已添加 `ownership: tool`:

| 技能ID | 状态 |
|--------|------|
| skill-network | ✅ 完成 |
| skill-openwrt | ✅ 完成 |
| skill-trae-solo | ✅ 完成 |
| skill-a2ui | ✅ 完成 |

### 6.4 已完成 - Nexus UI 技能完善

以下纯前端 UI 组件已完善配置：

| 技能ID | 完善内容 | 依赖服务 |
|--------|---------|---------|
| skill-nexus-dashboard-nexus-ui | 添加 ownership, dependencies, capabilities, endpoints | skill-monitor, skill-health |
| skill-storage-management-nexus-ui | 添加 ownership, dependencies, capabilities, endpoints | skill-vfs-local, skill-vfs-minio, skill-vfs-oss |
| skill-personal-dashboard-nexus-ui | 添加 ownership, dependencies, capabilities, endpoints | skill-user-auth, skill-management |

**nexus-ui 类型说明**：
- `type: nexus-ui` 表示纯前端 UI 组件
- `runtime.language: javascript` 表示前端运行时
- `renderMode: client-side` 表示客户端渲染
- 通过 `dependencies` 声明对后端服务的依赖

### 6.5 分类调整记录

| 技能ID | 原分类 | 新分类 | 调整原因 |
|--------|--------|--------|---------|
| skill-network | TOOL | PC | 网络管理属于平台基础设施 |

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-08
