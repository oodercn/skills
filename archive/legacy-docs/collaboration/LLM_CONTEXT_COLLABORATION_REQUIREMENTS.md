# LLM 上下文管理协作需求文档

> **版本**: 1.0.0  
> **日期**: 2026-03-14  
> **状态**: 待评审  
> **涉及团队**: SE Team, LLM Team, Skills Team

---

## 1. 概述

### 1.1 背景

当前 Ooder 平台的 LLM 模块存在以下问题：
- 上下文持久化分散在各 Skill 中实现，缺乏统一管理
- Skills 切换时上下文无法正确保存和恢复
- 知识库 RAG 未与对话自动关联
- LLM 调用仍为 Mock 实现，未接入真实 API

### 1.2 目标

建立完整的 LLM 上下文管理体系，实现：
1. **统一存储**: SE 提供集中式上下文持久化服务
2. **无缝切换**: Skills 切换时自动保存/恢复上下文
3. **智能检索**: 对话时自动触发知识库 RAG
4. **真实调用**: LLM Provider 接入真实 API

### 1.3 涉及模块

| 模块 | 负责团队 | 当前状态 | 目标状态 |
|------|----------|----------|----------|
| scene-engine (SE) | SE Team | 提供基础存储 | 提供上下文管理服务 |
| skill-llm-core | LLM Team | Mock 实现 | 真实 API 调用 |
| skill-llm-chat | Skills Team | 基础功能 | 完整闭环 |
| skill-knowledge-base | Skills Team | 内存存储 | 持久化 + RAG |

---

## 2. SE Team 需求

### 2.1 上下文持久化服务

#### 2.1.1 接口定义

```java
package net.ooder.skill.engine.context;

/**
 * Context Storage Service
 * 上下文存储服务 - SE 核心服务
 */
public interface ContextStorageService {

    // ========== 用户上下文 ==========
    
    /**
     * 保存用户上下文
     * @param userId 用户ID
     * @param context 上下文数据
     */
    void saveUserContext(String userId, Map<String, Object> context);
    
    /**
     * 加载用户上下文
     * @param userId 用户ID
     * @return 上下文数据，不存在返回空Map
     */
    Map<String, Object> loadUserContext(String userId);
    
    // ========== 会话上下文 ==========
    
    /**
     * 保存会话上下文
     * @param sessionId 会话ID
     * @param context 上下文数据
     */
    void saveSessionContext(String sessionId, Map<String, Object> context);
    
    /**
     * 加载会话上下文
     * @param sessionId 会话ID
     * @return 上下文数据
     */
    Map<String, Object> loadSessionContext(String sessionId);
    
    /**
     * 检查会话是否存在
     */
    boolean sessionExists(String sessionId);
    
    /**
     * 删除会话
     */
    void deleteSession(String sessionId);
    
    // ========== Skill 上下文 ==========
    
    /**
     * 保存 Skill 上下文
     * @param skillId Skill ID
     * @param sessionId 会话ID
     * @param context 上下文数据
     */
    void saveSkillContext(String skillId, String sessionId, Map<String, Object> context);
    
    /**
     * 加载 Skill 上下文
     * @param skillId Skill ID
     * @param sessionId 会话ID
     * @return 上下文数据
     */
    Map<String, Object> loadSkillContext(String skillId, String sessionId);
    
    // ========== 对话历史 ==========
    
    /**
     * 保存对话消息
     * @param sessionId 会话ID
     * @param message 消息
     */
    void saveChatMessage(String sessionId, Map<String, Object> message);
    
    /**
     * 加载对话历史
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 消息列表
     */
    List<Map<String, Object>> loadChatHistory(String sessionId, int limit);
    
    // ========== 页面状态 ==========
    
    /**
     * 保存页面状态
     * @param sessionId 会话ID
     * @param pageId 页面ID
     * @param state 状态数据
     */
    void savePageState(String sessionId, String pageId, Map<String, Object> state);
    
    /**
     * 加载页面状态
     */
    Map<String, Object> loadPageState(String sessionId, String pageId);
}
```

#### 2.1.2 存储结构

```
data/
├── users/
│   └── {userId}.json           # 用户配置和偏好
│
├── sessions/
│   └── {sessionId}/
│       ├── context.json        # 会话上下文
│       ├── chat-history.json   # 对话历史
│       └── pages/
│           └── {pageId}.json   # 页面状态
│
└── skills/
    └── {skillId}/
        └── {sessionId}/
            └── context.json    # Skill 特定上下文
```

#### 2.1.3 数据格式

**用户上下文** (`users/{userId}.json`):
```json
{
    "userId": "user-001",
    "userName": "张三",
    "department": "研发部",
    "roles": ["user", "admin"],
    "permissions": ["chat", "knowledge", "scene"],
    "preferences": {
        "theme": "dark",
        "language": "zh-CN"
    },
    "llmConfig": {
        "provider": "qianwen",
        "model": "qwen-plus"
    },
    "createTime": 1709251200000,
    "updateTime": 1709337600000
}
```

**会话上下文** (`sessions/{sessionId}/context.json`):
```json
{
    "sessionId": "session-001",
    "userId": "user-001",
    "currentSkill": "skill-capability",
    "currentPage": "capability-discovery",
    "messageCount": 15,
    "createdAt": 1709251200000,
    "lastActiveAt": 1709337600000,
    "metadata": {
        "userAgent": "Chrome/120.0",
        "ip": "192.168.1.100"
    }
}
```

**Skill 上下文** (`skills/{skillId}/{sessionId}/context.json`):
```json
{
    "skillId": "skill-capability",
    "sessionId": "session-001",
    "skillName": "能力管理",
    "availableApis": [
        {"name": "discoverCapabilities", "description": "发现新能力"},
        {"name": "installCapability", "description": "安装能力"},
        {"name": "bindCapability", "description": "绑定能力"}
    ],
    "pageState": {
        "currentTab": "discovery",
        "searchKeyword": "",
        "selectedItems": []
    },
    "createdAt": 1709251200000,
    "updatedAt": 1709337600000
}
```

### 2.2 Skills 切换处理

#### 2.2.1 切换接口

```java
/**
 * Skill Switch Handler
 * Skill 切换处理器
 */
public interface SkillSwitchHandler {
    
    /**
     * 切换前处理
     * - 保存当前 Skill 的页面状态
     * - 保存当前 Skill 的上下文
     * - 更新会话的 currentSkill
     */
    void beforeSwitch(String fromSkillId, String toSkillId, String sessionId);
    
    /**
     * 切换后处理
     * - 恢复目标 Skill 的上下文
     * - 恢复目标 Skill 的页面状态
     * - 更新菜单高亮
     */
    void afterSwitch(String fromSkillId, String toSkillId, String sessionId);
    
    /**
     * 获取全局共享上下文
     * - 用户信息
     * - 系统配置
     * - 已安装 Skills 列表
     */
    GlobalContext getGlobalContext(String userId);
}
```

#### 2.2.2 切换流程

```
用户点击菜单切换 Skill
        │
        ▼
┌───────────────────────────┐
│  1. beforeSwitch()        │
│  - 保存当前页面状态        │
│  - 保存当前 Skill 上下文   │
│  - 更新会话 currentSkill   │
└───────────────────────────┘
        │
        ▼
┌───────────────────────────┐
│  2. 页面导航               │
│  - 加载目标 Skill 页面     │
└───────────────────────────┘
        │
        ▼
┌───────────────────────────┐
│  3. afterSwitch()         │
│  - 恢复目标 Skill 上下文   │
│  - 恢复页面状态            │
│  - 更新菜单高亮            │
└───────────────────────────┘
        │
        ▼
    切换完成
```

### 2.3 知识库存储

#### 2.3.1 接口定义

```java
/**
 * Knowledge Storage Service
 * 知识库存储服务
 */
public interface KnowledgeStorageService {
    
    /**
     * 保存知识库文档
     */
    void saveDocument(String kbId, String docId, Map<String, Object> document);
    
    /**
     * 加载所有文档
     */
    List<Map<String, Object>> loadAllDocuments(String kbId);
    
    /**
     * 删除文档
     */
    void deleteDocument(String kbId, String docId);
    
    /**
     * 保存向量索引
     */
    void saveVectorIndex(String kbId, byte[] indexData);
    
    /**
     * 加载向量索引
     */
    byte[] loadVectorIndex(String kbId);
}
```

#### 2.3.2 存储结构

```
data/
└── knowledge/
    └── {kbId}/
        ├── documents/
        │   ├── {docId}.json    # 文档元数据
        │   └── {docId}.txt     # 文档内容
        └── index/
            └── vectors.db      # SQLite-Vec 向量索引
```

---

## 3. LLM Team 需求

### 3.1 真实 API 调用

#### 3.1.1 Provider 接口完善

```java
/**
 * LLM Provider Interface
 * LLM 提供者接口
 */
public interface LlmProvider {
    
    /**
     * 获取提供者类型
     */
    String getProviderType();
    
    /**
     * 获取支持的模型列表
     */
    List<String> getSupportedModels();
    
    /**
     * 对话补全
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 流式对话 (SSE)
     */
    void chatStream(ChatRequest request, StreamHandler handler);
    
    /**
     * 文本嵌入
     */
    List<float[]> embed(String model, List<String> texts);
    
    /**
     * 健康检查
     */
    boolean healthCheck();
}
```

#### 3.1.2 请求/响应模型

```java
public class ChatRequest {
    private String model;
    private List<Message> messages;
    private Map<String, Object> options;  // temperature, maxTokens, etc.
    private String systemPrompt;          // 系统提示
    private List<Tool> tools;             // Function Calling
}

public class ChatResponse {
    private String content;
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;
    private String finishReason;
    private List<ToolCall> toolCalls;
}

public class Message {
    private String role;      // system, user, assistant
    private String content;
    private String name;
    private List<ToolCall> toolCalls;
}
```

#### 3.1.3 配置管理

```java
/**
 * LLM Config Service
 * LLM 配置服务 - 多级配置
 */
public interface LlmConfigService {
    
    /**
     * 解析有效配置
     * 按优先级: SCENE_STEP > SCENE > SCENE_GROUP > PERSONAL > ENTERPRISE > SYSTEM
     */
    ResolvedConfig resolveConfig(ConfigResolveRequest request);
    
    /**
     * 保存企业级配置
     */
    void saveEnterpriseConfig(LlmConfig config);
    
    /**
     * 保存个人配置
     */
    void savePersonalConfig(String userId, LlmConfig config);
    
    /**
     * 保存场景配置
     */
    void saveSceneConfig(String sceneId, LlmConfig config);
}
```

### 3.2 上下文构建器

#### 3.2.1 接口定义

```java
/**
 * Context Builder Service
 * 上下文构建服务
 */
public interface ContextBuilderService {
    
    /**
     * 构建完整上下文
     * @param request 构建请求
     * @return 合并后的上下文
     */
    MergedContext buildContext(ContextBuildRequest request);
    
    /**
     * 格式化为系统提示
     */
    String formatSystemPrompt(MergedContext context);
}

public class ContextBuildRequest {
    private String userId;
    private String sessionId;
    private String skillId;
    private String sceneId;
    private List<ContextSource> sources;  // PAGE, SKILL, SCENE, USER, KNOWLEDGE
    private int maxTokens;
}
```

#### 3.2.2 上下文合并规则

```java
public class ContextMerger {
    
    private static final int MAX_TOKENS = 4096;
    
    public MergedContext merge(List<ContextData> contexts) {
        // 1. 按优先级排序
        contexts.sort(Comparator.comparingInt(c -> c.getSource().getPriority()));
        
        // 2. 合并数据
        Map<String, Object> mergedData = new LinkedHashMap<>();
        for (ContextData context : contexts) {
            mergeData(mergedData, context.getData());
        }
        
        // 3. Token 限制裁剪
        String prompt = formatPrompt(mergedData);
        if (countTokens(prompt) > MAX_TOKENS) {
            prompt = truncatePrompt(prompt, MAX_TOKENS);
        }
        
        return new MergedContext(mergedData, prompt);
    }
}
```

---

## 4. Skills Team 需求

### 4.1 skill-llm-chat 改造

#### 4.1.1 使用 SE 存储服务

```java
@RestController
public class ChatController {
    
    @Autowired
    private ContextStorageService storageService;  // SE 提供的服务
    
    @Autowired
    private KnowledgeStorageService knowledgeService;
    
    @PostMapping("/sessions/{sessionId}/messages")
    public ResultModel<ChatMessage> sendMessage(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> request) {
        
        String content = (String) request.get("content");
        Boolean useKnowledge = (Boolean) request.get("useKnowledge");
        
        // 1. 加载会话上下文
        Map<String, Object> sessionContext = storageService.loadSessionContext(sessionId);
        
        // 2. 知识库检索
        String knowledgeContext = "";
        if (Boolean.TRUE.equals(useKnowledge)) {
            List<String> results = knowledgeService.search(content, 5);
            knowledgeContext = String.join("\n", results);
        }
        
        // 3. 构建系统提示
        String systemPrompt = contextBuilder.formatSystemPrompt(
            sessionContext, knowledgeContext
        );
        
        // 4. 调用 LLM
        ChatResponse response = llmService.chat(systemPrompt, content);
        
        // 5. 持久化消息
        storageService.saveChatMessage(sessionId, Map.of(
            "role", "user",
            "content", content,
            "timestamp", System.currentTimeMillis()
        ));
        storageService.saveChatMessage(sessionId, Map.of(
            "role", "assistant",
            "content", response.getContent(),
            "timestamp", System.currentTimeMillis()
        ));
        
        return ResultModel.success(response);
    }
}
```

#### 4.1.2 前端上下文收集

```javascript
// 页面上下文收集器
class PageContextCollector {
    
    collect() {
        return {
            pageType: this.getPageType(),
            pageUrl: window.location.href,
            pageTitle: document.title,
            pageData: this.getPageData(),
            selectedItems: this.getSelectedItems(),
            formData: this.getFormData(),
            timestamp: Date.now()
        };
    }
    
    getPageType() {
        const match = window.location.pathname.match(/\/skills\/([^/]+)\/pages\/([^/]+)\.html/);
        return match ? { skillId: match[1], pageId: match[2] } : null;
    }
}

// 切换 Skill 时保存状态
async function switchSkill(toSkillId) {
    const currentContext = pageContextCollector.collect();
    
    // 保存当前状态
    await fetch('/api/v1/context/page-state', {
        method: 'POST',
        body: JSON.stringify({
            sessionId: currentSessionId,
            pageId: currentContext.pageType.pageId,
            state: currentContext
        })
    });
    
    // 导航到新 Skill
    window.location.href = `/console/skills/${toSkillId}/pages/index.html`;
}
```

### 4.2 知识库 RAG 集成

#### 4.2.1 RAG 服务接口

```java
/**
 * RAG Service
 * 检索增强生成服务
 */
public interface RagService {
    
    /**
     * RAG 查询
     * @param query 用户问题
     * @param kbIds 知识库ID列表
     * @param options 检索选项
     * @return 增强后的回答
     */
    RagResponse query(String query, List<String> kbIds, RagOptions options);
    
    /**
     * 流式 RAG 查询
     */
    void queryStream(String query, List<String> kbIds, RagOptions options, StreamHandler handler);
}

public class RagOptions {
    private int topK = 5;
    private float scoreThreshold = 0.7f;
    private boolean includeSources = true;
    private boolean rerank = false;
}
```

#### 4.2.2 RAG 流程

```
用户问题
    │
    ▼
┌───────────────────────────┐
│  1. 问题理解              │
│  - 意图分类               │
│  - 实体提取               │
└───────────────────────────┘
    │
    ▼
┌───────────────────────────┐
│  2. 知识检索              │
│  - 向量检索               │
│  - 关键词检索             │
│  - 混合检索               │
└───────────────────────────┘
    │
    ▼
┌───────────────────────────┐
│  3. 上下文构建            │
│  - 合并检索结果           │
│  - Token 限制裁剪         │
└───────────────────────────┘
    │
    ▼
┌───────────────────────────┐
│  4. LLM 生成              │
│  - 系统提示 + 知识上下文  │
│  - 用户问题               │
└───────────────────────────┘
    │
    ▼
  回答 + 来源
```

---

## 5. 实施计划

### 5.1 阶段一：SE 基础服务 (P0)

| 任务 | 负责团队 | 预计工时 | 依赖 |
|------|----------|----------|------|
| ContextStorageService 接口定义 | SE Team | 0.5天 | - |
| JsonStorageService 实现 | SE Team | 1天 | 接口定义 |
| SkillSwitchHandler 实现 | SE Team | 1天 | JsonStorageService |
| KnowledgeStorageService 实现 | SE Team | 1天 | JsonStorageService |

### 5.2 阶段二：LLM 真实调用 (P0)

| 任务 | 负责团队 | 预计工时 | 依赖 |
|------|----------|----------|------|
| LlmProvider 接口完善 | LLM Team | 0.5天 | - |
| OpenAI Provider 真实实现 | LLM Team | 1天 | 接口完善 |
| 通义千问 Provider 真实实现 | LLM Team | 1天 | 接口完善 |
| LlmConfigService 实现 | LLM Team | 1天 | SE 存储 |

### 5.3 阶段三：上下文构建 (P1)

| 任务 | 负责团队 | 预计工时 | 依赖 |
|------|----------|----------|------|
| ContextBuilderService 实现 | LLM Team | 2天 | SE 存储 |
| 前端 PageContextCollector | Skills Team | 1天 | - |
| 切换流程集成 | Skills Team | 1天 | SE 切换接口 |

### 5.4 阶段四：RAG 集成 (P2)

| 任务 | 负责团队 | 预计工时 | 依赖 |
|------|----------|----------|------|
| SQLite-Vec 集成 | Skills Team | 2天 | SE 存储 |
| RagService 实现 | Skills Team | 2天 | SQLite-Vec |
| 对话 RAG 触发 | Skills Team | 1天 | RagService |

---

## 6. 验收标准

### 6.1 SE Team

- [ ] ContextStorageService 所有方法可用
- [ ] 数据正确持久化到 `data/` 目录
- [ ] Skills 切换时上下文正确保存/恢复
- [ ] 知识库文档正确存储和检索

### 6.2 LLM Team

- [ ] OpenAI Provider 真实 API 调用成功
- [ ] 通义千问 Provider 真实 API 调用成功
- [ ] 流式输出正常工作
- [ ] 多级配置正确解析

### 6.3 Skills Team

- [ ] 对话历史正确持久化
- [ ] 知识库 RAG 自动触发
- [ ] Skills 切换无状态丢失
- [ ] AI 助手上下文正确构建

---

## 7. 风险与依赖

### 7.1 详细风险分析与缓解方案

#### 7.1.1 循环依赖风险

**问题**: `ContextBuilderService` 需要访问存储数据，导致 LLM Team 与 SE Team 循环依赖

**解决方案**: 参数传递方式

```java
// 错误方式：ContextBuilderService 直接依赖 StorageService
public class ContextBuilderServiceImpl implements ContextBuilderService {
    @Autowired
    private ContextStorageService storageService;  // ❌ 循环依赖
}

// 正确方式：通过参数传递数据
public class ContextBuilderServiceImpl implements ContextBuilderService {
    
    // ✅ 不依赖 StorageService，接收已加载的数据
    public MergedContext buildContext(ContextBuildRequest request) {
        // request 中已包含所需数据，由调用方加载
        return mergeContexts(
            request.getUserContext(),
            request.getSessionContext(),
            request.getSkillContext(),
            request.getKnowledgeContext()
        );
    }
}

// 调用方负责加载数据
public class ChatController {
    @Autowired private ContextStorageService storageService;
    @Autowired private ContextBuilderService contextBuilder;
    
    public ChatResponse sendMessage(String sessionId, String content) {
        // SE Team 负责加载
        Map<String, Object> userContext = storageService.loadUserContext(userId);
        Map<String, Object> sessionContext = storageService.loadSessionContext(sessionId);
        Map<String, Object> skillContext = storageService.loadSkillContext(skillId, sessionId);
        
        // 传递给 LLM Team
        ContextBuildRequest request = new ContextBuildRequest()
            .setUserContext(userContext)
            .setSessionContext(sessionContext)
            .setSkillContext(skillContext);
            
        MergedContext context = contextBuilder.buildContext(request);
        // ...
    }
}
```

**依赖关系**:
```
Skills Team (ChatController)
    ├── 依赖 SE Team (ContextStorageService) - 加载数据
    └── 依赖 LLM Team (ContextBuilderService) - 构建上下文

SE Team 和 LLM Team 之间无直接依赖 ✅
```

---

#### 7.1.2 Token 限制冲突

**问题**: 4096 限制与完整上下文需求矛盾

**解决方案**: SmartContextTruncator 智能裁剪

```java
/**
 * Smart Context Truncator
 * 智能上下文裁剪器 - 按优先级保留
 */
public class SmartContextTruncator {
    
    // 上下文优先级（从高到低）
    private static final List<String> PRIORITY_ORDER = Arrays.asList(
        "system",       // 系统信息 - 最高优先级
        "user",         // 用户身份
        "scene",        // 场景上下文
        "skill",        // Skill 信息
        "knowledge",    // 知识库检索结果
        "history",      // 对话历史
        "page"          // 页面状态 - 最低优先级
    );
    
    private static final int MAX_TOKENS = 4096;
    private static final int RESERVE_TOKENS = 512;  // 为用户输入和响应预留
    
    /**
     * 智能裁剪
     * @param contexts 各类上下文
     * @return 裁剪后的合并上下文
     */
    public String truncate(Map<String, String> contexts) {
        StringBuilder result = new StringBuilder();
        int usedTokens = 0;
        int availableTokens = MAX_TOKENS - RESERVE_TOKENS;
        
        // 按优先级顺序处理
        for (String contextType : PRIORITY_ORDER) {
            String content = contexts.get(contextType);
            if (content == null || content.isEmpty()) continue;
            
            int contentTokens = countTokens(content);
            
            if (usedTokens + contentTokens <= availableTokens) {
                // 完整保留
                result.append(formatSection(contextType, content));
                usedTokens += contentTokens;
            } else {
                // 需要裁剪
                int remainingTokens = availableTokens - usedTokens;
                if (remainingTokens > 100) {  // 至少保留 100 tokens
                    String truncated = smartTruncate(content, remainingTokens);
                    result.append(formatSection(contextType, truncated));
                    result.append("\n[...已裁剪...]\n");
                }
                break;  // 后续低优先级上下文不再处理
            }
        }
        
        return result.toString();
    }
    
    /**
     * 智能裁剪单个上下文
     * 保留开头和结尾，中间省略
     */
    private String smartTruncate(String content, int maxTokens) {
        int totalTokens = countTokens(content);
        if (totalTokens <= maxTokens) return content;
        
        // 保留 40% 开头 + 40% 结尾
        int headTokens = (int) (maxTokens * 0.4);
        int tailTokens = (int) (maxTokens * 0.4);
        
        String head = getFirstTokens(content, headTokens);
        String tail = getLastTokens(content, tailTokens);
        
        return head + "\n...[中间内容已省略]...\n" + tail;
    }
}
```

**裁剪策略**:

| 上下文类型 | 优先级 | 裁剪策略 |
|------------|--------|----------|
| system | 1 (最高) | 不裁剪，必须保留 |
| user | 2 | 不裁剪 |
| scene | 3 | 保留关键信息 |
| skill | 4 | 保留 API 列表摘要 |
| knowledge | 5 | 按相关性分数保留 Top-K |
| history | 6 | 保留最近 N 轮对话 |
| page | 7 (最低) | 可完全裁剪 |

---

#### 7.1.3 状态同步时机

**问题**: 异步保存与同步导航的竞态条件

**解决方案**: 后端同步接口 + 前端等待确认后跳转

```java
/**
 * Skill Switch Service
 * Skill 切换同步服务
 */
@RestController
@RequestMapping("/api/v1/switch")
public class SkillSwitchController {
    
    @Autowired
    private ContextStorageService storageService;
    
    /**
     * 同步切换接口
     * 确保状态保存完成后再返回
     */
    @PostMapping("/prepare")
    public ResultModel<SwitchPrepareResult> prepareSwitch(
            @RequestBody SwitchRequest request) {
        
        String fromSkillId = request.getFromSkillId();
        String toSkillId = request.getToSkillId();
        String sessionId = request.getSessionId();
        
        // 1. 同步保存当前 Skill 状态
        Map<String, Object> currentState = request.getCurrentState();
        storageService.saveSkillContext(fromSkillId, sessionId, currentState);
        
        // 2. 同步保存页面状态
        storageService.savePageState(sessionId, request.getPageId(), currentState);
        
        // 3. 更新会话当前 Skill
        Map<String, Object> sessionContext = storageService.loadSessionContext(sessionId);
        sessionContext.put("currentSkill", toSkillId);
        sessionContext.put("lastActiveAt", System.currentTimeMillis());
        storageService.saveSessionContext(sessionId, sessionContext);
        
        // 4. 预加载目标 Skill 上下文
        Map<String, Object> targetContext = storageService.loadSkillContext(toSkillId, sessionId);
        
        // 5. 返回切换准备结果
        return ResultModel.success(new SwitchPrepareResult()
            .setReady(true)
            .setTargetContext(targetContext)
            .setTargetUrl(buildTargetUrl(toSkillId))
        );
    }
}
```

**前端切换流程**:

```javascript
class SkillSwitcher {
    
    async switchTo(toSkillId) {
        // 1. 收集当前状态
        const currentState = pageContextCollector.collect();
        
        // 2. 调用同步接口（等待完成）
        const result = await fetch('/api/v1/switch/prepare', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                fromSkillId: currentSkillId,
                toSkillId: toSkillId,
                sessionId: currentSessionId,
                pageId: currentPageId,
                currentState: currentState
            })
        });
        
        const data = await result.json();
        
        // 3. 确认保存成功后再跳转
        if (data.data.ready) {
            // 4. 跳转到目标 Skill
            window.location.href = data.data.targetUrl;
        } else {
            // 处理失败情况
            console.error('Switch prepare failed');
        }
    }
}
```

**时序图**:
```
前端                    后端                    存储
 │                        │                        │
 │  1. collect state      │                        │
 │───────────────────────>│                        │
 │                        │                        │
 │  2. POST /switch/prepare                       │
 │───────────────────────>│                        │
 │                        │  3. saveSkillContext   │
 │                        │───────────────────────>│
 │                        │  4. savePageState      │
 │                        │───────────────────────>│
 │                        │  5. updateSession      │
 │                        │───────────────────────>│
 │                        │  6. loadTargetContext  │
 │                        │<───────────────────────│
 │  7. return ready=true  │                        │
 │<───────────────────────│                        │
 │                        │                        │
 │  8. navigate           │                        │
 │─────────────────────────────────────────────────>
```

---

#### 7.1.4 RAG 触发规则

**问题**: "自动触发"缺乏具体条件

**解决方案**: RagTrigger 智能判断

```java
/**
 * Rag Trigger Service
 * RAG 触发判断服务
 */
public class RagTriggerService {
    
    // 知识库相关关键词
    private static final Set<String> KNOWLEDGE_KEYWORDS = Set.of(
        "文档", "资料", "手册", "指南", "教程", "说明",
        "如何", "怎么", "什么是", "配置", "设置",
        "ooder", "skill", "scene", "能力", "场景"
    );
    
    // 问题型关键词
    private static final Set<String> QUESTION_PATTERNS = Set.of(
        "什么是", "如何", "怎么", "为什么", "哪些",
        "能不能", "可以吗", "有没有", "帮我", "请"
    );
    
    /**
     * 判断是否触发 RAG
     * @param query 用户输入
     * @param context 上下文
     * @return 是否触发
     */
    public boolean shouldTriggerRag(String query, RagTriggerContext context) {
        int score = 0;
        
        // 1. 关键词匹配 (+30分)
        for (String keyword : KNOWLEDGE_KEYWORDS) {
            if (query.toLowerCase().contains(keyword.toLowerCase())) {
                score += 10;
            }
        }
        
        // 2. 问题型检测 (+20分)
        for (String pattern : QUESTION_PATTERNS) {
            if (query.contains(pattern)) {
                score += 20;
                break;
            }
        }
        
        // 3. 历史触发记录 (+15分)
        if (context.getRecentRagRate() > 0.5) {
            score += 15;  // 最近频繁使用 RAG
        }
        
        // 4. 用户显式标志 (+50分)
        if (context.isUserRagEnabled()) {
            score += 50;
        }
        
        // 5. 知识库内容相关性 (+25分)
        if (context.hasRelevantKnowledge(query)) {
            score += 25;
        }
        
        // 6. 长问题 (+10分)
        if (query.length() > 20) {
            score += 10;
        }
        
        // 阈值判断
        return score >= 40;
    }
    
    /**
     * 获取触发原因
     */
    public String getTriggerReason(String query, RagTriggerContext context) {
        List<String> reasons = new ArrayList<>();
        
        if (containsAnyKeyword(query)) {
            reasons.add("包含知识库关键词");
        }
        if (isQuestionType(query)) {
            reasons.add("问题型查询");
        }
        if (context.isUserRagEnabled()) {
            reasons.add("用户开启知识库");
        }
        if (context.hasRelevantKnowledge(query)) {
            reasons.add("存在相关知识");
        }
        
        return String.join(" + ", reasons);
    }
}
```

**触发规则表**:

| 条件 | 分值 | 说明 |
|------|------|------|
| 关键词匹配 | +10/词 | 最多 +30 |
| 问题型检测 | +20 | 包含疑问词 |
| 历史触发率 > 50% | +15 | 用户习惯 |
| 用户显式开启 | +50 | 界面开关 |
| 知识库相关 | +25 | 快速预检 |
| 长问题 > 20字 | +10 | 复杂问题 |
| **触发阈值** | **≥40** | - |

---

#### 7.1.5 并发文件锁

**问题**: 文件系统不支持并发写入

**解决方案**: ReentrantReadWriteLock 会话级锁 + 定时清理

```java
/**
 * File Lock Manager
 * 文件锁管理器
 */
@Component
public class FileLockManager {
    
    // 会话级读写锁
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> sessionLocks = 
        new ConcurrentHashMap<>();
    
    // 锁超时时间
    private static final long LOCK_TIMEOUT_MS = 5000;
    
    // 锁清理间隔
    private static final long CLEANUP_INTERVAL_MS = 60000;
    
    // 锁最后使用时间
    private final ConcurrentHashMap<String, Long> lockLastUsed = 
        new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 启动定时清理线程
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
            this::cleanupIdleLocks,
            CLEANUP_INTERVAL_MS,
            CLEANUP_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );
    }
    
    /**
     * 获取读锁
     */
    public <T> T withReadLock(String sessionId, Supplier<T> action) {
        ReentrantReadWriteLock lock = getLock(sessionId);
        
        try {
            if (lock.readLock().tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                try {
                    lockLastUsed.put(sessionId, System.currentTimeMillis());
                    return action.get();
                } finally {
                    lock.readLock().unlock();
                }
            } else {
                throw new LockTimeoutException("Read lock timeout for session: " + sessionId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException("Read lock interrupted", e);
        }
    }
    
    /**
     * 获取写锁
     */
    public <T> T withWriteLock(String sessionId, Supplier<T> action) {
        ReentrantReadWriteLock lock = getLock(sessionId);
        
        try {
            if (lock.writeLock().tryLock(LOCK_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                try {
                    lockLastUsed.put(sessionId, System.currentTimeMillis());
                    return action.get();
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                throw new LockTimeoutException("Write lock timeout for session: " + sessionId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockInterruptedException("Write lock interrupted", e);
        }
    }
    
    /**
     * 获取或创建锁
     */
    private ReentrantReadWriteLock getLock(String sessionId) {
        return sessionLocks.computeIfAbsent(sessionId, k -> new ReentrantReadWriteLock());
    }
    
    /**
     * 清理空闲锁
     */
    private void cleanupIdleLocks() {
        long now = System.currentTimeMillis();
        long idleThreshold = CLEANUP_INTERVAL_MS * 5;  // 5分钟未使用
        
        lockLastUsed.forEach((sessionId, lastUsed) -> {
            if (now - lastUsed > idleThreshold) {
                ReentrantReadWriteLock lock = sessionLocks.get(sessionId);
                if (lock != null && !lock.isWriteLocked() && lock.getReadLockCount() == 0) {
                    sessionLocks.remove(sessionId);
                    lockLastUsed.remove(sessionId);
                    log.debug("Cleaned up idle lock for session: {}", sessionId);
                }
            }
        });
    }
}
```

**使用示例**:

```java
@Service
public class ContextStorageServiceImpl implements ContextStorageService {
    
    @Autowired
    private FileLockManager lockManager;
    
    @Override
    public void saveSessionContext(String sessionId, Map<String, Object> context) {
        lockManager.withWriteLock(sessionId, () -> {
            String filePath = getFilePath("sessions", sessionId, "context.json");
            writeJson(filePath, context);
            return null;
        });
    }
    
    @Override
    public Map<String, Object> loadSessionContext(String sessionId) {
        return lockManager.withReadLock(sessionId, () -> {
            String filePath = getFilePath("sessions", sessionId, "context.json");
            return readJson(filePath);
        });
    }
}
```

---

#### 7.1.6 配置优先级冲突

**问题**: 多级配置与 Skill 配置冲突

**解决方案**: 增加 SKILL 级别，明确优先级顺序

```java
/**
 * Config Level
 * 配置层级枚举
 */
public enum ConfigLevel {
    SYSTEM(0, "系统级"),
    ENTERPRISE(20, "企业级"),
    DEPARTMENT(30, "部门级"),
    PERSONAL(40, "个人级"),
    SCENE_GROUP(50, "场景组级"),
    SCENE(60, "场景级"),
    SKILL(70, "Skill级"),        // 新增
    SCENE_STEP(80, "场景步骤级"),
    SESSION(90, "会话级");       // 新增
    
    private final int priority;
    private final String description;
    
    ConfigLevel(int priority, String description) {
        this.priority = priority;
        this.description = description;
    }
    
    public int getPriority() {
        return priority;
    }
}
```

**优先级顺序（从高到低）**:

```
SESSION (90)     - 当前会话临时配置
    ↓
SCENE_STEP (80) - 场景步骤配置
    ↓
SKILL (70)      - Skill 级配置 ← 新增
    ↓
SCENE (60)      - 场景配置
    ↓
SCENE_GROUP (50)- 场景组配置
    ↓
PERSONAL (40)   - 个人配置
    ↓
DEPARTMENT (30) - 部门配置 ← 新增
    ↓
ENTERPRISE (20) - 企业配置
    ↓
SYSTEM (0)      - 系统默认
```

**配置解析服务**:

```java
@Service
public class LlmConfigService {
    
    /**
     * 解析有效配置
     */
    public ResolvedConfig resolveConfig(ConfigResolveRequest request) {
        List<ConfigCandidate> candidates = new ArrayList<>();
        
        // 按优先级从高到低收集
        candidates.add(findConfig(ConfigLevel.SESSION, request.getSessionId()));
        candidates.add(findConfig(ConfigLevel.SCENE_STEP, request.getStepId()));
        candidates.add(findConfig(ConfigLevel.SKILL, request.getSkillId()));      // 新增
        candidates.add(findConfig(ConfigLevel.SCENE, request.getSceneId()));
        candidates.add(findConfig(ConfigLevel.SCENE_GROUP, request.getSceneGroupId()));
        candidates.add(findConfig(ConfigLevel.PERSONAL, request.getUserId()));
        candidates.add(findConfig(ConfigLevel.DEPARTMENT, request.getDepartmentId())); // 新增
        candidates.add(findConfig(ConfigLevel.ENTERPRISE, request.getEnterpriseId()));
        candidates.add(getSystemDefault());
        
        // 找到第一个有效配置
        for (ConfigCandidate candidate : candidates) {
            if (candidate != null && candidate.isActive()) {
                return buildResolvedConfig(candidate);
            }
        }
        
        throw new LlmConfigNotFoundException("No valid LLM config found");
    }
    
    /**
     * 构建解析路径（用于调试）
     */
    private String buildResolutionPath(List<ConfigCandidate> candidates) {
        return candidates.stream()
            .map(c -> c == null ? "null" : 
                 c.isActive() ? c.getLevel() + "(active)" : 
                 c.getLevel() + "(inactive)")
            .collect(Collectors.joining(" -> "));
    }
}
```

**Skill 配置示例**:

```json
// data/skills/skill-llm-chat/config.json
{
    "skillId": "skill-llm-chat",
    "llmConfig": {
        "provider": "qianwen",
        "model": "qwen-plus",
        "options": {
            "temperature": 0.7,
            "maxTokens": 4096
        }
    },
    "knowledgeConfig": {
        "autoTrigger": true,
        "defaultKbIds": ["kb-system-docs"],
        "topK": 5
    }
}
```

---

### 7.2 风险汇总表

| 风险 | 影响 | 解决方案 | 负责团队 |
|------|------|----------|----------|
| 循环依赖 | 高 | 参数传递方式，SE 加载后传递给 LLM | SE + LLM |
| Token 限制冲突 | 中 | SmartContextTruncator 智能裁剪 | LLM Team |
| 状态同步时机 | 高 | 后端同步接口 + 前端等待确认 | SE + Skills |
| RAG 触发规则 | 中 | RagTrigger 智能判断，评分机制 | Skills Team |
| 并发文件锁 | 高 | ReentrantReadWriteLock + 定时清理 | SE Team |
| 配置优先级冲突 | 中 | 增加 SKILL/SESSION/DEPARTMENT 级别 | SE Team |

### 7.3 依赖

| 依赖项 | 提供方 | 状态 |
|--------|--------|------|
| scene-engine 2.3.1 | SE Team | 已发布 |
| LLM SDK 2.3.1 | LLM Team | 已发布 |
| skill-common 2.3.1 | Common Team | 已发布 |

---

## 8. 联系方式

| 团队 | 负责人 | 联系方式 |
|------|--------|----------|
| SE Team | - | - |
| LLM Team | - | - |
| Skills Team | - | - |

---

**文档结束**
