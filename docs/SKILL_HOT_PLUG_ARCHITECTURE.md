# Skill 热插拔架构方案

> **目标**: 实现 Skill 的动态安装、卸载、更新，无需重启 Spring 应用  
> **当前问题**: Spring 需要重启才能加载新 Bean  
> **解决方案**: 微内核 + 插件化架构  
> **最后更新**: 2026-02-25

---

## 1. 问题分析

### 1.1 Spring 热插拔的限制

```
┌─────────────────────────────────────────────────────────────────┐
│                    Spring 传统架构限制                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  问题1: Bean 定义在启动时加载                                      │
│  ┌─────────────┐                                                │
│  │  @Component │ 启动时扫描 → 生成 BeanDefinition                │
│  │  @Service   │         → 实例化 Bean                           │
│  │  @Controller│         → 注入依赖                              │
│  └─────────────┘         → 无法运行时新增                        │
│                                                                  │
│  问题2: ClassLoader 不变                                          │
│  ┌─────────────────┐                                            │
│  │  AppClassLoader │ 启动时加载所有类                            │
│  │  (无法卸载类)    │ 运行时新增 jar → ClassNotFoundException     │
│  └─────────────────┘                                            │
│                                                                  │
│  问题3: 路由注册静态化                                             │
│  ┌─────────────────┐                                            │
│  │  @RequestMapping│ 启动时注册到 HandlerMapping                 │
│  │  (无法动态添加)  │ 运行时新增 Controller → 404                 │
│  └─────────────────┘                                            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 热插拔需求

| 操作 | 传统 Spring | 期望行为 |
|------|------------|----------|
| 安装 Skill | 重启应用 | 即时生效 |
| 卸载 Skill | 重启应用 | 即时生效 |
| 更新 Skill | 重启应用 | 即时生效 |
| 版本回滚 | 重启应用 | 即时生效 |

---

## 2. 解决方案对比

### 2.1 方案对比表

| 方案 | 复杂度 | 性能 | 兼容性 | 推荐度 |
|------|--------|------|--------|--------|
| **OSGi** | 高 | 中 | 差 | ⭐⭐ |
| **Spring Boot DevTools** | 低 | 差 | 好 | ⭐⭐ |
| **自定义 ClassLoader** | 中 | 好 | 好 | ⭐⭐⭐⭐ |
| **微服务拆分** | 高 | 好 | 好 | ⭐⭐⭐ |
| **Java Agent + Instrumentation** | 高 | 中 | 中 | ⭐⭐⭐ |
| **GraalVM Native Image** | 高 | 极好 | 差 | ⭐ |

### 2.2 推荐方案：自定义 ClassLoader + 动态代理

```
┌─────────────────────────────────────────────────────────────────┐
│              推荐方案：微内核 + 插件化架构                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    微内核 (Micro Kernel)                 │   │
│  │                                                          │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │  Plugin     │  │  ClassLoader│  │  Service    │     │   │
│  │  │  Manager    │  │  Manager    │  │  Registry   │     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘     │   │
│  │                                                          │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │   │
│  │  │  Route      │  │  Event      │  │  Config     │     │   │
│  │  │  Registry   │  │  Bus        │  │  Center     │     │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘     │   │
│  │                                                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              ▲                                   │
│                              │ 动态加载/卸载                      │
│  ┌───────────────────────────┼───────────────────────────┐     │
│  │                           ▼                           │     │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  │     │
│  │  │ Skill 1 │  │ Skill 2 │  │ Skill 3 │  │ Skill N │  │     │
│  │  │(Plugin) │  │(Plugin) │  │(Plugin) │  │(Plugin) │  │     │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘  │     │
│  │                                                       │     │
│  │  每个 Skill 独立的 ClassLoader 隔离                    │     │
│  └───────────────────────────────────────────────────────┘     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. 详细架构设计

### 3.1 核心组件

#### 3.1.1 Plugin Manager（插件管理器）

```java
// PluginManager.java
@Component
public class PluginManager {
    
    @Autowired
    private ClassLoaderManager classLoaderManager;
    
    @Autowired
    private ServiceRegistry serviceRegistry;
    
    @Autowired
    private RouteRegistry routeRegistry;
    
    private final Map<String, PluginContext> activePlugins = new ConcurrentHashMap<>();
    
    /**
     * 安装 Skill（热插拔）
     */
    public synchronized PluginInstallResult installSkill(SkillPackage skillPackage) {
        String skillId = skillPackage.getMetadata().getId();
        
        // 1. 检查是否已存在
        if (activePlugins.containsKey(skillId)) {
            throw new SkillAlreadyExistsException(skillId);
        }
        
        // 2. 创建独立 ClassLoader
        PluginClassLoader classLoader = classLoaderManager.createClassLoader(skillPackage);
        
        // 3. 加载 Skill 配置
        SkillConfiguration config = loadSkillConfiguration(skillPackage, classLoader);
        
        // 4. 创建 PluginContext
        PluginContext context = new PluginContext(skillId, classLoader, config);
        
        // 5. 注册服务
        registerServices(context);
        
        // 6. 注册路由
        registerRoutes(context);
        
        // 7. 启动 Skill
        startSkill(context);
        
        // 8. 保存上下文
        activePlugins.put(skillId, context);
        
        return PluginInstallResult.success(skillId);
    }
    
    /**
     * 卸载 Skill（热插拔）
     */
    public synchronized PluginUninstallResult uninstallSkill(String skillId) {
        PluginContext context = activePlugins.get(skillId);
        if (context == null) {
            throw new SkillNotFoundException(skillId);
        }
        
        // 1. 停止 Skill
        stopSkill(context);
        
        // 2. 注销路由
        unregisterRoutes(context);
        
        // 3. 注销服务
        unregisterServices(context);
        
        // 4. 清理资源
        cleanupResources(context);
        
        // 5. 关闭 ClassLoader
        classLoaderManager.destroyClassLoader(context.getClassLoader());
        
        // 6. 移除上下文
        activePlugins.remove(skillId);
        
        // 7. 触发 GC
        System.gc();
        
        return PluginUninstallResult.success(skillId);
    }
    
    /**
     * 更新 Skill（热插拔）
     */
    public synchronized PluginUpdateResult updateSkill(SkillPackage newPackage) {
        String skillId = newPackage.getMetadata().getId();
        
        // 1. 备份旧版本
        PluginContext oldContext = activePlugins.get(skillId);
        if (oldContext != null) {
            backupSkill(oldContext);
        }
        
        // 2. 卸载旧版本
        uninstallSkill(skillId);
        
        // 3. 安装新版本
        try {
            return installSkill(newPackage);
        } catch (Exception e) {
            // 4. 回滚到旧版本
            if (oldContext != null) {
                rollbackToOldVersion(oldContext);
            }
            throw e;
        }
    }
}
```

#### 3.1.2 ClassLoader Manager（类加载管理器）

```java
// ClassLoaderManager.java
@Component
public class ClassLoaderManager {
    
    private final Map<String, PluginClassLoader> classLoaders = new ConcurrentHashMap<>();
    
    /**
     * 为 Skill 创建独立的 ClassLoader
     */
    public PluginClassLoader createClassLoader(SkillPackage skillPackage) {
        String skillId = skillPackage.getMetadata().getId();
        
        // 创建 URL 列表
        List<URL> urls = new ArrayList<>();
        
        // 1. Skill 自身的 jar/classes
        urls.addAll(skillPackage.getResourceUrls());
        
        // 2. Skill 依赖的库
        for (Dependency dependency : skillPackage.getDependencies()) {
            urls.add(resolveDependencyUrl(dependency));
        }
        
        // 3. 创建 ClassLoader（父类加载器为 AppClassLoader）
        PluginClassLoader classLoader = new PluginClassLoader(
            skillId,
            urls.toArray(new URL[0]),
            this.getClass().getClassLoader()  // 父 ClassLoader
        );
        
        classLoaders.put(skillId, classLoader);
        
        return classLoader;
    }
    
    /**
     * 销毁 ClassLoader
     */
    public void destroyClassLoader(PluginClassLoader classLoader) {
        String skillId = classLoader.getSkillId();
        
        try {
            // 1. 关闭 ClassLoader
            classLoader.close();
            
            // 2. 从缓存移除
            classLoaders.remove(skillId);
            
            // 3. 清理加载的类缓存
            classLoader.clearCache();
            
        } catch (IOException e) {
            log.error("Failed to close ClassLoader for skill: {}", skillId, e);
        }
    }
}

/**
 * Skill 专用的 ClassLoader
 */
public class PluginClassLoader extends URLClassLoader {
    
    private final String skillId;
    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();
    
    public PluginClassLoader(String skillId, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.skillId = skillId;
    }
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 1. 先检查已加载的类
        Class<?> clazz = loadedClasses.get(name);
        if (clazz != null) {
            return clazz;
        }
        
        // 2. 优先从父类加载器加载（共享 Spring 等框架类）
        if (shouldLoadFromParent(name)) {
            try {
                clazz = getParent().loadClass(name);
                if (clazz != null) {
                    return clazz;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        
        // 3. 从 Skill 自身加载
        clazz = findClass(name);
        loadedClasses.put(name, clazz);
        
        if (resolve) {
            resolveClass(clazz);
        }
        
        return clazz;
    }
    
    private boolean shouldLoadFromParent(String className) {
        // Spring 框架类、Java 标准库等从父类加载器加载
        return className.startsWith("org.springframework") ||
               className.startsWith("java.") ||
               className.startsWith("javax.") ||
               className.startsWith("net.ooder.core");  // 核心共享类
    }
    
    public void clearCache() {
        loadedClasses.clear();
    }
    
    public String getSkillId() {
        return skillId;
    }
}
```

#### 3.1.3 Route Registry（动态路由注册器）

```java
// RouteRegistry.java
@Component
public class RouteRegistry {
    
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;
    
    private final Map<String, List<RegisteredRoute>> skillRoutes = new ConcurrentHashMap<>();
    
    /**
     * 动态注册 Skill 的路由
     */
    public void registerRoutes(String skillId, List<RouteDefinition> routes, PluginClassLoader classLoader) {
        List<RegisteredRoute> registeredRoutes = new ArrayList<>();
        
        for (RouteDefinition route : routes) {
            try {
                // 1. 加载 Controller 类
                Class<?> controllerClass = classLoader.loadClass(route.getControllerClass());
                
                // 2. 创建 Controller 实例
                Object controller = createControllerInstance(controllerClass);
                
                // 3. 创建 RequestMappingInfo
                RequestMappingInfo mappingInfo = createMappingInfo(route);
                
                // 4. 创建 HandlerMethod
                Method method = findMethod(controllerClass, route.getMethodName());
                HandlerMethod handlerMethod = new HandlerMethod(controller, method);
                
                // 5. 注册到 Spring
                handlerMapping.registerMapping(mappingInfo, controller, method);
                
                // 6. 保存注册信息
                registeredRoutes.add(new RegisteredRoute(mappingInfo, handlerMethod));
                
                log.info("Registered route: {} -> {}.{}", 
                    route.getPath(), route.getControllerClass(), route.getMethodName());
                
            } catch (Exception e) {
                log.error("Failed to register route: {}", route.getPath(), e);
                throw new RouteRegistrationException(route.getPath(), e);
            }
        }
        
        skillRoutes.put(skillId, registeredRoutes);
    }
    
    /**
     * 注销 Skill 的路由
     */
    public void unregisterRoutes(String skillId) {
        List<RegisteredRoute> routes = skillRoutes.get(skillId);
        if (routes == null) {
            return;
        }
        
        for (RegisteredRoute route : routes) {
            try {
                // 从 Spring 中注销路由
                handlerMapping.unregisterMapping(route.getMappingInfo());
                
                log.info("Unregistered route: {}", route.getMappingInfo());
                
            } catch (Exception e) {
                log.error("Failed to unregister route: {}", route.getMappingInfo(), e);
            }
        }
        
        skillRoutes.remove(skillId);
    }
    
    private Object createControllerInstance(Class<?> controllerClass) {
        // 使用 Skill 的 ClassLoader 创建实例
        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ControllerInstantiationException(controllerClass.getName(), e);
        }
    }
    
    private RequestMappingInfo createMappingInfo(RouteDefinition route) {
        return RequestMappingInfo
            .paths(route.getPath())
            .methods(RequestMethod.valueOf(route.getHttpMethod()))
            .produces(route.getProduces())
            .consumes(route.getConsumes())
            .build();
    }
}
```

#### 3.1.4 Service Registry（服务注册中心）

```java
// ServiceRegistry.java
@Component
public class ServiceRegistry {
    
    private final Map<String, Map<String, Object>> skillServices = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> serviceProxies = new ConcurrentHashMap<>();
    
    /**
     * 注册 Skill 的服务
     */
    public void registerServices(String skillId, List<ServiceDefinition> services, PluginClassLoader classLoader) {
        Map<String, Object> instances = new HashMap<>();
        Map<String, Object> proxies = new HashMap<>();
        
        for (ServiceDefinition service : services) {
            try {
                // 1. 加载服务类
                Class<?> serviceClass = classLoader.loadClass(service.getClassName());
                
                // 2. 创建服务实例
                Object instance = serviceClass.getDeclaredConstructor().newInstance();
                
                // 3. 注入依赖
                injectDependencies(instance, classLoader);
                
                // 4. 创建动态代理（用于拦截方法调用）
                Object proxy = createServiceProxy(instance, skillId);
                
                // 5. 保存
                instances.put(service.getName(), instance);
                proxies.put(service.getName(), proxy);
                
                // 6. 发布到应用上下文
                publishService(service.getName(), proxy);
                
            } catch (Exception e) {
                log.error("Failed to register service: {}", service.getName(), e);
                throw new ServiceRegistrationException(service.getName(), e);
            }
        }
        
        skillServices.put(skillId, instances);
        serviceProxies.put(skillId, proxies);
    }
    
    /**
     * 注销 Skill 的服务
     */
    public void unregisterServices(String skillId) {
        Map<String, Object> proxies = serviceProxies.get(skillId);
        if (proxies != null) {
            for (String serviceName : proxies.keySet()) {
                unpublishService(serviceName);
            }
        }
        
        skillServices.remove(skillId);
        serviceProxies.remove(skillId);
    }
    
    /**
     * 获取服务（通过代理）
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(String serviceName) {
        for (Map<String, Object> proxies : serviceProxies.values()) {
            Object proxy = proxies.get(serviceName);
            if (proxy != null) {
                return (T) proxy;
            }
        }
        return null;
    }
    
    private Object createServiceProxy(Object target, String skillId) {
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new ServiceInvocationHandler(target, skillId)
        );
    }
    
    private void publishService(String serviceName, Object service) {
        // 发布到 Spring 上下文或其他服务发现机制
        ApplicationContextHolder.getContext().getBeanFactory()
            .registerSingleton(serviceName, service);
    }
    
    private void unpublishService(String serviceName) {
        // 从 Spring 上下文移除
        // 注意：Spring 不支持直接移除 Bean，这里使用代理置空的方式
    }
}

/**
 * 服务调用拦截器
 */
public class ServiceInvocationHandler implements InvocationHandler {
    
    private final Object target;
    private final String skillId;
    
    public ServiceInvocationHandler(Object target, String skillId) {
        this.target = target;
        this.skillId = skillId;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1. 检查 Skill 是否活跃
        if (!PluginManager.getInstance().isSkillActive(skillId)) {
            throw new SkillNotActiveException(skillId);
        }
        
        // 2. 执行方法
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
```

---

## 4. 完整热插拔流程

### 4.1 Skill 安装流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    Skill 安装流程（热插拔）                        │
└─────────────────────────────────────────────────────────────────┘

[1] 接收安装请求
    │
    ▼
[2] 验证 Skill 包
    ├── 检查格式完整性
    ├── 验证数字签名
    └── 检查版本兼容性
    │
    ▼
[3] 创建 PluginClassLoader
    ├── 解析依赖
    ├── 下载依赖库
    └── 创建 URLClassLoader
    │
    ▼
[4] 加载 Skill 配置
    ├── 解析 skill.yaml
    └── 加载元数据
    │
    ▼
[5] 注册服务
    ├── 实例化 Service
    ├── 注入依赖
    ├── 创建代理
    └── 发布到上下文
    │
    ▼
[6] 注册路由
    ├── 加载 Controller
    ├── 创建 HandlerMethod
    └── 注册到 HandlerMapping
    │
    ▼
[7] 启动 Skill
    ├── 执行生命周期钩子
    ├── 初始化资源
    └── 启动后台任务
    │
    ▼
[8] 完成安装
    ├── 保存 PluginContext
    ├── 记录日志
    └── 通知监听者
    │
    ▼
[✓] Skill 即时生效（无需重启）
```

### 4.2 Skill 卸载流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    Skill 卸载流程（热插拔）                        │
└─────────────────────────────────────────────────────────────────┘

[1] 接收卸载请求
    │
    ▼
[2] 停止 Skill
    ├── 停止后台任务
    ├── 释放资源
    └── 执行 preDestroy 钩子
    │
    ▼
[3] 注销路由
    ├── 从 HandlerMapping 移除
    └── 清理路由缓存
    │
    ▼
[4] 注销服务
    ├── 从上下文移除
    ├── 销毁代理
    └── 执行销毁逻辑
    │
    ▼
[5] 清理资源
    ├── 关闭数据库连接
    ├── 释放文件句柄
    └── 清理临时文件
    │
    ▼
[6] 关闭 ClassLoader
    ├── 清理类缓存
    └── 关闭 URLClassLoader
    │
    ▼
[7] 触发 GC
    ├── 提示 JVM 回收
    └── 等待类卸载
    │
    ▼
[8] 完成卸载
    ├── 移除 PluginContext
    ├── 记录日志
    └── 通知监听者
    │
    ▼
[✓] Skill 已卸载（无需重启）
```

---

## 5. 使用示例

### 5.1 安装 Skill

```java
// SkillController.java
@RestController
@RequestMapping("/api/skills")
public class SkillController {
    
    @Autowired
    private PluginManager pluginManager;
    
    @PostMapping("/install")
    public ResponseEntity<ApiResponse<PluginInstallResult>> installSkill(
            @RequestParam("file") MultipartFile skillPackage) {
        
        try {
            // 1. 解析 Skill 包
            SkillPackage pkg = SkillPackageParser.parse(skillPackage.getInputStream());
            
            // 2. 安装（热插拔）
            PluginInstallResult result = pluginManager.installSkill(pkg);
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }
}
```

### 5.2 调用已安装的 Skill

```java
// 其他服务调用 Skill 服务
@Service
public class SomeService {
    
    @Autowired
    private ServiceRegistry serviceRegistry;
    
    public void useSkillService() {
        // 获取 Skill 提供的服务（通过代理）
        WeatherService weatherService = serviceRegistry.getService("weatherService");
        
        // 调用方法
        WeatherData data = weatherService.getCurrentWeather("Beijing");
    }
}
```

### 5.3 Skill 内部实现

```java
// skill-weather/src/main/java/WeatherController.java
@RestController
@RequestMapping("/api/skills/weather")
public class WeatherController {
    
    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/current")
    public ResponseEntity<A2AResponse<WeatherData>> getCurrentWeather(
            @RequestParam String city) {
        
        WeatherData data = weatherService.getCurrentWeather(city);
        return ResponseEntity.ok(A2AResponse.success(data));
    }
}

@Service
public class WeatherService {
    
    public WeatherData getCurrentWeather(String city) {
        // 业务逻辑
        return new WeatherData(city, 25, "Sunny");
    }
}
```

---

## 6. 注意事项与限制

### 6.1 类加载器泄漏预防

```java
// 确保资源正确关闭
public class PluginContext implements Closeable {
    
    private final List<Closeable> resources = new ArrayList<>();
    
    public void addResource(Closeable resource) {
        resources.add(resource);
    }
    
    @Override
    public void close() {
        for (Closeable resource : resources) {
            try {
                resource.close();
            } catch (IOException e) {
                log.error("Failed to close resource", e);
            }
        }
    }
}
```

### 6.2 线程清理

```java
// 确保 Skill 创建的线程被清理
public class ThreadManager {
    
    private final Map<String, List<Thread>> skillThreads = new ConcurrentHashMap<>();
    
    public void registerThread(String skillId, Thread thread) {
        skillThreads.computeIfAbsent(skillId, k -> new ArrayList<>()).add(thread);
    }
    
    public void stopSkillThreads(String skillId) {
        List<Thread> threads = skillThreads.get(skillId);
        if (threads != null) {
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    thread.interrupt();
                }
            }
        }
        skillThreads.remove(skillId);
    }
}
```

### 6.3 内存泄漏检查

```java
// 定期检查类加载器泄漏
@Component
public class MemoryLeakDetector {
    
    @Scheduled(fixedDelay = 60000)
    public void checkMemoryLeak() {
        // 检查未卸载的类
        // 使用 Java Agent 或 JMX 监控
    }
}
```

---

## 7. 总结

### 7.1 核心优势

| 特性 | 传统 Spring | 热插拔方案 |
|------|------------|-----------|
| 安装 Skill | 重启应用 | 即时生效 |
| 卸载 Skill | 重启应用 | 即时生效 |
| 更新 Skill | 重启应用 | 即时生效 |
| 版本回滚 | 重启应用 | 即时生效 |
| 隔离性 | 无 | ClassLoader 隔离 |
| 依赖管理 | 复杂 | 自动解析 |

### 7.2 关键技术点

1. **自定义 ClassLoader**: 实现 Skill 类隔离
2. **动态路由注册**: 运行时增删 Controller
3. **服务代理**: 解耦服务调用与实现
4. **资源管理**: 确保卸载时资源释放
5. **生命周期管理**: 规范 Skill 启动/停止流程

### 7.3 实施建议

1. **Phase 1**: 实现 PluginManager 和 ClassLoaderManager
2. **Phase 2**: 实现 RouteRegistry 和 ServiceRegistry
3. **Phase 3**: 集成到现有 Nexus 系统
4. **Phase 4**: 编写 Skill 开发 SDK
5. **Phase 5**: 迁移现有 Skill 到热插拔架构
