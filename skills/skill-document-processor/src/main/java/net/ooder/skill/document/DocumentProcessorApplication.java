package net.ooder.skill.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.document")
public class DocumentProcessorApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DocumentProcessorApplication.class, args);
    }
}
