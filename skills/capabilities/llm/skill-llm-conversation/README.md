# skill-llm-conversation

LLM对话服务 - 提供智能对话、会话管理、历史记录、流式输出、Function Calling支持

## 功能特性

- 智能对话 - 与LLM进行自然语言对话
- 会话管理 - 完整的会话生命周期管理
- 历史记录 - 对话历史持久化存储
- 流式输出 - SSE流式响应
- Function Calling - 支持LLM函数调用
- 多Provider - 支持多种LLM后端

## 快速开始

### 安装

```bash
skill install skill-llm-conversation
```

### 配置

```yaml
skill-llm-conversation:
  enabled: true
  max-history: 100
  session-timeout: 3600
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/llm/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "你好",
    "sessionId": "test-session"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [API参考](docs/api-reference.md)
- [配置指南](docs/configuration.md)
- [会话管理](docs/session-management.md)
- [流式输出](docs/streaming.md)

## 依赖

- skill-llm-context-builder (必需) - 上下文构建服务
- skill-llm-config-manager (可选) - LLM配置管理

## 许可证

Apache-2.0
