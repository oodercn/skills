# skill-knowledge-qa 依赖配置问题协助文档

## 概述

本文档描述了 `skill-knowledge-qa` 场景能力包在依赖管理、LLM集成和场景能力初始化方面的问题，需要Engine团队协助解决。

---

## 1. 依赖配置现状

### 1.1 skill.yaml 依赖声明

```yaml
spec:
  type: scene-skill
  
  dependencies:
    - id: skill-knowledge-base
      version: ">=1.0.0"
      required: true
      description: "知识库核心服务"
      capabilities:
        - kb-management
        - document-management
        - kb-search
        
    - id: skill-rag
      version: ">=1.0.0"
      required: false
      description: "RAG检索增强（可选）"
      
    - id: skill-llm-assistant
      version: ">=1.0.0"
      required: false
      description: "LLM智能助手（可选）"
      
    - id: skill-indexing
      version: ">=1.0.0"
      required: true
      description: "文档索引服务"
```

### 1.2 本地skills目录现状

```
skills/
├── skill-knowledge-qa/                    # 主skill ✅
├── skill-nexus-dashboard-nexus-ui/        # UI skill
├── skill-nexus-health-check-nexus-ui/     # UI skill
├── skill-nexus-system-status-nexus-ui/    # UI skill
├── skill-personal-dashboard-nexus-ui/     # UI skill
└── skill-storage-management-nexus-ui/     # UI skill
```

### 1.3 问题：依赖skill不存在

| 依赖ID | 是否必需 | 本地状态 | 问题 |
|--------|----------|----------|------|
| skill-knowledge-base | **required** | ❌ 不存在 | 核心功能缺失 |
| skill-indexing | **required** | ❌ 不存在 | 索引功能缺失 |
| skill-rag | optional | ❌ 不存在 | RAG功能不可用 |
| skill-llm-assistant | optional | ❌ 不存在 | LLM问答不可用 |

---

## 2. 场景能力配置问题

### 2.1 sceneCapabilities 配置

```yaml
sceneCapabilities:
  - id: scene-knowledge-qa
    name: 知识问答场景能力
    type: SCENE
    mainFirst: true
    
    mainFirstConfig:
      selfCheck:
        - checkCapabilities: [kb-management, document-management, kb-search]
        - checkDriverCapabilities: [intent-receiver, event-listener]
        - checkCollaborative: [scene-indexing]        # ⚠️ scene-indexing 不存在
        
      selfStart:
        - initDriverCapabilities: [intent-receiver, event-listener, capability-invoker]
        - initCapabilities: [kb-management, document-management, kb-search]
        - bindAddresses: auto
        
      startCollaboration:
        - startScene: scene-indexing                   # ⚠️ scene-indexing 不存在
        - bindInterface: indexing-service
```

### 2.2 collaborativeCapabilities 配置

```yaml
collaborativeCapabilities:
  - capabilityId: scene-indexing
    role: PROVIDER
    interface: indexing-service
    autoStart: true
    
  - capabilityId: scene-llm-assistant
    role: PROVIDER
    interface: llm-service
    autoStart: false
```

### 2.3 问题：场景能力检查失败

| 检查项 | 状态 | 说明 |
|--------|------|------|
| checkCapabilities | ⚠️ | 能力由主skill提供，但依赖skill不存在 |
| checkDriverCapabilities | ⚠️ | driver能力定义在主skill中 |
| checkCollaborative | ❌ | `scene-indexing` 场景不存在 |

---

## 3. LLM集成问题

### 3.1 当前LLM配置方式

```yaml
# application.yml
ooder:
  llm:
    provider: openai
    api-key: ${OODER_LLM_API_KEY:}
    base-url: ${OODER_LLM_BASE_URL:https://api.openai.com/v1}
    model: gpt-3.5-turbo
    embedding-model: text-embedding-ada-002
```

### 3.2 问题：LLM配置与skill依赖未关联

- `skill-llm-assistant` 依赖与 `ooder.llm.*` 配置无关联
- 无法通过skill依赖自动配置LLM参数
- LLM就绪状态未纳入场景能力检查

---

## 4. 需要Engine团队协助的问题

### 4.1 依赖自动安装机制

**问题**：Engine是否支持检测并自动安装 `required: true` 的依赖？

**期望行为**：
```yaml
dependencies:
  - id: skill-knowledge-base
    version: ">=1.0.0"
    required: true
    autoInstall: true    # 建议添加：是否自动安装
    installSource: remote # 建议添加：安装来源
```

**需要确认**：
1. 依赖解析的触发时机是什么？（启动时？首次访问时？）
2. 依赖安装失败时的处理策略？
3. 是否支持依赖版本冲突解决？

### 4.2 LLM配置与skill依赖集成

**问题**：如何将 `skill-llm-assistant` 与 `ooder.llm.*` 配置关联？

**建议方案**：
```yaml
dependencies:
  - id: skill-llm-assistant
    version: ">=1.0.0"
    required: false
    configMapping:           # 建议添加：配置映射
      apiKey: "${ooder.llm.api-key}"
      baseUrl: "${ooder.llm.base-url}"
      model: "${ooder.llm.model}"
      embeddingModel: "${ooder.llm.embedding-model}"
```

**需要确认**：
1. LLM配置是否应该通过skill依赖方式管理？
2. LLM就绪检查是否应该在 `selfCheck` 阶段执行？
3. 如何处理LLM配置缺失时的降级策略？

### 4.3 场景能力初始化流程

**问题**：`selfCheck` 失败时的处理策略？

**当前配置**：
```yaml
selfCheck:
  - checkCapabilities: [kb-management, document-management, kb-search]
  - checkDriverCapabilities: [intent-receiver, event-listener]
  - checkCollaborative: [scene-indexing]
```

**需要确认**：
1. `checkCollaborative` 检查失败时，场景是否应该启动？
2. `collaborativeCapabilities.autoStart` 的执行时机？
3. 是否支持部分能力可用时的降级运行？

### 4.4 能力提供者注册

**问题**：当依赖skill不存在时，如何处理能力提供？

**当前状态**：
- `skill-knowledge-base` 不存在，但 `kb-management` 等能力在主skill中定义
- 能力由主skill提供还是依赖skill提供？

**需要确认**：
1. 能力定义在主skill中，实现是否应该在依赖skill中？
2. 当依赖skill不存在时，主skill是否应该提供fallback实现？

---

## 5. 建议的配置增强

### 5.1 依赖配置增强

```yaml
dependencies:
  - id: skill-knowledge-base
    version: ">=1.0.0"
    required: true
    autoInstall: true
    installSource: https://gitee.com/ooderCN/ooder-skills/releases
    fallback: embedded      # 当依赖不可用时的fallback策略
    
  - id: skill-llm-assistant
    version: ">=1.0.0"
    required: false
    autoInstall: false
    configMapping:
      apiKey: "${ooder.llm.api-key}"
      baseUrl: "${ooder.llm.base-url}"
      model: "${ooder.llm.model}"
    healthCheck:
      enabled: true
      endpoint: /api/llm/health
```

### 5.2 场景能力配置增强

```yaml
sceneCapabilities:
  - id: scene-knowledge-qa
    mainFirst: true
    
    mainFirstConfig:
      selfCheck:
        - checkCapabilities: [kb-management, document-management, kb-search]
        - checkDependencies: [skill-knowledge-base, skill-indexing]  # 新增：检查依赖
        - checkConfig: [ooder.llm.api-key]                           # 新增：检查配置
        
      onCheckFailed:
        action: degrade          # degrade | fail | continue
        degradedCapabilities: [rag-retrieval]  # 降级时禁用的能力
        
      selfStart:
        - installDependencies: auto   # 新增：自动安装依赖
        - initDriverCapabilities: [intent-receiver, event-listener, capability-invoker]
        - initCapabilities: [kb-management, document-management, kb-search]
```

---

## 6. 当前临时解决方案

由于依赖管理机制尚未完善，当前采用以下临时方案：

### 6.1 内置实现

在主应用中内置了以下服务实现：

| 服务 | 文件 | 功能 |
|------|------|------|
| DocumentIndexService | DocumentIndexService.java | 文档索引和检索 |
| LLMService | LLMService.java | LLM API调用 |

### 6.2 配置方式

```yaml
ooder:
  kb:
    storage-path: ./kb-storage
    index-path: ./kb-index
  llm:
    provider: openai
    api-key: ${OODER_LLM_API_KEY:}
    base-url: ${OODER_LLM_BASE_URL:https://api.openai.com/v1}
    model: gpt-3.5-turbo
```

### 6.3 API端点

| 端点 | 方法 | 功能 |
|------|------|------|
| /api/kb/list | GET | 列出知识库 |
| /api/kb/create | POST | 创建知识库 |
| /api/kb/upload | POST | 上传文档 |
| /api/kb/search | POST | 搜索文档 |
| /api/kb/qa | POST | 智能问答 |
| /api/kb/llm/status | GET | LLM状态检查 |

---

## 7. 联系方式

如有问题，请联系：

- **项目**: ooder-skills
- **仓库**: https://gitee.com/ooderCN/ooder-skills
- **文档日期**: 2026-03-03

---

## 附录：相关文件路径

| 文件 | 路径 |
|------|------|
| skill.yaml | skills/skill-knowledge-qa/skill.yaml |
| 前端页面 | skills/skill-knowledge-qa/ui/pages/index.html |
| 索引服务 | src/main/java/net/ooder/skill/test/service/DocumentIndexService.java |
| LLM服务 | src/main/java/net/ooder/skill/test/service/LLMService.java |
| 控制器 | src/main/java/net/ooder/skill/test/controller/KnowledgeBaseController.java |
| 配置文件 | src/main/resources/application.yml |
