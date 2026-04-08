# RAG 向量设计与规范化方案

**文档版本**: 1.0  
**创建日期**: 2026-04-08  
**项目路径**: E:\github\ooder-skills

---

## 📋 一、RAG 向量设计分析

### 1.1 现有 RagPipeline 实现

**文件路径**: `E:\github\ooder-skills\skills\_system\skill-rag\src\main\java\net\ooder\skill\rag\RagPipeline.java`

**核心功能**:

| 方法 | 功能 | 输入 | 输出 |
|------|------|------|------|
| `ingestBusinessData` | 摄取业务数据 | BusinessDataIngestRequest | KnowledgeDocument |
| `buildKnowledgeConfig` | 构建知识配置 | sceneGroupId, query | RagKnowledgeConfig |
| `enhancePromptWithRAG` | RAG 增强 Prompt | userQuery, sceneGroupId, knowledgeBaseIds | String (RAG Context) |
| `searchRelated` | 搜索相关文档 | query, limit | List<KnowledgeDocument> |
| `syncToDictionary` | 同步到字典 | doc, category | void |

### 1.2 向量化设计需求

#### 1.2.1 流程定义向量化

```java
public class ProcessDefVector {
    private String processDefId;
    private String name;
    private String description;
    private List<String> keywords;
    private float[] embedding;
    private Map<String, Float> fieldWeights;
    
    public ProcessDefVector(ProcessDefDTO processDef) {
        this.processDefId = processDef.getProcessDefId();
        this.name = processDef.getName();
        this.description = processDef.getDescription();
        this.keywords = extractKeywords(processDef);
        this.embedding = generateEmbedding(processDef);
        this.fieldWeights = calculateFieldWeights();
    }
    
    private List<String> extractKeywords(ProcessDefDTO processDef) {
        List<String> keywords = new ArrayList<>();
        keywords.add(processDef.getName());
        keywords.add(processDef.getDescription());
        
        if (processDef.getActivities() != null) {
            for (ActivityDefDTO activity : processDef.getActivities()) {
                keywords.add(activity.getName());
                keywords.add(activity.getActivityType());
                keywords.add(activity.getActivityCategory());
            }
        }
        
        return keywords.stream().distinct().collect(Collectors.toList());
    }
    
    private float[] generateEmbedding(ProcessDefDTO processDef) {
        String text = String.join(" ", extractKeywords(processDef));
        return embeddingService.embed(text);
    }
    
    private Map<String, Float> calculateFieldWeights() {
        Map<String, Float> weights = new HashMap<>();
        weights.put("name", 0.3f);
        weights.put("description", 0.2f);
        weights.put("activities", 0.3f);
        weights.put("routes", 0.2f);
        return weights;
    }
}
```

#### 1.2.2 活动定义向量化

```java
public class ActivityDefVector {
    private String activityDefId;
    private String processDefId;
    private String name;
    private String activityType;
    private String activityCategory;
    private float[] embedding;
    private Map<String, Object> configVector;
    
    public ActivityDefVector(ActivityDefDTO activityDef, String processDefId) {
        this.activityDefId = activityDef.getActivityDefId();
        this.processDefId = processDefId;
        this.name = activityDef.getName();
        this.activityType = activityDef.getActivityType();
        this.activityCategory = activityDef.getActivityCategory();
        this.embedding = generateEmbedding(activityDef);
        this.configVector = extractConfigVector(activityDef);
    }
    
    private float[] generateEmbedding(ActivityDefDTO activityDef) {
        StringBuilder text = new StringBuilder();
        text.append(activityDef.getName()).append(" ");
        text.append(activityDef.getActivityType()).append(" ");
        text.append(activityDef.getActivityCategory()).append(" ");
        text.append(activityDef.getDescription());
        
        return embeddingService.embed(text.toString());
    }
    
    private Map<String, Object> extractConfigVector(ActivityDefDTO activityDef) {
        Map<String, Object> vector = new HashMap<>();
        
        if (activityDef.getAgentConfig() != null) {
            vector.put("agentType", activityDef.getAgentConfig().get("agentType"));
            vector.put("llmModel", activityDef.getAgentConfig().get("llmModel"));
        }
        
        if (activityDef.getSceneConfig() != null) {
            vector.put("sceneType", activityDef.getSceneConfig().get("sceneType"));
            vector.put("capabilities", activityDef.getSceneConfig().get("capabilities"));
        }
        
        if (activityDef.getRight() != null) {
            vector.put("performType", activityDef.getRight().get("performType"));
            vector.put("performSequence", activityDef.getRight().get("performSequence"));
        }
        
        return vector;
    }
}
```

### 1.3 向量存储设计

```java
public interface VectorStoreProvider {
    void store(String id, float[] vector, Map<String, Object> metadata);
    List<VectorSearchResult> search(float[] query, int k, float minScore);
    void delete(String id);
    void update(String id, float[] vector, Map<String, Object> metadata);
}

public class VectorSearchResult {
    private String id;
    private float score;
    private Map<String, Object> metadata;
    
    public String getId() { return id; }
    public float getScore() { return score; }
    public Map<String, Object> getMetadata() { return metadata; }
}
```

---

## 📋 二、YAML/JSON 规范化方案

### 2.1 流程定义 YAML 规范

```yaml
process:
  id: leave_approval
  name: 请假审批流程
  description: 员工请假申请审批流程
  classification: NORMAL
  systemCode: HR_SYSTEM
  accessLevel: PUBLIC
  version: 1
  publicationStatus: DRAFT
  
  timing:
    limit: 3
    durationUnit: D
    activeTime: "2026-01-01 00:00:00"
    freezeTime: null
  
  metadata:
    creatorId: user001
    creatorName: 张三
    createdTime: "2026-04-08 10:00:00"
    modifierId: user001
    modifierName: 张三
    modifyTime: "2026-04-08 10:00:00"
  
  lock:
    strategy: Lock
    holder: user001
  
  extendedAttributes:
    approvalLevel: 2
    isUrgent: false
    department: HR

activities:
  - id: start
    name: 开始
    type: START
    category: HUMAN
    position: START
    implementation: IMPL_NO
    
  - id: submit
    name: 提交申请
    type: TASK
    category: HUMAN
    position: NORMAL
    implementation: IMPL_NO
    description: 员工提交请假申请
    timing:
      limit: 1
      durationUnit: D
    right:
      performType: SINGLE
      performSequence: FIRST
      
  - id: manager_approve
    name: 部门经理审批
    type: TASK
    category: HUMAN
    position: NORMAL
    implementation: IMPL_NO
    timing:
      limit: 2
      durationUnit: D
    right:
      performType: SINGLE
      performSequence: FIRST
      performerSelectedId: manager_formula
      performerSelectedAtt:
        formula: getManagers(dept)
        formulaType: EXPRESSION
        
  - id: hr_approve
    name: HR审批
    type: TASK
    category: AGENT
    position: NORMAL
    implementation: IMPL_AGENT
    agentConfig:
      agentId: hr_agent
      agentName: HR审批助手
      agentType: LLM_AGENT
      llmModel: gpt-4
      promptTemplate: |
        你是一个HR审批助手，请根据以下信息进行审批：
        - 申请人：{{applicant}}
        - 请假类型：{{leaveType}}
        - 请假天数：{{days}}
        - 请假原因：{{reason}}
      maxTokens: 2048
      temperature: 0.7
      
  - id: end
    name: 结束
    type: END
    category: HUMAN
    position: END
    implementation: IMPL_NO

routes:
  - id: route_1
    from: start
    to: submit
    name: 开始到提交
    
  - id: route_2
    from: submit
    to: manager_approve
    name: 提交到经理审批
    condition: "days <= 3"
    
  - id: route_3
    from: submit
    to: hr_approve
    name: 提交到HR审批
    condition: "days > 3"
    
  - id: route_4
    from: manager_approve
    to: end
    name: 经理审批到结束
    
  - id: route_5
    from: hr_approve
    to: end
    name: HR审批到结束
```

### 2.2 流程定义 JSON 规范

```json
{
  "processDefId": "leave_approval",
  "name": "请假审批流程",
  "description": "员工请假申请审批流程",
  "classification": "NORMAL",
  "systemCode": "HR_SYSTEM",
  "accessLevel": "PUBLIC",
  "version": 1,
  "publicationStatus": "DRAFT",
  "limit": 3,
  "durationUnit": "D",
  "activeTime": "2026-01-01T00:00:00",
  "freezeTime": null,
  "creatorId": "user001",
  "creatorName": "张三",
  "createdTime": "2026-04-08T10:00:00",
  "modifierId": "user001",
  "modifierName": "张三",
  "modifyTime": "2026-04-08T10:00:00",
  "mark": "ProcessInst",
  "lock": "Lock",
  "autoSave": true,
  "noSqlType": false,
  "tableNames": ["t_leave_request"],
  "moduleNames": ["hr"],
  "extendedAttributes": {
    "approvalLevel": 2,
    "isUrgent": false,
    "department": "HR"
  },
  "activities": [
    {
      "activityDefId": "start",
      "name": "开始",
      "activityType": "START",
      "activityCategory": "HUMAN",
      "position": "START",
      "implementation": "IMPL_NO"
    },
    {
      "activityDefId": "submit",
      "name": "提交申请",
      "activityType": "TASK",
      "activityCategory": "HUMAN",
      "position": "NORMAL",
      "implementation": "IMPL_NO",
      "description": "员工提交请假申请",
      "timing": {
        "limit": 1,
        "durationUnit": "D"
      },
      "right": {
        "performType": "SINGLE",
        "performSequence": "FIRST"
      }
    },
    {
      "activityDefId": "manager_approve",
      "name": "部门经理审批",
      "activityType": "TASK",
      "activityCategory": "HUMAN",
      "position": "NORMAL",
      "implementation": "IMPL_NO",
      "timing": {
        "limit": 2,
        "durationUnit": "D"
      },
      "right": {
        "performType": "SINGLE",
        "performSequence": "FIRST",
        "performerSelectedId": "manager_formula",
        "performerSelectedAtt": {
          "formula": "getManagers(dept)",
          "formulaType": "EXPRESSION"
        }
      }
    },
    {
      "activityDefId": "hr_approve",
      "name": "HR审批",
      "activityType": "TASK",
      "activityCategory": "AGENT",
      "position": "NORMAL",
      "implementation": "IMPL_AGENT",
      "agentConfig": {
        "agentId": "hr_agent",
        "agentName": "HR审批助手",
        "agentType": "LLM_AGENT",
        "llmModel": "gpt-4",
        "promptTemplate": "你是一个HR审批助手...",
        "maxTokens": 2048,
        "temperature": 0.7
      }
    },
    {
      "activityDefId": "end",
      "name": "结束",
      "activityType": "END",
      "activityCategory": "HUMAN",
      "position": "END",
      "implementation": "IMPL_NO"
    }
  ],
  "routes": [
    {
      "routeDefId": "route_1",
      "fromActivityDefId": "start",
      "toActivityDefId": "submit",
      "name": "开始到提交"
    },
    {
      "routeDefId": "route_2",
      "fromActivityDefId": "submit",
      "toActivityDefId": "manager_approve",
      "name": "提交到经理审批",
      "condition": "days <= 3"
    },
    {
      "routeDefId": "route_3",
      "fromActivityDefId": "submit",
      "toActivityDefId": "hr_approve",
      "name": "提交到HR审批",
      "condition": "days > 3"
    },
    {
      "routeDefId": "route_4",
      "fromActivityDefId": "manager_approve",
      "toActivityDefId": "end",
      "name": "经理审批到结束"
    },
    {
      "routeDefId": "route_5",
      "fromActivityDefId": "hr_approve",
      "toActivityDefId": "end",
      "name": "HR审批到结束"
    }
  ]
}
```

### 2.3 规范化验证规则

```java
public class ProcessDefValidator {
    
    public ValidationResult validateYaml(String yamlContent) {
        ValidationResult result = new ValidationResult();
        
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(yamlContent);
        
        validateProcessSection(data, result);
        validateActivitiesSection(data, result);
        validateRoutesSection(data, result);
        validateReferences(data, result);
        
        return result;
    }
    
    public ValidationResult validateJson(String jsonContent) {
        ValidationResult result = new ValidationResult();
        
        ObjectMapper mapper = new ObjectMapper();
        try {
            ProcessDefDTO processDef = mapper.readValue(jsonContent, ProcessDefDTO.class);
            validateProcessDef(processDef, result);
        } catch (Exception e) {
            result.addError("JSON解析失败: " + e.getMessage());
        }
        
        return result;
    }
    
    private void validateProcessSection(Map<String, Object> data, ValidationResult result) {
        Map<String, Object> process = (Map<String, Object>) data.get("process");
        
        if (process == null) {
            result.addError("缺少 process 节点");
            return;
        }
        
        if (!process.containsKey("id")) {
            result.addError("process.id 不能为空");
        }
        
        if (!process.containsKey("name")) {
            result.addError("process.name 不能为空");
        }
        
        if (!process.containsKey("accessLevel")) {
            result.addError("process.accessLevel 不能为空");
        }
        
        validateEnumValue("process.accessLevel", process.get("accessLevel"), 
            Arrays.asList("PUBLIC", "PRIVATE", "BLOCK"), result);
    }
    
    private void validateActivitiesSection(Map<String, Object> data, ValidationResult result) {
        List<Map<String, Object>> activities = (List<Map<String, Object>>) data.get("activities");
        
        if (activities == null || activities.isEmpty()) {
            result.addError("activities 不能为空");
            return;
        }
        
        boolean hasStart = false;
        boolean hasEnd = false;
        Set<String> ids = new HashSet<>();
        
        for (int i = 0; i < activities.size(); i++) {
            Map<String, Object> activity = activities.get(i);
            String prefix = "activities[" + i + "]";
            
            if (!activity.containsKey("id")) {
                result.addError(prefix + ".id 不能为空");
            } else {
                String id = (String) activity.get("id");
                if (ids.contains(id)) {
                    result.addError(prefix + ".id 重复: " + id);
                }
                ids.add(id);
            }
            
            if (!activity.containsKey("type")) {
                result.addError(prefix + ".type 不能为空");
            } else {
                String type = (String) activity.get("type");
                if ("START".equals(type)) hasStart = true;
                if ("END".equals(type)) hasEnd = true;
                
                validateEnumValue(prefix + ".type", type,
                    Arrays.asList("TASK", "SERVICE", "SCRIPT", "START", "END", 
                        "XOR_GATEWAY", "AND_GATEWAY", "OR_GATEWAY", "SUBPROCESS", "LLM_TASK"),
                    result);
            }
            
            if (!activity.containsKey("category")) {
                result.addError(prefix + ".category 不能为空");
            }
        }
        
        if (!hasStart) {
            result.addWarning("缺少 START 类型的活动");
        }
        if (!hasEnd) {
            result.addWarning("缺少 END 类型的活动");
        }
    }
    
    private void validateRoutesSection(Map<String, Object> data, ValidationResult result) {
        List<Map<String, Object>> routes = (List<Map<String, Object>>) data.get("routes");
        
        if (routes == null || routes.isEmpty()) {
            result.addWarning("routes 为空，流程可能无法正常流转");
            return;
        }
        
        Set<String> activityIds = getActivityIds(data);
        
        for (int i = 0; i < routes.size(); i++) {
            Map<String, Object> route = routes.get(i);
            String prefix = "routes[" + i + "]";
            
            if (!route.containsKey("from")) {
                result.addError(prefix + ".from 不能为空");
            } else if (!activityIds.contains(route.get("from"))) {
                result.addError(prefix + ".from 引用的活动不存在: " + route.get("from"));
            }
            
            if (!route.containsKey("to")) {
                result.addError(prefix + ".to 不能为空");
            } else if (!activityIds.contains(route.get("to"))) {
                result.addError(prefix + ".to 引用的活动不存在: " + route.get("to"));
            }
        }
    }
    
    private void validateReferences(Map<String, Object> data, ValidationResult result) {
        List<Map<String, Object>> activities = (List<Map<String, Object>>) data.get("activities");
        List<Map<String, Object>> routes = (List<Map<String, Object>>) data.get("routes");
        
        if (activities == null || routes == null) return;
        
        Set<String> activityIds = activities.stream()
            .map(a -> (String) a.get("id"))
            .collect(Collectors.toSet());
        
        Map<String, Integer> incomingCount = new HashMap<>();
        Map<String, Integer> outgoingCount = new HashMap<>();
        
        for (Map<String, Object> route : routes) {
            String from = (String) route.get("from");
            String to = (String) route.get("to");
            
            outgoingCount.merge(from, 1, Integer::sum);
            incomingCount.merge(to, 1, Integer::sum);
        }
        
        for (String id : activityIds) {
            if (!incomingCount.containsKey(id) && !"start".equals(id.toLowerCase())) {
                result.addWarning("活动 " + id + " 没有入路由");
            }
            if (!outgoingCount.containsKey(id) && !"end".equals(id.toLowerCase())) {
                result.addWarning("活动 " + id + " 没有出路由");
            }
        }
    }
    
    private void validateEnumValue(String field, Object value, List<String> validValues, 
                                   ValidationResult result) {
        if (value == null) return;
        
        String strValue = value.toString().toUpperCase();
        if (!validValues.contains(strValue)) {
            result.addError(field + " 的值 " + value + " 不在有效值列表中: " + validValues);
        }
    }
    
    private Set<String> getActivityIds(Map<String, Object> data) {
        List<Map<String, Object>> activities = (List<Map<String, Object>>) data.get("activities");
        if (activities == null) return Collections.emptySet();
        
        return activities.stream()
            .map(a -> (String) a.get("id"))
            .collect(Collectors.toSet());
    }
}
```

---

## 📋 三、NLP 语境标准设计

### 3.1 Workflow-Chat 提示语环境

```java
public class WorkflowChatPromptBuilder {
    
    public String buildSystemPrompt(WorkflowChatContext context) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append(buildRoleSection());
        prompt.append("\n");
        prompt.append(buildTerminologySection());
        prompt.append("\n");
        prompt.append(buildSchemaSection());
        prompt.append("\n");
        prompt.append(buildRulesSection());
        prompt.append("\n");
        prompt.append(buildExamplesSection());
        
        return prompt.toString();
    }
    
    private String buildRoleSection() {
        return """
            ## 角色定义
            
            你是一个专业的 BPM 流程设计助手，具备以下能力：
            
            1. **流程设计**：根据用户描述创建完整的流程定义
            2. **活动配置**：创建和配置各种类型的活动节点
            3. **路由设计**：设置活动之间的流转条件和路径
            4. **权限配置**：配置办理人、阅办人、代签人等权限
            5. **智能建议**：根据上下文提供设计建议和最佳实践
            
            """;
    }
    
    private String buildTerminologySection() {
        return """
            ## 核心术语
            
            ### 流程术语
            - **流程定义 (ProcessDef)**: 完整的业务流程描述
            - **活动定义 (ActivityDef)**: 流程中的执行单元
            - **路由定义 (RouteDef)**: 活动之间的流转路径
            
            ### 活动类型
            - **TASK**: 用户任务 - 需要人工处理
            - **SERVICE**: 服务任务 - 自动服务调用
            - **START**: 开始节点 - 流程起始点
            - **END**: 结束节点 - 流程结束点
            - **XOR_GATEWAY**: 排他网关 - 条件分支
            - **AND_GATEWAY**: 并行网关 - 并行分支
            - **LLM_TASK**: LLM任务 - AI智能处理
            
            ### 活动分类
            - **HUMAN**: 人工活动 - 需要人工参与
            - **AGENT**: Agent活动 - AI代理执行
            - **SCENE**: 场景活动 - 场景驱动执行
            
            ### 办理类型
            - **SINGLE**: 单人办理
            - **MULTIPLE**: 多人办理
            - **JOINTSIGN**: 会签
            
            """;
    }
    
    private String buildSchemaSection() {
        return """
            ## 数据结构
            
            ### 流程定义结构
            ```json
            {
              "processDefId": "流程ID",
              "name": "流程名称",
              "description": "流程描述",
              "accessLevel": "访问级别",
              "activities": [...],
              "routes": [...]
            }
            ```
            
            ### 活动定义结构
            ```json
            {
              "activityDefId": "活动ID",
              "name": "活动名称",
              "activityType": "活动类型",
              "activityCategory": "活动分类",
              "implementation": "实现方式",
              "timing": {...},
              "right": {...},
              "agentConfig": {...}
            }
            ```
            
            """;
    }
    
    private String buildRulesSection() {
        return """
            ## 交互规则
            
            1. **理解意图**
               - 分析用户描述的核心意图
               - 识别要创建或修改的对象
               - 提取关键属性信息
            
            2. **规范输出**
               - 使用标准 JSON 格式
               - 确保必填字段完整
               - 使用正确的枚举值
            
            3. **智能建议**
               - 基于上下文提供建议
               - 遵循最佳实践
               - 检查设计合理性
            
            4. **错误处理**
               - 检测输入问题
               - 提供清晰错误说明
               - 给出修复建议
            
            """;
    }
    
    private String buildExamplesSection() {
        return """
            ## 示例交互
            
            ### 示例1：创建流程
            用户: "创建一个请假审批流程"
            
            回复:
            ```json
            {
              "action": "create_process",
              "params": {
                "processDefId": "leave_approval",
                "name": "请假审批流程",
                "activities": [
                  {"activityDefId": "start", "name": "开始", "activityType": "START"},
                  {"activityDefId": "submit", "name": "提交申请", "activityType": "TASK"},
                  {"activityDefId": "approve", "name": "审批", "activityType": "TASK"},
                  {"activityDefId": "end", "name": "结束", "activityType": "END"}
                ]
              }
            }
            ```
            
            ### 示例2：添加活动
            用户: "添加一个经理审批节点"
            
            回复:
            ```json
            {
              "action": "add_activity",
              "params": {
                "activityDefId": "manager_approve",
                "name": "经理审批",
                "activityType": "TASK",
                "activityCategory": "HUMAN",
                "right": {
                  "performType": "SINGLE"
                }
              }
            }
            ```
            
            """;
    }
    
    public String buildUserPrompt(String userInput, WorkflowChatContext context) {
        StringBuilder prompt = new StringBuilder();
        
        if (context.hasConversationHistory()) {
            prompt.append("## 对话历史\n");
            for (ChatMessage msg : context.getConversationHistory()) {
                prompt.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            }
            prompt.append("\n");
        }
        
        if (context.hasCurrentProcess()) {
            prompt.append("## 当前流程\n");
            prompt.append("- ID: ").append(context.getCurrentProcess().getProcessDefId()).append("\n");
            prompt.append("- 名称: ").append(context.getCurrentProcess().getName()).append("\n");
            prompt.append("- 活动数: ").append(context.getCurrentProcess().getActivities().size()).append("\n");
            prompt.append("\n");
        }
        
        prompt.append("## 用户输入\n");
        prompt.append(userInput).append("\n");
        
        return prompt.toString();
    }
}
```

---

## 📋 四、Skills README.md 规范

### 4.1 README.md 标准模板

```markdown
# Skill 名称

## 概述

简要描述该 Skill 的功能和用途。

## 功能特性

- 功能点1
- 功能点2
- 功能点3

## 配置说明

### 必需配置

| 配置项 | 类型 | 描述 | 示例值 |
|--------|------|------|--------|
| config1 | String | 配置说明 | value1 |
| config2 | Integer | 配置说明 | 100 |

### 可选配置

| 配置项 | 类型 | 描述 | 默认值 |
|--------|------|------|--------|
| config3 | Boolean | 配置说明 | false |

## API 接口

### 接口1

**请求**:
```json
{
  "param1": "value1",
  "param2": "value2"
}
```

**响应**:
```json
{
  "code": 200,
  "data": {...}
}
```

## 使用示例

### 示例1

描述示例场景...

```java
// 代码示例
```

## 流程定义

该 Skill 可用于以下流程场景：

### 场景1: XXX处理

```yaml
activities:
  - id: activity1
    name: XXX处理
    type: TASK
    category: AGENT
    implementation: IMPL_AGENT
    agentConfig:
      skillId: this-skill-id
```

## 依赖

- 依赖1
- 依赖2

## 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| 1.0.0 | 2026-04-08 | 初始版本 |
```

### 4.2 README.md 解析器

```java
public class SkillReadmeParser {
    
    public SkillDefinition parse(String readmeContent) {
        SkillDefinition skill = new SkillDefinition();
        
        skill.setName(extractSection(readmeContent, "# (.+)"));
        skill.setOverview(extractSection(readmeContent, "## 概述\n\n(.+?)\n\n"));
        skill.setFeatures(extractList(readmeContent, "## 功能特性\n\n((?:- .+\n)+)"));
        skill.setConfig(extractConfig(readmeContent));
        skill.setApiInterfaces(extractApiInterfaces(readmeContent));
        skill.setProcessScenarios(extractProcessScenarios(readmeContent));
        
        return skill;
    }
    
    private String extractSection(String content, String pattern) {
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(content);
        return m.find() ? m.group(1).trim() : null;
    }
    
    private List<String> extractList(String content, String pattern) {
        List<String> items = new ArrayList<>();
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(content);
        
        if (m.find()) {
            String listContent = m.group(1);
            for (String line : listContent.split("\n")) {
                if (line.startsWith("- ")) {
                    items.add(line.substring(2).trim());
                }
            }
        }
        
        return items;
    }
    
    private SkillConfig extractConfig(String content) {
        SkillConfig config = new SkillConfig();
        
        String requiredConfig = extractSection(content, 
            "### 必需配置\n\n\\|[^\\|]+\\|[^\\|]+\\|[^\\|]+\\|[^\\|]+\\|\\n\\|[-]+\\|[-]+\\|[-]+\\|[-]+\\|\\n((?:\\|[^\\|]+\\|[^\\|]+\\|[^\\|]+\\|[^\\|]+\\|\\n)+)");
        
        if (requiredConfig != null) {
            for (String line : requiredConfig.split("\n")) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    ConfigItem item = new ConfigItem();
                    item.setName(parts[1].trim());
                    item.setType(parts[2].trim());
                    item.setDescription(parts[3].trim());
                    item.setExample(parts[4].trim());
                    item.setRequired(true);
                    config.addRequired(item);
                }
            }
        }
        
        return config;
    }
    
    private List<ProcessScenario> extractProcessScenarios(String content) {
        List<ProcessScenario> scenarios = new ArrayList<>();
        
        Pattern p = Pattern.compile("### 场景\\d+: (.+)\n\n```yaml\\n([\\s\\S]+?)```");
        Matcher m = p.matcher(content);
        
        while (m.find()) {
            ProcessScenario scenario = new ProcessScenario();
            scenario.setName(m.group(1).trim());
            scenario.setYamlDefinition(m.group(2).trim());
            scenarios.add(scenario);
        }
        
        return scenarios;
    }
    
    private List<ApiInterface> extractApiInterfaces(String content) {
        List<ApiInterface> apis = new ArrayList<>();
        
        Pattern p = Pattern.compile("### (.+)\n\n\\*\\*请求\\*\\*:\n```json\n([\\s\\S]+?)```\n\n\\*\\*响应\\*\\*:\n```json\n([\\s\\S]+?)```");
        Matcher m = p.matcher(content);
        
        while (m.find()) {
            ApiInterface api = new ApiInterface();
            api.setName(m.group(1).trim());
            api.setRequestSchema(m.group(2).trim());
            api.setResponseSchema(m.group(3).trim());
            apis.add(api);
        }
        
        return apis;
    }
}
```

---

## 📋 五、自动生成方案设计

### 5.1 从 README.md 自动生成流程定义

```java
public class ProcessDefGenerator {
    
    @Autowired
    private SkillReadmeParser readmeParser;
    
    @Autowired
    private RagPipeline ragPipeline;
    
    public ProcessDefDTO generateFromReadme(String readmeContent, String skillId) {
        SkillDefinition skill = readmeParser.parse(readmeContent);
        
        ProcessDefDTO processDef = new ProcessDefDTO();
        processDef.setProcessDefId(skillId + "_process");
        processDef.setName(skill.getName() + " 流程");
        processDef.setDescription(skill.getOverview());
        processDef.setClassification("NORMAL");
        processDef.setAccessLevel("PUBLIC");
        processDef.setVersion(1);
        processDef.setPublicationStatus("DRAFT");
        
        List<ActivityDefDTO> activities = generateActivities(skill);
        processDef.setActivities(activities);
        
        List<RouteDefDTO> routes = generateRoutes(activities);
        processDef.setRoutes(routes);
        
        return processDef;
    }
    
    private List<ActivityDefDTO> generateActivities(SkillDefinition skill) {
        List<ActivityDefDTO> activities = new ArrayList<>();
        
        activities.add(createStartActivity());
        
        if (!skill.getFeatures().isEmpty()) {
            for (String feature : skill.getFeatures()) {
                ActivityDefDTO activity = createFeatureActivity(feature, skill.getSkillId());
                activities.add(activity);
            }
        }
        
        if (!skill.getProcessScenarios().isEmpty()) {
            ProcessScenario scenario = skill.getProcessScenarios().get(0);
            List<ActivityDefDTO> scenarioActivities = parseYamlActivities(scenario.getYamlDefinition());
            activities.addAll(scenarioActivities);
        }
        
        activities.add(createEndActivity());
        
        return activities;
    }
    
    private ActivityDefDTO createFeatureActivity(String feature, String skillId) {
        ActivityDefDTO activity = new ActivityDefDTO();
        activity.setActivityDefId(generateActivityId(feature));
        activity.setName(extractActivityName(feature));
        activity.setActivityType("TASK");
        activity.setActivityCategory("AGENT");
        activity.setImplementation("IMPL_AGENT");
        activity.setDescription(feature);
        
        Map<String, Object> agentConfig = new HashMap<>();
        agentConfig.put("skillId", skillId);
        agentConfig.put("feature", feature);
        agentConfig.put("agentType", "SKILL_AGENT");
        activity.setAgentConfig(agentConfig);
        
        return activity;
    }
    
    private List<RouteDefDTO> generateRoutes(List<ActivityDefDTO> activities) {
        List<RouteDefDTO> routes = new ArrayList<>();
        
        for (int i = 0; i < activities.size() - 1; i++) {
            RouteDefDTO route = new RouteDefDTO();
            route.setRouteDefId("route_" + i);
            route.setFromActivityDefId(activities.get(i).getActivityDefId());
            route.setToActivityDefId(activities.get(i + 1).getActivityDefId());
            route.setName(activities.get(i).getName() + " -> " + activities.get(i + 1).getName());
            routes.add(route);
        }
        
        return routes;
    }
    
    private ActivityDefDTO createStartActivity() {
        ActivityDefDTO activity = new ActivityDefDTO();
        activity.setActivityDefId("start");
        activity.setName("开始");
        activity.setActivityType("START");
        activity.setActivityCategory("HUMAN");
        activity.setPosition("START");
        activity.setImplementation("IMPL_NO");
        return activity;
    }
    
    private ActivityDefDTO createEndActivity() {
        ActivityDefDTO activity = new ActivityDefDTO();
        activity.setActivityDefId("end");
        activity.setName("结束");
        activity.setActivityType("END");
        activity.setActivityCategory("HUMAN");
        activity.setPosition("END");
        activity.setImplementation("IMPL_NO");
        return activity;
    }
    
    private String generateActivityId(String feature) {
        return feature.toLowerCase()
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_|_$", "");
    }
    
    private String extractActivityName(String feature) {
        return feature.split("[，,。.]")[0].trim();
    }
    
    private List<ActivityDefDTO> parseYamlActivities(String yamlContent) {
        List<ActivityDefDTO> activities = new ArrayList<>();
        
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(yamlContent);
        
        if (data.containsKey("activities")) {
            List<Map<String, Object>> activityList = (List<Map<String, Object>>) data.get("activities");
            
            for (Map<String, Object> activityMap : activityList) {
                ActivityDefDTO activity = new ActivityDefDTO();
                activity.setActivityDefId((String) activityMap.get("id"));
                activity.setName((String) activityMap.get("name"));
                activity.setActivityType((String) activityMap.get("type"));
                activity.setActivityCategory((String) activityMap.get("category"));
                activity.setImplementation((String) activityMap.get("implementation"));
                
                if (activityMap.containsKey("agentConfig")) {
                    activity.setAgentConfig((Map<String, Object>) activityMap.get("agentConfig"));
                }
                
                activities.add(activity);
            }
        }
        
        return activities;
    }
}
```

### 5.2 自动生成流程

```
┌─────────────────────────────────────────────────────────────┐
│                  自动生成流程                                 │
│                                                              │
│  1. README.md 解析                                           │
│     - 提取 Skill 名称、描述                                   │
│     - 提取功能特性列表                                        │
│     - 提取配置项                                              │
│     - 提取 API 接口定义                                       │
│     - 提取流程场景示例                                        │
│                                                              │
│  2. 流程定义生成                                              │
│     - 创建流程基本信息                                        │
│     - 根据功能特性生成活动                                    │
│     - 根据场景示例补充活动                                    │
│     - 自动生成路由连接                                        │
│                                                              │
│  3. RAG 增强                                                  │
│     - 向量化流程定义                                          │
│     - 存储到知识库                                            │
│     - 建立检索索引                                            │
│                                                              │
│  4. 验证和优化                                                │
│     - 验证流程完整性                                          │
│     - 检查活动引用                                            │
│     - 优化流程结构                                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 六、实施建议

### 6.1 分阶段实施

| 阶段 | 任务 | 预计时间 |
|------|------|----------|
| 第一阶段 | 完善术语表文档 | 1 天 |
| 第二阶段 | 实现 YAML/JSON 验证器 | 2 天 |
| 第三阶段 | 实现 NLP 语境标准 | 2 天 |
| 第四阶段 | 实现 README.md 解析器 | 2 天 |
| 第五阶段 | 实现自动生成器 | 3 天 |
| 第六阶段 | 集成测试和优化 | 2 天 |

### 6.2 技术选型

| 组件 | 技术选型 | 说明 |
|------|----------|------|
| 向量数据库 | Milvus / Pinecone | 存储流程定义向量 |
| YAML 解析 | SnakeYAML | Java YAML 解析库 |
| JSON 解析 | Jackson | Java JSON 处理库 |
| Markdown 解析 | CommonMark | Markdown 解析库 |
| LLM 集成 | OpenAI API | 智能生成和推理 |

---

**文档生成时间**: 2026-04-08  
**文档路径**: E:\github\ooder-skills\docs\bpm-designer\rag-vector-design-spec.md
