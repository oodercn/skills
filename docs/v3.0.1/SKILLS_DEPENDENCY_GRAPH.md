# Skills 依赖关系图

> 文档路径: `e:\github\ooder-skills\docs\v3.0.1\SKILLS_DEPENDENCY_GRAPH.md`
> 创建时间: 2026-04-03
> 版本: 3.0.1

---

## 一、依赖关系总览

### 1.1 依赖层级架构

```
┌─────────────────────────────────────────────────────────────────┐
│                     Layer 4: 场景技能 (Scene Skills)              │
│  skill-business / skill-knowledge-qa / skill-llm-chat / ...     │
└─────────────────────────────────────────────────────────────────┘
                                ↓ 依赖
┌─────────────────────────────────────────────────────────────────┐
│                     Layer 3: 能力服务 (Capability Skills)         │
│  skill-llm-config / skill-scenes / skill-search / ...           │
└─────────────────────────────────────────────────────────────────┘
                                ↓ 依赖
┌─────────────────────────────────────────────────────────────────┐
│                     Layer 2: 驱动服务 (Driver Skills)             │
│  skill-llm-deepseek / skill-vfs-local / skill-org-dingding / ...│
└─────────────────────────────────────────────────────────────────┘
                                ↓ 依赖
┌─────────────────────────────────────────────────────────────────┐
│                     Layer 1: 基础服务 (Base Skills)               │
│  skill-common / skill-llm-base / skill-vfs-base / skill-org-base│
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 依赖统计

| 层级 | 技能数 | 依赖数(平均) | 说明 |
|------|--------|--------------|------|
| Layer 1 | 5 | 0 | 无外部依赖，提供基础能力 |
| Layer 2 | 25+ | 1-2 | 依赖Layer 1 |
| Layer 3 | 15+ | 2-3 | 依赖Layer 1-2 |
| Layer 4 | 20+ | 3-5 | 依赖Layer 1-3 |

---

## 二、Layer 1: 基础服务层

### 2.1 skill-common (通用工具库)

**版本**: 3.0.1
**依赖**: 无
**提供能力**:
- auth-service (认证服务)
- org-service (组织管理)
- config-service (系统配置)
- storage-service (存储服务)

```
skill-common (无依赖)
    ↓ 被依赖
    ├── skill-llm-base
    ├── skill-vfs-base
    ├── skill-org-base
    ├── skill-llm-config
    ├── skill-scenes
    └── ... (几乎所有技能)
```

### 2.2 skill-llm-base (LLM驱动基类)

**版本**: 3.0.1
**依赖**: skill-common@3.0.1
**提供能力**:
- LlmProvider (LLM提供商接口)
- LlmRequest/Response (请求响应模型)
- LlmMessage/Usage (消息和使用量模型)

```
skill-llm-base
    ├── 依赖: skill-common@3.0.1
    └── 被依赖:
        ├── skill-llm-deepseek
        ├── skill-llm-openai
        ├── skill-llm-qianwen
        ├── skill-llm-volcengine
        ├── skill-llm-ollama
        └── skill-llm-baidu
```

### 2.3 skill-vfs-base (VFS基础服务)

**版本**: 2.3.1
**依赖**: 无
**提供能力**:
- vfs-read (文件读取)
- vfs-write (文件写入)
- vfs-delete (文件删除)
- vfs-list (文件列表)

```
skill-vfs-base (无依赖)
    ↓ 被依赖
    ├── skill-vfs-local
    ├── skill-vfs-database
    ├── skill-vfs-minio
    ├── skill-vfs-oss
    └── skill-vfs-s3
```

### 2.4 skill-org-base (组织基础服务)

**版本**: 2.3
**依赖**: 无
**提供能力**:
- user-auth (用户认证)
- org-management (组织管理)
- role-detection (角色识别)

```
skill-org-base (无依赖)
    ↓ 被依赖
    ├── skill-org-dingding
    ├── skill-org-feishu
    ├── skill-org-wecom
    └── skill-org-ldap
```

### 2.5 skill-spi-llm (LLM SPI接口)

**版本**: 3.0.1
**依赖**: 无
**提供能力**:
- LlmService (LLM服务接口)
- LlmStreamHandler (流式处理接口)

```
skill-spi-llm (无依赖)
    ↓ 被依赖
    ├── skill-llm-base
    └── skill-llm-config
```

---

## 三、Layer 2: 驱动服务层

### 3.1 LLM驱动依赖图

```
                    skill-llm-base
                         ↑
    ┌────────────────────┼────────────────────┐
    │         │          │          │         │
skill-llm  skill-llm  skill-llm  skill-llm  skill-llm
-deepseek  -openai   -qianwen  -volcengine  -ollama
    │         │          │          │         │
    └─────────┴──────────┴──────────┴─────────┘
                         ↓
                    llm-sdk@3.0.1
```

| 驱动 | 依赖 | 版本要求 |
|------|------|----------|
| skill-llm-deepseek | llm-sdk | >=3.0.1 |
| skill-llm-openai | llm-sdk | >=3.0.1 |
| skill-llm-qianwen | llm-sdk | >=3.0.1 |
| skill-llm-volcengine | llm-sdk | >=3.0.1 |
| skill-llm-ollama | llm-sdk | >=3.0.1 |
| skill-llm-baidu | llm-sdk | >=3.0.1 |
| skill-llm-monitor | skill-common | >=3.0.1 |

### 3.2 VFS驱动依赖图

```
                    skill-vfs-base
                         ↑
    ┌────────────────────┼────────────────────┐
    │                    │                    │
skill-vfs-local    skill-vfs-database   skill-vfs-minio
    │                    │                    │
    └────────────────────┴────────────────────┘
                         ↓
                   (无额外依赖)
```

### 3.3 组织驱动依赖图

```
                    skill-org-base
                         ↑
    ┌────────────────────┼────────────────────┐
    │                    │                    │
skill-org-dingding  skill-org-feishu  skill-org-wecom
    │                    │                    │
    └────────────────────┴────────────────────┘
                         ↓
              (各平台SDK依赖)
```

### 3.4 媒体驱动依赖图

```
skill-media-wechat ────┐
skill-media-weibo  ────┤
skill-media-zhihu  ────┼──→ media-sdk
skill-media-toutiao────┤
skill-media-xiaohongshu┘
```

### 3.5 支付驱动依赖图

```
skill-payment-alipay ───┐
skill-payment-wechat ───┼──→ payment-sdk
skill-payment-unionpay ─┘
```

---

## 四、Layer 3: 能力服务层

### 4.1 skill-llm-config (LLM配置管理)

**版本**: 3.0.1
**依赖**:
- skill-common@3.0.1
- skill-spi-llm@3.0.1

```
skill-llm-config
    ├── skill-common
    └── skill-spi-llm
```

### 4.2 skill-scenes (场景管理)

**版本**: 3.0.1
**依赖**:
- skill-common@3.0.1

```
skill-scenes
    └── skill-common
```

### 4.3 skill-search (搜索服务)

**版本**: 2.3.1
**依赖**:
- skill-common@3.0.1

```
skill-search
    └── skill-common
```

### 4.4 能力服务依赖关系

```
                    skill-common
                         ↑
    ┌────────────────────┼────────────────────┐
    │                    │                    │
skill-llm-config    skill-scenes      skill-search
    │
    └── skill-spi-llm
```

---

## 五、Layer 4: 场景技能层

### 5.1 skill-llm-chat (LLM聊天助手)

**版本**: 3.0.1
**依赖**:
- skill-common
- skill-llm (任意LLM驱动)

```
skill-llm-chat
    ├── skill-common
    └── skill-llm (deepseek/openai/qianwen/...)
```

### 5.2 skill-knowledge-qa (知识问答)

**版本**: 2.3.1
**依赖**:
- skill-knowledge-base@>=2.3.1 (必需)
- skill-indexing@>=2.3.1 (必需)
- skill-rag@>=2.3.1 (可选)

```
skill-knowledge-qa
    ├── skill-knowledge-base (必需)
    ├── skill-indexing (必需)
    └── skill-rag (可选)
```

### 5.3 skill-business (业务场景)

**版本**: 2.3.1
**依赖**: 无

```
skill-business (无外部依赖)
```

### 5.4 场景技能依赖关系

```
skill-knowledge-qa
    ├── skill-knowledge-base
    │       └── skill-vfs-base
    ├── skill-indexing
    │       └── skill-common
    └── skill-rag (可选)
            └── skill-llm-base

skill-llm-chat
    ├── skill-common
    └── skill-llm-*
            └── skill-llm-base
                    └── skill-common
```

---

## 六、依赖关系树形图

### 6.1 完整依赖树

```
skill-common (基础)
├── skill-llm-base
│   ├── skill-llm-deepseek
│   │   └── llm-sdk
│   ├── skill-llm-openai
│   │   └── llm-sdk
│   ├── skill-llm-qianwen
│   │   └── llm-sdk
│   ├── skill-llm-volcengine
│   │   └── llm-sdk
│   ├── skill-llm-ollama
│   │   └── llm-sdk
│   └── skill-llm-baidu
│       └── llm-sdk
│
├── skill-vfs-base
│   ├── skill-vfs-local
│   ├── skill-vfs-database
│   ├── skill-vfs-minio
│   ├── skill-vfs-oss
│   └── skill-vfs-s3
│
├── skill-org-base
│   ├── skill-org-dingding
│   ├── skill-org-feishu
│   ├── skill-org-wecom
│   └── skill-org-ldap
│
├── skill-llm-config
│   └── skill-spi-llm
│
├── skill-scenes
│
├── skill-llm-chat
│   └── skill-llm-*
│
└── skill-knowledge-qa
    ├── skill-knowledge-base
    ├── skill-indexing
    └── skill-rag
```

### 6.2 按服务类别分类

```
┌── LLM服务 ──────────────────────────────────┐
│  skill-spi-llm (SPI)                        │
│      └── skill-llm-base (基类)              │
│           ├── skill-llm-deepseek            │
│           ├── skill-llm-openai              │
│           ├── skill-llm-qianwen             │
│           ├── skill-llm-volcengine          │
│           ├── skill-llm-ollama              │
│           └── skill-llm-baidu               │
│  skill-llm-config (配置管理)                │
│  skill-llm-monitor (监控)                   │
│  skill-llm-chat (聊天应用)                  │
└─────────────────────────────────────────────┘

┌── 组织服务 ──────────────────────────────────┐
│  skill-org-base (基类)                      │
│      ├── skill-org-dingding                 │
│      ├── skill-org-feishu                   │
│      ├── skill-org-wecom                    │
│      └── skill-org-ldap                     │
│  skill-user-auth (认证)                     │
└─────────────────────────────────────────────┘

┌── 存储服务 ──────────────────────────────────┐
│  skill-vfs-base (基类)                      │
│      ├── skill-vfs-local                    │
│      ├── skill-vfs-database                 │
│      ├── skill-vfs-minio                    │
│      ├── skill-vfs-oss                      │
│      └── skill-vfs-s3                       │
└─────────────────────────────────────────────┘

┌── 知识服务 ──────────────────────────────────┐
│  skill-knowledge-base                       │
│  skill-indexing                             │
│  skill-rag                                  │
│  skill-search                               │
│  skill-knowledge-qa (场景)                  │
└─────────────────────────────────────────────┘

┌── 消息通讯 ──────────────────────────────────┐
│  skill-mqtt                                 │
│  skill-msg                                  │
│  skill-im                                   │
│  skill-group                                │
│  skill-email                                │
│  skill-notify                               │
└─────────────────────────────────────────────┘

┌── 媒体服务 ──────────────────────────────────┐
│  skill-media-wechat                         │
│  skill-media-weibo                          │
│  skill-media-zhihu                          │
│  skill-media-toutiao                        │
│  skill-media-xiaohongshu                    │
└─────────────────────────────────────────────┘

┌── 支付服务 ──────────────────────────────────┐
│  skill-payment-alipay                       │
│  skill-payment-wechat                       │
│  skill-payment-unionpay                     │
└─────────────────────────────────────────────┘
```

---

## 七、安装顺序建议

### 7.1 基础安装顺序

```
1. skill-common (必须最先安装)
2. skill-spi-llm (LLM SPI)
3. skill-llm-base (LLM基类)
4. skill-vfs-base (VFS基类)
5. skill-org-base (组织基类)
```

### 7.2 LLM场景安装顺序

```
1. skill-common
2. skill-spi-llm
3. skill-llm-base
4. skill-llm-deepseek (或其他LLM驱动)
5. skill-llm-config
6. skill-llm-chat
```

### 7.3 知识问答安装顺序

```
1. skill-common
2. skill-vfs-base
3. skill-knowledge-base
4. skill-indexing
5. skill-rag (可选)
6. skill-knowledge-qa
```

---

## 八、依赖冲突解决

### 8.1 版本冲突

| 场景 | 解决方案 |
|------|----------|
| 多个技能依赖不同版本的skill-common | 使用最高版本 |
| LLM驱动版本不一致 | 统一使用3.0.1版本 |

### 8.2 循环依赖

当前无循环依赖问题。

### 8.3 可选依赖处理

| 技能 | 可选依赖 | 说明 |
|------|----------|------|
| skill-knowledge-qa | skill-rag | 不安装时RAG功能不可用 |
| skill-llm-chat | skill-llm-* | 需至少安装一个LLM驱动 |

---

## 九、变更记录

| 日期 | 版本 | 变更内容 |
|------|------|----------|
| 2026-04-03 | v1.0 | 初始创建，整理所有Skills依赖关系 |
