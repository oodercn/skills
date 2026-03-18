# Skills 分类规范 v2.3.1

> **文档状态**: 正式发布  
> **生效日期**: 2026-03-18  
> **适用范围**: MVP / SE / Skills 团队  
> **优先级**: P0 - 立即执行

---

## 一、背景与问题

### 1.1 当前问题
- `abs`/`tbs`/`ass` 是**废弃的场景技能类型**，被错误地用作分类
- 分类字段位置不统一，导致后端统计失败
- my-capabilities.html 页面分类统计显示为 0

### 1.2 目标
- 统一分类体系，移除废弃字段
- 建立可维护的分类标准
- 实现分类统计正常化

---

## 二、分类定义（Category）

### 2.1 标准分类列表（11个）

| 分类ID | 分类名称 | 英文名称 | 描述 | 图标 | 状态 |
|--------|----------|----------|------|------|:----:|
| `org` | 组织服务 | Organization | 企业组织架构、用户认证相关服务 | users | ✅ 标准 |
| `vfs` | 存储服务 | Storage | 文件存储、对象存储相关服务 | database | ✅ 标准 |
| `llm` | LLM服务 | LLM Services | 大语言模型服务、对话、配置、上下文管理 | brain | ✅ 标准 |
| `knowledge` | 知识服务 | Knowledge | 知识库、RAG、向量存储、文档处理 | book | ✅ 标准 |
| `sys` | 系统管理 | System | 系统监控、网络管理、安全审计 | settings | ✅ 标准 |
| `msg` | 消息通讯 | Messaging | 消息队列、通讯协议服务 | message | ✅ 标准 |
| `ui` | UI生成 | UI Generation | 界面生成、设计转代码服务 | palette | ✅ 标准 |
| `payment` | 支付服务 | Payment | 支付渠道、退款管理、交易处理 | credit-card | ✅ 标准 |
| `media` | 媒体发布 | Media Publishing | 自媒体文章发布、内容管理、数据分析 | edit | ✅ 标准 |
| `nexus-ui` | Nexus界面 | Nexus UI | Nexus管理界面、仪表盘、监控页面 | layout | ✅ 标准 |

### 2.2 禁止使用的值（已废弃）

| 废弃值 | 原因 | 迁移到 |
|--------|------|--------|
| `abs` | 场景技能类型，非分类 | `knowledge` + `sceneType: abs` |
| `tbs` | 场景技能类型，非分类 | `knowledge` + `sceneType: tbs` |
| `ass` | 场景技能类型，非分类 | `knowledge` + `sceneType: ass` |
| `SYSTEM` | 大写格式不规范 | `sys` |
| `COMMUNICATION` | 大写格式不规范 | `msg` |
| `COLLABORATION` | 大写格式不规范 | `util` |
| `business` | 未定义分类 | `util` |
| `infrastructure` | 未定义分类 | `sys` |
| `scheduler` | 未定义分类 | `sys` |

---

## 三、字段定义规范

### 3.1 Skill Index Entry 字段

```yaml
# 正确的字段定义
- skillId: skill-document-assistant
  name: 智能文档助手
  version: "1.0.0"
  
  # ✅ 正确的分类字段
  category: knowledge        # 分类，必须是 11 个标准值之一
  
  # ✅ 场景技能特有字段（仅场景技能）
  sceneType: abs               # 场景技能类型：abs/tbs/ass
  
  # ✅ 子分类（可选）
  subCategory: qa             # 子分类：qa/chat/document/...
  
  # ✅ 标签
  tags:
    - document
    - assistant
    - rag
    - scene-skill
    - mainFirst
```

### 3.2 Skill.yaml 字段

```yaml
# 场景技能 (Scene Skill)
apiVersion: skill.ooder.net/v1
kind: Skill
metadata:
  id: skill-document-assistant
  name: 智能文档助手
  # ❌ 废弃：不再使用 metadata.category
  # category: abs
  
spec:
  type: scene-skill
  
  # ✅ 场景技能类型（在 skill-index 中使用）
  # sceneType: abs  # 在 skill-index-entry.yaml 中定义
  
  # ✅ 分类字段（在 skill-index 中使用）
  # category: knowledge  # 在 skill-index-entry.yaml 中定义
  
  classification:
    # ❌ 废弃：不再使用 classification.category
    # category: abs
    categoryName: 自驱业务场景
    mainFirst: true
    businessSemanticsScore: 9
```

### 3.3 废弃字段清单

| 废弃字段路径 | 替代方案 | 备注 |
|--------------|----------|------|
| `metadata.category` | `skill-index` 中的 `category` | 从 skill.yaml 中移除 |
| `spec.category` | `skill-index` 中的 `category` | 从 skill.yaml 中移除 |
| `spec.classification.category` | `skill-index` 中的 `sceneType` | 从 skill.yaml 中移除 |
| `category: abs` | `category: knowledge` + `sceneType: abs` | 更新 skill-index |
| `category: tbs` | `category: knowledge` + `sceneType: tbs` | 更新 skill-index |
| `category: ass` | `category: knowledge` + `sceneType: ass` | 更新 skill-index |

---

## 四、协作分工

### 4.1 团队分工

| 团队 | 职责 | 交付物 | 截止日期 |
|------|------|--------|----------|
| **Skills 团队** | 更新 skill-index-entry.yaml | 修复后的 index 文件 | 2026-03-20 |
| **MVP 团队** | 更新 SkillIndexLoader | 支持新字段的后端代码 | 2026-03-22 |
| **SE 团队** | 更新前端展示 | 适配新分类的 UI | 2026-03-25 |
| **QA 团队** | 验证修复结果 | 测试报告 | 2026-03-28 |

### 4.2 检查清单

- [ ] 移除 `metadata.category` 字段
- [ ] 移除 `spec.category` 字段  
- [ ] 移除 `spec.classification.category` 字段
- [ ] 更新 skill-index 中的 `category` 字段（11个标准值）
- [ ] 为场景技能添加 `sceneType` 字段
- [ ] 修复大写分类值为小写
- [ ] 迁移未定义分类到标准分类
- [ ] 更新后端读取逻辑
- [ ] 更新前端展示逻辑
- [ ] 验证 my-capabilities.html 分类统计正常

---

## 五、附录

### 5.1 参考文档

- [ABS/TBS/ASS 代码分析](./SKILL_CATEGORY_ANALYSIS.md)
- [分类对比分析](./SKILL_CATEGORY_COMPARISON.md)
- [Skills System Planning](./SKILLS_SYSTEM_PLANNING.md)

### 5.2 相关代码

- `SceneSkillCategory.java` - 场景技能类型定义
- `SceneSkillCategoryDetector.java` - 分类检测逻辑
- `SkillIndexLoader.java` - Index 加载逻辑

---

**文档维护者**: Skills Team  
**最后更新**: 2026-03-18  
**版本**: v1.0.0
