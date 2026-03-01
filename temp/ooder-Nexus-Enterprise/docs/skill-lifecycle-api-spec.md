# Skill 生命周期管理 API 接口规范

## 文档信息
- **版本**: 1.0
- **日期**: 2026-02-25
- **协议**: RESTful API
- **数据格式**: JSON

---

## 一、接口概览

### 1.1 基础信息

| 项目 | 值 |
|------|-----|
| **Base URL** | `http://localhost:9082/api` |
| **认证方式** | Bearer Token (JWT) |
| **Content-Type** | `application/json` |
| **字符编码** | UTF-8 |

### 1.2 接口分类

| 分类 | 前缀 | 描述 |
|------|------|------|
| **Skill管理** | `/skills` | Skill的CRUD操作 |
| **运行时监控** | `/runtime` | 实时状态和监控数据 |
| **能力管理** | `/capabilities` | 能力列表和调用追踪 |
| **事件中心** | `/events` | 生命周期事件 |
| **分析报告** | `/analysis` | 数据分析和报告 |

### 1.3 通用响应格式

```typescript
// 成功响应
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": 1708838400000,
  "requestId": "req-xxx-xxx"
}

// 错误响应
{
  "code": 400,
  "message": "Invalid parameter",
  "error": {
    "type": "VALIDATION_ERROR",
    "details": [ ... ]
  },
  "timestamp": 1708838400000,
  "requestId": "req-xxx-xxx"
}
```

### 1.4 HTTP状态码

| 状态码 | 含义 | 使用场景 |
|--------|------|----------|
| 200 | OK | 请求成功 |
| 201 | Created | 资源创建成功 |
| 204 | No Content | 删除成功 |
| 400 | Bad Request | 请求参数错误 |
| 401 | Unauthorized | 未认证 |
| 403 | Forbidden | 无权限 |
| 404 | Not Found | 资源不存在 |
| 409 | Conflict | 资源冲突 |
| 500 | Internal Server Error | 服务器内部错误 |

---

## 二、Skill管理接口

### 2.1 获取Skill列表

**接口**: `GET /skills`

**描述**: 获取所有Skill的列表，支持分页、过滤和排序

**请求参数**:

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| page | integer | 否 | 页码，默认1 |
| size | integer | 否 | 每页数量，默认20 |
| type | string | 否 | 类型过滤: `system`, `application` |
| status | string | 否 | 状态过滤: `active`, `inactive`, `error` |
| keyword | string | 否 | 关键词搜索 |
| sort | string | 否 | 排序字段，如 `name:asc`, `updatedAt:desc` |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "skill-llm",
        "name": "LLM服务",
        "type": "application",
        "status": "active",
        "version": "2.3.1",
        "description": "提供大语言模型对话能力",
        "capabilities": 3,
        "createdAt": "2024-01-15T08:00:00Z",
        "updatedAt": "2024-02-25T10:30:00Z"
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 11,
      "totalPages": 1
    }
  }
}
```

---

### 2.2 获取Skill详情

**接口**: `GET /skills/{skillId}`

**描述**: 获取单个Skill的详细信息

**路径参数**:

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| skillId | string | 是 | Skill唯一标识 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "skill-llm",
    "name": "LLM服务",
    "type": "application",
    "status": "active",
    "version": "2.3.1",
    "description": "提供大语言模型对话能力",
    "metadata": {
      "author": "ooder-team",
      "tags": ["ai", "nlp"]
    },
    "config": {
      "yaml": "metadata:\n  skillId: skill-llm\n  ..."
    },
    "runtime": {
      "status": "running",
      "uptime": 259200000,
      "health": "healthy",
      "lastHeartbeat": "2024-02-25T10:29:50Z"
    },
    "capabilities": [
      {
        "id": "cap-llm-chat",
        "name": "智能对话",
        "type": "chat",
        "description": "基于LLM的智能对话能力"
      }
    ],
    "dependencies": [
      {
        "skillId": "skill-auth-service",
        "required": true
      }
    ],
    "createdAt": "2024-01-15T08:00:00Z",
    "updatedAt": "2024-02-25T10:30:00Z"
  }
}
```

---

### 2.3 创建Skill

**接口**: `POST /skills`

**描述**: 创建新的Skill

**请求体**:
```json
{
  "id": "skill-new",
  "name": "新Skill",
  "type": "application",
  "description": "描述信息",
  "config": {
    "yaml": "metadata:\n  skillId: skill-new\n  ..."
  },
  "capabilities": [
    {
      "id": "cap-new-1",
      "name": "新能力",
      "type": "custom"
    }
  ]
}
```

**响应**: 201 Created，返回创建的Skill详情

---

### 2.4 更新Skill

**接口**: `PUT /skills/{skillId}`

**描述**: 更新Skill信息

**请求体**:
```json
{
  "name": "更新后的名称",
  "description": "更新后的描述",
  "config": {
    "yaml": "..."
  }
}
```

---

### 2.5 删除Skill

**接口**: `DELETE /skills/{skillId}`

**描述**: 删除Skill

**响应**: 204 No Content

---

### 2.6 控制Skill生命周期

**接口**: `POST /skills/{skillId}/control`

**描述**: 控制Skill的启动、停止、重启等操作

**请求体**:
```json
{
  "action": "start",  // start, stop, restart, pause, resume
  "reason": "手动启动",
  "force": false
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "skillId": "skill-llm",
    "action": "start",
    "previousStatus": "stopped",
    "currentStatus": "starting",
    "executedAt": "2024-02-25T10:30:00Z"
  }
}
```

---

### 2.7 获取Skill配置

**接口**: `GET /skills/{skillId}/config`

**描述**: 获取Skill的YAML配置

**查询参数**:

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| format | string | 否 | 格式: `yaml`, `json`, 默认`yaml` |
| version | string | 否 | 配置版本号 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "skillId": "skill-llm",
    "format": "yaml",
    "content": "metadata:\n  skillId: skill-llm\n  skillName: LLM服务\n  ...",
    "version": "v1.2.0",
    "updatedAt": "2024-02-25T10:30:00Z",
    "updatedBy": "admin"
  }
}
```

---

### 2.8 更新Skill配置

**接口**: `PUT /skills/{skillId}/config`

**描述**: 更新Skill的YAML配置

**请求体**:
```json
{
  "content": "metadata:\n  skillId: skill-llm\n  ...",
  "format": "yaml",
  "comment": "更新配置",
  "validate": true
}
```

---

### 2.9 获取Skill日志

**接口**: `GET /skills/{skillId}/logs`

**描述**: 获取Skill的运行日志

**查询参数**:

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| startTime | string | 否 | 开始时间，ISO 8601格式 |
| endTime | string | 否 | 结束时间，ISO 8601格式 |
| level | string | 否 | 日志级别: `debug`, `info`, `warn`, `error` |
| keyword | string | 否 | 关键词过滤 |
| lines | integer | 否 | 返回行数，默认100 |
| follow | boolean | 否 | 是否实时流式返回 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "skillId": "skill-llm",
    "logs": [
      {
        "timestamp": "2024-02-25T10:30:00Z",
        "level": "INFO",
        "message": "Service started successfully",
        "source": "SkillLifecycleManager"
      }
    ],
    "totalLines": 156
  }
}
```

---

## 三、运行时监控接口

### 3.1 获取运行时概览

**接口**: `GET /runtime/overview`

**描述**: 获取所有Skill的运行时概览信息

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summary": {
      "total": 11,
      "active": 8,
      "inactive": 2,
      "error": 1,
      "healthScore": 85
    },
    "skills": [
      {
        "id": "skill-llm",
        "name": "LLM服务",
        "status": "active",
        "health": "healthy",
        "uptime": 259200000,
        "resources": {
          "cpu": "12%",
          "memory": "256MB",
          "connections": 45
        },
        "lastHeartbeat": "2024-02-25T10:30:00Z"
      }
    ],
    "alerts": [
      {
        "id": "alert-001",
        "skillId": "skill-org",
        "level": "warning",
        "message": "High latency detected",
        "timestamp": "2024-02-25T10:25:00Z"
      }
    ]
  }
}
```

---

### 3.2 获取Skill运行时详情

**接口**: `GET /runtime/skills/{skillId}`

**描述**: 获取单个Skill的详细运行时信息

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "skill-llm",
    "status": "running",
    "health": {
      "status": "healthy",
      "score": 95,
      "checks": [
        {
          "name": "connectivity",
          "status": "pass",
          "latency": 15
        },
        {
          "name": "resource_usage",
          "status": "pass",
          "cpu": "12%",
          "memory": "256MB"
        }
      ]
    },
    "resources": {
      "cpu": {
        "usage": 12.5,
        "limit": 100,
        "unit": "percent"
      },
      "memory": {
        "usage": 268435456,
        "limit": 1073741824,
        "unit": "bytes"
      },
      "connections": {
        "active": 45,
        "idle": 12,
        "max": 100
      }
    },
    "performance": {
      "requestRate": 120,
      "avgLatency": 245,
      "p95Latency": 520,
      "p99Latency": 890,
      "errorRate": 0.1
    },
    "uptime": 259200000,
    "startedAt": "2024-02-22T10:30:00Z",
    "lastHeartbeat": "2024-02-25T10:30:00Z"
  }
}
```

---

### 3.3 获取运行时指标

**接口**: `GET /runtime/metrics`

**描述**: 获取运行时指标数据，用于图表展示

**查询参数**:

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| skillId | string | 否 | 指定Skill，不指定则返回所有 |
| metrics | string | 是 | 指标列表，逗号分隔: `cpu,memory,latency` |
| startTime | string | 是 | 开始时间 |
| endTime | string | 是 | 结束时间 |
| interval | string | 否 | 聚合间隔: `1m`, `5m`, `1h`, 默认`5m` |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "skillId": "skill-llm",
    "metrics": [
      {
        "name": "cpu",
        "unit": "percent",
        "dataPoints": [
          {
            "timestamp": "2024-02-25T10:00:00Z",
            "value": 12.5
          },
          {
            "timestamp": "2024-02-25T10:05:00Z",
            "value": 15.2
          }
        ]
      }
    ]
  }
}
```

---

### 3.4 WebSocket实时推送

**接口**: `WS /ws/runtime`

**描述**: WebSocket连接，实时推送运行时状态更新

**消息格式**:
```json
// 客户端订阅
{
  "type": "subscribe",
  "channels": ["skill-status", "metrics", "alerts"],
  "filter": {
    "skillIds": ["skill-llm", "skill-org"]
  }
}

// 服务端推送 - 状态更新
{
  "type": "skill-status",
  "timestamp": "2024-02-25T10:30:00Z",
  "data": {
    "skillId": "skill-llm",
    "previousStatus": "starting",
    "currentStatus": "active",
    "health": "healthy"
  }
}

// 服务端推送 - 指标更新
{
  "type": "metrics",
  "timestamp": "2024-02-25T10:30:00Z",
  "data": {
    "skillId": "skill-llm",
    "metrics": {
      "cpu": 12.5,
      "memory": 268435456,
      "latency": 245
    }
  }
}

// 服务端推送 - 告警
{
  "type": "alert",
  "timestamp": "2024-02-25T10:30:00Z",
  "data": {
    "id": "alert-001",
    "level": "warning",
    "skillId": "skill-org",
    "message": "High latency detected: 1200ms"
  }
}
```

---

## 四、能力管理接口

### 4.1 获取能力列表

**接口**: `GET /capabilities`

**描述**: 获取所有能力的列表

**查询参数**:

| 参数 | 类型 | 必填 | 描述 |
|------|------|------|------|
| skillId | string | 否 | 按Skill过滤 |
| type | string | 否 | 按类型过滤 |
| page | integer | 否 | 页码 |
| size | integer | 否 | 每页数量 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "cap-llm-chat",
        "name": "智能对话",
        "skillId": "skill-llm",
        "skillName": "LLM服务",
        "type": "chat",
        "description": "基于LLM的智能对话能力",
        "status": "available",
        "stats": {
          "totalInvocations": 156,
          "successRate": 98.5,
          "avgLatency": 245
        }
      }
    ],
    "pagination": {
      "page": 1,
      "size": 20,
      "total": 28
    }
  }
}
```

---

### 4.2 获取能力详情

**接口**: `GET /capabilities/{capabilityId}`

**描述**: 获取单个能力的详细信息

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "cap-llm-chat",
    "name": "智能对话",
    "skillId": "skill-llm",
    "type": "chat",
    "description": "基于LLM的智能对话能力",
    "inputSchema": {
      "type": "object",
      "properties": {
        "message": { "type": "string" },
        "conversationId": { "type": "string" }
      },
      "required": ["message"]
    },
    "outputSchema": {
      "type": "object",
      "properties": {
        "response": { "type": "string" },
        "tokensUsed": { "type": "integer" }
      }
    },
    "stats": {
      "totalInvocations": 156,
      "successCount": 154,
      "failureCount": 2,
      "successRate": 98.5,
      "avgLatency": 245,
      "p95Latency": 520,
      "p99Latency": 890
    },
    "status": "available"
  }
}
```

---

### 4.3 调用能力

**