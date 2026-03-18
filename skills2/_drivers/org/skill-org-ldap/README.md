# Skill Org LDAP - LDAP组织服务

## 概述

LDAP组织服务技能，提供基于LDAP的组织架构管理和用户认证能力。

## 功能特性

- **组织架构管理**：基于LDAP的组织架构管理
- **用户认证**：支持LDAP用户认证
- **部门管理**：查询和管理LDAP部门结构
- **用户管理**：查询和管理LDAP用户信息

## 配置参数

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| LDAP_URL | string | 是 | - | LDAP服务器URL |
| LDAP_BASE_DN | string | 是 | - | LDAP基础DN |
| LDAP_USERNAME | string | 是 | - | LDAP管理员用户名 |
| LDAP_PASSWORD | string | 是 | - | LDAP管理员密码 |
| LDAP_USER_SEARCH_BASE | string | 否 | - | 用户搜索基础DN |
| LDAP_GROUP_SEARCH_BASE | string | 否 | - | 组搜索基础DN |

## 能力列表

- `org-data-read`：组织数据读取
- `org-data-sync`：组织数据同步
- `user-auth`：用户认证
- `department-query`：部门查询
- `user-query`：用户查询

## 场景关联

- `auth`：认证场景

## 快速开始

### 1. 配置LDAP连接

```yaml
ldap:
  url: ldap://localhost:389
  base-dn: dc=example,dc=com
  username: cn=admin,dc=example,dc=com
  password: admin-password
  user-search-base: ou=users,dc=example,dc=com
  group-search-base: ou=groups,dc=example,dc=com
```

### 2. 启动服务

```bash
java -jar skill-org-ldap-0.7.3.jar
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
- Spring LDAP 2.4.1

## 版本历史

- 0.7.3：统一版本号，完善配置
- 0.7.0：初始版本

## 许可证

Apache-2.0