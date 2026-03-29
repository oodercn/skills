package net.ooder.skill.todo.sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.todo.sync")
public class TodoSyncSkillApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoSyncSkillApplication.class, args);
    }
}
