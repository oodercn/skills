# Ooder 2.3 三团队协作需求说�?
> **文档版本**: v1.0  
> **发布日期**: 2026-02-25  
> **适用范围**: SDK团队、Nexus团队、Skills团队  
> **文档状�?*: 正式发布

---

## 一、协作背�?
Ooder 2.3版本涉及三个核心团队的协同工作：
- **SDK团队**: 负责热插拔核心、A2A协议、生命周期管�?- **Nexus团队**: 负责前端组件化、CSS变量系统、A2A渲染�?- **Skills团队**: 负责规范制定、页面转换、架构设�?
本说明明确各团队的协作需求、接口契约和交付标准�?
---

## 二、协作目�?
### 2.1 总体目标

实现Ooder平台的Skills化改造，支持�?1. Skill热插拔机�?2. A2A协议通信
3. 前端组件化渲�?4. 平滑迁移现有功能

### 2.2 里程碑目�?
| 里程�?| 日期 | 目标 | 验收标准 |
|--------|------|------|----------|
| M1 | Week 2 | 基础设施就绪 | 规范发布+组件首版 |
| M2 | Week 4 | 核心能力完成 | 生命周期API+组件映射 |
| M3 | Week 6 | 简单页面转�?| 4个页面完成转�?|
| M4 | Week 8 | 集成验证 | 端到端流程跑�?|

---

## 三、团队间协作需�?
### 3.1 SDK团队 �?Nexus团队

#### 接口需�?
| 需求项 | 说明 | 优先�?| 交付时间 |
|--------|------|--------|----------|
| **A2A消息格式** | 提供消息类型定义和序列化 | P0 | Week 2 |
| **Skill状态API** | RESTful API获取Skill状�?| P0 | Week 2 |
| **CSS变量规范** | 定义标准CSS变量�?| P0 | Week 1 |
| **组件元数�?* | Skill组件配置信息 | P1 | Week 3 |

#### API契约

```yaml
# Skill状态查询接�?GET /api/v1/skills/{skillId}/state
Response:
  skillId: string
  state: INSTALLED|STARTING|RUNNING|STOPPING|ERROR
  version: string
  health:
    status: UP|DOWN
    checks: []
```

#### 协作方式
- **接口文档**: YAML格式OpenAPI规范
- **联调时间**: Week 3开始每周二下午
- **问题升级**: 24小时内响�?
---

### 3.2 SDK团队 �?Skills团队

#### 接口需�?
| 需求项 | 说明 | 优先�?| 交付时间 |
|--------|------|--------|----------|
| **skill.yaml解析** | 支持完整规范解析 | P0 | Week 2 |
| **依赖解析** | 版本冲突检�?| P1 | Week 3 |
| **生命周期事件** | 状态变更事件发�?| P1 | Week 3 |
| **发现服务** | Skill注册与发�?| P1 | Week 4 |

#### 配置契约

```yaml
# skill.yaml 解析支持
skill:
  id: string (required)
  version: SemVer (required)
  
dependencies:
  skills:
    - id: string
      version: string (^1.0.0, >=2.0.0)
      optional: boolean

lifecycle:
  startupOrder: number (0-100)
  healthCheck:
    enabled: boolean
    interval: number
```

#### 协作方式
- **规范评审**: 每周�?5:00三团队会�?- **变更通知**: 规范变更需提前48小时通知
- **测试支持**: 提供测试Skill�?
---

### 3.3 Nexus团队 �?Skills团队

#### 接口需�?
| 需求项 | 说明 | 优先�?| 交付时间 |
|--------|------|--------|----------|
| **组件接口定义** | Web Components接口规范 | P0 | Week 2 |
| **CSS变量映射** | 品牌变量到标准变量映�?| P0 | Week 2 |
| **渲染器API** | A2A组件渲染接口 | P1 | Week 3 |
| **主题系统** | 暗黑模式支持 | P1 | Week 4 |

#### 组件契约

```javascript
// Web Component接口规范
interface SkillComponent extends HTMLElement {
  // 属�?  skillId: string;
  config: object;
  
  // 方法
  refresh(): void;
  destroy(): void;
  
  // 事件
  onStateChange: (state: SkillState) => void;
  onError: (error: Error) => void;
}

// CSS变量规范
:root {
  /* 品牌�?*/
  --ooder-primary: #1890ff;
  --ooder-success: #52c41a;
  --ooder-warning: #faad14;
  --ooder-error: #f5222d;
  
  /* 背景�?*/
  --ooder-bg-primary: #ffffff;
  --ooder-bg-secondary: #f5f5f5;
  
  /* 文字�?*/
  --ooder-text-primary: rgba(0, 0, 0, 0.85);
  --ooder-text-secondary: rgba(0, 0, 0, 0.45);
}
```

#### 协作方式
- **组件评审**: 每周五组件走�?- **设计稿交�?*: Figma链接共享
- **联调支持**: 提供测试页面

---

### 3.4 Skills团队 �?SDK/Nexus团队

#### 交付物需�?
| 交付�?| 内容 | 优先�?| 截止时间 |
|--------|------|--------|----------|
| **Ooder-A2A规范v1.0** | 完整协议规范 | P0 | 本周�?|
| **Nexus页面清单** | 100+页面分类 | P0 | 本周�?|
| **组件映射�?* | Nexus→A2A组件对照 | P1 | Week 2 |
| **转换工具** | 页面转换脚本 | P2 | Week 4 |

#### 规范内容

```markdown
# Ooder-A2A规范v1.0 必须包含:

1. 消息格式规范
   - 消息类型枚举 (10�?
   - JSON Schema定义
   - 序列化规�?
2. CSS变量规范
   - 三层变量架构
   - 标准变量列表 (50+)
   - 暗黑模式变量

3. 生命周期规范
   - SkillState枚举
   - 状态转换图
   - 事件定义

4. 组件接口规范
   - Web Components标准
   - 属�?方法/事件
   - 样式隔离
```

---

## 四、关键协作节�?
### 4.1 协作时间�?
```
Week 1 (当前)
├── 周一: Skills团队完成规范文档80%
├── 周三: 三团队协调会�?(15:00)
└── 周五: 规范文档发布截止

Week 2
├── 周一: Nexus开始组件封�?├── 周二: SDK-Nexus首次联调
├── 周三: 规范评审会议
└── 周五: Week 2检查点 (CP1)

Week 3-4
├── 组件开发并行进�?├── 每周�? 进度同步�?└── 每周�? 检查点评审

Week 5-8
├── 页面转换开�?├── 集成测试
└── 问题修复
```

### 4.2 检查点定义

| 检查点 | 时间 | 参与团队 | 检查内�?|
|--------|------|----------|----------|
| **CP1** | Week 2周五 | 全体 | 规范发布+组件首版 |
| **CP2** | Week 4周五 | 全体 | 生命周期API+组件映射 |
| **CP3** | Week 6周五 | 全体 | 4个页面转换完�?|
| **CP4** | Week 8周五 | 全体 | 端到端集成验�?|

---

## 五、接口详细规�?
### 5.1 RESTful API规范

#### SDK提供接口

```yaml
# 1. Skill生命周期管理
POST   /api/v1/skills/{skillId}/install
POST   /api/v1/skills/{skillId}/uninstall
POST   /api/v1/skills/{skillId}/start
POST   /api/v1/skills/{skillId}/stop
GET    /api/v1/skills/{skillId}/state

# 2. Skill发现
GET    /api/v1/skills
GET    /api/v1/skills/{skillId}
GET    /api/v1/skills/{skillId}/capabilities

# 3. 配置管理
GET    /api/v1/skills/{skillId}/config
PUT    /api/v1/skills/{skillId}/config

# 4. 日志和监�?GET    /api/v1/skills/{skillId}/logs
GET    /api/v1/skills/{skillId}/metrics
```

#### 错误码规�?
| 错误�?| 说明 | HTTP状�?|
|--------|------|----------|
| 4001 | Skill不存�?| 404 |
| 4002 | 状态转换非�?| 400 |
| 4003 | 配置验证失败 | 400 |
| 5001 | 内部错误 | 500 |
| 5002 | 依赖未满�?| 422 |

### 5.2 WebSocket规范

```yaml
# 连接URL
wss://{host}/ws/skills/{skillId}?token={jwt}

# 消息类型
Client �?Server:
  - CONFIG_UPDATE: 配置更新
  - TASK_CANCEL: 取消任务
  
Server �?Client:
  - STATE_CHANGE: 状态变�?  - TASK_UPDATE: 任务更新
  - LOG_STREAM: 日志�?```

### 5.3 JavaScript API规范

```javascript
// Nexus �?Skill (iframe通信)
parent.postMessage({
    type: 'THEME_CHANGE',
    data: {
        mode: 'dark',
        cssVariables: { ... }
    }
}, '*');

// Skill �?Nexus
window.parent.postMessage({
    type: 'RESIZE_REQUEST',
    data: { height: 500 }
}, '*');
```

---

## 六、质量要�?
### 6.1 代码质量

| 指标 | 要求 | 检查方�?|
|------|------|----------|
| 单元测试覆盖�?| �?0% | CI自动检�?|
| 代码复杂�?| 方法�?0�?| Code Review |
| 文档注释 | 公共API必须注释 | 静态检�?|
| API兼容�?| 向后兼容 | 版本控制 |

### 6.2 接口稳定�?
- **P0接口**: 发布后不允许Breaking Change
- **P1接口**: 允许扩展，不允许删除
- **实验性接�?*: 标记@Experimental，允许变�?
### 6.3 性能要求

| 指标 | 目标�?| 测试方法 |
|------|--------|----------|
| Skill启动时间 | �?�?| 集成测试 |
| API响应时间 | �?00ms | 压力测试 |
| 内存占用增长 | �?0%/�?| 监控 |

---

## 七、问题升级机�?
### 7.1 问题分级

| 级别 | 定义 | 响应时间 | 升级路径 |
|------|------|----------|----------|
| **P0** | 阻塞开�?| 2小时 | 立即上报项目负责�?|
| **P1** | 影响进度 | 24小时 | 上报团队负责�?|
| **P2** | 一般问�?| 48小时 | 团队内部解决 |

### 7.2 升级流程

```
问题发现
  └── 团队内部尝试解决 (2小时)
      └── 无法解决 �?团队间协�?          └── 24小时无法解决 �?上报架构�?              └── 48小时无法解决 �?项目决策会议
```

---

## 八、沟通机�?
### 8.1 日常沟�?
| 机制 | 频率 | 参与�?| 目的 |
|------|------|--------|------|
| 每日站会 | 每天17:00 | 三团队代�?| 同步进度 |
| 阻塞问题�?| 随时 | 相关人员 | 解决阻塞 |
| 周度回顾 | 周五16:00 | 全体 | 回顾与计�?|

### 8.2 文档协作

| 文档 | 位置 | 维护�?| 更新频率 |
|------|------|--------|----------|
| 集成状态报�?| INTEGRATION_STATUS_REPORT.md | Skills团队 | 每周�?|
| 协调状�?| A2UI_COORDINATION_STATUS.md | Skills团队 | 实时 |
| API文档 | Swagger/OpenAPI | SDK团队 | 随代码更�?|

### 8.3 联系方式

| 角色 | 团队 | 联系方式 |
|------|------|----------|
| 集成协调�?| Skills团队 | skills-arch@ooder.net |
| SDK负责�?| SDK团队 | sdk-backend@ooder.net |
| Nexus负责�?| Nexus团队 | nexus-fe@ooder.net |

---

## 九、风险与应对

### 9.1 当前风险

| 风险 | 概率 | 影响 | 应对措施 |
|------|------|------|----------|
| 规范延期 | �?| �?| Skills团队加班，每日跟�?|
| 接口理解偏差 | �?| �?| 增加评审会议，文档示�?|
| 联调时间不足 | �?| �?| 提前联调，预留缓�?|

### 9.2 应急预�?
1. **规范延期**: 优先完成核心章节，边缘章节延�?2. **人员变动**: 每个关键岗位设置Backup
3. **技术难�?*: 引入外部专家支持

---

## 十、附�?
### 10.1 参考文�?
- [OODER-A2A-SPECIFICATION-v1.0.md](OODER-A2A-SPECIFICATION-v1.0.md)
- [SDK_TEAM_IMPLEMENTATION_GUIDE.md](SDK_TEAM_IMPLEMENTATION_GUIDE.md)
- [NEXUS_TEAM_IMPLEMENTATION_GUIDE.md](NEXUS_TEAM_IMPLEMENTATION_GUIDE.md)
- [TEAM_TASKS_ALLOCATION.md](TEAM_TASKS_ALLOCATION.md)

### 10.2 术语�?
| 术语 | 说明 |
|------|------|
| A2A | Agent-to-Agent协议 |
| Skill | 可热插拔的功能模�?|
| Web Components | W3C标准组件化技�?|
| Hot Plug | 热插拔，不停机安�?卸载 |

### 10.3 变更记录

| 版本 | 日期 | 变更内容 | 作�?|
|------|------|----------|------|
| v1.0 | 2026-02-25 | 初始版本 | AI Assistant |

---

**文档结束**

如有疑问，请联系集成协调人或在此文档基础上提出Issue�?