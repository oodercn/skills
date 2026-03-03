package net.ooder.skill.knowledge.config;

import net.ooder.skill.knowledge.service.DocumentIndexService;
import net.ooder.skill.knowledge.service.impl.DocumentIndexServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnowledgeBaseConfig {
    
    @Bean
    public DocumentIndexService documentIndexService() {
        return new DocumentIndexServiceImpl();
    }
}
