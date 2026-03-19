package net.ooder.skill.document.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DocumentAssistantApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DocumentAssistantApplication.class, args);
    }
}
