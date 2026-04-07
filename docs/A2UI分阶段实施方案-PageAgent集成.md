# A2UI 分阶段实施方案 - PageAgent 集成与 Function Calling 扩展

**文档版本**: v1.0  
**创建日期**: 2026-04-06  
**项目路径**: E:\github\ooder-skills  
**协作文档输出路径**: E:\github\ooder-skills\docs\A2UI分阶段实施方案-PageAgent集成.md

---

## 一、概述

本文档基于 nexus 架构，提出了一套分阶段实施 A2UI 的方案。第一阶段优先实现 pageAgent 功能集成，扩展 function calling 以支持 UI/UE 数据定义；第二阶段进入 ou-core 的流式 UI。该方案保留了 html+js+css 的落地设计，同时进行了 JS 和组件层次的提取定义。

---

## 二、分阶段实施策略

### 2.1 实施路线图

```
Phase 1: PageAgent 功能集成 (2-3 个月)
├── Function Calling 扩展
│   ├── UI/UE 数据定义标准
│   ├── LLM + Workflow 双向支持
│   └── 组件层次提取
├── PageAgent 架构设计
│   ├── 页面级 Agent 实现
│   ├── UI 组件管理
│   └── 事件驱动机制
└── HTML+JS+CSS 落地设计
    ├── JS 模块化提取
    ├── 组件标准化定义
    └── 样式分离管理

Phase 2: 流式 UI (2-3 个月)
├── ou-core 集成
│   ├── 流式渲染引擎
│   ├── 实时数据绑定
│   └── 动态组件加载
├── 高级 UI 能力
│   ├── 拖拽式设计器
│   ├── 可视化编排
│   └── 智能布局
└── 性能优化
    ├── 按需加载
    ├── 缓存策略
    └── 渲染优化
```

---

## 三、第一阶段：PageAgent 功能集成

### 3.1 PageAgent 架构设计

#### 3.1.1 PageAgent 定义

**PageAgent** 是页面级的智能代理，负责管理整个页面的 UI 组件、事件处理和数据绑定。

**核心职责**:
- 页面生命周期管理
- UI 组件注册和管理
- 事件监听和分发
- 数据绑定和同步
- 与 LLM 和 Workflow 的交互

**架构图**:

```
┌─────────────────────────────────────────────────────────────┐
│                    PageAgent 架构                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              PageAgent Core                            │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │ │
│  │  │ Lifecycle   │  │ Component   │  │ Event       │  │ │
│  │  │ Manager     │  │ Manager     │  │ Manager     │  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  │ │
│  └───────────────────────────────────────────────────────┘ │
│                              ↓                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              Function Calling Layer                    │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │ │
│  │  │ LLM         │  │ Workflow    │  │ UI/UE       │  │ │
│  │  │ Integration │  │ Integration │  │ Definition  │  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  │ │
│  └───────────────────────────────────────────────────────┘ │
│                              ↓                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              Component Layer                           │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │ │
│  │  │ Form        │  │ List        │  │ Chart       │  │ │
│  │  │ Components  │  │ Components  │  │ Components  │  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  │ │
│  └───────────────────────────────────────────────────────┘ │
│                              ↓                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              Render Layer (HTML+JS+CSS)                │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │ │
│  │  │ HTML        │  │ JavaScript  │  │ CSS         │  │ │
│  │  │ Templates   │  │ Modules     │  │ Styles      │  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### 3.1.2 PageAgent 核心实现

**PageAgent 接口定义**:

```java
/**
 * PageAgent 接口 - 页面级智能代理
 * PageAgent Interface - Page-level Intelligent Agent
 */
package net.ooder.skill.agent.page;

import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PageAgent {
    
    /**
     * 获取 PageAgent ID
     */
    String getAgentId();
    
    /**
     * 获取 PageAgent 名称
     */
    String getAgentName();
    
    /**
     * 获取页面 ID
     */
    String getPageId();
    
    /**
     * 初始化 PageAgent
     */
    void initialize(PageAgentConfig config);
    
    /**
     * 启动 PageAgent
     */
    void start();
    
    /**
     * 停止 PageAgent
     */
    void stop();
    
    /**
     * 注册 UI 组件
     */
    void registerComponent(UIComponent component);
    
    /**
     * 注销 UI 组件
     */
    void unregisterComponent(String componentId);
    
    /**
     * 获取组件
     */
    UIComponent getComponent(String componentId);
    
    /**
     * 获取所有组件
     */
    List<UIComponent> getAllComponents();
    
    /**
     * 触发事件
     */
    void triggerEvent(UIEvent event);
    
    /**
     * 监听事件
     */
    void addEventListener(String eventType, UIEventListener listener);
    
    /**
     * 移除事件监听
     */
    void removeEventListener(String eventType, UIEventListener listener);
    
    /**
     * 更新数据
     */
    void updateData(String dataPath, Object data);
    
    /**
     * 获取数据
     */
    Object getData(String dataPath);
    
    /**
     * 执行 Function Calling
     */
    CompletableFuture<FunctionResult> executeFunction(
        String functionName, 
        Map<String, Object> arguments
    );
    
    /**
     * 获取可用的 Functions
     */
    List<FunctionDefinition> getAvailableFunctions();
    
    /**
     * 渲染页面
     */
    void render();
    
    /**
     * 销毁 PageAgent
     */
    void destroy();
}
```

**PageAgent 配置**:

```java
/**
 * PageAgent 配置
 */
package net.ooder.skill.agent.page;

import java.util.Map;
import java.util.List;

public class PageAgentConfig {
    
    private String agentId;
    private String agentName;
    private String pageId;
    private String pageType; // form, list, dashboard, etc.
    private String templatePath; // HTML 模板路径
    private String stylePath; // CSS 样式路径
    private String scriptPath; // JavaScript 脚本路径
    private Map<String, Object> initialData; // 初始数据
    private List<UIComponentConfig> components; // 组件配置
    private List<FunctionDefinition> functions; // Function Calling 定义
    private Map<String, Object> metadata; // 元数据
    
    // Getters and Setters
    // ...
}
```

**PageAgent 实现示例**:

```java
/**
 * PageAgent 实现示例 - 审批表单页面
 */
package net.ooder.skill.agent.page.impl;

import net.ooder.skill.agent.page.*;
import net.ooder.skill.agent.function.FunctionCallingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class ApprovalFormPageAgent implements PageAgent {
    
    private String agentId;
    private String agentName;
    private String pageId;
    private PageAgentConfig config;
    
    @Autowired
    private FunctionCallingService functionCallingService;
    
    private Map<String, UIComponent> components = new ConcurrentHashMap<>();
    private Map<String, List<UIEventListener>> eventListeners = new ConcurrentHashMap<>();
    private Map<String, Object> dataStore = new ConcurrentHashMap<>();
    
    @Override
    public void initialize(PageAgentConfig config) {
        this.config = config;
        this.agentId = config.getAgentId();
        this.agentName = config.getAgentName();
        this.pageId = config.getPageId();
        
        // 初始化组件
        initializeComponents(config.getComponents());
        
        // 注册 Functions
        registerFunctions(config.getFunctions());
        
        // 加载初始数据
        if (config.getInitialData() != null) {
            dataStore.putAll(config.getInitialData());
        }
    }
    
    @Override
    public void start() {
        // 启动 PageAgent
        render();
    }
    
    @Override
    public void stop() {
        // 停止 PageAgent
    }
    
    @Override
    public void registerComponent(UIComponent component) {
        components.put(component.getComponentId(), component);
        
        // 触发组件注册事件
        triggerEvent(new UIEvent(
            "component.registered",
            Map.of("componentId", component.getComponentId())
        ));
    }
    
    @Override
    public void unregisterComponent(String componentId) {
        UIComponent component = components.remove(componentId);
        if (component != null) {
            // 触发组件注销事件
            triggerEvent(new UIEvent(
                "component.unregistered",
                Map.of("componentId", componentId)
            ));
        }
    }
    
    @Override
    public UIComponent getComponent(String componentId) {
        return components.get(componentId);
    }
    
    @Override
    public List<UIComponent> getAllComponents() {
        return new ArrayList<>(components.values());
    }
    
    @Override
    public void triggerEvent(UIEvent event) {
        List<UIEventListener> listeners = eventListeners.get(event.getType());
        if (listeners != null) {
            for (UIEventListener listener : listeners) {
                try {
                    listener.onEvent(event);
                } catch (Exception e) {
                    // 错误处理
                }
            }
        }
    }
    
    @Override
    public void addEventListener(String eventType, UIEventListener listener) {
        eventListeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }
    
    @Override
    public void removeEventListener(String eventType, UIEventListener listener) {
        List<UIEventListener> listeners = eventListeners.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }
    
    @Override
    public void updateData(String dataPath, Object data) {
        dataStore.put(dataPath, data);
        
        // 触发数据变更事件
        triggerEvent(new UIEvent(
            "data.changed",
            Map.of("dataPath", dataPath, "data", data)
        ));
    }
    
    @Override
    public Object getData(String dataPath) {
        return dataStore.get(dataPath);
    }
    
    @Override
    public CompletableFuture<FunctionResult> executeFunction(
        String functionName, 
        Map<String, Object> arguments
    ) {
        return CompletableFuture.supplyAsync(() -> {
            return functionCallingService.executeFunction(functionName, arguments);
        });
    }
    
    @Override
    public List<FunctionDefinition> getAvailableFunctions() {
        return functionCallingService.getAvailableFunctions();
    }
    
    @Override
    public void render() {
        // 渲染页面
        // 实际实现中会调用模板引擎渲染 HTML
        triggerEvent(new UIEvent("page.rendered", Map.of("pageId", pageId)));
    }
    
    @Override
    public void destroy() {
        components.clear();
        eventListeners.clear();
        dataStore.clear();
    }
    
    private void initializeComponents(List<UIComponentConfig> componentConfigs) {
        if (componentConfigs != null) {
            for (UIComponentConfig compConfig : componentConfigs) {
                UIComponent component = createComponent(compConfig);
                registerComponent(component);
            }
        }
    }
    
    private UIComponent createComponent(UIComponentConfig config) {
        // 根据配置创建组件
        return new GenericUIComponent(config);
    }
    
    private void registerFunctions(List<FunctionDefinition> functions) {
        if (functions != null) {
            for (FunctionDefinition function : functions) {
                functionCallingService.registerFunction(function);
            }
        }
    }
}
```

### 3.2 Function Calling 扩展设计

#### 3.2.1 UI/UE 数据定义标准

**扩展 Function Calling 以支持 UI/UE 数据定义**:

```java
/**
 * UI/UE Function Definition - 扩展 Function Calling
 */
package net.ooder.skill.agent.function;

import java.util.Map;
import java.util.List;

public class UIFunctionDefinition extends FunctionDefinition {
    
    private String functionType; // "llm", "workflow", "ui", "ue"
    private UIComponentDefinition uiComponent; // UI 组件定义
    private UEInteractionDefinition ueInteraction; // UE 交互定义
    private WorkflowBinding workflowBinding; // Workflow 绑定
    
    /**
     * 创建 LLM Function
     */
    public static UIFunctionDefinition createLLMFunction(
        String name,
        String description,
        Map<String, ParamDefinition> parameters,
        FunctionExecutor executor
    ) {
        UIFunctionDefinition def = new UIFunctionDefinition();
        def.setName(name);
        def.setDescription(description);
        def.setParameters(parameters);
        def.setExecutor(executor);
        def.setFunctionType("llm");
        return def;
    }
    
    /**
     * 创建 UI Function
     */
    public static UIFunctionDefinition createUIFunction(
        String name,
        String description,
        UIComponentDefinition uiComponent,
        FunctionExecutor executor
    ) {
        UIFunctionDefinition def = new UIFunctionDefinition();
        def.setName(name);
        def.setDescription(description);
        def.setUiComponent(uiComponent);
        def.setExecutor(executor);
        def.setFunctionType("ui");
        return def;
    }
    
    /**
     * 创建 Workflow Function
     */
    public static UIFunctionDefinition createWorkflowFunction(
        String name,
        String description,
        WorkflowBinding workflowBinding,
        FunctionExecutor executor
    ) {
        UIFunctionDefinition def = new UIFunctionDefinition();
        def.setName(name);
        def.setDescription(description);
        def.setWorkflowBinding(workflowBinding);
        def.setExecutor(executor);
        def.setFunctionType("workflow");
        return def;
    }
    
    /**
     * 创建 UE Function
     */
    public static UIFunctionDefinition createUEFunction(
        String name,
        String description,
        UEInteractionDefinition ueInteraction,
        FunctionExecutor executor
    ) {
        UIFunctionDefinition def = new UIFunctionDefinition();
        def.setName(name);
        def.setDescription(description);
        def.setUeInteraction(ueInteraction);
        def.setExecutor(executor);
        def.setFunctionType("ue");
        return def;
    }
    
    /**
     * 转换为 OpenAI Function Schema
     */
    @Override
    public Map<String, Object> toOpenAISchema() {
        Map<String, Object> schema = super.toOpenAISchema();
        
        // 添加 UI/UE 扩展信息
        if (uiComponent != null) {
            schema.put("ui_component", uiComponent.toSchema());
        }
        
        if (ueInteraction != null) {
            schema.put("ue_interaction", ueInteraction.toSchema());
        }
        
        if (workflowBinding != null) {
            schema.put("workflow_binding", workflowBinding.toSchema());
        }
        
        schema.put("function_type", functionType);
        
        return schema;
    }
    
    // Getters and Setters
    // ...
}
```

**UI 组件定义**:

```java
/**
 * UI 组件定义
 */
package net.ooder.skill.agent.function;

import java.util.Map;
import java.util.List;

public class UIComponentDefinition {
    
    private String componentId;
    private String componentType; // form, list, chart, button, input, etc.
    private String componentName;
    private String componentLabel;
    private Map<String, Object> properties; // 组件属性
    private List<UIComponentDefinition> children; // 子组件
    private Map<String, String> dataBindings; // 数据绑定
    private List<String> eventBindings; // 事件绑定
    private String styleClass; // 样式类
    private Map<String, Object> layout; // 布局信息
    
    /**
     * 转换为 Schema
     */
    public Map<String, Object> toSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("component_id", componentId);
        schema.put("component_type", componentType);
        schema.put("component_name", componentName);
        schema.put("component_label", componentLabel);
        schema.put("properties", properties);
        schema.put("data_bindings", dataBindings);
        schema.put("event_bindings", eventBindings);
        schema.put("style_class", styleClass);
        schema.put("layout", layout);
        
        if (children != null && !children.isEmpty()) {
            List<Map<String, Object>> childSchemas = new ArrayList<>();
            for (UIComponentDefinition child : children) {
                childSchemas.add(child.toSchema());
            }
            schema.put("children", childSchemas);
        }
        
        return schema;
    }
    
    /**
     * 从 Schema 创建组件定义
     */
    public static UIComponentDefinition fromSchema(Map<String, Object> schema) {
        UIComponentDefinition def = new UIComponentDefinition();
        def.setComponentId((String) schema.get("component_id"));
        def.setComponentType((String) schema.get("component_type"));
        def.setComponentName((String) schema.get("component_name"));
        def.setComponentLabel((String) schema.get("component_label"));
        def.setProperties((Map<String, Object>) schema.get("properties"));
        def.setDataBindings((Map<String, String>) schema.get("data_bindings"));
        def.setEventBindings((List<String>) schema.get("event_bindings"));
        def.setStyleClass((String) schema.get("style_class"));
        def.setLayout((Map<String, Object>) schema.get("layout"));
        
        // 递归处理子组件
        List<Map<String, Object>> childSchemas = (List<Map<String, Object>>) schema.get("children");
        if (childSchemas != null && !childSchemas.isEmpty()) {
            List<UIComponentDefinition> children = new ArrayList<>();
            for (Map<String, Object> childSchema : childSchemas) {
                children.add(fromSchema(childSchema));
            }
            def.setChildren(children);
        }
        
        return def;
    }
    
    // Getters and Setters
    // ...
}
```

**UE 交互定义**:

```java
/**
 * UE 交互定义
 */
package net.ooder.skill.agent.function;

import java.util.Map;
import java.util.List;

public class UEInteractionDefinition {
    
    private String interactionId;
    private String interactionType; // click, hover, focus, blur, change, submit, etc.
    private String triggerComponent; // 触发组件 ID
    private String targetComponent; // 目标组件 ID
    private Map<String, Object> interactionConfig; // 交互配置
    private List<String> actionSequence; // 动作序列
    private Map<String, Object> animation; // 动画效果
    private String feedback; // 反馈信息
    
    /**
     * 转换为 Schema
     */
    public Map<String, Object> toSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("interaction_id", interactionId);
        schema.put("interaction_type", interactionType);
        schema.put("trigger_component", triggerComponent);
        schema.put("target_component", targetComponent);
        schema.put("interaction_config", interactionConfig);
        schema.put("action_sequence", actionSequence);
        schema.put("animation", animation);
        schema.put("feedback", feedback);
        return schema;
    }
    
    /**
     * 从 Schema 创建交互定义
     */
    public static UEInteractionDefinition fromSchema(Map<String, Object> schema) {
        UEInteractionDefinition def = new UEInteractionDefinition();
        def.setInteractionId((String) schema.get("interaction_id"));
        def.setInteractionType((String) schema.get("interaction_type"));
        def.setTriggerComponent((String) schema.get("trigger_component"));
        def.setTargetComponent((String) schema.get("target_component"));
        def.setInteractionConfig((Map<String, Object>) schema.get("interaction_config"));
        def.setActionSequence((List<String>) schema.get("action_sequence"));
        def.setAnimation((Map<String, Object>) schema.get("animation"));
        def.setFeedback((String) schema.get("feedback"));
        return def;
    }
    
    // Getters and Setters
    // ...
}
```

**Workflow 绑定**:

```java
/**
 * Workflow 绑定
 */
package net.ooder.skill.agent.function;

import java.util.Map;

public class WorkflowBinding {
    
    private String workflowId;
    private String workflowName;
    private String processDefId; // 流程定义 ID
    private String activityId; // 活动 ID
    private Map<String, String> inputMappings; // 输入映射
    private Map<String, String> outputMappings; // 输出映射
    private String triggerEvent; // 触发事件
    private String completionEvent; // 完成事件
    
    /**
     * 转换为 Schema
     */
    public Map<String, Object> toSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("workflow_id", workflowId);
        schema.put("workflow_name", workflowName);
        schema.put("process_def_id", processDefId);
        schema.put("activity_id", activityId);
        schema.put("input_mappings", inputMappings);
        schema.put("output_mappings", outputMappings);
        schema.put("trigger_event", triggerEvent);
        schema.put("completion_event", completionEvent);
        return schema;
    }
    
    /**
     * 从 Schema 创建绑定
     */
    public static WorkflowBinding fromSchema(Map<String, Object> schema) {
        WorkflowBinding binding = new WorkflowBinding();
        binding.setWorkflowId((String) schema.get("workflow_id"));
        binding.setWorkflowName((String) schema.get("workflow_name"));
        binding.setProcessDefId((String) schema.get("process_def_id"));
        binding.setActivityId((String) schema.get("activity_id"));
        binding.setInputMappings((Map<String, String>) schema.get("input_mappings"));
        binding.setOutputMappings((Map<String, String>) schema.get("output_mappings"));
        binding.setTriggerEvent((String) schema.get("trigger_event"));
        binding.setCompletionEvent((String) schema.get("completion_event"));
        return binding;
    }
    
    // Getters and Setters
    // ...
}
```

#### 3.2.2 Function Calling 扩展实现

**扩展的 FunctionCallingService**:

```java
/**
 * 扩展的 FunctionCallingService - 支持 UI/UE 定义
 */
package net.ooder.skill.agent.function;

import net.ooder.skill.agent.llm.LLMService;
import net.ooder.skill.agent.page.PageAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExtendedFunctionCallingService extends FunctionCallingService {
    
    @Autowired
    private LLMService llmService;
    
    private Map<String, UIFunctionDefinition> uiFunctionRegistry = new ConcurrentHashMap<>();
    private Map<String, PageAgent> pageAgentRegistry = new ConcurrentHashMap<>();
    
    /**
     * 注册 UI Function
     */
    public void registerUIFunction(UIFunctionDefinition function) {
        uiFunctionRegistry.put(function.getName(), function);
        
        // 同时注册到父类
        super.registerFunction(function);
        
        log.info("Registered UI function: {} with type: {}", 
            function.getName(), function.getFunctionType());
    }
    
    /**
     * 注册 PageAgent
     */
    public void registerPageAgent(PageAgent pageAgent) {
        pageAgentRegistry.put(pageAgent.getAgentId(), pageAgent);
        log.info("Registered PageAgent: {}", pageAgent.getAgentId());
    }
    
    /**
     * 获取指定类型的 Functions
     */
    public List<UIFunctionDefinition> getFunctionsByType(String functionType) {
        List<UIFunctionDefinition> functions = new ArrayList<>();
        for (UIFunctionDefinition func : uiFunctionRegistry.values()) {
            if (functionType.equals(func.getFunctionType())) {
                functions.add(func);
            }
        }
        return functions;
    }
    
    /**
     * 获取 LLM 可用的 Functions
     */
    public List<Map<String, Object>> getLLMFunctionSchemas() {
        List<Map<String, Object>> schemas = new ArrayList<>();
        for (UIFunctionDefinition func : uiFunctionRegistry.values()) {
            if ("llm".equals(func.getFunctionType()) || "ui".equals(func.getFunctionType())) {
                schemas.add(func.toOpenAISchema());
            }
        }
        return schemas;
    }
    
    /**
     * 获取 Workflow 可用的 Functions
     */
    public List<Map<String, Object>> getWorkflowFunctionSchemas() {
        List<Map<String, Object>> schemas = new ArrayList<>();
        for (UIFunctionDefinition func : uiFunctionRegistry.values()) {
            if ("workflow".equals(func.getFunctionType())) {
                schemas.add(func.toOpenAISchema());
            }
        }
        return schemas;
    }
    
    /**
     * 执行 UI Function
     */
    public FunctionResult executeUIFunction(String functionName, Map<String, Object> arguments) {
        UIFunctionDefinition function = uiFunctionRegistry.get(functionName);
        if (function == null) {
            return FunctionResult.error("UI Function not found: " + functionName);
        }
        
        try {
            // 根据函数类型执行不同的逻辑
            switch (function.getFunctionType()) {
                case "llm":
                    return executeLLMFunction(function, arguments);
                case "workflow":
                    return executeWorkflowFunction(function, arguments);
                case "ui":
                    return executeUIComponentFunction(function, arguments);
                case "ue":
                    return executeUEInteractionFunction(function, arguments);
                default:
                    return FunctionResult.error("Unknown function type: " + function.getFunctionType());
            }
        } catch (Exception e) {
            log.error("Error executing UI function {}: {}", functionName, e.getMessage(), e);
            return FunctionResult.error("Function execution failed: " + e.getMessage());
        }
    }
    
    /**
     * 执行 LLM Function
     */
    private FunctionResult executeLLMFunction(UIFunctionDefinition function, Map<String, Object> arguments) {
        // 调用 LLM 服务
        Object result = function.getExecutor().execute(arguments);
        return FunctionResult.success(result);
    }
    
    /**
     * 执行 Workflow Function
     */
    private FunctionResult executeWorkflowFunction(UIFunctionDefinition function, Map<String, Object> arguments) {
        WorkflowBinding binding = function.getWorkflowBinding();
        
        // 映射输入参数
        Map<String, Object> workflowInput = new HashMap<>();
        for (Map.Entry<String, String> entry : binding.getInputMappings().entrySet()) {
            String paramName = entry.getKey();
            String dataPath = entry.getValue();
            Object value = arguments.get(dataPath);
            workflowInput.put(paramName, value);
        }
        
        // 执行 Workflow
        // 实际实现中会调用 Workflow 服务
        Object result = function.getExecutor().execute(workflowInput);
        
        return FunctionResult.success(result);
    }
    
    /**
     * 执行 UI Component Function
     */
    private FunctionResult executeUIComponentFunction(UIFunctionDefinition function, Map<String, Object> arguments) {
        UIComponentDefinition component = function.getUiComponent();
        
        // 创建或更新 UI 组件
        // 实际实现中会调用 PageAgent 来管理组件
        Object result = function.getExecutor().execute(arguments);
        
        return FunctionResult.success(result);
    }
    
    /**
     * 执行 UE Interaction Function
     */
    private FunctionResult executeUEInteractionFunction(UIFunctionDefinition function, Map<String, Object> arguments) {
        UEInteractionDefinition interaction = function.getUeInteraction();
        
        // 执行交互逻辑
        // 实际实现中会触发相应的交互事件
        Object result = function.getExecutor().execute(arguments);
        
        return FunctionResult.success(result);
    }
}
```

### 3.3 JS 和组件层次提取方案

#### 3.3.1 JS 模块化提取

**JavaScript 模块结构**:

```javascript
/**
 * PageAgent SDK - JavaScript 模块
 * 用于前端页面与 PageAgent 交互
 */

// ============================================
// 核心模块
// ============================================

const PageAgentSDK = (function() {
    
    // 私有变量
    let _agentId = null;
    let _pageId = null;
    let _socket = null;
    let _eventHandlers = {};
    let _components = {};
    
    // ============================================
    // 初始化
    // ============================================
    
    function init(config) {
        _agentId = config.agentId;
        _pageId = config.pageId;
        
        // 建立 WebSocket 连接
        _connectWebSocket(config.wsUrl);
        
        // 初始化组件
        _initComponents(config.components);
        
        console.log('[PageAgentSDK] Initialized for agent:', _agentId);
    }
    
    // ============================================
    // WebSocket 连接
    // ============================================
    
    function _connectWebSocket(wsUrl) {
        _socket = new WebSocket(wsUrl);
        
        _socket.onopen = function() {
            console.log('[PageAgentSDK] WebSocket connected');
            _triggerEvent('ws.connected', {});
        };
        
        _socket.onmessage = function(event) {
            const message = JSON.parse(event.data);
            _handleMessage(message);
        };
        
        _socket.onerror = function(error) {
            console.error('[PageAgentSDK] WebSocket error:', error);
            _triggerEvent('ws.error', { error: error });
        };
        
        _socket.onclose = function() {
            console.log('[PageAgentSDK] WebSocket closed');
            _triggerEvent('ws.disconnected', {});
        };
    }
    
    // ============================================
    // 消息处理
    // ============================================
    
    function _handleMessage(message) {
        const type = message.type;
        const data = message.data;
        
        switch (type) {
            case 'component.update':
                _updateComponent(data);
                break;
            case 'data.update':
                _updateData(data);
                break;
            case 'event.trigger':
                _triggerEvent(data.eventType, data.eventData);
                break;
            case 'function.result':
                _handleFunctionResult(data);
                break;
            default:
                console.warn('[PageAgentSDK] Unknown message type:', type);
        }
    }
    
    // ============================================
    // 组件管理
    // ============================================
    
    function _initComponents(componentConfigs) {
        componentConfigs.forEach(config => {
            const component = _createComponent(config);
            _components[config.componentId] = component;
        });
    }
    
    function _createComponent(config) {
        const element = document.getElementById(config.componentId);
        if (!element) {
            console.warn('[PageAgentSDK] Component element not found:', config.componentId);
            return null;
        }
        
        // 绑定事件
        if (config.eventBindings) {
            config.eventBindings.forEach(eventBinding => {
                const [eventType, handler] = eventBinding.split(':');
                element.addEventListener(eventType, (e) => {
                    _handleComponentEvent(config.componentId, eventType, e);
                });
            });
        }
        
        return {
            element: element,
            config: config,
            data: {}
        };
    }
    
    function _updateComponent(data) {
        const component = _components[data.componentId];
        if (!component) {
            console.warn('[PageAgentSDK] Component not found:', data.componentId);
            return;
        }
        
        // 更新组件数据
        Object.assign(component.data, data.updates);
        
        // 更新 DOM
        _renderComponent(component);
    }
    
    function _renderComponent(component) {
        const element = component.element;
        const data = component.data;
        
        // 根据 componentType 进行不同的渲染
        switch (component.config.componentType) {
            case 'input':
            case 'textarea':
                element.value = data.value || '';
                break;
            case 'text':
            case 'label':
                element.textContent = data.text || '';
                break;
            case 'list':
                _renderList(element, data.items);
                break;
            case 'form':
                _renderForm(element, data);
                break;
            default:
                console.warn('[PageAgentSDK] Unknown component type:', component.config.componentType);
        }
    }
    
    // ============================================
    // 事件处理
    // ============================================
    
    function _handleComponentEvent(componentId, eventType, event) {
        const component = _components[componentId];
        if (!component) return;
        
        const eventData = {
            componentId: componentId,
            eventType: eventType,
            value: _getComponentValue(component),
            timestamp: new Date().toISOString()
        };
        
        // 发送事件到后端
        _sendMessage('event.trigger', eventData);
        
        // 触发本地事件
        _triggerEvent('component.event', eventData);
    }
    
    function _getComponentValue(component) {
        const element = component.element;
        
        switch (component.config.componentType) {
            case 'input':
            case 'textarea':
            case 'select':
                return element.value;
            case 'checkbox':
                return element.checked;
            default:
                return null;
        }
    }
    
    // ============================================
    // 数据管理
    // ============================================
    
    function _updateData(data) {
        const dataPath = data.dataPath;
        const value = data.value;
        
        // 更新绑定的组件
        Object.values(_components).forEach(component => {
            const bindings = component.config.dataBindings;
            if (bindings && bindings[dataPath]) {
                component.data[bindings[dataPath]] = value;
                _renderComponent(component);
            }
        });
    }
    
    function getData(dataPath) {
        // 从后端获取数据
        return new Promise((resolve, reject) => {
            _sendMessage('data.get', { dataPath: dataPath });
            
            // 等待响应
            const handler = (event) => {
                if (event.dataPath === dataPath) {
                    removeEventListener('data.received', handler);
                    resolve(event.value);
                }
            };
            
            addEventListener('data.received', handler);
        });
    }
    
    function setData(dataPath, value) {
        // 发送数据到后端
        _sendMessage('data.set', { dataPath: dataPath, value: value });
    }
    
    // ============================================
    // Function Calling
    // ============================================
    
    function executeFunction(functionName, arguments) {
        return new Promise((resolve, reject) => {
            const callId = _generateCallId();
            
            _sendMessage('function.execute', {
                callId: callId,
                functionName: functionName,
                arguments: arguments
            });
            
            // 等待结果
            const handler = (event) => {
                if (event.callId === callId) {
                    removeEventListener('function.result', handler);
                    if (event.error) {
                        reject(new Error(event.error));
                    } else {
                        resolve(event.result);
                    }
                }
            };
            
            addEventListener('function.result', handler);
        });
    }
    
    function _handleFunctionResult(data) {
        _triggerEvent('function.result', data);
    }
    
    // ============================================
    // 事件监听
    // ============================================
    
    function addEventListener(eventType, handler) {
        if (!_eventHandlers[eventType]) {
            _eventHandlers[eventType] = [];
        }
        _eventHandlers[eventType].push(handler);
    }
    
    function removeEventListener(eventType, handler) {
        const handlers = _eventHandlers[eventType];
        if (handlers) {
            const index = handlers.indexOf(handler);
            if (index !== -1) {
                handlers.splice(index, 1);
            }
        }
    }
    
    function _triggerEvent(eventType, eventData) {
        const handlers = _eventHandlers[eventType];
        if (handlers) {
            handlers.forEach(handler => {
                try {
                    handler(eventData);
                } catch (error) {
                    console.error('[PageAgentSDK] Event handler error:', error);
                }
            });
        }
    }
    
    // ============================================
    // 工具方法
    // ============================================
    
    function _sendMessage(type, data) {
        if (_socket && _socket.readyState === WebSocket.OPEN) {
            _socket.send(JSON.stringify({
                type: type,
                data: data
            }));
        } else {
            console.error('[PageAgentSDK] WebSocket not connected');
        }
    }
    
    function _generateCallId() {
        return 'call_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
    }
    
    // ============================================
    // 公共 API
    // ============================================
    
    return {
        init: init,
        getData: getData,
        setData: setData,
        executeFunction: executeFunction,
        addEventListener: addEventListener,
        removeEventListener: removeEventListener
    };
})();

// 导出模块
if (typeof module !== 'undefined' && module.exports) {
    module.exports = PageAgentSDK;
}
```

#### 3.3.2 组件层次提取

**组件层次结构**:

```
Component Hierarchy:
├── BaseComponent (基础组件)
│   ├── properties (属性)
│   ├── events (事件)
│   ├── methods (方法)
│   └── lifecycle (生命周期)
│
├── UIComponent (UI 组件)
│   ├── FormComponent (表单组件)
│   │   ├── InputComponent (输入框)
│   │   ├── SelectComponent (下拉框)
│   │   ├── CheckboxComponent (复选框)
│   │   └── ButtonComponent (按钮)
│   │
│   ├── ListComponent (列表组件)
│   │   ├── TableComponent (表格)
│   │   ├── CardListComponent (卡片列表)
│   │   └── TreeComponent (树形列表)
│   │
│   └── ChartComponent (图表组件)
│       ├── LineChartComponent (折线图)
│       ├── BarChartComponent (柱状图)
│       └── PieChartComponent (饼图)
│
└── ContainerComponent (容器组件)
    ├── LayoutComponent (布局组件)
    ├── TabComponent (标签页组件)
    └── ModalComponent (模态框组件)
```

**组件定义示例**:

```javascript
/**
 * 组件层次定义 - JavaScript
 */

// ============================================
// 基础组件
// ============================================

class BaseComponent {
    constructor(config) {
        this.componentId = config.componentId;
        this.componentType = config.componentType;
        this.properties = config.properties || {};
        this.events = config.events || [];
        this.methods = config.methods || {};
        this.lifecycle = {
            onCreate: config.onCreate || null,
            onMount: config.onMount || null,
            onUpdate: config.onUpdate || null,
            onDestroy: config.onDestroy || null
        };
        this.state = {};
    }
    
    // 生命周期方法
    create() {
        if (this.lifecycle.onCreate) {
            this.lifecycle.onCreate.call(this);
        }
    }
    
    mount() {
        if (this.lifecycle.onMount) {
            this.lifecycle.onMount.call(this);
        }
    }
    
    update(newProps) {
        Object.assign(this.properties, newProps);
        if (this.lifecycle.onUpdate) {
            this.lifecycle.onUpdate.call(this, newProps);
        }
    }
    
    destroy() {
        if (this.lifecycle.onDestroy) {
            this.lifecycle.onDestroy.call(this);
        }
    }
    
    // 状态管理
    setState(newState) {
        Object.assign(this.state, newState);
        this.update(this.state);
    }
    
    getState() {
        return this.state;
    }
    
    // 事件触发
    triggerEvent(eventType, eventData) {
        const event = {
            type: eventType,
            data: eventData,
            componentId: this.componentId,
            timestamp: new Date().toISOString()
        };
        
        // 触发事件监听器
        if (this.events[eventType]) {
            this.events[eventType].forEach(handler => {
                handler(event);
            });
        }
    }
}

// ============================================
// UI 组件
// ============================================

class UIComponent extends BaseComponent {
    constructor(config) {
        super(config);
        this.element = null;
        this.dataBindings = config.dataBindings || {};
        this.eventBindings = config.eventBindings || [];
        this.styleClass = config.styleClass || '';
        this.layout = config.layout || {};
    }
    
    // 渲染组件
    render() {
        // 由子类实现
    }
    
    // 绑定数据
    bindData(dataPath, propertyName) {
        this.dataBindings[dataPath] = propertyName;
    }
    
    // 绑定事件
    bindEvent(eventType, handler) {
        if (!this.events[eventType]) {
            this.events[eventType] = [];
        }
        this.events[eventType].push(handler);
    }
    
    // 更新数据
    updateData(dataPath, value) {
        if (this.dataBindings[dataPath]) {
            const propertyName = this.dataBindings[dataPath];
            this.properties[propertyName] = value;
            this.render();
        }
    }
}

// ============================================
// 表单组件
// ============================================

class FormComponent extends UIComponent {
    constructor(config) {
        super(config);
        this.fields = config.fields || [];
        this.validators = config.validators || {};
        this.formData = {};
    }
    
    render() {
        // 渲染表单
        const formElement = document.createElement('form');
        formElement.id = this.componentId;
        formElement.className = this.styleClass;
        
        this.fields.forEach(field => {
            const fieldElement = this._renderField(field);
            formElement.appendChild(fieldElement);
        });
        
        this.element = formElement;
        return formElement;
    }
    
    _renderField(field) {
        const fieldContainer = document.createElement('div');
        fieldContainer.className = 'form-field';
        
        // 标签
        if (field.label) {
            const labelElement = document.createElement('label');
            labelElement.textContent = field.label;
            labelElement.setAttribute('for', field.fieldId);
            fieldContainer.appendChild(labelElement);
        }
        
        // 输入控件
        const inputElement = this._createInputElement(field);
        fieldContainer.appendChild(inputElement);
        
        return fieldContainer;
    }
    
    _createInputElement(field) {
        let inputElement;
        
        switch (field.type) {
            case 'text':
            case 'email':
            case 'password':
                inputElement = document.createElement('input');
                inputElement.type = field.type;
                inputElement.value = field.value || '';
                break;
            case 'textarea':
                inputElement = document.createElement('textarea');
                inputElement.value = field.value || '';
                break;
            case 'select':
                inputElement = document.createElement('select');
                field.options.forEach(option => {
                    const optionElement = document.createElement('option');
                    optionElement.value = option.value;
                    optionElement.textContent = option.label;
                    inputElement.appendChild(optionElement);
                });
                break;
            default:
                inputElement = document.createElement('input');
                inputElement.type = 'text';
        }
        
        inputElement.id = field.fieldId;
        inputElement.name = field.name;
        
        // 绑定事件
        inputElement.addEventListener('change', (e) => {
            this.formData[field.name] = e.target.value;
            this.triggerEvent('field.change', {
                fieldName: field.name,
                value: e.target.value
            });
        });
        
        return inputElement;
    }
    
    // 获取表单数据
    getFormData() {
        return this.formData;
    }
    
    // 设置表单数据
    setFormData(data) {
        this.formData = data;
        this.fields.forEach(field => {
            const inputElement = document.getElementById(field.fieldId);
            if (inputElement && data[field.name] !== undefined) {
                inputElement.value = data[field.name];
            }
        });
    }
    
    // 验证表单
    validate() {
        const errors = {};
        
        this.fields.forEach(field => {
            const value = this.formData[field.name];
            const validators = this.validators[field.name];
            
            if (validators) {
                validators.forEach(validator => {
                    const error = validator(value);
                    if (error) {
                        errors[field.name] = error;
                    }
                });
            }
        });
        
        return {
            isValid: Object.keys(errors).length === 0,
            errors: errors
        };
    }
    
    // 提交表单
    submit() {
        const validation = this.validate();
        if (!validation.isValid) {
            this.triggerEvent('form.invalid', validation);
            return;
        }
        
        this.triggerEvent('form.submit', {
            data: this.formData
        });
    }
}

// ============================================
// 列表组件
// ============================================

class ListComponent extends UIComponent {
    constructor(config) {
        super(config);
        this.items = config.items || [];
        this.itemTemplate = config.itemTemplate || null;
        this.selectedItems = [];
    }
    
    render() {
        const listElement = document.createElement('div');
        listElement.id = this.componentId;
        listElement.className = this.styleClass;
        
        this.items.forEach((item, index) => {
            const itemElement = this._renderItem(item, index);
            listElement.appendChild(itemElement);
        });
        
        this.element = listElement;
        return listElement;
    }
    
    _renderItem(item, index) {
        const itemElement = document.createElement('div');
        itemElement.className = 'list-item';
        itemElement.dataset.index = index;
        
        if (this.itemTemplate) {
            itemElement.innerHTML = this.itemTemplate(item);
        } else {
            itemElement.textContent = JSON.stringify(item);
        }
        
        // 绑定点击事件
        itemElement.addEventListener('click', (e) => {
            this._handleItemClick(item, index, e);
        });
        
        return itemElement;
    }
    
    _handleItemClick(item, index, event) {
        // 切换选中状态
        const selectedIndex = this.selectedItems.indexOf(index);
        if (selectedIndex === -1) {
            this.selectedItems.push(index);
        } else {
            this.selectedItems.splice(selectedIndex, 1);
        }
        
        this.triggerEvent('item.click', {
            item: item,
            index: index,
            selected: this.selectedItems.includes(index)
        });
    }
    
    // 添加项目
    addItem(item) {
        this.items.push(item);
        this.render();
    }
    
    // 移除项目
    removeItem(index) {
        this.items.splice(index, 1);
        this.render();
    }
    
    // 更新项目
    updateItem(index, newItem) {
        this.items[index] = newItem;
        this.render();
    }
    
    // 获取选中项目
    getSelectedItems() {
        return this.selectedItems.map(index => this.items[index]);
    }
}

// ============================================
// 导出组件
// ============================================

if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        BaseComponent,
        UIComponent,
        FormComponent,
        ListComponent
    };
}
```

---

## 四、第二阶段：流式 UI (ou-core 集成)

### 4.1 流式 UI 概念

**流式 UI** 是基于 ou-core 的实时渲染 UI，支持：
- 实时数据绑定
- 动态组件加载
- 流式渲染
- 智能布局

### 4.2 ou-core 集成架构

```
┌─────────────────────────────────────────────────────────────┐
│                    流式 UI 架构                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              ou-core Streaming Engine                  │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │ │
│  │  │ Real-time   │  │ Dynamic     │  │ Streaming   │  │ │
│  │  │ Data Binding│  │ Component   │  │ Renderer    │  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  │ │
│  └───────────────────────────────────────────────────────┘ │
│                              ↓                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              Advanced UI Capabilities                  │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │ │
│  │  │ Drag & Drop │  │ Visual      │  │ Smart       │  │ │
│  │  │ Designer    │  │ Orchestration│  │ Layout      │  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  │ │
│  └───────────────────────────────────────────────────────┘ │
│                              ↓                              │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              Performance Optimization                  │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │ │
│  │  │ Lazy Load   │  │ Caching     │  │ Render      │  │ │
│  │  │             │  │ Strategy    │  │ Optimization│  │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 五、实施计划

### 5.1 第一阶段实施计划 (2-3 个月)

#### 第 1-2 周：架构设计和基础实现
- [ ] 设计 PageAgent 接口和实现
- [ ] 设计 Function Calling 扩展方案
- [ ] 设计 UI/UE 数据定义标准
- [ ] 设计 JS 模块化提取方案

#### 第 3-4 周：核心功能开发
- [ ] 实现 PageAgent 核心类
- [ ] 实现 ExtendedFunctionCallingService
- [ ] 实现 UIComponentDefinition
- [ ] 实现 UEInteractionDefinition

#### 第 5-6 周：JavaScript SDK 开发
- [ ] 开发 PageAgentSDK
- [ ] 开发组件层次结构
- [ ] 开发表单组件
- [ ] 开发列表组件

#### 第 7-8 周：集成和测试
- [ ] 集成到 nexus 架构
- [ ] 集成到 skill-agent
- [ ] 编写单元测试
- [ ] 编写集成测试

#### 第 9-12 周：优化和文档
- [ ] 性能优化
- [ ] 代码重构
- [ ] 编写文档
- [ ] 编写示例

### 5.2 第二阶段实施计划 (2-3 个月)

#### 第 1-4 周：ou-core 集成
- [ ] 研究 ou-core 架构
- [ ] 设计集成方案
- [ ] 实现流式渲染引擎
- [ ] 实现实时数据绑定

#### 第 5-8 周：高级 UI 能力
- [ ] 实现拖拽式设计器
- [ ] 实现可视化编排
- [ ] 实现智能布局
- [ ] 实现动态组件加载

#### 第 9-12 周：性能优化和文档
- [ ] 实现按需加载
- [ ] 实现缓存策略
- [ ] 实现渲染优化
- [ ] 编写完整文档

---

## 六、总结

本方案提出了一个分阶段实施 A2UI 的完整计划：

**第一阶段**：
- 实现 PageAgent 功能集成
- 扩展 Function Calling 支持 UI/UE 数据定义
- 实现 JS 和组件层次提取
- 保留 HTML+JS+CSS 落地设计

**第二阶段**：
- 集成 ou-core 流式 UI
- 实现高级 UI 能力
- 性能优化

该方案既保留了现有的技术栈，又为未来的流式 UI 打下了基础，是一个务实且可落地的实施方案。

---

## 七、附录

### 7.1 关键文件路径

**Function Calling 实现**:
- `E:\github\ooder-skills\skills\_system\skill-agent\src\main\java\net\ooder\skill\agent\function\FunctionCallingService.java`
- `E:\github\ooder-skills\mvp\src\main\java\net\ooder\mvp\skill\scene\llm\FunctionCallingRegistry.java`

**Nexus 架构**:
- `E:\github\ooder-skills\temp\ooder-Nexus\`

**审批表单示例**:
- `E:\github\ooder-skills\skill-ui-test\skills\skill-approval-form\`

### 7.2 参考资料

1. OoderAgent(Nexus) Skills 移植完成总结报告: `E:\github\ooder-skills\docs\v3.0.1\OODER_AGENT_MIGRATION_SUMMARY.md`
2. 三大IM Skills与Apex深度融合: `E:\github\ooder-skills\docs\v3.0.1\blog-im-skills-apex-integration.md`
3. 场景驱动表单架构设计: `E:\github\ooder-skills\docs\场景驱动表单架构设计-A2UI标准化.md`

---

**文档维护**: 本文档应在后续开发过程中持续更新。

**变更记录**:
- 2026-04-06 v1.0: 初始版本创建，完成 A2UI 分阶段实施方案设计
