# Skills 分类统计问题 - 协作文档

## 📋 问题概述

**my-capabilities.html 页面分类统计显示为 0**，总能力数 167 正确，但各分类（LLM、数据库、文件存储、知识库等）统计均为 0。

---

## 🔍 问题根源分析

### 1. 数据验证

API 返回的数据示例：
```json
{
  "capabilityId": "protocol-parse",
  "name": "协议解析",
  "category": null,              ← ❌ 问题：category 为 null
  "capabilityCategory": null,    ← ❌ 问题：capabilityCategory 为 null
  ...
}
```

### 2. 代码分析

**问题 1：SkillIndexLoader 未读取 skill.yaml 中的 category**

- **当前逻辑**：`SkillIndexLoader` 从 `index/skills/*.yaml` 和 `skill-index-entry.yaml` 加载数据
- **缺失逻辑**：**从未直接读取实际 `skill.yaml` 文件中的 `spec.capability.category`**

**问题 2：skill.yaml 中 category 字段位置不统一**

```yaml
# skill-common/skill.yaml
spec:
  capability:
    category: SYS    # ← 在 spec.capability 下

# skill-management/skill.yaml  
spec:
  capabilities:
    - id: xxx
      category: management  # ← 在 spec.capabilities[] 下
```

**问题 3：CODE_MAPPING 映射不完整**

当前映射（CapabilityCategory.java）：
```java
CODE_MAPPING.put("service", "util");
CODE_MAPPING.put("knowledge", "know");
// ...
```

**缺失映射**：
- `management` → ???
- `storage` → ???
- `SYS`/`VFS`（大写）→ 需要验证是否能正确映射

---

## ✅ 已完成的修复

| 修复项 | 状态 | 说明 |
|--------|------|------|
| 前端 JavaScript 统计逻辑 | ✅ | 修复 `updateStats` 函数，使用 `categoryStatIds` 映射 |
| 前端 HTML 统计卡片 | ✅ | 添加 `util` 分类统计卡片 |
| 前端 CSS 样式 | ✅ | 添加 `.stat-icon.util` 和 `.stat-card[data-category="util"]` 样式 |
| 后端 CODE_MAPPING | ✅ | 添加 `service`→`util`, `knowledge`→`know` 等映射 |

---

## 🔧 需要协作的修复

### 方案 1（推荐）：修改 SkillIndexLoader

**修改文件**：`SkillIndexLoader.java`

**添加逻辑**：在加载数据时，直接从 skill.yaml 读取 `spec.capability.category`。

```java
// 在 getSkillsFromEntryFiles 方法中，添加以下逻辑：

// 从 entry file 获取 skill.yaml 路径
String skillYamlPath = (String) entry.get("skillYamlPath"); // 或其他方式获取
if (skillYamlPath != null) {
    File skillYamlFile = new File(skillYamlPath);
    if (skillYamlFile.exists()) {
        try (InputStream skillIs = new FileInputStream(skillYamlFile)) {
            Map<String, Object> skillData = yaml.load(skillIs);
            if (skillData != null) {
                Map<String, Object> spec = (Map<String, Object>) skillData.get("spec");
                if (spec != null) {
                    Map<String, Object> capability = (Map<String, Object>) spec.get("capability");
                    if (capability != null) {
                        String category = (String) capability.get("category");
                        if (category != null) {
                            cap.setCategory(category);
                            log.info("[getSkillsFromEntryFiles] Set category {} for skill {}", category, skillId);
                        }
                    }
                }
            }
        }
    }
}
```

### 方案 2：修改 index 生成逻辑

**修改文件**：生成 `skill-index-entry.yaml` 的工具/脚本

**修改内容**：在生成 entry 文件时，从 skill.yaml 提取 `spec.capability.category` 并写入 entry 文件。

### 方案 3：完善 CODE_MAPPING

**修改文件**：`CapabilityCategory.java`

**添加缺失映射**：

```java
static {
    // 已有映射
    CODE_MAPPING.put("msg", "comm");
    CODE_MAPPING.put("service", "util");
    CODE_MAPPING.put("knowledge", "know");
    // ...
    
    // 需要添加的映射
    CODE_MAPPING.put("management", "util");  // 或合适的分类
    CODE_MAPPING.put("storage", "vfs");     // 存储相关映射到 vfs
    CODE_MAPPING.put("sys", "sys");         // 确保大小写不敏感
    CODE_MAPPING.put("vfs", "vfs");
    // 可能还需要其他映射...
}
```

---

## 📝 测试验证

修复后，需要验证：

1. **API 返回数据**：`category` 字段不再为 null
2. **前端统计显示**：各分类统计数正确显示（不再为 0）
3. **分类映射正确**：如 `SYS` → `sys`, `VFS` → `vfs`, `service` → `util` 等

---

## 👥 协作分工

| 任务 | 负责人 | 状态 |
|------|--------|------|
| 方案 1：修改 SkillIndexLoader | SE 团队 | 待分配 |
| 方案 2：修改 index 生成逻辑 | Skills 团队 | 待分配 |
| 方案 3：完善 CODE_MAPPING | SE 团队 | 待分配 |
| 测试验证 | 双方协作 | 待分配 |

---

## 📞 联系方式

如有疑问或需要讨论，请联系：
- SE 团队：[联系方式]
- Skills 团队：[联系方式]

---

**文档版本**: v1.0  
**创建日期**: 2026-03-18  
**最后更新**: 2026-03-18
