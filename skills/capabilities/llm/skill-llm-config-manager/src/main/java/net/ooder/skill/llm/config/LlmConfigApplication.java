package net.ooder.skill.llm.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.llm.config")
public class LlmConfigApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LlmConfigApplication.class, args);
    }
}
