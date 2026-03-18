# 协作任务说明 - Skills 团队

## 任务概述

**提交人**: MVP Core 团队  
**提交时间**: 2026-03-17  
**优先级**: 高  
**涉及模块**: `skill-capability`

---

## 问题背景

在 MVP Core 集成测试过程中，发现 `skill-capability` 模块存在以下问题，影响能力发现和安装功能的正常使用。

---

## 问题清单

### 问题 1: 前端统计数量显示不正确

**文件**: `skills/_system/skill-capability/src/main/resources/static/console/js/pages/capability-discovery.js`

**问题描述**: 
- 能力发现页面扫描完成后，"已扫描"数量显示为固定的 100，而非实际扫描数量
- 当扫描结果为空时，也显示为 100

**影响**: 用户无法准确了解扫描进度和结果数量

**修复方案**:

```javascript
// 第 238 行修改
// 原代码:
scanStats.scanned = 100;

// 修改为:
scanStats.scanned = data.total || caps.length;

// 第 260 行修改（showEmptyResult 函数）
// 原代码:
scanStats.scanned = 100;

// 修改为:
scanStats.scanned = 0;
```

---

### 问题 2: 能力安装 API 实现不完整

**文件**: `skills/_system/skill-capability/src/main/java/net/ooder/skill/capability/controller/DiscoveryController.java`

**问题描述**:
- `/api/v1/discovery/install` 端点仅标记 skill 为已安装状态
- 未实际调用 `PluginManager` 进行 skill 安装
- 缺少依赖检查和实际安装逻辑

**影响**: 用户点击"安装"按钮后，skill 并未真正安装，页面刷新后状态丢失

**修复方案**:

```java
// 第 107-132 行修改为:

@PostMapping("/install")
public ResultModel<InstallResult> installCapability(@RequestBody Map<String, Object> request) {
    String capabilityId = (String) request.get("capabilityId");
    String skillId = (String) request.get("skillId");
    String source = (String) request.get("source"); // LOCAL, GITHUB, GITEE

    if (skillId == null) {
        skillId = capabilityId;
    }

    log.info("[installCapability] Installing capability: {} from source: {}", skillId, source);

    InstallResult result = new InstallResult();
    result.setCapabilityId(skillId);

    try {
        // 1. 从 skill-index.yaml 获取 skill 信息
        Map<String, Object> skillInfo = skillIndexLoader.getSkillInfo(skillId);
        if (skillInfo == null) {
            result.setSuccess(false);
            result.setMessage("Skill not found in index: " + skillId);
            return ResultModel.success(result);
        }

        // 2. 检查依赖
        List<Map<String, Object>> dependencies = (List<Map<String, Object>>) skillInfo.get("dependencies");
        if (dependencies != null && !dependencies.isEmpty()) {
            for (Map<String, Object> dep : dependencies) {
                String depId = (String) dep.get("id");
                boolean isRequired = (Boolean) dep.getOrDefault("required", false);
                
                // 检查依赖是否已安装
                if (!skillIndexLoader.isInstalled(depId)) {
                    if (isRequired) {
                        result.setSuccess(false);
                        result.setMessage("Required dependency not installed: " + depId);
                        return ResultModel.success(result);
                    }
                }
            }
        }

        // 3. 实际安装 skill
        String skillPath = (String) skillInfo.get("path");
        if (skillPath != null) {
            // 调用 PluginManager 安装 skill
            boolean installed = pluginManager.installSkill(skillPath, skillId);
            if (installed) {
                // 标记为已安装
                skillIndexLoader.markAsInstalled(skillId);
                result.setSuccess(true);
                result.setMessage("Capability installed successfully");
                log.info("[installCapability] Capability {} installed successfully", skillId);
            } else {
                result.setSuccess(false);
                result.setMessage("Failed to install capability: " + skillId);
                log.error("[installCapability] Failed to install capability {}", skillId);
            }
        } else {
            result.setSuccess(false);
            result.setMessage("Skill path not found: " + skillId);
        }

    } catch (Exception e) {
        log.error("[installCapability] Error installing capability {}: {}", skillId, e.getMessage(), e);
        result.setSuccess(false);
        result.setMessage("Installation error: " + e.getMessage());
    }

    return ResultModel.success(result);
}
```

**需要添加的依赖注入**:
```java
@Autowired
private PluginManager pluginManager;
```

**需要添加的方法到 SkillIndexLoader**:
```java
public Map<String, Object> getSkillInfo(String skillId) {
    for (Map<String, Object> skill : skills) {
        if (skillId.equals(skill.get("id"))) {
            return skill;
        }
    }
    return null;
}

public boolean isInstalled(String skillId) {
    return mockInstalledSkills.contains(skillId);
}
```

---

## 验证步骤

1. **前端修复验证**:
   - 访问 http://localhost:8084/console/skills/skill-capability/pages/capability-discovery.html
   - 点击"开始扫描"
   - 验证"已扫描"数量显示为实际数量（如 128）

2. **后端修复验证**:
   - 选择一个未安装的 skill
   - 点击"安装"
   - 验证安装成功后，skill 状态变为"已安装"
   - 刷新页面后，状态保持"已安装"

---

## 相关 API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| `/api/v1/discovery/local` | POST | 本地扫描 |
| `/api/v1/discovery/install` | POST | 安装能力 |
| `/api/v1/discovery/github` | POST | GitHub 扫描 |
| `/api/v1/discovery/gitee` | POST | Gitee 扫描 |

---

## 联系方式

如有疑问，请联系 MVP Core 团队。

---

**任务状态**: 待处理  
**预计完成时间**: 待定
