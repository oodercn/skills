package net.ooder.spi.database;

import javax.sql.DataSource;

/**
 * 数据源提供者 SPI 接口
 * 用于提供不同类型数据库的数据源
 */
public interface DataSourceProvider {
    
    /**
     * 获取数据库类型标识
     * @return 数据库类型，如 "sqlite", "mysql", "postgresql"
     */
    String getDatabaseType();
    
    /**
     * 获取数据库类型显示名称
     * @return 数据库类型名称，如 "SQLite", "MySQL", "PostgreSQL"
     */
    String getDatabaseTypeName();
    
    /**
     * 创建数据源
     * @param config 数据库配置
     * @return 数据源实例
     */
    DataSource createDataSource(DatabaseConfig config);
    
    /**
     * 验证数据库配置是否有效
     * @param config 数据库配置
     * @return 是否有效
     */
    boolean validateConfig(DatabaseConfig config);
    
    /**
     * 获取数据库连接URL模板
     * @return JDBC URL 模板
     */
    String getJdbcUrlTemplate();
    
    /**
     * 获取数据库驱动类名
     * @return 驱动类名
     */
    String getDriverClassName();
}
