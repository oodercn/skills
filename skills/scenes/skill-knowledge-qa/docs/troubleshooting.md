# 故障排查

## 常见问题

### 问题1: 文档上传失败

**现象**: 上传文档时返回错误

**原因**: 文档大小超限或格式不支持

**解决方案**:

1. 检查文档大小
```yaml
skill-knowledge-qa:
  max-document-size: 52428800  # 增加到50MB
```

2. 检查文档格式
```yaml
skill-knowledge-qa:
  document:
    supported-formats:
      - pdf
      - docx
      - md
      - txt
```

### 问题2: 检索结果不准确

**现象**: 检索返回的结果与查询不相关

**原因**: 检索策略或分块配置不当

**解决方案**:

1. 使用混合检索
```yaml
skill-knowledge-qa:
  rag:
    retrieval-strategy: hybrid
```

2. 调整分块大小
```yaml
skill-knowledge-qa:
  document:
    chunk-size: 300
    chunk-overlap: 50
```

3. 启用重排序
```yaml
skill-knowledge-qa:
  rag:
    rerank-enabled: true
```

### 问题3: 向量索引构建慢

**现象**: 文档上传后索引时间过长

**原因**: 嵌入模型调用慢或线程池配置不当

**解决方案**:

1. 增加线程池大小
```yaml
skill-knowledge-qa:
  performance:
    index-thread-pool: 8
```

2. 使用更快的嵌入模型
```yaml
skill-knowledge-qa:
  vector:
    embedding-model: text-embedding-3-small
```

### 问题4: 内存占用过高

**现象**: 服务内存持续增长

**原因**: 向量索引缓存过多

**解决方案**:

1. 限制缓存大小
```yaml
skill-knowledge-qa:
  performance:
    cache-size: 1000
```

2. 定期清理
```yaml
skill-knowledge-qa:
  performance:
    cache-ttl: 1800
```

### 问题5: 离线模式不可用

**现象**: 断网后无法使用知识库

**原因**: 离线配置未启用

**解决方案**:

```yaml
skill-knowledge-qa:
  offline:
    enabled: true
    cache-strategy: local
    sync-on-reconnect: true
```

## 日志分析

### 日志位置

```
logs/skill-knowledge-qa.log
```

### 关键日志关键字

| 关键字 | 说明 |
|--------|------|
| DOC_UPLOAD | 文档上传 |
| DOC_INDEX | 文档索引 |
| KB_SEARCH | 知识检索 |
| RAG_QUERY | RAG查询 |
| ERROR | 错误信息 |

### 日志级别配置

```yaml
logging:
  level:
    net.ooder.skill.knowledge: DEBUG
```

## 调试模式

```yaml
logging:
  level:
    net.ooder.skill.knowledge: DEBUG
    
skill-knowledge-qa:
  debug:
    enabled: true
    log-retrieval: true
    log-chunks: true
```

## 健康检查

```bash
curl http://localhost:8080/actuator/health
```

## 获取帮助

- 查看 [FAQ](faq.md)
- 提交 [Issue](https://gitee.com/ooderCN/ooder-skills/issues)
