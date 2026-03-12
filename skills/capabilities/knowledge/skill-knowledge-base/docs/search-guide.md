# 检索指南

## 检索类型

### 1. 关键词检索 (BM25)

基于词汇匹配的检索方式，适合精确查询。

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "配置文件",
    "limit": 10
  }'
```

**特点**:
- 速度快
- 精确匹配
- 适合专业术语

### 2. 语义检索

基于向量相似度的检索方式，理解语义含义。

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/search/semantic \
  -H "Content-Type: application/json" \
  -d '{
    "query": "如何修改系统设置",
    "limit": 10,
    "threshold": 0.7
  }'
```

**特点**:
- 语义理解
- 同义词匹配
- 效果依赖嵌入模型

### 3. 混合检索

结合关键词和语义检索的优势。

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/search/hybrid \
  -H "Content-Type: application/json" \
  -d '{
    "query": "系统配置方法",
    "limit": 10
  }'
```

**特点**:
- 综合效果最好
- 兼顾精确和语义

## 检索参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| query | 检索语句 | 必填 |
| limit | 返回数量 | 10 |
| threshold | 相似度阈值 | 0.7 |
| offset | 偏移量 | 0 |

## 检索结果

```json
{
  "results": [
    {
      "docId": "doc-001",
      "docName": "配置指南.pdf",
      "content": "相关内容片段...",
      "score": 0.95,
      "metadata": {
        "page": 5,
        "chunk": 12
      }
    }
  ],
  "total": 25
}
```

## 跨库检索

同时检索多个知识库：

```bash
curl -X POST http://localhost:8080/api/v1/kb/search/cross \
  -H "Content-Type: application/json" \
  -d '{
    "query": "系统配置",
    "kbIds": ["kb-001", "kb-002"],
    "limit": 20
  }'
```

## 检索优化

### 1. 调整分块大小

```yaml
skill-knowledge-base:
  chunk-size: 300    # 更小的分块，更精确
  chunk-overlap: 50
```

### 2. 调整相似度阈值

```json
{
  "query": "查询内容",
  "threshold": 0.8  # 提高阈值，过滤低质量结果
}
```

### 3. 使用更好的嵌入模型

```yaml
skill-knowledge-base:
  embedding-model: text-embedding-3-large
```

## 最佳实践

1. **选择合适的检索类型**: 精确查询用关键词，语义理解用语义检索
2. **优化查询语句**: 使用清晰、具体的描述
3. **合理设置阈值**: 平衡召回率和准确率
4. **监控检索效果**: 分析用户反馈，持续优化
