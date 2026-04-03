package net.ooder.skill.scenes;

import net.ooder.skill.scenes.service.SceneService;
import net.ooder.skill.scenes.service.impl.SceneServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.scenes.enabled", havingValue = "true", matchIfMissing = true)
public class ScenesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SceneService.class)
    public SceneService sceneService() {
        return new SceneServiceImpl();
    }
}