# skill-llm-openai

OpenAI LLM驱动服务，支持GPT-4、GPT-3.5等模型。

## 功能特性

- **对话补全** - 多轮对话支持
- **代码生成** - 代码生成与补全
- **流式输出** - SSE流式响应
- **Function Calling** - 函数调用支持
- **Vision** - 图像理解(GPT-4 Vision)
- **JSON Mode** - 结构化输出

## 支持模型

| 模型ID | 名称 | 最大Token | 说明 |
|--------|------|-----------|------|
| gpt-4 | GPT-4 | 8192 | 旗舰模型 |
| gpt-4-turbo | GPT-4 Turbo | 128000 | 高速版本 |
| gpt-4o | GPT-4o | 128000 | 多模态模型 |
| gpt-3.5-turbo | GPT-3.5 Turbo | 16384 | 高性价比模型 |

## 快速开始

### 安装

```bash
skill install skill-llm-openai
```

### 配置

```yaml
skill-llm-openai:
  api-key: ${OPENAI_API_KEY}
  base-url: https://api.openai.com/v1
  model: gpt-4o
  max-tokens: 4096
  temperature: 0.7
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {"role": "user", "content": "Hello"}
    ],
    "model": "gpt-4o"
  }'
```

## 配置项

| 配置项 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| apiKey | string | 是 | - | OpenAI API Key |
| baseUrl | string | 否 | https://api.openai.com/v1 | API基础URL |
| model | string | 否 | gpt-4o | 默认模型 |
| maxTokens | int | 否 | 4096 | 最大Token数 |
| temperature | float | 否 | 0.7 | 温度参数 |

## 安全配置

支持API Key加密存储：

```yaml
skill-llm-openai:
  api-key-encrypted: ${OPENAI_API_KEY_ENCRYPTED}
  encryption-key: ${ENCRYPTION_KEY}
```

## 许可证

Apache-2.0
