package net.ooder.skill.selector;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.selector.enabled", havingValue = "true", matchIfMissing = true)
public class SelectorAutoConfiguration {
}