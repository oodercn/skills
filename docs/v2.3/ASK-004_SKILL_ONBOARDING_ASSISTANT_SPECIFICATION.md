# TASK-004 skill-onboarding-assistant 新人培训助手 需求规格

> **版本**: v1.0  
> **分类**: ABS (自驱业务场景)  
> **创建日期**: 2026-03-07  
> **状态**: 需求细化中

---

## 一、用户故事

### 1.1 核心用户故事

**作为** 新入职员工  
**我希望** 有一个智能助手帮助我快速了解公司和岗位知识  
**以便于** 缩短适应期，快速进入工作状态

### 1.2 用户故事分解

| 编号 | 用户故事 | 优先级 | 验收标准 |
|------|---------|--------|---------|
| US-001 | 自动生成学习路径 | P0 | 根据岗位自动生成个性化学习计划 |
| US-002 | 智能问答 | P0 | 7x24小时回答培训相关问题 |
| US-003 | 学习进度跟踪 | P0 | 实时显示学习完成进度 |
| US-004 | 阶段性测试 | P1 | 完成阶段后进行知识测试 |
| US-005 | 生成学习报告 | P1 | 输出学习进度和评估报告 |
| US-006 | 转人工支持 | P2 | 复杂问题自动转人工HR |
| US-007 | 学习提醒 | P2 | 定期提醒学习进度 |
| US-008 | 同事推荐 | P2 | 推荐可咨询的资深同事 |

### 1.3 逻辑推理流程

```
员工入职事件 → 触发学习路径初始化
     ↓
获取员工岗位信息 → 匹配岗位课程模板 → 生成个性化学习路径
     ↓
员工提问 → RAG检索培训知识库 → LLM生成答案
     ↓
置信度判断 → 高置信度(≥0.7)直接回答 → 低置信度转人工HR
     ↓
完成学习阶段 → 更新进度 → 触发下一阶段/生成报告
```

---

## 二、三闭环检查设计

### 2.1 生命周期闭环

```
┌──────────┐    入职    ┌──────────┐    学习    ┌──────────┐
│   待开始  │ ────────► │  进行中   │ ────────► │  已完成   │
│ PENDING  │           │IN_PROGRESS│           │COMPLETED │
└──────────┘           └──────────┘           └──────────┘
      │                      │                      │
      │                      │                      │
      ▼                      ▼                      ▼
   可开始               可暂停/继续            可查看报告
```

**学习阶段状态定义**:

| 状态 | 代码 | 说明 | 可执行操作 |
|------|------|------|-----------|
| 待开始 | PENDING | 阶段未开始 | 开始学习 |
| 进行中 | IN_PROGRESS | 正在学习 | 暂停、完成 |
| 已暂停 | PAUSED | 学习暂停 | 继续 |
| 已完成 | COMPLETED | 阶段完成 | 查看结果、重学 |

**生命周期API检查表**:

| 生命周期阶段 | API | 方法 | 路径 | 状态 |
|-------------|-----|------|------|------|
| 初始化路径 | initLearningPath | POST | /api/v1/onboarding/learning-path | ✅ |
| 查询路径 | getLearningPath | GET | /api/v1/onboarding/learning-path/{employeeId} | ❌ 待实现 |
| 更新进度 | updateProgress | PUT | /api/v1/onboarding/progress | ✅ |
| 智能问答 | askQuestion | POST | /api/v1/onboarding/question | ✅ |
| 获取报告 | getReport | GET | /api/v1/onboarding/report/{employeeId} | ✅ |
| 查询历史 | getHistory | GET | /api/v1/onboarding/history/{employeeId} | ❌ 待实现 |

### 2.2 数据实体关系闭环

```
┌─────────────────┐
│  LearningPath   │
│   学习路径表     │
├─────────────────┤
│ pathId (PK)     │
│ employeeId      │
│ position        │
│ department      │
│ totalStages     │
│ completedStages │
│ status          │
│ createdAt       │
└────────┬────────┘
         │
         │ 1:N
         │
         ▼
┌─────────────────┐      ┌─────────────────┐
│  LearningStage  │      │  QuestionLog    │
│   学习阶段表     │      │   问答日志表     │
├─────────────────┤      ├─────────────────┤
│ stageId (PK)    │      │ logId (PK)      │
│ pathId (FK)     │      │ employeeId      │
│ stageName       │      │ question        │
│ stageType       │      │ answer          │
│ content         │      │ confidence      │
│ duration        │      │ needHuman       │
│ status          │      │ askedAt         │
│ seq             │      └─────────────────┘
│ startedAt       │
│ completedAt     │
└────────┬────────┘
         │
         │ 1:N
         │
         ▼
┌─────────────────┐
│  StageTask      │
│   阶段任务表     │
├─────────────────┤
│ taskId (PK)     │
│ stageId (FK)    │
│ taskName        │
│ taskType        │
│ content         │
│ status          │
│ seq             │
└─────────────────┘
```

**数据一致性检查项**:

| 检查项 | 要求 | 实现状态 |
|--------|------|---------|
| 级联创建 | 创建路径时生成阶段和任务 | ❌ 待实现 |
| 进度计算 | completedStages/totalStages | ❌ 待实现 |
| 外键验证 | employeeId 存在性验证 | ❌ 待实现 |

### 2.3 按钮事件和API闭环

**前端页面按钮闭环检查表**:

| 页面 | 按钮 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|------|---------|---------|---------|---------|
| 学习路径 | 开始学习 | `startLearning()` | PUT /progress | OnboardingController.updateProgress() | ✅ |
| 学习路径 | 暂停学习 | `pauseLearning()` | PUT /progress | OnboardingController.updateProgress() | ✅ |
| 学习路径 | 完成阶段 | `completeStage()` | PUT /progress | OnboardingController.updateProgress() | ✅ |
| 智能问答 | 提问 | `askQuestion()` | POST /question | OnboardingController.askQuestion() | ✅ |
| 学习报告 | 查看报告 | `getReport()` | GET /report/{id} | OnboardingController.getReport() | ✅ |
| 学习报告 | 导出报告 | `exportReport()` | GET /report/{id}/export | OnboardingController.exportReport() | ❌ |

---

## 三、字典枚举定义

### 3.1 学习阶段状态枚举

```java
@Dict(code = "learning_stage_status", name = "学习阶段状态", description = "学习阶段的执行状态")
public enum LearningStageStatus implements DictItem {
    
    PENDING("PENDING", "待开始", "阶段未开始", "ri-time-line", 1),
    IN_PROGRESS("IN_PROGRESS", "进行中", "正在学习", "ri-play-circle-line", 2),
    PAUSED("PAUSED", "已暂停", "学习暂停", "ri-pause-circle-line", 3),
    COMPLETED("COMPLETED", "已完成", "阶段完成", "ri-checkbox-circle-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    LearningStageStatus(String code, String name, String description, String icon, int sort) {
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

### 3.2 阶段类型枚举

```java
@Dict(code = "stage_type", name = "阶段类型", description = "学习阶段的类型分类")
public enum StageType implements DictItem {
    
    ORIENTATION("ORIENTATION", "入职引导", "公司文化、制度介绍", "ri-home-line", 1),
    POLICY("POLICY", "制度学习", "公司规章制度学习", "ri-file-list-line", 2),
    SKILL("SKILL", "技能培训", "岗位技能培训", "ri-tools-line", 3),
    PROJECT("PROJECT", "项目实践", "实际项目参与", "ri-folder-line", 4),
    ASSESSMENT("ASSESSMENT", "考核评估", "学习效果评估", "ri-checkbox-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    StageType(String code, String name, String description, String icon, int sort) {
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

### 3.3 问题分类枚举

```java
@Dict(code = "question_category", name = "问题分类", description = "培训问题的分类")
public enum QuestionCategory implements DictItem {
    
    POLICY("POLICY", "制度政策", "公司制度、政策相关问题", "ri-file-list-line", 1),
    PROCESS("PROCESS", "流程规范", "工作流程、规范相关问题", "ri-flow-line", 2),
    BENEFITS("BENEFITS", "福利待遇", "薪资福利相关问题", "ri-money-line", 3),
    TECHNICAL("TECHNICAL", "技术问题", "岗位技术相关问题", "ri-code-line", 4),
    GENERAL("GENERAL", "通用问题", "其他通用问题", "ri-question-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    QuestionCategory(String code, String name, String description, String icon, int sort) {
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
| 初始化学习路径 | POST | /api/v1/onboarding/learning-path | 根据岗位生成学习路径 |
| 智能问答 | POST | /api/v1/onboarding/question | 培训问答 |
| 获取学习报告 | GET | /api/v1/onboarding/report/{employeeId} | 获取学习报告 |
| 更新学习进度 | PUT | /api/v1/onboarding/progress | 更新进度 |

### 4.2 待实现API

#### 4.2.1 查询学习路径

```
GET /api/v1/onboarding/learning-path/{employeeId}

Response:
{
  "code": 200,
  "data": {
    "pathId": "path-001",
    "employeeId": "emp-001",
    "employeeName": "张三",
    "position": "Java开发工程师",
    "department": "研发部",
    "totalStages": 5,
    "completedStages": 2,
    "progress": 40,
    "status": "IN_PROGRESS",
    "stages": [
      {
        "stageId": "stage-001",
        "stageName": "入职引导",
        "stageType": "ORIENTATION",
        "status": "COMPLETED",
        "duration": 2,
        "seq": 1
      },
      {
        "stageId": "stage-002",
        "stageName": "制度学习",
        "stageType": "POLICY",
        "status": "IN_PROGRESS",
        "duration": 3,
        "seq": 2
      }
    ],
    "createdAt": "2026-03-01 09:00:00"
  }
}
```

#### 4.2.2 查询问答历史

```
GET /api/v1/onboarding/history/{employeeId}

Query Parameters:
  - pageNum: 页码
  - pageSize: 每页数量

Response:
{
  "code": 200,
  "data": {
    "list": [
      {
        "logId": "log-001",
        "question": "公司年假制度是怎样的？",
        "answer": "根据公司规定，员工入职满一年后...",
        "confidence": 0.95,
        "needHuman": false,
        "askedAt": "2026-03-05 14:30:00"
      }
    ],
    "total": 20
  }
}
```

#### 4.2.3 导出学习报告

```
GET /api/v1/onboarding/report/{employeeId}/export?format=pdf

Response:
{
  "code": 200,
  "data": {
    "format": "pdf",
    "content": "...",
    "filename": "学习报告_张三_20260307.pdf"
  }
}
```

#### 4.2.4 获取待回答问题（HR视角）

```
GET /api/v1/onboarding/pending-questions

Response:
{
  "code": 200,
  "data": [
    {
      "logId": "log-005",
      "employeeId": "emp-002",
      "employeeName": "李四",
      "question": "项目组的技术栈是什么？",
      "askedAt": "2026-03-07 10:00:00"
    }
  ]
}
```

#### 4.2.5 回答问题（HR视角）

```
POST /api/v1/onboarding/answer

Request Body:
{
  "logId": "log-005",
  "answer": "我们项目组主要使用Java技术栈..."
}

Response:
{
  "code": 200,
  "message": "回答已发送"
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
│  - 我的学习   │   ┌────────────────────────────────────┐   │
│  - 智能问答   │   │                                    │   │
│  - 学习报告   │   │     学习路径/问答/报告内容          │   │
│              │   │                                    │   │
│              │   └────────────────────────────────────┘   │
│              │                                              │
└──────────────┴──────────────────────────────────────────────┘
```

### 5.2 学习路径页面

```
┌─────────────────────────────────────────────────────────────┐
│  我的学习路径                              进度: 40% ████░░░░ │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ ✅ 1. 入职引导                          已完成  2天     ││
│  │    公司文化、组织架构介绍                              ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 🔄 2. 制度学习                          进行中  3天     ││
│  │    公司规章制度、考勤制度学习                          ││
│  │    [继续学习]                                          ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ ⏳ 3. 技能培训                          待开始  5天     ││
│  │    岗位技能、工具使用培训                              ││
│  │    [开始学习]                                          ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 智能问答页面

```
┌─────────────────────────────────────────────────────────────┐
│  智能问答                                                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 👤 公司年假制度是怎样的？                              ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 🤖 根据公司规定，员工入职满一年后可享受5天年假，       ││
│  │    每增加一年工龄增加1天，上限15天。                   ││
│  │    年假需提前3个工作日申请...                          ││
│  │                                                        ││
│  │ 📚 来源: 《员工手册-假期管理》                         ││
│  │                                                        ││
│  │ [👍 有帮助] [👎 无帮助] [转人工]                       ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ [请输入您的问题...]                          [发送]    ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.4 学习报告页面

```
┌─────────────────────────────────────────────────────────────┐
│  学习报告                                    [导出PDF]      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  员工: 张三    岗位: Java开发工程师    部门: 研发部        │
│                                                             │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│  📊 学习进度                                                │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 总进度: ████████████░░░░░░░░ 60%                       ││
│  │ 已完成阶段: 3/5                                        ││
│  │ 学习时长: 10天 / 预计15天                              ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  📝 阶段完成情况                                            │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 阶段          │ 状态   │ 完成时间   │ 评估分数        ││
│  │───────────────│────────│────────────│────────────────││
│  │ 入职引导      │ ✅完成 │ 2026-03-02 │ 95分           ││
│  │ 制度学习      │ ✅完成 │ 2026-03-05 │ 88分           ││
│  │ 技能培训      │ ✅完成 │ 2026-03-10 │ 92分           ││
│  │ 项目实践      │ 🔄进行 │ -          │ -              ││
│  │ 考核评估      │ ⏳待定 │ -          │ -              ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  💬 问答统计                                                │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 总提问次数: 15                                         ││
│  │ 自动回答: 12 (80%)                                     ││
│  │ 转人工: 3 (20%)                                        ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 六、外部集成需求

### 6.1 依赖服务

| 服务 | 用途 | 必需性 | 当前状态 |
|------|------|--------|---------|
| skill-knowledge-base | 知识库核心服务 | 必需 | ✅ 已依赖 |
| skill-llm-conversation | LLM对话服务 | 必需 | ✅ 已依赖 |
| skill-rag | RAG检索增强 | 必需 | ✅ 已依赖 |

### 6.2 可选集成

| 集成项 | 用途 | 优先级 |
|--------|------|--------|
| 通知服务 | 学习提醒通知 | P1 |
| HR系统 | 员工信息同步 | P1 |
| 考试系统 | 阶段考核 | P2 |

---

## 七、开发任务清单

### 7.1 后端开发任务

| 任务 | 说明 | 优先级 | 状态 |
|------|------|--------|------|
| 添加字典枚举 | LearningStageStatus, StageType, QuestionCategory | P0 | ❌ |
| 实现学习路径查询 | 查询员工学习路径详情 | P0 | ❌ |
| 实现问答历史 | 查询问答历史记录 | P1 | ❌ |
| 实现报告导出 | 导出PDF格式报告 | P1 | ❌ |
| 实现HR问答 | 待回答问题列表和回答 | P2 | ❌ |

### 7.2 前端开发任务

| 任务 | 说明 | 优先级 | 状态 |
|------|------|--------|------|
| 创建UI技能包 | skill-onboarding-assistant-ui | P0 | ❌ |
| 学习路径页 | 路径展示、进度跟踪 | P0 | ❌ |
| 智能问答页 | 问答交互、来源展示 | P0 | ❌ |
| 学习报告页 | 报告展示、导出 | P1 | ❌ |

---

## 八、验收标准

### 8.1 功能验收

- [ ] 能够根据岗位自动生成学习路径
- [ ] 能够智能回答培训相关问题
- [ ] 能够实时跟踪学习进度
- [ ] 能够生成学习报告
- [ ] 低置信度问题能够转人工

### 8.2 三闭环验收

- [ ] 生命周期闭环：学习路径CRUD完整
- [ ] 数据实体闭环：Path→Stage→Task关联正确
- [ ] 按钮API闭环：每个操作都有对应API调用

---

## 九、变更记录

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|---------|------|
| v1.0 | 2026-03-07 | 初始版本 | Ooder Team |
