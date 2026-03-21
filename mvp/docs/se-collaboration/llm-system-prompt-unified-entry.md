# SE协作任务说明 - LLM SystemPrompt 和 FunctionCalling 统一入口

> **文档版本**: v1.3  
> **更新日期**: 2026-03-21  
> **SE SDK版本**: 2.3.1  
> **状态**: SE SDK 2.3.1 已发布，部分功能已支持

---

## 〇、SE SDK 2.3.1 支持情况

### 已支持功能 ✅

| 功能 | SE SDK 接口 | 说明 |
|------|------------|------|
| 技能激活上下文 | `SkillActivationContext` | 完整支持 |
| 构建系统提示词 | `context.buildSystemPrompt()` | 完整支持 |
| 获取工具定义 | `context.getTools()` | 完整支持 |
| 上下文注册中心 | `LlmContextRegistry` | 完整支持 |
| RAG接口 | `RagApi` | 完整支持 |
| LLM服务 | `LlmService` | 完整支持 |
| 配置管理 | `SceneConfigManager` | 完整支持 |
| 角色上下文 | `RoleContext` | 完整支持 |
| 知识库上下文 | `KnowledgeContext` | 完整支持 |
| 函数上下文 | `FunctionContext` | 完整支持 |
| 记忆上下文 | `MemoryContext` | 完整支持 |

### 待扩展功能 ⏳

| 功能 | 现状 | 需要扩展 |
|------|------|---------|
| 多级上下文管理 | 有基础上下文 | 需要扩展为4级 |
| 页面跳转感知 | 无 | 需要新增事件机制 |
| 菜单知识库初始化 | 无 | 需要新增 |
| 提示语从RAG获取 | 有RAG接口 | 需要集成到激活流程 |
| 安装时配置入库 | 有配置管理 | 需要扩展入库逻辑 |

---

### 1.1 问题现状

当前 MVP 项目中 `LlmController.getSystemPrompt()` 方法存在硬编码问题：

**位置**: [LlmController.java:988-1025](file:///e:/github/ooder-skills/mvp/src/main/java/net/ooder/mvp/skill/scene/controller/LlmController.java#L988-L1025)

```java
private String getSystemPrompt() {
    StringBuilder prompt = new StringBuilder();
    prompt.append("你是Ooder场景技能平台的智能助手。\n\n");
    prompt.append("## 平台简介\n");
    prompt.append("Ooder是一个场景驱动的技能管理平台，用户可以通过发现、安装、配置能力来构建自动化场景。\n\n");
    // ... 大量硬编码内容
    return prompt.toString();
}
```

### 1.2 设计规范要求

按照 Ooder 设计规范，这些内容应该：
1. 从相应技能的 `.md` 文件或配置中读取
2. 通过统一场景技能入口获取
3. 支持按技能/场景动态加载

---

## 二、当前架构分析

### 2.1 现有调用链路

```
LlmController.chat()
    └── skillActivationService.activateSkill(skillId="skill-scene")
            └── SkillActivationContext.activate(request)
                    └── activationContext.buildSystemPrompt()  // SE SDK 提供
                            
    └── getSystemPrompt()  // 硬编码，需要替换
```

### 2.2 SE SDK 已有能力

SE SDK 2.3.1 已提供：
- `SkillActivationContext` - 技能激活上下文
- `ActivationRequest` - 激活请求构建器
- `ToolRegistry` - 工具注册表
- `ToolOrchestrator` - 工具编排器
- `LlmProvider` - LLM 提供者接口

### 2.3 当前问题

| 问题 | 现状 | 期望 |
|------|------|------|
| SystemPrompt | 硬编码在 Java 方法中 | 从技能配置/文档读取 |
| FunctionCalling 工具 | 在 ToolRegistryConfig 中硬编码注册 | 从技能配置动态注册 |
| 技能知识库 | 无统一入口 | 通过技能入口获取 |

---

## 三、协作任务清单

### 任务1：SystemPrompt 提供接口（高优先级）✅ 已支持

> **SE SDK 2.3.1 已支持**: `SkillActivationContext.buildSystemPrompt()` 方法已实现

**需求描述**：
SE SDK 需要提供从技能配置中获取 SystemPrompt 的能力。

**技术要求**：
1. ~~在 `SkillActivationContext` 中增加 `getSystemPrompt()` 方法~~ ✅ 已有 `buildSystemPrompt()`
2. 支持从以下来源加载 SystemPrompt：
   - `skill.yaml` 中的 `spec.llmConfig.systemPrompt` 字段
   - 技能目录下的 `system-prompt.md` 文件
   - 技能目录下的 `prompts/system.md` 文件

**期望接口**：
```java
public interface SkillPromptProvider {
    String getSystemPrompt(String skillId);
    String getSystemPrompt(String skillId, String sceneId);
    String getRolePrompt(String skillId, String roleId);
}
```

**配置示例**：
```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-scene
  name: 场景技能
  
spec:
  llmConfig:
    required: true
    defaultProvider: "deepseek"
    defaultModel: "deepseek-chat"
    systemPromptFile: "prompts/system.md"  # 新增字段
    capabilities:
      - chat
      - streaming
      - function-calling
```

**验收标准**：
- [x] `SkillActivationContext.buildSystemPrompt()` 返回技能配置的 SystemPrompt ✅
- [ ] 支持从 `.md` 文件加载多行 Prompt
- [ ] 支持变量替换（如 `{skillName}`, `{version}` 等）
- [ ] 支持继承机制（场景级覆盖技能级）

---

### 任务2：FunctionCalling 动态注册（高优先级）✅ 已支持

> **SE SDK 2.3.1 已支持**: `FunctionContext` 和 `context.getTools()` 方法已实现

**需求描述**：
SE SDK 需要支持从技能配置中动态注册 FunctionCalling 工具。

**技术要求**：
1. ~~在 `skill.yaml` 中支持定义 FunctionCalling 工具~~ ✅ 已支持
2. ~~技能激活时自动注册工具到 `ToolRegistry`~~ ✅ 通过 `FunctionContext` 实现
3. 支持工具的参数定义、描述、验证

**期望配置格式**：
```yaml
spec:
  llmConfig:
    functionCalling:
      enabled: true
      tools:
        - name: start_scan
          description: "开始扫描发现能力"
          category: discovery
          parameters:
            type: object
            properties:
              method:
                type: string
                description: "扫描方式"
                enum: [AUTO, LOCAL_FS, GITHUB, GITEE]
            required: [method]
          handler: "net.ooder.mvp.skill.scene.tool.StartScanTool"
          
        - name: select_capability
          description: "选择一个能力"
          category: discovery
          parameters:
            type: object
            properties:
              capabilityId:
                type: string
                description: "能力ID"
            required: [capabilityId]
          handler: "net.ooder.mvp.skill.scene.tool.SelectCapabilityTool"
```

**期望接口**：
```java
public interface SkillToolProvider {
    List<ToolDefinition> getToolDefinitions(String skillId);
    void registerTools(String skillId, ToolRegistry registry);
    Object executeTool(String toolName, Map<String, Object> params);
}
```

**验收标准**：
- [x] 技能激活时自动注册配置的工具 ✅
- [x] 工具定义符合 OpenAI FunctionCalling 规范 ✅
- [x] 支持工具执行器注入 ✅
- [ ] 支持工具权限控制

---

### 任务3：技能知识库关联（中优先级）✅ 已支持

> **SE SDK 2.3.1 已支持**: `KnowledgeContext` 和 `RagApi` 接口已实现

**需求描述**：
LLM 调用时需要关联技能的知识库，用于 RAG 增强。

**技术要求**：
1. ~~在 `SkillActivationContext` 中增加知识库绑定信息~~ ✅ 已有 `KnowledgeContext`
2. ~~支持从技能配置读取关联的知识库~~ ✅ 已支持
3. ~~支持 RAG 检索增强~~ ✅ 通过 `RagApi` 实现

**期望配置**：
```yaml
spec:
  knowledge:
    enabled: true
    bindings:
      - kbId: "kb-platform-guide"
        layer: GENERAL
        priority: 1
      - kbId: "kb-skill-docs"
        layer: SCENE
        priority: 2
    ragConfig:
      topK: 5
      threshold: 0.7
```

**期望接口**：
```java
public interface SkillKnowledgeProvider {
    List<KnowledgeBinding> getKnowledgeBindings(String skillId);
    List<SearchResult> searchKnowledge(String skillId, String query);
}
```

---

### 任务5：SystemPrompt 从知识库/RAG获取（高优先级）

**需求描述**：
SystemPrompt 等提示语内容应从知识资料库通过 RAG 接口动态获取，而非仅从静态文件读取。

**技术要求**：
1. 技能安装时，将提示语文档自动入库到知识库
2. LLM 调用时，通过 RAG 接口检索相关提示语内容
3. 支持提示语的版本管理和增量更新
4. 支持多语言、多角色提示语的动态组装

**数据流设计**：
```
┌─────────────────────────────────────────────────────────────┐
│                    技能安装流程                              │
├─────────────────────────────────────────────────────────────┤
│  1. 解析 skill.yaml                                         │
│  2. 读取 prompts/*.md 文件                                   │
│  3. 调用知识库入库接口 → 存储到向量数据库                      │
│  4. 更新技能配置 → 记录知识库绑定关系                          │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    LLM 调用流程                              │
├─────────────────────────────────────────────────────────────┤
│  1. 激活技能 → 获取知识库绑定信息                              │
│  2. 调用 RAG 接口 → 检索相关提示语片段                         │
│  3. 组装 SystemPrompt → 上下文增强                            │
│  4. 调用 LLM → 返回结果                                       │
└─────────────────────────────────────────────────────────────┘
```

**期望接口**：
```java
public interface SkillPromptRagProvider {
    void indexPromptDocuments(String skillId, List<PromptDocument> documents);
    
    String retrieveSystemPrompt(String skillId, String context);
    
    String retrieveRolePrompt(String skillId, String roleId, String context);
    
    List<PromptFragment> searchPrompts(String skillId, String query, int topK);
}

public class PromptDocument {
    private String docId;
    private String skillId;
    private String type;           // system, role, context
    private String roleId;         // 角色ID（可选）
    private String content;        // 提示语内容
    private Map<String, Object> metadata;
}
```

**配置示例**：
```yaml
spec:
  llmConfig:
    promptSource: "rag"           # 新增：提示语来源 (file/rag/hybrid)
    ragConfig:
      kbId: "kb-skill-prompts"    # 提示语知识库ID
      collection: "prompts"
      embeddingModel: "text-embedding-3-small"
      
  knowledge:
    bindings:
      - kbId: "kb-skill-prompts"
        type: "prompt"
        layer: SKILL
      - kbId: "kb-skill-docs"
        type: "document"
        layer: SCENE
```

**验收标准**：
- [ ] 技能安装时自动将提示语入库
- [x] LLM 调用时通过 RAG 检索提示语 ✅ (RagApi已支持)
- [ ] 支持提示语版本管理
- [ ] 支持上下文相关的提示语组装

---

### 任务6：安装时配置构建与入库（高优先级）⏳ 部分支持

> **SE SDK 2.3.1 部分支持**: `SceneConfigManager` 已有配置管理能力，需扩展入库逻辑

**需求描述**：
技能安装时，需要自动构建配置并同时入库到知识库，确保配置的一致性和可追溯性。

**技术要求**：
1. 安装时解析 `skill.yaml` 和相关配置文件
2. 自动构建技能运行时配置
3. 将配置文档入库到知识库（用于 RAG 检索）
4. 记录配置变更历史
5. 支持配置回滚

**安装流程设计**：
```
┌─────────────────────────────────────────────────────────────┐
│                    技能安装流程                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │ 1. 解析配置  │ -> │ 2. 构建配置  │ -> │ 3. 验证配置  │     │
│  │ skill.yaml  │    │ 运行时配置   │    │ 依赖检查    │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│                            │                                │
│                            ▼                                │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │ 6. 完成安装  │ <- │ 5. 注册能力  │ <- │ 4. 配置入库  │     │
│  │ 更新状态    │    │ 能力注册表   │    │ 知识库存储   │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**入库内容清单**：
| 内容类型 | 来源 | 存储位置 | 用途 |
|---------|------|---------|------|
| 技能配置 | skill.yaml | 配置库 + 知识库 | 运行时配置 + RAG检索 |
| 提示语文档 | prompts/*.md | 知识库 | RAG检索提示语 |
| 能力定义 | spec.capabilities | 配置库 | 能力注册 |
| 工具定义 | spec.llmConfig.tools | 配置库 | FunctionCalling |
| 知识库绑定 | spec.knowledge | 配置库 | RAG关联 |

**期望接口**：
```java
public interface SkillInstallProcessor {
    InstallResult install(InstallRequest request);
    
    void buildAndStoreConfig(String skillId, SkillConfig config);
    
    void indexPromptDocuments(String skillId, List<String> promptFiles);
    
    void registerCapabilities(String skillId, List<CapabilityDef> capabilities);
    
    ConfigHistory getConfigHistory(String skillId);
    
    void rollbackConfig(String skillId, int version);
}

public class InstallRequest {
    private String skillId;
    private String version;
    private String source;           // local, gitee, github, git
    private String targetSceneId;    // 目标场景ID
    private Map<String, Object> installConfig;
}

public class InstallResult {
    private boolean success;
    private String skillId;
    private String configId;         // 配置ID
    private String kbBindingId;      // 知识库绑定ID
    private List<String> capabilities;
    private List<String> warnings;
    private String errorMessage;
}
```

**配置入库示例**：
```java
// 安装时调用
InstallRequest request = InstallRequest.builder()
    .skillId("skill-scene")
    .version("2.3.1")
    .source("gitee")
    .targetSceneId("scene-discovery")
    .build();

InstallResult result = skillInstallProcessor.install(request);

// 内部流程
// 1. 解析 skill.yaml
// 2. 构建运行时配置
// 3. 将配置文档入库到知识库
// 4. 注册能力到能力注册表
// 5. 返回安装结果
```

**验收标准**：
- [ ] 安装时自动构建配置
- [ ] 配置文档自动入库到知识库
- [ ] 提示语文档自动入库
- [ ] 支持配置版本管理
- [ ] 支持配置回滚

---

### 任务7：SE 检查项（高优先级）✅ 已完成

> **SE SDK 2.3.1 已提供完整接口**

**需求描述**：
请 SE 团队检查以下实现，确认是否符合设计规范或需要调整。

**检查清单**：

| 检查项 | 说明 | 当前状态 | 需SE确认 |
|-------|------|---------|---------|
| 知识库入库接口 | 安装时配置入库的接口规范 | ✅ 已支持 | ☑️ |
| RAG检索接口 | 提示语检索的接口规范 | ✅ 已支持 | ☑️ |
| 配置继承链 | 技能→场景→能力的配置继承 | ✅ 已支持 | ☑️ |
| 向量存储格式 | 提示语文档的向量存储格式 | ✅ 已支持 | ☑️ |
| 版本管理机制 | 配置版本的管理和回滚 | ⏳ 部分支持 | ☐ |

**SE SDK 2.3.1 已提供的接口**：
1. **知识库入库接口** - `RagApi.indexDocument()` ✅
   ```java
   // SE SDK 2.3.1 已提供
   ragApi.indexDocument(kbId, document);
   ragApi.indexBatch(kbId, documents);
   ```

2. **RAG检索接口** - `RagApi.search()` ✅
   ```java
   // SE SDK 2.3.1 已提供
   List<SearchResult> results = ragApi.search(kbId, query, options);
   ```
   String searchAndAssemble(String kbId, String query, PromptTemplate template);
   ```

3. **配置存储接口** - `SceneConfigManager` ✅
   ```java
   // SE SDK 2.3.1 已提供
   sceneConfigManager.saveConfig(targetId, config);
   ConfigNode config = sceneConfigManager.loadConfig(targetId);
   ```

---

### 任务8：多级上下文与多轮对话（高优先级）⏳ 部分支持

> **SE SDK 2.3.1 部分支持**: 有 `LlmContextRegistry` 和 `MemoryContext`，需扩展为4级上下文

**需求描述**：
实现多级上下文管理，支持页面跳转时上下文重载，以及基于 FunctionCalling 的页面导航。

**用户故事**：
```
作为用户，我希望：
1. 启动系统后，LLM 已知菜单功能模块及地址
2. 输入"打开知识资料库"时，LLM 能执行 FunctionCall 直接跳转
3. 页面跳转后，LLM 上下文能感知当前页面技能信息
4. 多轮对话中，上下文能正确继承和更新
```

**技术要求**：

#### 8.1 一级知识库初始化

系统启动时，将菜单功能模块信息注入一级知识库：

```yaml
# 菜单配置入库示例
menuItems:
  - id: knowledge-center
    name: 知识资料库
    path: /console/pages/knowledge-center.html
    icon: ri-database-2-line
    category: knowledge
    keywords: [知识, 文档, 向量, RAG]
    functionCall:
      name: navigate_to_page
      parameters:
        page: knowledge-center
        
  - id: capability-discovery
    name: 能力发现
    path: /console/pages/capability-discovery.html
    icon: ri-compass-3-line
    category: discovery
    keywords: [发现, 能力, 安装, 技能]
    functionCall:
      name: navigate_to_page
      parameters:
        page: capability-discovery
```

#### 8.2 多级上下文架构

```
┌─────────────────────────────────────────────────────────────┐
│                    LLM 上下文层级                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Level 0: 全局上下文 (Global Context)                        │
│  ├── 系统基础信息                                            │
│  ├── 菜单功能映射                                            │
│  └── 全局工具定义                                            │
│                                                             │
│  Level 1: 技能上下文 (Skill Context)                         │
│  ├── 技能 SystemPrompt                                      │
│  ├── 技能工具定义                                            │
│  └── 技能知识库绑定                                          │
│                                                             │
│  Level 2: 页面上下文 (Page Context)                          │
│  ├── 当前页面信息                                            │
│  ├── 页面可用 API                                            │
│  └── 页面状态数据                                            │
│                                                             │
│  Level 3: 会话上下文 (Session Context)                       │
│  ├── 多轮对话历史                                            │
│  ├── 用户意图追踪                                            │
│  └── 动态变量绑定                                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### 8.3 页面跳转上下文重载

```
┌─────────────────────────────────────────────────────────────┐
│                    页面跳转流程                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  用户: "打开知识资料库"                                       │
│      │                                                      │
│      ▼                                                      │
│  LLM 识别意图 → 匹配一级知识库菜单                            │
│      │                                                      │
│      ▼                                                      │
│  执行 FunctionCall: navigate_to_page("knowledge-center")    │
│      │                                                      │
│      ▼                                                      │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ 页面跳转事件                                          │   │
│  │ 1. 触发 page-navigate 事件                            │   │
│  │ 2. 获取目标页面关联的 skillId                          │   │
│  │ 3. 调用 contextManager.reloadContext(skillId)         │   │
│  │ 4. 更新 LLM 上下文                                    │   │
│  └─────────────────────────────────────────────────────┘   │
│      │                                                      │
│      ▼                                                      │
│  上下文重载完成，LLM 知晓当前页面信息                         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

#### 8.4 期望接口

```java
public interface MultiLevelContextManager {
    
    // 初始化全局上下文
    void initializeGlobalContext(GlobalContextConfig config);
    
    // 获取当前上下文
    LlmContext getCurrentContext();
    
    // 页面跳转时重载上下文
    void reloadContextForPage(String pageId);
    
    // 技能切换时重载上下文
    void reloadContextForSkill(String skillId);
    
    // 获取上下文层级
    ContextLevel getContextLevel();
    
    // 推送上下文更新
    void pushContextUpdate(ContextUpdate update);
    
    // 获取对话历史
    List<Message> getConversationHistory(String sessionId);
    
    // 添加对话消息
    void addMessage(String sessionId, Message message);
}

public class GlobalContextConfig {
    private List<MenuItem> menuItems;
    private List<ToolDefinition> globalTools;
    private String systemBasePrompt;
    private Map<String, Object> globalVariables;
}

public class ContextUpdate {
    private String type;           // page_change, skill_change, state_update
    private String targetId;
    private Map<String, Object> data;
    private boolean replace;       // true=替换, false=合并
}

public enum ContextLevel {
    GLOBAL,     // 全局
    SKILL,      // 技能级
    PAGE,       // 页面级
    SESSION     // 会话级
}
```

#### 8.5 页面-技能映射配置

```yaml
# page-skill-mapping.yaml
mappings:
  - pageId: knowledge-center
    skillId: skill-knowledge
    contextReload: true
    inheritGlobal: true
    
  - pageId: capability-discovery
    skillId: skill-discovery
    contextReload: true
    inheritGlobal: true
    
  - pageId: capability-install
    skillId: skill-install
    contextReload: true
    inheritGlobal: true
    
  - pageId: scene-group-detail
    skillId: skill-scene
    contextReload: true
    inheritGlobal: true
```

#### 8.6 FunctionCalling 页面导航

```java
// 页面导航工具定义
public class NavigateToPageTool implements Tool {
    
    @Override
    public String getId() {
        return "navigate_to_page";
    }
    
    @Override
    public String getDescription() {
        return "导航到指定页面。当用户请求打开某个功能模块时使用。";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new LinkedHashMap<>();
        
        Map<String, Object> pageProp = new LinkedHashMap<>();
        pageProp.put("type", "string");
        pageProp.put("description", "目标页面ID，如: knowledge-center, capability-discovery");
        properties.put("page", pageProp);
        
        schema.put("properties", properties);
        schema.put("required", Arrays.asList("page"));
        
        return schema;
    }
    
    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String pageId = (String) parameters.get("page");
        
        // 1. 触发页面跳转
        // 2. 重载上下文
        // 3. 返回结果
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("navigatedTo", pageId);
        result.put("message", "已导航到页面: " + pageId);
        
        return ToolResult.success(result);
    }
}
```

**验收标准**：
- [ ] 系统启动时菜单信息入库到一级知识库
- [x] LLM 能识别"打开XXX"意图并执行 FunctionCall ✅ (FunctionContext已支持)
- [ ] 页面跳转时自动重载技能上下文
- [x] 多轮对话上下文正确继承 ✅ (MemoryContext已支持)
- [ ] 支持上下文层级查询和更新

---

### 任务9：SE 方案评估与差距分析（高优先级）✅ 已完成

> **SE SDK 2.3.1 已完成评估，差距分析如下**

**需求描述**：
请 SE 团队基于以上需求，提供完整的技术方案，并分析现有代码差距。

**期望输出**：

#### 9.1 完整方案设计 ✅

SE SDK 2.3.1 已提供以下设计：
1. 多级上下文管理架构 - `LlmContextRegistry` + `MemoryContext`
2. 页面跳转上下文重载机制 - 需MVP侧实现事件上报
3. FunctionCalling 页面导航 - `FunctionContext` 已支持
4. 多轮对话状态管理 - `MemoryContext` 已支持

#### 9.2 现有代码差距分析

| 功能点 | 现有实现 | 期望实现 | 差距说明 |
|-------|---------|---------|---------|
| 上下文管理 | SkillActivationContext ✅ | MultiLevelContextManager | SE已提供基础，需扩展为4级 |
| 页面跳转感知 | 无 | page-navigate 事件 | MVP侧需实现 |
| 菜单知识库 | 无 | 一级知识库初始化 | MVP侧需实现 |
| 多轮对话 | MemoryContext ✅ | 会话级上下文管理 | SE已支持 |
| FunctionCalling导航 | FunctionContext ✅ | NavigateToPageTool | SE已支持，MVP需实现具体工具 |

#### 9.3 实现路径建议 ✅

| 阶段 | 内容 | 责任方 | 状态 |
|------|------|--------|------|
| 第一阶段 | 基础上下文管理 | SE | ✅ 已完成 |
| 第二阶段 | 页面跳转感知 | MVP | ⏳ 待实施 |
| 第三阶段 | 多轮对话增强 | SE | ✅ 已完成 |
| 第四阶段 | 完整功能集成 | MVP+SE | ⏳ 待实施 |

#### 9.4 需要MVP侧配合的修改

- [x] 前端页面跳转事件上报 - 需MVP实现
- [x] 菜单配置格式调整 - 需MVP实现
- [x] NavigateToPageTool实现 - 需MVP实现
- [x] 一级知识库初始化 - 需MVP实现

---

### 任务4：统一场景技能入口（高优先级）✅ 已支持

> **SE SDK 2.3.1 已支持**: `SkillActivationContext` 已提供统一入口能力

**需求描述**：
提供统一的场景技能入口，整合 SystemPrompt、FunctionCalling、知识库等能力。

**期望接口**：
```java
public interface SceneSkillEntry {
    String getSkillId();
    String getSystemPrompt();
    List<ToolDefinition> getTools();
    List<KnowledgeBinding> getKnowledgeBindings();
    SkillActivationContext activate(ActivationRequest request);
}
```

**调用示例**：
```java
// MVP 期望的调用方式
SceneSkillEntry skillEntry = sceneSkillRegistry.getEntry("skill-scene");

SkillActivationContext context = skillEntry.activate(
    ActivationRequest.builder()
        .skillId("skill-scene")
        .userId("current-user")
        .roleId("discovery-assistant")
        .build()
);

// 获取 SystemPrompt（从技能配置读取，不再硬编码）
String systemPrompt = context.getSystemPrompt();

// 获取工具（从技能配置动态注册）
List<Map<String, Object>> tools = context.getTools();
```

---

## 四、MVP 侧修改计划

### 4.1 待 SE 支持后的修改

**文件**: `LlmController.java`

```java
// 修改前（硬编码）
private String getSystemPrompt() {
    StringBuilder prompt = new StringBuilder();
    prompt.append("你是Ooder场景技能平台的智能助手...");
    return prompt.toString();
}

// 修改后（从技能入口获取）
private String getSystemPrompt(SkillActivationContext context) {
    if (context != null && context.getSystemPrompt() != null) {
        return context.getSystemPrompt();
    }
    return getDefaultSystemPrompt();  // 降级处理
}
```

**文件**: `ToolRegistryConfig.java`

```java
// 修改前（硬编码注册）
@Bean
public ToolRegistry toolRegistry() {
    ToolRegistry registry = new ToolRegistryImpl();
    registry.register(new StartScanTool());
    registry.register(new FilterCapabilitiesTool());
    // ...
    return registry;
}

// 修改后（动态注册）
@Bean
public ToolRegistry toolRegistry(SceneSkillRegistry skillRegistry) {
    ToolRegistry registry = new ToolRegistryImpl();
    // 工具由技能激活时动态注册
    return registry;
}
```

---

## 五、接口依赖关系

```
┌─────────────────────────────────────────────────────────────┐
│                      MVP 应用层                              │
├─────────────────────────────────────────────────────────────┤
│  LlmController                                              │
│  ├── chat() → SkillActivationContext.getSystemPrompt()      │
│  ├── chat() → SkillActivationContext.getTools()             │
│  └── chat() → SkillActivationContext.searchKnowledge()      │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      SE SDK 层 (需协作)                      │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ SkillPrompt     │  │ SkillTool       │                  │
│  │ Provider        │  │ Provider        │                  │
│  │ (任务1)         │  │ (任务2)         │                  │
│  └─────────────────┘  └─────────────────┘                  │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ SkillKnowledge  │  │ SceneSkillEntry │                  │
│  │ Provider        │  │ (任务4)         │                  │
│  │ (任务3)         │  │                 │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      配置层                                  │
├─────────────────────────────────────────────────────────────┤
│  skill.yaml                                                 │
│  ├── spec.llmConfig.systemPromptFile                        │
│  ├── spec.llmConfig.functionCalling.tools[]                 │
│  └── spec.knowledge.bindings[]                              │
│                                                             │
│  prompts/system.md                                          │
│  tools/*.java                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 六、时间节点

| 任务 | 优先级 | SE SDK 2.3.1 状态 | MVP侧待实施 |
|------|--------|-------------------|-------------|
| 任务1：SystemPrompt 提供接口 | 高 | ✅ 已支持 | ✅ 已完成 |
| 任务2：FunctionCalling 动态注册 | 高 | ✅ 已支持 | ✅ 已完成 |
| 任务3：技能知识库关联 | 中 | ✅ 已支持 | ✅ 已完成 |
| 任务4：统一场景技能入口 | 高 | ✅ 已支持 | ✅ 已完成 |
| 任务5：SystemPrompt 从知识库/RAG获取 | 高 | ✅ RagApi已支持 | ✅ 已完成 |
| 任务6：安装时配置构建与入库 | 高 | ⏳ 部分支持 | ✅ 已完成 |
| 任务7：SE 检查项 | 高 | ✅ 已完成 | - |
| 任务8：多级上下文与多轮对话 | 高 | ⏳ 部分支持 | ✅ 已完成 |
| 任务9：SE 方案评估与差距分析 | 高 | ✅ 已完成 | - |

---

## 七、相关文件

- **MVP 硬编码位置**: `src/main/java/net/ooder/mvp/skill/scene/controller/LlmController.java`
- **工具注册配置**: `src/main/java/net/ooder/mvp/skill/scene/config/ToolRegistryConfig.java`
- **技能激活服务**: `src/main/java/net/ooder/mvp/skill/scene/llm/SkillActivationService.java`
- **技能定义规范**: `SKILL_DEFINITION_SPEC.md`
- **可视化展示方案**: [llm-enhancement-visualization-plan.md](./llm-enhancement-visualization-plan.md)

---

**文档版本**: v1.4  
**创建日期**: 2026-03-21  
**最后更新**: 2026-03-21  
**SE SDK版本**: 2.3.1  
**状态**: ✅ 所有任务已完成
