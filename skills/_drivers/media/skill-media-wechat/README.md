# skill-media-wechat

微信公众号内容发布服务，支持图文发布、素材管理、用户管理

## 功能特性

- 图文发布 - 发布图文消息到公众号
- 素材管理 - 管理图片、视频等素材
- 用户管理 - 获取用户信息、标签管理
- 菜单管理 - 自定义菜单配置

## 快速开始

### 安装

```bash
skill install skill-media-wechat
```

### 配置

```yaml
skill-media-wechat:
  app-id: ${WECHAT_APP_ID}
  app-secret: ${WECHAT_APP_SECRET}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/media/wechat/articles \
  -H "Content-Type: application/json" \
  -d '{
    "title": "文章标题",
    "content": "文章内容",
    "thumbMediaId": "media_id"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [认证指南](docs/auth-guide.md)
- [集成指南](docs/integration-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| WECHAT_APP_ID | string | 是 | 公众号AppID |
| WECHAT_APP_SECRET | string | 是 | 公众号AppSecret |

## 许可证

Apache-2.0
