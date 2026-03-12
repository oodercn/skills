# skill-knowledge-qa

知识问答场景能力，支持知识库管理、文档管理、语义检索、RAG智能问答

## 功能特性

- 知识库管理 - 创建、更新、删除知识库
- 文档管理 - 上传、解析、索引文档
- 语义检索 - BM25关键词检索 + 向量语义检索
- RAG智能问答 - 检索增强生成，精准回答
- 多格式支持 - PDF、Word、Markdown等
- 离线支持 - 本地缓存，断网可用

## 快速开始

### 安装

```bash
skill install skill-knowledge-qa
```

### 配置

```yaml
skill-knowledge-qa:
  enabled: true
  storage-path: ./data/kb
  embedding-model: text-embedding-3-small
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/knowledge/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "如何配置LLM？",
    "topK": 5
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [API参考](docs/api-reference.md)
- [配置指南](docs/configuration.md)
- [故障排查](docs/troubleshooting.md)
- [常见问题](docs/faq.md)

## 依赖

- skill-knowledge-base (必需) - 知识库核心服务
- skill-rag (可选) - RAG检索增强
- skill-vector-sqlite (可选) - 向量存储服务

## 许可证

Apache-2.0
