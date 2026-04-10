package net.ooder.skill.database.sqlite;

import net.ooder.spi.database.DataSourceProvider;
import net.ooder.spi.facade.SpiServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * SQLite 数据源自动配置
 */
@AutoConfiguration
@ConditionalOnClass({DataSource.class, DataSourceProvider.class})
public class SQLiteDataSourceAutoConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(SQLiteDataSourceAutoConfiguration.class);
    
    @Bean
    @ConditionalOnMissingBean(DataSourceProvider.class)
    public DataSourceProvider sqliteDataSourceProvider() {
        logger.info("Initializing SQLite DataSourceProvider");
        return new SQLiteDataSourceProvider();
    }
    
    @Bean
    public SQLiteDataSourceInitializer sqliteDataSourceInitializer(DataSourceProvider dataSourceProvider) {
        logger.info("Initializing SQLite DataSourceInitializer");
        return new SQLiteDataSourceInitializer(dataSourceProvider);
    }
    
    /**
     * SQLite 数据源初始化器
     */
    public static class SQLiteDataSourceInitializer {
        
        private static final Logger logger = LoggerFactory.getLogger(SQLiteDataSourceInitializer.class);
        
        public SQLiteDataSourceInitializer(DataSourceProvider dataSourceProvider) {
            SpiServices services = SpiServices.getInstance();
            if (services != null) {
                services.setDataSourceProvider(dataSourceProvider);
                logger.info("SQLite DataSourceProvider registered to SpiServices");
            } else {
                logger.warn("SpiServices instance not found, DataSourceProvider not registered");
            }
        }
    }
}
