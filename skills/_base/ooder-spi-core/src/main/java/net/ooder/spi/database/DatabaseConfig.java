package net.ooder.spi.database;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库配置
 */
public class DatabaseConfig {
    
    private String databaseType;
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
    private String databaseName;
    private String dataDir;
    private Map<String, Object> poolConfig = new HashMap<>();
    private Map<String, Object> extensions = new HashMap<>();
    
    public static Builder builder() {
        return new Builder();
    }
    
    public String getDatabaseType() {
        return databaseType;
    }
    
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }
    
    public String getJdbcUrl() {
        return jdbcUrl;
    }
    
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getDriverClassName() {
        return driverClassName;
    }
    
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    
    public String getDatabaseName() {
        return databaseName;
    }
    
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    
    public String getDataDir() {
        return dataDir;
    }
    
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
    
    public Map<String, Object> getPoolConfig() {
        return poolConfig;
    }
    
    public void setPoolConfig(Map<String, Object> poolConfig) {
        this.poolConfig = poolConfig;
    }
    
    public Map<String, Object> getExtensions() {
        return extensions;
    }
    
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions;
    }
    
    public static class Builder {
        private DatabaseConfig config = new DatabaseConfig();
        
        public Builder databaseType(String databaseType) {
            config.setDatabaseType(databaseType);
            return this;
        }
        
        public Builder jdbcUrl(String jdbcUrl) {
            config.setJdbcUrl(jdbcUrl);
            return this;
        }
        
        public Builder username(String username) {
            config.setUsername(username);
            return this;
        }
        
        public Builder password(String password) {
            config.setPassword(password);
            return this;
        }
        
        public Builder driverClassName(String driverClassName) {
            config.setDriverClassName(driverClassName);
            return this;
        }
        
        public Builder databaseName(String databaseName) {
            config.setDatabaseName(databaseName);
            return this;
        }
        
        public Builder dataDir(String dataDir) {
            config.setDataDir(dataDir);
            return this;
        }
        
        public Builder poolConfig(String key, Object value) {
            config.getPoolConfig().put(key, value);
            return this;
        }
        
        public Builder extension(String key, Object value) {
            config.getExtensions().put(key, value);
            return this;
        }
        
        public DatabaseConfig build() {
            return config;
        }
    }
}
