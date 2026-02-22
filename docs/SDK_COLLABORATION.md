# Skills Team SDK 协作文档

**文档版本**: 1.0  
**创建日期**: 2026-02-22  
**文档类型**: 协作协议  
**状�?*: 已发�?
---

## 1. 概述

### 1.1 文档目的

本文档定�?Skills Team �?SDK Team 的协作边界，明确责任归属，避免重复建设�?
### 1.2 协作原则

| 原则 | 说明 |
|------|------|
| 基础设施归SDK | 协议、离线、事件总线等基础设施由SDK Team实现 |
| 业务服务归Skills | Provider、北向服务等业务实现由Skills Team完成 |
| 接口定义归SDK | 接口抽象由SDK定义，Skills提供实现 |
| 避免重复 | 已有实现的不再重复建�?|

---

## 2. 责任边界

### 2.1 SDK Team 职责

| 职责 | 优先�?| 状�?|
|------|--------|------|
| DiscoveryProtocol | P0 | 待实�?|
| LoginProtocol | P0 | 待实�?|
| CollaborationProtocol | P0 | 待实�?|
| OfflineService | P0 | 待实�?|
| SkillShareService | P0 | 待实�?|
| EventBus | P1 | 待实�?|
| InstalledSkill补充属�?| P1 | 待实�?|
| LinkInfo补充属�?| P1 | 待实�?|
| SceneGroupInfo补充属�?| P1 | 待实�?|

### 2.2 Skills Team 职责

| 职责 | 状�?|
|------|------|
| SEC Provider实现 | �?已完�?|
| 自定义Provider实现 | �?已完�?|
| 北向服务实现 | �?已完�?|
| 场景驱动定义 | �?已完�?|
| 技能注册与发布 | �?已完�?|

---

## 3. Skills 已实现清�?
### 3.1 SEC Provider 实现

| Provider | 技�?| 实现�?| 状�?|
|----------|------|--------|------|
| AgentProvider | skill-agent | AgentProviderImpl | �?|
| HealthProvider | skill-health | HealthProviderImpl | �?|
| NetworkProvider | skill-network | NetworkProviderImpl | �?|
| ProtocolProvider | skill-protocol | ProtocolProviderImpl | �?|
| SecurityProvider | skill-security | SecurityProviderImpl | �?|
| HostingProvider | skill-hosting | HostingProviderImpl | �?|
| SkillShareProvider | skill-share | SkillShareProviderImpl | �?|

### 3.2 Skill Provider 实现

| Provider | 技�?| 实现�?| 状�?|
|----------|------|--------|------|
| LlmProvider | skill-llm-openai | OpenAiLlmProvider | �?|
| LlmProvider | skill-llm-ollama | OllamaLlmProvider | �?|
| LlmProvider | skill-llm-volcengine | VolcEngineLlmProvider | �?|
| LlmProvider | skill-llm-qianwen | QianwenLlmProvider | �?|
| LlmProvider | skill-llm-deepseek | DeepSeekLlmProvider | �?|
| SchedulerProvider | skill-scheduler-quartz | QuartzSchedulerProvider | �?|
| HttpClientProvider | skill-httpclient-okhttp | OkHttpProvider | �?|

### 3.3 自定�?Provider 实现

| Provider | 技�?| 实现�?| 状�?|
|----------|------|--------|------|
| PaymentProvider | skill-payment-alipay | AlipayProvider | �?|
| PaymentProvider | skill-payment-wechat | WechatPayProvider | �?|
| PaymentProvider | skill-payment-unionpay | UnionPayProvider | �?|
| MediaPublishProvider | skill-media-wechat | WechatMediaProvider | �?|
| MediaPublishProvider | skill-media-weibo | WeiboMediaProvider | �?|
| MediaPublishProvider | skill-media-zhihu | ZhihuMediaProvider | �?|
| MediaPublishProvider | skill-media-toutiao | ToutiaoMediaProvider | �?|
| MediaPublishProvider | skill-media-xiaohongshu | XiaohongshuMediaProvider | �?|
| NotifyProvider | skill-notify | DefaultNotifyProvider | �?|
| EmailProvider | skill-email | SmtpEmailProvider | �?|
| SearchProvider | skill-search | ElasticSearchProvider | �?|
| AuditProvider | skill-audit | DefaultAuditProvider | �?|
| ReportProvider | skill-report | DefaultReportProvider | �?|
| TaskProvider | skill-task | DefaultTaskProvider | �?|

### 3.4 Driver 实现

| Driver | 技�?| 实现�?| 状�?|
|--------|------|--------|------|
| OpenWrtDriver | skill-openwrt | OpenWrtDriverImpl | �?SSH完整实现 |

---

## 4. 北向服务覆盖

### 4.1 组织服务 (skill-org)

| 技�?| 功能 | 状�?|
|------|------|------|
| skill-org-dingding | 钉钉组织集成 | �?|
| skill-org-feishu | 飞书组织集成 | �?|
| skill-org-wecom | 企业微信集成 | �?|
| skill-org-weixin | 微信组织集成 | �?|

### 4.2 文件服务 (skill-vfs)

| 技�?| 功能 | 状�?|
|------|------|------|
| skill-vfs-local | 本地文件系统 | �?|
| skill-vfs-s3 | AWS S3存储 | �?|
| skill-vfs-minio | MinIO存储 | �?|
| skill-vfs-oss | 阿里云OSS | �?|
| skill-vfs-webdav | WebDAV协议 | �?|

### 4.3 消息服务 (skill-msg)

| 技�?| 功能 | 状�?|
|------|------|------|
| skill-msg | 消息服务 | �?|
| skill-mqtt | MQTT协议 | �?|
| skill-im | IM服务 | �?|
| skill-group | 群组管理 | �?|

### 4.4 系统服务 (skill-sys)

| 技�?| 功能 | 状�?|
|------|------|------|
| skill-network | 网络管理 | �?|
| skill-security | 安全管理 | �?|
| skill-hosting | 托管服务 | �?|
| skill-monitor | 监控服务 | �?|
| skill-health | 健康检�?| �?|
| skill-protocol | 协议处理 | �?|
| skill-agent | 代理管理 | �?|
| skill-search | 搜索服务 | �?|
| skill-audit | 审计服务 | �?|
| skill-report | 报表服务 | �?|
| skill-task | 任务管理 | �?|

### 4.5 其他服务

| 技�?| 功能 | 状�?|
|------|------|------|
| skill-user-auth | 用户认证 | �?|
| skill-openwrt | OpenWrt管理 | �?|
| skill-a2ui | UI生成 | �?|
| skill-notify | 通知服务 | �?|
| skill-email | 邮件服务 | �?|

---

## 5. 场景驱动定义

### 5.1 已定义场�?
| 场景ID | 场景名称 | 驱动能力 |
|--------|----------|----------|
| sys | 系统管理 | network-management, security-management, health-monitoring |
| msg | 消息通讯 | message-send, message-broadcast, group-message |
| vfs | 虚拟文件 | file-upload, file-download, directory-management |
| org | 组织管理 | user-auth, org-tree, department-management |
| payment | 支付服务 | create-payment, query-payment, create-refund |
| media | 媒体发布 | publish-article, update-article, get-stats |
| auth | 认证授权 | user-authentication, permission-check |

### 5.2 场景与技能映�?
```yaml
scene-mapping:
  sys:
    - skill-network
    - skill-security
    - skill-hosting
    - skill-monitor
    - skill-health
    - skill-protocol
    - skill-agent
    - skill-search
    - skill-audit
    - skill-report
    - skill-task
    
  msg:
    - skill-msg
    - skill-mqtt
    - skill-im
    - skill-group
    - skill-notify
    - skill-email
    
  vfs:
    - skill-vfs-local
    - skill-vfs-s3
    - skill-vfs-minio
    - skill-vfs-oss
    - skill-vfs-webdav
    
  org:
    - skill-org-dingding
    - skill-org-feishu
    - skill-org-wecom
    - skill-org-weixin
    - skill-user-auth
    
  payment:
    - skill-payment-alipay
    - skill-payment-wechat
    - skill-payment-unionpay
    
  media:
    - skill-media-wechat
    - skill-media-weibo
    - skill-media-zhihu
    - skill-media-toutiao
    - skill-media-xiaohongshu
```

---

## 6. 协作接口定义

### 6.1 Skills 需要的 SDK 接口

| 接口 | 用�?| 优先�?|
|------|------|--------|
| DiscoveryProtocol | 节点发现 | P0 |
| LoginProtocol | 本地认证 | P0 |
| CollaborationProtocol | 场景组协�?| P0 |
| OfflineService | 离线模式 | P0 |
| EventBus | 事件传�?| P1 |

### 6.2 Skills 提供的接�?
| 接口 | 类型 | 说明 |
|------|------|------|
| Provider接口 | SPI | 所有Provider通过ServiceLoader加载 |
| 场景驱动 | YAML | 场景配置文件定义 |
| 技能元数据 | YAML | skill.yaml定义 |

---

## 7. 版本兼容�?
### 7.1 SDK 版本要求

| Skills版本 | SDK版本 | 兼容�?|
|------------|---------|--------|
| 0.7.3 | 0.7.3 | �?完全兼容 |
| 0.7.3 | 0.7.2 | ⚠️ 部分功能不可�?|
| 0.7.3 | 0.7.3 | �?不兼�?|

### 7.2 升级计划

| 阶段 | 内容 | 时间 |
|------|------|------|
| Phase 1 | SDK完成P0接口 | 待定 |
| Phase 2 | Skills适配新接�?| SDK完成�?�?|
| Phase 3 | 集成测试 | Skills适配完成�?�?|

---

## 8. 联系方式

| 团队 | 负责�?| 联系方式 |
|------|--------|----------|
| Skills Team | - | GitHub: ooderCN/skills |
| SDK Team | - | GitHub: ooderCN/agent-sdk |
| Nexus Team | - | GitHub: ooderCN/ooder-Nexus |

---

## 9. 版本历史

| 版本 | 日期 | 作�?| 变更说明 |
|------|------|------|---------|
| 1.0 | 2026-02-22 | Skills Team | 初始版本 |
