# Agent心跳服务集成说明

## 概述

本文档说明如何集成SDK的EnhancedHeartbeatService来实现Agent心跳维持机制。

## 问题分析

### 原始问题
- **所有Agent显示为离线**：agent-sessions.json中的时间戳是未来时间（2026年）
- **没有主动心跳发送**：Agent端没有定时发送心跳
- **HeartbeatService未被使用**：虽然SDK提供了心跳服务，- **状态判断逻辑**：根据心跳时间判断状态（< 1分钟：在线， < 5分钟：忙碌， >= 5分钟：离线）

## 解决方案

### 1. 创建AgentHeartbeatConfig配置类

**位置**：`src/main/java/net/ooder/mvp/skill/scene/agent/config/AgentHeartbeatConfig.java`

**功能**：
- 集成`EnhancedHeartbeatService`到Spring容器
- 实现`AgentFactory`和`AgentAdapter`
- 配置定时任务维护心跳
- 提供Agent注册/注销接口

### 2. 更新application.yml

```yaml
agent:
  heartbeat:
    interval: 30000        # 心跳间隔：30秒
    timeout: 60000          # 心跳超时：60秒
    offline-threshold: 3    # 连续3次心跳失败判定为离线
    check-interval: 30000    # 检查间隔：30秒
```

### 3. 集成到AgentSessionService

**修改**：`AgentSessionServiceImpl.java`
- 在`register()`中注册Agent到心跳服务
- 在`logout()`中从心跳服务注销Agent

### 4. 修复agent-sessions.json

**修改**：将所有时间戳更新为当前时间

### 5. 创建测试端点

**位置**：`AgentHeartbeatTestController.java`

**端点**：
- `GET /api/agent/heartbeat/stats/{agentId}` - 获取心跳统计
- `GET /api/agent/heartbeat/status/{agentId}` - 获取设备状态
- `GET /api/agent/heartbeat/all/stats` - 获取所有Agent统计
- `POST /api/agent/heartbeat/trigger/{agentId}` - 手动触发心跳

## 心跳机制架构

```
┌─────────────────────────────────────────────────────────────┐
│                    MVP Server 端                      │
│                                                             │
│  ┌──────────────────────┐      ┌─────────────────────┐   │
│  │ AgentHeartbeatConfig  │      │ AgentSessionService │   │
│  │ - EnhancedHeartbeatService │◄─────│ - 会话管理           │   │
│  │ - AgentFactoryImpl    │      │ - 心跳接收           │   │
│  │ - AgentAdapter         │      │ - 状态更新           │   │
│  └──────────────────────┘      └─────────────────────┘   │
│           │                            │                      │
│           │ 定时发送心跳              │ 心跳请求              │
│           ▼                            ▼                      │
└─────────────────────────────────────────────────────────────┘
                          ▲
                          │
┌─────────────────────────────────────────────────────────────┐
│                    Agent 端                              │
│                                                             │
│  ┌──────────────────────┐                                  │
│  │ AgentAdapter           │                                  │
│  │ - isHealthy()           │                                  │
│  │ - 调用AgentService      │                                  │
│  │   .sendHeartbeat()      │                                  │
│  └──────────────────────┘                                  │
└─────────────────────────────────────────────────────────────┘
```

## 心跳流程

### 1. 启动流程
```
1. Spring启动
2. AgentHeartbeatConfig.init() 被调用
3. 创建EnhancedHeartbeatService
4. 注册所有已存在的Agent
5. 启动心跳监控
```

### 2. 心跳发送流程
```
1. EnhancedHeartbeatService定时任务触发
2. 调用AgentAdapter.isHealthy()
3. AgentAdapter调用AgentService.sendHeartbeat()
4. 更新lastHeartbeat时间戳
5. 更新Agent状态
```

### 3. 状态判断逻辑
```java
long heartbeatAge = System.currentTimeMillis() - agent.getLastHeartbeat();

if (heartbeatAge < 60000) {        // < 1分钟
    agent.setStatus("online");
} else if (heartbeatAge < 300000) {  // < 5分钟
    agent.setStatus("busy");
} else {                          // >= 5分钟
    agent.setStatus("offline");
}
```

### 4. 离线判断
```
1. 连续3次心跳失败
2. 标记为DEGRADED状态
3. 继续失败达到阈值
4. 标记为OFFLINE状态
5. 30秒后尝试自动重连
```

## 配置说明

### application.yml配置
```yaml
agent:
  session:
    timeout: 86400          # 会话超时：24小时
  message:
    expire: 86400          # 消息过期时间：24小时
    max-queue: 1000        # 最大队列大小
  heartbeat:
    interval: 30000        # 心跳间隔：30秒
    timeout: 60000          # 心跳超时：60秒
    offline-threshold: 3    # 离线阈值：连续3次失败
    check-interval: 30000    # 检查间隔：30秒
```

### 设备类型配置
- **FIXED**: 固定设备（PC、服务器） - 30秒心跳
- **MOBILE**: 移动设备（手机、平板） - 30秒正常，60秒睡眠
- **BATTERY**: 电池设备（传感器、IoT） - 60秒正常，120秒睡眠

## 测试验证

### 1. 检查Agent状态
```bash
curl http://localhost:8084/api/agent/list
```

### 2. 查看心跳统计
```bash
curl http://localhost:8084/api/agent/heartbeat/stats/agent-llm-001
```

### 3. 查看设备状态
```bash
curl http://localhost:8084/api/agent/heartbeat/status/agent-llm-001
```

### 4. 手动触发心跳
```bash
curl -X POST http://localhost:8084/api/agent/heartbeat/trigger/agent-llm-001
```

### 5. 查看所有Agent统计
```bash
curl http://localhost:8084/api/agent/heartbeat/all/stats
```

## 监控指标

### HeartbeatStats
- `totalHeartbeats`: 总心跳次数
- `successfulHeartbeats`: 成功心跳次数
- `failedHeartbeats`: 失败心跳次数
- `consecutiveMisses`: 连续丢失次数
- `lastHeartbeatTime`: 最后心跳时间
- `status`: 设备状态

### DeviceStatus
- `ONLINE`: 在线
- `DEGRADED`: 降级（心跳异常）
- `OFFLINE`: 离线
- `FAULT`: 故障
- `UNKNOWN`: 未知

## 注意事项

1. **时间同步**：确保服务器时间准确
2. **网络稳定性**：确保网络连接稳定
3. **资源监控**：监控线程池和内存使用
4. **日志查看**：定期检查心跳相关日志

## 后续优化建议

1. **心跳频率优化**：根据实际负载调整心跳频率
2. **离线恢复策略**：优化自动重连逻辑
3. **告警机制**：添加心跳异常告警
4. **性能监控**：添加性能指标监控

## 相关文件

- `AgentHeartbeatConfig.java` - 心跳配置类
- `AgentHeartbeatTestController.java` - 测试控制器
- `AgentSessionServiceImpl.java` - 会话服务实现
- `application.yml` - 应用配置
- `agent-sessions.json` - Agent会话数据
