package net.ooder.skill.cli.model;

import java.time.LocalDateTime;
import java.util.Map;

public class SkillEntity {
    
    private String skillId;
    private String name;
    private String version;
    private String description;
    private String author;
    private SkillStatus status;
    private String category;
    private Map<String, Object> config;
    private Map<String, String> capabilities;
    private LocalDateTime installedAt;
    private LocalDateTime startedAt;
    private String source;
    
    public String getSkillId() {
        return skillId;
    }
    
    public void setSkillId(String skillId) {
        this.skillId = skillId;
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
    
    public SkillStatus getStatus() {
        return status;
    }
    
    public void setStatus(SkillStatus status) {
        this.status = status;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Map<String, Object> getConfig() {
        return config;
    }
    
    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
    
    public Map<String, String> getCapabilities() {
        return capabilities;
    }
    
    public void setCapabilities(Map<String, String> capabilities) {
        this.capabilities = capabilities;
    }
    
    public LocalDateTime getInstalledAt() {
        return installedAt;
    }
    
    public void setInstalledAt(LocalDateTime installedAt) {
        this.installedAt = installedAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
}
