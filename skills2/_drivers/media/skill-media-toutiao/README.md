# skill-media-toutiao

头条内容发布服务，支持文章发布、更新、删除、统计

## 功能特性

- 文章发布 - 发布文章到今日头条
- 文章更新 - 更新已发布文章
- 文章删除 - 删除已发布文章
- 数据统计 - 获取文章阅读、点赞、评论等数据

## 快速开始

### 安装

```bash
skill install skill-media-toutiao
```

### 配置

```yaml
skill-media-toutiao:
  app-id: ${TOUTIAO_APP_ID}
  app-secret: ${TOUTIAO_APP_SECRET}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/media/toutiao/articles \
  -H "Content-Type: application/json" \
  -d '{
    "title": "文章标题",
    "content": "文章内容",
    "cover": "https://example.com/cover.jpg"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [API映射](docs/api-mapping.md)
- [集成指南](docs/integration-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| TOUTIAO_APP_ID | string | 是 | 头条应用ID |
| TOUTIAO_APP_SECRET | string | 是 | 头条应用密钥 |

## 许可证

Apache-2.0
