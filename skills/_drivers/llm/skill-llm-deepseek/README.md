# skill-llm-deepseek

DeepSeek LLM驱动服务，支持deepseek-chat、deepseek-coder、deepseek-reasoner等模型。

## 功能特性

- **对话补全** - 多轮对话支持
- **代码生成** - 代码生成与补全
- **推理能力** - 复杂推理任务(DeepSeek-R1)
- **流式输出** - SSE流式响应
- **Function Calling** - 函数调用支持

## 支持模型

| 模型ID | 名称 | 最大Token | 说明 |
|--------|------|-----------|------|
| deepseek-chat | DeepSeek Chat | 64000 | 通用对话模型 |
| deepseek-coder | DeepSeek Coder | 16000 | 代码专用模型 |
| deepseek-reasoner | DeepSeek R1 | 64000 | 推理增强模型 |

## 快速开始

### 安装

```bash
skill install skill-llm-deepseek
```

### 配置

```yaml
skill-llm-deepseek:
  api-key: ${DEEPSEEK_API_KEY}
  base-url: https://api.deepseek.com/v1
  model: deepseek-chat
  max-tokens: 64000
  temperature: 0.7
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {"role": "user", "content": "你好"}
    ],
    "model": "deepseek-chat"
  }'
```

## API端点

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/llm/chat | POST | 对话补全 |
| /api/llm/stream | POST | 流式对话 |
| /api/llm/models | GET | 获取模型列表 |

## 配置项

| 配置项 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| apiKey | string | 是 | - | DeepSeek API Key |
| baseUrl | string | 否 | https://api.deepseek.com/v1 | API基础URL |
| model | string | 否 | deepseek-chat | 默认模型 |
| maxTokens | int | 否 | 64000 | 最大Token数 |
| temperature | float | 否 | 0.7 | 温度参数 |

## 能力声明

```yaml
capabilities:
  - id: chat-completion
    name: 对话补全
    autoBind: true
  - id: code-generation
    name: 代码生成
    autoBind: true
  - id: reasoning
    name: 推理能力
    autoBind: false
  - id: function-calling
    name: 函数调用
    autoBind: false
  - id: streaming
    name: 流式输出
    autoBind: false
```

## 许可证

Apache-2.0
