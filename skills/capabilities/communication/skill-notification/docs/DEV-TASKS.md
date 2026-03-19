# skill-notification 消息通知技能开发任务

> **技能类型**: 协作技能  
> **工程路径**: skills/capabilities/communication/skill-notification/  
> **优先级**: P1  
> **开发周期**: 1周

---

## 一、技能定位

skill-notification 是协作场景技能，提供消息推送、订阅管理、通知模板等功能。

### 1.1 职责边界

| 由本技能负责 | 由 skill-scene 负责 |
|-------------|-------------------|
| 消息发送界面 | 参与者列表 |
| 订阅管理 | 场景事件发布 |
| 邮件发送 | 权限控制 |
| 站内信 | - |

### 1.2 依赖关系

```yaml
dependencies:
  - skillId: skill-scene
    capability: participant-list
    usage: 获取场景参与者列表作为通知目标
    required: true
  
  - skillId: skill-scene
    capability: scene-event
    usage: 订阅场景事件触发通知
    required: true
```

---

## 二、能力定义

### 2.1 skill.yaml

```yaml
apiVersion: skill.ooder.net/v1
kind: Skill

metadata:
  id: skill-notification
  name: 消息通知技能
  version: 1.0.0
  description: 提供消息推送、订阅管理、通知模板功能
  author: ooder
  category: communication
  tags:
    - notification
    - message
    - email

spec:
  form: PROVIDER
  visibility: PUBLIC
  sceneTypes:
    - all
  
  capabilities:
    - id: notification-send
      name: 发送通知
      description: 发送消息通知
      category: communication
      connectorType: HTTP
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
      category: communication
      connectorType: HTTP
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
      category: communication
      connectorType: HTTP
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

  ui:
    pages:
      - path: /notification/messages.html
        title: 消息中心
        icon: ri-notification-line
    menu:
      - title: 消息通知
        icon: ri-notification-line
        path: /notification/messages.html
        order: 150
```

---

## 三、开发任务

### Phase 1: 核心功能（Week 1）

| 任务ID | 任务名称 | 工作量 | 优先级 |
|--------|----------|--------|--------|
| NOTIFY-001 | 技能骨架创建 | 1d | P0 |
| NOTIFY-002 | 消息发送实现 | 2d | P0 |
| NOTIFY-003 | 订阅管理实现 | 1d | P1 |
| NOTIFY-004 | UI界面开发 | 1d | P0 |

#### NOTIFY-001: 技能骨架创建

**产出物**:
```
skill-notification/
├── pom.xml
├── skill.yaml
├── skill-index-entry.yaml
└── src/main/
    ├── java/net/ooder/skill/notification/
    │   ├── NotificationSkill.java
    │   ├── controller/
    │   │   └── NotificationController.java
    │   ├── service/
    │   │   ├── NotificationService.java
    │   │   ├── EmailService.java
    │   │   └── impl/
    │   │       ├── NotificationServiceImpl.java
    │   │       └── EmailServiceImpl.java
    │   └── model/
    │       ├── Notification.java
    │       ├── Subscription.java
    │       └── EmailRequest.java
    └── resources/
        └── static/console/
            └── pages/notification/
                ├── messages.html
                └── messages.js
```

#### NOTIFY-002: 消息发送实现

**验收标准**:
- [ ] 支持站内信发送
- [ ] 支持邮件发送
- [ ] 支持多目标发送
- [ ] 支持消息类型分类

#### NOTIFY-003: 订阅管理实现

**验收标准**:
- [ ] 支持事件订阅
- [ ] 支持渠道选择
- [ ] 支持订阅开关

#### NOTIFY-004: UI界面开发

**参考设计**: `temp/ooder-Nexus/src/main/resources/static/console/pages/group/group-message.html`

**功能要求**:
- [ ] 消息列表展示
- [ ] 发送消息表单
- [ ] 订阅管理界面
- [ ] 未读消息提示

---

## 四、API端点

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | /api/notification/send | 发送通知 |
| GET | /api/notification/list | 消息列表 |
| PUT | /api/notification/read | 标记已读 |
| POST | /api/notification/subscribe | 订阅管理 |
| POST | /api/notification/email | 发送邮件 |
| GET | /api/notification/templates | 模板列表 |
| GET | /api/notification/unread-count | 未读数量 |

---

*文档生成时间: 2026-03-15*
