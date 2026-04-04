package net.ooder.skill.context.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.context")
@ConditionalOnProperty(name = "skill.context.enabled", havingValue = "true", matchIfMissing = true)
public class ContextAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ContextAutoConfiguration.class);

    public ContextAutoConfiguration() {
        log.info("[ContextAutoConfiguration] Initializing context skill module");
    }
}
