package net.ooder.vfs.jdbc.transaction;

import net.ooder.vfs.jdbc.VFSException;
import net.ooder.vfs.jdbc.ConnectionProvider;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TransactionManager测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionManagerTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    private TransactionManager transactionManager;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() throws SQLException {
        closeable = MockitoAnnotations.openMocks(this);
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        
        TransactionManager.resetInstance();
        transactionManager = TransactionManager.getInstance(connectionProvider);
    }

    @AfterEach
    void tearDown() throws Exception {
        TransactionManager.cleanupCurrentThread();
        TransactionManager.resetInstance();
        closeable.close();
    }

    @Test
    @Order(1)
    @DisplayName("开始事务测试")
    void testBeginTransaction() throws SQLException {
        Connection conn = transactionManager.beginTransaction();
        
        assertNotNull(conn);
        assertTrue(transactionManager.isInTransaction());
        assertEquals(1, transactionManager.getTransactionDepth());
        
        verify(connection).setAutoCommit(false);
        
        transactionManager.rollbackTransaction();
    }

    @Test
    @Order(2)
    @DisplayName("提交事务测试")
    void testCommitTransaction() throws SQLException {
        transactionManager.beginTransaction();
        transactionManager.commitTransaction();
        
        assertFalse(transactionManager.isInTransaction());
        assertEquals(0, transactionManager.getTransactionDepth());
        
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @Order(3)
    @DisplayName("回滚事务测试")
    void testRollbackTransaction() throws SQLException {
        transactionManager.beginTransaction();
        transactionManager.rollbackTransaction();
        
        assertFalse(transactionManager.isInTransaction());
        assertEquals(0, transactionManager.getTransactionDepth());
        
        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
        verify(connectionProvider).releaseConnection(connection);
    }

    @Test
    @Order(4)
    @DisplayName("executeInTransaction成功执行测试")
    void testExecuteInTransactionSuccess() throws SQLException {
        String result = transactionManager.executeInTransaction(() -> {
            assertTrue(transactionManager.isInTransaction());
            return "success";
        });
        
        assertEquals("success", result);
        assertFalse(transactionManager.isInTransaction());
        
        verify(connection).commit();
    }

    @Test
    @Order(5)
    @DisplayName("executeInTransaction异常回滚测试")
    void testExecuteInTransactionRollback() throws SQLException {
        assertThrows(VFSException.class, () -> {
            transactionManager.executeInTransaction(() -> {
                assertTrue(transactionManager.isInTransaction());
                throw new RuntimeException("Test exception");
            });
        });
        
        assertFalse(transactionManager.isInTransaction());
        
        verify(connection).rollback();
    }

    @Test
    @Order(6)
    @DisplayName("嵌套事务测试")
    void testNestedTransaction() throws SQLException {
        transactionManager.executeInTransaction(() -> {
            assertEquals(1, transactionManager.getTransactionDepth());
            
            transactionManager.executeInTransaction(() -> {
                assertEquals(2, transactionManager.getTransactionDepth());
                return null;
            });
            
            assertEquals(1, transactionManager.getTransactionDepth());
            
            return null;
        });
        
        assertEquals(0, transactionManager.getTransactionDepth());
        assertFalse(transactionManager.isInTransaction());
    }

    @Test
    @Order(7)
    @DisplayName("setRollbackOnly测试")
    void testSetRollbackOnly() throws SQLException {
        assertThrows(SQLException.class, () -> {
            transactionManager.beginTransaction();
            transactionManager.setRollbackOnly();
            assertTrue(transactionManager.isRollbackOnly());
            transactionManager.commitTransaction();
        });
        
        verify(connection).rollback();
    }

    @Test
    @Order(8)
    @DisplayName("多线程事务隔离测试")
    void testMultiThreadTransactionIsolation() throws InterruptedException, SQLException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Connection threadConn = mock(Connection.class);
                    when(threadConn.getAutoCommit()).thenReturn(true);
                    when(connectionProvider.getConnection()).thenReturn(threadConn);
                    
                    transactionManager.executeInTransaction(() -> {
                        assertTrue(transactionManager.isInTransaction());
                        Thread.sleep(10);
                        successCount.incrementAndGet();
                        return null;
                    });
                } catch (Exception e) {
                    fail("Exception during transaction: " + e.getMessage());
                }
            });
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        
        assertEquals(threadCount, successCount.get());
    }

    @Test
    @Order(9)
    @DisplayName("getCurrentConnection测试")
    void testGetCurrentConnection() throws SQLException {
        Connection conn1 = transactionManager.getCurrentConnection();
        
        transactionManager.beginTransaction();
        
        Connection conn2 = transactionManager.getCurrentConnection();
        assertNotNull(conn2);
        
        assertSame(connection, conn2);
        
        transactionManager.rollbackTransaction();
    }

    @Test
    @Order(10)
    @DisplayName("事务深度测试")
    void testTransactionDepth() throws SQLException {
        assertEquals(0, transactionManager.getTransactionDepth());
        
        transactionManager.beginTransaction();
        assertEquals(1, transactionManager.getTransactionDepth());
        
        transactionManager.beginTransaction();
        assertEquals(2, transactionManager.getTransactionDepth());
        
        transactionManager.commitTransaction();
        assertEquals(1, transactionManager.getTransactionDepth());
        
        transactionManager.commitTransaction();
        assertEquals(0, transactionManager.getTransactionDepth());
    }

    @Test
    @Order(11)
    @DisplayName("活动事务计数测试")
    void testActiveTransactionCount() throws SQLException {
        assertEquals(0, TransactionManager.getActiveTransactionCount());
        
        transactionManager.beginTransaction();
        assertEquals(1, TransactionManager.getActiveTransactionCount());
        
        transactionManager.commitTransaction();
        assertEquals(0, TransactionManager.getActiveTransactionCount());
    }

    @Test
    @Order(12)
    @DisplayName("cleanupCurrentThread测试")
    void testCleanupCurrentThread() throws SQLException {
        transactionManager.beginTransaction();
        assertTrue(transactionManager.isInTransaction());
        
        TransactionManager.cleanupCurrentThread();
        
        assertFalse(transactionManager.isInTransaction());
        assertEquals(0, transactionManager.getTransactionDepth());
    }

    @Test
    @Order(13)
    @DisplayName("executeInTransactionWithoutResult测试")
    void testExecuteInTransactionWithoutResult() throws SQLException {
        AtomicInteger counter = new AtomicInteger(0);
        
        transactionManager.executeInTransaction(() -> {
            counter.incrementAndGet();
        });
        
        assertEquals(1, counter.get());
        assertFalse(transactionManager.isInTransaction());
    }

    @Test
    @Order(14)
    @DisplayName("重复提交事务测试")
    void testDoubleCommitTransaction() throws SQLException {
        transactionManager.beginTransaction();
        transactionManager.commitTransaction();
        
        assertThrows(SQLException.class, () -> {
            transactionManager.commitTransaction();
        });
    }

    @Test
    @Order(15)
    @DisplayName("无事务时回滚测试")
    void testRollbackWithoutTransaction() throws SQLException {
        assertDoesNotThrow(() -> {
            transactionManager.rollbackTransaction();
        });
    }
}
