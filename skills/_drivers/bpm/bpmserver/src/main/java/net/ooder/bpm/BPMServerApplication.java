package net.ooder.bpm;

import net.ooder.bpm.config.EarlyJDSConfigInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BPMServerApplication {

    public static void main(String[] args) {
        EarlyJDSConfigInitializer.init();
        SpringApplication.run(BPMServerApplication.class, args);
    }
}
