package net.ooder.skill.database.sqlite;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ooder.spi.database.DataSourceProvider;
import net.ooder.spi.database.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * SQLite 数据源提供者
 */
public class SQLiteDataSourceProvider implements DataSourceProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(SQLiteDataSourceProvider.class);
    private static final String DATABASE_TYPE = "sqlite";
    private static final String DATABASE_TYPE_NAME = "SQLite";
    private static final String DRIVER_CLASS_NAME = "org.sqlite.JDBC";
    private static final String JDBC_URL_TEMPLATE = "jdbc:sqlite:{dataDir}/{databaseName}.db";
    
    @Override
    public String getDatabaseType() {
        return DATABASE_TYPE;
    }
    
    @Override
    public String getDatabaseTypeName() {
        return DATABASE_TYPE_NAME;
    }
    
    @Override
    public DataSource createDataSource(DatabaseConfig config) {
        logger.info("Creating SQLite data source for database: {}", config.getDatabaseName());
        
        String jdbcUrl = buildJdbcUrl(config);
        
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setDriverClassName(DRIVER_CLASS_NAME);
        
        if (config.getUsername() != null) {
            hikariConfig.setUsername(config.getUsername());
        }
        if (config.getPassword() != null) {
            hikariConfig.setPassword(config.getPassword());
        }
        
        configurePool(hikariConfig, config);
        
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        
        logger.info("SQLite data source created successfully: {}", jdbcUrl);
        return dataSource;
    }
    
    @Override
    public boolean validateConfig(DatabaseConfig config) {
        if (config == null) {
            return false;
        }
        
        String dataDir = config.getDataDir();
        if (dataDir == null || dataDir.isEmpty()) {
            logger.warn("Data directory is not specified");
            return false;
        }
        
        File dir = new File(dataDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                logger.warn("Failed to create data directory: {}", dataDir);
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String getJdbcUrlTemplate() {
        return JDBC_URL_TEMPLATE;
    }
    
    @Override
    public String getDriverClassName() {
        return DRIVER_CLASS_NAME;
    }
    
    private String buildJdbcUrl(DatabaseConfig config) {
        String dataDir = config.getDataDir();
        if (dataDir == null || dataDir.isEmpty()) {
            dataDir = System.getProperty("user.home") + "/.apexos/data";
        }
        
        String databaseName = config.getDatabaseName();
        if (databaseName == null || databaseName.isEmpty()) {
            databaseName = "apexos";
        }
        
        if (config.getJdbcUrl() != null && !config.getJdbcUrl().isEmpty()) {
            return config.getJdbcUrl();
        }
        
        return String.format("jdbc:sqlite:%s/%s.db", dataDir, databaseName);
    }
    
    private void configurePool(HikariConfig hikariConfig, DatabaseConfig config) {
        hikariConfig.setMaximumPoolSize(getPoolConfig(config, "max-size", 5));
        hikariConfig.setMinimumIdle(getPoolConfig(config, "min-idle", 1));
        hikariConfig.setConnectionTimeout(getPoolConfig(config, "connection-timeout", 30000L));
        hikariConfig.setIdleTimeout(getPoolConfig(config, "idle-timeout", 600000L));
        hikariConfig.setMaxLifetime(getPoolConfig(config, "max-lifetime", 1800000L));
        
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setAutoCommit(true);
    }
    
    private int getPoolConfig(DatabaseConfig config, String key, int defaultValue) {
        Object value = config.getPoolConfig().get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
    
    private long getPoolConfig(DatabaseConfig config, String key, long defaultValue) {
        Object value = config.getPoolConfig().get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }
}
