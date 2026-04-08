# skill-messaging

消息服务 - 提供系统内部消息的发送、接收和管理功能。

## 功能特性

- **消息发送** - 发送系统内部消息
- **消息接收** - 接收和处理消息
- **消息队列** - 支持消息队列
- **消息路由** - 消息路由和分发

## 核心接口

### MessagingController

消息控制器。

```java
@RestController
@RequestMapping("/api/v1/messaging")
public class MessagingController {
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public MessageDTO sendMessage(@RequestBody SendMessageRequest request);
    
    /**
     * 接收消息
     */
    @GetMapping("/receive")
    public List<MessageDTO> receiveMessages(@RequestParam String queue);
    
    /**
     * 确认消息
     */
    @PostMapping("/{messageId}/ack")
    public void acknowledgeMessage(@PathVariable String messageId);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/messaging/send | POST | 发送消息 |
| /api/v1/messaging/receive | GET | 接收消息 |
| /api/v1/messaging/{messageId}/ack | POST | 确认消息 |

## 消息模型

### MessageDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 消息ID |
| queue | String | 队列名称 |
| payload | String | 消息内容 |
| priority | int | 优先级 |
| createdAt | Long | 创建时间 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-messaging</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private MessagingService messagingService;

// 发送消息
SendMessageRequest request = new SendMessageRequest();
request.setQueue("task-queue");
request.setPayload("{\"task\": \"process-data\"}");
MessageDTO message = messagingService.sendMessage(request);

// 接收消息
List<MessageDTO> messages = messagingService.receiveMessages("task-queue");

// 确认消息
messagingService.acknowledgeMessage(message.getId());
```

## 许可证

Apache-2.0
