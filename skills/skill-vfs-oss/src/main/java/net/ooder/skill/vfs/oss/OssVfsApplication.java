package net.ooder.skill.vfs.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder")
public class OssVfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OssVfsApplication.class, args);
    }
}
