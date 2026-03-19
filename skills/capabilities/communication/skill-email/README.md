# skill-email

邮件服务，支持邮件发送、接收、模板管理

## 功能特性

- 邮件发送 - 发送文本、HTML邮件
- 邮件接收 - 接收和处理邮件
- 模板管理 - 邮件模板创建和管理
- 附件支持 - 支持发送附件

## 快速开始

### 安装

```bash
skill install skill-email
```

### 配置

```yaml
skill-email:
  smtp-host: smtp.example.com
  smtp-port: 587
  smtp-username: ${SMTP_USERNAME}
  smtp-password: ${SMTP_PASSWORD}
```

### 使用示例

```bash
curl -X POST http://localhost:8080/api/email/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "user@example.com",
    "subject": "测试邮件",
    "content": "这是一封测试邮件"
  }'
```

## 文档目录

- [快速开始](docs/quick-start.md)
- [SMTP配置](docs/smtp-config.md)
- [模板指南](docs/template-guide.md)

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| SMTP_HOST | string | 是 | SMTP服务器地址 |
| SMTP_PORT | integer | 是 | SMTP端口 |
| SMTP_USERNAME | string | 是 | SMTP用户名 |
| SMTP_PASSWORD | string | 是 | SMTP密码 |

## 许可证

Apache-2.0
