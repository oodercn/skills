# 协作需求：UnifiedDiscoveryServiceImpl 解析 skillForm 字段

## 需求编号
COLLAB-2026-002

## 需求方
Skills 仓库团队

## 需求背景

### 问题描述
能力发现页面显示所有技能都被归类为"能力服务"，没有正确区分"场景应用"、"能力服务"、"驱动适配"三种形态。

### 根因分析
经深入分析，问题存在两个层面：

#### 1. 数据层面（已修复）
Skills 仓库的 YAML 文件缺少 `skillForm` 字段。已在本次更新中添加完成。

#### 2. 程序层面（需 SE 团队修复）
`UnifiedDiscoveryServiceImpl.createSkillPackage()` 方法没有解析 `skillForm` 字段，导致 `SkillPackage.metadata` 为 null，最终 `DiscoveryConverter` 使用默认逻辑判断。

### 问题代码位置

**文件**: `e:\apex\app\se-sources\net\ooder\scene\discovery\impl\UnifiedDiscoveryServiceImpl.java`

**方法**: `createSkillPackage()` (约第 993-1035 行)

```java
private SkillPackage createSkillPackage(JSONObject skillData) {
    try {
        String skillId = skillData.getString("skillId");
        // ...
        
        SkillPackage skill = new SkillPackage();
        skill.setSkillId(skillId);
        skill.setName(name);
        skill.setVersion(version);
        skill.setDescription(description);
        skill.setCategory(category);
        
        // ❌ 缺少 skillForm 解析
        // ❌ 缺少 metadata 设置
        
        return skill;
    } catch (Exception e) {
        return null;
    }
}
```

## 需求详情

### 修改要求

请在 `createSkillPackage()` 方法中添加以下逻辑：

```java
private SkillPackage createSkillPackage(JSONObject skillData) {
    try {
        String skillId = skillData.getString("skillId");
        // ... 现有代码 ...
        
        SkillPackage skill = new SkillPackage();
        skill.setSkillId(skillId);
        skill.setName(name);
        skill.setVersion(version);
        skill.setDescription(description);
        skill.setCategory(category);
        
        // ✅ 新增：解析 skillForm 字段
        String skillForm = skillData.getString("skillForm");
        if (skillForm != null) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("skillForm", skillForm);
            
            // 同时解析 sceneType（如果存在）
            String sceneType = skillData.getString("sceneType");
            if (sceneType != null) {
                metadata.put("sceneType", sceneType);
            }
            
            skill.setMetadata(metadata);
        }
        
        // ... 现有 tags 解析代码 ...
        
        return skill;
    } catch (Exception e) {
        return null;
    }
}
```

### skillForm 字段规范

| skillForm 值 | 含义 | 适用场景 |
|-------------|------|---------|
| `SCENE` | 场景应用 | 完整的业务场景，包含角色、流程、UI |
| `PROVIDER` | 能力服务 | 提供特定能力的原子服务 |
| `DRIVER` | 驱动适配 | 硬件/系统/协议适配器 |

### sceneType 字段规范（可选）

| sceneType 值 | 含义 |
|-------------|------|
| `AUTO` | 自驱型场景（SuperAgent） |
| `MANUAL` | 手动型场景 |

## 验证标准

修改完成后，从 Gitee 发现技能时应正确返回 skillForm：

```json
{
  "skillId": "skill-llm-chat",
  "name": "LLM智能对话场景能力",
  "metadata": {
    "skillForm": "SCENE",
    "sceneType": "AUTO"
  }
}
```

## 关联需求

- 关联需求文档：`e:\apex\app\docs\collaboration\skills-skillform-requirement.md`
- Skills 仓库更新：已完成 skillForm 字段添加

## 时间要求

- 希望在 2026-04-05 前完成

## 联系方式

- 需求方：Skills 仓库团队
- 文件路径：`e:\github\ooder-skills\docs\collaboration\se-sdk-skillform-parsing-requirement.md`

---

*此文档由 Skills 仓库团队生成*
*生成时间：2026-03-29*
