# skill-media-zhihu

知乎内容发布服务，支持文章发布、回答问题、专栏管理

## 功能特性

- 文章发布 - 发布文章到知乎专栏
- 回答问题 - 回答知乎问题
- 专栏管理 - 管理个人专栏
- 数据统计 - 获取文章互动数据

## 快速开始

### 安装

```bash
skill install skill-media-zhihu
```

### 配置

```yaml
skill-media-zhihu:
  client-id: ${ZHIHU_CLIENT_ID}
  client-secret: ${ZHIHU_CLIENT_SECRET}
  access-token: ${ZHIHU_ACCESS_TOKEN}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/media/zhihu/articles \
  -H "Content-Type: application/json" \
  -d '{
    "title": "文章标题",
    "content": "文章内容",
    "columnId": "column_id"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [常见问题](docs/faq.md)
- [集成指南](docs/integration-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| ZHIHU_CLIENT_ID | string | 是 | 知乎应用Client ID |
| ZHIHU_CLIENT_SECRET | string | 是 | 知乎应用Client Secret |
| ZHIHU_ACCESS_TOKEN | string | 是 | 授权Token |

## 许可证

Apache-2.0
