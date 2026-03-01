# SDK协作请求：Skill生命周期事件订阅机制

## 请求日期
2026-02-25

## 请求方
Nexus Enterprise 能力管理团队

## 问题背景
通过黑盒测试和白盒代码审查，发现当前SDK**完全缺失**Skill生命周期事件订阅机制。外部系统无法通过观察者模式监听Skill的创建、加载、启动、停止、卸载等关键事件。

---

## 黑盒探测结果

### 1. 已执行的探测手段

| 探测方法 | 执行结果 | 结论 |
|---------|---------|------|
| Actuator端点检查 | 404 Not Found | 未启用Spring Boot Actuator |
| Spring Event监听扫描 | 未发现 | 无`@EventListener`或`ApplicationListener`实现 |
| 接口扫描 | 未发现 | 无`Listener`/`Observer`相关接口 |
| 动态代理拦截测试 | 无事件产生 | SDK未发布任何生命周期事件 |
| 外部观察者轮询 | 仅能获取静态配置 | 运行时状态与配置完全脱节 |

### 2. 关键发现

```
探测结论：SDK未提供任何生命周期事件订阅机制

所有观察到的事件均来自外部轮询，而非SDK主动推送：
- ❌ SKILL_CREATED - 无原生事件
- ❌ SKILL_LOADED - 无原生事件  
- ❌ SKILL_STARTED - 无原生事件
- ❌ SKILL_STOPPED - 无原生事件
- ❌ SKILL_UNLOADED - 无原生事件
- ❌ CAPABILITY_INVOKED - 无原生事件
- ❌ HEALTH_CHECK - 无原生事件
- ❌ ERROR_OCCURRED - 无原生事件
```

---

## 期望的SDK能力

### 1. 事件类型定义

```java
public enum SkillLifecycleEventType {
    // 生命周期事件
    SKILL_CREATED,          // Skill被创建
    SKILL_VALIDATING,       // Skill验证中
    SKILL_VALIDATED,        // Skill验证通过/失败
    SKILL_LOADING,          // Skill加载中
    SKILL_LOADED,           // Skill加载完成
    SKILL_REGISTERING,      // Skill注册中
    SKILL_REGISTERED,       // Skill注册完成
    SKILL_STARTING,         // Skill启动中
    SKILL_STARTED,          // Skill启动完成
    SKILL_STOPPING,         // Skill停止中
    SKILL_STOPPED,          // Skill停止完成
    SKILL_UNLOADING,        // Skill卸载中
    SKILL_UNLOADED,         // Skill卸载完成
    SKILL_DELETED,          // Skill被删除
    
    // 运行时事件
    CAPABILITY_INVOKED,     // 能力被调用
    CAPABILITY_COMPLETED,   // 能力调用完成
    CAPABILITY_FAILED,      // 能力调用失败
    
    // 健康事件
    HEALTH_CHECK_STARTED,   // 健康检查开始
    HEALTH_STATUS_CHANGED,  // 健康状态变化
    
    // 错误事件
    ERROR_OCCURRED,         // 发生错误
    RECOVERY_ATTEMPTED      // 尝试恢复
}
```

### 2. 事件对象定义

```java
public class SkillLifecycleEvent {
    private String eventId;           // 事件唯一ID
    private SkillLifecycleEventType type;  // 事件类型
    private String skillId;           // 关联的Skill ID
    private String sceneId;           // 关联的场景ID（如有）
    private long timestamp;           // 事件发生时间
    private Map<String, Object> payload;   // 事件负载数据
    private String source;            // 事件来源组件
    
    // 构造方法、Getter/Setter
}
```

### 3. 观察者接口

```java
/**
 * Skill生命周期观察者接口
 */
public interface SkillLifecycleObserver {
    
    /**
     * 当Skill生命周期事件发生时调用
     * @param event 生命周期事件
     */
    void onSkillLifecycleEvent(SkillLifecycleEvent event);
    
    /**
     * 获取观察者关心的Skill ID列表
     * 返回null或空列表表示关心所有Skill
     * @return Skill ID列表
     */
    default List<String> getInterestedSkills() {
        return null;
    }
    
    /**
     * 获取观察者关心的事件类型
     * 返回null或空列表表示关心所有事件
     * @return 事件类型列表
     */
    default List<SkillLifecycleEventType> getInterestedEventTypes() {
        return null;
    }
}
```

### 4. 事件发布/订阅服务

```java
/**
 * Skill生命周期事件服务
 */
public interface SkillLifecycleEventService {
    
    /**
     * 订阅Skill生命周期事件
     * @param observer 观察者实例
     * @return 订阅ID
     */
    String subscribe(SkillLifecycleObserver observer);
    
    /**
     * 订阅特定Skill的事件
     * @param skillId Skill ID
     * @param observer 观察者实例
     * @return 订阅ID
     */
    String subscribe(String skillId, SkillLifecycleObserver observer);
    
    /**
     * 取消订阅
     * @param subscriptionId 订阅ID
     */
    void unsubscribe(String subscriptionId);
    
    /**
     * 发布事件（SDK内部使用）
     * @param event 生命周期事件
     */
    void publishEvent(SkillLifecycleEvent event);
    
    /**
     * 获取事件历史
     * @param skillId Skill ID
     * @param limit 最大返回数量
     * @return 事件列表
     */
    List<SkillLifecycleEvent> getEventHistory(String skillId, int limit);
}
```

### 5. Spring Boot Starter支持

```java
/**
 * 自动配置类
 */
@Configuration
@ConditionalOnClass(SkillLifecycleEventService.class)
@EnableConfigurationProperties(SkillLifecycleProperties.class)
public class SkillLifecycleAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SkillLifecycleEventService skillLifecycleEventService() {
        return new DefaultSkillLifecycleEventService();
    }
}

/**
 * 配置属性
 */
@ConfigurationProperties(prefix = "ooder.skill.lifecycle")
public class SkillLifecycleProperties {
    private boolean enabled = true;           // 是否启用生命周期事件
    private int eventQueueSize = 1000;        // 事件队列大小
    private int historyRetentionMinutes = 60; // 历史事件保留时间
    private boolean asyncDispatch = true;     // 是否异步分发事件
}
```

---

## 使用示例

### 示例1：基础观察者

```java
@Component
public class MySkillObserver implements SkillLifecycleObserver {
    
    @Autowired
    private SkillLifecycleEventService eventService;
    
    @PostConstruct
    public void init() {
        // 订阅所有Skill的所有事件
        eventService.subscribe(this);
    }
    
    @Override
    public void onSkillLifecycleEvent(SkillLifecycleEvent event) {
        System.out.println("[Skill事件] " + event.getType() + 
                          " - Skill: " + event.getSkillId());
        
        // 根据事件类型处理
        switch (event.getType()) {
            case SKILL_STARTED:
                // Skill启动后的处理
                break;
            case SKILL_STOPPED:
                // Skill停止后的处理
                break;
            case ERROR_OCCURRED:
                // 错误处理
                break;
        }
    }
}
```

### 示例2：特定Skill监听

```java
// 只监听特定Skill
String subscriptionId = eventService.subscribe("skill-org-dingding", new SkillLifecycleObserver() {
    @Override
    public void onSkillLifecycleEvent(SkillLifecycleEvent event) {
        // 只处理钉钉组织服务的事件
    }
    
    @Override
    public List<SkillLifecycleEventType> getInterestedEventTypes() {
        // 只关心错误和健康事件
        return Arrays.asList(
            SkillLifecycleEventType.ERROR_OCCURRED,
            SkillLifecycleEventType.HEALTH_STATUS_CHANGED
        );
    }
});
```

### 示例3：通过Spring Event集成

```java
@Component
public class SkillEventBridge implements SkillLifecycleObserver {
    
    @Autowired
    private ApplicationEventPublisher springEventPublisher;
    
    @Override
    public void onSkillLifecycleEvent(SkillLifecycleEvent event) {
        // 将SDK事件转换为Spring Event
        springEventPublisher.publishEvent(
            new SkillLifecycleSpringEvent(this, event)
        );
    }
}

// 使用Spring Event监听
@Component
public class MySpringListener {
    
    @EventListener
    public void handleSkillStarted(SkillLifecycleSpringEvent event) {
        if (event.getSkillEvent().getType() == SkillLifecycleEventType.SKILL_STARTED) {
            // 处理Skill启动事件
        }
    }
}
```

---

## 优先级

**P0 - 阻塞性问题**

当前无法构建完整的能力管理界面，因为：
1. 无法实时感知Skill状态变化
2. 无法追踪能力调用链路
3. 无法及时响应Skill故障

---

## 期望交付时间

- **初步版本**：2周内提供Beta版本
- **正式版本**：1个月内提供稳定版本

---

## 联系方式

如有疑问，请联系 Nexus Enterprise 能力管理团队。
