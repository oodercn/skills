# User Authentication Service

## 概述

用户认证服务，支持多种认证方式

## 版本

当前版本: 0.7.1

## 能力

| 能力ID | 名称 | 描述 |
|--------|------|------|
| user-auth | User Authentication | 用户登录认证 |
| token-validate | Token Validation | 验证Token有效性 |
| session-manage | Session Management | 会话管理 |

## 配置参数

### 可选参数

| 参数名 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| AUTH_TOKEN_EXPIRE | number | 3600 | Token过期时间(秒) |
| AUTH_REFRESH_ENABLED | boolean | true | 是否启用Token刷新 |

## API端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/info | GET | 获取技能信息 |
| /api/login | POST | 用户登录 |
| /api/validate | POST | 验证Token |
| /api/logout | POST | 用户登出 |
| /api/health | GET | 健康检查 |

## 使用示例

```bash
# 安装技能
ooder skill install skill-user-auth

# 启动技能
ooder skill start skill-user-auth

# 用户登录
curl -X POST http://localhost:8082/api/login -d '{"username":"admin","password":"123456"}'
```

## 依赖

- SDK版本: >=0.7.0
- Java版本: >=8

## 许可证

Apache-2.0

## 作者

Ooder Team
