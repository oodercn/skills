package net.ooder.skill.im.wecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.im.wecom")
public class WeComImSkillApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WeComImSkillApplication.class, args);
    }
}
