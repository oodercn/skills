package net.ooder.skill.org.dingding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"net.ooder.skill.org.dingding", "net.ooder.sdk"})
public class DingdingOrgApplication {

    public static void main(String[] args) {
        SpringApplication.run(DingdingOrgApplication.class, args);
    }
}
