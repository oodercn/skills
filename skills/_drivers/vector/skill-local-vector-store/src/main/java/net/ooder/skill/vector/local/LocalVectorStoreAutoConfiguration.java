package net.ooder.skill.vector.local;

import net.ooder.spi.vector.VectorStoreProvider;
import net.ooder.spi.facade.SpiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 本地向量存储自动配置
 */
@AutoConfiguration
@ConditionalOnClass(VectorStoreProvider.class)
public class LocalVectorStoreAutoConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalVectorStoreAutoConfiguration.class);
    
    @Bean
    @ConditionalOnMissingBean(VectorStoreProvider.class)
    public VectorStoreProvider localVectorStoreProvider() {
        logger.info("Initializing LocalVectorStoreProvider");
        return new LocalVectorStoreProvider();
    }
    
    @Bean
    public LocalVectorStoreInitializer localVectorStoreInitializer(VectorStoreProvider vectorStoreProvider) {
        logger.info("Initializing LocalVectorStoreInitializer");
        return new LocalVectorStoreInitializer(vectorStoreProvider);
    }
    
    /**
     * 本地向量存储初始化器
     */
    public static class LocalVectorStoreInitializer {
        
        private static final Logger logger = LoggerFactory.getLogger(LocalVectorStoreInitializer.class);
        
        public LocalVectorStoreInitializer(VectorStoreProvider vectorStoreProvider) {
            SpiServices services = SpiServices.getInstance();
            if (services != null) {
                services.setVectorStoreProvider(vectorStoreProvider);
                logger.info("LocalVectorStoreProvider registered to SpiServices");
            } else {
                logger.warn("SpiServices instance not found, VectorStoreProvider not registered");
            }
        }
    }
}
