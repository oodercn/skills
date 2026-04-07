# OODER SKILLS 分类统计报告

**生成时间**: 2026-04-06  
**统计范围**: e:\github\ooder-skills\skills 目录  
**报告用途**: 为测试团队提供完整的skills分类信息，便于测试比对

---

## 一、总体统计概览

### 1.1 目录结构统计

| 目录名称 | 说明 | Skill数量 |
|---------|------|----------|
| _system | 系统级服务 | 20+ |
| _drivers | 驱动适配器 | 30+ |
| _business | 业务服务 | 4 |
| capabilities | 能力组件 | 20+ |
| tools | 工具服务 | 10+ |
| scenes | 场景应用 | 15+ |

**总计**: 约100+ skills

---

## 二、按 metadata.category 分类统计

### 2.1 系统类 (sys)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-tenant | 多租户管理服务 | e:\github\ooder-skills\skills\_system\skill-tenant\skill.yaml |
| skill-rag | RAG检索增强服务 | e:\github\ooder-skills\skills\_system\skill-rag\skill.yaml |
| skill-common | 通用工具集 | e:\github\ooder-skills\skills\_system\skill-common\skill.yaml |
| skill-agent | Agent管理服务 | e:\github\ooder-skills\skills\_system\skill-agent\skill.yaml |
| skill-auth | 认证服务 | e:\github\ooder-skills\skills\_system\skill-auth\skill.yaml |
| skill-capability | 能力管理服务 | e:\github\ooder-skills\skills\_system\skill-capability\skill.yaml |
| skill-dict | 字典管理服务 | e:\github\ooder-skills\skills\_system\skill-dict\skill.yaml |
| skill-audit | 日志审计服务 | e:\github\ooder-skills\skills\_system\skill-audit\skill.yaml |
| skill-discovery | 能力发现服务 | e:\github\ooder-skills\skills\_system\skill-discovery\skill.yaml |
| skill-org | 组织管理 | e:\github\ooder-skills\skills\_system\skill-org\skill.yaml |
| skill-role | 角色权限服务 | e:\github\ooder-skills\skills\_system\skill-role\skill.yaml |
| skill-scene | 场景管理服务 | e:\github\ooder-skills\skills\_system\skill-scene\skill.yaml |
| skill-protocol | 协议管理技能 | e:\github\ooder-skills\skills\_system\skill-protocol\skill.yaml |
| skill-install | 能力安装服务 | e:\github\ooder-skills\skills\_system\skill-install\skill.yaml |
| skill-knowledge | 知识管理服务 | e:\github\ooder-skills\skills\_system\skill-knowledge\skill.yaml |
| skill-menu | 菜单管理服务 | e:\github\ooder-skills\skills\_system\skill-menu\skill.yaml |
| skill-llm-chat | LLM对话服务 | e:\github\ooder-skills\skills\_system\skill-llm-chat\skill.yaml |
| skill-management | 管理服务 | e:\github\ooder-skills\skills\_system\skill-management\skill.yaml |
| skills-bpm-demo | BPM演示 | e:\github\ooder-skills\skills\_system\skills-bpm-demo\skill.yaml |

**小计**: 19个

### 2.2 组织管理类 (org)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-org-local | 本地组织管理服务 | e:\github\ooder-skills\skills\_drivers\org\skill-org-base\skill.yaml |
| skill-org-wecom | WeCom Organization Service | e:\github\ooder-skills\skills\_drivers\org\skill-org-wecom\skill.yaml |
| skill-org-ldap | LDAP Organization | e:\github\ooder-skills\skills\_drivers\org\skill-org-ldap\skill.yaml |
| skill-org-dingding | DingTalk Organization Service | e:\github\ooder-skills\skills\_drivers\org\skill-org-dingding\skill.yaml |
| skill-org-feishu | 飞书组织管理 | e:\github\ooder-skills\skills\_drivers\org\skill-org-feishu\skill.yaml |
| skill-user-auth | 用户认证服务 | e:\github\ooder-skills\skills\capabilities\auth\skill-user-auth\skill.yaml |

**小计**: 6个

### 2.3 业务类 (biz)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-payment-wechat | 微信支付服务 | e:\github\ooder-skills\skills\_drivers\payment\skill-payment-wechat\skill.yaml |
| skill-payment-unionpay | 银联支付服务 | e:\github\ooder-skills\skills\_drivers\payment\skill-payment-unionpay\skill.yaml |
| skill-payment-alipay | 支付宝支付服务 | e:\github\ooder-skills\skills\_drivers\payment\skill-payment-alipay\skill.yaml |
| skill-media-zhihu | 知乎发布服务 | e:\github\ooder-skills\skills\_drivers\media\skill-media-zhihu\skill.yaml |
| skill-media-xiaohongshu | 小红书发布服务 | e:\github\ooder-skills\skills\_drivers\media\skill-media-xiaohongshu\skill.yaml |
| skill-media-weibo | 微博发布服务 | e:\github\ooder-skills\skills\_drivers\media\skill-media-weibo\skill.yaml |
| skill-media-wechat | 微信公众号发布服务 | e:\github\ooder-skills\skills\_drivers\media\skill-media-wechat\skill.yaml |
| skill-media-toutiao | 头条发布服务 | e:\github\ooder-skills\skills\_drivers\media\skill-media-toutiao\skill.yaml |
| skill-report | 报表服务 | e:\github\ooder-skills\skills\tools\skill-report\skill.yaml |
| skill-share | 分享服务 | e:\github\ooder-skills\skills\tools\skill-share\skill.yaml |
| skill-market | 应用市场 | e:\github\ooder-skills\skills\tools\skill-market\skill.yaml |
| skill-todo-sync | 待办同步 | e:\github\ooder-skills\skills\tools\skill-todo-sync\skill.yaml |
| skill-msg-push | 消息推送 | e:\github\ooder-skills\skills\tools\skill-msg-push\skill.yaml |
| skill-document-processor | 文档处理器 | e:\github\ooder-skills\skills\tools\skill-document-processor\skill.yaml |
| skill-doc-collab | 文档协作 | e:\github\ooder-skills\skills\tools\skill-doc-collab\skill.yaml |
| skill-calendar | 日程管理服务 | e:\github\ooder-skills\skills\tools\skill-calendar\skill.yaml |
| skill-agent-cli | Agent命令行 | e:\github\ooder-skills\skills\tools\skill-agent-cli\skill.yaml |
| skill-approval-form | 审批表单系统 | e:\github\ooder-skills\skills\scenes\skill-approval-form\skill.yaml |
| skill-business | 业务场景服务 | e:\github\ooder-skills\skills\scenes\skill-business\skill.yaml |

**小计**: 19个

### 2.4 虚拟文件系统类 (vfs)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-vfs-local | Local VFS Service | e:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-local\skill.yaml |
| skill-vfs-database | Database VFS Service | e:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-database\skill.yaml |
| skill-vfs-base | VFS基础服务 | e:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-base\skill.yaml |
| skill-vfs-s3 | AWS S3 VFS Service | e:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-s3\skill.yaml |
| skill-vfs-oss | 阿里云OSS VFS Service | e:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-oss\skill.yaml |
| skill-vfs-minio | MinIO VFS Service | e:\github\ooder-skills\skills\_drivers\vfs\skill-vfs-minio\skill.yaml |
| skills-vfs-demo | VFS演示 | e:\github\ooder-skills\skills\_drivers\vfs\skills-vfs-demo\skill.yaml |

**小计**: 7个

### 2.5 LLM类 (llm)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-llm-base | LLM Base Driver | e:\github\ooder-skills\skills\_drivers\llm\skill-llm-base\skill.yaml |
| skill-llm-volcengine | VolcEngine (Doubao) LLM Provider | e:\github\ooder-skills\skills\_drivers\llm\skill-llm-volcengine\src\main\resources\skill.yaml |
| skill-llm-qianwen | Qianwen (Tongyi) LLM Provider | e:\github\ooder-skills\skills\_drivers\llm\skill-llm-qianwen\src\main\resources\skill.yaml |
| skill-llm-openai | OpenAI LLM Provider | e:\github\ooder-skills\skills\_drivers\llm\skill-llm-openai\src\main\resources\skill.yaml |
| skill-llm-ollama | Ollama LLM Provider | e:\github\ooder-skills\skills\_drivers\llm\skill-llm-ollama\src\main\resources\skill.yaml |
| skill-llm-monitor | LLM监控服务 | e:\github\ooder-skills\skills\_drivers\llm\skill-llm-monitor\skill.yaml |
| skill-llm-deepseek | DeepSeek LLM Provider | e:\github\ooder-skills\skills\_drivers\llm\skill-llm-deepseek\src\main\resources\skill.yaml |
| skill-llm-baidu | 百度千帆LLM Provider | e:\github\ooder-skills\skills\_drivers\llm\skill-llm-baidu\skill.yaml |
| skill-llm-config | LLM配置服务 | e:\github\ooder-skills\skills\capabilities\llm\skill-llm-config\skill.yaml |
| skill-llm-config-manager | LLM配置管理器 | e:\github\ooder-skills\skills\capabilities\llm\skill-llm-config-manager\skill.yaml |

**小计**: 10个

### 2.6 消息类 (msg)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-im-wecom | WeCom IM Service | e:\github\ooder-skills\skills\_drivers\im\skill-im-wecom\skill.yaml |
| skill-im-feishu | Feishu IM Service | e:\github\ooder-skills\skills\_drivers\im\skill-im-feishu\skill.yaml |
| skill-im-dingding | DingTalk IM Service | e:\github\ooder-skills\skills\_drivers\im\skill-im-dingding\skill.yaml |
| skill-email | 邮件服务 | e:\github\ooder-skills\skills\capabilities\communication\skill-email\skill.yaml |
| skill-notify | 通知服务 | e:\github\ooder-skills\skills\capabilities\communication\skill-notify\skill.yaml |
| skill-notification | 通知服务 | e:\github\ooder-skills\skills\capabilities\communication\skill-notification\skill.yaml |
| skill-msg | 消息服务 | e:\github\ooder-skills\skills\capabilities\communication\skill-msg\skill.yaml |
| skill-group | 群组服务 | e:\github\ooder-skills\skills\capabilities\communication\skill-group\skill.yaml |
| skill-im | IM服务 | e:\github\ooder-skills\skills\capabilities\communication\skill-im\skill.yaml |
| skill-mqtt | MQTT服务 | e:\github\ooder-skills\skills\capabilities\communication\skill-mqtt\skill.yaml |

**小计**: 10个

### 2.7 知识类 (knowledge)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-search | 搜索服务 | e:\github\ooder-skills\skills\capabilities\search\skill-search\skill.yaml |
| skill-knowledge-qa | 知识问答场景能力 | e:\github\ooder-skills\skills\scenes\skill-knowledge-qa\skill.yaml |
| skill-knowledge-management | 知识管理 | e:\github\ooder-skills\skills\scenes\skill-knowledge-management\skill.yaml |
| skill-knowledge-share | 知识分享 | e:\github\ooder-skills\skills\scenes\skill-knowledge-share\skill.yaml |
| skill-project-knowledge | 项目知识 | e:\github\ooder-skills\skills\scenes\skill-project-knowledge\skill.yaml |

**小计**: 5个

### 2.8 其他分类

#### 2.8.1 监控类 (monitor)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-remote-terminal | 远程终端 | e:\github\ooder-skills\skills\capabilities\monitor\skill-remote-terminal\skill.yaml |
| skill-cmd-service | 命令服务 | e:\github\ooder-skills\skills\capabilities\monitor\skill-cmd-service\skill.yaml |
| skill-res-service | 资源服务 | e:\github\ooder-skills\skills\capabilities\monitor\skill-res-service\skill.yaml |
| skill-network | 网络监控 | e:\github\ooder-skills\skills\capabilities\monitor\skill-network\skill.yaml |
| skill-monitor | 监控服务 | e:\github\ooder-skills\skills\capabilities\monitor\skill-monitor\skill.yaml |
| skill-health | 健康检查 | e:\github\ooder-skills\skills\capabilities\monitor\skill-health\skill.yaml |

**小计**: 6个

#### 2.8.2 调度类 (scheduler)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-task | 任务管理服务 | e:\github\ooder-skills\skills\capabilities\scheduler\skill-task\skill.yaml |
| skill-scheduler-quartz | Quartz调度服务 | e:\github\ooder-skills\skills\capabilities\scheduler\skill-scheduler-quartz\skill.yaml |

**小计**: 2个

#### 2.8.3 场景类 (scenes)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-real-estate-form | 房地产表单 | e:\github\ooder-skills\skills\scenes\skill-real-estate-form\skill.yaml |
| skill-onboarding-assistant | 入职助手 | e:\github\ooder-skills\skills\scenes\skill-onboarding-assistant\skill.yaml |
| skill-document-assistant | 文档助手 | e:\github\ooder-skills\skills\scenes\skill-document-assistant\skill.yaml |
| skill-recruitment-management | 招聘管理 | e:\github\ooder-skills\skills\scenes\skill-recruitment-management\skill.yaml |
| skill-recording-qa | 录音问答 | e:\github\ooder-skills\skills\scenes\skill-recording-qa\skill.yaml |
| skill-platform-bind | 平台绑定 | e:\github\ooder-skills\skills\scenes\skill-platform-bind\skill.yaml |
| skill-meeting-minutes | 会议纪要 | e:\github\ooder-skills\skills\scenes\skill-meeting-minutes\skill.yaml |
| skill-collaboration | 协作服务 | e:\github\ooder-skills\skills\scenes\skill-collaboration\skill.yaml |
| daily-report | 日报 | e:\github\ooder-skills\skills\scenes\daily-report\skill.yaml |

**小计**: 9个

#### 2.8.4 基础设施类 (infrastructure)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-openwrt | OpenWRT管理 | e:\github\ooder-skills\skills\capabilities\infrastructure\skill-openwrt\skill.yaml |

**小计**: 1个

#### 2.8.5 工作流类 (_drivers/bpm)

| Skill ID | 名称 | 文件路径 |
|----------|------|---------|
| skill-bpm | BPM Workflow Server | e:\github\ooder-skills\skills\_drivers\bpm\skill-bpm\skill.yaml |

**小计**: 1个

---

## 三、按 spec.skillForm 分类统计

### 3.1 PROVIDER (服务提供者)

**说明**: 提供基础能力和服务的skill

**数量**: 约60+

**主要类型**:
- 系统服务 (sys)
- 组织管理 (org)
- 消息服务 (msg)
- 存储服务 (vfs)
- LLM服务 (llm)
- 监控服务 (monitor)
- 调度服务 (scheduler)

### 3.2 DRIVER (驱动适配器)

**说明**: 第三方平台和服务的驱动适配

**数量**: 约20+

**主要类型**:
- LLM Provider驱动 (llm)
- IM平台驱动 (im)
- 组织系统驱动 (org)
- 支付平台驱动 (payment)
- 媒体平台驱动 (media)
- 存储驱动 (vfs)

### 3.3 SCENE (场景应用)

**说明**: 面向具体业务场景的应用

**数量**: 约15+

**主要类型**:
- 知识管理场景
- 审批流程场景
- 业务管理场景
- 协作场景

---

## 四、按 spec.skillCategory 分类统计

### 4.1 SERVICE (服务类)

**数量**: 约50+

**说明**: 提供核心业务服务

### 4.2 LLM (大语言模型类)

**数量**: 约10+

**说明**: LLM相关服务和驱动

### 4.3 WORKFLOW (工作流类)

**数量**: 约3+

**说明**: 工作流和流程管理

### 4.4 DATA (数据类)

**数量**: 约7+

**说明**: 数据存储和管理

---

## 五、按 spec.capability.category 分类统计

### 5.1 SYS (系统)

**说明**: 系统级核心能力

**Skills**: skill-tenant, skill-auth, skill-capability, skill-dict, skill-audit, skill-discovery, skill-org, skill-role, skill-scene, skill-install等

**数量**: 约15+

### 5.2 ORG (组织)

**说明**: 组织管理能力

**Skills**: skill-org-local, skill-org-wecom, skill-org-ldap等

**数量**: 约6+

### 5.3 BIZ (业务)

**说明**: 业务能力

**Skills**: skill-context, skill-payment-*, skill-media-*等

**数量**: 约15+

### 5.4 IM (即时通讯)

**说明**: 即时通讯能力

**Skills**: skill-im-wecom, skill-im-feishu, skill-im-dingding等

**数量**: 约3+

### 5.5 SEARCH (搜索)

**说明**: 搜索能力

**Skills**: skill-search

**数量**: 约1+

### 5.6 SCHED (调度)

**说明**: 任务调度能力

**Skills**: skill-task, skill-scheduler-quartz

**数量**: 约2+

### 5.7 AUTH (认证)

**说明**: 认证授权能力

**Skills**: skill-user-auth

**数量**: 约1+

### 5.8 COMM (通讯)

**说明**: 通讯能力

**Skills**: skill-email, skill-notify, skill-msg, skill-group, skill-im, skill-mqtt

**数量**: 约6+

### 5.9 UTIL (工具)

**说明**: 工具类能力

**Skills**: skill-report, skill-share

**数量**: 约2+

---

## 六、按目录结构详细清单

### 6.1 _system 目录 (系统服务)

```
e:\github\ooder-skills\skills\_system\
├── skill-agent\skill.yaml              # Agent管理服务
├── skill-auth\skill.yaml               # 认证服务
├── skill-capability\skill.yaml         # 能力管理服务
├── skill-common\skill.yaml             # 通用工具集
├── skill-dict\skill.yaml               # 字典管理服务
├── skill-discovery\skill.yaml          # 能力发现服务
├── skill-install\skill.yaml            # 能力安装服务
├── skill-knowledge\skill.yaml          # 知识管理服务
├── skill-llm-chat\skill.yaml           # LLM对话服务
├── skill-management\skill.yaml         # 管理服务
├── skill-menu\skill.yaml               # 菜单管理服务
├── skill-org\skill.yaml                # 组织管理
├── skill-protocol\skill.yaml           # 协议管理技能
├── skill-rag\skill.yaml                # RAG检索增强服务
├── skill-role\skill.yaml               # 角色权限服务
├── skill-scene\skill.yaml              # 场景管理服务
├── skill-tenant\skill.yaml             # 多租户管理服务
├── skill-audit\skill.yaml              # 日志审计服务
└── skills-bpm-demo\skill.yaml          # BPM演示
```

### 6.2 _drivers 目录 (驱动适配器)

```
e:\github\ooder-skills\skills\_drivers\
├── bpm\
│   └── skill-bpm\skill.yaml            # BPM工作流服务
├── im\
│   ├── skill-im-dingding\skill.yaml    # 钉钉IM服务
│   ├── skill-im-feishu\skill.yaml      # 飞书IM服务
│   └── skill-im-wecom\skill.yaml       # 企业微信IM服务
├── llm\
│   ├── skill-llm-base\skill.yaml       # LLM基础驱动
│   ├── skill-llm-baidu\skill.yaml      # 百度千帆LLM
│   ├── skill-llm-deepseek\...\skill.yaml # DeepSeek LLM
│   ├── skill-llm-monitor\skill.yaml    # LLM监控服务
│   ├── skill-llm-ollama\...\skill.yaml # Ollama LLM
│   ├── skill-llm-openai\...\skill.yaml # OpenAI LLM
│   ├── skill-llm-qianwen\...\skill.yaml # 通义千问LLM
│   └── skill-llm-volcengine\...\skill.yaml # 火山引擎LLM
├── media\
│   ├── skill-media-toutiao\skill.yaml  # 头条发布服务
│   ├── skill-media-wechat\skill.yaml   # 微信公众号发布
│   ├── skill-media-weibo\skill.yaml    # 微博发布服务
│   ├── skill-media-xiaohongshu\skill.yaml # 小红书发布服务
│   └── skill-media-zhihu\skill.yaml    # 知乎发布服务
├── org\
│   ├── skill-org-base\skill.yaml       # 本地组织管理
│   ├── skill-org-dingding\skill.yaml   # 钉钉组织管理
│   ├── skill-org-feishu\skill.yaml     # 飞书组织管理
│   ├── skill-org-ldap\skill.yaml       # LDAP组织管理
│   └── skill-org-wecom\skill.yaml      # 企业微信组织管理
├── payment\
│   ├── skill-payment-alipay\skill.yaml # 支付宝支付服务
│   ├── skill-payment-unionpay\skill.yaml # 银联支付服务
│   └── skill-payment-wechat\skill.yaml # 微信支付服务
└── vfs\
    ├── skill-vfs-base\skill.yaml       # VFS基础服务
    ├── skill-vfs-database\skill.yaml   # 数据库VFS服务
    ├── skill-vfs-local\skill.yaml      # 本地VFS服务
    ├── skill-vfs-minio\skill.yaml      # MinIO VFS服务
    ├── skill-vfs-oss\skill.yaml        # 阿里云OSS VFS服务
    ├── skill-vfs-s3\skill.yaml         # AWS S3 VFS服务
    └── skills-vfs-demo\skill.yaml      # VFS演示
```

### 6.3 _business 目录 (业务服务)

```
e:\github\ooder-skills\skills\_business\
├── skill-context\skill.yaml            # 上下文管理服务
├── skill-llm-config\skill.yaml         # LLM配置服务
├── skill-scenes\skill.yaml             # 场景服务
└── skill-selector\skill.yaml           # 选择器服务
```

### 6.4 capabilities 目录 (能力组件)

```
e:\github\ooder-skills\skills\capabilities\
├── auth\
│   └── skill-user-auth\skill.yaml      # 用户认证服务
├── communication\
│   ├── skill-email\skill.yaml          # 邮件服务
│   ├── skill-group\skill.yaml          # 群组服务
│   ├── skill-im\skill.yaml             # IM服务
│   ├── skill-msg\skill.yaml            # 消息服务
│   ├── skill-mqtt\skill.yaml           # MQTT服务
│   ├── skill-notification\skill.yaml   # 通知服务
│   └── skill-notify\skill.yaml         # 通知服务
├── infrastructure\
│   └── skill-openwrt\skill.yaml        # OpenWRT管理
├── llm\
│   ├── skill-llm-config\skill.yaml     # LLM配置服务
│   └── skill-llm-config-manager\skill.yaml # LLM配置管理器
├── monitor\
│   ├── skill-cmd-service\skill.yaml    # 命令服务
│   ├── skill-health\skill.yaml         # 健康检查
│   ├── skill-monitor\skill.yaml        # 监控服务
│   ├── skill-network\skill.yaml        # 网络监控
│   ├── skill-remote-terminal\skill.yaml # 远程终端
│   └── skill-res-service\skill.yaml    # 资源服务
├── scheduler\
│   ├── skill-scheduler-quartz\skill.yaml # Quartz调度服务
│   └── skill-task\skill.yaml           # 任务管理服务
├── scenes\
│   └── skill-scenes\skill.yaml         # 场景服务
└── search\
    └── skill-search\skill.yaml         # 搜索服务
```

### 6.5 tools 目录 (工具服务)

```
e:\github\ooder-skills\skills\tools\
├── skill-agent-cli\skill.yaml          # Agent命令行
├── skill-calendar\skill.yaml           # 日程管理服务
├── skill-doc-collab\skill.yaml         # 文档协作
├── skill-document-processor\skill.yaml # 文档处理器
├── skill-market\skill.yaml             # 应用市场
├── skill-msg-push\skill.yaml           # 消息推送
├── skill-report\skill.yaml             # 报表服务
├── skill-share\skill.yaml              # 分享服务
└── skill-todo-sync\skill.yaml          # 待办同步
```

### 6.6 scenes 目录 (场景应用)

```
e:\github\ooder-skills\skills\scenes\
├── daily-report\skill.yaml             # 日报
├── skill-approval-form\skill.yaml      # 审批表单系统
├── skill-business\skill.yaml           # 业务场景服务
├── skill-collaboration\skill.yaml      # 协作服务
├── skill-document-assistant\skill.yaml # 文档助手
├── skill-knowledge-management\skill.yaml # 知识管理
├── skill-knowledge-qa\skill.yaml       # 知识问答场景能力
├── skill-knowledge-share\skill.yaml    # 知识分享
├── skill-meeting-minutes\skill.yaml    # 会议纪要
├── skill-onboarding-assistant\skill.yaml # 入职助手
├── skill-platform-bind\skill.yaml      # 平台绑定
├── skill-project-knowledge\skill.yaml  # 项目知识
├── skill-real-estate-form\skill.yaml   # 房地产表单
├── skill-recording-qa\skill.yaml       # 录音问答
└── skill-recruitment-management\skill.yaml # 招聘管理
```

---

## 七、分类维度对比表

| 分类维度 | 主要类别 | 数量范围 | 说明 |
|---------|---------|---------|------|
| metadata.category | sys, org, biz, vfs, llm, msg, knowledge等 | 10+类别 | 业务分类 |
| spec.skillForm | PROVIDER, DRIVER, SCENE | 3类别 | 形态分类 |
| spec.skillCategory | SERVICE, LLM, WORKFLOW, DATA | 4+类别 | 功能分类 |
| spec.capability.category | SYS, ORG, BIZ, IM, SEARCH, SCHED, AUTH, COMM, UTIL | 9+类别 | 能力分类 |
| 目录结构 | _system, _drivers, _business, capabilities, tools, scenes | 6目录 | 组织分类 |

---

## 八、测试建议

### 8.1 测试覆盖建议

1. **系统服务测试** (_system)
   - 重点测试认证、授权、租户管理等核心功能
   - 验证系统服务的稳定性和性能

2. **驱动适配器测试** (_drivers)
   - 测试各平台API集成
   - 验证错误处理和重试机制
   - 测试配置管理和密钥安全

3. **能力组件测试** (capabilities)
   - 测试各能力模块的独立性
   - 验证能力间的依赖关系
   - 测试监控和日志功能

4. **工具服务测试** (tools)
   - 测试工具的易用性
   - 验证数据处理的准确性
   - 测试并发和性能

5. **场景应用测试** (scenes)
   - 测试端到端业务流程
   - 验证用户交互体验
   - 测试多角色协作

### 8.2 分类测试矩阵

| 测试类型 | _system | _drivers | capabilities | tools | scenes |
|---------|---------|----------|--------------|-------|--------|
| 单元测试 | ✓✓✓ | ✓✓ | ✓✓✓ | ✓✓ | ✓ |
| 集成测试 | ✓✓ | ✓✓✓ | ✓✓ | ✓✓ | ✓✓✓ |
| 端到端测试 | ✓ | ✓ | ✓ | ✓ | ✓✓✓ |
| 性能测试 | ✓✓ | ✓ | ✓ | ✓ | ✓ |
| 安全测试 | ✓✓✓ | ✓✓ | ✓✓ | ✓ | ✓ |

---

## 九、附录

### 9.1 完整Skill ID列表

```
skill-agent
skill-auth
skill-capability
skill-common
skill-dict
skill-discovery
skill-install
skill-knowledge
skill-llm-chat
skill-management
skill-menu
skill-org
skill-protocol
skill-rag
skill-role
skill-scene
skill-tenant
skill-audit
skills-bpm-demo
skill-bpm
skill-im-dingding
skill-im-feishu
skill-im-wecom
skill-llm-base
skill-llm-baidu
skill-llm-deepseek
skill-llm-monitor
skill-llm-ollama
skill-llm-openai
skill-llm-qianwen
skill-llm-volcengine
skill-media-toutiao
skill-media-wechat
skill-media-weibo
skill-media-xiaohongshu
skill-media-zhihu
skill-org-base
skill-org-dingding
skill-org-feishu
skill-org-ldap
skill-org-wecom
skill-payment-alipay
skill-payment-unionpay
skill-payment-wechat
skill-vfs-base
skill-vfs-database
skill-vfs-local
skill-vfs-minio
skill-vfs-oss
skill-vfs-s3
skills-vfs-demo
skill-context
skill-llm-config
skill-scenes
skill-selector
skill-user-auth
skill-email
skill-group
skill-im
skill-msg
skill-mqtt
skill-notification
skill-notify
skill-openwrt
skill-llm-config-manager
skill-cmd-service
skill-health
skill-monitor
skill-network
skill-remote-terminal
skill-res-service
skill-scheduler-quartz
skill-task
skill-search
skill-agent-cli
skill-calendar
skill-doc-collab
skill-document-processor
skill-market
skill-msg-push
skill-report
skill-share
skill-todo-sync
daily-report
skill-approval-form
skill-business
skill-collaboration
skill-document-assistant
skill-knowledge-management
skill-knowledge-qa
skill-knowledge-share
skill-meeting-minutes
skill-onboarding-assistant
skill-platform-bind
skill-project-knowledge
skill-real-estate-form
skill-recording-qa
skill-recruitment-management
```

### 9.2 统计汇总

- **总Skill数量**: 约100+
- **系统服务**: 19个
- **驱动适配器**: 30+个
- **能力组件**: 20+个
- **工具服务**: 9个
- **场景应用**: 15个

---

**报告生成完毕**

**注意事项**:
1. 本报告基于skills目录下的skill.yaml文件统计
2. 排除了temp、mvp、skill-ui-test、app等测试和临时目录
3. 部分skill可能存在多个分类标签
4. 建议测试团队根据实际需求选择测试重点
