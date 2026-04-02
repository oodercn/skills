package net.ooder.skill.hotplug.registry;

import net.ooder.skill.hotplug.classloader.PluginClassLoader;
import net.ooder.skill.hotplug.config.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态路由注册器
 * 负责将Skill的Controller动态注册到Spring MVC
 */
@Component
public class RouteRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RouteRegistry.class);

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    private ApplicationContext applicationContext;

    // Skill ID -> 注册的路由信息映射
    private final Map<String, Set<RegisteredRoute>> registeredRoutes = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("RouteRegistry initialized");
    }

    /**
     * 注册路由
     */
    public void registerRoutes(String skillId, List<RouteDefinition> routes, PluginClassLoader classLoader) {
        logger.info("Registering {} routes for skill: {}", routes.size(), skillId);

        Set<RegisteredRoute> skillRoutes = ConcurrentHashMap.newKeySet();
        int skippedCount = 0;

        for (RouteDefinition route : routes) {
            try {
                RegisteredRoute registeredRoute = registerRoute(skillId, route, classLoader);
                if (registeredRoute != null) {
                    skillRoutes.add(registeredRoute);
                    logger.debug("Registered route: {} {} -> {}.{}",
                            route.getMethod(), route.getPath(),
                            route.getControllerClass(), route.getMethodName());
                } else {
                    skippedCount++;
                    logger.debug("Skipped route: {} {} -> {}.{}",
                            route.getMethod(), route.getPath(),
                            route.getControllerClass(), route.getMethodName());
                }
            } catch (Exception e) {
                logger.error("Failed to register route: {}", route, e);
            }
        }

        registeredRoutes.put(skillId, skillRoutes);
        logger.info("Successfully registered {} routes for skill: {} (skipped: {})", 
                skillRoutes.size(), skillId, skippedCount);
    }

    /**
     * 注销指定Skill的所有路由
     */
    public void unregisterRoutes(String skillId) {
        logger.info("Unregistering routes for skill: {}", skillId);

        Set<RegisteredRoute> routes = registeredRoutes.remove(skillId);
        if (routes == null || routes.isEmpty()) {
            logger.warn("No routes found for skill: {}", skillId);
            return;
        }

        for (RegisteredRoute route : routes) {
            try {
                unregisterRoute(route);
                logger.debug("Unregistered route: {}", route.getMappingInfo());
            } catch (Exception e) {
                logger.error("Failed to unregister route: {}", route, e);
            }
        }

        logger.info("Successfully unregistered {} routes for skill: {}", routes.size(), skillId);
    }

    /**
     * 获取已注册的路由
     */
    public Set<RegisteredRoute> getRegisteredRoutes(String skillId) {
        return registeredRoutes.get(skillId);
    }

    /**
     * 获取所有已注册的路由
     */
    public Map<String, Set<RegisteredRoute>> getAllRegisteredRoutes() {
        return new ConcurrentHashMap<>(registeredRoutes);
    }

    // ==================== 私有方法 ====================

    private RegisteredRoute registerRoute(String skillId, RouteDefinition routeDef, 
                                           PluginClassLoader classLoader) throws Exception {
        logger.info("Registering route: {} {} -> {}.{}", 
                routeDef.getMethod(), routeDef.getPath(),
                routeDef.getControllerClass(), routeDef.getMethodName());
        
        Class<?> controllerClass = classLoader.loadClass(routeDef.getControllerClass());
        logger.debug("Loaded controller class: {}", controllerClass.getName());

        // 检查是否已由 Spring 管理（方案B：跳过已注册路由）
        Object existingController = findExistingSpringController(controllerClass);
        if (existingController != null) {
            logger.debug("Controller {} is already managed by Spring, checking route registration", 
                    controllerClass.getName());
            
            // 检查路由是否已存在
            if (isRouteAlreadyRegistered(routeDef.getPath(), routeDef.getMethod())) {
                logger.warn("Route {} {} is already registered by Spring, skipping", 
                        routeDef.getMethod(), routeDef.getPath());
                return null;
            }
        }

        Object controller = createControllerInstance(controllerClass);
        logger.debug("Created controller instance: {}", controller.getClass().getName());

        Method method = findMethod(controllerClass, routeDef.getMethodName(), routeDef.getParameterTypes(), classLoader);
        logger.debug("Found method: {} with {} parameters", method.getName(), method.getParameterCount());

        RequestMappingInfo mappingInfo = createMappingInfo(routeDef);
        logger.debug("Created RequestMappingInfo: {}", mappingInfo);

        org.springframework.web.method.HandlerMethod handlerMethod = 
                new org.springframework.web.method.HandlerMethod(controller, method);

        handlerMapping.registerMapping(mappingInfo, controller, method);
        logger.info("Successfully registered route: {} {} to Spring MVC", routeDef.getMethod(), routeDef.getPath());

        RegisteredRoute registeredRoute = new RegisteredRoute();
        registeredRoute.setSkillId(skillId);
        registeredRoute.setMappingInfo(mappingInfo);
        registeredRoute.setController(controller);
        registeredRoute.setMethod(method);
        registeredRoute.setHandlerMethod(handlerMethod);
        registeredRoute.setRouteDefinition(routeDef);

        return registeredRoute;
    }

    private void unregisterRoute(RegisteredRoute route) {
        handlerMapping.unregisterMapping(route.getMappingInfo());
    }

    /**
     * 查找是否已由 Spring 管理的 Controller Bean
     * @param controllerClass Controller 类
     * @return 如果存在返回 Bean 实例，否则返回 null
     */
    private Object findExistingSpringController(Class<?> controllerClass) {
        try {
            // 尝试按类型获取
            Object bean = applicationContext.getBean(controllerClass);
            if (bean != null) {
                logger.debug("Found existing Spring controller by type: {}", controllerClass.getName());
                return bean;
            }
        } catch (Exception e) {
            logger.debug("No existing Spring controller found by type: {}", controllerClass.getName());
        }
        
        // 尝试按简单类名获取
        String beanName = controllerClass.getSimpleName();
        // 首字母小写（Spring 默认命名规则）
        beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
        try {
            Object bean = applicationContext.getBean(beanName);
            if (bean != null) {
                logger.debug("Found existing Spring controller by name: {}", beanName);
                return bean;
            }
        } catch (Exception e) {
            logger.debug("No existing Spring controller found by name: {}", beanName);
        }
        
        return null;
    }

    /**
     * 检查路由是否已注册
     * @param path 路由路径
     * @param method HTTP 方法
     * @return 如果已存在返回 true
     */
    private boolean isRouteAlreadyRegistered(String path, String method) {
        try {
            // 获取已注册的所有处理器映射
            Map<?, ?> handlerMethods = handlerMapping.getHandlerMethods();
            
            // 遍历检查是否存在相同路径和方法的映射
            for (Object mappingInfo : handlerMethods.keySet()) {
                if (mappingInfo instanceof RequestMappingInfo) {
                    RequestMappingInfo existingMapping = (RequestMappingInfo) mappingInfo;
                    
                    // 检查路径是否匹配
                    Set<String> existingPatterns = existingMapping.getPatternValues();
                    if (existingPatterns != null && existingPatterns.contains(path)) {
                        // 检查 HTTP 方法是否匹配
                        Set<RequestMethod> existingMethods = existingMapping.getMethodsCondition().getMethods();
                        if (existingMethods == null || existingMethods.isEmpty() || 
                            existingMethods.contains(RequestMethod.valueOf(method.toUpperCase()))) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error checking existing route registration: {}", e.getMessage());
        }
        return false;
    }

    private Object createControllerInstance(Class<?> controllerClass) throws Exception {
        String beanName = controllerClass.getSimpleName();
        String className = controllerClass.getName();
        
        // 首先尝试按类型获取已存在的 Spring Bean，避免重复创建
        try {
            Object existingBean = applicationContext.getBean(controllerClass);
            if (existingBean != null) {
                logger.debug("Using existing Spring bean for controller: {}", className);
                return existingBean;
            }
        } catch (Exception e) {
            logger.debug("No existing bean found for controller: {}", className);
        }
        
        // 其次尝试按 beanName 获取
        try {
            Object existingBean = applicationContext.getBean(beanName);
            if (existingBean != null) {
                logger.debug("Using existing Spring bean by name for controller: {}", beanName);
                return existingBean;
            }
        } catch (Exception e) {
            logger.debug("No existing bean found by name for controller: {}", beanName);
        }
        
        // 创建新的 Bean 实例
        try {
            Object bean = applicationContext.getAutowireCapableBeanFactory()
                    .createBean(controllerClass);
            logger.info("Created and autowired controller instance: {}", className);
            return bean;
        } catch (Exception e) {
            logger.warn("Failed to create controller with Spring autowiring, trying default constructor: {}", className);
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create controller instance: " + className, ex);
            }
        }
    }

    private Method findMethod(Class<?> clazz, String methodName, String[] parameterTypes, 
                              PluginClassLoader classLoader) throws NoSuchMethodException {
        if (parameterTypes == null || parameterTypes.length == 0) {
            // 当没有指定参数类型时，查找所有同名方法并选择参数最少的一个
            // 这样可以避免重载方法匹配错误
            Method bestMatch = null;
            int minParams = Integer.MAX_VALUE;
            
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    int paramCount = method.getParameterCount();
                    if (paramCount < minParams) {
                        minParams = paramCount;
                        bestMatch = method;
                    }
                }
            }
            
            if (bestMatch != null) {
                return bestMatch;
            }
        } else {
            Class<?>[] paramClasses = new Class<?>[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                try {
                    paramClasses[i] = loadClass(parameterTypes[i], classLoader);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class not found: " + parameterTypes[i], e);
                }
            }
            return clazz.getMethod(methodName, paramClasses);
        }
        throw new NoSuchMethodException("Method not found: " + methodName);
    }

    private RequestMappingInfo createMappingInfo(RouteDefinition routeDef) {
        // 创建 BuilderConfiguration 并设置 PathPatternParser
        RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();
        config.setPatternParser(new PathPatternParser());

        // 使用 builder() 方法创建，这是 Spring Boot 2.7 兼容的方式
        RequestMappingInfo.Builder builder = RequestMappingInfo
                .paths(routeDef.getPath())
                .methods(RequestMethod.valueOf(routeDef.getMethod().toUpperCase()));

        if (routeDef.getConsumes() != null) {
            builder.consumes(routeDef.getConsumes());
        }

        if (routeDef.getProduces() != null) {
            builder.produces(routeDef.getProduces());
        }

        if (routeDef.getParams() != null) {
            builder.params(routeDef.getParams());
        }

        if (routeDef.getHeaders() != null) {
            builder.headers(routeDef.getHeaders());
        }

        // 设置 options 以确保与 Spring Boot 2.7 兼容
        builder.options(config);

        return builder.build();
    }

    private Class<?> loadClass(String className, PluginClassLoader classLoader) throws ClassNotFoundException {
        try {
            // 首先尝试使用 PluginClassLoader 加载类（优先）
            if (classLoader != null) {
                try {
                    return classLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    logger.debug("PluginClassLoader failed to load class: {}, falling back to system classloader", className);
                }
            }
            // 回退到系统类加载器
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className, e);
        }
    }

    /**
     * 已注册的路由信息
     */
    public static class RegisteredRoute {
        private String skillId;
        private RequestMappingInfo mappingInfo;
        private Object controller;
        private Method method;
        private org.springframework.web.method.HandlerMethod handlerMethod;
        private RouteDefinition routeDefinition;

        // Getters and Setters
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public RequestMappingInfo getMappingInfo() { return mappingInfo; }
        public void setMappingInfo(RequestMappingInfo mappingInfo) { this.mappingInfo = mappingInfo; }
        public Object getController() { return controller; }
        public void setController(Object controller) { this.controller = controller; }
        public Method getMethod() { return method; }
        public void setMethod(Method method) { this.method = method; }
        public org.springframework.web.method.HandlerMethod getHandlerMethod() { return handlerMethod; }
        public void setHandlerMethod(org.springframework.web.method.HandlerMethod handlerMethod) { this.handlerMethod = handlerMethod; }
        public RouteDefinition getRouteDefinition() { return routeDefinition; }
        public void setRouteDefinition(RouteDefinition routeDefinition) { this.routeDefinition = routeDefinition; }

        @Override
        public String toString() {
            return "RegisteredRoute{" +
                    "skillId='" + skillId + '\'' +
                    ", path='" + (routeDefinition != null ? routeDefinition.getPath() : "null") + '\'' +
                    ", method='" + (routeDefinition != null ? routeDefinition.getMethod() : "null") + '\'' +
                    '}';
        }
    }
}
