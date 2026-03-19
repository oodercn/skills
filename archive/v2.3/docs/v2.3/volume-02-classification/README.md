# 第二分册：分类体系

> **版本**: v2.3.5  
> **更新日期**: 2026-03-08

---

## 分册说明

本分册包含场景技能的分类定义、能力归属判定规则和能力盘点。

---

## 文档清单

| 文档 | 说明 | 状态 |
|------|------|------|
| [SCENE_SKILL_CLASSIFICATION.md](./SCENE_SKILL_CLASSIFICATION_AND_LIFECYCLE.md) | 场景技能分类体系 | ✅ 已发布 |
| [CAPABILITY_TYPE_SYSTEM.md](./SCENE_CAPABILITY_CLASSIFICATION_SYSTEM.md) | 能力类型体系 | ✅ 已发布 |
| [CAPABILITY_OWNERSHIP_GUIDE.md](./CAPABILITY_OWNERSHIP_GUIDE.md) | 能力归属判定指南 | ✅ 已发布 |
| [CAPABILITY_INVENTORY.md](./CAPABILITY_INVENTORY.md) | 现有能力归属盘点 | ✅ 已发布 |
| [CAPABILITY_GOVERNANCE_TASKS.md](./CAPABILITY_GOVERNANCE_TASKS.md) | 能力治理任务清单 | ✅ 已发布 |

---

## 能力归属分类

| 分类 | 简写 | 定义位置 | 可见性 |
|------|------|----------|--------|
| **场景内部能力** | SIC | skill.yaml 的 capabilities 列表 | 仅场景内 |
| **独立能力** | IC | 独立 skill 包 | supportedSceneTypes |
| **平台能力** | PC | 平台内置 | 全局 |
| **工具技能** | TOOL | 独立工具包 | 按需 |

---

## 场景技能三大分类

| 分类 | 简写 | 特征 |
|------|------|------|
| **自驱业务场景** | ABS | mainFirst=true + 业务语义完整 |
| **自驱系统场景** | ASS | mainFirst=true + 业务语义弱 |
| **触发业务场景** | TBS | mainFirst=false + 业务语义完整 |

---

## 业务语义评分标准

| 评分项 | 分值 |
|--------|------|
| driverConditions 非空 | 3分 |
| participants 非空 | 3分 |
| visibility = public | 2分 |
| 有协作能力 | 1分 |
| 有业务标签 | 1分 |

---

## 能力归属判定流程

```
开始判定
    │
    ▼
是否为平台核心功能？ ────是───▶ 平台能力(PC)
    │
   否
    │
    ▼
是否只服务于单一场景？ ────是───▶ 场景内部能力(SIC)
    │
   否
    │
    ▼
是否需要跨场景复用？ ────是───▶ 独立能力(IC)
    │
   否
    │
    ▼
场景内部能力(SIC)
```

---

## 相关分册

- [第一分册：规范规格](../volume-01-specification/)
- [第三分册：生命周期](../volume-03-lifecycle/)
- [术语表](../GLOSSARY_V2.3.md)

---

**文档维护**: Ooder 开发团队  
**最后更新**: 2026-03-08
