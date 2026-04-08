package net.ooder.skill.history.config;

import net.ooder.skill.history.service.*;
import net.ooder.skill.history.service.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.history")
@ConditionalOnProperty(name = "skill.history.enabled", havingValue = "true", matchIfMissing = true)
public class HistoryAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(HistoryService.class)
    public HistoryService historyService() {
        return new HistoryServiceImpl();
    }
}
