# skill-spi-core

技能SPI核心接口模块，定义技能系统的核心SPI接口。

## 功能特性

- **ImService接口** - IM服务标准接口定义
- **消息模型** - MessageContent、SendResult等核心数据模型

## 核心接口

### ImService

IM服务标准接口。

```java
public interface ImService {
    /**
     * 发送消息
     */
    SendResult sendMessage(String userId, MessageContent message);
    
    /**
     * 批量发送消息
     */
    List<SendResult> sendBatch(List<String> userIds, MessageContent message);
}
```

## 数据模型

### MessageContent

消息内容模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| type | String | 消息类型 |
| content | String | 消息内容 |
| attachments | List<Attachment> | 附件列表 |

### SendResult

发送结果模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| success | boolean | 是否成功 |
| messageId | String | 消息ID |
| error | String | 错误信息 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-spi-core</artifactId>
    <version>3.0.2</version>
</dependency>
```

### 实现ImService

```java
public class MyImService implements ImService {
    
    @Override
    public SendResult sendMessage(String userId, MessageContent message) {
        // 实现消息发送逻辑
        SendResult result = new SendResult();
        result.setSuccess(true);
        result.setMessageId(UUID.randomUUID().toString());
        return result;
    }
    
    @Override
    public List<SendResult> sendBatch(List<String> userIds, MessageContent message) {
        // 实现批量发送逻辑
        return userIds.stream()
            .map(userId -> sendMessage(userId, message))
            .collect(Collectors.toList());
    }
}
```

## SPI服务发现

支持通过SPI机制自动发现实现：

```
# META-INF/services/net.ooder.spi.im.ImService
com.example.MyImService
```

## 许可证

Apache-2.0
