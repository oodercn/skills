package net.ooder.skill.vfs.local;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder")
public class LocalVfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalVfsApplication.class, args);
    }
}
