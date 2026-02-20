package net.ooder.skill.mqtt.autoconfigure;

import net.ooder.skill.mqtt.config.MqttSceneConfig;
import net.ooder.skill.mqtt.config.SceneConfigService;
import net.ooder.skill.mqtt.discovery.SysConfigService;
import net.ooder.skill.mqtt.server.MqttServer;
import net.ooder.skill.mqtt.server.MqttServerConfig;
import net.ooder.skill.mqtt.provider.MqttProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@EnableConfigurationProperties(MqttSceneConfig.class)
@ConditionalOnProperty(prefix = "mqtt", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MqttAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MqttAutoConfiguration.class);

    @Autowired
    private MqttSceneConfig sceneConfig;

    @Autowired
    private SceneConfigService sceneConfigService;

    @Autowired
    private SysConfigService sysConfigService;

    private MqttServer mqttServer;

    @PostConstruct
    public void init() {
        log.info("Initializing MQTT Skill with zero-config support");

        if (isZeroConfigMode()) {
            applyZeroConfig();
        }

        if (sceneConfig.getDiscovery().isEnabled()) {
            applySceneDiscovery();
        }
    }

    private boolean isZeroConfigMode() {
        return sceneConfig.getScene().getSceneId() == null &&
               sceneConfig.getProvider() == null;
    }

    private void applyZeroConfig() {
        log.info("Zero-config mode detected, applying defaults");
        sysConfigService.applyZeroConfig();
    }

    private void applySceneDiscovery() {
        String sceneType = sceneConfig.getScene().getSceneType();
        if (sceneType != null) {
            log.info("Scene discovery enabled for: {}", sceneType);
            var sysConfig = sysConfigService.getSysSceneConfig(sceneType);
            sysConfigService.applySysConfig(sceneType, sysConfig);

            if (sceneConfig.getDiscovery().isAutoInstall()) {
                sysConfigService.autoInstallSkills(sceneType);
            }
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttProviderFactory mqttProviderFactory() {
        MqttProviderFactory factory = MqttProviderFactory.getInstance();
        factory.initialize();
        log.info("MQTT Provider Factory initialized with {} providers", 
            factory.getProviderCount());
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttServer mqttServer(MqttProviderFactory factory) {
        String providerId = sceneConfig.getProvider();
        MqttServerConfig config = sceneConfigService.buildServerConfig();

        log.info("Creating MQTT Server with provider: {}", providerId);
        mqttServer = factory.createServer(providerId, config);

        return mqttServer;
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttServerConfig mqttServerConfig() {
        return sceneConfigService.buildServerConfig();
    }

    @PreDestroy
    public void destroy() {
        if (mqttServer != null && mqttServer.isRunning()) {
            try {
                mqttServer.stop();
                log.info("MQTT Server stopped");
            } catch (Exception e) {
                log.error("Failed to stop MQTT Server", e);
            }
        }
    }
}
