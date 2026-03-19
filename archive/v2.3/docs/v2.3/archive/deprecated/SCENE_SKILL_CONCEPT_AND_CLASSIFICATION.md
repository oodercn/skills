# Ooder 场景技能概念明确与分类

> **文档版本**: 1.0.0  
> **编写日期**: 2026-03-05  
> **核心思想**: 场景即技能 (Scene = Skill)

---

## 一、场景技能概念明确

### 1.1 什么是场景技能 (Scene-Skill)

场景技能是一种**特殊的 Skill 类型**，它将"场景"的概念与"技能"的技术实现深度融合，形成可发现、可分发、可自驱的智能实体。

```
┌─────────────────────────────────────────────────────────────────┐
│                    场景技能核心定义                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   场景技能 = 场景定义 + 技能实现 + 自驱能力                        │
│                                                                 │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐        │
│   │   场景定义   │ +  │   技能实现   │ +  │   自驱能力   │        │
│   │  (业务语义)  │    │  (技术实现)  │    │  (mainFirst) │        │
│   └─────────────┘    └─────────────┘    └─────────────┘        │
│                                                                 │
│   = 可发现的业务场景能力                                         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 场景技能 vs 普通技能

| 维度 | 普通技能 (Service-Skill) | 场景技能 (Scene-Skill) |
|------|-------------------------|----------------------|
| **类型标识** | `spec.type: service-skill` | `spec.type: scene-skill` |
| **核心能力** | 提供原子服务能力 | 提供完整业务场景能力 |
| **自驱能力** | 无 | 有 (`mainFirst: true`) |
| **发现方式** | 系统内部调用 | 能力发现页面可见 |
| **分发方式** | 依赖注入 | 可推送给组织成员 |
| **业务语义** | 技术能力 | 完整业务场景 |
| **生命周期** | 随系统启动 | 可独立激活/停用 |

### 1.3 场景技能的三大特征

```yaml
# 特征1: 类型标识为 scene-skill
metadata:
  type: scene-skill

# 特征2: 包含 sceneCapabilities 定义
spec:
  sceneCapabilities:
    - id: scene-xxx
      mainFirst: true  # 特征3: 自驱入口
      mainFirstConfig:
        selfDrive:
          scheduleRules: [...]   # 定时驱动
          eventRules: [...]      # 事件驱动
          capabilityChains: [...] # 能力链
```

### 1.4 场景技能检测标准

```java
public boolean isSceneSkill(SkillPackage skill) {
    // 标准1: spec.type = scene-skill
    if (!"scene-skill".equals(skill.getSpec().getType())) {
        return false;
    }
    
    // 标准2: 存在 sceneCapabilities 定义
    if (skill.getSpec().getSceneCapabilities() == null 
        || skill.getSpec().getSceneCapabilities().isEmpty()) {
        return false;
    }
    
    // 标准3: 存在 mainFirst 标识
    for (SceneCapability cap : skill.getSpec().getSceneCapabilities()) {
        if (cap.isMainFirst()) {
            return true;
        }
    }
    
    return false;
}
```

---

## 二、现有 Skills 分类整理

### 2.1 分类体系

```
Skills/
├── 🔵 基础服务技能 (Foundation Skills)
│   ├── skill-scene          # 场景管理核心
│   ├── skill-capability     # 能力管理服务
│   ├── skill-user-auth      # 用户认证服务
│   ├── skill-health         # 健康检查服务
│   └── skill-monitor        # 监控服务
│
├── 🟢 知识库与AI技能 (Knowledge & AI Skills)
│   ├── skill-knowledge-base # 知识库核心服务
│   ├── skill-rag            # RAG检索增强
│   ├── skill-local-knowledge # 本地知识库
│   └── skill-document-processor # 文档处理
│
├── 🟡 LLM相关技能 (LLM Skills)
│   ├── skill-llm-chat           # LLM智能对话 ⭐场景技能
│   ├── skill-llm-conversation   # LLM对话服务
│   ├── skill-llm-context-builder # 上下文构建
│   ├── skill-llm-config-manager  # LLM配置管理
│   ├── skill-llm-assistant-ui    # LLM助手UI
│   └── skill-llm-management-ui   # LLM管理UI
│
├── 🟠 组织与通讯技能 (Org & Communication Skills)
│   ├── skill-org-dingding   # 钉钉集成
│   ├── skill-org-feishu     # 飞书集成
│   ├── skill-org-wecom      # 企业微信集成
│   ├── skill-org-ldap       # LDAP集成
│   └── skill-msg            # 消息服务
│
├── 🔴 存储与文件技能 (Storage Skills)
│   ├── skill-vfs-local      # 本地文件存储
│   ├── skill-vfs-minio      # MinIO存储
│   ├── skill-vfs-oss        # 阿里云OSS
│   ├── skill-vfs-s3         # AWS S3
│   └── skill-vfs-database   # 数据库存储
│
├── 🟣 基础设施技能 (Infrastructure Skills)
│   ├── skill-a2ui           # A2UI渲染引擎
│   ├── skill-protocol       # 协议处理
│   ├── skill-security       # 安全服务
│   ├── skill-collaboration  # 协作服务
│   ├── skill-mqtt           # MQTT服务
│   └── skill-network        # 网络服务
│
├── ⚫ 运维与托管技能 (DevOps Skills)
│   ├── skill-k8s            # Kubernetes管理
│   ├── skill-hosting        # 托管服务
│   ├── skill-openwrt        # OpenWrt管理
│   └── skill-trae-solo      # Trae Solo集成
│
└── ⚪ UI展示技能 (UI Skills)
    ├── skill-knowledge-ui           # 知识库UI
    ├── skill-knowledge-qa           # 知识问答UI ⭐场景技能
    ├── skill-personal-dashboard-nexus-ui # 个人仪表板UI
    ├── skill-nexus-dashboard-nexus-ui    # Nexus仪表板UI
    ├── skill-nexus-health-check-nexus-ui # 健康检查UI
    ├── skill-nexus-system-status-nexus-ui # 系统状态UI
    └── skill-storage-management-nexus-ui  # 存储管理UI
```

---

### 2.2 场景技能清单 (Scene-Skills)

符合 `scene-skill` 类型定义的现有技能：

| 技能ID | 技能名称 | 场景能力ID | 驱动方式 | 说明 |
|--------|---------|-----------|---------|------|
| **skill-llm-chat** | LLM智能对话 | scene-llm-chat | intent-receiver + event-listener | 多轮对话场景 |
| **skill-knowledge-qa** | 知识问答 | scene-knowledge-qa | intent-receiver | 知识库问答场景 |

#### skill-llm-chat 场景技能详情

```yaml
# 类型: scene-skill
# 场景能力: scene-llm-chat
# 自驱机制: mainFirst

spec:
  type: scene-skill
  
  sceneCapabilities:
    - id: scene-llm-chat
      name: LLM智能对话场景能力
      mainFirst: true
      
      mainFirstConfig:
        selfCheck:
          - checkCapabilities: [llm-chat, session-manage, context-extraction]
          - checkDriverCapabilities: [intent-receiver]
          
        selfStart:
          - initDriverCapabilities: [intent-receiver]
          - initCapabilities: [llm-chat, session-manage, context-extraction]
          
        selfDrive:
          eventRules:
            - event: user.message
              action: chat-flow
          capabilityChains:
            chat-flow:
              - capability: context-extraction
              - capability: llm-chat
```

**依赖技能**:
- skill-llm-conversation (必需)
- skill-llm-context-builder (必需)
- skill-llm-config-manager (可选)

**提供能力**:
- llm-chat: 智能对话
- session-manage: 会话管理
- history-record: 历史记录
- context-extraction: 上下文提取

---

### 2.3 基础服务技能清单 (Foundation Skills)

支撑系统运行的核心服务技能：

| 技能ID | 技能名称 | 类型 | 核心能力 | 说明 |
|--------|---------|------|---------|------|
| **skill-scene** | 场景管理 | service-skill | scene-definition, scene-session, scene-validation, scene-role | 场景管理核心服务 |
| **skill-capability** | 能力管理 | service-skill | capability-register, capability-discover, capability-validate, capability-stats | 能力注册发现服务 |
| **skill-user-auth** | 用户认证 | service-skill | user-auth, token-validate, session-manage | 用户认证服务 |
| **skill-health** | 健康检查 | system-service | health-check, service-check, health-report, health-schedule, health-status | 系统健康检查 |
| **skill-monitor** | 监控服务 | system-service | metrics-collect, alert-manage, log-query, observation, metrics-query | 系统监控告警 |

---

### 2.4 知识库与AI技能清单 (Knowledge & AI Skills)

| 技能ID | 技能名称 | 类型 | 核心能力 | 说明 |
|--------|---------|------|---------|------|
| **skill-knowledge-base** | 知识库核心 | system-service | kb-management, document-management, search | 知识库管理、BM25检索 |
| **skill-rag** | RAG检索增强 | system-service | retrieval, prompt-building, kb-registration | 语义检索、Prompt构建 |
| **skill-local-knowledge** | 本地知识库 | system-service | local-kb, offline-search | 本地知识库存储 |
| **skill-document-processor** | 文档处理 | system-service | doc-parse, doc-chunk, doc-index | 文档解析分块索引 |

---

### 2.5 LLM相关技能清单 (LLM Skills)

| 技能ID | 技能名称 | 类型 | 核心能力 | 说明 |
|--------|---------|------|---------|------|
| **skill-llm-chat** | LLM智能对话 | **scene-skill** | llm-chat, session-manage, history-record, context-extraction, context-merging | 智能对话场景 |
| **skill-llm-conversation** | LLM对话服务 | service-skill | llm-chat, session-manage, history-record | 底层对话服务 |
| **skill-llm-context-builder** | 上下文构建 | service-skill | context-extraction, context-merging | 上下文提取合并 |
| **skill-llm-config-manager** | LLM配置管理 | service-skill | config-manage, provider-manage | LLM配置管理 |
| **skill-llm-assistant-ui** | LLM助手UI | ui-skill | - | LLM助手界面 |
| **skill-llm-management-ui** | LLM管理UI | ui-skill | - | LLM管理界面 |

---

### 2.6 组织与通讯技能清单 (Org & Communication Skills)

| 技能ID | 技能名称 | 类型 | 核心能力 | 说明 |
|--------|---------|------|---------|------|
| **skill-org-dingding** | 钉钉集成 | integration-skill | dd-auth, dd-user-sync, dd-msg | 钉钉组织集成 |
| **skill-org-feishu** | 飞书集成 | integration-skill | fs-auth, fs-user-sync, fs-msg | 飞书组织集成 |
| **skill-org-wecom** | 企业微信集成 | integration-skill | wc-auth, wc-user-sync, wc-msg | 企业微信集成 |
| **skill-org-ldap** | LDAP集成 | integration-skill | ldap-auth, ldap-user-sync | LDAP目录集成 |
| **skill-msg** | 消息服务 | service-skill | msg-send, msg-template, msg-history | 消息通知服务 |

---

### 2.7 存储与文件技能清单 (Storage Skills)

| 技能ID | 技能名称 | 类型 | 核心能力 | 说明 |
|--------|---------|------|---------|------|
| **skill-vfs-local** | 本地文件存储 | storage-skill | file-read, file-write, file-delete | 本地文件系统 |
| **skill-vfs-minio** | MinIO存储 | storage-skill | object-put, object-get, object-delete | MinIO对象存储 |
| **skill-vfs-oss** | 阿里云OSS | storage-skill | object-put, object-get, object-delete | 阿里云OSS |
| **skill-vfs-s3** | AWS S3 | storage-skill | object-put, object-get, object-delete | AWS S3 |
| **skill-vfs-database** | 数据库存储 | storage-skill | blob-store, blob-retrieve | 数据库Blob存储 |

---

## 三、场景技能开发规范

### 3.1 最小场景技能模板

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-{scene-name}
  name: {场景名称}场景技能
  version: 1.0.0
  description: {场景描述}
  author: {作者}
  type: scene-skill  # 关键：类型必须是 scene-skill

spec:
  type: scene-skill
  
  # 依赖声明
  dependencies:
    - id: skill-{dependency}
      version: ">=1.0.0"
      required: true
      description: "依赖说明"
  
  # 场景能力定义（核心）
  sceneCapabilities:
    - id: scene-{scene-name}
      name: {场景名称}场景能力
      type: SCENE
      mainFirst: true  # 关键：必须有 mainFirst 标识
      
      mainFirstConfig:
        # 自检阶段
        selfCheck:
          - checkCapabilities: [{capability-list}]
          - checkDriverCapabilities: [{driver-list}]
          - checkDependencies: [{dependency-list}]
        
        # 自启阶段
        selfStart:
          - installDependencies: auto
          - initDriverCapabilities: [{driver-list}]
          - initCapabilities: [{capability-list}]
          - bindAddresses: auto
        
        # 启动协作
        startCollaboration:
          - startScene: scene-{collaborative-scene}
            optional: true
            bindInterface: {interface-name}
        
        # 自驱运行
        selfDrive:
          # 定时规则
          scheduleRules:
            - trigger: "{cron-expression}"
              action: {flow-name}
          
          # 事件规则
          eventRules:
            - event: {event-name}
              condition: "{condition}"
              action: {flow-name}
          
          # 能力链
          capabilityChains:
            {flow-name}:
              - capability: {capability-id}
                input: {input-params}
  
  # 原子能力定义
  capabilities:
    - id: {capability-id}
      name: {能力名称}
      description: {能力描述}
      category: {category}
      type: {ATOMIC|COMPOSITE|DRIVER}
  
  # 端点定义
  endpoints:
    - path: /api/{path}
      method: {GET|POST|PUT|DELETE}
      description: {描述}
      capability: {capability-id}
```

### 3.2 场景技能类型检查清单

开发场景技能时，请确认以下检查项：

| 检查项 | 要求 | 位置 |
|--------|------|------|
| 类型标识 | `metadata.type: scene-skill` | metadata |
| 规格类型 | `spec.type: scene-skill` | spec |
| 场景能力 | `sceneCapabilities` 非空 | spec |
| 自驱标识 | `mainFirst: true` | sceneCapabilities[] |
| 自驱配置 | `mainFirstConfig` 完整 | sceneCapabilities[] |
| 自检配置 | `selfCheck` 定义 | mainFirstConfig |
| 自启配置 | `selfStart` 定义 | mainFirstConfig |
| 自驱配置 | `selfDrive` 定义 | mainFirstConfig |

---

## 四、总结

### 4.1 场景技能核心要点

1. **场景即技能**: 场景不再是静态模板，而是可发现、可分发的技能包
2. **自驱能力**: 通过 `mainFirst` 实现场景的自主运行
3. **能力组合**: 场景技能由多个原子能力组合而成
4. **依赖管理**: 自动解析和安装依赖技能
5. **协作能力**: 可与其他场景技能协作

### 4.2 现有技能统计

| 分类 | 数量 | 场景技能数量 |
|------|------|-------------|
| 基础服务技能 | 5 | 0 |
| 知识库与AI技能 | 4 | 0 |
| LLM相关技能 | 6 | **1** (skill-llm-chat) |
| 组织与通讯技能 | 5 | 0 |
| 存储与文件技能 | 5 | 0 |
| 基础设施技能 | 6 | 0 |
| 运维与托管技能 | 4 | 0 |
| UI展示技能 | 7 | **1** (skill-knowledge-qa) |
| **总计** | **42** | **2** |

### 4.3 后续规划

建议将以下业务场景开发为场景技能：

1. **日志汇报场景** - skill-daily-report
2. **智能家居场景** - skill-smart-home
3. **审批流程场景** - skill-approval-flow
4. **会议管理场景** - skill-meeting-mgmt
5. **项目管理场景** - skill-project-mgmt

---

**文档编写者**: Ooder 开发团队  
**文档日期**: 2026-03-05  
**核心思想**: 场景即技能 (Scene = Skill)
