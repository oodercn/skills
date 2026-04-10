package net.ooder.bpm;

import net.ooder.bpm.config.EarlyJDSConfigInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BPMServerApplication {

    // 在类加载时就触发EarlyJDSConfigInitializer的静态初始化
    static {
        EarlyJDSConfigInitializer.init();
    }

    public static void main(String[] args) {
        SpringApplication.run(BPMServerApplication.class, args);
    }
}
