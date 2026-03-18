# skill-project-knowledge

项目知识场景服务，支持项目知识管理、文档归档

## 功能特性

- 知识管理 - 项目知识分类管理
- 文档归档 - 项目文档归档存储
- 知识检索 - 项目知识快速检索
- 权限控制 - 项目知识访问权限

## 快速开始

### 安装

```bash
skill install skill-project-knowledge
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/project/knowledge \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": "PROJ001",
    "name": "技术文档",
    "content": "文档内容..."
  }'
```

## 许可证

Apache-2.0
