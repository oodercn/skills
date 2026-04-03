# skill-llm-baidu

百度文心LLM驱动服务，支持ERNIE系列模型。

## 功能特性

- **对话补全** - 多轮对话支持
- **流式输出** - SSE流式响应
- **Function Calling** - 函数调用支持
- **向量嵌入** - 文本向量生成

## 支持模型

| 模型ID | 名称 | 最大Token | 说明 |
|--------|------|-----------|------|
| ernie-4.0-8k | ERNIE 4.0 | 8192 | 旗舰模型 |
| ernie-3.5-8k | ERNIE 3.5 | 8192 | 高性价比模型 |
| ernie-speed-8k | ERNIE Speed | 8192 | 高速模型 |

## 快速开始

### 安装

```bash
skill install skill-llm-baidu
```

### 配置

```yaml
skill-llm-baidu:
  api-key: ${BAIDU_API_KEY}
  secret-key: ${BAIDU_SECRET_KEY}
  model: ernie-3.5-8k
  max-tokens: 4096
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
    "model": "ernie-3.5-8k"
  }'
```

## 配置项

| 配置项 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| apiKey | string | 是 | - | 百度API Key |
| secretKey | string | 是 | - | 百度Secret Key |
| model | string | 否 | ernie-3.5-8k | 默认模型 |
| maxTokens | int | 否 | 4096 | 最大Token数 |
| temperature | float | 否 | 0.7 | 温度参数 |

## 许可证

Apache-2.0
