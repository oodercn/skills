package net.ooder.skill.scheduler.quartz.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.scheduler.quartz")
@ConditionalOnProperty(name = "skill.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulerAutoConfiguration {
}
