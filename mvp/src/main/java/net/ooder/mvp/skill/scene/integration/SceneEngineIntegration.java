package net.ooder.mvp.skill.scene.integration;

import net.ooder.mvp.skill.scene.dto.scene.*;
import net.ooder.mvp.skill.scene.service.SceneGroupService;
import net.ooder.mvp.skill.scene.service.SceneTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SceneEngineIntegration {

    private static final Logger log = LoggerFactory.getLogger(SceneEngineIntegration.class);

    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    @Autowired(required = false)
    @Lazy
    private Object skillPackageManager;

    @Autowired(required = false)
    @Lazy
    private Object capabilityRegistry;

    @Autowired(required = false)
    @Lazy
    private Object skillManager;

    @Autowired
    private SceneGroupService sceneGroupService;

    @Autowired
    private SceneTemplateService templateService;

    private final Map<String, Object> sdkCache = new ConcurrentHashMap<>();

    public boolean isSdkAvailable() {
        return skillPackageManager != null || capabilityRegistry != null;
    }

    public List<Map<String, Object>> discoverSkills() {
        List<Map<String, Object>> skills = new ArrayList<>();
        
        if (skillManager != null) {
            try {
                Method getAllMethod = skillManager.getClass().getMethod("getAllSkills");
                @SuppressWarnings("unchecked")
                List<?> skillList = (List<?>) getAllMethod.invoke(skillManager);
                
                for (Object skill : skillList) {
                    Map<String, Object> skillMap = convertToMap(skill);
                    if (skillMap != null) {
                        skills.add(skillMap);
                    }
                }
                log.info("[discoverSkills] Discovered {} skills from SDK", skills.size());
            } catch (NoSuchMethodException e) {
                log.warn("[discoverSkills] Method getAllSkills not found, trying discoverSkills");
                try {
                    Method discoverMethod = skillManager.getClass().getMethod("discoverSkills");
                    @SuppressWarnings("unchecked")
                    List<?> skillList = (List<?>) discoverMethod.invoke(skillManager);
                    
                    for (Object skill : skillList) {
                        Map<String, Object> skillMap = convertToMap(skill);
                        if (skillMap != null) {
                            skills.add(skillMap);
                        }
                    }
                    log.info("[discoverSkills] Discovered {} skills from SDK", skills.size());
                } catch (Exception ex) {
                    log.warn("[discoverSkills] Failed to discover skills: {}", ex.getMessage());
                }
            } catch (Exception e) {
                log.warn("[discoverSkills] Failed to discover skills: {}", e.getMessage());
            }
        }
        
        if (skills.isEmpty()) {
            skills = getDefaultSkills();
        }
        
        return skills;
    }

    public List<Map<String, Object>> discoverCapabilities() {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        if (capabilityRegistry != null) {
            try {
                Method getAllMethod = capabilityRegistry.getClass().getMethod("getAllCapabilities");
                @SuppressWarnings("unchecked")
                List<?> caps = (List<?>) getAllMethod.invoke(capabilityRegistry);
                
                for (Object cap : caps) {
                    Map<String, Object> capMap = convertToMap(cap);
                    if (capMap != null) {
                        capabilities.add(capMap);
                    }
                }
                log.info("[discoverCapabilities] Discovered {} capabilities from SDK", capabilities.size());
            } catch (NoSuchMethodException e) {
                log.warn("[discoverCapabilities] Method getAllCapabilities not found, trying discoverCapabilities");
                try {
                    Method discoverMethod = capabilityRegistry.getClass().getMethod("discoverCapabilities", String.class);
                    @SuppressWarnings("unchecked")
                    List<?> caps = (List<?>) discoverMethod.invoke(capabilityRegistry, "all");
                    
                    for (Object cap : caps) {
                        Map<String, Object> capMap = convertToMap(cap);
                        if (capMap != null) {
                            capabilities.add(capMap);
                        }
                    }
                    log.info("[discoverCapabilities] Discovered {} capabilities from SDK", capabilities.size());
                } catch (Exception ex) {
                    log.warn("[discoverCapabilities] Failed to discover capabilities: {}", ex.getMessage());
                }
            } catch (Exception e) {
                log.warn("[discoverCapabilities] Failed to discover capabilities: {}", e.getMessage());
            }
        }
        
        if (capabilities.isEmpty()) {
            capabilities = getDefaultCapabilities();
        }
        
        return capabilities;
    }

    public Object invokeCapability(String capabilityId, Map<String, Object> params) {
        if (capabilityRegistry != null) {
            try {
                Method invokeMethod = capabilityRegistry.getClass().getMethod("invokeCapability", String.class, Map.class);
                Object result = invokeMethod.invoke(capabilityRegistry, capabilityId, params);
                log.info("Invoked capability {} via SDK", capabilityId);
                return result;
            } catch (Exception e) {
                log.error("Failed to invoke capability {}: {}", capabilityId, e.getMessage());
            }
        }
        
        if (mockEnabled) {
            log.warn("SDK not available, returning mock result for capability: {} (mockEnabled=true)", capabilityId);
            return createMockResult(capabilityId, params);
        } else {
            log.error("SDK not available and mock is disabled for capability: {}", capabilityId);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "SDK not available and mock is disabled");
            errorResult.put("capabilityId", capabilityId);
            return errorResult;
        }
    }

    public String getProviderSkill(String capabilityId) {
        if (capabilityRegistry != null) {
            try {
                Method getProviderMethod = capabilityRegistry.getClass().getMethod("getProviderSkill", String.class);
                String skillId = (String) getProviderMethod.invoke(capabilityRegistry, capabilityId);
                return skillId;
            } catch (Exception e) {
                log.warn("Failed to get provider skill: {}", e.getMessage());
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertToMap(Object obj) {
        try {
            Map<String, Object> map = new HashMap<>();
            Class<?> clazz = obj.getClass();
            
            for (Method method : clazz.getMethods()) {
                if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                    String fieldName = method.getName().substring(3);
                    fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                    Object value = method.invoke(obj);
                    map.put(fieldName, value);
                }
            }
            return map;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> convertCapabilityToSkill(Object capability) {
        try {
            Map<String, Object> skill = convertToMap(capability);
            if (skill != null && skill.containsKey("id")) {
                String capId = (String) skill.get("id");
                skill.put("skillId", "skill-" + capId);
                skill.put("capabilities", Collections.singletonList(capId));
            }
            return skill;
        } catch (Exception e) {
            return null;
        }
    }

    private List<Map<String, Object>> getDefaultSkills() {
        List<Map<String, Object>> skills = new ArrayList<>();
        
        Map<String, Object> dailyReportSkill = new HashMap<>();
        dailyReportSkill.put("skillId", "skill-daily-report");
        dailyReportSkill.put("name", "日志汇报技能");
        dailyReportSkill.put("version", "1.0.0");
        dailyReportSkill.put("capabilities", Arrays.asList("report-remind", "report-submit", "report-aggregate", "report-analyze"));
        skills.add(dailyReportSkill);
        
        Map<String, Object> notificationSkill = new HashMap<>();
        notificationSkill.put("skillId", "skill-notification");
        notificationSkill.put("name", "通知技能");
        notificationSkill.put("version", "1.0.0");
        notificationSkill.put("capabilities", Arrays.asList("notification-email", "notification-sms"));
        skills.add(notificationSkill);
        
        return skills;
    }

    private List<Map<String, Object>> getDefaultCapabilities() {
        List<Map<String, Object>> capabilities = new ArrayList<>();
        
        String[] capIds = {"report-remind", "report-submit", "report-aggregate", "report-analyze", "notification-email", "notification-sms"};
        String[] capNames = {"日志提醒", "日志提交", "日志汇总", "日志分析", "邮件通知", "短信通知"};
        String[] capCategories = {"notification", "data-input", "data-processing", "intelligence", "notification", "notification"};
        String[] capDescs = {"提醒用户提交日志", "提交工作日志", "汇总团队日志", "分析日志数据", "发送邮件通知", "发送短信通知"};
        
        for (int i = 0; i < capIds.length; i++) {
            Map<String, Object> cap = new HashMap<>();
            cap.put("id", capIds[i]);
            cap.put("name", capNames[i]);
            cap.put("category", capCategories[i]);
            cap.put("description", capDescs[i]);
            cap.put("skillForm", "PROVIDER");
            cap.put("installed", false);
            cap.put("skillId", "skill-daily-report");
            cap.put("version", "1.0.0");
            cap.put("visibility", "public");
            cap.put("sceneCapability", true);
            capabilities.add(cap);
        }
        
        return capabilities;
    }

    private Object createMockResult(String capabilityId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("capabilityId", capabilityId);
        result.put("timestamp", System.currentTimeMillis());
        result.put("error", "SDK not available. Please configure SE SDK properly.");
        result.put("message", "Mock mode - capability invocation requires SE SDK");
        return result;
    }

    public void registerCapabilityBinding(String capabilityId, String skillId) {
        if (capabilityRegistry != null) {
            try {
                Method registerMethod = capabilityRegistry.getClass().getMethod("registerForSkill", String.class, String.class);
                registerMethod.invoke(capabilityRegistry, skillId, capabilityId);
                log.info("Registered capability {} for skill {}", capabilityId, skillId);
            } catch (Exception e) {
                log.warn("Failed to register capability binding: {}", e.getMessage());
            }
        }
    }
    
    public Object getSdkService(String serviceName) {
        try {
            if ("sceneMonitor".equals(serviceName)) {
                // 尝试从 Spring 上下文获取 SceneMonitor
                return getBeanFromContext("sceneMonitor");
            } else if ("llmProxyMonitor".equals(serviceName)) {
                return getBeanFromContext("llmProxyMonitor");
            } else if ("performanceMonitor".equals(serviceName)) {
                return getBeanFromContext("performanceMonitor");
            }
        } catch (Exception e) {
            log.debug("Failed to get SDK service {}: {}", serviceName, e.getMessage());
        }
        return null;
    }
    
    private Object getBeanFromContext(String beanName) {
        try {
            // 尝试从 skillManager 获取 Spring 上下文
            if (skillManager != null) {
                Method getApplicationContext = skillManager.getClass().getMethod("getApplicationContext");
                Object context = getApplicationContext.invoke(skillManager);
                if (context != null) {
                    Method getBean = context.getClass().getMethod("getBean", String.class);
                    return getBean.invoke(context, beanName);
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get bean {} from context: {}", beanName, e.getMessage());
        }
        return null;
    }
}
