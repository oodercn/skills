package net.ooder.spi.database;

import javax.sql.DataSource;

public interface DataSourceProvider {
    
    String getDatabaseType();
    
    String getDatabaseTypeName();
    
    DataSource createDataSource(DatabaseConfig config);
    
    boolean validateConfig(DatabaseConfig config);
    
    String getJdbcUrlTemplate();
    
    String getDriverClassName();
}
