# skill-notification

通知服务 - 提供系统通知、消息推送、消息管理等功能。

## 功能特性

- **通知管理** - 通知的增删改查
- **消息发送** - 多渠道消息发送
- **未读统计** - 统计未读消息数量
- **消息推送** - 推送消息到用户

## 核心接口

### NotificationController

通知管理控制器。

```java
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    /**
     * 获取通知列表
     */
    @GetMapping
    public NotificationListResult getNotifications(
        @RequestParam(required = false) String type,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    );
    
    /**
     * 获取未读数量
     */
    @GetMapping("/unread-count")
    public UnreadCountsDTO getUnreadCount();
    
    /**
     * 标记已读
     */
    @PostMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable String notificationId);
    
    /**
     * 全部标记已读
     */
    @PostMapping("/read-all")
    public void markAllAsRead();
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public SendMessageResultDTO sendMessage(@RequestBody SendMessageRequest request);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/notifications | GET | 获取通知列表 |
| /api/v1/notifications/unread-count | GET | 获取未读数量 |
| /api/v1/notifications/{notificationId}/read | POST | 标记已读 |
| /api/v1/notifications/read-all | POST | 全部标记已读 |
| /api/v1/notifications/channels | GET | 获取可用渠道 |
| /api/v1/notifications/send | POST | 发送消息 |

## 通知模型

### NotificationDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 通知ID |
| title | String | 通知标题 |
| content | String | 通知内容 |
| type | NotificationType | 通知类型 |
| read | boolean | 是否已读 |
| createdAt | Long | 创建时间 |

### NotificationType

- **SYSTEM** - 系统通知
- **TASK** - 任务通知
- **MESSAGE** - 消息通知
- **ALERT** - 警告通知

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-notification</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private NotificationService notificationService;

// 获取通知列表
NotificationListResult notifications = notificationService.getNotifications("SYSTEM", 0, 20);

// 获取未读数量
UnreadCountsDTO unread = notificationService.getUnreadCount();

// 标记已读
notificationService.markAsRead("notification-001");

// 发送消息
SendMessageRequest request = new SendMessageRequest();
request.setUserId("user123");
request.setTitle("新消息");
request.setContent("您有一条新消息");
request.setChannels(Arrays.asList("dingding", "email"));
SendMessageResultDTO result = notificationService.sendMessage(request);
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| MSG_PUSH_BASE_URL | string | http://localhost:8081 | 消息推送服务地址 |

## 许可证

Apache-2.0
