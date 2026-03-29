package net.ooder.skill.im.feishu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.im.feishu")
public class FeishuImSkillApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FeishuImSkillApplication.class, args);
    }
}
