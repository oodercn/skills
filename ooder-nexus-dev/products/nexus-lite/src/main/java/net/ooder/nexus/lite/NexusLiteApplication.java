package net.ooder.nexus.lite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"net.ooder.nexus"})
public class NexusLiteApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NexusLiteApplication.class, args);
    }
}
