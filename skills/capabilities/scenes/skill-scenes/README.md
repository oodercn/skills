# skill-scenes

场景管理服务，提供场景组管理、场景激活、能力绑定、协作管理等功能。

## 功能特性

- **场景组管理** - 创建/删除/查询场景组
- **场景激活** - 激活/停用场景
- **能力绑定** - 为场景绑定能力
- **协作管理** - 多场景协作
- **快照管理** - 场景状态快照
- **日志追踪** - 场景操作日志

## API端点

### 场景管理

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/scenes/list | GET | 获取场景列表 |
| /api/v1/scenes/create | POST | 创建场景 |
| /api/v1/scenes/get | GET | 获取场景详情 |
| /api/v1/scenes/delete | DELETE | 删除场景 |
| /api/v1/scenes/activate | POST | 激活场景 |
| /api/v1/scenes/deactivate | POST | 停用场景 |

### 能力管理

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/scenes/capabilities/list | GET | 获取场景能力列表 |
| /api/v1/scenes/capabilities/add | POST | 添加场景能力 |
| /api/v1/scenes/capabilities/remove | DELETE | 移除场景能力 |

### 协作管理

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/scenes/collaborative/list | GET | 获取协作场景列表 |
| /api/v1/scenes/collaborative/add | POST | 添加协作场景 |
| /api/v1/scenes/collaborative/remove | DELETE | 移除协作场景 |

### 快照管理

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/scenes/snapshot/create | POST | 创建快照 |
| /api/v1/scenes/snapshot/list | GET | 获取快照列表 |
| /api/v1/scenes/snapshot/restore | POST | 恢复快照 |

### 日志管理

| 端点 | 方法 | 说明 |
|------|------|------|
| /api/v1/scenes/logs | GET | 获取场景日志 |

## 快速开始

### 安装

```bash
skill install skill-scenes
```

### 配置

```yaml
ooder:
  scenes:
    enabled: true
    max-active-scenes: 10
    snapshot-retention: 7d
```

### 使用示例

```bash
# 创建场景
curl -X POST http://localhost:8080/api/v1/scenes/create \
  -H "Content-Type: application/json" \
  -d '{
    "name": "客服场景",
    "description": "智能客服场景",
    "capabilities": ["llm-chat", "knowledge-qa"]
  }'

# 激活场景
curl -X POST http://localhost:8080/api/v1/scenes/activate \
  -H "Content-Type: application/json" \
  -d '{"sceneId": "scene-001"}'
```

## 数据模型

### SceneGroup

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 场景ID |
| name | String | 场景名称 |
| description | String | 场景描述 |
| capabilities | List | 能力列表 |
| active | boolean | 是否激活 |
| createdAt | Date | 创建时间 |

### SceneCapability

| 字段 | 类型 | 说明 |
|------|------|------|
| sceneId | String | 场景ID |
| capabilityId | String | 能力ID |
| priority | int | 优先级 |
| enabled | boolean | 是否启用 |

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| ooder.scenes.enabled | boolean | true | 是否启用场景服务 |
| ooder.scenes.max-active-scenes | int | 10 | 最大激活场景数 |
| ooder.scenes.snapshot-retention | string | 7d | 快照保留时间 |

## 依赖

- skill-common (3.0.1)
- spring-boot-starter-data-jpa
- sqlite-jdbc

## 许可证

Apache-2.0
