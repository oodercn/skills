package net.ooder.skill.im.wecom.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.im.wecom")
@ConditionalOnProperty(name = "skill.im.wecom.enabled", havingValue = "true", matchIfMissing = true)
public class WeComImAutoConfiguration {
}
