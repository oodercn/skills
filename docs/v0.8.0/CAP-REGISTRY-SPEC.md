# CAP 注册表规范 v0.8.0

## 1. 概述

### 1.1 文档目的

本文档定义 CAP (Capability) 注册表的规范，包括：
- CAP 地址空间划分
- CAP 定义格式
- CAP 接口契约
- CAP 文档规范
- 离线降级要求

### 1.2 适用范围

- Skills 团队：开发符合 CAP 规范的 Skill
- SkillCenter 团队：实现 CAP 注册表 API
- SDK 团队：实现 CAP 验证和调用机制
- Engine 团队：实现 CAP 路由和管理

---

## 2. CAP 地址空间

### 2.1 地址格式

CAP 地址为 2 位 16 进制字符，范围 00-FF，共 256 个地址。

```
地址格式: [0-9A-F][0-9A-F]
示例: 00, 40, A0, FF
```

### 2.2 区域划分

| 区域 | 地址范围 | 数量 | 用途 |
|------|----------|------|------|
| 系统区 | 00-3F | 64 | 核心系统能力，由 Ooder 官方定义 |
| 通用区 | 40-9F | 96 | 通用业务能力，由 Ooder 官方定义 |
| 扩展区 | A0-FF | 96 | 扩展能力，可由社区申请定义 |

### 2.3 详细分配

#### 系统区 (00-3F)

| 地址范围 | 分类 | 说明 |
|----------|------|------|
| 00-0F | 核心系统 | 能力注册、发现、认证、路由 |
| 10-1F | Agent | Agent 注册、心跳、故障切换 |
| 20-2F | 场景组 | 场景加入、离开、同步 |
| 30-3F | VFS | 文件读写、目录管理 |

#### 通用区 (40-9F)

| 地址范围 | 分类 | 说明 |
|----------|------|------|
| 40-4F | 消息通讯 | 消息发送、接收、Topic、队列 |
| 50-5F | 组织管理 | 认证、用户、部门管理 |
| 60-6F | 存储 | 文件、对象存储操作 |
| 70-7F | 监控 | 指标采集、告警管理 |
| 80-8F | 安全 | 认证、审计、加密 |
| 90-9F | 支付 | 支付创建、查询、退款 |

#### 扩展区 (A0-FF)

| 地址范围 | 分类 | 说明 |
|----------|------|------|
| A0-AF | 媒体发布 | 微信、微博、知乎等 |
| B0-BF | AI/LLM | 对话、嵌入、微调 |
| C0-CF | 行业定制 | 医疗、金融、教育等 |
| D0-FF | 预留 | 未来扩展 |

---

## 3. CAP 定义格式

### 3.1 cap.yaml 规范

```yaml
apiVersion: ooder.io/v1
kind: CapabilityDefinition

metadata:
  capId: "40"                         # CAP 地址（16进制，必须）
  name: MSG_SEND                      # CAP 名称（大写下划线，必须）
  version: "1.0.0"                    # 语义化版本（必须）
  category: messaging                 # 分类（必须）
  status: stable                      # draft | stable | deprecated（必须）
  description: 发送消息给指定用户或群组  # 描述（必须）
  author: Ooder Team                  # 作者
  createdAt: 2026-02-20               # 创建日期
  updatedAt: 2026-02-20               # 更新日期

spec:
  # 接口定义
  interface:
    protocol: http                    # http | grpc | websocket | udp | local-jar
    path: /api/msg/send               # 接口路径
    method: POST                      # HTTP 方法
    timeout: 30000                    # 超时时间（毫秒）
    
    # 请求定义
    request:
      schema:
        type: object
        properties:
          to:
            type: string
            required: true
            description: 接收者ID
          content:
            type: object
            required: true
            description: 消息内容
            properties:
              type:
                type: string
                enum: [text, image, file, json]
              body:
                type: string
          options:
            type: object
            required: false
            description: 发送选项
            properties:
              priority:
                type: string
                enum: [low, normal, high]
                default: normal
              expireTime:
                type: integer
                description: 过期时间（毫秒）
                
    # 响应定义
    response:
      schema:
        type: object
        properties:
          messageId:
            type: string
            description: 消息ID
          status:
            type: string
            enum: [sent, failed, pending, queued]
            description: 发送状态
          timestamp:
            type: integer
            description: 时间戳
            
    # 错误定义
    errors:
      - code: MSG_001
        message: 用户不存在
        httpStatus: 404
      - code: MSG_002
        message: 消息内容过长
        httpStatus: 400
      - code: MSG_003
        message: 无发送权限
        httpStatus: 403
      - code: MSG_004
        message: 服务暂时不可用
        httpStatus: 503

  # 离线降级要求
  offline:
    required: true                    # 是否必须实现降级
    strategy: queue                   # queue | cache | reject | custom
    maxQueueSize: 1000                # 最大队列大小
    syncOnReconnect: true             # 重连后是否同步
    syncStrategy: fifo                # fifo | lifo | priority
    
    # 降级接口定义
    fallback:
      method: queueMessage
      params:
        - name: to
          type: string
        - name: content
          type: object
        - name: options
          type: object
      returns:
        type: object
        properties:
          queuedMessageId:
            type: string
          status:
            type: string
            enum: [queued, rejected]

  # 权限要求
  permissions:
    - msg:send
    - msg:priority

  # 速率限制
  rateLimit:
    max: 100                          # 最大请求数
    window: 60                        # 时间窗口（秒）
    unit: seconds
    strategy: sliding                 # fixed | sliding | token

  # 依赖的其他 CAP
  dependencies:
    - capId: "50"                      # ORG_AUTH
      required: true
      description: 需要认证能力验证用户身份

  # 调用类型支持
  connectorTypes:
    - http                            # HTTP 远程调用
    - local-jar                       # 本地 JAR 调用
    - grpc                            # gRPC 调用
```

### 3.2 cap.md 规范

```markdown
# CAP 40: MSG_SEND

## 基本信息

| 属性 | 值 |
|------|-----|
| CAP ID | 40 (0x28) |
| 名称 | MSG_SEND |
| 版本 | 1.0.0 |
| 分类 | messaging |
| 状态 | stable |
| 必须降级 | 是 |

## 功能描述

发送消息给指定用户或群组。支持文本、图片、文件等多种消息类型。

## 使用场景

- 即时消息发送
- 系统通知推送
- 群组消息广播
- 延迟消息发送

## 接口定义

### 请求

**HTTP**
```http
POST /api/msg/send
Content-Type: application/json
Authorization: Bearer {token}

{
  "to": "user-001",
  "content": {
    "type": "text",
    "body": "Hello, World!"
  },
  "options": {
    "priority": "high",
    "expireTime": 3600000
  }
}
```

**gRPC**
```protobuf
rpc SendMessage(SendMessageRequest) returns (SendMessageResponse);

message SendMessageRequest {
  string to = 1;
  Content content = 2;
  SendOptions options = 3;
}
```

### 响应

**成功响应**
```json
{
  "messageId": "msg-abc123",
  "status": "sent",
  "timestamp": 1700000000000
}
```

**错误响应**
```json
{
  "code": "MSG_001",
  "message": "用户不存在",
  "timestamp": 1700000000000
}
```

## 错误码

| 错误码 | HTTP状态 | 说明 | 处理建议 |
|--------|----------|------|----------|
| MSG_001 | 404 | 用户不存在 | 检查用户ID是否正确 |
| MSG_002 | 400 | 消息内容过长 | 减少消息内容长度 |
| MSG_003 | 403 | 无发送权限 | 检查用户权限设置 |
| MSG_004 | 503 | 服务暂时不可用 | 稍后重试或使用降级 |

## 离线降级

### 降级策略

- **策略类型**: queue（消息队列）
- **最大队列**: 1000 条
- **同步时机**: 网络恢复后自动同步
- **同步顺序**: FIFO（先进先出）

### 降级接口

```java
/**
 * 离线消息入队
 * @param to 接收者ID
 * @param content 消息内容
 * @param options 发送选项
 * @return 入队结果
 */
QueuedMessageResult queueMessage(String to, Object content, Object options);
```

### 降级响应

```json
{
  "queuedMessageId": "queue-xyz789",
  "status": "queued",
  "estimatedSyncTime": "unknown"
}
```

## Skill 实现指南

### 必须实现

1. **主接口**: `POST /api/msg/send`
2. **降级接口**: `queueMessage()` 方法
3. **重连同步**: `onReconnect()` 方法

### 示例代码

```java
@CapabilityEndpoint(capId = "40")
public class MsgSendCapability implements CapabilityHandler, FallbackHandler {

    @Override
    public SendMessageResult send(SendMessageRequest request) {
        // 在线模式：直接发送
        validateRequest(request);
        return messageClient.send(request);
    }
    
    @Override
    @FallbackMethod(capId = "40")
    public QueuedMessageResult queueMessage(String to, Object content, Object options) {
        // 离线模式：消息入队
        if (offlineQueue.size() >= MAX_QUEUE_SIZE) {
            return QueuedMessageResult.rejected("队列已满");
        }
        
        QueuedMessage msg = new QueuedMessage();
        msg.setTo(to);
        msg.setContent(content);
        msg.setOptions(options);
        msg.setCreateTime(System.currentTimeMillis());
        
        offlineQueue.offer(msg);
        return QueuedMessageResult.queued(msg.getId());
    }
    
    @Override
    public void onReconnect() {
        // 网络恢复后同步队列
        while (!offlineQueue.isEmpty()) {
            QueuedMessage msg = offlineQueue.poll();
            try {
                messageClient.send(msg.toRequest());
            } catch (Exception e) {
                log.error("同步消息失败: {}", msg.getId(), e);
                offlineQueue.offer(msg); // 重新入队
                break;
            }
        }
    }
}
```

## 版本历史

| 版本 | 日期 | 变更 | 兼容性 |
|------|------|------|--------|
| 1.0.0 | 2026-02-20 | 初始版本 | - |
```

---

## 4. CAP 注册流程

### 4.1 新增 CAP 流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CAP 注册流程                                              │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 申请 CAP 地址                                                          │
│     │                                                                        │
│     ├── 系统区/通用区：提交 Issue 到 ooder-cap-registry                     │
│     ├── 扩展区：在 cap-index.yaml 中预留地址                                │
│     └── 等待审批                                                            │
│                                                                             │
│  2. 编写 CAP 定义                                                          │
│     │                                                                        │
│     ├── 创建 cap.yaml 文件                                                 │
│     ├── 创建 cap.md 文档                                                   │
│     └── 遵循命名和格式规范                                                  │
│                                                                             │
│  3. 提交审核                                                                │
│     │                                                                        │
│     ├── 提交 Pull Request                                                  │
│     ├── 通过 CI 检查                                                       │
│     └── 等待代码审查                                                        │
│                                                                             │
│  4. 合并发布                                                                │
│     │                                                                        │
│     ├── 审核通过后合并                                                      │
│     ├── 更新 cap-index.yaml                                                │
│     └── 发布到 SkillCenter                                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 CAP 命名规范

| 规则 | 示例 | 说明 |
|------|------|------|
| 全大写 | MSG_SEND | 使用大写字母 |
| 下划线分隔 | ORG_USER_MGMT | 多个单词用下划线连接 |
| 动词+名词 | SEND_MESSAGE | 推荐动词在前 |
| 简洁明了 | AUTH | 避免过长名称 |

---

## 5. CAP 版本管理

### 5.1 版本号规则

采用语义化版本：`MAJOR.MINOR.PATCH`

| 变更类型 | 版本变更 | 说明 |
|----------|----------|------|
| 不兼容变更 | MAJOR+ | 接口重构、删除字段 |
| 新增功能 | MINOR+ | 新增可选字段、新接口 |
| Bug修复 | PATCH+ | 修复问题、文档更新 |

### 5.2 兼容性保证

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    CAP 版本兼容性                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  兼容变更 (MINOR/PATCH)                                                     │
│  ─────────────────────────────────────                                      │
│  ✅ 新增可选请求字段                                                        │
│  ✅ 新增可选响应字段                                                        │
│  ✅ 新增错误码                                                              │
│  ✅ 新增可选能力                                                            │
│  ✅ 文档更新                                                                │
│                                                                             │
│  不兼容变更 (MAJOR)                                                         │
│  ─────────────────────────────────────                                      │
│  ❌ 删除必填字段                                                            │
│  ❌ 修改字段类型                                                            │
│  ❌ 修改接口路径                                                            │
│  ❌ 删除错误码                                                              │
│  ❌ 修改离线策略                                                            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. CAP 验证

### 6.1 契约验证

Skill 注册时，SkillCenter 验证：

1. **接口路径验证**: 检查是否实现了 CAP 定义的接口
2. **请求格式验证**: 检查请求参数是否符合 schema
3. **响应格式验证**: 检查响应数据是否符合 schema
4. **降级实现验证**: 检查是否实现了 Fallback 方法
5. **权限验证**: 检查是否声明了所需权限

### 6.2 运行时验证

SDK 在运行时验证：

1. **请求参数验证**: 发送前验证参数格式
2. **响应格式验证**: 接收后验证响应格式
3. **错误处理验证**: 检查错误码是否在定义范围内
4. **降级触发验证**: 检查降级条件是否满足

---

## 7. 相关文档

- [架构设计总览](./ARCHITECTURE-V0.8.0.md)
- [场景引擎规范](./SCENE-ENGINE-SPEC.md)
- [能力发现协议](./CAPABILITY-DISCOVERY-PROTOCOL.md)
