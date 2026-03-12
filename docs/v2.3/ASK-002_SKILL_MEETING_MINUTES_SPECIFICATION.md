# TASK-002 skill-meeting-minutes 会议纪要整理 需求规格

> **版本**: v1.0  
> **分类**: TBS (触发业务场景)  
> **创建日期**: 2026-03-07  
> **状态**: 需求细化中

---

## 一、用户故事

### 1.1 核心用户故事

**作为** 项目团队成员  
**我希望** 能够快速将会议录音或笔记整理成结构化的会议纪要  
**以便于** 追踪会议决策和行动项，并归档到知识库供后续查阅

### 1.2 用户故事分解

| 编号 | 用户故事 | 优先级 | 验收标准 |
|------|---------|--------|---------|
| US-001 | 上传会议内容（文本/录音转文字） | P0 | 支持粘贴文本、上传文件 |
| US-002 | 自动提取会议主题和摘要 | P0 | LLM自动生成标题和摘要 |
| US-003 | 自动提取关键决策 | P0 | 识别并列出所有决策点 |
| US-004 | 自动提取行动项 | P0 | 提取任务、责任人、截止时间 |
| US-005 | 手动编辑和调整纪要 | P1 | 支持在线编辑所有字段 |
| US-006 | 导出多种格式 | P1 | 支持Markdown/Word/PDF |
| US-007 | 归档到知识库 | P2 | 关联项目知识库自动归档 |
| US-008 | 行动项跟踪提醒 | P2 | 截止时间提醒通知 |

---

## 二、三闭环检查设计

### 2.1 生命周期闭环

```
┌──────────┐    创建    ┌──────────┐    编辑    ┌──────────┐
│   草稿   │ ────────► │   完成   │ ────────► │  已归档  │
│  DRAFT   │           │ COMPLETED│           │ ARCHIVED │
└──────────┘           └──────────┘           └──────────┘
      │                      │                      │
      │                      │                      │
      ▼                      ▼                      ▼
   可删除               可编辑/导出            只读/可查询
```

**状态定义**:

| 状态 | 代码 | 说明 | 可执行操作 |
|------|------|------|-----------|
| 草稿 | DRAFT | 初始创建，内容待确认 | 编辑、删除、发布 |
| 完成 | COMPLETED | 内容已确认，可使用 | 编辑、导出、归档 |
| 已归档 | ARCHIVED | 已存入知识库 | 查看、导出 |

**生命周期API检查表**:

| 生命周期阶段 | API | 方法 | 路径 | 状态 |
|-------------|-----|------|------|------|
| 创建 | organizeMeeting | POST | /api/v1/meeting-minutes/organize | ✅ |
| 查询列表 | listMinutes | GET | /api/v1/meeting-minutes | ❌ 待实现 |
| 查询详情 | getMinutes | GET | /api/v1/meeting-minutes/{id} | ❌ 待实现 |
| 更新 | updateMinutes | PUT | /api/v1/meeting-minutes/{id} | ❌ 待实现 |
| 状态变更 | updateStatus | PUT | /api/v1/meeting-minutes/{id}/status | ❌ 待实现 |
| 删除 | deleteMinutes | DELETE | /api/v1/meeting-minutes/{id} | ❌ 待实现 |

### 2.2 数据实体关系闭环

```
┌─────────────────┐
│  MeetingMinutes │
│   会议纪要主表   │
├─────────────────┤
│ minutesId (PK)  │
│ title           │
│ summary         │
│ status          │
│ projectId (FK)  │
│ kbId (FK)       │
│ createdBy       │
│ createdAt       │
└────────┬────────┘
         │
         │ 1:N
         │
         ▼
┌─────────────────┐      ┌─────────────────┐
│   ActionItem    │      │    Decision     │
│    行动项表      │      │    决策表       │
├─────────────────┤      ├─────────────────┤
│ actionId (PK)   │      │ decisionId (PK) │
│ minutesId (FK)  │      │ minutesId (FK)  │
│ task            │      │ content         │
│ assignee        │      │ priority        │
│ deadline        │      │ seq             │
│ status          │      └─────────────────┘
│ seq             │
└─────────────────┘
         │
         │ N:1
         │
         ▼
┌─────────────────┐
│    Project      │
│    项目表       │
│ (外部关联)      │
└─────────────────┘
```

**数据一致性检查项**:

| 检查项 | 要求 | 实现状态 |
|--------|------|---------|
| 级联创建 | 创建纪要时同步创建行动项和决策 | ✅ |
| 级联删除 | 删除纪要时同步删除关联数据 | ❌ 待实现 |
| 外键验证 | projectId/kbId 存在性验证 | ❌ 待实现 |
| 数据完整性 | 行动项必须关联有效纪要 | ❌ 待实现 |

### 2.3 按钮事件和API闭环

**前端页面按钮闭环检查表**:

| 页面 | 按钮 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|------|---------|---------|---------|---------|
| 纪要列表 | 新建纪要 | `createMinutes()` | POST /organize | MeetingMinutesController.organizeMeeting() | ✅ |
| 纪要列表 | 查看详情 | `viewMinutes(id)` | GET /{id} | MeetingMinutesController.getMinutes() | ❌ |
| 纪要列表 | 删除纪要 | `deleteMinutes(id)` | DELETE /{id} | MeetingMinutesController.deleteMinutes() | ❌ |
| 纪要详情 | 编辑保存 | `saveMinutes()` | PUT /{id} | MeetingMinutesController.updateMinutes() | ❌ |
| 纪要详情 | 发布纪要 | `publishMinutes()` | PUT /{id}/status | MeetingMinutesController.updateStatus() | ❌ |
| 纪要详情 | 导出MD | `exportMarkdown()` | POST /export/markdown | MeetingMinutesController.exportMinutes() | ✅ |
| 纪要详情 | 导出Word | `exportWord()` | POST /export/docx | MeetingMinutesController.exportMinutes() | ⚠️ 格式待完善 |
| 纪要详情 | 归档 | `archiveToKb()` | POST /archive | MeetingMinutesController.archiveToKb() | ✅ |
| 行动项 | 更新状态 | `updateActionStatus()` | PUT /actions/{id}/status | ActionController.updateStatus() | ❌ |

---

## 三、字典枚举定义

### 3.1 会议纪要状态枚举

```java
@Dict(code = "minutes_status", name = "会议纪要状态", description = "会议纪要的生命周期状态")
public enum MinutesStatus implements DictItem {
    
    DRAFT("DRAFT", "草稿", "会议纪要草稿状态，内容待确认", "ri-draft-line", 1),
    COMPLETED("COMPLETED", "完成", "会议纪要已完成，可导出归档", "ri-checkbox-circle-line", 2),
    ARCHIVED("ARCHIVED", "已归档", "会议纪要已归档到知识库", "ri-archive-line", 3);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    MinutesStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
```

### 3.2 行动项状态枚举

```java
@Dict(code = "action_item_status", name = "行动项状态", description = "行动项的执行状态")
public enum ActionItemStatus implements DictItem {
    
    PENDING("PENDING", "待办", "行动项待处理", "ri-time-line", 1),
    IN_PROGRESS("IN_PROGRESS", "进行中", "行动项正在执行", "ri-loader-line", 2),
    COMPLETED("COMPLETED", "已完成", "行动项已完成", "ri-checkbox-circle-line", 3),
    CANCELLED("CANCELLED", "已取消", "行动项已取消", "ri-close-circle-line", 4),
    OVERDUE("OVERDUE", "已逾期", "行动项已逾期", "ri-error-warning-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ActionItemStatus(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
```

### 3.3 会议类型枚举

```java
@Dict(code = "meeting_type", name = "会议类型", description = "会议的类型分类")
public enum MeetingType implements DictItem {
    
    REGULAR("REGULAR", "例会", "定期召开的常规会议", "ri-calendar-line", 1),
    PROJECT("PROJECT", "项目会", "项目相关会议", "ri-folder-line", 2),
    DECISION("DECISION", "决策会", "重要决策会议", "ri-checkbox-line", 3),
    BRAINSTORM("BRAINSTORM", "头脑风暴", "创意讨论会议", "ri-lightbulb-line", 4),
    REVIEW("REVIEW", "评审会", "方案/代码评审会议", "ri-eye-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    MeetingType(String code, String name, String description, String icon, int sort) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.sort = sort;
    }

    @Override
    public String getCode() { return code; }
    @Override
    public String getName() { return name; }
    @Override
    public String getDescription() { return description; }
    @Override
    public String getIcon() { return icon; }
    @Override
    public int getSort() { return sort; }
}
```

---

## 四、API接口设计

### 4.1 现有API（已实现）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 整理会议 | POST | /api/v1/meeting-minutes/organize | 整理会议内容生成纪要 |
| 提取行动项 | POST | /api/v1/meeting-minutes/action-items | 从内容提取行动项 |
| 归档 | POST | /api/v1/meeting-minutes/archive | 归档到知识库 |
| 导出 | POST | /api/v1/meeting-minutes/export/{format} | 导出指定格式 |

### 4.2 待实现API

#### 4.2.1 查询纪要列表

```
GET /api/v1/meeting-minutes

Query Parameters:
  - projectId: 项目ID（可选）
  - status: 状态过滤（可选）
  - keyword: 关键词搜索（可选）
  - pageNum: 页码（默认1）
  - pageSize: 每页数量（默认20）

Response:
{
  "code": 200,
  "data": {
    "list": [
      {
        "minutesId": "mm-001",
        "title": "项目进度讨论会",
        "meetingDate": "2026-03-06",
        "status": "COMPLETED",
        "actionItemCount": 3,
        "createdBy": "user-001",
        "createdAt": "2026-03-06 14:30:00"
      }
    ],
    "total": 100,
    "pageNum": 1,
    "pageSize": 20
  }
}
```

#### 4.2.2 查询纪要详情

```
GET /api/v1/meeting-minutes/{minutesId}

Response:
{
  "code": 200,
  "data": {
    "minutesId": "mm-001",
    "title": "项目进度讨论会",
    "meetingDate": "2026-03-06",
    "location": "会议室A",
    "summary": "本次会议讨论了项目进度...",
    "decisions": [
      {"decisionId": "d-001", "content": "决定增加开发资源", "priority": "HIGH"}
    ],
    "actionItems": [
      {
        "actionId": "a-001",
        "task": "完成API开发",
        "assignee": "张三",
        "deadline": "2026-03-10",
        "status": "PENDING"
      }
    ],
    "participants": ["张三", "李四", "王五"],
    "projectId": "proj-001",
    "status": "COMPLETED",
    "createdBy": "user-001",
    "createdAt": "2026-03-06 14:30:00"
  }
}
```

#### 4.2.3 更新纪要

```
PUT /api/v1/meeting-minutes/{minutesId}

Request Body:
{
  "title": "更新后的标题",
  "summary": "更新后的摘要",
  "decisions": [...],
  "actionItems": [...]
}

Response:
{
  "code": 200,
  "message": "更新成功"
}
```

#### 4.2.4 更新状态

```
PUT /api/v1/meeting-minutes/{minutesId}/status

Request Body:
{
  "status": "COMPLETED"
}

Response:
{
  "code": 200,
  "message": "状态更新成功"
}
```

#### 4.2.5 删除纪要

```
DELETE /api/v1/meeting-minutes/{minutesId}

Response:
{
  "code": 200,
  "message": "删除成功"
}
```

#### 4.2.6 更新行动项状态

```
PUT /api/v1/meeting-minutes/actions/{actionId}/status

Request Body:
{
  "status": "COMPLETED"
}

Response:
{
  "code": 200,
  "message": "状态更新成功"
}
```

---

## 五、UI/UE设计

### 5.1 页面结构

```
┌─────────────────────────────────────────────────────────────┐
│                        顶部导航栏                            │
├──────────────┬──────────────────────────────────────────────┤
│              │                                              │
│   侧边栏      │              主内容区                        │
│              │                                              │
│  - 我的纪要   │   ┌────────────────────────────────────┐   │
│  - 待办行动   │   │                                    │   │
│  - 已归档     │   │         纪要列表/详情               │   │
│              │   │                                    │   │
│  ──────────  │   │                                    │   │
│              │   └────────────────────────────────────┘   │
│  + 新建纪要   │                                              │
│              │                                              │
└──────────────┴──────────────────────────────────────────────┘
```

### 5.2 新建纪要页面

```
┌─────────────────────────────────────────────────────────────┐
│  新建会议纪要                                          [×]  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  会议类型:  [例会 ▼]    会议日期:  [2026-03-07 📅]          │
│                                                             │
│  关联项目:  [选择项目 ▼]          地点:  [____________]     │
│                                                             │
│  参与人:    [张三] [李四] [王五] [+ 添加]                   │
│                                                             │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│  会议内容:                                                  │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                                                     │   │
│  │  请粘贴会议记录、录音转文字或笔记内容...              │   │
│  │                                                     │   │
│  │                                                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  或  [📁 上传文件] 支持 txt, docx, pdf                      │
│                                                             │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│                    [取消]  [智能整理]                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 纪要详情页面

```
┌─────────────────────────────────────────────────────────────┐
│  ← 返回列表     项目进度讨论会                               │
│                                                             │
│  状态: [已完成 ✓]    日期: 2026-03-06    地点: 会议室A      │
│                                                             │
│  [编辑] [导出 ▼] [归档]                                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  📋 会议摘要                                                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 本次会议讨论了项目进度和下一步计划，确定了关键里程碑   │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ✅ 关键决策 (2)                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 1. 决定增加开发资源投入                              │   │
│  │ 2. 确定下周发布v1.2版本                              │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  📌 行动项 (3)                                    状态筛选[全部▼] │
│  ┌───────────────────────────────────────────────────────┐ │
│  │ # │ 任务          │ 责任人 │ 截止时间  │ 状态        │ │
│  │───│───────────────│────────│───────────│─────────────│ │
│  │ 1 │ 完成API开发   │ 张三   │ 2026-03-10│ ⏳ 进行中   │ │
│  │ 2 │ 编写测试用例  │ 李四   │ 2026-03-12│ 📋 待办     │ │
│  │ 3 │ 更新文档      │ 王五   │ 2026-03-08│ ✅ 已完成   │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  👥 参与人: 张三, 李四, 王五                                 │
│                                                             │
│  📅 下次会议: 2026-03-13 版本发布评审                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.4 我的待办页面

```
┌─────────────────────────────────────────────────────────────┐
│  我的待办行动项                           筛选: [全部状态 ▼] │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 🔴 完成API开发                                         ││
│  │    来源: 项目进度讨论会  │  截止: 2026-03-10 (还剩3天) ││
│  │    [标记完成] [查看纪要]                               ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 🟡 编写测试用例                                        ││
│  │    来源: 项目进度讨论会  │  截止: 2026-03-12 (还剩5天) ││
│  │    [标记完成] [查看纪要]                               ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ ⚫ 准备演示材料                                        ││
│  │    来源: 周例会         │  截止: 2026-03-08 (明天!)   ││
│  │    [标记完成] [查看纪要]                               ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 六、外部集成需求

### 6.1 依赖服务

| 服务 | 用途 | 必需性 | 当前状态 |
|------|------|--------|---------|
| skill-llm-conversation | LLM对话服务，用于智能整理 | 必需 | ✅ 已依赖 |
| skill-knowledge-base | 知识库服务，用于归档 | 可选 | ✅ 已依赖 |

### 6.2 可选集成

| 集成项 | 用途 | 优先级 |
|--------|------|--------|
| 项目管理服务 | 关联项目、同步任务 | P1 |
| 用户服务 | 获取用户信息、@提醒 | P1 |
| 通知服务 | 行动项截止提醒 | P2 |
| 日历服务 | 同步会议日程 | P2 |

---

## 七、开发任务清单

### 7.1 后端开发任务

| 任务 | 说明 | 优先级 | 状态 |
|------|------|--------|------|
| 添加字典枚举 | MinutesStatus, ActionItemStatus, MeetingType | P0 | ❌ |
| 实现CRUD接口 | 列表、详情、更新、删除 | P0 | ❌ |
| 实现状态变更接口 | 发布、归档状态转换 | P0 | ❌ |
| 实现行动项管理 | 独立CRUD、状态更新 | P1 | ❌ |
| 添加分页查询 | 支持多条件筛选 | P1 | ❌ |
| 数据持久化 | 添加数据库存储 | P1 | ❌ |

### 7.2 前端开发任务

| 任务 | 说明 | 优先级 | 状态 |
|------|------|--------|------|
| 创建UI技能包 | skill-meeting-minutes-ui | P0 | ❌ |
| 纪要列表页 | 展示、筛选、分页 | P0 | ❌ |
| 新建纪要页 | 表单、上传、智能整理 | P0 | ❌ |
| 纪要详情页 | 展示、编辑、导出 | P0 | ❌ |
| 我的待办页 | 行动项列表、状态更新 | P1 | ❌ |
| 字典缓存集成 | 使用DictCache | P1 | ❌ |

---

## 八、验收标准

### 8.1 功能验收

- [ ] 能够粘贴会议内容并智能生成结构化纪要
- [ ] 能够正确提取会议主题、摘要、决策、行动项
- [ ] 支持手动编辑所有字段
- [ ] 支持导出 Markdown/Word/PDF 格式
- [ ] 支持归档到知识库
- [ ] 行动项状态可独立更新
- [ ] 支持按项目、状态、关键词筛选

### 8.2 三闭环验收

- [ ] 生命周期闭环：创建→查询→更新→删除 API完整
- [ ] 数据实体闭环：纪要与行动项/决策关联正确
- [ ] 按钮API闭环：每个操作都有对应API调用

### 8.3 性能验收

- [ ] 智能整理响应时间 < 10秒
- [ ] 列表查询响应时间 < 500ms
- [ ] 支持并发处理

---

## 九、附录

### 9.1 相关文档

| 文档 | 路径 |
|------|------|
| 新功能开发手册 | .trae/skills/new-feature-guide |
| 公共技术规范 | docs/COMMON_TECHNICAL_SPECIFICATION.md |
| 字典表规范 | docs/DICT_SPECIFICATION.md |

### 9.2 变更记录

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|---------|------|
| v1.0 | 2026-03-07 | 初始版本 | Ooder Team |
