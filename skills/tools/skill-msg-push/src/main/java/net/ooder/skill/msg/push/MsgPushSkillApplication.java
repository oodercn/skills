package net.ooder.skill.msg.push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.msg")
public class MsgPushSkillApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MsgPushSkillApplication.class, args);
    }
}
