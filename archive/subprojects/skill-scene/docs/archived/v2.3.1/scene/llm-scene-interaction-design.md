# LLM与场景技能交互设计方案（讨论稿）

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.2-draft |
| 创建日期 | 2026-03-09 |
| 更新日期 | 2026-03-09 |
| 所属模块 | skill-scene / engine |
| 状态 | 讨论中 |
| 作者 | AI Assistant |

---

## 一、背景与目标

### 1.1 背景

在智能安装和激活流程中，LLM需要与前端页面进行交互，包括：
- 感知当前页面状态和上下文
- 执行页面操作（选择能力、开始安装、配置参数等）
- 引导用户完成复杂流程

### 1.2 目标

1. **安全性**：LLM只能执行当前模块允许的操作
2. **可扩展性**：支持多种脚本类型（MVEL、JavaScript）
3. **可复用性**：程序助手能力应作为通用Skills，供所有模块复用
4. **分层清晰**：Engine层提供基础能力，Skill层实现业务逻辑

### 1.3 核心设计决策（讨论确定）

| 决策项 | 决策内容 | 说明 |
|--------|----------|------|
| 安全边界 | LLM仅能执行当前模块的API | 模块级API权限控制，防止越权操作 |
| 脚本执行 | 使用内置MVEL引擎 | LLM生成MVEL脚本+JavaScript脚本 |
| 上下文同步 | 由LLM决定是否同步 | 公共信任层，LLM通过syncContext字段控制 |
| 程序助手 | 采用Skills架构实现模块复用 | 不单独建立，作为通用可复用能力 |

---

## 二、分层架构设计

### 2.1 架构总览

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    LLM与场景技能交互分层架构                                      │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                          应用层 (Application)                              │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐      │  │
│  │  │ 发现页面    │  │ 安装向导    │  │ 激活配置    │  │ 其他页面    │      │  │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘      │  │
│  │         │                │                │                │             │  │
│  └─────────┼────────────────┼────────────────┼────────────────┼─────────────┘  │
│            │                │                │                │                 │
│            ▼                ▼                ▼                ▼                 │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                          Skill层 (场景技能)                                │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐  │  │
│  │  │  skill-scene (场景管理技能)                                          │  │  │
│  │  │  - ModuleApiRegistry     模块API注册表                               │  │  │
│  │  │  - ContextProvider       上下文提供者                                │  │  │
│  │  │  - ActionExecutor        动作执行器                                  │  │  │
│  │  └─────────────────────────────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐  │  │
│  │  │  skill-program-assistant (程序助手技能) - 通用可复用                  │  │  │
│  │  │  - ScriptGeneration      脚本生成                                    │  │  │
│  │  │  - CodeCompletion        代码补全                                    │  │  │
│  │  │  - CodeExplanation       代码解释                                    │  │  │
│  │  └─────────────────────────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                      │                                          │
│                                      │ 调用基础能力                              │
│                                      ▼                                          │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                          Engine层 (引擎层)                                 │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐  │  │
│  │  │  脚本执行引擎                                                        │  │  │
│  │  │  - MvelScriptExecutor    MVEL脚本执行器（内置）                      │  │  │
│  │  │  - JavaScriptExecutor    JavaScript执行器                           │  │  │
│  │  │  - ScriptValidator       脚本安全验证                                │  │  │
│  │  │  - Sandboxing            沙箱隔离                                    │  │  │
│  │  └─────────────────────────────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐  │  │
│  │  │  公共信任层                                                          │  │  │
│  │  │  - ContextTrustLayer     公共信任层                                  │  │  │
│  │  │  - ContextSyncPolicy     同步策略（LLM决策）                         │  │  │
│  │  │  - SessionManager        会话管理                                    │  │  │
│  │  └─────────────────────────────────────────────────────────────────────┘  │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐  │  │
│  │  │  LLM核心服务                                                         │  │  │
│  │  │  - LlmProvider           LLM提供商接口                               │  │  │
│  │  │  - FunctionCalling       函数调用支持                                │  │  │
│  │  │  - PromptTemplate        提示词模板                                  │  │  │
│  │  └─────────────────────────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 层级职责划分

| 层级 | 职责 | 示例组件 |
|------|------|----------|
| **Engine层** | 提供基础能力，与具体业务无关 | MVEL执行器、公共信任层、LLM提供商 |
| **Skill层** | 实现业务逻辑，可复用的能力单元 | 程序助手、场景管理、文档助手 |
| **Application层** | 具体页面和应用 | 发现页面、安装向导、激活配置 |

---

## 三、安全机制设计

### 3.1 模块级API权限控制

**核心原则**：LLM仅能执行当前模块的API，不能跨模块操作。

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    模块级API权限控制架构                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  模块API注册表 (ModuleApiRegistry)                                       │    │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐          │    │
│  │  │ discovery模块   │  │ install模块     │  │ activation模块  │          │    │
│  │  │ ─────────────── │  │ ─────────────── │  │ ─────────────── │          │    │
│  │  │ selectCapability│  │ startInstall    │  │ executeStep     │          │    │
│  │  │ startScan       │  │ setConfig       │  │ skipStep        │          │    │
│  │  │ filterCap...    │  │ nextStep        │  │ selectDriver... │          │    │
│  │  │ getDetail       │  │ confirm         │  │ complete        │          │    │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘          │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                      │                                          │
│                                      │ 当前模块标识                             │
│                                      ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  权限验证流程                                                            │    │
│  │                                                                         │    │
│  │  LLM请求执行: filterCapabilities(['keyword': '日志'])                   │    │
│  │       │                                                                 │    │
│  │       ▼                                                                 │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │ 1. 检查当前模块: currentModule = "discovery"                     │   │    │
│  │  │ 2. 检查API白名单: filterCapabilities ∈ discovery.apis? ✓        │   │    │
│  │  │ 3. 验证参数: keyword参数合法 ✓                                   │   │    │
│  │  │ 4. 执行API                                                       │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  │                                                                         │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
│  跨模块操作（需要用户确认）:                                                    │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ LLM请求: startInstall('daily-log-scene')                                │    │
│  │ 当前模块: discovery                                                      │    │
│  │ 目标模块: install                                                        │    │
│  │ 结果: 模块不匹配 → 提示用户确认 → 用户确认后切换模块执行                  │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 安全验证机制

| 验证层 | 验证内容 | 失败处理 |
|--------|----------|----------|
| 模块匹配 | script.module == currentModule | 拒绝执行，提示确认 |
| API白名单 | apiName ∈ module.allowedApis | 拒绝执行，返回错误 |
| 参数验证 | 必填参数存在，类型正确 | 拒绝执行，返回错误 |
| 脚本安全 | 无危险操作（文件、网络等） | 拒绝执行，返回错误 |
| 超时控制 | 执行时间 < timeout | 强制终止 |

---

## 四、MVEL脚本执行设计

### 4.1 执行流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    MVEL脚本执行流程                                              │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. LLM生成脚本                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ LLM响应:                                                                 │    │
│  │ {                                                                        │    │
│  │   "message": "我帮您筛选日志相关的能力...",                               │    │
│  │   "script": {                                                            │    │
│  │     "type": "mvel",                                                      │    │
│  │     "code": "filterCapabilities(['keyword': '日志'])",                   │    │
│  │     "module": "discovery"                                                │    │
│  │   },                                                                     │    │
│  │   "syncContext": true                                                    │    │
│  │ }                                                                        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                      │                                          │
│                                      ▼                                          │
│  2. Skill层验证                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ SceneActionExecutor.execute(action, currentModule):                     │    │
│  │                                                                         │    │
│  │ if (script.module != currentModule) {                                   │    │
│  │     if (action.requireConfirm) {                                        │    │
│  │         return needConfirm("跨模块操作，请确认");                        │    │
│  │     }                                                                   │    │
│  │     return error("模块不匹配");                                          │    │
│  │ }                                                                       │    │
│  │                                                                         │    │
│  │ Set<String> allowedApis = moduleApiRegistry.getAvailableApis(module);   │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                      │                                          │
│                                      ▼                                          │
│  3. Engine层执行                                                                │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │ MvelScriptExecutor.execute(request):                                    │    │
│  │                                                                         │    │
│  │ // 1. 脚本安全验证                                                       │    │
│  │ if (!validate(script, allowedApis)) {                                   │    │
│  │     return error("脚本安全验证失败");                                    │    │
│  │ }                                                                       │    │
│  │                                                                         │    │
│  │ // 2. 构建沙箱上下文                                                     │    │
│  │ Map<String, Object> sandbox = buildSandbox(allowedApis);                │    │
│  │                                                                         │    │
│  │ // 3. 编译并执行                                                         │    │
│  │ Serializable compiled = MVEL.compileExpression(script);                 │    │
│  │ Object result = MVEL.executeExpression(compiled, sandbox);              │    │
│  │                                                                         │    │
│  │ // 4. 返回结果                                                           │    │
│  │ return success(result);                                                 │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 MVEL沙箱设计

```java
// Engine层实现
public class MvelSandbox {
    
    private static final Set<String> FORBIDDEN_PATTERNS = Set.of(
        "Runtime", "Process", "System.exit", 
        "FileInputStream", "FileOutputStream",
        "Socket", "URL", "HttpURLConnection",
        "Class.forName", "ClassLoader"
    );
    
    public Map<String, Object> buildSandbox(Set<String> allowedApis) {
        Map<String, Object> sandbox = new HashMap<>();
        
        // 只注册允许的API
        for (String apiName : allowedApis) {
            sandbox.put(apiName, (Function<Object[], Object>) args -> {
                return apiRegistry.execute(apiName, args);
            });
        }
        
        // 添加安全工具
        sandbox.put("log", safeLogger);
        sandbox.put("json", jsonUtil);
        
        return sandbox;
    }
    
    public boolean validateScript(String script, Set<String> allowedApis) {
        // 检查禁用模式
        for (String pattern : FORBIDDEN_PATTERNS) {
            if (script.contains(pattern)) {
                return false;
            }
        }
        
        // 检查API白名单
        // 只允许调用已注册的API
        
        return true;
    }
}
```

### 4.3 支持的脚本类型

| 类型 | 执行引擎 | 适用场景 |
|------|----------|----------|
| MVEL | 内置MVEL引擎 | 简单表达式、API调用 |
| JavaScript | Nashorn/GraalVM | 复杂逻辑、异步操作 |

---

## 五、公共信任层设计

### 5.1 核心概念

**公共信任层**是一个安全的上下文管理机制，由LLM决定是否同步上下文。

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    公共信任层架构                                                 │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  会话上下文 (SessionContext)                                             │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │ sessionId: "sess-xxx"                                           │   │    │
│  │  │ userId: "user-001"                                              │   │    │
│  │  │ currentModule: "discovery"                                      │   │    │
│  │  │ pageState: {                                                    │   │    │
│  │  │   currentMethod: "AUTO",                                        │   │    │
│  │  │   discoveredCapabilities: [...],                                │   │    │
│  │  │   selectedCapability: null                                      │   │    │
│  │  │ }                                                               │   │    │
│  │  │ lastUpdate: 1709876543210                                       │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                      │                                          │
│                                      │ LLM决策                                  │
│                                      ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  同步决策机制                                                            │    │
│  │                                                                         │    │
│  │  LLM响应中的 syncContext 字段:                                          │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │ syncContext: true  → 立即同步上下文到前端                         │   │    │
│  │  │ syncContext: false → 不同步                                      │   │    │
│  │  │ syncContext: null  → 根据策略决定（默认不同步）                   │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  │                                                                         │    │
│  │  同步策略:                                                              │    │
│  │  - 执行了页面操作 → 自动同步                                            │    │
│  │  - 用户主动请求 → 同步                                                  │    │
│  │  - 仅对话响应 → 不同步                                                  │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 上下文同步流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    上下文同步流程                                                │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  前端页面                                                                        │
│      │                                                                          │
│      │ 1. 页面状态变化                                                          │
│      ▼                                                                          │
│  ModuleApiRegistry.setCurrentModule("discovery")                                │
│  ContextTrustLayer.updatePageState({ method, capabilities, ... })               │
│      │                                                                          │
│      │ 2. 推送到信任层                                                          │
│      ▼                                                                          │
│  Engine层: ContextTrustLayer.updateContext(sessionId, module, data)             │
│      │                                                                          │
│      │ 3. LLM处理请求                                                           │
│      ▼                                                                          │
│  LLM响应: { syncContext: true, ... }                                            │
│      │                                                                          │
│      │ 4. 判断是否同步                                                          │
│      ▼                                                                          │
│  Engine层: SyncDecision = shouldSync(sessionId, response)                       │
│      │                                                                          │
│      │ 5. 同步到前端                                                            │
│      ▼                                                                          │
│  前端: ContextTrustLayer.onSync(syncData)                                       │
│      │                                                                          │
│      ▼                                                                          │
│  页面更新                                                                        │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 5.3 信任机制

| 机制 | 说明 |
|------|------|
| LLM决策同步 | LLM通过`syncContext`字段决定是否同步 |
| 上下文验证 | 上下文变更需要经过验证，防止篡改 |
| 敏感数据脱敏 | 敏感数据（密码、密钥）需要脱敏处理 |
| 会话隔离 | 不同用户的会话完全隔离 |

---

## 六、Engine层需求（协作任务）

### 6.1 脚本执行引擎

**需求描述**：提供安全的脚本执行能力，支持MVEL和JavaScript。

**接口设计**：
```java
package net.ooder.engine.script;

public interface ScriptExecutor {
    
    ScriptResult execute(ScriptRequest request);
    
    boolean validate(String script, Set<String> allowedApis);
    
    void registerFunction(String name, Object function);
    
    void setGlobalVariable(String name, Object value);
}

public interface ScriptRequest {
    String getScriptType();      // mvel, javascript
    String getScript();
    Map<String, Object> getContext();
    Set<String> getAllowedApis();
    long getTimeout();
}

public interface ScriptResult {
    boolean isSuccess();
    Object getResult();
    String getError();
    long getExecutionTime();
}
```

**安全要求**：
1. 沙箱隔离：脚本无法访问文件系统、网络等敏感资源
2. API白名单：只能调用预注册的函数
3. 超时控制：防止死循环（默认5秒）
4. 资源限制：内存限制128MB

### 6.2 公共信任层

**需求描述**：管理会话上下文，提供安全的上下文同步机制。

**接口设计**：
```java
package net.ooder.engine.context;

public interface ContextTrustLayer {
    
    String createSession(String userId);
    
    void updateContext(String sessionId, String module, Map<String, Object> data);
    
    Map<String, Object> getContext(String sessionId);
    
    SyncDecision shouldSync(String sessionId, LlmResponse response);
    
    void invalidateSession(String sessionId);
}

public interface SyncDecision {
    boolean shouldSync();
    String getReason();
    Map<String, Object> getSyncData();
}
```

### 6.3 LLM函数调用支持

**需求描述**：支持LLM Function Calling能力。

**接口设计**：
```java
package net.ooder.engine.llm;

public interface FunctionCallingSupport {
    
    void registerFunction(FunctionDefinition function);
    
    List<FunctionDefinition> getAvailableFunctions(String module);
    
    FunctionResult executeFunction(String name, Map<String, Object> args);
}

public interface FunctionDefinition {
    String getName();
    String getDescription();
    Map<String, ParamDefinition> getParameters();
    String getModule();  // 所属模块
}
```

---

## 七、Skill层设计（本模块实现）

### 7.1 模块API注册表

**职责**：定义每个模块可用的API，供LLM调用。

**已实现**：[ModuleApiRegistry.java](../src/main/java/net/ooder/skill/scene/llm/ModuleApiRegistry.java)

```java
@Component
public class ModuleApiRegistry {
    
    private Map<String, ModuleApis> modules = new HashMap<>();
    
    @PostConstruct
    public void init() {
        registerDiscoveryApis();
        registerInstallApis();
        registerActivationApis();
    }
    
    public void setCurrentModule(String module) {
        this.currentModule = module;
    }
    
    public Set<String> getAvailableApis(String module) {
        return modules.get(module).getApiNames();
    }
}
```

### 7.2 上下文提供者

**职责**：收集当前页面状态，构建LLM上下文。

**已实现**：[SceneContextProvider.java](../src/main/java/net/ooder/skill/scene/llm/SceneContextProvider.java)

### 7.3 动作执行器

**职责**：执行LLM生成的动作指令。

**待实现**：等待Engine层脚本执行引擎就绪后实现。

---

## 八、通用程序助手Skill设计

### 8.1 设计原则

程序助手（Program Assistant）应该是一个**独立的、可复用的Skill**，遵循Skills架构：

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    程序助手Skill复用架构                                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  skill-program-assistant (独立Skill，可复用)                             │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │ capabilities:                                                    │   │    │
│  │  │   - script-generation    脚本生成                                │   │    │
│  │  │   - code-completion      代码补全                                │   │    │
│  │  │   - code-explanation     代码解释                                │   │    │
│  │  │   - debug-suggestion     调试建议                                │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                      │                                          │
│                                      │ 被以下模块复用                            │
│                                      ▼                                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐            │
│  │ skill-scene │  │skill-workflow│  │skill-integration│ │skill-custom│           │
│  │ 场景管理    │  │ 工作流      │  │ 集成        │  │ 用户自定义  │            │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘            │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 8.2 接口设计

```java
package net.ooder.skill.assistant;

public interface ProgramAssistantSkill {
    
    /**
     * 生成脚本
     * @param request 脚本生成请求
     * @return 生成的脚本
     */
    ScriptGenerationResult generateScript(ScriptGenerationRequest request);
    
    /**
     * 代码补全
     * @param request 补全请求
     * @return 补全结果
     */
    CodeCompletionResult completeCode(CodeCompletionRequest request);
    
    /**
     * 代码解释
     * @param request 解释请求
     * @return 解释结果
     */
    CodeExplanationResult explainCode(CodeExplanationRequest request);
    
    /**
     * 调试建议
     * @param request 调试请求
     * @return 调试建议
     */
    DebugSuggestionResult suggestDebug(DebugSuggestionRequest request);
}

public interface ScriptGenerationRequest {
    String getLanguage();        // mvel, javascript, python, etc.
    String getModule();          // 目标模块
    String getIntent();          // 用户意图
    Map<String, Object> getContext();  // 上下文
    Set<String> getAllowedApis();      // 允许的API
}
```

### 8.3 复用场景

| 场景 | 使用方式 | 说明 |
|------|----------|------|
| skill-scene | 生成场景操作脚本 | 发现、安装、激活流程 |
| skill-workflow | 生成工作流脚本 | 工作流定义、节点配置 |
| skill-integration | 生成集成脚本 | API集成、数据转换 |
| skill-custom | 用户自定义脚本 | 用户自定义能力 |

---

## 九、交互流程

### 9.1 发现流程示例

```
用户进入发现页面
    │
    ▼
Skill层: ModuleApiRegistry.setCurrentModule("discovery")
Skill层: SceneContextProvider.buildContext()
    │
    ▼
Engine层: ContextTrustLayer.updateContext(sessionId, "discovery", context)
    │
    ▼
用户: "帮我找一个日志汇报的能力"
    │
    ▼
Engine层: LLM处理请求，生成响应
    │
    ▼
LLM响应:
{
    "message": "我帮您筛选日志相关的能力...",
    "script": {
        "type": "mvel",
        "code": "filterCapabilities(['keyword': '日志'])",
        "module": "discovery"
    },
    "syncContext": true
}
    │
    ▼
Skill层: SceneActionExecutor.execute(action, "discovery")
    │
    ├── 验证模块匹配 ✓
    ├── 获取API白名单
    └── 调用Engine层执行脚本
    │
    ▼
Engine层: MvelScriptExecutor.execute(request)
    │
    ├── 验证脚本安全性
    ├── 沙箱执行
    └── 返回结果
    │
    ▼
Skill层: 返回执行结果，更新上下文
    │
    ▼
页面: 显示筛选结果
```

### 9.2 跨模块操作

```
用户: "开始安装"
    │
    ▼
LLM响应:
{
    "message": "即将切换到安装模块...",
    "script": {
        "type": "mvel",
        "code": "startInstall('daily-log-scene')",
        "module": "install"  // 不同模块
    },
    "requireConfirm": true
}
    │
    ▼
Skill层: 检测到跨模块操作
    │
    ▼
页面: 弹出确认对话框
    │
    ├── 用户确认 → 切换模块，执行操作
    └── 用户取消 → 不执行
```

---

## 十、协作任务清单

### 10.1 Engine层任务（需要协作）

| 任务ID | 任务名称 | 优先级 | 状态 |
|--------|----------|--------|------|
| ENG-001 | MVEL脚本执行器实现（内置） | P0 | 待开发 |
| ENG-002 | JavaScript执行器实现 | P1 | 待开发 |
| ENG-003 | 脚本安全验证机制 | P0 | 待开发 |
| ENG-004 | 公共信任层实现 | P0 | 待开发 |
| ENG-005 | LLM Function Calling支持 | P1 | 待开发 |

### 10.2 Skill层任务（本模块开发）

| 任务ID | 任务名称 | 优先级 | 状态 |
|--------|----------|--------|------|
| SKILL-001 | ModuleApiRegistry实现 | P0 | ✅ 已完成 |
| SKILL-002 | SceneContextProvider实现 | P0 | ✅ 已完成 |
| SKILL-003 | SceneActionExecutor实现 | P0 | ✅ 已完成 |
| SKILL-004 | 前端ModuleApiRegistry.js | P0 | ✅ 已完成 |
| SKILL-005 | 前端ContextTrustLayer.js | P0 | ✅ 已完成 |
| SKILL-006 | 集成到发现页面 | P1 | ✅ 已完成 |
| SKILL-007 | ProgramAssistantSkill实现 | P0 | ✅ 已完成 |
| SKILL-008 | LlmScriptController实现 | P0 | ✅ 已完成 |

### 10.3 通用Skill任务（可复用）

| 任务ID | 任务名称 | 优先级 | 状态 |
|--------|----------|--------|------|
| COMMON-001 | ProgramAssistantSkill设计 | P1 | ✅ 已完成 |
| COMMON-002 | 脚本生成能力实现 | P1 | ✅ 已完成 |
| COMMON-003 | 代码补全能力实现 | P2 | 待开发 |

---

## 十一、待讨论问题

### 11.1 安全性

| 问题 | 讨论结果 | 状态 |
|------|----------|------|
| 脚本权限边界 | 沙箱隔离 + API白名单 | ✅ 已确定 |
| 跨模块操作确认 | requireConfirm字段控制 | ✅ 已确定 |
| 敏感数据处理 | 上下文脱敏处理 | 待细化 |

### 11.2 性能

| 问题 | 讨论结果 | 状态 |
|------|----------|------|
| 上下文同步频率 | 由LLM决策，按需同步 | ✅ 已确定 |
| 脚本执行超时 | 默认5秒，可配置 | ✅ 已确定 |
| 并发控制 | 会话隔离 | 待细化 |

### 11.3 扩展性

| 问题 | 讨论结果 | 状态 |
|------|----------|------|
| 新模块接入 | ModuleApiRegistry注册 | ✅ 已确定 |
| 自定义脚本 | 支持用户自定义 | 待讨论 |
| 第三方集成 | 通过Skills架构复用 | ✅ 已确定 |

---

## 十二、参考资料

- [智能安装技术设计文档](./smart-install-technical-design.md)
- [场景技能类型规格](./scene-skill-types-specification.md)
- [Engine与SDK协作规格](./engine-sdk-collaboration-spec.md)
- [Engine层协作需求](./engine-collaboration-request.md)

---

## 十三、版本历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| v1.0-draft | 2026-03-09 | 初稿 |
| v1.1-draft | 2026-03-09 | 补充安全机制设计、MVEL脚本执行设计、公共信任层设计、程序助手Skills架构 |
| v1.2-draft | 2026-03-09 | 完成Skill层核心实现：ProgramAssistantSkill、SceneActionExecutor、ContextTrustLayer.js、LlmScriptController |
