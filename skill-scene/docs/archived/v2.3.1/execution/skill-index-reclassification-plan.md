# skill-index.yaml 重新分类方案

## 一、当前分类问题

### 1.1 分类过于宽泛

当前 `SERVICE` 分类包含 71 个技能（占 53.4%），过于宽泛，不利于技能发现和管理。

### 1.2 分类不一致

- 部分分类使用小写格式
- `MESSAGING` 与 `COMMUNICATION` 功能重叠
- `SCENE` 分类定义不清晰

### 1.3 缺少业务领域分类

当前分类偏向技术类型，缺少业务领域维度。

---

## 二、重新分类方案

### 2.1 双维度分类体系

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        双维度分类体系                                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   维度一：功能分类 (category)                                                  │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  KNOWLEDGE    - 知识管理 (知识库、RAG、向量存储)                          │   │
│   │  LLM          - AI模型服务 (对话、嵌入、配置)                             │   │
│   │  DATA         - 数据存储 (文件系统、对象存储、数据库)                       │   │
│   │  COMMUNICATION- 通讯服务 (消息、邮件、通知)                               │   │
│   │  WORKFLOW     - 业务流程 (支付、媒体发布、审批)                           │   │
│   │  SECURITY     - 安全服务 (认证、授权、审计)                               │   │
│   │  MONITOR      - 监控运维 (健康检查、日志、指标)                           │   │
│   │  TOOL         - 工具服务 (文档处理、通用工具)                             │   │
│   │  UI           - 界面服务 (仪表盘、管理界面)                               │   │
│   │  ORG          - 组织管理 (用户、部门、角色)                               │   │
│   │  IOT          - 物联网 (设备、边缘计算)                                   │   │
│   │  COLLABORATION- 协作服务 (会议、文档协作、任务)                           │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│   维度二：业务领域 (domain)                                                    │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │  core         - 核心平台服务                                            │   │
│   │  ai           - AI能力服务                                              │   │
│   │  storage      - 存储服务                                                │   │
│   │  messaging    - 消息服务                                                │   │
│   │  payment      - 支付服务                                                │   │
│   │  media        - 媒体服务                                                │   │
│   │  hr           - 人力资源                                                │   │
│   │  crm          - 客户管理                                                │   │
│   │  finance      - 财务管理                                                │   │
│   │  project      - 项目管理                                                │   │
│   │  iot          - 物联网                                                  │   │
│   │  nexus        - Nexus平台                                               │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 新分类定义

| 分类 | 说明 | 原分类映射 |
|------|------|------------|
| **KNOWLEDGE** | 知识管理服务 | KNOWLEDGE |
| **LLM** | AI模型服务 | LLM |
| **DATA** | 数据存储服务 | DATA |
| **COMMUNICATION** | 通讯服务 | COMMUNICATION + MESSAGING |
| **WORKFLOW** | 业务流程服务 | WORKFLOW |
| **SECURITY** | 安全服务 | SERVICE (认证/授权/审计) |
| **MONITOR** | 监控运维服务 | SERVICE (监控/健康检查) |
| **TOOL** | 工具服务 | TOOL |
| **UI** | 界面服务 | UI |
| **ORG** | 组织管理服务 | SERVICE (组织/用户) |
| **IOT** | 物联网服务 | IOT |
| **COLLABORATION** | 协作服务 | COLLABORATION |

---

## 三、技能重新分类映射

### 3.1 KNOWLEDGE (知识管理)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-knowledge-base | 知识库核心服务 | ai |
| skill-rag | RAG检索增强 | ai |
| skill-local-knowledge | 本地知识服务 | ai |
| skill-vector-sqlite | SQLite向量存储 | ai |
| skill-knowledge-qa | 知识问答场景能力 | ai |
| skill-knowledge-ui | 知识库管理界面 | nexus |

### 3.2 LLM (AI模型服务)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-llm-chat | LLM智能对话场景能力 | ai |
| skill-llm-conversation | LLM对话服务 | ai |
| skill-llm-context-builder | 上下文构建服务 | ai |
| skill-llm-config-manager | LLM配置管理 | ai |
| skill-llm-volcengine | 火山引擎豆包LLM Provider | ai |
| skill-llm-qianwen | 通义千问LLM Provider | ai |
| skill-llm-deepseek | DeepSeek LLM Provider | ai |
| skill-llm-assistant-ui | LLM智能助手界面 | nexus |
| skill-llm-management-ui | LLM管理界面 | nexus |

### 3.3 DATA (数据存储)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-vfs-base | VFS基础服务 | storage |
| skill-vfs-database | 数据库存储服务 | storage |
| skill-vfs-local | 本地文件系统存储服务 | storage |
| skill-vfs-minio | MinIO存储服务 | storage |
| skill-vfs-oss | 阿里云OSS存储服务 | storage |
| skill-vfs-s3 | AWS S3存储服务 | storage |
| skill-storage-management-ui | 存储管理界面 | nexus |

### 3.4 COMMUNICATION (通讯服务)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-mqtt | MQTT服务 | messaging |
| skill-im | 即时通讯服务 | messaging |
| skill-group | 群组管理服务 | messaging |
| skill-msg-service | 消息服务 | messaging |
| skill-msg | 消息服务 | messaging |
| skill-notify | 通知服务 | messaging |
| skill-email | 邮件服务 | messaging |

### 3.5 WORKFLOW (业务流程)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-payment-alipay | 支付宝支付Provider | payment |
| skill-payment-wechat | 微信支付Provider | payment |
| skill-payment-unionpay | 银联支付Provider | payment |
| skill-media-wechat | 微信公众号发布Provider | media |
| skill-media-weibo | 微博发布Provider | media |
| skill-media-zhihu | 知乎发布Provider | media |
| skill-media-toutiao | 头条发布Provider | media |
| skill-media-xiaohongshu | 小红书发布Provider | media |
| skill-business | 业务场景服务 | core |

### 3.6 SECURITY (安全服务)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-org-dingding | 钉钉组织数据集成服务 | hr |
| skill-org-feishu | 飞书组织数据集成服务 | hr |
| skill-org-wecom | 企业微信组织数据集成服务 | hr |
| skill-org-ldap | LDAP组织服务 | hr |
| skill-user-auth | 用户认证服务 | core |
| skill-security | 安全管理服务 | core |
| skill-access-control | 访问控制服务 | core |
| skill-audit | 审计服务 | core |

### 3.7 MONITOR (监控运维)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-capability | 能力管理服务 | core |
| skill-network | 网络管理服务 | core |
| skill-agent | 代理管理服务 | core |
| skill-health | 健康检查服务 | core |
| skill-protocol | 协议管理服务 | core |
| skill-openwrt | OpenWrt路由器驱动 | iot |
| skill-hosting | 托管服务 | core |
| skill-monitor | 监控服务 | core |
| skill-cmd-service | 命令监控服务 | core |
| skill-res-service | 资源管理服务 | core |
| skill-search | 搜索服务 | core |
| skill-report | 报表服务 | core |
| skill-task | 任务管理服务 | core |
| skill-k8s | Kubernetes集群管理 | core |
| skill-scheduler-quartz | Quartz调度服务 | core |
| skill-remote-terminal | 远程终端服务 | core |

### 3.8 TOOL (工具服务)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-document-processor | 文档处理服务 | core |
| skill-share | 技能分享服务 | core |
| skill-trae-solo | Trae Solo服务 | core |
| skill-market | 技能市场服务 | nexus |
| skill-collaboration | 协作场景服务 | core |
| skill-common | 技能公共库 | core |

### 3.9 UI (界面服务)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-a2ui | A2UI图转代码技能 | ai |
| skill-nexus-dashboard-nexus-ui | Nexus仪表盘界面 | nexus |
| skill-nexus-system-status-nexus-ui | Nexus系统状态界面 | nexus |
| skill-personal-dashboard-nexus-ui | 个人仪表盘界面 | nexus |
| skill-nexus-health-check-nexus-ui | Nexus健康检查界面 | nexus |

### 3.10 ORG (组织管理)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-org-base | 组织基础服务 | hr |

### 3.11 IOT (物联网)

| 技能ID | 名称 | domain |
|--------|------|--------|
| (场景定义) | 设备管理 | iot |
| (场景定义) | 数据采集 | iot |
| (场景定义) | 边缘计算 | iot |

### 3.12 COLLABORATION (协作服务)

| 技能ID | 名称 | domain |
|--------|------|--------|
| skill-im | 即时通讯服务 | messaging |
| skill-group | 群组管理服务 | messaging |
| (场景定义) | 会议协作 | core |
| (场景定义) | 文档协作 | core |
| (场景定义) | 任务协作 | core |

---

## 四、分类统计对比

### 4.1 重新分类后统计

| 分类 | 数量 | 占比 |
|------|:----:|:----:|
| **MONITOR** | 16 | 12.0% |
| **COMMUNICATION** | 7 | 5.3% |
| **LLM** | 9 | 6.8% |
| **KNOWLEDGE** | 6 | 4.5% |
| **DATA** | 7 | 5.3% |
| **WORKFLOW** | 9 | 6.8% |
| **SECURITY** | 8 | 6.0% |
| **TOOL** | 6 | 4.5% |
| **UI** | 9 | 6.8% |
| **ORG** | 1 | 0.8% |
| **IOT** | 4 | 3.0% |
| **COLLABORATION** | 3 | 2.3% |
| **待清理重复** | 4 | 3.0% |

### 4.2 对比分析

| 指标 | 原分类 | 新分类 | 改善 |
|------|:------:|:------:|:----:|
| 最大分类占比 | 53.4% (SERVICE) | 12.0% (MONITOR) | ✅ 更均衡 |
| 分类数量 | 12 | 12 | - |
| 单分类最大技能数 | 71 | 16 | ✅ 更合理 |

---

## 五、迁移脚本

```powershell
# skill-index.yaml 重新分类脚本

$filePath = "e:\github\ooder-skills\skill-index.yaml"

$bytes = [System.IO.File]::ReadAllBytes($filePath)
$content = [System.Text.Encoding]::UTF8.GetString($bytes)

# 1. 删除重复定义 (需要手动处理)

# 2. 分类映射
$categoryMappings = @{
    # SECURITY (从 SERVICE 分离)
    'skill-org-dingding' = 'SECURITY'
    'skill-org-feishu' = 'SECURITY'
    'skill-org-wecom' = 'SECURITY'
    'skill-org-ldap' = 'SECURITY'
    'skill-user-auth' = 'SECURITY'
    'skill-security' = 'SECURITY'
    'skill-access-control' = 'SECURITY'
    'skill-audit' = 'SECURITY'
    
    # MONITOR (从 SERVICE 分离)
    'skill-capability' = 'MONITOR'
    'skill-network' = 'MONITOR'
    'skill-agent' = 'MONITOR'
    'skill-health' = 'MONITOR'
    'skill-protocol' = 'MONITOR'
    'skill-hosting' = 'MONITOR'
    'skill-monitor' = 'MONITOR'
    'skill-cmd-service' = 'MONITOR'
    'skill-res-service' = 'MONITOR'
    'skill-search' = 'MONITOR'
    'skill-report' = 'MONITOR'
    'skill-task' = 'MONITOR'
    'skill-k8s' = 'MONITOR'
    'skill-scheduler-quartz' = 'MONITOR'
    'skill-remote-terminal' = 'MONITOR'
    
    # ORG (组织管理)
    'skill-org-base' = 'ORG'
    
    # MESSAGING -> COMMUNICATION
    'MESSAGING' = 'COMMUNICATION'
}

# 3. 保存文件
$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText($filePath, $content, $utf8NoBom)

Write-Host "Reclassification completed!"
```

---

## 六、执行建议

### 6.1 分阶段执行

| 阶段 | 任务 | 工作量 |
|------|------|:------:|
| **Phase 1** | 删除重复技能定义 | 1天 |
| **Phase 2** | 统一分类格式 (MESSAGING→COMMUNICATION) | 0.5天 |
| **Phase 3** | SERVICE 分类拆分 | 2天 |
| **Phase 4** | 添加 domain 字段 | 1天 |
| **Phase 5** | 验证和测试 | 1天 |

### 6.2 风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 分类变更影响现有代码 | 高 | 保持向后兼容，旧分类作为别名 |
| domain 字段新增 | 低 | 可选字段，不影响现有功能 |
| 重复技能删除 | 中 | 确认无依赖后删除 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Skills Team
