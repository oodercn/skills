# BPM Designer LLM核心实现方案

## 一、概述

### 1.1 目标
以LLM为核心，设计办理人推导、能力匹配、表单匹配三个核心服务，通过Function Calling机制实现智能配置推导。

### 1.2 核心设计原则
1. **LLM驱动决策** - 所有智能推导均由LLM进行语义理解和决策
2. **Function Calling桥接** - 通过函数调用机制连接LLM与后端服务
3. **抽象接口解耦** - 通过SceneEngine抽象接口获取组织、能力、场景等预定义信息
4. **面板渲染驱动** - 将推导结果渲染到流程定义面板

---

## 二、Function Calling注册机制设计

### 2.1 架构设计

```
┌─────────────────────────────────────────────────────────────────┐
│                        LLM Service                               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    Function Registry                      │    │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │    │
│  │  │ Org Tools   │ │ Cap Tools   │ │ Form Tools  │        │    │
│  │  └─────────────┘ └─────────────┘ └─────────────┘        │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SceneEngine SDK Adapter                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐               │
│  │ Org API     │ │ Cap API     │ │ Scene API   │               │
│  └─────────────┘ └─────────────┘ └─────────────┘               │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Function Definition Schema

```java
public class DesignerFunctionDefinition {
    private String name;
    private String description;
    private Map<String, ParameterDefinition> parameters;
    private List<String> required;
    private FunctionCategory category;
    private FunctionHandler handler;
    
    public enum FunctionCategory {
        ORGANIZATION,    // 组织机构相关
        CAPABILITY,      // 能力匹配相关
        FORM,            // 表单匹配相关
        SCENE,           // 场景相关
        WORKFLOW         // 工作流相关
    }
    
    public Map<String, Object> toOpenAISchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", name);
        schema.put("description", description);
        
        Map<String, Object> paramsSchema = new HashMap<>();
        paramsSchema.put("type", "object");
        paramsSchema.put("properties", buildPropertiesSchema());
        paramsSchema.put("required", required);
        
        schema.put("parameters", paramsSchema);
        return schema;
    }
}
```

### 2.3 核心Function注册列表

#### 2.3.1 组织机构相关函数

| 函数名 | 描述 | 参数 | 返回 |
|--------|------|------|------|
| `get_organization_tree` | 获取组织架构树 | rootOrgId(可选) | 组织树结构 |
| `get_users_by_role` | 按角色获取用户列表 | roleId, includeSubRoles | 用户列表 |
| `get_user_info` | 获取用户详细信息 | userId | 用户详情 |
| `search_users` | 语义搜索用户 | query, filters | 匹配用户列表 |
| `get_department_members` | 获取部门成员 | deptId, recursive | 成员列表 |
| `get_user_capabilities` | 获取用户具备的能力 | userId | 能力列表 |

#### 2.3.2 能力匹配相关函数

| 函数名 | 描述 | 参数 | 返回 |
|--------|------|------|------|
| `list_capabilities` | 列出所有可用能力 | category(可选) | 能力列表 |
| `search_capabilities` | 语义搜索能力 | query, context | 匹配能力列表 |
| `get_capability_detail` | 获取能力详情 | capId | 能力详情 |
| `get_capability_skills` | 获取能力关联的技能 | capId | 技能列表 |
| `match_capability_by_activity` | 根据活动描述匹配能力 | activityDesc, context | 匹配结果 |

#### 2.3.3 表单匹配相关函数

| 函数名 | 描述 | 参数 | 返回 |
|--------|------|------|------|
| `list_forms` | 列出所有可用表单 | category(可选) | 表单列表 |
| `search_forms` | 语义搜索表单 | query, context | 匹配表单列表 |
| `get_form_schema` | 获取表单Schema | formId | 表单结构 |
| `match_form_by_activity` | 根据活动描述匹配表单 | activityDesc, context | 匹配结果 |

#### 2.3.4 场景相关函数

| 函数名 | 描述 | 参数 | 返回 |
|--------|------|------|------|
| `list_scene_templates` | 列出场景模板 | category(可选) | 模板列表 |
| `get_scene_template` | 获取场景模板详情 | templateId | 模板详情 |
| `get_scene_capabilities` | 获取场景绑定的能力 | sceneGroupId | 能力绑定列表 |

---

## 三、办理人推导服务设计

### 3.1 服务架构

```java
@Service
public class PerformerDerivationService {
    
    @Autowired
    private DesignerFunctionRegistry functionRegistry;
    
    @Autowired
    private LLMService llmService;
    
    @Autowired
    private SceneSdkAdapter sceneSdkAdapter;
    
    /**
     * 推导办理人
     * @param context 设计器上下文
     * @param activityDesc 活动描述
     * @return 推导结果
     */
    public PerformerDerivationResult derive(DesignerContextDTO context, String activityDesc) {
        // 1. 构建推导Prompt
        String prompt = buildDerivationPrompt(context, activityDesc);
        
        // 2. 获取可用函数
        List<Map<String, Object>> functions = getAvailableFunctions();
        
        // 3. 调用LLM进行推导
        LLMResponse response = llmService.chatWithFunctions(prompt, functions);
        
        // 4. 处理Function Calling结果
        if (response.hasFunctionCalls()) {
            processFunctionCalls(response.getFunctionCalls());
        }
        
        // 5. 返回推导结果
        return buildDerivationResult(response);
    }
}
```

### 3.2 推导Prompt模板

```yaml
system_prompt: |
  你是一个BPM流程设计专家，负责根据活动描述推导合适的办理人。
  
  你可以调用以下函数获取信息：
  - get_organization_tree: 获取组织架构
  - get_users_by_role: 按角色获取用户
  - search_users: 语义搜索用户
  - get_department_members: 获取部门成员
  
  推导规则：
  1. 首先理解活动描述中的角色需求
  2. 调用相关函数获取候选人信息
  3. 根据业务规则筛选合适的办理人
  4. 返回推导结果和推理过程

user_prompt_template: |
  当前流程: {{processName}}
  当前活动: {{activityName}}
  活动描述: {{activityDesc}}
  
  请根据以上信息推导合适的办理人配置。
```

### 3.3 推导结果数据结构

```java
public class PerformerDerivationResult {
    private DerivationStatus status;
    private List<PerformerCandidate> candidates;
    private String reasoning;
    private List<FunctionCallTrace> functionTraces;
    private Map<String, Object> derivedConfig;
    
    public enum DerivationStatus {
        SUCCESS,           // 推导成功
        PARTIAL,           // 部分推导
        NEED_CLARIFICATION, // 需要澄清
        FAILED             // 推导失败
    }
    
    public static class PerformerCandidate {
        private String userId;
        private String userName;
        private String deptId;
        private String deptName;
        private String roleId;
        private String roleName;
        private double matchScore;
        private String matchReason;
    }
    
    public static class FunctionCallTrace {
        private String functionName;
        private Map<String, Object> arguments;
        private Object result;
        private long executionTime;
    }
}
```

### 3.4 Function Calling流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    用户输入活动描述                              │
│  "HR筛选简历"                                                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    LLM分析语义                                   │
│  识别关键词: HR、筛选、简历                                       │
│  推断角色需求: 人力资源专员                                       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Function Calling                              │
│  Call: get_users_by_role(roleId="hr_specialist")                │
│  Result: [张三, 李四, 王五]                                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    LLM决策                                       │
│  根据候选人列表和业务规则，推荐办理人配置                          │
│  - 办理人类型: 角色办理                                          │
│  - 办理人: HR专员                                                │
│  - 推理: 活动涉及简历筛选，通常由HR专员执行                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    返回推导结果                                   │
│  {                                                              │
│    "status": "SUCCESS",                                         │
│    "candidates": [...],                                         │
│    "derivedConfig": {                                           │
│      "assigneeType": "ROLE",                                    │
│      "assigneeId": "hr_specialist",                             │
│      "assigneeName": "HR专员"                                   │
│    },                                                           │
│    "reasoning": "活动涉及简历筛选..."                            │
│  }                                                              │
└─────────────────────────────────────────────────────────────────┘
```

---

## 四、能力匹配服务设计

### 4.1 服务架构

```java
@Service
public class CapabilityMatchingService {
    
    @Autowired
    private DesignerFunctionRegistry functionRegistry;
    
    @Autowired
    private LLMService llmService;
    
    @Autowired
    private SceneSdkAdapter sceneSdkAdapter;
    
    /**
     * 匹配能力
     * @param context 设计器上下文
     * @param activityDesc 活动描述
     * @return 匹配结果
     */
    public CapabilityMatchingResult match(DesignerContextDTO context, String activityDesc) {
        // 1. 获取可用能力列表
        List<CapabilityDefDTO> capabilities = getAvailableCapabilities(context);
        
        // 2. 构建匹配Prompt
        String prompt = buildMatchingPrompt(context, activityDesc, capabilities);
        
        // 3. 调用LLM进行语义匹配
        LLMResponse response = llmService.chat(prompt);
        
        // 4. 解析匹配结果
        return parseMatchingResult(response, capabilities);
    }
    
    /**
     * 智能匹配能力（带Function Calling）
     */
    public CapabilityMatchingResult smartMatch(DesignerContextDTO context, String activityDesc) {
        // 1. 构建智能匹配Prompt
        String prompt = buildSmartMatchingPrompt(context, activityDesc);
        
        // 2. 获取可用函数
        List<Map<String, Object>> functions = getCapabilityFunctions();
        
        // 3. 调用LLM
        LLMResponse response = llmService.chatWithFunctions(prompt, functions);
        
        // 4. 处理Function Calling
        if (response.hasFunctionCalls()) {
            for (FunctionCall call : response.getFunctionCalls()) {
                Object result = executeFunction(call);
                // 将结果反馈给LLM进行进一步决策
                response = llmService.chatWithFunctionResult(prompt, call.getName(), result);
            }
        }
        
        return parseMatchingResult(response);
    }
}
```

### 4.2 能力匹配Prompt模板

```yaml
system_prompt: |
  你是一个能力匹配专家，负责根据活动描述匹配合适的能力。
  
  能力匹配规则：
  1. 分析活动描述中的动词和名词，识别所需能力
  2. 根据能力描述进行语义匹配
  3. 考虑能力的参数和返回值是否满足需求
  4. 返回匹配度排序和能力绑定配置
  
  匹配维度：
  - 语义相似度: 活动描述与能力描述的语义相似程度
  - 功能匹配度: 活动需求与能力功能的匹配程度
  - 参数兼容性: 活动输入输出与能力参数的兼容程度

user_prompt_template: |
  当前流程: {{processName}}
  当前活动: {{activityName}}
  活动描述: {{activityDesc}}
  
  可用能力列表:
  {{#each capabilities}}
  - ID: {{capId}}
    名称: {{name}}
    描述: {{description}}
    类别: {{category}}
    参数: {{parameters}}
  {{/each}}
  
  请匹配最合适的能力，并给出匹配理由。
```

### 4.3 匹配结果数据结构

```java
public class CapabilityMatchingResult {
    private MatchingStatus status;
    private List<CapabilityMatch> matches;
    private String reasoning;
    private CapabilityBindingDTO recommendedBinding;
    
    public enum MatchingStatus {
        EXACT_MATCH,      // 精确匹配
        PARTIAL_MATCH,    // 部分匹配
        NO_MATCH,         // 无匹配
        NEED_CLARIFICATION // 需要澄清
    }
    
    public static class CapabilityMatch {
        private String capId;
        private String capName;
        private double matchScore;
        private MatchDimensionScores dimensionScores;
        private String matchReason;
        private CapabilityBindingDTO bindingConfig;
    }
    
    public static class MatchDimensionScores {
        private double semanticSimilarity;    // 语义相似度
        private double functionalMatch;       // 功能匹配度
        private double parameterCompatibility; // 参数兼容性
    }
}
```

### 4.4 能力匹配示例流程

```
活动描述: "约谈面试"

LLM分析:
- 动词: 约谈、面试
- 名词: 面试
- 推断能力需求: 日程安排、会议管理、通知发送

Function Calling:
1. search_capabilities(query="面试 日程 安排")
   → 返回: [calendar_schedule, meeting_arrange, notification_send]

2. get_capability_detail(capId="calendar_schedule")
   → 返回: 日程安排能力详情

LLM决策:
- 主能力: calendar_schedule (日程安排)
- 辅助能力: meeting_arrange (会议管理), notification_send (通知发送)
- 匹配度: 0.92

返回结果:
{
  "status": "EXACT_MATCH",
  "matches": [
    {
      "capId": "calendar_schedule",
      "capName": "日程安排",
      "matchScore": 0.92,
      "bindingConfig": {
        "priority": 1,
        "connectorType": "SDK",
        "llmConfig": {
          "enableFunctionCall": true
        }
      }
    }
  ]
}
```

---

## 五、表单匹配服务设计

### 5.1 服务架构

```java
@Service
public class FormMatchingService {
    
    @Autowired
    private DesignerFunctionRegistry functionRegistry;
    
    @Autowired
    private LLMService llmService;
    
    @Autowired
    private FormRepository formRepository;
    
    /**
     * 匹配表单
     */
    public FormMatchingResult match(DesignerContextDTO context, String activityDesc) {
        // 1. 获取可用表单
        List<FormDTO> forms = getAvailableForms(context);
        
        // 2. 构建匹配Prompt
        String prompt = buildMatchingPrompt(context, activityDesc, forms);
        
        // 3. 调用LLM
        LLMResponse response = llmService.chat(prompt);
        
        return parseMatchingResult(response, forms);
    }
    
    /**
     * 智能匹配表单（带Function Calling）
     */
    public FormMatchingResult smartMatch(DesignerContextDTO context, String activityDesc) {
        String prompt = buildSmartMatchingPrompt(context, activityDesc);
        List<Map<String, Object>> functions = getFormFunctions();
        
        LLMResponse response = llmService.chatWithFunctions(prompt, functions);
        
        if (response.hasFunctionCalls()) {
            for (FunctionCall call : response.getFunctionCalls()) {
                Object result = executeFunction(call);
                response = llmService.chatWithFunctionResult(prompt, call.getName(), result);
            }
        }
        
        return parseMatchingResult(response);
    }
    
    /**
     * 生成表单Schema
     */
    public FormSchema generateSchema(DesignerContextDTO context, String activityDesc) {
        String prompt = buildSchemaGenerationPrompt(context, activityDesc);
        LLMResponse response = llmService.chat(prompt);
        return parseSchemaResult(response);
    }
}
```

### 5.2 表单匹配Prompt模板

```yaml
system_prompt: |
  你是一个表单匹配专家，负责根据活动描述匹配合适的表单。
  
  表单匹配规则：
  1. 分析活动描述，识别需要收集的数据
  2. 根据表单字段进行语义匹配
  3. 考虑表单的适用场景和业务领域
  4. 返回匹配度排序和表单配置
  
  如果没有合适的现有表单，可以建议生成新表单。

user_prompt_template: |
  当前流程: {{processName}}
  当前活动: {{activityName}}
  活动描述: {{activityDesc}}
  
  可用表单列表:
  {{#each forms}}
  - ID: {{formId}}
    名称: {{name}}
    描述: {{description}}
    字段: {{fields}}
    类别: {{category}}
  {{/each}}
  
  请匹配最合适的表单，或建议生成新表单。
```

### 5.3 表单匹配结果数据结构

```java
public class FormMatchingResult {
    private MatchingStatus status;
    private List<FormMatch> matches;
    private String reasoning;
    private FormSchema suggestedSchema;  // 建议生成的新表单Schema
    
    public static class FormMatch {
        private String formId;
        private String formName;
        private double matchScore;
        private List<FieldMapping> fieldMappings;
        private String matchReason;
    }
    
    public static class FieldMapping {
        private String activityField;    // 活动需要的字段
        private String formField;        // 表单中的字段
        private double mappingScore;     // 映射匹配度
    }
    
    public static class FormSchema {
        private String formName;
        private String description;
        private List<FormField> fields;
        private List<String> required;
    }
}
```

---

## 六、数据格式定义

### 6.1 LLM引导决策数据结构

```java
public class LLMDerivationContext {
    private String sessionId;
    private DerivationType derivationType;
    private Map<String, Object> inputContext;
    private List<FunctionDefinition> availableFunctions;
    private List<FunctionCallTrace> callTraces;
    private DerivationResult result;
    
    public enum DerivationType {
        PERFORMER,      // 办理人推导
        CAPABILITY,     // 能力匹配
        FORM,           // 表单匹配
        FULL_PROCESS    // 完整流程推导
    }
}

public class FunctionCallTrace {
    private int sequence;
    private String functionName;
    private Map<String, Object> arguments;
    private Object result;
    private long executionTime;
    private String llmReasoning;  // LLM调用此函数的原因
}
```

### 6.2 面板渲染数据结构

```java
public class PanelRenderData {
    private String panelType;
    private String title;
    private String description;
    private List<RenderSection> sections;
    private Map<String, Object> derivedConfig;
    private List<DerivationSuggestion> suggestions;
    
    public static class RenderSection {
        private String sectionId;
        private String title;
        private RenderType renderType;
        private List<RenderItem> items;
        private boolean collapsible;
        private boolean editable;
    }
    
    public static class RenderItem {
        private String itemId;
        private String label;
        private Object value;
        private ValueType valueType;
        private boolean derived;      // 是否由LLM推导
        private double confidence;    // 推导置信度
        private String reasoning;     // 推导原因
        private List<Alternative> alternatives;  // 备选方案
    }
    
    public static class Alternative {
        private Object value;
        private double score;
        private String reason;
    }
    
    public enum RenderType {
        LIST,           // 列表渲染
        TREE,           // 树形渲染
        FORM,           // 表单渲染
        TABLE,          // 表格渲染
        CARD            // 卡片渲染
    }
    
    public enum ValueType {
        STRING, NUMBER, BOOLEAN, OBJECT, ARRAY, USER, ROLE, DEPT, CAPABILITY, FORM
    }
}
```

### 6.3 完整推导请求/响应格式

```json
{
  "request": {
    "sessionId": "session-001",
    "derivationType": "PERFORMER",
    "context": {
      "processId": "recruitment-approval",
      "processName": "招聘审批流程",
      "activityId": "hr-screening",
      "activityName": "HR筛选简历",
      "activityDescription": "HR筛选候选人简历，初步评估是否符合岗位要求"
    },
    "options": {
      "maxCandidates": 5,
      "includeReasoning": true,
      "includeAlternatives": true
    }
  },
  "response": {
    "status": "SUCCESS",
    "result": {
      "assigneeType": "ROLE",
      "assigneeId": "hr_specialist",
      "assigneeName": "HR专员",
      "candidates": [
        {
          "userId": "user-001",
          "userName": "张三",
          "matchScore": 0.95,
          "matchReason": "HR部门专员，负责招聘工作"
        }
      ]
    },
    "reasoning": "活动描述中明确提到'HR筛选简历'，这通常由人力资源部门的专员执行...",
    "functionTraces": [
      {
        "sequence": 1,
        "functionName": "get_users_by_role",
        "arguments": {"roleId": "hr_specialist"},
        "result": {"users": [...]},
        "llmReasoning": "需要获取HR专员角色的用户列表"
      }
    ],
    "panelData": {
      "panelType": "PERFORMER",
      "title": "办理人配置",
      "sections": [...]
    }
  }
}
```

---

## 七、面板渲染机制

### 7.1 渲染流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    LLM推导结果                                   │
│  {                                                              │
│    "assigneeType": "ROLE",                                      │
│    "assigneeId": "hr_specialist",                               │
│    "reasoning": "..."                                           │
│  }                                                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PanelRenderBuilder                           │
│  - 解析推导结果                                                  │
│  - 构建渲染数据结构                                              │
│  - 添加备选方案                                                  │
│  - 添加推理说明                                                  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    前端渲染                                      │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ 办理人配置                                    [AI推荐]   │    │
│  │ ─────────────────────────────────────────────────────── │    │
│  │ 办理人类型: [角色 ▼]                                    │    │
│  │ 办理人:     [HR专员 ▼]                                  │    │
│  │                                                         │    │
│  │ 💡 推荐理由: 活动描述中明确提到'HR筛选简历'...           │    │
│  │                                                         │    │
│  │ 备选方案:                                               │    │
│  │ ○ 指定人员: 张三 (匹配度: 95%)                          │    │
│  │ ○ 部门负责人: HR部门负责人                              │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### 7.2 渲染服务实现

```java
@Service
public class PanelRenderService {
    
    /**
     * 构建办理人面板渲染数据
     */
    public PanelRenderData buildPerformerPanel(PerformerDerivationResult result) {
        PanelRenderData panel = new PanelRenderData();
        panel.setPanelType("PERFORMER");
        panel.setTitle("办理人配置");
        
        // 基础配置区
        RenderSection basicSection = new RenderSection();
        basicSection.setTitle("基础配置");
        basicSection.setRenderType(RenderType.FORM);
        
        // 办理人类型
        RenderItem typeItem = new RenderItem();
        typeItem.setLabel("办理人类型");
        typeItem.setValue(result.getDerivedConfig().get("assigneeType"));
        typeItem.setDerived(true);
        typeItem.setConfidence(result.getConfidence());
        basicSection.getItems().add(typeItem);
        
        // 办理人
        RenderItem assigneeItem = new RenderItem();
        assigneeItem.setLabel("办理人");
        assigneeItem.setValue(result.getDerivedConfig().get("assigneeName"));
        assigneeItem.setDerived(true);
        assigneeItem.setReasoning(result.getReasoning());
        assigneeItem.setAlternatives(buildAlternatives(result.getCandidates()));
        basicSection.getItems().add(assigneeItem);
        
        panel.getSections().add(basicSection);
        
        // 推理说明区
        RenderSection reasoningSection = buildReasoningSection(result);
        panel.getSections().add(reasoningSection);
        
        return panel;
    }
    
    /**
     * 构建能力面板渲染数据
     */
    public PanelRenderData buildCapabilityPanel(CapabilityMatchingResult result) {
        PanelRenderData panel = new PanelRenderData();
        panel.setPanelType("CAPABILITY");
        panel.setTitle("能力配置");
        
        // 能力列表区
        RenderSection capSection = new RenderSection();
        capSection.setTitle("匹配的能力");
        capSection.setRenderType(RenderType.CARD);
        
        for (CapabilityMatch match : result.getMatches()) {
            RenderItem item = new RenderItem();
            item.setLabel(match.getCapName());
            item.setValue(match);
            item.setDerived(true);
            item.setConfidence(match.getMatchScore());
            capSection.getItems().add(item);
        }
        
        panel.getSections().add(capSection);
        
        return panel;
    }
}
```

---

## 八、完整实现任务列表

### 8.1 核心服务实现

| 序号 | 任务 | 优先级 | 依赖 |
|------|------|--------|------|
| 1 | 实现DesignerFunctionRegistry - 函数注册中心 | P0 | 无 |
| 2 | 实现组织机构相关Function Tools | P0 | 1 |
| 3 | 实现能力匹配相关Function Tools | P0 | 1 |
| 4 | 实现表单匹配相关Function Tools | P0 | 1 |
| 5 | 实现PerformerDerivationService | P0 | 2 |
| 6 | 实现CapabilityMatchingService | P0 | 3 |
| 7 | 实现FormMatchingService | P0 | 4 |
| 8 | 实现PanelRenderService | P1 | 5,6,7 |

### 8.2 接口层实现

| 序号 | 任务 | 优先级 | 依赖 |
|------|------|--------|------|
| 9 | 实现DesignerDerivationController | P0 | 5,6,7 |
| 10 | 实现WebSocket推送机制 | P1 | 8 |
| 11 | 实现前端面板渲染组件 | P1 | 8,10 |

### 8.3 Prompt工程

| 序号 | 任务 | 优先级 | 依赖 |
|------|------|--------|------|
| 12 | 设计办理人推导Prompt模板 | P0 | 无 |
| 13 | 设计能力匹配Prompt模板 | P0 | 无 |
| 14 | 设计表单匹配Prompt模板 | P0 | 无 |
| 15 | 设计完整流程推导Prompt模板 | P1 | 12,13,14 |

---

## 九、关键接口定义

### 9.1 DesignerFunctionRegistry

```java
public interface DesignerFunctionRegistry {
    
    void registerFunction(DesignerFunctionDefinition function);
    
    void registerFunctions(List<DesignerFunctionDefinition> functions);
    
    List<DesignerFunctionDefinition> getFunctionsByCategory(FunctionCategory category);
    
    List<Map<String, Object>> getOpenAISchemas();
    
    Object executeFunction(String functionName, Map<String, Object> arguments);
    
    List<FunctionCallTrace> executeFunctionCalls(List<FunctionCall> calls);
}
```

### 9.2 PerformerDerivationService

```java
public interface PerformerDerivationService {
    
    PerformerDerivationResult derive(DesignerContextDTO context, String activityDesc);
    
    PerformerDerivationResult deriveWithCandidates(
        DesignerContextDTO context, 
        String activityDesc,
        List<String> candidateUserIds
    );
    
    List<PerformerCandidate> searchCandidates(String query, Map<String, Object> filters);
}
```

### 9.3 CapabilityMatchingService

```java
public interface CapabilityMatchingService {
    
    CapabilityMatchingResult match(DesignerContextDTO context, String activityDesc);
    
    CapabilityMatchingResult smartMatch(DesignerContextDTO context, String activityDesc);
    
    List<CapabilityMatch> matchByKeywords(List<String> keywords);
    
    CapabilityBindingDTO buildBindingConfig(String capId, Map<String, Object> context);
}
```

### 9.4 FormMatchingService

```java
public interface FormMatchingService {
    
    FormMatchingResult match(DesignerContextDTO context, String activityDesc);
    
    FormMatchingResult smartMatch(DesignerContextDTO context, String activityDesc);
    
    FormSchema generateSchema(DesignerContextDTO context, String activityDesc);
    
    List<FormMatch> matchByFields(List<String> requiredFields);
}
```

---

## 十、总结

本方案设计了以LLM为核心的BPM Designer智能推导系统，核心要点：

1. **Function Calling机制** - 通过函数注册中心管理所有可用函数，LLM通过Function Calling获取组织、能力、表单等预定义信息

2. **三大核心服务**:
   - 办理人推导服务：基于活动描述语义推导合适的办理人
   - 能力匹配服务：基于活动描述语义匹配可用能力
   - 表单匹配服务：基于活动描述语义匹配或生成表单

3. **面板渲染机制** - 将LLM推导结果转换为前端可渲染的数据结构，支持推理说明和备选方案展示

4. **数据格式定义** - 完整的请求/响应格式、Function Call Trace记录、推导结果数据结构

下一步工作：
1. 实现DesignerFunctionRegistry函数注册中心
2. 实现组织机构、能力、表单相关的Function Tools
3. 实现三大核心推导服务
4. 实现面板渲染服务
5. 完善Prompt模板工程
