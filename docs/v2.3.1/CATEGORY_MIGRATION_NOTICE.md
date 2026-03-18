# Skills 分类规范迁移通知

> **优先级**: P0 - 立即执行  
> **影响范围**: MVP / SE / Skills 团队  
> **截止日期**: 2026-03-28

---

## 一、背景

`abs`/`tbs`/`ass` 是**废弃的场景技能类型**，被错误地用作分类，导致：
1. my-capabilities.html 分类统计显示为 0
2. 分类体系混乱
3. 前后端数据不一致

---

## 二、废弃清单

### 2.1 废弃的分类值

| 废弃值 | 原因 | 迁移到 |
|--------|------|--------|
| `abs` | 场景技能类型 | `knowledge` + `sceneType: abs` |
| `tbs` | 场景技能类型 | `knowledge` + `sceneType: tbs` |
| `ass` | 场景技能类型 | `knowledge` + `sceneType: ass` |
| `SYSTEM` | 大写格式 | `sys` |
| `COMMUNICATION` | 大写格式 | `msg` |
| `COLLABORATION` | 大写格式 | `util` |
| `business` | 未定义 | `util` |
| `infrastructure` | 未定义 | `sys` |
| `scheduler` | 未定义 | `sys` |

### 2.2 废弃的字段

| 废弃字段路径 | 位置 | 替代方案 |
|--------------|------|----------|
| `metadata.category` | skill.yaml | skill-index 中的 `category` |
| `spec.category` | skill.yaml | skill-index 中的 `category` |
| `spec.classification.category` | skill.yaml | skill-index 中的 `sceneType` |

---

## 三、协作分工

### 3.1 团队任务

| 团队 | 任务 | 交付物 | 截止日期 |
|------|------|--------|----------|
| **Skills 团队** | 更新 skill-index-entry.yaml | 修复后的 index 文件 | 2026-03-20 |
| **MVP 团队** | 更新 SkillIndexLoader.java | 支持新字段的后端代码 | 2026-03-22 |
| **SE 团队** | 更新前端展示 | 适配新分类的 UI | 2026-03-25 |
| **QA 团队** | 验证修复结果 | 测试报告 | 2026-03-28 |

### 3.2 详细任务清单

#### Skills 团队
- [ ] 移除 `metadata.category` 字段
- [ ] 移除 `spec.category` 字段
- [ ] 移除 `spec.classification.category` 字段
- [ ] 更新 skill-index 中的 `category` 为 11 个标准值
- [ ] 为场景技能添加 `sceneType` 字段
- [ ] 修复大写分类值为小写
- [ ] 迁移未定义分类到标准分类

#### MVP 团队
- [ ] 修改 SkillIndexLoader 读取 `category` 字段
- [ ] 修改 SkillIndexLoader 读取 `sceneType` 字段（场景技能）
- [ ] 废弃读取 `metadata.category`
- [ ] 废弃读取 `spec.category`
- [ ] 废弃读取 `spec.classification.category`
- [ ] 更新 CODE_MAPPING 映射
- [ ] 测试分类统计功能

#### SE 团队
- [ ] 更新 my-capabilities.html 分类展示
- [ ] 适配 11 个标准分类的图标和名称
- [ ] 处理场景技能的 `sceneType` 展示
- [ ] 测试分类统计正确性

#### QA 团队
- [ ] 验证所有 skill 的 category 值正确
- [ ] 验证 sceneType 仅用于场景技能
- [ ] 验证分类统计功能正常
- [ ] 验证 my-capabilities.html 展示正确
- [ ] 输出测试报告

---

## 四、标准分类定义

### 4.1 11 个标准分类

| 分类ID | 分类名称 | 图标 |
|--------|----------|------|
| `org` | 组织服务 | users |
| `vfs` | 存储服务 | database |
| `llm` | LLM服务 | brain |
| `knowledge` | 知识服务 | book |
| `sys` | 系统管理 | settings |
| `msg` | 消息通讯 | message |
| `ui` | UI生成 | palette |
| `payment` | 支付服务 | credit-card |
| `media` | 媒体发布 | edit |
| `util` | 工具服务 | tool |
| `nexus-ui` | Nexus界面 | layout |

---

## 五、参考文档

- [SKILL_CATEGORY_SPECIFICATION.md](./SKILL_CATEGORY_SPECIFICATION.md) - 完整规范
- [abs-tbs-ass-code-analysis-report.md](./abs-tbs-ass-code-analysis-report.md) - ABS/TBS/ASS 分析

---

**发布者**: Skills Team  
**发布日期**: 2026-03-18  
**版本**: v1.0.0
