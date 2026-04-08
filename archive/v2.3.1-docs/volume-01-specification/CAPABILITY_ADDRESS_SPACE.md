# 能力地址空间设计 v2.3.1

## 设计原则

1. **地址即路由** - 每个地址对应一个实际的服务/驱动，作为调用路由路径
2. **多驱动并存** - 不同驱动可以同时运行，地址作为区分标识
3. **固定地址分配** - 每个驱动/服务占用固定地址，便于配置和路由
4. **1+6+1模式** - 每个分类8个地址：1个默认 + 6个常用 + 1个预留

---

## 一、地址统计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     地址统计 (总计 256 地址)                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   区域           │  地址范围        │  数量    │  说明                       │
│   ───────────────┼──────────────────┼──────────┼────────────────────────────│
│   系统区 (SYS)   │  0x00 - 0x07     │  8       │  系统核心服务               │
│   组织区 (ORG)   │  0x08 - 0x0F     │  8       │  组织驱动 (dingding等)      │
│   认证区 (AUTH)  │  0x10 - 0x17     │  8       │  认证驱动                  │
│   存储区 (VFS)   │  0x18 - 0x1F     │  8       │  存储驱动 (local/minio等)   │
│   数据库区 (DB)  │  0x20 - 0x27     │  8       │  数据库驱动                │
│   AI区 (LLM)     │  0x28 - 0x2F     │  8       │  LLM驱动 (openai/qianwen等)│
│   知识区 (KNOW)  │  0x30 - 0x37     │  8       │  知识库驱动                │
│   支付区 (PAY)   │  0x38 - 0x3F     │  8       │  支付驱动 (alipay/wechat等)│
│   媒体区 (MEDIA) │  0x40 - 0x47     │  8       │  媒体发布驱动              │
│   通讯区 (COMM)  │  0x48 - 0x4F     │  8       │  通讯驱动                  │
│   监控区 (MON)   │  0x50 - 0x57     │  8       │  监控驱动                  │
│   IoT区 (IOT)    │  0x58 - 0x5F     │  8       │  IoT驱动                   │
│   搜索区 (SEARCH)│  0x60 - 0x67     │  8       │  搜索驱动                  │
│   调度区 (SCHED) │  0x68 - 0x6F     │  8       │  任务调度驱动              │
│   安全区 (SEC)   │  0x70 - 0x77     │  8       │  安全驱动                  │
│   网络区 (NET)   │  0x78 - 0x7F     │  8       │  网络驱动                  │
│   ───────────────┼──────────────────┼──────────┼────────────────────────────│
│   已分配         │  0x00 - 0x7F     │  128     │  16个分类 × 8地址           │
│   扩展区         │  0x80 - 0xFF     │  128     │  用户自定义扩展            │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 二、详细地址分配

### 2.1 系统区 (SYS) - 0x00 - 0x07

| 地址 | 代码 | 名称 | 路由说明 |
|------|------|------|---------|
| 0x00 | sys.core | 系统核心 | [默认] 核心服务入口 |
| 0x01 | sys.installer | 安装器 | 技能安装服务 |
| 0x02 | sys.registry | 能力注册表 | 能力发现与注册 |
| 0x03 | sys.config | 配置管理 | 系统配置服务 |
| 0x04 | sys.logging | 日志服务 | 日志收集与查询 |
| 0x05 | sys.audit | 审计服务 | 操作审计记录 |
| 0x06 | sys.monitor | 系统监控 | 系统指标采集 |
| 0x07 | sys.reserved | 系统预留 | [预留] 扩展 |

### 2.2 组织区 (ORG) - 0x08 - 0x0F

| 地址 | 代码 | 名称 | 路由说明 |
|------|------|------|---------|
| 0x08 | org.local | 本地组织 | [默认] 本地组织管理 |
| 0x09 | org.dingding | 钉钉 | 钉钉组织数据 |
| 0x0A | org.feishu | 飞书 | 飞书组织数据 |
| 0x0B | org.wecom | 企业微信 | 企业微信组织数据 |
| 0x0C | org.ldap | LDAP | LDAP组织数据 |
| 0x0D | org.ad | AD域 | Active Directory |
| 0x0E | org.custom | 自定义组织 | 自定义组织源 |
| 0x0F | org.reserved | 组织预留 | [预留] 扩展 |

### 2.3 认证区 (AUTH) - 0x10 - 0x17

| 地址 | 代码 | 名称 | 路由说明 |
|------|------|------|---------|
| 0x10 | auth.local | 本地认证 | [默认] 本地用户认证 |
| 0x11 | auth.dingding | 钉钉认证 | 钉钉OAuth |
| 0x12 | auth.feishu | 飞书认证 | 飞书OAuth |
| 0x13 | auth.wecom | 企业微信认证 | 企业微信OAuth |
| 0x14 | auth.ldap | LDAP认证 | LDAP认证 |
| 0x15 | auth.saml | SAML认证 | SAML SSO |
| 0x16 | auth.oauth2 | OAuth2认证 | 通用OAuth2 |
| 0x17 | auth.reserved | 认证预留 | [预留] 扩展 |

### 2.4 存储区 (VFS) - 0x18 - 0x1F

| 地址 | 代码 | 名称 | 路由说明 |
|------|------|------|---------|
| 0x18 | vfs.local | 本地存储 | [默认] 本地文件系统 |
| 0x19 | vfs.database | 数据库存储 | 数据库文件存储 |
| 0x1A | vfs.minio | MinIO | MinIO对象存储 |
| 0x1B | vfs.oss | 阿里云OSS | 阿里云对象存储 |
| 0x1C | vfs.s3 | AWS S3 | AWS对象存储 |
| 0x1D | vfs.cos | 腾讯云COS | 腾讯云对象存储 |
| 0x1E | vfs.obs | 华为云OBS | 华为云对象存储 |
| 0x1F | vfs.reserved | 存储预留 | [预留] 扩展 |

### 2.5 数据库区 (DB) - 0x20 - 0x27

| 地址 | 代码 | 名称 | 路由说明 |
|------|------|------|---------|
| 0x20 | db.sqlite | SQLite | [默认] 嵌入式数据库 |
| 0x21 | db.mysql | MySQL | MySQL数据库 |
| 0x22 | db.postgresql | PostgreSQL | PostgreSQL数据库 |
| 0x23 | db.mongodb | MongoDB | MongoDB文档数据库 |
| 0x24 | db.redis | Redis | Redis缓存数据库 |
| 0x25 | db.elasticsearch | Elasticsearch | ES搜索引擎 |
| 0x26 | db.clickhouse | ClickHouse | ClickHouse分析库 |
| 0x27 | db.reserved | 数据库预留 | [预留] 扩展 |

### 2.6 AI区 (LLM) - 0x28 - 0x2F

| 地址 | 代码 | 名称 | 路由说明 |
|------|------|------|---------|
| 0x28 | llm.ollama | Ollama本地 | [默认] 本地LLM |
| 0x29 | llm.openai | OpenAI | OpenAI GPT |
| 0x2A | llm.qianwen | 通义千问 | 阿里云通义 |
| 0x2B | llm.deepseek | DeepSeek | DeepSeek模型 |
| 0x2C | llm.volcengine | 火山引擎 | 字节豆包 |
| 0x2D | llm.claude | Claude | Anthropic Claude |
| 0x2E | llm.gemini | Gemini | Google Gemini |
| 0x2F | llm.reserved | LLM预留 | [预留] 扩展 |

### 2.7 知识区 (KNOW) - 0x30 - 0x37

| 地址 | 代码 | 名称 | 路由说明 |
|------|------|------|---------|
| 0x30 | know.local | 本地知识库 | [默认] 本地向量库 |
| 0x31 | know.rag | RAG服务 | RAG检索增强 |
| 0x32 | know.milvus | Milvus | Milvus向量库 |
| 0x33 | know.pinecone | Pinecone | Pinecone向量库 |
| 0x34 | know.weaviate | Weaviate | Weaviate向量库 |
| 0x35 | know.qdrant | Qdrant | Qdrant向量库 |
| 0x36 | know.chroma | Chroma | Chroma向量库 |
| 0x37 | know.reserved | 知识库预留 | [预留] 扩展 |

---

## 三、地址使用规范

### 3.1 地址分配原则

1. **固定地址** - 每个驱动使用固定地址，不可动态分配
2. **单例服务** - 系统区服务单例运行，不可多实例
3. **多驱动并存** - 其他区域驱动可多实例并存

### 3.2 能力注册格式

```json
{
    "id": "cap-llm-chat",
    "address": "0x28",
    "addressCode": "llm.ollama",
    "name": "LLM对话能力",
    "category": "llm",
    "type": "driver"
}
```

### 3.3 路由规则

```
请求 → 能力地址 → 驱动实例 → 执行

示例:
POST /api/v1/capabilities/cap-llm-chat/invoke
  → 地址 0x28 (llm.ollama)
  → OllamaDriver 实例
  → 执行对话
```

---

## 四、skill.yaml 配置

### 4.1 能力定义

```yaml
capabilities:
  - id: cap-llm-chat
    name: LLM对话
    description: 大语言模型对话能力
    category: llm
    address: "0x28"
    addressCode: llm.ollama
    type: driver
    input:
      prompt:
        type: string
        required: true
      model:
        type: string
        default: "deepseek-chat"
    output:
      response:
        type: string
```

### 4.2 驱动配置

```yaml
drivers:
  - id: llm.ollama
    name: Ollama本地LLM
    address: "0x28"
    class: net.ooder.driver.llm.OllamaDriver
    config:
      baseUrl: http://localhost:11434
      defaultModel: deepseek-chat
```

---

## 五、迁移指南

从 v2.3 迁移到 v2.3.1：

1. 更新能力定义
   - 添加 `address` 字段
   - 添加 `addressCode` 字段

2. 配置驱动
   - 使用固定地址
   - 配置路由映射

3. 更新调用方式
   - 通过地址路由
   - 支持多驱动切换
