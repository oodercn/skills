package net.ooder.skill.scene.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

@Component
public class ModuleApiRegistry {

    private static final Logger log = LoggerFactory.getLogger(ModuleApiRegistry.class);

    private final Map<String, ModuleApis> modules = new ConcurrentHashMap<>();
    private String currentModule;

    @PostConstruct
    public void init() {
        registerDiscoveryApis();
        registerInstallApis();
        registerActivationApis();
        log.info("[ModuleApiRegistry] Initialized {} modules", modules.size());
    }

    private void registerDiscoveryApis() {
        ModuleApis discovery = new ModuleApis("discovery", "能力发现模块");

        discovery.api("selectCapability")
            .description("选择一个能�?)
            .param("capabilityId", "string", "能力ID", true)
            .param("capabilityName", "string", "能力名称", false);

        discovery.api("startScan")
            .description("开始扫描能�?)
            .param("method", "string", "发现方式(LOCAL_FS/GITHUB/GITEE/AUTO)", true)
            .param("forceRefresh", "boolean", "是否强制刷新", false);

        discovery.api("filterCapabilities")
            .description("筛选能�?)
            .param("type", "string", "能力类型(SCENE/SKILL/COMMUNICATION/AI)", false)
            .param("keyword", "string", "关键�?, false)
            .param("installed", "boolean", "是否已安�?, false);

        discovery.api("getCapabilityDetail")
            .description("获取能力详情")
            .param("capabilityId", "string", "能力ID", true);

        discovery.api("refreshScan")
            .description("刷新扫描结果")
            .param("method", "string", "发现方式", false);

        discovery.api("getDriverConditions")
            .description("获取驱动条件")
            .param("capabilityId", "string", "能力ID", true);

        modules.put("discovery", discovery);
        log.debug("[ModuleApiRegistry] Registered discovery module with {} APIs", discovery.getApiNames().size());
    }

    private void registerInstallApis() {
        ModuleApis install = new ModuleApis("install", "安装向导模块");

        install.api("startInstall")
            .description("开始安�?)
            .param("capabilityId", "string", "能力ID", true)
            .param("capabilityName", "string", "能力名称", false)
            .param("config", "object", "安装配置", false);

        install.api("setConfig")
            .description("设置配置�?)
            .param("key", "string", "配置�?, true)
            .param("value", "any", "配置�?, true);

        install.api("nextStep")
            .description("下一�?);

        install.api("prevStep")
            .description("上一�?);

        install.api("confirm")
            .description("确认安装");

        install.api("cancel")
            .description("取消安装");

        install.api("checkDependencies")
            .description("检查依�?)
            .param("capabilityId", "string", "能力ID", true);

        modules.put("install", install);
        log.debug("[ModuleApiRegistry] Registered install module with {} APIs", install.getApiNames().size());
    }

    private void registerActivationApis() {
        ModuleApis activation = new ModuleApis("activation", "激活流程模�?);

        activation.api("executeStep")
            .description("执行激活步�?)
            .param("stepId", "string", "步骤ID", true)
            .param("data", "object", "步骤数据", false);

        activation.api("skipStep")
            .description("跳过步骤")
            .param("stepId", "string", "步骤ID", true);

        activation.api("selectDriverCondition")
            .description("选择驱动条件")
            .param("conditionId", "string", "条件ID", true);

        activation.api("setParticipants")
            .description("设置参与�?)
            .param("participants", "array", "参与者列�?, true);

        activation.api("configurePrivateCapabilities")
            .description("配置私有能力")
            .param("enabledCapabilityIds", "array", "启用的能力ID列表", true);

        activation.api("getKey")
            .description("获取安全密钥");

        activation.api("complete")
            .description("完成激�?);

        activation.api("cancel")
            .description("取消激�?);

        modules.put("activation", activation);
        log.debug("[ModuleApiRegistry] Registered activation module with {} APIs", activation.getApiNames().size());
    }

    public void setCurrentModule(String module) {
        if (modules.containsKey(module)) {
            this.currentModule = module;
            log.info("[ModuleApiRegistry] Current module set to: {}", module);
        } else {
            log.warn("[ModuleApiRegistry] Unknown module: {}", module);
        }
    }

    public String getCurrentModule() {
        return currentModule;
    }

    public Set<String> getAvailableApis(String module) {
        ModuleApis moduleApis = modules.get(module);
        return moduleApis != null ? moduleApis.getApiNames() : Collections.emptySet();
    }

    public Set<String> getCurrentAvailableApis() {
        return getAvailableApis(currentModule);
    }

    public ApiDefinition getApiDefinition(String module, String apiName) {
        ModuleApis moduleApis = modules.get(module);
        return moduleApis != null ? moduleApis.getApi(apiName) : null;
    }

    public Map<String, Object> getModuleInfo(String module) {
        ModuleApis moduleApis = modules.get(module);
        if (moduleApis == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> info = new HashMap<>();
        info.put("name", moduleApis.getName());
        info.put("description", moduleApis.getDescription());
        info.put("apis", moduleApis.getApiDefinitions());
        return info;
    }

    public List<Map<String, Object>> getAllModulesInfo() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String moduleName : modules.keySet()) {
            result.add(getModuleInfo(moduleName));
        }
        return result;
    }

    public boolean validateApiCall(String module, String apiName) {
        ModuleApis moduleApis = modules.get(module);
        if (moduleApis == null) {
            return false;
        }
        return moduleApis.hasApi(apiName);
    }

    public boolean validateCurrentApiCall(String apiName) {
        return validateApiCall(currentModule, apiName);
    }

    public static class ModuleApis {
        private final String name;
        private final String description;
        private final Map<String, ApiDefinition> apis = new LinkedHashMap<>();

        public ModuleApis(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public ApiDefinition api(String name) {
            ApiDefinition api = new ApiDefinition(name);
            apis.put(name, api);
            return api;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public Set<String> getApiNames() { return apis.keySet(); }
        public ApiDefinition getApi(String name) { return apis.get(name); }
        public boolean hasApi(String name) { return apis.containsKey(name); }

        public List<Map<String, Object>> getApiDefinitions() {
            List<Map<String, Object>> result = new ArrayList<>();
            for (ApiDefinition api : apis.values()) {
                result.add(api.toMap());
            }
            return result;
        }
    }

    public static class ApiDefinition {
        private final String name;
        private String description;
        private final Map<String, ParamDefinition> parameters = new LinkedHashMap<>();

        public ApiDefinition(String name) {
            this.name = name;
        }

        public ApiDefinition description(String description) {
            this.description = description;
            return this;
        }

        public ApiDefinition param(String name, String type, String description, boolean required) {
            parameters.put(name, new ParamDefinition(name, type, description, required));
            return this;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public Map<String, ParamDefinition> getParameters() { return parameters; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("description", description);

            List<Map<String, Object>> params = new ArrayList<>();
            for (ParamDefinition param : parameters.values()) {
                params.add(param.toMap());
            }
            map.put("parameters", params);

            return map;
        }
    }

    public static class ParamDefinition {
        private final String name;
        private final String type;
        private final String description;
        private final boolean required;

        public ParamDefinition(String name, String type, String description, boolean required) {
            this.name = name;
            this.type = type;
            this.description = description;
            this.required = required;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public boolean isRequired() { return required; }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("type", type);
            map.put("description", description);
            map.put("required", required);
            return map;
        }
    }
}
