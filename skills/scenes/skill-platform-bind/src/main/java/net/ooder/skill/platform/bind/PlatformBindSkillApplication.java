package net.ooder.skill.platform.bind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.platform.bind")
public class PlatformBindSkillApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatformBindSkillApplication.class, args);
    }
}
