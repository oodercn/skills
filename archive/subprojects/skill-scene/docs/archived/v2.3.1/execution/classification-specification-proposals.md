# 场景技能分类规范方案

## 一、字段使用情况分析

### 1.1 核心字段使用统计

| 字段 | 使用次数 | 是否可删除 | 说明 |
|------|:--------:|:----------:|------|
| `category` | 1235 | ❌ 不可删除 | 技能分类核心字段 |
| `participants` | 495 | ❌ 不可删除 | 协作模型核心字段 |
| `visibility` | 334 | ❌ 不可删除 | 访问控制核心字段 |
| `driverConditions` | 261 | ❌ 不可删除 | 自驱能力判定字段 |
| `mainFirstConfig` | 128 | ❌ 不可删除 | 自驱能力配置字段 |
| `skillType` | 103 | ⚠️ 需评估 | 主要在 temp 目录 |
| `sceneSkill` | 54 | ❌ 不可删除 | 场景技能标识字段 |
| `businessTags` | 52 | ❌ 不可删除 | 业务语义评分字段 |
| `type` (兼容) | 43 | ⚠️ 可废弃 | 向后兼容字段 |

### 1.2 冗余分析

| 冗余点 | 问题 | 影响 |
|--------|------|------|
| `sceneSkill` vs `type` | 两者功能重叠 | 代码需要兼容处理 |
| `category` 误用 | abs/tbs/ass 被当作分类 | 概念混淆 |
| `skillType` 定义不清 | 与 sceneSkill 功能重叠 | 开发者困惑 |
| `tags` vs `businessTags` | 两个字段功能相似 | 需要兼容处理 |

---

## 二、规范方案

### 方案一：最小化方案 (推荐)

**原则**: 保留核心字段，废弃冗余字段，统一命名

```yaml
# 技能定义规范 (v2.3)
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-document-assistant
  name: 智能文档助手
  version: 2.3
  category: knowledge           # 技能分类 (必填)
  
spec:
  type: scene-skill             # 技能类型 (必填): scene-skill | service-skill | provider-skill
  
  # 场景技能属性 (仅 scene-skill 需要)
  sceneSkill: true              # 冗余但保留，便于快速判断
  skillType: abs                # 场景技能类型 (自动计算): abs | ass | tbs
  
  # 自驱能力配置
  mainFirst: true
  mainFirstConfig:              # 自驱配置
    autoStart: true
    delay: 5s
  driverConditions:             # 驱动条件 (评分+3)
    - type: user-query
      
  # 业务语义配置
  participants:                 # 参与者 (评分+3)
    - role: EMPLOYEE
  visibility: public            # 可见性 (评分+2): public | private | internal
  businessTags:                 # 业务标签 (评分+1)
    - document
    - assistant
```

**字段规范**:

| 字段 | 必填 | 说明 | 废弃 |
|------|:----:|------|:----:|
| `category` | ✅ | 技能分类 | - |
| `spec.type` | ✅ | 技能类型 | - |
| `sceneSkill` | ⚠️ | 快速标识，与 type 配合 | 可选 |
| `skillType` | ❌ | 自动计算，可手动覆盖 | - |
| `mainFirst` | ❌ | 自驱标志 | - |
| `mainFirstConfig` | ❌ | 自驱配置 | - |
| `driverConditions` | ❌ | 驱动条件 | - |
| `participants` | ❌ | 参与者 | - |
| `visibility` | ❌ | 可见性 | - |
| `businessTags` | ❌ | 业务标签 | - |
| `tags` | ❌ | - | ✅ 废弃，用 businessTags |

**代码修改**:
```java
// 判断场景技能 (简化)
public boolean isSceneSkill(Map<String, Object> metadata) {
    // 优先级1: sceneSkill 字段
    if (Boolean.TRUE.equals(metadata.get("sceneSkill"))) {
        return true;
    }
    // 优先级2: type 字段
    Object type = metadata.get("type");
    return "scene-skill".equals(type);
}

// 废弃 tags 字段，统一使用 businessTags
public List<String> getBusinessTags(Map<String, Object> metadata) {
    List<String> tags = (List<String>) metadata.get("businessTags");
    if (tags == null || tags.isEmpty()) {
        tags = (List<String>) metadata.get("tags");  // 兼容旧字段
    }
    return tags != null ? tags : Collections.emptyList();
}
```

**优点**: 
- 保留所有核心功能
- 兼容现有代码
- 废弃冗余字段

**缺点**:
- sceneSkill 和 type 仍有重叠

---

### 方案二：极简方案

**原则**: 完全消除冗余，只保留必要字段

```yaml
# 技能定义规范 (v2.3 极简版)
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-document-assistant
  name: 智能文档助手
  version: 2.3
  category: knowledge           # 技能分类 (必填)
  
spec:
  type: scene-skill             # 技能类型 (必填)
  
  # 自驱能力配置 (仅 scene-skill)
  mainFirst: true
  mainFirstConfig: {...}
  driverConditions: [...]
  
  # 业务语义配置 (仅 scene-skill)
  participants: [...]
  visibility: public
  businessTags: [...]
```

**字段规范**:

| 字段 | 必填 | 说明 | 废弃 |
|------|:----:|------|:----:|
| `category` | ✅ | 技能分类 | - |
| `spec.type` | ✅ | 技能类型，scene-skill 自动识别 | - |
| `sceneSkill` | ❌ | - | ✅ 废弃，用 type 判断 |
| `skillType` | ❌ | 自动计算，不存储 | - |
| `tags` | ❌ | - | ✅ 废弃，用 businessTags |

**代码修改**:
```java
// 判断场景技能 (极简)
public boolean isSceneSkill(Map<String, Object> metadata) {
    Object type = metadata.get("type");
    return "scene-skill".equals(type);
}

// skillType 完全自动计算，不存储
public SceneSkillCategory calculateSkillType(SkillPackage skillPackage) {
    if (!isSceneSkill(skillPackage)) {
        return NOT_SCENE_SKILL;
    }
    boolean hasSelfDrive = checkSelfDrive(skillPackage);
    int score = calculateScore(skillPackage);
    return determineCategory(hasSelfDrive, score);
}
```

**优点**:
- 完全消除冗余
- 代码简洁

**缺点**:
- 需要大量代码修改
- 不兼容旧数据
- type 字段语义不够明确

---

### 方案三：分离方案

**原则**: 场景技能独立配置，与普通技能完全分离

```yaml
# 普通技能定义
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-knowledge-base
  name: 知识库服务
  version: 2.3
  category: knowledge           # 技能分类
  
spec:
  type: service-skill           # 服务技能
  capabilities: [...]           # 能力列表

---
# 场景技能定义 (独立 Kind)
apiVersion: skill.ooder.net/v1
kind: SceneSkill

metadata:
  id: skill-document-assistant
  name: 智能文档助手
  version: 2.3
  category: knowledge           # 技能分类
  
spec:
  skillType: abs                # 场景技能类型 (自动计算)
  
  # 自驱能力
  selfDrive:
    enabled: true
    config: {...}
    conditions: [...]
  
  # 业务语义
  participants: [...]
  visibility: public
  businessTags: [...]
```

**字段规范**:

| 字段 | 必填 | 说明 |
|------|:----:|------|
| `kind` | ✅ | Skill 或 SceneSkill |
| `category` | ✅ | 技能分类 |
| `skillType` | ❌ | 自动计算 |
| `selfDrive.enabled` | ❌ | 自驱标志 |
| `selfDrive.config` | ❌ | 自驱配置 |
| `selfDrive.conditions` | ❌ | 驱动条件 |

**代码修改**:
```java
// 根据 Kind 判断
public boolean isSceneSkill(SkillDocument doc) {
    return "SceneSkill".equals(doc.getKind());
}

// 场景技能独立模型
public class SceneSkill extends Skill {
    private SceneSkillType skillType;
    private SelfDriveConfig selfDrive;
    private List<Participant> participants;
    private Visibility visibility;
    private List<String> businessTags;
}
```

**优点**:
- 概念清晰，完全分离
- 类型安全
- 易于扩展

**缺点**:
- 需要重构大量代码
- 不兼容现有数据结构
- 增加复杂度

---

## 三、方案对比

| 维度 | 方案一 (最小化) | 方案二 (极简) | 方案三 (分离) |
|------|:---------------:|:-------------:|:-------------:|
| **兼容性** | ✅ 高 | ⚠️ 中 | ❌ 低 |
| **简洁性** | ⚠️ 中 | ✅ 高 | ❌ 低 |
| **扩展性** | ⚠️ 中 | ⚠️ 中 | ✅ 高 |
| **改动量** | ✅ 小 | ⚠️ 中 | ❌ 大 |
| **风险** | ✅ 低 | ⚠️ 中 | ❌ 高 |

---

## 四、推荐方案

**推荐：方案一 (最小化方案)**

### 4.1 实施步骤

| 阶段 | 任务 | 工作量 |
|------|------|:------:|
| **Phase 1** | 修复 skill-index.yaml 中的 category 误用 | 1天 |
| **Phase 2** | 添加 skillType 字段，实现自动计算 | 2天 |
| **Phase 3** | 废弃 tags 字段，统一使用 businessTags | 1天 |
| **Phase 4** | 更新文档和规范 | 1天 |

### 4.2 废弃字段处理

| 字段 | 处理方式 | 时间线 |
|------|----------|--------|
| `tags` | 标记废弃，兼容读取 | v2.3 废弃，v2.4 移除 |
| `sceneSkill` | 保留但可选 | 长期保留 |

### 4.3 代码修改清单

| 文件 | 修改内容 | 风险 |
|------|----------|:----:|
| `skill-index.yaml` | 修复 category 误用 | 低 |
| `MetadataCompat.java` | 添加 skillType 计算 | 低 |
| `SceneSkillClassifierImpl.java` | 废弃 tags 兼容 | 低 |
| `skill.yaml` 模板 | 更新字段规范 | 低 |

---

## 五、删除不影响功能的字段

### 5.1 可安全删除

| 字段 | 原因 | 验证方法 |
|------|------|----------|
| `tags` (用 businessTags) | 功能重复 | 代码有兼容处理 |
| `skillType` (temp目录) | 历史遗留 | 不在主代码路径 |

### 5.2 不可删除

| 字段 | 原因 |
|------|------|
| `sceneSkill` | 核心标识，代码依赖 |
| `category` | 分类核心，大量使用 |
| `mainFirstConfig` | 自驱能力配置 |
| `driverConditions` | 自驱能力判定 |
| `participants` | 协作模型核心 |
| `visibility` | 访问控制核心 |
| `businessTags` | 业务语义评分 |

---

## 六、最终规范

```yaml
# skill.yaml 规范 v2.3
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: string                    # 必填：技能ID
  name: string                  # 必填：技能名称
  version: string               # 必填：版本号
  category: string              # 必填：技能分类
  description: string           # 可选：描述
  
spec:
  type: enum                    # 必填：scene-skill | service-skill | provider-skill
  
  # === 场景技能属性 (仅 type=scene-skill) ===
  skillType: enum               # 自动计算：abs | ass | tbs (可手动覆盖)
  
  # 自驱能力
  mainFirst: boolean            # 自驱标志
  mainFirstConfig: object       # 自驱配置
  driverConditions: array       # 驱动条件 (评分+3)
  
  # 业务语义
  participants: array           # 参与者 (评分+3)
  visibility: enum              # 可见性 (评分+2): public | private | internal
  businessTags: array           # 业务标签 (评分+1)
```

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
