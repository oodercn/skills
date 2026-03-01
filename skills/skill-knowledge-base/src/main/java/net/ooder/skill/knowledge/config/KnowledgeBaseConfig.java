package net.ooder.skill.knowledge.config;

import net.ooder.skill.knowledge.service.DocumentIndexService;
import net.ooder.skill.knowledge.service.KnowledgeBaseService;
import net.ooder.skill.knowledge.service.impl.DocumentIndexServiceImpl;
import net.ooder.skill.knowledge.service.impl.KnowledgeBaseServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KnowledgeBaseConfig {
    
    @Bean
    public DocumentIndexService documentIndexService() {
        return new DocumentIndexServiceImpl();
    }
    
    @Bean
    public KnowledgeBaseService knowledgeBaseService(DocumentIndexService indexService) {
        return new KnowledgeBaseServiceImpl(indexService);
    }
}
