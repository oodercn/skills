# skill-local-knowledge

本地知识库服务，提供本地知识存储和检索

## 功能特性

- 本地存储 - 知识本地持久化
- 快速检索 - 本地快速搜索
- 离线支持 - 完全离线可用
- 轻量部署 - 无需外部依赖

## 快速开始

### 安装

```bash
skill install skill-local-knowledge
```

### 配置

```yaml
skill-local-knowledge:
  storage-path: ./data/local-kb
  max-documents: 10000
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/local-kb/documents \
  -F "file=@document.pdf"
```

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| STORAGE_PATH | string | 否 | 存储路径 |
| MAX_DOCUMENTS | integer | 否 | 最大文档数 |

## 许可证

Apache-2.0
