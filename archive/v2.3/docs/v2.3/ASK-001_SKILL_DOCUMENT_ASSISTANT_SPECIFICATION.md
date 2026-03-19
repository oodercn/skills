# ASK-001 智能文档助手需求规格说明书

> **版本**: v1.0.0  
> **日期**: 2026-03-06  
> **状态**: 正式发布  
> **遵循规范**: new-feature-guide

---

## 一、用户故事

### 1.1 核心用户故事

```
作为 企业员工
我希望 通过自然语言查询公司制度、流程文档
以便 快速获取工作所需信息，无需翻阅大量文档
```

### 1.2 用户故事分解

| 故事ID | 角色 | 故事描述 | 优先级 |
|--------|------|---------|--------|
| US-001 | 员工 | 我希望用自然语言提问，获得准确答案 | P0 |
| US-002 | 员工 | 我希望看到答案的来源文档，便于验证 | P0 |
| US-003 | 管理员 | 我希望上传公司文档，自动建立知识库 | P0 |
| US-004 | 管理员 | 我希望管理知识库，控制访问权限 | P1 |
| US-005 | 员工 | 我希望查看历史问答记录 | P2 |

### 1.3 用户角色定义

| 角色 | 描述 | 权限 |
|------|------|------|
| 普通员工 | 查询知识库 | READ |
| 知识管理员 | 管理知识库和文档 | READ, WRITE |
| 系统管理员 | 系统配置和权限管理 | READ, WRITE, DELETE, ADMIN |

---

## 二、UI/UE 设计

### 2.1 界面布局

```
┌─────────────────────────────────────────────────────────────────────────┐
│  智能文档助手                                          [用户] [设置]     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                        知识库选择                                  │  │
│  │  [▼ 公司制度库 ]  [▼ 产品文档库 ]  [▼ 技术文档库 ]                 │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                                                                    │  │
│  │  ┌─────────────────────────────────────────────────────────────┐  │  │
│  │  │  🤖 智能助手                                                 │  │  │
│  │  │                                                              │  │  │
│  │  │  您好！我是智能文档助手，可以帮您查询公司制度、流程文档。      │  │  │
│  │  │  请问有什么可以帮您的？                                       │  │  │
│  │  │                                                              │  │  │
│  │  └─────────────────────────────────────────────────────────────┘  │  │
│  │                                                                    │  │
│  │  ┌─────────────────────────────────────────────────────────────┐  │  │
│  │  │  👤 用户                                                     │  │  │
│  │  │                                                              │  │  │
│  │  │  公司的请假流程是怎样的？                                     │  │  │
│  │  │                                                              │  │  │
│  │  └─────────────────────────────────────────────────────────────┘  │  │
│  │                                                                    │  │
│  │  ┌─────────────────────────────────────────────────────────────┐  │  │
│  │  │  🤖 智能助手                                                 │  │  │
│  │  │                                                              │  │  │
│  │  │  根据公司规定，请假流程如下：                                 │  │  │
│  │  │                                                              │  │  │
│  │  │  1. 登录OA系统，进入"请假申请"模块                           │  │  │
│  │  │  2. 填写请假类型、时间、原因                                 │  │  │
│  │  │  3. 提交审批，等待直属领导审批                               │  │  │
│  │  │  4. 审批通过后，系统自动通知人事部门                         │  │  │
│  │  │                                                              │  │  │
│  │  │  📄 来源：《员工手册》第3章-考勤管理                         │  │  │
│  │  │  📊 置信度：92%                                              │  │  │
│  │  │                                                              │  │  │
│  │  │  [查看原文] [复制答案] [反馈问题]                            │  │  │
│  │  │                                                              │  │  │
│  │  └─────────────────────────────────────────────────────────────┘  │  │
│  │                                                                    │  │
│  │                         [加载更多历史记录...]                      │  │
│  │                                                                    │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  [输入您的问题...]                              [📎] [🎤] [发送]  │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 知识库管理界面

```
┌─────────────────────────────────────────────────────────────────────────┐
│  知识库管理                                        [+ 新建知识库]        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  📚 公司制度库                                    [编辑] [删除]   │  │
│  │  ─────────────────────────────────────────────────────────────── │  │
│  │  文档数量: 156    更新时间: 2026-03-06    可见性: 全公司          │  │
│  │  描述: 包含公司规章制度、流程文档、员工手册等                     │  │
│  │                                                    [上传文档]     │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  📚 产品文档库                                    [编辑] [删除]   │  │
│  │  ─────────────────────────────────────────────────────────────── │  │
│  │  文档数量: 89     更新时间: 2026-03-05    可见性: 产品部门        │  │
│  │  描述: 产品需求文档、设计文档、用户手册                           │  │
│  │                                                    [上传文档]     │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  📚 技术文档库                                    [编辑] [删除]   │  │
│  │  ─────────────────────────────────────────────────────────────── │  │
│  │  文档数量: 234    更新时间: 2026-03-06    可见性: 技术部门        │  │
│  │  描述: 技术架构文档、API文档、开发规范                            │  │
│  │                                                    [上传文档]     │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.3 文档上传界面

```
┌─────────────────────────────────────────────────────────────────────────┐
│  上传文档 - 公司制度库                                          [×]    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                                                                    │  │
│  │                     📄 拖拽文件到此处上传                          │  │
│  │                     或点击选择文件                                 │  │
│  │                                                                    │  │
│  │                     支持: PDF, Word, Excel, Markdown              │  │
│  │                     单个文件最大: 50MB                             │  │
│  │                                                                    │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  已选文件:                                                              │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │  📄 员工手册_v2.3.pdf                         2.5MB  [× 删除]    │  │
│  │  📄 考勤管理制度.docx                         1.2MB  [× 删除]    │  │
│  │  📄 报销流程说明.pdf                         0.8MB  [× 删除]    │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
│  处理选项:                                                              │
│  [✓] 自动提取文本内容                                                  │
│  [✓] 自动建立索引                                                      │
│  [ ] 启用OCR识别（扫描件）                                              │
│                                                                         │
│                                          [取消]  [开始上传]            │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.4 交互流程图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          用户交互流程                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────┐                                                            │
│  │  用户   │                                                            │
│  └────┬────┘                                                            │
│       │                                                                 │
│       ▼                                                                 │
│  ┌─────────────────┐     ┌─────────────────┐                           │
│  │   选择知识库    │────►│   输入问题      │                           │
│  └─────────────────┘     └────────┬────────┘                           │
│                                   │                                     │
│                                   ▼                                     │
│                          ┌─────────────────┐                           │
│                          │   发送查询      │                           │
│                          └────────┬────────┘                           │
│                                   │                                     │
│                                   ▼                                     │
│                          ┌─────────────────┐                           │
│                          │   显示加载状态   │                           │
│                          └────────┬────────┘                           │
│                                   │                                     │
│                    ┌──────────────┼──────────────┐                     │
│                    │              │              │                     │
│                    ▼              ▼              ▼                     │
│            ┌───────────┐  ┌───────────┐  ┌───────────┐                │
│            │ 成功响应  │  │ 无结果    │  │ 错误响应  │                │
│            └─────┬─────┘  └─────┬─────┘  └─────┬─────┘                │
│                  │              │              │                       │
│                  ▼              ▼              ▼                       │
│            ┌───────────┐  ┌───────────┐  ┌───────────┐                │
│            │ 显示答案  │  │ 显示提示  │  │ 显示错误  │                │
│            │ + 来源    │  │ 建议换词  │  │ 重试按钮  │                │
│            └─────┬─────┘  └───────────┘  └───────────┘                │
│                  │                                                      │
│                  ▼                                                      │
│            ┌───────────┐                                                │
│            │ 用户反馈  │                                                │
│            │ ✓ 有帮助  │                                                │
│            │ ✗ 无帮助  │                                                │
│            └───────────┘                                                │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 三、三闭环流程

### 3.1 闭环一：能力生命周期流程闭环

#### 3.1.1 知识库生命周期

```
┌─────────┐    创建     ┌─────────┐    编辑     ┌─────────┐
│  无     │ ─────────► │  草稿   │ ─────────► │  激活   │
└─────────┘            └─────────┘            └────┬────┘
                                                  │
                    ┌─────────────────────────────┤
                    │                             │
                    ▼                             ▼
              ┌─────────┐                   ┌─────────┐
              │  归档   │                   │  删除   │
              └─────────┘                   └─────────┘
```

#### 3.1.2 知识库状态字典

```java
@Dict(code = "kb_status", name = "知识库状态", description = "知识库的生命周期状态")
public enum KbStatus implements DictItem {
    
    DRAFT("DRAFT", "草稿", "知识库创建中，尚未发布", "ri-draft-line", 1),
    ACTIVE("ACTIVE", "激活", "知识库已发布，可正常使用", "ri-check-line", 2),
    ARCHIVED("ARCHIVED", "归档", "知识库已归档，只读访问", "ri-archive-line", 3),
    DELETED("DELETED", "删除", "知识库已删除", "ri-delete-bin-line", 4);
    
    private final String code;
    private final String name;
    private final String description;
    private final String icon;
    private final int sort;
    
    KbStatus(String code, String name, String description, String icon, int sort) {
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

#### 3.1.3 知识库 API 闭环检查表

| 操作 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 创建知识库 | `createKb()` | `POST /api/v1/document-assistant/kb` | DocumentAssistantController.createKnowledgeBase() | ✅ |
| 获取知识库列表 | `listKbs()` | `GET /api/v1/document-assistant/kb?userId={userId}` | DocumentAssistantController.listKnowledgeBases() | ✅ |
| 获取知识库详情 | `getKb()` | `GET /api/v1/document-assistant/kb/{kbId}` | DocumentAssistantController.getKnowledgeBase() | ✅ |
| 更新知识库 | `updateKb()` | `PUT /api/v1/document-assistant/kb/{kbId}` | DocumentAssistantController.updateKnowledgeBase() | ⚠️ 需补充 |
| 删除知识库 | `deleteKb()` | `DELETE /api/v1/document-assistant/kb/{kbId}` | DocumentAssistantController.deleteKnowledgeBase() | ✅ |
| 归档知识库 | `archiveKb()` | `POST /api/v1/document-assistant/kb/{kbId}/archive` | DocumentAssistantController.archiveKnowledgeBase() | ⚠️ 需补充 |

#### 3.1.4 文档生命周期

```
┌─────────┐    上传     ┌─────────┐    处理     ┌─────────┐
│  无     │ ─────────► │  待处理 │ ─────────► │  索引中 │
└─────────┘            └─────────┘            └────┬────┘
                                                  │
                    ┌─────────────────────────────┤
                    │                             │
                    ▼                             ▼
              ┌─────────┐                   ┌─────────┐
              │  已发布 │                   │  失败   │
              └─────────┘                   └─────────┘
```

#### 3.1.5 文档状态字典

```java
@Dict(code = "doc_status", name = "文档状态", description = "文档的处理状态")
public enum DocStatus implements DictItem {
    
    PENDING("PENDING", "待处理", "文档已上传，等待处理", "ri-time-line", 1),
    PROCESSING("PROCESSING", "处理中", "正在提取文本和建立索引", "ri-loader-line", 2),
    PUBLISHED("PUBLISHED", "已发布", "文档已处理完成，可检索", "ri-check-line", 3),
    FAILED("FAILED", "处理失败", "文档处理失败", "ri-error-warning-line", 4);
    
    // 实现 DictItem 接口...
}
```

#### 3.1.6 文档 API 闭环检查表

| 操作 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 上传文档 | `uploadDoc()` | `POST /api/v1/document-assistant/upload` | DocumentAssistantController.uploadDocument() | ✅ |
| 获取文档列表 | `listDocs()` | `GET /api/v1/document-assistant/kb/{kbId}/documents` | DocumentAssistantController.listDocuments() | ⚠️ 需补充 |
| 获取文档详情 | `getDoc()` | `GET /api/v1/document-assistant/documents/{docId}` | DocumentAssistantController.getDocument() | ⚠️ 需补充 |
| 删除文档 | `deleteDoc()` | `DELETE /api/v1/document-assistant/documents/{docId}` | DocumentAssistantController.deleteDocument() | ⚠️ 需补充 |
| 重新处理 | `reprocessDoc()` | `POST /api/v1/document-assistant/documents/{docId}/reprocess` | DocumentAssistantController.reprocessDocument() | ⚠️ 需补充 |

---

### 3.2 闭环二：能力数据实体关系闭环

#### 3.2.1 实体关系图

```
┌─────────────────┐     1:N     ┌─────────────────┐
│  KnowledgeBase  │────────────►│    Document     │
│    知识库实体    │             │    文档实体      │
├─────────────────┤             ├─────────────────┤
│ + kbId: String  │             │ + docId: String │
│ + name: String  │             │ + title: String │
│ + ownerId:String│             │ + content: Text │
│ + visibility    │             │ + status: Enum  │
│ + status: Enum  │             │ + kbId: FK      │
│ + createdAt     │             │ + createdAt     │
└────────┬────────┘             └────────┬────────┘
         │                               │
         │ N:M                           │ 1:N
         ▼                               ▼
┌─────────────────┐             ┌─────────────────┐
│     User        │             │     Chunk       │
│    用户实体      │             │    分块实体      │
├─────────────────┤             ├─────────────────┤
│ + userId: String│             │ + chunkId: String│
│ + name: String  │             │ + content: Text │
│ + deptId: String│             │ + docId: FK     │
│ + role: Enum    │             │ + embedding     │
└─────────────────┘             │ + position: Int │
                                └─────────────────┘
                                         │
                                         │ 1:1
                                         ▼
                                ┌─────────────────┐
                                │   Embedding     │
                                │   向量实体       │
                                ├─────────────────┤
                                │ + vector: float[]│
                                │ + chunkId: FK   │
                                └─────────────────┘
```

#### 3.2.2 数据一致性保障

| 场景 | 保障措施 | 实现方式 |
|------|---------|---------|
| 删除知识库 | 级联删除所有文档和分块 | `ON DELETE CASCADE` |
| 删除文档 | 级联删除所有分块和向量 | `ON DELETE CASCADE` |
| 更新文档 | 重新建立索引 | 异步任务处理 |
| 用户权限变更 | 刷新权限缓存 | 事件驱动更新 |

#### 3.2.3 数据一致性代码示例

```javascript
// 正确模式：删除知识库后刷新列表
async function deleteKnowledgeBase(kbId) {
    try {
        const response = await fetch(`/api/v1/document-assistant/kb/${kbId}`, {
            method: 'DELETE'
        });
        const result = await response.json();
        
        if (result.code === 200) {
            // 重新加载列表确保数据一致
            loadKnowledgeBases();
            showSuccess('知识库删除成功');
        } else {
            showError('删除失败: ' + result.message);
        }
    } catch (error) {
        console.error('Delete failed:', error);
        showError('删除失败，请重试');
    }
}

// 错误模式：仅本地删除
function deleteKnowledgeBase(kbId) {
    knowledgeBases = knowledgeBases.filter(kb => kb.kbId !== kbId);
    render();  // 不要这样做！
}
```

---

### 3.3 闭环三：按钮事件和API闭环

#### 3.3.1 主界面按钮闭环检查表

| 按钮 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 发送问题 | `sendQuery()` | `POST /api/v1/document-assistant/query` | DocumentAssistantController.queryDocument() | ✅ |
| 查看原文 | `viewSource()` | `GET /api/v1/document-assistant/documents/{docId}` | DocumentAssistantController.getDocument() | ⚠️ 需补充 |
| 复制答案 | `copyAnswer()` | 无需API（本地操作） | - | ✅ |
| 反馈问题 | `feedback()` | `POST /api/v1/document-assistant/feedback` | DocumentAssistantController.submitFeedback() | ⚠️ 需补充 |

#### 3.3.2 知识库管理按钮闭环检查表

| 按钮 | 前端函数 | API调用 | 后端接口 | 闭环状态 |
|------|---------|---------|---------|---------|
| 新建知识库 | `createKb()` | `POST /api/v1/document-assistant/kb` | DocumentAssistantController.createKnowledgeBase() | ✅ |
| 编辑知识库 | `editKb()` | `PUT /api/v1/document-assistant/kb/{kbId}` | DocumentAssistantController.updateKnowledgeBase() | ⚠️ 需补充 |
| 删除知识库 | `deleteKb()` | `DELETE /api/v1/document-assistant/kb/{kbId}` | DocumentAssistantController.deleteKnowledgeBase() | ✅ |
| 上传文档 | `uploadDoc()` | `POST /api/v1/document-assistant/upload` | DocumentAssistantController.uploadDocument() | ✅ |
| 删除文档 | `deleteDoc()` | `DELETE /api/v1/document-assistant/documents/{docId}` | DocumentAssistantController.deleteDocument() | ⚠️ 需补充 |

#### 3.3.3 标准闭环模式实现

```javascript
// 查询文档 - 标准闭环实现
async function sendQuery() {
    const queryInput = document.getElementById('queryInput');
    const query = queryInput.value.trim();
    
    if (!query) {
        showError('请输入问题');
        return;
    }
    
    // 1. 显示加载状态
    showLoading();
    addMessageToChat('user', query);
    queryInput.value = '';
    
    try {
        // 2. 调用后端API
        const response = await fetch('/api/v1/document-assistant/query', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                kbId: currentKbId,
                query: query,
                topK: 5
            })
        });
        
        const result = await response.json();
        
        // 3. 处理结果
        hideLoading();
        
        if (result.code === 200) {
            // 4. 更新UI
            addMessageToChat('assistant', result.answer, result.sources, result.confidence);
        } else {
            addMessageToChat('error', '查询失败: ' + result.message);
        }
    } catch (error) {
        hideLoading();
        addMessageToChat('error', '网络错误，请重试');
        console.error('Query failed:', error);
    }
}

// 上传文档 - 标准闭环实现
async function uploadDocument(files, kbId) {
    // 1. 用户确认
    if (files.length === 0) return;
    
    showUploadProgress(0);
    
    try {
        const formData = new FormData();
        formData.append('file', files[0]);
        formData.append('userId', currentUserId);
        formData.append('kbId', kbId);
        formData.append('autoProcess', true);
        
        // 2. 调用后端API
        const response = await fetch('/api/v1/document-assistant/upload', {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        
        // 3. 处理结果
        if (result.code === 200) {
            showSuccess('文档上传成功，正在处理...');
            // 4. 刷新数据
            loadDocuments(kbId);
        } else {
            showError('上传失败: ' + result.message);
        }
    } catch (error) {
        showError('上传失败，请重试');
        console.error('Upload failed:', error);
    } finally {
        hideUploadProgress();
    }
}
```

---

## 四、外部集成需求

### 4.1 依赖技能集成

#### 4.1.1 skill-knowledge-base 集成

| 集成点 | 接口 | 说明 |
|--------|------|------|
| 创建知识库 | `KnowledgeBaseService.create()` | 创建知识库实体 |
| 获取知识库 | `KnowledgeBaseService.get()` | 获取知识库详情 |
| 添加文档 | `KnowledgeBaseService.addDocument()` | 添加文档到知识库 |
| 重建索引 | `KnowledgeBaseService.rebuildIndex()` | 重建知识库索引 |

```java
@Autowired
private KnowledgeBaseService knowledgeBaseService;

public KbManageResult createKnowledgeBase(String userId, String name, String visibility) {
    KnowledgeBaseCreateRequest request = KnowledgeBaseCreateRequest.builder()
        .name(name)
        .ownerId(userId)
        .visibility(visibility != null ? visibility : KnowledgeBase.VISIBILITY_PRIVATE)
        .build();
    
    KnowledgeBase kb = knowledgeBaseService.create(request);
    return new KbManageResult(kb.getKbId(), "created", kb.getName());
}
```

#### 4.1.2 skill-rag 集成

| 集成点 | 接口 | 说明 |
|--------|------|------|
| 文档检索 | `RagApi.retrieve()` | 检索相关文档片段 |
| 答案生成 | `RagApi.generate()` | 生成最终答案 |

```java
@Autowired
private RagApi ragPipeline;

public QueryResult queryDocument(String kbId, String query, Integer topK) {
    RagContext context = RagContext.builder()
        .kbId(kbId)
        .query(query)
        .topK(topK != null ? topK : 5)
        .threshold(0.7f)
        .build();
    
    RagResult ragResult = ragPipeline.retrieve(context);
    String answer = ragPipeline.generate(query, ragResult);
    
    List<SourceReference> sources = buildSources(ragResult);
    float confidence = calculateConfidence(ragResult);
    
    return new QueryResult(answer, sources, confidence);
}
```

#### 4.1.3 skill-llm-conversation 集成

| 集成点 | 接口 | 说明 |
|--------|------|------|
| 对话上下文 | `ConversationService.createSession()` | 创建对话会话 |
| 发送消息 | `ConversationService.sendMessage()` | 发送消息获取回复 |

```java
@Autowired
private ConversationService conversationService;

public String generateAnswer(String query, RagResult ragResult) {
    String context = buildContextFromRagResult(ragResult);
    
    MessageRequest request = new MessageRequest();
    request.setContent(buildPrompt(query, context));
    
    MessageResponse response = conversationService.sendMessage(sessionId, request);
    return response.getContent();
}
```

### 4.2 外部系统集成

#### 4.2.1 组织系统集成

| 系统 | 集成方式 | 用途 |
|------|---------|------|
| 钉钉 | skill-org-dingding | 获取组织架构、用户信息 |
| 飞书 | skill-org-feishu | 获取组织架构、用户信息 |
| 企业微信 | skill-org-wecom | 获取组织架构、用户信息 |
| LDAP | skill-org-ldap | 用户认证、组织架构 |

#### 4.2.2 存储系统集成

| 系统 | 集成方式 | 用途 |
|------|---------|------|
| 本地存储 | skill-vfs-local | 文档存储（开发环境） |
| MinIO | skill-vfs-minio | 文档存储（生产环境） |
| 阿里云OSS | skill-vfs-oss | 文档存储（云环境） |

#### 4.2.3 消息通知集成

| 系统 | 集成方式 | 用途 |
|------|---------|------|
| 邮件 | skill-email | 文档处理完成通知 |
| 系统消息 | skill-notify | 知识库更新通知 |

### 4.3 集成架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      skill-document-assistant                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                        核心依赖技能                               │   │
│  │                                                                  │   │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │   │
│  │  │skill-knowledge- │  │   skill-rag     │  │skill-llm-       │  │   │
│  │  │      base       │  │                 │  │ conversation    │  │   │
│  │  │                 │  │                 │  │                 │  │   │
│  │  │ - 知识库管理    │  │ - 文档检索      │  │ - 对话管理      │  │   │
│  │  │ - 文档管理      │  │ - 答案生成      │  │ - 上下文构建    │  │   │
│  │  │ - 索引管理      │  │ - Prompt构建    │  │ - 多轮对话      │  │   │
│  │  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘  │   │
│  │           │                    │                    │           │   │
│  └───────────┼────────────────────┼────────────────────┼───────────┘   │
│              │                    │                    │               │
│  ┌───────────┼────────────────────┼────────────────────┼───────────┐   │
│  │           │         可选依赖技能                     │           │   │
│  │           ▼                    ▼                    ▼           │   │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │   │
│  │  │skill-document-  │  │skill-vector-    │  │skill-knowledge- │  │   │
│  │  │   processor     │  │    sqlite       │  │     share       │  │   │
│  │  │                 │  │                 │  │                 │  │   │
│  │  │ - 文档解析      │  │ - 向量存储      │  │ - 权限管理      │  │   │
│  │  │ - 文档分块      │  │ - 相似度检索    │  │ - 分享功能      │  │   │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘  │   │
│  │                                                                  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                        外部系统集成                               │   │
│  │                                                                   │   │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐   │   │
│  │  │   组织系统      │  │   存储系统       │  │   通知系统      │   │   │
│  │  │                 │  │                 │  │                 │   │   │
│  │  │ - 钉钉         │  │ - MinIO        │  │ - 邮件         │   │   │
│  │  │ - 飞书         │  │ - OSS          │  │ - 系统消息      │   │   │
│  │  │ - 企业微信      │  │ - 本地存储      │  │ - IM推送       │   │   │
│  │  │ - LDAP         │  │                 │  │                 │   │   │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘   │   │
│  │                                                                   │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 五、API 完整清单

### 5.1 知识库管理 API

| 方法 | 路径 | 说明 | 闭环状态 |
|------|------|------|---------|
| POST | /api/v1/document-assistant/kb | 创建知识库 | ✅ |
| GET | /api/v1/document-assistant/kb | 获取知识库列表 | ✅ |
| GET | /api/v1/document-assistant/kb/{kbId} | 获取知识库详情 | ✅ |
| PUT | /api/v1/document-assistant/kb/{kbId} | 更新知识库 | ⚠️ 需补充 |
| DELETE | /api/v1/document-assistant/kb/{kbId} | 删除知识库 | ✅ |
| POST | /api/v1/document-assistant/kb/{kbId}/archive | 归档知识库 | ⚠️ 需补充 |

### 5.2 文档管理 API

| 方法 | 路径 | 说明 | 闭环状态 |
|------|------|------|---------|
| POST | /api/v1/document-assistant/upload | 上传文档 | ✅ |
| GET | /api/v1/document-assistant/kb/{kbId}/documents | 获取文档列表 | ⚠️ 需补充 |
| GET | /api/v1/document-assistant/documents/{docId} | 获取文档详情 | ⚠️ 需补充 |
| DELETE | /api/v1/document-assistant/documents/{docId} | 删除文档 | ⚠️ 需补充 |
| POST | /api/v1/document-assistant/documents/{docId}/reprocess | 重新处理文档 | ⚠️ 需补充 |

### 5.3 问答 API

| 方法 | 路径 | 说明 | 闭环状态 |
|------|------|------|---------|
| POST | /api/v1/document-assistant/query | 文档查询 | ✅ |
| POST | /api/v1/document-assistant/feedback | 提交反馈 | ⚠️ 需补充 |
| GET | /api/v1/document-assistant/history | 获取历史记录 | ⚠️ 需补充 |

---

## 六、验收标准

### 6.1 三闭环验收清单

```
闭环一：能力生命周期流程闭环
□ 知识库：创建 → 查询 → 更新 → 删除 API完整
□ 文档：上传 → 处理 → 发布 → 删除 API完整
□ 状态机：DRAFT → ACTIVE → ARCHIVED → DELETED 完整

闭环二：能力数据实体关系闭环
□ 实体关系图已绘制
□ 级联删除已实现
□ 数据一致性已保障

闭环三：按钮事件和API闭环
□ 所有按钮都有对应API
□ 所有操作都有错误处理
□ 操作后正确刷新数据
```

### 6.2 功能验收清单

- [ ] 用户可选择知识库进行查询
- [ ] 用户输入问题后获得准确答案
- [ ] 答案显示来源文档和置信度
- [ ] 管理员可创建知识库
- [ ] 管理员可上传文档
- [ ] 文档自动处理和索引
- [ ] 支持多种文档格式（PDF、Word、Excel、Markdown）
- [ ] 权限控制正常工作

### 6.3 性能验收标准

| 指标 | 目标值 |
|------|--------|
| 查询响应时间 | < 3秒 |
| 文档上传处理 | < 30秒/文档 |
| 并发用户数 | 100+ |
| 知识库容量 | 10000+ 文档 |

---

## 七、版本历史

| 版本 | 日期 | 变更 |
|------|------|------|
| 1.0.0 | 2026-03-06 | 初始版本，完成三闭环需求推导 |

---

**文档编写**: Skills Team  
**最后更新**: 2026-03-06
