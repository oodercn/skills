# Ooder SDK 实现分析与Skills迁移指南

> **报告日期**: 2026-02-25  
> **分析范围**: ooder-sdk (agent-sdk, llm-sdk, scene-engine)  
> **目的**: 确定正确的Skills迁移路径

---

## 一、SDK实现分析

### 1.1 agent-sdk 核心结构

```
agent-sdk/
├── a2a/                    # A2A协议实现 �?�?  ├── capability/         # 能力发现
�?  ├── error/              # 错误�?�?  └── message/            # 消息类型 (13�?
�?├── api/                    # 核心API接口 �?�?  ├── agent/              # Agent接口
�?  ├── cap/                # 能力接口
�?  ├── event/              # 事件接口
�?  ├── lifecycle/          # 生命周期接口
�?  ├── llm/                # LLM接口
�?  ├── network/            # 网络接口
�?  ├── scene/              # 场景接口
�?  ├── scheduler/          # 调度接口
�?  ├── security/           # 安全接口
�?  ├── skill/              # Skill接口
�?  └── storage/            # 存储接口
�?├── core/                   # 核心实现 �?�?  ├── agent/              # Agent实现
�?  ├── event/              # 事件实现
�?  ├── scene/              # 场景实现
�?  ├── security/           # 安全实现
�?  ├── skill/              # Skill实现
�?  �?  ├── discovery/      # Skill发现
�?  �?  ├── installer/      # Skill安装
�?  �?  └── lifecycle/      # Skill生命周期
�?  └── transport/          # 传输�?�?├── classloader/            # 类加载器 �?├── dependency/             # 依赖解析 �?├── discovery/              # 发现服务 �?├── driver/                 # 驱动框架 �?└── drivers/                # 内置驱动 �?```

### 1.2 关键接口定义

#### SkillService 接口

```java
public interface SkillService {
    String getSkillId();
    String getSkillType();
    String getSceneId();
    String getGroupId();
    
    void initialize(SkillContext context);
    void start();
    void stop();
    
    Map<String, Object> getSkillInfo();
    Map<String, Object> getCapabilities();
    
    Object execute(SkillRequest request);
    CompletableFuture<Object> executeAsync(SkillRequest request);
    
    boolean isRunning();
    String getStatus();
}
```

#### SkillState 枚举

```java
public enum SkillState {
    CREATED, INITIALIZING, INITIALIZED,
    STARTING, RUNNING, PAUSED,
    STOPPING, STOPPED, DESTROYING, DESTROYED,
    ERROR, FAILED
}
```

#### A2AMessage 基类

```java
public class A2AMessage {
    private String id;
    private A2AMessageType type;
    private long timestamp;
    private String skillId;
    private String sessionId;
    private Map<String, Object> data;
    private Map<String, Object> metadata;
}
```

---

## 二、Result类分�?
### 2.1 发现的Result�?
| 位置 | 类型 | 说明 |
|------|------|------|
| `net.ooder.common.Result` | 接口 | 简单接�?|
| `net.ooder.scene.core.Result` | �?| **旧版scene-engine** |

### 2.2 正确的导入路�?
```java
// 旧版 (scene-engine) - 应该废弃
import net.ooder.scene.core.Result;

// 新版 (agent-sdk没有Result�? - 需要使�?import net.ooder.common.Result;  // 或自己实�?```

### 2.3 建议

**agent-sdk没有提供Result工具�?*，Skills需要：
1. 使用 `net.ooder.common.Result` 接口
2. 或自己实现Result�?
---

## 三、Skills迁移正确路径

### 3.1 Skill实现模式

基于SDK�?`SkillService` 接口，正确的Skill实现模式�?
```java
@Service
public class MySkillService implements SkillService {
    
    private SkillContext context;
    private SkillState state = SkillState.CREATED;
    
    @Override
    public String getSkillId() {
        return "com.ooder.skills.my-skill";
    }
    
    @Override
    public void initialize(SkillContext context) {
        this.context = context;
        this.state = SkillState.INITIALIZING;
        // 初始化逻辑
        this.state = SkillState.INITIALIZED;
    }
    
    @Override
    public void start() {
        this.state = SkillState.STARTING;
        // 启动逻辑
        this.state = SkillState.RUNNING;
    }
    
    @Override
    public void stop() {
        this.state = SkillState.STOPPING;
        // 停止逻辑
        this.state = SkillState.STOPPED;
    }
    
    @Override
    public Object execute(SkillRequest request) {
        // 执行逻辑
        return result;
    }
}
```

### 3.2 驱动层Skill实现

```java
// 驱动�?- 不暴露REST API，仅内部调用
@Service
public class NetworkDriver implements SkillService {
    
    // 不需�?@RestController
    
    @Override
    public Object execute(SkillRequest request) {
        String operation = request.getOperation();
        switch (operation) {
            case "getNetworkInfo":
                return getNetworkInfo();
            case "ping":
                return ping(request.getParameter("host"));
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }
    }
    
    // 内部方法，高性能
    private NetworkInfo getNetworkInfo() {
        // 直接操作网络接口
    }
}
```

### 3.3 应用层Skill实现

```java
// 应用�?- 可选REST API
@RestController
@RequestMapping("/api/v1/security")
public class SecuritySkillController {
    
    @Autowired
    private SecuritySkillService securityService;
    
    @GetMapping("/status")
    public Object getStatus() {
        SkillRequest request = new SkillRequest();
        request.setOperation("getStatus");
        return securityService.execute(request);
    }
}

@Service
public class SecuritySkillService implements SkillService {
    // 实现 SkillService 接口
}
```

### 3.4 UI层Skill实现

```java
// UI�?- 必须支持A2A + Web
@Controller
public class WebSkillController {
    
    @Autowired
    private WebSkillService webService;
    
    // A2A接口
    @PostMapping("/a2a/task")
    public A2AMessage handleA2ATask(@RequestBody A2AMessage message) {
        // A2A协议处理
        return response;
    }
    
    // Web页面
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
```

---

## 四、迁移修正建�?
### 4.1 当前迁移的问�?
| 问题 | 说明 | 修正 |
|------|------|------|
| **Result类路径错�?* | 使用了不存在�?`net.ooder.sdk.infra.utils.Result` | 改用 `net.ooder.common.Result` 或自实现 |
| **未实现SkillService接口** | Skills没有实现SDK标准接口 | 实现 `SkillService` 接口 |
| **驱动层暴露REST** | 驱动层Skills有Controller | 移除Controller |

### 4.2 正确的pom.xml依赖

```xml
<dependencies>
    <!-- agent-sdk核心 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <version>2.3</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- ooder-annotation (包含Result接口) -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-annotation</artifactId>
        <version>2.3</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- 应用层和UI层才需要web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <optional>true</optional> <!-- 驱动层不需�?-->
    </dependency>
</dependencies>
```

---

## 五、最终架�?
### 5.1 三层架构

```
┌─────────────────────────────────────────────────────────────�?�?                    UI�?(全栈)                             �?�? - 必须支持A2A + Web                                        �?�? - 实现 SkillService 接口                                   �?�? - 可选REST Controller                                      �?└─────────────────────────┬───────────────────────────────────�?                          �?                          �?┌─────────────────────────────────────────────────────────────�?�?                    应用�?(业务)                           �?�? - 可选REST API                                             �?�? - 实现 SkillService 接口                                   �?└─────────────────────────┬───────────────────────────────────�?                          �?                          �?┌─────────────────────────────────────────────────────────────�?�?                    驱动�?(底层)                           �?�? - 不暴露REST API                                           �?�? - 实现 SkillService 接口                                   �?�? - 高性能内部调用                                           �?└─────────────────────────────────────────────────────────────�?```

### 5.2 Skills分类

| 层级 | Skills | 对外API | 实现要求 |
|------|--------|---------|----------|
| **驱动�?* | network, openwrt, remote-terminal, mqtt, msg | �?�?| 实现SkillService |
| **应用�?* | security, share, audit, access-control, search, report, cmd-service, health, monitor, protocol, agent, hosting | �?可选REST | 实现SkillService |
| **UI�?* | web, admin, portal (新建) | �?A2A + Web | 实现SkillService |

---

## 六、下一步行�?
### 6.1 立即修正

1. **修正Result类导�?*
   - 所有Skills改用 `net.ooder.common.Result`
   - 或在agent-sdk中添加Result工具�?
2. **实现SkillService接口**
   - 所有Skills实现 `SkillService` 接口

3. **移除驱动层Controller**
   - skill-network
   - skill-openwrt
   - skill-remote-terminal

### 6.2 后续完善

1. 完善A2A协议支持
2. 新建UI层Skills
3. 编译验证

---

**报告结束**

最后更�? 2026-02-25
