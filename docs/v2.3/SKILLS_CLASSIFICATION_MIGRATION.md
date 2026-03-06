# Skills 分类迁移方案

> **版本**: v2.3.5  
> **日期**: 2026-03-06  
> **状态**: 规划中

---

## 一、分类标准

根据 v2.3 场景技能分类体系，现有 skills 按以下标准分类：

| 分类 | 简写 | 判定条件 | 特征 |
|------|------|---------|------|
| **自驱业务场景** | ABS | mainFirst=true + 业务语义≥8分 | 自动启动、业务闭环、有驱动能力 |
| **自驱系统场景** | ASS | mainFirst=true + 业务语义<8分 | 自动启动、系统功能、无业务语义 |
| **触发业务场景** | TBS | mainFirst=false + 业务语义≥8分 | 人工触发、业务功能、被动响应 |
| **服务技能** | SVC | 无sceneCapabilities | 纯服务提供、无场景特性 |

---

## 二、现有 Skills 分类分析

### 2.1 ABS - 自驱业务场景 (4个)

| Skill ID | 名称 | 判定依据 |
|----------|------|---------|
| skill-llm-chat | LLM智能对话 | mainFirst=true, sceneCapabilities, 业务语义强 |
| skill-knowledge-qa | 知识问答场景 | mainFirst=true, sceneCapabilities, 业务语义强 |
| skill-daily-report | 日志汇报场景 | mainFirst=true, sceneCapabilities, 业务语义强 |
| skill-llm-workspace | LLM工作空间 | mainFirst=true, sceneCapabilities, 业务语义强 |

### 2.2 ASS - 自驱系统场景 (8个)

| Skill ID | 名称 | 判定依据 |
|----------|------|---------|
| skill-health | 健康检查 | mainFirst=true, 系统功能 |
| skill-monitor | 监控服务 | mainFirst=true, 系统功能 |
| skill-scheduler-quartz | 调度服务 | mainFirst=true, 系统功能 |
| skill-update-checker | 更新检查 | mainFirst=true, 系统功能 |
| skill-failover-manager | 故障转移 | mainFirst=true, 系统功能 |
| skill-load-balancer | 负载均衡 | mainFirst=true, 系统功能 |
| skill-capability-coordinator | 能力协调 | mainFirst=true, 系统功能 |
| skill-agent | 代理管理 | mainFirst=true, 系统功能 |

### 2.3 TBS - 触发业务场景 (6个)

| Skill ID | 名称 | 判定依据 |
|----------|------|---------|
| skill-business | 业务场景服务 | mainFirst=false, 业务功能 |
| skill-collaboration | 协作场景服务 | mainFirst=false, 业务功能 |
| skill-report | 报表服务 | mainFirst=false, 业务功能 |
| skill-task | 任务管理 | mainFirst=false, 业务功能 |
| skill-market | 技能市场 | mainFirst=false, 业务功能 |
| skill-share | 技能分享 | mainFirst=false, 业务功能 |

### 2.4 SVC - 服务技能 (65个)

#### 2.4.1 组织服务 (org)

| Skill ID | 名称 |
|----------|------|
| skill-user-auth | 用户认证 |
| skill-org-dingding | 钉钉集成 |
| skill-org-feishu | 飞书集成 |
| skill-org-wecom | 企业微信集成 |
| skill-org-ldap | LDAP集成 |
| skill-org-base | 组织基础 |
| skill-access-control | 访问控制 |
| skill-security | 安全管理 |
| skill-audit | 审计日志 |

#### 2.4.2 存储服务 (vfs)

| Skill ID | 名称 |
|----------|------|
| skill-vfs-local | 本地存储 |
| skill-vfs-database | 数据库存储 |
| skill-vfs-minio | MinIO存储 |
| skill-vfs-oss | 阿里云OSS |
| skill-vfs-s3 | AWS S3 |
| skill-vfs-base | VFS基础 |

#### 2.4.3 消息通讯 (msg)

| Skill ID | 名称 |
|----------|------|
| skill-mqtt | MQTT服务 |
| skill-msg | 消息服务 |
| skill-msg-service | 消息推送 |
| skill-im | 即时通讯 |
| skill-group | 群组管理 |
| skill-email | 邮件服务 |
| skill-notify | 通知服务 |

#### 2.4.4 LLM服务 (llm)

| Skill ID | 名称 |
|----------|------|
| skill-llm-conversation | LLM对话服务 |
| skill-llm-context-builder | 上下文构建 |
| skill-llm-config-manager | LLM配置管理 |
| skill-llm-openai | OpenAI Provider |
| skill-llm-qianwen | 通义千问 Provider |
| skill-llm-deepseek | DeepSeek Provider |
| skill-llm-volcengine | 火山引擎 Provider |
| skill-llm-ollama | Ollama Provider |

#### 2.4.5 知识服务 (knowledge)

| Skill ID | 名称 |
|----------|------|
| skill-knowledge-base | 知识库服务 |
| skill-local-knowledge | 本地知识 |
| skill-rag | RAG服务 |
| skill-vector-sqlite | 向量存储 |
| skill-search | 搜索服务 |

#### 2.4.6 系统服务 (sys)

| Skill ID | 名称 |
|----------|------|
| skill-network | 网络管理 |
| skill-protocol | 协议管理 |
| skill-openwrt | OpenWrt管理 |
| skill-hosting | 托管服务 |
| skill-k8s | K8s管理 |
| skill-cmd-service | 命令服务 |
| skill-res-service | 资源服务 |
| skill-remote-terminal | 远程终端 |

#### 2.4.7 支付服务 (payment)

| Skill ID | 名称 |
|----------|------|
| skill-payment-alipay | 支付宝 |
| skill-payment-wechat | 微信支付 |
| skill-payment-unionpay | 银联支付 |

#### 2.4.8 媒体服务 (media)

| Skill ID | 名称 |
|----------|------|
| skill-media-wechat | 微信公众号 |
| skill-media-weibo | 微博 |
| skill-media-zhihu | 知乎 |
| skill-media-toutiao | 头条 |
| skill-media-xiaohongshu | 小红书 |

#### 2.4.9 工具服务 (util)

| Skill ID | 名称 |
|----------|------|
| skill-a2ui | A2UI图转代码 |
| skill-trae-solo | Trae Solo |
| skill-common | 公共库 |
| skill-document-processor | 文档处理 |
| skill-command-shortcut | 快捷命令 |
| skill-httpclient-okhttp | HTTP客户端 |

---

## 三、新目录结构

```
skills/
├── README.md                           # Skills目录说明
│
├── scene-skills/                       # 场景技能
│   ├── abs/                            # 自驱业务场景
│   │   ├── skill-llm-chat/
│   │   ├── skill-knowledge-qa/
│   │   ├── skill-daily-report/
│   │   └── skill-llm-workspace/
│   │
│   ├── ass/                            # 自驱系统场景
│   │   ├── skill-health/
│   │   ├── skill-monitor/
│   │   ├── skill-scheduler-quartz/
│   │   ├── skill-update-checker/
│   │   ├── skill-failover-manager/
│   │   ├── skill-load-balancer/
│   │   ├── skill-capability-coordinator/
│   │   └── skill-agent/
│   │
│   └── tbs/                            # 触发业务场景
│       ├── skill-business/
│       ├── skill-collaboration/
│       ├── skill-report/
│       ├── skill-task/
│       ├── skill-market/
│       └── skill-share/
│
├── service-skills/                     # 服务技能
│   ├── org/                            # 组织服务
│   │   ├── skill-user-auth/
│   │   ├── skill-org-dingding/
│   │   ├── skill-org-feishu/
│   │   ├── skill-org-wecom/
│   │   ├── skill-org-ldap/
│   │   ├── skill-org-base/
│   │   ├── skill-access-control/
│   │   ├── skill-security/
│   │   └── skill-audit/
│   │
│   ├── vfs/                            # 存储服务
│   │   ├── skill-vfs-local/
│   │   ├── skill-vfs-database/
│   │   ├── skill-vfs-minio/
│   │   ├── skill-vfs-oss/
│   │   ├── skill-vfs-s3/
│   │   └── skill-vfs-base/
│   │
│   ├── msg/                            # 消息通讯
│   │   ├── skill-mqtt/
│   │   ├── skill-msg/
│   │   ├── skill-msg-service/
│   │   ├── skill-im/
│   │   ├── skill-group/
│   │   ├── skill-email/
│   │   └── skill-notify/
│   │
│   ├── llm/                            # LLM服务
│   │   ├── skill-llm-conversation/
│   │   ├── skill-llm-context-builder/
│   │   ├── skill-llm-config-manager/
│   │   ├── skill-llm-openai/
│   │   ├── skill-llm-qianwen/
│   │   ├── skill-llm-deepseek/
│   │   ├── skill-llm-volcengine/
│   │   └── skill-llm-ollama/
│   │
│   ├── knowledge/                      # 知识服务
│   │   ├── skill-knowledge-base/
│   │   ├── skill-local-knowledge/
│   │   ├── skill-rag/
│   │   ├── skill-vector-sqlite/
│   │   └── skill-search/
│   │
│   ├── sys/                            # 系统服务
│   │   ├── skill-network/
│   │   ├── skill-protocol/
│   │   ├── skill-openwrt/
│   │   ├── skill-hosting/
│   │   ├── skill-k8s/
│   │   ├── skill-cmd-service/
│   │   ├── skill-res-service/
│   │   └── skill-remote-terminal/
│   │
│   ├── payment/                        # 支付服务
│   │   ├── skill-payment-alipay/
│   │   ├── skill-payment-wechat/
│   │   └── skill-payment-unionpay/
│   │
│   ├── media/                          # 媒体服务
│   │   ├── skill-media-wechat/
│   │   ├── skill-media-weibo/
│   │   ├── skill-media-zhihu/
│   │   ├── skill-media-toutiao/
│   │   └── skill-media-xiaohongshu/
│   │
│   └── util/                           # 工具服务
│       ├── skill-a2ui/
│       ├── skill-trae-solo/
│       ├── skill-common/
│       ├── skill-document-processor/
│       ├── skill-command-shortcut/
│       └── skill-httpclient-okhttp/
│
└── ui-skills/                          # UI技能（独立分类）
    ├── skill-knowledge-ui/
    ├── skill-llm-assistant-ui/
    └── skill-llm-management-ui/
```

---

## 四、配置扩展

### 4.1 skill-index.yaml 扩展

```yaml
spec:
  categories:
    - id: abs
      name: 自驱业务场景
      nameEn: Auto Business Scene
      description: 自动启动、业务闭环的场景技能
      icon: auto-scene
      order: 1
      type: scene-skill
      
    - id: ass
      name: 自驱系统场景
      nameEn: Auto System Scene
      description: 自动启动、系统功能的场景技能
      icon: auto-system
      order: 2
      type: scene-skill
      
    - id: tbs
      name: 触发业务场景
      nameEn: Trigger Business Scene
      description: 人工触发、被动响应的场景技能
      icon: trigger-scene
      order: 3
      type: scene-skill
      
    - id: svc
      name: 服务技能
      nameEn: Service Skill
      description: 纯服务提供的技能
      icon: service
      order: 4
      type: service-skill

  skills:
    - skillId: skill-llm-chat
      name: LLM智能对话
      version: "2.3.0"
      category: abs                    # 新增：场景分类
      sceneCategory: abs               # 新增：场景分类（兼容）
      mainFirst: true
      businessSemanticsScore: 9        # 新增：业务语义评分
      path: skills/scene-skills/abs/skill-llm-chat  # 新路径
      ...
```

### 4.2 skill.yaml 扩展

```yaml
metadata:
  id: skill-llm-chat
  name: LLM智能对话
  type: scene-skill
  category: abs                        # 新增：场景分类
  sceneCategory: abs                   # 新增：场景分类（兼容）

spec:
  type: scene-skill
  category: abs                        # 新增：场景分类
  
  classification:                      # 新增：分类信息
    category: abs
    categoryName: 自驱业务场景
    mainFirst: true
    businessSemanticsScore: 9
    detectedAt: 2026-03-06T00:00:00Z
    detectionVersion: "2.3.0"
```

---

## 五、迁移步骤

### 5.1 Phase 1: 配置扩展（无破坏性）

1. 扩展 skill-index.yaml 添加分类字段
2. 更新 skill.yaml 模板添加分类字段
3. 保持现有目录结构不变

### 5.2 Phase 2: 软链接迁移

1. 创建新目录结构
2. 使用符号链接保持兼容性
3. 更新文档和索引

### 5.3 Phase 3: 物理迁移

1. 移动文件到新目录
2. 更新所有引用路径
3. 删除旧目录

---

## 六、兼容性保障

### 6.1 向后兼容

- 保留原有 skillId 不变
- 支持旧路径访问（软链接）
- API 保持兼容

### 6.2 Engine 支持

- Engine 需支持新分类字段
- detectCategory() 方法自动分类
- 安装逻辑支持分类路径

---

**维护团队**: Skills Team  
**最后更新**: 2026-03-06
