package net.ooder.skill.mqtt.discovery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SkillDiscoveryService {

    private static final Logger log = LoggerFactory.getLogger(SkillDiscoveryService.class);

    private static final String GITHUB_RAW = "https://raw.githubusercontent.com/ooderCN/skills/main/";
    private static final String GITEE_RAW = "https://gitee.com/ooderCN/skills/raw/main/";

    private final Map<String, SkillInfo> skillCache = new ConcurrentHashMap<>();
    private final Map<String, SceneTemplate> sceneTemplates = new ConcurrentHashMap<>();
    private long lastCacheUpdate = 0;
    private int cacheTimeout = 3600;

    public static class SkillInfo {
        private String skillId;
        private String name;
        private String version;
        private String description;
        private String sceneId;
        private String downloadUrl;
        private String giteeDownloadUrl;
        private List<String> capabilities;
        private Map<String, Object> config;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getDownloadUrl() { return downloadUrl; }
        public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
        public String getGiteeDownloadUrl() { return giteeDownloadUrl; }
        public void setGiteeDownloadUrl(String giteeDownloadUrl) { this.giteeDownloadUrl = giteeDownloadUrl; }
        public List<String> getCapabilities() { return capabilities; }
        public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }

    public static class SceneTemplate {
        private String sceneId;
        private String name;
        private String description;
        private List<String> requiredCapabilities;
        private List<String> recommendedSkills;
        private Map<String, Object> defaultConfig;
        private int maxMembers;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getRequiredCapabilities() { return requiredCapabilities; }
        public void setRequiredCapabilities(List<String> requiredCapabilities) { this.requiredCapabilities = requiredCapabilities; }
        public List<String> getRecommendedSkills() { return recommendedSkills; }
        public void setRecommendedSkills(List<String> recommendedSkills) { this.recommendedSkills = recommendedSkills; }
        public Map<String, Object> getDefaultConfig() { return defaultConfig; }
        public void setDefaultConfig(Map<String, Object> defaultConfig) { this.defaultConfig = defaultConfig; }
        public int getMaxMembers() { return maxMembers; }
        public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
    }

    public void refreshCache() {
        if (System.currentTimeMillis() - lastCacheUpdate < cacheTimeout * 1000) {
            return;
        }

        try {
            String indexContent = fetchSkillIndex();
            if (indexContent != null) {
                parseSkillIndex(indexContent);
                lastCacheUpdate = System.currentTimeMillis();
                log.info("Skill cache refreshed: {} skills, {} scenes", 
                    skillCache.size(), sceneTemplates.size());
            }
        } catch (Exception e) {
            log.error("Failed to refresh skill cache", e);
        }
    }

    private String fetchSkillIndex() {
        String[] urls = {
            GITHUB_RAW + "skill-index.yaml",
            GITEE_RAW + "skill-index.yaml"
        };

        for (String url : urls) {
            try {
                return fetchUrl(url);
            } catch (Exception e) {
                log.warn("Failed to fetch from {}: {}", url, e.getMessage());
            }
        }

        return null;
    }

    private String fetchUrl(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("Accept", "application/yaml, text/plain, */*");
        conn.setRequestProperty("User-Agent", "Ooder-Skill-Discovery/1.0");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP " + responseCode);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    private void parseSkillIndex(String yamlContent) {
        skillCache.clear();
        sceneTemplates.clear();

        String currentSection = null;
        SkillInfo currentSkill = null;
        SceneTemplate currentScene = null;

        for (String line : yamlContent.split("\n")) {
            String trimmed = line.trim();

            if (trimmed.startsWith("skills:")) {
                currentSection = "skills";
                continue;
            } else if (trimmed.startsWith("scenes:")) {
                currentSection = "scenes";
                continue;
            }

            if ("skills".equals(currentSection)) {
                if (trimmed.startsWith("- skillId:")) {
                    if (currentSkill != null) {
                        skillCache.put(currentSkill.getSkillId(), currentSkill);
                    }
                    currentSkill = new SkillInfo();
                    currentSkill.setCapabilities(new ArrayList<>());
                    currentSkill.setConfig(new HashMap<>());
                    currentSkill.setSkillId(trimmed.substring("- skillId:".length()).trim());
                } else if (currentSkill != null) {
                    parseSkillProperty(currentSkill, trimmed);
                }
            } else if ("scenes".equals(currentSection)) {
                if (trimmed.startsWith("- sceneId:")) {
                    if (currentScene != null) {
                        sceneTemplates.put(currentScene.getSceneId(), currentScene);
                    }
                    currentScene = new SceneTemplate();
                    currentScene.setRequiredCapabilities(new ArrayList<>());
                    currentScene.setRecommendedSkills(new ArrayList<>());
                    currentScene.setDefaultConfig(new HashMap<>());
                    currentScene.setSceneId(trimmed.substring("- sceneId:".length()).trim());
                } else if (currentScene != null) {
                    parseSceneProperty(currentScene, trimmed);
                }
            }
        }

        if (currentSkill != null) {
            skillCache.put(currentSkill.getSkillId(), currentSkill);
        }
        if (currentScene != null) {
            sceneTemplates.put(currentScene.getSceneId(), currentScene);
        }
    }

    private void parseSkillProperty(SkillInfo skill, String line) {
        if (line.startsWith("name:")) {
            skill.setName(line.substring("name:".length()).trim());
        } else if (line.startsWith("version:")) {
            skill.setVersion(line.substring("version:".length()).trim().replace("\"", ""));
        } else if (line.startsWith("description:")) {
            skill.setDescription(line.substring("description:".length()).trim());
        } else if (line.startsWith("sceneId:")) {
            skill.setSceneId(line.substring("sceneId:".length()).trim());
        } else if (line.startsWith("downloadUrl:")) {
            skill.setDownloadUrl(line.substring("downloadUrl:".length()).trim());
        } else if (line.startsWith("giteeDownloadUrl:")) {
            skill.setGiteeDownloadUrl(line.substring("giteeDownloadUrl:".length()).trim());
        }
    }

    private void parseSceneProperty(SceneTemplate scene, String line) {
        if (line.startsWith("name:")) {
            scene.setName(line.substring("name:".length()).trim());
        } else if (line.startsWith("description:")) {
            scene.setDescription(line.substring("description:".length()).trim());
        } else if (line.startsWith("maxMembers:")) {
            try {
                scene.setMaxMembers(Integer.parseInt(line.substring("maxMembers:".length()).trim()));
            } catch (NumberFormatException e) {
            }
        }
    }

    public List<SkillInfo> discoverSkills() {
        refreshCache();
        return new ArrayList<>(skillCache.values());
    }

    public SkillInfo getSkill(String skillId) {
        refreshCache();
        return skillCache.get(skillId);
    }

    public List<SceneTemplate> discoverScenes() {
        refreshCache();
        return new ArrayList<>(sceneTemplates.values());
    }

    public SceneTemplate getSceneTemplate(String sceneId) {
        refreshCache();
        return sceneTemplates.get(sceneId);
    }

    public List<SkillInfo> findSkillsByCapability(String capability) {
        refreshCache();
        List<SkillInfo> result = new ArrayList<>();
        for (SkillInfo skill : skillCache.values()) {
            if (skill.getCapabilities() != null && 
                skill.getCapabilities().contains(capability)) {
                result.add(skill);
            }
        }
        return result;
    }

    public List<SkillInfo> findSkillsByScene(String sceneId) {
        refreshCache();
        List<SkillInfo> result = new ArrayList<>();
        for (SkillInfo skill : skillCache.values()) {
            if (sceneId.equals(skill.getSceneId())) {
                result.add(skill);
            }
        }
        return result;
    }

    public Map<String, Object> getSysConfig(String sceneId) {
        SceneTemplate template = getSceneTemplate(sceneId);
        if (template == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> sysConfig = new HashMap<>();
        sysConfig.put("sceneId", template.getSceneId());
        sysConfig.put("sceneName", template.getName());
        sysConfig.put("description", template.getDescription());
        sysConfig.put("requiredCapabilities", template.getRequiredCapabilities());
        sysConfig.put("recommendedSkills", template.getRecommendedSkills());
        sysConfig.put("defaultConfig", template.getDefaultConfig());
        sysConfig.put("maxMembers", template.getMaxMembers());

        return sysConfig;
    }

    public boolean installSkill(String skillId, String targetDir) {
        SkillInfo skill = getSkill(skillId);
        if (skill == null) {
            log.error("Skill not found: {}", skillId);
            return false;
        }

        String downloadUrl = skill.getDownloadUrl();
        String giteeUrl = skill.getGiteeDownloadUrl();

        String[] urls = {downloadUrl, giteeUrl};
        for (String url : urls) {
            if (url == null || url.isEmpty()) {
                continue;
            }

            try {
                downloadAndInstall(url, skillId, skill.getVersion(), targetDir);
                log.info("Skill installed successfully: {} from {}", skillId, url);
                return true;
            } catch (Exception e) {
                log.warn("Failed to install from {}: {}", url, e.getMessage());
            }
        }

        log.error("Failed to install skill: {}", skillId);
        return false;
    }

    private void downloadAndInstall(String url, String skillId, String version, String targetDir) 
            throws IOException {
        Path targetPath = Paths.get(targetDir, "skills");
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        String fileName = skillId + "-" + version + ".jar";
        Path jarPath = targetPath.resolve(fileName);

        URL downloadUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) downloadUrl.openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(120000);

        try (InputStream is = conn.getInputStream();
             OutputStream os = Files.newOutputStream(jarPath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } finally {
            conn.disconnect();
        }

        log.info("Downloaded skill to: {}", jarPath);
    }

    public void setCacheTimeout(int timeout) {
        this.cacheTimeout = timeout;
    }
}
