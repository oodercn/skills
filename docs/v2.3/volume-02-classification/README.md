# 第二分册：分类体系

> **版本**: v2.3.1  
> **更新日期**: 2026-03-05

---

## 分册说明

本分册包含场景技能的分类定义和判定规则。

---

## 文档清单

| 文档 | 说明 | 状态 |
|------|------|------|
| [SCENE_SKILL_CLASSIFICATION.md](../SCENE_SKILL_CLASSIFICATION_AND_LIFECYCLE.md) | 场景技能分类体系 | ✅ 已发布 |
| [CAPABILITY_TYPE_SYSTEM.md](../SCENE_CAPABILITY_CLASSIFICATION_SYSTEM.md) | 能力类型体系 | ✅ 已发布 |

---

## 三大分类

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

## 相关分册

- [第一分册：规范规格](../volume-01-specification/)
- [第三分册：生命周期](../volume-03-lifecycle/)
- [术语表](../GLOSSARY_V2.3.md)
