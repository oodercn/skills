# Skill.yaml 定义规范摘要

## 问题背景

my-capabilities.html 页面分类统计显示为 0，总能力数 167 正确。

**根本原因**: `skill.yaml` 中的 `spec.capability.category` 从未被代码读取！

---

## Skill.yaml 结构规范

### 标准结构

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-common                    # Skill ID
  name: 通用工具库                     # Skill 名称
  version: 2.3.1                      # 版本
  description: 通用工具库描述          # 描述
  author: ooder Team                  # 作者
  type: system-service                # 类型
  license: Apache-2.0                 # 许可证
  homepage: https://...               # 主页
  keywords:                           # 关键词
    - common
    - auth
    - org

spec:
  type: service-skill                 # Spec 类型
  
  ownership: platform                 # 所有权
  
  # ================================
  # 【关键】能力地址和分类定义
  # ================================
  capability:
    address: 0x01                     # 能力地址
    category: SYS                     # ← 【问题】这个值从未被读取！
    code: SYS_COMMON                  # 能力代码
    operations: [auth, org, config, storage]
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot

  llmConfig:
    required: false
    defaultProvider: "deepseek"
    defaultModel: "deepseek-chat"
    capabilities:
      - chat
      - streaming
      - function-calling
```

### 另一种结构（capabilities 数组）

```yaml
spec:
  capabilities:
    - id: skill-registration
      name: Skill Registration
      description: 技能注册管理
      category: management          # ← 在数组项中定义
    - id: skill-lifecycle
      name: Skill Lifecycle
      description: 技能生命周期管理
      category: management
```

---

## Category 字段位置总结

| 位置 | 路径 | 示例值 | 读取状态 |
|------|------|--------|----------|
| 技能级 capability | `spec.capability.category` | `SYS`, `VFS` | ❌ **未被读取** |
| 能力级 capabilities | `spec.capabilities[].category` | `management`, `service`, `storage` | ✅ 已读取（entry files） |

**关键问题**: 代码只读取了 `spec.capabilities[].category`，但 **从未读取 `spec.capability.category`**！

---

## 代码层面的问题

### SkillIndexLoader.java

当前代码（来源 1 - index 目录）：
```java
Object categoryObj = skill.get("category");  // 从 index/skills/*.yaml 读取
if (categoryObj != null) {
    cap.setCategory(String.valueOf(categoryObj));
}
```

当前代码（来源 2 - entry files）：
```java
String category = (String) spec.get("category");  // 从 skill-index-entry.yaml 读取
cap.setCategory(category);
```

**缺失的代码**（需要从实际 skill.yaml 读取）：
```java
// 需要从 skill.yaml 的 spec.capability.category 读取
Map<String, Object> capability = (Map<String, Object>) spec.get("capability");
if (capability != null) {
    String category = (String) capability.get("category");
    if (category != null) {
        cap.setCategory(category);
    }
}
```

---

## 修复方案

### 方案 1：修改 SkillIndexLoader（推荐）

**修改文件**: `SkillIndexLoader.java`

**修改内容**: 在 `getSkillsFromEntryFiles` 方法中，添加从实际 `skill.yaml` 文件读取 `spec.capability.category` 的逻辑。

**实现思路**:
1. 从 `skill-index-entry.yaml` 获取对应的 `skill.yaml` 路径
2. 读取 `skill.yaml` 文件
3. 提取 `spec.capability.category`
4. 设置到 `CapabilityDTO`

### 方案 2：修改 index 生成逻辑

**修改文件**: 生成 `skill-index-entry.yaml` 的工具/脚本

**修改内容**: 在生成 entry 文件时，从 `skill.yaml` 提取 `spec.capability.category` 并写入 entry 文件。

### 方案 3：完善 CODE_MAPPING

**修改文件**: `CapabilityCategory.java`

**添加缺失映射**:
- `management` → `util` (或合适的分类)
- `storage` → `vfs`
- `SYS`/`VFS`（大写）→ 确保大小写不敏感

---

## 测试验证

修复后需要验证：

1. **API 返回数据**: `category` 字段不再为 null
2. **前端统计显示**: 各分类统计数正确显示（不再为 0）
3. **分类映射正确**: 如 `SYS` → `sys`, `VFS` → `vfs`, `service` → `util` 等

---

**文档版本**: v1.0  
**创建日期**: 2026-03-18
