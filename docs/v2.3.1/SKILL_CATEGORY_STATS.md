# Skills 标准分类统计表

> **版本**: v2.3.1  
> **统计日期**: 2026-03-18  
> **数据来源**: skill-index/skills/*.yaml  
> **目标受众**: 前端团队 (SE Team)

---

## 一、分类统计总览

| 排序 | 分类ID | 分类名称 | 英文名称 | 图标 | Skills 数量 | 占比 |
|:----:|--------|----------|----------|------|:-----------:|:----:|
| 1 | `sys` | 系统管理 | System | settings | **8** | 12.7% |
| 2 | `util` | 工具服务 | Utility | tool | **8** | 12.7% |
| 3 | `nexus-ui` | Nexus界面 | Nexus UI | layout | **8** | 12.7% |
| 4 | `msg` | 消息通讯 | Messaging | message | **7** | 11.1% |
| 5 | `vfs` | 存储服务 | Storage | database | **6** | 9.5% |
| 6 | `org` | 组织服务 | Organization | users | **6** | 9.5% |
| 7 | `media` | 媒体发布 | Media Publishing | edit | **5** | 7.9% |
| 8 | `business` | 业务场景 | Business | briefcase | **5** | 7.9% |
| 9 | `llm` | LLM服务 | LLM Services | brain | **3** | 4.8% |
| 10 | `payment` | 支付服务 | Payment | credit-card | **3** | 4.8% |
| 11 | `knowledge` | 知识服务 | Knowledge | book | **1** | 1.6% |
| 12 | `ui` | UI生成 | UI Generation | palette | **1** | 1.6% |
| 13 | `scheduler` | 调度服务 | Scheduler | clock | **1** | 1.6% |
| 14 | `infrastructure` | 基础设施 | Infrastructure | server | **1** | 1.6% |
| **总计** | - | - | - | - | **63** | **100%** |

---

## 二、分类详细清单

### 2.1 系统管理 (sys) - 8 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-network | 网络管理技能 | network |
| skill-protocol | 协议管理技能 | protocol |
| skill-openwrt | OpenWrt管理技能 | router |
| skill-monitor | 监控服务 | monitor |
| skill-health | 健康检查服务 | health |
| skill-remote-terminal | 远程终端服务 | remote |
| skill-cmd-service | 命令监控服务 | cmd |
| skill-res-service | 资源管理服务 | res |

### 2.2 工具服务 (util) - 8 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-document-processor | 文档处理服务 | document |
| skill-trae-solo | Trae Solo Service | general |
| skill-share | 技能分享技能 | share |
| skill-llm-volcengine | 火山引擎豆包LLM | llm |
| skill-llm-qianwen | 通义千问LLM | llm |
| skill-llm-deepseek | DeepSeek LLM | llm |
| skill-market | 技能市场服务 | market |
| skill-collaboration | 协作场景服务 | collaboration |

### 2.3 Nexus界面 (nexus-ui) - 8 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-knowledge-ui | 知识库管理界面 | knowledge |
| skill-llm-assistant-ui | LLM智能助手界面 | llm |
| skill-llm-management-ui | LLM管理界面 | llm |
| skill-nexus-dashboard-nexus-ui | Nexus仪表盘界面 | dashboard |
| skill-nexus-system-status-nexus-ui | Nexus系统状态界面 | status |
| skill-personal-dashboard-nexus-ui | 个人仪表盘界面 | personal |
| skill-nexus-health-check-nexus-ui | Nexus健康检查界面 | health |
| skill-storage-management-nexus-ui | 存储管理界面 | storage |

### 2.4 消息通讯 (msg) - 7 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-mqtt | MQTT服务技能 | mqtt |
| skill-im | 即时通讯服务 | im |
| skill-group | 群组管理服务 | group |
| skill-msg-service | 消息服务 | message |
| skill-notify | 通知服务 | notify |
| skill-email | 邮件服务 | email |
| skill-msg | 消息服务 | message |

### 2.5 存储服务 (vfs) - 6 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-vfs-base | VFS基础服务 | base |
| skill-vfs-database | 数据库存储服务 | database |
| skill-vfs-local | 本地文件系统存储 | local |
| skill-vfs-minio | MinIO存储服务 | object-storage |
| skill-vfs-oss | 阿里云OSS存储 | object-storage |
| skill-vfs-s3 | AWS S3存储服务 | object-storage |

### 2.6 组织服务 (org) - 6 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-org-base | 组织基础服务 | base |
| skill-org-dingding | 钉钉组织服务 | dingtalk |
| skill-org-feishu | 飞书组织服务 | feishu |
| skill-org-wecom | 企业微信组织服务 | wecom |
| skill-org-ldap | LDAP组织服务 | ldap |
| skill-user-auth | 用户认证服务 | auth |

### 2.7 媒体发布 (media) - 5 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-media-wechat | 微信公众号发布 | wechat |
| skill-media-weibo | 微博发布 | weibo |
| skill-media-zhihu | 知乎发布 | zhihu |
| skill-media-toutiao | 头条发布 | toutiao |
| skill-media-xiaohongshu | 小红书发布 | xiaohongshu |

### 2.8 业务场景 (business) - 5 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-business | 业务场景服务 | scenario |
| skill-recruitment-management | 招聘管理系统 | hr |
| skill-approval-form | 审批表单系统 | approval |
| skill-real-estate-form | 房产中介房源表单 | realestate |
| skill-recording-qa | 录音质检系统 | qa |

### 2.9 LLM服务 (llm) - 3 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-llm-chat | LLM智能对话场景能力 | chat |
| skill-llm-config-manager | LLM配置管理 | config |
| skill-llm-baidu | 百度千帆LLM Provider | provider |

### 2.10 支付服务 (payment) - 3 个

| Skill ID | 名称 | 子分类 |
|----------|------|--------|
| skill-payment-alipay | 支付宝支付Provider | alipay |
| skill-payment-wechat | 微信支付Provider | wechat |
| skill-payment-unionpay | 银联支付Provider | unionpay |

### 2.11 其他分类 - 各 1 个

| 分类 | Skill ID | 名称 |
|------|----------|------|
| knowledge | skill-knowledge-qa | 知识问答场景能力 |
| ui | skill-a2ui | A2UI图转代码技能 |
| scheduler | skill-scheduler-quartz | Quartz调度服务 |
| infrastructure | skill-k8s | Kubernetes集群管理服务 |

---

## 三、前端展示建议

### 3.1 分类卡片配置

```javascript
const categoryStats = [
  { id: 'sys', name: '系统管理', icon: 'settings', count: 8, color: '#3b82f6' },
  { id: 'util', name: '工具服务', icon: 'tool', count: 8, color: '#8b5cf6' },
  { id: 'nexus-ui', name: 'Nexus界面', icon: 'layout', count: 8, color: '#06b6d4' },
  { id: 'msg', name: '消息通讯', icon: 'message', count: 7, color: '#10b981' },
  { id: 'vfs', name: '存储服务', icon: 'database', count: 6, color: '#f59e0b' },
  { id: 'org', name: '组织服务', icon: 'users', count: 6, color: '#ef4444' },
  { id: 'media', name: '媒体发布', icon: 'edit', count: 5, color: '#ec4899' },
  { id: 'business', name: '业务场景', icon: 'briefcase', count: 5, color: '#6366f1' },
  { id: 'llm', name: 'LLM服务', icon: 'brain', count: 3, color: '#14b8a6' },
  { id: 'payment', name: '支付服务', icon: 'credit-card', count: 3, color: '#f97316' },
  { id: 'knowledge', name: '知识服务', icon: 'book', count: 1, color: '#84cc16' },
  { id: 'ui', name: 'UI生成', icon: 'palette', count: 1, color: '#a855f7' },
  { id: 'scheduler', name: '调度服务', icon: 'clock', count: 1, color: '#64748b' },
  { id: 'infrastructure', name: '基础设施', icon: 'server', count: 1, color: '#78716c' }
];
```

### 3.2 图标映射

| 分类 | 推荐图标 | Lucide Icons | FontAwesome |
|------|----------|--------------|-------------|
| sys | 设置 | `Settings` | `fa-cog` |
| util | 工具 | `Wrench` | `fa-wrench` |
| nexus-ui | 布局 | `LayoutDashboard` | `fa-th-large` |
| msg | 消息 | `MessageSquare` | `fa-comment` |
| vfs | 数据库 | `Database` | `fa-database` |
| org | 用户 | `Users` | `fa-users` |
| media | 编辑 | `Edit` | `fa-edit` |
| business | 公文包 | `Briefcase` | `fa-briefcase` |
| llm | 大脑 | `Brain` | `fa-brain` |
| payment | 信用卡 | `CreditCard` | `fa-credit-card` |
| knowledge | 书本 | `BookOpen` | `fa-book` |
| ui | 调色板 | `Palette` | `fa-palette` |
| scheduler | 时钟 | `Clock` | `fa-clock` |
| infrastructure | 服务器 | `Server` | `fa-server` |

---

## 四、注意事项

### 4.1 MVP 内置功能（不计入 Skills 库）

以下功能由 MVP 直接提供，不在 skills 库统计中：
- Capability 管理
- Scene 管理
- LLM 对话（基础）
- 知识库（基础）
- 安全审计
- 监控告警

### 4.2 废弃分类（不应显示）

以下分类值已废弃，前端应忽略：
- `abs` - 场景技能类型，非分类
- `tbs` - 场景技能类型，非分类
- `ass` - 场景技能类型，非分类
- 大写格式：`SYS`, `MSG`, `VFS`, `LLM` 等

### 4.3 统计接口

前端应从以下接口获取实时统计：
```
GET /api/skills/categories/stats
```

返回格式：
```json
{
  "total": 63,
  "categories": [
    { "id": "sys", "name": "系统管理", "count": 8 },
    ...
  ]
}
```

---

**文档维护者**: Skills Team  
**最后更新**: 2026-03-18  
**版本**: v1.0.0
