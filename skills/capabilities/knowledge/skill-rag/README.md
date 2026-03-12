# skill-rag

RAG检索增强生成服务 - 支持关键词检索、语义检索、混合检索策略，知识库注册与Prompt构建

## 功能特性

- 多种检索策略 - 关键词、语义、混合检索
- Prompt构建 - 构建带检索上下文的Prompt
- 知识库注册 - 注册外部知识库
- 灵活配置 - 可配置检索参数和策略

## 快速开始

### 安装

```bash
skill install skill-rag
```

### 配置

```yaml
skill-rag:
  enabled: true
  default-strategy: HYBRID
  max-results: 10
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/v1/rag/retrieve \
  -H "Content-Type: application/json" \
  -d '{
    "query": "如何配置系统？",
    "strategy": "HYBRID",
    "maxResults": 5
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [API参考](docs/api-reference.md)
- [配置指南](docs/configuration.md)
- [检索策略](docs/retrieval-guide.md)

## 依赖

- skill-knowledge-base (可选) - 知识库服务
- skill-vector-sqlite (可选) - 向量存储服务

## 许可证

Apache-2.0
