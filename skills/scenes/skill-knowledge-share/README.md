# skill-knowledge-share

知识分享场景服务，支持知识发布、分享、协作

## 功能特性

- 知识发布 - 发布知识内容
- 知识分享 - 分享给团队成员
- 协作编辑 - 多人协作编辑
- 版本管理 - 知识版本控制

## 快速开始

### 安装

```bash
skill install skill-knowledge-share
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/knowledge/share \
  -H "Content-Type: application/json" \
  -d '{
    "title": "技术分享",
    "content": "分享内容...",
    "recipients": ["user1", "user2"]
  }'
```

## 许可证

Apache-2.0
