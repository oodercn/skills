package net.ooder.skill.task.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.task")
@ConditionalOnProperty(name = "skill.task.enabled", havingValue = "true", matchIfMissing = true)
public class TaskAutoConfiguration {
}
