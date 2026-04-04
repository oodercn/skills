# Ooder 多平台统一调用集成指南

> **版本**: 3.0.1 | **更新日期**: 2026-04-04
> **适用范围**: IM、Todo、Calendar、OrgSync 等所有业务模块的跨平台集成

---

## 目录

1. [架构总览](#1-架构总览)
2. [快速开始（5分钟上手）](#2-快速开始)
3. [SPI 接口参考手册](#3-spi-接口参考手册)
   - 3.1 [ImService — 统一消息服务](#31-imservice--统一消息服务)
   - 3.2 [OrgSyncService — 组织架构同步](#32-orgsyncservice--组织架构同步)
   - 3.3 [TodoSyncService — 待办事项同步](#33-todosyncservice--待办事项同步)
   - 3.4 [CalendarService — 日历事件同步](#34-calendarservice--日历事件同步)
4. [平台标识规范](#4-平台标识规范)
5. [DTO 数据模型速查](#5-dto-数据模型速查)
6. [REST API 端点汇总](#6-rest-api-端点汇总)
7. [配置说明](#7-配置说明)
8. [最佳实践与注意事项](#8-最佳实践与注意事项)

---

## 1. 架构总览

### 1.1 分层架构

```
┌─────────────────────────────────────────────────────┐
│                  业务模块 (你的代码)                    │
│              Todo / Calendar / Scene / ...            │
│                                                     │
│         只依赖 skill-common (SPI 接口层)               │
└──────────────────────┬──────────────────────────────┘
                       │ SPI 接口调用 (ImService, OrgSyncService, ...)
                       ▼
┌─────────────────────────────────────────────────────┐
│              skill-common (SPI 接口定义)                │
│    ImService / OrgSyncService / TodoSyncService ...   │
│    MessageContent / SendResult / OrgUserInfo ...      │
└──────────────────────┬──────────────────────────────┘
                       │ Spring 自动注入 (SPI 实现)
                       ▼
┌─────────────────────────────────────────────────────┐
│              Driver 层 (平台实现)                      │
│                                                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │ 钉钉      │ │ 企业微信   │ │ 飞书       │            │
│  │ DingTalk  │ │ WeCom    │ │ Feishu    │            │
│  └──────────┘ └──────────┘ └──────────┘            │
│                                                     │
│  每个 Driver 独立 JAR:                               │
│  skill-im-dingding / skill-org-wecom / ...           │
└──────────────────────┬──────────────────────────────┘
                       │ RestTemplate HTTP 调用
                       ▼
┌─────────────────────────────────────────────────────┐
│                  外部平台 API                          │
│  oapi.dingtalk.com / qyapi.weixin.qq.com             │
│  open.feishu.cn                                      │
└─────────────────────────────────────────────────────┘
```

### 1.2 核心设计原则

| 原则 | 说明 |
|------|------|
| **面向接口编程** | 业务模块只依赖 `skill-common` 的 SPI 接口，不直接依赖具体平台实现 |
| **平台无关** | 通过 `platform` 参数区分目标平台，一套代码适配三大平台 |
| **零持久化** | Driver 层不做任何数据存储，100% 对接外部 API |
| **热插拔** | 通过 Spring Boot AutoConfiguration + spring.factories 实现模块化加载 |
| **缓存穿透** | SyncService 使用 ConcurrentHashMap 做 Read-Through 缓存，数据源始终来自外部 API |

---

## 2. 快速开始

### 2.1 Maven 依赖

```xml
<!-- 必须依赖：SPI 接口定义 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-common</artifactId>
    <version>3.0.1</version>
</dependency>

<!-- 按需引入平台 Driver（至少引入一个） -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-im-dingding</artifactId>
    <version>3.0.1</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skill-org-wecom</artifactId>
    <version>3.0.1</version>
</dependency>
<!-- 更多 driver... -->
```

**本地 Maven 仓库路径**: `D:\maven\.m2\repository\net\ooder\`

### 2.2 application.yml 配置

```yaml
# ====== 钉钉配置 ======
ooder:
  dingding:
    enabled: true
    app-key: ${DINGDING_APP_KEY}
    app-secret: ${DINGDING_APP_SECRET}
    api-base-url: https://oapi.dingtalk.com
    redirect-uri: https://your-domain.com/api/auth/dingding/callback
    cache-enabled: true

# ====== 企业微信配置 ======
ooder:
  wecom:
    enabled: true
    corp-id: ${WECOM_CORP_ID}
    secret: ${WECOM_SECRET}
    agent-id: ${WECOM_AGENT_ID:1000001}
    api-base-url: https://qyapi.weixin.qq.com
    redirect-uri: https://your-domain.com/api/auth/wecom/callback
    cache-enabled: true

# ====== 飞书配置 ======
ooder:
  feishu:
    enabled: true
    app-id: ${FEISHU_APP_ID}
    app-secret: ${FEISHU_APP_SECRET}
    api-base-url: https://open.feishu.cn/open-apis
    redirect-uri: https://your-domain.com/api/auth/feishu/callback
    cache-enabled: true
```

### 2.3 最简使用示例

```java
@Service
public class MyBusinessService {

    @Autowired
    private ImService imService;          // 自动注入（Spring 选择可用实现）

    @Autowired
    private OrgSyncService orgSyncService;

    public void notifyUser(String platform, String userId, String message) {
        // 发送文本消息 — 一行搞定，自动路由到对应平台
        SendResult result = imService.sendToUser(platform, userId, MessageContent.text(message));

        if (!result.isSuccess()) {
            log.error("发送失败: {}", result.getError());
        }
    }

    public void syncAndNotify(String platform) {
        // 同步组织架构
        SyncResult syncResult = orgSyncService.syncAll(platform);
        log.info("同步完成: {} 用户, {} 部门", syncResult.getUserCount(), syncResult.getDepartmentCount());

        // 获取所有用户并发送通知
        List<OrgUserInfo> users = orgSyncService.getUsers(platform);
        for (OrgUserInfo user : users) {
            if (user.isActive()) {
                imService.sendToUser(platform, user.getUserId(),
                    MessageContent.markdown("通知", "你好 **" + user.getName() + "**"));
            }
        }
    }
}
```

---

## 3. SPI 接口参考手册

### 3.1 ImService — 统一消息服务

**接口位置**: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\ImService.java`

#### 方法列表

| 方法 | 说明 | 平台参数 |
|------|------|---------|
| `sendToUser(platform, userId, content)` | 给指定用户发消息 | dingding / wecom / feishu |
| `sendToGroup(platform, groupId, content)` | 给群聊发消息 | dingding / wecom / feishu |
| `sendDing(userId, title, content)` | 发送 DING 工作通知（仅钉钉） | 固定走钉钉 |
| `sendMarkdown(platform, userId, title, markdown)` | 发送 Markdown 格式消息 | dingding / wecom / feishu |
| `getAvailablePlatforms()` | 获取当前可用的消息平台列表 | - |
| `isPlatformAvailable(platform)` | 检查某平台是否可用 | - |

#### MessageContent 构建方式

```java
// 文本消息
MessageContent text = MessageContent.text("Hello World");

// Markdown 消息
MessageContent md = MessageContent.markdown("标题", "**加粗** _斜体_ [链接](url)");

// 链接卡片消息
MessageContent link = MessageContent.link("标题", "描述文字", "https://example.com");

// 自定义构建
MessageContent custom = new MessageContent();
custom.setType(MessageType.ACTION_CARD);
custom.setTitle("审批通知");
custom.setContent("# 请审批\n申请人: 张三");
```

#### MessageType 枚举

| 值 | 说明 | 支持平台 |
|----|------|---------|
| `TEXT` | 纯文本 | 全部 |
| `MARKDOWN` | Markdown 富文本 | 全部 |
| `ACTION_CARD` | 行动卡片 | 钉钉 / 飞书 |
| `LINK` | 链接消息 | 全部 |
| `DING` | DING 工作通知 | 仅钉钉 |

#### SendResult 返回值处理

```java
SendResult result = imService.sendToUser("dingding", "user123", MessageContent.text("测试"));

if (result.isSuccess()) {
    String msgId = result.getMessageId();  // 平台返回的消息ID，可用于撤回/状态查询
} else {
    String error = result.getError();      // 错误信息
}
```

#### 各平台消息类型映射

| MessageType | 钉钉实际API | 企业微信实际API | 飞书实际API |
|-------------|------------|---------------|------------|
| TEXT | `asyncsend_v2` + msgtype=text | `/cgi-bin/message/send` + msgtype=text | POST `/im/v1/messages` + msg_type=text |
| MARKDOWN | `asyncsend_v2` + msgtype=markdown | `/cgi-bin/message/send` + msgtype=markdown | POST `/im/v1/messages` + msg_type=post (降级) |
| ACTION_CARD | `asyncsend_v2` + msgtype=actionCard | `/cgi-bin/message/send` + msgtype=textcard | POST `/im/v1/messages` + msg_type=interactive |
| LINK | `asyncsend_v2` + msgtype=link | `/cgi-bin/message/send` + msgtype=news | POST `/im/v1/messages` + msg_type=post |
| DING | `/topapi/ding/create` | 不支持(→降级为text) | 不支持(→降级为text) |

---

### 3.2 OrgSyncService — 组织架构同步

**接口位置**: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\OrgSyncService.java`

#### 方法列表

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `syncAll(platform)` | 全量同步（部门+用户） | `SyncResult` |
| `syncUsers(platform)` | 仅同步用户 | `SyncResult` |
| `syncDepartments(platform)` | 仅同步部门 | `SyncResult` |
| `getUsers(platform)` | 获取全部用户列表 | `List<OrgUserInfo>` |
| `getUsersByDepartment(platform, deptId)` | 获取部门下用户 | `List<OrgUserInfo>` |
| `getUser(platform, userId)` | 获取单个用户详情 | `OrgUserInfo` |
| `getDepartments(platform)` | 获取全部部门列表 | `List<OrgDepartmentInfo>` |
| `getDepartment(platform, deptId)` | 获取单个部门详情 | `OrgDepartmentInfo` |
| `getOrgTree(platform)` | 获取组织树形结构 | `List<OrgDepartmentInfo>` |
| `clearCache(platform)` | 清除本地缓存 | void |
| `getAvailablePlatforms()` | 可用平台列表 | `List<String>` |

#### 典型使用场景

```java
// 场景1: 定时全量同步
@Scheduled(cron = "0 0 2 * * ?")
public void scheduledSync() {
    for (String platform : orgSyncService.getAvailablePlatforms()) {
        SyncResult result = orgSyncService.syncAll(platform);
        log.info("[{}] 同步完成: {} 用户, {} 部门",
            platform, result.getUserCount(), result.getDepartmentCount());
    }
}

// 场景2: 查询用户信息
public OrgUserInfo findUser(String platform, String userId) {
    return orgSyncService.getUser(platform, userId);
}

// 场景3: 获取部门成员
public List<OrgUserInfo> getDeptMembers(String platform, String deptId) {
    return orgSyncService.getUsersByDepartment(platform, deptId);
}

// 场景4: 构建组织树
public List<OrgDepartmentInfo> getOrgStructure(String platform) {
    return orgSyncService.getOrgTree(platform);
}
```

#### SyncResult 结构

```java
SyncResult result = orgSyncService.syncAll("dingding");
if (result.isSuccess()) {
    int userCount = result.getUserCount();       // 同步的用户数
    int deptCount = result.getDepartmentCount(); // 同步的部门数
    long time = result.getSyncTime();            // 同步时间戳
} else {
    String msg = result.getMessage();            // 失败原因
}
```

---

### 3.3 TodoSyncService — 待办事项同步

**接口位置**: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\TodoSyncService.java`

#### 方法列表

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `createTodo(todo)` | 创建待办 | `TodoInfo` |
| `getTodo(todoId)` | 查看待办 | `TodoInfo` |
| `listTodos(userId)` | 用户待办列表 | `List<TodoInfo>` |
| `listTodosByStatus(userId, status)` | 按状态筛选 | `List<TodoInfo>` |
| `updateTodo(todo)` | 更新待办 | `TodoInfo` |
| `completeTodo(todoId)` | 完成待办 | `TodoInfo` |
| `deleteTodo(todoId)` | 删除待办 | void |
| `syncFromPlatform(platform)` | 从平台同步 | void |
| `getAvailablePlatforms()` | 可用平台列表 | `List<String>` |

#### TodoStatus 枚举

| 值 | 含义 |
|----|------|
| `PENDING` | 待处理 |
| `IN_PROGRESS` | 进行中 |
| `COMPLETED` | 已完成 |
| `CANCELLED` | 已取消 |

---

### 3.4 CalendarService — 日历事件同步

**接口位置**: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\CalendarService.java`

#### 方法列表

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `createEvent(event)` | 创建日程 | `EventInfo` |
| `getEvent(eventId)` | 查询日程 | `EventInfo` |
| `updateEvent(event)` | 更新日程 | `EventInfo` |
| `deleteEvent(eventId)` | 删除日程 | void |
| `listEvents(userId, start, end)` | 时间范围内日程 | `List<EventInfo>` |
| `findFreeTime(userIds, start, end)` | 查找空闲时段 | `List<TimeSlot>` |
| `syncFromPlatform(platform)` | 从平台同步 | void |
| `getAvailablePlatforms()` | 可用平台列表 | `List<String>` |

---

## 4. 平台标识规范

### 4.1 标准平台常量

```java
// 在代码中应使用以下标准字符串作为 platform 参数
public final class PlatformConstants {
    /** 钉钉 */
    public static final String DINGDING = "dingding";
    /** 企业微信 */
    public static final String WECOM = "wecom";
    /** 飞书 / Lark */
    public static final String FEISHU = "feishu";
}
```

### 4.2 平台能力差异矩阵

| 能力 | 钉钉 (dingding) | 企业微信 (wecom) | 飞书 (feishu) |
|------|:-:|:-:|:-:|
| **IM - 文本消息** | ✅ | ✅ | ✅ |
| **IM - Markdown** | ✅ | ✅ | ✅ (降级为post) |
| **IM - DING工作通知** | ✅ 独有 | ❌ → 降级text | ❌ → 降级text |
| **IM - 群消息** | ✅ | ✅ | ✅ |
| **Org - 部门CRUD** | ✅ | ✅ | ✅ |
| **Org - 用户CRUD** | ✅ | ✅ | ✅ |
| **Org - 免登(SSO)** | ✅ H5微应用 | ✅ OAuth2 code | ✅ OIDC code |
| **Org - 密码验证** | ❌ 平台不支持 | ❌ 平台不支持 | ❌ 平台不支持 |
| **Auth - 二维码登录** | ✅ | ✅ | ✅ |
| **Todo 同步** | ✅ (需driver) | ✅ (需driver) | ✅ (需driver) |
| **日历同步** | ✅ (需driver) | ✅ (需driver) | ✅ (需driver) |

### 4.3 认证机制对比

| 认证方式 | 钉钉 | 企业微信 | 飞书 |
|---------|------|---------|------|
| **应用凭证获取Token** | `POST /gettoken` (appKey+secret) | `GET /cgi-bin/gettoken` (corpId+secret) | `POST /auth/v3/tenant_access_token/internal` (appId+secret) |
| **用户免登** | H5微应用 authCode → `/user/getuserinfo` | OAuth2 code → `/auth/getuserinfo` | OIDC code → `/authen/v1/oidc/access_token` → `/user_info` |
| **密码验证** | ❌ 不支持（官方设计如此） | ❌ 不支持 | ❌ 不支持（仅授权码模式） |
| **Token有效期** | 7200秒 | 7200秒 | 7200秒 |
| **线程安全** | synchronized ensureToken | synchronized getAccessToken | synchronized ensureToken |

---

## 5. DTO 数据模型速查

### 5.1 IM 相关 DTO

**文件路径**: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\im\`

| 类名 | 关键字段 | 用途 |
|------|---------|------|
| `MessageContent` | type, title, content, url | 消息内容载体 |
| `MessageType` | TEXT/MARKDOWN/ACTION_CARD/LINK/DING | 消息类型枚举 |
| `SendResult` | success, messageId, error, timestamp | 发送结果 |

### 5.2 OrgSync 相关 DTO

**文件路径**: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\orgsync\`

| 类名 | 关键字段 | 用途 |
|------|---------|------|
| `OrgUserInfo` | userId, name, email, phone, departmentId, departmentIds(List), title, avatar, active | 标准化用户信息 |
| `OrgDepartmentInfo` | departmentId, name, parentId, managerId, memberCount, memberIds(List), level | 标准化部门信息 |
| `SyncResult` | success, userCount, departmentCount, message, syncTime | 同步操作结果 |

### 5.3 Todo 相关 DTO

**文件路径**: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\todo\`

| 类名 | 说明 |
|------|------|
| `TodoInfo` | 待办事项实体（id, title, description, status, dueDate, assignee等） |
| `TodoStatus` | PENDING / IN_PROGRESS / COMPLETED / CANCELLED |

### 5.4 Calendar 相关 DTO

**文件路径**: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\calendar\`

| 类名 | 说明 |
|------|------|
| `EventInfo` | 日历事件实体（id, title, startTime, endTime, location, attendees等） |
| `TimeSlot` | 时间段实体（startTime, endTime, isFree） |

---

## 6. REST API 端点汇总

### 6.1 钉钉 (DingTalk)

**Base URL**: `/api/v1/org/dingding`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/auth/token` | 获取应用访问令牌 |
| GET | `/auth/user-info?code=xxx` | 通过code获取用户信息 |
| GET | `/auth/qrcode` | 生成扫码登录二维码 |
| GET | `/auth/free-login?code=xxx` | H5免登 |
| GET | `/departments` | 获取全部部门列表 |
| GET | `/departments/{deptId}` | 获取部门详情 |
| POST | `/departments` | 创建子部门 |
| PUT | `/departments/{deptId}` | 更新部门 |
| DELETE | `/departments/{deptId}` | 删除部门 |
| GET | `/users` | 获取全部用户 |
| GET | `/users/dept/{deptId}` | 获取部门用户 |
| GET | `/users/{userId}` | 获取用户详情 |
| GET | `/users/by-mobile?mobile=xxx` | 手机号查用户 |
| GET | `/users/by-email?email=xxx` | 邮箱查用户 |
| POST | `/users` | 创建用户 |
| PUT | `/users/{userId}` | 更新用户 |
| DELETE | `/users/{userId}` | 删除用户 |
| POST | `/users/{userId}/disable` | 禁用用户 |
| POST | `/users/{userId}/enable` | 启用用户 |
| POST | `/sync/full` | 全量同步 |
| POST | `/sync/users` | 用户同步 |
| POST | `/sync/departments` | 部门同步 |
| GET | `/sync/tree` | 组织树 |
| DELETE | `/cache` | 清除缓存 |
| POST | `/message/read-receipt` | 标记会话已读 |

### 6.2 企业微信 (WeCom)

**Base URL**: `/api/v1/org/wecom`

端点结构与钉钉完全一致（25个端点），路径前缀改为 `/api/v1/org/wecom`。

### 6.3 飞书 (Feishu)

**Base URL**: `/api/v1/org/feishu`

端点结构同上（26个端点，额外含 OIDC 免登端点）。

---

## 7. 配置说明

### 7.1 各平台 Config 类完整字段

#### DingdingConfig (`e:\github\ooder-skills\skills\_drivers\org\skill-org-dingding\src\main\java\net\ooder\skill\org\dingding\config\DingdingConfig.java`)

| 字段 | 类型 | 默认值 | 是否必填 | 说明 |
|------|------|--------|---------|------|
| `appKey` | String | - | ✅ | 钉钉应用的 AppKey |
| `appSecret` | String | - | ✅ | 钉钉应用的 AppSecret |
| `apiBaseUrl` | String | `https://oapi.dingtalk.com` | 否 | API基础URL（测试环境可改） |
| `redirectUri` | String | - | ✅ | OAuth回调地址 |
| `enabled` | boolean | `true` | 否 | 是否启用 |
| `cacheEnabled` | boolean | `true` | 否 | 是否启用本地缓存 |

#### WeComConfig (`e:\github\ooder-skills\skills\_drivers\org\skill-org-wecom\src\main\java\net\ooder\skill\org\wecom\config\WeComConfig.java`)

| 字段 | 类型 | 默认值 | 是否必填 | 说明 |
|------|------|--------|---------|------|
| `corpId` | String | - | ✅ | 企业ID |
| `secret` | String | - | ✅ | 应用Secret |
| `agentId` | String | `1000001` | 否 | 应用AgentId |
| `apiBaseUrl` | String | `https://qyapi.weixin.qq.com` | 否 | API基础URL |
| `redirectUri` | String | - | ✅ | OAuth回调地址 |
| `cacheEnabled` | boolean | `true` | 否 | 是否启用本地缓存 |

#### FeishuConfig (`e:\github\ooder-skills\skills\_drivers\org\skill-org-feishu\src\main\java\net\ooder\skill\org\feishu\config\FeishuConfig.java`)

| 字段 | 类型 | 默认值 | 是否必填 | 说明 |
|------|------|--------|---------|------|
| `appId` | String | - | ✅ | 飞书应用ID |
| `appSecret` | String | - | ✅ | 飞书应用Secret |
| `apiBaseUrl` | String | `https://open.feishu.cn/open-apis` | 否 | API基础URL |
| `redirectUri` | String | - | ✅ | OIDC回调地址 |
| `enabled` | boolean | `true` | 否 | 是否启用 |

### 7.2 Spring Boot 自动装配

各 Driver 模块通过以下机制自动加载：

1. **spring.factories** — 声明 AutoConfiguration 类
2. ***AutoConfiguration.java** — `@Configuration` + `@ComponentScan` + `@ConditionalOnProperty`
3. **条件激活** — 当对应的 `*.enabled=true` 时自动创建 Bean

示例（以钉钉为例）：

```java
@Configuration
@ComponentScan(basePackages = "net.ooder.skill.org.dingding")
@ConditionalOnProperty(prefix = "ooder.dingding", name = "enabled", havingValue = "true")
public class DingTalkOrgAutoConfiguration { }
```

---

## 8. 最佳实践与注意事项

### 8.1 必须遵守的规则

| 规则 | 说明 |
|------|------|
| **❌ 禁止自行持久化** | 所有 Driver 层不实现数据库写入，数据100%来自外部API |
| **❌ 禁止Mock/Fake** | 所有 ApiClient 方法必须调用真实外部HTTP API |
| **❌ 禁止内存伪造数据** | ConcurrentHashMap 仅作 Read-Through 缓存 |
| **✅ 必须通过SPI接口调用** | 业务模块只依赖 skill-common 接口 |
| **✅ 必须传入platform参数** | 除 sendDing 外的所有方法都需要 platform 参数 |

### 8.2 Token 管理

- 三大平台的 Token 有效期均为 **7200秒（2小时）**
- 内部自动刷新机制：`ensureToken()` / `getAccessToken()` 检测过期后自动重新获取
- **线程安全**：三个 ApiClient 的 token 获取方法均已加 `synchronized`
- **无需业务层关心**：Token管理对上层透明

### 8.3 缓存策略

- 使用 `ConcurrentHashMap<String, T>` 实现
- Key 为 `userId` 或 `departmentId`
- **Read-Through 模式**：先查缓存 → 未命中则调API → 写入缓存
- 手动清除：调用 `clearCache(platform)` 或 `DELETE /cache` 端点
- 重启后缓存清空，下次请求自动从API重建

### 8.4 错误处理建议

```java
// 推荐：检查 SendResult
SendResult result = imService.sendToUser("dingding", userId, content);
if (!result.isSuccess()) {
    // 根据错误类型做不同处理
    if (result.getError().contains("invalid user")) {
        // 用户不存在或已离职 → 清理本地关联
    } else if (result.getError().contains("rate limit")) {
        // 限流 → 延迟重试
    } else {
        // 其他错误 → 记录日志并告警
    }
}

// 推荐：检查 SyncResult
SyncResult result = orgSyncService.syncAll("wecom");
if (!result.isSuccess()) {
    log.error("同步失败: {}", result.getMessage());
    // 触发告警
}
```

### 8.5 性能注意

| 注意项 | 说明 | 建议 |
|--------|------|------|
| N+1 查询 | `getAllUsers()` 会遍历每个部门逐一请求 | 避免频繁调用，使用定时批量同步 |
| RestTemplate 无连接池 | 当前每次请求新建连接 | 高并发场景建议替换为 RestClient 或配置连接池 |
| 大量数据同步 | 全量同步可能耗时较长 | 使用异步执行 + 进度回调 |

### 8.6 新增平台 Driver 开发模板

如果要新增第四个平台（如 Slack、Lark 国际版等），需要：

1. 创建模块 `skill-{type}-{platform}`
2. 实现 SPI 接口（如 `ImService`）
3. 编写 `{Platform}ApiClient` 调用真实外部API
4. 编写 `{Platform}Config` 配置类
5. 编写 `{Platform}*AutoConfiguration` + `META-INF/spring.factories`
6. 在 `getAvailablePlatforms()` 返回新的 platform 标识符

---

## 附录 A: 关键文件索引（绝对路径）

### SPI 接口定义 (skill-common)
| 文件 | 路径 |
|------|------|
| ImService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\ImService.java` |
| OrgSyncService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\OrgSyncService.java` |
| TodoSyncService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\TodoSyncService.java` |
| CalendarService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\CalendarService.java` |
| MessageContent | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\im\MessageContent.java` |
| SendResult | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\im\SendResult.java` |
| OrgUserInfo | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\orgsync\OrgUserInfo.java` |
| OrgDepartmentInfo | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\orgsync\OrgDepartmentInfo.java` |
| SyncResult | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\orgsync\SyncResult.java` |

### 钉钉 Driver
| 文件 | 路径 |
|------|------|
| DingdingApiClient | `e:\github\ooder-skills\skills\_drivers\org\skill-org-dingding\src\main\java\net\ooder\skill\org\dingding\client\DingdingApiClient.java` |
| DingTalkOrgController | `e:\github\ooder-skills\skills\_drivers\org\skill-org-dingding\src\main\java\net\ooder\skill\org\dingding\controller\DingTalkOrgController.java` |
| DingTalkOrgSyncService | `e:\github\ooder-skills\skills\_drivers\org\skill-org-dingding\src\main\java\net\ooder\skill\org\dingding\service\DingTalkOrgSyncService.java` |
| DingTalkAuthService | `e:\github\ooder-skills\skills\_drivers\org\skill-org-dingding\src\main\java\net\ooder\skill\org\dingding\service\DingTalkAuthService.java` |
| DingTalkMessageService | `e:\github\ooder-skills\skills\_drivers\im\skill-im-dingding\src\main\java\net\ooder\skill\im\dingding\service\DingTalkMessageService.java` |
| DingTalkImServiceImpl | `e:\github\ooder-skills\skills\_drivers\im\skill-im-dingding\src\main\java\net\ooder\skill\im\dingding\spi\DingTalkImServiceImpl.java` |
| DingdingConfig | `e:\github\ooder-skills\skills\_drivers\org\skill-org-dingding\src\main\java\net\ooder\skill\org\dingding\config\DingdingConfig.java` |

### 企业微信 Driver
| 文件 | 路径 |
|------|------|
| WeComApiClient | `e:\github\ooder-skills\skills\_drivers\org\skill-org-wecom\src\main\java\net\ooder\skill\org\wecom\client\WeComApiClient.java` |
| WeComOrgController | `e:\github\ooder-skills\skills\_drivers\org\skill-org-wecom\src\main\java\net\ooder\skill\org\wecom\controller\WeComOrgController.java` |
| WeComOrgSyncService | `e:\github\ooder-skills\skills\_drivers\org\skill-org-wecom\src\main\java\net\ooder\skill\org\wecom\service\WeComOrgSyncService.java` |
| WeComAuthService | `e:\github\ooder-skills\skills\_drivers\org\skill-org-wecom\src\main\java\net\ooder\skill\org\wecom\service\WeComAuthService.java` |
| WeComMessageService | `e:\github\ooder-skills\skills\_drivers\im\skill-im-wecom\src\main\java\net\ooder\skill\im\wecom\service\WeComMessageService.java` |
| WeComImServiceImpl | `e:\github\ooder-skills\skills\_drivers\im\skill-im-wecom\src\main\java\net\ooder\skill\im\wecom\spi\WeComImServiceImpl.java` |
| WeComConfig | `e:\github\ooder-skills\skills\_drivers\org\skill-org-wecom\src\main\java\net\ooder\skill\org\wecom\config\WeComConfig.java` |

### 飞书 Driver
| 文件 | 路径 |
|------|------|
| FeishuApiClient | `e:\github\ooder-skills\skills\_drivers\org\skill-org-feishu\src\main\java\net\ooder\skill\org\feishu\client\FeishuApiClient.java` |
| FeishuOrgController | `e:\github\ooder-skills\skills\_drivers\org\skill-org-feishu\src\main\java\net\ooder\skill\org\feishu\controller\FeishuOrgController.java` |
| FeishuOrgSyncService | `e:\github\ooder-skills\skills\_drivers\org\skill-org-feishu\src\main\java\net\ooder\skill\org\feishu\service\FeishuOrgSyncService.java` |
| FeishuAuthService | `e:\github\ooder-skills\skills\_drivers\org\skill-org-feishu\src\main\java\net\ooder\skill\org\feishu\service\FeishuAuthService.java` |
| FeishuMessageService | `e:\github\ooder-skills\skills\_drivers\im\skill-im-feishu\src\main\java\net\ooder\skill\im\feishu\service\FeishuMessageService.java` |
| FeishuImServiceImpl | `e:\github\ooder-skills\skills\_drivers\im\skill-im-feishu\src\main\java\net\ooder\skill\im\feishu\spi\FeishuImServiceImpl.java` |
| FeishuConfig | `e:\github\ooder-skills\skills\_drivers\org\skill-org-feishu\src\main\java\net\ooder\skill\org\feishu\config\FeishuConfig.java` |

### Framework 核心
| 文件 | 路径 |
|------|------|
| ServiceRegistry | `e:\github\ooder-skills\skill-hotplug-starter\src\main\java\net\ooder\skill\hotplug\registry\ServiceRegistry.java` |
| RouteRegistry | `e:\github\ooder-skills\skill-hotplug-starter\src\main\java\net\ooder\skill\hotplug\registry\RouteRegistry.java` |

---

*文档结束 — 基于 ooder-skills 3.0.1 版本审计后生成*
