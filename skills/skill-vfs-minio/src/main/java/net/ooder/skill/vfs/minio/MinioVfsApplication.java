package net.ooder.skill.vfs.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder")
public class MinioVfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinioVfsApplication.class, args);
    }
}
