package net.ooder.skill.hotplug.registry;

import net.ooder.skill.hotplug.classloader.PluginClassLoader;
import net.ooder.skill.hotplug.config.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务注册器
 * 负责管理Skill服务的动态注册和代理
 */
@Component
public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    // Skill ID -> 服务代理映射
    private final Map<String, Map<String, ServiceProxy>> serviceProxies = new ConcurrentHashMap<>();

    // 接口 -> 实现映射（用于服务发现）
    private final Map<String, List<ServiceReference>> interfaceRegistry = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("ServiceRegistry initialized");
    }

    /**
     * 注册服务
     */
    public void registerService(String skillId, ServiceDefinition serviceDef, PluginClassLoader classLoader) {
        logger.info("Registering service: {} for skill: {}", serviceDef.getName(), skillId);

        try {
            // 加载接口和实现类
            Class<?> interfaceClass = classLoader.loadClass(serviceDef.getInterfaceClass());
            Class<?> implClass = classLoader.loadClass(serviceDef.getImplementationClass());

            // 创建服务实例
            Object serviceInstance = createServiceInstance(implClass);
            
            if (serviceInstance == null) {
                logger.info("Skipping service registration for: {} (requires Spring injection)", serviceDef.getName());
                return;
            }

            // 创建动态代理
            Object proxy = createProxy(interfaceClass, serviceInstance, classLoader, skillId);

            // 保存服务代理
            Map<String, ServiceProxy> skillServices = serviceProxies.computeIfAbsent(skillId, k -> new ConcurrentHashMap<>());
            ServiceProxy serviceProxy = new ServiceProxy(skillId, serviceDef.getName(), interfaceClass, serviceInstance, proxy);
            skillServices.put(serviceDef.getName(), serviceProxy);

            // 注册到接口索引
            String interfaceName = interfaceClass.getName();
            List<ServiceReference> references = interfaceRegistry.computeIfAbsent(interfaceName, k -> new ArrayList<>());
            references.add(new ServiceReference(skillId, serviceDef.getName(), serviceProxy));

            logger.info("Successfully registered service: {}.{} (interface: {})",
                    skillId, serviceDef.getName(), interfaceName);

        } catch (Exception e) {
            logger.error("Failed to register service: {} for skill: {}", serviceDef.getName(), skillId, e);
            throw new RuntimeException("Service registration failed", e);
        }
    }

    /**
     * 注销指定Skill的所有服务
     */
    public void unregisterServices(String skillId) {
        logger.info("Unregistering services for skill: {}", skillId);

        Map<String, ServiceProxy> services = serviceProxies.remove(skillId);
        if (services == null || services.isEmpty()) {
            logger.warn("No services found for skill: {}", skillId);
            return;
        }

        // 从接口索引中移除
        for (ServiceProxy proxy : services.values()) {
            String interfaceName = proxy.getInterfaceClass().getName();
            List<ServiceReference> references = interfaceRegistry.get(interfaceName);
            if (references != null) {
                references.removeIf(ref -> ref.getSkillId().equals(skillId));
                if (references.isEmpty()) {
                    interfaceRegistry.remove(interfaceName);
                }
            }
        }

        logger.info("Successfully unregistered {} services for skill: {}", services.size(), skillId);
    }

    /**
     * 获取服务代理
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(String skillId, String serviceName, Class<T> interfaceClass) {
        Map<String, ServiceProxy> skillServices = serviceProxies.get(skillId);
        if (skillServices == null) {
            return null;
        }

        ServiceProxy proxy = skillServices.get(serviceName);
        if (proxy == null) {
            return null;
        }

        if (!interfaceClass.isAssignableFrom(proxy.getInterfaceClass())) {
            throw new IllegalArgumentException("Interface mismatch for service: " + serviceName);
        }

        return (T) proxy.getProxy();
    }

    /**
     * 通过接口获取服务
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getServicesByInterface(Class<T> interfaceClass) {
        List<ServiceReference> references = interfaceRegistry.get(interfaceClass.getName());
        if (references == null || references.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> services = new ArrayList<>();
        for (ServiceReference ref : references) {
            services.add((T) ref.getServiceProxy().getProxy());
        }
        return services;
    }

    /**
     * 获取指定Skill的所有服务
     */
    public Map<String, ServiceProxy> getServices(String skillId) {
        return serviceProxies.get(skillId);
    }

    /**
     * 获取所有已注册的服务
     */
    public Map<String, Map<String, ServiceProxy>> getAllServices() {
        return new ConcurrentHashMap<>(serviceProxies);
    }

    /**
     * 调用服务方法
     */
    public Object invokeService(String skillId, String serviceName, String methodName, Object... args) throws Exception {
        Map<String, ServiceProxy> skillServices = serviceProxies.get(skillId);
        if (skillServices == null) {
            throw new IllegalArgumentException("Skill not found: " + skillId);
        }

        ServiceProxy proxy = skillServices.get(serviceName);
        if (proxy == null) {
            throw new IllegalArgumentException("Service not found: " + serviceName);
        }

        Object serviceInstance = proxy.getTarget();
        Method method = findMethod(serviceInstance.getClass(), methodName, args);
        return method.invoke(serviceInstance, args);
    }

    // ==================== 私有方法 ====================

    private Object createServiceInstance(Class<?> implClass) throws Exception {
        try {
            return implClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.warn("Could not create service instance via default constructor: {}, skipping service registration", implClass.getName());
            return null;
        }
    }

    private Object createProxy(Class<?> interfaceClass, Object target, PluginClassLoader classLoader, String skillId) {
        // 使用JDK动态代理
        return Proxy.newProxyInstance(
                classLoader,
                new Class<?>[]{interfaceClass},
                new ServiceInvocationHandler(target, skillId)
        );
    }

    private Method findMethod(Class<?> clazz, String methodName, Object[] args) throws NoSuchMethodException {
        if (args == null || args.length == 0) {
            return clazz.getMethod(methodName);
        }

        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
        }

        return clazz.getMethod(methodName, paramTypes);
    }

    /**
     * 服务调用处理器
     */
    private static class ServiceInvocationHandler implements InvocationHandler {

        private final Object target;
        private final String skillId;

        public ServiceInvocationHandler(Object target, String skillId) {
            this.target = target;
            this.skillId = skillId;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 可以在这里添加拦截逻辑：日志、监控、权限检查等
            long startTime = System.currentTimeMillis();
            try {
                return method.invoke(target, args);
            } finally {
                long duration = System.currentTimeMillis() - startTime;
                if (logger.isDebugEnabled()) {
                    logger.debug("Service method invoked: {}.{} ({}ms)",
                            skillId, method.getName(), duration);
                }
            }
        }
    }

    /**
     * 服务代理信息
     */
    public static class ServiceProxy {
        private final String skillId;
        private final String serviceName;
        private final Class<?> interfaceClass;
        private final Object target;
        private final Object proxy;

        public ServiceProxy(String skillId, String serviceName, Class<?> interfaceClass, 
                           Object target, Object proxy) {
            this.skillId = skillId;
            this.serviceName = serviceName;
            this.interfaceClass = interfaceClass;
            this.target = target;
            this.proxy = proxy;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public Class<?> getInterfaceClass() {
            return interfaceClass;
        }

        public Object getTarget() {
            return target;
        }

        public Object getProxy() {
            return proxy;
        }
    }

    /**
     * 服务引用
     */
    public static class ServiceReference {
        private final String skillId;
        private final String serviceName;
        private final ServiceProxy serviceProxy;

        public ServiceReference(String skillId, String serviceName, ServiceProxy serviceProxy) {
            this.skillId = skillId;
            this.serviceName = serviceName;
            this.serviceProxy = serviceProxy;
        }

        public String getSkillId() {
            return skillId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public ServiceProxy getServiceProxy() {
            return serviceProxy;
        }
    }
}
