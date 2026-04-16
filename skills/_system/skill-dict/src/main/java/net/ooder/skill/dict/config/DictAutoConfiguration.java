package net.ooder.skill.dict.config;

import net.ooder.skill.dict.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.dict")
@ConditionalOnProperty(name = "skill.dict.enabled", havingValue = "true", matchIfMissing = true)
public class DictAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DictService.class)
    public DictService dictService() {
        return new DictService();
    }
}
