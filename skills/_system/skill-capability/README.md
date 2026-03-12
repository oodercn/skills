# skill-capability

能力注册系统服务，管理所有Skill能力的注册与发现

## 功能特性

- 能力注册 - 注册Skill能力
- 能力发现 - 发现可用能力
- 能力元数据 - 管理能力元信息
- 能力分类 - 能力分类管理

## 快速开始

### 安装

```bash
skill install skill-capability
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/capabilities/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "email-send",
    "category": "communication",
    "description": "发送邮件"
  }'
```

## 许可证

Apache-2.0
