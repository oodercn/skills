package net.ooder.bpm.test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BPMTestConfig {

    @Value("${bpm.server.url}")
    private String bpmServerUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public String bpmServerUrl() {
        return bpmServerUrl;
    }
}
