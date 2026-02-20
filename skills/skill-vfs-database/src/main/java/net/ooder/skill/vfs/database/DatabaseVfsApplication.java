package net.ooder.skill.vfs.database;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder")
public class DatabaseVfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseVfsApplication.class, args);
    }
}
