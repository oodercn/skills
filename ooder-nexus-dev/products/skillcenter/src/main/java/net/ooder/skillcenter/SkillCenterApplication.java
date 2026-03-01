package net.ooder.skillcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"net.ooder.nexus", "net.ooder.skillcenter"})
public class SkillCenterApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SkillCenterApplication.class, args);
    }
}
