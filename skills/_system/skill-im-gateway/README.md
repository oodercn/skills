# skill-im-gateway

IM网关服务 - 提供IM消息网关、多渠道消息投递、RAG增强等功能。

## 功能特性

- **消息网关** - 统一消息网关，支持多渠道消息投递
- **MQTT通道** - MQTT协议通道适配
- **RAG增强** - 检索增强生成功能
- **Webhook支持** - Webhook回调支持

## 核心接口

### MessageGateway

消息网关接口。

```java
public interface MessageGateway {
    /**
     * 发送消息
     */
    void sendMessage(MultiChannelMessageDTO message);
    
    /**
     * 注册通道
     */
    void registerChannel(String channelId, Channel channel);
    
    /**
     * 健康检查
     */
    GatewayHealthDTO health();
}
```

### MqttChannelAdapter

MQTT通道适配器。

```java
public class MqttChannelAdapter {
    /**
     * 连接MQTT代理
     */
    void connect(String brokerUrl, String clientId);
    
    /**
     * 订阅主题
     */
    void subscribe(String topic);
    
    /**
     * 发布消息
     */
    void publish(String topic, String message);
}
```

### RagEnhancer

RAG增强器。

```java
public class RagEnhancer {
    /**
     * 增强消息
     */
    String enhance(String message, String context);
    
    /**
     * 检索相关文档
     */
    List<Document> retrieve(String query);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/im-gateway/send | POST | 发送消息 |
| /api/v1/im-gateway/health | GET | 健康检查 |
| /webhook/callback | POST | Webhook回调 |

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| MQTT_BROKER_URL | string | tcp://localhost:1883 | MQTT代理地址 |
| MQTT_CLIENT_ID | string | ooder-im-gateway | MQTT客户端ID |
| RAG_ENABLED | boolean | true | 是否启用RAG增强 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-im-gateway</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private MessageGateway messageGateway;

// 发送消息
MultiChannelMessageDTO message = new MultiChannelMessageDTO();
message.setChannels(Arrays.asList("dingding", "feishu"));
message.setContent("Hello, World!");
messageGateway.sendMessage(message);

// 健康检查
GatewayHealthDTO health = messageGateway.health();
```

## 许可证

Apache-2.0
