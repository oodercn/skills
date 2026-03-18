# skill-media-xiaohongshu

小红书内容发布服务，支持笔记发布、图片上传、标签管理

## 功能特性

- 笔记发布 - 发布图文笔记到小红书
- 图片上传 - 上传多张图片
- 标签管理 - 添加话题标签
- 数据统计 - 获取笔记互动数据

## 快速开始

### 安装

```bash
skill install skill-media-xiaohongshu
```

### 配置

```yaml
skill-media-xiaohongshu:
  app-id: ${XHS_APP_ID}
  app-secret: ${XHS_APP_SECRET}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/media/xiaohongshu/notes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "笔记标题",
    "content": "笔记内容",
    "images": ["url1", "url2"],
    "tags": ["标签1", "标签2"]
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [API映射](docs/api-mapping.md)
- [集成指南](docs/integration-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| XHS_APP_ID | string | 是 | 小红书应用ID |
| XHS_APP_SECRET | string | 是 | 小红书应用密钥 |

## 许可证

Apache-2.0
