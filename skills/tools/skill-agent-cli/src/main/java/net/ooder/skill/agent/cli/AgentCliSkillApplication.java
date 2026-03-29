package net.ooder.skill.agent.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.agent.cli")
public class AgentCliSkillApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentCliSkillApplication.class, args);
    }
}
