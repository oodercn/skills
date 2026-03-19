# 冲突与歧义讨论决策记录

> **文档版本**: v2.3.1  
> **讨论日期**: 2026-03-05  
> **状态**: 已完成

---

## 一、决策汇总

| 编号 | 问题 | 决策结果 | 状态 |
|------|------|---------|------|
| Q1 | 简写CC冲突 | COMP = 组合能力，CLC = 协作能力 | ✅ 已确认 |
| Q2 | 业务语义量化 | 采用评分制（10分制） | ✅ 已确认 |
| Q3 | 分类检测实现 | 已提交 Engine Team 处理依赖 | ⏳ 待Engine Team |
| Q4 | PENDING vs WAITING | PENDING=等待触发，WAITING=条件不满足 | ✅ 已确认 |
| Q5 | capability-invoker类型 | 区分 DRIVER 和 EXECUTOR | ✅ 已确认 |
| Q6 | API命名统一 | SceneCapability中文改为"场景特性" | ✅ 已确认 |
| Q7 | mainFirst设计 | 保持布尔值 + 配置对象分离 | ✅ 已确认 |
| Q8 | 自动归档 | 不自动归档，手动触发 | ✅ 已确认 |
| Q9 | 文档合并 | 按分册结构合并 | ✅ 已确认 |

---

## 二、详细决策记录

### Q1：简写CC冲突

**决策**：
| 原简写 | 新简写 | 全称 | 中文 |
|--------|--------|------|------|
| CC | **COMP** | CompositeCapability | 组合能力 |
| CC | **CLC** | CollaborativeCapability | 协作能力 |

**影响范围**：术语表、代码注释、文档

---

### Q2：业务语义量化

**决策**：采用评分制

| 评分项 | 分值 | 判定条件 |
|--------|------|---------|
| driverConditions 非空 | 3分 | 有驱动条件定义 |
| participants 非空 | 3分 | 有参与者定义 |
| visibility = public | 2分 | 对外可见 |
| 有协作能力 | 1分 | collaborativeCapabilities 非空 |
| 有业务标签 | 1分 | labels.scene.category 存在 |

**分类阈值**：
| 总分 | 分类 |
|------|------|
| ≥ 8分 | ABS（自驱业务场景） |
| 3-7分 | 待定（需人工判定） |
| < 3分 | ASS（自驱系统场景） |

---

### Q3：分类检测实现

**决策**：依赖已提交 Engine Team 处理

**待完成**：
- Engine Team 提供接口
- Skills Team 实现分类检测逻辑

---

### Q4：PENDING vs WAITING

**决策**：

| 状态 | 触发条件 | 说明 |
|------|---------|------|
| **PENDING** | 场景配置完成后 | 等待外部触发（人工/API） |
| **WAITING** | 触发后条件不满足 | 等待特定条件满足 |

**WAITING 子状态**：
- WAITING_APPROVAL
- WAITING_CONDITION
- WAITING_RESOURCE
- WAITING_SCHEDULE

---

### Q5：capability-invoker类型

**决策**：区分 DRIVER 和 EXECUTOR

| 能力 | 类型 | 理由 |
|------|------|------|
| intent-receiver | DRIVER | 外部触发源 |
| scheduler | DRIVER | 外部触发源（时间） |
| event-listener | DRIVER | 外部触发源（事件） |
| capability-invoker | EXECUTOR | 内部执行器 |
| collaboration-coordinator | EXECUTOR | 内部协调器 |

---

### Q6：API命名统一

**决策**：

| 英文 | 中文 | 简写 |
|------|------|------|
| SceneSkill | 场景技能 | SSK |
| SceneCapability | 场景特性 | SC |

**API路径**：
- `/scene-skills/install` - 安装场景技能
- `/scene-skills/{id}` - 管理场景技能

---

### Q7：mainFirst设计

**决策**：保持当前设计

| 字段 | 类型 | 说明 |
|------|------|------|
| mainFirst | boolean | 是否启用自驱 |
| mainFirstConfig | object | 自驱配置详情 |

---

### Q8：自动归档

**决策**：不自动归档，手动触发

| 状态 | 处理方式 |
|------|---------|
| COMPLETED | 保持，可查看结果 |
| ARCHIVED | 手动触发归档 |

---

### Q9：文档合并

**决策**：按分册结构合并

| 分册 | 合并来源 | 输出文档 |
|------|---------|---------|
| 第一分册 | SCENE_CAPABILITY_REQUIREMENT_SPEC.md | SCENE_CAPABILITY_SPEC.md |
| 第二分册 | SCENE_SKILL_CLASSIFICATION_AND_LIFECYCLE.md + SCENE_CAPABILITY_CLASSIFICATION_SYSTEM.md | SCENE_SKILL_CLASSIFICATION.md |
| 第三分册 | SCENE_SKILL_CLASSIFICATION_AND_LIFECYCLE.md（生命周期部分） | SCENE_LIFECYCLE_MANAGEMENT.md |
| 第四分册 | ENGINE_COLLABORATION_STATUS_V2.3.md + ENGINE_COLLABORATION_REQUEST.md | ENGINE_COLLABORATION_STATUS.md |

---

## 三、待执行任务

### 3.1 术语表更新

| 任务 | 优先级 | 状态 |
|------|--------|------|
| 更新CC简写为COMP和CLC | P0 | ⬜ 待执行 |
| SceneCapability中文改为"场景特性" | P0 | ⬜ 待执行 |
| 增加DRIVER/EXECUTOR类型区分 | P1 | ⬜ 待执行 |
| 增加业务语义评分标准 | P1 | ⬜ 待执行 |

### 3.2 代码实现

| 任务 | 优先级 | 状态 | 依赖 |
|------|--------|------|------|
| 定义SceneSkillCategory枚举 | P0 | ⬜ 待执行 | Engine Team |
| 实现detectCategory()方法 | P0 | ⬜ 待执行 | Engine Team |
| 增加DRIVER/EXECUTOR类型区分 | P1 | ⬜ 待执行 | - |
| 实现WAITING子状态 | P1 | ⬜ 待执行 | - |

### 3.3 文档更新

| 任务 | 优先级 | 状态 |
|------|--------|------|
| 更新GLOSSARY_V2.3.md | P0 | ⬜ 待执行 |
| 合并分类文档到第二分册 | P1 | ⬜ 待执行 |
| 合并生命周期文档到第三分册 | P1 | ⬜ 待执行 |
| 合并Engine协作文档到第四分册 | P1 | ⬜ 待执行 |

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-05
