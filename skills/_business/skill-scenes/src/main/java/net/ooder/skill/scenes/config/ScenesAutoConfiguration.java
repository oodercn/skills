package net.ooder.skill.scenes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.scenes")
@ConditionalOnProperty(name = "skill.scenes.enabled", havingValue = "true", matchIfMissing = true)
public class ScenesAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ScenesAutoConfiguration.class);

    public ScenesAutoConfiguration() {
        log.info("[ScenesAutoConfiguration] Initializing scenes skill module");
    }
}
