package net.ooder.skill.chat.config;

import net.ooder.spi.facade.SpiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SPI 配置
 */
@Configuration
public class SpiConfig {

    @Bean
    public SpiServices spiServices() {
        SpiServices services = new SpiServices();
        SpiServices.init(services);
        return services;
    }
}
