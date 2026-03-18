# skill-llm-volcengine

火山引擎LLM驱动服务，支持豆包系列模型对话补全、向量嵌入

## 功能特性

- 对话补全 - 多轮对话支持
- 流式输出 - SSE流式响应
- 向量嵌入 - 文本向量生成
- Function Calling - 函数调用支持

## 支持模型

| 模型ID | 名称 | 最大Token |
|--------|------|-----------|
| doubao-pro-32k | 豆包Pro 32K | 32768 |
| doubao-pro-128k | 豆包Pro 128K | 131072 |
| doubao-lite-32k | 豆包Lite 32K | 32768 |

## 快速开始

### 安装

```bash
skill install skill-llm-volcengine
```

### 配置

```yaml
skill-llm-volcengine:
  api-key: ${VOLCENGINE_API_KEY}
  endpoint-id: ${VOLCENGINE_ENDPOINT_ID}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {"role": "user", "content": "你好"}
    ],
    "model": "doubao-pro-32k"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [模型指南](docs/model-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| VOLCENGINE_API_KEY | string | 是 | 火山引擎API Key |
| VOLCENGINE_ENDPOINT_ID | string | 是 | 推理接入点ID |
| DEFAULT_MODEL | string | 否 | 默认模型 |

## 许可证

Apache-2.0
