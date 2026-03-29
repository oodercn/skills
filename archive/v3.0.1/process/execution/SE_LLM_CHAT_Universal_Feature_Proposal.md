# LLM-CHAT 通用功能建设方案

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-10 |
| 提出方 | skill-scene 团队 |
| 目标团队 | SE (SkillsEngine) 团队 |

---

## 一、现状分析

### 1.1 当前实现架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        当前 llm-chat 实现架构                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐         │
│  │  LlmController  │───▶│  LlmProvider    │───▶│  DeepSeek API   │         │
│  │  (REST API)     │    │  (Interface)    │    │  (外部服务)      │         │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘         │
│          │                      │                                          │
│          │                      │                                          │
│          ▼                      ▼                                          │
│  ┌─────────────────┐    ┌─────────────────┐                               │
│  │ SkillActivation │    │  ToolRegistry   │                               │
│  │ Service         │    │  ToolOrchestrator│                              │
│  └─────────────────┘    └─────────────────┘                               │
│                                                                             │
│  问题点：                                                                    │
│  1. 配置分散在多个 @Value 注解中                                             │
│  2. Provider 需要手动管理和初始化                                            │
│  3. System Prompt 硬编码在 Controller 中                                    │
│  4. Tool 注册逻辑分散                                                        │
│  5. 上下文生命周期管理复杂                                                    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 当前代码问题详解

#### 问题1：配置分散
```java
// 当前实现 - 配置分散在多处
@Value("${ooder.llm.provider:mock}")
private String configProvider;

@Value("${ooder.llm.model:default}")
private String configModel;

@Value("${ooder.llm.baidu.api-key:}")
private String baiduApiKey;

@Value("${ooder.llm.deepseek.api-key:}")
private String deepseekApiKey;
```

#### 问题2：Provider 手动管理
```java
// 当前实现 - 需要手动初始化每个 Provider
private void initDeepSeekProvider() {
    if (deepseekApiKey != null && !deepseekApiKey.isEmpty()) {
        DeepSeekLlmProvider deepseekProvider = new DeepSeekLlmProvider();
        deepseekProvider.setApiKey(deepseekApiKey);
        deepseekProvider.setToolRegistry(toolRegistry);
        providers.put("deepseek", deepseekProvider);
    }
}
```

#### 问题3：System Prompt 硬编码
```java
// 当前实现 - System Prompt 硬编码在 Controller 中
private String getSystemPrompt() {
    StringBuilder prompt = new StringBuilder();
    prompt.append("你是Ooder场景技能平台的智能助手。\n\n");
    // ... 大量硬编码内容
    return prompt.toString();
}
```

---

## 二、建设目标

### 2.1 目标架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        目标架构 - 配置驱动 + SE 接口                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        SE SDK 统一入口                                │   │
│  │  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐        │   │
│  │  │ LlmService│  │ToolService│  │ContextMgr │  │PromptMgr  │        │   │
│  │  └───────────┘  └───────────┘  └───────────┘  └───────────┘        │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│          │                      │                      │                   │
│          ▼                      ▼                      ▼                   │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐        │
│  │ llm-config.yaml │    │ tools.yaml      │    │ prompts.yaml    │        │
│  │ (配置文件)       │    │ (工具定义)       │    │ (提示词模板)    │        │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘        │
│                                                                             │
│  业务层只需：                                                                │
│  1. 编写配置文件 (YAML)                                                      │
│  2. 调用 SE 统一接口                                                         │
│  3. 无需关心 Provider 初始化、Tool 注册等细节                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 核心设计原则

| 原则 | 说明 |
|------|------|
| **配置驱动** | 所有 LLM 相关配置通过 YAML 文件管理 |
| **接口统一** | SE SDK 提供统一的 LLM 服务接口 |
| **自动发现** | Provider 自动发现和注册 |
| **模板化** | System Prompt 支持模板化和动态注入 |
| **生命周期管理** | SE SDK 自动管理上下文生命周期 |

---

## 三、SE SDK 接口设计

### 3.1 LLM 配置接口

```yaml
# llm-config.yaml - LLM 统一配置文件
apiVersion: ooder.net/v1
kind: LlmConfig

metadata:
  name: default-llm-config
  version: "1.0.0"

spec:
  # 默认 Provider
  defaultProvider: deepseek
  defaultModel: deepseek-chat
  
  # Provider 配置
  providers:
    - id: deepseek
      type: deepseek
      enabled: true
      config:
        apiKey: ${DEEPSEEK_API_KEY}
        baseUrl: https://api.deepseek.com/v1
      models:
        - id: deepseek-chat
          name: DeepSeek Chat
          contextWindow: 64000
          supportsFunctionCalling: true
          supportsStreaming: true
        - id: deepseek-coder
          name: DeepSeek Coder
          contextWindow: 16000
          supportsFunctionCalling: true
          supportsStreaming: true
          
    - id: baidu
      type: baidu
      enabled: true
      config:
        apiKey: ${BAIDU_API_KEY}
        secretKey: ${BAIDU_SECRET_KEY}
      models:
        - id: ernie-bot-4
          name: 文心一言 4.0
          contextWindow: 8000
          supportsFunctionCalling: true
          
    - id: openai
      type: openai
      enabled: false
      config:
        apiKey: ${OPENAI_API_KEY}
        baseUrl: https://api.openai.com/v1
      models:
        - id: gpt-4
          name: GPT-4
          contextWindow: 8192
          
    - id: mock
      type: mock
      enabled: true
      config:
        responseDelay: 100ms
      models:
        - id: default
          name: Mock Model
          
  # 全局默认参数
  defaults:
    temperature: 0.7
    maxTokens: 4096
    timeout: 60s
    
  # 重试策略
  retry:
    maxAttempts: 3
    backoff: exponential
    initialDelay: 1s
    maxDelay: 30s
    
  # 降级策略
  fallback:
    enabled: true
    provider: mock
    model: default
```

### 3.2 LlmService 接口定义

```java
package net.ooder.scene.llm;

/**
 * LLM 统一服务接口 - SE SDK 提供
 */
public interface LlmService {
    
    /**
     * 同步聊天
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 流式聊天
     */
    Flux<ChatChunk> chatStream(ChatRequest request);
    
    /**
     * 文本补全
     */
    String complete(String prompt, CompleteOptions options);
    
    /**
     * 翻译
     */
    String translate(String text, String targetLang, String sourceLang);
    
    /**
     * 摘要
     */
    String summarize(String text, int maxLength);
    
    /**
     * 向量化
     */
    List<float[]> embed(List<String> texts, String model);
    
    /**
     * 结构化输出
     */
    <T> T structuredOutput(String prompt, Class<T> schema, StructuredOptions options);
    
    /**
     * 获取可用 Provider 列表
     */
    List<ProviderInfo> getProviders();
    
    /**
     * 获取可用模型列表
     */
    List<ModelInfo> getModels(String providerId);
    
    /**
     * 切换当前 Provider/Model
     */
    void setActiveModel(String providerId, String modelId);
    
    /**
     * 获取当前配置
     */
    LlmConfig getConfig();
}

/**
 * 聊天请求
 */
public class ChatRequest {
    private String message;
    private String conversationId;
    private String providerId;
    private String modelId;
    private List<Message> history;
    private Map<String, Object> context;
    private PromptTemplate promptTemplate;
    private List<ToolBinding> tools;
    private ChatOptions options;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        public Builder message(String message) { ... }
        public Builder provider(String providerId) { ... }
        public Builder model(String modelId) { ... }
        public Builder history(List<Message> history) { ... }
        public Builder context(Map<String, Object> context) { ... }
        public Builder promptTemplate(String templateId) { ... }
        public Builder tools(List<ToolBinding> tools) { ... }
        public Builder temperature(double temperature) { ... }
        public Builder maxTokens(int maxTokens) { ... }
        public ChatRequest build() { ... }
    }
}

/**
 * 聊天响应
 */
public class ChatResponse {
    private String content;
    private String providerId;
    private String modelId;
    private Map<String, Object> action;
    private List<ToolCall> toolCalls;
    private UsageInfo usage;
    private boolean error;
    private String errorMessage;
}
```

### 3.3 Prompt 模板管理接口

```yaml
# prompts.yaml - Prompt 模板配置
apiVersion: ooder.net/v1
kind: PromptRegistry

metadata:
  name: default-prompts
  version: "1.0.0"

spec:
  templates:
    # 场景技能助手
    - id: scene-skill-assistant
      name: 场景技能助手
      description: Ooder场景技能平台智能助手
      category: assistant
      
      systemPrompt: |
        你是Ooder场景技能平台的智能助手。
        
        ## 平台简介
        Ooder是一个场景驱动的技能管理平台，用户可以通过发现、安装、配置能力来构建自动化场景。
        
        ## 技能分类体系 v3.0
        ### 技能形态(SkillForm)
        - **SCENE**: 场景技能 - 具有完整场景流程的技能
        - **STANDALONE**: 独立技能 - 独立运行的功能单元
        
        ### 场景类型(SceneType)
        - **AUTO**: 自驱场景 - 自动驱动执行
        - **TRIGGER**: 触发场景 - 需要外部触发
        - **HYBRID**: 混合场景 - 结合自驱和触发
        
        {{#if activationContext}}
        ## 当前技能上下文
        {{activationContext}}
        {{/if}}
        
        {{#if availableTools}}
        ## 可用工具
        {{availableTools}}
        {{/if}}
        
      variables:
        - name: activationContext
          type: string
          description: 技能激活上下文
          required: false
        - name: availableTools
          type: string
          description: 可用工具列表
          required: false
          
    # 安装向导
    - id: install-wizard
      name: 安装向导
      description: 引导用户安装技能
      category: wizard
      
      systemPrompt: |
        你是Ooder平台的安装助手。
        
        ## 当前任务
        帮助用户安装场景技能：{{targetSkill.name}}
        
        ## 可用工具
        {{availableTools}}
        
        ## 约束
        1. 只使用提供的工具
        2. 输出必须是JSON格式
        3. 每个步骤完成后等待用户确认
        
      variables:
        - name: targetSkill
          type: object
          description: 目标技能信息
          required: true
```

```java
package net.ooder.scene.llm.prompt;

/**
 * Prompt 模板服务接口
 */
public interface PromptService {
    
    /**
     * 获取模板
     */
    PromptTemplate getTemplate(String templateId);
    
    /**
     * 渲染模板
     */
    String render(String templateId, Map<String, Object> variables);
    
    /**
     * 注册模板
     */
    void registerTemplate(PromptTemplate template);
    
    /**
     * 列出所有模板
     */
    List<PromptTemplate> listTemplates(String category);
}

/**
 * Prompt 模板
 */
public class PromptTemplate {
    private String id;
    private String name;
    private String description;
    private String category;
    private String systemPrompt;
    private List<TemplateVariable> variables;
    private Map<String, Object> defaults;
}
```

### 3.4 Tool 自动注册接口

```yaml
# tools.yaml - 工具定义配置
apiVersion: ooder.net/v1
kind: ToolRegistry

metadata:
  name: default-tools
  version: "1.0.0"

spec:
  # 工具分类
  categories:
    - id: discovery
      name: 发现能力
      description: 能力发现相关工具
      
    - id: install
      name: 安装管理
      description: 技能安装相关工具
      
    - id: config
      name: 配置管理
      description: 配置管理相关工具
      
  # 工具定义
  tools:
    - id: start-scan
      name: 开始扫描
      description: 启动能力发现扫描
      category: discovery
      
      parameters:
        - name: method
          type: string
          enum: [AUTO, LOCAL, GITEE, GITHUB, GIT]
          default: AUTO
          description: 扫描方法
          
      returns:
        type: object
        properties:
          success: boolean
          message: string
          scanId: string
          
      handler:
        type: spring-bean
        bean: capabilityDiscoveryService
        method: startScan
        
    - id: filter-capabilities
      name: 筛选能力
      description: 按条件筛选能力列表
      category: discovery
      
      parameters:
        - name: skillForm
          type: string
          enum: [SCENE, STANDALONE]
          description: 技能形态
          
        - name: sceneType
          type: string
          enum: [AUTO, TRIGGER, HYBRID]
          description: 场景类型
          
        - name: keyword
          type: string
          description: 关键词搜索
          
      handler:
        type: spring-bean
        bean: capabilityService
        method: filterCapabilities
        
    - id: install-capability
      name: 安装能力
      description: 安装指定能力
      category: install
      
      parameters:
        - name: capabilityId
          type: string
          required: true
          description: 能力ID
          
        - name: config
          type: object
          description: 安装配置
          
      handler:
        type: spring-bean
        bean: installService
        method: installCapability
```

```java
package net.ooder.scene.llm.tool;

/**
 * Tool 注册服务接口
 */
public interface ToolService {
    
    /**
     * 获取工具定义列表 (LLM 格式)
     */
    List<Map<String, Object>> getToolDefinitions();
    
    /**
     * 获取工具定义列表 (按分类)
     */
    List<Map<String, Object>> getToolDefinitions(String category);
    
    /**
     * 执行工具调用
     */
    ToolResult executeTool(String toolId, Map<String, Object> parameters);
    
    /**
     * 注册工具
     */
    void registerTool(ToolDefinition tool);
    
    /**
     * 批量注册工具
     */
    void registerTools(List<ToolDefinition> tools);
}

/**
 * 工具定义
 */
public class ToolDefinition {
    private String id;
    private String name;
    private String description;
    private String category;
    private List<ParameterDef> parameters;
    private ReturnDef returns;
    private HandlerDef handler;
}
```

### 3.5 上下文管理接口

```java
package net.ooder.scene.llm.context;

/**
 * LLM 上下文管理服务
 */
public interface ContextService {
    
    /**
     * 创建上下文
     */
    LlmContext createContext(ContextConfig config);
    
    /**
     * 获取上下文
     */
    LlmContext getContext(String contextId);
    
    /**
     * 更新上下文
     */
    void updateContext(String contextId, Map<String, Object> updates);
    
    /**
     * 销毁上下文
     */
    void destroyContext(String contextId);
    
    /**
     * 添加消息到历史
     */
    void addMessage(String contextId, Message message);
    
    /**
     * 获取对话历史
     */
    List<Message> getHistory(String contextId, int limit);
}

/**
 * LLM 上下文
 */
public class LlmContext {
    private String contextId;
    private String skillId;
    private String userId;
    private String roleId;
    private Map<String, Object> variables;
    private List<Message> history;
    private List<ToolBinding> tools;
    private PromptTemplate promptTemplate;
    private long createTime;
    private long lastAccessTime;
    
    /**
     * 构建系统提示词
     */
    public String buildSystemPrompt();
    
    /**
     * 获取工具定义
     */
    public List<Map<String, Object>> getToolDefinitions();
    
    /**
     * 执行工具
     */
    public ToolResult executeTool(String toolId, Map<String, Object> parameters);
}
```

---

## 四、业务层简化实现

### 4.1 简化后的 Controller

```java
@RestController
@RequestMapping("/api/llm")
public class LlmController {
    
    @Autowired
    private LlmService llmService;
    
    @Autowired
    private PromptService promptService;
    
    @Autowired
    private ToolService toolService;
    
    @Autowired
    private ContextService contextService;
    
    @PostMapping("/chat")
    public ResultModel<ChatResponseDTO> chat(@RequestBody ChatRequestDTO request) {
        // 1. 获取或创建上下文
        LlmContext context = contextService.getContext(request.getConversationId());
        if (context == null) {
            context = contextService.createContext(ContextConfig.builder()
                .skillId("skill-scene")
                .promptTemplate("scene-skill-assistant")
                .tools(Arrays.asList("start-scan", "filter-capabilities", "install-capability"))
                .build());
        }
        
        // 2. 构建请求
        ChatRequest chatRequest = ChatRequest.builder()
            .message(request.getMessage())
            .provider(request.getProvider())
            .model(request.getModel())
            .history(context.getHistory())
            .promptTemplate(context.getPromptTemplate())
            .tools(context.getTools())
            .temperature(request.getTemperature())
            .build();
        
        // 3. 调用 LLM 服务
        ChatResponse response = llmService.chat(chatRequest);
        
        // 4. 更新上下文
        contextService.addMessage(context.getContextId(), Message.user(request.getMessage()));
        contextService.addMessage(context.getContextId(), Message.assistant(response.getContent()));
        
        return ResultModel.success(ChatResponseDTO.from(response));
    }
    
    @GetMapping("/providers")
    public ResultModel<List<ProviderInfo>> getProviders() {
        return ResultModel.success(llmService.getProviders());
    }
    
    @GetMapping("/models")
    public ResultModel<List<ModelInfo>> getModels(@RequestParam(required = false) String provider) {
        return ResultModel.success(llmService.getModels(provider));
    }
}
```

### 4.2 配置文件示例

```yaml
# application.yml
ooder:
  llm:
    config-path: classpath:llm-config.yaml
    prompts-path: classpath:prompts.yaml
    tools-path: classpath:tools.yaml
```

---

## 五、实施计划

### 5.1 阶段划分

| 阶段 | 内容 | 工作量 | 优先级 |
|------|------|--------|--------|
| **P0** | LlmService 核心接口 | 2周 | 高 |
| **P0** | LLM 配置文件解析 | 1周 | 高 |
| **P1** | PromptService 模板管理 | 1周 | 高 |
| **P1** | ToolService 自动注册 | 1周 | 中 |
| **P2** | ContextService 上下文管理 | 1周 | 中 |
| **P2** | Provider SPI 扩展机制 | 1周 | 低 |

### 5.2 接口清单

| 接口 | 包名 | 说明 |
|------|------|------|
| `LlmService` | `net.ooder.scene.llm` | LLM 统一服务 |
| `PromptService` | `net.ooder.scene.llm.prompt` | Prompt 模板管理 |
| `ToolService` | `net.ooder.scene.llm.tool` | Tool 自动注册 |
| `ContextService` | `net.ooder.scene.llm.context` | 上下文管理 |
| `LlmProvider` | `net.ooder.scene.skill` | Provider SPI (已有) |

### 5.3 配置文件格式

| 文件 | 格式 | 说明 |
|------|------|------|
| `llm-config.yaml` | YAML | LLM 配置 |
| `prompts.yaml` | YAML | Prompt 模板 |
| `tools.yaml` | YAML | Tool 定义 |

---

## 六、收益分析

### 6.1 代码简化对比

| 指标 | 当前实现 | 目标实现 | 减少 |
|------|----------|----------|------|
| Controller 代码行数 | ~800 行 | ~100 行 | 87.5% |
| 配置管理代码 | ~150 行 | 0 行 (配置文件) | 100% |
| Provider 初始化代码 | ~100 行 | 0 行 (自动发现) | 100% |
| System Prompt 管理 | 硬编码 | 模板化 | - |

### 6.2 功能增强

| 功能 | 当前状态 | 目标状态 |
|------|----------|----------|
| Provider 切换 | 手动实现 | 配置驱动 |
| 模型切换 | 手动实现 | 配置驱动 |
| Prompt 模板 | 不支持 | 支持 |
| Tool 自动注册 | 部分支持 | 完全支持 |
| 上下文管理 | 手动管理 | 自动管理 |
| 重试/降级 | 不支持 | 支持 |

---

## 七、附录

### 7.1 相关文档

- [SE_SDK_Collaboration_Report.md](./SE_SDK_Collaboration_Report.md)
- [system-builtin-skills-and-llm-collaboration.md](./system-builtin-skills-and-llm-collaboration.md)
- [deep-analysis-scene-skill-classification.md](./deep-analysis-scene-skill-classification.md)

### 7.2 联系方式

- 提出方：skill-scene 团队
- 目标团队：SE (SkillsEngine) 团队

---

**文档状态**: 待评审  
**下一步**: SE 团队评审并确认实施方案
