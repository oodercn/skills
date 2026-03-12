package net.ooder.skill.common.config;

import net.ooder.skill.common.api.AuthApi;
import net.ooder.skill.common.api.ConfigApi;
import net.ooder.skill.common.api.OrgApi;
import net.ooder.skill.common.service.AuthService;
import net.ooder.skill.common.service.OrgService;
import net.ooder.skill.common.storage.JsonStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkillCommonAutoConfiguration {

    @Value("${app.storage.path:./data}")
    private String storagePath;

    @Bean
    @ConditionalOnMissingBean
    public JsonStorageService jsonStorageService() {
        JsonStorageService service = new JsonStorageService(storagePath);
        service.init();
        return service;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthService authService() {
        return new AuthService();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrgService orgService(JsonStorageService storage) {
        OrgService service = new OrgService(storage);
        service.init();
        return service;
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthApi authApi() {
        return new AuthApi();
    }

    @Bean
    @ConditionalOnMissingBean
    public OrgApi orgApi() {
        return new OrgApi();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigApi configApi() {
        return new ConfigApi();
    }
}
