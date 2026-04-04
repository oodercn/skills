# AI Bridge Protocol API 使用文档

**文档版本**: v1.0  
**创建日期**: 2026-04-04  
**API版本**: v1  
**基础路径**: `/api/v1/protocol`

---

## 一、概述

AI Bridge Protocol 是 OoderAgent(Nexus) 的核心通信协议，支持技能发现、智能体管理、场景管理等功能。本文档提供了完整的 API 使用指南。

### 1.1 协议特点

- ✅ **统一消息格式** - 所有命令使用统一的消息格式
- ✅ **策略模式** - 每个命令独立处理，易于扩展
- ✅ **同步/异步支持** - 支持同步和异步消息处理
- ✅ **批量处理** - 支持批量命令执行
- ✅ **错误处理** - 完善的错误响应机制

### 1.2 基础URL

```
http://localhost:8080/api/v1/protocol
```

---

## 二、消息格式

### 2.1 请求消息格式

```json
{
  "id": "msg-001",
  "timestamp": 1712236800000,
  "command": "skill.discover",
  "params": {
    "key": "value"
  },
  "source": "agent-001",
  "target": "agent-002"
}
```

### 2.2 字段说明

| 字段 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| id | String | 是 | 消息唯一标识 |
| timestamp | Long | 否 | 消息时间戳（默认当前时间） |
| command | String | 是 | 命令类型 |
| params | Object | 否 | 命令参数 |
| source | String | 否 | 消息来源 |
| target | String | 否 | 消息目标 |

### 2.3 响应消息格式

**成功响应**:
```json
{
  "id": "msg-002",
  "timestamp": 1712236800001,
  "command": "skill.discover",
  "status": "success",
  "result": {
    // 返回数据
  },
  "response_to": "msg-001"
}
```

**错误响应**:
```json
{
  "id": "msg-003",
  "timestamp": 1712236800002,
  "command": "skill.discover",
  "status": "error",
  "error": {
    "code": 2001,
    "message": "Skill not found",
    "details": "Skill with ID 'skill-001' does not exist"
  },
  "response_to": "msg-001"
}
```

---

## 三、API 端点

### 3.1 AI Bridge Protocol API

**基础路径**: `/api/v1/protocol/aibridge`

#### 3.1.1 处理单个消息

**端点**: `POST /message`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/protocol/aibridge/message \
  -H "Content-Type: application/json" \
  -d '{
    "id": "msg-001",
    "command": "skill.discover",
    "params": {
      "category": "system"
    }
  }'
```

**响应示例**:
```json
{
  "id": "msg-002",
  "timestamp": 1712236800001,
  "command": "skill.discover",
  "status": "success",
  "result": {
    "skills": [
      {
        "skill_id": "skill-001",
        "name": "System Monitor",
        "description": "Monitor system resources",
        "category": "system",
        "version": "1.0.0"
      }
    ],
    "total": 1
  },
  "response_to": "msg-001"
}
```

#### 3.1.2 处理JSON字符串消息

**端点**: `POST /message/json`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/protocol/aibridge/message/json \
  -H "Content-Type: application/json" \
  -d '{"id":"msg-001","command":"skill.discover","params":{"category":"system"}}'
```

#### 3.1.3 异步处理消息

**端点**: `POST /message/async`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/protocol/aibridge/message/async \
  -H "Content-Type: application/json" \
  -d '{
    "id": "msg-001",
    "command": "skill.invoke",
    "params": {
      "skill_id": "skill-001",
      "parameters": {
        "interval": 5000
      }
    }
  }'
```

**响应示例**:
```json
{
  "id": "msg-002",
  "timestamp": 1712236800001,
  "command": "skill.invoke",
  "status": "success",
  "result": {
    "success": true,
    "message": "Skill invoked successfully",
    "execution_time": 123
  },
  "response_to": "msg-001"
}
```

#### 3.1.4 批量处理消息

**端点**: `POST /batch`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/protocol/aibridge/batch \
  -H "Content-Type: application/json" \
  -d '{
    "cmd1": {
      "id": "msg-001",
      "command": "skill.discover"
    },
    "cmd2": {
      "id": "msg-002",
      "command": "agent.register",
      "params": {
        "agent_id": "agent-001",
        "agent_name": "Test Agent"
      }
    }
  }'
```

**响应示例**:
```json
{
  "cmd1": {
    "id": "msg-003",
    "command": "skill.discover",
    "status": "success",
    "result": {
      "skills": [],
      "total": 0
    },
    "response_to": "msg-001"
  },
  "cmd2": {
    "id": "msg-004",
    "command": "agent.register",
    "status": "success",
    "result": {
      "agent_id": "agent-001",
      "status": "registered"
    },
    "response_to": "msg-002"
  }
}
```

---

### 3.2 Agent Protocol API

**基础路径**: `/api/v1/protocol/agent`

#### 3.2.1 处理北上协议消息

**端点**: `POST /north`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/protocol/agent/north \
  -H "Content-Type: application/json" \
  -d '{
    "type": "REGISTER_REQUEST",
    "source_agent_id": "agent-001",
    "data": {
      "agent_id": "agent-001",
      "agent_type": "end",
      "address": "192.168.1.100",
      "port": 8080
    }
  }'
```

**响应示例**:
```json
{
  "type": "REGISTER_REQUEST_RESPONSE",
  "timestamp": 1712236800001,
  "source_agent_id": null,
  "target_agent_id": "agent-001",
  "data": {
    "success": true,
    "message": "Register request processed"
  }
}
```

#### 3.2.2 处理南下协议消息

**端点**: `POST /south`

**请求示例**:
```bash
curl -X POST http://localhost:8080/api/v1/protocol/agent/south \
  -H "Content-Type: application/json" \
  -d '{
    "type": "COMMAND",
    "target_agent_id": "agent-001",
    "data": {
      "command_id": "cmd-001",
      "action": "start_monitoring",
      "parameters": {
        "interval": 5000
      }
    }
  }'
```

**响应示例**:
```json
{
  "type": "COMMAND_ACK",
  "timestamp": 1712236800001,
  "source_agent_id": "agent-001",
  "target_agent_id": null,
  "data": {
    "success": true,
    "message": "Command processed"
  }
}
```

---

## 四、命令参考

### 4.1 技能相关命令

#### skill.discover

**功能**: 发现技能

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| skill_id | String | 否 | 技能ID（查询特定技能） |
| category | String | 否 | 技能分类 |

**请求示例**:
```json
{
  "command": "skill.discover",
  "params": {
    "category": "system"
  }
}
```

**响应示例**:
```json
{
  "status": "success",
  "result": {
    "skills": [
      {
        "skill_id": "skill-001",
        "name": "System Monitor",
        "description": "Monitor system resources",
        "category": "system",
        "version": "1.0.0",
        "status": "IDLE",
        "available": true
      }
    ],
    "total": 1
  }
}
```

#### skill.invoke

**功能**: 调用技能

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| skill_id | String | 是 | 技能ID |
| parameters | Object | 否 | 技能参数 |

**请求示例**:
```json
{
  "command": "skill.invoke",
  "params": {
    "skill_id": "skill-001",
    "parameters": {
      "interval": 5000
    }
  }
}
```

#### skill.register

**功能**: 注册技能

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| skill_id | String | 是 | 技能ID |
| name | String | 是 | 技能名称 |
| description | String | 否 | 技能描述 |
| category | String | 否 | 技能分类 |
| version | String | 否 | 技能版本 |

---

### 4.2 智能体相关命令

#### agent.register

**功能**: 注册智能体

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| agent_id | String | 是 | 智能体ID |
| agent_name | String | 是 | 智能体名称 |
| agent_type | String | 否 | 智能体类型 |
| ip_address | String | 否 | IP地址 |
| port | Integer | 否 | 端口号 |

#### agent.unregister

**功能**: 注销智能体

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| agent_id | String | 是 | 智能体ID |

---

### 4.3 场景相关命令

#### scene.join

**功能**: 加入场景

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| scene_id | String | 是 | 场景ID |
| user_id | String | 是 | 用户ID |
| role | String | 否 | 角色 |

#### scene.leave

**功能**: 离开场景

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| scene_id | String | 是 | 场景ID |
| user_id | String | 是 | 用户ID |

#### scene.query

**功能**: 查询场景

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| scene_id | String | 否 | 场景ID（查询特定场景） |
| status | String | 否 | 场景状态 |
| page_num | Integer | 否 | 页码 |
| page_size | Integer | 否 | 每页数量 |

---

### 4.4 Cap相关命令

#### cap.declare

**功能**: 声明Capability

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| cap_id | String | 是 | Capability ID |
| name | String | 是 | 名称 |
| description | String | 否 | 描述 |
| type | String | 否 | 类型 |
| category | String | 否 | 分类 |
| version | String | 否 | 版本 |

#### cap.query

**功能**: 查询Capability

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| cap_id | String | 否 | Capability ID |
| type | String | 否 | 类型 |
| category | String | 否 | 分类 |

---

### 4.5 Group相关命令

#### group.member.add

**功能**: 添加Group成员

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| group_id | String | 是 | Group ID |
| user_id | String | 是 | 用户ID |
| user_name | String | 否 | 用户名称 |

#### group.member.remove

**功能**: 移除Group成员

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| group_id | String | 是 | Group ID |
| user_id | String | 是 | 用户ID |

---

### 4.6 资源相关命令

#### resource.list

**功能**: 列出系统资源

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| resource_type | String | 否 | 资源类型（storage, compute, network） |

**响应示例**:
```json
{
  "status": "success",
  "result": {
    "resources": [
      {
        "type": "storage",
        "path": "/",
        "total_space": 107374182400,
        "usable_space": 53687091200,
        "usage_percent": 50.0
      },
      {
        "type": "compute",
        "available_processors": 8,
        "system_load_average": 2.5,
        "heap_memory_used": 1073741824,
        "heap_memory_max": 2147483648,
        "memory_usage_percent": 50.0
      },
      {
        "type": "network",
        "hostname": "localhost",
        "port_range_start": 10000,
        "port_range_end": 20000
      }
    ],
    "total": 3
  }
}
```

#### resource.get

**功能**: 获取资源详情

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| resource_id | String | 是 | 资源ID |
| resource_type | String | 是 | 资源类型（quota） |

---

### 4.7 批量命令

#### batch.execute

**功能**: 批量执行命令

**参数**:
| 参数 | 类型 | 必填 | 说明 |
|-----|------|------|------|
| commands | Array | 是 | 命令数组 |

**请求示例**:
```json
{
  "command": "batch.execute",
  "params": {
    "commands": [
      {
        "command": "skill.discover",
        "params": {"category": "system"}
      },
      {
        "command": "agent.register",
        "params": {
          "agent_id": "agent-001",
          "agent_name": "Test Agent"
        }
      }
    ]
  }
}
```

**响应示例**:
```json
{
  "status": "success",
  "result": {
    "total": 2,
    "success_count": 2,
    "failure_count": 0,
    "results": [
      {
        "index": 0,
        "command": "skill.discover",
        "status": "success",
        "result": {
          "skills": [],
          "total": 0
        }
      },
      {
        "index": 1,
        "command": "agent.register",
        "status": "success",
        "result": {
          "agent_id": "agent-001",
          "status": "registered"
        }
      }
    ]
  }
}
```

---

## 五、错误处理

### 5.1 错误码

| 错误码 | 说明 |
|-------|------|
| 0 | 成功 |
| 400 | 无效请求 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源未找到 |
| 408 | 请求超时 |
| 409 | 冲突 |
| 500 | 内部错误 |
| 1001 | 无效参数 |
| 2001 | 技能未找到 |
| 2002 | 技能调用错误 |
| 3001 | 场景未找到 |
| 3002 | 场景加入错误 |
| 4001 | 智能体未找到 |
| 4002 | 智能体注册错误 |
| 5001 | Capability未找到 |
| 5002 | Capability声明错误 |
| 6001 | 资源未找到 |
| 6002 | 资源访问错误 |

### 5.2 错误响应示例

```json
{
  "id": "msg-002",
  "timestamp": 1712236800001,
  "command": "skill.invoke",
  "status": "error",
  "error": {
    "code": 2001,
    "message": "Skill not found",
    "details": "Skill with ID 'skill-999' does not exist"
  },
  "response_to": "msg-001"
}
```

---

## 六、最佳实践

### 6.1 消息ID生成

建议使用 UUID 作为消息ID：

```java
String messageId = UUID.randomUUID().toString();
```

### 6.2 超时设置

建议为每个请求设置合理的超时时间：

```java
AiBridgeMessage message = AiBridgeMessageBuilder.create()
    .id(UUID.randomUUID().toString())
    .command("skill.invoke")
    .param("skill_id", "skill-001")
    .metadata(new Metadata())
    .build();

message.getMetadata().setTimeout(30000); // 30秒超时
```

### 6.3 错误处理

建议检查响应状态并处理错误：

```java
AiBridgeMessage response = protocolService.sendMessage(message);

if ("success".equals(response.getStatus())) {
    Object result = response.getResult();
    // 处理成功响应
} else {
    ErrorInfo error = response.getError();
    logger.error("Command failed: code={}, message={}", 
        error.getCode(), error.getMessage());
    // 处理错误
}
```

### 6.4 批量操作

对于多个独立操作，建议使用批量命令：

```java
List<Map<String, Object>> commands = new ArrayList<>();

commands.add(Map.of(
    "command", "skill.discover",
    "params", Map.of("category", "system")
));

commands.add(Map.of(
    "command", "agent.register",
    "params", Map.of(
        "agent_id", "agent-001",
        "agent_name", "Test Agent"
    )
));

AiBridgeMessage batchMessage = AiBridgeMessageBuilder.create()
    .command("batch.execute")
    .param("commands", commands)
    .build();
```

---

## 七、SDK 使用示例

### 7.1 Java SDK

```java
@Autowired
private AiBridgeProtocolService protocolService;

public void example() {
    // 创建消息
    AiBridgeMessage message = AiBridgeMessageBuilder.create()
        .id(UUID.randomUUID().toString())
        .command("skill.discover")
        .param("category", "system")
        .source("client-001")
        .build();
    
    // 发送消息
    AiBridgeMessage response = protocolService.sendMessage(message);
    
    // 处理响应
    if ("success".equals(response.getStatus())) {
        Map<String, Object> result = (Map<String, Object>) response.getResult();
        List<Map<String, Object>> skills = (List<Map<String, Object>>) result.get("skills");
        
        for (Map<String, Object> skill : skills) {
            System.out.println("Skill: " + skill.get("name"));
        }
    } else {
        System.err.println("Error: " + response.getError().getMessage());
    }
}
```

### 7.2 REST API 调用

```bash
# 使用curl调用
curl -X POST http://localhost:8080/api/v1/protocol/aibridge/message \
  -H "Content-Type: application/json" \
  -d '{
    "id": "msg-001",
    "command": "skill.discover",
    "params": {
      "category": "system"
    }
  }'

# 使用JavaScript fetch API
fetch('http://localhost:8080/api/v1/protocol/aibridge/message', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    id: 'msg-001',
    command: 'skill.discover',
    params: {
      category: 'system'
    }
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

---

## 八、常见问题

### 8.1 如何处理超时？

**问题**: 命令执行时间过长导致超时。

**解决方案**:
1. 设置合理的超时时间
2. 使用异步API
3. 优化命令处理逻辑

### 8.2 如何调试命令？

**问题**: 命令执行失败，需要调试。

**解决方案**:
1. 检查错误响应中的错误码和消息
2. 查看服务器日志
3. 使用批量命令逐步测试

### 8.3 如何提高性能？

**问题**: 命令执行速度慢。

**解决方案**:
1. 使用异步API
2. 使用批量命令减少网络开销
3. 缓存常用数据

---

## 九、相关文档

- **开发者指南**: `E:\github\ooder-skills\docs\v3.0.1\AI_BRIDGE_PROTOCOL_DEVELOPER_GUIDE.md`
- **移植报告**: `E:\github\ooder-skills\docs\v3.0.1\AI_BRIDGE_PROTOCOL_MIGRATION_FINAL_REPORT.md`
- **扩展命令完成报告**: `E:\github\ooder-skills\docs\v3.0.1\AI_BRIDGE_PROTOCOL_EXTENSION_COMPLETION_REPORT.md`

---

**文档维护**: 本文档应在API变更时及时更新。

**变更记录**:
- 2026-04-04 v1.0: 初始版本创建，完整的API使用文档
