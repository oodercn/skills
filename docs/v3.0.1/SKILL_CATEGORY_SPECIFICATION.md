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
- 业务场景技能无处安放

### 1.2 目标
- 统一分类体系，移除废弃字段
- 建立可维护的分类标准
- 新增 `biz` 业务场景分类
- 实现分类统计正常化

---

## 二、分类定义（Category）

### 2.1 标准分类列表（12个）

| 分类ID | 分类名称 | 英文名称 | 描述 | 图标 | 面向用户 | 状态 |
|--------|----------|----------|------|------|:--------:|:----:|
| `org` | 组织服务 | Organization | 企业组织架构、用户认证相关服务 | users | ❌ | ✅ 标准 |
| `vfs` | 存储服务 | Storage | 文件存储、对象存储相关服务 | database | ❌ | ✅ 标准 |
| `llm` | LLM服务 | LLM Services | 大语言模型服务、对话、配置、上下文管理 | brain | ✅ | ✅ 标准 |
| `knowledge` | 知识服务 | Knowledge | 知识库、RAG、向量存储、文档处理 | book | ✅ | ✅ 标准 |
| `sys` | 系统管理 | System | 系统监控、网络管理、安全审计 | settings | ❌ | ✅ 标准 |
| `msg` | 消息通讯 | Messaging | 消息队列、通讯协议服务 | message | ❌ | ✅ 标准 |
| `ui` | UI生成 | UI Generation | 界面生成、设计转代码服务 | palette | ❌ | ✅ 标准 |
| `payment` | 支付服务 | Payment | 支付渠道、退款管理、交易处理 | credit-card | ❌ | ✅ 标准 |
| `media` | 媒体发布 | Media Publishing | 自媒体文章发布、内容管理、数据分析 | edit | ❌ | ✅ 标准 |
| `nexus-ui` | Nexus界面 | Nexus UI | Nexus管理界面、仪表盘、监控页面 | layout | ❌ | ✅ 标准 |
| `util` | 工具服务 | Utility | 通用工具、辅助功能 | tool | ✅ | ✅ 标准 |
| `biz` | 业务场景 | Business | 人力资源、客户管理、财务审批、项目管理等业务场景 | briefcase | ✅ | ✅ 新增 |

### 2.2 普通用户可见分类（4个）

```
普通用户可见分类:
├── 🤖 AI助手 (llm) - 智能对话、代码助手、写作助手
├── 📚 知识服务 (knowledge) - 知识问答、文档检索、知识库管理
├── 💼 业务场景 (biz) - HR、财务、审批、项目、工作日志
│   ├── 人力资源 (hr) - 员工管理、考勤打卡、薪资查询、招聘助手
│   ├── 客户管理 (crm) - 客户管理、联系人、商机管理
│   ├── 财务管理 (finance) - 报销审批、预算管理、账务查询
│   ├── 审批流程 (approval) - 请假审批、报销审批、采购审批
│   ├── 项目协作 (project) - 项目管理、任务分配、进度跟踪
│   └── 工作日志 (worklog) - 日报填写、周报汇总、工时统计
└── 🛠️ 工具服务 (util) - 技能市场、报表、分享
```

### 2.3 子分类体系（subCategory）

`biz` 分类的子分类标准值：

| 子分类ID | 名称 | 描述 | 示例技能 |
|----------|------|------|----------|
| `hr` | 人力资源 | 员工管理、考勤、薪酬、招聘 | skill-recruitment-management |
| `crm` | 客户管理 | 客户、联系人、商机、合同 | skill-real-estate-form |
| `finance` | 财务管理 | 账务、报销、预算、结算 | - |
| `approval` | 审批流程 | 流程定义、审批、流转 | skill-approval-form |
| `project` | 项目协作 | 项目、任务、文档、进度 | - |
| `worklog` | 工作日志 | 日报、周报、工时统计 | - |
| `qa` | 质检管理 | 质量检查、审核、分析 | skill-recording-qa |
| `scenario` | 通用业务 | 业务场景服务 | skill-business |

### 2.4 禁止使用的值（已废弃）

| 废弃值 | 原因 | 迁移到 |
|--------|------|--------|
| `abs` | 场景技能类型，非分类 | `knowledge` + `sceneType: abs` |
| `tbs` | 场景技能类型，非分类 | `knowledge` + `sceneType: tbs` |
| `ass` | 场景技能类型，非分类 | `knowledge` + `sceneType: ass` |
| `SYSTEM` | 大写格式不规范 | `sys` |
| `COMMUNICATION` | 大写格式不规范 | `msg` |
| `COLLABORATION` | 大写格式不规范 | `util` |
| `business` | 已定义 `biz` 分类 | `biz` |
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
  category: knowledge        # 分类，必须是 12 个标准值之一
  
  # ✅ 场景技能特有字段（仅场景技能）
  sceneType: abs               # 场景技能类型：abs/tbs/ass
  
  # ✅ 子分类（可选）
  subCategory: qa             # 子分类：qa/chat/document/hr/crm/...
  
  # ✅ 标签
  tags:
    - document
    - assistant
    - rag
    - scene-skill
    - mainFirst
```

### 3.2 业务场景技能示例

```yaml
# 业务场景技能
- skillId: skill-recruitment-management
  name: 招聘管理系统
  version: "1.0.0"
  
  # ✅ 业务场景分类
  category: biz
  
  # ✅ 子分类：人力资源
  subCategory: hr
  
  tags:
    - recruitment
    - hr
    - hiring
    - interview
    - resume
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
| `category: business` | `category: biz` | 更新 skill-index |

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

- [x] 移除 `metadata.category` 字段
- [x] 移除 `spec.category` 字段  
- [x] 移除 `spec.classification.category` 字段
- [x] 更新 skill-index 中的 `category` 字段（12个标准值）
- [x] 新增 `biz` 分类
- [x] 定义 `subCategory` 标准值
- [ ] 为场景技能添加 `sceneType` 字段
- [ ] 修复大写分类值为小写
- [ ] 更新后端读取逻辑
- [ ] 更新前端展示逻辑
- [ ] 验证 my-capabilities.html 分类统计正常

---

## 五、附录

### 5.1 参考文档

- [ABS/TBS/ASS 代码分析](./SKILL_CATEGORY_ANALYSIS.md)
- [分类对比分析](./SKILL_CATEGORY_COMPARISON.md)
- [Skills System Planning](./SKILLS_SYSTEM_PLANNING.md)
- [文档冲突分析报告](./DOCUMENT_CONFLICT_ANALYSIS.md)

### 5.2 相关代码

- `SceneSkillCategory.java` - 场景技能类型定义
- `SceneSkillCategoryDetector.java` - 分类检测逻辑
- `SkillIndexLoader.java` - Index 加载逻辑

---

**文档维护者**: Skills Team  
**最后更新**: 2026-03-18  
**版本**: v1.1.0
