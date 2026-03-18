# skill-document-processor

文档处理工具服务，支持文档解析、分块、内容提取

## 功能特性

- 文档解析 - 解析PDF、Word等格式
- 内容分块 - 文档内容智能分块
- 内容提取 - 提取文档关键内容
- 格式转换 - 文档格式转换

## 快速开始

### 安装

```bash
skill install skill-document-processor
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/document/chunk \
  -F "file=@document.pdf" \
  -F "chunkSize=500"
```

## 许可证

Apache-2.0
