# OODER 能力依赖协作关系图

## 一、能力概览

根据API `/api/v1/capabilities` 返回的数据，当前系统共有 **12个能力**：

| 能力ID | 名称 | 类型 | 分类 | 描述 |
|--------|------|------|------|------|
| cap-llm-vision | LLM视觉 | LLM | COMMUNICATION | 大语言模型视觉理解能力 |
| cap-llm-chat | LLM对话 | LLM | COMMUNICATION | 大语言模型对话能力 |
| cap-llm-embedding | LLM嵌入 | LLM | COMMUNICATION | 文本嵌入向量生成能力 |
| cap-knowledge-index | 知识索引 | KNOWLEDGE | KNOWLEDGE | 知识库索引管理能力 |
| cap-knowledge-search | 知识检索 | KNOWLEDGE | KNOWLEDGE | 知识库检索能力 |
| cap-db-query | 数据库查询 | DRIVER | STORAGE | 数据库查询能力 |
| cap-vfs-read | 文件读取 | VFS | STORAGE | 虚拟文件系统读取能力 |
| cap-vfs-write | 文件写入 | VFS | STORAGE | 虚拟文件系统写入能力 |
| cap-http-request | HTTP请求 | TOOL | INTEGRATION | HTTP请求能力 |
| cap-scene-manage | 场景管理 | SCENE | BUSINESS | 场景管理能力 |
| cap-scene-create | 创建新场景 | SCENE | BUSINESS | 创建新场景能力 |

---

## 二、能力分类架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           OODER 能力生态系统                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐             │
│  │   COMMUNICATION │  │    KNOWLEDGE    │  │     STORAGE     │             │
│  │     通信层      │  │     知识层      │  │     存储层      │             │
│  ├─────────────────┤  ├─────────────────┤  ├─────────────────┤             │
│  │ cap-llm-vision  │  │cap-knowledge-   │  │ cap-db-query    │             │
│  │ cap-llm-chat    │  │    index        │  │ cap-vfs-read    │             │
│  │ cap-llm-        │  │cap-knowledge-   │  │ cap-vfs-write   │             │
│  │    embedding    │  │    search       │  │                 │             │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘             │
│           │                    │                    │                       │
│           └────────────────────┼────────────────────┘                       │
│                                │                                            │
│                                ▼                                            │
│                    ┌─────────────────────┐                                 │
│                    │     INTEGRATION     │                                 │
│                    │       集成层        │                                 │
│                    ├─────────────────────┤                                 │
│                    │  cap-http-request   │                                 │
│                    └──────────┬──────────┘                                 │
│                               │                                             │
│                               ▼                                             │
│                    ┌─────────────────────┐                                 │
│                    │      BUSINESS       │                                 │
│                    │       业务层        │                                 │
│                    ├─────────────────────┤                                 │
│                    │  cap-scene-manage   │                                 │
│                    │  cap-scene-create   │                                 │
│                    └─────────────────────┘                                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、能力依赖关系图

### 3.1 核心依赖关系

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              能力依赖关系图                                    │
└──────────────────────────────────────────────────────────────────────────────┘

                    ┌─────────────────┐
                    │  cap-scene-     │
                    │    manage       │
                    │   (场景管理)     │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
    ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
    │cap-scene-   │  │cap-knowledge│  │ cap-llm-    │
    │  create     │  │  -search    │  │   chat      │
    │(创建新场景)  │  │ (知识检索)   │  │ (LLM对话)   │
    └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
           │                │                │
           │                ▼                │
           │        ┌─────────────┐          │
           │        │cap-knowledge│          │
           │        │   -index    │          │
           │        │ (知识索引)   │          │
           │        └──────┬──────┘          │
           │               │                 │
           │               ▼                 │
           │        ┌─────────────┐          │
           │        │cap-llm-     │          │
           │        │ embedding   │◄─────────┘
           │        │ (LLM嵌入)   │
           │        └──────┬──────┘
           │               │
           ▼               ▼
    ┌─────────────────────────────┐
    │        cap-http-request      │
    │         (HTTP请求)           │
    └──────────────┬──────────────┘
                   │
         ┌─────────┴─────────┐
         │                   │
         ▼                   ▼
    ┌─────────────┐   ┌─────────────┐
    │ cap-vfs-    │   │ cap-db-     │
    │   read      │   │   query     │
    │ (文件读取)   │   │ (数据库查询) │
    └──────┬──────┘   └─────────────┘
           │
           ▼
    ┌─────────────┐
    │ cap-vfs-    │
    │   write     │
    │ (文件写入)   │
    └─────────────┘


    ┌─────────────┐
    │ cap-llm-    │  ◄── 独立能力
    │   vision    │
    │ (LLM视觉)   │
    └─────────────┘
```

### 3.2 依赖关系矩阵

| 能力 | cap-llm-chat | cap-llm-embedding | cap-knowledge-index | cap-knowledge-search | cap-http-request | cap-vfs-read | cap-vfs-write | cap-db-query |
|------|:------------:|:-----------------:|:-------------------:|:--------------------:|:----------------:|:------------:|:-------------:|:------------:|
| cap-scene-manage | ✓ | - | - | ✓ | - | - | - | - |
| cap-scene-create | - | - | - | - | ✓ | ✓ | ✓ | - |
| cap-llm-chat | - | ✓ | - | - | - | - | - | - |
| cap-knowledge-search | - | ✓ | ✓ | - | - | - | - | - |
| cap-knowledge-index | - | ✓ | - | - | - | - | ✓ | - |
| cap-http-request | - | - | - | - | - | ✓ | ✓ | ✓ |

---

## 四、能力协作流程

### 4.1 知识问答场景

```
用户问题
    │
    ▼
┌─────────────┐
│ cap-llm-    │ ──── 理解问题意图
│   chat      │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│cap-knowledge│ ──── 检索相关知识
│  -search    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│cap-knowledge│ ──── 获取索引数据
│  -index     │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│cap-llm-     │ ──── 向量化检索
│ embedding   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ cap-llm-    │ ──── 生成回答
│   chat      │
└─────────────┘
```

### 4.2 场景创建流程

```
创建场景请求
    │
    ▼
┌─────────────┐
│ cap-scene-  │ ──── 验证场景配置
│  create     │
└──────┬──────┘
       │
       ├──────────────────┐
       │                  │
       ▼                  ▼
┌─────────────┐    ┌─────────────┐
│cap-http-    │    │ cap-vfs-    │
│  request    │    │   write     │
│(获取配置)    │    │(保存场景)    │
└─────────────┘    └─────────────┘
```

---

## 五、能力分层架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        应用层 (Application)                      │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    场景管理能力                           │   │
│  │         cap-scene-manage  │  cap-scene-create           │   │
│  └─────────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────────┤
│                        服务层 (Service)                          │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐       │
│  │  LLM服务      │  │  知识服务      │  │  集成服务      │       │
│  │ cap-llm-chat  │  │cap-knowledge- │  │cap-http-      │       │
│  │ cap-llm-      │  │  search       │  │  request      │       │
│  │  embedding    │  │cap-knowledge- │  │               │       │
│  │ cap-llm-vision│  │  index        │  │               │       │
│  └───────────────┘  └───────────────┘  └───────────────┘       │
├─────────────────────────────────────────────────────────────────┤
│                        存储层 (Storage)                          │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐       │
│  │  文件系统      │  │  数据库        │  │  向量存储      │       │
│  │ cap-vfs-read  │  │ cap-db-query  │  │ (embedding)   │       │
│  │ cap-vfs-write │  │               │  │               │       │
│  └───────────────┘  └───────────────┘  └───────────────┘       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 六、能力统计

### 6.1 按类型统计

| 类型 | 数量 | 能力列表 |
|------|------|----------|
| LLM | 3 | cap-llm-vision, cap-llm-chat, cap-llm-embedding |
| KNOWLEDGE | 2 | cap-knowledge-index, cap-knowledge-search |
| VFS | 2 | cap-vfs-read, cap-vfs-write |
| DRIVER | 1 | cap-db-query |
| TOOL | 1 | cap-http-request |
| SCENE | 2 | cap-scene-manage, cap-scene-create |

### 6.2 按分类统计

| 分类 | 数量 | 能力列表 |
|------|------|----------|
| COMMUNICATION | 3 | cap-llm-vision, cap-llm-chat, cap-llm-embedding |
| KNOWLEDGE | 2 | cap-knowledge-index, cap-knowledge-search |
| STORAGE | 3 | cap-db-query, cap-vfs-read, cap-vfs-write |
| INTEGRATION | 1 | cap-http-request |
| BUSINESS | 2 | cap-scene-manage, cap-scene-create |

---

## 七、建议的依赖定义

当前系统中能力的 `dependencies` 字段均为空，建议按以下规则定义依赖：

```json
{
  "capabilityId": "cap-knowledge-search",
  "dependencies": ["cap-llm-embedding", "cap-knowledge-index"]
}
```

```json
{
  "capabilityId": "cap-knowledge-index",
  "dependencies": ["cap-llm-embedding", "cap-vfs-write"]
}
```

```json
{
  "capabilityId": "cap-llm-chat",
  "dependencies": ["cap-llm-embedding"]
}
```

```json
{
  "capabilityId": "cap-scene-create",
  "dependencies": ["cap-http-request", "cap-vfs-read", "cap-vfs-write"]
}
```

```json
{
  "capabilityId": "cap-scene-manage",
  "dependencies": ["cap-scene-create", "cap-knowledge-search", "cap-llm-chat"]
}
```

---

*文档生成时间: 2026-03-16*
*数据来源: /api/v1/capabilities*
