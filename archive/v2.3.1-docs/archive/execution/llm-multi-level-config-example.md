# LLM 多级配置完整示例

## 一、能力段定义

### 1.1 LLM 能力段 (0x0200 - 0x0204)

```yaml
# capability-segments.yaml

segments:
  - baseAddress: 0x0200
    code: llm
    name: 大语言模型
    description: 大语言模型对话能力，支持多模型切换
    
    selectionMode: SINGLE
    switchScope: RUNTIME
    switchEffect: 每次调用可手工选择模型
    
    slots:
      - offset: 0
        name: PRIMARY
        description: 主LLM提供者
        role: DATA_PROVIDER
        providers:
          - skillId: skill-llm-openai
            tier: large
            name: OpenAI
            description: OpenAI GPT系列模型
            
          - skillId: skill-llm-qianwen
            tier: large
            name: 通义千问
            description: 阿里云通义千问大模型
            
          - skillId: skill-llm-deepseek
            tier: medium
            name: DeepSeek
            description: DeepSeek大模型
            
          - skillId: skill-llm-volcengine
            tier: large
            name: 火山引擎豆包
            description: 字节跳动火山引擎豆包大模型
            
          - skillId: skill-llm-ollama
            tier: micro
            name: Ollama本地模型
            description: Ollama本地部署模型
            
        fallback: skill-llm-ollama
        
      - offset: 1
        name: STANDBY
        description: 备用LLM提供者
        role: BACKUP_PROVIDER
        providers:
          - skillId: skill-llm-openai
          - skillId: skill-llm-qianwen
          - skillId: skill-llm-deepseek
        default: null
        
      - offset: 2
        name: CACHE
        description: LLM响应缓存
        role: CACHE_LAYER
        providers:
          - skillId: skill-cache-redis
          - skillId: skill-cache-memory
        default: null
        
      - offset: 3
        name: READONLY
        description: 只读模型（低成本）
        role: READONLY_REPLICA
        providers: []
        default: null
        
      - offset: 4
        name: ARCHIVE
        description: 对话历史归档
        role: ARCHIVE_LAYER
        providers:
          - skillId: skill-vfs-oss
          - skillId: skill-vfs-minio
        default: null
```

---

## 二、LLM 驱动配置

### 2.1 OpenAI 驱动

```yaml
# skill-llm-openai/skill.yaml

skillId: skill-llm-openai
name: OpenAI LLM Provider
version: "2.3.1"
type: PROVIDER
subType: DRIVER
category: LLM

# 提供的能力
provides:
  address: 0x0200
  code: llm

# 驱动属性
driverGroup: llm
tier: large
exclusive: true

# 模型配置
models:
  - id: gpt-4o
    name: GPT-4o
    type: chat
    contextWindow: 128000
    pricing:
      input: 2.50  # $/1M tokens
      output: 10.00
      
  - id: gpt-4o-mini
    name: GPT-4o Mini
    type: chat
    contextWindow: 128000
    pricing:
      input: 0.15
      output: 0.60
      
  - id: gpt-4-turbo
    name: GPT-4 Turbo
    type: chat
    contextWindow: 128000
    pricing:
      input: 10.00
      output: 30.00
      
  - id: gpt-3.5-turbo
    name: GPT-3.5 Turbo
    type: chat
    contextWindow: 16385
    pricing:
      input: 0.50
      output: 1.50
      
  - id: text-embedding-3-small
    name: Text Embedding 3 Small
    type: embedding
    dimensions: 1536
    pricing:
      input: 0.02
      
  - id: text-embedding-3-large
    name: Text Embedding 3 Large
    type: embedding
    dimensions: 3072
    pricing:
      input: 0.13

# 默认模型
defaultModel: gpt-4o-mini

# API配置
api:
  baseUrl: https://api.openai.com/v1
  timeout: 60000
  maxRetries: 3
  
# 认证配置
auth:
  type: api_key
  header: Authorization
  prefix: Bearer
  envKey: OPENAI_API_KEY
```

### 2.2 通义千问驱动

```yaml
# skill-llm-qianwen/skill.yaml

skillId: skill-llm-qianwen
name: Qianwen LLM Provider
version: "2.3.1"
type: PROVIDER
subType: DRIVER
category: LLM

provides:
  address: 0x0200
  code: llm

driverGroup: llm
tier: large
exclusive: true

models:
  - id: qwen-max
    name: 通义千问-Max
    type: chat
    contextWindow: 32000
    pricing:
      input: 0.04  # ¥/1K tokens
      output: 0.12
      
  - id: qwen-plus
    name: 通义千问-Plus
    type: chat
    contextWindow: 128000
    pricing:
      input: 0.004
      output: 0.012
      
  - id: qwen-turbo
    name: 通义千问-Turbo
    type: chat
    contextWindow: 128000
    pricing:
      input: 0.002
      output: 0.006
      
  - id: qwen-long
    name: 通义千问-Long
    type: chat
    contextWindow: 10000000
    pricing:
      input: 0.0005
      output: 0.002
      
  - id: text-embedding-v3
    name: Text Embedding v3
    type: embedding
    dimensions: 1024
    pricing:
      input: 0.0007

defaultModel: qwen-turbo

api:
  baseUrl: https://dashscope.aliyuncs.com/api/v1
  timeout: 60000
  maxRetries: 3
  
auth:
  type: api_key
  header: Authorization
  prefix: Bearer
  envKey: DASHSCOPE_API_KEY
```

### 2.3 DeepSeek 驱动

```yaml
# skill-llm-deepseek/skill.yaml

skillId: skill-llm-deepseek
name: DeepSeek LLM Provider
version: "2.3.1"
type: PROVIDER
subType: DRIVER
category: LLM

provides:
  address: 0x0200
  code: llm

driverGroup: llm
tier: medium
exclusive: true

models:
  - id: deepseek-chat
    name: DeepSeek Chat
    type: chat
    contextWindow: 64000
    pricing:
      input: 0.001  # ¥/1K tokens
      output: 0.002
      
  - id: deepseek-reasoner
    name: DeepSeek Reasoner
    type: chat
    contextWindow: 64000
    pricing:
      input: 0.001
      output: 0.002

defaultModel: deepseek-chat

api:
  baseUrl: https://api.deepseek.com/v1
  timeout: 60000
  maxRetries: 3
  
auth:
  type: api_key
  header: Authorization
  prefix: Bearer
  envKey: DEEPSEEK_API_KEY
```

### 2.4 Ollama 本地驱动

```yaml
# skill-llm-ollama/skill.yaml

skillId: skill-llm-ollama
name: Ollama Local LLM Provider
version: "2.3.1"
type: PROVIDER
subType: DRIVER
category: LLM

provides:
  address: 0x0200
  code: llm

driverGroup: llm
tier: micro
exclusive: true
builtIn: true

models:
  - id: llama3.2
    name: Llama 3.2
    type: chat
    contextWindow: 128000
    
  - id: llama3.1
    name: Llama 3.1
    type: chat
    contextWindow: 128000
    
  - id: qwen2.5
    name: Qwen 2.5
    type: chat
    contextWindow: 128000
    
  - id: deepseek-coder
    name: DeepSeek Coder
    type: chat
    contextWindow: 16384
    
  - id: nomic-embed-text
    name: Nomic Embed Text
    type: embedding
    dimensions: 768

defaultModel: llama3.2

api:
  baseUrl: http://localhost:11434
  timeout: 120000
  maxRetries: 1
  
auth:
  type: none
```

---

## 三、系统默认配置

### 3.1 按环境配置

```yaml
# config/capability-defaults.yaml

environment: production
tier: large

capabilityDefaults:
  
  # LLM 能力默认配置
  0x0200:
    # 生产环境
    production:
      primary: skill-llm-openai
      standby: skill-llm-qianwen
      cache: skill-cache-redis
      
    # 预发环境
    staging:
      primary: skill-llm-qianwen
      standby: skill-llm-deepseek
      cache: skill-cache-memory
      
    # 开发环境
    development:
      primary: skill-llm-ollama
      cache: skill-cache-memory
      
    # 按规模
    large:
      primary: skill-llm-openai
      standby: skill-llm-qianwen
      
    medium:
      primary: skill-llm-deepseek
      standby: skill-llm-qianwen
      
    small:
      primary: skill-llm-qianwen
      
    micro:
      primary: skill-llm-ollama
      
    # 兜底
    fallback: skill-llm-ollama
```

---

## 四、场景技能配置

### 4.1 LLM 智能对话场景

```yaml
# skill-llm-chat/skill.yaml

skillId: skill-llm-chat
name: LLM智能对话场景
version: "2.3.1"
type: SCENE
category: LLM
domain: ai

# 场景类型
sceneType: AUTO
visibility: PUBLIC

# 声明需要的能力
requiredCapabilities:
  - segment: 0x0200        # LLM
    slots: [PRIMARY, CACHE]
    required: true
    
  - segment: 0x020F        # KNOWLEDGE
    slots: [PRIMARY]
    required: false
    
  - segment: 0x0104        # NOTIFICATION
    slots: [PRIMARY]
    required: false

# 场景默认配置
sceneDefaults:
  0x0200:
    primary: skill-llm-qianwen
    cache: skill-cache-memory

# 模型选择策略
modelSelection:
  # 默认模型
  default: qwen-turbo
  
  # 按任务类型选择
  byTask:
    code: deepseek-coder
    math: gpt-4o
    creative: qwen-max
    general: qwen-turbo
    
  # 按成本选择
  byCost:
    low: qwen-turbo
    medium: qwen-plus
    high: qwen-max
    
  # 按速度选择
  bySpeed:
    fast: qwen-turbo
    normal: qwen-plus
    slow: qwen-max

# 对话配置
conversation:
  maxHistory: 20
  maxTokens: 4096
  temperature: 0.7
  topP: 0.9
  
# 依赖
dependencies:
  - skillId: skill-llm-conversation
    version: ">=2.3.0"
    required: true
    
  - skillId: skill-llm-context-builder
    version: ">=2.3.0"
    required: true
```

### 4.2 知识问答场景

```yaml
# skill-knowledge-qa/skill.yaml

skillId: skill-knowledge-qa
name: 知识问答场景
version: "2.3.1"
type: SCENE
category: KNOWLEDGE
domain: ai

sceneType: AUTO
visibility: PUBLIC

requiredCapabilities:
  - segment: 0x020F        # KNOWLEDGE
    slots: [PRIMARY]
    required: true
    
  - segment: 0x0214        # RAG
    slots: [PRIMARY]
    required: true
    
  - segment: 0x0200        # LLM
    slots: [PRIMARY]
    required: true

sceneDefaults:
  0x020F:
    primary: skill-knowledge-base
  0x0214:
    primary: skill-rag
  0x0200:
    primary: skill-llm-qianwen

# RAG配置
rag:
  topK: 5
  scoreThreshold: 0.7
  rerank: true
  
# 依赖
dependencies:
  - skillId: skill-knowledge-base
    version: ">=2.3.0"
    required: true
    
  - skillId: skill-rag
    version: ">=2.3.0"
    required: true
```

---

## 五、用户配置示例

### 5.1 系统级配置

```yaml
# 用户激活场景时的配置

sceneActivation:
  sceneId: llm-chat
  
  # 用户显式配置（最高优先级）
  capabilityBindings:
    0x0200:
      primary: skill-llm-deepseek    # 用户选择 DeepSeek
      cache: skill-cache-redis       # 使用 Redis 缓存
      
  # 模型配置
  modelConfig:
    provider: deepseek
    model: deepseek-chat
    temperature: 0.8
    maxTokens: 2048
```

### 5.2 运行时切换

```yaml
# 运行时切换模型（无需重启）

runtimeSwitch:
  address: 0x0200
  from: skill-llm-deepseek
  to: skill-llm-openai
  
  # 切换原因
  reason: "需要使用 GPT-4o 进行复杂推理"
  
  # 是否保存为默认
  saveAsDefault: false
```

---

## 六、配置优先级

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        LLM 配置优先级                                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   1. 用户显式配置                                                            │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  capabilityBindings:                                             │    │
│      │    0x0200:                                                       │    │
│      │      primary: skill-llm-deepseek   # 用户选择                    │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓ 未配置                                        │
│   2. 场景默认配置                                                            │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  sceneDefaults:                                                  │    │
│      │    0x0200:                                                       │    │
│      │      primary: skill-llm-qianwen    # 场景推荐                    │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓ 未配置                                        │
│   3. 系统配置(环境)                                                          │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  environment: production                                         │    │
│      │  0x0200.production.primary: skill-llm-openai   # 生产环境       │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓ 未配置                                        │
│   4. 系统配置(规模)                                                          │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  tier: large                                                     │    │
│      │  0x0200.large.primary: skill-llm-openai        # 大规模         │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                              ↓ 未配置                                        │
│   5. 枚举内置兜底                                                            │
│      ┌─────────────────────────────────────────────────────────────────┐    │
│      │  LLM(0x0200, "llm", "大语言模型", SINGLE, RUNTIME,               │    │
│      │      "skill-llm-ollama")  # 兜底：Ollama本地模型                 │    │
│      └─────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 七、总结

### 配置层级

| 层级 | 位置 | 优先级 |
|------|------|:------:|
| 用户配置 | capabilityBindings | 1（最高） |
| 场景默认 | sceneDefaults | 2 |
| 系统环境 | capabilityDefaults.{env} | 3 |
| 系统规模 | capabilityDefaults.{tier} | 4 |
| 枚举兜底 | CapabilitySegment.fallback | 5（最低） |

### 切换范围

| 能力 | 范围 | 说明 |
|------|------|------|
| LLM | RUNTIME | 每次调用可手工选择模型 |

### 多级配置优势

1. **灵活性** - 支持多级配置，满足不同场景需求
2. **可用性** - 内置兜底保证系统始终可用
3. **可维护性** - 配置分层，易于管理
4. **可扩展性** - 支持新增模型和提供者

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
