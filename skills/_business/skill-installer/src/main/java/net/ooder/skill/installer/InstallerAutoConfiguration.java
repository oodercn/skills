package net.ooder.skill.installer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ooder.installer.enabled", havingValue = "true", matchIfMissing = true)
public class InstallerAutoConfiguration {
}