package net.ooder.vfs.jdbc;

import net.ooder.vfs.jdbc.cache.DefaultVfsCacheProvider;
import net.ooder.vfs.jdbc.cache.VfsCacheProvider;
import net.ooder.vfs.jdbc.dialect.DatabaseDialect;
import net.ooder.vfs.jdbc.dialect.DialectFactory;
import net.ooder.vfs.jdbc.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class JdbcManager implements ConnectionProvider {
    
    private static final Logger log = LoggerFactory.getLogger(JdbcManager.class);
    
    private static volatile JdbcManager instance;
    
    private static final ConcurrentHashMap<Long, Connection> nonTxConnections = new ConcurrentHashMap<>();
    
    private static final ConcurrentHashMap<Long, Long> connectionTimestamps = new ConcurrentHashMap<>();
    
    private static final AtomicLong connectionCounter = new AtomicLong(0);
    
    private static final long CONNECTION_TIMEOUT = 30 * 60 * 1000;
    
    private volatile DatabaseDialect dialect;
    
    private TransactionManager transactionManager;
    
    private volatile boolean initialized = false;
    
    private ConnectionProvider externalProvider;
    
    private volatile VfsCacheProvider cacheProvider;
    
    private JdbcManager() {
    }
    
    public static JdbcManager getInstance() {
        if (instance == null) {
            synchronized (JdbcManager.class) {
                if (instance == null) {
                    instance = new JdbcManager();
                    instance.init();
                }
            }
        }
        return instance;
    }
    
    public static JdbcManager create(ConnectionProvider provider) {
        JdbcManager manager = new JdbcManager();
        manager.externalProvider = provider;
        manager.init();
        return manager;
    }
    
    private synchronized void init() {
        if (!initialized) {
            transactionManager = TransactionManager.getInstance(this);
            
            Thread cleanupThread = new Thread(this::cleanupIdleConnections, "JdbcManager-Cleanup");
            cleanupThread.setDaemon(true);
            cleanupThread.start();
            
            initialized = true;
            log.info("JdbcManager initialized");
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        Connection transConn = transactionManager.getTransactionConnection();
        if (transConn != null) {
            return transConn;
        }
        
        Connection conn;
        if (externalProvider != null) {
            conn = externalProvider.getConnection();
        } else {
            throw new SQLException("No ConnectionProvider configured. Use JdbcManager.create(provider) or set externalProvider.");
        }
        
        long threadId = Thread.currentThread().getId();
        nonTxConnections.put(threadId, conn);
        connectionTimestamps.put(threadId, System.currentTimeMillis());
        
        if (dialect == null) {
            try {
                dialect = DialectFactory.getDialect(conn);
                log.info("Database dialect initialized: {}", dialect.getName());
            } catch (SQLException e) {
                log.error("Failed to detect database dialect", e);
            }
        }
        
        return conn;
    }
    
    @Override
    public void releaseConnection(Connection conn) {
        if (conn == null) {
            return;
        }
        
        if (transactionManager.isInTransaction()) {
            return;
        }
        
        long threadId = Thread.currentThread().getId();
        nonTxConnections.remove(threadId);
        connectionTimestamps.remove(threadId);
        
        if (externalProvider != null) {
            externalProvider.releaseConnection(conn);
        } else {
            try {
                if (!conn.isClosed()) {
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("Failed to release connection", e);
            }
        }
    }
    
    @Override
    public String getDriverClassName() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            return metaData.getDriverName();
        } catch (SQLException e) {
            log.error("Failed to get driver class name", e);
            return null;
        }
    }
    
    public DatabaseDialect getDialect() {
        return dialect;
    }
    
    public VfsCacheProvider getCacheProvider() {
        if (cacheProvider == null) {
            synchronized (this) {
                if (cacheProvider == null) {
                    cacheProvider = new DefaultVfsCacheProvider();
                }
            }
        }
        return cacheProvider;
    }
    
    public void setCacheProvider(VfsCacheProvider provider) {
        this.cacheProvider = provider;
    }
    
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
    
    public Connection beginTransaction() throws SQLException {
        return transactionManager.beginTransaction();
    }
    
    public void commitTransaction() throws SQLException {
        transactionManager.commitTransaction();
    }
    
    public void rollbackTransaction() {
        transactionManager.rollbackTransaction();
    }
    
    public <T> T executeInTransaction(TransactionManager.TransactionCallback<T> action) throws VFSException {
        return transactionManager.executeInTransaction(action);
    }
    
    public void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("Failed to close statement", e);
            }
        }
    }
    
    public void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("Failed to close result set", e);
            }
        }
    }
    
    public void close(Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
    }
    
    public void close(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    log.error("Failed to close resource", e);
                }
            }
        }
    }
    
    public int getActiveConnectionCount() {
        return nonTxConnections.size();
    }
    
    public boolean isInTransaction() {
        return transactionManager.isInTransaction();
    }
    
    private void cleanupIdleConnections() {
        while (true) {
            try {
                Thread.sleep(60 * 1000);
                
                long currentTime = System.currentTimeMillis();
                
                connectionTimestamps.entrySet().removeIf(entry -> {
                    long threadId = entry.getKey();
                    long timestamp = entry.getValue();
                    
                    if (currentTime - timestamp > CONNECTION_TIMEOUT) {
                        Connection conn = nonTxConnections.remove(threadId);
                        if (conn != null) {
                            try {
                                if (!conn.isClosed()) {
                                    conn.close();
                                    log.warn("Closed idle connection for thread: {}", threadId);
                                }
                            } catch (SQLException e) {
                                log.error("Failed to close idle connection", e);
                            }
                        }
                        return true;
                    }
                    
                    return false;
                });
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in connection cleanup", e);
            }
        }
    }
    
    public static void cleanup() {
        if (instance != null) {
            instance.nonTxConnections.clear();
            instance.connectionTimestamps.clear();
            log.info("JdbcManager cleaned up");
        }
    }
    
    public void printConnectionStats() {
        log.info("Non-transaction connections: {}", nonTxConnections.size());
        log.info("In transaction: {}", isInTransaction());
        if (dialect != null) {
            log.info("Database dialect: {}", dialect.getName());
        }
    }
}
