# skill-knowledge-base

知识库核心服务 - 提供知识库管理、文档管理、BM25检索、向量存储、语义检索能力

## 功能特性

- 知识库管理 - 创建、更新、删除知识库
- 文档管理 - 上传、解析、索引文档
- BM25检索 - 关键词检索
- 向量存储 - 文档向量嵌入与存储
- 语义检索 - 基于向量的语义相似度检索
- 混合检索 - 关键词+语义混合检索

## 快速开始

### 安装

```bash
skill install skill-knowledge-base
```

### 配置

```yaml
skill-knowledge-base:
  enabled: true
  storage-path: ./data/kb
  enable-vector-search: true
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/v1/kb \
  -H "Content-Type: application/json" \
  -d '{
    "name": "技术文档库",
    "description": "技术文档和API参考"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [API参考](docs/api-reference.md)
- [配置指南](docs/configuration.md)
- [知识库管理](docs/kb-management.md)
- [检索指南](docs/search-guide.md)

## 依赖

- skill-vector-sqlite (可选) - 向量存储服务
- skill-document-processor (可选) - 文档处理服务

## 许可证

Apache-2.0
