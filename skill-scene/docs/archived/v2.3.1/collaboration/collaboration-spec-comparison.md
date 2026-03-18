# 协作规范文档对比与整合

> **文档版本**: 1.0.0  
> **编写日期**: 2026-03-09  
> **对比文档**:
> - collaboration-specification.md (v1.0)
> - engine-sdk-collaboration-spec.md (v1.0.0)
> - collaboration-specification-v2.md (修正版)

---

## 一、文档对比分析

### 1.1 文档定位对比

| 文档 | 侧重点 | 覆盖范围 | 详细程度 |
|------|--------|---------|---------|
| **collaboration-specification.md** | Skills视角 | Engine+SDK+Skills协作 | 中等 |
| **engine-sdk-collaboration-spec.md** | Engine/SDK视角 | Engine+SDK详细接口 | 高 |
| **collaboration-specification-v2.md** | 修正版Skills视角 | Skills修改任务 | 高 |

### 1.2 关键差异发现

#### 差异1: skill-user-auth 角色识别

| 文档 | 结论 | 状态 |
|------|------|------|
| collaboration-specification.md | skill-user-auth需扩展角色识别 | ❌ 错误 |
| collaboration-specification-v2.md | 角色识别应在skill-org-base | ✅ 正确 |
| engine-sdk-collaboration-spec.md | 未提及 | - |

**修正结论**: 角色识别应在 **skill-org-base** 中实现，skill-user-auth 仅负责认证

---

#### 差异2: skill-vfs-database 向量存储

| 文档 | 结论 | 状态 |
|------|------|------|
| collaboration-specification.md | skill-vfs-database需扩展向量存储 | ❌ 错误 |
| collaboration-specification-v2.md | skill-vfs-database是文件存储，无需修改 | ✅ 正确 |
| engine-sdk-collaboration-spec.md | 未提及 | - |

**修正结论**: 向量存储应在 **skill-knowledge-base** 中实现

---

#### 差异3: Engine扩展任务

| 文档 | 任务数 | 关键差异 |
|------|--------|---------|
| collaboration-specification.md | 4个引擎 | 激活流程、菜单生成、LLM上下文、工具调用 |
| engine-sdk-collaboration-spec.md | 5个扩展 | 生命周期、上下文隔离、结构化输出、工具注册、状态持久化 |

**整合结论**: engine-sdk-collaboration-spec.md 更详细，但缺少 **菜单生成引擎**

---

#### 差异4: SDK扩展任务

| 文档 | LLM-SDK任务 | Agent-SDK任务 |
|------|-------------|---------------|
| collaboration-specification.md | 4个接口 | 3个接口 |
| engine-sdk-collaboration-spec.md | 3个接口 | 3个接口 |

**差异详情**:
- collaboration-specification.md 多了 **DegradationApi** (降级策略)
- engine-sdk-collaboration-spec.md 多了 **InstallationContextManager** (安装上下文)

**整合结论**: 两者互补，需合并

---

## 二、按角色整合任务列表

### 2.1 Engine Team 任务清单

#### P0 优先级（核心阻塞）

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **ENGINE-001** | 场景技能生命周期管理 | engine-sdk-spec | 5天 | installSkill/activateSkill接口 |
| **ENGINE-002** | LLM上下文隔离管理 | engine-sdk-spec | 4天 | 安装流程专用上下文隔离 |
| **ENGINE-003** | 结构化输出支持 | engine-sdk-spec | 4天 | Schema约束+验证 |
| **ENGINE-004** | 激活流程引擎 | collab-spec | 10天 | 按角色步骤执行+回滚 |
| **ENGINE-005** | 菜单生成引擎 | collab-spec | 7天 | 按角色动态生成菜单 |

#### P1 优先级（重要）

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **ENGINE-006** | 工具调用注册中心 | engine-sdk-spec | 3天 | 工具注册+执行+权限 |
| **ENGINE-007** | 安装状态持久化 | engine-sdk-spec | 2天 | 状态保存+恢复 |

**Engine Team 总计**: 35天

---

### 2.2 SDK Team 任务清单

#### LLM-SDK 扩展

##### P0 优先级

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **LLM-SDK-001** | ToolCallingApi | collab-spec | 5天 | 工具注册+执行+LLM调用 |
| **LLM-SDK-002** | StructuredOutputApi | both | 3天 | Schema约束输出+验证 |
| **LLM-SDK-003** | ContextTemplateApi | collab-spec | 5天 | 上下文模板+管理 |

##### P1 优先级

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **LLM-SDK-004** | DegradationApi | collab-spec | 3天 | 降级策略+恢复 |
| **LLM-SDK-005** | InstallationContextManager | engine-sdk-spec | 3天 | 安装专用上下文+检查点 |

#### Agent-SDK 扩展

##### P0 优先级

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **AGENT-SDK-001** | 场景协作命令 | engine-sdk-spec | 3天 | SCENE_INSTALL/ACTIVATE等 |

##### P1 优先级

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **AGENT-SDK-002** | SceneCollaborationApi | collab-spec | 5天 | 加入/离开场景组+事件订阅 |
| **AGENT-SDK-003** | CapabilityInvocationApi | collab-spec | 3天 | 能力发现+调用+状态 |
| **AGENT-SDK-004** | LLMCollaborationApi | collab-spec | 5天 | LLM能力分配+对话 |
| **AGENT-SDK-005** | 安装状态同步 | engine-sdk-spec | 2天 | 状态广播+订阅+同步 |

##### P2 优先级

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **AGENT-SDK-006** | LLM服务发现 | engine-sdk-spec | 2天 | Provider发现+端点选择 |

**SDK Team 总计**: 39天

---

### 2.3 Skills Team 任务清单

#### 现有Skills修改

##### P0 优先级

| 任务ID | Skill ID | 任务名称 | 来源文档 | 工作量 | 修改内容 |
|--------|----------|---------|---------|--------|---------|
| **SKILL-MOD-001** | skill-llm-conversation | LLM对话扩展 | collab-spec | 7天 | +工具调用+结构化输出+降级 |
| **SKILL-MOD-002** | skill-knowledge-base | 知识库扩展 | collab-spec-v2 | 5天 | +向量存储+RAG+预制知识 |
| **SKILL-MOD-003** | skill-org-base | 组织服务扩展 | collab-spec-v2 | 5天 | +角色识别+角色配置 |

##### P1 优先级

| 任务ID | Skill ID | 任务名称 | 来源文档 | 工作量 | 修改内容 |
|--------|----------|---------|---------|--------|---------|
| **SKILL-MOD-004** | skill-llm-config-manager | 配置管理扩展 | collab-spec | 5天 | +多模型配置+降级策略 |
| **SKILL-MOD-005** | skill-rag | RAG扩展 | collab-spec | 3天 | +安装场景RAG模板 |

#### 新增Skills

| 任务ID | Skill ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|----------|---------|---------|--------|------|
| **SKILL-NEW-001** | skill-scene-installer | 场景安装器 | collab-spec-v2 | 10天 | LLM引导安装+降级 |
| **SKILL-NEW-002** | skill-scene-activator | 场景激活器 | collab-spec-v2 | 8天 | 多角色激活流程 |
| **SKILL-NEW-003** | skill-llm-installer | LLM安装技能 | collab-spec-v2 | 5天 | 内置默认场景技能 |

**Skills Team 总计**: 48天

---

### 2.4 LLM/AI Team 任务清单

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **LLM-001** | 安装说明书编写 | collab-spec-v2 | 3天 | 步骤、工具、问题清单 |
| **LLM-002** | 知识库预制内容 | collab-spec-v2 | 5天 | 场景模板、配置示例、FAQ |
| **LLM-003** | 工具定义设计 | both | 2天 | 工具参数、返回值定义 |
| **LLM-004** | Schema设计 | both | 2天 | installation-step、activation-flow等 |
| **LLM-005** | Prompt模板设计 | collab-spec-v2 | 3天 | 安装引导、配置确认、错误处理 |

**LLM/AI Team 总计**: 15天

---

### 2.5 应用开发 Team 任务清单

| 任务ID | 任务名称 | 来源文档 | 工作量 | 说明 |
|--------|---------|---------|--------|------|
| **APP-001** | 安装向导UI | collab-spec-v2 | 5天 | LLM对话式安装界面 |
| **APP-002** | 激活流程UI | collab-spec-v2 | 5天 | 多角色激活步骤界面 |
| **APP-003** | 配置界面生成器 | collab-spec-v2 | 5天 | 根据skill.yaml自动生成配置UI |
| **APP-004** | 安装进度监控 | collab-spec-v2 | 3天 | 实时显示安装/激活进度 |
| **APP-005** | 降级手动配置UI | collab-spec-v2 | 3天 | LLM不可用时手动配置界面 |
| **APP-006** | 工具调用可视化 | both | 3天 | 显示LLM调用的工具和执行结果 |

**应用开发 Team 总计**: 24天

---

## 三、关键协作点矩阵

### 3.1 跨团队协作点

| 协作点 | Engine | SDK | Skills | LLM | 应用 |
|--------|--------|-----|--------|-----|------|
| **工具调用链** | ToolRegistry | ToolCallingApi | skill-llm-conversation | 工具定义 | 工具调用可视化 |
| **结构化输出** | StructuredOutputManager | StructuredOutputApi | - | Schema设计 | - |
| **安装上下文** | LLMContextManager | ContextTemplateApi | skill-llm-context-builder | Prompt模板 | 安装向导UI |
| **激活流程** | ActivationEngine | SceneCollaborationApi | skill-scene-activator | - | 激活流程UI |
| **菜单生成** | MenuGenerator | - | skill-org-base | - | 配置界面生成器 |
| **知识检索** | - | - | skill-knowledge-base | 知识库预制 | - |
| **状态同步** | 安装状态持久化 | 安装状态同步 | skill-scene-installer | - | 安装进度监控 |

### 3.2 依赖关系图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           依赖关系图                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  Layer 1: 基础SDK (Week 1-2)                                                 │
│  ├── LLM-SDK: ToolCallingApi, StructuredOutputApi, ContextTemplateApi       │
│  └── Agent-SDK: 场景协作命令                                                 │
│                                                                              │
│  Layer 2: Engine核心 (Week 3-4)                                              │
│  ├── 场景技能生命周期管理 ◄── 依赖 SDK Layer 1                                │
│  ├── LLM上下文隔离管理 ◄── 依赖 ContextTemplateApi                           │
│  └── 结构化输出支持 ◄── 依赖 StructuredOutputApi                             │
│                                                                              │
│  Layer 3: Skills修改 (Week 3-5)                                              │
│  ├── skill-llm-conversation ◄── 依赖 ToolCallingApi, StructuredOutputApi    │
│  ├── skill-knowledge-base ◄── 独立                                           │
│  └── skill-org-base ◄── 独立                                                 │
│                                                                              │
│  Layer 4: Engine高级 (Week 5-6)                                              │
│  ├── 激活流程引擎 ◄── 依赖 skill-org-base                                    │
│  ├── 菜单生成引擎 ◄── 依赖 skill-org-base                                    │
│  └── 工具调用注册中心 ◄── 依赖 ToolCallingApi                                │
│                                                                              │
│  Layer 5: SDK高级 (Week 7-8)                                                 │
│  ├── SceneCollaborationApi ◄── 依赖 激活流程引擎                             │
│  ├── CapabilityInvocationApi ◄── 独立                                        │
│  └── LLMCollaborationApi ◄── 依赖 Layer 1                                    │
│                                                                              │
│  Layer 6: 新增Skills (Week 7-9)                                              │
│  ├── skill-scene-installer ◄── 依赖 Layer 1-5                                │
│  ├── skill-scene-activator ◄── 依赖 激活流程引擎                             │
│  └── skill-llm-installer ◄── 依赖 全部                                       │
│                                                                              │
│  Layer 7: 应用开发 (Week 8-10)                                               │
│  └── 全部UI ◄── 依赖 Layer 1-6                                               │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、查漏补缺总结

### 4.1 新增发现（原文档缺失）

| 发现项 | 重要性 | 处理方案 |
|--------|--------|---------|
| **skill-org-base角色识别** | 高 | 从skill-user-auth移到skill-org-base |
| **skill-vfs-database无需修改** | 中 | 确认是文件存储，非向量存储 |
| **菜单生成引擎** | 高 | 从collab-spec补充到engine-sdk-spec |
| **DegradationApi** | 高 | 从collab-spec补充到SDK任务 |
| **InstallationContextManager** | 中 | 从engine-sdk-spec补充到SDK任务 |
| **LLM/AI Team任务** | 高 | 新增角色，负责知识库和Prompt |
| **应用开发 Team任务** | 高 | 新增角色，负责UI实现 |

### 4.2 冲突解决

| 冲突项 | 文档A | 文档B | 决议 |
|--------|-------|-------|------|
| skill-user-auth修改 | 需扩展角色识别 | 无需修改 | ✅ 采用B，角色识别在skill-org-base |
| skill-vfs-database修改 | 需扩展向量存储 | 是文件存储 | ✅ 采用B，向量存储在skill-knowledge-base |
| Engine任务数量 | 4个 | 5个 | ✅ 合并为7个 |
| SDK任务数量 | 7个 | 6个 | ✅ 合并为10个 |

### 4.3 工作量汇总

| 团队 | 工作量 | 关键路径 |
|------|--------|---------|
| **Engine Team** | 35天 | 激活流程引擎(10天) |
| **SDK Team** | 39天 | ContextTemplateApi(5天) |
| **Skills Team** | 48天 | skill-scene-installer(10天) |
| **LLM/AI Team** | 15天 | 知识库预制内容(5天) |
| **应用开发 Team** | 24天 | 安装向导UI+激活流程UI(10天) |
| **总计** | **161天** | 约 **8周**（并行） |

---

## 五、整合后完整任务列表

### 5.1 按优先级分组

#### P0 阻塞任务（必须先完成）

| 序号 | 任务ID | 团队 | 任务 | 天数 |
|------|--------|------|------|------|
| 1 | LLM-SDK-001 | SDK | ToolCallingApi | 5 |
| 2 | LLM-SDK-002 | SDK | StructuredOutputApi | 3 |
| 3 | LLM-SDK-003 | SDK | ContextTemplateApi | 5 |
| 4 | AGENT-SDK-001 | SDK | 场景协作命令 | 3 |
| 5 | ENGINE-001 | Engine | 场景技能生命周期管理 | 5 |
| 6 | ENGINE-002 | Engine | LLM上下文隔离管理 | 4 |
| 7 | ENGINE-003 | Engine | 结构化输出支持 | 4 |
| 8 | ENGINE-004 | Engine | 激活流程引擎 | 10 |
| 9 | SKILL-MOD-001 | Skills | skill-llm-conversation扩展 | 7 |
| 10 | SKILL-MOD-002 | Skills | skill-knowledge-base扩展 | 5 |
| 11 | SKILL-MOD-003 | Skills | skill-org-base扩展 | 5 |
| 12 | LLM-001 | LLM | 安装说明书编写 | 3 |
| 13 | LLM-004 | LLM | Schema设计 | 2 |

**P0总计**: 61天

#### P1 重要任务

| 序号 | 任务ID | 团队 | 任务 | 天数 |
|------|--------|------|------|------|
| 14 | ENGINE-005 | Engine | 菜单生成引擎 | 7 |
| 15 | ENGINE-006 | Engine | 工具调用注册中心 | 3 |
| 16 | ENGINE-007 | Engine | 安装状态持久化 | 2 |
| 17 | LLM-SDK-004 | SDK | DegradationApi | 3 |
| 18 | LLM-SDK-005 | SDK | InstallationContextManager | 3 |
| 19 | AGENT-SDK-002 | SDK | SceneCollaborationApi | 5 |
| 20 | AGENT-SDK-003 | SDK | CapabilityInvocationApi | 3 |
| 21 | AGENT-SDK-004 | SDK | LLMCollaborationApi | 5 |
| 22 | AGENT-SDK-005 | SDK | 安装状态同步 | 2 |
| 23 | SKILL-MOD-004 | Skills | skill-llm-config-manager扩展 | 5 |
| 24 | SKILL-MOD-005 | Skills | skill-rag扩展 | 3 |
| 25 | SKILL-NEW-001 | Skills | skill-scene-installer | 10 |
| 26 | SKILL-NEW-002 | Skills | skill-scene-activator | 8 |
| 27 | LLM-002 | LLM | 知识库预制内容 | 5 |
| 28 | LLM-003 | LLM | 工具定义设计 | 2 |
| 29 | LLM-005 | LLM | Prompt模板设计 | 3 |
| 30 | APP-001 | 应用 | 安装向导UI | 5 |
| 31 | APP-002 | 应用 | 激活流程UI | 5 |

**P1总计**: 79天

#### P2 增强任务

| 序号 | 任务ID | 团队 | 任务 | 天数 |
|------|--------|------|------|------|
| 32 | AGENT-SDK-006 | SDK | LLM服务发现 | 2 |
| 33 | SKILL-NEW-003 | Skills | skill-llm-installer | 5 |
| 34 | APP-003 | 应用 | 配置界面生成器 | 5 |
| 35 | APP-004 | 应用 | 安装进度监控 | 3 |
| 36 | APP-005 | 应用 | 降级手动配置UI | 3 |
| 37 | APP-006 | 应用 | 工具调用可视化 | 3 |

**P2总计**: 21天

---

## 六、执行建议

### 6.1 并行执行策略

```
Week 1-2:  基础SDK开发 (SDK Team)
Week 3-4:  Engine核心 + Skills修改 (Engine + Skills Team)
Week 5-6:  Engine高级 + SDK高级 (Engine + SDK Team)
Week 7-8:  新增Skills + 应用开发 (Skills + 应用 Team)
Week 9-10: 集成测试 + Bug修复 (全部)
```

### 6.2 关键里程碑

| 里程碑 | 时间 | 交付物 |
|--------|------|--------|
| M1 | Week 2 | SDK基础接口发布 |
| M2 | Week 4 | Engine核心功能发布 |
| M3 | Week 6 | Engine+SDK高级功能发布 |
| M4 | Week 8 | Skills+应用开发完成 |
| M5 | Week 10 | 系统集成测试通过 |

---

**文档状态**: 整合完成  
**下一步**: 各团队按任务列表启动开发
