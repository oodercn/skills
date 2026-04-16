package net.ooder.skill.context;

import net.ooder.skill.context.service.MultiLevelContextManager;
import net.ooder.skill.context.service.impl.MultiLevelContextManagerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.context")
@ConditionalOnProperty(name = "skill.context.enabled", havingValue = "true", matchIfMissing = true)
public class ContextAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MultiLevelContextManager.class)
    public MultiLevelContextManager multiLevelContextManager() {
        return new MultiLevelContextManagerImpl();
    }
}