package net.ooder.skill.hotplug.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Skill配置
 * 对应skill.yaml的完整配置
 */
public class SkillConfiguration {

    private String id;
    private String name;
    private String version;
    private String description;
    private String author;
    private String type;
    private List<String> dependencies;
    private Map<String, Object> config;

    // 生命周期配置
    private LifecycleConfiguration lifecycle;

    // 路由配置
    private List<RouteDefinition> routes;

    // 服务配置
    private List<ServiceDefinition> services;

    // UI配置
    private UIConfiguration ui;

    /**
     * 从YAML加载
     */
    @SuppressWarnings("unchecked")
    public static SkillConfiguration load(InputStream is, ClassLoader classLoader) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(is);

        SkillConfiguration config = new SkillConfiguration();
        config.id = (String) data.get("id");
        config.name = (String) data.get("name");
        config.version = (String) data.get("version");
        config.description = (String) data.get("description");
        config.author = (String) data.get("author");
        config.type = (String) data.get("type");
        config.dependencies = (List<String>) data.get("dependencies");
        config.config = (Map<String, Object>) data.get("config");

        // 解析生命周期配置
        Map<String, Object> lifecycleData = (Map<String, Object>) data.get("lifecycle");
        if (lifecycleData != null) {
            config.lifecycle = new LifecycleConfiguration();
            config.lifecycle.setStartup((String) lifecycleData.get("startup"));
            config.lifecycle.setShutdown((String) lifecycleData.get("shutdown"));
        }

        // 解析路由配置
        List<Map<String, Object>> routesData = (List<Map<String, Object>>) data.get("routes");
        if (routesData != null) {
            config.routes = RouteDefinition.fromList(routesData);
        }

        // 解析服务配置
        List<Map<String, Object>> servicesData = (List<Map<String, Object>>) data.get("services");
        if (servicesData != null) {
            config.services = ServiceDefinition.fromList(servicesData);
        }

        // 解析UI配置
        Map<String, Object> uiData = (Map<String, Object>) data.get("ui");
        if (uiData != null) {
            config.ui = UIConfiguration.fromMap(uiData);
        }

        return config;
    }

    /**
     * 合并配置（用于更新时保留用户配置）
     */
    public void merge(SkillConfiguration other) {
        if (other == null) return;

        // 保留用户自定义配置
        if (other.config != null) {
            if (this.config == null) {
                this.config = other.config;
            } else {
                this.config.putAll(other.config);
            }
        }

        // 可以添加更多合并逻辑
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public LifecycleConfiguration getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(LifecycleConfiguration lifecycle) {
        this.lifecycle = lifecycle;
    }

    public List<RouteDefinition> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteDefinition> routes) {
        this.routes = routes;
    }

    public List<ServiceDefinition> getServices() {
        return services;
    }

    public void setServices(List<ServiceDefinition> services) {
        this.services = services;
    }

    public UIConfiguration getUi() {
        return ui;
    }

    public void setUi(UIConfiguration ui) {
        this.ui = ui;
    }
}
