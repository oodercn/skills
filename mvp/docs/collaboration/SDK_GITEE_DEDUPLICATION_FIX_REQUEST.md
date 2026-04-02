# SE SDK 团队协作请求：修复 Gitee 发现功能去重逻辑问题

## 一、问题描述

### 1.1 现象
Gitee 发现功能返回的技能中，部分场景技能（如 `skill-recruitment-management`、`skill-approval-form`、`skill-real-estate-form` 等）的 `skillForm` 字段为 `PROVIDER`，而非正确的 `SCENE`。

### 1.2 问题根因
**Gitee 仓库中存在同一技能的多个版本（PROVIDER 和 SCENE），而 SDK 的去重逻辑只保留了第一个遇到的版本，丢弃了后续版本。**

从 API 返回日志分析：
```
skill-recruitment-management: PROVIDER (先遇到的，保留)
skill-recruitment-management: PROVIDER (重复)
skill-recruitment-management: SCENE (后遇到的，被丢弃) ❌
```

### 1.3 影响范围
- **Gitee 发现**：102 个技能中部分技能受影响
- **本地 JAR 加载**：正常

---

## 二、问题定位

### 2.1 Gitee 仓库检查结果

**Gitee 仓库中不存在重复！**

使用脚本验证，以下技能在 Gitee 上只有一个版本，且 `skillForm: SCENE` 正确：

| 技能 ID | 路径 | skillForm |
|---------|------|-----------|
| skill-approval-form | skills/scenes/skill-approval-form/skill.yaml | SCENE ✅ |
| skill-real-estate-form | skills/scenes/skill-real-estate-form/skill.yaml | SCENE ✅ |
| skill-recruitment-management | skills/scenes/skill-recruitment-management/skill.yaml | SCENE ✅ |

**问题不在 Gitee 仓库，重复问题在于 SDK 内部！**

### 2.2 SDK 内部去重问题

既然 Gitee 仓库没有重复，问题一定在 SDK 内部。SDK 可能在以下环节产生重复：

1. **GitRepositoryDiscovererAdapter 可能被多次调用**
   - 每次调用时，从不同的目录扫描到同一技能
   - 去重时保留了先遇到的版本（PROVIDER）

2. **SkillPackage 的 skillForm 解析不一致**
   - GiteeDiscoverer 解析时未读取 `spec.skillForm`
   - 返回的 SkillPackage 的 skillForm 是默认值（PROVIDER）

### 2.3 推测的 SDK 去重代码

```java
// 可能的去重逻辑
Map<String, SkillPackage> uniqueSkills = new HashMap<>();
for (SkillPackage pkg : discoveredPackages) {
    uniqueSkills.put(pkg.getSkillId(), pkg);  // 后来的覆盖先前的
}
```

或者：

```java
// 或者先到先得
if (!uniqueSkills.containsKey(pkg.getSkillId())) {
    uniqueSkills.put(pkg.getSkillId(), pkg);
}
```

---

## 三、修复要求

### 3.1 修复目标

修改 SDK 的去重逻辑，确保当存在多个版本的同一技能时，优先保留 `skillForm` 为 `SCENE` 的版本。

### 3.2 修复优先级

当发现重复技能时，应按以下优先级保留：

| 优先级 | skillForm | 说明 |
|--------|------------|------|
| 1 | `SCENE` | 场景应用优先 |
| 2 | `DRIVER` | 驱动适配次之 |
| 3 | `PROVIDER` | 能力提供最后 |
| 4 | `null`/其他 | 无声明的使用默认值 |

### 3.3 修复示例

```java
// 修改去重逻辑
Map<String, SkillPackage> uniqueSkills = new LinkedHashMap<>();  // 保持插入顺序

for (SkillPackage pkg : discoveredPackages) {
    String skillId = pkg.getSkillId();
    SkillPackage existing = uniqueSkills.get(skillId);

    if (existing == null) {
        // 第一次遇到，直接添加
        uniqueSkills.put(skillId, pkg);
    } else {
        // 遇到重复，比较 skillForm 优先级
        String existingForm = existing.getSkillForm();
        String newForm = pkg.getSkillForm();

        if (shouldReplace(existingForm, newForm)) {
            uniqueSkills.put(skillId, pkg);
            logger.debug("Replaced {} with higher priority version: {} -> {}", skillId, existingForm, newForm);
        }
    }
}

private boolean shouldReplace(String existingForm, String newForm) {
    if (newForm == null) return false;
    if (existingForm == null) return true;

    // SCENE > DRIVER > PROVIDER
    int existingPriority = getPriority(existingForm);
    int newPriority = getPriority(newForm);

    return newPriority < existingPriority;  // 数值越小优先级越高
}

private int getPriority(String form) {
    switch (form.toUpperCase()) {
        case "SCENE": return 1;
        case "DRIVER": return 2;
        case "PROVIDER": return 3;
        default: return 4;
    }
}
```

---

## 四、日志要求

修复后，应输出以下日志：

```java
// 发现重复时
logger.info("[Deduplication] Found duplicate skill: {}, keeping higher priority version ({} > {})",
    skillId, keptForm, discardedForm);

// 无重复时
logger.debug("[Deduplication] No duplicate for skill: {}", skillId);

// 最终选择的版本
logger.info("[Deduplication] Selected {} for skill: {}", finalForm, skillId);
```

---

## 五、验证方式

修复后，Gitee 发现的技能应该：
1. 所有场景技能（skill-approval-form、skill-recruitment-management 等）的 `skillForm` 为 `SCENE`
2. 控制台输出去重相关的日志

---

## 六、相关文件

- Gitee 仓库检查：需要确认是否存在重复的技能目录
- SDK 去重逻辑可能位于：
  - `UnifiedDiscoveryServiceImpl.java`
  - `GitRepositoryDiscovererAdapter.java`
  - `GiteeDiscoverer.java`
  - `GitHubDiscoverer.java`

---

## 七、优先级

**高优先级** - 影响 Gitee 发现功能的核心业务逻辑

---

**创建时间**: 2026-04-02
**状态**: 待 SDK 团队处理
**指派**: SE SDK 团队
