package net.ooder.skill.selector.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.selector")
@ConditionalOnProperty(name = "skill.selector.enabled", havingValue = "true", matchIfMissing = true)
public class SelectorAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SelectorAutoConfiguration.class);

    public SelectorAutoConfiguration() {
        log.info("[SelectorAutoConfiguration] Initializing selector skill module");
    }
}
