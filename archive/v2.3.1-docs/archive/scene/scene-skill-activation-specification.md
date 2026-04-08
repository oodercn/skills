# 场景技能激活规范文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 版本 | v1.0 |
| 创建日期 | 2026-03-09 |
| 所属模块 | skill-scene |
| 状态 | 规范定义 |

---

## 一、概述

### 1.1 激活的目的

**激活**是场景技能从"已安装"状态变为"可用"状态的关键步骤。激活的核心目的：

1. **个性化配置** - 根据用户角色配置不同的功能入口
2. **菜单生成** - 自动生成角色专属的导航菜单
3. **权限绑定** - 绑定用户与场景的访问权限
4. **能力初始化** - 初始化场景所需的各项能力

### 1.2 技能类型

| 类型 | 说明 | 示例 |
|------|------|------|
| **场景技能** | 多角色协作的完整业务场景 | 日志汇报、项目管理、审批流程 |
| **独立技能** | 单用户使用的工具类技能 | 邮件助手、日程管理、文档转换 |

---

## 二、激活流程规范

### 2.1 场景技能激活流程（领导/管理者）

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    场景技能激活流程（领导）                                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  Step 1: 确认参与者                                                              │
│  ├── 选择场景的主导者（Manager）                                                  │
│  ├── 选择协作者（Collaborator）                                                  │
│  └── 确认参与者角色分配                                                          │
│                                                                                 │
│  Step 2: 选择推送目标                                                            │
│  ├── 选择要推送的下属员工                                                        │
│  ├── 设置员工的角色（员工/HR/观察者）                                             │
│  └── 确认推送列表                                                                │
│                                                                                 │
│  Step 3: 配置驱动条件                                                            │
│  ├── 设置定时触发规则（如每天17:00提醒）                                          │
│  ├── 配置事件触发规则（如新成员加入）                                             │
│  └── 设置条件触发规则（如日志未提交）                                             │
│                                                                                 │
│  Step 4: 获取KEY                                                                 │
│  ├── 获取访问安全数据的密钥                                                      │
│  ├── 配置数据访问权限                                                            │
│  └── 确认密钥有效期                                                              │
│                                                                                 │
│  Step 5: 确认激活                                                                │
│  ├── 确认所有配置项                                                              │
│  ├── 预览激活后的效果                                                            │
│  └── 确认激活                                                                    │
│                                                                                 │
│  Step 6: 入网动作（推送通知）                                                     │
│  ├── 推送通知给选定的员工                                                        │
│  ├── 更新参与者的待办列表                                                        │
│  └── 生成领导专属菜单                                                            │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐        │
│  │  激活完成后自动生成的菜单（领导）                                      │        │
│  ├─────────────────────────────────────────────────────────────────────┤        │
│  │  📊 日志汇报                                                         │        │
│  │  ├── 📝 填写日志                                                     │        │
│  │  ├── 📋 历史查询                                                     │        │
│  │  ├── 📈 项目跟踪                                                     │        │
│  │  ├── 👥 团队日志                                                     │        │
│  │  └── ⚙️ 场景配置                                                     │        │
│  └─────────────────────────────────────────────────────────────────────┘        │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 场景技能激活流程（员工）

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    场景技能激活流程（员工）                                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  收到推送通知                                                                    │
│  └── 在"我的待办"中看到场景邀请                                                  │
│                                                                                 │
│  Step 1: 确认加入场景                                                            │
│  ├── 查看场景描述和角色                                                          │
│  ├── 确认加入                                                                    │
│  └── 接受邀请                                                                    │
│                                                                                 │
│  Step 2: 配置私有能力                                                            │
│  ├── 选择要绑定的私有能力                                                        │
│  │   ├── 邮件能力（自动获取邮件内容）                                            │
│  │   ├── Git Skill（自动获取代码提交）                                           │
│  │   └── 日历能力（自动获取日程）                                                │
│  └── 确认能力绑定                                                                │
│                                                                                 │
│  Step 3: 确认激活                                                                │
│  ├── 确认所有配置项                                                              │
│  └── 完成激活                                                                    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐        │
│  │  激活完成后自动生成的菜单（员工）                                      │        │
│  ├─────────────────────────────────────────────────────────────────────┤        │
│  │  📊 日志汇报                                                         │        │
│  │  ├── 🔔 日志提醒                                                     │        │
│  │  ├── 📝 填写日志                                                     │        │
│  │  ├── 📋 历史汇报                                                     │        │
│  │  └── 📤 日志报送                                                     │        │
│  └─────────────────────────────────────────────────────────────────────┘        │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 独立技能激活流程

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    独立技能激活流程                                               │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  Step 1: 确认安装                                                                │
│  ├── 查看技能详情                                                                │
│  ├── 确认安装                                                                    │
│  └── 开始激活                                                                    │
│                                                                                 │
│  Step 2: 配置参数                                                                │
│  ├── 配置API密钥（如需要）                                                       │
│  ├── 配置连接参数（如需要）                                                      │
│  └── 配置默认选项                                                                │
│                                                                                 │
│  Step 3: 获取KEY（如需要）                                                       │
│  ├── 获取访问密钥                                                                │
│  └── 确认权限                                                                    │
│                                                                                 │
│  Step 4: 确认激活                                                                │
│  ├── 确认所有配置项                                                              │
│  └── 完成激活                                                                    │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐        │
│  │  激活完成后自动生成的菜单                                             │        │
│  ├─────────────────────────────────────────────────────────────────────┤        │
│  │  🛠️ 邮件助手                                                         │        │
│  │  ├── 📧 发送邮件                                                     │        │
│  │  ├── 📥 收件箱                                                       │        │
│  │  └── ⚙️ 设置                                                         │        │
│  └─────────────────────────────────────────────────────────────────────┘        │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、菜单生成规范

### 3.1 菜单配置结构

```yaml
MenuConfig:
  menuId: "menu-daily-report"
  name: "日志汇报"
  icon: "ri-file-list-3-line"
  type: "SCENE"  # SCENE | SKILL
  sceneGroupId: "sg-xxx"
  roles:
    - role: "MANAGER"
      items:
        - id: "write-log"
          name: "填写日志"
          icon: "ri-edit-line"
          url: "/console/pages/daily-report-form.html"
          order: 1
        - id: "history-query"
          name: "历史查询"
          icon: "ri-history-line"
          url: "/console/pages/daily-report-history.html"
          order: 2
        - id: "project-tracking"
          name: "项目跟踪"
          icon: "ri-line-chart-line"
          url: "/console/pages/daily-report-tracking.html"
          order: 3
        - id: "team-logs"
          name: "团队日志"
          icon: "ri-team-line"
          url: "/console/pages/daily-report-team.html"
          order: 4
        - id: "scene-config"
          name: "场景配置"
          icon: "ri-settings-3-line"
          url: "/console/pages/scene-config.html"
          order: 5
    - role: "EMPLOYEE"
      items:
        - id: "log-reminder"
          name: "日志提醒"
          icon: "ri-notification-3-line"
          url: "/console/pages/daily-report-reminder.html"
          order: 1
        - id: "write-log"
          name: "填写日志"
          icon: "ri-edit-line"
          url: "/console/pages/daily-report-form.html"
          order: 2
        - id: "history-report"
          name: "历史汇报"
          icon: "ri-history-line"
          url: "/console/pages/daily-report-history.html"
          order: 3
        - id: "submit-report"
          name: "日志报送"
          icon: "ri-send-plane-line"
          url: "/console/pages/daily-report-submit.html"
          order: 4
```

### 3.2 菜单生成时机

| 时机 | 操作 | 说明 |
|------|------|------|
| 领导激活完成 | 生成领导菜单 | 根据领导角色生成专属菜单 |
| 员工激活完成 | 生成员工菜单 | 根据员工角色生成专属菜单 |
| 角色变更 | 更新菜单 | 角色变更后更新对应菜单 |
| 场景销毁 | 移除菜单 | 场景销毁后移除相关菜单 |

### 3.3 菜单存储结构

```java
public class UserMenuDTO {
    private String userId;
    private String menuId;
    private String sceneGroupId;
    private String role;
    private List<MenuItemDTO> items;
    private Long createTime;
    private Long updateTime;
}

public class MenuItemDTO {
    private String id;
    private String name;
    private String icon;
    private String url;
    private int order;
    private boolean visible;
}
```

---

## 四、场景技能初始化清单

### 4.1 日志汇报场景

```yaml
DailyReportScene:
  templateId: "tpl-daily-report"
  name: "日志汇报场景"
  type: "SCENE"
  
  capabilities:
    - capId: "report-remind"
      name: "日志提醒"
      category: "notification"
      autoBind: true
    - capId: "report-submit"
      name: "日志提交"
      category: "data-input"
      autoBind: true
    - capId: "report-aggregate"
      name: "日志汇总"
      category: "data-processing"
      autoBind: true
    - capId: "report-analyze"
      name: "日志分析"
      category: "intelligence"
      autoBind: true
    - capId: "report-ui-form"
      name: "日志填写表单"
      category: "ui"
      autoBind: true
  
  roles:
    - name: "MANAGER"
      description: "场景管理者（领导）"
      required: true
      minCount: 1
      maxCount: 1
      menuItems:
        - { id: "write-log", name: "填写日志", icon: "ri-edit-line", url: "/console/pages/daily-report-form.html" }
        - { id: "history-query", name: "历史查询", icon: "ri-history-line", url: "/console/pages/daily-report-history.html" }
        - { id: "project-tracking", name: "项目跟踪", icon: "ri-line-chart-line", url: "/console/pages/daily-report-tracking.html" }
        - { id: "team-logs", name: "团队日志", icon: "ri-team-line", url: "/console/pages/daily-report-team.html" }
        - { id: "scene-config", name: "场景配置", icon: "ri-settings-3-line", url: "/console/pages/scene-config.html" }
    - name: "EMPLOYEE"
      description: "普通员工"
      required: true
      minCount: 1
      maxCount: 100
      menuItems:
        - { id: "log-reminder", name: "日志提醒", icon: "ri-notification-3-line", url: "/console/pages/daily-report-reminder.html" }
        - { id: "write-log", name: "填写日志", icon: "ri-edit-line", url: "/console/pages/daily-report-form.html" }
        - { id: "history-report", name: "历史汇报", icon: "ri-history-line", url: "/console/pages/daily-report-history.html" }
        - { id: "submit-report", name: "日志报送", icon: "ri-send-plane-line", url: "/console/pages/daily-report-submit.html" }
  
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
  
  privateCapabilities:
    - capId: "email-skill"
      name: "邮件能力"
      description: "自动获取邮件内容"
      optional: true
    - capId: "git-skill"
      name: "Git Skill"
      description: "自动获取代码提交记录"
      optional: true
    - capId: "calendar-skill"
      name: "日历能力"
      description: "自动获取日程安排"
      optional: true
```

### 4.2 项目管理场景

```yaml
ProjectManagementScene:
  templateId: "tpl-project-management"
  name: "项目管理场景"
  type: "SCENE"
  
  capabilities:
    - capId: "project-create"
      name: "项目创建"
      category: "management"
      autoBind: true
    - capId: "task-assign"
      name: "任务分配"
      category: "management"
      autoBind: true
    - capId: "progress-track"
      name: "进度跟踪"
      category: "tracking"
      autoBind: true
    - capId: "report-generate"
      name: "报告生成"
      category: "reporting"
      autoBind: true
  
  roles:
    - name: "PROJECT_MANAGER"
      description: "项目经理"
      required: true
      minCount: 1
      maxCount: 1
      menuItems:
        - { id: "project-overview", name: "项目概览", icon: "ri-dashboard-line", url: "/console/pages/project-overview.html" }
        - { id: "task-management", name: "任务管理", icon: "ri-task-line", url: "/console/pages/task-management.html" }
        - { id: "team-management", name: "团队管理", icon: "ri-team-line", url: "/console/pages/team-management.html" }
        - { id: "progress-report", name: "进度报告", icon: "ri-file-chart-line", url: "/console/pages/progress-report.html" }
    - name: "TEAM_MEMBER"
      description: "团队成员"
      required: true
      minCount: 1
      maxCount: 50
      menuItems:
        - { id: "my-tasks", name: "我的任务", icon: "ri-task-line", url: "/console/pages/my-tasks.html" }
        - { id: "task-submit", name: "任务提交", icon: "ri-send-plane-line", url: "/console/pages/task-submit.html" }
        - { id: "project-calendar", name: "项目日历", icon: "ri-calendar-line", url: "/console/pages/project-calendar.html" }
```

### 4.3 独立技能示例

```yaml
EmailAssistantSkill:
  skillId: "skill-email-assistant"
  name: "邮件助手"
  type: "SKILL"
  
  capabilities:
    - capId: "email-send"
      name: "发送邮件"
      category: "communication"
    - capId: "email-read"
      name: "读取邮件"
      category: "communication"
    - capId: "email-summarize"
      name: "邮件摘要"
      category: "intelligence"
  
  menuItems:
    - { id: "send-email", name: "发送邮件", icon: "ri-mail-send-line", url: "/console/pages/send-email.html" }
    - { id: "inbox", name: "收件箱", icon: "ri-inbox-line", url: "/console/pages/email-inbox.html" }
    - { id: "email-settings", name: "设置", icon: "ri-settings-3-line", url: "/console/pages/email-settings.html" }
  
  activationSteps:
    - { stepId: "confirm-install", name: "确认安装", required: true }
    - { stepId: "config-parameters", name: "配置参数", required: true }
    - { stepId: "get-key", name: "获取KEY", required: false }
    - { stepId: "confirm-activation", name: "确认激活", required: true }
```

---

## 五、API接口规范

### 5.1 菜单生成API

```http
POST /api/v1/menu/generate
Content-Type: application/json

{
  "userId": "user-xxx",
  "sceneGroupId": "sg-xxx",
  "role": "MANAGER",
  "templateId": "tpl-daily-report"
}

Response:
{
  "status": "success",
  "data": {
    "menuId": "menu-xxx",
    "items": [...]
  }
}
```

### 5.2 获取用户菜单API

```http
GET /api/v1/menu/user/{userId}

Response:
{
  "status": "success",
  "data": [
    {
      "menuId": "menu-xxx",
      "name": "日志汇报",
      "icon": "ri-file-list-3-line",
      "items": [...]
    }
  ]
}
```

### 5.3 激活完成回调API

```http
POST /api/v1/activation/complete
Content-Type: application/json

{
  "installId": "install-xxx",
  "userId": "user-xxx",
  "sceneGroupId": "sg-xxx",
  "role": "MANAGER"
}

Response:
{
  "status": "success",
  "data": {
    "menuGenerated": true,
    "capabilitiesBound": true,
    "notificationsSent": true
  }
}
```

---

## 六、实施计划

### 6.1 第一阶段：菜单系统

| 任务 | 说明 | 优先级 |
|------|------|--------|
| 创建菜单数据模型 | UserMenuDTO, MenuItemDTO | 高 |
| 实现菜单生成服务 | MenuGenerationService | 高 |
| 实现菜单存储服务 | MenuStorageService | 高 |
| 更新激活流程 | 激活完成后生成菜单 | 高 |

### 6.2 第二阶段：页面开发

| 任务 | 说明 | 优先级 |
|------|------|--------|
| 创建历史查询页面 | daily-report-history.html | 高 |
| 创建项目跟踪页面 | daily-report-tracking.html | 中 |
| 创建团队日志页面 | daily-report-team.html | 中 |
| 创建日志提醒页面 | daily-report-reminder.html | 中 |

### 6.3 第三阶段：集成测试

| 任务 | 说明 | 优先级 |
|------|------|--------|
| 完整流程测试 | 从安装到使用的完整流程 | 高 |
| 菜单生成测试 | 不同角色的菜单生成 | 高 |
| 权限控制测试 | 不同角色的访问权限 | 中 |

---

## 七、验收标准

### 7.1 领导激活后

- [ ] 领导专属菜单自动生成
- [ ] 菜单包含：填写日志、历史查询、项目跟踪、团队日志、场景配置
- [ ] 菜单显示在导航栏
- [ ] 点击菜单可跳转到对应页面

### 7.2 员工激活后

- [ ] 员工专属菜单自动生成
- [ ] 菜单包含：日志提醒、填写日志、历史汇报、日志报送
- [ ] 菜单显示在导航栏
- [ ] 点击菜单可跳转到对应页面

### 7.3 场景销毁后

- [ ] 相关菜单自动移除
- [ ] 导航栏不再显示该场景菜单

---

**文档状态**: 规范定义  
**下一步**: 根据规范实施菜单系统和页面开发
