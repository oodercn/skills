package net.ooder.skill.recording.qa.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.recording.qa")
@ConditionalOnProperty(name = "skill.recording.qa.enabled", havingValue = "true", matchIfMissing = true)
public class RecordingQaAutoConfiguration {
}
