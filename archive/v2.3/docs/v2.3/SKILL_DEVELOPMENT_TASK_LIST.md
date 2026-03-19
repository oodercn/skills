# Ooder Skills v2.3 场景技能开发任务列表

> **版本**: v2.3  
> **创建日期**: 2026-03-07  
> **状态**: 进行中

---

## 任务概览

| 任务编号 | 技能ID | 技能名称 | 分类 | 用户故事 | 需求规格 | 开发设计 |
|---------|--------|---------|------|---------|---------|---------|
| TASK-001 | skill-document-assistant | 智能文档助手 | ABS | ✅ 完成 | ✅ 完成 | ✅ 完成 |
| TASK-002 | skill-meeting-minutes | 会议纪要整理 | TBS | ✅ 完成 | ✅ 完成 | ✅ 完成 |
| TASK-003 | skill-knowledge-share | 知识共享管理 | ASS | ✅ 完成 | ✅ 完成 | ✅ 完成 |
| TASK-004 | skill-onboarding-assistant | 新人培训助手 | ABS | ✅ 完成 | ✅ 完成 | ✅ 完成 |
| TASK-005 | skill-project-knowledge | 项目知识沉淀 | TBS | ✅ 完成 | ✅ 完成 | ✅ 完成 |

---

## TASK-001 skill-document-assistant 智能文档助手

### 基本信息

| 属性 | 值 |
|------|-----|
| **分类** | ABS (自驱业务场景) |
| **mainFirst** | true |
| **业务语义分** | 9 |
| **状态** | ✅ 已完成 |

### 1. 用户故事详情

#### 核心用户故事

**作为** 企业员工  
**我希望** 能够通过自然语言查询公司制度和流程文档  
**以便于** 快速获取工作所需信息，无需翻阅大量文档

#### 用户故事分解

| 编号 | 用户故事 | 优先级 | 验收标准 | 状态 |
|------|---------|--------|---------|------|
| US-001 | 创建知识库 | P0 | 支持私有/团队/公开三种可见性 | ✅ |
| US-002 | 上传文档 | P0 | 支持PDF/Word/Excel/Markdown格式 | ✅ |
| US-003 | 智能问答 | P0 | 基于RAG返回答案和来源 | ✅ |
| US-004 | 查看来源文档 | P1 | 点击来源可查看原文 | ✅ |
| US-005 | 反馈评价 | P1 | 支持点赞/点踩反馈 | ✅ |
| US-006 | 查询历史 | P2 | 查看历史问答记录 | ✅ |

#### 逻辑推理

```
用户输入查询 → RAG检索相关文档片段 → LLM生成答案 → 返回答案+来源
     ↓
置信度判断 → 高置信度直接返回 → 低置信度提示用户确认
```

### 2. 需求规格分析

#### 三闭环检查

| 闭环类型 | 检查项 | 状态 |
|---------|-------|------|
| 生命周期闭环 | 知识库CRUD完整 | ✅ |
| 数据实体闭环 | KB 1:N Document | ✅ |
| 按钮API闭环 | 所有操作有对应API | ✅ |

#### API清单

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 创建知识库 | POST | /api/v1/document-assistant/kb | ✅ |
| 查询知识库 | GET | /api/v1/document-assistant/kb | ✅ |
| 上传文档 | POST | /api/v1/document-assistant/upload | ✅ |
| 智能问答 | POST | /api/v1/document-assistant/query | ✅ |
| 反馈评价 | POST | /api/v1/document-assistant/feedback | ✅ |
| 查询历史 | GET | /api/v1/document-assistant/history | ✅ |

### 3. 执行开发设计

#### 已完成文件

| 文件 | 说明 | 状态 |
|------|------|------|
| skill.yaml | 技能定义 | ✅ |
| DocumentAssistantService.java | 服务实现 | ✅ |
| DocumentAssistantController.java | 控制器 | ✅ |
| skill-document-assistant-ui/ | 前端UI | ✅ |

---

## TASK-002 skill-meeting-minutes 会议纪要整理

### 基本信息

| 属性 | 值 |
|------|-----|
| **分类** | TBS (触发业务场景) |
| **mainFirst** | false |
| **业务语义分** | 8 |
| **状态** | ✅ 已完成 |

### 1. 用户故事详情

#### 核心用户故事

**作为** 项目团队成员  
**我希望** 能够快速将会议录音或笔记整理成结构化的会议纪要  
**以便于** 追踪会议决策和行动项，并归档到知识库供后续查阅

#### 用户故事分解

| 编号 | 用户故事 | 优先级 | 验收标准 | 状态 |
|------|---------|--------|---------|------|
| US-001 | 上传会议内容 | P0 | 支持粘贴文本、上传文件 | ✅ |
| US-002 | 自动提取会议主题和摘要 | P0 | LLM自动生成标题和摘要 | ✅ |
| US-003 | 自动提取关键决策 | P0 | 识别并列出所有决策点 | ✅ |
| US-004 | 自动提取行动项 | P0 | 提取任务、责任人、截止时间 | ✅ |
| US-005 | 手动编辑和调整纪要 | P1 | 支持在线编辑所有字段 | ✅ |
| US-006 | 导出多种格式 | P1 | 支持Markdown/Word/PDF | ✅ |
| US-007 | 归档到知识库 | P2 | 关联项目知识库自动归档 | ✅ |
| US-008 | 行动项跟踪提醒 | P2 | 截止时间提醒通知 | ⏳ |

#### 逻辑推理

```
会议内容输入 → LLM结构化处理 → 提取主题/摘要/决策/行动项
     ↓
用户确认/编辑 → 发布/归档 → 行动项跟踪
```

### 2. 需求规格分析

#### 三闭环检查

| 闭环类型 | 检查项 | 状态 |
|---------|-------|------|
| 生命周期闭环 | 纪要CRUD + 状态转换 | ✅ |
| 数据实体闭环 | Minutes 1:N ActionItem/Decision | ✅ |
| 按钮API闭环 | 所有操作有对应API | ✅ |

#### 字典枚举

| 枚举 | 代码 | 值 |
|------|------|-----|
| MinutesStatus | minutes_status | DRAFT/COMPLETED/ARCHIVED |
| ActionItemStatus | action_item_status | PENDING/IN_PROGRESS/COMPLETED/CANCELLED/OVERDUE |
| MeetingType | meeting_type | REGULAR/PROJECT/DECISION/BRAINSTORM/REVIEW |

#### API清单

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 查询列表 | GET | /api/v1/meeting-minutes | ✅ |
| 查询详情 | GET | /api/v1/meeting-minutes/{id} | ✅ |
| 整理会议 | POST | /api/v1/meeting-minutes/organize | ✅ |
| 更新纪要 | PUT | /api/v1/meeting-minutes/{id} | ✅ |
| 更新状态 | PUT | /api/v1/meeting-minutes/{id}/status | ✅ |
| 删除纪要 | DELETE | /api/v1/meeting-minutes/{id} | ✅ |
| 我的待办 | GET | /api/v1/meeting-minutes/my-actions | ✅ |
| 更新行动项状态 | PUT | /api/v1/meeting-minutes/actions/{id}/status | ✅ |

### 3. 执行开发设计

#### 已完成文件

| 文件 | 说明 | 状态 |
|------|------|------|
| skill.yaml | 技能定义 | ✅ |
| MeetingMinutesService.java | 服务实现 | ✅ |
| MeetingMinutesController.java | 控制器 | ✅ |
| MinutesStatus.java | 状态枚举 | ✅ |
| ActionItemStatus.java | 行动项状态枚举 | ✅ |
| MeetingType.java | 会议类型枚举 | ✅ |
| ASK-002_SKILL_MEETING_MINUTES_SPECIFICATION.md | 需求规格 | ✅ |

---

## TASK-003 skill-knowledge-share 知识共享管理

### 基本信息

| 属性 | 值 |
|------|-----|
| **分类** | ASS (自驱系统场景) |
| **mainFirst** | true |
| **业务语义分** | 6 |
| **状态** | ⏳ 待细化 |

### 1. 用户故事详情

#### 核心用户故事

**作为** 知识库管理员  
**我希望** 能够灵活设置知识库的共享权限  
**以便于** 实现部门内和跨部门的知识安全共享

#### 用户故事分解（待细化）

| 编号 | 用户故事 | 优先级 | 验收标准 | 状态 |
|------|---------|--------|---------|------|
| US-001 | 设置知识库权限 | P0 | 支持READ/WRITE/ADMIN/OWNER权限 | ⏳ |
| US-002 | 创建分享链接 | P0 | 生成带有效期和密码的分享链接 | ⏳ |
| US-003 | 访问分享链接 | P0 | 通过分享码访问知识库 | ⏳ |
| US-004 | 协作编辑 | P1 | 多人同时编辑文档 | ⏳ |
| US-005 | 版本控制 | P1 | 文档版本历史和回滚 | ⏳ |
| US-006 | 访问统计 | P2 | 统计分享链接访问情况 | ⏳ |

#### 逻辑推理

```
知识库创建 → 权限设置 → 用户/部门授权
     ↓
分享链接生成 → 设置有效期/密码/访问次数 → 外部用户访问
     ↓
协作模式 → 实时同步 → 版本管理
```

### 2. 需求规格分析

#### 三闭环检查

| 闭环类型 | 检查项 | 状态 |
|---------|-------|------|
| 生命周期闭环 | 分享链接CRUD | ⏳ 待实现 |
| 数据实体闭环 | Share 1:N AccessLog | ⏳ 待实现 |
| 按钮API闭环 | 所有操作有对应API | ⏳ 待实现 |

#### 字典枚举（待定义）

| 枚举 | 代码 | 值 |
|------|------|-----|
| PermissionType | permission_type | READ/WRITE/ADMIN/OWNER |
| ShareStatus | share_status | ACTIVE/EXPIRED/REVOKED |

#### API清单

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 权限管理 | POST | /api/v1/knowledge-share/permission | ⏳ |
| 检查权限 | GET | /api/v1/knowledge-share/permission/check | ⏳ |
| 创建分享 | POST | /api/v1/knowledge-share/share | ⏳ |
| 验证分享 | GET | /api/v1/knowledge-share/share/{shareCode} | ⏳ |
| 协作管理 | POST | /api/v1/knowledge-share/collaboration | ⏳ |

### 3. 执行开发设计

#### 待完成文件

| 文件 | 说明 | 状态 |
|------|------|------|
| skill.yaml | 技能定义 | ✅ 已有 |
| KnowledgeShareService.java | 服务实现 | ⏳ 需完善 |
| KnowledgeShareController.java | 控制器 | ⏳ 需完善 |
| 枚举类 | 权限/分享状态枚举 | ⏳ 待创建 |
| 需求规格文档 | ASK-003_SPECIFICATION | ⏳ 待创建 |

---

## TASK-004 skill-onboarding-assistant 新人培训助手

### 基本信息

| 属性 | 值 |
|------|-----|
| **分类** | ABS (自驱业务场景) |
| **mainFirst** | true |
| **业务语义分** | 9 |
| **状态** | ⏳ 待细化 |

### 1. 用户故事详情

#### 核心用户故事

**作为** 新入职员工  
**我希望** 有一个智能助手帮助我快速了解公司和岗位知识  
**以便于** 缩短适应期，快速进入工作状态

#### 用户故事分解（待细化）

| 编号 | 用户故事 | 优先级 | 验收标准 | 状态 |
|------|---------|--------|---------|------|
| US-001 | 自动生成学习路径 | P0 | 根据岗位生成个性化学习计划 | ⏳ |
| US-002 | 智能问答 | P0 | 7x24小时回答培训相关问题 | ⏳ |
| US-003 | 学习进度跟踪 | P0 | 实时显示学习完成进度 | ⏳ |
| US-004 | 阶段性测试 | P1 | 完成阶段后进行知识测试 | ⏳ |
| US-005 | 生成学习报告 | P1 | 输出学习进度和评估报告 | ⏳ |
| US-006 | 转人工支持 | P2 | 复杂问题转人工HR | ⏳ |

#### 逻辑推理

```
员工入职事件 → 触发学习路径初始化 → 根据岗位匹配课程
     ↓
员工提问 → RAG检索培训知识 → LLM生成答案
     ↓
置信度判断 → 高置信度直接回答 → 低置信度转人工
     ↓
完成学习阶段 → 更新进度 → 生成报告
```

### 2. 需求规格分析

#### 三闭环检查

| 闭环类型 | 检查项 | 状态 |
|---------|-------|------|
| 生命周期闭环 | 学习路径CRUD | ⏳ 待实现 |
| 数据实体闭环 | LearningPath 1:N Stage 1:N Task | ⏳ 待实现 |
| 按钮API闭环 | 所有操作有对应API | ⏳ 待实现 |

#### 字典枚举（待定义）

| 枚举 | 代码 | 值 |
|------|------|-----|
| LearningStageStatus | learning_stage_status | NOT_STARTED/IN_PROGRESS/COMPLETED |
| QuestionCategory | question_category | POLICY/PROCESS/BENEFIT/TECHNICAL |

#### API清单

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 初始化学习路径 | POST | /api/v1/onboarding/learning-path | ⏳ |
| 培训问答 | POST | /api/v1/onboarding/question | ⏳ |
| 获取学习报告 | GET | /api/v1/onboarding/report/{employeeId} | ⏳ |
| 更新学习进度 | PUT | /api/v1/onboarding/progress | ⏳ |

### 3. 执行开发设计

#### 待完成文件

| 文件 | 说明 | 状态 |
|------|------|------|
| skill.yaml | 技能定义 | ✅ 已有 |
| OnboardingAssistantService.java | 服务实现 | ⏳ 需完善 |
| OnboardingAssistantController.java | 控制器 | ⏳ 需完善 |
| 枚举类 | 学习阶段/问题分类枚举 | ⏳ 待创建 |
| 需求规格文档 | ASK-004_SPECIFICATION | ⏳ 待创建 |

---

## TASK-005 skill-project-knowledge 项目知识沉淀

### 基本信息

| 属性 | 值 |
|------|-----|
| **分类** | TBS (触发业务场景) |
| **mainFirst** | false |
| **业务语义分** | 8 |
| **状态** | ⏳ 待细化 |

### 1. 用户故事详情

#### 核心用户故事

**作为** 项目经理  
**我希望** 项目文档能够自动分类整理并形成可复用的知识资产  
**以便于** 团队成员快速获取项目知识，避免重复造轮子

#### 用户故事分解（待细化）

| 编号 | 用户故事 | 优先级 | 验收标准 | 状态 |
|------|---------|--------|---------|------|
| US-001 | 批量导入项目文档 | P0 | 支持ZIP压缩包批量上传 | ⏳ |
| US-002 | 自动文档分类 | P0 | 自动识别需求/设计/测试文档 | ⏳ |
| US-003 | 智能标签 | P0 | 自动提取文档关键词标签 | ⏳ |
| US-004 | 相似项目推荐 | P1 | 推荐相似项目供参考 | ⏳ |
| US-005 | 知识图谱生成 | P1 | 提取实体关系生成图谱 | ⏳ |
| US-006 | 项目知识检索 | P1 | 搜索项目内所有知识 | ⏳ |

#### 逻辑推理

```
项目文档上传 → 文档解析 → 内容提取
     ↓
LLM分类 → 打标签 → 存入知识库
     ↓
相似度计算 → 推荐相似项目
     ↓
实体抽取 → 关系抽取 → 知识图谱
```

### 2. 需求规格分析

#### 三闭环检查

| 闭环类型 | 检查项 | 状态 |
|---------|-------|------|
| 生命周期闭环 | 项目知识库CRUD | ⏳ 待实现 |
| 数据实体闭环 | Project 1:N Document, Document有分类标签 | ⏳ 待实现 |
| 按钮API闭环 | 所有操作有对应API | ⏳ 待实现 |

#### 字典枚举（待定义）

| 枚举 | 代码 | 值 |
|------|------|-----|
| DocumentType | document_type | REQUIREMENT/DESIGN/TEST/SUMMARY/OTHER |
| ImportTaskStatus | import_task_status | PENDING/PROCESSING/COMPLETED/FAILED |

#### API清单

| 接口 | 方法 | 路径 | 状态 |
|------|------|------|------|
| 导入项目文档 | POST | /api/v1/project-knowledge/import | ⏳ |
| 文档分类 | POST | /api/v1/project-knowledge/classify | ⏳ |
| 发现相似项目 | GET | /api/v1/project-knowledge/similar/{projectId} | ⏳ |
| 生成知识图谱 | GET | /api/v1/project-knowledge/graph/{projectId} | ⏳ |
| 查询导入任务 | GET | /api/v1/project-knowledge/task/{taskId} | ⏳ |

### 3. 执行开发设计

#### 待完成文件

| 文件 | 说明 | 状态 |
|------|------|------|
| skill.yaml | 技能定义 | ✅ 已有 |
| ProjectKnowledgeService.java | 服务实现 | ⏳ 需完善 |
| ProjectKnowledgeController.java | 控制器 | ⏳ 需完善 |
| 枚举类 | 文档类型/任务状态枚举 | ⏳ 待创建 |
| 需求规格文档 | ASK-005_SPECIFICATION | ⏳ 待创建 |

---

## 任务执行计划

### 阶段一：已完成任务（TASK-001, TASK-002）

| 任务 | 用户故事 | 需求规格 | 开发设计 | 测试验证 |
|------|---------|---------|---------|---------|
| TASK-001 | ✅ | ✅ | ✅ | ⏳ |
| TASK-002 | ✅ | ✅ | ✅ | ⏳ |

### 阶段二：待细化任务（TASK-003, TASK-004, TASK-005）

| 任务 | 用户故事 | 需求规格 | 开发设计 | 预计完成 |
|------|---------|---------|---------|---------|
| TASK-003 | ⏳ | ⏳ | ⏳ | - |
| TASK-004 | ⏳ | ⏳ | ⏳ | - |
| TASK-005 | ⏳ | ⏳ | ⏳ | - |

### 下一步行动

1. **TASK-003 知识共享管理**
   - 细化用户故事详情
   - 创建 ASK-003 需求规格文档
   - 完善后端API实现
   - 创建字典枚举

2. **TASK-004 新人培训助手**
   - 细化用户故事详情
   - 创建 ASK-004 需求规格文档
   - 完善后端API实现
   - 创建字典枚举

3. **TASK-005 项目知识沉淀**
   - 细化用户故事详情
   - 创建 ASK-005 需求规格文档
   - 完善后端API实现
   - 创建字典枚举

---

## 附录

### 分类说明

| 分类 | 全称 | 说明 | 特点 |
|------|------|------|------|
| ABS | 自驱业务场景 | 业务驱动的主动场景 | mainFirst=true, 高业务语义分 |
| ASS | 自驱系统场景 | 系统驱动的自动化场景 | mainFirst=true, 中业务语义分 |
| TBS | 触发业务场景 | 用户触发的被动场景 | mainFirst=false, 高业务语义分 |

### 相关文档

| 文档 | 路径 |
|------|------|
| ASK-001 需求规格 | docs/v2.3/ASK-001_SKILL_DOCUMENT_ASSISTANT_SPECIFICATION.md |
| ASK-002 需求规格 | docs/v2.3/ASK-002_SKILL_MEETING_MINUTES_SPECIFICATION.md |
| 新功能开发手册 | .trae/skills/new-feature-guide |

### 变更记录

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|---------|------|
| v1.0 | 2026-03-07 | 创建任务列表 | Ooder Team |
