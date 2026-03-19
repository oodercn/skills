# Ooder v2.3 文档分册总览

> **文档版本**: v2.3.5  
> **发布日期**: 2026-03-08  
> **文档状态**: 正式发布  
> **更新说明**: 能力治理专项完成，新增能力归属分类体系

---

## 一、分册结构

```
docs/v2.3/
├── README.md                           # 本文档（分册总览）
├── GLOSSARY_V2.3.md                    # 术语表（含简写）
│
├── volume-01-specification/            # 第一分册：规范规格
│   ├── SCENE_CAPABILITY_REQUIREMENT_SPEC.md
│   ├── OODER_2.3_SPECIFICATION.md
│   ├── SKILL_YAML_STANDARD.md
│   └── CAPABILITY_MANAGEMENT_SPEC_V2.3.md
│
├── volume-02-classification/           # 第二分册：分类体系
│   ├── SCENE_SKILL_CLASSIFICATION_AND_LIFECYCLE.md
│   ├── SCENE_CAPABILITY_CLASSIFICATION_SYSTEM.md
│   ├── CAPABILITY_OWNERSHIP_GUIDE.md
│   ├── CAPABILITY_INVENTORY.md
│   └── CAPABILITY_GOVERNANCE_TASKS.md
│
├── volume-03-lifecycle/                # 第三分册：生命周期
│   └── README.md
│
├── volume-04-architecture/             # 第四分册：架构设计
│   ├── CAPABILITY_DRIVEN_ARCHITECTURE.md
│   ├── ENGINE_COLLABORATION_STATUS_V2.3.md
│   ├── ENGINE_TEAM_DELIVERY.md
│   └── SCENE-Engine-SPEC.md
│
├── volume-05-development/              # 第五分册：开发指南
│   ├── SDK_2.3_UPGRADE_GUIDE.md
│   ├── SDK_V2.3_MODULE_ADDRESS_TABLE.md
│   └── ... (其他SDK文档)
│
├── volume-06-user-stories/             # 第六分册：用户故事
│   ├── USER_CLOSED_LOOP_STORIES_V2.3.md
│   └── STORY_001_*.md
│
└── archive/                            # 归档文档
    ├── deprecated/                     # 已废弃文档 (15个)
    ├── historical/                     # 历史版本 (10个)
    ├── drafts/                         # 草稿文档 (16个)
    ├── a2ui/                           # A2UI相关 (5个)
    ├── blog/                           # 博客文章 (5个)
    ├── nexus/                          # Nexus相关 (3个)
    ├── product/                        # 产品文档 (2个)
    └── skills-dev/                     # Skills开发 (9个)
```

---

## 二、核心术语简写

| 简写 | 全称 | 中文 |
|------|------|------|
| **ABS** | Auto Business Scene | 自驱业务场景 |
| **ASS** | Auto System Scene | 自驱系统场景 |
| **TBS** | Trigger Business Scene | 触发业务场景 |
| **SSK** | SceneSkill | 场景技能 |
| **SC** | SceneCapability | 场景特性 |
| **COMP** | CompositeCapability | 组合能力 |
| **CLC** | CollaborativeCapability | 协作能力 |

---

## 三、能力归属分类

| 分类 | 简写 | 定义位置 | 可见性 |
|------|------|----------|--------|
| **场景内部能力** | SIC | skill.yaml 的 capabilities 列表 | 仅场景内 |
| **独立能力** | IC | 独立 skill 包 | supportedSceneTypes |
| **平台能力** | PC | 平台内置 | 全局 |
| **工具技能** | TOOL | 独立工具包 | 按需 |

---

## 四、场景技能三大分类

| 分类 | 简写 | 自驱能力 | 业务语义 | 触发方式 |
|------|------|---------|---------|---------|
| **自驱业务场景** | ABS | ✓ 有 | ✓ 强 | 自动 |
| **自驱系统场景** | ASS | ✓ 有 | ✗ 弱 | 自动 |
| **触发业务场景** | TBS | ✗ 无 | ✓ 强 | 人工/API |

---

## 五、分册说明

### 第一分册：规范规格
核心规范文档，定义系统的基本概念和规则

### 第二分册：分类体系
场景技能分类定义和判定规则

### 第三分册：生命周期
生命周期状态和流程管理

### 第四分册：架构设计
系统架构和 Engine 协作关系

### 第五分册：开发指南
开发者指南和实施手册

### 第六分册：用户故事
用户故事和用例

---

## 五、讨论决策汇总

| 编号 | 问题 | 决策结果 |
|------|------|---------|
| Q1 | 简写CC冲突 | COMP = 组合能力，CLC = 协作能力 |
| Q2 | 业务语义量化 | 采用评分制（10分制） |
| Q3 | 分类检测实现 | 已提交 Engine Team 处理依赖 |
| Q4 | PENDING vs WAITING | PENDING=等待触发，WAITING=条件不满足 |
| Q5 | capability-invoker类型 | 区分 DRIVER 和 EXECUTOR |
| Q6 | API命名统一 | SceneCapability中文改为"场景特性" |
| Q7 | mainFirst设计 | 保持布尔值 + 配置对象分离 |
| Q8 | 自动归档 | 不自动归档，手动触发 |
| Q9 | 文档合并 | 按分册结构合并 |

---

## 七、任务执行状态

### 能力治理专项 ✅ 已完成

| 任务 | 优先级 | 状态 |
|------|--------|------|
| 定义 CapabilityOwnership 枚举 | P0 | ✅ 已完成 |
| 实现能力归属判定方法 | P0 | ✅ 已完成 |
| 更新现有 skill.yaml 归属配置 | P1 | ✅ 已完成 |
| 编写能力归属判定指南 | P0 | ✅ 已完成 |
| 完善不完整技能配置 | P1 | ✅ 已完成 |

### 术语表更新 ✅ 已完成

### 文档整理 ✅ 已完成

### Engine Team 实现 ✅ 已完成

| 任务 | 优先级 | 状态 |
|------|--------|------|
| 定义SceneSkillCategory枚举 | P0 | ✅ 已完成 |
| 实现detectCategory()方法 | P0 | ✅ 已完成 |
| 增加DRIVER/EXECUTOR类型区分 | P1 | ✅ 已完成 |
| 实现WAITING子状态 | P1 | ✅ 已完成 |

### Engine Team 交付文档

详见：[ENGINE_TEAM_DELIVERY.md](./volume-04-architecture/ENGINE_TEAM_DELIVERY.md)

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-08
