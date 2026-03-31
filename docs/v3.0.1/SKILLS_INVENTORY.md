# Ooder Skills 完整清单

> 版本: 3.0.1 | 更新日期: 2026-03-30

## 一、Skills 总览

| 分类 | 数量 | 开放批次 | 说明 |
|------|------|----------|------|
| **系统级 (_system)** | 3 | 第1批 | 核心基础设施 |
| **组织驱动 (_drivers/org)** | 4 | 第1批 | 组织架构数据源 |
| **存储驱动 (_drivers/vfs)** | 5 | 第1批 | 文件存储驱动 |
| **LLM驱动 (_drivers/llm)** | 6 | 第1批 | 大语言模型驱动 |
| **IM驱动 (_drivers/im)** | 3 | 第2批 | 即时通讯驱动 |
| **支付驱动 (_drivers/payment)** | 3 | 第3批 | 支付渠道驱动 |
| **媒体驱动 (_drivers/media)** | 5 | 第3批 | 自媒体平台驱动 |
| **能力服务** | 15 | 第2批 | 通用能力服务 |
| **工具服务** | 8 | 第2批 | 通用工具服务 |
| **场景服务** | 15 | 第3批 | 业务场景服务 |

**总计: 67 个 Skills**

---

## 二、分批开放策略

### 第1批：核心基础设施 (立即开放)

**开放原则**: 系统运行必需，稳定性高，文档完善

| Skill ID | 名称 | 分类 | 形式 | 状态 |
|----------|------|------|------|------|
| skill-common | 公共基础服务 | _system | PROVIDER | ✅ 稳定 |
| skill-protocol | 协议服务 | _system | PROVIDER | ✅ 稳定 |
| skill-org-base | 本地组织服务 | _drivers/org | DRIVER | ✅ 稳定 |
| skill-org-ldap | LDAP组织服务 | _drivers/org | DRIVER | ✅ 稳定 |
| skill-org-dingding | 钉钉组织服务 | _drivers/org | DRIVER | ✅ 稳定 |
| skill-org-feishu | 飞书组织服务 | _drivers/org | DRIVER | ✅ 稳定 |
| skill-org-wecom | 企业微信组织服务 | _drivers/org | DRIVER | ✅ 稳定 |
| skill-vfs-base | 存储基础服务 | _drivers/vfs | DRIVER | ✅ 稳定 |
| skill-vfs-local | 本地存储服务 | _drivers/vfs | DRIVER | ✅ 稳定 |
| skill-vfs-database | 数据库存储服务 | _drivers/vfs | DRIVER | ✅ 稳定 |
| skill-vfs-minio | MinIO存储服务 | _drivers/vfs | DRIVER | ✅ 稳定 |
| skill-vfs-oss | 阿里云OSS存储 | _drivers/vfs | DRIVER | ✅ 稳定 |
| skill-vfs-s3 | S3存储服务 | _drivers/vfs | DRIVER | ✅ 稳定 |
| skill-llm-deepseek | DeepSeek LLM | _drivers/llm | DRIVER | ✅ 稳定 |
| skill-llm-openai | OpenAI LLM | _drivers/llm | DRIVER | ✅ 稳定 |
| skill-llm-qianwen | 通义千问 LLM | _drivers/llm | DRIVER | ✅ 稳定 |
| skill-llm-ollama | Ollama LLM | _drivers/llm | DRIVER | ✅ 稳定 |
| skill-llm-volcengine | 火山引擎 LLM | _drivers/llm | DRIVER | ✅ 稳定 |
| skill-llm-baidu | 百度文心 LLM | _drivers/llm | DRIVER | ✅ 稳定 |

**第1批小计: 19 个**

---

### 第2批：能力与工具 (条件开放)

**开放原则**: 业务增强能力，需要一定配置

| Skill ID | 名称 | 分类 | 形式 | 状态 |
|----------|------|------|------|------|
| skill-im-dingding | 钉钉IM服务 | _drivers/im | DRIVER | ✅ 稳定 |
| skill-im-feishu | 飞书IM服务 | _drivers/im | DRIVER | ✅ 稳定 |
| skill-im-wecom | 企业微信IM服务 | _drivers/im | DRIVER | ✅ 稳定 |
| skill-llm-chat | LLM对话服务 | capabilities/llm | PROVIDER | ✅ 稳定 |
| skill-llm-config-manager | LLM配置管理 | capabilities/llm | PROVIDER | ✅ 稳定 |
| skill-user-auth | 用户认证服务 | capabilities/auth | PROVIDER | ✅ 稳定 |
| skill-monitor | 系统监控服务 | capabilities/monitor | PROVIDER | ⚠️ 测试中 |
| skill-health | 健康检查服务 | capabilities/monitor | PROVIDER | ⚠️ 测试中 |
| skill-network | 网络管理服务 | capabilities/monitor | PROVIDER | ⚠️ 测试中 |
| skill-cmd-service | 命令服务 | capabilities/monitor | PROVIDER | ⚠️ 测试中 |
| skill-remote-terminal | 远程终端服务 | capabilities/monitor | PROVIDER | ⚠️ 测试中 |
| skill-res-service | 资源服务 | capabilities/monitor | PROVIDER | ⚠️ 测试中 |
| skill-openwrt | OpenWRT服务 | capabilities/infrastructure | PROVIDER | ⚠️ 测试中 |
| skill-group | 群组服务 | capabilities/communication | PROVIDER | ✅ 稳定 |
| skill-im | IM服务 | capabilities/communication | PROVIDER | ✅ 稳定 |
| skill-mqtt | MQTT服务 | capabilities/communication | PROVIDER | ✅ 稳定 |
| skill-msg | 消息服务 | capabilities/communication | PROVIDER | ✅ 稳定 |
| skill-notify | 通知服务 | capabilities/communication | PROVIDER | ✅ 稳定 |
| skill-email | 邮件服务 | capabilities/communication | PROVIDER | ✅ 稳定 |
| skill-notification | 通知推送服务 | capabilities/communication | PROVIDER | ✅ 稳定 |
| skill-scheduler-quartz | Quartz调度服务 | capabilities/scheduler | PROVIDER | ✅ 稳定 |
| skill-task | 任务服务 | capabilities/scheduler | PROVIDER | ✅ 稳定 |
| skill-search | 搜索服务 | capabilities/search | PROVIDER | ✅ 稳定 |
| skill-calendar | 日历服务 | tools | PROVIDER | ✅ 稳定 |
| skill-msg-push | 消息推送服务 | tools | PROVIDER | ✅ 稳定 |
| skill-todo-sync | 待办同步服务 | tools | PROVIDER | ✅ 稳定 |
| skill-doc-collab | 文档协作服务 | tools | PROVIDER | ✅ 稳定 |
| skill-agent-cli | Agent CLI服务 | tools | PROVIDER | ✅ 稳定 |
| skill-document-processor | 文档处理服务 | tools | PROVIDER | ⚠️ 测试中 |
| skill-market | 技能市场服务 | tools | PROVIDER | ⚠️ 测试中 |
| skill-report | 报告服务 | tools | PROVIDER | ⚠️ 测试中 |
| skill-share | 分享服务 | tools | PROVIDER | ⚠️ 测试中 |

**第2批小计: 32 个**

---

### 第3批：业务场景与扩展 (限制开放)

**开放原则**: 特定业务场景，需要定制化配置

| Skill ID | 名称 | 分类 | 形式 | 状态 |
|----------|------|------|------|------|
| skill-payment-alipay | 支付宝支付 | _drivers/payment | DRIVER | ⚠️ 测试中 |
| skill-payment-wechat | 微信支付 | _drivers/payment | DRIVER | ⚠️ 测试中 |
| skill-payment-unionpay | 银联支付 | _drivers/payment | DRIVER | ⚠️ 测试中 |
| skill-media-wechat | 微信公众号 | _drivers/media | DRIVER | ⚠️ 测试中 |
| skill-media-weibo | 微博 | _drivers/media | DRIVER | ⚠️ 测试中 |
| skill-media-toutiao | 今日头条 | _drivers/media | DRIVER | ⚠️ 测试中 |
| skill-media-xiaohongshu | 小红书 | _drivers/media | DRIVER | ⚠️ 测试中 |
| skill-media-zhihu | 知乎 | _drivers/media | DRIVER | ⚠️ 测试中 |
| skill-platform-bind | 平台绑定场景 | scenes | SCENE | ✅ 稳定 |
| skill-recruitment-management | 招聘管理场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-real-estate-form | 房地产表单场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-project-knowledge | 项目知识场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-onboarding-assistant | 入职助手场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-knowledge-share | 知识分享场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-knowledge-management | 知识管理场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-document-assistant | 文档助手场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-daily-report | 日报场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-meeting-minutes | 会议纪要场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-knowledge-qa | 知识问答场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-collaboration | 协作场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-business | 业务场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-recording-qa | 录音质检场景 | scenes | SCENE | ⚠️ 测试中 |
| skill-approval-form | 审批表单场景 | scenes | SCENE | ⚠️ 测试中 |

**第3批小计: 23 个**

---

## 三、分类详细清单

### 3.1 系统级 (_system)

```
skills/_system/
├── skill-common/           # 公共基础服务
├── skill-llm-chat/         # LLM对话服务
└── skill-management/       # 技能管理服务
```

### 3.2 驱动级 (_drivers)

```
skills/_drivers/
├── org/                    # 组织驱动
│   ├── skill-org-base/     # 本地组织
│   ├── skill-org-ldap/     # LDAP
│   ├── skill-org-dingding/ # 钉钉
│   ├── skill-org-feishu/   # 飞书
│   └── skill-org-wecom/    # 企业微信
├── vfs/                    # 存储驱动
│   ├── skill-vfs-base/     # 存储基础
│   ├── skill-vfs-local/    # 本地存储
│   ├── skill-vfs-database/ # 数据库存储
│   ├── skill-vfs-minio/    # MinIO
│   ├── skill-vfs-oss/      # 阿里云OSS
│   └── skill-vfs-s3/       # S3
├── llm/                    # LLM驱动
│   ├── skill-llm-deepseek/ # DeepSeek
│   ├── skill-llm-openai/   # OpenAI
│   ├── skill-llm-qianwen/  # 通义千问
│   ├── skill-llm-ollama/   # Ollama
│   ├── skill-llm-volcengine/ # 火山引擎
│   └── skill-llm-baidu/    # 百度文心
├── im/                     # IM驱动
│   ├── skill-im-dingding/  # 钉钉IM
│   ├── skill-im-feishu/    # 飞书IM
│   └── skill-im-wecom/     # 企业微信IM
├── payment/                # 支付驱动
│   ├── skill-payment-alipay/ # 支付宝
│   ├── skill-payment-wechat/ # 微信支付
│   └── skill-payment-unionpay/ # 银联
└── media/                  # 媒体驱动
    ├── skill-media-wechat/ # 微信公众号
    ├── skill-media-weibo/  # 微博
    ├── skill-media-toutiao/ # 今日头条
    ├── skill-media-xiaohongshu/ # 小红书
    └── skill-media-zhihu/  # 知乎
```

### 3.3 能力服务

```
skills/capabilities/
├── llm/                    # LLM能力
│   ├── skill-llm-chat/     # 对话服务
│   └── skill-llm-config-manager/ # 配置管理
├── auth/                   # 认证能力
│   └── skill-user-auth/    # 用户认证
├── monitor/                # 监控能力
│   ├── skill-monitor/      # 系统监控
│   ├── skill-health/       # 健康检查
│   ├── skill-network/      # 网络管理
│   ├── skill-cmd-service/  # 命令服务
│   ├── skill-remote-terminal/ # 远程终端
│   └── skill-res-service/  # 资源服务
├── communication/          # 通讯能力
│   ├── skill-group/        # 群组服务
│   ├── skill-im/           # IM服务
│   ├── skill-mqtt/         # MQTT
│   ├── skill-msg/          # 消息服务
│   ├── skill-notify/       # 通知服务
│   ├── skill-email/        # 邮件服务
│   └── skill-notification/ # 通知推送
├── scheduler/              # 调度能力
│   ├── skill-scheduler-quartz/ # Quartz调度
│   └── skill-task/         # 任务服务
├── search/                 # 搜索能力
│   └── skill-search/       # 搜索服务
└── infrastructure/         # 基础设施
    └── skill-openwrt/      # OpenWRT
```

### 3.4 工具服务

```
skills/tools/
├── skill-calendar/         # 日历服务
├── skill-msg-push/         # 消息推送
├── skill-todo-sync/        # 待办同步
├── skill-doc-collab/       # 文档协作
├── skill-agent-cli/        # Agent CLI
├── skill-document-processor/ # 文档处理
├── skill-market/           # 技能市场
├── skill-report/           # 报告服务
└── skill-share/            # 分享服务
```

### 3.5 场景服务

```
skills/scenes/
├── skill-platform-bind/    # 平台绑定
├── skill-recruitment-management/ # 招聘管理
├── skill-real-estate-form/ # 房地产表单
├── skill-project-knowledge/ # 项目知识
├── skill-onboarding-assistant/ # 入职助手
├── skill-knowledge-share/  # 知识分享
├── skill-knowledge-management/ # 知识管理
├── skill-document-assistant/ # 文档助手
├── daily-report/           # 日报
├── skill-meeting-minutes/  # 会议纪要
├── skill-knowledge-qa/     # 知识问答
├── skill-collaboration/    # 协作场景
├── skill-business/         # 业务场景
├── skill-recording-qa/     # 录音质检
└── skill-approval-form/    # 审批表单
```

---

## 四、开放状态说明

| 状态 | 图标 | 说明 |
|------|------|------|
| **稳定** | ✅ | 已测试，可用于生产环境 |
| **测试中** | ⚠️ | 功能完整，正在测试验证 |
| **开发中** | 🔧 | 功能开发中，不建议使用 |
| **计划中** | 📋 | 已规划，尚未开发 |

---

## 五、版本信息

- **当前版本**: 3.0.1
- **发布日期**: 2026-03-29
- **维护团队**: OoderAgent Team

---

## 六、相关文档

| 文档 | 路径 |
|------|------|
| 分类定义 | `e:\github\ooder-skills\skill-index\categories.yaml` |
| 技能索引 | `e:\github\ooder-skills\skill-index\skills\*.yaml` |
| 主索引文件 | `e:\github\ooder-skills\skill-index.yaml` |
