# 配置指南

## 基础配置

```yaml
skill-rag:
  default-strategy: HYBRID
  max-results: 10
  embedding-model: text-embedding-3-small
  score-threshold: 0.7
```

## 检索策略配置

```yaml
skill-rag:
  strategies:
    keyword:
      enabled: true
      bm25-k1: 1.5
      bm25-b: 0.75
      
    semantic:
      enabled: true
      embedding-model: text-embedding-3-small
      similarity-metric: cosine
      
    hybrid:
      enabled: true
      keyword-weight: 0.3
      semantic-weight: 0.7
```

## Prompt模板配置

```yaml
skill-rag:
  prompt:
    default-template: |
      你是一个专业的AI助手。请根据以下参考信息回答用户问题。
      
      参考信息：
      {context}
      
      用户问题：{query}
      
      请提供准确、有帮助的回答。
    
    max-context-tokens: 2000
    include-sources: true
```

## 性能配置

```yaml
skill-rag:
  performance:
    cache-enabled: true
    cache-size: 1000
    cache-ttl: 3600
    parallel-retrieval: true
```

## 配置项参考

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| default-strategy | string | HYBRID | 默认检索策略 |
| max-results | integer | 10 | 最大返回结果数 |
| embedding-model | string | text-embedding-3-small | 嵌入模型 |
| score-threshold | number | 0.7 | 相似度阈值 |
