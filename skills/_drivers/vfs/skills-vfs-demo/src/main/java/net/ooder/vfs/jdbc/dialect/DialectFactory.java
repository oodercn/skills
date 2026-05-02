package net.ooder.vfs.jdbc.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class DialectFactory {
    
    private static final Logger log = LoggerFactory.getLogger(DialectFactory.class);
    
    private static final ConcurrentHashMap<String, DatabaseDialect> dialectCache = new ConcurrentHashMap<>();
    
    private static final String MYSQL = "mysql";
    private static final String ORACLE = "oracle";
    private static final String POSTGRESQL = "postgresql";
    private static final String SQLSERVER = "sqlserver";
    private static final String DB2 = "db2";
    private static final String H2 = "h2";
    private static final String SQLITE = "sqlite";
    
    public static DatabaseDialect getDialect(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String databaseProductName = metaData.getDatabaseProductName().toLowerCase();
        String databaseVersion = metaData.getDatabaseProductVersion();
        
        String dialectKey = databaseProductName + "_" + databaseVersion;
        
        return dialectCache.computeIfAbsent(dialectKey, key -> {
            DatabaseDialect dialect = createDialect(databaseProductName);
            log.info("Detected database: {} {}, using dialect: {}", databaseProductName, databaseVersion, dialect.getName());
            return dialect;
        });
    }
    
    public static DatabaseDialect getDialect(String driverClassName) {
        String driverLower = driverClassName.toLowerCase();
        
        if (driverLower.contains(MYSQL)) {
            return new MySQLDialect();
        } else if (driverLower.contains(ORACLE)) {
            return new OracleDialect();
        } else if (driverLower.contains(POSTGRESQL)) {
            return new PostgreSQLDialect();
        } else if (driverLower.contains(SQLSERVER)) {
            return new SQLServerDialect();
        } else if (driverLower.contains(DB2)) {
            return new DB2Dialect();
        } else if (driverLower.contains(H2)) {
            return new H2Dialect();
        } else if (driverLower.contains(SQLITE)) {
            return new SQLiteDialect();
        }
        
        log.warn("Unknown database driver: {}, using MySQL dialect as default", driverClassName);
        return new MySQLDialect();
    }
    
    private static DatabaseDialect createDialect(String databaseProductName) {
        String nameLower = databaseProductName.toLowerCase();
        
        if (nameLower.contains(MYSQL)) {
            return new MySQLDialect();
        } else if (nameLower.contains(ORACLE)) {
            return new OracleDialect();
        } else if (nameLower.contains(POSTGRESQL)) {
            return new PostgreSQLDialect();
        } else if (nameLower.contains(SQLSERVER)) {
            return new SQLServerDialect();
        } else if (nameLower.contains(DB2)) {
            return new DB2Dialect();
        } else if (nameLower.contains(H2)) {
            return new H2Dialect();
        } else if (nameLower.contains(SQLITE)) {
            return new SQLiteDialect();
        }
        
        log.warn("Unknown database: {}, using MySQL dialect as default", databaseProductName);
        return new MySQLDialect();
    }
    
    public static void clearCache() {
        dialectCache.clear();
    }
}
