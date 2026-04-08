# OoderAgent Skills 完整列表（修订版）

**统计时间**: 2026-04-08  
**统计路径**: E:\github\ooder-skills\skills  
**总计 Skills 数量**: 134 个

## 一、_base 基础层（4个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 1 | ooder-spi-core | _base/ooder-spi-core | 统一SPI核心接口 |
| 2 | skill-spi-core | _base/skill-spi-core | 技能SPI核心接口 |
| 3 | skill-spi-llm | _base/skill-spi-llm | LLM服务SPI接口 |
| 4 | skill-spi-messaging | _base/skill-spi-messaging | 统一消息服务SPI |

## 二、_business 业务层（11个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 5 | skill-context | _business/skill-context | 上下文管理服务 |
| 6 | skill-driver-config | _business/skill-driver-config | 驱动配置管理服务 |
| 7 | skill-install-scene | _business/skill-install-scene | 场景安装流程管理 |
| 8 | skill-installer | _business/skill-installer | 安装器服务 |
| 9 | skill-keys | _business/skill-keys | API密钥管理服务 |
| 10 | skill-llm-config | _business/skill-llm-config | LLM配置服务 |
| 11 | skill-procedure | _business/skill-procedure | 企业流程管理服务 |
| 12 | skill-scenes | _business/skill-scenes | 场景管理服务 |
| 13 | skill-security | _business/skill-security | 安全策略管理服务 |
| 14 | skill-selector | _business/skill-selector | 选择器服务 |
| 15 | skill-todo | _business/skill-todo | 待办任务管理服务 |

## 三、_drivers 驱动层（37个）

### 3.1 BPM 驱动（4个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 16 | bpm-designer | _drivers/bpm/bpm-designer | BPM流程设计器 |
| 17 | bpm-test | _drivers/bpm/bpm-test | BPM测试模块 |
| 18 | bpmserver | _drivers/bpm/bpmserver | BPM服务器 |
| 19 | skill-bpm | _drivers/bpm/skill-bpm | BPM技能模块 |

### 3.2 IM 驱动（4个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 20 | skill-im-dingding | _drivers/im/skill-im-dingding | 钉钉IM驱动 |
| 21 | skill-im-feishu | _drivers/im/skill-im-feishu | 飞书IM驱动 |
| 22 | skill-im-wecom | _drivers/im/skill-im-wecom | 企业微信IM驱动 |
| 23 | skill-im-weixin | _drivers/im/skill-im-weixin | 个人微信IM驱动 |

### 3.3 LLM 驱动（8个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 24 | skill-llm-baidu | _drivers/llm/skill-llm-baidu | 百度文心一言驱动 |
| 25 | skill-llm-base | _drivers/llm/skill-llm-base | LLM基础驱动 |
| 26 | skill-llm-deepseek | _drivers/llm/skill-llm-deepseek | DeepSeek驱动 |
| 27 | skill-llm-monitor | _drivers/llm/skill-llm-monitor | LLM监控服务 |
| 28 | skill-llm-ollama | _drivers/llm/skill-llm-ollama | Ollama本地模型驱动 |
| 29 | skill-llm-openai | _drivers/llm/skill-llm-openai | OpenAI驱动 |
| 30 | skill-llm-qianwen | _drivers/llm/skill-llm-qianwen | 阿里通义千问驱动 |
| 31 | skill-llm-volcengine | _drivers/llm/skill-llm-volcengine | 火山引擎驱动 |

### 3.4 Media 驱动（5个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 32 | skill-media-toutiao | _drivers/media/skill-media-toutiao | 今日头条媒体驱动 |
| 33 | skill-media-wechat | _drivers/media/skill-media-wechat | 微信公众号媒体驱动 |
| 34 | skill-media-weibo | _drivers/media/skill-media-weibo | 微博媒体驱动 |
| 35 | skill-media-xiaohongshu | _drivers/media/skill-media-xiaohongshu | 小红书媒体驱动 |
| 36 | skill-media-zhihu | _drivers/media/skill-media-zhihu | 知乎媒体驱动 |

### 3.5 ORG 驱动（6个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 37 | skill-org-base | _drivers/org/skill-org-base | 组织架构基础服务 |
| 38 | skill-org-dingding | _drivers/org/skill-org-dingding | 钉钉组织架构 |
| 39 | skill-org-feishu | _drivers/org/skill-org-feishu | 飞书组织架构 |
| 40 | skill-org-ldap | _drivers/org/skill-org-ldap | LDAP组织架构 |
| 41 | skill-org-web | _drivers/org/skill-org-web | Web组织架构服务 |
| 42 | skill-org-wecom | _drivers/org/skill-org-wecom | 企业微信组织架构 |

### 3.6 Payment 驱动（3个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 43 | skill-payment-alipay | _drivers/payment/skill-payment-alipay | 支付宝支付驱动 |
| 44 | skill-payment-unionpay | _drivers/payment/skill-payment-unionpay | 银联支付驱动 |
| 45 | skill-payment-wechat | _drivers/payment/skill-payment-wechat | 微信支付驱动 |

### 3.7 SPI 驱动（1个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 46 | skill-spi | _drivers/spi/skill-spi | SPI服务实现 |

### 3.8 VFS 驱动（7个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 47 | skill-vfs-base | _drivers/vfs/skill-vfs-base | 虚拟文件系统基础 |
| 48 | skill-vfs-database | _drivers/vfs/skill-vfs-database | 数据库文件系统 |
| 49 | skill-vfs-local | _drivers/vfs/skill-vfs-local | 本地文件系统 |
| 50 | skill-vfs-minio | _drivers/vfs/skill-vfs-minio | MinIO文件系统 |
| 51 | skill-vfs-oss | _drivers/vfs/skill-vfs-oss | 阿里云OSS文件系统 |
| 52 | skill-vfs-s3 | _drivers/vfs/skill-vfs-s3 | AWS S3文件系统 |
| 53 | skills-vfs-demo | _drivers/vfs/skills-vfs-demo | VFS演示模块 |

## 四、_system 系统层（32个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 54 | skill-agent | _system/skill-agent | Agent管理服务 |
| 55 | skill-audit | _system/skill-audit | 审计服务 |
| 56 | skill-auth | _system/skill-auth | 认证服务 |
| 57 | skill-capability | _system/skill-capability | 能力管理服务 |
| 58 | skill-common | _system/skill-common | 公共服务模块 |
| 59 | skill-config | _system/skill-config | 配置管理服务 |
| 60 | skill-dashboard | _system/skill-dashboard | 仪表板服务 |
| 61 | skill-dict | _system/skill-dict | 字典服务 |
| 62 | skill-discovery | _system/skill-discovery | 服务发现 |
| 63 | skill-history | _system/skill-history | 历史记录服务 |
| 64 | skill-im-gateway | _system/skill-im-gateway | IM网关服务 |
| 65 | skill-install | _system/skill-install | 安装服务 |
| 66 | skill-key | _system/skill-key | 密钥管理服务 |
| 67 | skill-knowledge | _system/skill-knowledge | 知识管理服务 |
| 68 | skill-knowledge-platform | _system/skill-knowledge-platform | 知识平台基础设施 |
| 69 | skill-llm-chat | _system/skill-llm-chat | LLM聊天服务 |
| 70 | skill-management | _system/skill-management | 技能管理服务 |
| 71 | skill-menu | _system/skill-menu | 菜单服务 |
| 72 | skill-messaging | _system/skill-messaging | 消息服务 |
| 73 | skill-notification | _system/skill-notification | 通知服务 |
| 74 | skill-org | _system/skill-org | 组织架构服务 |
| 75 | skill-protocol | _system/skill-protocol | 协议服务 |
| 76 | skill-rag | _system/skill-rag | RAG服务 |
| 77 | skill-role | _system/skill-role | 角色管理服务 |
| 78 | skill-scene | _system/skill-scene | 场景服务 |
| 79 | skill-setup | _system/skill-setup | 系统设置服务 |
| 80 | skill-support | _system/skill-support | 支持服务 |
| 81 | skill-template | _system/skill-template | 场景模板服务 |
| 82 | skill-tenant | _system/skill-tenant | 租户服务 |
| 83 | skill-vfs | _system/skill-vfs | 虚拟文件系统服务 |
| 84 | skill-workflow | _system/skill-workflow | 工作流服务 |
| 85 | skills-bpm-demo | _system/skills-bpm-demo | BPM演示模块 |

## 五、capabilities 能力层（24个）

### 5.1 auth 认证能力（1个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 86 | skill-user-auth | capabilities/auth/skill-user-auth | 用户认证服务 |

### 5.2 communication 通信能力（7个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 87 | skill-email | capabilities/communication/skill-email | 邮件服务 |
| 88 | skill-group | capabilities/communication/skill-group | 群组服务 |
| 89 | skill-im | capabilities/communication/skill-im | IM通信服务 |
| 90 | skill-mqtt | capabilities/communication/skill-mqtt | MQTT服务 |
| 91 | skill-msg | capabilities/communication/skill-msg | 消息服务 |
| 92 | skill-notification | capabilities/communication/skill-notification | 通知服务 |
| 93 | skill-notify | capabilities/communication/skill-notify | 通知推送服务 |

### 5.3 infrastructure 基础设施能力（6个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 94 | skill-failover-manager | capabilities/infrastructure/skill-failover-manager | 故障转移管理器 |
| 95 | skill-hosting | capabilities/infrastructure/skill-hosting | 托管服务 |
| 96 | skill-httpclient-okhttp | capabilities/infrastructure/skill-httpclient-okhttp | OkHttp客户端 |
| 97 | skill-k8s | capabilities/infrastructure/skill-k8s | Kubernetes服务 |
| 98 | skill-load-balancer | capabilities/infrastructure/skill-load-balancer | 负载均衡器 |
| 99 | skill-openwrt | capabilities/infrastructure/skill-openwrt | OpenWrt路由器服务 |

### 5.4 llm LLM能力（1个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 100 | skill-llm-config-manager | capabilities/llm/skill-llm-config-manager | LLM配置管理器 |

### 5.5 monitor 监控能力（6个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 101 | skill-cmd-service | capabilities/monitor/skill-cmd-service | 命令服务 |
| 102 | skill-health | capabilities/monitor/skill-health | 健康检查服务 |
| 103 | skill-monitor | capabilities/monitor/skill-monitor | 监控服务 |
| 104 | skill-network | capabilities/monitor/skill-network | 网络监控服务 |
| 105 | skill-remote-terminal | capabilities/monitor/skill-remote-terminal | 远程终端服务 |
| 106 | skill-res-service | capabilities/monitor/skill-res-service | 资源服务 |

### 5.6 scheduler 调度能力（2个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 107 | skill-scheduler-quartz | capabilities/scheduler/skill-scheduler-quartz | Quartz调度器 |
| 108 | skill-task | capabilities/scheduler/skill-task | 任务调度服务 |

### 5.7 search 搜索能力（1个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 109 | skill-search | capabilities/search/skill-search | 搜索服务 |

## 六、scenes 场景层（15个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 110 | daily-report | scenes/daily-report | 日报场景 |
| 111 | skill-agent-recommendation | scenes/skill-agent-recommendation | Agent推荐场景 |
| 112 | skill-approval-form | scenes/skill-approval-form | 审批表单场景 |
| 113 | skill-business | scenes/skill-business | 业务场景 |
| 114 | skill-collaboration | scenes/skill-collaboration | 协作场景 |
| 115 | skill-document-assistant | scenes/skill-document-assistant | 文档助手场景 |
| 116 | skill-knowledge-management | scenes/skill-knowledge-management | 知识管理场景 |
| 117 | skill-knowledge-qa | scenes/skill-knowledge-qa | 知识问答场景 |
| 118 | skill-knowledge-share | scenes/skill-knowledge-share | 知识分享场景 |
| 119 | skill-meeting-minutes | scenes/skill-meeting-minutes | 会议纪要场景 |
| 120 | skill-onboarding-assistant | scenes/skill-onboarding-assistant | 入职助手场景 |
| 121 | skill-platform-bind | scenes/skill-platform-bind | 平台绑定场景 |
| 122 | skill-project-knowledge | scenes/skill-project-knowledge | 项目知识场景 |
| 123 | skill-real-estate-form | scenes/skill-real-estate-form | 房地产表单场景 |
| 124 | skill-recording-qa | scenes/skill-recording-qa | 录音问答场景 |

## 七、tools 工具层（10个）

| 序号 | Skill ID | 目录 | 说明 |
|------|----------|------|------|
| 125 | skill-agent-cli | tools/skill-agent-cli | Agent命令行工具 |
| 126 | skill-calendar | tools/skill-calendar | 日历工具 |
| 127 | skill-command-shortcut | tools/skill-command-shortcut | 命令快捷方式工具 |
| 128 | skill-doc-collab | tools/skill-doc-collab | 文档协作工具 |
| 129 | skill-document-processor | tools/skill-document-processor | 文档处理工具 |
| 130 | skill-market | tools/skill-market | 技能市场工具 |
| 131 | skill-msg-push | tools/skill-msg-push | 消息推送工具 |
| 132 | skill-share | tools/skill-share | 分享工具 |
| 133 | skill-todo-sync | tools/skill-todo-sync | 待办同步工具 |
| 134 | skill-update-checker | tools/skill-update-checker | 更新检查工具 |

## 八、统计汇总

### 8.1 按目录分类统计

| 目录 | 数量 | 占比 |
|------|------|------|
| _base | 4 | 2.99% |
| _business | 11 | 8.21% |
| _drivers | 37 | 27.61% |
| _system | 32 | 23.88% |
| capabilities | 24 | 17.91% |
| scenes | 15 | 11.19% |
| tools | 10 | 7.46% |
| **总计** | **133** | **100%** |

### 8.2 按功能分类统计

| 功能分类 | 数量 | 包含模块 |
|----------|------|----------|
| SPI核心 | 4 | ooder-spi-core, skill-spi-core, skill-spi-llm, skill-spi-messaging |
| 业务服务 | 11 | skill-todo, skill-procedure, skill-security 等 |
| BPM驱动 | 4 | bpm-designer, bpm-test, bpmserver, skill-bpm |
| IM驱动 | 4 | skill-im-dingding, skill-im-feishu, skill-im-wecom, skill-im-weixin |
| LLM驱动 | 8 | skill-llm-base, skill-llm-deepseek 等 |
| Media驱动 | 5 | skill-media-toutiao, skill-media-wechat 等 |
| ORG驱动 | 6 | skill-org-base, skill-org-dingding 等 |
| Payment驱动 | 3 | skill-payment-alipay, skill-payment-unionpay, skill-payment-wechat |
| VFS驱动 | 7 | skill-vfs-base, skill-vfs-local 等 |
| 系统服务 | 32 | skill-auth, skill-audit, skill-config 等 |
| 能力服务 | 22 | skill-user-auth, skill-health, skill-email 等 |
| 场景应用 | 15 | daily-report, skill-approval-form 等 |
| 工具服务 | 10 | skill-calendar, skill-market 等 |

---

**文档版本**: v2.0  
**最后更新**: 2026-04-08

**问题**: 是否还有其他遗漏的目录或文件？
**回答**: 经过深度检查，已发现并补充了 payment 目录（3个）、capabilities/infrastructure 目录（6个）、capabilities/communication 目录（7个）。当前总计 133 个 skills。
