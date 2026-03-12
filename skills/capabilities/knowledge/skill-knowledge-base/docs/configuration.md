# 配置指南

## 基础配置

```yaml
skill-knowledge-base:
  storage-path: ./data/kb
  max-document-size: 10485760
  search-limit: 10
  enable-vector-search: true
  embedding-model: text-embedding-3-small
  chunk-size: 500
  chunk-overlap: 50
```

## 存储配置

```yaml
skill-knowledge-base:
  storage-path: ./data/kb
  
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ooder_kb
    username: root
    password: ${DB_PASSWORD}
```

## 向量检索配置

```yaml
skill-knowledge-base:
  enable-vector-search: true
  embedding-model: text-embedding-3-small
  chunk-size: 500
  chunk-overlap: 50
```

### 嵌入模型选择

| 模型 | 维度 | 说明 |
|------|------|------|
| text-embedding-3-small | 1536 | 性价比高 |
| text-embedding-3-large | 3072 | 效果更好 |
| text-embedding-ada-002 | 1536 | 旧版模型 |

## 文档处理配置

```yaml
skill-knowledge-base:
  document:
    supported-formats:
      - pdf
      - docx
      - md
      - txt
    chunk-size: 500
    chunk-overlap: 50
```

## 性能配置

```yaml
skill-knowledge-base:
  performance:
    index-thread-pool: 4
    search-thread-pool: 8
    cache-size: 1000
```

## 配置项参考

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| storage-path | string | ./data/kb | 存储路径 |
| max-document-size | integer | 10485760 | 最大文档大小 |
| search-limit | integer | 10 | 检索结果限制 |
| enable-vector-search | boolean | true | 启用向量检索 |
| embedding-model | string | text-embedding-3-small | 嵌入模型 |
| chunk-size | integer | 500 | 分块大小 |
| chunk-overlap | integer | 50 | 分块重叠 |
