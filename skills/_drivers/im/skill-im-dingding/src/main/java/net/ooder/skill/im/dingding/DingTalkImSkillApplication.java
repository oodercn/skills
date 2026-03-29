package net.ooder.skill.im.dingding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.im.dingding")
public class DingTalkImSkillApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DingTalkImSkillApplication.class, args);
    }
}
