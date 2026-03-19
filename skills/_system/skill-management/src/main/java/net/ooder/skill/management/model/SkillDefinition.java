package net.ooder.skill.management.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SkillDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String skillId;
    private String name;
    private String description;
    private String version;
    private String category;
    private String type;
    private String author;
    private String icon;
    private List<String> tags;
    private List<String> capabilities;
    private List<String> scenes;
    private String jarPath;
    private String configPath;
    private String mainClass;
    private String endpoint;
    private String homepage;
    private String repository;
    private String license;
    private SkillStatus status;
    private SkillSource source;
    private Date installTime;
    private Date lastRunTime;
    private Date lastUpdated;
    private int runCount;
    private int downloadCount;
    private double rating;
    private int reviewCount;
    private Map<String, Object> config;
    private Map<String, Object> metadata;
    private String routeAgentId;
    private String endAgentId;
    private List<String> requiredCapabilities;
    private List<SkillDependencyInfo> dependencies;

    public enum SkillStatus {
        DISCOVERED,
        LOADING,
        LOADED,
        INITIALIZING,
        INITIALIZED,
        STARTING,
        RUNNING,
        IDLE,
        PAUSED,
        STOPPING,
        STOPPED,
        UNLOADING,
        UNLOADED,
        ERROR,
        INSTALLED,
        UPDATING
    }

    public enum SkillSource {
        SKILLCENTER,
        LOCAL,
        EXTERNAL,
        GIT,
        MARKETPLACE
    }

    public enum SkillType {
        TOOL_SKILL("tool-skill", "工具技能"),
        INFRASTRUCTURE_SKILL("infrastructure-skill", "基础设施技能"),
        ENTERPRISE_SKILL("enterprise-skill", "企业技能"),
        DRIVER_SKILL("driver-skill", "驱动技能"),
        APPLICATION_SKILL("application-skill", "应用技能");

        private final String code;
        private final String description;

        SkillType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static SkillType fromCode(String code) {
            for (SkillType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return TOOL_SKILL;
        }
    }

    public SkillDefinition() {
        this.status = SkillStatus.DISCOVERED;
        this.source = SkillSource.LOCAL;
        this.runCount = 0;
        this.downloadCount = 0;
        this.rating = 0.0;
        this.reviewCount = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }

    public List<String> getScenes() {
        return scenes;
    }

    public void setScenes(List<String> scenes) {
        this.scenes = scenes;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public SkillStatus getStatus() {
        return status;
    }

    public void setStatus(SkillStatus status) {
        this.status = status;
    }

    public SkillSource getSource() {
        return source;
    }

    public void setSource(SkillSource source) {
        this.source = source;
    }

    public Date getInstallTime() {
        return installTime;
    }

    public void setInstallTime(Date installTime) {
        this.installTime = installTime;
    }

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getRouteAgentId() {
        return routeAgentId;
    }

    public void setRouteAgentId(String routeAgentId) {
        this.routeAgentId = routeAgentId;
    }

    public String getEndAgentId() {
        return endAgentId;
    }

    public void setEndAgentId(String endAgentId) {
        this.endAgentId = endAgentId;
    }

    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }

    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }

    public List<SkillDependencyInfo> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<SkillDependencyInfo> dependencies) {
        this.dependencies = dependencies;
    }

    public void incrementRunCount() {
        this.runCount++;
        this.lastRunTime = new Date();
    }

    public boolean isRunning() {
        return SkillStatus.RUNNING.equals(status);
    }

    public boolean canRun() {
        return SkillStatus.INSTALLED.equals(status) 
            || SkillStatus.STOPPED.equals(status)
            || SkillStatus.INITIALIZED.equals(status);
    }

    public boolean isAvailable() {
        return status != null && 
               status != SkillStatus.ERROR && 
               status != SkillStatus.UNLOADED &&
               status != SkillStatus.LOADING &&
               status != SkillStatus.INITIALIZING;
    }
}
