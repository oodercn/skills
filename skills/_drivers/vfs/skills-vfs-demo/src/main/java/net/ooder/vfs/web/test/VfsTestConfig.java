package net.ooder.vfs.web.test;

import net.ooder.vfs.jdbc.JdbcManager;
import net.ooder.vfs.jdbc.JdbcTemplate;
import net.ooder.vfs.jdbc.VFSException;
import net.ooder.vfs.jdbc.concurrent.LockManager;
import net.ooder.vfs.jdbc.dialect.SQLiteDialect;
import net.ooder.vfs.jdbc.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class VfsTestConfig {
    
    private static final Logger log = LoggerFactory.getLogger(VfsTestConfig.class);
    
    @Bean
    public Connection sqliteConnection() throws SQLException {
        String dbPath = System.getProperty("vfs.db.path", "vfs_test.db");
        String url = "jdbc:sqlite:" + dbPath;
        log.info("Initializing SQLite database: {}", url);
        
        Connection conn = DriverManager.getConnection(url);
        initializeSchema(conn);
        return conn;
    }
    
    @Bean
    public ConnectionProviderAdapter connectionProvider(Connection sqliteConnection) {
        return new ConnectionProviderAdapter(sqliteConnection);
    }
    
    @Bean
    public JdbcManager jdbcManager(ConnectionProviderAdapter connectionProvider) {
        TransactionManager.resetInstance();
        JdbcManager.cleanup();
        JdbcManager manager = JdbcManager.create(connectionProvider);
        log.info("JdbcManager created with SQLite dialect");
        return manager;
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate(ConnectionProviderAdapter connectionProvider) {
        JdbcTemplate template = new JdbcTemplate(connectionProvider);
        template.setDialect(new SQLiteDialect());
        return template;
    }
    
    @Bean
    CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            log.info("VFS Web Test Application started successfully");
            log.info("Database dialect: {}", jdbcTemplate.getDialect() != null ? jdbcTemplate.getDialect().getName() : "not set");
        };
    }
    
    private void initializeSchema(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS VFS_FILE (" +
                "FILE_ID TEXT PRIMARY KEY, NAME TEXT, FOLDER_ID TEXT, FILE_TYPE INTEGER, " +
                "PERSON_ID TEXT, DESCRITION TEXT, CREATE_TIME INTEGER, UPDATE_TIME INTEGER, " +
                "IS_RECYCLED INTEGER DEFAULT 0, IS_LOCKED INTEGER DEFAULT 0, RIGHT TEXT, " +
                "ACTIVITY_INST_ID TEXT, HASH TEXT, LENGTH INTEGER DEFAULT 0)");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS VFS_FOLDER (" +
                "FOLDER_ID TEXT PRIMARY KEY, NAME TEXT, PARENT_ID TEXT, PERSON_ID TEXT, " +
                "CREATE_TIME INTEGER, UPDATE_TIME INTEGER, PATH TEXT, SIZE INTEGER DEFAULT 0, " +
                "IS_RECYCLED INTEGER DEFAULT 0, FOLDER_TYPE INTEGER DEFAULT 0, FOLDER_STATE INTEGER DEFAULT 0, " +
                "DESCRIPTION TEXT)");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS VFS_FILE_VERSION (" +
                "VERSION_ID TEXT PRIMARY KEY, FILE_ID TEXT, NAME TEXT, PERSON_ID TEXT, " +
                "CREATE_TIME INTEGER, FILE_OBJECT_ID TEXT, HASH TEXT, LENGTH INTEGER)");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS VFS_FILE_LINK (" +
                "LINK_ID TEXT PRIMARY KEY, FILE_ID TEXT, PERSON_ID TEXT, CREATE_TIME INTEGER, RIGHT TEXT)");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS VFS_FILE_COPY (" +
                "COPY_ID TEXT PRIMARY KEY, FILE_ID TEXT, PERSON_ID TEXT, CREATE_TIME INTEGER, TARGET_FOLDER_ID TEXT)");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS VFS_FILE_VIEW (" +
                "VIEW_ID TEXT PRIMARY KEY, FILE_ID TEXT, PERSON_ID TEXT, CREATE_TIME INTEGER, VIEW_TYPE INTEGER)");
            
            if (!folderExists(conn, "folder-root")) {
                stmt.executeUpdate("INSERT INTO VFS_FOLDER (FOLDER_ID, NAME, PARENT_ID, PERSON_ID, CREATE_TIME, UPDATE_TIME, PATH) " +
                    "VALUES ('folder-root', 'Root', NULL, 'system', " + System.currentTimeMillis() + ", " + System.currentTimeMillis() + ", '/')");
            }
            
            log.info("Database schema initialized");
        }
    }
    
    private boolean folderExists(Connection conn, String folderId) throws SQLException {
        try (var ps = conn.prepareStatement("SELECT COUNT(*) FROM VFS_FOLDER WHERE FOLDER_ID = ?")) {
            ps.setString(1, folderId);
            try (var rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    public static class ConnectionProviderAdapter implements net.ooder.vfs.jdbc.ConnectionProvider {
        private final Connection connection;
        
        public ConnectionProviderAdapter(Connection connection) {
            this.connection = connection;
        }
        
        @Override
        public Connection getConnection() throws SQLException {
            return connection;
        }
        
        @Override
        public void releaseConnection(Connection conn) {
            // SQLite: shared connection, don't close
        }
        
        @Override
        public String getDriverClassName() {
            return "org.sqlite.JDBC";
        }
    }
}
