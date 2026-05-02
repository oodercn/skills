package net.ooder.vfs.event.config;

import net.ooder.sdk.api.event.EventBus;
import net.ooder.sdk.api.event.impl.EventBusImpl;
import net.ooder.vfs.event.VfsEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "net.ooder.sdk.api.event.EventBus")
public class VfsEventAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(VfsEventAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public EventBus vfsEventBus() {
        log.info("Creating VFS EventBus instance (EventBusImpl)");
        return new EventBusImpl();
    }

    @Bean
    public VfsEventPublisher vfsEventPublisher(EventBus eventBus) {
        log.info("Initializing VfsEventPublisher with agent-sdk EventBus: {}",
            eventBus.getClass().getSimpleName());
        return VfsEventPublisher.initialize(eventBus);
    }

    @Bean
    public VfsEventPublisher vfsEventPublisherFallback() {
        VfsEventPublisher existing = VfsEventPublisher.getInstance();
        if (existing == null) {
            log.info("Initializing VfsEventPublisher without EventBus (fallback)");
            return VfsEventPublisher.initialize(null);
        }
        return existing;
    }
}
