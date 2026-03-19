# Ooder Skills 场景能力筛选分析

> **文档版本**: 1.0.0  
> **编写日期**: 2026-03-05  
> **分析范围**: 现有技能库中具备或应该具备场景能力的技能

---

## 一、筛选标准

### 1.1 场景技能的判定标准

根据 `CapabilityDiscoveryServiceImpl` 和 `USER_CLOSED_LOOP_STORIES_V2.3.md` 的定义：

| 标准 | 说明 |
|------|------|
| **标准1** | `metadata.type = scene-skill` |
| **标准2** | 存在 `sceneCapabilities` 定义 |
| **标准3** | `mainFirst = true` 自驱标识 |
| **标准4** | 具备完整的业务场景语义（可发现、可分发、可自驱） |

### 1.2 应该改造为场景技能的特征

- 具备完整的业务闭环流程
- 需要主导者/协作者参与
- 有明确的驱动条件（定时/事件）
- 可独立部署和运行
- 有明确的业务目标

---

## 二、现有技能筛选结果

### 2.1 已具备场景能力的技能（2个）✅

| 技能ID | 技能名称 | 当前类型 | 场景能力ID | 驱动方式 | 业务场景 |
|--------|---------|---------|-----------|---------|---------|
| **skill-llm-chat** | LLM智能对话 | scene-skill ✅ | scene-llm-chat | intent-receiver + event-listener | 多轮对话场景 |
| **skill-knowledge-qa** | 知识问答 | scene-skill ✅ | scene-knowledge-qa | intent-receiver + event-listener + scheduler | 知识库问答场景 |

#### skill-llm-chat 详情
```yaml
# 已具备完整的场景技能结构
spec:
  type: scene-skill
  sceneCapabilities:
    - id: scene-llm-chat
      mainFirst: true
      mainFirstConfig:
        selfCheck: [...]
        selfStart: [...]
        selfDrive:
          eventRules: [...]
          capabilityChains: [...]
```

#### skill-knowledge-qa 详情
```yaml
# 已具备完整的场景技能结构
spec:
  type: scene-skill
  sceneCapabilities:
    - id: scene-knowledge-qa
      mainFirst: true
      mainFirstConfig:
        selfCheck: [...]
        selfStart: [...]
        selfDrive:
          scheduleRules: [...]  # 定时重建索引
          eventRules: [...]      # 文档上传事件
          capabilityChains: [...]
```

---

### 2.2 应该改造为场景技能的候选（11个）⚠️

以下技能具备业务场景特征，建议改造为 `scene-skill` 类型：

| 技能ID | 技能名称 | 当前类型 | 建议场景能力ID | 业务场景描述 | 驱动条件建议 | 优先级 |
|--------|---------|---------|---------------|-------------|-------------|--------|
| **skill-collaboration** | 协作场景服务 | service-skill | scene-collaboration | 团队协作场景管理 | event-listener | P0 |
| **skill-agent** | Agent管理 | service-skill | scene-agent-mgmt | Agent网络管理场景 | scheduler + event-listener | P0 |
| **skill-openwrt** | OpenWrt管理 | service-skill | scene-openwrt | 路由器管理场景 | event-listener | P1 |
| **skill-k8s** | K8s集群管理 | service-skill | scene-k8s-mgmt | K8s集群运维场景 | scheduler + event-listener | P1 |
| **skill-hosting** | 托管服务 | service-skill | scene-hosting | 容器托管场景 | scheduler + event-listener | P1 |
| **skill-market** | 技能市场 | service-skill | scene-skill-market | 技能发现安装场景 | event-listener | P2 |
| **skill-share** | 技能分享 | service-skill | scene-skill-share | 技能分享场景 | event-listener | P2 |
| **skill-monitor** | 监控服务 | system-service | scene-monitoring | 系统监控场景 | scheduler | P1 |
| **skill-health** | 健康检查 | system-service | scene-health-check | 健康检查场景 | scheduler | P1 |
| **skill-network** | 网络服务 | service-skill | scene-network | 网络管理场景 | event-listener | P2 |
| **skill-mqtt** | MQTT服务 | service-skill | scene-mqtt | IoT消息场景 | event-listener | P2 |

---

### 2.3 候选技能详细分析

#### 2.3.1 skill-collaboration（协作场景服务）- P0

**当前状态**: service-skill  
**建议改造**: scene-skill  
**业务场景**: 团队协作场景管理

**驱动条件设计**:
```yaml
mainFirstConfig:
  selfDrive:
    eventRules:
      - event: member.joined
        action: onboarding-flow
      - event: member.left
        action: cleanup-flow
      - event: scene.expiring
        action: renewal-reminder-flow
    capabilityChains:
      onboarding-flow:
        - capability: member-auth
        - capability: key-generation
        - capability: welcome-notification
```

**参与者角色**:
- 主导者: 场景创建者（OWNER）
- 协作者: 团队成员（MEMBER/ADMIN）

---

#### 2.3.2 skill-agent（Agent管理）- P0

**当前状态**: service-skill  
**建议改造**: scene-skill  
**业务场景**: Agent网络管理场景

**驱动条件设计**:
```yaml
mainFirstConfig:
  selfDrive:
    scheduleRules:
      - trigger: "0 */5 * * * *"  # 每5分钟心跳检查
        action: heartbeat-check-flow
    eventRules:
      - event: agent.offline
        action: offline-alert-flow
      - event: command.completed
        action: result-notification-flow
    capabilityChains:
      heartbeat-check-flow:
        - capability: agent-ping
        - capability: status-update
        - capability: alert-if-failed
```

**参与者角色**:
- 主导者: 网络管理员
- 协作者: 运维人员

---

#### 2.3.3 skill-openwrt（OpenWrt管理）- P1

**当前状态**: service-skill  
**建议改造**: scene-skill  
**业务场景**: 路由器管理场景

**驱动条件设计**:
```yaml
mainFirstConfig:
  selfDrive:
    scheduleRules:
      - trigger: "0 0 * * *"  # 每日备份
        action: config-backup-flow
    eventRules:
      - event: router.disconnected
        action: reconnect-flow
      - event: bandwidth.threshold-exceeded
        action: alert-flow
```

---

#### 2.3.4 skill-k8s（K8s集群管理）- P1

**当前状态**: service-skill  
**建议改造**: scene-skill  
**业务场景**: K8s集群运维场景

**驱动条件设计**:
```yaml
mainFirstConfig:
  selfDrive:
    scheduleRules:
      - trigger: "0 */10 * * * *"  # 每10分钟健康检查
        action: cluster-health-check-flow
    eventRules:
      - event: pod.crashed
        action: pod-restart-flow
      - event: node.not-ready
        action: node-alert-flow
```

---

#### 2.3.5 skill-hosting（托管服务）- P1

**当前状态**: service-skill  
**建议改造**: scene-skill  
**业务场景**: 容器托管场景

**驱动条件设计**:
```yaml
mainFirstConfig:
  selfDrive:
    scheduleRules:
      - trigger: "0 * * * *"  # 每小时健康检查
        action: instance-health-check-flow
    eventRules:
      - event: instance.failed
        action: auto-restart-flow
      - event: resource.threshold-exceeded
        action: scale-alert-flow
```

---

#### 2.3.6 skill-monitor（监控服务）- P1

**当前状态**: system-service  
**建议改造**: scene-skill  
**业务场景**: 系统监控场景

**驱动条件设计**:
```yaml
mainFirstConfig:
  selfDrive:
    scheduleRules:
      - trigger: "0 */5 * * * *"  # 每5分钟采集指标
        action: metrics-collect-flow
      - trigger: "0 0 * * *"      # 每日生成报告
        action: daily-report-flow
    eventRules:
      - event: alert.triggered
        action: alert-notification-flow
```

---

#### 2.3.7 skill-health（健康检查）- P1

**当前状态**: system-service  
**建议改造**: scene-skill  
**业务场景**: 健康检查场景

**驱动条件设计**:
```yaml
mainFirstConfig:
  selfDrive:
    scheduleRules:
      - trigger: "0 */5 * * * *"  # 每5分钟健康检查
        action: health-check-flow
      - trigger: "0 0 * * 0"      # 每周生成报告
        action: weekly-report-flow
```

---

### 2.4 保持为服务技能的技能（22个）✓

以下技能更适合保持为 `service-skill` 或 `system-service` 类型，作为场景技能的依赖组件：

| 技能ID | 技能名称 | 当前类型 | 说明 |
|--------|---------|---------|------|
| **skill-scene** | 场景管理 | service-skill | 场景管理核心服务，被其他场景技能依赖 |
| **skill-capability** | 能力管理 | service-skill | 能力注册发现服务，基础服务 |
| **skill-user-auth** | 用户认证 | service-skill | 认证服务，基础服务 |
| **skill-knowledge-base** | 知识库核心 | system-service | 知识库存储服务，被skill-knowledge-qa依赖 |
| **skill-rag** | RAG检索增强 | system-service | RAG服务，被skill-knowledge-qa依赖 |
| **skill-local-knowledge** | 本地知识库 | system-service | 本地存储服务 |
| **skill-document-processor** | 文档处理 | system-service | 文档解析服务 |
| **skill-llm-conversation** | LLM对话服务 | service-skill | 底层对话服务，被skill-llm-chat依赖 |
| **skill-llm-context-builder** | 上下文构建 | service-skill | 上下文服务，被skill-llm-chat依赖 |
| **skill-llm-config-manager** | LLM配置管理 | service-skill | 配置管理服务 |
| **skill-llm-assistant-ui** | LLM助手UI | ui-skill | UI展示技能 |
| **skill-llm-management-ui** | LLM管理UI | ui-skill | UI展示技能 |
| **skill-org-dingding** | 钉钉集成 | integration-skill | 组织集成服务 |
| **skill-org-feishu** | 飞书集成 | integration-skill | 组织集成服务 |
| **skill-org-wecom** | 企业微信集成 | integration-skill | 组织集成服务 |
| **skill-org-ldap** | LDAP集成 | integration-skill | 组织集成服务 |
| **skill-msg** | 消息服务 | service-skill | 消息通知服务 |
| **skill-vfs-local** | 本地文件存储 | storage-skill | 存储服务 |
| **skill-vfs-minio** | MinIO存储 | storage-skill | 存储服务 |
| **skill-vfs-oss** | 阿里云OSS | storage-skill | 存储服务 |
| **skill-vfs-s3** | AWS S3 | storage-skill | 存储服务 |
| **skill-vfs-database** | 数据库存储 | storage-skill | 存储服务 |

---

### 2.5 UI展示技能（7个）🎨

UI技能保持为 `ui-skill` 类型，但应该与场景技能关联：

| 技能ID | 技能名称 | 当前类型 | 关联场景技能 |
|--------|---------|---------|-------------|
| **skill-knowledge-ui** | 知识库UI | ui-skill | skill-knowledge-qa |
| **skill-llm-assistant-ui** | LLM助手UI | ui-skill | skill-llm-chat |
| **skill-llm-management-ui** | LLM管理UI | ui-skill | - |
| **skill-personal-dashboard-nexus-ui** | 个人仪表板UI | ui-skill | - |
| **skill-nexus-dashboard-nexus-ui** | Nexus仪表板UI | ui-skill | - |
| **skill-nexus-health-check-nexus-ui** | 健康检查UI | ui-skill | skill-health |
| **skill-nexus-system-status-nexus-ui** | 系统状态UI | ui-skill | skill-monitor |
| **skill-storage-management-nexus-ui** | 存储管理UI | ui-skill | - |

---

## 三、改造优先级规划

### 3.1 改造路线图

```
Phase 1 (P0) - 核心场景技能
├── skill-collaboration → scene-collaboration
└── skill-agent → scene-agent-mgmt

Phase 2 (P1) - 运维场景技能
├── skill-monitor → scene-monitoring
├── skill-health → scene-health-check
├── skill-openwrt → scene-openwrt
├── skill-k8s → scene-k8s-mgmt
└── skill-hosting → scene-hosting

Phase 3 (P2) - 扩展场景技能
├── skill-market → scene-skill-market
├── skill-share → scene-skill-share
├── skill-network → scene-network
└── skill-mqtt → scene-mqtt
```

### 3.2 改造工作量评估

| 技能ID | 改造复杂度 | 预估工期 | 主要工作 |
|--------|-----------|---------|---------|
| skill-collaboration | 中 | 5天 | 添加sceneCapabilities、mainFirstConfig |
| skill-agent | 中 | 5天 | 添加sceneCapabilities、mainFirstConfig |
| skill-monitor | 低 | 3天 | 添加sceneCapabilities、scheduleRules |
| skill-health | 低 | 3天 | 添加sceneCapabilities、scheduleRules |
| skill-openwrt | 中 | 4天 | 添加sceneCapabilities、eventRules |
| skill-k8s | 中 | 4天 | 添加sceneCapabilities、eventRules |
| skill-hosting | 中 | 4天 | 添加sceneCapabilities、scheduleRules |
| skill-market | 低 | 2天 | 添加sceneCapabilities |
| skill-share | 低 | 2天 | 添加sceneCapabilities |
| skill-network | 低 | 2天 | 添加sceneCapabilities |
| skill-mqtt | 低 | 2天 | 添加sceneCapabilities |

---

## 四、改造检查清单

### 4.1 skill.yaml 改造检查项

- [ ] `metadata.type` 改为 `scene-skill`
- [ ] `spec.type` 改为 `scene-skill`
- [ ] 添加 `sceneCapabilities` 定义
- [ ] 设置 `mainFirst: true`
- [ ] 添加完整的 `mainFirstConfig`
  - [ ] `selfCheck` 配置
  - [ ] `selfStart` 配置
  - [ ] `startCollaboration` 配置（如有协作需求）
  - [ ] `selfDrive` 配置
    - [ ] `scheduleRules`（定时驱动）
    - [ ] `eventRules`（事件驱动）
    - [ ] `capabilityChains`（能力链）
- [ ] 添加 `capabilities` 原子能力定义
- [ ] 添加 `collaborativeCapabilities`（如有协作需求）
- [ ] 更新 `endpoints` 配置

### 4.2 代码改造检查项

- [ ] 实现场景激活逻辑
- [ ] 实现驱动条件处理
- [ ] 实现参与者管理
- [ ] 实现自驱执行逻辑
- [ ] 实现协作能力调用
- [ ] 添加场景状态管理
- [ ] 添加入网动作处理

---

## 五、总结

### 5.1 统计汇总

| 分类 | 数量 | 说明 |
|------|------|------|
| **已具备场景能力** | 2 | skill-llm-chat, skill-knowledge-qa |
| **应该改造为场景技能** | 11 | 具备业务场景特征的服务技能 |
| **保持为服务技能** | 22 | 基础服务、存储、集成类技能 |
| **UI展示技能** | 7 | 与场景技能关联的UI技能 |
| **总计** | **42** | 现有技能库总量 |

### 5.2 关键建议

1. **优先改造核心场景技能**：skill-collaboration 和 skill-agent 是最需要优先改造的技能，它们具备完整的业务闭环和参与者管理需求。

2. **运维类技能逐步改造**：skill-monitor、skill-health、skill-k8s 等运维类技能具备定时驱动特征，适合改造为场景技能。

3. **保持服务技能的稳定性**：skill-scene、skill-capability、skill-user-auth 等基础服务技能应保持为 service-skill，作为场景技能的依赖基础。

4. **UI技能与场景技能关联**：UI技能应保持为 ui-skill，但需要在配置中明确关联对应的场景技能。

---

**文档编写者**: Ooder 开发团队  
**文档日期**: 2026-03-05  
**分析依据**: CapabilityDiscoveryServiceImpl.java, USER_CLOSED_LOOP_STORIES_V2.3.md, 现有skill.yaml配置
