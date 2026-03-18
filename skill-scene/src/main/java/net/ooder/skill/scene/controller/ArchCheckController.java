package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/arch-check")
public class ArchCheckController {

    private static final Logger log = LoggerFactory.getLogger(ArchCheckController.class);

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping("/controllers")
    public ResultModel<List<Map<String, Object>>> checkControllers() {
        log.info("[checkControllers] Starting architecture check");
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        RequestMappingHandlerMapping handlerMapping = applicationContext
            .getBean(RequestMappingHandlerMapping.class);
        
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        
        Map<String, List<Map<String, Object>>> controllerMethods = new LinkedHashMap<>();
        
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Method method = handlerMethod.getMethod();
            Class<?> controllerClass = handlerMethod.getBeanType();
            
            String controllerName = controllerClass.getSimpleName();
            
            if (!controllerName.contains("Controller")) {
                continue;
            }
            
            if (controllerName.startsWith("org.springframework") || 
                controllerName.startsWith("org.springframework.boot")) {
                continue;
            }
            
            Map<String, Object> methodInfo = checkMethod(method);
            
            controllerMethods.computeIfAbsent(controllerName, k -> new ArrayList<>())
                .add(methodInfo);
        }
        
        for (Map.Entry<String, List<Map<String, Object>>> entry : controllerMethods.entrySet()) {
            String controllerName = entry.getKey();
            List<Map<String, Object>> methods = entry.getValue();
            
            boolean hasIssues = methods.stream()
                .anyMatch(m -> "fail".equals(m.get("status")));
            
            long failedCount = methods.stream()
                .filter(m -> "fail".equals(m.get("status")))
                .count();
            
            Map<String, Object> controllerResult = new LinkedHashMap<>();
            controllerResult.put("controller", controllerName);
            controllerResult.put("hasIssues", hasIssues);
            controllerResult.put("message", hasIssues ? 
                String.format("发现 %d 个问题", failedCount) : "符合规范");
            controllerResult.put("issues", hasIssues ? methods : null);
            
            results.add(controllerResult);
        }
        
        log.info("[checkControllers] Checked {} controllers", results.size());
        return ResultModel.success(results);
    }

    private Map<String, Object> checkMethod(Method method) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        String methodName = method.getName();
        result.put("method", methodName);
        
        List<String> issues = new ArrayList<>();
        
        boolean hasResponseBody = method.isAnnotationPresent(ResponseBody.class) ||
            method.getDeclaringClass().isAnnotationPresent(RestController.class);
        
        if (!hasResponseBody) {
            issues.add("缺少 @ResponseBody 或 @RestController");
        }
        
        Parameter[] parameters = method.getParameters();
        for (Parameter param : parameters) {
            String paramType = param.getType().getSimpleName();
            
            if (paramType.equals("Map") || 
                paramType.equals("HashMap") ||
                param.getType().getName().contains("Map")) {
                
                boolean hasRequestParam = param.isAnnotationPresent(RequestParam.class);
                boolean hasRequestBody = param.isAnnotationPresent(RequestBody.class);
                boolean hasPathVariable = param.isAnnotationPresent(PathVariable.class);
                
                if (!hasRequestParam && !hasRequestBody && !hasPathVariable) {
                    issues.add("参数 " + param.getName() + " 使用 Map 类型，建议使用 DTO");
                }
            }
        }
        
        if (issues.isEmpty()) {
            result.put("status", "pass");
            result.put("message", "符合规范");
        } else {
            result.put("status", "fail");
            result.put("message", String.join("; ", issues));
        }
        
        return result;
    }

    @GetMapping("/rules")
    public ResultModel<Map<String, Object>> getRules() {
        Map<String, Object> rules = new LinkedHashMap<>();
        
        List<Map<String, String>> frontendRules = Arrays.asList(
            createRule("脚本引用", "必须包含 nexus.js, menu.js, page-init.js, api.js"),
            createRule("页面结构", "必须使用 nx-page, nx-page__sidebar, nx-page__content"),
            createRule("图标系统", "仅使用 Remix Icon (ri-*)"),
            createRule("CSS变量", "使用 --ns-*, --nx-* 变量"),
            createRule("模态框", "使用 classList toggle"),
            createRule("API响应", "检查 status === 'success'")
        );
        
        List<Map<String, String>> backendRules = Arrays.asList(
            createRule("简单参数", "使用 @RequestParam"),
            createRule("复杂对象", "使用 @RequestBody DTO"),
            createRule("禁止Map参数", "Controller 方法禁止使用 Map 作为参数"),
            createRule("响应注解", "必须有 @ResponseBody 或 @RestController"),
            createRule("响应格式", "统一使用 ResultModel")
        );
        
        rules.put("frontend", frontendRules);
        rules.put("backend", backendRules);
        
        return ResultModel.success(rules);
    }

    private Map<String, String> createRule(String name, String description) {
        Map<String, String> rule = new LinkedHashMap<>();
        rule.put("name", name);
        rule.put("description", description);
        return rule;
    }
}
