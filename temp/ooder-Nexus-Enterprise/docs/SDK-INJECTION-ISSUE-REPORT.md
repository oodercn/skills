# SDK组件注入问题报告

**报告日期**: 2026-02-28  
**报告项目**: ooder-Nexus-Enterprise  
**SDK版本**: v2.3  
**报告人**: Nexus Enterprise Team

---

## 一、问题描述

Nexus Enterprise 项目启动时，以下SDK组件无法通过Spring `@Autowired` 注入：

| 组件 | 包路径 | 状态 | 问题原因 |
|------|--------|------|----------|
| **SceneEngine** | `net.ooder.scene.core.SceneEngine` | ❌ 不可用 | 使用`@Component`注解，但可能缺少包扫描配置 |
| **DriverRegistry** | `net.ooder.scene.core.driver.DriverRegistry` | ❌ 不可用 | **未使用Spring注解**，是普通Java类 |
| **CapabilityRegistry** | `net.ooder.sdk.a2a.capability.CapabilityRegistry` | ❌ 不可用 | **未使用Spring注解**，使用单例模式 |

**启动日志**:
```
2026-02-28 11:16:29.160 [main] WARN  n.o.n.service.SdkIntegrationService - [SdkIntegration] SceneManager 未注入，SDK 集成功能将不可用
2026-02-28 11:16:29.161 [main] INFO  n.o.n.service.SdkIntegrationService - [SdkIntegration] 可用服务:
2026-02-28 11:16:29.161 [main] INFO  n.o.n.service.SdkIntegrationService -   - SceneEngine: 不可用
2026-02-28 11:16:29.162 [main] INFO  n.o.n.service.SdkIntegrationService -   - SceneManager: 可用
2026-02-28 11:16:29.162 [main] INFO  n.o.n.service.SdkIntegrationService -   - DriverRegistry: 不可用
2026-02-28 11:16:29.164 [main] INFO  n.o.n.service.SdkIntegrationService -   - CapabilityRegistry: 不可用
```

---

## 二、SDK代码分析

### 2.1 SceneEngine

**文件位置**: `scene-engine/src/main/java/net/ooder/scene/core/impl/SceneEngineImpl.java`

```java
@Component
public class SceneEngineImpl implements SceneEngine {
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private SkillService skillService;
    
    // ...
}
```

**分析**: 使用了`@Component`注解，理论上应该可以注入。可能原因：
1. Nexus项目未扫描到SDK的包路径
2. SceneEngineImpl的依赖项（SessionManager、SkillService等）未注入成功

### 2.2 DriverRegistry

**文件位置**: `scene-engine/src/main/java/net/ooder/scene/core/driver/DriverRegistry.java`

```java
package net.ooder.scene.core.driver;

public class DriverRegistry {
    
    private final Map<String, Driver> drivers = new ConcurrentHashMap<String, Driver>();
    private final Map<String, InterfaceDefinition> interfaceDefinitions = new ConcurrentHashMap<String, InterfaceDefinition>();
    
    public void register(Driver driver) { ... }
    
    public Driver getDriver(String category) { ... }
    
    public <T extends Driver> T getDriver(String category, Class<T> type) { ... }
}
```

**分析**: 
- **无任何Spring注解**
- 是普通Java类，不由Spring容器管理
- 无法通过`@Autowired`注入

### 2.3 CapabilityRegistry

**文件位置**: `agent-sdk/agent-sdk-core/src/main/java/net/ooder/sdk/a2a/capability/CapabilityRegistry.java`

```java
package net.ooder.sdk.a2a.capability;

public class CapabilityRegistry {

    private static final Logger log = LoggerFactory.getLogger(CapabilityRegistry.class);

    private final Map<String, List<CapabilityDeclaration>> skillCapabilities;
    private final Map<String, Map<String, CapabilityDeclaration>> capabilityIndex;

    public CapabilityRegistry() {
        this.skillCapabilities = new ConcurrentHashMap<>();
        this.capabilityIndex = new ConcurrentHashMap<>();
    }
    
    public void registerCapability(String skillId, CapabilityDeclaration capability) { ... }
    
    public void unregisterCapability(String skillId, String capabilityId) { ... }
    
    // ...
}
```

**分析**: 
- **无任何Spring注解**
- 无单例模式实现
- 无法通过`@Autowired`注入

**注意**: 在`ooder-config`模块中存在另一个CapabilityRegistry，使用单例模式：

```java
// ooder-config/src/main/java/net/ooder/config/scene/registry/CapabilityRegistry.java
public class CapabilityRegistry {
    private static final CapabilityRegistry INSTANCE = new CapabilityRegistry();
    
    public static CapabilityRegistry getInstance() {
        return INSTANCE;
    }
}
```

---

## 三、问题影响

| 功能模块 | 影响程度 | 说明 |
|----------|----------|------|
| 数据源连接测试 | ⚠️ 中等 | 无法使用SDK DatabaseDriver，回退到Mock实现 |
| 能力注册管理 | ⚠️ 中等 | 无法使用SDK CapabilityRegistry，回退到本地存储 |
| 场景引擎功能 | 🔴 高 | 无法使用SDK SceneEngine，核心功能受限 |
| 场景生命周期管理 | ✅ 正常 | SceneManager可正常注入 |

---

## 四、建议解决方案

### 方案1：添加Spring Bean定义（推荐）

在SDK中添加配置类，将组件注册为Spring Bean：

```java
package net.ooder.sdk.config;

import net.ooder.scene.core.driver.DriverRegistry;
import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SdkAutoConfiguration {
    
    @Bean
    public DriverRegistry driverRegistry() {
        return new DriverRegistry();
    }
    
    @Bean
    public CapabilityRegistry capabilityRegistry() {
        return new CapabilityRegistry();
    }
}
```

### 方案2：添加Spring Boot自动配置（推荐）

创建`META-INF/spring.factories`文件：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
net.ooder.sdk.config.SdkAutoConfiguration
```

### 方案3：提供静态获取方法

如果不使用Spring管理，请提供静态方法获取实例：

```java
// DriverRegistry 添加单例模式
public class DriverRegistry {
    private static final DriverRegistry INSTANCE = new DriverRegistry();
    
    public static DriverRegistry getInstance() {
        return INSTANCE;
    }
}

// CapabilityRegistry 已有 getInstance() 方法
```

### 方案4：Nexus端手动创建实例（临时方案）

```java
@Service
public class SdkIntegrationService {
    
    private DriverRegistry driverRegistry;
    private CapabilityRegistry capabilityRegistry;
    
    @PostConstruct
    public void init() {
        // 手动创建实例
        this.driverRegistry = new DriverRegistry();
        this.capabilityRegistry = new CapabilityRegistry();
    }
}
```

---

## 五、需要SDK团队确认

1. **SceneEngine** 为何无法注入？
   - 是否需要特定的包扫描配置？
   - 依赖项（SessionManager、SkillService等）是否都已正确配置？

2. **DriverRegistry** 和 **CapabilityRegistry** 是否计划支持Spring管理？
   - 如果支持，预计哪个版本发布？
   - 如果不支持，请提供推荐的获取方式

3. SDK v2.3 是否有Spring Boot Starter计划？
   - 是否会提供`ooder-sdk-spring-boot-starter`？

4. 组件初始化顺序
   - 各组件之间是否存在依赖关系？
   - 是否有推荐的初始化顺序？

---

## 六、附录

### 6.1 Nexus项目依赖配置

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>scene-engine</artifactId>
    <version>0.7.3</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>0.7.3</version>
</dependency>
```

### 6.2 Nexus项目包扫描配置

```java
@SpringBootApplication(scanBasePackages = {
    "net.ooder.nexus",
    "net.ooder.scene",      // SDK scene-engine
    "net.ooder.sdk"         // SDK agent-sdk
})
public class NexusSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusSpringApplication.class, args);
    }
}
```

### 6.3 相关文件路径

| 组件 | SDK文件路径 |
|------|-------------|
| SceneEngine | `E:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\impl\SceneEngineImpl.java` |
| DriverRegistry | `E:\github\ooder-sdk\scene-engine\src\main\java\net\ooder\scene\core\driver\DriverRegistry.java` |
| CapabilityRegistry | `E:\github\ooder-sdk\agent-sdk\agent-sdk-core\src\main\java\net\ooder\sdk\a2a\capability\CapabilityRegistry.java` |

---

## 七、联系方式

如有问题或需要进一步沟通，请联系：

- **Nexus Enterprise Team**
- **项目地址**: https://gitee.com/ooderCN
