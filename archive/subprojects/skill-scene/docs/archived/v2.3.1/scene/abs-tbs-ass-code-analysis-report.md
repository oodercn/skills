# ABS/TBS/ASS 代码设计与分类使用对比分析报告

## 一、代码设计分析

### 1.1 场景技能类型定义 (SceneSkillCategory)

**代码位置**: [SceneSkillCategory.java](file:///e:/github/ooder-skills/skills/skill-scene/src/main/java/net/ooder/skill/scene/capability/model/SceneSkillCategory.java)

```java
@Dict(code = "scene_skill_category", name = "场景技能分类", description = "场景技能三大分类体系 v2.3.1")
public enum SceneSkillCategory {
    ABS("ABS", "自驱业务场景", "Auto Business Scene - hasSelfDrive=true + score>=8"),
    ASS("ASS", "自驱系统场景", "Auto System Scene - hasSelfDrive=true + score<8"),
    TBS("TBS", "触发业务场景", "Trigger Business Scene - hasSelfDrive=false + score>=8"),
    NOT_SCENE_SKILL("NOT_SCENE_SKILL", "非场景技能", "不满足场景技能基本标准");
}
```

### 1.2 分类判定逻辑

**代码位置**: [SceneSkillCategoryDetector.java](file:///e:/github/ooder-skills/skills/skill-scene/src/main/java/net/ooder/skill/scene/capability/service/SceneSkillCategoryDetector.java)

```java
private SceneSkillCategory determineCategory(boolean hasSelfDrive, boolean hasHighBusinessSemantics, int score) {
    if (hasSelfDrive && hasHighBusinessSemantics) {
        return SceneSkillCategory.ABS;      // 自驱 + 高业务语义
    } else if (hasSelfDrive && !hasHighBusinessSemantics) {
        return SceneSkillCategory.ASS;      // 自驱 + 低业务语义
    } else if (!hasSelfDrive && hasHighBusinessSemantics) {
        return SceneSkillCategory.TBS;      // 非自驱 + 高业务语义
    } else {
        return SceneSkillCategory.NOT_SCENE_SKILL;
    }
}
```

### 1.3 自驱能力判定条件

```java
private boolean hasSelfDriveCapability(Capability capability) {
    boolean mainFirst = capability.isMainFirst();
    boolean hasMainFirstConfig = capability.getMainFirstConfig() != null 
        && !capability.getMainFirstConfig().isEmpty();
    boolean hasDriverConditions = capability.getDriverConditions() != null 
        && !capability.getDriverConditions().isEmpty();
    
    return mainFirst && hasMainFirstConfig && hasDriverConditions;  // 三者必须同时满足
}
```

### 1.4 业务语义评分标准

| 评分项 | 分值 | 检查条件 |
|--------|:----:|----------|
| driverConditions | 3 | 驱动条件非空 |
| participants | 3 | 参与者非空 |
| visibility=public | 2 | 公开可见 |
| collaboration | 1 | 有协作能力 |
| businessTags | 1 | 有业务标签 |
| **满分** | **10** | - |
| **高业务语义阈值** | **≥8** | - |

---

## 二、skill-index.yaml 使用现状

### 2.1 错误使用示例

```yaml
# ❌ 错误：ABS/TBS/ASS 被当作 category 使用
- skillId: skill-document-assistant
  category: abs              # 错误！ABS 是场景技能类型，不是分类
  subCategory: knowledge

- skillId: skill-meeting-minutes
  category: tbs              # 错误！TBS 是场景技能类型，不是分类
  subCategory: knowledge

- skillId: skill-knowledge-share
  category: ass              # 错误！ASS 是场景技能类型，不是分类
```

### 2.2 正确使用示例

```yaml
# ✅ 正确：分类与场景技能类型分离
- skillId: skill-document-assistant
  category: knowledge        # 正确：知识服务分类
  skillType: abs             # 正确：场景技能类型
  sceneType: knowledge-qa    # 正确：场景类型
```

---

## 三、逐项对比分析

### 3.1 概念对比

| 概念 | 代码设计 | skill-index.yaml 使用 | 状态 |
|------|----------|----------------------|:----:|
| **分类 (category)** | 技能库组织分类，如 `org`, `vfs`, `llm`, `knowledge` | 被错误用于 `abs`, `tbs`, `ass` | ❌ |
| **场景技能类型 (skillType)** | `SceneSkillCategory` 枚举，ABS/ASS/TBS | 未使用独立字段 | ⚠️ |
| **场景类型 (sceneType)** | 场景模板ID，如 `knowledge-qa` | 部分使用 `sceneId` | ⚠️ |

### 3.2 分类逻辑对比

| 维度 | 代码设计 | skill-index.yaml | 差异 |
|------|----------|------------------|------|
| **自驱能力判断** | `mainFirst && mainFirstConfig && driverConditions` | 无此逻辑 | 代码有完整判断 |
| **业务语义评分** | 10分制，阈值8分 | 无此逻辑 | 代码有完整评分 |
| **分类结果** | 自动计算 ABS/ASS/TBS | 手动指定 | 应该自动计算 |

### 3.3 数据结构对比

**代码设计中的数据结构**:
```java
public class Capability {
    private boolean mainFirst;           // 自驱标志
    private MainFirstConfig mainFirstConfig;  // 自驱配置
    private List<DriverCondition> driverConditions;  // 驱动条件
    private List<String> participants;   // 参与者
    private String visibility;           // 可见性
    private List<String> businessTags;   // 业务标签
    // ...
}
```

**skill-index.yaml 中的数据结构**:
```yaml
- skillId: skill-document-assistant
  category: abs              # ❌ 应该是 skillType
  subCategory: knowledge     # ⚠️ 应该是 category
  tags: [...]                # ✅ 有 tags
  mainFirst: true            # ✅ 有 mainFirst
  # ❌ 缺少 mainFirstConfig
  # ❌ 缺少 driverConditions
  # ❌ 缺少 participants
  # ❌ 缺少 visibility
```

---

## 四、闭环度检查

### 4.1 数据闭环检查

| 检查项 | 代码支持 | 数据提供 | 闭环状态 |
|--------|:--------:|:--------:|:--------:|
| mainFirst 字段 | ✅ | ✅ | ✅ 闭环 |
| mainFirstConfig 字段 | ✅ | ❌ | ❌ 不闭环 |
| driverConditions 字段 | ✅ | ❌ | ❌ 不闭环 |
| participants 字段 | ✅ | ❌ | ❌ 不闭环 |
| visibility 字段 | ✅ | ❌ | ❌ 不闭环 |
| businessTags 字段 | ✅ | ⚠️ (用tags) | ⚠️ 部分闭环 |

**闭环度**: 1/6 完全闭环 = **16.7%**

### 4.2 流程闭环检查

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        流程闭环检查                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   skill-index.yaml                    SceneSkillCategoryDetector             │
│   ┌─────────────────────┐            ┌─────────────────────────┐            │
│   │ category: abs       │ ──读取──→  │ detectCategory()        │            │
│   │ mainFirst: true     │            │   ├─ hasSelfDrive()     │            │
│   │ tags: [...]         │            │   ├─ calculateScore()   │            │
│   └─────────────────────┘            │   └─ determineCategory()│            │
│            │                         └─────────────────────────┘            │
│            │                                    │                            │
│            ▼                                    ▼                            │
│   ┌─────────────────────┐            ┌─────────────────────────┐            │
│   │ 问题：               │            │ 问题：                   │            │
│   │ 1. category被误用    │            │ 1. 缺少必要字段无法计算   │            │
│   │ 2. 缺少计算所需字段   │            │ 2. 分类结果可能不准确     │            │
│   └─────────────────────┘            └─────────────────────────┘            │
│                                                                             │
│   闭环状态: ❌ 不闭环                                                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.3 代码符合度检查

| 检查项 | 代码要求 | 实际数据 | 符合度 |
|--------|----------|----------|:------:|
| **字段命名** | `category` 应为分类 | 被用于场景技能类型 | 0% |
| **字段完整性** | 需要6个字段计算分类 | 仅提供2个字段 | 33% |
| **分类逻辑** | 自动计算 | 手动指定 | 0% |
| **数据类型** | 特定类型 | 类型不匹配 | 50% |
| **总体符合度** | - | - | **20%** |

---

## 五、问题汇总

### 5.1 严重问题 (P0)

| 问题 | 描述 | 影响 | 修复建议 |
|------|------|------|----------|
| **概念混淆** | ABS/TBS/ASS 被当作 category 使用 | 分类体系混乱 | 分离 category 和 skillType |
| **字段缺失** | 缺少 mainFirstConfig, driverConditions 等字段 | 无法自动计算分类 | 补充必要字段 |
| **逻辑断裂** | 代码有自动分类逻辑，但数据不支持 | 功能无法使用 | 完善数据结构 |

### 5.2 中等问题 (P1)

| 问题 | 描述 | 影响 | 修复建议 |
|------|------|------|----------|
| **字段命名不一致** | tags vs businessTags | 代码兼容处理 | 统一使用 businessTags |
| **分类结果不可信** | 手动指定可能不符合计算结果 | 分类不准确 | 使用自动计算 |

### 5.3 低优先级问题 (P2)

| 问题 | 描述 | 影响 | 修复建议 |
|------|------|------|----------|
| **文档缺失** | 缺少分类计算说明 | 理解困难 | 补充文档 |

---

## 六、修复方案

### 6.1 数据结构修复

**修复前**:
```yaml
- skillId: skill-document-assistant
  category: abs              # ❌ 错误
  subCategory: knowledge
  tags: [...]
  mainFirst: true
```

**修复后**:
```yaml
- skillId: skill-document-assistant
  category: knowledge        # ✅ 正确分类
  skillType: abs             # ✅ 场景技能类型
  sceneType: knowledge-qa    # ✅ 场景类型
  
  # 自驱能力配置
  mainFirst: true
  mainFirstConfig:
    autoStart: true
    delay: 5s
  driverConditions:
    - type: schedule
      cron: "0 9 * * *"
  
  # 业务语义配置
  participants:
    - role: EMPLOYEE
      required: true
  visibility: public
  businessTags:
    - document
    - assistant
    - rag
```

### 6.2 分类定义修复

**修复前** (skill-index.yaml):
```yaml
categories:
  - id: abs    # ❌ 不应该存在
  - id: tbs    # ❌ 不应该存在
  - id: ass    # ❌ 不应该存在
```

**修复后**:
```yaml
categories:
  - id: org
  - id: vfs
  - id: llm
  - id: knowledge
  - id: sys
  - id: msg
  - id: ui
  - id: payment
  - id: media
  - id: util
  - id: nexus-ui

# 新增场景技能类型定义
skillTypes:
  - id: abs
    name: 自驱业务场景
    description: hasSelfDrive=true + score>=8
  - id: ass
    name: 自驱系统场景
    description: hasSelfDrive=true + score<8
  - id: tbs
    name: 触发业务场景
    description: hasSelfDrive=false + score>=8
```

### 6.3 自动分类流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        自动分类流程                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   1. 数据输入                                                                │
│      ├── mainFirst: boolean                                                 │
│      ├── mainFirstConfig: Map                                               │
│      ├── driverConditions: List                                             │
│      ├── participants: List                                                 │
│      ├── visibility: String                                                 │
│      └── businessTags: List                                                 │
│                                                                             │
│   2. 自驱能力计算                                                            │
│      hasSelfDrive = mainFirst && mainFirstConfig非空 && driverConditions非空 │
│                                                                             │
│   3. 业务语义评分                                                            │
│      score = driverConditions(3) + participants(3) + visibility(2)          │
│            + collaboration(1) + businessTags(1)                             │
│                                                                             │
│   4. 分类判定                                                                │
│      if (hasSelfDrive && score >= 8) → ABS                                  │
│      if (hasSelfDrive && score < 8)  → ASS                                  │
│      if (!hasSelfDrive && score >= 8) → TBS                                 │
│      else → NOT_SCENE_SKILL                                                 │
│                                                                             │
│   5. 结果输出                                                                │
│      skillType: ABS | ASS | TBS | NOT_SCENE_SKILL                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、闭环检测结论

### 7.1 总体评估

| 检测维度 | 完整度 | 状态 |
|----------|:------:|:----:|
| 数据闭环 | 16.7% | ❌ 严重不足 |
| 流程闭环 | 20% | ❌ 严重不足 |
| 代码符合度 | 20% | ❌ 严重不足 |
| 概念一致性 | 0% | ❌ 完全不一致 |

### 7.2 修复优先级

| 优先级 | 修复项 | 工作量 |
|:------:|--------|:------:|
| P0 | 分离 category 和 skillType | 2天 |
| P0 | 补充必要字段 | 3天 |
| P1 | 实现自动分类计算 | 2天 |
| P2 | 完善文档 | 1天 |

### 7.3 风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 数据迁移 | 现有数据需要转换 | 编写迁移脚本 |
| 兼容性 | 旧代码可能依赖错误字段 | 保留兼容层 |
| 测试覆盖 | 需要验证分类逻辑 | 增加单元测试 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
