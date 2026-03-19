package net.ooder.skill.mqtt.discovery;

import net.ooder.skill.mqtt.config.MqttSceneConfig;
import net.ooder.skill.mqtt.config.SceneConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysConfigService {

    private static final Logger log = LoggerFactory.getLogger(SysConfigService.class);

    @Autowired
    private SkillDiscoveryService discoveryService;

    @Autowired
    private SceneConfigService sceneConfigService;

    @Autowired
    private MqttSceneConfig mqttSceneConfig;

    public Map<String, Object> getSysSceneConfig(String sceneType) {
        log.info("Fetching SYS scene config for: {}", sceneType);

        SkillDiscoveryService.SceneTemplate template = discoveryService.getSceneTemplate(sceneType);
        if (template == null) {
            log.warn("Scene template not found: {}, using defaults", sceneType);
            return getDefaultSysConfig(sceneType);
        }

        Map<String, Object> sysConfig = new HashMap<>();
        sysConfig.put("sceneId", template.getSceneId());
        sysConfig.put("sceneName", template.getName());
        sysConfig.put("description", template.getDescription());
        sysConfig.put("requiredCapabilities", template.getRequiredCapabilities());
        sysConfig.put("recommendedSkills", template.getRecommendedSkills());
        sysConfig.put("maxMembers", template.getMaxMembers());

        Map<String, Object> mqttConfig = buildMqttConfigFromTemplate(template);
        sysConfig.put("mqttConfig", mqttConfig);

        return sysConfig;
    }

    private Map<String, Object> buildMqttConfigFromTemplate(
            SkillDiscoveryService.SceneTemplate template) {
        Map<String, Object> config = new HashMap<>();

        config.put("provider", "lightweight-mqtt");
        config.put("topicPrefix", "ooder");

        String sceneId = template.getSceneId();
        if (sceneId != null) {
            switch (sceneId) {
                case "mqtt-messaging":
                    config.put("maxConnections", 1000);
                    config.put("allowAnonymous", false);
                    config.put("provider", "lightweight-mqtt");
                    break;

                case "iot-device":
                    config.put("maxConnections", 10000);
                    config.put("allowAnonymous", true);
                    config.put("topicPrefix", "ooder");
                    break;

                case "auth":
                    config.put("maxConnections", 500);
                    config.put("allowAnonymous", false);
                    config.put("provider", "lightweight-mqtt");
                    break;

                case "ui-generation":
                    config.put("maxConnections", 100);
                    config.put("allowAnonymous", false);
                    break;

                default:
                    config.put("maxConnections", 1000);
                    config.put("allowAnonymous", false);
                    break;
            }
        }

        if (template.getDefaultConfig() != null) {
            config.putAll(template.getDefaultConfig());
        }

        return config;
    }

    private Map<String, Object> getDefaultSysConfig(String sceneType) {
        Map<String, Object> config = new HashMap<>();
        config.put("sceneId", sceneType);
        config.put("sceneName", sceneType);
        config.put("mqttConfig", getDefaultMqttConfig());
        return config;
    }

    private Map<String, Object> getDefaultMqttConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("provider", "lightweight-mqtt");
        config.put("port", 1883);
        config.put("websocketPort", 8083);
        config.put("maxConnections", 1000);
        config.put("allowAnonymous", false);
        config.put("topicPrefix", "ooder");
        return config;
    }

    public void applySysConfig(String sceneType, Map<String, Object> sysConfig) {
        log.info("Applying SYS config for scene: {}", sceneType);

        mqttSceneConfig.getScene().setSceneType(sceneType);
        mqttSceneConfig.getScene().setAutoConfigure(true);

        if (sysConfig.containsKey("mqttConfig")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mqttConfig = (Map<String, Object>) sysConfig.get("mqttConfig");
            sceneConfigService.configureFromSceneParams(mqttConfig);
        }

        if (sysConfig.containsKey("sceneId")) {
            mqttSceneConfig.getScene().setSceneId((String) sysConfig.get("sceneId"));
        }

        log.info("SYS config applied successfully");
    }

    public List<String> getRecommendedSkills(String sceneType) {
        SkillDiscoveryService.SceneTemplate template = discoveryService.getSceneTemplate(sceneType);
        if (template != null && template.getRecommendedSkills() != null) {
            return template.getRecommendedSkills();
        }

        List<String> defaultSkills = new ArrayList<>();
        defaultSkills.add("skill-mqtt");
        return defaultSkills;
    }

    public boolean autoInstallSkills(String sceneType) {
        List<String> skills = getRecommendedSkills(sceneType);
        boolean allSuccess = true;

        for (String skillId : skills) {
            if (!discoveryService.getSkill(skillId).equals(null)) {
                log.info("Skill already available: {}", skillId);
                continue;
            }

            String targetDir = System.getProperty("user.home") + "/.ooder/skills";
            boolean installed = discoveryService.installSkill(skillId, targetDir);
            if (!installed) {
                log.warn("Failed to auto-install skill: {}", skillId);
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    public Map<String, Object> getZeroConfigDefaults() {
        Map<String, Object> defaults = new HashMap<>();

        defaults.put("provider", "lightweight-mqtt");
        defaults.put("port", 1883);
        defaults.put("websocketPort", 8083);
        defaults.put("maxConnections", 1000);
        defaults.put("allowAnonymous", false);
        defaults.put("topicPrefix", "ooder");
        defaults.put("autoStart", true);

        return defaults;
    }

    public void applyZeroConfig() {
        Map<String, Object> defaults = getZeroConfigDefaults();
        sceneConfigService.configureFromSceneParams(defaults);
        log.info("Zero-config defaults applied");
    }
}
