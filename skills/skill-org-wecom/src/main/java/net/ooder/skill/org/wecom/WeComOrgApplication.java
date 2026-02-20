package net.ooder.skill.org.wecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder")
public class WeComOrgApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeComOrgApplication.class, args);
    }
}
