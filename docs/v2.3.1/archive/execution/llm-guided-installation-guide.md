# LLM主导安装说明书

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 适用场景 | LLM可用的场景技能安装 |

---

## 一、安装流程总览

### 1.1 安装模式对比

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    场景技能安装模式对比                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  LLM主导模式                                                                    │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  特点：                                                                │    │
│  │  • 智能推荐配置                                                          │    │
│  │  • 自动化安装流程                                                        │    │
│  │  • 个性化安装体验                                                        │    │
│  │  • 减少用户手动操作                                                      │    │
│  │                                                                         │    │
│  │  适用场景：                                                              │    │
│  │  • LLM服务可用                                                          │    │
│  │  • 用户希望简化安装流程                                                    │    │
│  │  • 需要智能配置推荐                                                      │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  传统模式（无LLM）                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  特点：                                                                │    │
│  │  • 基于规则的安装                                                        │    │
│  │  • 手动配置参数                                                          │    │
│  │  • 标准化安装流程                                                        │    │
│  │  • 用户完全控制安装过程                                                  │    │
│  │                                                                         │    │
│  │  适用场景：                                                              │    │
│  │  • LLM服务不可用                                                        │    │
│  │  • 用户需要完全控制安装过程                                                │    │
│  │  • 需要标准化安装流程                                                    │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 安装流程对比

| 阶段 | LLM主导模式 | 传统模式 |
|------|------------|---------|
| **前置检查** | LLM智能分析依赖 | 规则检查依赖 |
| **依赖分析** | LLM推荐最优配置 | 读取默认配置 |
| **配置生成** | LLM生成个性化配置 | 用户手动填写 |
| **安装执行** | LLM监控安装进度 | 标准安装流程 |
| **激活配置** | LLM引导激活步骤 | 用户手动配置 |
| **问题处理** | LLM智能诊断 | 错误提示 |

---

## 二、LLM主导安装流程

### 2.1 阶段1：前置检查

#### 步骤1.1：LLM可用性检测

```yaml
Step_1_1:
  name: "LLM可用性检测"
  type: "auto"
  timeout: 10s
  
  llmAction:
    enabled: true
    fallback: "rule-based"
    
  actions:
    - id: "ping-llm"
      description: "发送测试请求到LLM服务"
      method: "POST"
      url: "/api/llm/ping"
      payload:
        message: "Hello, are you available?"
      expectedResponse: "Yes"
      
    - id: "check-models"
      description: "获取可用的LLM模型列表"
      method: "GET"
      url: "/api/llm/models"
      timeout: 5s
      
  outputs:
    - id: "llm-available"
      type: "boolean"
      description: "LLM服务是否可用"
    - id: "available-models"
      type: "array"
      description: "可用的LLM模型列表"
      
  onFail:
    action: "fallback-to-traditional"
    message: "LLM服务不可用，切换到传统安装模式"
    log: "WARN: LLM service unavailable, falling back to traditional installation"
```

#### 步骤1.2：依赖检查

```yaml
Step_1_2:
  name: "依赖检查"
  type: "auto"
  timeout: 30s
  
  llmAction:
    enabled: true
    fallback: "rule-based"
    
  actions:
    - id: "check-database"
      description: "检查数据库连接"
      method: "POST"
      url: "/api/database/test-connection"
      payload:
        timeout: 5s
      expectedStatus: 200
      
    - id: "check-mqtt"
      description: "检查MQTT连接"
      method: "POST"
      url: "/api/mqtt/test-connection"
      payload:
        timeout: 3s
      expectedStatus: 200
      
    - id: "check-redis"
      description: "检查Redis连接"
      method: "POST"
      url: "/api/redis/test-connection"
      payload:
        timeout: 3s
      expectedStatus: 200
      
  outputs:
    - id: "dependencies-status"
      type: "object"
      description: "依赖服务状态"
      fields:
        - name: "database"
          type: "boolean"
        - name: "mqtt"
          type: "boolean"
        - name: "redis"
          type: "boolean"
          
  onFail:
    action: "prompt-user"
    message: "部分依赖服务不可用，是否继续安装？"
    options:
      - label: "继续安装"
        value: "continue"
      - label: "取消安装"
        value: "abort"
```

### 2.2 阶段2：依赖分析

#### 步骤2.1：LLM智能分析

```yaml
Step_2_1:
  name: "LLM智能分析"
  type: "llm-assisted"
  timeout: 30s
  
  llmAction:
    enabled: true
    fallback: "rule-based"
    
  prompt:
    template: "analyze-scene-requirements"
    systemPrompt: |
      你是一个专业的场景技能安装助手。
      请分析用户的需求和场景技能的配置，推荐最优的安装配置。
      
    userPrompt: |
      场景技能信息：
      - 技能ID：{skillId}
      - 技能名称：{skillName}
      - 技能描述：{skillDescription}
      
      用户需求：
      - 用户角色：{userRole}
      - 团队规模：{teamSize}
      - 功能需求：{featureRequirements}
      - 技术环境：{techEnvironment}
      
      请分析并推荐：
      1. 必需的依赖配置
      2. 推荐的LLM模型
      3. 推荐的配置参数
      4. 潜在的问题和解决方案
      
  outputs:
    - id: "analysis-result"
      type: "object"
      description: "LLM分析结果"
      fields:
        - name: "required-dependencies"
          type: "array"
        - name: "recommended-model"
          type: "string"
        - name: "recommended-config"
          type: "object"
        - name: "potential-issues"
          type: "array"
        - name: "solutions"
          type: "array"
          
  onFail:
    action: "use-default-config"
    message: "LLM分析失败，使用默认配置"
    log: "WARN: LLM analysis failed, using default configuration"
```

#### 步骤2.2：配置推荐

```yaml
Step_2_2:
  name: "配置推荐"
  type: "llm-assisted"
  timeout: 20s
  
  llmAction:
    enabled: true
    fallback: "rule-based"
    
  prompt:
    template: "recommend-config"
    systemPrompt: |
      你是一个专业的配置推荐助手。
      请根据用户的具体情况，推荐最优的配置参数。
      
    userPrompt: |
      用户情况：
      - 用户角色：{userRole}
      - 团队规模：{teamSize}
      - 使用频率：{usageFrequency}
      - 性能要求：{performanceRequirement}
      
      场景技能配置项：
      {configItems}
      
      请为每个配置项推荐最优值，并说明推荐理由。
      
  outputs:
    - id: "recommended-config"
      type: "object"
      description: "推荐的配置参数"
      
  onFail:
    action: "use-default-config"
    message: "配置推荐失败，使用默认配置"
```

### 2.3 阶段3：配置生成

#### 步骤3.1：个性化配置生成

```yaml
Step_3_1:
  name: "个性化配置生成"
  type: "llm-assisted"
  timeout: 30s
  
  llmAction:
    enabled: true
    fallback: "user-input"
    
  prompt:
    template: "generate-personalized-config"
    systemPrompt: |
      你是一个专业的配置生成助手。
      请根据用户的具体需求，生成个性化的配置文件。
      
    userPrompt: |
      用户需求：
      - 用户角色：{userRole}
      - 团队规模：{teamSize}
      - 业务需求：{businessRequirements}
      - 技术环境：{techEnvironment}
      
      场景技能配置模板：
      {configTemplate}
      
      请生成完整的配置文件，包括：
      1. 角色配置
      2. 菜单配置
      3. 功能配置
      4. LLM配置
      5. 降级配置
      
  outputs:
    - id: "generated-config"
      type: "object"
      description: "生成的配置文件"
      
  onFail:
    action: "prompt-user-input"
    message: "配置生成失败，请手动填写配置"
```

### 2.4 阶段4：安装执行

#### 步骤4.1：自动安装

```yaml
Step_4_1:
  name: "自动安装"
  type: "auto"
  timeout: 120s
  
  actions:
    - id: "init-database"
      description: "初始化数据库"
      method: "POST"
      url: "/api/database/init"
      payload:
        scripts:
          - "create-tables.sql"
          - "init-data.sql"
      timeout: 60s
      
    - id: "register-capabilities"
      description: "注册能力"
      method: "POST"
      url: "/api/capability/register"
      payload:
        capabilities: "{capabilities}"
      timeout: 10s
      
    - id: "bind-capabilities"
      description: "绑定能力到场景组"
      method: "POST"
      url: "/api/capability/bind"
      payload:
        sceneGroupId: "{sceneGroupId}"
        capabilities: "{capabilities}"
      timeout: 10s
      
  llmMonitor:
    enabled: true
    checkpoints:
      - step: "init-database"
        prompt: "数据库初始化完成了吗？"
      - step: "register-capabilities"
        prompt: "能力注册完成了吗？"
      - step: "bind-capabilities"
        prompt: "能力绑定完成了吗？"
        
  onFail:
    action: "rollback"
    message: "安装失败，正在回滚..."
    log: "ERROR: Installation failed, rolling back"
```

### 2.5 阶段5：激活配置

#### 步骤5.1：激活引导

```yaml
Step_5_1:
  name: "激活引导"
  type: "llm-assisted"
  timeout: 60s
  
  llmAction:
    enabled: true
    fallback: "wizard-guide"
    
  prompt:
    template: "guide-activation"
    systemPrompt: |
      你是一个专业的激活向导助手。
      请引导用户完成场景技能的激活流程。
      
    userPrompt: |
      场景技能信息：
      - 技能ID：{skillId}
      - 技能名称：{skillName}
      
      用户角色：{userRole}
      激活步骤：{activationSteps}
      
      请为每个激活步骤提供详细的说明和指导。
      
  outputs:
    - id: "activation-guide"
      type: "object"
      description: "激活引导内容"
      
  onFail:
    action: "use-wizard"
    message: "激活引导失败，使用标准向导"
```

---

## 三、知识库预制信息规范

### 3.1 知识库分类

```yaml
KnowledgeBase:
  # 系统知识库
  system:
    - id: "platform-docs"
      name: "平台文档"
      description: "Ooder平台相关文档"
      type: "document"
      embeddingModel: "text-embedding-ada-002"
      chunkSize: 500
      chunkOverlap: 50
      
    - id: "api-docs"
      name: "API文档"
      description: "Ooder平台API文档"
      type: "document"
      embeddingModel: "text-embedding-ada-002"
      chunkSize: 500
      chunkOverlap: 50
      
    - id: "troubleshooting"
      name: "故障排除"
      description: "常见问题和解决方案"
      type: "qa"
      embeddingModel: "text-embedding-ada-002"
      chunkSize: 300
      chunkOverlap: 30
      
  # 场景知识库
  scene:
    - id: "skill-docs"
      name: "技能文档"
      description: "场景技能使用文档"
      type: "document"
      embeddingModel: "text-embedding-ada-002"
      chunkSize: 500
      chunkOverlap: 50
      
    - id: "feature-guide"
      name: "功能指南"
      description: "场景技能功能使用指南"
      type: "document"
      embeddingModel: "text-embedding-ada-002"
      chunkSize: 500
      chunkOverlap: 50
      
    - id: "best-practices"
      name: "最佳实践"
      description: "场景技能使用最佳实践"
      type: "document"
      embeddingModel: "text-embedding-ada-002"
      chunkSize: 500
      chunkOverlap: 50
      
    - id: "faq"
      name: "常见问题"
      description: "场景技能常见问题"
      type: "qa"
      embeddingModel: "text-embedding-ada-002"
      chunkSize: 300
      chunkOverlap: 30
```

### 3.2 向量库预制信息

```yaml
VectorDatabase:
  # 预制向量数据
  preloaded:
    # 平台知识
    platform:
      - category: "platform"
        documents:
          - id: "platform-intro"
            title: "Ooder平台介绍"
            content: |
              Ooder是一个智能场景技能管理平台...
            metadata:
              type: "introduction"
                tags: ["platform", "overview"]
                
          - id: "installation-guide"
            title: "安装指南"
            content: |
              场景技能安装流程...
            metadata:
              type: "guide"
                tags: ["installation", "guide"]
                
    # 场景知识
    scene:
      - category: "scene"
        documents:
          - id: "scene-intro"
            title: "场景技能介绍"
            content: |
              场景技能是Ooder平台的核心功能...
            metadata:
              type: "introduction"
                tags: ["scene", "overview"]
                
          - id: "activation-guide"
            title: "激活指南"
            content: |
              场景技能激活流程...
            metadata:
              type: "guide"
                tags: ["activation", "guide"]
                
          - id: "configuration-guide"
            title: "配置指南"
            content: |
              场景技能配置说明...
            metadata:
              type: "guide"
                tags: ["configuration", "guide"]
```

### 3.3 知识库更新机制

```yaml
KnowledgeUpdate:
  # 自动更新
  autoUpdate:
    enabled: true
    schedule: "0 0 2 * * ?"  # 每天凌晨2点
    
    sources:
      - id: "platform-docs"
        url: "https://docs.ooder.net/latest"
        type: "document"
        updateStrategy: "full-sync"
        
      - id: "scene-docs"
        url: "https://github.com/ooder-skills/docs"
        type: "document"
        updateStrategy: "incremental"
        
  # 手动更新
  manualUpdate:
    enabled: true
    api:
      endpoint: "/api/knowledge/upload"
      methods: ["POST"]
      authentication: "required"
      
    supportedFormats:
      - type: "document"
        formats: [".md", ".pdf", ".txt", ".docx"]
        maxSize: "10MB"
        
      - type: "qa"
        formats: [".json", ".yaml"]
        maxSize: "1MB"
```

---

## 四、工具库规范

### 4.1 默认工具库

```yaml
ToolLibrary:
  # 默认工具
  default:
    # 网络工具
    network:
      - id: "http-request"
        name: "HTTP请求"
        description: "发送HTTP请求到外部API"
        version: "1.0.0"
        type: "network"
        parameters:
          - name: "url"
            type: "string"
            required: true
            description: "请求URL"
          - name: "method"
            type: "string"
            required: true
            description: "请求方法"
            enum: ["GET", "POST", "PUT", "DELETE"]
          - name: "headers"
            type: "object"
            required: false
            description: "请求头"
          - name: "body"
            type: "object"
            required: false
            description: "请求体"
          - name: "timeout"
            type: "integer"
            default: 5000
            description: "超时时间(毫秒)"
            
      - id: "websocket-client"
        name: "WebSocket客户端"
        description: "建立WebSocket连接"
        version: "1.0.0"
        type: "network"
        parameters:
          - name: "url"
            type: "string"
            required: true
          - name: "protocols"
            type: "array"
            default: ["chat", "binary"]
            
    # 数据工具
    data:
      - id: "database-query"
        name: "数据库查询"
        description: "查询数据库"
        version: "1.0.0"
        type: "data"
        parameters:
          - name: "query"
            type: "string"
            required: true
            description: "SQL查询语句"
          - name: "limit"
            type: "integer"
            default: 100
            description: "返回结果限制"
            
      - id: "redis-cache"
        name: "Redis缓存"
        description: "Redis缓存操作"
        version: "1.0.0"
        type: "data"
        parameters:
          - name: "key"
            type: "string"
            required: true
          - name: "operation"
            type: "string"
            enum: ["get", "set", "delete"]
            required: true
          - name: "value"
            type: "string"
            required: false
            
    # 消息工具
    messaging:
      - id: "mqtt-publish"
        name: "MQTT发布"
        description: "发布MQTT消息"
        version: "1.0.0"
        type: "messaging"
        parameters:
          - name: "topic"
            type: "string"
            required: true
            description: "MQTT主题"
          - name: "payload"
            type: "object"
            required: true
            description: "消息内容"
          - name: "qos"
            type: "integer"
            default: 1
            description: "服务质量"
            
      - id: "mqtt-subscribe"
        name: "MQTT订阅"
        description: "订阅MQTT主题"
        version: "1.0.0"
        type: "messaging"
        parameters:
          - name: "topic"
            type: "string"
            required: true
          - name: "qos"
            type: "integer"
            default: 1
            
    # 存储工具
    storage:
      - id: "file-read"
        name: "文件读取"
        description: "读取存储文件"
        version: "1.0.0"
        type: "storage"
        parameters:
          - name: "path"
            type: "string"
            required: true
            description: "文件路径"
          - name: "encoding"
            type: "string"
            default: "utf-8"
            
      - id: "file-write"
        name: "文件写入"
        description: "写入存储文件"
        version: "1.0.0"
        type: "storage"
        parameters:
          - name: "path"
            type: "string"
            required: true
          - name: "content"
            type: "string"
            required: true
          - name: "encoding"
            type: "string"
            default: "utf-8"
```

### 4.2 扩展工具下载与更新

```yaml
ToolExtension:
  # 工具仓库
  repository:
    enabled: true
    url: "https://tools.ooder.net/registry"
    authentication: "token"
    
  # 工具下载
  download:
    enabled: true
    api:
      endpoint: "/api/tools/download"
      methods: ["POST"]
      parameters:
        - name: "toolId"
          type: "string"
          required: true
        - name: "version"
          type: "string"
          required: false
          
    validation:
      - id: "signature-check"
        description: "验证工具签名"
        enabled: true
        
      - id: "security-scan"
        description: "安全扫描"
        enabled: true
        
  # 工具更新
  update:
    enabled: true
    schedule: "0 0 3 * * ?"  # 每天凌晨3点
    
    strategies:
      - id: "auto-update"
        description: "自动更新"
        enabled: false
        
      - id: "prompt-user"
        description: "提示用户更新"
        enabled: true
        conditions:
          - type: "security"
            action: "force-update"
          - type: "feature"
            action: "ask-user"
          - type: "bugfix"
            action: "ask-user"
            
  # 工具版本管理
  versioning:
    enabled: true
    strategy: "semantic"  # semantic | major | minor | patch
    
    compatibility:
      - id: "backward-compatible"
        description: "向后兼容"
        enabled: true
```

---

## 五、传统安装模式（无LLM）

### 5.1 传统安装流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    传统安装流程（无LLM）                                      │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  Step 1: 前置检查                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  • 检查依赖服务                                                        │    │
│  │  • 验证系统环境                                                        │    │
│  │  • 检查磁盘空间                                                        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  Step 2: 配置填写                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  • 显示配置表单                                                        │    │
│  │  • 提供默认值                                                          │    │
│  │  • 提供配置说明                                                        │    │
│  │  • 验证配置有效性                                                      │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  Step 3: 安装执行                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  • 初始化数据库                                                        │    │
│  │  • 注册能力                                                            │    │
│  │  • 绑定能力到场景组                                                  │    │
│  │  • 生成菜单                                                            │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  Step 4: 激活配置                                                              │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  • 选择参与者                                                          │    │
│  │  • 配置角色权限                                                        │    │
│  │  • 配置功能参数                                                        │    │
│  │  • 确认激活                                                            │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 传统安装配置

```yaml
TraditionalInstallation:
  # 配置表单
  configForm:
    enabled: true
    
    sections:
      - id: "basic-config"
        name: "基础配置"
        fields:
          - id: "scene-name"
            type: "string"
            label: "场景名称"
            required: true
            defaultValue: "{skillName}"
            
          - id: "scene-description"
            type: "text"
            label: "场景描述"
            required: false
            defaultValue: "{skillDescription}"
            
      - id: "role-config"
        name: "角色配置"
        fields:
          - id: "manager"
            type: "user-selector"
            label: "场景管理者"
            required: true
            minCount: 1
            maxCount: 1
            
          - id: "employees"
            type: "user-selector"
            label: "员工"
            required: true
            minCount: 1
            maxCount: 100
            
      - id: "feature-config"
        name: "功能配置"
        fields:
          - id: "daily-remind"
            type: "boolean"
            label: "每日提醒"
            defaultValue: true
            
          - id: "ai-generate"
            type: "boolean"
            label: "AI生成"
            defaultValue: false
            disabled: true  # 无LLM时禁用
```

---

## 六、SE支持能力匹配度

### 6.1 SE支持情况检查

| 功能 | LLM主导模式 | 传统模式 | SE支持 |
|------|------------|---------|--------|
| LLM可用性检测 | ✅ 需要 | ❌ 不需要 | ✅ 支持 |
| LLM智能分析 | ✅ 需要 | ❌ 不需要 | ⚠️ 部分支持 |
| 配置推荐 | ✅ 需要 | ❌ 不需要 | ❌ 不支持 |
| 个性化配置生成 | ✅ 需要 | ❌ 不需要 | ❌ 不支持 |
| 激活引导 | ✅ 需要 | ❌ 不需要 | ❌ 不支持 |
| 依赖检查 | ✅ 需要 | ✅ 需要 | ✅ 支持 |
| 数据库初始化 | ✅ 需要 | ✅ 需要 | ✅ 支持 |
| 能力注册 | ✅ 需要 | ✅ 需要 | ✅ 支持 |
| 菜单生成 | ✅ 需要 | ✅ 需要 | ✅ 支持 |
| 激活流程 | ✅ 需要 | ✅ 需要 | ✅ 支持 |

### 6.2 SE支持差距分析

| 差距项 | 当前状态 | 影响 | 优先级 | 建议 |
|--------|---------|------|--------|------|
| LLM智能分析 | ⚠️ 部分支持 | LLM主导模式体验 | P0 | 实现LLM分析接口 |
| 配置推荐 | ❌ 不支持 | 用户体验 | P0 | 实现配置推荐引擎 |
| 个性化配置生成 | ❌ 不支持 | 用户体验 | P0 | 实现配置生成引擎 |
| 激活引导 | ❌ 不支持 | 用户体验 | P1 | 实现激活引导引擎 |

### 6.3 需要新增的SE能力

```yaml
# 需要新增的SE配置项
SE_Enhancements:
  # LLM配置
  llm:
    - id: "llm-config"
      type: "object"
      description: "LLM服务配置"
      fields:
        - name: "enabled"
          type: "boolean"
        - name: "endpoint"
          type: "string"
        - name: "apiKey"
          type: "string"
        - name: "models"
          type: "array"
          
  # LLM分析
  llmAnalysis:
    - id: "llm-analysis-config"
      type: "object"
      description: "LLM分析配置"
      fields:
        - name: "enabled"
          type: "boolean"
        - name: "prompts"
          type: "object"
        - name: "fallback"
          type: "string"
          
  # 工具库
  toolLibrary:
    - id: "tool-registry"
      type: "object"
      description: "工具注册表"
      fields:
        - name: "default-tools"
          type: "array"
        - name: "extension-tools"
          type: "array"
        - name: "download-url"
          type: "string"
```

---

## 七、安装问题处理

### 7.1 常见问题

| 问题ID | 问题描述 | LLM主导解决 | 传统解决 |
|--------|---------|------------|---------|
| **INS-001** | LLM服务不可用 | 自动切换到传统模式 | 提示用户检查LLM服务 |
| **INS-002** | 依赖服务不可用 | LLM分析降级策略 | 提示用户手动配置依赖 |
| **INS-003** | 配置参数不明确 | LLM解释参数含义 | 提供配置说明文档 |
| **INS-004** | 安装超时 | LLM诊断问题原因 | 提供重试机制 |
| **INS-005** | 激活失败 | LLM分析失败原因 | 提供错误日志 |

### 7.2 问题诊断

```yaml
ProblemDiagnosis:
  # LLM诊断
  llmDiagnosis:
    enabled: true
    
    prompts:
      - id: "diagnose-install-failure"
        template: "diagnose-install-failure"
        systemPrompt: |
          你是一个专业的安装诊断助手。
          请分析安装失败的原因，并提供解决方案。
          
        userPrompt: |
          安装信息：
          - 技能ID：{skillId}
          - 失败步骤：{failedStep}
          - 错误信息：{errorMessage}
          - 日志：{logs}
          
          请分析失败原因并提供解决方案。
          
  # 传统诊断
  traditionalDiagnosis:
    enabled: true
    
    rules:
      - id: "check-dependency"
        condition: "dependency-check-failed"
        solution: "检查依赖服务状态"
        
      - id: "check-config"
        condition: "config-validation-failed"
        solution: "检查配置参数"
```

---

## 八、验收标准

### 8.1 LLM主导模式

- [ ] LLM可用性检测正常工作
- [ ] LLM智能分析能够推荐配置
- [ ] LLM能够引导激活流程
- [ ] 安装进度能够被LLM监控
- [ ] LLM不可用时能够自动降级

### 8.2 传统模式

- [ ] 依赖检查正常工作
- [ ] 配置表单能够正常填写
- [ ] 数据库初始化正常工作
- [ ] 能力注册正常工作
- [ ] 菜单生成正常工作
- [ ] 激活流程正常工作

### 8.3 SE支持

- [ ] SE支持LLM可用性检测
- [ ] SE支持依赖检查
- [ ] SE支持数据库初始化
- [ ] SE支持能力注册
- [ ] SE支持菜单生成
- [ ] SE支持激活流程

---

## 九、实施建议

### 9.1 第一阶段：LLM集成

| 任务 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| 实现LLM可用性检测 | P0 | 1周 | 检测LLM服务状态 |
| 实现LLM分析接口 | P0 | 2周 | 支持LLM智能分析 |
| 实现LLM监控机制 | P1 | 2周 | 监控安装进度 |

### 9.2 第二阶段：知识库与工具库

| 任务 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| 实现知识库预制 | P0 | 2周 | 预制平台和场景知识 |
| 实现工具库管理 | P0 | 2周 | 默认工具和扩展工具 |
| 实现工具下载与更新 | P1 | 2周 | 工具仓库和更新机制 |

### 9.3 第三阶段：传统安装优化

| 任务 | 优先级 | 工作量 | 说明 |
|------|--------|--------|------|
| 优化配置表单 | P1 | 1周 | 提供更好的用户体验 |
| 实现问题诊断 | P1 | 1周 | 提供问题诊断能力 |
| 实现降级策略 | P0 | 2周 | LLM不可用时的降级 |

---

**文档状态**: 安装说明书  
**下一步**: 根据说明书实施相关功能
