# skill-spi-messaging

统一消息服务SPI模块，定义统一消息系统的标准接口和数据模型。

## 功能特性

- **UnifiedMessagingService** - 统一消息服务接口
- **UnifiedSessionService** - 统一会话服务接口
- **UnifiedWebSocketService** - 统一WebSocket服务接口
- **完整消息模型** - 支持多种消息类型和会话类型

## 核心接口

### UnifiedMessagingService

统一消息服务接口。

```java
public interface UnifiedMessagingService {
    /**
     * 发送消息
     */
    UnifiedMessage sendMessage(SendMessageRequest request);
    
    /**
     * 获取消息列表
     */
    List<UnifiedMessage> getMessages(String sessionId, int limit, String cursor);
    
    /**
     * 标记消息已读
     */
    void markAsRead(String sessionId, List<String> messageIds);
}
```

### UnifiedSessionService

统一会话服务接口。

```java
public interface UnifiedSessionService {
    /**
     * 创建会话
     */
    UnifiedSession createSession(CreateSessionRequest request);
    
    /**
     * 获取会话
     */
    UnifiedSession getSession(String sessionId);
    
    /**
     * 获取用户会话列表
     */
    List<UnifiedSession> getUserSessions(String userId);
}
```

### UnifiedWebSocketService

统一WebSocket服务接口。

```java
public interface UnifiedWebSocketService {
    /**
     * 生成WebSocket令牌
     */
    WsToken generateToken(String userId);
    
    /**
     * 刷新WebSocket令牌
     */
    WsToken refreshToken(String refreshToken);
}
```

## 数据模型

### UnifiedMessage

统一消息模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 消息ID |
| sessionId | String | 会话ID |
| senderId | String | 发送者ID |
| content | Content | 消息内容 |
| type | MessageType | 消息类型 |
| status | MessageStatus | 消息状态 |
| createdAt | long | 创建时间 |

### UnifiedSession

统一会话模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 会话ID |
| type | SessionType | 会话类型 |
| participants | List<Participant> | 参与者列表 |
| lastMessage | UnifiedMessage | 最后一条消息 |
| unreadCount | int | 未读消息数 |

### Content

消息内容模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| text | String | 文本内容 |
| attachments | List<Attachment> | 附件列表 |

### Participant

参与者模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | String | 用户ID |
| role | String | 角色 |
| joinedAt | long | 加入时间 |

## 消息类型

- **TEXT** - 文本消息
- **IMAGE** - 图片消息
- **FILE** - 文件消息
- **AUDIO** - 音频消息
- **VIDEO** - 视频消息
- **LOCATION** - 位置消息
- **SYSTEM** - 系统消息

## 会话类型

- **PRIVATE** - 私聊
- **GROUP** - 群聊
- **CHANNEL** - 频道

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-spi-messaging</artifactId>
    <version>3.0.2</version>
</dependency>
```

### 实现UnifiedMessagingService

```java
public class MyMessagingService implements UnifiedMessagingService {
    
    @Override
    public UnifiedMessage sendMessage(SendMessageRequest request) {
        // 实现消息发送逻辑
        UnifiedMessage message = new UnifiedMessage();
        message.setId(UUID.randomUUID().toString());
        message.setSessionId(request.getSessionId());
        message.setSenderId(request.getSenderId());
        message.setContent(request.getContent());
        message.setCreatedAt(System.currentTimeMillis());
        return message;
    }
    
    @Override
    public List<UnifiedMessage> getMessages(String sessionId, int limit, String cursor) {
        // 实现获取消息列表逻辑
        return new ArrayList<>();
    }
    
    @Override
    public void markAsRead(String sessionId, List<String> messageIds) {
        // 实现标记已读逻辑
    }
}
```

### 实现UnifiedSessionService

```java
public class MySessionService implements UnifiedSessionService {
    
    @Override
    public UnifiedSession createSession(CreateSessionRequest request) {
        // 实现创建会话逻辑
        UnifiedSession session = new UnifiedSession();
        session.setId(UUID.randomUUID().toString());
        session.setType(request.getType());
        session.setParticipants(request.getParticipants());
        session.setCreatedAt(System.currentTimeMillis());
        return session;
    }
    
    @Override
    public UnifiedSession getSession(String sessionId) {
        // 实现获取会话逻辑
        return null;
    }
    
    @Override
    public List<UnifiedSession> getUserSessions(String userId) {
        // 实现获取用户会话列表逻辑
        return new ArrayList<>();
    }
}
```

## SPI服务发现

支持通过SPI机制自动发现实现：

```
# META-INF/services/net.ooder.spi.messaging.UnifiedMessagingService
com.example.MyMessagingService

# META-INF/services/net.ooder.spi.messaging.UnifiedSessionService
com.example.MySessionService
```

## 许可证

Apache-2.0
