package net.ooder.skill.scene.config;

import net.ooder.skill.scene.config.sdk.SdkConfigStorage;
import net.ooder.skill.scene.config.sdk.JsonConfigStorageImpl;
import net.ooder.skill.scene.config.service.ConfigLoaderService;
import net.ooder.skill.scene.config.service.impl.ConfigLoaderServiceImpl;
import net.ooder.skill.scene.config.init.SystemConfigInitializer;
import net.ooder.skill.scene.config.install.SkillInstallConfigHandler;
import net.ooder.skill.scene.config.install.SceneInstallConfigHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigAutoConfiguration {

    @Value("${ooder.config.root:./config}")
    private String configRoot;

    @Value("${ooder.config.cache-ttl:300000}")
    private long cacheTtl;

    @Bean
    public SdkConfigStorage sdkConfigStorage() {
        return new JsonConfigStorageImpl(configRoot, cacheTtl);
    }
}
