package net.ooder.skill.res.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.res")
@ConditionalOnProperty(name = "skill.res.enabled", havingValue = "true", matchIfMissing = true)
public class ResAutoConfiguration {
}
