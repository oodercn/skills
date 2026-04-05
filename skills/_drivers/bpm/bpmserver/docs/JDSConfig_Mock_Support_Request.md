# JDSConfig 测试 Mock 支持需求文档

## 文档信息

| 项目 | 内容 |
|------|------|
| 提出团队 | bpmserver 开发团队 |
| 提出日期 | 2026-04-05 |
| 文档路径 | `E:\github\ooder-skills\skills\_drivers\bpm\bpmserver\docs\JDSConfig_Mock_Support_Request.md` |
| 目标团队 | ooder-common-client 开发团队 |
| Maven 仓库 | `D:\maven\.m2\repository\net\ooder\ooder-common-client\3.0.1\` |

---

## 1. 问题背景

### 1.1 当前架构

bpmserver 作为独立的 Spring Boot 微服务运行，需要使用 `ooder-common-client` 提供的核心功能：

- `JDSConfig` - 配置管理
- `CommonConfig` - 通用配置
- `LogFactory` - 日志工厂
- `JDSServer` - 服务器核心
- `EsbBeanManager` - ESB Bean 管理

### 1.2 遇到的问题

在 Spring Boot 环境中启动时，`JDSConfig` 的静态初始化失败：

```
java.lang.ExceptionInInitializerError
    at net.ooder.config.JDSConfig$Config.rootServerHome(JDSConfig.java:230)
    at net.ooder.config.JDSConfig$Config.applicationHome(JDSConfig.java:248)
    at net.ooder.config.JDSConfig$Config.currServerHome(JDSConfig.java:267)
    at net.ooder.config.JDSConfig$Config.configPath(JDSConfig.java:276)
    at net.ooder.common.CommonConfig.init(CommonConfig.java:126)
    at net.ooder.common.logging.LogFactory.getFactory(LogFactory.java:138)
```

**根本原因**：`JDSConfig` 在静态代码块中尝试读取配置文件，但 Spring Boot 测试环境没有提供预期的配置文件路径。

---

## 2. 需求说明

### 2.1 核心需求：支持测试 Mock 模式

**需求描述**：提供一种机制，允许在不依赖外部配置文件的情况下初始化配置系统。

**建议方案**：

#### 方案 A：添加静态配置注入方法

```java
public class JDSConfig {
    
    private static Properties props;
    private static boolean mockMode = false;
    
    /**
     * 用于测试环境的 Mock 初始化
     * 调用此方法后，JDSConfig 将使用注入的配置，不再读取配置文件
     */
    public static void initForTest(Properties testProps) {
        props = testProps;
        mockMode = true;
    }
    
    /**
     * 重置为正常模式（用于测试后清理）
     */
    public static void reset() {
        props = null;
        mockMode = false;
    }
    
    /**
     * 检查是否处于 Mock 模式
     */
    public static boolean isMockMode() {
        return mockMode;
    }
}
```

#### 方案 B：添加 Spring Boot Starter 支持

```java
@Configuration
@ConditionalOnClass(JDSConfig.class)
public class JDSConfigAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public JDSConfigProperties jdsConfigProperties() {
        return new JDSConfigProperties();
    }
    
    @PostConstruct
    public void initJDSConfig(JDSConfigProperties properties) {
        if (properties.isEnabled()) {
            Properties props = new Properties();
            props.setProperty("jds.home", properties.getHome());
            props.setProperty("server.home", properties.getServerHome());
            props.setProperty("application.home", properties.getApplicationHome());
            JDSConfig.initForTest(props);
        }
    }
}

@ConfigurationProperties(prefix = "jds.config")
public class JDSConfigProperties {
    private boolean enabled = false;
    private String home;
    private String serverHome;
    private String applicationHome;
    // getters and setters
}
```

### 2.2 配套需求

#### 2.2.1 JDSConfig Mock 支持

```java
public class JDSConfig {
    
    /**
     * 用于测试环境的 Mock 初始化
     */
    public static void initForTest(Properties testProps);
    
    /**
     * 重置配置（清除测试配置）
     */
    public static void reset();
    
    /**
     * 检查是否处于测试模式
     */
    public static boolean isTestMode();
}
```

#### 2.2.2 CommonConfig Mock 支持

```java
public class CommonConfig {
    
    /**
     * 用于测试环境的 Mock 初始化
     */
    public static void initForTest(Properties testProps);
    
    /**
     * 重置配置
     */
    public static void reset();
}
```

#### 2.2.3 LogFactory Mock 支持

```java
public class LogFactory {
    
    /**
     * 用于测试环境的 Mock 初始化
     * 允许注入自定义 LogFactory 实现
     */
    public static void setTestFactory(Object factory);
    
    /**
     * 清除 Mock 工厂
     */
    public static void clearTestFactory();
}
```

#### 2.2.4 JDSServer Mock 支持

```java
public class JDSServer {
    
    /**
     * 设置 Mock 模式
     * @param mockMode true 启用 Mock 模式，跳过真实服务器初始化
     */
    public static void setMockMode(boolean mockMode);
    
    /**
     * 检查是否处于 Mock 模式
     */
    public static boolean isMockMode();
    
    /**
     * 创建测试用的 Mock 实例
     */
    public static JDSServer createMockInstance();
}
```

---

## 3. 使用场景

### 3.1 Spring Boot 测试环境（完整示例）

```java
@SpringBootTest
public class BPMIntegrationTest {
    
    @BeforeAll
    static void setupJDSConfig() {
        Properties props = new Properties();
        props.setProperty("JDSHome", System.getProperty("java.io.tmpdir"));
        props.setProperty("jds.home", System.getProperty("java.io.tmpdir"));
        
        JDSConfig.initForTest(props);
        CommonConfig.initForTest(props);
        JDSServer.setMockMode(true);
    }
    
    @AfterAll
    static void cleanup() {
        JDSConfig.reset();
        CommonConfig.reset();
        LogFactory.clearTestFactory();
        JDSServer.setMockMode(false);
    }
}
```

### 3.2 单元测试 Mock

```java
@Test
void testWorkflowClientService() {
    // 使用 Mock 模式
    Properties props = new Properties();
    props.setProperty("jds.home", ".");
    JDSConfig.initForTest(props);
    CommonConfig.initForTest(props);
    
    // 现在可以安全地创建依赖 JDSConfig 的对象
    WorkflowClientService service = new WorkflowClientServiceImpl();
    
    JDSConfig.reset();
    CommonConfig.reset();
}
```

---

## 4. 接口定义建议

### 4.1 JDSConfig 增强接口

```java
public class JDSConfig {
    
    // 现有方法保持不变...
    
    // ===== 新增 Mock 支持方法 =====
    
    /**
     * 初始化测试配置
     * @param testProps 测试用的配置属性
     */
    public static synchronized void initForTest(Properties testProps);
    
    /**
     * 设置单个配置值（用于测试）
     * @param key 配置键
     * @param value 配置值
     */
    public static void setTestValue(String key, String value);
    
    /**
     * 重置配置（清除测试配置）
     */
    public static synchronized void reset();
    
    /**
     * 检查是否处于测试模式
     * @return true 如果是通过 initForTest 初始化的
     */
    public static boolean isTestMode();
    
    /**
     * 获取当前配置模式
     * @return "test" 或 "production"
     */
    public static String getConfigMode();
}
```

### 4.2 CommonConfig 增强接口

```java
public class CommonConfig {
    
    // 现有方法保持不变...
    
    // ===== 新增 Mock 支持方法 =====
    
    /**
     * 初始化测试配置
     * @param testProps 测试用的配置属性
     */
    public static synchronized void initForTest(Properties testProps);
    
    /**
     * 重置配置
     */
    public static synchronized void reset();
}
```

### 4.3 LogFactory 增强接口

```java
public class LogFactory {
    
    // 现有方法保持不变...
    
    // ===== 新增 Mock 支持方法 =====
    
    /**
     * 设置测试用的 Log 工厂
     * @param factory 测试用工厂实例
     */
    public static void setTestFactory(Object factory);
    
    /**
     * 清除测试工厂，恢复默认行为
     */
    public static void clearTestFactory();
    
    /**
     * 创建测试用的 Log 实例
     * @param configKey 配置键
     * @param clazz 类
     * @return Log 实例
     */
    public static Log createTestLog(String configKey, Class<?> clazz);
}
```

### 4.4 JDSServer 增强接口

```java
public class JDSServer {
    
    // 现有方法保持不变...
    
    // ===== 新增 Mock 支持方法 =====
    
    /**
     * 设置 Mock 模式
     * @param mockMode true 启用 Mock 模式
     */
    public static void setMockMode(boolean mockMode);
    
    /**
     * 检查是否处于 Mock 模式
     * @return true 如果处于 Mock 模式
     */
    public static boolean isMockMode();
    
    /**
     * 创建 Mock 实例（用于测试）
     * @return Mock JDSServer 实例
     */
    public static JDSServer createMockInstance();
}
```

---

## 5. 兼容性要求

### 5.1 向后兼容

- 所有新增方法必须是静态方法
- 不修改现有方法的签名
- 默认行为保持不变（生产环境无需任何改动）

### 5.2 线程安全

- `initForTest()` 和 `reset()` 必须是线程安全的
- 支持并发测试场景

### 5.3 依赖要求

- 不引入新的外部依赖
- Mock 支持代码应放在单独的可选模块或通过条件编译排除

---

## 6. 验收标准

### 6.1 功能验收

- [ ] `JDSConfig.initForTest(Properties)` 可以成功初始化配置
- [ ] `CommonConfig.initForTest(Properties)` 可以成功初始化配置
- [ ] 初始化后，`JDSConfig.getValue()` 返回注入的值
- [ ] `JDSConfig.reset()` 可以清除测试配置
- [ ] `CommonConfig.reset()` 可以清除测试配置
- [ ] `LogFactory.getLog()` 在 Mock 模式下正常工作
- [ ] `JDSServer.setMockMode(true)` 可以跳过服务器初始化
- [ ] 不影响现有生产环境的使用

### 6.2 测试验收

```java
@Test
void testJDSConfigMockSupport() {
    // 1. 初始化测试配置
    Properties props = new Properties();
    props.setProperty("test.key", "test.value");
    props.setProperty("jds.home", "/tmp/test");
    props.setProperty("JDSHome", "/tmp/test");
    
    JDSConfig.initForTest(props);
    CommonConfig.initForTest(props);
    JDSServer.setMockMode(true);
    
    // 2. 验证配置可用
    assertTrue(JDSConfig.isTestMode());
    assertEquals("test.value", JDSConfig.getValue("test.key"));
    assertEquals("/tmp/test", JDSConfig.getValue("jds.home"));
    
    // 3. 验证 LogFactory 工作
    Log log = LogFactory.getLog("test", JDSConfigMockTest.class);
    assertNotNull(log);
    
    // 4. 验证 JDSServer Mock 模式
    assertTrue(JDSServer.isMockMode());
    
    // 5. 重置
    JDSConfig.reset();
    CommonConfig.reset();
    LogFactory.clearTestFactory();
    JDSServer.setMockMode(false);
    
    assertFalse(JDSConfig.isTestMode());
    assertFalse(JDSServer.isMockMode());
}
```

---

## 7. 时间要求

| 里程碑 | 期望完成时间 |
|--------|--------------|
| 需求确认 | 2026-04-08 |
| 设计评审 | 2026-04-10 |
| 开发完成 | 2026-04-15 |
| 测试验收 | 2026-04-17 |

---

## 8. 联系方式

- **bpmserver 团队负责人**: [待填写]
- **技术问题反馈**: 在 ooder-common-client 项目创建 Issue，标签 `mock-support`

---

## 附录 A：当前错误堆栈

```
java.lang.ExceptionInInitializerError
    at net.ooder.bpm.engine.BPMServer.getInstance(BPMServer.java:XXX)
    at net.ooder.bpm.config.BPMServiceConfig.bpmServer(BPMServiceConfig.java:XX)
Caused by: java.lang.NullPointerException
    at net.ooder.config.JDSConfig$Config.rootServerHome(JDSConfig.java:230)
    at net.ooder.config.JDSConfig$Config.applicationHome(JDSConfig.java:248)
    at net.ooder.config.JDSConfig$Config.currServerHome(JDSConfig.java:267)
    at net.ooder.config.JDSConfig$Config.configPath(JDSConfig.java:276)
    at net.ooder.common.CommonConfig.init(CommonConfig.java:126)
    at net.ooder.common.CommonConfig.getValue(CommonConfig.java:67)
    at net.ooder.common.logging.LogFactory.getFactory(LogFactory.java:138)
    at net.ooder.common.logging.LogFactory.getLog(LogFactory.java:180)
    at net.ooder.server.JDSServer.<clinit>(JDSServer.java:101)
    ... 57 more
```

## 附录 B：相关代码位置

| 组件 | 文件路径 |
|------|----------|
| JDSConfig | `net.ooder.config.JDSConfig` |
| CommonConfig | `net.ooder.common.CommonConfig` |
| LogFactory | `net.ooder.common.logging.LogFactory` |
| JDSServer | `net.ooder.server.JDSServer` |
| bpmserver 使用 | `E:\github\ooder-skills\skills\_drivers\bpm\bpmserver\src\main\java\net\ooder\bpm\config\BPMServiceConfig.java` |

## 附录 C：Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>3.0.1</version>
</dependency>
```

**本地仓库路径**:
```
D:\maven\.m2\repository\net\ooder\ooder-common-client\3.0.1\
├── ooder-common-client-3.0.1.jar
├── ooder-common-client-3.0.1-sources.jar
├── ooder-common-client-3.0.1-javadoc.jar
└── ooder-common-client-3.0.1.pom
```
