# Skills 前端可视化分类展示开发说明

> **文档版本**: 1.0.0  
> **创建日期**: 2026-03-12  
> **适用前端**: Vue3 / React / Angular  
> **数据版本**: 2.3.1

---

## 一、概述

本文档为前端开发人员提供 Skills 分类可视化展示的开发指南，包含：
- 分类维度定义
- API 接口规范
- 真实数据示例
- 前端展示建议

---

## 二、分类维度

### 2.1 主要分类维度

| 维度 | 字段名 | 枚举值数量 | 说明 |
|------|--------|:----------:|------|
| **业务领域** | businessCategory | 10 | 用户视角的业务分类 |
| **技能形态** | skillForm | 4 | 技能的技术形态 |
| **可见性** | visibility | 3 | 技能的访问权限 |
| **技术分类** | category | 8 | SE标准技术分类 |
| **能力地址** | capabilityCategory | 17 | 能力地址空间分类 |

### 2.2 枚举值定义

#### businessCategory (业务领域)

```typescript
enum BusinessCategory {
  AI_ASSISTANT = 'AI_ASSISTANT',           // AI助手
  INFRASTRUCTURE = 'INFRASTRUCTURE',       // 基础设施
  SYSTEM_TOOLS = 'SYSTEM_TOOLS',           // 系统工具
  SYSTEM_MONITOR = 'SYSTEM_MONITOR',       // 系统监控
  OFFICE_COLLABORATION = 'OFFICE_COLLABORATION', // 办公协作
  MARKETING_OPERATIONS = 'MARKETING_OPERATIONS', // 营销运营
  SECURITY_AUDIT = 'SECURITY_AUDIT',       // 安全审计
  DATA_PROCESSING = 'DATA_PROCESSING',     // 数据处理
  HUMAN_RESOURCE = 'HUMAN_RESOURCE',       // 人力资源
  FINANCE_ACCOUNTING = 'FINANCE_ACCOUNTING' // 财务会计
}

const BusinessCategoryLabels: Record<BusinessCategory, string> = {
  [BusinessCategory.AI_ASSISTANT]: 'AI助手',
  [BusinessCategory.INFRASTRUCTURE]: '基础设施',
  [BusinessCategory.SYSTEM_TOOLS]: '系统工具',
  [BusinessCategory.SYSTEM_MONITOR]: '系统监控',
  [BusinessCategory.OFFICE_COLLABORATION]: '办公协作',
  [BusinessCategory.MARKETING_OPERATIONS]: '营销运营',
  [BusinessCategory.SECURITY_AUDIT]: '安全审计',
  [BusinessCategory.DATA_PROCESSING]: '数据处理',
  [BusinessCategory.HUMAN_RESOURCE]: '人力资源',
  [BusinessCategory.FINANCE_ACCOUNTING]: '财务会计'
}
```

#### skillForm (技能形态)

```typescript
enum SkillForm {
  PROVIDER = 'PROVIDER',   // 能力服务
  DRIVER = 'DRIVER',       // 驱动适配
  SCENE = 'SCENE',         // 场景应用
  INTERNAL = 'INTERNAL'    // 内部服务
}

const SkillFormLabels: Record<SkillForm, string> = {
  [SkillForm.PROVIDER]: '能力服务',
  [SkillForm.DRIVER]: '驱动适配',
  [SkillForm.SCENE]: '场景应用',
  [SkillForm.INTERNAL]: '内部服务'
}

const SkillFormColors: Record<SkillForm, string> = {
  [SkillForm.PROVIDER]: '#1890ff',   // 蓝色
  [SkillForm.DRIVER]: '#52c41a',     // 绿色
  [SkillForm.SCENE]: '#722ed1',      // 紫色
  [SkillForm.INTERNAL]: '#8c8c8c'    // 灰色
}
```

#### visibility (可见性)

```typescript
enum Visibility {
  PUBLIC = 'public',         // 公开
  DEVELOPER = 'developer',   // 开发者
  INTERNAL = 'internal'      // 内部
}

const VisibilityLabels: Record<Visibility, string> = {
  [Visibility.PUBLIC]: '公开',
  [Visibility.DEVELOPER]: '开发者',
  [Visibility.INTERNAL]: '内部'
}

const VisibilityColors: Record<Visibility, string> = {
  [Visibility.PUBLIC]: '#52c41a',     // 绿色
  [Visibility.DEVELOPER]: '#faad14',  // 橙色
  [Visibility.INTERNAL]: '#8c8c8c'    // 灰色
}
```

#### category (技术分类)

```typescript
enum Category {
  LLM = 'LLM',           // 大语言模型
  KNOWLEDGE = 'KNOWLEDGE', // 知识库
  TOOL = 'TOOL',         // 工具
  WORKFLOW = 'WORKFLOW', // 工作流
  DATA = 'DATA',         // 数据
  SERVICE = 'SERVICE',   // 服务
  UI = 'UI',             // 界面
  OTHER = 'OTHER'        // 其他
}

const CategoryLabels: Record<Category, string> = {
  [Category.LLM]: '大语言模型',
  [Category.KNOWLEDGE]: '知识库',
  [Category.TOOL]: '工具',
  [Category.WORKFLOW]: '工作流',
  [Category.DATA]: '数据',
  [Category.SERVICE]: '服务',
  [Category.UI]: '界面',
  [Category.OTHER]: '其他'
}
```

---

## 三、API 接口规范

### 3.1 获取技能列表

**接口地址**: `GET /api/v1/skills`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|:----:|------|
| businessCategory | string | 否 | 业务领域筛选 |
| skillForm | string | 否 | 技能形态筛选 |
| visibility | string | 否 | 可见性筛选 |
| category | string | 否 | 技术分类筛选 |
| keyword | string | 否 | 关键词搜索 |
| page | number | 否 | 页码，默认1 |
| pageSize | number | 否 | 每页数量，默认20 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 71,
    "page": 1,
    "pageSize": 20,
    "items": [
      {
        "id": "skill-llm-chat",
        "name": "LLM智能对话场景能力",
        "version": "2.3.0",
        "description": "LLM智能对话场景能力，支持多轮对话、上下文感知、流式输出",
        "skillForm": "SCENE",
        "sceneType": "AUTO",
        "visibility": "public",
        "businessCategory": "AI_ASSISTANT",
        "category": "LLM",
        "capabilityCategory": "llm",
        "tags": ["llm", "chat", "ai", "conversation"],
        "path": "skills/scenes/skill-llm-chat"
      }
    ]
  }
}
```

### 3.2 获取分类统计

**接口地址**: `GET /api/v1/skills/statistics`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 71,
    "byBusinessCategory": [
      { "name": "AI_ASSISTANT", "label": "AI助手", "count": 16, "percentage": 23 },
      { "name": "INFRASTRUCTURE", "label": "基础设施", "count": 12, "percentage": 17 },
      { "name": "SYSTEM_TOOLS", "label": "系统工具", "count": 8, "percentage": 11 },
      { "name": "SYSTEM_MONITOR", "label": "系统监控", "count": 8, "percentage": 11 },
      { "name": "OFFICE_COLLABORATION", "label": "办公协作", "count": 7, "percentage": 10 },
      { "name": "MARKETING_OPERATIONS", "label": "营销运营", "count": 5, "percentage": 7 },
      { "name": "SECURITY_AUDIT", "label": "安全审计", "count": 5, "percentage": 7 },
      { "name": "DATA_PROCESSING", "label": "数据处理", "count": 4, "percentage": 6 },
      { "name": "HUMAN_RESOURCE", "label": "人力资源", "count": 2, "percentage": 3 },
      { "name": "FINANCE_ACCOUNTING", "label": "财务会计", "count": 1, "percentage": 1 }
    ],
    "bySkillForm": [
      { "name": "PROVIDER", "label": "能力服务", "count": 35, "percentage": 49 },
      { "name": "DRIVER", "label": "驱动适配", "count": 20, "percentage": 28 },
      { "name": "SCENE", "label": "场景应用", "count": 9, "percentage": 13 },
      { "name": "INTERNAL", "label": "内部服务", "count": 7, "percentage": 10 }
    ],
    "byVisibility": [
      { "name": "public", "label": "公开", "count": 28, "percentage": 39 },
      { "name": "developer", "label": "开发者", "count": 30, "percentage": 42 },
      { "name": "internal", "label": "内部", "count": 13, "percentage": 19 }
    ],
    "byCategory": [
      { "name": "SERVICE", "label": "服务", "count": 32, "percentage": 45 },
      { "name": "LLM", "label": "大语言模型", "count": 10, "percentage": 14 },
      { "name": "KNOWLEDGE", "label": "知识库", "count": 8, "percentage": 11 },
      { "name": "TOOL", "label": "工具", "count": 5, "percentage": 7 },
      { "name": "WORKFLOW", "label": "工作流", "count": 5, "percentage": 7 },
      { "name": "DATA", "label": "数据", "count": 5, "percentage": 7 },
      { "name": "UI", "label": "界面", "count": 2, "percentage": 3 },
      { "name": "OTHER", "label": "其他", "count": 1, "percentage": 1 }
    ]
  }
}
```

### 3.3 获取技能详情

**接口地址**: `GET /api/v1/skills/{skillId}`

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "skill-llm-chat",
    "name": "LLM智能对话场景能力",
    "version": "2.3.0",
    "description": "LLM智能对话场景能力，支持多轮对话、上下文感知、流式输出",
    "skillForm": "SCENE",
    "sceneType": "AUTO",
    "visibility": "public",
    "businessCategory": "AI_ASSISTANT",
    "subCategory": "chat",
    "category": "LLM",
    "capabilityCategory": "llm",
    "capabilityAddresses": {
      "required": [
        { "address": "0x30", "name": "LLM_BASE", "description": "LLM基础服务", "fallback": "0x31" }
      ],
      "optional": []
    },
    "tags": ["llm", "chat", "ai", "conversation", "scene-capability"],
    "dependencies": ["skill-llm-conversation", "skill-llm-context-builder"],
    "roles": [
      {
        "name": "MANAGER",
        "displayName": "场景管理员",
        "minCount": 1,
        "maxCount": 1,
        "permissions": ["READ", "WRITE", "CONFIG", "DELETE"]
      },
      {
        "name": "MEMBER",
        "displayName": "场景成员",
        "minCount": 0,
        "maxCount": 100,
        "permissions": ["READ", "WRITE"]
      }
    ],
    "path": "skills/scenes/skill-llm-chat"
  }
}
```

---

## 四、真实数据示例

### 4.1 完整技能列表数据 (71个)

```json
{
  "skills": [
    {
      "id": "skill-capability",
      "name": "能力管理服务",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "sys"
    },
    {
      "id": "skill-management",
      "name": "技能管理服务",
      "version": "1.0.0",
      "skillForm": "SCENE",
      "sceneType": "AUTO",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "sys"
    },
    {
      "id": "skill-common",
      "name": "通用工具库",
      "version": "1.0.0",
      "skillForm": "INTERNAL",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "sys"
    },
    {
      "id": "skill-protocol",
      "name": "协议处理服务",
      "version": "1.0.0",
      "skillForm": "INTERNAL",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "sys"
    },
    {
      "id": "skill-llm-ollama",
      "name": "Ollama LLM服务",
      "version": "2.0.0",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-llm-openai",
      "name": "OpenAI LLM服务",
      "version": "2.0.0",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-llm-qianwen",
      "name": "通义千问LLM服务",
      "version": "2.0.0",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-llm-deepseek",
      "name": "DeepSeek LLM服务",
      "version": "2.0.0",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-llm-volcengine",
      "name": "火山引擎豆包LLM服务",
      "version": "2.0.0",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-org-base",
      "name": "组织基础服务",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "org"
    },
    {
      "id": "skill-org-dingding",
      "name": "钉钉组织服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "org"
    },
    {
      "id": "skill-org-feishu",
      "name": "飞书组织服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "org"
    },
    {
      "id": "skill-org-wecom",
      "name": "企业微信组织服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "org"
    },
    {
      "id": "skill-org-ldap",
      "name": "LDAP组织服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "org"
    },
    {
      "id": "skill-vfs-base",
      "name": "VFS基础服务",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "DATA",
      "capabilityCategory": "vfs"
    },
    {
      "id": "skill-vfs-local",
      "name": "本地文件存储",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "DATA",
      "capabilityCategory": "vfs"
    },
    {
      "id": "skill-vfs-minio",
      "name": "MinIO对象存储",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "DATA",
      "capabilityCategory": "vfs"
    },
    {
      "id": "skill-vfs-oss",
      "name": "阿里云OSS存储",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "DATA",
      "capabilityCategory": "vfs"
    },
    {
      "id": "skill-vfs-s3",
      "name": "AWS S3存储",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "DATA",
      "capabilityCategory": "vfs"
    },
    {
      "id": "skill-vfs-database",
      "name": "数据库存储",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "DATA",
      "capabilityCategory": "vfs"
    },
    {
      "id": "skill-knowledge-base",
      "name": "知识库核心服务",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "AI_ASSISTANT",
      "category": "KNOWLEDGE",
      "capabilityCategory": "know"
    },
    {
      "id": "skill-rag",
      "name": "RAG检索增强",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "AI_ASSISTANT",
      "category": "KNOWLEDGE",
      "capabilityCategory": "know"
    },
    {
      "id": "skill-llm-conversation",
      "name": "LLM对话服务",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-llm-context-builder",
      "name": "上下文构建服务",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-user-auth",
      "name": "用户认证服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SECURITY_AUDIT",
      "category": "SERVICE",
      "capabilityCategory": "auth"
    },
    {
      "id": "skill-llm-chat",
      "name": "LLM智能对话场景能力",
      "version": "2.3.0",
      "skillForm": "SCENE",
      "sceneType": "AUTO",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-knowledge-qa",
      "name": "知识问答场景能力",
      "version": "2.3.0",
      "skillForm": "SCENE",
      "sceneType": "AUTO",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "KNOWLEDGE",
      "capabilityCategory": "know"
    },
    {
      "id": "skill-business",
      "name": "业务场景服务",
      "version": "0.7.3",
      "skillForm": "SCENE",
      "sceneType": "TRIGGER",
      "visibility": "public",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "WORKFLOW",
      "capabilityCategory": "util"
    },
    {
      "id": "skill-collaboration",
      "name": "协作场景服务",
      "version": "0.7.3",
      "skillForm": "SCENE",
      "sceneType": "TRIGGER",
      "visibility": "public",
      "businessCategory": "OFFICE_COLLABORATION",
      "category": "WORKFLOW",
      "capabilityCategory": "util"
    },
    {
      "id": "skill-market",
      "name": "技能市场服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "SERVICE",
      "capabilityCategory": "util"
    },
    {
      "id": "skill-media-toutiao",
      "name": "头条发布服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "MARKETING_OPERATIONS",
      "category": "SERVICE",
      "capabilityCategory": "media"
    },
    {
      "id": "skill-media-wechat",
      "name": "微信公众号发布服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "MARKETING_OPERATIONS",
      "category": "SERVICE",
      "capabilityCategory": "media"
    },
    {
      "id": "skill-media-weibo",
      "name": "微博发布服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "MARKETING_OPERATIONS",
      "category": "SERVICE",
      "capabilityCategory": "media"
    },
    {
      "id": "skill-media-xiaohongshu",
      "name": "小红书发布服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "MARKETING_OPERATIONS",
      "category": "SERVICE",
      "capabilityCategory": "media"
    },
    {
      "id": "skill-media-zhihu",
      "name": "知乎发布服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "MARKETING_OPERATIONS",
      "category": "SERVICE",
      "capabilityCategory": "media"
    },
    {
      "id": "skill-payment-alipay",
      "name": "支付宝支付服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "SERVICE",
      "capabilityCategory": "payment"
    },
    {
      "id": "skill-payment-wechat",
      "name": "微信支付服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "SERVICE",
      "capabilityCategory": "payment"
    },
    {
      "id": "skill-payment-unionpay",
      "name": "银联支付服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "developer",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "SERVICE",
      "capabilityCategory": "payment"
    },
    {
      "id": "skill-email",
      "name": "邮件服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "OFFICE_COLLABORATION",
      "category": "SERVICE",
      "capabilityCategory": "comm"
    },
    {
      "id": "skill-mqtt",
      "name": "MQTT服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "OFFICE_COLLABORATION",
      "category": "SERVICE",
      "capabilityCategory": "comm"
    },
    {
      "id": "skill-msg",
      "name": "消息服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "OFFICE_COLLABORATION",
      "category": "SERVICE",
      "capabilityCategory": "comm"
    },
    {
      "id": "skill-notify",
      "name": "通知服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "OFFICE_COLLABORATION",
      "category": "SERVICE",
      "capabilityCategory": "comm"
    },
    {
      "id": "skill-im",
      "name": "即时通讯服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "OFFICE_COLLABORATION",
      "category": "SERVICE",
      "capabilityCategory": "comm"
    },
    {
      "id": "skill-group",
      "name": "群组服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "OFFICE_COLLABORATION",
      "category": "SERVICE",
      "capabilityCategory": "comm"
    },
    {
      "id": "skill-agent",
      "name": "代理管理服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SYSTEM_MONITOR",
      "category": "SERVICE",
      "capabilityCategory": "mon"
    },
    {
      "id": "skill-health",
      "name": "健康检查服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SYSTEM_MONITOR",
      "category": "SERVICE",
      "capabilityCategory": "mon"
    },
    {
      "id": "skill-monitor",
      "name": "监控服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SYSTEM_MONITOR",
      "category": "SERVICE",
      "capabilityCategory": "mon"
    },
    {
      "id": "skill-network",
      "name": "网络管理服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SYSTEM_MONITOR",
      "category": "SERVICE",
      "capabilityCategory": "net"
    },
    {
      "id": "skill-remote-terminal",
      "name": "远程终端服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SYSTEM_MONITOR",
      "category": "SERVICE",
      "capabilityCategory": "mon"
    },
    {
      "id": "skill-res-service",
      "name": "资源管理服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SYSTEM_MONITOR",
      "category": "SERVICE",
      "capabilityCategory": "mon"
    },
    {
      "id": "skill-local-knowledge",
      "name": "本地知识服务",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "AI_ASSISTANT",
      "category": "KNOWLEDGE",
      "capabilityCategory": "know"
    },
    {
      "id": "skill-vector-sqlite",
      "name": "SQLite向量存储",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "AI_ASSISTANT",
      "category": "KNOWLEDGE",
      "capabilityCategory": "know"
    },
    {
      "id": "skill-llm-config-manager",
      "name": "LLM配置管理",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "AI_ASSISTANT",
      "category": "LLM",
      "capabilityCategory": "llm"
    },
    {
      "id": "skill-scheduler-quartz",
      "name": "Quartz调度服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "sched"
    },
    {
      "id": "skill-task",
      "name": "任务管理服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "sched"
    },
    {
      "id": "skill-search",
      "name": "搜索服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "developer",
      "businessCategory": "DATA_PROCESSING",
      "category": "DATA",
      "capabilityCategory": "search"
    },
    {
      "id": "skill-security",
      "name": "安全管理服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SECURITY_AUDIT",
      "category": "SERVICE",
      "capabilityCategory": "sec"
    },
    {
      "id": "skill-access-control",
      "name": "访问控制服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SECURITY_AUDIT",
      "category": "SERVICE",
      "capabilityCategory": "sec"
    },
    {
      "id": "skill-audit",
      "name": "审计服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SECURITY_AUDIT",
      "category": "SERVICE",
      "capabilityCategory": "sec"
    },
    {
      "id": "skill-document-processor",
      "name": "文档处理服务",
      "version": "1.0.0",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "TOOL",
      "capabilityCategory": "util"
    },
    {
      "id": "skill-report",
      "name": "报表服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "DATA_PROCESSING",
      "category": "TOOL",
      "capabilityCategory": "util"
    },
    {
      "id": "skill-share",
      "name": "分享服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "public",
      "businessCategory": "SYSTEM_TOOLS",
      "category": "TOOL",
      "capabilityCategory": "util"
    },
    {
      "id": "skill-openwrt",
      "name": "OpenWrt管理服务",
      "version": "0.7.3",
      "skillForm": "DRIVER",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "iot"
    },
    {
      "id": "skill-hosting",
      "name": "托管服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "iot"
    },
    {
      "id": "skill-k8s",
      "name": "Kubernetes集群管理",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "INFRASTRUCTURE",
      "category": "SERVICE",
      "capabilityCategory": "iot"
    },
    {
      "id": "skill-cmd-service",
      "name": "命令监控服务",
      "version": "0.7.3",
      "skillForm": "PROVIDER",
      "visibility": "internal",
      "businessCategory": "SYSTEM_MONITOR",
      "category": "SERVICE",
      "capabilityCategory": "mon"
    },
    {
      "id": "skill-document-assistant",
      "name": "文档助手场景能力",
      "version": "2.3.0",
      "skillForm": "SCENE",
      "sceneType": "AUTO",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "TOOL",
      "capabilityCategory": "util"
    },
    {
      "id": "skill-knowledge-share",
      "name": "知识分享场景能力",
      "version": "2.3.0",
      "skillForm": "SCENE",
      "sceneType": "TRIGGER",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "KNOWLEDGE",
      "capabilityCategory": "know"
    },
    {
      "id": "skill-meeting-minutes",
      "name": "会议纪要场景能力",
      "version": "2.3.0",
      "skillForm": "SCENE",
      "sceneType": "TRIGGER",
      "visibility": "public",
      "businessCategory": "OFFICE_COLLABORATION",
      "category": "WORKFLOW",
      "capabilityCategory": "util"
    },
    {
      "id": "skill-project-knowledge",
      "name": "项目知识场景能力",
      "version": "2.3.0",
      "skillForm": "SCENE",
      "sceneType": "TRIGGER",
      "visibility": "public",
      "businessCategory": "AI_ASSISTANT",
      "category": "KNOWLEDGE",
      "capabilityCategory": "know"
    },
    {
      "id": "skill-onboarding-assistant",
      "name": "入职助手场景能力",
      "version": "2.3.0",
      "skillForm": "SCENE",
      "sceneType": "AUTO",
      "visibility": "public",
      "businessCategory": "HUMAN_RESOURCE",
      "category": "WORKFLOW",
      "capabilityCategory": "util"
    }
  ]
}
```

---

## 五、前端展示建议

### 5.1 推荐图表类型

| 分类维度 | 推荐图表 | 说明 |
|----------|----------|------|
| 业务领域 | 饼图/环形图 | 展示各领域占比 |
| 技能形态 | 柱状图/条形图 | 展示形态分布 |
| 可见性 | 环形图 | 展示权限分布 |
| 技术分类 | 雷达图 | 展示技术栈覆盖 |
| 能力地址 | 树图 | 展示地址空间分配 |

### 5.2 Vue3 组件示例

```vue
<template>
  <div class="skills-statistics">
    <a-row :gutter="16">
      <a-col :span="12">
        <a-card title="业务领域分布">
          <PieChart :data="businessCategoryData" />
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="技能形态分布">
          <BarChart :data="skillFormData" />
        </a-card>
      </a-col>
    </a-row>
    
    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="24">
        <a-card title="技能列表">
          <SkillsTable 
            :data="skills" 
            :loading="loading"
            @filter="handleFilter"
          />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getSkillsStatistics, getSkills } from '@/api/skills'
import PieChart from '@/components/PieChart.vue'
import BarChart from '@/components/BarChart.vue'
import SkillsTable from '@/components/SkillsTable.vue'

const loading = ref(false)
const skills = ref([])
const statistics = ref(null)

const businessCategoryData = computed(() => {
  if (!statistics.value) return []
  return statistics.value.byBusinessCategory.map(item => ({
    name: item.label,
    value: item.count
  }))
})

const skillFormData = computed(() => {
  if (!statistics.value) return []
  return statistics.value.bySkillForm.map(item => ({
    name: item.label,
    value: item.count,
    color: SkillFormColors[item.name]
  }))
})

const fetchData = async () => {
  loading.value = true
  try {
    const [statsRes, skillsRes] = await Promise.all([
      getSkillsStatistics(),
      getSkills()
    ])
    statistics.value = statsRes.data
    skills.value = skillsRes.data.items
  } finally {
    loading.value = false
  }
}

const handleFilter = (filters) => {
  // 处理筛选逻辑
}

onMounted(() => {
  fetchData()
})
</script>
```

### 5.3 React 组件示例

```tsx
import React, { useState, useEffect } from 'react'
import { Card, Row, Col, Table, Spin } from 'antd'
import { Pie, Bar } from '@ant-design/charts'
import { getSkillsStatistics, getSkills } from '@/api/skills'

const SkillsStatistics: React.FC = () => {
  const [loading, setLoading] = useState(false)
  const [skills, setSkills] = useState([])
  const [statistics, setStatistics] = useState(null)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    setLoading(true)
    try {
      const [statsRes, skillsRes] = await Promise.all([
        getSkillsStatistics(),
        getSkills()
      ])
      setStatistics(statsRes.data)
      setSkills(skillsRes.data.items)
    } finally {
      setLoading(false)
    }
  }

  const pieConfig = {
    data: statistics?.byBusinessCategory.map(item => ({
      type: item.label,
      value: item.count
    })) || [],
    angleField: 'value',
    colorField: 'type',
    radius: 0.8,
  }

  const barConfig = {
    data: statistics?.bySkillForm.map(item => ({
      type: item.label,
      value: item.count
    })) || [],
    xField: 'type',
    yField: 'value',
  }

  return (
    <Spin spinning={loading}>
      <Row gutter={16}>
        <Col span={12}>
          <Card title="业务领域分布">
            <Pie {...pieConfig} />
          </Card>
        </Col>
        <Col span={12}>
          <Card title="技能形态分布">
            <Bar {...barConfig} />
          </Card>
        </Col>
      </Row>
    </Spin>
  )
}

export default SkillsStatistics
```

---

## 六、数据 Mock 文件

### 6.1 Mock 数据文件

```json
// mock/skills.json
{
  "statistics": {
    "total": 71,
    "byBusinessCategory": [
      { "name": "AI_ASSISTANT", "label": "AI助手", "count": 16, "percentage": 23 },
      { "name": "INFRASTRUCTURE", "label": "基础设施", "count": 12, "percentage": 17 },
      { "name": "SYSTEM_TOOLS", "label": "系统工具", "count": 8, "percentage": 11 },
      { "name": "SYSTEM_MONITOR", "label": "系统监控", "count": 8, "percentage": 11 },
      { "name": "OFFICE_COLLABORATION", "label": "办公协作", "count": 7, "percentage": 10 },
      { "name": "MARKETING_OPERATIONS", "label": "营销运营", "count": 5, "percentage": 7 },
      { "name": "SECURITY_AUDIT", "label": "安全审计", "count": 5, "percentage": 7 },
      { "name": "DATA_PROCESSING", "label": "数据处理", "count": 4, "percentage": 6 },
      { "name": "HUMAN_RESOURCE", "label": "人力资源", "count": 2, "percentage": 3 },
      { "name": "FINANCE_ACCOUNTING", "label": "财务会计", "count": 1, "percentage": 1 }
    ],
    "bySkillForm": [
      { "name": "PROVIDER", "label": "能力服务", "count": 35, "percentage": 49 },
      { "name": "DRIVER", "label": "驱动适配", "count": 20, "percentage": 28 },
      { "name": "SCENE", "label": "场景应用", "count": 9, "percentage": 13 },
      { "name": "INTERNAL", "label": "内部服务", "count": 7, "percentage": 10 }
    ],
    "byVisibility": [
      { "name": "public", "label": "公开", "count": 28, "percentage": 39 },
      { "name": "developer", "label": "开发者", "count": 30, "percentage": 42 },
      { "name": "internal", "label": "内部", "count": 13, "percentage": 19 }
    ]
  }
}
```

---

## 七、注意事项

1. **数据缓存**: 建议在前端缓存统计数据，减少请求频率
2. **权限控制**: 根据用户权限过滤 `internal` 类型的技能
3. **国际化**: 所有标签支持多语言配置
4. **实时更新**: 技能列表变化时需要刷新统计数据

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-12  
**维护团队**: Skills Team & Frontend Team
