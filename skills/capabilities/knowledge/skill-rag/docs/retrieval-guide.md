# 检索策略

## 策略概述

skill-rag 支持三种检索策略：

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| KEYWORD | BM25关键词检索 | 精确匹配、专业术语 |
| SEMANTIC | 向量语义检索 | 语义理解、同义词 |
| HYBRID | 混合检索 | 综合效果最好 |

## 关键词检索 (KEYWORD)

基于BM25算法的关键词检索。

### 特点

- 速度快
- 精确匹配
- 适合专业术语

### 使用

```bash
curl -X POST http://localhost:8080/api/v1/rag/retrieve \
  -H "Content-Type: application/json" \
  -d '{
    "query": "配置文件路径",
    "strategy": "KEYWORD"
  }'
```

### 配置

```yaml
skill-rag:
  strategies:
    keyword:
      bm25-k1: 1.5
      bm25-b: 0.75
```

## 语义检索 (SEMANTIC)

基于向量相似度的语义检索。

### 特点

- 语义理解
- 同义词匹配
- 效果依赖嵌入模型

### 使用

```bash
curl -X POST http://localhost:8080/api/v1/rag/retrieve \
  -H "Content-Type: application/json" \
  -d '{
    "query": "如何修改系统设置",
    "strategy": "SEMANTIC"
  }'
```

### 配置

```yaml
skill-rag:
  strategies:
    semantic:
      embedding-model: text-embedding-3-large
      similarity-metric: cosine
```

## 混合检索 (HYBRID)

结合关键词和语义检索的优势。

### 特点

- 综合效果最好
- 兼顾精确和语义
- 可调整权重

### 使用

```bash
curl -X POST http://localhost:8080/api/v1/rag/retrieve \
  -H "Content-Type: application/json" \
  -d '{
    "query": "系统配置方法",
    "strategy": "HYBRID"
  }'
```

### 配置

```yaml
skill-rag:
  strategies:
    hybrid:
      keyword-weight: 0.3
      semantic-weight: 0.7
```

## 策略选择建议

1. **精确查询**: 使用KEYWORD策略
2. **语义理解**: 使用SEMANTIC策略
3. **通用场景**: 使用HYBRID策略（推荐）

## 高级配置

### 重排序

```yaml
skill-rag:
  rerank:
    enabled: true
    model: cross-encoder
    top-k: 20
    rerank-top-k: 5
```

### 多路召回

```yaml
skill-rag:
  multi-way:
    enabled: true
    ways:
      - strategy: KEYWORD
        weight: 0.3
      - strategy: SEMANTIC
        weight: 0.5
      - strategy: SEMANTIC
        embedding-model: text-embedding-3-large
        weight: 0.2
```
