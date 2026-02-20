package net.ooder.skill.org.feishu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"net.ooder.skill.org.feishu", "net.ooder.sdk"})
public class FeishuOrgApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeishuOrgApplication.class, args);
    }
}
