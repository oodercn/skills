# Engine层协作需求：LLM脚本执行与上下文信任层

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 发起模块 | skill-scene |
| 目标模块 | engine |
| 优先级 | P0 |
| 状态 | 待评审 |

---

## 一、需求背景

skill-scene模块正在实现LLM辅助的智能安装和激活流程，需要Engine层提供以下基础能力：

1. **脚本执行引擎**：安全执行LLM生成的MVEL/JavaScript脚本
2. **上下文信任层**：管理会话上下文，提供安全的上下文同步机制
3. **LLM函数调用支持**：支持LLM Function Calling能力

---

## 二、需求详情

### 2.1 脚本执行引擎

#### 接口定义

```java
package net.ooder.engine.script;

public interface ScriptExecutor {
    
    /**
     * 执行脚本
     * @param request 脚本执行请求
     * @return 执行结果
     */
    ScriptResult execute(ScriptRequest request);
    
    /**
     * 验证脚本安全性
     * @param script 脚本内容
     * @param allowedApis 允许调用的API列表
     * @return 是否安全
     */
    boolean validate(String script, Set<String> allowedApis);
    
    /**
     * 注册全局函数
     * @param name 函数名
     * @param function 函数实现
     */
    void registerFunction(String name, Object function);
    
    /**
     * 设置全局变量
     * @param name 变量名
     * @param value 变量值
     */
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

#### 安全要求

| 要求 | 说明 |
|------|------|
| 沙箱隔离 | 脚本无法访问文件系统、网络等敏感资源 |
| API白名单 | 只能调用预注册的函数 |
| 超时控制 | 默认5秒，可配置 |
| 资源限制 | 内存限制128MB，CPU限制50% |

#### 使用示例

```java
// Skill层调用示例
ScriptRequest request = new ScriptRequestImpl();
request.setScriptType("mvel");
request.setScript("filterCapabilities(['keyword': '日志'])");
request.setAllowedApis(Set.of("filterCapabilities", "selectCapability"));
request.setTimeout(5000);

ScriptResult result = scriptExecutor.execute(request);
if (result.isSuccess()) {
    System.out.println("执行结果: " + result.getResult());
} else {
    System.out.println("执行失败: " + result.getError());
}
```

---

### 2.2 上下文信任层

#### 接口定义

```java
package net.ooder.engine.context;

public interface ContextTrustLayer {
    
    /**
     * 创建会话
     * @param userId 用户ID
     * @return 会话ID
     */
    String createSession(String userId);
    
    /**
     * 更新上下文
     * @param sessionId 会话ID
     * @param module 当前模块
     * @param data 上下文数据
     */
    void updateContext(String sessionId, String module, Map<String, Object> data);
    
    /**
     * 获取上下文
     * @param sessionId 会话ID
     * @return 上下文数据
     */
    Map<String, Object> getContext(String sessionId);
    
    /**
     * 判断是否需要同步上下文
     * @param sessionId 会话ID
     * @param response LLM响应
     * @return 同步决策
     */
    SyncDecision shouldSync(String sessionId, LlmResponse response);
    
    /**
     * 使会话失效
     * @param sessionId 会话ID
     */
    void invalidateSession(String sessionId);
}

public interface SyncDecision {
    boolean shouldSync();
    String getReason();
    Map<String, Object> getSyncData();
}

public interface LlmResponse {
    String getMessage();
    ScriptInfo getScript();
    Boolean getSyncContext();
}
```

#### 信任机制

1. **LLM决策同步**：LLM通过响应中的`syncContext`字段决定是否同步
2. **上下文验证**：上下文变更需要经过验证
3. **敏感数据脱敏**：敏感数据需要脱敏处理

#### 使用示例

```java
// Skill层调用示例
String sessionId = contextTrustLayer.createSession("user-001");

// 更新上下文
Map<String, Object> context = new HashMap<>();
context.put("module", "discovery");
context.put("pageState", pageState);
contextTrustLayer.updateContext(sessionId, "discovery", context);

// 获取上下文
Map<String, Object> currentContext = contextTrustLayer.getContext(sessionId);

// 判断是否同步
SyncDecision decision = contextTrustLayer.shouldSync(sessionId, llmResponse);
if (decision.shouldSync()) {
    // 执行同步逻辑
}
```

---

### 2.3 LLM函数调用支持

#### 接口定义

```java
package net.ooder.engine.llm;

public interface FunctionCallingSupport {
    
    /**
     * 注册函数
     * @param function 函数定义
     */
    void registerFunction(FunctionDefinition function);
    
    /**
     * 获取模块可用的函数列表
     * @param module 模块名
     * @return 函数定义列表
     */
    List<FunctionDefinition> getAvailableFunctions(String module);
    
    /**
     * 执行函数
     * @param name 函数名
     * @param args 参数
     * @return 执行结果
     */
    FunctionResult executeFunction(String name, Map<String, Object> args);
}

public interface FunctionDefinition {
    String getName();
    String getDescription();
    Map<String, ParamDefinition> getParameters();
    String getModule();
}

public interface ParamDefinition {
    String getType();
    String getDescription();
    boolean isRequired();
}
```

---

## 三、协作任务清单

| 任务ID | 任务名称 | 优先级 | 预计工时 | 负责模块 |
|--------|----------|--------|----------|----------|
| ENG-001 | MVEL脚本执行器实现 | P0 | 3天 | engine |
| ENG-002 | JavaScript执行器实现 | P1 | 2天 | engine |
| ENG-003 | 脚本安全验证机制 | P0 | 2天 | engine |
| ENG-004 | 上下文信任层实现 | P0 | 2天 | engine |
| ENG-005 | LLM Function Calling支持 | P1 | 2天 | engine |

---

## 四、依赖关系

```
skill-scene (本模块)
    │
    ├── ModuleApiRegistry (Skill层实现)
    │       │
    │       └── 依赖 → ScriptExecutor (Engine层提供)
    │
    ├── SceneContextProvider (Skill层实现)
    │       │
    │       └── 依赖 → ContextTrustLayer (Engine层提供)
    │
    └── SceneActionExecutor (Skill层实现)
            │
            ├── 依赖 → ScriptExecutor (Engine层提供)
            └── 依赖 → FunctionCallingSupport (Engine层提供)
```

---

## 五、验收标准

### 5.1 脚本执行引擎

- [ ] 支持MVEL脚本执行
- [ ] 支持JavaScript脚本执行
- [ ] 沙箱隔离生效
- [ ] API白名单验证生效
- [ ] 超时控制生效
- [ ] 单元测试覆盖率 > 80%

### 5.2 上下文信任层

- [ ] 会话创建和管理正常
- [ ] 上下文更新和获取正常
- [ ] 同步决策机制正常
- [ ] 敏感数据脱敏生效
- [ ] 单元测试覆盖率 > 80%

### 5.3 LLM函数调用支持

- [ ] 函数注册正常
- [ ] 函数执行正常
- [ ] 模块隔离正常
- [ ] 单元测试覆盖率 > 80%

---

## 六、联系方式

- 发起人：skill-scene模块
- 文档地址：[llm-scene-interaction-design.md](./llm-scene-interaction-design.md)
- 协作方式：请Engine团队评审后反馈开发计划

---

## 七、版本历史

| 版本 | 日期 | 变更内容 |
|------|------|----------|
| v1.0 | 2026-03-09 | 初稿 |
