# 协作场景技能需求文档

> **文档版本**: 1.0  
> **创建日期**: 2026-03-15  
> **适用范围**: 协作场景技能开发

---

## 一、概述

### 1.1 文档目的

本文档定义 skills-scene 核心功能之外，由独立协作场景技能提供的功能需求。这些技能通过场景机制与核心系统协作，提供增值功能。

### 1.2 职责划分原则

| 原则 | 说明 |
|------|------|
| **核心优先** | 核心数据管理和流程控制由 skills-scene 负责 |
| **技能解耦** | 可独立部署、可替换的功能封装为技能 |
| **场景协作** | 技能通过场景机制与核心系统交互 |
| **按需加载** | 用户可按需安装/卸载协作技能 |

---

## 二、协作技能清单

### 2.1 技能列表总览

| 技能ID | 技能名称 | 类型 | 优先级 | 说明 |
|--------|----------|------|--------|------|
| skill-llm-chat | LLM对话技能 | 协作技能 | P0 | 提供LLM对话能力 |
| skill-audit-log | 审计日志技能 | 协作技能 | P1 | 提供审计日志查询和导出 |
| skill-notification | 消息通知技能 | 协作技能 | P1 | 提供消息推送能力 |
| skill-storage-ui | 存储管理技能 | 协作技能 | P2 | 提供存储管理界面 |
| skill-monitor-dashboard | 监控面板技能 | 协作技能 | P2 | 提供监控可视化 |
| skill-report | 报告生成技能 | 协作技能 | P2 | 提供报告生成能力 |

---

## 三、skill-llm-chat（LLM对话技能）

### 3.1 功能概述

提供LLM对话能力，支持多Provider、流式输出、Function Calling。

### 3.2 能力列表

```yaml
capabilities:
  - id: llm-chat
    name: LLM对话
    description: 与LLM进行对话交互
    category: intelligence
    parameters:
      - name: prompt
        type: string
        required: true
        description: 用户输入
      - name: systemPrompt
        type: string
        required: false
        description: 系统提示词
      - name: model
        type: string
        required: false
        description: 指定模型
      - name: stream
        type: boolean
        required: false
        defaultValue: true
        description: 是否流式输出
    returns:
      type: ChatResponse
      properties:
        - response: string
        - tokenUsage: object
        - model: string

  - id: llm-chat-stream
    name: LLM流式对话
    description: SSE流式对话
    category: intelligence
    connectorType: WEBSOCKET
    parameters:
      - name: prompt
        type: string
        required: true
      - name: systemPrompt
        type: string
        required: false
    returns:
      type: SSEStream

  - id: llm-translate
    name: 文本翻译
    description: 翻译文本
    category: intelligence
    parameters:
      - name: text
        type: string
        required: true
      - name: targetLang
        type: string
        required: true
    returns:
      type: string

  - id: llm-summarize
    name: 文本摘要
    description: 生成文本摘要
    category: intelligence
    parameters:
      - name: text
        type: string
        required: true
      - name: maxLength
        type: integer
        required: false
        defaultValue: 200
    returns:
      type: string

  - id: llm-models
    name: 模型列表
    description: 获取可用模型列表
    category: management
    parameters: []
    returns:
      type: array
      items: string

  - id: llm-set-model
    name: 切换模型
    description: 切换当前使用的模型
    category: management
    parameters:
      - name: model
        type: string
        required: true
    returns:
      type: boolean
```

### 3.3 依赖 skills-scene 能力

```yaml
dependencies:
  - capability: scene-context
    usage: 获取场景上下文信息
    required: true
  
  - capability: audit-log
    usage: 记录对话日志
    required: false
```

### 3.4 UI页面参考

**参考文件**: `temp/ooder-Nexus/src/main/resources/static/console/pages/llm/llm-chat.html`

**页面结构**:
```html
- 对话头部
  - 模型选择器
  - Token统计
  - 设置按钮
- 消息列表
  - 欢迎消息
  - 快捷操作按钮
  - 历史消息
- 输入区域
  - 文本输入框
  - 发送按钮
  - 状态提示
- 设置模态框
  - 系统提示词
  - 温度参数
  - 最大Token
  - 流式开关
```

### 3.5 API端点

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | /api/llm/chat | 同步对话 |
| POST | /api/llm/chat/stream | 流式对话(SSE) |
| POST | /api/llm/translate | 翻译 |
| POST | /api/llm/summarize | 摘要 |
| POST | /api/llm/models | 获取模型列表 |
| PUT | /api/llm/model | 切换模型 |
| GET | /api/llm/providers | 获取Provider列表 |

---

## 四、skill-audit-log（审计日志技能）

### 4.1 功能概述

提供审计日志的查询、过滤、导出功能。

### 4.2 能力列表

```yaml
capabilities:
  - id: audit-log-query
    name: 日志查询
    description: 查询审计日志
    category: monitoring
    parameters:
      - name: startTime
        type: long
        required: false
      - name: endTime
        type: long
        required: false
      - name: operation
        type: string
        required: false
      - name: operator
        type: string
        required: false
      - name: sceneId
        type: string
        required: false
      - name: page
        type: integer
        required: false
        defaultValue: 1
      - name: pageSize
        type: integer
        required: false
        defaultValue: 20
    returns:
      type: PageResult
      items: AuditLogEntry

  - id: audit-log-export
    name: 日志导出
    description: 导出审计日志
    category: monitoring
    parameters:
      - name: startTime
        type: long
        required: true
      - name: endTime
        type: long
        required: true
      - name: format
        type: string
        required: false
        defaultValue: csv
        enum: [csv, json, excel]
    returns:
      type: File

  - id: audit-log-stats
    name: 日志统计
    description: 审计日志统计
    category: monitoring
    parameters:
      - name: period
        type: string
        required: false
        defaultValue: day
        enum: [hour, day, week, month]
    returns:
      type: AuditStats
```

### 4.3 依赖 skills-scene 能力

```yaml
dependencies:
  - capability: scene-list
    usage: 获取场景列表用于过滤
    required: false
  
  - capability: participant-list
    usage: 获取参与者列表用于过滤
    required: false
```

### 4.4 UI页面参考

**参考文件**: `temp/ooder-Nexus/src/main/resources/static/console/pages/audit/audit-logs.html`

**页面结构**:
```html
- 过滤区域
  - 时间范围选择
  - 操作类型选择
  - 操作者选择
  - 场景选择
  - 搜索按钮
- 统计卡片
  - 总操作数
  - 今日操作
  - 异常操作
  - 活跃用户
- 日志表格
  - 时间
  - 操作类型
  - 操作者
  - 场景
  - 详情
  - IP地址
- 分页控制
- 导出按钮
```

### 4.5 API端点

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | /api/audit/logs | 查询日志 |
| POST | /api/audit/export | 导出日志 |
| GET | /api/audit/stats | 日志统计 |

---

## 五、skill-notification（消息通知技能）

### 5.1 功能概述

提供消息推送、订阅管理、通知模板等功能。

### 5.2 能力列表

```yaml
capabilities:
  - id: notification-send
    name: 发送通知
    description: 发送消息通知
    category: notification
    parameters:
      - name: title
        type: string
        required: true
      - name: content
        type: string
        required: true
      - name: type
        type: string
        required: false
        enum: [info, warning, error, success]
      - name: targets
        type: array
        required: true
        items: string
      - name: channels
        type: array
        required: false
        items: string
        defaultValue: [in-app]
    returns:
      type: NotificationResult

  - id: notification-subscribe
    name: 订阅管理
    description: 管理消息订阅
    category: notification
    parameters:
      - name: eventType
        type: string
        required: true
      - name: channels
        type: array
        required: true
        items: string
      - name: enabled
        type: boolean
        required: true
    returns:
      type: boolean

  - id: notification-email
    name: 邮件发送
    description: 发送邮件通知
    category: notification
    parameters:
      - name: to
        type: array
        required: true
        items: string
      - name: subject
        type: string
        required: true
      - name: body
        type: string
        required: true
      - name: isHtml
        type: boolean
        required: false
        defaultValue: false
      - name: attachments
        type: array
        required: false
    returns:
      type: EmailResult

  - id: notification-template
    name: 模板管理
    description: 管理通知模板
    category: management
    parameters:
      - name: templateId
        type: string
        required: true
      - name: variables
        type: object
        required: false
    returns:
      type: NotificationTemplate
```

### 5.3 依赖 skills-scene 能力

```yaml
dependencies:
  - capability: participant-list
    usage: 获取场景参与者列表作为通知目标
    required: true
  
  - capability: scene-event
    usage: 订阅场景事件触发通知
    required: true
```

### 5.4 UI页面参考

**参考文件**: `temp/ooder-Nexus/src/main/resources/static/console/pages/group/group-message.html`

**页面结构**:
```html
- 消息列表
  - 未读消息
  - 已读消息
  - 消息详情
- 发送消息
  - 收件人选择
  - 消息类型
  - 消息内容
  - 发送渠道
- 订阅管理
  - 事件类型列表
  - 渠道选择
  - 开关控制
```

### 5.5 API端点

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | /api/notification/send | 发送通知 |
| GET | /api/notification/list | 消息列表 |
| PUT | /api/notification/read | 标记已读 |
| POST | /api/notification/subscribe | 订阅管理 |
| POST | /api/notification/email | 发送邮件 |
| GET | /api/notification/templates | 模板列表 |

---

## 六、skill-storage-ui（存储管理技能）

### 6.1 功能概述

提供存储空间管理、文件管理、存储统计等功能。

### 6.2 能力列表

```yaml
capabilities:
  - id: storage-list
    name: 存储列表
    description: 获取存储空间列表
    category: storage
    parameters: []
    returns:
      type: array
      items: StorageInfo

  - id: storage-stats
    name: 存储统计
    description: 存储使用统计
    category: monitoring
    parameters:
      - name: storageId
        type: string
        required: false
    returns:
      type: StorageStats

  - id: file-list
    name: 文件列表
    description: 获取文件列表
    category: storage
    parameters:
      - name: path
        type: string
        required: false
      - name: type
        type: string
        required: false
    returns:
      type: array
      items: FileInfo

  - id: file-upload
    name: 文件上传
    description: 上传文件
    category: storage
    parameters:
      - name: file
        type: file
        required: true
      - name: path
        type: string
        required: false
    returns:
      type: FileInfo

  - id: file-download
    name: 文件下载
    description: 下载文件
    category: storage
    parameters:
      - name: fileId
        type: string
        required: true
    returns:
      type: File

  - id: file-share
    name: 文件分享
    description: 分享文件
    category: collaboration
    parameters:
      - name: fileId
        type: string
        required: true
      - name: expireTime
        type: long
        required: false
      - name: password
        type: string
        required: false
    returns:
      type: ShareLink
```

### 6.3 依赖 skills-scene 能力

```yaml
dependencies:
  - capability: scene-context
    usage: 获取场景上下文
    required: false
```

### 6.4 UI页面参考

**参考文件**: `temp/ooder-Nexus/src/main/resources/static/console/pages/storage/storage-management.html`

**页面结构**:
```html
- 存储概览
  - 总容量
  - 已使用
  - 文件数
  - 存储列表
- 文件管理
  - 路径导航
  - 文件列表
  - 上传按钮
  - 操作菜单
- 分享管理
  - 分享列表
  - 分享链接
  - 过期时间
```

### 6.5 API端点

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | /api/storage/list | 存储列表 |
| GET | /api/storage/stats | 存储统计 |
| GET | /api/files | 文件列表 |
| POST | /api/files/upload | 文件上传 |
| GET | /api/files/{id}/download | 文件下载 |
| POST | /api/files/{id}/share | 文件分享 |
| GET | /api/shares | 分享列表 |

---

## 七、skill-monitor-dashboard（监控面板技能）

### 7.1 功能概述

提供系统监控、性能指标、告警管理等功能。

### 7.2 能力列表

```yaml
capabilities:
  - id: monitor-metrics
    name: 性能指标
    description: 获取性能指标
    category: monitoring
    parameters:
      - name: metricType
        type: string
        required: false
        enum: [cpu, memory, disk, network]
      - name: period
        type: string
        required: false
        defaultValue: 1h
    returns:
      type: array
      items: MetricPoint

  - id: monitor-health
    name: 健康检查
    description: 系统健康检查
    category: monitoring
    parameters: []
    returns:
      type: HealthStatus

  - id: monitor-alerts
    name: 告警列表
    description: 获取告警列表
    category: monitoring
    parameters:
      - name: status
        type: string
        required: false
        enum: [active, resolved, all]
      - name: severity
        type: string
        required: false
        enum: [info, warning, critical]
    returns:
      type: array
      items: Alert

  - id: monitor-alert-config
    name: 告警配置
    description: 配置告警规则
    category: management
    parameters:
      - name: alertRule
        type: object
        required: true
    returns:
      type: AlertRule

  - id: monitor-dashboard
    name: 监控面板
    description: 获取监控面板数据
    category: monitoring
    parameters:
      - name: dashboardId
        type: string
        required: false
    returns:
      type: DashboardData
```

### 7.3 依赖 skills-scene 能力

```yaml
dependencies:
  - capability: capability-stats
    usage: 获取能力调用统计
    required: true
  
  - capability: scene-status
    usage: 获取场景状态
    required: true
  
  - capability: participant-status
    usage: 获取参与者状态
    required: true
```

### 7.4 UI页面参考

**参考文件**: `temp/ooder-Nexus/src/main/resources/static/console/pages/nexus/system-status.html`

**页面结构**:
```html
- 概览卡片
  - CPU使用率
  - 内存使用率
  - 磁盘使用率
  - 网络流量
- 场景状态
  - 活跃场景数
  - 场景列表
  - 状态分布
- 能力统计
  - 调用次数
  - 成功率
  - 平均延迟
- 告警面板
  - 活跃告警
  - 告警历史
  - 告警配置
```

### 7.5 API端点

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | /api/monitor/metrics | 性能指标 |
| GET | /api/monitor/health | 健康检查 |
| GET | /api/monitor/alerts | 告警列表 |
| POST | /api/monitor/alerts/config | 告警配置 |
| GET | /api/monitor/dashboard | 监控面板 |

---

## 八、skill-report（报告生成技能）

### 8.1 功能概述

提供报告模板、报告生成、报告导出等功能。

### 8.2 能力列表

```yaml
capabilities:
  - id: report-template-list
    name: 模板列表
    description: 获取报告模板列表
    category: management
    parameters: []
    returns:
      type: array
      items: ReportTemplate

  - id: report-generate
    name: 生成报告
    description: 生成报告
    category: creation
    parameters:
      - name: templateId
        type: string
        required: true
      - name: data
        type: object
        required: true
      - name: format
        type: string
        required: false
        enum: [pdf, html, markdown, word]
    returns:
      type: Report

  - id: report-schedule
    name: 定时报告
    description: 配置定时报告
    category: management
    parameters:
      - name: templateId
        type: string
        required: true
      - name: cron
        type: string
        required: true
      - name: recipients
        type: array
        required: true
        items: string
    returns:
      type: ScheduleTask

  - id: report-history
    name: 报告历史
    description: 获取报告历史
    category: management
    parameters:
      - name: templateId
        type: string
        required: false
      - name: page
        type: integer
        required: false
    returns:
      type: PageResult
      items: Report
```

### 8.3 依赖 skills-scene 能力

```yaml
dependencies:
  - capability: scene-stats
    usage: 获取场景统计数据
    required: true
  
  - capability: capability-stats
    usage: 获取能力调用统计
    required: true
  
  - capability: notification-email
    usage: 发送报告邮件
    required: false
```

### 8.4 UI页面参考

**参考文件**: `temp/ooder-Nexus/src/main/resources/static/console/pages/task/data-extract-tasks.html`

**页面结构**:
```html
- 模板管理
  - 模板列表
  - 模板编辑
  - 模板预览
- 报告生成
  - 选择模板
  - 配置参数
  - 生成报告
  - 预览下载
- 定时任务
  - 任务列表
  - 任务配置
  - 执行历史
- 报告历史
  - 历史列表
  - 报告下载
  - 报告分享
```

### 8.5 API端点

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | /api/report/templates | 模板列表 |
| POST | /api/report/generate | 生成报告 |
| POST | /api/report/schedule | 定时报告 |
| GET | /api/report/history | 报告历史 |
| GET | /api/report/{id}/download | 下载报告 |

---

## 九、技能开发规范

### 9.1 目录结构

```
skill-{name}/
├── skill.yaml              # 技能定义
├── src/
│   └── main/
│       ├── java/
│       │   └── net/ooder/skill/{name}/
│       │       ├── controller/    # API控制器
│       │       ├── service/       # 业务服务
│       │       ├── model/         # 数据模型
│       │       └── config/        # 配置类
│       └── resources/
│           ├── static/            # 静态资源
│           │   └── console/
│           │       ├── pages/     # 页面
│           │       ├── js/        # JavaScript
│           │       └── css/       # 样式
│           └── application.yml    # 配置文件
└── pom.xml
```

### 9.2 skill.yaml 规范

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-{name}
  name: {技能名称}
  version: 1.0.0
  description: {技能描述}
  author: ooder
  category: {分类}

spec:
  form: PROVIDER          # 技能形态
  visibility: PUBLIC      # 可见性
  
  capabilities:
    - id: {能力ID}
      name: {能力名称}
      description: {能力描述}
      category: {能力分类}
      parameters:
        - name: {参数名}
          type: {参数类型}
          required: {是否必需}
          defaultValue: {默认值}
      returns:
        type: {返回类型}
  
  dependencies:
    - skillId: {依赖技能ID}
      versionRange: ">=1.0.0"
      required: {是否必需}
  
  ui:
    pages:
      - path: /{name}/main.html
        title: {页面标题}
        icon: {图标}
    menu:
      - title: {菜单标题}
        icon: {图标}
        path: /{name}/main.html
        order: {排序}
```

### 9.3 与 skills-scene 集成

```java
@RestController
@RequestMapping("/api/{skill-name}")
public class SkillController {
    
    @Autowired
    private SceneContext sceneContext;
    
    @PostMapping("/capability/{capId}")
    public Object invokeCapability(
        @PathVariable String capId,
        @RequestBody Map<String, Object> params
    ) {
        return sceneContext.invokeCapability(capId, params);
    }
}
```

---

## 十、开发优先级

### 10.1 第一优先级（P0）

| 技能 | 原因 |
|------|------|
| skill-llm-chat | 核心AI能力，场景必需 |

### 10.2 第二优先级（P1）

| 技能 | 原因 |
|------|------|
| skill-audit-log | 安全审计必需 |
| skill-notification | 场景协作必需 |

### 10.3 第三优先级（P2）

| 技能 | 原因 |
|------|------|
| skill-storage-ui | 增强功能 |
| skill-monitor-dashboard | 运维增强 |
| skill-report | 业务增强 |

---

*文档生成时间: 2026-03-15*
