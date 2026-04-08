# skill-im-weixin

微信IM驱动 - 提供个人微信消息发送、接收、事件订阅等功能。

## 功能特性

- **消息发送** - 发送微信消息
- **消息接收** - 接收微信消息回调
- **Markdown消息** - 发送Markdown格式消息
- **模板消息** - 发送模板消息

## 核心接口

### WeixinImService

微信IM服务接口。

```java
public interface WeixinImService {
    /**
     * 发送文本消息
     */
    SendMessageResult sendTextMessage(String toUser, String content);
    
    /**
     * 发送Markdown消息
     */
    SendMessageResult sendMarkdownMessage(String toUser, String markdown);
    
    /**
     * 发送模板消息
     */
    SendMessageResult sendTemplateMessage(String toUser, String templateId, Map<String, String> data);
}
```

## 配置项

| 配置项 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| appId | string | 是 | 微信AppID |
| appSecret | string | 是 | 微信AppSecret |
| token | string | 否 | Token |
| encodingAESKey | string | 否 | EncodingAESKey |
| callbackUrl | string | 否 | 回调URL |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-im-weixin</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private WeixinImService weixinImService;

// 发送文本消息
SendMessageResult result = weixinImService.sendTextMessage("user123", "Hello, World!");

// 发送Markdown消息
SendMessageResult mdResult = weixinImService.sendMarkdownMessage("user123", "# 标题\n内容");

// 发送模板消息
Map<String, String> data = new HashMap<>();
data.put("first", "您好");
data.put("keyword1", "订单号123");
SendMessageResult tplResult = weixinImService.sendTemplateMessage("user123", "template-001", data);
```

## 注意事项

- 本服务是个人微信IM驱动，与企业微信IM服务(skill-im-wecom)不同
- 需要在微信公众平台配置回调URL
- 建议使用加密模式，配置EncodingAESKey

## 许可证

Apache-2.0
