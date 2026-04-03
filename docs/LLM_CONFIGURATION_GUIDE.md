# LLM 配置指南

> 文档路径: `e:\github\ooder-skills\docs\LLM_CONFIGURATION_GUIDE.md`
> 版本: 3.0.1

---

## 一、概述

本文档描述Ooder Skills框架中LLM（大语言模型）的配置方式和使用方法。

### 1.1 架构层次

```
LLM 架构层次
├── SPI层 (接口定义)
│   └── LlmProvider接口 - 定义标准LLM操作接口
│
├── Driver层 (驱动实现)
│   ├── skill-llm-base      - 基础驱动，定义数据模型
│   ├── skill-llm-deepseek  - DeepSeek驱动
│   ├── skill-llm-openai    - OpenAI驱动
│   ├── skill-llm-qianwen   - 通义千问驱动
│   ├── skill-llm-volcengine- 火山引擎驱动
│   ├── skill-llm-ollama    - Ollama本地驱动
│   └── skill-llm-baidu     - 百度文心驱动
│
├── Config层 (配置管理)
│   └── skill-llm-config-manager - 多级配置管理
│
└── Chat层 (聊天应用)
    └── skill-llm-chat      - LLM聊天助手
```

---

## 二、Provider配置

### 2.1 DeepSeek配置

```yaml
skill-llm-deepseek:
  api-key: ${DEEPSEEK_API_KEY}
  base-url: https://api.deepseek.com/v1
  model: deepseek-chat
  max-tokens: 64000
  temperature: 0.7
```

| 配置项 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| api-key | string | 是 | - | DeepSeek API Key |
| base-url | string | 否 | https://api.deepseek.com/v1 | API基础URL |
| model | string | 否 | deepseek-chat | 默认模型 |
| max-tokens | int | 否 | 64000 | 最大Token数 |
| temperature | float | 否 | 0.7 | 温度参数 |

### 2.2 OpenAI配置

```yaml
skill-llm-openai:
  api-key: ${OPENAI_API_KEY}
  base-url: https://api.openai.com/v1
  model: gpt-4o
  max-tokens: 4096
  temperature: 0.7
```

### 2.3 通义千问配置

```yaml
skill-llm-qianwen:
  api-key: ${QIANWEN_API_KEY}
  base-url: https://dashscope.aliyuncs.com/api/v1
  model: qwen-plus
  max-tokens: 8192
  temperature: 0.7
```

### 2.4 火山引擎配置

```yaml
skill-llm-volcengine:
  api-key: ${VOLCENGINE_API_KEY}
  endpoint-id: ${VOLCENGINE_ENDPOINT_ID}
  model: doubao-pro-32k
  max-tokens: 32768
  temperature: 0.7
```

### 2.5 Ollama本地配置

```yaml
skill-llm-ollama:
  base-url: http://localhost:11434
  model: llama2
  max-tokens: 4096
  temperature: 0.7
```

### 2.6 百度文心配置

```yaml
skill-llm-baidu:
  api-key: ${BAIDU_API_KEY}
  secret-key: ${BAIDU_SECRET_KEY}
  model: ernie-3.5-8k
  max-tokens: 4096
  temperature: 0.7
```

---

## 三、多级配置

### 3.1 配置层级

配置按优先级从高到低：

1. **用户级配置** - 用户个人偏好设置
2. **会话级配置** - 当前会话临时设置
3. **技能级配置** - 技能默认配置
4. **系统级配置** - 系统全局默认配置

### 3.2 配置解析示例

```java
// 解析配置
ResolveConfigRequest request = new ResolveConfigRequest();
request.setSkillId("skill-llm-chat");
request.setUserId("user-001");
request.setSessionId("session-001");

ResolvedConfig config = llmConfigService.resolveConfig(request);
```

### 3.3 配置存储

配置支持加密存储：

```yaml
llm-config:
  encryption:
    enabled: true
    algorithm: AES-256
    key: ${ENCRYPTION_KEY}
```

---

## 四、API使用

### 4.1 对话补全

```bash
POST /api/llm/chat
Content-Type: application/json

{
  "messages": [
    {"role": "system", "content": "你是一个助手"},
    {"role": "user", "content": "你好"}
  ],
  "model": "deepseek-chat",
  "maxTokens": 4096,
  "temperature": 0.7
}
```

### 4.2 流式对话

```bash
POST /api/llm/stream
Content-Type: application/json

{
  "messages": [
    {"role": "user", "content": "你好"}
  ],
  "model": "deepseek-chat",
  "stream": true
}
```

### 4.3 获取模型列表

```bash
GET /api/llm/models
```

### 4.4 获取Provider列表

```bash
GET /api/llm/providers
```

---

## 五、模型对照表

| Provider | 模型ID | 最大Token | 用途 |
|----------|--------|-----------|------|
| deepseek | deepseek-chat | 64000 | 通用对话 |
| deepseek | deepseek-coder | 16000 | 代码生成 |
| deepseek | deepseek-reasoner | 64000 | 推理任务 |
| openai | gpt-4o | 128000 | 通用对话 |
| openai | gpt-4-turbo | 128000 | 高速对话 |
| openai | gpt-3.5-turbo | 16384 | 高性价比 |
| qianwen | qwen-turbo | 8192 | 高速对话 |
| qianwen | qwen-plus | 32768 | 增强对话 |
| qianwen | qwen-max | 32768 | 旗舰对话 |
| volcengine | doubao-pro-32k | 32768 | 通用对话 |
| volcengine | doubao-pro-128k | 131072 | 长文本 |
| ollama | llama2 | 4096 | 本地对话 |
| ollama | mistral | 4096 | 本地对话 |
| baidu | ernie-4.0-8k | 8192 | 旗舰对话 |
| baidu | ernie-3.5-8k | 8192 | 通用对话 |

---

## 六、最佳实践

### 6.1 API Key安全

- 使用环境变量存储API Key
- 启用加密存储功能
- 定期轮换API Key

### 6.2 模型选择

- 简单对话：使用turbo/lite版本
- 复杂推理：使用pro/max版本
- 代码生成：使用coder专用模型
- 长文本：使用128k/long版本

### 6.3 成本优化

- 合理设置maxTokens
- 使用缓存减少重复调用
- 选择合适的模型规格

---

## 七、故障排查

### 7.1 常见错误

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 401 | API Key无效 | 检查API Key配置 |
| 429 | 请求频率限制 | 降低请求频率或升级套餐 |
| 500 | 服务端错误 | 重试或联系服务商 |

### 7.2 日志调试

```yaml
logging:
  level:
    net.ooder.skill.llm: DEBUG
```

---

## 八、变更记录

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2026-04-03 | v3.0.1 | 初始创建LLM配置指南 |
