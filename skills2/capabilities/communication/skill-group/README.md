# skill-group

群组管理服务，支持群组创建、成员管理

## 功能特性

- 群组创建 - 创建群组
- 成员管理 - 添加、移除成员
- 权限管理 - 群组权限设置
- 群组搜索 - 搜索群组

## 快速开始

### 安装

```bash
skill install skill-group
```

### 配置

```yaml
skill-group:
  max-group-size: 500
  enable-search: true
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "项目组",
    "description": "项目讨论群"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [群组管理](docs/group-management.md)

## 许可证

Apache-2.0
