package net.ooder.skill.calendar.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.calendar")
@ConditionalOnProperty(name = "skill.calendar.enabled", havingValue = "true", matchIfMissing = true)
public class CalendarAutoConfiguration {
}
