# skill-support

支持服务 - 提供系统支持和帮助功能。

## 功能特性

- **帮助文档** - 提供系统帮助文档
- **FAQ管理** - 管理常见问题
- **问题反馈** - 用户问题反馈
- **支持请求** - 处理支持请求

## 核心接口

### SupportController

支持服务控制器。

```java
@RestController
@RequestMapping("/api/v1/support")
public class SupportController {
    /**
     * 获取帮助文档
     */
    @GetMapping("/help")
    public List<HelpDocumentDTO> getHelpDocuments();
    
    /**
     * 获取FAQ
     */
    @GetMapping("/faq")
    public List<FAQDTO> getFAQs();
    
    /**
     * 提交反馈
     */
    @PostMapping("/feedback")
    public FeedbackDTO submitFeedback(@RequestBody FeedbackRequest request);
    
    /**
     * 创建支持请求
     */
    @PostMapping("/tickets")
    public SupportTicketDTO createTicket(@RequestBody CreateTicketRequest request);
}
```

## API 端点

| 端点 | 方法 | 描述 |
|------|------|------|
| /api/v1/support/help | GET | 获取帮助文档 |
| /api/v1/support/faq | GET | 获取FAQ |
| /api/v1/support/feedback | POST | 提交反馈 |
| /api/v1/support/tickets | POST | 创建支持请求 |

## 支持模型

### SupportTicketDTO

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 工单ID |
| subject | String | 主题 |
| description | String | 描述 |
| priority | Priority | 优先级 |
| status | TicketStatus | 状态 |
| createdAt | Long | 创建时间 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-support</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用示例

```java
@Autowired
private SupportService supportService;

// 获取帮助文档
List<HelpDocumentDTO> docs = supportService.getHelpDocuments();

// 获取FAQ
List<FAQDTO> faqs = supportService.getFAQs();

// 提交反馈
FeedbackRequest request = new FeedbackRequest();
request.setContent("系统运行良好");
FeedbackDTO feedback = supportService.submitFeedback(request);

// 创建支持请求
CreateTicketRequest ticketRequest = new CreateTicketRequest();
ticketRequest.setSubject("功能请求");
ticketRequest.setDescription("希望增加新功能");
SupportTicketDTO ticket = supportService.createTicket(ticketRequest);
```

## 许可证

Apache-2.0
