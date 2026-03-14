# LLM 服务对接 scene-engine 方案

## 1. SE LLM 架构分析

### 1.1 核心接口

```java
// SE 提供的 LLM 服务接口
public interface LlmService {
    ChatResponse chat(ChatRequest request);
    void chatStream(ChatRequest request, StreamHandler handler);
    String complete(String prompt, int maxTokens);
    
    // Provider 管理
    List<ProviderInfo> getProviders();
    List<ModelInfo> getModels(String providerId);
    void setActiveProvider(String providerId);
    void setActiveModel(String providerId, String modelId);
    
    // Function Calling
    void registerFunction(String functionId, FunctionConfig functionConfig);
    void unregisterFunction(String functionId);
}
```

### 1.2 配置层级

```java
// SE 的 LLM 配置
public class LlmConfig {
    private String endpoint;      // API 端点
    private String apiKey;        // API 密钥
    private String model;         // 模型名称
    private int maxTokens;        // 最大 Token
    private double temperature;   // 温度参数
    private long timeout;         // 超时时间
    private String provider;      // 提供者类型
}
```

### 1.3 上下文结构

```java
// SE 的运行时上下文
public class LlmRuntimeContext {
    private String systemPrompt;              // 系统提示词
    private List<Map<String, Object>> tools;  // 函数定义
    private List<Map<String, Object>> messages; // 消息历史
    
    // 子上下文
    private RoleContext roleContext;          // 角色上下文
    private KnowledgeContext knowledgeContext;// 知识库上下文
    private FunctionContext functionContext;  // 函数上下文
    private MemoryContext memoryContext;      // 记忆上下文
}
```

## 2. 对接方案

### 2.1 skill-llm 作为 SE LlmService 的实现

```
┌─────────────────────────────────────────────────────────┐
│                    scene-engine                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │              LlmService (接口)                   │   │
│  └─────────────────────────────────────────────────┘   │
│                          ▲                              │
│                          │ 实现                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │           skill-llm (实现模块)                   │   │
│  │  ┌─────────────────────────────────────────┐    │   │
│  │  │        LlmServiceImpl                   │    │   │
│  │  │  - DeepSeek Provider                    │    │   │
│  │  │  - OpenAI Provider                      │    │   │
│  │  │  - Qianwen Provider                     │    │   │
│  │  └─────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### 2.2 配置来源

```
配置优先级（从高到低）:
1. SESSION 级 - 会话临时配置
2. SCENE_STEP 级 - 场景步骤配置
3. SKILL 级 - Skill 级配置 ← skill-llm 使用
4. SCENE 级 - 场景配置
5. PERSONAL 级 - 个人配置
6. ENTERPRISE 级 - 企业配置
7. SYSTEM 级 - 系统默认
```

### 2.3 配置存储

```
data/
├── llm/
│   ├── config.json           # 全局 LLM 配置
│   ├── providers/
│   │   ├── deepseek.json     # DeepSeek 配置
│   │   ├── openai.json       # OpenAI 配置
│   │   └── qianwen.json      # 通义千问配置
│   └── sessions/
│       └── {sessionId}.json  # 会话级配置
```

## 3. 实现步骤

### 3.1 创建 SE 兼容的 LlmService 实现

```java
// skill-llm 模块
package net.ooder.skill.llm.service;

import net.ooder.scene.llm.LlmService;
import net.ooder.scene.llm.config.LlmConfig;

public class SeLlmServiceImpl implements LlmService {
    
    private final Map<String, LlmProvider> providers;
    private String activeProvider;
    private String activeModel;
    
    @Override
    public ChatResponse chat(ChatRequest request) {
        LlmProvider provider = providers.get(activeProvider);
        return provider.chat(request);
    }
    
    @Override
    public void chatStream(ChatRequest request, StreamHandler handler) {
        LlmProvider provider = providers.get(activeProvider);
        provider.chatStream(request, handler);
    }
}
```

### 3.2 配置加载

```java
// 从 Skills 配置加载 LLM 配置
public class LlmConfigLoader {
    
    public LlmConfig loadFromSkillConfig(String skillId) {
        // 1. 加载 skill.yaml 中的 llm 配置
        // 2. 加载 data/llm/providers/{providerId}.json
        // 3. 合并配置
    }
}
```

### 3.3 上下文组装

```java
// 使用 SE 的上下文组装器
public class LlmRuntimeContextAssembler {
    
    public LlmRuntimeContext assemble(AssemblyRequest request) {
        LlmRuntimeContext context = new LlmRuntimeContext();
        
        // 1. 组装角色上下文
        context.setRoleContext(buildRoleContext(request.getSkillId()));
        
        // 2. 组装知识库上下文
        context.setKnowledgeContext(buildKnowledgeContext(request.getQuery()));
        
        // 3. 组装函数上下文
        context.setFunctionContext(buildFunctionContext(request.getSkillId()));
        
        // 4. 组装记忆上下文
        context.setMemoryContext(buildMemoryContext(request.getSessionId()));
        
        // 5. 生成系统提示词
        context.setSystemPrompt(assembleSystemPrompt(context));
        
        return context;
    }
}
```

## 4. API 端点映射

### 4.1 skill-llm API → SE LlmService

| skill-llm API | SE LlmService 方法 |
|---------------|-------------------|
| POST /api/v1/llm/chat | chat(ChatRequest) |
| POST /api/v1/llm/stream | chatStream(ChatRequest, StreamHandler) |
| GET /api/v1/llm/providers | getProviders() |
| GET /api/v1/llm/models | getModels(providerId) |
| POST /api/v1/llm/providers/{id}/configure | setActiveProvider() + 配置持久化 |

### 4.2 配置 API

```
POST /api/v1/llm/config
{
    "provider": "deepseek",
    "apiKey": "sk-xxx",
    "baseUrl": "https://api.deepseek.com/v1",
    "model": "deepseek-chat",
    "level": "SKILL"  // 配置层级
}
```

## 5. 依赖关系

```xml
<!-- skill-llm pom.xml -->
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>scene-engine</artifactId>
        <version>2.3.1</version>
        <scope>provided</scope>
    </dependency>
    
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>skill-common</artifactId>
        <version>2.3.1</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## 6. 下一步行动

1. **修改 skill-llm 实现 SE LlmService 接口**
2. **使用 SE 的 LlmConfig 和 ChatRequest 类**
3. **实现配置从 Skills 配置加载**
4. **实现上下文组装器**
5. **测试与 SE 的集成**
