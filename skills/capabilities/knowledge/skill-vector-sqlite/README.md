# skill-vector-sqlite

向量存储服务，基于SQLite的向量数据库

## 功能特性

- 向量存储 - 存储文本向量
- 相似度检索 - 向量相似度搜索
- 本地部署 - 轻量级本地存储
- 嵌入服务 - 文本嵌入生成

## 快速开始

### 安装

```bash
skill install skill-vector-sqlite
```

### 配置

```yaml
skill-vector-sqlite:
  db-path: ./data/vectors.db
  embedding-model: text-embedding-3-small
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/vector/store \
  -H "Content-Type: application/json" \
  -d '{
    "id": "doc-001",
    "text": "这是一段文本",
    "metadata": {"source": "doc.pdf"}
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [向量操作](docs/vector-ops.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| DB_PATH | string | 否 | 数据库路径 |
| EMBEDDING_MODEL | string | 否 | 嵌入模型 |

## 许可证

Apache-2.0
