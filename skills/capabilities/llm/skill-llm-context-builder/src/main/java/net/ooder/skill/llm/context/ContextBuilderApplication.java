package net.ooder.skill.llm.context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.llm.context")
public class ContextBuilderApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ContextBuilderApplication.class, args);
    }
}
