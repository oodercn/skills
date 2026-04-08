# SE团队文档技术可行性分析报告

> **文档类型**: 技术可行性分析  
> **分析日期**: 2026-03-11  
> **分析范围**: 
> - PHASE1_BASE_LAYER_CONFIG.md (v1.0.0)
> - SKILL_INDEX_REFACTORING_PROPOSAL.md (v1.0.0)
> - skill-index.yaml (当前实现)
> **状态**: 待评审

---

## 一、文档对比分析

### 1.1 版本信息对比

| 文档 | 版本 | 创建日期 | 目标 |
|------|------|----------|------|
| PHASE1_BASE_LAYER_CONFIG.md | 1.0.0 | 2026-03-11 | 创建基础层配置 |
| SKILL_INDEX_REFACTORING_PROPOSAL.md | 1.0.0 | 2026-03-11 | 分层重构方案 |
| skill-index.yaml (当前) | 2.3.0 | 2026-03-11 | 现有实现 |

**发现**: 文档版本为 1.0.0，建议统一到 **2.3.1**

---

## 二、与现有实现的冲突分析

### 2.1 字段冲突检查

| 字段 | SE标准v1.1.0 | 现有实现 | 冲突级别 | 状态 |
|------|--------------|----------|:--------:|------|
| `id` | ✅ 必需 | ✅ 已改为 id | 🟢 无 | 已完成 |
| `skillForm` | ✅ 必需 (4个枚举) | ✅ 已改为 skillForm | 🟢 无 | 已完成 |
| `visibility` | ✅ 必需 (3个枚举) | ✅ 已存在 | 🟢 无 | 已完成 |
| `category` | ✅ 必需 (8个枚举) | ⚠️ 在scenes中 | 🟡 需新增 | **待执行** |
| `capabilityCategory` | ✅ 必需 (17个枚举) | ✅ 已改为 capabilityCategory | 🟢 无 | 已完成 |
| `businessCategory` | ✅ 必需 (10个枚举) | ❌ 无 | 🔴 严重 | **待新增** |
| `sceneType` | ✅ 条件必需 | ❌ 无 | 🔴 严重 | **待新增** |
| `capabilityAddresses` | ✅ 必需 | ❌ 无 | 🔴 严重 | **待新增** |
| `roles` | ✅ 条件必需 | ❌ 无 | 🟡 中等 | **待新增** |

### 2.2 冲突详情

#### 冲突1: businessCategory 缺失
- **SE标准要求**: 10个业务分类枚举
- **现有实现**: 无此字段
- **解决方案**: 为每个skill新增businessCategory字段

#### 冲突2: sceneType 缺失
- **SE标准要求**: SCENE技能必需，值为AUTO或TRIGGER
- **现有实现**: mainFirst字段存在但语义不同
- **解决方案**: 将mainFirst映射为sceneType (true=AUTO, false=TRIGGER)

#### 冲突3: capabilityAddresses 缺失
- **SE标准要求**: required和optional地址配置
- **现有实现**: capabilities字段存在但格式不同
- **解决方案**: 根据capabilityCategory自动生成地址

#### 冲突4: category (SE标准) 缺失
- **SE标准要求**: 8个技术分类 (KNOWLEDGE, LLM, TOOL等)
- **现有实现**: scenes中有category，skills中无
- **解决方案**: 为每个skill新增category字段

---

## 三、技术可行性评估

### 3.1 依赖关系分析

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         执行依赖关系                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Phase 1: 基础层配置                                                        │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ config/schema.yaml        ─────────────────────────────────────┐     │   │
│  │ config/addresses.yaml    ──────────────────────────────────┼─────┤     │   │
│  │ config/categories.yaml  ────────────────────────────────┬──┴─────┤     │   │
│  └─────────────────────────────────────────────────────────┼─────────┘     │   │
│                                                            │                │
│  Phase 2: skill-index.yaml 字段更新                              │                │
│  ┌─────────────────────────────────────────────────────────┴─────────┐     │   │
│  │ • id 字段重命名 (skillId → id)                               ✅ 已完成│   │
│  │ • skillForm 字段重命名 (skillType → skillForm)              ✅ 已完成│   │
│  │ • capabilityCategory 字段重命名 (category → capabilityCategory) ✅ 已完成│   │
│  │ • 新增 businessCategory 字段                                 ⚠️ 待执行│   │
│  │ • 新增 sceneType 字段                                        ⚠️ 待执行│   │
│  │ • 新增 capabilityAddresses 字段                              ⚠️ 待执行│   │
│  │ • 新增 category 字段 (SE标准)                                 ⚠️ 待执行│   │
│  │ • 新增 roles 字段                                            ⚠️ 待执行│   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 工作量评估

| 任务 | 优先级 | 工作量 | 依赖 |
|------|:------:|:------:|------|
| 版本统一到2.3.1 | P0 | 0.5h | 无 |
| 创建config/schema.yaml | P0 | 2h | 无 |
| 创建config/addresses.yaml | P0 | 3h | schema.yaml |
| 创建config/categories.yaml | P0 | 2h | schema.yaml |
| skill-index.yaml: 新增businessCategory | P0 | 4h | categories.yaml |
| skill-index.yaml: 新增sceneType | P0 | 2h | categories.yaml |
| skill-index.yaml: 新增capabilityAddresses | P0 | 3h | addresses.yaml |
| skill-index.yaml: 新增category (SE标准) | P1 | 2h | categories.yaml |
| skill-index.yaml: 新增roles | P1 | 2h | schema.yaml |
| **总计** | | **20.5h** | |

---

## 四、执行计划

### 4.1 推荐执行顺序

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         执行计划 (建议5天)                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  Day 1: 准备与基础层                                                         │
│  ───────────────────────────────────────────────────────────────────────    │
│  [0.5h]  版本统一到2.3.1                                                      │
│  [2h]   创建 config/schema.yaml                                              │
│  [2h]   创建 config/addresses.yaml                                           │
│  [2h]   创建 config/categories.yaml                                          │
│                                                                             │
│  Day 2-3: skill-index.yaml 核心字段                                          │
│  ───────────────────────────────────────────────────────────────────────    │
│  [4h]   为所有skills新增 businessCategory 字段                               │
│  [2h]   为所有skills新增 sceneType 字段 (AUTO/TRIGGER)                       │
│                                                                             │
│  Day 4: skill-index.yaml 扩展字段                                            │
│  ───────────────────────────────────────────────────────────────────────    │
│  [3h]   为所有skills新增 capabilityAddresses 字段                           │
│  [2h]   为所有skills新增 category 字段 (SE标准8个枚举)                       │
│                                                                             │
│  Day 5: 验证与文档                                                           │
│  ───────────────────────────────────────────────────────────────────────    │
│  [2h]   为SCENE类型新增 roles 字段                                          │
│  [3h]   全面验证 + 更新文档                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 字段映射规则

```yaml
# 字段映射规则

# 1. mainFirst → sceneType
mapping:
  mainFirst:
    true: AUTO    # 自驱型场景
    false: TRIGGER # 触发型场景

# 2. capabilityCategory → capabilityAddresses 自动生成
mapping:
  capabilityCategory:
    llm:
      required:
        - address: 0x28
          name: LLM_PROVIDER
    know:
      required:
        - address: 0x30
          name: KNOWLEDGE_BASE

# 3. capabilityCategory → category (SE标准)
mapping:
  capabilityCategory:
    llm: LLM
    know: KNOWLEDGE
    util: TOOL
    org: SERVICE
    vfs: DATA
    comm: SERVICE
    payment: SERVICE
    media: SERVICE
    mon: SERVICE
    sec: SERVICE
    iot: SERVICE
    search: DATA
    sched: SERVICE
    sys: SERVICE
    net: SERVICE
    auth: SERVICE
    db: DATA
```

### 4.3 businessCategory 分配规则

```yaml
# 根据 skillForm 和 capabilityCategory 自动分配
mapping:
  skillForm_SCENE:
    capabilityCategory:
      llm: AI_ASSISTANT
      know: AI_ASSISTANT
      util: SYSTEM_TOOLS
      comm: OFFICE_COLLABORATION
      media: MARKETING_OPERATIONS
      payment: SYSTEM_TOOLS
      
  skillForm_PROVIDER:
    capabilityCategory:
      llm: AI_ASSISTANT
      know: DATA_PROCESSING
      vfs: SYSTEM_TOOLS
      comm: OFFICE_COLLABORATION
      
  skillForm_DRIVER:
    capabilityCategory:
      org: INFRASTRUCTURE
      vfs: INFRASTRUCTURE
      llm: AI_ASSISTANT
      payment: SYSTEM_TOOLS
      media: MARKETING_OPERATIONS
```

---

## 五、风险评估

### 5.1 风险与缓解

| 风险 | 可能性 | 影响 | 缓解措施 |
|------|:------:|:----:|----------|
| 字段映射错误 | 中 | 高 | 自动化脚本 + 人工复核 |
| 版本不兼容 | 低 | 高 | 保留旧字段作为兼容 |
| 工作量超预期 | 中 | 中 | 分阶段执行 + 优先级调整 |

### 5.2 回滚方案

```bash
# 如果出现问题，回滚步骤
git stash
# 或
git revert HEAD
```

---

## 六、决策请求

请 SE 团队确认以下决策点：

| 决策点 | 选项 | 建议 |
|--------|------|------|
| 版本统一 | 2.3.1 | ✅ 确认 |
| 字段映射规则 | 自动生成/手动配置 | 自动生成为主，手动复核 |
| 执行顺序 | 按计划/调整 | ✅ 确认按计划 |
| businessCategory分配 | 按规则/自定义 | ✅ 按规则自动分配 |

---

## 七、待执行任务清单

### 7.1 任务清单 (Skill Index v2.3.1)

| ID | 任务 | 优先级 | 状态 | 工作量 |
|----|------|:------:|:----:|:------:|
| T01 | 版本统一到2.3.1 | P0 | ⏳ 待执行 | 0.5h |
| T02 | 创建config/schema.yaml | P0 | ⏳ 待执行 | 2h |
| T03 | 创建config/addresses.yaml | P0 | ⏳ 待执行 | 3h |
| T04 | 创建config/categories.yaml | P0 | ⏳ 待执行 | 2h |
| T05 | 新增businessCategory字段 | P0 | ⏳ 待执行 | 4h |
| T06 | 新增sceneType字段 | P0 | ⏳ 待执行 | 2h |
| T07 | 新增capabilityAddresses字段 | P0 | ⏳ 待执行 | 3h |
| T08 | 新增category字段(SE标准) | P1 | ⏳ 待执行 | 2h |
| T09 | 新增roles字段 | P1 | ⏳ 待执行 | 2h |
| T10 | 全面验证与测试 | P0 | ⏳ 待执行 | 3h |

### 7.2 已完成任务

| ID | 任务 | 状态 | 完成时间 |
|----|------|:----:|----------|
| T-01 | skillId → id 重命名 | ✅ 已完成 | 2026-03-11 |
| T-02 | skillType → skillForm 重命名 | ✅ 已完成 | 2026-03-11 |
| T-03 | category → capabilityCategory 重命名 | ✅ 已完成 | 2026-03-11 |

---

## 八、附录

### 8.1 相关文档

| 文档 | 路径 |
|------|------|
| SE强制执行标准 | `E:\github\ooder-sdk\scene-engine\docs\SKILL_CLASSIFICATION_ENFORCEMENT_STANDARD.md` |
| Phase 1基础层配置 | `E:\github\ooder-sdk\scene-engine\docs\PHASE1_BASE_LAYER_CONFIG.md` |
| 分层重构方案 | `E:\github\ooder-sdk\scene-engine\docs\SKILL_INDEX_REFACTORING_PROPOSAL.md` |
| 字段检查报告 | `E:\github\ooder-skills\skills\skill-scene\docs\SKILL_INDEX_FIELD_AUDIT_REPORT.md` |

### 8.2 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2026-03-11 | 初始版本 |

---

**文档状态**: 待评审  
**下一步**: 等待 SE 团队确认后执行
