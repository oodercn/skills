package net.ooder.skill.knowledge.local;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.knowledge.local")
public class LocalKnowledgeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LocalKnowledgeApplication.class, args);
    }
}
