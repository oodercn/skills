# IM 集成项目协同需求说明

> **版本**: v1.0  
> **创建日期**: 2026-03-29  
> **项目**: ooder-skills + apex IM 集成  
> **状态**: 进行中

---

## 一、项目进度总览

### 1.1 已完成任务 (6项)

| 任务ID | 任务名称 | 负责人 | 状态 | 完成日期 |
|--------|----------|--------|------|----------|
| IM-001 | 钉钉组织服务 Skill | AI Assistant | ✅ 已完成 | 2026-03-29 |
| IM-002 | 飞书组织服务 Skill 升级 | AI Assistant | ✅ 已完成 | 2026-03-29 |
| IM-003 | 企业微信组织服务升级 | AI Assistant | ✅ 已完成 | 2026-03-29 |
| IM-004 | 多渠道消息推送服务 | AI Assistant | ✅ 已完成 | 2026-03-29 |
| IM-005 | skill-index 索引更新 | AI Assistant | ✅ 已完成 | 2026-03-29 |
| APEX-001 | PlatformAuthBindingController | AI Assistant | ✅ 已完成 | 2026-03-29 |

### 1.2 待完成任务 (11项)

| 任务ID | 任务名称 | 优先级 | 预计工时 | 协同方 |
|--------|----------|--------|----------|--------|
| SKILL-001 | 钉钉 IM 服务 Skill | P1 | 3天 | 后端开发 |
| SKILL-002 | 飞书 IM 服务 Skill | P1 | 3天 | 后端开发 |
| SKILL-003 | 企业微信 IM 服务 Skill | P1 | 2天 | 后端开发 |
| SKILL-004 | 平台绑定场景 Skill | P1 | 2天 | 后端开发 |
| SKILL-005 | 日程管理 Skill | P2 | 2天 | 后端开发 |
| SKILL-006 | 待办同步 Skill | P2 | 1天 | 后端开发 |
| SKILL-007 | 文档协作 Skill | P2 | 2天 | 后端开发 |
| SKILL-008 | AI Agent CLI Skills 集成 | P1 | 3天 | 后端开发 |
| APEX-002 | 组织同步服务升级 | P0 | 1天 | 全栈开发 |
| APEX-003 | 消息通知服务升级 | P1 | 1天 | 全栈开发 |
| APEX-004 | 平台绑定前端页面 | P0 | 1天 | 前端开发 |

---

## 二、协同任务详情

### 2.1 前端开发协同 (APEX-004)

#### 任务描述

开发平台绑定管理页面，用户可通过扫码绑定钉钉/飞书/企业微信账号。

#### 协同需求

**后端已提供 API：**

| API | 方法 | 说明 |
|-----|------|------|
| `/api/v1/auth/bind/platforms` | GET | 获取可用平台列表 |
| `/api/v1/auth/bind/dingtalk/qrcode` | POST | 生成钉钉扫码二维码 |
| `/api/v1/auth/bind/feishu/qrcode` | POST | 生成飞书扫码二维码 |
| `/api/v1/auth/bind/wecom/qrcode` | POST | 生成企业微信扫码二维码 |
| `/api/v1/auth/bind/status/{platform}/{sessionId}` | GET | 检查绑定状态 |
| `/api/v1/auth/bind/callback/{platform}` | GET | OAuth回调处理 |
| `/api/v1/auth/bind/{platform}` | DELETE | 解绑平台 |

**前端需要实现：**

1. 平台绑定卡片组件
   - 显示平台图标、名称、绑定状态
   - 扫码按钮触发二维码弹窗
   - 解绑按钮

2. 二维码弹窗组件
   - 显示平台扫码二维码
   - 轮询绑定状态（每2秒）
   - 状态显示：待扫码 → 已扫码 → 已确认 → 绑定成功

3. 绑定历史记录列表

**文件位置：**
- `e:\apex\app\src\main\resources\static\console\pages\platform-binding.html`

**交付物：**
- [ ] platform-binding.html 页面
- [ ] 相关 JS 组件
- [ ] CSS 样式

---

### 2.2 后端开发协同 (SKILL-001/002/003)

#### 任务描述

开发钉钉、飞书、企业微信的 IM 服务 Skill，提供消息发送、日程管理等功能。

#### 协同需求

**依赖已完成模块：**

| 模块 | 路径 | 说明 |
|------|------|------|
| skill-org-dingding | `skills/_drivers/org/skill-org-dingding` | 钉钉组织服务 |
| skill-org-feishu | `skills/_drivers/org/skill-org-feishu` | 飞书组织服务 |
| skill-org-wecom | `skills/_drivers/org/skill-org-wecom` | 企业微信组织服务 |
| skill-msg-push | `skills/tools/skill-msg-push` | 多渠道消息推送 |

**需要实现：**

1. **钉钉 IM 服务 (SKILL-001)**
   ```
   skills/_drivers/im/skill-im-dingding/
   ├── src/main/java/net/ooder/skill/im/dingding/
   │   ├── controller/DingTalkImController.java
   │   ├── service/DingTalkMessageService.java
   │   └── dto/MessageDTO.java
   └── skill.yaml
   ```

2. **飞书 IM 服务 (SKILL-002)**
   ```
   skills/_drivers/im/skill-im-feishu/
   ├── src/main/java/net/ooder/skill/im/feishu/
   │   ├── controller/FeishuImController.java
   │   ├── service/FeishuMessageService.java
   │   └── dto/MessageDTO.java
   └── skill.yaml
   ```

3. **企业微信 IM 服务 (SKILL-003)**
   ```
   skills/_drivers/im/skill-im-wecom/
   ├── src/main/java/net/ooder/skill/im/wecom/
   │   ├── controller/WeComImController.java
   │   ├── service/WeComMessageService.java
   │   └── dto/MessageDTO.java
   └── skill.yaml
   ```

**API 规范：**

| API | 方法 | 说明 |
|-----|------|------|
| `/api/v1/im/{platform}/send` | POST | 发送消息 |
| `/api/v1/im/{platform}/ding` | POST | DING消息(钉钉) |
| `/api/v1/im/{platform}/calendar` | GET/POST | 日程管理 |
| `/api/v1/im/{platform}/todo` | GET/POST | 待办管理 |

---

### 2.3 全栈开发协同 (APEX-002/003)

#### APEX-002: 组织同步服务升级

**任务描述：**

升级 OrgWebAdapter，支持从钉钉/飞书/企业微信同步组织架构。

**协同需求：**

1. 创建 `OrgSyncAdapter` 接口
2. 实现各平台适配器
3. 支持增量同步

**文件位置：**
- `e:\apex\app\src\main\java\net\ooder\mvp\skill\scene\adapter\`

**依赖：**
- skill-org-dingding API
- skill-org-feishu API
- skill-org-wecom API

---

#### APEX-003: 消息通知服务升级

**任务描述：**

升级 NotificationController，支持多渠道消息发送。

**协同需求：**

1. 新增多渠道发送接口
2. 集成 skill-msg-push 服务

**文件位置：**
- `e:\apex\app\src\main\java\net\ooder\mvp\skill\scene\controller\NotificationController.java`

---

## 三、开箱即用闭环检查

### 3.1 生命周期闭环检查

#### skill-org-dingding ✅

| 检查项 | API | 状态 |
|--------|-----|------|
| 创建 | `POST /api/v1/org/dingtalk/sync/all` | ✅ |
| 查询 | `GET /api/v1/org/dingtalk/users` | ✅ |
| 查询 | `GET /api/v1/org/dingtalk/departments` | ✅ |
| 删除 | `DELETE /api/v1/org/dingtalk/cache` | ✅ |

#### skill-org-feishu ✅

| 检查项 | API | 状态 |
|--------|-----|------|
| 创建 | `POST /api/v1/org/feishu/sync/all` | ✅ |
| 查询 | `GET /api/v1/org/feishu/users` | ✅ |
| 查询 | `GET /api/v1/org/feishu/departments` | ✅ |
| 删除 | `DELETE /api/v1/org/feishu/cache` | ✅ |

#### skill-org-wecom ✅

| 检查项 | API | 状态 |
|--------|-----|------|
| 创建 | `POST /api/v1/org/wecom/sync/all` | ✅ |
| 查询 | `GET /api/v1/org/wecom/users` | ✅ |
| 查询 | `GET /api/v1/org/wecom/departments` | ✅ |
| 删除 | `DELETE /api/v1/org/wecom/cache` | ✅ |

#### skill-msg-push ✅

| 检查项 | API | 状态 |
|--------|-----|------|
| 创建 | `POST /api/v1/msg/push/send` | ✅ |
| 查询 | `GET /api/v1/msg/push/channels` | ✅ |
| 批量 | `POST /api/v1/msg/push/broadcast` | ✅ |

#### PlatformAuthBinding ✅

| 检查项 | API | 状态 |
|--------|-----|------|
| 创建 | `POST /api/v1/auth/bind/{platform}/qrcode` | ✅ |
| 查询 | `GET /api/v1/auth/bind/status/{platform}/{sessionId}` | ✅ |
| 回调 | `GET /api/v1/auth/bind/callback/{platform}` | ✅ |
| 删除 | `DELETE /api/v1/auth/bind/{platform}` | ✅ |

### 3.2 数据实体闭环检查

| 模块 | DTO定义 | 状态 |
|------|---------|------|
| skill-org-dingding | QrCodeDTO, AuthTokenDTO, SyncResultDTO | ✅ |
| skill-org-feishu | QrCodeDTO, AuthTokenDTO, SyncResultDTO | ✅ |
| skill-org-wecom | QrCodeDTO, AuthTokenDTO, SyncResultDTO | ✅ |
| skill-msg-push | PushRequestDTO, PushResultDTO | ✅ |
| PlatformAuthBinding | PlatformQrCodeDTO, PlatformAuthTokenDTO, PlatformBindStatusDTO | ✅ |

### 3.3 按钮API闭环检查

#### 扫码绑定流程 ✅

```
用户点击绑定按钮
    ↓
前端调用 POST /api/v1/auth/bind/{platform}/qrcode
    ↓
显示二维码弹窗
    ↓
前端轮询 GET /api/v1/auth/bind/status/{platform}/{sessionId}
    ↓
状态变为 CONFIRMED
    ↓
前端调用 GET /api/v1/auth/bind/callback/{platform}
    ↓
绑定成功，刷新页面
```

#### 消息发送流程 ✅

```
用户填写消息内容
    ↓
选择发送渠道
    ↓
前端调用 POST /api/v1/msg/push/send
    ↓
后端调用对应渠道 Channel
    ↓
返回发送结果
    ↓
前端显示发送状态
```

---

## 四、待完成闭环项

### 4.1 前端页面闭环 (待开发)

| 页面 | 按钮闭环 | 状态 |
|------|----------|------|
| platform-binding.html | 扫码绑定按钮 → API调用 → 状态轮询 → 结果显示 | ❌ 待开发 |
| org-sync.html | 同步按钮 → API调用 → 进度显示 → 结果刷新 | ❌ 待开发 |
| notification-center.html | 发送按钮 → API调用 → 渠道选择 → 结果显示 | ❌ 待开发 |

### 4.2 IM服务闭环 (待开发)

| 服务 | 发送消息闭环 | 状态 |
|------|--------------|------|
| skill-im-dingding | 发送按钮 → API → 钉钉SDK → 结果 | ❌ 待开发 |
| skill-im-feishu | 发送按钮 → API → 飞书SDK → 结果 | ❌ 待开发 |
| skill-im-wecom | 发送按钮 → API → 企业微信SDK → 结果 | ❌ 待开发 |

---

## 五、协同开发时间线

```
Week 2 (当前):
├── Day 1: APEX-004 前端页面开发 (前端)
├── Day 2: APEX-002 组织同步升级 (全栈)
├── Day 3: SKILL-001 钉钉IM服务 (后端A)
├── Day 4: SKILL-002 飞书IM服务 (后端B)
└── Day 5: SKILL-003 企业微信IM服务 (后端C)

Week 3:
├── Day 1: SKILL-004 平台绑定场景 (后端A)
├── Day 2: APEX-003 消息通知升级 (全栈)
├── Day 3: SKILL-005 日程管理 (后端B)
├── Day 4: SKILL-006 待办同步 (后端C)
└── Day 5: 集成测试

Week 4:
├── Day 1-2: SKILL-007 文档协作
├── Day 3-5: SKILL-008 AI Agent CLI集成
└── Day 5: 完整验收
```

---

## 六、联系方式

| 角色 | 负责人 | 联系方式 |
|------|--------|----------|
| 项目协调 | - | - |
| 后端开发A | 待分配 | - |
| 后端开发B | 待分配 | - |
| 后端开发C | 待分配 | - |
| 前端开发 | 待分配 | - |
| 全栈开发 | 待分配 | - |

---

## 七、相关文档

| 文档 | 路径 |
|------|------|
| 任务分配 | `e:\github\ooder-skills\docs\v2.3.1\IM-INTEGRATION-TASKS.md` |
| 协同需求 | `e:\github\ooder-skills\docs\v2.3.1\IM-COLLABORATION-REQUIREMENTS.md` |
| 新功能规范 | `e:\github\ooder-skills\.trae\skills\new-feature-guide\SKILL.md` |
| 组织索引 | `e:\github\ooder-skills\skill-index\skills\org.yaml` |
| 消息索引 | `e:\github\ooder-skills\skill-index\skills\msg.yaml` |
