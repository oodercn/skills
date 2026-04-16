# Ooder Skills SPI 桥接关系分析报告

> 生成时间: 2026-04-15
> 项目路径: e:\github\ooder-skills

---

## 一、SPI 架构概述

Ooder Skills 采用 **SPI (Service Provider Interface)** 架构实现模块间的解耦和可扩展性。核心思想是：
- **接口定义** 在基础模块中
- **实现类** 在各驱动模块中
- **自动装配** 通过 Spring Boot 条件装配实现

---

## 二、SPI 接口定义位置

### 2.1 核心 SPI 模块

| 模块 | 路径 | 版本 | 说明 |
|------|------|------|------|
| skill-spi-core | `skills/_base/skill-spi-core` | 3.0.3 | 核心 SPI 接口定义 |
| skill-spi-messaging | `skills/_base/skill-spi-messaging` | 3.0.2 | 消息服务 SPI 接口 |
| skill-common | `skills/_system/skill-common` | 3.0.5 | 通用 SPI 接口定义 |

### 2.2 接口定义文件清单

#### skill-spi-core (net.ooder.spi.*)

| 接口名 | 文件路径 | 功能说明 |
|--------|----------|----------|
| VectorStoreProvider | `spi/vector/VectorStoreProvider.java` | 向量存储提供者 |
| DataSourceProvider | `spi/database/DataSourceProvider.java` | 数据源提供者 |
| DocumentParser | `spi/document/DocumentParser.java` | 文档解析器 |
| RagEnhanceDriver | `spi/rag/RagEnhanceDriver.java` | RAG 增强驱动 |
| WorkflowDriver | `spi/workflow/WorkflowDriver.java` | 工作流驱动 |
| SpiServices | `spi/facade/SpiServices.java` | SPI 门面服务 |

#### skill-spi-messaging (net.ooder.spi.messaging.*)

| 接口名 | 文件路径 | 功能说明 |
|--------|----------|----------|
| UnifiedMessagingService | `messaging/UnifiedMessagingService.java` | 统一消息服务 |
| UnifiedSessionService | `messaging/UnifiedSessionService.java` | 统一会话服务 |
| UnifiedWebSocketService | `messaging/UnifiedWebSocketService.java` | WebSocket 服务 |
| MessageStreamHandler | `messaging/MessageStreamHandler.java` | 消息流处理器 |

#### skill-common (net.ooder.skill.common.spi.*)

| 接口名 | 文件路径 | 功能说明 |
|--------|----------|----------|
| ImService | `spi/ImService.java` | IM 消息服务 |
| OrgSyncService | `spi/OrgSyncService.java` | 组织架构同步服务 |
| CalendarService | `spi/CalendarService.java` | 日历服务 |
| TodoSyncService | `spi/TodoSyncService.java` | 待办同步服务 |
| PlatformBindService | `spi/PlatformBindService.java` | 平台绑定服务 |
| UserService | `spi/UserService.java` | 用户服务 |
| MessageService | `spi/MessageService.java` | 消息服务 |
| StorageService | `spi/StorageService.java` | 存储服务 |
| PermissionService | `spi/PermissionService.java` | 权限服务 |
| ConfigService | `spi/ConfigService.java` | 配置服务 |
| AuditService | `spi/AuditService.java` | 审计服务 |
| SceneServices | `spi/SceneServices.java` | 场景服务门面 |

---

## 三、SPI 实现类清单

### 3.1 ImService 实现

| 实现类 | 模块路径 | 平台 | 条件装配 |
|--------|----------|------|----------|
| DingTalkImServiceImpl | `skills/_drivers/im/skill-im-dingding` | dingtalk | @Service |
| FeishuImServiceImpl | `skills/_drivers/im/skill-im-feishu` | feishu | @Service |
| WeComImServiceImpl | `skills/_drivers/im/skill-im-wecom` | wecom | @Service |
| MqttChannelAdapter | `skills/_system/skill-im-gateway` | mqtt | @ConditionalOnProperty(name="mqtt.enabled") |
| SkillImServiceImpl | `skills/tools/skill-msg-push` | skill | @Service |
| ImServiceImpl | `skills/capabilities/communication/skill-im` | default | @Service |

### 3.2 OrgSyncService 实现

| 实现类 | 模块路径 | 平台 | 条件装配 |
|--------|----------|------|----------|
| DingTalkOrgSyncServiceImpl | `skills/_drivers/org/skill-org-dingding` | dingtalk | @ConditionalOnProperty(name="skill.org.dingtalk.enabled") |
| FeishuOrgSyncServiceImpl | `skills/_drivers/org/skill-org-feishu` | feishu | @Service |
| WeComOrgSyncServiceImpl | `skills/_drivers/org/skill-org-wecom` | wecom | @Service |
| SkillOrgSyncServiceImpl | `skills/_drivers/org/skill-org-base` | skill | @Service |

### 3.3 VectorStoreProvider 实现

| 实现类 | 模块路径 | 类型 | 条件装配 |
|--------|----------|------|----------|
| LocalVectorStoreProvider | `skills/_drivers/vector/skill-local-vector-store` | local | @ConditionalOnProperty |

### 3.4 DataSourceProvider 实现

| 实现类 | 模块路径 | 数据库类型 | 条件装配 |
|--------|----------|------------|----------|
| SQLiteDataSourceProvider | `skills/_drivers/database/skill-sqlite-driver` | sqlite | @Service |

### 3.5 DocumentParser 实现

| 实现类 | 模块路径 | 支持格式 | 条件装配 |
|--------|----------|----------|----------|
| MarkdownDocumentParser | `skills/_drivers/document/skill-markdown-parser` | markdown | @Service |

### 3.6 UnifiedMessagingService 实现

| 实现类 | 模块路径 | 说明 | 条件装配 |
|--------|----------|------|----------|
| UnifiedMessagingServiceImpl | `skills/_system/skill-messaging` | 统一消息服务 | @Service |

### 3.7 其他服务实现

| 接口 | 实现类 | 模块路径 |
|------|--------|----------|
| CalendarService | SkillCalendarServiceImpl | `skills/tools/skill-calendar` |
| TodoSyncService | SkillTodoSyncServiceImpl | `skills/tools/skill-todo-sync` |
| PlatformBindService | SkillPlatformBindServiceImpl | `skills/scenes/skill-platform-bind` |

---

## 四、SPI 桥接关系图

### 4.1 IM 服务桥接关系

```
┌─────────────────────────────────────────────────────────────────┐
│                        ImService 接口                            │
│              (skill-common/spi/ImService.java)                  │
└─────────────────────────────────────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
        ▼                       ▼                       ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│ DingTalkIm    │     │ FeishuIm      │     │ WeComIm       │
│ ServiceImpl   │     │ ServiceImpl   │     │ ServiceImpl   │
│ (dingtalk)    │     │ (feishu)      │     │ (wecom)       │
└───────────────┘     └───────────────┘     └───────────────┘
        │                       │                       │
        ▼                       ▼                       ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│ DingTalk      │     │ Feishu        │     │ WeCom         │
│ MessageService│     │ MessageService│     │ MessageService│
└───────────────┘     └───────────────┘     └───────────────┘

        ┌───────────────────────┐
        │                       │
        ▼                       ▼
┌───────────────┐     ┌───────────────┐
│ MqttChannel   │     │ SkillIm       │
│ Adapter       │     │ ServiceImpl   │
│ (mqtt)        │     │ (skill)       │
└───────────────┘     └───────────────┘
```

### 4.2 组织架构同步桥接关系

```
┌─────────────────────────────────────────────────────────────────┐
│                      OrgSyncService 接口                         │
│            (skill-common/spi/OrgSyncService.java)               │
└─────────────────────────────────────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
        ▼                       ▼                       ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│ DingTalkOrg   │     │ FeishuOrg     │     │ WeComOrg      │
│ SyncServiceImpl│    │ SyncServiceImpl│    │ SyncServiceImpl│
└───────────────┘     └───────────────┘     └───────────────┘
        │                       │                       │
        ▼                       ▼                       ▼
┌───────────────┐     ┌───────────────┐     ┌───────────────┐
│ DingTalk      │     │ Feishu        │     │ WeCom         │
│ OrgSyncService│     │ OrgSyncService│     │ OrgSyncService│
└───────────────┘     └───────────────┘     └───────────────┘
```

### 4.3 向量存储桥接关系

```
┌─────────────────────────────────────────────────────────────────┐
│                   VectorStoreProvider 接口                       │
│          (skill-spi-core/spi/vector/VectorStoreProvider.java)   │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
                    ┌───────────────────┐
                    │ LocalVectorStore  │
                    │ Provider          │
                    │ (local)           │
                    └───────────────────┘
                                │
                                ▼
                    ┌───────────────────┐
                    │ SQLite + HikariCP │
                    └───────────────────┘
```

### 4.4 数据源桥接关系

```
┌─────────────────────────────────────────────────────────────────┐
│                    DataSourceProvider 接口                       │
│          (skill-spi-core/spi/database/DataSourceProvider.java)  │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
                    ┌───────────────────┐
                    │ SQLiteDataSource  │
                    │ Provider          │
                    │ (sqlite)          │
                    └───────────────────┘
                                │
                                ▼
                    ┌───────────────────┐
                    │ HikariDataSource  │
                    └───────────────────┘
```

---

## 五、Maven 依赖关系

### 5.1 核心 SPI 模块依赖

```
skill-spi-core (3.0.3)
    ├── lombok (1.18.30)
    └── jackson-annotations (2.17.0)

skill-spi-messaging (3.0.2)
    ├── skill-spi-core (3.0.3)
    └── scene-engine (3.0.2) [provided]

skill-common (3.0.5)
    ├── scene-engine (3.0.3)
    ├── agent-sdk-core (3.0.3)
    ├── spring-boot-starter (3.2.5)
    ├── spring-boot-autoconfigure (3.2.5)
    ├── spring-boot-starter-freemarker (3.2.5)
    ├── jackson-databind (2.17.0)
    └── snakeyaml (2.2)
```

### 5.2 驱动模块依赖

```
skill-im-dingding (3.0.2)
    ├── spring-boot-starter-web
    ├── spring-boot-starter-validation
    └── jackson-databind
    [注意: 缺少 skill-common 或 skill-spi-core 依赖]

skill-local-vector-store (3.0.3)
    ├── skill-spi-core (3.0.3)
    ├── sqlite-jdbc (3.49.1.0)
    ├── HikariCP (5.1.0)
    └── spring-boot-autoconfigure (3.2.5) [provided]
```

---

## 六、SpiServices 门面服务

`SpiServices` 是一个静态门面类，提供全局访问 SPI 服务的能力：

```java
// 文件: skills/_base/skill-spi-core/src/main/java/net/ooder/spi/facade/SpiServices.java

public class SpiServices {
    // 静态访问方法
    public static ImService getImService();
    public static RagEnhanceDriver getRagEnhanceDriver();
    public static WorkflowDriver getWorkflowDriver();
    public static DataSourceProvider getDataSourceProvider();
    public static VectorStoreProvider getVectorStoreProvider();
    public static List<DocumentParser> getDocumentParsers();
    public static DocumentParser getDocumentParser(String mimeType);
    
    // Optional 包装方法
    public static Optional<ImService> im();
    public static Optional<RagEnhanceDriver> rag();
    public static Optional<WorkflowDriver> workflow();
    public static Optional<DataSourceProvider> dataSource();
    public static Optional<VectorStoreProvider> vectorStore();
    public static Optional<DocumentParser> documentParser(String mimeType);
}
```

---

## 七、缺失的 SPI 实现

### 7.1 RagEnhanceDriver - 缺失实现

**接口定义**: `skill-spi-core/spi/rag/RagEnhanceDriver.java`

```java
public interface RagEnhanceDriver {
    boolean isAvailable();
    String enhancePrompt(String query, String sceneGroupId, List<String> knowledgeBaseIds);
    RagKnowledgeConfig buildKnowledgeConfig(String sceneGroupId, String query);
    List<RagRelatedDocument> searchRelated(String query, int limit);
}
```

**状态**: 未找到实现类

### 7.2 WorkflowDriver - 缺失实现

**接口定义**: `skill-spi-core/spi/workflow/WorkflowDriver.java`

```java
public interface WorkflowDriver {
    boolean isAvailable();
    <T> WorkflowResult<T> routeTo(String activityInstId, String toUserId, Map<String, Object> vars);
    <T> WorkflowResult<T> endTask(String activityInstId);
    <T> WorkflowResult<T> getActivityInfo(String activityInstId);
    <T> WorkflowResult<T> startProcess(String processDefKey, Map<String, Object> vars);
}
```

**状态**: 未找到实现类（BPM 模块可能有相关实现，但未直接实现此接口）

---

## 八、自动装配配置

### 8.1 条件装配注解使用

| 注解 | 使用场景 | 示例 |
|------|----------|------|
| @Service | 默认装配 | DingTalkImServiceImpl |
| @ConditionalOnProperty | 配置开关 | MqttChannelAdapter (mqtt.enabled=true) |
| @ConditionalOnClass | 类存在时装配 | AutoConfiguration |
| @ConditionalOnMissingBean | Bean 不存在时装配 | 默认实现 |

### 8.2 配置属性示例

```yaml
# MQTT 通道配置
mqtt:
  enabled: true
  broker-url: tcp://localhost:1883
  client-id: agent-chat-gateway
  topic-prefix: agent/chat/

# 组织架构同步配置
skill:
  org:
    dingtalk:
      enabled: true
    feishu:
      enabled: true
    wecom:
      enabled: true
```

---

## 九、版本一致性检查

### 9.1 发现的版本问题

| 模块 | 当前版本 | 依赖版本 | 状态 |
|------|----------|----------|------|
| skill-spi-core | 3.0.3 | - | 正常 |
| skill-spi-messaging | 3.0.2 | skill-spi-core:3.0.3 | 正常 |
| skill-common | 3.0.5 | scene-engine:3.0.3, agent-sdk:3.0.3 | 正常 |
| skill-im-dingding | 3.0.2 | - | 缺少 SPI 依赖 |
| skill-local-vector-store | 3.0.3 | skill-spi-core:3.0.3 | 正常 |

### 9.2 建议修复

1. **skill-im-dingding** 需要添加 `skill-common` 依赖以正确实现 `ImService` 接口
2. 所有 IM 驱动模块应统一依赖 `skill-common` 或 `skill-spi-core`

---

## 十、文件路径索引

### 10.1 SPI 接口定义

```
e:\github\ooder-skills\skills\_base\skill-spi-core\src\main\java\net\ooder\spi\
├── vector\VectorStoreProvider.java
├── database\DataSourceProvider.java
├── document\DocumentParser.java
├── rag\RagEnhanceDriver.java
├── workflow\WorkflowDriver.java
└── facade\SpiServices.java

e:\github\ooder-skills\skills\_base\skill-spi-messaging\src\main\java\net\ooder\spi\messaging\
├── UnifiedMessagingService.java
├── UnifiedSessionService.java
├── UnifiedWebSocketService.java
└── MessageStreamHandler.java

e:\github\ooder-skills\skills\_system\skill-common\src\main\java\net\ooder\skill\common\spi\
├── ImService.java
├── OrgSyncService.java
├── CalendarService.java
├── TodoSyncService.java
├── PlatformBindService.java
├── UserService.java
├── MessageService.java
├── StorageService.java
├── PermissionService.java
├── ConfigService.java
├── AuditService.java
└── SceneServices.java
```

### 10.2 SPI 实现类

```
e:\github\ooder-skills\skills\_drivers\im\
├── skill-im-dingding\src\main\java\net\ooder\skill\im\dingding\spi\DingTalkImServiceImpl.java
├── skill-im-feishu\src\main\java\net\ooder\skill\im\feishu\spi\FeishuImServiceImpl.java
└── skill-im-wecom\src\main\java\net\ooder\skill\im\wecom\spi\WeComImServiceImpl.java

e:\github\ooder-skills\skills\_drivers\org\
├── skill-org-dingding\src\main\java\net\ooder\skill\org\dingding\spi\DingTalkOrgSyncServiceImpl.java
├── skill-org-feishu\src\main\java\net\ooder\skill\org\feishu\spi\FeishuOrgSyncServiceImpl.java
└── skill-org-wecom\src\main\java\net\ooder\skill\org\wecom\spi\WeComOrgSyncServiceImpl.java

e:\github\ooder-skills\skills\_drivers\vector\
└── skill-local-vector-store\src\main\java\net\ooder\skill\vector\local\LocalVectorStoreProvider.java

e:\github\ooder-skills\skills\_drivers\database\
└── skill-sqlite-driver\src\main\java\net\ooder\skill\database\sqlite\SQLiteDataSourceProvider.java

e:\github\ooder-skills\skills\_drivers\document\
└── skill-markdown-parser\src\main\java\net\ooder\skill\document\markdown\MarkdownDocumentParser.java

e:\github\ooder-skills\skills\_system\
├── skill-im-gateway\src\main\java\net\ooder\skill\im\gateway\MqttChannelAdapter.java
└── skill-messaging\src\main\java\net\ooder\skill\messaging\service\impl\UnifiedMessagingServiceImpl.java
```

---

## 十一、总结

### 11.1 SPI 架构优势

1. **解耦**: 接口与实现分离，便于独立开发和测试
2. **可扩展**: 新增平台支持只需实现对应接口
3. **可配置**: 通过条件装配实现按需加载
4. **统一门面**: SpiServices 提供全局访问入口

### 11.2 待完善项

1. **RagEnhanceDriver** 需要实现类
2. **WorkflowDriver** 需要实现类
3. IM 驱动模块需补充 SPI 依赖声明
4. 版本管理需统一

### 11.3 Maven 本地仓库位置

```
D:\maven\.m2\repository\net\ooder\
├── skill-spi-core\3.0.3\
├── skill-spi-messaging\3.0.2\
├── skill-common\3.0.5\
└── skill-local-vector-store\3.0.3\
```
