package net.ooder.skill.hotplug.registry;

import net.ooder.skill.hotplug.classloader.PluginClassLoader;
import net.ooder.skill.hotplug.config.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
                // 检查路由是否已存在（方案B：跳过已注册路由）
                if (isRouteAlreadyRegistered(route.getPath(), route.getMethod())) {
                    logger.warn("Route {} {} is already registered, skipping", 
                            route.getMethod(), route.getPath());
                    skippedCount++;
                    continue;
                }
                
                RegisteredRoute registeredRoute = registerRoute(skillId, route, classLoader);
                skillRoutes.add(registeredRoute);
                logger.debug("Registered route: {} {} -> {}.{}",
                        route.getMethod(), route.getPath(),
                        route.getControllerClass(), route.getMethodName());
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
        // 加载Controller类
        Class<?> controllerClass = classLoader.loadClass(routeDef.getControllerClass());

        // 创建Controller实例
        Object controller = createControllerInstance(controllerClass);

        // 查找方法
        Method method = findMethod(controllerClass, routeDef.getMethodName(), routeDef.getParameterTypes(), classLoader);

        // 创建RequestMappingInfo
        RequestMappingInfo mappingInfo = createMappingInfo(routeDef);

        // 创建HandlerMethod
        org.springframework.web.method.HandlerMethod handlerMethod = 
                new org.springframework.web.method.HandlerMethod(controller, method);

        // 注册到Spring MVC
        handlerMapping.registerMapping(mappingInfo, controller, method);

        // 创建注册信息
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
        // 尝试使用默认构造器
        try {
            return controllerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            // 尝试使用Spring的依赖注入
            logger.debug("Creating controller with Spring dependency injection: {}", controllerClass.getName());
            // 这里可以扩展为使用ApplicationContext来创建
            throw new RuntimeException("Failed to create controller instance: " + controllerClass.getName(), e);
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
