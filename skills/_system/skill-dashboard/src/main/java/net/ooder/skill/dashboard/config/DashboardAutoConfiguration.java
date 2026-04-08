package net.ooder.skill.dashboard.config;

import net.ooder.skill.dashboard.service.*;
import net.ooder.skill.dashboard.service.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.dashboard")
@ConditionalOnProperty(name = "skill.dashboard.enabled", havingValue = "true", matchIfMissing = true)
public class DashboardAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DashboardService.class)
    public DashboardService dashboardService() {
        return new DashboardServiceImpl();
    }
}
