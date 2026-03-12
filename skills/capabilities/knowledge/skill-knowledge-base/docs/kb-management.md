# 知识库管理

## 创建知识库

```bash
curl -X POST http://localhost:8080/api/v1/kb \
  -H "Content-Type: application/json" \
  -d '{
    "name": "技术文档库",
    "description": "技术文档和API参考"
  }'
```

## 知识库结构

每个知识库包含：

- 元数据 (名称、描述、创建时间)
- 文档集合
- 索引数据 (BM25索引、向量索引)

## 文档管理

### 上传文档

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/documents \
  -F "file=@document.pdf"
```

### 支持的文档格式

- PDF (.pdf)
- Word (.docx)
- Markdown (.md)
- 纯文本 (.txt)

### 文档处理流程

```
上传 -> 解析 -> 分块 -> 嵌入 -> 索引
```

### 检查处理状态

```bash
curl http://localhost:8080/api/v1/kb/{kbId}/documents/{docId}/status
```

## 知识库维护

### 重建索引

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/reindex
```

### 清理无效文档

```bash
curl -X POST http://localhost:8080/api/v1/kb/{kbId}/cleanup
```

## 知识库权限

知识库支持以下权限级别：

- 只读: 只能检索
- 读写: 可上传文档
- 管理: 可删除文档和知识库

## 最佳实践

1. **合理命名**: 使用清晰的知识库名称
2. **分类管理**: 按主题或部门分类
3. **定期维护**: 清理过期文档
4. **监控容量**: 关注存储空间使用
