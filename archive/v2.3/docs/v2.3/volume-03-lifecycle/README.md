# 第三分册：生命周期

> **版本**: v2.3.1  
> **更新日期**: 2026-03-05

---

## 分册说明

本分册包含场景技能的生命周期状态和流程管理。

---

## 文档清单

| 文档 | 说明 | 状态 |
|------|------|------|
| [SCENE_LIFECYCLE_MANAGEMENT.md](../SCENE_SKILL_CLASSIFICATION_AND_LIFECYCLE.md) | 场景生命周期管理 | ✅ 已发布 |

---

## 生命周期状态

### 基础状态

| 状态 | 简写 | 说明 |
|------|------|------|
| DRAFT | D | 草稿状态 |
| PENDING | P | 待处理状态 |
| ACTIVE | A | 激活状态 |
| SCHEDULED | S | 已调度状态 |
| RUNNING | R | 运行中状态 |
| PAUSED | PA | 暂停状态 |
| ERROR | E | 错误状态 |
| COMPLETED | C | 完成状态 |
| ARCHIVED | AR | 归档状态 |

### WAITING 子状态

| 子状态 | 说明 |
|--------|------|
| WAITING_APPROVAL | 等待审批 |
| WAITING_CONDITION | 等待条件满足 |
| WAITING_RESOURCE | 等待资源可用 |
| WAITING_SCHEDULE | 等待指定时间 |

---

## 各分类生命周期

| 分类 | 生命周期 |
|------|---------|
| ABS | DRAFT → ACTIVE → SCHEDULED → RUNNING → COMPLETED → ARCHIVED |
| ASS | ACTIVE ↔ SCHEDULED ↔ RUNNING (循环) |
| TBS | DRAFT → PENDING → WAITING → RUNNING → COMPLETED → ARCHIVED |

---

## 相关分册

- [第二分册：分类体系](../volume-02-classification/)
- [第四分册：架构设计](../volume-04-architecture/)
- [术语表](../GLOSSARY_V2.3.md)
