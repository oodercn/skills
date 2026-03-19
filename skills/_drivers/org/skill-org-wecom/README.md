# Skill Org WeCom - 企业微信组织服务

## 概述

企业微信组织服务技能，提供企业微信组织架构同步和用户认证能力。

## 功能特性

- **组织架构同步**：同步企业微信的部门和用户信息
- **用户认证**：支持企业微信用户认证
- **部门管理**：查询和管理企业微信部门结构
- **用户管理**：查询和管理企业微信用户信息

## 配置参数

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| WECOM_CORP_ID | string | 是 | - | 企业微信CorpId |
| WECOM_AGENT_ID | string | 是 | - | 企业微信AgentId |
| WECOM_SECRET | string | 是 | - | 企业微信Secret |
| WECOM_API_BASE_URL | string | 否 | https://qyapi.weixin.qq.com | 企业微信API基础URL |
| WECOM_TIMEOUT | number | 否 | 30000 | 请求超时时间(毫秒) |
| WECOM_CACHE_ENABLED | boolean | 否 | true | 是否启用缓存 |
| WECOM_CACHE_TTL | number | 否 | 300000 | 缓存过期时间(毫秒) |

## 能力列表

- `org-data-read`：组织数据读取
- `org-data-sync`：组织数据同步
- `user-auth`：用户认证
- `department-query`：部门查询
- `user-query`：用户查询

## 场景关联

- `auth`：认证场景

## 快速开始

### 1. 配置企业微信应用

在企业微信管理后台创建应用，获取CorpId、AgentId和Secret。

### 2. 配置技能参数

```yaml
wecom:
  corp-id: your-corp-id
  agent-id: your-agent-id
  secret: your-secret
  api-base-url: https://qyapi.weixin.qq.com
  timeout: 30000
  cache-enabled: true
  cache-ttl: 300000
```

### 3. 启动服务

```bash
java -jar skill-org-wecom-0.7.3.jar
```

## API接口

### 获取部门列表

```
GET /api/org/departments
```

### 获取部门用户

```
GET /api/org/departments/{deptId}/users
```

### 获取用户信息

```
GET /api/org/users/{userId}
```

### 用户认证

```
POST /api/auth/verify
{
  "userId": "user-id",
  "password": "password"
}
```

## 技术栈

- Java 8
- Spring Boot 2.7.0
- 企业微信API

## 版本历史

- 0.7.3：统一版本号，完善配置
- 0.7.0：初始版本

## 许可证

Apache-2.0