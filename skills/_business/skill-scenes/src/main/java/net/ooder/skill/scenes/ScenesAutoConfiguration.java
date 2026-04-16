package net.ooder.skill.scenes;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.group.persistence.SceneGroupPersistence;
import net.ooder.skill.scenes.config.CustomSceneGroupPersistence;
import net.ooder.skill.scenes.service.SceneGroupService;
import net.ooder.skill.scenes.service.SceneService;
import net.ooder.skill.scenes.service.impl.SceneGroupServiceSEImpl;
import net.ooder.skill.scenes.service.impl.SceneServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.scenes.enabled", havingValue = "true", matchIfMissing = true)
public class ScenesAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ScenesAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public SceneService sceneService() {
        log.info("[sceneService] Creating SceneServiceImpl");
        return new SceneServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public SceneEventPublisher sceneEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        log.info("[sceneEventPublisher] Creating SceneEventPublisher");
        return new SceneEventPublisher(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public SceneGroupPersistence sceneGroupPersistence() {
        log.info("[sceneGroupPersistence] Creating CustomSceneGroupPersistence");
        return new CustomSceneGroupPersistence();
    }

    @Bean
    public SceneGroupService sceneGroupService(SceneGroupManager sceneGroupManager) {
        log.info("[sceneGroupService] Creating SceneGroupServiceSEImpl with injected SceneGroupManager");
        return new SceneGroupServiceSEImpl(sceneGroupManager, null, null, null);
    }
}
