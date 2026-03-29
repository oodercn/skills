# 场景技能初始化规范

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 规范定义 |

---

## 一、场景技能组成结构

### 1.1 场景技能的组成

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        场景技能组成结构                                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                      场景技能 (Scene Skill)                               │    │
│  ├─────────────────────────────────────────────────────────────────────────┤    │
│  │                                                                         │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │    │
│  │  │  UI Skills  │  │ Data Skills │  │  AI Skills  │  │  Msg Skills │     │    │
│  │  │  (界面技能)  │  │ (数据技能)   │  │ (智能技能)   │  │ (消息技能)   │     │    │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘     │    │
│  │         │                │                │                │            │    │
│  │         ▼                ▼                ▼                ▼            │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │                      依赖技能 (Dependencies)                      │   │    │
│  │  │  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐      │   │    │
│  │  │  │ MQTT推送  │  │ 邮件服务  │  │ 数据库    │  │ LLM服务   │      │   │    │
│  │  │  └───────────┘  └───────────┘  └───────────┘  └───────────┘      │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  │                                                                         │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 技能类型分类

| 类型 | 说明 | 是否有UI | 是否有菜单 | 示例 |
|------|------|:--------:|:----------:|------|
| **UI Skill** | 用户界面技能 | ✅ | ✅ | 日志填写表单、历史查询页面 |
| **Data Skill** | 数据处理技能 | ❌ | ❌ | 日志存储、数据汇总 |
| **AI Skill** | 智能分析技能 | ❌ | ❌ | AI生成、智能分析 |
| **Msg Skill** | 消息通知技能 | ❌ | ❌ | MQTT推送、邮件通知 |
| **Integration Skill** | 集成技能 | ❌ | ❌ | Git集成、邮件集成 |

---

## 二、配置项清单

### 2.1 场景技能安装时需要的配置

```yaml
SceneSkillConfig:
  # 基本信息
  id: "daily-report-scene"
  name: "日志汇报场景"
  version: "1.0.0"
  type: "SCENE"  # SCENE | SKILL
  
  # 依赖技能
  dependencies:
    required:
      - skillId: "mqtt-push"
        version: ">=1.0.0"
        autoInstall: true
      - skillId: "email-service"
        version: ">=1.0.0"
        autoInstall: false
    optional:
      - skillId: "git-integration"
        version: ">=1.0.0"
        autoInstall: false
      - skillId: "calendar-integration"
        version: ">=1.0.0"
        autoInstall: false
  
  # UI技能配置
  uiSkills:
    - id: "report-form"
      name: "日志填写表单"
      entryUrl: "/console/pages/daily-report-form.html"
      icon: "ri-edit-line"
      roles: ["MANAGER", "EMPLOYEE"]
      order: 1
    - id: "history-query"
      name: "历史查询"
      entryUrl: "/console/pages/daily-report-history.html"
      icon: "ri-history-line"
      roles: ["MANAGER", "HR"]
      order: 2
    - id: "project-tracking"
      name: "项目跟踪"
      entryUrl: "/console/pages/daily-report-tracking.html"
      icon: "ri-line-chart-line"
      roles: ["MANAGER"]
      order: 3
  
  # 菜单配置
  menus:
    MANAGER:
      - { id: "write-log", name: "填写日志", icon: "ri-edit-line", url: "/console/pages/daily-report-form.html", order: 1 }
      - { id: "history-query", name: "历史查询", icon: "ri-history-line", url: "/console/pages/daily-report-history.html", order: 2 }
      - { id: "project-tracking", name: "项目跟踪", icon: "ri-line-chart-line", url: "/console/pages/daily-report-tracking.html", order: 3 }
      - { id: "team-logs", name: "团队日志", icon: "ri-team-line", url: "/console/pages/daily-report-team.html", order: 4 }
    EMPLOYEE:
      - { id: "log-reminder", name: "日志提醒", icon: "ri-notification-3-line", url: "/console/pages/daily-report-reminder.html", order: 1 }
      - { id: "write-log", name: "填写日志", icon: "ri-edit-line", url: "/console/pages/daily-report-form.html", order: 2 }
      - { id: "history-report", name: "历史汇报", icon: "ri-history-line", url: "/console/pages/daily-report-history.html", order: 3 }
  
  # 能力配置
  capabilities:
    - id: "report-remind"
      name: "日志提醒"
      type: "notification"
      autoBind: true
      dependencies: ["mqtt-push"]
    - id: "report-submit"
      name: "日志提交"
      type: "data-input"
      autoBind: true
    - id: "report-aggregate"
      name: "日志汇总"
      type: "data-processing"
      autoBind: true
    - id: "report-analyze"
      name: "日志分析"
      type: "intelligence"
      autoBind: true
      dependencies: ["llm-service"]
  
  # 激活流程配置
  activationSteps:
    MANAGER:
      - { stepId: "confirm-participants", name: "确认参与者", required: true }
      - { stepId: "select-push-targets", name: "选择推送目标", required: true }
      - { stepId: "config-conditions", name: "配置驱动条件", required: true }
      - { stepId: "get-key", name: "获取KEY", required: false }
      - { stepId: "confirm-activation", name: "确认激活", required: true }
      - { stepId: "network-actions", name: "入网动作", required: true }
    EMPLOYEE:
      - { stepId: "confirm-join", name: "确认加入场景", required: true }
      - { stepId: "config-private-capabilities", name: "配置私有能力", required: false }
      - { stepId: "confirm-activation", name: "确认激活", required: true }
```

### 2.2 配置项统计

| 配置类型 | 数量 | 初始化时机 | 存储位置 |
|----------|------|------------|----------|
| 基本信息 | 4 | 安装时 | skill-registry |
| 依赖技能 | 2-10 | 安装前检查 | skill-dependencies |
| UI技能 | 1-20 | 激活时 | menu-config |
| 菜单配置 | 5-50 | 激活时 | menu-config |
| 能力配置 | 1-20 | 激活时 | capability-bindings |
| 激活流程 | 3-10 | 激活时 | activation-process |

---

## 三、初始化时机和流程

### 3.1 完整初始化流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        场景技能初始化流程                                         │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  阶段1: 安装前检查                                                               │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  1. 检查依赖技能是否存在                                                   │    │
│  │     ├── 必需依赖: mqtt-push, email-service                               │    │
│  │     ├── 可选依赖: git-integration, calendar-integration                  │    │
│  │     └── 自动安装标记: autoInstall=true 的依赖自动安装                      │    │
│  │                                                                         │    │
│  │  2. 检查资源是否满足                                                       │    │
│  │     ├── 数据库连接                                                       │    │
│  │     ├── LLM服务                                                         │    │
│  │     └── MQTT服务                                                        │    │
│  │                                                                         │    │
│  │  3. 检查权限是否满足                                                       │    │
│  │     ├── 用户是否有安装权限                                                 │    │
│  │     └── 是否有配置权限                                                    │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  阶段2: 安装阶段                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  1. 安装依赖技能（如果需要）                                                │    │
│  │     ├── 自动安装 autoInstall=true 的依赖                                  │    │
│  │     └── 提示用户手动安装 autoInstall=false 的依赖                          │    │
│  │                                                                         │    │
│  │  2. 注册场景技能到技能注册表                                                │    │
│  │     ├── 保存基本信息                                                     │    │
│  │     ├── 保存依赖关系                                                     │    │
│  │     └── 保存能力配置                                                     │    │
│  │                                                                         │    │
│  │  3. 创建场景组（如果需要）                                                  │    │
│  │     ├── 创建场景组实例                                                   │    │
│  │     └── 绑定默认能力                                                     │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  阶段3: 激活阶段                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  1. 执行激活流程                                                          │    │
│  │     ├── 领导激活流程（6步）                                                │    │
│  │     └── 员工激活流程（3步）                                                │    │
│  │                                                                         │    │
│  │  2. 注册菜单                                                              │    │
│  │     ├── 根据角色生成菜单                                                  │    │
│  │     ├── 注册到公共菜单服务                                                │    │
│  │     └── 用户可在导航栏看到菜单                                             │    │
│  │                                                                         │    │
│  │  3. 绑定能力                                                              │    │
│  │     ├── 绑定场景能力                                                     │    │
│  │     ├── 绑定私有能力（员工）                                               │    │
│  │     └── 配置能力参数                                                     │    │
│  │                                                                         │    │
│  │  4. 初始化通知                                                            │    │
│  │     ├── 配置MQTT推送                                                     │    │
│  │     ├── 配置邮件通知                                                     │    │
│  │     └── 配置定时任务                                                     │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│                                    ▼                                            │
│  阶段4: 运行阶段                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │  1. 用户使用                                                              │    │
│  │     ├── 通过菜单访问UI技能                                                │    │
│  │     ├── 调用能力API                                                      │    │
│  │     └── 接收通知提醒                                                     │    │
│  │                                                                         │    │
│  │  2. 数据处理                                                              │    │
│  │     ├── 日志存储                                                        │    │
│  │     ├── 日志汇总                                                        │    │
│  │     └── 日志分析                                                        │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 依赖技能处理流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        依赖技能处理流程                                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  检查依赖技能                                                                    │
│  │                                                                              │
│  ├── 依赖存在?                                                                  │
│  │   ├── YES → 检查版本兼容性                                                   │
│  │   │         ├── 兼容 → 继续                                                  │
│  │   │         └── 不兼容 → 提示升级或降级                                       │
│  │   │                                                                         │
│  │   └── NO → 检查 autoInstall                                                 │
│  │             ├── true → 自动安装依赖技能                                      │
│  │             │         ├── 安装成功 → 继续                                    │
│  │             │         └── 安装失败 → 提示用户手动安装                         │
│  │             │                                                               │
│  │             └── false → 提示用户手动安装                                     │
│  │                         ├── 用户安装 → 重新检查                              │
│  │                         └── 用户跳过 → 标记为可选依赖缺失                     │
│  │                                                                             │
│  └── 所有必需依赖满足?                                                          │
│      ├── YES → 继续安装场景技能                                                 │
│      └── NO → 阻止安装，提示缺失依赖                                            │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 四、配置项详细说明

### 4.1 入口菜单配置

**配置时机**: 激活阶段

**配置数量**: 
- 每个角色: 3-10个菜单项
- 总计: 5-50个菜单项（多角色）

**存储形式**:
```json
{
  "users": {
    "user-001": {
      "sceneMenus": {
        "sg-xxx": {
          "sceneGroupId": "sg-xxx",
          "sceneName": "日志汇报",
          "role": "MANAGER",
          "items": [
            { "id": "write-log", "name": "填写日志", "icon": "ri-edit-line", "url": "/console/pages/daily-report-form.html?sceneGroupId=sg-xxx", "order": 1 }
          ]
        }
      }
    }
  }
}
```

### 4.2 依赖技能配置

**配置时机**: 安装前检查

**配置数量**: 2-10个依赖

**依赖类型**:
| 类型 | 说明 | autoInstall | 示例 |
|------|------|-------------|------|
| **必需依赖** | 场景运行必须的技能 | true | mqtt-push, email-service |
| **可选依赖** | 增强功能的技能 | false | git-integration, calendar-integration |
| **私有能力** | 用户个人配置的技能 | false | private-email, private-git |

### 4.3 能力绑定配置

**配置时机**: 激活阶段

**配置数量**: 1-20个能力

**绑定类型**:
| 类型 | 说明 | 绑定时机 | 示例 |
|------|------|----------|------|
| **自动绑定** | 场景必需的能力 | 激活时自动绑定 | report-remind, report-submit |
| **手动绑定** | 用户选择的能力 | 用户手动绑定 | git-integration, email-integration |
| **私有能力** | 用户个人的能力 | 员工激活时绑定 | private-email, private-git |

---

## 五、日志汇报场景完整配置示例

### 5.1 依赖技能

```yaml
dependencies:
  required:
    - skillId: "mqtt-push"
      name: "MQTT消息推送"
      version: ">=1.0.0"
      autoInstall: true
      description: "用于发送日志提醒通知"
    
    - skillId: "email-service"
      name: "邮件服务"
      version: ">=1.0.0"
      autoInstall: true
      description: "用于发送邮件通知"
    
    - skillId: "llm-service"
      name: "LLM服务"
      version: ">=1.0.0"
      autoInstall: true
      description: "用于AI智能生成日志内容"
  
  optional:
    - skillId: "git-integration"
      name: "Git集成"
      version: ">=1.0.0"
      autoInstall: false
      description: "用于自动获取Git提交记录"
    
    - skillId: "calendar-integration"
      name: "日历集成"
      version: ">=1.0.0"
      autoInstall: false
      description: "用于自动获取日程安排"
```

### 5.2 UI技能和菜单

```yaml
uiSkills:
  - id: "report-form"
    name: "日志填写表单"
    entryUrl: "/console/pages/daily-report-form.html"
    icon: "ri-edit-line"
    roles: ["MANAGER", "EMPLOYEE"]
    order: 1
  
  - id: "history-query"
    name: "历史查询"
    entryUrl: "/console/pages/daily-report-history.html"
    icon: "ri-history-line"
    roles: ["MANAGER", "HR"]
    order: 2
  
  - id: "project-tracking"
    name: "项目跟踪"
    entryUrl: "/console/pages/daily-report-tracking.html"
    icon: "ri-line-chart-line"
    roles: ["MANAGER"]
    order: 3
  
  - id: "team-logs"
    name: "团队日志"
    entryUrl: "/console/pages/daily-report-team.html"
    icon: "ri-team-line"
    roles: ["MANAGER", "HR"]
    order: 4
  
  - id: "log-reminder"
    name: "日志提醒"
    entryUrl: "/console/pages/daily-report-reminder.html"
    icon: "ri-notification-3-line"
    roles: ["EMPLOYEE"]
    order: 1

menus:
  MANAGER:
    sceneName: "日志汇报"
    items:
      - { id: "write-log", name: "填写日志", icon: "ri-edit-line", url: "/console/pages/daily-report-form.html", order: 1 }
      - { id: "history-query", name: "历史查询", icon: "ri-history-line", url: "/console/pages/daily-report-history.html", order: 2 }
      - { id: "project-tracking", name: "项目跟踪", icon: "ri-line-chart-line", url: "/console/pages/daily-report-tracking.html", order: 3 }
      - { id: "team-logs", name: "团队日志", icon: "ri-team-line", url: "/console/pages/daily-report-team.html", order: 4 }
      - { id: "scene-config", name: "场景配置", icon: "ri-settings-3-line", url: "/console/pages/scene-config.html", order: 5 }
  
  EMPLOYEE:
    sceneName: "日志汇报"
    items:
      - { id: "log-reminder", name: "日志提醒", icon: "ri-notification-3-line", url: "/console/pages/daily-report-reminder.html", order: 1 }
      - { id: "write-log", name: "填写日志", icon: "ri-edit-line", url: "/console/pages/daily-report-form.html", order: 2 }
      - { id: "history-report", name: "历史汇报", icon: "ri-history-line", url: "/console/pages/daily-report-history.html", order: 3 }
      - { id: "submit-report", name: "日志报送", icon: "ri-send-plane-line", url: "/console/pages/daily-report-submit.html", order: 4 }
  
  HR:
    sceneName: "日志汇报"
    items:
      - { id: "team-logs", name: "团队日志", icon: "ri-team-line", url: "/console/pages/daily-report-team.html", order: 1 }
      - { id: "history-query", name: "历史查询", icon: "ri-history-line", url: "/console/pages/daily-report-history.html", order: 2 }
      - { id: "statistics", name: "统计分析", icon: "ri-bar-chart-line", url: "/console/pages/daily-report-statistics.html", order: 3 }
```

---

## 六、总结

### 6.1 配置项统计

| 配置类型 | 数量范围 | 初始化时机 | 存储位置 | 责任方 |
|----------|----------|------------|----------|--------|
| 基本信息 | 4项 | 安装时 | skill-registry | 场景技能 |
| 依赖技能 | 2-10个 | 安装前检查 | skill-dependencies | 系统 |
| UI技能 | 1-20个 | 激活时 | menu-config | 场景技能 |
| 菜单配置 | 5-50项 | 激活时 | menu-config | 场景技能 |
| 能力配置 | 1-20个 | 激活时 | capability-bindings | 场景技能 |
| 激活流程 | 3-10步 | 激活时 | activation-process | 场景技能 |

### 6.2 关键原则

1. **依赖优先**: 必需依赖必须先安装，可选依赖可以后续安装
2. **菜单统一**: 所有菜单通过公共菜单服务注册，不单独创建
3. **按需配置**: 不同角色看到不同的菜单和功能
4. **延迟初始化**: UI和菜单在激活时才初始化，不是安装时

---

**文档状态**: 规范定义  
**下一步**: 实现依赖检查和自动安装机制
