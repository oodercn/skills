package net.ooder.skill.llm.volcengine.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.llm.volcengine")
@ConditionalOnProperty(name = "skill.llm.volcengine.enabled", havingValue = "true", matchIfMissing = true)
public class VolcEngineAutoConfiguration {
}
