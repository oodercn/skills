# Skills 分类对比分析报告

## 一、文档定义 vs 实际实现对比

### 1.1 skills-category-proposal.md 定义的分类 (6个)

| 分类ID | 分类名称 | 英文名称 | 描述 | 图标 |
|--------|----------|----------|------|------|
| `org` | 组织服务 | Organization | 企业组织架构、用户认证相关服务 | users |
| `vfs` | 存储服务 | Storage | 文件存储、对象存储相关服务 | database |
| `ui` | UI生成 | UI Generation | 界面生成、设计转代码服务 | palette |
| `msg` | 消息通讯 | Messaging | 消息队列、通讯协议服务 | message |
| `sys` | 系统管理 | System | 系统监控、网络管理、安全审计 | settings |
| `util` | 工具服务 | Utility | 通用工具、辅助功能 | tool |

### 1.2 skill-index.yaml 中定义的分类 (11个)

| 分类ID | 分类名称 | 英文名称 | 描述 | 图标 | 状态 |
|--------|----------|----------|------|------|:----:|
| `org` | 组织服务 | Organization | 企业组织架构、用户认证相关服务 | users | ✅ 一致 |
| `vfs` | 存储服务 | Storage | 文件存储、对象存储相关服务 | database | ✅ 一致 |
| `ui` | UI生成 | UI Generation | 界面生成、设计转代码服务 | palette | ✅ 一致 |
| `msg` | 消息通讯 | Messaging | 消息队列、通讯协议服务 | message | ✅ 一致 |
| `sys` | 系统管理 | System | 系统监控、网络管理、安全审计 | settings | ✅ 一致 |
| `util` | 工具服务 | Utility | 通用工具、辅助功能 | tool | ✅ 一致 |
| `llm` | LLM服务 | LLM Services | 大语言模型服务、对话、配置、上下文管理 | brain | 🆕 新增 |
| `knowledge` | 知识服务 | Knowledge | 知识库、RAG、向量存储、文档处理 | book | 🆕 新增 |
| `payment` | 支付服务 | Payment | 支付渠道、退款管理、交易处理 | credit-card | 🆕 新增 |
| `media` | 媒体发布 | Media Publishing | 自媒体文章发布、内容管理、数据分析 | edit | 🆕 新增 |
| `nexus-ui` | Nexus界面 | Nexus UI | Nexus管理界面、仪表盘、监控页面 | layout | 🆕 新增 |

---

## 二、分类使用情况分析

### 2.1 分类使用统计

| 分类 | 使用次数 | Skills 示例 |
|------|:-------:|-------------|
| `sys` | 18 | skill-network, skill-agent, skill-security, skill-health, skill-protocol, skill-openwrt, skill-hosting, skill-monitor, skill-audit, skill-access-control, skill-remote-terminal, skill-cmd-service, skill-res-service, skill-search, skill-report |
| `llm` | 10 | skill-llm-chat, skill-llm-conversation, skill-llm-context-builder, skill-llm-config-manager, skill-llm-openai, skill-llm-qianwen, skill-llm-deepseek, skill-llm-ollama, skill-llm-volcengine |
| `knowledge` | 6 | skill-knowledge-base, skill-rag, skill-local-knowledge, skill-vector-sqlite, skill-document-processor, skill-knowledge-qa |
| `nexus-ui` | 6 | skill-knowledge-ui, skill-llm-assistant-ui, skill-llm-management-ui, skill-knowledge-ui, skill-knowledge-ui |
| `msg` | 6 | skill-mqtt, skill-im, skill-group, skill-msg-service, skill-notify, skill-email |
| `vfs` | 5 | skill-vfs-base, skill-vfs-database, skill-vfs-local, skill-vfs-minio, skill-vfs-oss, skill-vfs-s3 |
| `org` | 5 | skill-org-base, skill-org-dingding, skill-org-feishu, skill-org-wecom, skill-org-ldap, skill-user-auth |
| `util` | 5 | skill-document-processor, skill-trae-solo, skill-share, skill-common, skill-market |
| `media` | 5 | skill-media-wechat, skill-media-weibo, skill-media-zhihu, skill-media-toutiao, skill-media-xiaohongshu |
| `payment` | 3 | skill-payment-alipay, skill-payment-wechat, skill-payment-unionpay |
| `ui` | 1 | skill-a2ui |

### 2.2 问题分类使用 (需要修复)

| 分类 | 使用次数 | 问题类型 | Skills |
|------|:-------:|----------|--------|
| `abs` | 2 | ❌ 场景类型误用 | skill-document-assistant, skill-onboarding-assistant |
| `tbs` | 2 | ❌ 场景类型误用 | skill-meeting-minutes, skill-project-knowledge |
| `ass` | 1 | ❌ 场景类型误用 | skill-knowledge-share |
| `business` | 1 | ❌ 未定义分类 | skill-business |
| `infrastructure` | 1 | ❌ 未定义分类 | skill-k8s |
| `scheduler` | 1 | ❌ 未定义分类 | skill-scheduler-quartz |
| `COLLABORATION` | 1 | ❌ 大写格式 | skill-collaboration |
| `SYSTEM` | 8 | ❌ 大写格式 | 多个 skills |
| `INFRASTRUCTURE` | 1 | ❌ 大写格式 | - |
| `COMMUNICATION` | 3 | ❌ 大写格式 | skill-notify, skill-email, skill-msg |
| `IOT` | 1 | ❌ 大写格式 | - |

---

## 三、问题汇总

### 3.1 严重问题 (P0)

| 问题 | 描述 | 影响 | 建议 |
|------|------|------|------|
| 场景类型误作分类 | `abs`/`tbs`/`ass` 是场景技能类型，不应作为分类 | 分类体系混乱 | 移除这些分类，使用正确的分类 |
| 大小写不一致 | 存在 `SYSTEM`/`COMMUNICATION` 等大写格式 | 分类识别失败 | 统一使用小写格式 |
| 未定义分类使用 | `business`/`infrastructure`/`scheduler` 未在 categories 中定义 | 索引解析可能失败 | 添加分类定义或迁移到现有分类 |

### 3.2 中等问题 (P1)

| 问题 | 描述 | 建议 |
|------|------|------|
| 文档未更新 | skills-category-proposal.md 缺少新增分类 | 更新文档添加 llm/knowledge/payment/media/nexus-ui |
| 分类归属不明确 | 部分技能分类归属不合理 | 重新评估分类归属 |

### 3.3 建议改进 (P2)

| 问题 | 描述 | 建议 |
|------|------|------|
| 分类数量增长 | 从 6 个增长到 11 个 | 考虑是否需要合并部分分类 |
| sceneDriver 缺失 | llm/knowledge/ui/nexus-ui 缺少 sceneDriver | 评估是否需要添加 |

---

## 四、修复建议

### 4.1 分类映射修复

| 当前分类 | 修复为 | 涉及 Skills |
|----------|--------|-------------|
| `abs` | `knowledge` | skill-document-assistant, skill-onboarding-assistant |
| `tbs` | `knowledge` | skill-meeting-minutes, skill-project-knowledge |
| `ass` | `knowledge` | skill-knowledge-share |
| `business` | `util` | skill-business |
| `infrastructure` | `sys` | skill-k8s |
| `scheduler` | `sys` | skill-scheduler-quartz |
| `COLLABORATION` | `util` | skill-collaboration |
| `SYSTEM` | `sys` | 多个 skills |
| `INFRASTRUCTURE` | `sys` | - |
| `COMMUNICATION` | `msg` | skill-notify, skill-email, skill-msg |
| `IOT` | `sys` | - |

### 4.2 新增分类定义

```yaml
categories:
  - id: llm
    name: LLM服务
    nameEn: LLM Services
    description: 大语言模型服务、对话、配置、上下文管理
    icon: brain
    order: 6
    sceneDriver: null
    
  - id: knowledge
    name: 知识服务
    nameEn: Knowledge
    description: 知识库、RAG、向量存储、文档处理
    icon: book
    order: 7
    sceneDriver: null
    
  - id: payment
    name: 支付服务
    nameEn: Payment
    description: 支付渠道、退款管理、交易处理
    icon: credit-card
    order: 8
    sceneDriver: payment
    
  - id: media
    name: 媒体发布
    nameEn: Media Publishing
    description: 自媒体文章发布、内容管理、数据分析
    icon: edit
    order: 9
    sceneDriver: media
    
  - id: nexus-ui
    name: Nexus界面
    nameEn: Nexus UI
    description: Nexus管理界面、仪表盘、监控页面
    icon: layout
    order: 11
    sceneDriver: null
```

### 4.3 场景技能类型字段分离

建议为场景技能添加独立的 `sceneType` 字段，而非使用 `category`：

```yaml
- skillId: skill-document-assistant
  name: 智能文档助手
  version: "1.0.0"
  category: knowledge          # 分类：知识服务
  sceneType: abs               # 场景类型：自动业务场景
  subCategory: qa              # 子分类：问答
  tags:
    - document
    - assistant
    - rag
    - scene-skill
    - mainFirst
```

---

## 五、执行计划

### 5.1 Phase 1: 紧急修复 (立即)

1. 修复大写分类为小写
2. 移除 `abs`/`tbs`/`ass` 作为分类的使用
3. 添加缺失的分类定义

### 5.2 Phase 2: 结构优化 (本周)

1. 为场景技能添加 `sceneType` 字段
2. 更新 skills-category-proposal.md
3. 统一所有 skills 的分类归属

### 5.3 Phase 3: 文档同步 (下周)

1. 更新 README 文档
2. 更新 API 文档
3. 通知相关团队

---

## 六、检测结论

| 检测项 | 状态 | 说明 |
|--------|:----:|------|
| 分类定义完整性 | ⚠️ | 存在未定义分类被使用 |
| 分类使用一致性 | ❌ | 大小写不一致、场景类型误用 |
| 文档与实现同步 | ⚠️ | 文档缺少新增分类 |
| 分类归属合理性 | ⚠️ | 部分技能分类归属需重新评估 |

**总体评估**: 分类体系基本完整，但存在严重的一致性问题需要立即修复。

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
