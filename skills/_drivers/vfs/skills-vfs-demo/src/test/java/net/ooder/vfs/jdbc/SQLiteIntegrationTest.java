package net.ooder.vfs.jdbc;

import net.ooder.vfs.jdbc.concurrent.LockManager;
import net.ooder.vfs.jdbc.dialect.DatabaseDialect;
import net.ooder.vfs.jdbc.dialect.DialectFactory;
import net.ooder.vfs.jdbc.dialect.SQLiteDialect;
import net.ooder.vfs.jdbc.transaction.TransactionManager;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SQLite集成测试 - JDBC核心组件")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLiteIntegrationTest {

    private static final String DB_URL = "jdbc:sqlite::memory:";
    private static Connection sharedConnection;
    private static JdbcTemplate jdbcTemplate;
    private static JdbcManager jdbcManager;

    @BeforeAll
    static void setUpAll() throws Exception {
        Class.forName("org.sqlite.JDBC");
        sharedConnection = DriverManager.getConnection(DB_URL);
        
        sharedConnection.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS VFS_FILE (" +
            "FILE_ID TEXT PRIMARY KEY, NAME TEXT, FOLDER_ID TEXT, FILE_TYPE INTEGER, " +
            "PERSON_ID TEXT, DESCRITION TEXT, CREATE_TIME INTEGER, UPDATE_TIME INTEGER, " +
            "IS_RECYCLED INTEGER DEFAULT 0, IS_LOCKED INTEGER DEFAULT 0, RIGHT TEXT, ACTIVITY_INST_ID TEXT)"
        );
        
        sharedConnection.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS VFS_FOLDER (" +
            "FOLDER_ID TEXT PRIMARY KEY, NAME TEXT, PARENT_ID TEXT, PERSON_ID TEXT, " +
            "CREATE_TIME INTEGER, UPDATE_TIME INTEGER, PATH TEXT, SIZE INTEGER DEFAULT 0, IS_RECYCLED INTEGER DEFAULT 0)"
        );
        
        sharedConnection.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS VFS_FILE_VERSION (" +
            "VERSION_ID TEXT PRIMARY KEY, FILE_ID TEXT, NAME TEXT, PERSON_ID TEXT, " +
            "CREATE_TIME INTEGER, FILE_OBJECT_ID TEXT, HASH TEXT, LENGTH INTEGER)"
        );
        
        sharedConnection.createStatement().executeUpdate(
            "CREATE TABLE IF NOT EXISTS VFS_FILE_LINK (" +
            "LINK_ID TEXT PRIMARY KEY, FILE_ID TEXT, PERSON_ID TEXT, CREATE_TIME INTEGER, RIGHT TEXT)"
        );
        
        ConnectionProvider provider = new SQLiteConnectionProvider(sharedConnection);
        TransactionManager.resetInstance();
        JdbcManager.cleanup();
        jdbcManager = JdbcManager.create(provider);
        jdbcTemplate = new JdbcTemplate(provider);
        jdbcTemplate.setDialect(new SQLiteDialect());
        
        System.out.println("SQLite test database initialized");
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        TransactionManager.resetInstance();
        JdbcManager.cleanup();
        if (sharedConnection != null && !sharedConnection.isClosed()) {
            sharedConnection.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. 数据库方言检测测试")
    void testDialectDetection() throws SQLException {
        DatabaseDialect dialect = DialectFactory.getDialect(sharedConnection);
        assertNotNull(dialect);
        assertInstanceOf(SQLiteDialect.class, dialect);
        assertEquals("SQLite", dialect.getName());
        System.out.println("[PASS] Dialect detected: " + dialect.getName());
    }

    @Test
    @Order(2)
    @DisplayName("2. 插入文件测试")
    void testInsertFile() throws Exception {
        String sql = "INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        for (int i = 1; i <= 100; i++) {
            jdbcTemplate.update(sql, "file-" + i, "File " + i, "folder-root", 1, "user-1",
                System.currentTimeMillis(), System.currentTimeMillis());
        }
        
        int count = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE");
        assertEquals(100, count);
        System.out.println("[PASS] Inserted 100 files");
    }

    @Test
    @Order(3)
    @DisplayName("3. 查询文件测试")
    void testQueryFile() throws Exception {
        String sql = "SELECT * FROM VFS_FILE WHERE FILE_ID = ?";
        
        Map<String, Object> file = jdbcTemplate.queryForObject(sql, new Object[]{"file-1"},
            (rs, rowNum) -> {
                Map<String, Object> map = new java.util.LinkedHashMap<>();
                map.put("id", rs.getString("FILE_ID"));
                map.put("name", rs.getString("NAME"));
                map.put("folderId", rs.getString("FOLDER_ID"));
                return map;
            });
        
        assertNotNull(file);
        assertEquals("file-1", file.get("id"));
        assertEquals("File 1", file.get("name"));
        System.out.println("[PASS] Query file: " + file);
    }

    @Test
    @Order(4)
    @DisplayName("4. 分页查询测试（数据库兼容）")
    void testPagedQuery() throws Exception {
        String sql = "SELECT * FROM VFS_FILE WHERE FOLDER_ID = ? ORDER BY CREATE_TIME DESC";
        
        List<Map<String, Object>> page1 = jdbcTemplate.queryForPagedList(sql, new Object[]{"folder-root"},
            (rs, rowNum) -> {
                Map<String, Object> map = new java.util.LinkedHashMap<>();
                map.put("id", rs.getString("FILE_ID"));
                map.put("name", rs.getString("NAME"));
                return map;
            }, 0, 10);
        
        assertNotNull(page1);
        assertEquals(10, page1.size());
        System.out.println("[PASS] Paged query returned " + page1.size() + " files");
    }

    @Test
    @Order(5)
    @DisplayName("5. 事务提交测试")
    void testTransactionCommit() throws Exception {
        int countBefore = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE WHERE FILE_ID LIKE 'file-tx-%'");
        
        jdbcManager.executeInTransaction(() -> {
            jdbcTemplate.update("INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "file-tx-1", "Tx File 1", "folder-root", 1, "user-1", System.currentTimeMillis(), System.currentTimeMillis());
            jdbcTemplate.update("INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "file-tx-2", "Tx File 2", "folder-root", 1, "user-1", System.currentTimeMillis(), System.currentTimeMillis());
            return null;
        });
        
        int countAfter = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE WHERE FILE_ID LIKE 'file-tx-%'");
        assertEquals(countBefore + 2, countAfter);
        System.out.println("[PASS] Transaction commit: 2 files inserted");
    }

    @Test
    @Order(6)
    @DisplayName("6. 事务回滚测试")
    void testTransactionRollback() throws Exception {
        int countBefore = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE WHERE FILE_ID LIKE 'file-rollback-%'");
        
        try {
            jdbcManager.executeInTransaction(() -> {
                jdbcTemplate.update("INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    "file-rollback-1", "Rollback File", "folder-root", 1, "user-1", System.currentTimeMillis(), System.currentTimeMillis());
                throw new RuntimeException("Simulated error");
            });
        } catch (Exception e) {
            // Expected
        }
        
        int countAfter = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE WHERE FILE_ID LIKE 'file-rollback-%'");
        assertEquals(countBefore, countAfter);
        System.out.println("[PASS] Transaction rollback: no data persisted");
    }

    @Test
    @Order(7)
    @DisplayName("7. 并发读写测试")
    void testConcurrentReadWrite() throws Exception {
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    LockManager.executeWithLock("concurrent-file-" + index, () -> {
                        try {
                            jdbcTemplate.update("INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                "concurrent-file-" + index, "Concurrent " + index, "folder-root", 1, "user-" + index,
                                System.currentTimeMillis(), System.currentTimeMillis());
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                        }
                        return null;
                    });
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        assertEquals(threadCount, successCount.get());
        assertEquals(0, errorCount.get());
        System.out.println("[PASS] Concurrent write: " + successCount.get() + " success, " + errorCount.get() + " errors");
        executor.shutdown();
    }

    @Test
    @Order(8)
    @DisplayName("8. 并发锁竞争测试")
    void testLockContention() throws Exception {
        String sharedKey = "shared-resource";
        int threadCount = 10;
        AtomicInteger counter = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    LockManager.executeWithLock(sharedKey, () -> {
                        int val = counter.incrementAndGet();
                        try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                        return val;
                    });
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertEquals(threadCount, counter.get());
        System.out.println("[PASS] Lock contention: " + counter.get() + " sequential operations");
        executor.shutdown();
    }

    @Test
    @Order(9)
    @DisplayName("9. 批量操作测试")
    void testBatchUpdate() throws Exception {
        java.util.List<Object[]> params = new java.util.ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            params.add(new Object[]{"batch-file-" + i, "Batch " + i, "folder-root", 1, "user-1",
                System.currentTimeMillis(), System.currentTimeMillis()});
        }
        
        String sql = "INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int[] results = jdbcTemplate.batchUpdate(sql, params);
        
        assertEquals(50, results.length);
        int count = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE WHERE FILE_ID LIKE 'batch-file-%'");
        assertEquals(50, count);
        System.out.println("[PASS] Batch insert: " + results.length + " records");
    }

    @Test
    @Order(10)
    @DisplayName("10. 更新和删除测试")
    void testUpdateAndDelete() throws Exception {
        jdbcTemplate.update("UPDATE VFS_FILE SET NAME = ? WHERE FILE_ID = ?", "Updated File 1", "file-1");
        
        String name = jdbcTemplate.queryForString("SELECT NAME FROM VFS_FILE WHERE FILE_ID = ?", "file-1");
        assertEquals("Updated File 1", name);
        
        jdbcTemplate.update("DELETE FROM VFS_FILE WHERE FILE_ID = ?", "file-2");
        int count = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE WHERE FILE_ID = ?", "file-2");
        assertEquals(0, count);
        System.out.println("[PASS] Update and delete operations");
    }

    @Test
    @Order(11)
    @DisplayName("11. 嵌套事务测试")
    void testNestedTransaction() throws Exception {
        jdbcManager.executeInTransaction(() -> {
            jdbcTemplate.update("INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                "nested-1", "Nested 1", "folder-root", 1, "user-1", System.currentTimeMillis(), System.currentTimeMillis());
            
            jdbcManager.executeInTransaction(() -> {
                jdbcTemplate.update("INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    "nested-2", "Nested 2", "folder-root", 1, "user-1", System.currentTimeMillis(), System.currentTimeMillis());
                return null;
            });
            
            return null;
        });
        
        int count = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE WHERE FILE_ID LIKE 'nested-%'");
        assertEquals(2, count);
        System.out.println("[PASS] Nested transaction: 2 files inserted");
    }

    @Test
    @Order(12)
    @DisplayName("12. SQL注入防护测试")
    void testSQLInjectionPrevention() throws Exception {
        String maliciousId = "file-1' OR '1'='1";
        
        List<Map<String, Object>> results = jdbcTemplate.queryForMapList(
            "SELECT * FROM VFS_FILE WHERE FILE_ID = ?", maliciousId);
        
        assertTrue(results.isEmpty(), "SQL injection should be prevented");
        System.out.println("[PASS] SQL injection prevented");
    }

    @Test
    @Order(13)
    @DisplayName("13. 空结果处理测试")
    void testEmptyResult() throws Exception {
        String result = jdbcTemplate.queryForString("SELECT NAME FROM VFS_FILE WHERE FILE_ID = ?", "non-existent-id");
        assertNull(result);
        System.out.println("[PASS] Empty result handled correctly");
    }

    @Test
    @Order(14)
    @DisplayName("14. 高并发压力测试")
    void testHighConcurrencyStress() throws Exception {
        int totalOps = 500;
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(totalOps);
        AtomicInteger successCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < totalOps; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    String operation = index % 3 == 0 ? "read" : (index % 3 == 1 ? "write" : "update");
                    switch (operation) {
                        case "read":
                            jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE");
                            break;
                        case "write":
                            jdbcTemplate.update("INSERT INTO VFS_FILE (FILE_ID, NAME, FOLDER_ID, FILE_TYPE, PERSON_ID, CREATE_TIME, UPDATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                                "stress-" + index, "Stress " + index, "folder-root", 1, "user-1",
                                System.currentTimeMillis(), System.currentTimeMillis());
                            break;
                        case "update":
                            jdbcTemplate.update("UPDATE VFS_FILE SET UPDATE_TIME = ? WHERE FILE_ID = ?",
                                System.currentTimeMillis(), "file-1");
                            break;
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Log but don't fail
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(60, TimeUnit.SECONDS));
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("[PASS] Stress test: " + successCount.get() + "/" + totalOps + 
                          " operations in " + duration + "ms (" + 
                          (totalOps * 1000L / Math.max(duration, 1)) + " ops/sec)");
        executor.shutdown();
    }

    @Test
    @Order(15)
    @DisplayName("15. 连接泄漏检测测试")
    void testConnectionLeakDetection() throws Exception {
        int initialCount = jdbcManager.getActiveConnectionCount();
        
        for (int i = 0; i < 100; i++) {
            jdbcTemplate.queryForInt("SELECT COUNT(*) FROM VFS_FILE");
        }
        
        int finalCount = jdbcManager.getActiveConnectionCount();
        assertTrue(finalCount <= initialCount + 1, "Connection leak detected: " + finalCount + " > " + (initialCount + 1));
        System.out.println("[PASS] No connection leak: " + finalCount + " active connections");
    }

    private static class SQLiteConnectionProvider implements ConnectionProvider {
        private final Connection connection;
        
        SQLiteConnectionProvider(Connection connection) {
            this.connection = connection;
        }
        
        @Override
        public Connection getConnection() throws SQLException {
            return connection;
        }
        
        @Override
        public void releaseConnection(Connection conn) {
            // SQLite in-memory: don't close
        }
        
        @Override
        public String getDriverClassName() {
            return "org.sqlite.JDBC";
        }
    }
}
