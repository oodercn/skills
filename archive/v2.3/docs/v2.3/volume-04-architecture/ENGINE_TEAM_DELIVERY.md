# Engine Team 交付任务清单

> **交付日期**: 2026-03-05  
> **交付方**: Skills Team  
> **接收方**: Engine Team  
> **优先级**: P0  
> **状态**: ✅ 已完成

---

## 一、交付背景

根据 v2.3 版本文档整理和冲突与歧义讨论决策，需要 Engine Team 完成以下接口定义和功能实现。

---

## 二、核心术语变更

### 2.1 术语变更

| 原术语 | 新术语 | 英文 | 简写 |
|--------|--------|------|------|
| 场景能力 | **场景特性** | SceneCapability | SC |
| 场景技能 | 场景技能 | SceneSkill | SSK |

### 2.2 简写变更

| 原简写 | 新简写 | 全称 | 中文 |
|--------|--------|------|------|
| CC | **COMP** | CompositeCapability | 组合能力 |
| CC | **CLC** | CollaborativeCapability | 协作能力 |

### 2.3 类型区分

| 类型 | 说明 | 示例 |
|------|------|------|
| **DRIVER** | 外部触发源 | intent-receiver, scheduler, event-listener |
| **EXECUTOR** | 内部执行器 | capability-invoker, collaboration-coordinator |

---

## 三、待实现任务

### 3.1 P0 任务（阻塞 Skills Team）

#### 任务1：定义 SceneSkillCategory 枚举

**需求描述**：定义场景技能分类枚举，用于分类检测

**枚举定义**：
```java
public enum SceneSkillCategory {
    ABS,        // Auto Business Scene - 自驱业务场景
    ASS,        // Auto System Scene - 自驱系统场景
    TBS,        // Trigger Business Scene - 触发业务场景
    PENDING,    // 待定（需人工判定）
    INVALID,    // 不符合任何分类
    NOT_SCENE_SKILL  // 非场景技能
}
```

**预期交付物**：
- `SceneSkillCategory.java` 枚举类
- 包路径：`net.ooder.scene.skill.model`

---

#### 任务2：实现 detectCategory() 方法

**需求描述**：根据四项标准检测场景技能分类

**方法签名**：
```java
public interface SceneSkillClassifier {
    /**
     * 检测场景技能分类
     * @param skillPackage 技能包
     * @return 场景技能分类
     */
    SceneSkillCategory detectCategory(SkillPackage skillPackage);
    
    /**
     * 计算业务语义评分
     * @param capability 场景特性
     * @return 评分（0-10）
     */
    int calculateBusinessSemanticsScore(SceneCapability capability);
}
```

**评分标准**：
| 评分项 | 分值 | 判定条件 |
|--------|------|---------|
| driverConditions 非空 | 3分 | 有驱动条件定义 |
| participants 非空 | 3分 | 有参与者定义 |
| visibility = public | 2分 | 对外可见 |
| 有协作能力 | 1分 | collaborativeCapabilities 非空 |
| 有业务标签 | 1分 | labels.scene.category 存在 |

**分类阈值**：
| 总分 | mainFirst=true | mainFirst=false |
|------|----------------|-----------------|
| ≥ 8分 | ABS | TBS |
| 3-7分 | PENDING | PENDING |
| < 3分 | ASS | INVALID |

**预期交付物**：
- `SceneSkillClassifier.java` 接口
- `SceneSkillClassifierImpl.java` 实现类
- 单元测试

---

### 3.2 P1 任务（非阻塞）

#### 任务3：增加 DRIVER/EXECUTOR 类型区分

**需求描述**：区分驱动能力和执行器

**枚举定义**：
```java
public enum CapabilitySubType {
    DRIVER,     // 外部触发源
    EXECUTOR    // 内部执行器
}
```

**驱动能力列表**：
- intent-receiver
- scheduler
- event-listener

**执行器列表**：
- capability-invoker
- collaboration-coordinator

**预期交付物**：
- `CapabilitySubType.java` 枚举类
- 更新 `DriverCapability.java` 添加 subType 字段

---

#### 任务4：实现 WAITING 子状态

**需求描述**：为 TBS（触发业务场景）增加 WAITING 子状态

**子状态定义**：
```java
public enum WaitingSubState {
    WAITING_APPROVAL,    // 等待审批
    WAITING_CONDITION,   // 等待条件满足
    WAITING_RESOURCE,    // 等待资源可用
    WAITING_SCHEDULE     // 等待指定时间
}
```

**预期交付物**：
- `WaitingSubState.java` 枚举类
- 更新场景状态管理逻辑

---

## 四、API 变更需求

### 4.1 发现 API 增强

**当前 API**：
```
GET /api/v1/capabilities/discover
```

**新增参数**：
| 参数 | 类型 | 说明 |
|------|------|------|
| category | string | 场景技能分类（ABS/ASS/TBS） |
| mainFirst | boolean | 是否自驱 |
| visibility | string | 可见性（public/internal） |

**响应变更**：
```json
{
  "sceneCapabilities": [
    {
      "capabilityId": "scene-daily-report",
      "name": "日志汇报场景特性",
      "type": "SCENE",
      "category": "ABS",
      "mainFirst": true,
      "businessSemanticsScore": 9
    }
  ]
}
```

---

## 五、数据模型变更

### 5.1 Capability 模型增强

**新增字段**：
```java
public class Capability {
    // 现有字段...
    
    // 新增字段
    private SceneSkillCategory category;        // 场景技能分类
    private Integer businessSemanticsScore;     // 业务语义评分
    private CapabilitySubType subType;          // 子类型（DRIVER/EXECUTOR）
    private String visibility;                  // 可见性（public/internal）
}
```

---

## 六、验收标准

### 6.1 P0 任务验收

| 验收项 | 标准 |
|--------|------|
| SceneSkillCategory 枚举 | 编译通过，包含所有分类值 |
| detectCategory() 方法 | 单元测试覆盖率 ≥ 90% |
| 分类准确性 | 10个测试用例全部通过 |

### 6.2 测试用例

| 场景特性 | mainFirst | driverConditions | participants | 预期分类 |
|---------|-----------|-----------------|--------------|---------|
| 日志汇报 | true | 有 | 有 | ABS |
| 系统清理 | true | 无 | 无 | ASS |
| 审批流程 | false | 有 | 有 | TBS |
| 缓存刷新 | true | 无 | 无 | ASS |
| 智能客服 | true | 有 | 有 | ABS |

---

## 七、交付时间线

| 里程碑 | 交付内容 | 预期日期 |
|--------|---------|---------|
| M1 | SceneSkillCategory 枚举 | Week 1 |
| M2 | detectCategory() 实现 | Week 1 |
| M3 | 单元测试 | Week 1 |
| M4 | DRIVER/EXECUTOR 区分 | Week 2 |
| M5 | WAITING 子状态 | Week 2 |

---

## 八、联系方式

**Skills Team 联系人**: [待填写]  
**Engine Team 联系人**: [待填写]

---

## 九、附件

- [术语表 v2.3](./GLOSSARY_V2.3.md)
- [冲突与歧义讨论决策记录](./CONFLICTS_AND_AMBIGUITIES.md)
- [场景特性需求规格说明书](./SCENE_CAPABILITY_REQUIREMENT_SPEC.md)

---

**交付状态**: ✅ 已完成  
**完成日期**: 2026-03-06  
**Skills Team**
