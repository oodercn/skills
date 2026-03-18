package net.ooder.skill.scene;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
})
@ComponentScan(
    basePackages = {
        "net.ooder.skill.scene",
        "net.ooder.skill.security.controller",
        "net.ooder.skill.security.service",
        "net.ooder.skill.security.service.impl",
        "net.ooder.skill.security.provider",
        "net.ooder.skill.security.api",
        "net.ooder.skill.security.integration"
    }
)
public class SceneSkillApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SceneSkillApplication.class, args);
    }
}
