# 第五分册：开发指南

> **版本**: v2.3.1  
> **更新日期**: 2026-03-05

---

## 分册说明

本分册包含开发者指南和实施手册。

---

## 文档清单

| 文档 | 说明 | 状态 |
|------|------|------|
| [SDK_2.3_UPGRADE_GUIDE.md](../SDK_2.3_UPGRADE_GUIDE.md) | SDK升级指南 | ✅ 已发布 |
| [INSTALL_LOGIC_ANALYSIS.md](../SCENE_SKILL_INSTALL_LOGIC_ANALYSIS.md) | 安装逻辑分析 | ✅ 已发布 |

---

## 待实现任务

| 任务 | 优先级 | 状态 | 依赖 |
|------|--------|------|------|
| 定义SceneSkillCategory枚举 | P0 | ⬜ 待执行 | Engine Team |
| 实现detectCategory()方法 | P0 | ⬜ 待执行 | Engine Team |
| 增加DRIVER/EXECUTOR类型区分 | P1 | ⬜ 待执行 | - |
| 实现WAITING子状态 | P1 | ⬜ 待执行 | - |

---

## 分类检测算法

```java
public SceneSkillCategory detectCategory(SkillPackage skill) {
    // 标准1: 检查 metadata.type
    if (!"scene-skill".equals(skill.getMetadata().getType())) {
        return SceneSkillCategory.NOT_SCENE_SKILL;
    }
    
    // 标准2: 检查 sceneCapabilities
    List<SceneCapability> sceneCaps = skill.getSpec().getSceneCapabilities();
    if (sceneCaps == null || sceneCaps.isEmpty()) {
        return SceneSkillCategory.NOT_SCENE_SKILL;
    }
    
    // 标准3: 检查 mainFirst
    boolean hasMainFirst = cap.isMainFirst() 
        && cap.getMainFirstConfig() != null;
    
    // 标准4: 业务语义评分
    int score = calculateBusinessSemanticsScore(cap);
    
    // 分类判断
    if (hasMainFirst && score >= 8) {
        return SceneSkillCategory.ABS;  // 自驱业务场景
    } else if (hasMainFirst && score < 3) {
        return SceneSkillCategory.ASS;  // 自驱系统场景
    } else if (!hasMainFirst && score >= 8) {
        return SceneSkillCategory.TBS;  // 触发业务场景
    } else {
        return SceneSkillCategory.PENDING;  // 待定
    }
}
```

---

## 相关分册

- [第四分册：架构设计](../volume-04-architecture/)
- [第六分册：用户故事](../volume-06-user-stories/)
- [术语表](../GLOSSARY_V2.3.md)
