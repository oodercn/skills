package net.ooder.mvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "net.ooder.mvp",
    "net.ooder.skill.common",
    "net.ooder.skill.capability"
})
public class MvpCoreApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(MvpCoreApplication.class, args);
    }
}
