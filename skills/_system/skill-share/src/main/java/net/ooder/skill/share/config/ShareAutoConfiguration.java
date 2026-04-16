package net.ooder.skill.share.config;

import net.ooder.scene.todo.TodoService;
import net.ooder.skill.share.service.CapabilityShareService;
import net.ooder.skill.share.service.impl.CapabilityShareServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.share")
@ConditionalOnProperty(name = "skill.share.enabled", havingValue = "true", matchIfMissing = true)
public class ShareAutoConfiguration {
    
    private static final Logger log = LoggerFactory.getLogger(ShareAutoConfiguration.class);
    
    @Value("${user.dir:./}")
    private String baseDir;
    
    @Autowired(required = false)
    private TodoService todoService;

    public ShareAutoConfiguration() {
        log.info("[ShareAutoConfiguration] Initializing capability share module");
    }

    @Bean
    @ConditionalOnMissingBean(CapabilityShareService.class)
    public CapabilityShareService capabilityShareService() {
        log.info("[ShareAutoConfiguration] Creating CapabilityShareService");
        log.info("[ShareAutoConfiguration] Base directory: {}", baseDir);
        log.info("[ShareAutoConfiguration] TodoService available: {}", todoService != null);
        
        return new CapabilityShareServiceImpl(baseDir, todoService);
    }
}
