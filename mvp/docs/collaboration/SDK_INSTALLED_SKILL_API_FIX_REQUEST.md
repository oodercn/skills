# SE SDK 团队协作请求：修复 InstalledSkill API 不一致问题

## 一、问题描述

### 1.1 现象
OS/apex-os 项目在集成 SE SDK 时，编译失败。原因是 SDK 的 `InstalledSkill` 接口缺少 `getCategory()` 和 `getSkillForm()` 方法，导致 OS 端的代码无法调用这些方法。

### 1.2 错误信息

```
错误: 找不到符号
  符号:   方法 getCategory()
  位置:   net.ooder.skills.api.InstalledSkill 类型的表达式

错误: 找不到符号
  符号:   方法 getSkillForm()
  位置:   net.ooder.skills.api.InstalledSkill 类型的表达式
```

### 1.3 影响范围
- **skills-framework** 模块
- **agent-sdk-core** 模块
- OS/apex-os 项目的过滤逻辑代码

---

## 二、问题定位

### 2.1 API 不一致分析

| 接口/类 | 方法 | 当前状态 | 期望状态 |
|---------|------|---------|---------|
| `InstalledSkill` | `getCategory()` | ❌ 不存在 | ✅ String |
| `InstalledSkill` | `getSkillForm()` | ❌ 不存在 | ✅ String |
| `InstalledSkill` | `getForm()` | ❌ 不存在 | ✅ SkillForm |
| `RichSkill` | `getCategory()` | ⚠️ 返回 SkillCategory | ✅ 应返回 String |

### 2.2 OS 端期望的 API

OS/apex-os 项目中有以下代码期望使用这些方法：

```java
// OS 端过滤逻辑示例
if (InstalledSkill.class.isAssignableFrom(skill.getClass())) {
    InstalledSkill installed = (InstalledSkill) skill;
    String category = installed.getCategory();           // 需要 getCategory()
    String skillForm = installed.getSkillForm();          // 需要 getSkillForm()
    SkillForm form = installed.getForm();                // 需要 getForm()
}

// RichSkill 期望返回 String
RichSkill richSkill = ...;
String category = richSkill.getCategory();  // 当前返回 SkillCategory，需要 String
```

### 2.3 相关 SDK 类

需要修复的类位于：
- `net.ooder.skills.api.InstalledSkill`
- `net.ooder.scene.skill.model.RichSkill`

---

## 三、修复要求

### 3.1 InstalledSkill 接口修复

**文件位置**：`skills-framework/src/main/java/net/ooder/skills/api/InstalledSkill.java`

**需要添加的方法**：

```java
/**
 * 获取技能分类
 * @return 分类字符串，如 "biz", "tool", "driver" 等
 */
String getCategory();

/**
 * 获取技能形态
 * @return 形态字符串，如 "SCENE", "PROVIDER", "DRIVER" 等
 */
String getSkillForm();

/**
 * 获取技能形态枚举
 * @return SkillForm 枚举值
 */
SkillForm getForm();
```

### 3.2 RichSkill 类修复

**文件位置**：`agent-sdk-core/src/main/java/net/ooder/scene/skill/model/RichSkill.java`

**需要修改的方法**：

```java
/**
 * 获取分类
 * @return 分类字符串，如 "biz", "tool", "driver" 等
 *
 * 当前实现返回 SkillCategory 对象，
 * 需要修改为返回 String 类型以保持 API 一致性
 */
String getCategory();  // 而不是 SkillCategory getCategory()
```

---

## 四、修复示例

### 4.1 InstalledSkill.java 修复示例

```java
package net.ooder.skills.api;

public interface InstalledSkill {
    String getSkillId();
    String getName();
    String getVersion();
    String getSource();

    /**
     * 获取技能分类
     * @return 分类字符串
     */
    String getCategory();

    /**
     * 获取技能形态
     * @return 形态字符串，如 "SCENE", "PROVIDER", "DRIVER"
     */
    String getSkillForm();

    /**
     * 获取技能形态枚举
     * @return SkillForm 枚举值
     */
    SkillForm getForm();

    // ... 其他现有方法
}
```

### 4.2 RichSkill.java 修复示例

```java
package net.ooder.scene.skill.model;

public class RichSkill {
    // 现有字段
    private String category;  // 改为 String 类型

    /**
     * 获取分类
     * @return 分类字符串
     */
    @Override
    public String getCategory() {
        return this.category;
    }

    // ... 其他现有代码
}
```

---

## 五、验证方式

修复后，OS/apex-os 项目应该能够：
1. 成功编译，无 "找不到符号" 错误
2. OS 端的过滤逻辑能够正确调用 `getCategory()` 和 `getSkillForm()` 方法
3. `RichSkill.getCategory()` 返回 String 类型

---

## 六、相关文件

- **SDK 端**：
  - `skills-framework/src/main/java/net/ooder/skills/api/InstalledSkill.java`
  - `agent-sdk-core/src/main/java/net/ooder/scene/skill/model/RichSkill.java`

- **OS 端**（等待 SDK 修复后可编译）：
  - 相关过滤逻辑代码

---

## 七、优先级

**高优先级** - 阻塞 OS/apex-os 项目的编译

---

## 八、依赖关系

此修复依赖于之前的需求文档：
1. `SDK_GITEE_SKILLFORM_PARSING_FIX_REQUEST.md` - spec.skillForm 解析修复
2. `SDK_GITEE_DEDUPLICATION_FIX_REQUEST.md` - 去重逻辑修复

建议按以下顺序修复：
1. ✅ spec.skillForm 解析（已完成）
2. ⏳ InstalledSkill API（当前文档）
3. ⏳ 去重逻辑（如需要）

---

**创建时间**: 2026-04-02
**状态**: 待 SDK 团队处理
**指派**: SE SDK 团队
