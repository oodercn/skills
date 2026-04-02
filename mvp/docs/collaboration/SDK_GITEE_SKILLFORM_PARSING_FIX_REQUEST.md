# SE SDK 团队协作请求：修复 Gitee 发现功能 spec.skillForm 解析问题

## 一、问题描述

### 1.1 现象
MVP/apex-os 项目集成 SE SDK 3.0.1 后，Gitee 发现功能返回的技能中，场景技能（如 `skill-approval-form`、`skill-recruitment-management`、`skill-real-estate-form` 等）的 `skillForm` 字段始终为 `PROVIDER`，而非正确的 `SCENE`。

### 1.2 影响范围
- **Gitee 发现**：102 个技能受影响
- **本地 JAR 加载**：正常（17 个技能）

### 1.3 根本原因

**SE SDK 的 Gitee 发现功能（GitRepositoryDiscovererAdapter / UnifiedDiscoveryServiceImpl）未正确读取 `spec.skillForm` 字段。**

Gitee 上的 skill.yaml 文件结构：
```yaml
metadata:
  id: skill-approval-form
  category: biz
  subCategory: approval

spec:
  skillForm: SCENE    # ← 正确的字段声明
  scene:
    type: INTERACTIVE
```

但 SE SDK 返回的 `SkillPackage` 中 `skillForm` 为 `PROVIDER`（默认值）。

---

## 二、问题定位

### 2.1 两套加载机制对比

| 加载方式 | 模块 | 是否正确读取 spec.skillForm |
|---------|------|---------------------------|
| 本地 JAR | skill-hotplug-starter | ✅ 正确 |
| Gitee 远程发现 | SE SDK (scene-engine) | ❌ 错误 |

### 2.2 本地修复已验证

`skill-hotplug-starter` 中的 `SkillMetadata.loadFromYaml()` 已修复，添加了以下逻辑：

```java
Map<String, Object> specData = (Map<String, Object>) data.get("spec");
if (specData != null) {
    Object specSkillForm = specData.get("skillForm");
    if (specSkillForm != null) {
        if (metadata.form == null) {
            metadata.form = (String) specSkillForm;
            System.out.println("[SkillMetadata] Loaded spec.skillForm: " + specSkillForm + " for skill: " + metadata.id);
        }
    }
}
```

本地 JAR 加载的技能日志：
```
[SkillMetadata] Loaded spec.skillForm: SCENE for skill: skill-scene ✅
[SkillMetadata] Loaded spec.skillForm: SCENE for skill: skill-template ✅
```

但 Gitee 发现的 102 个技能没有此日志。

### 2.3 SE SDK 解析链路推测

```
Gitee API (返回 JSON with Base64) 
    ↓
UnifiedDiscoveryServiceImpl.fetchSkillsFromGitee()
    ↓
parseSkillIndex() / 直接解析 YAML
    ↓
SkillPackage 对象（未包含 spec.skillForm）
    ↓
返回给 DiscoveryController
```

问题可能出在：
1. `parseSkillIndex()` 直接解析 YAML 时未读取 `spec.skillForm`
2. 或者 `SkillPackage` 的 DTO 映射未包含 `spec.skillForm` 字段

---

## 三、修复要求

### 3.1 修复目标

确保 SE SDK 的 Gitee 发现功能正确读取并返回 `spec.skillForm` 字段。

### 3.2 修复位置

`scene-engine` 模块中的以下位置之一：
- `UnifiedDiscoveryServiceImpl.java`
- `SkillPackage` 相关的 DTO 转换类
- YAML 解析逻辑

### 3.3 修复示例

参考 `skill-hotplug-starter` 的修复逻辑：

```java
// 在解析 skill.yaml 时，添加 spec.skillForm 的读取
Map<String, Object> specData = (Map<String, Object>) data.get("spec");
if (specData != null) {
    // 读取 spec.skillForm
    Object specSkillForm = specData.get("skillForm");
    if (specSkillForm != null && metadata.form == null) {
        metadata.form = (String) specSkillForm;
    }
    
    // 读取其他 spec 字段...
}
```

### 3.4 验证方式

修复后，Gitee 发现的技能应该：
1. 控制台输出 `[SkillMetadata] Loaded spec.skillForm: SCENE for skill: skill-xxx`
2. API 返回的 `skillForm` 字段为 `SCENE` 而非 `PROVIDER`

---

## 四、日志要求

建议在解析过程中添加日志：

```java
log.debug("Loaded spec.skillForm: {} for skill: {}", specSkillForm, skillId);
log.warn("Skill {} has no spec.skillForm, using fallback inference", skillId);
```

---

## 五、相关文件

- Gitee skill.yaml 示例：
  - https://gitee.com/ooderCN/skills/blob/master/skills/scenes/skill-approval-form/skill.yaml
  - https://gitee.com/ooderCN/skills/blob/master/skills/scenes/skill-real-estate-form/skill.yaml
  - https://gitee.com/ooderCN/skills/blob/master/skills/scenes/skill-recruitment-management/skill.yaml

- 本地已修复的参考实现：
  - `skill-hotplug-starter/src/main/java/net/ooder/skill/hotplug/model/SkillMetadata.java`

- MVP/apex-os 集成代码：
  - `mvp/src/main/java/net/ooder/mvp/skill/scene/controller/DiscoveryController.java`

---

## 六、优先级

**高优先级** - 影响 Gitee 发现功能的核心业务逻辑

---

**创建时间**: 2026-04-02
**状态**: 待 SDK 团队处理
**指派**: SE SDK 团队
