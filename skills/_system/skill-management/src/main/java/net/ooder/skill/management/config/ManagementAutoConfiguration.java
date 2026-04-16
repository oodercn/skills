package net.ooder.skill.management.config;

import net.ooder.skill.management.*;
import net.ooder.skill.management.lifecycle.*;
import net.ooder.skill.management.market.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.management")
@ConditionalOnProperty(name = "skill.management.enabled", havingValue = "true", matchIfMissing = true)
public class ManagementAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SkillManager.class)
    public SkillManager skillManager() {
        return new SkillManager();
    }

    @Bean
    @ConditionalOnMissingBean(SkillLifecycleManager.class)
    public SkillLifecycleManager skillLifecycleManager() {
        return new SkillLifecycleManager();
    }

    @Bean
    @ConditionalOnMissingBean(SkillMarketManager.class)
    public SkillMarketManager skillMarketManager() {
        return new SkillMarketManager();
    }
}
