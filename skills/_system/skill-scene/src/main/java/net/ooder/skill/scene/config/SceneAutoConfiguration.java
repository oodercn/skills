package net.ooder.skill.scene.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.scene")
@ConditionalOnProperty(name = "skill.scene.enabled", havingValue = "true", matchIfMissing = true)
public class SceneAutoConfiguration {

}
