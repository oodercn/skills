package net.ooder.skill.keys;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.keys.enabled", havingValue = "true", matchIfMissing = true)
public class KeysAutoConfiguration {
}