package net.ooder.skill.rag.config;

import net.ooder.skill.rag.service.RagEngine;
import net.ooder.skill.rag.service.impl.RagEngineImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RagConfig {
    
    @Bean
    public RagEngine ragEngine() {
        return new RagEngineImpl();
    }
}
