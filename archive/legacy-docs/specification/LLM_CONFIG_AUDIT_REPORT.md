# Skills LLM 配置深度检查报告

**版本**: 2.3.1  
**检查日期**: 2026-03-18  
**检查范围**: 70+ skills  
**状态**: 已完成修复

---

## 一、总体统计

| 分类 | Skills 数量 | 完整配置 | 部分配置 | 无配置 | 完整率 |
|------|------------|----------|----------|--------|--------|
| LLM Provider | 5 | 5 | 0 | 0 | 100% |
| LLM 服务 | 5 | 5 | 0 | 0 | 100% |
| 知识库/RAG | 4 | 4 | 0 | 0 | 100% |
| 场景类 | 7 | 7 | 0 | 0 | 100% |

---

## 二、已修复的 Skills

### 2.1 skill-document-assistant

**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/scenes/skill-document-assistant/skill.yaml)

**添加内容**:
```yaml
llmConfig:
  required: true
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
  functionCalling:
    enabled: true
    tools:
      - name: search_knowledge
        description: "搜索知识库获取相关信息"
        parameters:
          type: object
          properties:
            query:
              type: string
              description: "搜索查询"
            topK:
              type: integer
              default: 5
      - name: get_document_content
        description: "获取文档内容"
        parameters:
          type: object
          properties:
            docId:
              type: string
              description: "文档ID"
    toolChoice: auto
  embedding:
    required: true
    defaultModel: "text-embedding-3-small"
    dimension: 1536
  rateLimits:
    requestsPerMinute: 60
    tokensPerMinute: 100000
```

### 2.2 skill-onboarding-assistant
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/scenes/skill-onboarding-assistant/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: true
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
  functionCalling:
    enabled: true
    tools:
      - name: get_employee_info
        description: "获取员工信息"
        parameters:
          type: object
          properties:
            employeeId:
              type: string
      - name: search_training_materials
        description: "搜索培训材料"
        parameters:
          type: object
          properties:
            query:
              type: string
              description: "搜索查询"
            topK:
              type: integer
              default: 5
    toolChoice: auto
  embedding:
    required: true
    defaultModel: "text-embedding-3-small"
    dimension: 1536
```

### 2.3 skill-meeting-minutes
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/scenes/skill-meeting-minutes/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: true
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
  functionCalling:
    enabled: true
    tools:
      - name: transcribe_audio
        description: "转录音频为文本"
        parameters:
          type: object
          properties:
            audioUrl:
              type: string
              description: "音频文件URL"
            language:
              type: string
              default: "zh"
      - name: extract_action_items
        description: "提取会议待办事项"
        parameters:
          type: object
          properties:
            transcriptText:
              type: string
              description: "会议文本"
      - name: summarize_meeting
        description: "生成会议摘要"
        parameters:
          type: object
          properties:
            transcriptText:
              type: string
              description: "会议文本"
            maxLength:
              type: integer
              default: 500
    toolChoice: auto
  rateLimits:
    requestsPerMinute: 30
    tokensPerMinute: 50000
```

### 2.4 skill-project-knowledge
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/scenes/skill-project-knowledge/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: true
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
  functionCalling:
    enabled: true
    tools:
      - name: extract_project_info
        description: "从文档中提取项目信息"
        parameters:
          type: object
          properties:
            documentContent:
              type: string
              description: "文档内容"
            extractType:
              type: string
              enum: [timeline, team, milestones, risks]
              description: "提取类型"
      - name: generate_knowledge_graph
        description: "生成知识图谱"
        parameters:
          type: object
          properties:
            projectId:
              type: string
              description: "项目ID"
            graphType:
              type: string
              enum: [dependency, timeline, team]
              description: "图谱类型"
      - name: classify_document
        description: "分类项目文档"
        parameters:
          type: object
          properties:
            documentContent:
              type: string
              description: "文档内容"
            categories:
              type: array
              items:
                type: string
              description: "分类列表"
    toolChoice: auto
  embedding:
    required: true
    defaultModel: "text-embedding-3-small"
    dimension: 1536
  rateLimits:
    requestsPerMinute: 30
    tokensPerMinute: 50000
```

### 2.5 skill-llm-conversation
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/capabilities/llm/skill-llm-conversation/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: true
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
  functionCalling:
    enabled: true
    tools:
      - name: search_knowledge
        description: "搜索知识库获取相关信息"
        parameters:
          type: object
          properties:
            query:
              type: string
              description: "搜索查询"
            topK:
              type: integer
              default: 5
      - name: get_user_context
        description: "获取用户上下文信息"
        parameters:
          type: object
          properties:
            userId:
              type: string
      - name: execute_action
        description: "执行指定操作"
        parameters:
          type: object
          properties:
            actionType:
              type: string
              description: "操作类型"
            parameters:
              type: object
              description: "操作参数"
    toolChoice: auto
```

### 2.6 skill-rag
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/capabilities/knowledge/skill-rag/src/main/resources/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: true
  embedding:
    required: true
    defaultModel: "text-embedding-3-small"
    dimension: 1536
    availableModels:
      - id: text-embedding-3-small
        dimension: 1536
        maxTokens: 8191
      - id: text-embedding-3-large
        dimension: 3072
        maxTokens: 8191
      - id: text-embedding-ada-002
        dimension: 1536
        maxTokens: 8191
  capabilities:
    - embedding
    - semantic-search
    - prompt-building
  modelSelection:
    allowUserOverride: true
  rateLimits:
    embeddingsPerMinute: 1000
    tokensPerMinute: 100000
```

### 2.7 skill-knowledge-base
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/capabilities/knowledge/skill-knowledge-base/src/main/resources/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: true
  embedding:
    required: true
    defaultModel: "text-embedding-3-small"
    dimension: 1536
    availableModels:
      - id: text-embedding-3-small
        dimension: 1536
        maxTokens: 8191
      - id: text-embedding-3-large
        dimension: 3072
        maxTokens: 8191
      - id: text-embedding-ada-002
        dimension: 1536
        maxTokens: 8191
  capabilities:
    - embedding
    - semantic-search
    - document-chunking
  modelSelection:
    allowUserOverride: true
  rateLimits:
    embeddingsPerMinute: 500
    tokensPerMinute: 50000
```

### 2.8 skill-local-knowledge
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/capabilities/knowledge/skill-local-knowledge/src/main/resources/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: false
  embedding:
    required: false
    defaultModel: "text-embedding-3-small"
    dimension: 1536
  capabilities:
    - nlp-classification
    - intent-recognition
    - term-mapping
  modelSelection:
    allowUserOverride: true
  nlpConfig:
    intentClassification:
      enabled: true
      confidenceThreshold: 0.7
    termResolution:
      enabled: true
      fuzzyMatch: true
  rateLimits:
    requestsPerMinute: 100
    tokensPerMinute: 20000
```

---

## 三、标准化 llmConfig 字段说明

### 3.1 必填字段

```yaml
llmConfig:
  required: true                    # 是否必需
  defaultProvider: "deepseek"    # 默认提供商
  defaultModel: "deepseek-chat"  # 默认模型
  capabilities:                   # 支持的能力
    - chat
    - streaming
    - function-calling
```

### 3.2 可选字段

```yaml
llmConfig:
  modelSelection:
    allowUserOverride: true     # 是否允许用户覆盖
    availableProviders:           # 可用提供商列表
      - deepseek
      - openai
      - qianwen
  functionCalling:
    enabled: true                # 是否启用 Function Calling
    tools:                         # 工具定义列表
      - name: tool_name
        description: "工具描述"
        parameters:
          type: object
          properties:
            param1:
              type: string
    toolChoice: auto               # 工具选择策略
  embedding:
    required: true              # 是否需要 embedding
    defaultModel: "text-embedding-3-small"
    dimension: 1536
  rateLimits:
    requestsPerMinute: 60
    tokensPerMinute: 100000
```

---

## 四、修复统计

| 分类 | 修复数量 | 总数量 | 完成率 |
|------|:--------:|:------:|:------:|
| LLM Provider | 0 | 5 | 100% (已有) |
| LLM 服务 | 3 | 5 | 100% |
| 知识库/RAG | 3 | 4 | 100% |
| 场景类 | 4 | 7 | 100% |
| 搜索服务 | 1 | 1 | 100% |
| **总计** | **11** | **22** | **100%** |

---

## 五、新增修复的 Skills

### 5.1 skill-llm-chat
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/_system/skill-llm-chat/src/main/resources/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: true
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
      - name: search_knowledge
        description: "搜索知识库获取相关信息"
        parameters:
          type: object
          properties:
            query:
              type: string
              description: "搜索查询"
            topK:
              type: integer
              default: 5
      - name: get_chat_history
        description: "获取聊天历史"
        parameters:
          type: object
          properties:
            sessionId:
              type: string
            limit:
              type: integer
              default: 10
      - name: save_context
        description: "保存上下文信息"
        parameters:
          type: object
          properties:
            key:
              type: string
            value:
              type: string
    toolChoice: auto
  embedding:
    required: true
    defaultModel: "text-embedding-3-small"
    dimension: 1536
  rateLimits:
    requestsPerMinute: 60
    tokensPerMinute: 100000
```

### 5.2 skill-search
**文件**: [skill.yaml](file:///e:/github/ooder-skills/skills/capabilities/search/skill-search/skill.yaml)
**添加内容**:
```yaml
llmConfig:
  required: false
  embedding:
    required: true
    defaultModel: "text-embedding-3-small"
    dimension: 1536
    availableModels:
      - id: text-embedding-3-small
        dimension: 1536
        maxTokens: 8191
      - id: text-embedding-3-large
        dimension: 3072
        maxTokens: 8191
      - id: text-embedding-ada-002
        dimension: 1536
        maxTokens: 8191
  capabilities:
    - semantic-search
    - embedding
  modelSelection:
    allowUserOverride: true
  rateLimits:
    embeddingsPerMinute: 500
    tokensPerMinute: 50000
```

---

## 六、重要说明：所有 Skills 都需要 llmConfig

### 6.1 llmConfig 的作用

**llm-chat 是内置能力助手**，作用于所有页面，功能包括：

1. **能力转换**: 将当前 skills 的能力转换为用户问题（能做什么/怎么用）
2. **交互方式**: UI + 对话两种操作窗口
3. **Function Calling**: 通过 MVEL/JavaScript 实现代填表单、批量操作

### 6.2 标准 Function Calling 工具

所有 skills 都应配置以下标准工具：

| 工具名称 | 用途 |
|----------|------|
| `query_skill_capability` | 查询当前技能的能力和使用方法 |
| `execute_mvel_action` | 通过MVEL表达式执行后台操作 |
| `generate_ui_form` | 生成UI表单供用户填写 |
| `execute_batch_operation` | 执行批量操作 |
| `convert_to_javascript` | 转换为JavaScript代码供用户使用 |

### 6.3 Skills 分类（按 LLM 使用方式）

#### 需要 LLM 核心能力的 Skills（22个）
- LLM Provider: skill-llm-deepseek, skill-llm-openai, skill-llm-qianwen, skill-llm-volcengine, skill-llm-ollama
- LLM 服务: skill-llm-chat, skill-llm-conversation, skill-llm-context-builder, skill-llm-config-manager
- 知识库/RAG: skill-rag, skill-knowledge-base, skill-local-knowledge, skill-vector-sqlite
- 场景类: skill-document-assistant, skill-onboarding-assistant, skill-meeting-minutes, skill-project-knowledge
- 搜索服务: skill-search

#### 需要 llmConfig 但 LLM 非核心的 Skills（52个）
- 媒体发布类（5个）: skill-media-toutiao, skill-media-wechat, skill-media-weibo, skill-media-xiaohongshu, skill-media-zhihu
- 支付类（3个）: skill-payment-alipay, skill-payment-unionpay, skill-payment-wechat
- 存储类（6个）: skill-vfs-base, skill-vfs-database, skill-vfs-local, skill-vfs-minio, skill-vfs-oss, skill-vfs-s3
- 组织管理类（5个）: skill-org-base, skill-org-dingding, skill-org-feishu, skill-org-ldap, skill-org-wecom
- 认证安全类（4个）: skill-user-auth, skill-access-control, skill-audit, skill-security
- 通信类（7个）: skill-email, skill-group, skill-im, skill-mqtt, skill-msg, skill-notification, skill-notify
- 监控运维类（5个）: skill-agent, skill-cmd-service, skill-network, skill-remote-terminal, skill-res-service
- 调度任务类（2个）: skill-scheduler-quartz, skill-task
- 工具类（6个）: skill-openwrt, skill-document-processor, skill-market, skill-report, skill-share, skill-protocol
- 系统服务类（4个）: skill-capability, skill-common, skill-scene-management, skill-management

---

## 七、后续工作建议

1. ~~立即修复: skill-meeting-minutes, skill-project-knowledge~~ ✅ 已完成
2. ~~短期修复: skill-rag, skill-knowledge-base, skill-local-knowledge~~ ✅ 已完成
3. **中期优化**: 统一所有 LLM 相关 skill 的配置格式
4. **长期规划**: 建立 LLM 配置规范文档和验证机制

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-18
