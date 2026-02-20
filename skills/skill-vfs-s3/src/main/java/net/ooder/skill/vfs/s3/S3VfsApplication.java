package net.ooder.skill.vfs.s3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder")
public class S3VfsApplication {

    public static void main(String[] args) {
        SpringApplication.run(S3VfsApplication.class, args);
    }
}
