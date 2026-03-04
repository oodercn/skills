package net.ooder.skill.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"net.ooder.skill.test", "net.ooder.skill.llm.config"})
public class SkillUiTestApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SkillUiTestApplication.class, args);
    }
}
