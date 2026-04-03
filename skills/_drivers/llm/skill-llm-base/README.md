# skill-llm-base

LLM驱动基类模块，定义LLM Provider标准接口和数据模型。

## 功能特性

- **LlmProvider接口** - LLM提供商标准接口定义
- **数据模型** - 请求/响应/消息/使用量等核心模型
- **Provider注册表** - 动态Provider发现与管理
- **流式回调** - SSE流式输出支持

## 核心接口

### LlmProvider

```java
public interface LlmProvider {
    String getProviderId();           // 获取Provider ID
    String getProviderName();         // 获取Provider名称
    List<LlmModel> getAvailableModels(); // 获取可用模型列表
    LlmResponse chat(LlmRequest request);  // 同步对话
    void streamChat(LlmRequest request, LlmStreamCallback callback); // 流式对话
    LlmResponse complete(LlmRequest request); // 文本补全
    int[] getEmbedding(String text);  // 获取向量嵌入
    int countTokens(String text);     // 计算Token数
    boolean isAvailable();            // 检查可用性
    Map<String, Object> getProviderConfig(); // 获取配置
}
```

## 数据模型

### LlmRequest

| 字段 | 类型 | 说明 |
|------|------|------|
| messages | List<LlmMessage> | 消息列表 |
| model | String | 模型ID |
| maxTokens | int | 最大Token数 |
| temperature | double | 温度参数 |
| stream | boolean | 是否流式 |

### LlmResponse

| 字段 | 类型 | 说明 |
|------|------|------|
| id | String | 响应ID |
| content | String | 响应内容 |
| model | String | 使用的模型 |
| usage | LlmUsage | Token使用量 |
| finishReason | String | 结束原因 |

### LlmMessage

| 字段 | 类型 | 说明 |
|------|------|------|
| role | Role | 角色(system/user/assistant) |
| content | String | 消息内容 |
| name | String | 名称(可选) |

### LlmUsage

| 字段 | 类型 | 说明 |
|------|------|------|
| promptTokens | int | 提示Token数 |
| completionTokens | int | 补全Token数 |
| totalTokens | int | 总Token数 |

## 已实现驱动

| 驱动 | Provider ID | 说明 |
|------|-------------|------|
| skill-llm-deepseek | deepseek | DeepSeek模型驱动 |
| skill-llm-openai | openai | OpenAI GPT模型驱动 |
| skill-llm-qianwen | qianwen | 通义千问模型驱动 |
| skill-llm-volcengine | volcengine | 火山引擎豆包模型驱动 |
| skill-llm-ollama | ollama | Ollama本地模型驱动 |
| skill-llm-baidu | baidu | 百度文心模型驱动 |

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-llm-base</artifactId>
    <version>3.0.1</version>
</dependency>
```

### 实现自定义Provider

```java
public class MyLlmProvider implements LlmProvider {
    
    @Override
    public String getProviderId() {
        return "my-provider";
    }
    
    @Override
    public String getProviderName() {
        return "My LLM Provider";
    }
    
    @Override
    public LlmResponse chat(LlmRequest request) {
        // 实现对话逻辑
    }
    
    // ... 其他方法实现
}
```

### 注册Provider

```java
// 方式1: Spring自动注册
@Component
public class MyLlmProvider implements LlmProvider {
    // 自动注册到LlmProviderRegistry
}

// 方式2: SPI服务发现
// 在 META-INF/services/net.ooder.skill.llm.base.LlmProvider 文件中添加:
// com.example.MyLlmProvider
```

## 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| llm.default-provider | string | deepseek | 默认Provider |
| llm.timeout | int | 60000 | 请求超时(ms) |
| llm.retry-count | int | 3 | 重试次数 |

## 许可证

Apache-2.0
