# Ooder Skill 配置规范 v2.3

## 文档信息

| 属性 | 值 |
|------|-----|
| **文档版本** | 2.3 |
| **编写日期** | 2026-03-02 |
| **适用版本** | SDK v2.3 |
| **术语版本** | GLOSSARY_V2.md |

---

## 一、Skill 类型定义

### 1.1 类型分类

| 类型 | 标识 | 说明 | 依赖特性 |
|------|------|------|---------|
| **UI Skill** | `nexus-ui` | 前端界面组件 | 通常依赖后端Service |
| **Service Skill** | `system-service` | 后端服务 | 可独立运行 |
| **Tool Skill** | `tool-skill` | 工具类能力 | 轻量级，无状态 |
| **Driver Skill** | `driver-skill` | 驱动适配器 | 依赖具体平台 |
| **LLM Skill** | `llm-service` | LLM相关能力 | 可能依赖LLM Provider |
| **Scene Skill** | `scene-skill` | 场景能力包 | 包含场景能力定义 |

### 1.2 依赖层级

```
Level 0: 基础服务（无依赖）
  └─ skill-health, skill-monitor, skill-security, skill-vfs-local

Level 1: 核心服务（依赖Level 0）
  └─ skill-knowledge-base, skill-local-knowledge, skill-rag

Level 2: 业务服务（依赖Level 0-1）
  └─ skill-llm-context-builder, skill-llm-conversation

Level 3: UI组件（依赖Level 0-2）
  └─ skill-knowledge-ui, skill-llm-assistant-ui, skill-llm-management-ui

Level 4: 场景能力包（依赖Level 0-3）
  └─ skill-daily-report, skill-smart-home, skill-knowledge-qa
```

---

## 二、skill.yaml 完整规范

### 2.1 文件结构

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: <skill-id>
  name: <显示名称>
  version: <语义版本>
  description: <功能描述>
  author: <作者>
  type: <类型标识>
  license: <许可证>
  homepage: <主页URL>
  repository: <仓库URL>
  keywords:
    - <关键词1>
    - <关键词2>

spec:
  type: <类型>
  
  # 依赖声明
  dependencies:
    - id: <依赖skill-id>
      version: "<版本约束>"
      required: <true|false>
      description: "<依赖说明>"
      capabilities:
        - <所需能力1>
        - <所需能力2>
  
  # 接口声明
  providedInterfaces:
    - id: <接口标识>
      version: "<接口版本>"
      description: "<接口说明>"
  
  requiredInterfaces:
    - id: <接口标识>
      version: "<版本约束>"
      optional: <true|false>
      description: "<接口说明>"
  
  # 能力声明
  capabilities:
    - id: <能力标识>
      name: <能力名称>
      description: <能力描述>
      category: <能力分类>
      type: <能力类型>           # v2.3新增：ATOMIC/COMPOSITE/SCENE/DRIVER
  
  # 场景能力定义（v2.3新增）
  sceneCapabilities:
    - id: <场景能力标识>
      name: <场景能力名称>
      mainFirst: <true|false>
      mainFirstConfig:
        selfCheck: [...]
        selfStart: [...]
        selfDrive: [...]
        startCollaboration: [...]
      capabilities: [...]
      collaborativeCapabilities: [...]
  
  # API端点
  endpoints:
    - path: <API路径>
      method: <HTTP方法>
      description: <接口说明>
      capability: <关联能力>
  
  # UI配置（UI Skill专用）
  nexusUi:
    entry:
      page: <入口页面>
      title: <页面标题>
      icon: <Remix Icon标识>
    menu:
      position: <sidebar|header|none>
      category: <菜单分类>
      order: <排序权重>
    layout:
      type: <default|floating|fullscreen>
      sidebar: <true|false>
      header: <true|false>
    floating:
      enabled: <true|false>
      position: <bottom-right|bottom-left|top-right|top-left>
      trigger: <button|hover|auto>
      width: <宽度px>
      height: <高度px>
  
  # 能力绑定（v2.3：原sceneBindings）
  capabilityBindings:
    - sceneCapabilityType: <场景能力类型>
      autoBind: <true|false>
      capabilities:
        - <能力1>
        - <能力2>
  
  # 运行时配置
  runtime:
    language: <java|nodejs|python>
    javaVersion: "<Java版本>"
    framework: <spring-boot|none>
    mainClass: <主类>
  
  # 配置参数
  config:
    required:
      - name: <配置名>
        type: <string|integer|boolean>
        description: <说明>
    optional:
      - name: <配置名>
        type: <string|integer|boolean>
        default: <默认值>
        description: <说明>
  
  # 资源限制
  resources:
    cpu: "<CPU限制>"
    memory: "<内存限制>"
    storage: "<存储限制>"
  
  # 离线支持
  offline:
    enabled: <true|false>
    cacheStrategy: <local|redis>
    syncOnReconnect: <true|false>
```

### 2.2 版本约束语法

| 语法 | 说明 | 示例 |
|------|------|------|
| `1.0.0` | 精确版本 | `version: "1.0.0"` |
| `>=1.0.0` | 最低版本 | `version: ">=1.0.0"` |
| `<2.0.0` | 最高版本 | `version: "<2.0.0"` |
| `>=1.0.0 <2.0.0` | 版本范围 | `version: ">=1.0.0 <2.0.0"` |
| `~1.0.0` | 兼容版本（1.0.x） | `version: "~1.0.0"` |
| `^1.0.0` | 主版本兼容（1.x.x） | `version: "^1.0.0"` |

---

## 三、能力类型规范（v2.3新增）

### 3.1 能力类型枚举

| 类型 | 标识 | 说明 |
|------|------|------|
| **原子能力** | `ATOMIC` | 单一功能，不可分解 |
| **组合能力** | `COMPOSITE` | 组合多个原子能力 |
| **场景能力** | `SCENE` | 自驱型SuperAgent能力 |
| **驱动能力** | `DRIVER` | 意图/时间/事件驱动 |
| **协作能力** | `COLLABORATIVE` | 跨场景协作能力 |

### 3.2 能力声明示例

```yaml
capabilities:
  # 原子能力
  - id: email-send
    name: 邮件发送
    description: 发送邮件到指定收件人
    category: communication
    type: ATOMIC
    
  # 组合能力
  - id: notification-chain
    name: 通知链
    description: 邮件+短信组合通知
    category: communication
    type: COMPOSITE
    capabilities:
      - email-send
      - sms-send
      
  # 驱动能力
  - id: daily-scheduler
    name: 每日调度
    description: 每天17:00触发
    category: driver
    type: DRIVER
    driverType: scheduler
    config:
      cron: "0 17 * * 1-5"
```

### 3.3 驱动能力类型

| 驱动类型 | 标识 | 说明 |
|---------|------|------|
| 意图接收 | `intent-receiver` | 接收用户意图 |
| 时间驱动 | `scheduler` | 时间事件驱动 |
| 事件监听 | `event-listener` | 业务事件监听 |
| 能力调用 | `capability-invoker` | 能力调用链管理 |
| 协作协调 | `collaboration-coordinator` | 协作场景协调 |

---

## 四、场景能力配置规范（v2.3新增）

### 4.1 场景能力定义

```yaml
sceneCapabilities:
  - id: scene-daily-report
    name: 日志汇报场景能力
    type: SCENE
    mainFirst: true
    
    mainFirstConfig:
      # 自检配置
      selfCheck:
        - checkCapabilities: [report-remind, report-submit, report-aggregate]
        - checkDriverCapabilities: [scheduler, event-listener]
        - checkCollaborative: [scene-email-notification]
        
      # 自启配置
      selfStart:
        - initDriverCapabilities: [scheduler, event-listener, capability-invoker]
        - initCapabilities: [report-remind, report-submit, report-aggregate]
        - bindAddresses: auto
        
      # 协作启动配置
      startCollaboration:
        - startScene: scene-email-notification
          bindInterface: notification-service
          
      # 自驱配置
      selfDrive:
        scheduleRules:
          - trigger: "0 17 * * 1-5"
            action: remind-flow
            
        eventRules:
          - event: user-submitted
            condition: "all_submitted"
            action: aggregate-flow
            
        capabilityChains:
          remind-flow:
            - capability: report-remind
              input: { targetUsers: "${role.employee}" }
              
          aggregate-flow:
            - capability: report-aggregate
            - capability: report-analyze
            - capability: report-remind
              input: { targetUsers: "${role.manager}" }
    
    # 子能力
    capabilities:
      - report-remind
      - report-submit
      - report-aggregate
      - report-analyze
      
    # 协作能力
    collaborativeCapabilities:
      - capabilityId: scene-email-notification
        role: PROVIDER
        interface: notification-service
        autoStart: true
```

### 4.2 能力调用链配置

```yaml
capabilityChains:
  chain-name:
    - capability: capability-id
      input:
        key: value
      condition: "expression"      # 可选
      onError: continue | stop     # 可选
      
    - capability: another-capability
      input:
        data: "${previous.result}"  # 引用上一步结果
```

---

## 五、依赖声明规范

### 5.1 必选依赖

```yaml
dependencies:
  - id: skill-knowledge-base
    version: ">=1.0.0 <2.0.0"
    required: true
    description: "知识库核心服务"
    capabilities:
      - kb-management
      - document-management
      - search
```

### 5.2 可选依赖

```yaml
dependencies:
  - id: skill-rag
    version: ">=1.0.0"
    required: false
    description: "RAG检索增强（可选）"
```

### 5.3 场景能力依赖

```yaml
dependencies:
  - id: skill-daily-report
    version: ">=1.0.0"
    required: true
    description: "日志汇报场景能力"
    sceneCapabilities:
      - scene-daily-report
```

---

## 六、能力分类规范

### 6.1 能力分类

| 分类 | 标识 | 说明 |
|------|------|------|
| UI能力 | `ui` | 前端交互能力 |
| 服务能力 | `service` | 后端服务能力 |
| 通信能力 | `communication` | 消息、通知能力 |
| 存储能力 | `storage` | 数据存储能力 |
| AI能力 | `ai` | AI/LLM相关能力 |
| 监控能力 | `monitoring` | 监控、健康检查能力 |
| 安全能力 | `security` | 认证、授权能力 |
| 驱动能力 | `driver` | 驱动能力（v2.3新增） |

### 6.2 能力声明示例

```yaml
capabilities:
  - id: kb-management
    name: 知识库管理
    description: 创建、更新、删除知识库
    category: service
    type: ATOMIC
    
  - id: kb-view
    name: 查看知识库
    description: 查看知识库列表和详情
    category: ui
    type: ATOMIC
    
  - id: kb-search
    name: 知识检索
    description: BM25语义检索
    category: ai
    type: ATOMIC
```

---

## 七、UI Skill 配置规范

### 7.1 侧边栏菜单类型

```yaml
spec:
  type: nexus-ui
  
  nexusUi:
    entry:
      page: index.html
      title: 知识库
      icon: ri-book-3-line
      
    menu:
      position: sidebar
      category: knowledge
      order: 10
      
    layout:
      type: default
      sidebar: true
      header: true
```

### 7.2 悬浮组件类型

```yaml
spec:
  type: nexus-ui
  
  nexusUi:
    entry:
      page: index.html
      title: LLM助手
      icon: ri-robot-line
      
    menu:
      position: none
      
    layout:
      type: floating
      sidebar: false
      header: false
      
    floating:
      enabled: true
      position: bottom-right
      trigger: button
      width: 420
      height: 600
```

### 7.3 全屏页面类型

```yaml
spec:
  type: nexus-ui
  
  nexusUi:
    entry:
      page: index.html
      title: 系统仪表盘
      icon: ri-dashboard-line
      
    menu:
      position: header
      category: system
      order: 1
      
    layout:
      type: fullscreen
      sidebar: false
      header: true
```

---

## 八、能力绑定规范（v2.3更新）

### 8.1 自动绑定

```yaml
capabilityBindings:
  - sceneCapabilityType: knowledge
    autoBind: true
    capabilities:
      - kb-view
      - kb-manage
      - kb-search
```

### 8.2 多场景能力绑定

```yaml
capabilityBindings:
  - sceneCapabilityType: knowledge
    autoBind: true
    capabilities:
      - kb-view
      - kb-search
      
  - sceneCapabilityType: document
    autoBind: false
    capabilities:
      - kb-view
```

---

## 九、配置示例

### 9.1 独立服务 Skill

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-health
  name: 健康检查服务
  version: 1.0.0
  description: 系统健康检查、服务状态监控、报告生成
  author: ooder Team
  type: system-service

spec:
  type: system-service
  
  dependencies: []
  
  capabilities:
    - id: health-check
      name: 健康检查
      description: 执行系统健康检查
      category: monitoring
      type: ATOMIC
    - id: service-check
      name: 服务检查
      description: 检查各服务状态
      category: monitoring
      type: ATOMIC
    - id: report-generation
      name: 报告生成
      description: 生成健康报告
      category: monitoring
      type: ATOMIC
  
  endpoints:
    - path: /api/health/check
      method: POST
      description: 执行健康检查
      capability: health-check
    - path: /api/health/services
      method: GET
      description: 获取服务列表
      capability: service-check
    - path: /api/health/report
      method: POST
      description: 生成健康报告
      capability: report-generation
  
  runtime:
    language: java
    javaVersion: "8"
    framework: spring-boot
    mainClass: net.ooder.skill.health.HealthSkillApplication
  
  config:
    optional:
      - name: CHECK_INTERVAL
        type: integer
        default: 60000
        description: 检查间隔（毫秒）
      - name: TIMEOUT
        type: integer
        default: 5000
        description: 超时时间（毫秒）
  
  resources:
    cpu: "100m"
    memory: "128Mi"
    storage: "50Mi"
  
  offline:
    enabled: true
    cacheStrategy: local
    syncOnReconnect: true
```

### 9.2 场景能力 Skill（v2.3新增）

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-daily-report
  name: 日志汇报场景能力包
  version: 1.0.0
  description: 日志汇报场景能力，支持定时提醒、日志汇总、AI分析
  author: ooder Team
  type: scene-skill

spec:
  type: scene-skill
  
  dependencies:
    - id: skill-email-notification
      version: ">=1.0.0"
      required: true
      description: "邮件通知服务"
    - id: skill-llm-assistant
      version: ">=1.0.0"
      required: false
      description: "LLM分析助手（可选）"
  
  sceneCapabilities:
    - id: scene-daily-report
      name: 日志汇报场景能力
      type: SCENE
      mainFirst: true
      
      mainFirstConfig:
        selfCheck:
          - checkCapabilities: [report-remind, report-submit, report-aggregate]
          - checkDriverCapabilities: [scheduler, event-listener]
          
        selfStart:
          - initDriverCapabilities: [scheduler, event-listener]
          - initCapabilities: [report-remind, report-submit, report-aggregate]
          
        selfDrive:
          scheduleRules:
            - trigger: "0 17 * * 1-5"
              action: remind-flow
              
          capabilityChains:
            remind-flow:
              - capability: report-remind
                input: { targetUsers: "${role.employee}" }
                
      capabilities:
        - report-remind
        - report-submit
        - report-aggregate
        - report-analyze
        
      collaborativeCapabilities:
        - capabilityId: scene-email-notification
          role: PROVIDER
          interface: notification-service
          autoStart: true
  
  capabilities:
    - id: report-remind
      name: 日志提醒
      description: 提醒员工提交日志
      category: communication
      type: ATOMIC
      
    - id: report-submit
      name: 日志提交
      description: 员工提交日志
      category: service
      type: ATOMIC
      
    - id: report-aggregate
      name: 日志汇总
      description: 汇总员工日志
      category: service
      type: COMPOSITE
      
    - id: report-analyze
      name: 日志分析
      description: AI分析日志内容
      category: ai
      type: ATOMIC
```

---

## 十、术语变更对照表（v2.3）

| 旧术语 | 新术语 | 说明 |
|--------|--------|------|
| sceneType | sceneCapabilityType | 场景类型 → 场景能力类型 |
| sceneBindings | capabilityBindings | 场景绑定 → 能力绑定 |
| - | mainFirst | 新增：自驱入口 |
| - | mainFirstConfig | 新增：自驱配置 |
| - | capabilityChains | 新增：能力调用链 |
| - | sceneCapabilities | 新增：场景能力定义 |
| - | collaborativeCapabilities | 新增：协作能力 |

---

## 十一、验证清单

### 11.1 必填字段检查

- [ ] `metadata.id` - 唯一标识
- [ ] `metadata.name` - 显示名称
- [ ] `metadata.version` - 语义版本
- [ ] `metadata.type` - 类型标识
- [ ] `spec.type` - 与metadata.type一致

### 11.2 依赖声明检查

- [ ] 所有依赖都有 `id` 字段
- [ ] 所有依赖都有 `version` 版本约束
- [ ] 所有依赖都有 `required` 标记
- [ ] 必选依赖的 `required` 为 `true`

### 11.3 能力声明检查

- [ ] 每个能力有唯一 `id`
- [ ] 每个能力有 `name` 和 `description`
- [ ] 能力 `category` 符合预定义分类
- [ ] 能力 `type` 符合v2.3能力类型（ATOMIC/COMPOSITE/SCENE/DRIVER）

### 11.4 场景能力检查（v2.3新增）

- [ ] 场景能力有 `mainFirst` 标识
- [ ] 场景能力有 `mainFirstConfig` 配置
- [ ] 自驱配置包含 `selfDrive` 能力调用链

### 11.5 UI Skill 额外检查

- [ ] `nexusUi.entry.page` 存在
- [ ] `nexusUi.entry.icon` 使用 Remix Icon
- [ ] `nexusUi.menu.position` 有效值

---

## 十二、版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 2.0 | 2026-03-01 | 初始版本 |
| 2.3 | 2026-03-02 | **重大升级**：新增能力类型体系（ATOMIC/COMPOSITE/SCENE/DRIVER），新增mainFirst自驱配置，新增场景能力定义，术语统一 |

---

**文档维护者**: ooder开发团队  
**最后更新**: 2026-03-02
