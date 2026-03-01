package net.ooder.nexus.enterprise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"net.ooder.nexus"})
public class NexusEnterpriseApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NexusEnterpriseApplication.class, args);
    }
}
