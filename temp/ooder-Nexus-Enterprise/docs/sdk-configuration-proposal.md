# SDK 配置方案评估与建议

## 评估日期
2026-02-25

---

## 一、方案对比

### 方案 A: Spring Boot 自动配置（推荐）

#### 实现方式
```java
// 自动配置类
@Configuration
@ConditionalOnClass(SkillLifecycleEventService.class)
@EnableConfigurationProperties(SdkLifecycleProperties.class)
public class SdkLifecycleAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public EventBus eventBus() {
        return new EventBusImpl();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SkillLifecycleEventService skillLifecycleEventService(EventBus eventBus) {
        return new SkillLifecycleEventServiceImpl(eventBus);
    }
}

// 配置属性
@ConfigurationProperties(prefix = "ooder.sdk.lifecycle")
public class SdkLifecycleProperties {
    private boolean enabled = true;
    private int eventQueueSize = 1000;
    private boolean asyncDispatch = true;
}
```

#### 优点
| 优点 | 说明 |
|------|------|
| **零配置启动** | 引入依赖即可使用，无需手动创建 Bean |
| **符合 Spring 生态** | 与 Spring Boot 无缝集成，开发者熟悉 |
| **可扩展性强** | 通过 `application.yml` 配置，支持多环境 |
| **条件化装配** | 使用 `@ConditionalOnMissingBean` 允许自定义覆盖 |
| **自动发现** | 通过 `spring.factories` 自动加载配置 |

#### 缺点
| 缺点 | 说明 |
|------|------|
| **依赖 Spring** | 对非 Spring 项目不友好 |
| **版本兼容性** | 需要维护不同 Spring Boot 版本的兼容性 |
| **启动时间** | 自动扫描和配置会增加启动时间（约 50-100ms） |

---

### 方案 B: 自定义配置（当前实现）

#### 实现方式
```java
@Configuration
public class SdkEventConfiguration {
    
    @Bean
    public EventBus eventBus() {
        return new EventBusImpl();
    }
    
    @Bean
    public SkillLifecycleEventService skillLifecycleEventService(EventBus eventBus) {
        return new SkillLifecycleEventServiceImpl(eventBus);
    }
}
```

#### 优点
| 优点 | 说明 |
|------|------|
| **框架无关** | 不依赖 Spring，可用于任何 Java 项目 |
| **显式控制** | 开发者完全控制 Bean 的创建和生命周期 |
| **无启动开销** | 没有自动扫描的开销 |
| **易于调试** | 配置清晰可见，便于问题排查 |

#### 缺点
| 缺点 | 说明 |
|------|------|
| **重复劳动** | 每个项目都需要手动配置 |
| **配置分散** | 配置代码分散在各项目中，难以统一管理 |
| **学习成本** | 开发者需要了解 SDK 内部结构才能正确配置 |
| **易出错** | 手动配置容易遗漏或错误 |

---

## 二、详细对比分析

### 2.1 开发者体验

| 维度 | Spring 自动配置 | 自定义配置 |
|------|----------------|-----------|
| **上手难度** | ⭐ 低（引入依赖即可） | ⭐⭐⭐ 高（需要了解配置细节） |
| **配置复杂度** | ⭐ 低（YAML 配置） | ⭐⭐⭐ 高（Java 代码配置） |
| **文档依赖** | ⭐ 低（Spring 标准模式） | ⭐⭐⭐ 高（需要详细文档） |
| **出错概率** | ⭐ 低 | ⭐⭐⭐ 高 |

### 2.2 运维体验

| 维度 | Spring 自动配置 | 自定义配置 |
|------|----------------|-----------|
| **环境切换** | ⭐ 简单（profile 配置） | ⭐⭐⭐ 复杂（需要代码修改） |
| **配置集中** | ⭐ 集中（application.yml） | ⭐⭐⭐ 分散（代码中） |
| **动态调整** | ⭐ 支持（配置中心） | ⭐⭐⭐ 不支持（需重启） |
| **监控集成** | ⭐ 易集成（Actuator） | ⭐⭐⭐ 需手动实现 |

### 2.3 技术债务

| 维度 | Spring 自动配置 | 自定义配置 |
|------|----------------|-----------|
| **代码重复** | ⭐ 无 | ⭐⭐⭐ 高（每个项目重复） |
| **版本升级** | ⭐ 简单（更新依赖） | ⭐⭐⭐ 复杂（需修改配置代码） |
| **Bug 修复** | ⭐ 集中修复 | ⭐⭐⭐ 分散修复 |
| **功能迭代** | ⭐ 统一升级 | ⭐⭐⭐ 逐个升级 |

---

## 三、给 SDK 团队的建议

### 建议 1: 提供 Spring Boot Starter（高优先级）

**理由**:
1. 当前项目基于 Spring Boot 构建，这是主流技术栈
2. 自动配置是 Spring Boot 生态的标准做法
3. 大幅降低开发者使用门槛

**实现建议**:
```xml
<!-- 新的 starter 模块 -->
<artifactId>agent-sdk-spring-boot-starter</artifactId>
```

**包含内容**:
- `SdkLifecycleAutoConfiguration` - 自动配置类
- `SdkLifecycleProperties` - 配置属性类
- `spring.factories` - SPI 配置文件
- `additional-spring-configuration-metadata.json` - 配置提示

---

### 建议 2: 保留自定义配置能力（中优先级）

**理由**:
1. 部分项目可能不使用 Spring
2. 高级用户可能需要自定义实现
3. 保持框架中立性

**实现建议**:
```java
// 提供工厂类
public class SkillLifecycleServiceFactory {
    public static SkillLifecycleEventService create() {
        return new SkillLifecycleEventServiceImpl(new EventBusImpl());
    }
    
    public static SkillLifecycleEventService create(EventBus eventBus) {
        return new SkillLifecycleEventServiceImpl(eventBus);
    }
}
```

---

### 建议 3: 提供配置迁移工具（低优先级）

**理由**:
1. 现有项目使用自定义配置，需要平滑迁移
2. 降低升级成本

**实现建议**:
```java
// 兼容性配置
@Configuration
@Deprecated
public class SdkEventConfigurationCompatibility {
    
    @Bean
    public SkillLifecycleEventService skillLifecycleEventService(
            @Autowired(required = false) EventBus eventBus) {
        // 兼容旧版本配置
        return eventBus != null 
            ? new SkillLifecycleEventServiceImpl(eventBus)
            : SkillLifecycleServiceFactory.create();
    }
}
```

---

## 四、推荐的实现方案

### 4.1 模块结构

```
agent-sdk/
├── agent-sdk-core/           # 核心接口和实现（框架无关）
│   ├── SkillLifecycleEventService
│   ├── EventBus
│   └── EventBusImpl
│
├── agent-sdk-spring-boot-starter/  # Spring Boot 自动配置
│   ├── SdkLifecycleAutoConfiguration
│   ├── SdkLifecycleProperties
│   └── spring.factories
│
└── agent-sdk-spring/         # Spring 支持（可选）
    ├── @EnableSdkLifecycle
    └── SdkLifecycleRegistrar
```

### 4.2 使用方式对比

#### 方式 1: Spring Boot Starter（推荐）

**Maven 依赖**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-spring-boot-starter</artifactId>
    <version>2.3</version>
</dependency>
```

**代码使用**:
```java
@Service
public class MyService {
    @Autowired
    private SkillLifecycleEventService eventService;
    
    @PostConstruct
    public void init() {
        eventService.subscribeAll(new MyObserver());
    }
}
```

**配置**:
```yaml
ooder:
  sdk:
    lifecycle:
      enabled: true
      event-queue-size: 1000
      async-dispatch: true
```

#### 方式 2: 自定义配置（备选）

**Maven 依赖**:
```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk-core</artifactId>
    <version>2.3</version>
</dependency>
```

**代码使用**:
```java
// 手动创建
EventBus eventBus = new EventBusImpl();
SkillLifecycleEventService service = 
    SkillLifecycleServiceFactory.create(eventBus);

// 订阅事件
service.subscribeAll(new MyObserver());
```

---

## 五、实施路线图

### Phase 1: 立即实施（1-2 周）
- [ ] 创建 `agent-sdk-spring-boot-starter` 模块
- [ ] 实现 `SdkLifecycleAutoConfiguration`
- [ ] 编写单元测试
- [ ] 发布 SNAPSHOT 版本

### Phase 2: 完善文档（2-3 周）
- [ ] 编写 Spring Boot 集成文档
- [ ] 提供示例项目
- [ ] 编写迁移指南
- [ ] 发布 RELEASE 版本

### Phase 3: 长期优化（1-2 月）
- [ ] 收集用户反馈
- [ ] 优化自动配置逻辑
- [ ] 添加更多配置选项
- [ ] 考虑支持其他框架（如 Quarkus、Micronaut）

---

## 六、总结

| 方案 | 推荐指数 | 适用场景 |
|------|---------|---------|
| **Spring Boot Starter** | ⭐⭐⭐⭐⭐ | Spring Boot 项目（当前项目） |
| **自定义配置** | ⭐⭐⭐ | 非 Spring 项目或特殊需求 |
| **两者并存** | ⭐⭐⭐⭐⭐ | 通用 SDK 的最佳实践 |

**最终建议**: SDK 团队应该**两者都提供** - 以 Spring Boot Starter 为主，同时保留核心库供自定义配置使用。这是业界标准做法（如 MyBatis、Redis 等）。

---

*报告生成时间: 2026-02-25*
