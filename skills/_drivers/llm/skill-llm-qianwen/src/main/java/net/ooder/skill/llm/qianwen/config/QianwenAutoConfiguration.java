package net.ooder.skill.llm.qianwen.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.llm.qianwen")
@ConditionalOnProperty(name = "skill.llm.qianwen.enabled", havingValue = "true", matchIfMissing = true)
public class QianwenAutoConfiguration {
}
