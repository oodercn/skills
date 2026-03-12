# skill-llm-chat

LLM智能对话场景能力，支持多轮对话、上下文感知、流式输出

## 功能特性

- 多轮对话 - 支持上下文感知的多轮对话
- 流式输出 - SSE流式响应，实时输出
- 会话管理 - 完整的会话生命周期管理
- 上下文提取 - 自动提取页面上下文增强对话
- 多Provider支持 - 支持OpenAI、百度、阿里等主流LLM
- Function Calling - 支持LLM函数调用能力

## 快速开始

### 安装

```bash
skill install skill-llm-chat
```

### 配置

```yaml
skill-llm-chat:
  enabled: true
  default-provider: baidu
  default-model: ernie-bot-4
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "你好，请介绍一下你自己",
    "sessionId": "test-session"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [API参考](docs/api-reference.md)
- [配置指南](docs/configuration.md)
- [故障排查](docs/troubleshooting.md)
- [常见问题](docs/faq.md)

## 依赖

- skill-llm-conversation (必需) - LLM对话服务
- skill-llm-context-builder (必需) - 上下文构建服务
- skill-llm-config-manager (可选) - LLM配置管理

## 许可证

Apache-2.0
