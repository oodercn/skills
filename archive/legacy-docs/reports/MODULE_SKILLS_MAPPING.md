# 模块与 Skills 映射表

## 一、映射原则

1. **核心模块** → skill-common（公共基础能力）
2. **场景模块** → skill-scene（场景引擎）
3. **能力模块** → skill-capability（能力服务）
4. **知识模块** → skill-knowledge（知识库）
5. **驱动模块** → skill-vfs-*, skill-notification-*（驱动适配）

## 二、完整映射表

| 模块ID | 模块名称 | 类型 | 归属Skill | 安装方式 | 优先级 |
|--------|----------|------|-----------|----------|--------|
| **auth** | 认证授权 | core | skill-common | 内置 | P0 |
| **menu** | 菜单系统 | core | skill-common | 内置 | P0 |
| **config** | 配置管理 | core | skill-common | 内置 | P0 |
| **org-management** | 组织管理 | core | skill-common | 内置 | P0 |
| **audit-logs** | 审计日志 | core | skill-common | 内置 | P1 |
| **dict** | 字典服务 | core | skill-common | 内置 | P1 |
| **capability-discovery** | 能力发现 | scene | skill-scene | 安装 | P0 |
| **scene-management** | 场景管理 | scene | skill-scene | 安装 | P0 |
| **template-management** | 模板管理 | scene | skill-scene | 安装 | P1 |
| **personal-workspace** | 个人工作台 | scene | skill-scene | 安装 | P1 |
| **llm-config** | LLM配置 | provider | skill-scene | 安装 | P0 |
| **llm-provider** | LLM服务 | provider | skill-llm-* | 安装 | P0 |
| **knowledge-base** | 知识库 | provider | skill-knowledge | 安装 | P1 |
| **vfs-local** | 本地文件系统 | driver | skill-vfs-local | 安装 | P0 |
| **vfs-s3** | S3存储 | driver | skill-vfs-s3 | 安装 | P2 |
| **notification-email** | 邮件通知 | driver | skill-notification | 安装 | P1 |
| **notification-wechat** | 微信通知 | driver | skill-notification | 安装 | P2 |
| **user-management** | 用户管理 | core | skill-common | 内置 | P0 |
| **system-config** | 系统配置 | core | skill-scene | 内置 | P0 |

## 三、Skills 目录结构映射

```
skills/
├── _system/                          # 系统核心 Skills
│   ├── skill-common/                 # 公共基础能力
│   │   ├── modules/
│   │   │   ├── auth/                 # 认证授权模块
│   │   │   ├── menu/                 # 菜单系统模块
│   │   │   ├── config/               # 配置管理模块
│   │   │   ├── org-management/       # 组织管理模块
│   │   │   ├── user-management/      # 用户管理模块
│   │   │   ├── audit-logs/           # 审计日志模块
│   │   │   └── dict/                 # 字典服务模块
│   │   └── skill.json
│   ├── skill-capability/             # 能力服务
│   │   └── skill.json
│   └── skill-protocol/               # 协议层
│       └── skill.json
│
├── skill-scene/                      # 场景引擎 Skill
│   ├── modules/
│   │   ├── capability-discovery/     # 能力发现模块
│   │   ├── scene-management/         # 场景管理模块
│   │   ├── template-management/      # 模板管理模块
│   │   ├── personal-workspace/       # 个人工作台模块
│   │   ├── llm-config/               # LLM配置模块
│   │   └── system-config/            # 系统配置模块
│   └── skill.json
│
├── capabilities/                     # 能力 Skills
│   ├── knowledge/
│   │   ├── skill-knowledge-base/     # 知识库模块
│   │   ├── skill-rag/                # RAG模块
│   │   └── skill-vector-sqlite/      # 向量存储
│   ├── llm/
│   │   ├── skill-llm-conversation/   # 对话模块
│   │   └── skill-llm-context-builder/# 上下文构建
│   ├── communication/
│   │   ├── skill-notification/       # 通知模块
│   │   ├── skill-email/              # 邮件模块
│   │   └── skill-im/                 # 即时通讯
│   └── scheduler/
│       ├── skill-task/               # 任务模块
│       └── skill-scheduler-quartz/   # 调度模块
│
├── _drivers/                         # 驱动 Skills
│   ├── llm/                          # LLM驱动
│   │   ├── skill-llm-deepseek/
│   │   ├── skill-llm-openai/
│   │   ├── skill-llm-qianwen/
│   │   └── skill-llm-ollama/
│   ├── vfs/                          # 文件系统驱动
│   │   ├── skill-vfs-local/
│   │   ├── skill-vfs-s3/
│   │   ├── skill-vfs-oss/
│   │   └── skill-vfs-minio/
│   ├── org/                          # 组织驱动
│   │   ├── skill-org-ldap/
│   │   ├── skill-org-wecom/
│   │   ├── skill-org-feishu/
│   │   └── skill-org-dingding/
│   ├── media/                        # 媒体驱动
│   │   ├── skill-media-wechat/
│   │   ├── skill-media-weibo/
│   │   └── skill-media-xiaohongshu/
│   └── payment/                      # 支付驱动
│       ├── skill-payment-alipay/
│       ├── skill-payment-wechat/
│       └── skill-payment-unionpay/
│
└── scenes/                           # 场景 Skills
    ├── skill-collaboration/          # 协作场景
    └── skill-business/               # 业务场景
```

## 四、模块依赖关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                         MVP 工程                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                    内置模块 (builtin)                      │  │
│  │  ┌─────┐ ┌─────┐ ┌────────┐ ┌──────────┐ ┌────────────┐  │  │
│  │  │auth │ │menu │ │ config │ │org-manage│ │user-manage │  │  │
│  │  └──┬──┘ └──┬──┘ └───┬────┘ └────┬─────┘ └─────┬──────┘  │  │
│  │     │       │        │           │             │          │  │
│  │     └───────┴────────┴───────────┴─────────────┘          │  │
│  │                          │                                 │  │
│  └──────────────────────────┼────────────────────────────────┘  │
│                             │ 安装                               │
│  ┌──────────────────────────┼────────────────────────────────┐  │
│  │                    已安装模块 (installed)                  │  │
│  │  ┌───────────────┐ ┌───────────────┐ ┌────────────────┐  │  │
│  │  │capability-    │ │scene-         │ │llm-config      │  │  │
│  │  │discovery      │ │management     │ │                │  │  │
│  │  └───────┬───────┘ └───────┬───────┘ └───────┬────────┘  │  │
│  │          │                 │                 │            │  │
│  │          └─────────────────┴─────────────────┘            │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ 安装来源
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        skills 目录                              │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  skill-common   │  │  skill-scene    │  │skill-capability │ │
│  │  ┌───────────┐  │  │  ┌───────────┐  │  │  ┌───────────┐  │ │
│  │  │   auth    │  │  │  │capability │  │  │  │capability │  │ │
│  │  │   menu    │  │  │  │-discovery │  │  │  │  service  │  │ │
│  │  │   config  │  │  │  │scene-     │  │  │  │   ...     │  │ │
│  │  │   ...     │  │  │  │management │  │  │  │           │  │ │
│  │  └───────────┘  │  │  └───────────┘  │  │  └───────────┘  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
│                                                                 │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │  skill-llm-*    │  │  skill-vfs-*    │  │skill-knowledge  │ │
│  │  (驱动模块)     │  │  (驱动模块)     │  │  (能力模块)     │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 五、模块安装策略

### 5.1 内置模块 (builtin)

内置模块随 MVP 工程一起发布，无需安装：

| 模块 | 来源 | 说明 |
|------|------|------|
| auth | skill-common | 认证授权 |
| menu | skill-common | 菜单系统 |
| config | skill-common | 配置管理 |
| org-management | skill-common | 组织管理 |
| user-management | skill-common | 用户管理 |

### 5.2 可安装模块 (installable)

通过 capability-discovery 页面安装：

| 模块 | 来源Skill | 安装条件 |
|------|-----------|----------|
| capability-discovery | skill-scene | 依赖 auth, menu |
| scene-management | skill-scene | 依赖 auth, config |
| llm-config | skill-scene | 依赖 auth, config |
| template-management | skill-scene | 依赖 scene-management |
| knowledge-base | skill-knowledge | 依赖 llm-config |

### 5.3 驱动模块 (driver)

按需安装，提供特定功能：

| 模块 | 来源Skill | 安装条件 |
|------|-----------|----------|
| llm-deepseek | skill-llm-deepseek | 依赖 llm-config |
| llm-openai | skill-llm-openai | 依赖 llm-config |
| vfs-local | skill-vfs-local | 无依赖 |
| vfs-s3 | skill-vfs-s3 | 依赖 vfs-local |

## 六、模块版本兼容性

| MVP版本 | 支持的Skill版本 | 说明 |
|---------|-----------------|------|
| 2.3.1 | skill-* >= 2.3.1 | 完全兼容 |
| 2.3.0 | skill-* >= 2.3.0 | 基本兼容 |
| < 2.3.0 | 不支持 | 需要升级 |

## 七、模块注册表

MVP 维护一个模块注册表，记录已安装模块：

```json
{
  "registry": {
    "version": "1.0",
    "installed": [
      {
        "id": "auth",
        "version": "2.3.1",
        "source": "builtin",
        "status": "active",
        "installedAt": "2024-01-01T00:00:00Z"
      },
      {
        "id": "capability-discovery",
        "version": "2.3.1",
        "source": "skill-scene",
        "status": "active",
        "installedAt": "2024-03-13T10:00:00Z"
      }
    ]
  }
}
```
