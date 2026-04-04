package net.ooder.skill.dict.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.dict")
@ConditionalOnProperty(name = "skill.dict.enabled", havingValue = "true", matchIfMissing = true)
public class DictAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DictAutoConfiguration.class);

    public DictAutoConfiguration() {
        log.info("[DictAutoConfiguration] Initializing dict skill module");
    }
}
