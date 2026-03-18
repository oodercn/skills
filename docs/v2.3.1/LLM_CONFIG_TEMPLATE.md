# 标准 llmConfig 配置模板

**版本**: 2.3.1  
**用途**: 所有 skills 都需要配置 llmConfig，因为 llm-chat 是内置能力助手

---

## 一、llmConfig 的作用

### 1.1 核心功能

llm-chat 是**内置能力助手**，作用于所有页面，提供：

1. **能力转换**: 将当前 skills 的能力转换为用户问题（能做什么/怎么用）
2. **交互方式**: UI + 对话两种操作窗口
3. **Function Calling**: 通过 MVEL/JavaScript 实现代填表单、批量操作

### 1.2 适用范围

**所有 skills 都需要 llmConfig**，包括：
- LLM Provider 类
- 媒体发布类
- 支付类
- 存储类
- 组织管理类
- 认证安全类
- 通信类
- 监控运维类
- 调度任务类
- 工具类
- 系统服务类

---

## 二、标准配置模板

### 2.1 完整模板

```yaml
llmConfig:
  required: false
  defaultProvider: "deepseek"
  defaultModel: "deepseek-chat"
  capabilities:
    - chat
    - streaming
    - function-calling
  modelSelection:
    allowUserOverride: true
    availableProviders:
      - deepseek
      - openai
      - qianwen
      - volcengine
      - ollama
  functionCalling:
    enabled: true
    tools:
      - name: query_skill_capability
        description: "查询当前技能的能力和使用方法"
        parameters:
          type: object
          properties:
            capability:
              type: string
              description: "能力名称"
            detail:
              type: string
              enum: [brief, detailed, examples]
              default: "brief"
              description: "详情级别"
      - name: execute_mvel_action
        description: "通过MVEL表达式执行后台操作"
        parameters:
          type: object
          properties:
            expression:
              type: string
              description: "MVEL表达式"
            context:
              type: object
              description: "执行上下文"
      - name: generate_ui_form
        description: "生成UI表单供用户填写"
        parameters:
          type: object
          properties:
            formType:
              type: string
              description: "表单类型"
            fields:
              type: array
              items:
                type: object
              description: "表单字段定义"
            defaults:
              type: object
              description: "默认值"
      - name: execute_batch_operation
        description: "执行批量操作"
        parameters:
          type: object
          properties:
            operation:
              type: string
              description: "操作类型"
            items:
              type: array
              items:
                type: object
              description: "操作项列表"
      - name: convert_to_javascript
        description: "转换为JavaScript代码供用户使用"
        parameters:
          type: object
          properties:
            action:
              type: string
              description: "要执行的动作"
            parameters:
              type: object
              description: "动作参数"
    toolChoice: auto
  rateLimits:
    requestsPerMinute: 60
    tokensPerMinute: 100000
```

### 2.2 精简模板（适用于非核心 skills）

```yaml
llmConfig:
  required: false
  defaultProvider: "deepseek"
  defaultModel: "deepseek-chat"
  capabilities:
    - chat
    - streaming
  functionCalling:
    enabled: true
    tools:
      - name: query_skill_capability
        description: "查询当前技能的能力和使用方法"
        parameters:
          type: object
          properties:
            capability:
              type: string
    toolChoice: auto
```

---

## 三、标准 Function Calling 工具说明

### 3.1 query_skill_capability

**用途**: 查询当前技能的能力和使用方法

**参数**:
- `capability`: 能力名称
- `detail`: 详情级别

**示例**:
```json
{
  "name": "query_skill_capability",
  "arguments": {
    "capability": "file-upload",
    "detail": "detailed"
  }
}
```

### 3.2 execute_mvel_action

**用途**: 通过 MVEL 表达式执行后台操作

**参数**:
- `expression`: MVEL 表达式
- `context`: 执行上下文

**示例**:
```json
{
  "name": "execute_mvel_action",
  "arguments": {
    "expression": "userService.getUser(userId).name",
    "context": {
      "userId": "12345"
    }
  }
}
```

### 3.3 generate_ui_form

**用途**: 生成 UI 表单供用户填写

**参数**:
- `formType`: 表单类型
- `fields`: 表单字段定义
- `defaults`: 默认值

**示例**:
```json
{
  "name": "generate_ui_form",
  "arguments": {
    "formType": "user-profile",
    "fields": [
      {"name": "username", "type": "text", "label": "用户名"},
      {"name": "email", "type": "email", "label": "邮箱"}
    ],
    "defaults": {
      "username": "张三"
    }
  }
}
```

### 3.4 execute_batch_operation

**用途**: 执行批量操作

**参数**:
- `operation`: 操作类型
- `items`: 操作项列表

**示例**:
```json
{
  "name": "execute_batch_operation",
  "arguments": {
    "operation": "delete",
    "items": [
      {"id": "1"},
      {"id": "2"},
      {"id": "3"}
    ]
  }
}
```

### 3.5 convert_to_javascript

**用途**: 转换为 JavaScript 代码供用户使用

**参数**:
- `action`: 要执行的动作
- `parameters`: 动作参数

**示例**:
```json
{
  "name": "convert_to_javascript",
  "arguments": {
    "action": "submit-form",
    "parameters": {
      "formId": "user-form",
      "data": {"name": "张三", "age": 25}
    }
  }
}
```

---

## 四、配置位置

llmConfig 应添加在 `runtime` 配置之后：

```yaml
spec:
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot

  llmConfig:
    # ... 配置内容

  dependencies:
    # ...
```

---

## 五、特殊配置

### 5.1 需要 embedding 的 skills

```yaml
llmConfig:
  required: true
  embedding:
    required: true
    defaultModel: "text-embedding-3-small"
    dimension: 1536
```

### 5.2 LLM Provider skills

LLM Provider skills 本身提供 LLM 能力，但仍需要 llmConfig 用于能力助手：

```yaml
llmConfig:
  required: false
  capabilities:
    - chat
    - streaming
  functionCalling:
    enabled: false
```

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-18
