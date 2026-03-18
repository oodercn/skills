package net.ooder.skill.mqtt.discovery;

import net.ooder.skill.mqtt.config.MqttSceneConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/mqtt/discovery")
public class DiscoveryController {

    @Autowired
    private SkillDiscoveryService discoveryService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private MqttSceneConfig sceneConfig;

    @GetMapping("/skills")
    public Map<String, Object> listSkills() {
        Map<String, Object> result = new HashMap<>();
        List<SkillDiscoveryService.SkillInfo> skills = discoveryService.discoverSkills();
        result.put("skills", skills);
        result.put("count", skills.size());
        return result;
    }

    @GetMapping("/skills/{skillId}")
    public Map<String, Object> getSkill(@PathVariable String skillId) {
        Map<String, Object> result = new HashMap<>();
        SkillDiscoveryService.SkillInfo skill = discoveryService.getSkill(skillId);
        if (skill != null) {
            result.put("skill", skill);
            result.put("found", true);
        } else {
            result.put("found", false);
            result.put("error", "Skill not found: " + skillId);
        }
        return result;
    }

    @GetMapping("/scenes")
    public Map<String, Object> listScenes() {
        Map<String, Object> result = new HashMap<>();
        List<SkillDiscoveryService.SceneTemplate> scenes = discoveryService.discoverScenes();
        result.put("scenes", scenes);
        result.put("count", scenes.size());
        return result;
    }

    @GetMapping("/scenes/{sceneId}")
    public Map<String, Object> getScene(@PathVariable String sceneId) {
        Map<String, Object> result = new HashMap<>();
        SkillDiscoveryService.SceneTemplate scene = discoveryService.getSceneTemplate(sceneId);
        if (scene != null) {
            result.put("scene", scene);
            result.put("found", true);
        } else {
            result.put("found", false);
            result.put("error", "Scene not found: " + sceneId);
        }
        return result;
    }

    @GetMapping("/sys-config/{sceneType}")
    public Map<String, Object> getSysConfig(@PathVariable String sceneType) {
        return sysConfigService.getSysSceneConfig(sceneType);
    }

    @PostMapping("/sys-config/{sceneType}/apply")
    public Map<String, Object> applySysConfig(
            @PathVariable String sceneType,
            @RequestBody(required = false) Map<String, Object> overrides) {
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> sysConfig = sysConfigService.getSysSceneConfig(sceneType);
            if (overrides != null) {
                sysConfig.putAll(overrides);
            }
            sysConfigService.applySysConfig(sceneType, sysConfig);
            result.put("success", true);
            result.put("appliedConfig", sysConfig);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @PostMapping("/skills/{skillId}/install")
    public Map<String, Object> installSkill(
            @PathVariable String skillId,
            @RequestBody(required = false) Map<String, Object> options) {
        Map<String, Object> result = new HashMap<>();

        String targetDir = null;
        if (options != null && options.containsKey("targetDir")) {
            targetDir = (String) options.get("targetDir");
        } else {
            targetDir = System.getProperty("user.home") + "/.ooder/skills";
        }

        boolean installed = discoveryService.installSkill(skillId, targetDir);
        result.put("success", installed);
        result.put("skillId", skillId);
        result.put("targetDir", targetDir);

        return result;
    }

    @PostMapping("/scenes/{sceneType}/auto-install")
    public Map<String, Object> autoInstallForScene(@PathVariable String sceneType) {
        Map<String, Object> result = new HashMap<>();

        List<String> skills = sysConfigService.getRecommendedSkills(sceneType);
        List<String> installed = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (String skillId : skills) {
            String targetDir = System.getProperty("user.home") + "/.ooder/skills";
            if (discoveryService.installSkill(skillId, targetDir)) {
                installed.add(skillId);
            } else {
                failed.add(skillId);
            }
        }

        result.put("sceneType", sceneType);
        result.put("recommendedSkills", skills);
        result.put("installed", installed);
        result.put("failed", failed);
        result.put("success", failed.isEmpty());

        return result;
    }

    @GetMapping("/zero-config")
    public Map<String, Object> getZeroConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("defaults", sysConfigService.getZeroConfigDefaults());
        result.put("currentConfig", sceneConfig);
        return result;
    }

    @PostMapping("/zero-config/apply")
    public Map<String, Object> applyZeroConfig() {
        Map<String, Object> result = new HashMap<>();
        try {
            sysConfigService.applyZeroConfig();
            result.put("success", true);
            result.put("message", "Zero-config defaults applied");
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @GetMapping("/capabilities/{capability}/skills")
    public Map<String, Object> findSkillsByCapability(@PathVariable String capability) {
        Map<String, Object> result = new HashMap<>();
        List<SkillDiscoveryService.SkillInfo> skills = 
            discoveryService.findSkillsByCapability(capability);
        result.put("capability", capability);
        result.put("skills", skills);
        result.put("count", skills.size());
        return result;
    }

    @GetMapping("/scenes/{sceneId}/skills")
    public Map<String, Object> findSkillsByScene(@PathVariable String sceneId) {
        Map<String, Object> result = new HashMap<>();
        List<SkillDiscoveryService.SkillInfo> skills = 
            discoveryService.findSkillsByScene(sceneId);
        result.put("sceneId", sceneId);
        result.put("skills", skills);
        result.put("count", skills.size());
        return result;
    }

    @PostMapping("/cache/refresh")
    public Map<String, Object> refreshCache() {
        Map<String, Object> result = new HashMap<>();
        discoveryService.refreshCache();
        result.put("success", true);
        result.put("message", "Skill cache refreshed");
        return result;
    }
}
