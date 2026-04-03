# skill-llm-qianwen

通义千问LLM驱动服务，支持qwen-turbo、qwen-plus、qwen-max等模型。

## 功能特性

- **对话补全** - 多轮对话支持
- **长文本理解** - 支持超长上下文
- **流式输出** - SSE流式响应
- **Function Calling** - 函数调用支持
- **向量嵌入** - 文本向量生成

## 支持模型

| 模型ID | 名称 | 最大Token | 说明 |
|--------|------|-----------|------|
| qwen-turbo | 通义千问Turbo | 8192 | 高速模型 |
| qwen-plus | 通义千问Plus | 32768 | 增强模型 |
| qwen-max | 通义千问Max | 32768 | 旗舰模型 |
| qwen-long | 通义千问Long | 10000000 | 长文本模型 |

## 快速开始

### 安装

```bash
skill install skill-llm-qianwen
```

### 配置

```yaml
skill-llm-qianwen:
  api-key: ${QIANWEN_API_KEY}
  base-url: https://dashscope.aliyuncs.com/api/v1
  model: qwen-plus
  max-tokens: 8192
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
    "model": "qwen-plus"
  }'
```

## 配置项

| 配置项 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| apiKey | string | 是 | - | 阿里云API Key |
| baseUrl | string | 否 | https://dashscope.aliyuncs.com/api/v1 | API基础URL |
| model | string | 否 | qwen-plus | 默认模型 |
| maxTokens | int | 否 | 8192 | 最大Token数 |
| temperature | float | 否 | 0.7 | 温度参数 |

## 安全配置

支持API Key加密存储：

```yaml
skill-llm-qianwen:
  api-key-encrypted: ${QIANWEN_API_KEY_ENCRYPTED}
  encryption-key: ${ENCRYPTION_KEY}
```

## 许可证

Apache-2.0
