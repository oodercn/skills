package net.ooder.skill.doc.collab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.doc.collab")
public class DocCollabSkillApplication {
    public static void main(String[] args) {
        SpringApplication.run(DocCollabSkillApplication.class, args);
    }
}
