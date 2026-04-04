package net.ooder.skill.search.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.search")
@ConditionalOnProperty(name = "skill.search.enabled", havingValue = "true", matchIfMissing = true)
public class SearchAutoConfiguration {
}
