package net.ooder.skill.key.config;

import net.ooder.skill.key.controller.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.key")
@ConditionalOnProperty(name = "skill.key.enabled", havingValue = "true", matchIfMissing = true)
public class KeyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(KeyManagementService.class)
    public KeyManagementService keyManagementService() {
        return new KeyManagementServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(KeyRuleService.class)
    public KeyRuleService keyRuleService() {
        return new KeyRuleServiceImpl();
    }
}
