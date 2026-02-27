package net.ooder.skill.hotplug.model;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Skill元数据
 * 对应skill.yaml中的配置
 */
public class SkillMetadata {

    private String id;
    private String name;
    private String version;
    private String description;
    private String author;
    private String type;
    private List<String> dependencies;
    private Map<String, Object> config;
    private Map<String, Object> ui;

    /**
     * 从YAML加载
     */
    @SuppressWarnings("unchecked")
    public static SkillMetadata loadFromYaml(InputStream is) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(is);

        SkillMetadata metadata = new SkillMetadata();
        
        Map<String, Object> metaData = (Map<String, Object>) data.get("metadata");
        if (metaData != null) {
            metadata.id = (String) metaData.get("id");
            metadata.name = (String) metaData.get("name");
            metadata.version = (String) metaData.get("version");
            metadata.description = (String) metaData.get("description");
            metadata.author = (String) metaData.get("author");
            metadata.type = (String) metaData.get("type");
            metadata.dependencies = (List<String>) metaData.get("dependencies");
        }
        
        Map<String, Object> specData = (Map<String, Object>) data.get("spec");
        if (specData != null) {
            if (metadata.type == null) {
                metadata.type = (String) specData.get("type");
            }
            metadata.config = (Map<String, Object>) specData.get("config");
            
            Map<String, Object> uiData = new java.util.HashMap<>();
            if (specData.containsKey("nexusUi")) {
                uiData.put("nexusUi", specData.get("nexusUi"));
            }
            if (!uiData.isEmpty()) {
                metadata.ui = uiData;
            }
        }
        
        if (metadata.config == null) {
            metadata.config = (Map<String, Object>) data.get("config");
        }

        return metadata;
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

    public Map<String, Object> getUi() {
        return ui;
    }

    public void setUi(Map<String, Object> ui) {
        this.ui = ui;
    }

    @Override
    public String toString() {
        return "SkillMetadata{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
