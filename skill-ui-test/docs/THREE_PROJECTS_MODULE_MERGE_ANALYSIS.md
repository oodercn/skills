# 三工程模块合并分析报告

> **文档版本**: v1.0.0  
> **创建时间**: 2026-02-28  
> **目标**: 从三个 Nexus 规范工程中提取、合并、整理功能模块为 Skills

---

## 一、三工程菜单概览

### 1.1 agent-skillcenter (技能中心)

| 分类 | 模块数 | 定位 |
|------|--------|------|
| 概览仪表盘 | 1 | 技能生命周期管理 |
| 技能发现 | 3 | 市场、发现源、搜索 |
| 技能管理 | 4 | 安装、依赖、版本 |
| 技能托管 | 7 | 云托管、K8s、弹性伸缩 |
| 运行监控 | 4 | 实时监控、执行历史、日志 |
| 技能编排 | 5 | 模板、场景、流程设计 |
| 网络安全 | 3 | 网络、P2P、安全 |
| 个人中心 | 4 | 个人概览、技能、共享 |
| 管理中心 | 3 | 用户、认证 |
| 系统设置 | 3 | 配置、配额、存储 |
| **总计** | **37** | |

### 1.2 ooder-Nexus (个人版)

| 分类 | 模块数 | 定位 |
|------|--------|------|
| 我的能力 | 2 | 能力包、网络概览 |
| 个人中心 | 5 | 仪表盘、分享、身份、执行、帮助 |
| 协同协作 | 5 | 消息、联系人、场景、群组、文件 |
| 系统配置 | 4 | 能力约束、协作关系、健康、审计 |
| 资源管理 | 3 | 能力中心、LLM、存储 |
| 网络管理 | 5 | IP、流量、配置、设备、远程 |
| 协议管理 | 5 | 协作、发现、登录、观察、域 |
| Nexus管理 | 5 | 仪表盘、系统、P2P、安全、终端代理 |
| 任务管理 | 2 | 数据抽取、列表抽取 |
| 管理后台 | 5 | 仪表盘、技能审核、用户、存储、远程 |
| 安全管理 | 2 | 防火墙、访问控制 |
| **总计** | **43** | |

### 1.3 ooder-Nexus-Enterprise (企业版)

| 分类 | 模块数 | 定位 |
|------|--------|------|
| 工作台 | 1 | 企业IT管理视角 |
| 技能中心 | 6 | 市场、安装、同步、上传、分类 |
| LLM服务 | 4 | 对话、函数、嵌入、管理 |
| 资源管理 | 5 | Nexus节点、MCP Agent、能力服务、网络节点、链路 |
| 存储管理 | 6 | 类型、浏览、共享、接收、监控、清理 |
| 安全中心 | 7 | 用户、角色、密钥、证书、令牌 |
| 网络管理 | 7 | 概览、拓扑、设备、流量、IP、访问、防火墙 |
| 通信管理 | 5 | 命令转发、P2P审批/监控/可视化、权限 |
| 消息命令中心 | 5 | 推送、管理、Topic、命令、监控 |
| 即时通讯 | 3 | 会话、联系人、群组 |
| 企业业务 | 5 | 集成、分类、场景、组织架构 |
| 监控运维 | 7 | 仪表盘、拓扑、路由、资源、健康、日志 |
| 场景管理 | 6 | 列表、监控、流程、配置、历史、编排 |
| 数据源管理 | 3 | 配置、同步、提取任务 |
| 协作协调 | 3 | 请求、分配、跟踪 |
| 异常处理 | 3 | 检测、纠正、干预 |
| 路由管理 | 4 | 路由、协议、端路由、端代理 |
| OpenWrt设备 | 7 | 仪表盘、状态、网络、IP、配置、命令、黑名单 |
| 系统配置 | 5 | 会话、缓存、初始化、策略、应用 |
| **总计** | **89** | |

---

## 二、功能模块合并分析

### 2.1 高重复度模块 (需合并)

| 功能模块 | Nexus | Enterprise | SkillCenter | 最全版本 | 合并策略 |
|----------|:-----:|:----------:|:-----------:|----------|----------|
| **技能市场** | ❌ | ✅ | ✅ | Enterprise | 合并到 skill-market |
| **已安装技能** | ✅ | ✅ | ✅ | SkillCenter | 合并到 skill-management |
| **场景管理** | ✅ | ✅ | ✅ | Enterprise | 合并到 skill-scene |
| **网络管理** | ✅ | ✅ | ✅ | Enterprise | 合并到 skill-network |
| **存储管理** | ✅ | ✅ | ✅ | Enterprise | 合并到 skill-vfs-* |
| **用户管理** | ✅ | ✅ | ✅ | Enterprise | 合并到 skill-user-auth |
| **P2P网络** | ✅ | ✅ | ✅ | Enterprise | 合并到 skill-p2p |
| **健康检查** | ✅ | ✅ | ✅ | Nexus | 合并到 skill-health |
| **审计日志** | ✅ | ❌ | ❌ | Nexus | 保留 skill-audit |
| **LLM服务** | ✅ | ✅ | ❌ | Enterprise | 合并到 skill-llm-* |
| **即时通讯** | ✅ | ✅ | ❌ | Enterprise | 合并到 skill-im |
| **消息推送** | ❌ | ✅ | ❌ | Enterprise | 合并到 skill-msg |
| **K8s托管** | ❌ | ❌ | ✅ | SkillCenter | 保留 skill-k8s |
| **云托管** | ❌ | ❌ | ✅ | SkillCenter | 保留 skill-hosting |

### 2.2 中重复度模块 (需补充)

| 功能模块 | Nexus | Enterprise | SkillCenter | 合并策略 |
|----------|:-----:|:----------:|:-----------:|----------|
| **能力管理** | ✅ | ✅ | ❌ | 创建 skill-capability |
| **协作协调** | ✅ | ✅ | ❌ | 创建 skill-collaboration |
| **协议管理** | ✅ | ❌ | ❌ | 创建 skill-protocol |
| **任务管理** | ✅ | ✅ | ❌ | 创建 skill-task |
| **数据源管理** | ❌ | ✅ | ❌ | 创建 skill-datasource |
| **异常处理** | ❌ | ✅ | ❌ | 创建 skill-anomaly |
| **路由管理** | ❌ | ✅ | ❌ | 创建 skill-route |

### 2.3 独有模块 (保留)

| 工程来源 | 独有模块 | 处理方式 |
|----------|----------|----------|
| **Nexus** | 协议管理(5)、任务管理(2) | 创建独立 skill |
| **Enterprise** | OpenWrt(7)、数据源(3)、异常(3)、路由(4) | 创建独立 skill |
| **SkillCenter** | K8s托管(4)、云托管(2)、技能编排(5) | 已存在 skill |

---

## 三、Skills 分类规划

### 3.1 核心技能 (Core Skills)

| Skill ID | 名称 | 来源 | 优先级 |
|----------|------|------|--------|
| skill-market | 技能市场 | Enterprise | P0 |
| skill-management | 技能管理 | SkillCenter | P0 |
| skill-scene | 场景管理 | Enterprise | P0 |
| skill-capability | 能力管理 | Nexus+Enterprise | P0 |
| skill-user-auth | 用户认证 | Enterprise | P0 |

### 3.2 基础设施技能 (Infrastructure Skills)

| Skill ID | 名称 | 来源 | 优先级 |
|----------|------|------|--------|
| skill-network | 网络管理 | Enterprise | P1 |
| skill-p2p | P2P网络 | Enterprise | P1 |
| skill-k8s | K8s托管 | SkillCenter | P1 |
| skill-hosting | 云托管 | SkillCenter | P1 |
| skill-route | 路由管理 | Enterprise | P1 |

### 3.3 存储技能 (Storage Skills)

| Skill ID | 名称 | 来源 | 优先级 |
|----------|------|------|--------|
| skill-vfs-local | 本地存储 | 已存在 | P1 |
| skill-vfs-database | 数据库存储 | 已存在 | P1 |
| skill-vfs-minio | MinIO存储 | 已存在 | P2 |
| skill-datasource | 数据源管理 | Enterprise | P2 |

### 3.4 通信技能 (Communication Skills)

| Skill ID | 名称 | 来源 | 优先级 |
|----------|------|------|--------|
| skill-im | 即时通讯 | Enterprise | P1 |
| skill-msg | 消息推送 | Enterprise | P1 |
| skill-mqtt | MQTT服务 | 已存在 | P1 |
| skill-collaboration | 协作协调 | Nexus+Enterprise | P1 |

### 3.5 AI技能 (AI Skills)

| Skill ID | 名称 | 来源 | 优先级 |
|----------|------|------|--------|
| skill-llm-volcengine | 火山引擎 | 已存在 | P1 |
| skill-llm-qianwen | 通义千问 | 已存在 | P1 |
| skill-llm-deepseek | DeepSeek | 已存在 | P1 |
| skill-llm-openai | OpenAI | 待创建 | P2 |

### 3.6 系统技能 (System Skills)

| Skill ID | 名称 | 来源 | 优先级 |
|----------|------|------|--------|
| skill-health | 健康检查 | Nexus | P1 |
| skill-audit | 审计日志 | Nexus | P1 |
| skill-monitor | 监控运维 | Enterprise | P1 |
| skill-anomaly | 异常处理 | Enterprise | P2 |
| skill-task | 任务管理 | Nexus+Enterprise | P2 |

### 3.7 协议技能 (Protocol Skills)

| Skill ID | 名称 | 来源 | 优先级 |
|----------|------|------|--------|
| skill-protocol-collaboration | 协作协议 | Nexus | P2 |
| skill-protocol-discovery | 发现协议 | Nexus | P2 |
| skill-protocol-login | 登录协议 | Nexus | P2 |
| skill-protocol-observation | 观察协议 | Nexus | P2 |

### 3.8 设备技能 (Device Skills)

| Skill ID | 名称 | 来源 | 优先级 |
|----------|------|------|--------|
| skill-openwrt | OpenWrt设备 | Enterprise | P2 |
| skill-remote-terminal | 远程终端 | Nexus | P2 |

---

## 四、页面 URL 映射表

### 4.1 技能管理类

| 页面 | URL | 来源 | 目标 Skill |
|------|-----|------|------------|
| 技能市场 | /console/pages/llm-integration/skill-market.html | Enterprise | skill-market |
| 已安装技能 | /console/pages/skillcenter/installed-skills.html | Enterprise | skill-management |
| 技能搜索 | /console/pages/skill-search.html | SkillCenter | skill-market |
| 发现源管理 | /console/pages/discovery-sources.html | SkillCenter | skill-market |

### 4.2 场景管理类

| 页面 | URL | 来源 | 目标 Skill |
|------|-----|------|------------|
| 场景列表 | /console/pages/scene/list.html | Enterprise | skill-scene |
| 场景管理 | /console/pages/admin/scene-management.html | SkillCenter | skill-scene |
| 场景群组 | /console/pages/admin/scene-group-management.html | SkillCenter | skill-scene |
| 场景编排 | /console/pages/nexus/scenario-management.html | Enterprise | skill-scene |
| 流程设计 | /console/pages/flow-design.html | SkillCenter | skill-scene |

### 4.3 能力管理类

| 页面 | URL | 来源 | 目标 Skill |
|------|-----|------|------------|
| 能力约束 | /console/pages/scene/capability-list.html | Nexus | skill-capability |
| 能力服务 | /console/pages/enexus/capability-registry.html | Enterprise | skill-capability |
| 能力中心 | /console/pages/skillcenter-sync/skill-categories.html | Nexus | skill-capability |

### 4.4 网络管理类

| 页面 | URL | 来源 | 目标 Skill |
|------|-----|------|------------|
| 网络概览 | /console/pages/network/network-overview.html | Nexus+Enterprise | skill-network |
| 网络拓扑 | /console/pages/nexus/network-topology.html | Enterprise | skill-network |
| IP管理 | /console/pages/network/ip-management.html | Nexus+Enterprise | skill-network |
| 流量监控 | /console/pages/network/traffic-monitor.html | Nexus+Enterprise | skill-network |
| P2P网络 | /console/pages/p2p-network.html | SkillCenter | skill-p2p |
| P2P可视化 | /console/pages/nexus/p2p-visualization.html | Enterprise | skill-p2p |

### 4.5 存储管理类

| 页面 | URL | 来源 | 目标 Skill |
|------|-----|------|------------|
| 存储管理 | /console/pages/storage/storage-management.html | Nexus | skill-vfs-* |
| 文件浏览 | /console/pages/storage/file-browser.html | Enterprise | skill-vfs-local |
| 共享文件 | /console/pages/storage/shared-files.html | Enterprise | skill-vfs-local |

### 4.6 安全管理类

| 页面 | URL | 来源 | 目标 Skill |
|------|-----|------|------------|
| 用户管理 | /console/pages/enexus/user-management.html | Enterprise | skill-user-auth |
| 角色管理 | /console/pages/enexus/role-management.html | Enterprise | skill-user-auth |
| 访问控制 | /console/pages/security/access-control.html | Nexus | skill-security |
| 防火墙 | /console/pages/security/firewall.html | Nexus | skill-security |

### 4.7 监控运维类

| 页面 | URL | 来源 | 目标 Skill |
|------|-----|------|------------|
| 健康检查 | /console/pages/nexus/health-check.html | Nexus+Enterprise | skill-health |
| 审计日志 | /console/pages/audit/audit-logs.html | Nexus | skill-audit |
| 实时监控 | /console/pages/realtime-monitor.html | SkillCenter | skill-monitor |
| 日志查看 | /console/pages/log-viewer.html | SkillCenter | skill-monitor |

---

## 五、合并执行计划

### Phase 1: 核心技能合并 (第1周)

| 任务 | 源工程 | 目标 |
|------|--------|------|
| 技能市场合并 | Enterprise + SkillCenter | skill-market |
| 技能管理合并 | SkillCenter | skill-management |
| 场景管理合并 | Enterprise + SkillCenter | skill-scene |
| 能力管理创建 | Nexus + Enterprise | skill-capability |

### Phase 2: 基础设施合并 (第2周)

| 任务 | 源工程 | 目标 |
|------|--------|------|
| 网络管理合并 | Enterprise | skill-network |
| P2P网络合并 | Enterprise + SkillCenter | skill-p2p |
| K8s托管完善 | SkillCenter | skill-k8s |
| 云托管完善 | SkillCenter | skill-hosting |

### Phase 3: 通信协作合并 (第3周)

| 任务 | 源工程 | 目标 |
|------|--------|------|
| 即时通讯合并 | Enterprise | skill-im |
| 消息推送合并 | Enterprise | skill-msg |
| 协作协调创建 | Nexus + Enterprise | skill-collaboration |

### Phase 4: 系统管理合并 (第4周)

| 任务 | 源工程 | 目标 |
|------|--------|------|
| 健康检查完善 | Nexus | skill-health |
| 审计日志完善 | Nexus | skill-audit |
| 监控运维合并 | Enterprise | skill-monitor |
| 协议管理创建 | Nexus | skill-protocol-* |

---

## 六、包命名规范

### 6.1 包结构

```
net.ooder.skill.{skill-name}/
├── controller/
│   └── {SkillName}Controller.java
├── service/
│   ├── {SkillName}Service.java
│   └── impl/{SkillName}ServiceImpl.java
├── dto/
│   ├── {Entity}.java
│   └── {Entity}Request.java
├── entity/
│   └── {Entity}.java
├── repository/
│   └── {Entity}Repository.java
├── config/
│   └── {SkillName}Config.java
└── {SkillName}Application.java
```

### 6.2 命名约定

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| Skill ID | skill-{category}-{name} | skill-scene, skill-network |
| 包名 | net.ooder.skill.{name} | net.ooder.skill.scene |
| Controller | {Name}Controller | SceneController |
| Service | {Name}Service | SceneService |
| DTO | {Entity}{Purpose} | SceneCreateRequest |

---

## 七、验收标准

### 7.1 功能验收

- [ ] 所有页面 URL 可访问
- [ ] 所有 API 端点响应正常
- [ ] 前后端数据流完整

### 7.2 规范验收

- [ ] 符合 Nexus 架构规范
- [ ] 符合 Skill Manifest 规范
- [ ] 代码风格一致

### 7.3 测试验收

- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 用户故事闭环测试通过

---

**文档版本**: v1.0.0  
**创建时间**: 2026-02-28  
**适用团队**: Nexus, Enterprise, SkillCenter
