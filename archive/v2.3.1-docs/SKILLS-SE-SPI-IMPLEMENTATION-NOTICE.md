# Skills SPI 扩展实现完成通知

> **版本**: v1.0  
> **日期**: 2026-03-29  
> **发起方**: Skills 团队  
> **接收方**: Apex SE 团队  
> **状态**: 待集成

---

## 一、完成情况

Skills 团队已完成以下 SPI 扩展实现：

### 1.1 SPI 接口定义

| 接口 | 文件位置 | 状态 |
|------|----------|------|
| ImService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\ImService.java` | ✅ 完成 |
| OrgSyncService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\OrgSyncService.java` | ✅ 完成 |
| PlatformBindService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\PlatformBindService.java` | ✅ 完成 |
| TodoSyncService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\TodoSyncService.java` | ✅ 完成 |
| CalendarService | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\CalendarService.java` | ✅ 完成 |

### 1.2 DTO 类定义

| 包名 | 文件位置 | 状态 |
|------|----------|------|
| im 包 | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\im\` | ✅ 完成 |
| orgsync 包 | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\orgsync\` | ✅ 完成 |
| bind 包 | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\bind\` | ✅ 完成 |
| todo 包 | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\todo\` | ✅ 完成 |
| calendar 包 | `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\calendar\` | ✅ 完成 |

### 1.3 SceneServices 扩展

文件位置: `e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\SceneServices.java`

新增方法：
```java
ImService getImService();
OrgSyncService getOrgSyncService();
PlatformBindService getPlatformBindService();
TodoSyncService getTodoSyncService();
CalendarService getCalendarService();
```

### 1.4 SPI 实现类

| 实现类 | 文件位置 | 状态 |
|--------|----------|------|
| SkillImServiceImpl | `e:\github\ooder-skills\skills\tools\skill-msg-push\src\main\java\net\ooder\skill\msg\push\spi\SkillImServiceImpl.java` | ✅ 完成 |
| SkillOrgSyncServiceImpl | `e:\github\ooder-skills\skills\_drivers\org\skill-org-base\src\main\java\net\ooder\skill\org\base\spi\SkillOrgSyncServiceImpl.java` | ✅ 完成 |
| DingTalkOrgSyncServiceImpl | `e:\github\ooder-skills\skills\_drivers\org\skill-org-dingding\src\main\java\net\ooder\skill\org\dingding\spi\DingTalkOrgSyncServiceImpl.java` | ✅ 完成 |
| SkillPlatformBindServiceImpl | `e:\github\ooder-skills\skills\scenes\skill-platform-bind\src\main\java\net\ooder\skill\platform\bind\spi\SkillPlatformBindServiceImpl.java` | ✅ 完成 |
| SkillTodoSyncServiceImpl | `e:\github\ooder-skills\skills\tools\skill-todo-sync\src\main\java\net\ooder\skill\todo\sync\spi\SkillTodoSyncServiceImpl.java` | ✅ 完成 |
| SkillCalendarServiceImpl | `e:\github\ooder-skills\skills\tools\skill-calendar\src\main\java\net\ooder\skill\calendar\spi\SkillCalendarServiceImpl.java` | ✅ 完成 |

---

## 二、SE 团队待办事项

### 2.1 依赖配置

在 SE 的 `pom.xml` 中添加 skill-common 依赖：

```xml
<dependency>
    <groupId>net.ooder.skill</groupId>
    <artifactId>skill-common</artifactId>
    <version>${skill.version}</version>
</dependency>
```

### 2.2 功能开关配置

在 `application.yml` 中添加配置：

```yaml
skill:
  im:
    enabled: true
  org:
    sync:
      enabled: true
    dingtalk:
      enabled: true
  platform:
    bind:
      enabled: true
  todo:
    sync:
      enabled: true
  calendar:
    enabled: true
```

### 2.3 SceneServices 实现更新

SE 需要更新 `SceneServices` 实现类，添加新接口的 getter 方法：

```java
@Override
public ImService getImService() {
    return applicationContext.getBean(ImService.class);
}

@Override
public OrgSyncService getOrgSyncService() {
    return applicationContext.getBean(OrgSyncService.class);
}

@Override
public PlatformBindService getPlatformBindService() {
    return applicationContext.getBean(PlatformBindService.class);
}

@Override
public TodoSyncService getTodoSyncService() {
    return applicationContext.getBean(TodoSyncService.class);
}

@Override
public CalendarService getCalendarService() {
    return applicationContext.getBean(CalendarService.class);
}
```

### 2.4 默认实现（可选）

SE 可选择提供默认实现，当 Skills 实现不可用时使用：

```java
@Service
@ConditionalOnMissingBean(ImService.class)
public class DefaultImServiceImpl implements ImService {
    // 返回降级响应
}
```

---

## 三、使用示例

### 3.1 发送 IM 消息

```java
@Autowired
private SceneServices sceneServices;

public void sendMessage() {
    ImService imService = sceneServices.getImService();
    
    // 发送文本消息
    MessageContent content = MessageContent.text("这是一条测试消息");
    SendResult result = imService.sendToUser("dingtalk", "user123", content);
    
    // 发送 Markdown 消息
    SendResult mdResult = imService.sendMarkdown("dingtalk", "user123", "标题", "**加粗内容**");
    
    // 发送钉钉工作通知
    SendResult dingResult = imService.sendDing("user123", "提醒标题", "提醒内容");
}
```

### 3.2 组织同步

```java
@Autowired
private SceneServices sceneServices;

public void syncOrg() {
    OrgSyncService orgService = sceneServices.getOrgSyncService();
    
    // 全量同步
    SyncResult result = orgService.syncAll("dingtalk");
    
    // 获取用户列表
    List<OrgUserInfo> users = orgService.getUsers("dingtalk");
    
    // 获取部门树
    List<OrgDepartmentInfo> depts = orgService.getOrgTree("dingtalk");
}
```

### 3.3 平台绑定

```java
@Autowired
private SceneServices sceneServices;

public void bindPlatform() {
    PlatformBindService bindService = sceneServices.getPlatformBindService();
    
    // 生成绑定二维码
    QrCodeInfo qrCode = bindService.generateBindQrCode("dingtalk");
    
    // 检查绑定状态
    BindStatus status = bindService.checkBindStatus("dingtalk", qrCode.getSessionId());
    
    // 获取用户绑定信息
    BindInfo binding = bindService.getBinding("dingtalk", "user123");
}
```

### 3.4 待办同步

```java
@Autowired
private SceneServices sceneServices;

public void manageTodo() {
    TodoSyncService todoService = sceneServices.getTodoSyncService();
    
    // 创建待办
    TodoInfo todo = new TodoInfo("完成报告", "user123");
    TodoInfo created = todoService.createTodo(todo);
    
    // 查询待办
    List<TodoInfo> todos = todoService.listTodos("user123");
    
    // 完成待办
    todoService.completeTodo(created.getTodoId());
}
```

### 3.5 日程管理

```java
@Autowired
private SceneServices sceneServices;

public void manageCalendar() {
    CalendarService calendarService = sceneServices.getCalendarService();
    
    // 创建日程
    EventInfo event = new EventInfo("项目会议", "user123", 
        System.currentTimeMillis(), 
        System.currentTimeMillis() + 3600000);
    EventInfo created = calendarService.createEvent(event);
    
    // 查询日程
    List<EventInfo> events = calendarService.listEvents("user123", startTime, endTime);
    
    // 查找空闲时间
    List<TimeSlot> freeTime = calendarService.findFreeTime(
        Arrays.asList("user123", "user456"), startTime, endTime);
}
```

---

## 四、注意事项

### 4.1 条件装配

所有 SPI 实现都使用 `@ConditionalOnProperty` 注解，需要配置对应的开关才能生效：

| 实现类 | 配置项 |
|--------|--------|
| SkillImServiceImpl | `skill.im.enabled=true` |
| SkillOrgSyncServiceImpl | `skill.org.sync.enabled=true` |
| DingTalkOrgSyncServiceImpl | `skill.org.dingtalk.enabled=true` |
| SkillPlatformBindServiceImpl | `skill.platform.bind.enabled=true` |
| SkillTodoSyncServiceImpl | `skill.todo.sync.enabled=true` |
| SkillCalendarServiceImpl | `skill.calendar.enabled=true` |

### 4.2 平台标识

统一使用小写的平台标识：
- `dingtalk` - 钉钉
- `feishu` - 飞书
- `wecom` - 企业微信

### 4.3 异常处理

所有 SPI 方法都进行了异常捕获，返回值中包含错误信息：
- `SendResult.failure(error)` - 消息发送失败
- `SyncResult.failure(message)` - 同步失败
- `null` - 查询失败或无数据

---

## 五、验收检查清单

SE 团队集成后请验证：

- [ ] SPI 接口可正常注入
- [ ] ImService 消息发送正常
- [ ] OrgSyncService 组织同步正常
- [ ] PlatformBindService 绑定流程正常
- [ ] TodoSyncService 待办同步正常
- [ ] CalendarService 日程管理正常
- [ ] 功能开关可正常控制启用/禁用
- [ ] 默认实现可正常降级

---

## 六、联系方式

如有问题请联系 Skills 团队。

---

**请 SE 团队确认集成计划并反馈。**
