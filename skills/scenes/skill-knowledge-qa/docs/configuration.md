# 配置指南

## 配置概述

skill-knowledge-qa 支持以下配置方式：

1. 配置文件 (`application.yml`)
2. 环境变量
3. 配置中心 (Nacos/Apollo)

## 基础配置

### 启用Skill

```yaml
skill-knowledge-qa:
  enabled: true
```

### 核心配置

```yaml
skill-knowledge-qa:
  storage-path: ./data/kb           # 知识库存储路径
  max-document-size: 10485760       # 最大文档大小(字节)
  search-top-k: 10                  # 检索返回数量
  embedding-model: text-embedding-3-small  # 嵌入模型
```

## 向量检索配置

```yaml
skill-knowledge-qa:
  vector:
    enabled: true
    embedding-model: text-embedding-3-small
    chunk-size: 500
    chunk-overlap: 50
    score-threshold: 0.7
```

### 嵌入模型选择

| 模型 | 维度 | 说明 |
|------|------|------|
| text-embedding-3-small | 1536 | OpenAI小模型，性价比高 |
| text-embedding-3-large | 3072 | OpenAI大模型，效果更好 |
| text-embedding-ada-002 | 1536 | OpenAI旧版模型 |

## 文档处理配置

```yaml
skill-knowledge-qa:
  document:
    supported-formats:
      - pdf
      - docx
      - md
      - txt
    chunk-size: 500
    chunk-overlap: 50
    extract-images: false
    ocr-enabled: false
```

## RAG配置

```yaml
skill-knowledge-qa:
  rag:
    enabled: true
    retrieval-strategy: hybrid     # keyword, semantic, hybrid
    top-k: 5
    rerank-enabled: false
    context-max-tokens: 2000
```

### 检索策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| keyword | BM25关键词检索 | 精确匹配 |
| semantic | 向量语义检索 | 语义理解 |
| hybrid | 混合检索 | 综合效果最好 |

## 存储配置

### 本地存储

```yaml
skill-knowledge-qa:
  storage:
    type: local
    path: ./data/kb
```

### 数据库存储

```yaml
skill-knowledge-qa:
  storage:
    type: database
    
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ooder_kb
    username: root
    password: ${DB_PASSWORD}
```

## 高级配置

### 性能调优

```yaml
skill-knowledge-qa:
  performance:
    index-thread-pool: 4
    search-thread-pool: 8
    cache-size: 1000
    cache-ttl: 3600
```

### 离线支持

```yaml
skill-knowledge-qa:
  offline:
    enabled: true
    cache-strategy: local
    sync-on-reconnect: true
```

## 配置示例

### 开发环境

```yaml
skill-knowledge-qa:
  enabled: true
  storage-path: ./data/kb-dev
  vector:
    enabled: true
    embedding-model: text-embedding-3-small
  document:
    chunk-size: 300
```

### 生产环境

```yaml
skill-knowledge-qa:
  enabled: true
  storage-path: /data/kb
  max-document-size: 52428800
  
  vector:
    enabled: true
    embedding-model: text-embedding-3-large
    chunk-size: 500
    chunk-overlap: 50
    
  rag:
    enabled: true
    retrieval-strategy: hybrid
    top-k: 10
    rerank-enabled: true
    
  performance:
    index-thread-pool: 8
    search-thread-pool: 16
    cache-size: 5000
```

## 配置项参考

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| storage-path | string | ./data/kb | 知识库存储路径 |
| max-document-size | integer | 10485760 | 最大文档大小 |
| search-top-k | integer | 10 | 检索返回数量 |
| embedding-model | string | text-embedding-3-small | 嵌入模型 |
| vector.enabled | boolean | true | 启用向量检索 |
| vector.chunk-size | integer | 500 | 文档分块大小 |
| rag.retrieval-strategy | string | hybrid | 检索策略 |
