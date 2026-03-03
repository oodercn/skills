# Ooder v2.3 文档索引

## 文档版本信息

| 项目 | 说明 |
|------|------|
| 版本 | v2.3 |
| 发布日期 | 2026-03-02 |
| 状态 | 正式发布 |

---

## 一、核心规格文档

| 文档 | 描述 | 路径 |
|------|------|------|
| **术语表 v2** | 统一术语定义，整合能力驱动和南向协议术语 | [GLOSSARY_V2.md](./GLOSSARY_V2.md) |
| **场景能力需求规格** | 场景能力模型、mainFirst自驱机制、能力调用链 | [SCENE_CAPABILITY_REQUIREMENT_SPEC.md](./SCENE_CAPABILITY_REQUIREMENT_SPEC.md) |
| **能力管理规格 v2.3** | 能力类型体系、驱动能力、涌现能力 | [CAPABILITY_MANAGEMENT_SPEC_V2.3.md](./CAPABILITY_MANAGEMENT_SPEC_V2.3.md) |

---

## 二、架构设计文档

| 文档 | 描述 | 路径 |
|------|------|------|
| **能力驱动架构** | 能力驱动理论、场景即能力、驱动能力体系 | [CAPABILITY_DRIVEN_ARCHITECTURE.md](./CAPABILITY_DRIVEN_ARCHITECTURE.md) |
| **场景模板协议分析** | SDK层与南向协议映射、闭环实现 | [SCENE_TEMPLATE_PROTOCOL_ANALYSIS.md](./SCENE_TEMPLATE_PROTOCOL_ANALYSIS.md) |
| **术语新旧对应表** | 旧术语到新术语的映射关系 | [TERMINOLOGY_MAPPING.md](./TERMINOLOGY_MAPPING.md) |

---

## 三、实施文档

| 文档 | 描述 | 路径 |
|------|------|------|
| **API覆盖度与工作量评估** | 现有API覆盖度分析、工作量估算 | [CAPABILITY_DRIVEN_API_COVERAGE.md](./CAPABILITY_DRIVEN_API_COVERAGE.md) |
| **SDK团队任务分解** | SDK协作团队开发任务 | [SDK_TEAM_TASKS.md](./SDK_TEAM_TASKS.md) |
| **技能重构技术方案** | 零配置安装重构方案 | [SKILLS_REFACTOR_TECHNICAL_PROPOSAL.md](./SKILLS_REFACTOR_TECHNICAL_PROPOSAL.md) |

---

## 四、核心概念变更

### 4.1 术语变更

| 旧术语 | 新术语 | 说明 |
|--------|--------|------|
| SceneDefinition | SceneCapability | 场景定义变为场景能力 |
| primaryScene | mainFirst | 主场景变为自驱入口 |
| collaborativeScenes | collaborativeCapabilities | 协作场景变为协作能力 |
| WorkflowDefinition | capabilityChains | 工作流变为能力调用链 |
| Trigger | DriverCapability | 触发器变为驱动能力 |

### 4.2 新增能力类型

| 类型 | 英文标识 | 说明 |
|------|----------|------|
| 原子能力 | ATOMIC_CAPABILITY | 单一功能，不可分解 |
| 组合能力 | COMPOSITE_CAPABILITY | 组合多个原子能力 |
| **场景能力** | SCENE_CAPABILITY | 自驱型SuperAgent能力 |
| **驱动能力** | DRIVER_CAPABILITY | 意图/时间/事件驱动 |
| 协作能力 | COLLABORATIVE_CAPABILITY | 跨场景协作能力 |

### 4.3 新增驱动能力

| 能力 | 英文标识 | 说明 |
|------|----------|------|
| 意图接收 | intent-receiver | 接收用户意图 |
| 时间驱动 | scheduler | 时间事件驱动 |
| 事件监听 | event-listener | 业务事件监听 |
| 能力调用 | capability-invoker | 能力调用链管理 |
| 协作协调 | collaboration-coordinator | 协作场景协调 |

---

## 五、文档阅读顺序

### 5.1 新用户入门

```
1. GLOSSARY_V2.md           → 了解核心术语
2. CAPABILITY_DRIVEN_ARCHITECTURE.md → 理解能力驱动理论
3. SCENE_CAPABILITY_REQUIREMENT_SPEC.md → 学习场景能力模型
4. CAPABILITY_MANAGEMENT_SPEC_V2.3.md → 掌握能力管理
```

### 5.2 开发者实施

```
1. TERMINOLOGY_MAPPING.md   → 了解术语变更
2. CAPABILITY_DRIVEN_API_COVERAGE.md → 评估API覆盖度
3. SDK_TEAM_TASKS.md        → 获取开发任务
4. SKILLS_REFACTOR_TECHNICAL_PROPOSAL.md → 实施重构
```

### 5.3 架构师设计

```
1. CAPABILITY_DRIVEN_ARCHITECTURE.md → 理解架构设计
2. SCENE_TEMPLATE_PROTOCOL_ANALYSIS.md → 协议映射分析
3. CAPABILITY_DRIVEN_API_COVERAGE.md → 工作量评估
```

---

## 六、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| v2.0 | 2026-03-01 | 初始版本，场景和能力需求规格 |
| v2.1 | 2026-03-01 | 术语统一，引用统一术语表 |
| v2.2 | 2026-03-02 | 新增核心问题讨论，零配置安装用户故事 |
| v2.3 | 2026-03-02 | **重大升级**：能力驱动架构，场景即能力，mainFirst自驱机制，整合南向协议 |

---

**文档维护者**: ooder开发团队  
**最后更新**: 2026-03-02
