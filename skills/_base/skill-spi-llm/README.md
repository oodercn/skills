# skill-spi-llm

LLM SPI接口定义模块，定义LLM服务的标准接口和数据模型。

## 功能特性

- **LlmService接口** - LLM服务标准接口定义
- **LlmStreamHandler接口** - 流式处理回调接口
- **数据模型** - 请求/响应/配置/模型定义

## 核心接口

### LlmService

```java
public interface LlmService {
    /**
     * 获取Provider ID
     */
    String getProviderId();

    /**
     * 获取Provider名称
     */
    String getProviderName();

    /**
     * 获取可用模型列表
     */
    List<LlmModel> getAvailableModels();

    /**
     * 同步对话
     */
    LlmResponse chat(LlmRequest request);

    /**
     * 流式对话
     */
    void chatStream(LlmRequest request, LlmStreamHandler handler);

    /**
     * 检查服务可用性
     */
    boolean isAvailable();

    /**
     * 获取默认配置
     */
    LlmConfig getDefaultConfig();

    /**
     * 获取最大Token数
     */
    int getMaxTokens(String modelId);

    /**
     * 是否支持流式
     */
    boolean supportsStreaming(String modelId);
}
```

### LlmStreamHandler

```java
public interface LlmStreamHandler {
    /**
     * 处理流式数据块
     */
    void onChunk(String chunk);

    /**
     * 处理完成
     */
    void onComplete(LlmResponse response);

    /**
     * 处理错误
     */
    void onError(Throwable error);
}
```

## 数据模型

### LlmRequest

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| messages | List<LlmMessage> | 是 | 消息列表 |
| model | String | 是 | 模型ID |
| maxTokens | int | 否 | 最大Token数 |
| temperature | double | 否 | 温度参数(0-2) |
| stream | boolean | 否 | 是否流式输出 |
| stop | List<String> | 否 | 停止词列表 |

### LlmResponse

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 响应ID |
| content | String | 响应内容 |
| model | String | 使用的模型 |
| usage | LlmUsage | Token使用量 |
| finishReason | String | 结束原因(stop/length/error) |

### LlmMessage

| 字段 | 类型 | 说明 |
|------|------|------|
| role | Role | 角色(system/user/assistant) |
| content | String | 消息内容 |
| name | String | 名称(可选) |

### LlmModel

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 模型ID |
| name | String | 模型名称 |
| provider | String | 提供商 |
| maxTokens | int | 最大Token数 |
| supportsStreaming | boolean | 是否支持流式 |

### LlmConfig

| 字段 | 类型 | 说明 |
|------|------|------|
| providerId | String | Provider ID |
| model | String | 默认模型 |
| apiKey | String | API Key(加密存储) |
| baseUrl | String | API基础URL |
| timeout | int | 超时时间(ms) |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-spi-llm</artifactId>
    <version>3.0.1</version>
</dependency>
```

### 实现LlmService

```java
public class MyLlmService implements LlmService {
    
    @Override
    public String getProviderId() {
        return "my-provider";
    }
    
    @Override
    public LlmResponse chat(LlmRequest request) {
        // 实现对话逻辑
        LlmResponse response = new LlmResponse();
        response.setId(UUID.randomUUID().toString());
        response.setContent("Hello, I am your assistant.");
        response.setModel(request.getModel());
        return response;
    }
    
    @Override
    public void chatStream(LlmRequest request, LlmStreamHandler handler) {
        // 实现流式对话
        handler.onChunk("Hello");
        handler.onChunk(", ");
        handler.onChunk("world!");
        
        LlmResponse response = new LlmResponse();
        response.setContent("Hello, world!");
        handler.onComplete(response);
    }
    
    // ... 其他方法实现
}
```

## SPI服务发现

支持通过SPI机制自动发现LlmService实现：

```
# META-INF/services/net.ooder.spi.llm.LlmService
com.example.MyLlmService
```

## 许可证

Apache-2.0
