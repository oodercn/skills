# skill-document-assistant

文档助手场景服务，支持文档生成、摘要、翻译

## 功能特性

- 文档生成 - 智能生成文档
- 文档摘要 - 自动生成摘要
- 文档翻译 - 多语言翻译
- 格式转换 - 文档格式转换

## 快速开始

### 安装

```bash
skill install skill-document-assistant
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/document/summarize \
  -H "Content-Type: application/json" \
  -d '{
    "content": "长文档内容..."
  }'
```

## 许可证

Apache-2.0
