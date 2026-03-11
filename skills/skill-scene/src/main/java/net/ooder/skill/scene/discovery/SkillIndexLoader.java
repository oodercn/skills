package net.ooder.skill.scene.discovery;

import net.ooder.scene.skill.model.SceneType;
import net.ooder.scene.skill.model.SkillForm;
import net.ooder.skill.scene.capability.service.MetadataCompat;
import net.ooder.skill.scene.dto.discovery.CapabilityDTO;
import net.ooder.skill.scene.dto.discovery.RepositoryDTO;
import net.ooder.skills.api.SkillPackageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Value("${ooder.mock.enabled:false}")
    private boolean mockEnabled;

    @Autowired(required = false)
    private SkillPackageManager skillPackageManager;

    private Map<String, Object> skillIndex;
    private List<Map<String, Object>> skills = new ArrayList<>();
    private List<Map<String, Object>> scenes = new ArrayList<>();
    private List<Map<String, Object>> categories = new ArrayList<>();
    
    private Set<String> mockInstalledSkills = new HashSet<>();

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
            
            if (is == null) {
                File repoRootPath = new File("../../skill-index.yaml");
                if (repoRootPath.exists()) {
                    is = new FileInputStream(repoRootPath);
                    log.info("[loadSkillIndex] Loading from repo root: {}", repoRootPath.getAbsolutePath());
                }
            }
            
            if (is == null) {
                File absolutePath = new File("e:/github/ooder-skills/skill-index.yaml");
                if (absolutePath.exists()) {
                    is = new FileInputStream(absolutePath);
                    log.info("[loadSkillIndex] Loading from absolute path: {}", absolutePath.getAbsolutePath());
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
        
        Set<String> sceneIds = new HashSet<>();
        for (Map<String, Object> scene : scenes) {
            String sceneId = (String) scene.get("sceneId");
            if (sceneId != null) {
                sceneIds.add(sceneId);
            }
        }
        
        for (Map<String, Object> skill : skills) {
            CapabilityDTO cap = new CapabilityDTO();
            String skillId = (String) skill.get("skillId");
            cap.setId(skillId);
            cap.setName((String) skill.get("name"));
            cap.setDescription((String) skill.get("description"));
            cap.setVersion((String) skill.get("version"));
            cap.setSource(source);
            
            boolean isInstalled = checkIfInstalled(skillId);
            cap.setStatus(isInstalled ? "installed" : "available");
            
            Object caps = skill.get("capabilities");
            if (caps instanceof List) {
                cap.setCapabilities((List<String>) caps);
            }
            
            Object typeObj = skill.get("type");
            String type = typeObj != null ? String.valueOf(typeObj) : null;
            
            boolean isScene = "SCENE".equals(type) || "scene".equals(type) || "scene-skill".equals(type);
            
            cap.setType(isScene ? "SCENE" : "SKILL");
            cap.setSceneCapability(isScene);
            
            if (isScene) {
                Object sceneTypeObj = skill.get("sceneType");
                String sceneTypeCode = sceneTypeObj != null ? String.valueOf(sceneTypeObj) : "MANUAL";
                
                Object skillFormObj = skill.get("skillForm");
                String skillFormCode = skillFormObj != null ? String.valueOf(skillFormObj) : "STANDALONE";
                
                boolean hasSelfDrive = "AUTO".equals(sceneTypeCode);
                
                cap.setSceneType(sceneTypeCode);
                cap.setSkillForm(skillFormCode);
                cap.setMainFirst(hasSelfDrive);
                
                Object visibilityObj = skill.get("visibility");
                if (visibilityObj == null) {
                    Object labelsObj = skill.get("labels");
                    if (labelsObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> labels = (Map<String, Object>) labelsObj;
                        visibilityObj = labels.get("scene.visibility");
                    }
                }
                cap.setVisibility(visibilityObj != null ? String.valueOf(visibilityObj) : 
                    MetadataCompat.getVisibility(skill));
                
                Object driverConditionsObj = skill.get("driverConditions");
                if (driverConditionsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> driverConditions = (List<Map<String, Object>>) driverConditionsObj;
                    cap.setDriverConditions(convertToMapList(driverConditions));
                }
                
                Object participantsObj = skill.get("participants");
                if (participantsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> participants = (List<Map<String, Object>>) participantsObj;
                    cap.setParticipants(convertToMapList(participants));
                }
            }
            
            capabilities.add(cap);
        }
        
        return capabilities;
    }
    
    private List<Map<String, Object>> convertToMapList(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : list) {
            result.add(new HashMap<>(item));
        }
        return result;
    }
    
    private boolean checkIfInstalled(String skillId) {
        if (skillId == null) {
            return false;
        }
        
        if (mockEnabled && mockInstalledSkills.contains(skillId)) {
            return true;
        }
        
        if (skillPackageManager == null) {
            return false;
        }
        try {
            return skillPackageManager.isInstalled(skillId).get();
        } catch (Exception e) {
            log.debug("[checkIfInstalled] Failed to check installation status for {}: {}", skillId, e.getMessage());
            return false;
        }
    }
    
    public void markAsInstalled(String skillId) {
        if (skillId != null) {
            mockInstalledSkills.add(skillId);
            log.info("[markAsInstalled] Marked {} as installed (mock mode)", skillId);
        }
    }
    
    public void markAsUninstalled(String skillId) {
        if (skillId != null) {
            mockInstalledSkills.remove(skillId);
            log.info("[markAsUninstalled] Marked {} as uninstalled (mock mode)", skillId);
        }
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
            
            Object sceneTypeObj = scene.get("sceneType");
            String sceneTypeCode = sceneTypeObj != null ? String.valueOf(sceneTypeObj) : "MANUAL";
            
            Object skillFormObj = scene.get("skillForm");
            String skillFormCode = skillFormObj != null ? String.valueOf(skillFormObj) : "STANDALONE";
            
            cap.setSceneType(sceneTypeCode);
            cap.setSkillForm(skillFormCode);
            cap.setMainFirst("AUTO".equals(sceneTypeCode));
            
            Object visibilityObj = scene.get("visibility");
            cap.setVisibility(visibilityObj != null ? String.valueOf(visibilityObj) : "public");
            
            Object driverConditionsObj = scene.get("driverConditions");
            if (driverConditionsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> driverConditions = (List<Map<String, Object>>) driverConditionsObj;
                cap.setDriverConditions(convertToMapList(driverConditions));
            }
            
            Object participantsObj = scene.get("participants");
            if (participantsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> participants = (List<Map<String, Object>>) participantsObj;
                cap.setParticipants(convertToMapList(participants));
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
    
    public Map<String, Object> getSkillInfo(String skillId) {
        if (skillId == null || skills == null) {
            return null;
        }
        
        for (Map<String, Object> skill : skills) {
            String id = (String) skill.get("skillId");
            if (skillId.equals(id)) {
                return skill;
            }
        }
        
        return null;
    }
    
    public String getDownloadUrl(String skillId) {
        Map<String, Object> skill = getSkillInfo(skillId);
        if (skill != null) {
            String giteeUrl = (String) skill.get("giteeDownloadUrl");
            String githubUrl = (String) skill.get("downloadUrl");
            return giteeUrl != null ? giteeUrl : githubUrl;
        }
        return null;
    }
}
