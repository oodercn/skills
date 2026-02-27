package net.ooder.skill.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillIndex {
    
    private String name;
    private String version;
    private String description;
    private Spec spec;
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Spec {
        private List<Category> categories = new ArrayList<>();
        private List<SkillEntry> skills = new ArrayList<>();
        private List<SceneEntry> scenes = new ArrayList<>();
        
        public List<Category> getCategories() { return categories; }
        public void setCategories(List<Category> categories) { this.categories = categories; }
        public List<SkillEntry> getSkills() { return skills; }
        public void setSkills(List<SkillEntry> skills) { this.skills = skills; }
        public List<SceneEntry> getScenes() { return scenes; }
        public void setScenes(List<SceneEntry> scenes) { this.scenes = scenes; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {
        private String id;
        private String name;
        private String nameEn;
        private String description;
        private String icon;
        private int order;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getNameEn() { return nameEn; }
        public void setNameEn(String nameEn) { this.nameEn = nameEn; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SkillEntry {
        private String skillId;
        private String name;
        private String version;
        private String category;
        private String subCategory;
        private List<String> tags;
        private String description;
        private String sceneId;
        private String path;
        private String downloadUrl;
        private String giteeDownloadUrl;
        private String checksum;
        
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getSubCategory() { return subCategory; }
        public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public String getGiteeDownloadUrl() { return giteeDownloadUrl; }
        public void setGiteeDownloadUrl(String giteeDownloadUrl) { this.giteeDownloadUrl = giteeDownloadUrl; }
        public String getChecksum() { return checksum; }
        public void setChecksum(String checksum) { this.checksum = checksum; }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SceneEntry {
        private String sceneId;
        private String name;
        private String description;
        private String version;
        private String category;
        private List<String> requiredCapabilities;
        private int maxMembers;
        
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public List<String> getRequiredCapabilities() { return requiredCapabilities; }
        public void setRequiredCapabilities(List<String> requiredCapabilities) { this.requiredCapabilities = requiredCapabilities; }
        public int getMaxMembers() { return maxMembers; }
        public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Spec getSpec() { return spec; }
    public void setSpec(Spec spec) { this.spec = spec; }
    
    public List<Category> getCategories() { 
        return spec != null ? spec.getCategories() : new ArrayList<>(); 
    }
    
    public List<SkillEntry> getSkills() { 
        return spec != null ? spec.getSkills() : new ArrayList<>(); 
    }
    
    public List<SceneEntry> getScenes() { 
        return spec != null ? spec.getScenes() : new ArrayList<>(); 
    }
}
