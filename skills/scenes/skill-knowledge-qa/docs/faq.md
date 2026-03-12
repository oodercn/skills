# 常见问题

## 通用问题

### Q: 支持哪些文档格式？

A: 目前支持以下格式：
- PDF (.pdf)
- Word (.docx)
- Markdown (.md)
- 纯文本 (.txt)

### Q: 知识库有大小限制吗？

A: 默认单个文档最大10MB，可通过配置调整：
```yaml
skill-knowledge-qa:
  max-document-size: 52428800  # 50MB
```

### Q: 如何提高检索准确率？

A: 
1. 使用混合检索策略
2. 调整分块大小和重叠
3. 启用重排序功能
4. 使用更好的嵌入模型

## 文档处理

### Q: 文档处理需要多长时间？

A: 取决于文档大小和内容复杂度：
- 小文档(<1MB): 通常几秒
- 中等文档(1-10MB): 通常几十秒
- 大文档(>10MB): 可能需要几分钟

### Q: 如何处理扫描版PDF？

A: 启用OCR功能：
```yaml
skill-knowledge-qa:
  document:
    ocr-enabled: true
```

### Q: 文档更新后如何重新索引？

A: 
1. 删除旧文档，重新上传
2. 或调用重新索引接口：
```bash
POST /api/knowledge/bases/{kbId}/reindex
```

## 检索相关

### Q: 关键词检索和语义检索有什么区别？

A: 
- **关键词检索(BM25)**: 基于词汇匹配，适合精确查询
- **语义检索**: 基于向量相似度，理解语义含义
- **混合检索**: 结合两者优势，效果最好

### Q: 如何调整检索返回数量？

A: 
```yaml
skill-knowledge-qa:
  search-top-k: 20  # 返回20条结果
```

或在请求中指定：
```json
{
  "query": "查询内容",
  "topK": 20
}
```

### Q: 如何过滤低质量结果？

A: 设置相似度阈值：
```yaml
skill-knowledge-qa:
  vector:
    score-threshold: 0.7  # 只返回分数>0.7的结果
```

## RAG问答

### Q: RAG回答不准确怎么办？

A: 
1. 检查知识库内容是否完整
2. 增加检索数量(topK)
3. 调整上下文长度
4. 使用更好的LLM模型

### Q: 如何查看回答的来源？

A: RAG接口会返回sources字段：
```json
{
  "answer": "回答内容",
  "sources": [
    {
      "docId": "doc-001",
      "docName": "文档名",
      "content": "来源片段"
    }
  ]
}
```

### Q: 支持流式输出吗？

A: 支持，在请求中设置：
```json
{
  "question": "问题",
  "stream": true
}
```

## 性能问题

### Q: 如何提高索引速度？

A: 
1. 增加索引线程池
2. 使用更快的嵌入模型
3. 减小分块大小

### Q: 如何提高检索速度？

A: 
1. 启用缓存
2. 增加搜索线程池
3. 使用本地嵌入模型

### Q: 内存占用过高怎么办？

A: 
1. 减小缓存大小
2. 减小分块大小
3. 定期清理过期数据

## 更多问题

如有其他问题，请：
- 查看 [故障排查](troubleshooting.md)
- 提交 [Issue](https://gitee.com/ooderCN/ooder-skills/issues)
