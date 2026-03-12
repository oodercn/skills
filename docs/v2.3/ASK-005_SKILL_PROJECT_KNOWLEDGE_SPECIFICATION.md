# TASK-005 skill-project-knowledge 项目知识沉淀 需求规格

> **版本**: v1.0  
> **分类**: TBS (触发业务场景)  
> **创建日期**: 2026-03-07  
> **状态**: 需求细化中

---

## 一、用户故事

### 1.1 核心用户故事

**作为** 项目经理  
**我希望** 项目文档能够自动分类整理并形成可复用的知识资产  
**以便于** 团队成员快速获取项目知识，避免重复造轮子

### 1.2 用户故事分解

| 编号 | 用户故事 | 优先级 | 验收标准 |
|------|---------|--------|---------|
| US-001 | 批量导入项目文档 | P0 | 支持ZIP压缩包批量上传，自动解压处理 |
| US-002 | 自动文档分类 | P0 | 自动识别需求/设计/测试/会议纪要等类型 |
| US-003 | 智能标签提取 | P0 | 自动提取文档关键词作为标签 |
| US-004 | 相似项目推荐 | P1 | 基于项目特征推荐相似历史项目 |
| US-005 | 知识图谱生成 | P1 | 提取实体关系生成可视化图谱 |
| US-006 | 项目知识检索 | P1 | 全文检索项目内所有知识内容 |
| US-007 | 导入任务管理 | P1 | 查看导入任务进度和结果 |
| US-008 | 知识复用统计 | P2 | 统计知识被复用的情况 |

### 1.3 逻辑推理流程

```
项目文档上传 → 解压缩 → 文件类型识别
     ↓
文档解析 → 内容提取 → 向量化
     ↓
LLM分类 → 打标签 → 存入知识库
     ↓
相似度计算 → 推荐相似项目
     ↓
实体抽取 → 关系抽取 → 知识图谱
```

---

## 二、三闭环检查设计

### 2.1 生命周期闭环

```
┌──────────┐    上传    ┌──────────┐    处理    ┌──────────┐
│   待处理  │ ────────► │  处理中   │ ────────► │  已完成   │
│ PENDING  │           │PROCESSING│           │COMPLETED │
└──────────┘           └──────────┘           └──────────┘
      │                      │                      │
      │                      │                      │
      ▼                      ▼                      ▼
   可取消               可查看进度            可检索/复用
                          │
                          ▼
                    ┌──────────┐
                    │   失败   │
                    │  FAILED  │
                    └──────────┘
```

**导入任务状态定义**:

| 状态 | 代码 | 说明 | 可执行操作 |
|------|------|------|-----------|
| 待处理 | PENDING | 任务已创建，等待处理 | 取消 |
| 处理中 | PROCESSING | 正在处理文档 | 查看进度 |
| 已完成 | COMPLETED | 处理完成 | 查看结果 |
| 失败 | FAILED | 处理失败 | 查看错误、重试 |

**生命周期API检查表**:

| 生命周期阶段 | API | 方法 | 路径 | 状态 |
|-------------|-----|------|------|------|
| 导入文档 | importDocs | POST | /api/v1/project-knowledge/import | ✅ |
| 查询任务 | getTask | GET | /api/v1/project-knowledge/task/{taskId} | ✅ |
| 文档分类 | classifyDoc | POST | /api/v1/project-knowledge/classify | ✅ |
| 相似项目 | getSimilar | GET | /api/v1/project-knowledge/similar/{projectId} | ✅ |
| 知识图谱 | getGraph | GET | /api/v1/project-knowledge/graph/{projectId} | ✅ |
| 项目文档列表 | listDocs | GET | /api/v1/project-knowledge/docs | ❌ 待实现 |
| 删除文档 | deleteDoc | DELETE | /api/v1/project-knowledge/docs/{docId} | ❌ 待实现 |

### 2.2 数据实体关系闭环

```
┌─────────────────┐
│     Project     │
│     项目表       │
├─────────────────┤
│ projectId (PK)  │
│ projectName     │
│ description     │
│ status          │
│ createdAt       │
└────────┬────────┘
         │
         │ 1:N
         │
         ▼
┌─────────────────┐      ┌─────────────────┐
│  ImportTask     │      │    Document     │
│   导入任务表     │      │    文档表        │
├─────────────────┤      ├─────────────────┤
│ taskId (PK)     │      │ docId (PK)      │
│ projectId (FK)  │      │ projectId (FK)  │
│ totalFiles      │      │ taskId (FK)     │
│ processedFiles  │      │ fileName        │
│ status          │      │ docType         │
│ startedAt       │      │ content         │
│ completedAt     │      │ tags            │
│ error           │      │ confidence      │
└─────────────────┘      │ vectorId        │
                         │ createdAt       │
                         └────────┬────────┘
                                  │
                                  │ N:M
                                  │
                                  ▼
                         ┌─────────────────┐
                         │   KnowledgeTag  │
                         │    知识标签表    │
                         ├─────────────────┤
                         │ tagId (PK)      │
                         │ tagName         │
                         │ category        │
                         │ usageCount      │
                         └─────────────────┘
```

**数据一致性检查项**:

| 检查项 | 要求 | 实现状态 |
|--------|------|---------|
| 级联创建 | 导入时创建文档记录 | ❌ 待实现 |
| 级联删除 | 删除项目时清理文档 | ❌ 待实现 |
| 标签关联 | 文档与标签多对多关联 | ❌ 待实现 |
| 向量索引 | 文档内容向量化存储 | ❌ 待实现 |

### 2.3 按钮事件和API闭环

**前端页面按钮闭环检查表**:

| 页面 | 按钮 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|------|---------|---------|---------|---------|
| 项目列表 | 导入文档 | `importDocs()` | POST /import | ProjectKnowledgeController.importDocs() | ✅ |
| 项目列表 | 查看进度 | `getTaskProgress()` | GET /task/{id} | ProjectKnowledgeController.getTask() | ✅ |
| 文档列表 | 文档分类 | `classifyDoc()` | POST /classify | ProjectKnowledgeController.classifyDoc() | ✅ |
| 项目详情 | 相似项目 | `getSimilarProjects()` | GET /similar/{id} | ProjectKnowledgeController.getSimilar() | ✅ |
| 项目详情 | 知识图谱 | `getKnowledgeGraph()` | GET /graph/{id} | ProjectKnowledgeController.getGraph() | ✅ |
| 文档列表 | 删除文档 | `deleteDoc()` | DELETE /docs/{id} | ProjectKnowledgeController.deleteDoc() | ❌ |
| 文档列表 | 检索文档 | `searchDocs()` | GET /docs/search | ProjectKnowledgeController.searchDocs() | ❌ |

---

## 三、字典枚举定义

### 3.1 文档类型枚举

```java
@Dict(code = "document_type", name = "文档类型", description = "项目文档的类型分类")
public enum DocumentType implements DictItem {
    
    REQUIREMENT("REQUIREMENT", "需求文档", "产品需求规格说明", "ri-file-list-line", 1),
    DESIGN("DESIGN", "设计文档", "系统设计、架构设计", "ri-layout-line", 2),
    TEST("TEST", "测试文档", "测试用例、测试报告", "ri-bug-line", 3),
    MEETING("MEETING", "会议纪要", "会议记录和决议", "ri-team-line", 4),
    SUMMARY("SUMMARY", "项目总结", "项目总结和复盘", "ri-file-text-line", 5),
    MANUAL("MANUAL", "操作手册", "用户手册、操作指南", "ri-book-line", 6),
    OTHER("OTHER", "其他文档", "其他类型文档", "ri-file-line", 7);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    DocumentType(String code, String name, String description, String icon, int sort) {
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

### 3.2 导入任务状态枚举

```java
@Dict(code = "import_task_status", name = "导入任务状态", description = "文档导入任务的状态")
public enum ImportTaskStatus implements DictItem {
    
    PENDING("PENDING", "待处理", "任务已创建，等待处理", "ri-time-line", 1),
    PROCESSING("PROCESSING", "处理中", "正在处理文档", "ri-loader-line", 2),
    COMPLETED("COMPLETED", "已完成", "处理完成", "ri-checkbox-circle-line", 3),
    FAILED("FAILED", "失败", "处理失败", "ri-error-warning-line", 4),
    CANCELLED("CANCELLED", "已取消", "任务已取消", "ri-close-circle-line", 5);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ImportTaskStatus(String code, String name, String description, String icon, int sort) {
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

### 3.3 项目状态枚举

```java
@Dict(code = "project_status", name = "项目状态", description = "项目的生命周期状态")
public enum ProjectStatus implements DictItem {
    
    PLANNING("PLANNING", "规划中", "项目规划阶段", "ri-planning-line", 1),
    ACTIVE("ACTIVE", "进行中", "项目正在进行", "ri-play-circle-line", 2),
    COMPLETED("COMPLETED", "已完成", "项目已完成", "ri-checkbox-circle-line", 3),
    ARCHIVED("ARCHIVED", "已归档", "项目已归档", "ri-archive-line", 4);

    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;

    ProjectStatus(String code, String name, String description, String icon, int sort) {
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
| 导入项目文档 | POST | /api/v1/project-knowledge/import | 批量导入文档 |
| 文档分类 | POST | /api/v1/project-knowledge/classify | 自动分类文档 |
| 发现相似项目 | GET | /api/v1/project-knowledge/similar/{projectId} | 推荐相似项目 |
| 生成知识图谱 | GET | /api/v1/project-knowledge/graph/{projectId} | 生成知识图谱 |
| 查询导入任务 | GET | /api/v1/project-knowledge/task/{taskId} | 查询任务状态 |

### 4.2 待实现API

#### 4.2.1 查询项目文档列表

```
GET /api/v1/project-knowledge/docs

Query Parameters:
  - projectId: 项目ID
  - docType: 文档类型（可选）
  - keyword: 关键词（可选）
  - pageNum: 页码
  - pageSize: 每页数量

Response:
{
  "code": 200,
  "data": {
    "list": [
      {
        "docId": "doc-001",
        "projectId": "proj-001",
        "fileName": "产品需求规格说明书v1.0.docx",
        "docType": "REQUIREMENT",
        "docTypeName": "需求文档",
        "tags": ["用户管理", "权限系统", "登录"],
        "confidence": 0.95,
        "fileSize": 1024000,
        "createdAt": "2026-03-05 10:30:00"
      }
    ],
    "total": 50
  }
}
```

#### 4.2.2 删除文档

```
DELETE /api/v1/project-knowledge/docs/{docId}

Response:
{
  "code": 200,
  "message": "文档已删除"
}
```

#### 4.2.3 搜索文档

```
GET /api/v1/project-knowledge/search

Query Parameters:
  - projectId: 项目ID
  - query: 搜索关键词
  - topK: 返回数量（默认10）

Response:
{
  "code": 200,
  "data": [
    {
      "docId": "doc-001",
      "fileName": "产品需求规格说明书v1.0.docx",
      "highlight": "...用户管理模块支持<em>权限分配</em>...",
      "score": 0.92
    }
  ]
}
```

#### 4.2.4 更新文档标签

```
PUT /api/v1/project-knowledge/docs/{docId}/tags

Request Body:
{
  "tags": ["用户管理", "权限", "RBAC"]
}

Response:
{
  "code": 200,
  "message": "标签已更新"
}
```

#### 4.2.5 获取项目统计

```
GET /api/v1/project-knowledge/stats/{projectId}

Response:
{
  "code": 200,
  "data": {
    "projectId": "proj-001",
    "totalDocs": 50,
    "docTypeDistribution": {
      "REQUIREMENT": 10,
      "DESIGN": 15,
      "TEST": 12,
      "MEETING": 8,
      "OTHER": 5
    },
    "topTags": [
      {"tag": "用户管理", "count": 15},
      {"tag": "权限", "count": 12},
      {"tag": "API", "count": 10}
    ],
    "similarProjects": 3,
    "knowledgeGraphNodes": 25,
    "knowledgeGraphEdges": 40
  }
}
```

#### 4.2.6 取消导入任务

```
PUT /api/v1/project-knowledge/task/{taskId}/cancel

Response:
{
  "code": 200,
  "message": "任务已取消"
}
```

#### 4.2.7 重试导入任务

```
POST /api/v1/project-knowledge/task/{taskId}/retry

Response:
{
  "code": 200,
  "data": {
    "taskId": "task-002",
    "status": "PENDING"
  }
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
│  - 项目列表   │   ┌────────────────────────────────────┐   │
│  - 文档管理   │   │                                    │   │
│  - 知识图谱   │   │     项目/文档/图谱内容              │   │
│  - 相似项目   │   │                                    │   │
│              │   └────────────────────────────────────┘   │
│              │                                              │
└──────────────┴──────────────────────────────────────────────┘
```

### 5.2 项目列表页面

```
┌─────────────────────────────────────────────────────────────┐
│  项目知识库                              [+ 导入文档]       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 项目名称      │ 文档数 │ 状态   │ 最近更新  │ 操作     ││
│  │───────────────│────────│────────│───────────│──────────││
│  │ 电商平台v2.0  │ 50     │ 🟢进行 │ 2026-03-07│ [详情]   ││
│  │ CRM系统       │ 35     │ ✅完成 │ 2026-03-05│ [详情]   ││
│  │ OA办公系统    │ 28     │ 📦归档 │ 2026-02-28│ [详情]   ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 文档管理页面

```
┌─────────────────────────────────────────────────────────────┐
│  电商平台v2.0 - 文档管理                                     │
│  [全部 ▼] [需求] [设计] [测试] [会议] [其他]                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  搜索: [________________] [搜索]                            │
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 📄 产品需求规格说明书v1.0.docx                         ││
│  │    类型: 需求文档  │ 标签: 用户管理, 权限, 登录        ││
│  │    置信度: 95%  │ 大小: 1.2MB  │ 2026-03-05           ││
│  │    [查看] [编辑标签] [删除]                            ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 📄 系统架构设计文档.md                                 ││
│  │    类型: 设计文档  │ 标签: 微服务, 架构, 数据库        ││
│  │    置信度: 88%  │ 大小: 256KB  │ 2026-03-06           ││
│  │    [查看] [编辑标签] [删除]                            ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.4 导入进度弹窗

```
┌─────────────────────────────────────────────────────────────┐
│  导入进度                                             [×]   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  项目: 电商平台v2.0                                         │
│                                                             │
│  总文件数: 50                                               │
│  已处理: 35                                                 │
│  进度: ████████████░░░░░░░░ 70%                            │
│                                                             │
│  当前处理: 测试用例-用户模块.xlsx                           │
│                                                             │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│  已完成:                                                    │
│  ✅ 产品需求规格说明书v1.0.docx                             │
│  ✅ 系统架构设计文档.md                                     │
│  ✅ API接口文档.pdf                                         │
│  ...                                                        │
│                                                             │
│  ─────────────────────────────────────────────────────────  │
│                                                             │
│                    [取消导入]  [后台运行]                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.5 知识图谱页面

```
┌─────────────────────────────────────────────────────────────┐
│  电商平台v2.0 - 知识图谱                    [全屏] [导出]   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │                                                        ││
│  │         ┌──────────┐                                   ││
│  │         │ 用户管理 │                                   ││
│  │         └────┬─────┘                                   ││
│  │              │                                         ││
│  │    ┌─────────┼─────────┐                              ││
│  │    ▼         ▼         ▼                              ││
│  │ ┌──────┐ ┌──────┐ ┌──────┐                           ││
│  │ │ 登录 │ │ 权限 │ │ 角色 │                           ││
│  │ └──┬───┘ └──┬───┘ └──┬───┘                           ││
│  │    │        │        │                                ││
│  │    ▼        ▼        ▼                                ││
│  │ ┌──────┐ ┌──────┐ ┌──────┐                           ││
│  │ │认证  │ │RBAC  │ │管理  │                           ││
│  │ └──────┘ └──────┘ └──────┘                           ││
│  │                                                        ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  节点: 25  │ 边: 40  │ 聚类: 5                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.6 相似项目页面

```
┌─────────────────────────────────────────────────────────────┐
│  相似项目推荐                                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  基于当前项目"电商平台v2.0"的特征，推荐以下相似项目：        │
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 📊 电商平台v1.0                        相似度: 92%     ││
│  │    文档数: 45  │ 标签: 用户管理, 订单, 支付           ││
│  │    [查看文档] [复制知识]                              ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 📊 供应链管理系统                      相似度: 78%     ││
│  │    文档数: 38  │ 标签: 订单, 库存, 供应商             ││
│  │    [查看文档] [复制知识]                              ││
│  └────────────────────────────────────────────────────────┘│
│                                                             │
│  ┌────────────────────────────────────────────────────────┐│
│  │ 📊 支付网关系统                        相似度: 71%     ││
│  │    文档数: 25  │ 标签: 支付, 安全, 对账               ││
│  │    [查看文档] [复制知识]                              ││
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
| skill-document-processor | 文档处理服务 | 必需 | ✅ 已依赖 |
| skill-llm-conversation | LLM对话服务 | 可选 | ✅ 已依赖 |

### 6.2 可选集成

| 集成项 | 用途 | 优先级 |
|--------|------|--------|
| 向量数据库 | 文档向量存储和检索 | P1 |
| 图数据库 | 知识图谱存储 | P2 |
| 文件存储服务 | 大文件存储 | P1 |

---

## 七、开发任务清单

### 7.1 后端开发任务

| 任务 | 说明 | 优先级 | 状态 |
|------|------|--------|------|
| 添加字典枚举 | DocumentType, ImportTaskStatus, ProjectStatus | P0 | ❌ |
| 实现文档列表查询 | 分页、筛选、搜索 | P0 | ❌ |
| 实现文档删除 | 删除文档及关联数据 | P0 | ❌ |
| 实现文档搜索 | 向量相似度搜索 | P1 | ❌ |
| 实现标签管理 | 更新文档标签 | P1 | ❌ |
| 实现项目统计 | 文档分布、标签统计 | P1 | ❌ |
| 实现任务管理 | 取消、重试导入任务 | P1 | ❌ |

### 7.2 前端开发任务

| 任务 | 说明 | 优先级 | 状态 |
|------|------|--------|------|
| 创建UI技能包 | skill-project-knowledge-ui | P0 | ❌ |
| 项目列表页 | 项目展示、导入入口 | P0 | ❌ |
| 文档管理页 | 文档列表、搜索、标签 | P0 | ❌ |
| 导入进度弹窗 | 实时进度展示 | P0 | ❌ |
| 知识图谱页 | 可视化图谱展示 | P1 | ❌ |
| 相似项目页 | 推荐列表展示 | P1 | ❌ |

---

## 八、验收标准

### 8.1 功能验收

- [ ] 能够批量导入项目文档
- [ ] 能够自动识别文档类型
- [ ] 能够自动提取文档标签
- [ ] 能够推荐相似项目
- [ ] 能够生成知识图谱
- [ ] 能够全文检索文档

### 8.2 三闭环验收

- [ ] 生命周期闭环：导入任务CRUD完整
- [ ] 数据实体闭环：Project→Document→Tag关联正确
- [ ] 按钮API闭环：每个操作都有对应API调用

---

## 九、变更记录

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|---------|------|
| v1.0 | 2026-03-07 | 初始版本 | Ooder Team |
