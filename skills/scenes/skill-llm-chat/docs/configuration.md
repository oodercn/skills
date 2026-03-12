# 配置指南

## 配置概述

skill-llm-chat 支持以下配置方式：

1. 配置文件 (`application.yml`)
2. 环境变量
3. 配置中心 (Nacos/Apollo)

## 基础配置

### 启用Skill

```yaml
skill-llm-chat:
  enabled: true
```

### 核心配置

```yaml
skill-llm-chat:
  default-provider: baidu        # 默认LLM Provider
  default-model: ernie-bot-4     # 默认模型
  max-tokens: 4096               # 最大Token数
  temperature: 0.7               # 温度参数 (0-2)
  stream-enabled: true           # 启用流式输出
```

## Provider配置

### 百度文心一言

```yaml
llm:
  providers:
    baidu:
      enabled: true
      api-key: ${BAIDU_API_KEY}
      secret-key: ${BAIDU_SECRET_KEY}
      models:
        - id: ernie-bot-4
          name: 文心大模型4.0
          max-tokens: 8192
        - id: ernie-bot-turbo
          name: 文心大模型Turbo
          max-tokens: 4096
```

### OpenAI

```yaml
llm:
  providers:
    openai:
      enabled: true
      api-key: ${OPENAI_API_KEY}
      base-url: https://api.openai.com/v1
      models:
        - id: gpt-4
          name: GPT-4
          max-tokens: 8192
        - id: gpt-3.5-turbo
          name: GPT-3.5 Turbo
          max-tokens: 4096
```

### 阿里千问

```yaml
llm:
  providers:
    qianwen:
      enabled: true
      api-key: ${QIANWEN_API_KEY}
      models:
        - id: qwen-max
          name: 通义千问Max
          max-tokens: 8192
        - id: qwen-plus
          name: 通义千问Plus
          max-tokens: 4096
```

### DeepSeek

```yaml
llm:
  providers:
    deepseek:
      enabled: true
      api-key: ${DEEPSEEK_API_KEY}
      base-url: https://api.deepseek.com/v1
      models:
        - id: deepseek-chat
          name: DeepSeek Chat
          max-tokens: 4096
```

### Ollama (本地部署)

```yaml
llm:
  providers:
    ollama:
      enabled: true
      base-url: http://localhost:11434
      models:
        - id: llama2
          name: Llama 2
          max-tokens: 4096
```

## 会话配置

```yaml
skill-llm-chat:
  session:
    timeout: 3600              # 会话超时时间(秒)
    max-history: 100           # 最大历史记录数
    cleanup-interval: 300      # 清理间隔(秒)
```

## 上下文配置

```yaml
skill-llm-chat:
  context:
    max-tokens: 2000           # 最大上下文Token数
    include-system-prompt: true # 包含系统提示
    sources:
      - type: page-context     # 页面上下文
        enabled: true
      - type: knowledge-base   # 知识库
        enabled: false
```

## 高级配置

### 性能调优

```yaml
skill-llm-chat:
  performance:
    connection-pool-size: 10
    request-timeout: 60000
    retry-times: 3
    retry-interval: 1000
```

### 安全配置

```yaml
skill-llm-chat:
  security:
    rate-limit:
      enabled: true
      requests-per-minute: 60
    content-filter:
      enabled: true
      sensitive-words: []
```

## 配置示例

### 开发环境

```yaml
skill-llm-chat:
  enabled: true
  default-provider: ollama
  default-model: llama2
  stream-enabled: true
  
llm:
  providers:
    ollama:
      enabled: true
      base-url: http://localhost:11434
```

### 生产环境

```yaml
skill-llm-chat:
  enabled: true
  default-provider: baidu
  default-model: ernie-bot-4
  max-tokens: 4096
  temperature: 0.7
  stream-enabled: true
  
  session:
    timeout: 7200
    max-history: 200
    
  performance:
    connection-pool-size: 20
    request-timeout: 60000
    
  security:
    rate-limit:
      enabled: true
      requests-per-minute: 100

llm:
  providers:
    baidu:
      enabled: true
      api-key: ${BAIDU_API_KEY}
      secret-key: ${BAIDU_SECRET_KEY}
```

## 配置项参考

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| default-provider | string | baidu | 默认LLM Provider |
| default-model | string | ernie-bot-4 | 默认模型 |
| max-tokens | integer | 4096 | 最大Token数 |
| temperature | number | 0.7 | 温度参数 |
| stream-enabled | boolean | true | 启用流式输出 |
| session.timeout | integer | 3600 | 会话超时(秒) |
| session.max-history | integer | 100 | 最大历史记录数 |
