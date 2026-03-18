# skill-media-weibo

微博内容发布服务，支持微博发布、图片上传、话题管理

## 功能特性

- 微博发布 - 发布文字、图片微博
- 图片上传 - 上传图片到微博图床
- 话题管理 - 参与热门话题
- 数据统计 - 获取微博互动数据

## 快速开始

### 安装

```bash
skill install skill-media-weibo
```

### 配置

```yaml
skill-media-weibo:
  app-key: ${WEIBO_APP_KEY}
  app-secret: ${WEIBO_APP_SECRET}
  access-token: ${WEIBO_ACCESS_TOKEN}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/media/weibo/statuses \
  -H "Content-Type: application/json" \
  -d '{
    "status": "微博内容 #话题#"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [错误码](docs/error-codes.md)
- [集成指南](docs/integration-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| WEIBO_APP_KEY | string | 是 | 微博应用Key |
| WEIBO_APP_SECRET | string | 是 | 微博应用Secret |
| WEIBO_ACCESS_TOKEN | string | 是 | 授权Token |

## 许可证

Apache-2.0
