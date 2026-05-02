package net.ooder.vfs.jdbc.transaction;

import net.ooder.vfs.jdbc.VFSException;
import net.ooder.vfs.jdbc.ConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);
    
    private static final ConcurrentHashMap<Long, Connection> transactionConnections = new ConcurrentHashMap<>();
    
    private static final ConcurrentHashMap<Long, Integer> transactionDepth = new ConcurrentHashMap<>();
    
    private static final ConcurrentHashMap<Long, Boolean> rollbackOnly = new ConcurrentHashMap<>();
    
    private final ConnectionProvider connectionProvider;
    
    private static volatile TransactionManager instance;
    
    private TransactionManager(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    public static synchronized TransactionManager getInstance(ConnectionProvider connectionProvider) {
        if (instance == null) {
            instance = new TransactionManager(connectionProvider);
        } else if (instance.connectionProvider != connectionProvider) {
            log.warn("TransactionManager already initialized with a different ConnectionProvider, using existing one");
        }
        return instance;
    }
    
    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.cleanupAll();
            instance = null;
        }
    }
    
    public Connection getTransactionConnection() {
        long threadId = Thread.currentThread().getId();
        return transactionConnections.get(threadId);
    }
    
    public Connection getCurrentConnection() throws SQLException {
        long threadId = Thread.currentThread().getId();
        Connection transConn = transactionConnections.get(threadId);
        
        if (transConn != null) {
            return transConn;
        }
        
        return null;
    }
    
    public Connection getConnection() throws SQLException {
        long threadId = Thread.currentThread().getId();
        Connection transConn = transactionConnections.get(threadId);
        
        if (transConn != null) {
            return transConn;
        }
        
        return connectionProvider.getConnection();
    }
    
    public boolean isInTransaction() {
        long threadId = Thread.currentThread().getId();
        return transactionConnections.containsKey(threadId);
    }
    
    public int getTransactionDepth() {
        long threadId = Thread.currentThread().getId();
        return transactionDepth.getOrDefault(threadId, 0);
    }
    
    public Connection beginTransaction() throws SQLException {
        long threadId = Thread.currentThread().getId();
        
        Connection existing = transactionConnections.get(threadId);
        if (existing != null) {
            int depth = transactionDepth.getOrDefault(threadId, 0);
            transactionDepth.put(threadId, depth + 1);
            log.debug("Nested transaction started, depth: {}", depth + 1);
            return existing;
        }
        
        Connection conn = connectionProvider.getConnection();
        conn.setAutoCommit(false);
        
        transactionConnections.put(threadId, conn);
        transactionDepth.put(threadId, 1);
        rollbackOnly.put(threadId, false);
        
        log.debug("Transaction started for thread: {}", threadId);
        return conn;
    }
    
    public void commitTransaction() throws SQLException {
        long threadId = Thread.currentThread().getId();
        
        Connection conn = transactionConnections.get(threadId);
        if (conn == null) {
            throw new SQLException("当前线程没有活动的事务");
        }
        
        int depth = transactionDepth.getOrDefault(threadId, 1);
        if (depth > 1) {
            transactionDepth.put(threadId, depth - 1);
            log.debug("Nested transaction committed, remaining depth: {}", depth - 1);
            return;
        }
        
        Boolean isRollbackOnly = rollbackOnly.get(threadId);
        if (isRollbackOnly != null && isRollbackOnly) {
            rollbackTransaction();
            throw new SQLException("事务已标记为回滚");
        }
        
        try {
            conn.commit();
            log.debug("Transaction committed for thread: {}", threadId);
        } finally {
            cleanupTransaction(threadId, conn);
        }
    }
    
    public void rollbackTransaction() {
        long threadId = Thread.currentThread().getId();
        
        Connection conn = transactionConnections.get(threadId);
        if (conn == null) {
            log.warn("No active transaction to rollback for thread: {}", threadId);
            return;
        }
        
        try {
            conn.rollback();
            log.debug("Transaction rolled back for thread: {}", threadId);
        } catch (SQLException e) {
            log.error("Failed to rollback transaction", e);
        } finally {
            cleanupTransaction(threadId, conn);
        }
    }
    
    public void setRollbackOnly() {
        long threadId = Thread.currentThread().getId();
        if (transactionConnections.containsKey(threadId)) {
            rollbackOnly.put(threadId, true);
            log.debug("Transaction marked as rollback-only for thread: {}", threadId);
        }
    }
    
    public boolean isRollbackOnly() {
        long threadId = Thread.currentThread().getId();
        return rollbackOnly.getOrDefault(threadId, false);
    }
    
    private void cleanupTransaction(long threadId, Connection conn) {
        transactionConnections.remove(threadId);
        transactionDepth.remove(threadId);
        rollbackOnly.remove(threadId);
        
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                log.error("Failed to reset auto-commit", e);
            }
            connectionProvider.releaseConnection(conn);
        }
    }
    
    public <T> T executeInTransaction(TransactionCallback<T> action) throws VFSException {
        boolean isNewTransaction = !isInTransaction();
        
        try {
            beginTransaction();
            
            T result = action.doInTransaction();
            
            if (isNewTransaction) {
                commitTransaction();
            }
            
            return result;
            
        } catch (Exception e) {
            if (isNewTransaction) {
                rollbackTransaction();
            } else {
                setRollbackOnly();
            }
            
            if (e instanceof VFSException) {
                throw (VFSException) e;
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new VFSException("事务执行失败", e);
        }
    }
    
    public void executeInTransaction(TransactionCallbackWithoutResult action) throws VFSException {
        executeInTransaction((TransactionCallback<Void>) () -> {
            action.doInTransaction();
            return null;
        });
    }
    
    public static void cleanupCurrentThread() {
        long threadId = Thread.currentThread().getId();
        Connection conn = transactionConnections.remove(threadId);
        transactionDepth.remove(threadId);
        rollbackOnly.remove(threadId);
        
        if (conn != null) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                log.error("Failed to cleanup transaction for thread: {}", threadId, e);
            }
        }
        
        log.debug("Cleaned up transaction for thread: {}", threadId);
    }
    
    private void cleanupAll() {
        for (Long threadId : transactionConnections.keySet()) {
            Connection conn = transactionConnections.remove(threadId);
            transactionDepth.remove(threadId);
            rollbackOnly.remove(threadId);
            
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    log.error("Failed to cleanup transaction for thread: {}", threadId, e);
                }
            }
        }
    }
    
    public static int getActiveTransactionCount() {
        return transactionConnections.size();
    }
    
    @FunctionalInterface
    public interface TransactionCallback<T> {
        T doInTransaction() throws Exception;
    }
    
    @FunctionalInterface
    public interface TransactionCallbackWithoutResult {
        void doInTransaction() throws Exception;
    }
}
