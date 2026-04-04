package net.ooder.skill.llm.deepseek.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.llm.deepseek")
@ConditionalOnProperty(name = "skill.llm.deepseek.enabled", havingValue = "true", matchIfMissing = true)
public class DeepSeekAutoConfiguration {
}
