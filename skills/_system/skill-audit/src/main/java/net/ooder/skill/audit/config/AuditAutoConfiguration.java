package net.ooder.skill.audit.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.audit")
@ConditionalOnProperty(name = "skill.audit.enabled", havingValue = "true", matchIfMissing = true)
public class AuditAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AuditAutoConfiguration.class);

    public AuditAutoConfiguration() {
        log.info("[AuditAutoConfiguration] Initializing audit skill module");
    }
}
