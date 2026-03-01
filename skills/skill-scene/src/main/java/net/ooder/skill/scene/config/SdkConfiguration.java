package net.ooder.skill.scene.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PreDestroy;
import java.util.Optional;

@Configuration
public class SdkConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SdkConfiguration.class);

    @Value("${ooder.sdk.enabled:false}")
    private boolean sdkEnabled;

    @Value("${ooder.sdk.node-id:skill-scene}")
    private String nodeId;

    @Value("${ooder.sdk.skill-path:./skills}")
    private String skillPath;

    private Object sdk;

    @Bean
    @ConditionalOnProperty(name = "ooder.sdk.enabled", havingValue = "true")
    public Object ooderSDK() throws Exception {
        log.info("Initializing OoderSDK with agentId: {}", nodeId);

        try {
            Class<?> configClass = Class.forName("net.ooder.sdk.infra.config.SDKConfiguration");
            Object config = configClass.getDeclaredConstructor().newInstance();
            
            configClass.getMethod("setAgentId", String.class).invoke(config, nodeId);
            configClass.getMethod("setAgentName", String.class).invoke(config, "Skill Scene Service");
            configClass.getMethod("setAgentType", String.class).invoke(config, "SKILL");
            configClass.getMethod("setSkillRootPath", String.class).invoke(config, skillPath);

            Class<?> sdkClass = Class.forName("net.ooder.sdk.api.OoderSDK");
            Object builder = sdkClass.getMethod("builder").invoke(null);
            builder.getClass().getMethod("configuration", configClass).invoke(builder, config);
            sdk = builder.getClass().getMethod("build").invoke(builder);
            
            sdkClass.getMethod("initialize").invoke(sdk);
            sdkClass.getMethod("start").invoke(sdk);

            log.info("OoderSDK initialized and started successfully");
            return sdk;
        } catch (ClassNotFoundException e) {
            log.warn("OoderSDK not found in classpath, using memory implementation");
            return null;
        } catch (Exception e) {
            log.error("Failed to initialize OoderSDK: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    @ConditionalOnProperty(name = "ooder.sdk.enabled", havingValue = "true")
    public Object skillPackageManager(Object sdk) {
        if (sdk == null) return null;
        try {
            return sdk.getClass().getMethod("getSkillPackageManager").invoke(sdk);
        } catch (Exception e) {
            log.warn("Failed to get SkillPackageManager: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    @ConditionalOnProperty(name = "ooder.sdk.enabled", havingValue = "true")
    public Object sceneGroupManager(Object sdk) {
        if (sdk == null) return null;
        try {
            return sdk.getClass().getMethod("getSceneGroupManager").invoke(sdk);
        } catch (Exception e) {
            log.warn("Failed to get SceneGroupManager: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    @ConditionalOnProperty(name = "ooder.sdk.enabled", havingValue = "true")
    public Object capabilityInvoker(Object sdk) {
        if (sdk == null) return null;
        try {
            return sdk.getClass().getMethod("getCapabilityInvoker").invoke(sdk);
        } catch (Exception e) {
            log.warn("Failed to get CapabilityInvoker: {}", e.getMessage());
            return null;
        }
    }

    @PreDestroy
    public void shutdown() {
        if (sdk != null) {
            log.info("Shutting down OoderSDK");
            try {
                sdk.getClass().getMethod("shutdown").invoke(sdk);
            } catch (Exception e) {
                log.warn("Failed to shutdown OoderSDK: {}", e.getMessage());
            }
        }
    }

    public boolean isSdkAvailable() {
        return sdk != null;
    }
}
