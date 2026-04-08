# ooder-spi-core

OoderOS 统一SPI核心接口模块，定义跨模块的标准接口和数据模型。

## 功能特性

- **核心接口** - PageResult分页结果、SpiServices门面服务
- **IM SPI** - IM消息投递驱动、消息内容模型
- **RAG SPI** - RAG检索增强驱动、知识库配置模型
- **Workflow SPI** - 工作流驱动接口

## 核心接口

### ImDeliveryDriver

IM消息投递驱动接口，用于统一不同IM平台的消息投递。

```java
public interface ImDeliveryDriver {
    /**
     * 投递消息
     */
    void deliver(DeliveryContext context, DeliveryTemplate template);
    
    /**
     * 投递上下文
     */
    class DeliveryContext {
        private String sceneGroupId;
        private String userId;
        private Map<String, Object> variables;
    }
    
    /**
     * 投递模板
     */
    class DeliveryTemplate {
        private String templateId;
        private String content;
        private List<String> channels;
    }
}
```

### RagEnhanceDriver

RAG检索增强驱动接口，用于知识库检索增强。

```java
public interface RagEnhanceDriver {
    /**
     * 增强检索
     */
    List<RagRelatedDocument> enhance(RagKnowledgeConfig config, String query);
}
```

### WorkflowDriver

工作流驱动接口，用于工作流执行。

```java
public interface WorkflowDriver {
    /**
     * 执行工作流
     */
    WorkflowResult execute(String workflowId, Map<String, Object> params);
    
    /**
     * 工作流结果
     */
    class WorkflowResult {
        private String executionId;
        private String status;
        private Map<String, Object> outputs;
    }
}
```

## 数据模型

### PageResult

通用分页结果模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| items | List<T> | 数据列表 |
| total | long | 总数 |
| page | int | 当前页 |
| pageSize | int | 每页大小 |

### MessageContent

IM消息内容模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| type | MessageType | 消息类型 |
| content | String | 消息内容 |
| attachments | List<Attachment> | 附件列表 |

### RagKnowledgeConfig

RAG知识库配置模型。

| 字段 | 类型 | 说明 |
|------|------|------|
| kbId | String | 知识库ID |
| topK | int | 返回文档数量 |
| threshold | double | 相似度阈值 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-spi-core</artifactId>
    <version>3.2.0</version>
</dependency>
```

### 实现ImDeliveryDriver

```java
public class MyImDeliveryDriver implements ImDeliveryDriver {
    
    @Override
    public void deliver(DeliveryContext context, DeliveryTemplate template) {
        // 实现消息投递逻辑
        System.out.println("Delivering message: " + template.getContent());
    }
}
```

### 实现RagEnhanceDriver

```java
public class MyRagEnhanceDriver implements RagEnhanceDriver {
    
    @Override
    public List<RagRelatedDocument> enhance(RagKnowledgeConfig config, String query) {
        // 实现RAG增强逻辑
        List<RagRelatedDocument> docs = new ArrayList<>();
        // 检索相关文档
        return docs;
    }
}
```

## SPI服务发现

支持通过SPI机制自动发现实现：

```
# META-INF/services/net.ooder.spi.im.ImDeliveryDriver
com.example.MyImDeliveryDriver

# META-INF/services/net.ooder.spi.rag.RagEnhanceDriver
com.example.MyRagEnhanceDriver
```

## 许可证

Apache-2.0
