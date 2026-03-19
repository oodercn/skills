package net.ooder.skill.mqtt.config;

import net.ooder.skill.mqtt.server.MqttServerConfig;
import net.ooder.skill.mqtt.spec.MqttTopicSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SceneConfigService {

    private static final Logger log = LoggerFactory.getLogger(SceneConfigService.class);

    @Autowired
    private MqttSceneConfig sceneConfig;

    public MqttServerConfig buildServerConfig() {
        MqttSceneConfig.BrokerConfig broker = sceneConfig.getBroker();
        MqttSceneConfig.SceneConfig scene = sceneConfig.getScene();

        MqttServerConfig.Builder builder = MqttServerConfig.builder()
            .serverId(generateServerId())
            .port(broker.getPort())
            .websocketPort(broker.getWebsocketPort())
            .websocketEnabled(broker.isWebsocketEnabled())
            .maxConnections(broker.getMaxConnections())
            .allowAnonymous(broker.isAllowAnonymous())
            .sslEnabled(broker.isSslEnabled());

        if (broker.getUsername() != null && broker.getPassword() != null) {
            builder.auth(broker.getUsername(), broker.getPassword());
        }

        if (scene.isAutoConfigure() && scene.getSceneId() != null) {
            builder.extendedConfig(buildSceneExtendedConfig(scene));
        }

        return builder.build();
    }

    private String generateServerId() {
        MqttSceneConfig.SceneConfig scene = sceneConfig.getScene();
        if (scene.getSceneId() != null) {
            return "mqtt-" + scene.getSceneId();
        }
        return "mqtt-" + System.currentTimeMillis();
    }

    private Map<String, Object> buildSceneExtendedConfig(MqttSceneConfig.SceneConfig scene) {
        Map<String, Object> extended = new HashMap<>();
        extended.put("sceneId", scene.getSceneId());
        extended.put("sceneType", scene.getSceneType());
        extended.put("orgId", scene.getOrgId());
        extended.put("topicPrefix", scene.getTopicPrefix());
        extended.put("sceneParams", scene.getSceneParams());
        return extended;
    }

    public String buildSceneTopic(String resourceType, String action) {
        MqttSceneConfig.SceneConfig scene = sceneConfig.getScene();
        StringBuilder topic = new StringBuilder();

        if (scene.getOrgId() != null) {
            topic.append("org/").append(scene.getOrgId()).append("/");
        }

        if (scene.getSceneType() != null) {
            topic.append(scene.getSceneType()).append("/");
        }

        if (scene.getSceneId() != null) {
            topic.append(scene.getSceneId()).append("/");
        }

        topic.append(resourceType).append("/").append(action);

        return topic.toString();
    }

    public void applySceneDefaults(String sceneType) {
        MqttSceneConfig.SceneConfig scene = sceneConfig.getScene();
        MqttSceneConfig.BrokerConfig broker = sceneConfig.getBroker();

        if (sceneType == null) {
            sceneType = "default";
        }

        switch (sceneType.toLowerCase()) {
            case "mqtt-messaging":
                broker.setMaxConnections(1000);
                broker.setAllowAnonymous(false);
                scene.setTopicPrefix("ooder");
                log.info("Applied mqtt-messaging scene defaults");
                break;

            case "iot-device":
                broker.setMaxConnections(10000);
                broker.setAllowAnonymous(true);
                scene.setTopicPrefix("ooder");
                log.info("Applied iot-device scene defaults");
                break;

            case "team":
                broker.setMaxConnections(100);
                broker.setAllowAnonymous(false);
                scene.setTopicPrefix("ooder/team");
                log.info("Applied team scene defaults");
                break;

            case "enterprise":
                broker.setMaxConnections(5000);
                broker.setAllowAnonymous(false);
                broker.setSslEnabled(true);
                scene.setTopicPrefix("ooder/enterprise");
                log.info("Applied enterprise scene defaults");
                break;

            default:
                broker.setMaxConnections(1000);
                broker.setAllowAnonymous(false);
                scene.setTopicPrefix("ooder");
                log.info("Applied default scene defaults");
                break;
        }
    }

    public Map<String, Object> getSceneInfo() {
        Map<String, Object> info = new HashMap<>();
        MqttSceneConfig.SceneConfig scene = sceneConfig.getScene();

        info.put("sceneId", scene.getSceneId());
        info.put("sceneType", scene.getSceneType());
        info.put("orgId", scene.getOrgId());
        info.put("topicPrefix", scene.getTopicPrefix());
        info.put("autoConfigure", scene.isAutoConfigure());
        info.put("provider", sceneConfig.getProvider());

        return info;
    }

    public void configureFromSceneParams(Map<String, Object> params) {
        if (params == null) {
            return;
        }

        MqttSceneConfig.SceneConfig scene = sceneConfig.getScene();
        MqttSceneConfig.BrokerConfig broker = sceneConfig.getBroker();

        if (params.containsKey("provider")) {
            sceneConfig.setProvider((String) params.get("provider"));
        }

        if (params.containsKey("port")) {
            broker.setPort(((Number) params.get("port")).intValue());
        }

        if (params.containsKey("maxConnections")) {
            broker.setMaxConnections(((Number) params.get("maxConnections")).intValue());
        }

        if (params.containsKey("allowAnonymous")) {
            broker.setAllowAnonymous((Boolean) params.get("allowAnonymous"));
        }

        if (params.containsKey("topicPrefix")) {
            scene.setTopicPrefix((String) params.get("topicPrefix"));
        }

        scene.setSceneParams(params);
        log.info("Configured from scene params: {}", params.keySet());
    }
}
