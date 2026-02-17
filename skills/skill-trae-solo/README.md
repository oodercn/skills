# Trae Solo Service

## 概述

连接实用功能，运行结果通过A2UI节点展示，支持A2UI图转代码技术集成

## 版本

当前版本: 0.7.1

## 能力

| 能力ID | 名称 | 描述 |
|--------|------|------|
| execute-task | Execute Task | 执行任务并返回结果 |
| get-info | Get Info | 获取技能信息 |

## 配置参数

### 可选参数

| 参数名 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| TRAE_SOLO_PORT | number | 8085 | 服务端口 |
| A2UI_ENDPOINT | string | http://localhost:8081/api | A2UI服务端点 |

## API端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/info | GET | 获取技能信息 |
| /api/execute | POST | 执行任务 |
| /api/health | GET | 健康检查 |

## 使用示例

```bash
# 安装技能
ooder skill install skill-trae-solo

# 启动技能
ooder skill start skill-trae-solo

# 执行任务
curl -X POST http://localhost:8085/api/execute -d '{"task":"example","parameters":{}}'
```

## 依赖

- SDK版本: >=0.7.0
- Java版本: >=8
- 可选依赖: skill-a2ui >=0.7.0

## 许可证

Apache-2.0

## 作者

Ooder Team
