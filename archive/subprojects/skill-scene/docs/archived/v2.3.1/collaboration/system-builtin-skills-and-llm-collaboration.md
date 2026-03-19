# 系统内置技能与LLM协作规范

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 规范定义 |

---

## 一、LLM安装激活程序作为默认场景技能

### 1.1 设计理念

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    LLM安装激活程序设计理念                                       │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  核心思想：                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  LLM安装激活程序本身也是一个场景技能，具有以下特点：                       │    │
│  │                                                                         │    │
│  │  1. 默认内置：系统启动时自动加载                                         │    │
│  │  2. 特殊权限：拥有系统级安装权限                                         │    │
│  │  3. 上下文隔离：安装过程中的LLM对话上下文独立管理                         │    │
│  │  4. 递归支持：可以安装其他场景技能，包括自身升级                          │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  技能定义：                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  skillId: skill-llm-installer                                           │    │
│  │  type: system-scene-skill                                               │    │
│  │  mainFirst: true                                                        │    │
│  │  visibility: SYSTEM                                                     │    │
│  │  autoStart: true                                                        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 技能定义

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-llm-installer
  name: LLM安装激活程序
  version: 2.3.0
  description: 系统内置的LLM主导安装激活场景技能
  author: ooder Team
  type: system-scene-skill
  category: abs
  builtIn: true
  autoStart: true

spec:
  type: system-scene-skill
  
  # 系统级权限
  permissions:
    - SYSTEM_INSTALL
    - SYSTEM_CONFIG
    - CAPABILITY_REGISTER
    - DATABASE_INIT
    - FILE_WRITE
    
  # 角色定义
  roles:
    - name: SYSTEM
      displayName: "系统"
      description: "系统自动执行"
      permissions: [ALL]
      autoAssign: true
      
    - name: ADMIN
      displayName: "管理员"
      description: "系统管理员"
      permissions: [READ, WRITE, CONFIG]
      
    - name: USER
      displayName: "用户"
      description: "普通用户"
      permissions: [READ]
      
  # 能力定义
  capabilities:
    - id: llm-install-analyze
      name: LLM安装分析
      description: 使用LLM分析安装需求
      type: ATOMIC
      llmRequired: true
      
    - id: llm-install-guide
      name: LLM安装引导
      description: 使用LLM引导安装流程
      type: ATOMIC
      llmRequired: true
      
    - id: dependency-check
      name: 依赖检查
      description: 检查系统依赖
      type: ATOMIC
      llmRequired: false
      
    - id: config-generate
      name: 配置生成
      description: 生成安装配置
      type: ATOMIC
      llmRequired: false
      
    - id: install-execute
      name: 安装执行
      description: 执行安装操作
      type: ATOMIC
      llmRequired: false
      
    - id: activation-guide
      name: 激活引导
      description: 引导激活流程
      type: ATOMIC
      llmRequired: true
      
  # LLM上下文配置
  llmContext:
    enabled: true
    isolated: true
    contextType: "installation"
    maxTurns: 50
    retentionPolicy: "session-based"
    
    # 上下文模板
    templates:
      - id: "install-welcome"
        trigger: "on-install-start"
        content: |
          你是Ooder平台的安装助手。
          
          当前任务：帮助用户安装场景技能
          安装目标：{targetSkill}
          
          请按照以下流程引导用户：
          1. 分析用户需求
          2. 推荐配置参数
          3. 执行安装操作
          4. 引导激活流程
          
      - id: "install-progress"
        trigger: "on-install-progress"
        content: |
          安装进度：{progress}%
          当前步骤：{currentStep}
          已完成：{completedSteps}
          待完成：{pendingSteps}
          
          请继续引导用户完成安装。
          
  # 依赖声明
  dependencies:
    required:
      - id: database
        type: infrastructure
        autoConfig: true
        
      - id: llm-service
        type: ai-service
        autoConfig: true
        fallback: "rule-based-install"
        
    optional:
      - id: vector-db
        type: infrastructure
        autoConfig: true
        fallback: "disable-rag"
        
      - id: knowledge-base
        type: infrastructure
        autoConfig: true
        fallback: "disable-knowledge"
        
  # 菜单配置
  menus:
    ADMIN:
      - id: install-center
        name: 安装中心
        icon: ri-install-line
        url: /console/pages/install-center.html
        order: 1
      - id: install-history
        name: 安装历史
        icon: ri-history-line
        url: /console/pages/install-history.html
        order: 2
        
  # 自驱配置
  mainFirstConfig:
    selfCheck:
      - id: check-llm
        capability: llm-service
        action: ping
        timeout: 10s
        fallback: disable-llm-features
        
      - id: check-database
        capability: database
        action: test-connection
        timeout: 5s
        
    selfStart:
      - id: init-context
        action: create-install-context
        params:
          contextType: installation
          
      - id: load-tools
        action: load-install-tools
        params:
          toolSet: default-install-tools
          
    selfDrive:
      scheduleRules:
        - id: cleanup-expired-contexts
          cron: "0 0 3 * * ?"
          action: cleanup-install-contexts
          params:
            maxAge: 7d
            
      eventRules:
        - id: on-install-request
          trigger: "INSTALL_REQUEST"
          action: start-install-flow
          params:
            guidedByLLM: true
```

### 1.3 安装流程中的LLM上下文设计

```yaml
LLMContextDesign:
  # 上下文生命周期
  lifecycle:
    create: "on-install-start"
    update: "on-install-progress"
    persist: "on-install-complete"
    destroy: "on-install-rollback"
    
  # 上下文结构
  structure:
    # 基础信息
    basic:
      - name: sessionId
        type: string
        description: "安装会话ID"
        
      - name: targetSkill
        type: object
        description: "目标技能信息"
        
      - name: currentUser
        type: object
        description: "当前用户信息"
        
    # 安装状态
    state:
      - name: currentPhase
        type: string
        enum: [ANALYZE, CONFIG, INSTALL, ACTIVATE, COMPLETE]
        
      - name: currentStep
        type: string
        description: "当前步骤ID"
        
      - name: completedSteps
        type: array
        description: "已完成步骤列表"
        
      - name: pendingSteps
        type: array
        description: "待完成步骤列表"
        
    # LLM对话历史
    conversation:
      - name: messages
        type: array
        description: "对话消息列表"
        items:
          - name: role
            type: string
            enum: [system, user, assistant]
          - name: content
            type: string
          - name: timestamp
            type: datetime
            
    # 工具调用记录
    toolCalls:
      - name: history
        type: array
        description: "工具调用历史"
        items:
          - name: toolId
            type: string
          - name: input
            type: object
          - name: output
            type: object
          - name: success
            type: boolean
            
  # 上下文模板
  templates:
    # 安装开始
    installStart:
      systemPrompt: |
        你是Ooder平台的安装助手。
        
        ## 当前任务
        帮助用户安装场景技能：{targetSkill.name}
        
        ## 可用工具
        {availableTools}
        
        ## 知识库上下文
        {knowledgeContext}
        
        ## 约束
        1. 只使用提供的工具
        2. 输出必须是JSON格式
        3. 每个步骤完成后等待用户确认
        
      contextInjection:
        - type: knowledge
          query: "安装流程 {targetSkill.id}"
          topK: 3
        - type: vector
          query: "相似安装案例"
          topK: 5
          
    # 安装进度
    installProgress:
      systemPrompt: |
        安装进度更新：
        
        ## 当前状态
        - 阶段：{currentPhase}
        - 步骤：{currentStep}
        - 进度：{progress}%
        
        ## 已完成
        {completedSteps}
        
        ## 待完成
        {pendingSteps}
        
        请继续引导用户完成下一步。
        
    # 安装完成
    installComplete:
      systemPrompt: |
        安装已完成！
        
        ## 安装摘要
        - 技能：{targetSkill.name}
        - 耗时：{duration}
        - 配置：{finalConfig}
        
        请向用户确认安装结果，并提供使用建议。
```

---

## 二、LLM-TOOLS SKILLS.MD 说明书

### 2.1 文档结构

```markdown
# LLM-TOOLS SKILLS 说明书

## 概述

本文档描述了Ooder平台提供的LLM工具技能，用于与LLM协作完成各种任务。

## 工具分类

### 1. 系统工具
- check-dependency: 检查系统依赖
- get-system-info: 获取系统信息
- execute-command: 执行系统命令

### 2. 数据工具
- database-query: 数据库查询
- database-update: 数据库更新
- cache-get: 缓存读取
- cache-set: 缓存写入

### 3. 网络工具
- http-get: HTTP GET请求
- http-post: HTTP POST请求
- websocket-connect: WebSocket连接

### 4. 消息工具
- mqtt-publish: MQTT发布
- mqtt-subscribe: MQTT订阅
- send-notification: 发送通知

### 5. 存储工具
- file-read: 文件读取
- file-write: 文件写入
- file-delete: 文件删除

### 6. LLM工具
- llm-chat: LLM对话
- llm-structured-output: 结构化输出
- llm-embedding: 文本向量化

### 7. 向量库工具
- vector-search: 向量检索
- vector-insert: 向量插入
- vector-delete: 向量删除

### 8. 知识库工具
- knowledge-query: 知识查询
- knowledge-insert: 知识插入
- knowledge-delete: 知识删除

## 工具调用规范

### 请求格式
```json
{
  "toolId": "tool-name",
  "parameters": {
    "param1": "value1",
    "param2": "value2"
  }
}
```

### 响应格式
```json
{
  "success": true,
  "result": {
    "data": "..."
  },
  "error": null
}
```

## 使用示例

### 示例1：检查依赖
```json
{
  "toolId": "check-dependency",
  "parameters": {
    "dependencyId": "database"
  }
}
```

响应：
```json
{
  "success": true,
  "result": {
    "status": "available",
    "message": "数据库连接正常"
  }
}
```

### 示例2：向量检索
```json
{
  "toolId": "vector-search",
  "parameters": {
    "query": "如何配置日志提醒",
    "collection": "scene",
    "topK": 3
  }
}
```

响应：
```json
{
  "success": true,
  "result": {
    "results": [
      {
        "content": "日志提醒配置...",
        "score": 0.95,
        "metadata": {...}
      }
    ]
  }
}
```
```

### 2.2 完整说明书

```yaml
LLMToolsSpecification:
  # 文档元信息
  meta:
    version: "2.3.0"
    lastUpdate: "2026-03-09"
    author: "ooder Team"
    
  # 工具分类
  categories:
    - id: system
      name: "系统工具"
      description: "系统级操作工具"
      tools:
        - id: check-dependency
          name: "依赖检查"
          description: "检查系统依赖是否满足"
          version: "1.0.0"
          
          parameters:
            - name: dependencyId
              type: string
              required: true
              enum: [database, mqtt, redis, llm, vector-db, knowledge-base]
              description: "依赖ID"
              
          returns:
            - name: status
              type: string
              enum: [available, unavailable, degraded]
              description: "依赖状态"
            - name: message
              type: string
              description: "状态消息"
            - name: details
              type: object
              description: "详细信息"
              
          examples:
            - input:
                dependencyId: "database"
              output:
                status: "available"
                message: "数据库连接正常"
                details:
                  type: "mysql"
                  version: "8.0"
                  latency: "5ms"
                  
          errors:
            - code: "DEPENDENCY_NOT_FOUND"
              message: "依赖不存在"
            - code: "CONNECTION_FAILED"
              message: "连接失败"
              
        - id: get-system-info
          name: "获取系统信息"
          description: "获取系统运行环境信息"
          version: "1.0.0"
          
          parameters: []
          
          returns:
            - name: os
              type: string
              description: "操作系统"
            - name: memory
              type: object
              description: "内存信息"
            - name: disk
              type: object
              description: "磁盘信息"
            - name: network
              type: object
              description: "网络信息"
              
    - id: data
      name: "数据工具"
      description: "数据操作工具"
      tools:
        - id: database-query
          name: "数据库查询"
          description: "执行数据库查询"
          version: "1.0.0"
          
          parameters:
            - name: query
              type: string
              required: true
              description: "SQL查询语句"
            - name: params
              type: array
              required: false
              description: "查询参数"
            - name: limit
              type: integer
              required: false
              default: 100
              description: "返回结果限制"
              
          returns:
            - name: rows
              type: array
              description: "查询结果"
            - name: total
              type: integer
              description: "总记录数"
            - name: executionTime
              type: integer
              description: "执行时间(ms)"
              
          security:
            - type: "sql-injection-check"
              enabled: true
            - type: "query-whitelist"
              enabled: true
              allowedOperations: [SELECT]
              
    - id: llm
      name: "LLM工具"
      description: "LLM交互工具"
      tools:
        - id: llm-structured-output
          name: "结构化输出"
          description: "获取LLM的结构化输出"
          version: "1.0.0"
          
          parameters:
            - name: prompt
              type: string
              required: true
              description: "提示词"
            - name: schema
              type: object
              required: true
              description: "输出Schema"
            - name: model
              type: string
              required: false
              default: "gpt-4"
              description: "LLM模型"
            - name: temperature
              type: number
              required: false
              default: 0.7
              description: "温度参数"
              
          returns:
            - name: data
              type: object
              description: "结构化输出数据"
            - name: confidence
              type: number
              description: "置信度"
            - name: tokens
              type: object
              description: "Token使用情况"
              
          examples:
            - input:
                prompt: "推荐日志汇报场景的配置"
                schema:
                  type: object
                  properties:
                    remindTime:
                      type: string
                    aiGenerate:
                      type: boolean
              output:
                data:
                  remindTime: "16:30"
                  aiGenerate: true
                confidence: 0.95
                tokens:
                  input: 50
                  output: 30
                  
    - id: vector
      name: "向量库工具"
      description: "向量检索工具"
      tools:
        - id: vector-search
          name: "向量检索"
          description: "在向量库中检索相似内容"
          version: "1.0.0"
          
          parameters:
            - name: query
              type: string
              required: true
              description: "查询文本"
            - name: collection
              type: string
              required: true
              enum: [platform, scene, troubleshooting]
              description: "向量集合"
            - name: topK
              type: integer
              required: false
              default: 3
              description: "返回结果数量"
            - name: threshold
              type: number
              required: false
              default: 0.7
              description: "相似度阈值"
              
          returns:
            - name: results
              type: array
              description: "检索结果"
              items:
                - name: id
                  type: string
                - name: content
                  type: string
                - name: score
                  type: number
                - name: metadata
                  type: object
                  
    - id: knowledge
      name: "知识库工具"
      description: "知识查询工具"
      tools:
        - id: knowledge-query
          name: "知识查询"
          description: "在知识库中查询相关内容"
          version: "1.0.0"
          
          parameters:
            - name: query
              type: string
              required: true
              description: "查询文本"
            - name: knowledgeBase
              type: string
              required: true
              enum: [platform-docs, scene-docs, api-docs, troubleshooting]
              description: "知识库ID"
            - name: topK
              type: integer
              required: false
              default: 5
              description: "返回结果数量"
              
          returns:
            - name: results
              type: array
              description: "查询结果"
              items:
                - name: id
                  type: string
                - name: title
                  type: string
                - name: content
                  type: string
                - name: score
                  type: number
                - name: metadata
                  type: object
```

---

## 三、系统内置安装流程

### 3.1 内置技能列表

```yaml
BuiltInSkills:
  # 核心系统技能
  core:
    - id: skill-llm-installer
      name: "LLM安装激活程序"
      type: "system-scene-skill"
      priority: 1
      autoStart: true
      description: "系统内置的LLM主导安装激活场景技能"
      
    - id: skill-llm-chat
      name: "LLM聊天程序"
      type: "service-skill"
      priority: 2
      autoStart: true
      description: "系统内置的LLM聊天服务"
      
    - id: skill-scene
      name: "场景管理"
      type: "service-skill"
      priority: 3
      autoStart: true
      description: "场景技能管理服务"
      
    - id: skill-capability
      name: "能力管理"
      type: "service-skill"
      priority: 4
      autoStart: true
      description: "能力管理服务"
      
    - id: skill-management
      name: "技能管理"
      type: "service-skill"
      priority: 5
      autoStart: true
      description: "技能管理服务"
      
  # 基础设施技能
  infrastructure:
    - id: skill-user-auth
      name: "用户认证"
      type: "service-skill"
      priority: 10
      autoStart: true
      
    - id: skill-vfs-local
      name: "本地存储"
      type: "service-skill"
      priority: 11
      autoStart: true
      
    - id: skill-health
      name: "健康检查"
      type: "system-service"
      priority: 12
      autoStart: true
      
    - id: skill-monitor
      name: "监控服务"
      type: "system-service"
      priority: 13
      autoStart: true
      
  # 可选技能
  optional:
    - id: skill-knowledge-base
      name: "知识库服务"
      type: "service-skill"
      priority: 20
      autoStart: false
      
    - id: skill-rag
      name: "RAG服务"
      type: "service-skill"
      priority: 21
      autoStart: false
      
    - id: skill-mqtt
      name: "MQTT服务"
      type: "service-skill"
      priority: 22
      autoStart: false
```

### 3.2 系统启动安装流程

```yaml
SystemStartupFlow:
  # 阶段1：核心系统初始化
  phase1:
    name: "核心系统初始化"
    parallel: false
    steps:
      - id: init-database
        skill: skill-vfs-local
        action: init-database
        timeout: 60s
        
      - id: init-auth
        skill: skill-user-auth
        action: init-auth
        timeout: 30s
        
      - id: init-scene
        skill: skill-scene
        action: init-scene
        timeout: 30s
        
  # 阶段2：核心服务启动
  phase2:
    name: "核心服务启动"
    parallel: true
    steps:
      - id: start-llm-installer
        skill: skill-llm-installer
        action: start
        timeout: 30s
        
      - id: start-llm-chat
        skill: skill-llm-chat
        action: start
        timeout: 30s
        
      - id: start-capability
        skill: skill-capability
        action: start
        timeout: 30s
        
      - id: start-management
        skill: skill-management
        action: start
        timeout: 30s
        
  # 阶段3：健康检查
  phase3:
    name: "健康检查"
    parallel: true
    steps:
      - id: health-check
        skill: skill-health
        action: check-all
        timeout: 30s
        
      - id: monitor-start
        skill: skill-monitor
        action: start-monitoring
        timeout: 30s
        
  # 阶段4：可选服务初始化
  phase4:
    name: "可选服务初始化"
    parallel: true
    optional: true
    steps:
      - id: init-knowledge-base
        skill: skill-knowledge-base
        action: init
        timeout: 60s
        condition: "config.knowledge-base.enabled == true"
        
      - id: init-rag
        skill: skill-rag
        action: init
        timeout: 60s
        condition: "config.rag.enabled == true"
        
      - id: init-mqtt
        skill: skill-mqtt
        action: init
        timeout: 30s
        condition: "config.mqtt.enabled == true"
```

---

## 四、向量库与知识库维护接口

### 4.1 向量库维护接口

```yaml
VectorDBMaintenanceAPI:
  # 数据管理
  dataManagement:
    # 插入向量
    - id: vector-insert
      endpoint: /api/vector/insert
      method: POST
      description: "插入向量数据"
      parameters:
        - name: collection
          type: string
          required: true
          description: "向量集合"
        - name: id
          type: string
          required: true
          description: "向量ID"
        - name: content
          type: string
          required: true
          description: "文本内容"
        - name: metadata
          type: object
          required: false
          description: "元数据"
      returns:
        - name: success
          type: boolean
        - name: id
          type: string
          
    # 批量插入
    - id: vector-batch-insert
      endpoint: /api/vector/batch-insert
      method: POST
      description: "批量插入向量数据"
      parameters:
        - name: collection
          type: string
          required: true
        - name: items
          type: array
          required: true
          description: "向量数据列表"
          
    # 删除向量
    - id: vector-delete
      endpoint: /api/vector/delete
      method: DELETE
      description: "删除向量数据"
      parameters:
        - name: collection
          type: string
          required: true
        - name: id
          type: string
          required: true
          
    # 更新向量
    - id: vector-update
      endpoint: /api/vector/update
      method: PUT
      description: "更新向量数据"
      parameters:
        - name: collection
          type: string
          required: true
        - name: id
          type: string
          required: true
        - name: content
          type: string
          required: false
        - name: metadata
          type: object
          required: false
          
  # 集合管理
  collectionManagement:
    # 创建集合
    - id: collection-create
      endpoint: /api/vector/collection
      method: POST
      description: "创建向量集合"
      parameters:
        - name: name
          type: string
          required: true
        - name: dimension
          type: integer
          required: true
          default: 1536
        - name: metric
          type: string
          required: false
          default: "cosine"
          enum: [cosine, euclidean, dot]
          
    # 删除集合
    - id: collection-delete
      endpoint: /api/vector/collection/{name}
      method: DELETE
      description: "删除向量集合"
      
    # 列出集合
    - id: collection-list
      endpoint: /api/vector/collections
      method: GET
      description: "列出所有向量集合"
      
  # 维护操作
  maintenance:
    # 重建索引
    - id: rebuild-index
      endpoint: /api/vector/rebuild-index
      method: POST
      description: "重建向量索引"
      parameters:
        - name: collection
          type: string
          required: true
          
    # 统计信息
    - id: statistics
      endpoint: /api/vector/statistics
      method: GET
      description: "获取向量库统计信息"
      returns:
        - name: totalVectors
          type: integer
        - name: collections
          type: array
        - name: storageSize
          type: integer
```

### 4.2 知识库维护接口

```yaml
KnowledgeBaseMaintenanceAPI:
  # 文档管理
  documentManagement:
    # 上传文档
    - id: document-upload
      endpoint: /api/knowledge/document/upload
      method: POST
      description: "上传知识文档"
      parameters:
        - name: knowledgeBase
          type: string
          required: true
        - name: file
          type: file
          required: true
        - name: metadata
          type: object
          required: false
      returns:
        - name: success
          type: boolean
        - name: documentId
          type: string
        - name: chunks
          type: integer
          description: "切分后的文档块数量"
          
    # 删除文档
    - id: document-delete
      endpoint: /api/knowledge/document/{documentId}
      method: DELETE
      description: "删除知识文档"
      
    # 更新文档
    - id: document-update
      endpoint: /api/knowledge/document/{documentId}
      method: PUT
      description: "更新知识文档"
      
    # 列出文档
    - id: document-list
      endpoint: /api/knowledge/documents
      method: GET
      description: "列出知识文档"
      parameters:
        - name: knowledgeBase
          type: string
          required: true
        - name: page
          type: integer
          required: false
          default: 1
        - name: pageSize
          type: integer
          required: false
          default: 20
          
  # 知识库管理
  knowledgeBaseManagement:
    # 创建知识库
    - id: kb-create
      endpoint: /api/knowledge/base
      method: POST
      description: "创建知识库"
      parameters:
        - name: id
          type: string
          required: true
        - name: name
          type: string
          required: true
        - name: description
          type: string
          required: false
        - name: embeddingModel
          type: string
          required: false
          default: "text-embedding-ada-002"
        - name: chunkSize
          type: integer
          required: false
          default: 500
        - name: chunkOverlap
          type: integer
          required: false
          default: 50
          
    # 删除知识库
    - id: kb-delete
      endpoint: /api/knowledge/base/{id}
      method: DELETE
      description: "删除知识库"
      
    # 列出知识库
    - id: kb-list
      endpoint: /api/knowledge/bases
      method: GET
      description: "列出所有知识库"
      
  # 维护操作
  maintenance:
    # 重建索引
    - id: rebuild-index
      endpoint: /api/knowledge/rebuild-index
      method: POST
      description: "重建知识库索引"
      parameters:
        - name: knowledgeBase
          type: string
          required: true
          
    # 同步向量库
    - id: sync-vector
      endpoint: /api/knowledge/sync-vector
      method: POST
      description: "同步知识库到向量库"
      parameters:
        - name: knowledgeBase
          type: string
          required: true
          
    # 统计信息
    - id: statistics
      endpoint: /api/knowledge/statistics
      method: GET
      description: "获取知识库统计信息"
      returns:
        - name: totalDocuments
          type: integer
        - name: totalChunks
          type: integer
        - name: knowledgeBases
          type: array
        - name: storageSize
          type: integer
```

### 4.3 维护说明文档

```markdown
# 向量库与知识库维护指南

## 概述

本文档描述了向量库和知识库的维护操作和最佳实践。

## 向量库维护

### 1. 数据管理

#### 插入向量
```bash
curl -X POST http://localhost:8080/api/vector/insert \
  -H "Content-Type: application/json" \
  -d '{
    "collection": "scene",
    "id": "daily-report-config",
    "content": "日志汇报场景配置...",
    "metadata": {
      "sceneId": "skill-daily-report",
      "type": "config"
    }
  }'
```

#### 批量插入
```bash
curl -X POST http://localhost:8080/api/vector/batch-insert \
  -H "Content-Type: application/json" \
  -d '{
    "collection": "scene",
    "items": [
      {
        "id": "item-1",
        "content": "内容1",
        "metadata": {}
      },
      {
        "id": "item-2",
        "content": "内容2",
        "metadata": {}
      }
    ]
  }'
```

### 2. 索引维护

#### 重建索引
```bash
curl -X POST http://localhost:8080/api/vector/rebuild-index \
  -H "Content-Type: application/json" \
  -d '{
    "collection": "scene"
  }'
```

### 3. 监控

#### 统计信息
```bash
curl http://localhost:8080/api/vector/statistics
```

## 知识库维护

### 1. 文档管理

#### 上传文档
```bash
curl -X POST http://localhost:8080/api/knowledge/document/upload \
  -F "knowledgeBase=platform-docs" \
  -F "file=@document.md"
```

#### 列出文档
```bash
curl "http://localhost:8080/api/knowledge/documents?knowledgeBase=platform-docs&page=1&pageSize=20"
```

### 2. 知识库管理

#### 创建知识库
```bash
curl -X POST http://localhost:8080/api/knowledge/base \
  -H "Content-Type: application/json" \
  -d '{
    "id": "scene-docs",
    "name": "场景文档",
    "description": "场景技能使用文档",
    "embeddingModel": "text-embedding-ada-002",
    "chunkSize": 500,
    "chunkOverlap": 50
  }'
```

### 3. 同步操作

#### 同步到向量库
```bash
curl -X POST http://localhost:8080/api/knowledge/sync-vector \
  -H "Content-Type: application/json" \
  -d '{
    "knowledgeBase": "platform-docs"
  }'
```

## 最佳实践

### 1. 定期维护

- 每周重建索引
- 每月检查数据一致性
- 每季度清理过期数据

### 2. 性能优化

- 合理设置chunk大小
- 使用合适的embedding模型
- 定期监控查询性能

### 3. 数据备份

- 定期导出向量数据
- 备份知识库文档
- 保存元数据信息
```

---

## 五、总结

### 5.1 核心设计要点

| 要点 | 说明 |
|------|------|
| **LLM安装激活程序** | 作为默认场景技能，具有系统级权限 |
| **LLM-TOOLS说明书** | 提供完整的工具调用规范 |
| **系统内置安装** | 核心技能自动安装，可选技能按需安装 |
| **LLM上下文设计** | 安装过程上下文隔离，独立管理 |
| **维护透明接口** | 向量库和知识库维护接口公开透明 |

### 5.2 实施优先级

| 优先级 | 任务 | 工作量 |
|--------|------|--------|
| P0 | 实现LLM安装激活程序 | 3周 |
| P0 | 创建LLM-TOOLS说明书 | 1周 |
| P1 | 实现系统内置安装流程 | 2周 |
| P1 | 设计LLM上下文管理 | 2周 |
| P2 | 实现维护接口 | 2周 |

---

**文档状态**: 规范定义  
**下一步**: 根据规范实施相关功能
