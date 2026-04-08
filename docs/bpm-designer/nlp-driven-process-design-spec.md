# NLP 驱动流程定义设计规范

**文档版本**: 1.0  
**创建日期**: 2026-04-08  
**项目路径**: E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer

---

## 📋 一、概述

### 1.1 设计目标

构建一个基于自然语言处理（NLP）的 BPM 流程设计助手，实现：

1. **自然语言交互**：用户通过自然语言描述创建和修改流程定义
2. **智能上下文感知**：理解当前设计状态，提供精准建议
3. **RAG 知识增强**：结合知识库提供最佳实践建议
4. **SceneEngine 集成**：与场景引擎深度集成，支持场景驱动设计

### 1.2 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                     前端设计器                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  画布编辑器  │  │  属性面板   │  │  NLP助手    │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    后端 API 层                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  DesignerNlpController                                │  │
│  │  - /api/bpm/nlp/chat                                  │  │
│  │  - /api/bpm/nlp/process/create                        │  │
│  │  - /api/bpm/nlp/activity/create                       │  │
│  │  - /api/bpm/nlp/suggestions                           │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    服务层                                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ NlpService   │  │PromptBuilder │  │ RagPipeline  │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    集成层                                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │SceneEngine   │  │ BPM Server   │  │ Knowledge    │     │
│  │Integration   │  │              │  │ Service      │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔧 二、核心组件设计

### 2.1 DesignerContextDTO - 设计器上下文

**文件路径**: `E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\model\dto\DesignerContextDTO.java`

**核心字段**:

```java
public class DesignerContextDTO {
    // 用户信息
    private String sessionId;
    private String userId;
    private String userName;
    private String userRole;
    
    // 当前设计状态
    private ProcessDefDTO currentProcess;
    private ActivityDefDTO currentActivity;
    private String selectedElementId;
    private String selectedElementType;
    
    // 可用资源
    private List<ProcessDefDTO> recentProcesses;
    private List<String> availableCapabilities;
    private Map<String, List<Map<String, String>>> enumOptions;
    
    // SceneEngine 集成
    private Map<String, Object> sceneContext;
    private String sceneGroupId;
    private String sceneType;
    
    // 会话历史
    private List<Map<String, String>> conversationHistory;
    private Map<String, Object> userPreferences;
    
    // RAG 上下文
    private Map<String, Object> ragContext;
    private List<KnowledgeReference> knowledgeReferences;
    
    // 编辑器状态
    private DesignerMode mode;
    private EditorState editorState;
}
```

### 2.2 DesignerNlpService - NLP 服务接口

**文件路径**: `E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\DesignerNlpService.java`

**核心方法**:

| 方法 | 功能 | 输入 | 输出 |
|------|------|------|------|
| `processNaturalLanguage` | 处理自然语言输入 | 用户输入 + 上下文 | NlpResponse |
| `createProcessFromNlp` | 从自然语言创建流程 | 描述 + 上下文 | ProcessDefDTO |
| `createActivityFromNlp` | 从自然语言创建活动 | 描述 + 上下文 | ActivityDefDTO |
| `updateAttributeFromNlp` | 从自然语言更新属性 | 属性名 + 值 + 上下文 | 更新结果 |
| `getSuggestions` | 获取智能建议 | 上下文 | 建议列表 |
| `validateAndFix` | 验证并修复流程 | 流程定义 + 上下文 | 验证结果 |
| `analyzeIntent` | 分析用户意图 | 用户输入 | 意图列表 |
| `extractEntities` | 提取实体 | 用户输入 + 意图类型 | 实体映射 |

### 2.3 DesignerPromptBuilder - Prompt 构建器

**文件路径**: `E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\prompt\DesignerPromptBuilder.java`

**Prompt 结构**:

```
## Role Definition
[角色定义和能力说明]

## Current Context
[当前设计上下文]

## Available Schema Types
[可用的模式类型]

## Interaction Rules
[交互规则]

## Example Interactions
[示例交互]
```

---

## 🔄 三、交互流程设计

### 3.1 自然语言创建流程

```
用户输入: "创建一个请假审批流程"
         │
         ▼
┌─────────────────────────────────────┐
│  1. 意图分析                         │
│  - 识别意图: create_process          │
│  - 置信度: 0.85                      │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  2. 实体提取                         │
│  - processId: "leave_approval"       │
│  - processName: "请假审批流程"        │
│  - description: "请假审批流程"        │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  3. 流程推断                         │
│  - 推断活动: 开始→提交→审批→结束      │
│  - 推断路由: 顺序连接                │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  4. RAG 增强                         │
│  - 检索相关最佳实践                   │
│  - 添加知识库建议                     │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  5. 返回结果                         │
│  - action: "create_process"          │
│  - params: {流程定义}                │
│  - suggestions: [建议列表]           │
└─────────────────────────────────────┘
```

### 3.2 自然语言创建活动

```
用户输入: "添加一个经理审批节点"
         │
         ▼
┌─────────────────────────────────────┐
│  1. 意图分析                         │
│  - 识别意图: create_activity         │
│  - 置信度: 0.90                      │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  2. 实体提取                         │
│  - activityId: "manager_approve"     │
│  - activityName: "经理审批"          │
│  - activityType: "TASK"              │
│  - category: "HUMAN"                 │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  3. 属性推断                         │
│  - implementation: "IMPL_NO"         │
│  - position: "NORMAL"                │
│  - 权限配置: 单人办理                 │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  4. 返回结果                         │
│  - action: "add_activity"            │
│  - params: {活动定义}                │
└─────────────────────────────────────┘
```

### 3.3 自然语言更新属性

```
用户输入: "设置审批节点的时限为3天"
         │
         ▼
┌─────────────────────────────────────┐
│  1. 意图分析                         │
│  - 识别意图: update_attribute        │
│  - 置信度: 0.88                      │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  2. 实体提取                         │
│  - attribute: "timing.limit"         │
│  - value: 3                          │
│  - unit: "D"                         │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  3. 返回结果                         │
│  - action: "update_attribute"        │
│  - params: {属性更新}                │
└─────────────────────────────────────┘
```

---

## 📚 四、RAG 知识增强

### 4.1 RAG 集成架构

```
┌─────────────────────────────────────────────────────────────┐
│                     RAG Pipeline                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Knowledge    │  │ Dict         │  │ Classifier   │     │
│  │ Service      │  │ Service      │  │ Service      │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                   知识检索流程                               │
│                                                              │
│  1. 用户输入 → 向量化                                        │
│  2. 相似度检索 → Top-K 文档                                  │
│  3. 上下文构建 → Prompt 增强                                 │
│  4. 字典查询 → 枚举值映射                                    │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 知识库内容

**流程设计最佳实践**:
- 审批流程设计模式
- 网关使用规范
- 权限配置最佳实践
- 时限设置建议

**枚举值字典**:
- ActivityType 映射
- ActivityCategory 映射
- Implementation 映射
- PermissionType 映射

**场景模板**:
- 请假审批场景
- 报销审批场景
- 合同审批场景
- 采购审批场景

### 4.3 RAG Prompt 模板

```
你是一个智能助手。在回答问题时，请参考以下知识和字典数据：

{{knowledge_context}}

## 可用字典数据
{{dict_items}}

请基于以上资料给出准确、专业的回答。
```

---

## 🔌 五、SceneEngine 集成

### 5.1 集成点

| 集成点 | 说明 | 数据流向 |
|--------|------|----------|
| 场景上下文 | 获取当前场景信息 | SceneEngine → Designer |
| 能力发现 | 获取可用能力列表 | SceneEngine → Designer |
| 能力调用 | 调用场景能力 | Designer → SceneEngine |
| 状态同步 | 同步设计状态 | 双向 |

### 5.2 SceneEngineIntegration 接口

```java
public class SceneEngineIntegration {
    // 检查 SDK 是否可用
    public boolean isSdkAvailable();
    
    // 发现可用技能
    public List<Map<String, Object>> discoverSkills();
    
    // 发现可用能力
    public List<Map<String, Object>> discoverCapabilities();
    
    // 调用能力
    public Object invokeCapability(String capabilityId, Map<String, Object> params);
    
    // 获取提供者技能
    public String getProviderSkill(String capabilityId);
    
    // 注册能力绑定
    public void registerCapabilityBinding(String capabilityId, String skillId);
}
```

### 5.3 场景驱动设计流程

```
┌─────────────────────────────────────────────────────────────┐
│                   场景驱动设计流程                           │
│                                                              │
│  1. 场景识别                                                 │
│     - 分析用户描述                                           │
│     - 匹配场景模板                                           │
│     - 确定场景类型                                           │
│                                                              │
│  2. 能力发现                                                 │
│     - 查询可用能力                                           │
│     - 匹配能力需求                                           │
│     - 推荐能力配置                                           │
│                                                              │
│  3. 流程生成                                                 │
│     - 基于场景模板生成流程                                   │
│     - 配置能力调用                                           │
│     - 设置权限和时限                                         │
│                                                              │
│  4. 验证和优化                                               │
│     - 验证流程完整性                                         │
│     - 检查能力可用性                                         │
│     - 优化流程结构                                           │
└─────────────────────────────────────────────────────────────┘
```

---

## 📡 六、API 接口规范

### 6.1 NLP 对话接口

**POST /api/bpm/nlp/chat**

请求:
```json
{
  "input": "创建一个请假审批流程",
  "context": {
    "sessionId": "session-123",
    "userId": "user-001",
    "userName": "张三",
    "mode": "CREATE"
  }
}
```

响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "intent": "create_process",
    "confidence": 0.85,
    "entities": {
      "processId": "leave_approval",
      "processName": "请假审批流程"
    },
    "action": "create_process",
    "actionParams": {
      "processDefId": "leave_approval",
      "name": "请假审批流程",
      "activities": [...]
    },
    "message": "将创建新流程",
    "suggestions": [...]
  }
}
```

### 6.2 创建流程接口

**POST /api/bpm/nlp/process/create**

请求:
```json
{
  "description": "创建一个请假审批流程，包含提交申请、部门经理审批、HR审批三个节点",
  "context": {
    "sessionId": "session-123",
    "userId": "user-001"
  }
}
```

响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "processDefId": "leave_approval",
    "name": "请假审批流程",
    "description": "创建一个请假审批流程...",
    "activities": [
      {
        "activityDefId": "start",
        "name": "开始",
        "activityType": "START"
      },
      {
        "activityDefId": "submit",
        "name": "提交申请",
        "activityType": "TASK"
      },
      ...
    ],
    "routes": [...]
  }
}
```

### 6.3 获取建议接口

**POST /api/bpm/nlp/suggestions**

请求:
```json
{
  "currentProcess": {
    "processDefId": "leave_approval",
    "name": "请假审批流程"
  },
  "currentActivity": {
    "activityDefId": "approve",
    "name": "审批",
    "activityType": "TASK"
  }
}
```

响应:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "type": "config",
      "title": "配置时限",
      "description": "建议为活动设置处理时限",
      "action": "update_attribute"
    },
    {
      "type": "config",
      "title": "配置权限",
      "description": "建议为任务活动配置办理权限",
      "action": "update_attribute"
    }
  ]
}
```

---

## 📁 七、文件清单

| 文件 | 路径 | 说明 |
|------|------|------|
| DesignerContextDTO.java | `model/dto/DesignerContextDTO.java` | 设计器上下文 DTO |
| DesignerNlpService.java | `service/DesignerNlpService.java` | NLP 服务接口 |
| DesignerNlpServiceImpl.java | `service/impl/DesignerNlpServiceImpl.java` | NLP 服务实现 |
| DesignerPromptBuilder.java | `prompt/DesignerPromptBuilder.java` | Prompt 构建器 |
| DesignerNlpController.java | `controller/DesignerNlpController.java` | NLP API 控制器 |

**绝对路径**:
- `E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\model\dto\DesignerContextDTO.java`
- `E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\DesignerNlpService.java`
- `E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\service\impl\DesignerNlpServiceImpl.java`
- `E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\prompt\DesignerPromptBuilder.java`
- `E:\github\ooder-skills\skills\_drivers\bpm\bpm-designer\src\main\java\net\ooder\bpm\designer\controller\DesignerNlpController.java`

---

## 🎯 八、后续优化方向

1. **意图识别增强**
   - 引入更精确的意图分类模型
   - 支持多意图识别
   - 支持意图纠错

2. **实体提取优化**
   - 支持更复杂的实体关系
   - 支持实体消歧
   - 支持实体补全

3. **RAG 效果提升**
   - 优化知识库结构
   - 提高检索精度
   - 支持多轮对话上下文

4. **SceneEngine 深度集成**
   - 实时能力状态同步
   - 能力调用结果反馈
   - 场景模板自动推荐

---

**文档生成时间**: 2026-04-08  
**文档路径**: E:\github\ooder-skills\docs\bpm-designer\nlp-driven-process-design-spec.md
