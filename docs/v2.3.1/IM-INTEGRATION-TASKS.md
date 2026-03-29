# OoderAgent Skills 开发任务分配

> **版本**: v1.0  
> **创建日期**: 2026-03-29  
> **适用项目**: ooder-skills + apex  
> **开发协议**: 遵循 new-feature-guide 三闭环规范

---

## 一、任务总览

基于微信、钉钉、飞书 CLI 方案调研，按照 ooderAgent skills 开发协议，将开发任务分为三大模块：

| 模块 | 任务数量 | 优先级 | 预计周期 |
|------|----------|--------|----------|
| 原有IM集成调整 | 5项 | P0 | 1-2周 |
| 新功能Skill开发 | 8项 | P1 | 2-3周 |
| Apex配合开发 | 6项 | P1 | 2周 |

---

## 二、模块一：原有IM集成调整

### 2.1 现有架构分析

```
现有 Skills 结构：
skills/
├── _drivers/
│   └── org/
│       ├── skill-org-base/      # 组织基础服务
│       ├── skill-org-feishu/    # 飞书组织服务
│       ├── skill-org-wecom/     # 企业微信组织服务
│       └── skill-org-ldap/      # LDAP组织服务
└── skill-index/
    └── skills/
        ├── org.yaml             # 组织类技能索引
        └── msg.yaml             # 消息类技能索引
```

### 2.2 调整任务清单

#### 任务 IM-001：钉钉组织服务 Skill 开发 ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | IM-001 |
| **任务名称** | skill-org-dingding 开发 |
| **优先级** | P0 |
| **负责人** | AI Assistant |
| **预计工时** | 3天 |
| **状态** | ✅ 已完成 |

**开发内容：**

```yaml
skillId: skill-org-dingding
name: 钉钉组织服务
version: "2.3.1"
capabilityCategory: org
subCategory: dingtalk
skillForm: DRIVER
description: 钉钉组织数据集成服务，基于钉钉CLI开源能力
```

**三闭环检查项：**

| 闭环类型 | 检查项 | API |
|----------|--------|-----|
| 生命周期闭环 | 创建 | `POST /api/v1/org/dingtalk/sync` |
| 生命周期闭环 | 查询 | `GET /api/v1/org/dingtalk/users` |
| 生命周期闭环 | 更新 | `PUT /api/v1/org/dingtalk/config` |
| 生命周期闭环 | 删除 | `DELETE /api/v1/org/dingtalk/cache` |
| 数据实体闭环 | 用户同步 | `syncUsers()` |
| 数据实体闭环 | 部门同步 | `syncDepartments()` |
| 按钮API闭环 | 扫码绑定 | `bindByScan()` |

**文件结构：**

```
skills/_drivers/org/skill-org-dingding/
├── pom.xml
├── src/main/java/net/ooder/skill/org/dingding/
│   ├── DingTalkOrgSkill.java           # Skill主类
│   ├── DingTalkOrgConfig.java          # 配置类
│   ├── service/
│   │   ├── DingTalkOrgSyncService.java # 组织同步服务
│   │   └── DingTalkAuthService.java    # 认证服务
│   ├── adapter/
│   │   └── DingTalkOrgAdapter.java     # 组织适配器
│   └── dto/
│       ├── DingTalkUserDTO.java
│       └── DingTalkDeptDTO.java
└── src/main/resources/
    └── skill.yaml
```

---

#### 任务 IM-002：飞书组织服务 Skill 升级 ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | IM-002 |
| **任务名称** | skill-org-feishu 升级（支持CLI） |
| **优先级** | P0 |
| **负责人** | AI Assistant |
| **预计工时** | 2天 |
| **状态** | ✅ 已完成 |

**升级内容：**

1. 集成飞书官方 CLI (`@larksuite/cli`)
2. 新增扫码授权功能
3. 支持 19 个开箱即用的 AI Agent Skills

**新增API：**

```java
@RestController
@RequestMapping("/api/v1/org/feishu")
public class FeishuOrgController {
    
    @PostMapping("/auth/qrcode")
    public ResultModel<QrCodeDTO> generateQrCode();
    
    @GetMapping("/auth/callback")
    public ResultModel<AuthTokenDTO> handleCallback(@RequestParam String code);
    
    @PostMapping("/sync/users")
    public ResultModel<SyncResultDTO> syncUsers();
    
    @PostMapping("/sync/departments")
    public ResultModel<SyncResultDTO> syncDepartments();
}
```

---

#### 任务 IM-003：企业微信组织服务 Skill 升级 ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | IM-003 |
| **任务名称** | skill-org-wecom 升级 |
| **优先级** | P0 |
| **负责人** | AI Assistant |
| **预计工时** | 2天 |
| **状态** | ✅ 已完成 |

**升级内容：**

1. 完善企业微信扫码登录
2. 新增组织架构全量同步
3. 支持增量同步

---

#### 任务 IM-004：统一消息推送服务 Skill 开发 ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | IM-004 |
| **任务名称** | skill-msg-push 多渠道消息推送 |
| **优先级** | P0 |
| **负责人** | AI Assistant |
| **预计工时** | 3天 |
| **状态** | ✅ 已完成 |

**开发内容：**

```yaml
skillId: skill-msg-push
name: 多渠道消息推送服务
version: "2.3.1"
capabilityCategory: msg
subCategory: push
skillForm: PROVIDER
description: 统一消息推送服务，支持钉钉DING、飞书消息、企业微信
```

**支持渠道：**

| 渠道 | 方法 | 说明 |
|------|------|------|
| 钉钉DING | `sendDing()` | 高优先级提醒 |
| 钉钉机器人 | `sendDingBot()` | 群消息推送 |
| 飞书消息 | `sendFeishu()` | 即时消息 |
| 企业微信 | `sendWeCom()` | 应用消息 |

**文件结构：**

```
skills/tools/skill-msg-push/
├── pom.xml
├── src/main/java/net/ooder/skill/msg/push/
│   ├── MsgPushSkill.java
│   ├── channel/
│   │   ├── DingTalkChannel.java
│   │   ├── FeishuChannel.java
│   │   └── WeComChannel.java
│   └── dto/
│       ├── PushRequestDTO.java
│       └── PushResultDTO.java
└── skill.yaml
```

---

#### 任务 IM-005：IM Skill 索引更新 ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | IM-005 |
| **任务名称** | skill-index 更新 |
| **优先级** | P0 |
| **负责人** | AI Assistant |
| **预计工时** | 0.5天 |
| **状态** | ✅ 已完成 |

**更新文件：**

- `skill-index/skills/org.yaml` - 新增钉钉组织服务
- `skill-index/skills/msg.yaml` - 新增多渠道推送服务

---

## 三、模块二：新功能 Skill 开发

### 3.1 新增 Skills 规划

```
新增 Skills 结构：
skills/
├── _drivers/
│   └── im/                          # 新增IM驱动目录
│       ├── skill-im-dingding/       # 钉钉IM服务
│       ├── skill-im-feishu/         # 飞书IM服务
│       └── skill-im-wecom/          # 企业微信IM服务
├── scenes/
│   └── skill-platform-bind/         # 平台绑定场景
└── tools/
    ├── skill-calendar/              # 日程管理
    ├── skill-todo-sync/             # 待办同步
    └── skill-doc-collab/            # 文档协作
```

### 3.2 开发任务清单

#### 任务 SKILL-001：钉钉 IM 服务 Skill ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | SKILL-001 |
| **任务名称** | skill-im-dingding 开发 |
| **优先级** | P1 |
| **负责人** | AI Assistant |
| **预计工时** | 3天 |
| **状态** | ✅ 已完成 |

**功能清单：**

| 功能 | API | 说明 |
|------|-----|------|
| 发送消息 | `POST /api/v1/im/dingding/send` | 单聊/群聊 |
| DING消息 | `POST /api/v1/im/dingding/ding` | 高优先级 |
| 日程查询 | `GET /api/v1/im/dingding/calendar` | 查询空闲 |
| 待办同步 | `POST /api/v1/im/dingding/todo` | 创建待办 |

**字典定义：**

```java
@Dict(code = "ding_msg_type", name = "钉钉消息类型")
public enum DingMsgType implements DictItem {
    TEXT("TEXT", "文本消息", "普通文本", "ri-message-line", 1),
    MARKDOWN("MARKDOWN", "Markdown", "富文本消息", "ri-file-text-line", 2),
    DING("DING", "DING消息", "高优先级提醒", "ri-notification-line", 3),
    ACTION_CARD("ACTION_CARD", "卡片消息", "交互卡片", "ri-layout-card-line", 4);
}
```

---

#### 任务 SKILL-002：飞书 IM 服务 Skill ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | SKILL-002 |
| **任务名称** | skill-im-feishu 开发 |
| **优先级** | P1 |
| **负责人** | AI Assistant |
| **预计工时** | 3天 |
| **状态** | ✅ 已完成 |

**功能清单：**

| 功能 | API | 说明 |
|------|-----|------|
| 发送消息 | `POST /api/v1/im/feishu/send` | 单聊/群聊 |
| 创建文档 | `POST /api/v1/im/feishu/doc` | 飞书文档 |
| 日程管理 | `POST /api/v1/im/feishu/calendar` | 日历事件 |
| 多维表格 | `POST /api/v1/im/feishu/bitable` | 数据操作 |

---

#### 任务 SKILL-003：企业微信 IM 服务 Skill ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | SKILL-003 |
| **任务名称** | skill-im-wecom 开发 |
| **优先级** | P1 |
| **负责人** | AI Assistant |
| **预计工时** | 2天 |
| **状态** | ✅ 已完成 |

#### 任务 SKILL-004：平台绑定场景 Skill ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | SKILL-004 |
| **任务名称** | skill-platform-bind 开发 |
| **优先级** | P1 |
| **负责人** | AI Assistant |
| **预计工时** | 2天 |
| **状态** | ✅ 已完成 |

**功能清单：**

| 功能 | API | 说明 |
|------|-----|------|
| 生成二维码 | `POST /api/v1/bind/{platform}/qrcode` | 扫码绑定 |
| 检查状态 | `GET /api/v1/bind/{platform}/status` | 轮询状态 |
| 回调处理 | `GET /api/v1/bind/{platform}/callback` | OAuth回调 |
| 解绑平台 | `DELETE /api/v1/bind/{platform}` | 解除绑定 |

**支持的绑定状态：**

```java
@Dict(code = "bind_status", name = "绑定状态")
public enum BindStatus implements DictItem {
    PENDING("PENDING", "待扫码", "等待用户扫码", "ri-qr-code-line", 1),
    SCANNED("SCANNED", "已扫码", "用户已扫码待确认", "ri-smartphone-line", 2),
    CONFIRMED("CONFIRMED", "已确认", "用户已确认授权", "ri-check-line", 3),
    BOUND("BOUND", "已绑定", "绑定成功", "ri-link-line", 4),
    EXPIRED("EXPIRED", "已过期", "二维码过期", "ri-time-line", 5),
    FAILED("FAILED", "绑定失败", "绑定过程失败", "ri-close-line", 6);
}
```

---

#### 任务 SKILL-005：日程管理 Skill ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | SKILL-005 |
| **任务名称** | skill-calendar 开发 |
| **优先级** | P2 |
| **负责人** | AI Assistant |
| **预计工时** | 2天 |
| **状态** | ✅ 已完成 |

**功能清单：**

| 功能 | API | 说明 |
|------|-----|------|
| 创建日程 | `POST /api/v1/calendar` | 创建日程事件 |
| 查询空闲 | `GET /api/v1/calendar/free` | 查询空闲时段 |
| 预约会议 | `POST /api/v1/calendar/meeting` | 预约会议 |
| 同步日程 | `POST /api/v1/calendar/sync` | 同步到平台 |

---

#### 任务 SKILL-006：待办同步 Skill ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | SKILL-006 |
| **任务名称** | skill-todo-sync 开发 |
| **优先级** | P2 |
| **负责人** | AI Assistant |
| **预计工时** | 1天 |
| **状态** | ✅ 已完成 |

---

#### 任务 SKILL-007：文档协作 Skill ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | SKILL-007 |
| **任务名称** | skill-doc-collab 开发 |
| **优先级** | P2 |
| **负责人** | AI Assistant |
| **预计工时** | 2天 |
| **状态** | ✅ 已完成 |

---

#### 任务 SKILL-008：AI Agent CLI Skills 集成 ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | SKILL-008 |
| **任务名称** | AI Agent CLI Skills |
| **优先级** | P1 |
| **负责人** | AI Assistant |
| **预计工时** | 3天 |
| **状态** | ✅ 已完成 |

**集成内容：**

1. 钉钉 CLI 10项核心能力封装
2. 飞书 CLI 19个 Agent Skills 封装
3. 自然语言指令解析

---

## 四、模块三：Apex 配合开发

### 4.1 后端 API 开发任务

#### 任务 APEX-001：平台认证绑定 Controller ✅ 已完成

| 属性 | 内容 |
|------|------|
| **任务ID** | APEX-001 |
| **任务名称** | PlatformAuthBindingController 开发 |
| **优先级** | P0 |
| **负责人** | AI Assistant |
| **预计工时** | 1天 |
| **状态** | ✅ 已完成 |

**文件位置：** `e:\apex\app\src\main\java\net\ooder\mvp\skill\scene\controller\PlatformAuthBindingController.java`

**API设计：**

```java
@RestController
@RequestMapping("/api/v1/auth/bind")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlatformAuthBindingController {

    @PostMapping("/dingtalk/qrcode")
    public ResultModel<QrCodeDTO> getDingTalkQrCode();
    
    @PostMapping("/feishu/qrcode")
    public ResultModel<QrCodeDTO> getFeishuQrCode();
    
    @PostMapping("/wecom/qrcode")
    public ResultModel<QrCodeDTO> getWeComQrCode();
    
    @GetMapping("/callback/{platform}")
    public ResultModel<AuthTokenDTO> handleCallback(
        @PathVariable String platform,
        @RequestParam String code);
    
    @GetMapping("/status/{sessionId}")
    public ResultModel<BindStatusDTO> checkBindStatus(@PathVariable String sessionId);
    
    @DeleteMapping("/{platform}")
    public ResultModel<Boolean> unbind(@PathVariable String platform);
}
```

---

#### 任务 APEX-002：组织同步服务升级

| 属性 | 内容 |
|------|------|
| **任务ID** | APEX-002 |
| **任务名称** | OrgWebAdapter 升级 |
| **优先级** | P0 |
| **负责人** | 待分配 |
| **预计工时** | 1天 |

**升级内容：**

1. 新增 `OrgSyncAdapter` 接口
2. 实现钉钉、飞书、企业微信适配器
3. 支持增量同步

**文件位置：** `e:\apex\app\src\main\java\net\ooder\mvp\skill\scene\adapter\`

---

#### 任务 APEX-003：消息通知服务升级

| 属性 | 内容 |
|------|------|
| **任务ID** | APEX-003 |
| **任务名称** | NotificationController 升级 |
| **优先级** | P1 |
| **负责人** | 待分配 |
| **预计工时** | 1天 |

**升级内容：**

1. 新增多渠道发送接口
2. 支持钉钉DING消息
3. 支持飞书消息推送

---

### 4.2 前端页面开发任务

#### 任务 APEX-004：平台绑定页面

| 属性 | 内容 |
|------|------|
| **任务ID** | APEX-004 |
| **任务名称** | platform-binding.html 开发 |
| **优先级** | P0 |
| **负责人** | 待分配 |
| **预计工时** | 1天 |

**文件位置：** `e:\apex\app\src\main\resources\static\console\pages\platform-binding.html`

**页面功能：**

```
┌─────────────────────────────────────────────────────────────┐
│                    平台绑定管理                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   钉钉绑定   │  │  飞书绑定   │  │ 企业微信绑定 │         │
│  │   [扫码]    │  │   [扫码]    │  │   [扫码]    │         │
│  │   已绑定 ✓  │  │   未绑定    │  │   已绑定 ✓  │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  绑定历史记录                                        │   │
│  │  ─────────────────────────────────────────────────  │   │
│  │  2026-03-29 10:00  钉钉绑定成功  user@company.com   │   │
│  │  2026-03-28 15:30  飞书解绑      admin@company.com  │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

#### 任务 APEX-005：组织同步管理页面

| 属性 | 内容 |
|------|------|
| **任务ID** | APEX-005 |
| **任务名称** | org-sync.html 开发 |
| **优先级** | P1 |
| **负责人** | 待分配 |
| **预计工时** | 1天 |

**页面功能：**

- 平台连接配置
- 手动同步按钮
- 定时同步配置
- 同步日志查看

---

#### 任务 APEX-006：消息中心升级

| 属性 | 内容 |
|------|------|
| **任务ID** | APEX-006 |
| **任务名称** | notification-center.html 升级 |
| **优先级** | P1 |
| **负责人** | 待分配 |
| **预计工时** | 0.5天 |

**升级内容：**

- 新增渠道选择（钉钉/飞书/企业微信）
- 新增消息模板管理
- 新增发送记录

---

## 五、开发任务分配表

### 5.1 按角色分配

| 角色 | 任务ID | 任务名称 | 工时 |
|------|--------|----------|------|
| **后端开发A** | IM-001 | 钉钉组织服务 | 3天 |
| | IM-005 | 索引更新 | 0.5天 |
| | SKILL-001 | 钉钉IM服务 | 3天 |
| **后端开发B** | IM-002 | 飞书组织升级 | 2天 |
| | SKILL-002 | 飞书IM服务 | 3天 |
| | SKILL-004 | 平台绑定场景 | 2天 |
| **后端开发C** | IM-003 | 企业微信升级 | 2天 |
| | SKILL-003 | 企业微信IM | 2天 |
| | SKILL-008 | AI Agent集成 | 3天 |
| **后端开发D** | IM-004 | 消息推送服务 | 3天 |
| | SKILL-005 | 日程管理 | 2天 |
| | SKILL-006 | 待办同步 | 1天 |
| **前端开发** | APEX-004 | 平台绑定页面 | 1天 |
| | APEX-005 | 组织同步页面 | 1天 |
| | APEX-006 | 消息中心升级 | 0.5天 |
| **全栈开发** | APEX-001 | 认证绑定Controller | 1天 |
| | APEX-002 | 组织同步升级 | 1天 |
| | APEX-003 | 消息通知升级 | 1天 |

### 5.2 里程碑计划

```
Week 1:
├── Day 1-3: IM-001, IM-002, IM-003 (组织服务)
├── Day 3-5: IM-004, IM-005 (消息推送)
└── Day 5: APEX-001, APEX-002 (后端API)

Week 2:
├── Day 1-3: SKILL-001, SKILL-002, SKILL-003 (IM服务)
├── Day 3-4: SKILL-004 (平台绑定)
├── Day 4-5: APEX-004, APEX-005 (前端页面)
└── Day 5: 集成测试

Week 3:
├── Day 1-2: SKILL-005, SKILL-006 (日程待办)
├── Day 2-3: SKILL-007 (文档协作)
├── Day 3-5: SKILL-008 (AI Agent集成)
└── Day 5: 完整测试验收
```

---

## 六、三闭环检查清单

### 6.1 每个Skill必须完成的检查

```
□ 生命周期闭环
  □ 创建API: POST /api/v1/{resource}
  □ 查询API: GET /api/v1/{resource}
  □ 更新API: PUT /api/v1/{resource}/{id}
  □ 删除API: DELETE /api/v1/{resource}/{id}
  □ 状态枚举: 实现 DictItem 接口

□ 数据实体闭环
  □ 实体关系图绘制
  □ DTO定义完整
  □ 级联操作处理
  □ 外键约束验证

□ 按钮API闭环
  □ 每个按钮对应API
  □ 前端调用实现
  □ 错误处理完整
  □ 操作后刷新数据
```

### 6.2 字典注册检查

```
□ 创建枚举类实现 DictItem
□ 添加 @Dict 注解
□ 在 DictService 注册
□ 在前端添加 DICT_CODES 常量
```

---

## 七、开箱即用验收标准

### 7.1 功能验收

- [ ] 用户可通过扫码绑定钉钉/飞书/企业微信账号
- [ ] 组织架构可从绑定平台同步
- [ ] 消息可通过多渠道发送
- [ ] 日程可同步到各平台
- [ ] AI Agent 可通过自然语言调用平台能力

### 7.2 技术验收

- [ ] 所有API返回统一格式
- [ ] 所有枚举已注册字典
- [ ] 前端字典缓存正常
- [ ] 三闭环检查通过
- [ ] 单元测试覆盖

### 7.3 文档验收

- [ ] Skill README 完整
- [ ] API文档更新
- [ ] 配置说明清晰
- [ ] 使用示例完整

---

## 八、相关资源

| 资源 | 路径 |
|------|------|
| 新功能开发规范 | `e:\github\ooder-skills\.trae\skills\new-feature-guide\SKILL.md` |
| Ooder组件开发 | `~/.trae-cn/skills/ooder组件开发/SKILL.md` |
| 组织技能索引 | `e:\github\ooder-skills\skill-index\skills\org.yaml` |
| 消息技能索引 | `e:\github\ooder-skills\skill-index\skills\msg.yaml` |
| Apex项目 | `e:\apex\` |

---

**文档维护：** 本文档随开发进度持续更新，每个任务完成后更新状态。
