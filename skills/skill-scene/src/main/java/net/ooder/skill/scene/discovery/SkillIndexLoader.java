package net.ooder.skill.scene.discovery;

import net.ooder.skill.scene.dto.discovery.CapabilityDTO;
import net.ooder.skill.scene.dto.discovery.RepositoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

@Service
public class SkillIndexLoader {

    private static final Logger log = LoggerFactory.getLogger(SkillIndexLoader.class);

    @Value("${ooder.skill.index-path:skill-index.yaml}")
    private String skillIndexPath;

    private Map<String, Object> skillIndex;
    private List<Map<String, Object>> skills = new ArrayList<>();
    private List<Map<String, Object>> scenes = new ArrayList<>();
    private List<Map<String, Object>> categories = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadSkillIndex();
    }

    @SuppressWarnings("unchecked")
    private void loadSkillIndex() {
        log.info("[loadSkillIndex] Loading skill index from: {}", skillIndexPath);
        
        Yaml yaml = new Yaml();
        
        try {
            InputStream is = null;
            
            File file = new File(skillIndexPath);
            if (file.exists()) {
                is = new FileInputStream(file);
                log.info("[loadSkillIndex] Loading from file: {}", file.getAbsolutePath());
            } else {
                URL resource = getClass().getClassLoader().getResource(skillIndexPath);
                if (resource != null) {
                    is = resource.openStream();
                    log.info("[loadSkillIndex] Loading from classpath: {}", resource);
                }
            }
            
            if (is == null) {
                File defaultPath = new File("../skill-ui-test/skill-index.yaml");
                if (defaultPath.exists()) {
                    is = new FileInputStream(defaultPath);
                    log.info("[loadSkillIndex] Loading from default path: {}", defaultPath.getAbsolutePath());
                }
            }
            
            if (is != null) {
                skillIndex = yaml.load(is);
                is.close();
                
                if (skillIndex != null) {
                    Map<String, Object> spec = (Map<String, Object>) skillIndex.get("spec");
                    if (spec != null) {
                        skills = (List<Map<String, Object>>) spec.getOrDefault("skills", new ArrayList<>());
                        scenes = (List<Map<String, Object>>) spec.getOrDefault("scenes", new ArrayList<>());
                        categories = (List<Map<String, Object>>) spec.getOrDefault("categories", new ArrayList<>());
                    }
                }
                
                log.info("[loadSkillIndex] Loaded {} skills, {} scenes, {} categories", 
                    skills.size(), scenes.size(), categories.size());
            } else {
                log.warn("[loadSkillIndex] Skill index file not found: {}", skillIndexPath);
            }
        } catch (Exception e) {
            log.error("[loadSkillIndex] Failed to load skill index: {}", e.getMessage(), e);
        }
    }

    public List<CapabilityDTO> getSkillsFromIndex(String source) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        for (Map<String, Object> skill : skills) {
            CapabilityDTO cap = new CapabilityDTO();
            cap.setId((String) skill.get("skillId"));
            cap.setName((String) skill.get("name"));
            cap.setDescription((String) skill.get("description"));
            cap.setVersion((String) skill.get("version"));
            cap.setSource(source);
            cap.setStatus("available");
            
            Object caps = skill.get("capabilities");
            if (caps instanceof List) {
                cap.setCapabilities((List<String>) caps);
            }
            
            String skillId = (String) skill.get("skillId");
            Object typeObj = skill.get("type");
            String type = typeObj != null ? String.valueOf(typeObj) : null;
            
            boolean isScene = false;
            if ("SCENE".equals(type) || "scene".equals(type)) {
                isScene = true;
            } else if (skillId != null && 
                (skillId.contains("-scene") || skillId.endsWith("-scene") || 
                 "daily-log-scene".equals(skillId))) {
                isScene = true;
            }
            
            cap.setType(isScene ? "SCENE" : (type != null ? type : "SKILL"));
            cap.setSceneCapability(isScene);
            
            capabilities.add(cap);
        }
        
        return capabilities;
    }

    public List<RepositoryDTO> getRepositories(String source) {
        List<RepositoryDTO> repos = new ArrayList<>();
        
        for (Map<String, Object> skill : skills) {
            RepositoryDTO repo = new RepositoryDTO();
            repo.setFullName((String) skill.get("skillId"));
            repo.setName((String) skill.get("name"));
            repo.setDescription((String) skill.get("description"));
            repo.setHtmlUrl((String) skill.get("giteeDownloadUrl"));
            repo.setLatestVersion((String) skill.get("version"));
            repos.add(repo);
        }
        
        return repos;
    }

    public Map<String, Object> getSkillIndex() {
        return skillIndex;
    }

    public List<Map<String, Object>> getSkills() {
        return skills;
    }

    public List<Map<String, Object>> getScenes() {
        return scenes;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public List<CapabilityDTO> getScenesFromIndex(String source) {
        List<CapabilityDTO> capabilities = new ArrayList<>();
        
        for (Map<String, Object> scene : scenes) {
            CapabilityDTO cap = new CapabilityDTO();
            cap.setId((String) scene.get("sceneId"));
            cap.setName((String) scene.get("name"));
            cap.setDescription((String) scene.get("description"));
            cap.setVersion((String) scene.get("version"));
            cap.setSource(source);
            cap.setStatus("available");
            cap.setType("SCENE");
            cap.setSceneCapability(true);
            
            Object caps = scene.get("capabilities");
            if (caps instanceof List) {
                cap.setCapabilities((List<String>) caps);
            }
            
            capabilities.add(cap);
        }
        
        return capabilities;
    }

    public List<CapabilityDTO> getAllCapabilities(String source) {
        List<CapabilityDTO> all = new ArrayList<>();
        all.addAll(getSkillsFromIndex(source));
        all.addAll(getScenesFromIndex(source));
        return all;
    }
}
