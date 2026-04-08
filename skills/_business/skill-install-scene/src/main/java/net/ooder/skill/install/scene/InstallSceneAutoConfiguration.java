package net.ooder.skill.install.scene;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "ooder.install-scene.enabled", havingValue = "true", matchIfMissing = true)
public class InstallSceneAutoConfiguration {
}