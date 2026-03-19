# Feishu Organization Service

## 概述

飞书组织数据集成服务，包括组织架构同步和用户认证

## 版本

当前版本: 0.7.1

## 能力

| 能力ID | 名称 | 描述 |
|--------|------|------|
| org-data-read | Organization Data Read | 读取飞书组织架构和成员数据 |
| user-auth | User Authentication | 通过飞书OAuth认证用户 |

## 配置参数

### 必需参数

| 参数名 | 类型 | 描述 |
|--------|------|------|
| FEISHU_APP_ID | string | 飞书应用ID |
| FEISHU_APP_SECRET | string | 飞书应用Secret |

### 可选参数

| 参数名 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| FEISHU_API_BASE_URL | string | https://open.feishu.cn/open-apis | 飞书API基础URL |

## API端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/info | GET | 获取技能信息 |
| /api/org/tree | GET | 获取组织树 |
| /api/auth/verify | POST | 验证用户认证 |
| /api/health | GET | 健康检查 |

## 使用示例

```bash
# 安装技能
ooder skill install skill-org-feishu

# 配置参数
ooder skill config skill-org-feishu --set FEISHU_APP_ID=your-app-id
ooder skill config skill-org-feishu --set FEISHU_APP_SECRET=your-app-secret

# 启动技能
ooder skill start skill-org-feishu

# 获取组织树
curl http://localhost:8083/api/org/tree
```

## 依赖

- SDK版本: >=0.7.0
- Java版本: >=8

## 许可证

Apache-2.0

## 作者

Ooder Team
